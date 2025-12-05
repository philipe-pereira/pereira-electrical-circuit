package br.com.pereiraeng.electricalcircuit.solving;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import br.com.pereiraeng.math.Complex;
import br.com.pereiraeng.math.Partition;
import br.com.pereiraeng.core.ExtendedMath;
import br.com.pereiraeng.electricalcircuit.RLCZ;
import br.com.pereiraeng.electricalcircuit.components.ElecElem;
import br.com.pereiraeng.electricalcircuit.components.No;
import br.com.pereiraeng.electricalcircuit.components.Zcomp;
import br.com.pereiraeng.electricalcircuit.components.ZfComp;

import java.util.Set;
import java.util.TreeMap;

public class FitParams {

	/**
	 * Função que retorna a resposta em frequência dos elementos de um circuito em
	 * função das medições de impedância entre diferentes nós do circuito
	 * 
	 * @param circuit elementos cujos parâmetros serão determinados via otimização
	 * @param meas    tabela que associa para cada partição de nós (i.e., associação
	 *                dos nós) a resposta em frequência a ser aproximada
	 * @return tabela de dispersão que associa para cada elemento a resposta em
	 *         frequência que ele deveria ter de modo que as associações propostas
	 *         dêem a resposta em frequência a ser aproximada
	 */
	public static Map<ElecElem, TreeMap<Double, Complex>> getParams(Collection<ElecElem> circuit,
			Map<Partition<No>, TreeMap<Double, Complex>> meas) {

		// parte 1: conjunto de frequências e chute inicial
		Set<Double> fs = meas.values().iterator().next().keySet();
		TreeMap<Double, Complex> c0 = new TreeMap<>();
		for (Double f : fs)
			c0.put(f, new Complex(100, 1.E5 / f));

		// parte 2: tabela de parâmetros a serem otimizados
		Map<ElecElem, TreeMap<Double, Complex>> params = new HashMap<>();
		for (ElecElem ec : circuit) {
			if (ec instanceof ZfComp) {
				ZfComp comp = (ZfComp) ec;

				TreeMap<Double, Complex> rf = new TreeMap<>();
				for (Entry<Double, Complex> e : c0.entrySet())
					rf.put(e.getKey(), new Complex(e.getValue()));
				params.put(comp, rf);

				comp.setZf(rf);
			}
		}

		// parte 3: para cada partição de nó, seu circuito equivalente
		Map<Partition<No>, RLCZ> circsEqs = new HashMap<>();
		for (Partition<No> p : meas.keySet()) {
			// cópia
			Collection<ElecElem> newCircuit = Zcomp.copy(circuit, null, null); // TODO
																				// null?

			// curto-circuitar elementos e pegar os dois terminais
			No[] terms = new No[2];
			int j = 0;
			for (Set<No> set : p) {
				Set<No> newSet = select(newCircuit, set);
				No no = shortCircuit(newCircuit, newSet);
				if (j < 2)
					terms[j] = no;
				j++;
			}

			// obter equivalente
			RLCZ rlc = Zcomp.getRLC(newCircuit, terms[0], terms[1]);

			circsEqs.put(p, rlc);
		}

		// parte 4: para cada frequência
		for (Double f : fs) {

			// parâmetros iniciais
			Complex[] ps = new Complex[params.size()];
			int j = 0;
			for (TreeMap<Double, Complex> e : params.values())
				ps[j++] = e.get(f);

			for (int i = 1; i <= meas.size(); i++) {

				// circuitos equivalentes e valores esperados
				RLCZ[] rlcs = new RLCZ[i];
				Complex[] ms = new Complex[i];

				j = 0;
				for (Entry<Partition<No>, TreeMap<Double, Complex>> e : meas.entrySet()) {
					rlcs[j] = circsEqs.get(e.getKey());
					ms[j] = e.getValue().get(f);
					j++;
					if (j >= i)
						break;
				}
				otimiza(f, ps, rlcs, ms);
			}
		}

		return params;
	}

	private static No shortCircuit(Collection<ElecElem> circuit, Set<No> set) {
		No remaining = set.iterator().next();

		Iterator<ElecElem> it = circuit.iterator();

		while (it.hasNext()) {
			ElecElem elecElem = it.next();

			if (elecElem instanceof No) {
				No n = (No) elecElem;
				if (set.contains(n) ? !n.equals(remaining) : false)
					// se for nó do curto... sem ser o que fica
					it.remove();
			} else if (elecElem instanceof Zcomp) {
				Zcomp zc = (Zcomp) elecElem;

				// troca nós
				No n = zc.getNo();
				if (set.contains(n) ? !n.equals(remaining) : false)
					zc.setNo(remaining);

				n = zc.getN2();
				if (set.contains(n) ? !n.equals(remaining) : false)
					zc.setN2(remaining);

			}
		}
		return remaining;
	}

	private static final int ITER_MAX = 100000;

	private static final double STEP = 1;

	private static final double TOL = 1.E-10;

	private static final double EPS = 10000;

	private static final double EPS_MIN = 1.E-25;

	private static void otimiza(double f, Complex[] params, RLCZ[] rlcs, Complex[] ms) {

		double x0 = distance(f, rlcs, ms);

		// argumentos da função a ser otimizada (duas vezes a ordem do circuito:
		// uma resistência e uma indutância para cada ordem do circuito N-ramos
		// OU uma resistência e uma indutância para cada frequência da análise)
		int args = 2 * params.length;

		// definir os passos
		double stepR = 0., stepL = 0.;
		for (int i = 0; i < params.length; i++) {
			stepR += params[i].getRe();
			stepL += params[i].getIm();
		}
		double s = STEP / params.length;
		stepR *= s;
		stepL *= s;

		double[] grad = new double[args];
		// até um número máximo de iterações
		for (int k = 0; k < ITER_MAX; k++) {

			// cálculo do gradiente da função distância: aplica-se um pequeno passo (step)
			// em cada um dos argumentos e calcula-se a nova distância com relaçao aos
			// pontos
			for (int j = 0; j < args; j++) {
				Complex c = params[j / 2];
				Complex temp = new Complex(c);

				// pertubação
				double step = j % 2 == 0 ? stepR : stepL;
				if (j % 2 == 0)
					c.setRe(c.getRe() + step);
				else
					c.setIm(c.getIm() + step);

				// calcula nova distância (e compara com a velha, dy) e divide pelo passo
				// (obtendo assim dy/dx)
				grad[j] = (distance(f, rlcs, ms) - x0) / step;

				// volta ao 'normal'
				c.set(temp);
			}

			// novo n-ramos
			Complex[] temp = null;

			// próximo vetor da iteração é o inicial subtraído do gradiente
			// multiplicado por epsilon
			double x1 = x0, de = 0.;
			double e = EPS;
			while (de >= 0. && e > EPS_MIN) {
				e /= 10;

				temp = new Complex[params.length];
				for (int i = 0; i < params.length; i++)
					temp[i] = new Complex(params[i]);

				for (int j = 0; j < args; j++) {
					Complex c = params[j % 2];
					if (j % 2 == 0)
						c.setRe(c.getRe() - e * grad[j]);
					else
						c.setIm(c.getIm() - e * grad[j]);
				}

				x1 = distance(f, rlcs, ms);
				System.out.printf("%d\t%f\n", k, x1);
				de = x1 - x0;

				// se for dar mais um loop... restore
				if (de >= 0.)
					for (int i = 0; i < temp.length; i++)
						params[i].set(temp[i]);
			}
			x0 = x1;

			// critério de parada
			if (Math.abs(de) < TOL)
				break;
		}
	}

	private static double distance(double f, RLCZ[] rlcs, Complex[] meds) {
		double e = 0;
		for (int i = 0; i < meds.length; i++) {
			Complex calc = rlcs[i].getZ(ExtendedMath.TWO_PI * f);
			e += Complex.norma(Complex.sub(calc, meds[i]));
		}
		return e;
	}

	private static Set<No> select(Collection<ElecElem> newCircuit, Set<No> old) {
		Set<No> newNodes = new HashSet<No>();
		for (No n : old) {
			for (ElecElem newEe : newCircuit) {
				if (newEe instanceof No) {
					No newNode = (No) newEe;
					if (newNode.toString().equals(n.toString())) {
						newNodes.add(newNode);
						break;
					}
				}
			}
		}
		return newNodes;
	}
}

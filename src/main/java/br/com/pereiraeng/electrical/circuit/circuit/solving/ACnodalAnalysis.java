package br.com.pereiraeng.electrical.circuit.circuit.solving;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.com.pereiraeng.math.Complex;
import br.com.pereiraeng.math.Vec;
import br.com.pereiraeng.core.collections.ArrayUtils;
import br.com.pereiraeng.electrical.circuit.circuit.components.*;
import br.com.pereiraeng.electrical.circuit.circuit.components.Meter.GrandezaMed;

import java.util.Set;

/**
 * Classe das funções que permitem a resolução do circuito elétrico no domínio
 * da frequência a partir do método da análise nodal
 * 
 * @author Philipe PEREIRA
 *
 */
public class ACnodalAnalysis {

	/**
	 * Função que resolve o circuito linear a partir da análise nodal
	 * 
	 * @param circuit relação de elementos do circuito
	 * @param f       frequência, em Hz
	 * @return tabela de dispersão que associa para cada
	 *         <ul>
	 *         <li>nó, o fasor de tensão;</i>
	 *         <li>gerador de tensão, o fasor de corrente.</i>
	 *         </ul>
	 */
	public static Map<ElecElem, Complex> solve(Collection<ElecElem> circuit, double f) {
		Set<No> grnd = new HashSet<>();
		// nós aterrados
		for (ElecElem ee : circuit)
			if (ee instanceof Ground)
				grnd.add(((Ground) ee).getNo());

		// nós de amperímetros e wattímetros (1o ou 2o terminais) que estão
		// aterrados
		Set<No> grndAmp = new HashSet<>();

		// amperímetros, wattímetros e curtos são curtos-circuitos
		// TODO fontes de tensão s.getWf() == GeneratorType1.DC && s.getOutput()
		// == GeneratorType2.V também são curtos...
		List<LinkedHashSet<No>> ccs = new LinkedList<>();
		Set<Comp2> amps = new HashSet<>();
		for (ElecElem ee : circuit) {
			if (ee instanceof Comp2) {
				Comp2 c2 = (Comp2) ee;
				boolean ma = c2 instanceof Meter ? ((Meter) c2).getType() == GrandezaMed.A : false;
				boolean cc = c2 instanceof CurtoAberto ? ((CurtoAberto) c2).isCurto() : false;
				boolean mw = c2 instanceof Wattmeter;
				if (ma || cc || mw) {
					amps.add(c2);
					if (ma)
						((Meter) c2).setValue(Double.NaN);
					else if (cc)
						((CurtoAberto) c2).setValue(Double.NaN);
					else if (mw)
						((Wattmeter) c2).setVI(Double.NaN, Double.NaN);

					No n1 = c2.getNo();
					No n2 = c2.getN2();
					boolean c1 = grnd.contains(n1);
					if (!c1 && !grnd.contains(n2)) {
						boolean cc1 = false;
						Set<No> cs0 = null;
						for (Set<No> cs : ccs) {
							cc1 = cs.contains(n1);
							if (cc1 || cs.contains(n2)) {
								cs0 = cs;
								break;
							}
						}
						if (cs0 == null) // novo curto-circuito
							ccs.add(new LinkedHashSet<>(Arrays.asList(n1, n2)));
						else {
							Iterator<LinkedHashSet<No>> it = ccs.iterator();
							Set<No> cs1 = null;
							while (it.hasNext()) {
								Set<No> cs = it.next();
								if (cs != cs0) {
									boolean flag = cc1 ? cs.contains(n2) : cs.contains(n1);
									if (flag) {
										cs1 = cs;
										it.remove();
										break;
									}
								}
							}
							if (cs1 != null)
								cs0.addAll(cs1);
							else
								cs0.add(cc1 ? n2 : n1);
						}
					} else {
						grnd.add(c1 ? n2 : n1);
						grndAmp.add(c1 ? n1 : n2);
					}
				}
			}
		}

		// demais nós e geradores
		No[] nos = null;
		Source[] svs = null;
		{
			LinkedList<No> nl = new LinkedList<>();
			LinkedList<Source> svl = new LinkedList<>();
			for (ElecElem ee : circuit) {
				if (ee instanceof No) {
					No n = (No) ee;
					if (!grnd.contains(n) && !isVoltageSame(ccs, n))
						nl.add(n);
				} else if (ee instanceof Source) {
					Source s = (Source) ee;
					if (s.getWf() == GeneratorType1.AC && s.getOutput() == GeneratorType2.V)
						svl.add(s);
				}
			}
			nos = nl.toArray(new No[nl.size()]);
			svs = svl.toArray(new Source[svl.size()]);
		}

		// matriz de admitância
		// [ | ] [ ] [ ]
		// [ | 1] [ ] [ ]
		// [ Y | 0] [vn] [Ig]
		// [ |-1] [ ] = [ ]
		// [ | ] [ ] [ ]
		// --------------------------
		// [ 1 0 -1 | 0] [ig] [Vg]
		Complex[][] g = new Complex[nos.length + svs.length][nos.length + svs.length];
		for (int i = 0; i < g.length; i++)
			for (int j = 0; j < g.length; j++)
				g[i][j] = new Complex();

		for (int i0 = 0; i0 < nos.length; i0++) { // para cada nó
			No n0 = nos[i0];
			for (ElecElem ee : circuit) { // ver se o elemento que parte do nó
				if (ee instanceof RLCcomp) {
					RLCcomp r = (RLCcomp) ee;
					No n1 = r.getNo();
					{
						// se estiver em curto-circuito com outro nó
						No n = get(ccs, n1);
						if (n != null)
							n1 = n;
					}
					No n2 = r.getN2();
					{
						// se estiver em curto-circuito com outro nó
						No n = get(ccs, n2);
						if (n != null)
							n2 = n;
					}

					if (n0 == n1) {
						Complex y = r.getY(f);
						g[i0][i0].sum(y);
						int i2 = ArrayUtils.indexOf(nos, n2);
						if (i2 >= 0)
							g[i0][i2].sub(y);
					} else if (n0 == n2) {
						Complex y = r.getY(f);
						g[i0][i0].sum(y);
						int i1 = ArrayUtils.indexOf(nos, n1);
						if (i1 >= 0)
							g[i0][i1].sub(y);
					}
				}
			}
		}

		// vetor de constantes com as correntes nos geradores de corrente e
		// tensões nos geradores de tensão
		Complex[] vgig = new Complex[g.length];
		for (int i = 0; i < vgig.length; i++)
			vgig[i] = new Complex();

		for (ElecElem ee : circuit) { // para cada gerador de corrente
			if (ee instanceof Source) {
				Source src = (Source) ee;
				if (src.getWf() == GeneratorType1.AC && src.getOutput() == GeneratorType2.I) {
					No n1 = src.getNo();
					{
						// se estiver em curto-circuito com outro nó
						No n = get(ccs, n1);
						if (n != null)
							n1 = n;
					}
					No n2 = src.getN2();
					{
						// se estiver em curto-circuito com outro nó
						No n = get(ccs, n2);
						if (n != null)
							n2 = n;
					}

					Complex Ig = (Complex) src.getValue();

					int i = ArrayUtils.indexOf(nos, n1);
					if (i >= 0)
						vgig[i].sum(Ig);

					i = ArrayUtils.indexOf(nos, n2);
					if (i >= 0)
						vgig[i].sub(Ig);
				}
			}
		}

		for (int i0 = 0; i0 < svs.length; i0++) { // para cada gerador de tensão
			Source src = svs[i0];
			No n1 = src.getNo();
			{
				// se estiver em curto-circuito com outro nó
				No n = get(ccs, n1);
				if (n != null)
					n1 = n;
			}
			No n2 = src.getN2();
			{
				// se estiver em curto-circuito com outro nó
				No n = get(ccs, n2);
				if (n != null)
					n2 = n;
			}

			int i = ArrayUtils.indexOf(nos, n1);
			if (i >= 0) {
				g[nos.length + i0][i] = new Complex(1, 0);
				g[i][nos.length + i0] = new Complex(-1, 0);
			}

			i = ArrayUtils.indexOf(nos, n2);
			if (i >= 0) {
				g[nos.length + i0][i] = new Complex(-1, 0);
				g[i][nos.length + i0] = new Complex(1, 0);
			}

			Complex Vg = (Complex) src.getValue();
			vgig[nos.length + i0].sum(Vg);
		}

		// resolver sistema linear
		Complex[] vnIg = Vec.solveGauss(g, vgig);

		Map<ElecElem, Complex> out = new HashMap<>();
		for (int i = 0; i < nos.length; i++)
			out.put(nos[i], vnIg[i]);
		for (No n : grnd)
			out.put(n, new Complex());
		for (int i = 0; i < svs.length; i++)
			out.put(svs[i], vnIg[nos.length + i]);

		// nós curto-circuitados
		for (LinkedHashSet<No> cc : ccs) {
			Iterator<No> it = cc.iterator();
			Complex v = out.get(it.next());
			while (it.hasNext())
				out.put(it.next(), v);
		}

		// correntes nos amperímetros
		Iterator<Comp2> itc2 = null;
		while (amps.size() > 0) {
			itc2 = amps.iterator();
			eqp: while (itc2.hasNext()) {
				Comp2 c2 = itc2.next();

				boolean ma = c2 instanceof Meter ? ((Meter) c2).getType() == GrandezaMed.A : false;
				boolean cc = c2 instanceof CurtoAberto ? ((CurtoAberto) c2).isCurto() : false;
				boolean mw = c2 instanceof Wattmeter;
				if (ma || cc || mw) {
					No n = c2.getNo();
					if (grndAmp.contains(n)) // se um dos nós do amperímetro
												// está aterrado...
						n = c2.getN2(); // ... usar o outro

					// calcular corrente pelo componente de impedância nula
					// somando as correntes que chegam
					Complex ia = new Complex();
					for (ElecElem eeo : circuit) {
						if (eeo != c2) {
							if (eeo instanceof Comp2) {
								Comp2 co2 = (Comp2) eeo;
								No n1 = co2.getNo();
								No n2 = co2.getN2();
								boolean c1 = n == n1;
								if (c1 || n == n2) {
									if (co2 instanceof RLCcomp) {
										RLCcomp r = (RLCcomp) co2;
										ia.sum(Complex.div(
												Complex.mult(c1 ? -1 : 1, Complex.sub(out.get(n1), out.get(n2))),
												r.getZ(f)));
									} else if (co2 instanceof Source)
										ia.sum(Complex.mult(c1 ? 1 : -1, out.get(co2)));
									else {
										boolean mao = co2 instanceof Meter ? ((Meter) co2).getType() == GrandezaMed.A
												: false;
										boolean cco = co2 instanceof CurtoAberto ? ((CurtoAberto) co2).isCurto()
												: false;
										boolean mwo = co2 instanceof Wattmeter;
										if (mao || cco || mwo) {
											Number no = null;
											if (mao)
												no = ((Meter) co2).getValue();
											else if (cco)
												no = ((CurtoAberto) co2).getValue();
											else if (mwo)
												no = ((Wattmeter) co2).getI();
											if (no == null ? true : !(no instanceof Complex))
												continue eqp;
											ia.sum(Complex.mult(c1 ? 1 : -1, (Complex) no));
										}
									}
								}
							}
						}
					}
					ia.convert2polar();
					ia.mult(-1);

					if (ma)
						((Meter) c2).setValue(ia);
					else if (cc)
						((CurtoAberto) c2).setValue(ia);
					else if (mw) {
						Wattmeter wm = (Wattmeter) c2;
						Complex v1 = out.get(wm.getNo());
						Complex v2 = out.get(wm.getN3());
						Complex v = Complex.sub(v2, v1);
						v.convert2polar();
						wm.setVI(v, ia);
					}
				}
				itc2.remove();
			}
		}

		// voltímetros
		for (ElecElem ee : circuit) {
			boolean ma = ee instanceof Meter ? ((Meter) ee).getType() == GrandezaMed.V : false;
			boolean ca = ee instanceof CurtoAberto ? !((CurtoAberto) ee).isCurto() : false;
			if (ma || ca) {
				Comp2 c2 = (Comp2) ee;
				Complex v1 = out.get(c2.getNo());
				Complex v2 = out.get(c2.getN2());
				Complex value = Complex.sub(v2, v1);
				value.convert2polar();
				if (ma)
					((Meter) ee).setValue(value);
				else if (ca)
					((CurtoAberto) c2).setValue(value);
			}
		}

		return out;
	}

	private static boolean isVoltageSame(List<LinkedHashSet<No>> ccs, No n) {
		for (Set<No> cs : ccs) {
			Iterator<No> it = cs.iterator();
			No n0 = it.next();
			if (n0 == n) // se for o primeiro nó, tem um tensão independente
				return false;
			while (it.hasNext()) { // se for outro nó, tem uma tensão igual a
									// primeira da lista
				n0 = it.next();
				if (n == n0)
					return true;
			}
		}
		// se não estiver na lista de CC, então tem um tensão independente
		return false;
	}

	private static No get(List<LinkedHashSet<No>> ccs, No n) {
		for (Set<No> cs : ccs)
			if (cs.contains(n))
				return cs.iterator().next();
		return null;
	}
}

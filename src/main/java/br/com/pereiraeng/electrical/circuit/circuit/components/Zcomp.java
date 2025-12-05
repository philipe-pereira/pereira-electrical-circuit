package br.com.pereiraeng.electrical.circuit.circuit.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import br.com.pereiraeng.math.Complex;
import br.com.pereiraeng.core.Orientation;
import br.com.pereiraeng.electrical.circuit.circuit.RLCZ;
import br.com.pereiraeng.electrical.circuit.circuit.RLCZ.CompType;
import br.com.pereiraeng.electrical.circuit.circuit.RLCZ.LinearType;
import br.com.pereiraeng.graph.Edge;

/**
 * Classe abstrata que representa um componente que possui uma dada impedância
 * entre seus dois terminais. Essa impedância pode variar livremente com a
 * frequência {@link ZfComp} ou variar conforme as regras dos componentes
 * elétricos lineares básicos ({@link RLCcomp})
 * 
 * @author Philipe PEREIRA
 *
 */
public abstract class Zcomp extends Comp2 {

	public Zcomp(String label) {
		super(label);
	}

	/**
	 * Função que retorna o valor da impedância complexa equivalente entre os dois
	 * terminais deste componente
	 * 
	 * @param f
	 *            frequência elétrica, em Hertz
	 * @return número complexo que indica a impedância para a dada frequência
	 */
	public abstract Complex getZ(double f);

	/**
	 * Função que retorna o valor da admitância complexa equivalente entre os dois
	 * terminais deste componente
	 * 
	 * @param f
	 *            frequência elétrica, em Hertz
	 * @return número complexo que indica a admitância para a dada frequência
	 */
	public Complex getY(double f) {
		return Complex.inv(getZ(f));
	}

	// -------------------------------- COMP --------------------------------

	protected transient Orientation orientation;

	@Override
	public void setOrientation(int o) {
		this.orientation = Orientation.values()[o];
	}

	@Override
	public void rotate() {
		this.orientation = this.orientation.next();
	}

	// ------------------------ CIRCUITO -> REDE RLC ------------------------

	/**
	 * Função que retorna a rede {@link RLCZ linear} equivalente a um circuito
	 * quando vista a partir de dois nós
	 * 
	 * @param elements
	 *            relação de objetos que representam os nó e componentes do circuito
	 * @param term1
	 *            nó que representa um dos terminais
	 * @param term2
	 *            nó que representa o outro terminal
	 * @return rede linear equivalente
	 */
	public static RLCZ getRLC(Collection<ElecElem> elements, No term1, No term2) {
		// criar réplica do grafo, sendo que este pode ser editado (pode ter nós
		// apagados, topologia alterada, etc.)

		HashSet<ElecElem> newCircuit = copy(elements, term1, term2);

		// novos terminais (encontrar na cópia os correpondentes do original)
		No n1 = null, n2 = null;
		for (ElecElem ec : newCircuit) {
			if (ec instanceof No) {
				No no = (No) ec;

				if (term1.toString().equals(no.toString()))
					n1 = no;
				if (term2.toString().equals(no.toString()))
					n2 = no;

				if (n1 != null && n2 != null)
					break;
			}
		}

		// são dois os tipos de operação efetuados para reduzir o grafo:
		// * associação em paralelo
		// * transformação estrela-polígono (para uma estrela de n pontas, se n=2, a
		// transformação é equivalente à associação em série; se n=3, é equivalente a
		// transformação estrela-delta)
		int i = 0;
		main: while (true) {
			// se houver somente os dois terminais e um elemento ligando-os,
			// mission accomplished
			if (newCircuit.size() < 4)
				break;

			// procurar se tem paralelos e, se houver, associá-los
			for (ElecElem e1 : newCircuit) {
				// para cada aresta...
				if (e1 instanceof Zcomp) {
					Zcomp z1 = (Zcomp) e1;
					for (ElecElem e2 : newCircuit) {
						if (z1 != e2) {
							// se não for a mesma...
							if (e2 instanceof Zcomp) {
								Zcomp z2 = (Zcomp) e2;

								// ver se tem outra aresta em paralelo...
								if (Comp2.isParallel(z1, z2)) {
									// então, criar o equivalente paralelo

									RLCZ rlc1 = getRLCZ(z1), rlc2 = getRLCZ(z2);

									RLCcomp parallel = new RLCcomp("@" + (i++), new RLCZ(false, rlc1, rlc2));

									parallel.setNo(z1.getNo());
									parallel.setN2(z1.getN2());

									newCircuit.add(parallel);

									z1.remove();
									newCircuit.remove(z1);
									z2.remove();
									newCircuit.remove(z2);

									// ao se fazer alguma modificação, recomeçar a análise
									continue main;
								}
							}
						}
					}
				}
			}

			// Procurar se há elementos em série e, se houver, associá-los (isso é indicado
			// através da existência de um nó que só tem duas arestas; se não houver essa
			// possibilidade, procura-se a estrela com o menor número de pontas e faz-se a
			// associação estrela-polígono)
			// Esse procedimento remove um nó do sistema, de modo que ele não poderá ser
			// feito sobre um dos terminais
			int n = 2;
			while (true) {
				for (ElecElem e : newCircuit) {
					// para cada nó...
					if (e instanceof No) {
						No no = (No) e;

						if (n1 != no && n2 != no) {
							// se o nó não for um dos terminais

							Set<? extends Edge> edges = no.getEdges();
							if (edges.size() == n) {
								if (n == 2) {
									// então, criar o equivalente série
									Iterator<? extends Edge> it = edges.iterator();
									Zcomp z1 = (Zcomp) it.next();
									Zcomp z2 = (Zcomp) it.next();

									RLCZ rlc1 = getRLCZ(z1), rlc2 = getRLCZ(z2);

									RLCcomp serie = new RLCcomp("@" + (i++), new RLCZ(true, rlc1, rlc2));
									serie.setNo((No) z1.getOpposite(no));
									serie.setN2((No) z2.getOpposite(no));

									newCircuit.add(serie);

									newCircuit.remove(no);
									z1.remove();
									newCircuit.remove(z1);
									z2.remove();
									newCircuit.remove(z2);
								} else {
									// então, fazer a transformação estrela-polígono

									// criar lista de arestas da estrela
									ArrayList<Zcomp> star = new ArrayList<>(edges.size());
									for (Edge c : edges)
										star.add((Zcomp) c);

									// converter em polígono
									ArrayList<RLCcomp> mesh = star2mesh(star, no, i);
									i += mesh.size();

									// adicionar novas arestas ao grafo
									for (Comp2 m : mesh)
										newCircuit.add(m);

									// remover nó do centro da estrela
									newCircuit.remove(no);
									// remover estrela
									for (Comp2 s : star) {
										s.remove();
										newCircuit.remove(s);
									}
								}
								// ao se fazer alguma modificação (série ou estrela-polígono), recomeçar a
								// análise (vendo se com a alteração algum nó ficou em paralelo...)
								continue main;
							} else if (edges.size() == 1) {
								// ponta solta: aparar

								Zcomp z1 = (Zcomp) edges.iterator().next();
								z1.remove();

								// remover ponta
								newCircuit.remove(z1);
								// remove nó que estava na ponta solta
								newCircuit.remove(no);
							}
						}
					}
				}
				n++;
			}
		}

		RLCZ out = null;

		// procurar dentre os elementos que sobram qual deles não são os terminais
		// (sendo portanto o circuito RLC equivalente)
		for (ElecElem ec : newCircuit) {
			if (!n1.equals(ec) && !n2.equals(ec)) {
				out = ((RLCcomp) ec).getRLC();
				break;
			}
		}

		return out;
	}

	/**
	 * Cria uma cópia do grafo, sendo que esta cópia conterá somente {@link RLCcomp
	 * elementos lineares} (os demais elementos, tais como fontes e elementos
	 * não-lineares, serão ignorados) e as informações gráficas também serão
	 * ignoradas (esta cópia servirá somente do ponto de vista topológico). Há
	 * também um pré-tratamento onde vértices com uma só aresta ('pontas') e arestas
	 * com o mesmo vértice ('curtos'), são ignorados.
	 * 
	 * @param network
	 *            relação de objetos que representam os nó e componentes do circuito
	 * @param term1
	 *            um dos nós terminais (este obrigatoriamente deve ser copiado,
	 *            ainda que seja ponta)
	 * @param term2
	 *            o outro nó terminal (este obrigatoriamente deve ser copiado, ainda
	 *            que seja ponta)
	 * 
	 * @return conjunto de objetos que representam uma cópia do circuito
	 */
	public static HashSet<ElecElem> copy(Collection<ElecElem> network, No term1, No term2) {
		HashMap<String, ElecElem> newGraph = new HashMap<>();

		// primeiro, os nós
		for (ElecElem ec : network) {
			if (ec instanceof No) {
				No no = (No) ec;
				if (no.getEdges().size() > 1 || no == term1 || no == term2) {
					// ignorar elementos que formam 'pontas' (nós com só um
					// elemento), exceto se for um dos terminais
					newGraph.put(no.toString(), new No(no.toString()));
				}
			}
		}

		// em seguida, as arestas (a medida que as arestas são adicionadas, os
		// novos nó já vão sendo adicionados)
		for (ElecElem ec : network) {
			if (ec instanceof Zcomp) {
				Zcomp oldZ = (Zcomp) ec;

				No no = oldZ.getNo(), n2 = oldZ.getN2();
				if (n2.equals(no))
					// ignorar elementos curto-circuitados
					continue;

				// novos nós correspondentes
				no = (No) newGraph.get(no.toString());
				n2 = (No) newGraph.get(n2.toString());

				if (no != null && n2 != null) {
					// se os novos nós não estiverem na lista, é porque foram
					// ignorados na etapa anterior (logo esta aresta é de um
					// componente que era 'ponta' de circuito)

					String label = oldZ.toString();

					Zcomp newZ = null;
					if (oldZ instanceof RLCcomp)
						newZ = new RLCcomp(label, ((RLCcomp) oldZ).getRLC());
					else if (oldZ instanceof ZfComp)
						newZ = new ZfComp(label, ((ZfComp) oldZ).getZf());

					newZ.setNo(no);
					newZ.setN2(n2);

					newGraph.put(label, newZ);
				}
			}
		}

		// repassar para um conjunto
		return new HashSet<>(newGraph.values());
	}

	/**
	 * Função que cria uma lista de novas arestas que são obtidas a partir da
	 * <a href="https://en.wikipedia.org/wiki/Star-mesh_transform">transformação
	 * estrela-malha</a>.
	 * 
	 * @param star
	 *            lista de arestas que partem do centro da estrela
	 * @param no
	 *            nó que fica no centro da estrela
	 * @param cont
	 *            contador das arestas que estão sendo criadas
	 * @return lista de novas {@link RLCcomp arestas}
	 */
	private static ArrayList<RLCcomp> star2mesh(ArrayList<Zcomp> star, No no, int cont) {
		int size = star.size();
		ArrayList<RLCcomp> out = new ArrayList<>(size * (size - 1) / 2);

		for (int i = 0; i < size; i++) {
			// um dos vértices
			Zcomp z1 = star.get(i);
			for (int j = i + 1; j < size; j++) {
				// outro vértice diferente do primeiro
				Zcomp z2 = star.get(j);

				// demais vértices que não são nem o primeiro nem o segundo
				RLCZ[] rlcs = new RLCZ[size - 2];
				int k = 0;
				for (Zcomp rlc : star)
					if (!rlc.equals(z1) && !rlc.equals(z2))
						rlcs[k++] = getRLCZ(rlc);

				// nova aresta do polígono
				RLCcomp poli = new RLCcomp("@" + (cont++), new RLCZ(getRLCZ(z1), getRLCZ(z2), rlcs));
				poli.setNo((No) z1.getOpposite(no));
				poli.setN2((No) z2.getOpposite(no));

				out.add(poli);
			}
		}

		return out;
	}

	private static RLCZ getRLCZ(Zcomp z) {
		if (z instanceof RLCcomp)
			return ((RLCcomp) z).getRLC();
		else
			return new RLCZ(((ZfComp) z).getZf());
	}

	// ------------------------ REDE RLC -> CIRCUITO ------------------------

	/**
	 * Função que cria um circuito (com nós e arestas) a partir de uma rede RLC.
	 * 
	 * @param rlc
	 *            objeto RLC que representa as associações série ou paralelo de
	 *            elementos lineares
	 * @return coleção de elementos que acabam por representar o circuito
	 */
	public static Collection<ElecElem> getCircuit(RLCZ rlc) {
		int i = 0;
		No n1 = new No("c" + i++);
		No n2 = new No("c" + i++);
		HashSet<ElecElem> out = new HashSet<ElecElem>();
		out.add(n1);
		out.add(n2);
		createComp(n1, rlc, n2, out, i);
		return out;
	}

	private static int createComp(No n1, RLCZ rlc, No n2, HashSet<ElecElem> set, int i) {
		Object type = rlc.getType();

		if (type instanceof LinearType) {
			RLCcomp comp = new RLCcomp("c" + i++, rlc);
			comp.setNo(n1);
			comp.setN2(n2);
			set.add(comp);
		} else if (type instanceof CompType) {
			CompType ct = (CompType) type;
			switch (ct) {
			case S:
				No[] nos = new No[rlc.size() - 1];

				for (int j = 0; j < nos.length; j++) {
					nos[j] = new No("c" + i++);
					set.add(nos[j]);
				}

				for (int j = 0; j < rlc.size(); j++) {
					RLCZ l = rlc.get(j);
					i = createComp(j == 0 ? n1 : nos[j - 1], l, j == rlc.size() - 1 ? n2 : nos[j], set, i);
				}
				break;
			case P:
				for (RLCZ l : rlc)
					i = createComp(n1, l, n2, set, i);
				break;
			default:
				break;
			}
		}
		return i;
	}
}

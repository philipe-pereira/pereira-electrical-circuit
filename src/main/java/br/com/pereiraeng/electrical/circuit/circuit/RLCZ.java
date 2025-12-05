package br.com.pereiraeng.electrical.circuit.circuit;

import java.util.ArrayList;
import java.util.Map.Entry;

import java.util.TreeMap;

import br.com.pereiraeng.math.Complex;
import br.com.pereiraeng.core.ExtendedMath;
import br.com.pereiraeng.electrical.circuit.circuit.solving.CircuitCalc;

/**
 * Classe de objetos que representam uma rede linear RLC
 * 
 * @author Philipe PEREIRA
 *
 */
public class RLCZ extends ArrayList<RLCZ> {
	private static final long serialVersionUID = 1L;

	/**
	 * Se a rede linear é unária (i.e., composta por um só elemento) o
	 * {@link RLCZ#getType() tipo} dela é um dos seguintes itens dessa
	 * enumeração:
	 * 
	 * <ul>
	 * <li>R: resistência;</i>
	 * <li>L: indutância;</i>
	 * <li>C: capacitância;</i>
	 * <li>Z: impedância.</i>
	 * </ul>
	 * 
	 * @author Philipe PEREIRA
	 *
	 */
	public enum LinearType {
		R("\u03A9"), L("H"), C("F"), Z("\u03A9");

		private String unit;

		private LinearType(String unit) {
			this.unit = unit;
		}

		public String getUnit() {
			return unit;
		}
	}

	/**
	 * Se a rede linear é uma combinação de outras redes lineares, o
	 * {@link RLCZ#getType() tipo} dela é um dos seguintes itens dessa
	 * enumeração:
	 * 
	 * <ul>
	 * <li>S: associação em série de redes;</i>
	 * <li>P: associação em paralelo de redes;</i>
	 * <li>E: associação resultante da transformação estrela-polígono de
	 * redes.</i>
	 * </ul>
	 * 
	 * @author Philipe PEREIRA
	 *
	 */
	public enum CompType {
		S, P, E;
	}

	/**
	 * Tipo de ramo da rede RLC (pode ser do tipo {@link LinearType simples} ou
	 * {@link CompType composto})
	 */
	private Object type;

	/**
	 * Valor da resistência, da indutância ou da capacitância do ramo (se for um
	 * ramo representando uma associação série ou paralelo, este valor
	 * {@link Double#NaN não é um número}).
	 */
	private Object value;

	/**
	 * Construtor de um ramo RLC de base
	 * 
	 * @param type
	 *            tipo de ramo (R, L ou C)
	 * @param value
	 *            parâmetro do elemento do ramo (resistência em Ohms, indutância
	 *            em Henries ou capacitância em Faradays)
	 */
	public RLCZ(LinearType type, double value) {
		super(0);
		this.type = type;
		this.value = value;
	}

	/**
	 * Construtor de um ramo composto por uma impedância que varia com a
	 * frequência
	 * 
	 * @param z
	 *            resposta em frequência, dada na forma de uma tabela de
	 *            dispersão ordenada que associa a frequência com a impedância
	 *            complexa, em Ohms
	 */
	public RLCZ(TreeMap<Double, Complex> zf) {
		super(0);
		this.type = LinearType.Z;
		this.value = zf;
	}

	/**
	 * Construtor de um ramo RLC a partir da associação série ou paralelo de
	 * outros ramos
	 * 
	 * @param serie
	 *            <code>true</code> se a associação dos ramos é em série,
	 *            <code>false</code> se está em paralelo
	 * @param rlc
	 *            ramos a serem associados
	 */
	public RLCZ(boolean serie, RLCZ... rlc) {
		super(rlc.length);
		if (serie)
			this.type = CompType.S;
		else
			this.type = CompType.P;
		this.value = Double.NaN;

		for (RLCZ e : rlc) {
			if (e.isSingle() ? true : (CompType) e.getType() != this.type)
				this.add(e);
			else
				for (RLCZ e1 : e)
					this.add(e1);
		}
	}

	/**
	 * Construtor de um ramo RLC que seria obtido a partir da transformação
	 * estrela-malha entre dois pontos de uma estrela
	 * 
	 * @param eA
	 *            ramo que parte de um dos nós até o centro da estrela
	 * @param eB
	 *            ramo que parte do outro nó até o centro da estrela
	 * @param rlc
	 *            demais ramos da estrela
	 */
	public RLCZ(RLCZ eA, RLCZ eB, RLCZ... rlc) {
		super(rlc.length + 2);
		this.type = CompType.E;
		this.value = Double.NaN;

		this.add(eA);
		this.add(eB);
		for (RLCZ e : rlc)
			this.add(e);
	}

	// -------------------- GETTERS AND SETTERS --------------------

	/**
	 * Função que retorna o tipo de elemento que este objeto representa
	 * 
	 * @return tipo de objeto (pode ser {@link LinearType simples} ou
	 *         {@link CompType composto})
	 */
	public Object getType() {
		return this.type;
	}

	public void setType(Object type) {
		this.type = type;
	}

	public double getValue() {
		return (double) this.value;
	}

	public TreeMap<?, ?> getFR() {
		return (TreeMap<?, ?>) value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public boolean isSingle() {
		return this.type instanceof LinearType;
	}

	public boolean isComposed() {
		return this.type instanceof CompType;
	}

	@Override
	public String toString() {
		if (type instanceof LinearType) {
			LinearType lt = (LinearType) type;
			switch (lt) {
			case R:
				return String.format("R=%g", value);
			case L:
				return String.format("L=%g", value);
			case C:
				return String.format("C=%g", value);
			case Z:
				return String.format("Z=%s", value);
			}
		} else if (type instanceof CompType) {
			CompType ct = (CompType) type;
			switch (ct) {
			case S:
			case P:
				String out = "(" + this.get(0).toString();
				for (int i = 1; i < this.size(); i++)
					out += ((type.equals(CompType.S) ? "+" : "//") + this.get(i).toString());
				return out + ")";
			case E:
				out = "(" + this.get(0).toString() + "-" + this.get(1).toString();
				for (int i = 2; i < this.size(); i++)
					out += ("*" + this.get(i).toString());
				return out + ")";
			}
		}
		return null;
	}

	// ------------------------------------------------------------

	/**
	 * Função que calcula a impedância equivalente da rede RLC
	 * 
	 * @param w
	 *            frequência angular, em radianos por segundo
	 * @return valor da impedância complexa, em Ohms
	 */
	public Complex getZ(double w) {
		if (type instanceof LinearType) {
			LinearType lt = (LinearType) type;
			switch (lt) {
			case R:
				return CircuitCalc.getZr(w, (double) value);
			case L:
				return CircuitCalc.getZl(w, (double) value);
			case C:
				return CircuitCalc.getZc(w, (double) value);
			case Z:
				double f = w / ExtendedMath.TWO_PI;
				TreeMap<?, ?> f2z = (TreeMap<?, ?>) value;

				Complex c = (Complex) f2z.get(f);
				if (c == null) {
					// se não achou a frequência, procura a mais próxima
					Entry<?, ?> e0 = f2z.lastEntry();
					double maxDelta = (double) e0.getKey();
					c = (Complex) e0.getValue();

					for (Entry<?, ?> e1 : f2z.entrySet()) {
						double distance = Math.abs(((double) e1.getKey()) - f);
						if (distance < maxDelta) {
							maxDelta = distance;
							c = (Complex) e1.getValue();
						} else {
							break;
						}
					}
				}
				return c;
			}
		} else if (type instanceof CompType) {
			CompType ct = (CompType) type;
			switch (ct) {
			case S:
				Complex c = this.get(0).getZ(w);
				for (int i = 1; i < this.size(); i++)
					c = CircuitCalc.serie(c, this.get(i).getZ(w));
				return c;
			case P:
				c = this.get(0).getZ(w);
				for (int i = 1; i < this.size(); i++)
					c = CircuitCalc.parallel(c, this.get(i).getZ(w));
				return c;
			case E:
				Complex zA = get(0).getZ(w);
				Complex zB = get(1).getZ(w);

				Complex inv = Complex.sum(Complex.inv(zA), Complex.inv(zB));
				for (int i = 2; i < this.size(); i++)
					inv = Complex.sum(inv, Complex.inv(get(i).getZ(w)));

				return Complex.mult(zA, zB, inv);
			}
		}
		return null;
	}
}

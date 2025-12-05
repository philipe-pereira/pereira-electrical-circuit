package br.com.pereiraeng.electrical.circuit.circuit;

import br.com.pereiraeng.math.SymMatrix;

/**
 * Classe de objetos que representam um acoplamento magnético linear
 * 
 * @author Philipe PEREIRA
 *
 */
public class MC {
	private SymMatrix sm;

	/**
	 * Construtor do objeto de um acoplamento magnético de duas bobinas
	 * 
	 * @param l1
	 *            indutância própria de uma das bobinas, em Henries
	 * @param l2
	 *            indutância própria da outra bobina, em Henries
	 * @param m
	 *            indutância mútua entre as bobinas, em Henries
	 */
	public MC(double l1, double l2, double m) {
		this(2);
		this.setSelf(0, l1);
		this.setSelf(1, l2);
		this.setMutual(0, 1, m);
	}

	/**
	 * Construtor do objeto de um acoplamento magnético de duas bobinas
	 * 
	 * @param windings
	 */
	public MC(int windings) {
		this.setWindings(windings);
	}

	// ------------------------ GETTERS ------------------------

	public int getWindings() {
		return this.sm.getOrder();
	}

	public void setWindings(int windings) {
		if (windings < 2)
			throw new IllegalArgumentException("Acoplamento são entre duas ou mais bobinas");
		if (sm == null)
			this.sm = new SymMatrix(windings);
		else
			this.sm.setOrder(windings);
	}

	public SymMatrix getMatrix() {
		return this.sm;
	}

	public void setMatrix(SymMatrix sm) {
		this.sm = sm;
	}

	/**
	 * Função que retorna a indutância própria de uma bobina ou a mútua das
	 * bobinas deste acoplamento magnético
	 * 
	 * @param index1
	 *            índice de uma das bobinas (maior ou igual a 0 e menor que
	 *            número de bobinas)
	 * @param index2
	 *            índice da outra bobina
	 * @return
	 *         <ul>
	 *         <li>se os índices forem iguais: indutância própria da bobina, em
	 *         Henries;</i>
	 *         <li>se os índices forem diferentes: indutância mútua entre as
	 *         duas bobinas, em Henries.</i>
	 *         </ul>
	 */
	public double getInductance(int index1, int index2) {
		return sm.get(index1, index2);
	}

	/**
	 * Função que retorna a indutância própria de uma bobina
	 * 
	 * @param index
	 *            índice da bobina (maior ou igual a 0 e menor que número de
	 *            bobinas)
	 * @return indutância própria da bobina, em Henries
	 */
	public double getSelf(int index) {
		return sm.getPri(index);
	}

	/**
	 * Função que retorna a indutância mútua entre duas bobinas
	 * 
	 * @param index1
	 *            índice de uma das bobinas (maior ou igual a 0 e menor que
	 *            número de bobinas)
	 * @param index2
	 *            índice da outra bobina
	 * @return indutância mútua entre as duas bobinas, em Henries
	 */
	public double getMutual(int index1, int index2) {
		return sm.getNotPri(index1, index2);
	}

	public void setSelf(int index, double value) {
		sm.setPri(index, value);
	}

	public void setMutual(int index1, int index2, double value) {
		sm.setNotPri(index1, index2, value);
	}

	/**
	 * Função que retorna os coeficientes de acoplamento entre os enrolamentos
	 * 
	 * @param index1
	 *            índice de uma das bobinas (maior ou igual a 0 e menor que
	 *            número de bobinas)
	 * @param index2
	 *            índice da outra bobina
	 * @return coeficientes de acoplamento, dado pela fórmula k<sub>ij</sub> = M
	 *         <sub>ij</sub>/sqrt(L<sub>i</sub>*L<sub>j</sub>)
	 */
	public double getCouplingCoefficient(int index1, int index2) {
		if (index1 == index2)
			return Double.NaN;
		else
			return getMutual(index1, index2) / Math.sqrt(getSelf(index1) * getSelf(index2));
	}
}

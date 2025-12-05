package br.com.pereiraeng.electricalcircuit.components;

import br.com.pereiraeng.graph.Edge;
import br.com.pereiraeng.graph.Vertex;
import br.com.pereiraeng.math.Vec;

/**
 * Classe abstrata do objeto que representa um elemento do circuito com dois
 * terminais
 * 
 * @author Philipe PEREIRA
 *
 */
public abstract class Comp2 extends Comp implements Edge {

	public Comp2(String label) {
		super(label);
	}

	// ------------------------ NO 2 ------------------------

	protected No n2;

	public void setN2(No n2) {
		if (this.n2 != null)
			this.n2.remove(this);
		this.n2 = n2;
		this.n2.add(this);
	}

	public void removeN2() {
		if (this.n2 != null)
			this.n2.remove(this);
		this.n2 = null;
	}

	public No getN2() {
		return this.n2;
	}

	protected int pn2;

	public void setConnectionPoint2(int pn) {
		this.pn2 = pn;
	}

	public int getConnectionPoint2() {
		return this.pn2;
	}

	public static boolean isParallel(Comp2 comp1, Comp2 comp2) {
		return Vec.equalBinary(comp1.getNo(), comp1.getN2(), comp2.getNo(), comp2.getN2());
	}

	// ----------------- INTERFACE ARESTA -----------------

	@Override
	public Vertex getOpposite(Vertex v) {
		return v.equals(no) ? n2 : no;
	}

	@Override
	public boolean contains(Vertex v) {
		if (v == null)
			return false;
		else
			return v.equals(no) || v.equals(n2);
	}

	/**
	 * Função utilizada para remover esta aresta dos vértices até então associados
	 */
	public void remove() {
		no.remove(this);
		n2.remove(this);
	}
}

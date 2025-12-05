package br.com.pereiraeng.electricalcircuit.components;

import java.util.HashSet;
import java.util.Set;

import br.com.pereiraeng.graph.Vertex;

public abstract class Comp4 extends Comp3 {

	public Comp4(String label) {
		super(label);
	}

	// ------------------------ NO 4 ------------------------

	/**
	 * Nó conectado ao terminal de número 4
	 */
	protected No n4;

	public void setN4(No n4) {
		if (this.n4 != null)
			this.n4.remove(this);
		this.n4 = n4;
		this.n4.add(this);
	}

	public void removeN4() {
		if (this.n4 != null)
			this.n4.remove(this);
		this.n4 = null;
	}

	public No getN4() {
		return this.n4;
	}

	/**
	 * Ponto de conexão no nó do terminal 4
	 */
	protected int pn4;

	public void setConnectionPoint4(int pn) {
		this.pn4 = pn;
	}

	public int getConnectionPoint4() {
		return this.pn4;
	}

	// ----------------- INTERFACE SUPER-ARESTA -----------------

	@Override
	public boolean contains(Vertex v) {
		return super.contains(v) || v.equals(n4);
	}

	@Override
	public Set<? extends Vertex> getVertices() {
		Set<Vertex> out = new HashSet<>(super.getVertices());
		out.add(this.n4);
		return out;
	}
}

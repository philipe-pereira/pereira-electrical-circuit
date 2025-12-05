package br.com.pereiraeng.electrical.circuit.circuit.components;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import br.com.pereiraeng.graph.Hyperedge;
import br.com.pereiraeng.graph.Vertex;

public abstract class Comp3 extends Comp2 implements Hyperedge {

	// ------------------------ NO 3 ------------------------

	public Comp3(String label) {
		super(label);
	}

	protected No n3;

	public void setN3(No n3) {
		if (this.n3 != null)
			this.n3.remove(this);
		this.n3 = n3;
		this.n3.add(this);
	}

	public void removeN3() {
		if (this.n3 != null)
			this.n3.remove(this);
		this.n3 = null;
	}

	public No getN3() {
		return this.n3;
	}

	protected int pn3;

	public void setConnectionPoint3(int pn) {
		this.pn3 = pn;
	}

	public int getConnectionPoint3() {
		return this.pn3;
	}

	// ----------------- INTERFACE SUPER-ARESTA -----------------

	@Override
	public boolean contains(Vertex v) {
		return super.contains(v) || v.equals(n3);
	}

	@Override
	public Set<? extends Vertex> getVertices() {
		return new HashSet<>(Arrays.asList(super.no, super.n2, this.n3));
	}
}

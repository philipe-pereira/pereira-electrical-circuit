package br.com.pereiraeng.electricalcircuit.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import br.com.pereiraeng.graph.Vertex;

/**
 * Classe do objeto que representa componentes que podem ter 4 ou mais terminais
 * 
 * @author Philipe PEREIRA
 *
 */
public abstract class CompN extends Comp4 {

	public CompN(String label, int nn) {
		super(label);
		setNs(nn);
	}

	public int getNs() {
		return (nn == null ? 0 : this.nn.size()) + 4;
	}

	public void setNs(int ns) {
		ns -= 4;
		if (nn == null) {
			// ainda não há tabela de nós
			if (ns > 0) {
				this.nn = new ArrayList<>(ns);
				this.pnn = new ArrayList<>(ns);

				for (int i = 0; i < ns; i++) {
					this.nn.add(getNo());
					this.pnn.add(0);
				}
			}
		} else {
			if (ns > nn.size()) {
				// mais nós do que já existe
				this.nn.ensureCapacity(ns);
				this.pnn.ensureCapacity(ns);
				for (int i = 0; i < ns - nn.size(); i++) {
					this.nn.add(getNo());
					this.pnn.add(0);
				}
			} else {
				// menos nós do que já existe
				for (int i = 0; i < nn.size() - ns; i++) {
					this.nn.remove(this.nn.size() - 1);
					this.pnn.remove(this.pnn.size() - 1);
				}
			}
		}
	}

	// ------------------------ NO N ------------------------

	protected ArrayList<No> nn;

	public void setNN(No nn, int index) {
		switch (index) {
		case 0:
			this.setNo(nn);
			break;
		case 1:
			this.setN2(nn);
			break;
		case 2:
			this.setN3(nn);
			break;
		case 3:
			this.setN4(nn);
			break;
		default:
			index -= 4;
			if (this.nn.get(index) != null)
				this.nn.get(index).remove(this);
			this.nn.set(index, nn);
			this.nn.get(index).add(this);
			break;
		}
	}

	public void removeNN(int index) {
		switch (index) {
		case 0:
			this.removeNo();
			break;
		case 1:
			this.removeN2();
			break;
		case 2:
			this.removeN3();
			break;
		case 3:
			this.removeN4();
			break;
		default:
			index -= 4;
			if (this.nn.get(index) != null)
				this.nn.get(index).remove(this);
			this.nn.set(index, null);
			break;
		}
	}

	public No getNN(int index) {
		switch (index) {
		case 0:
			return this.getNo();
		case 1:
			return this.getN2();
		case 2:
			return this.getN3();
		case 3:
			return this.getN4();
		default:
			return this.nn.get(index - 4);
		}
	}

	protected ArrayList<Integer> pnn;

	public void setConnectionPointN(int pn, int index) {
		switch (index) {
		case 0:
			this.setConnectionPoint(pn);
			break;
		case 1:
			this.setConnectionPoint2(pn);
			break;
		case 2:
			this.setConnectionPoint3(pn);
			break;
		case 3:
			this.setConnectionPoint4(pn);
			break;
		default:
			this.pnn.set(index - 4, pn);
			break;
		}
	}

	public int getConnectionPointN(int index) {
		switch (index) {
		case 0:
			return this.getConnectionPoint();
		case 1:
			return this.getConnectionPoint2();
		case 2:
			return this.getConnectionPoint3();
		case 3:
			return this.getConnectionPoint4();
		default:
			return this.pnn.get(index - 4);
		}
	}

	// ----------------- INTERFACE SUPER-ARESTA -----------------

	@Override
	public boolean contains(Vertex v) {
		if (super.contains(v))
			return true;
		else
			for (No no : this.nn)
				if (v.equals(no))
					return true;
		return false;
	}

	@Override
	public Set<? extends Vertex> getVertices() {
		Set<Vertex> out = new HashSet<>(super.getVertices());
		for (No no : this.nn)
			out.add(no);
		return out;
	}
}
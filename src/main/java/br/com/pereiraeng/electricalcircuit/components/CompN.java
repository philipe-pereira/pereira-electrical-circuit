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
		setNumberOfNodes(nn);
	}

	public int getNumberOfNodes() {
		return (extraNodes == null ? 0 : this.extraNodes.size()) + 4;
	}

	public void setNumberOfNodes(int numberOfNodes) {
		numberOfNodes -= 4;
		if (extraNodes == null) {
			// ainda não há tabela de nós
			if (numberOfNodes > 0) {
				this.extraNodes = new ArrayList<>(numberOfNodes);
				this.pnn = new ArrayList<>(numberOfNodes);

				for (int i = 0; i < numberOfNodes; i++) {
					this.extraNodes.add(getNo());
					this.pnn.add(0);
				}
			}
		} else {
			if (numberOfNodes > extraNodes.size()) { // mais nós do que já existe
				this.extraNodes.ensureCapacity(numberOfNodes);
				this.pnn.ensureCapacity(numberOfNodes);
				for (int i = 0; i < numberOfNodes - extraNodes.size(); i++) {
					this.extraNodes.add(getNo());
					this.pnn.add(0);
				}
			} else { // menos nós do que já existe
				while (extraNodes.size() > numberOfNodes) {
					this.extraNodes.remove(this.extraNodes.size() - 1);
					this.pnn.remove(this.pnn.size() - 1);
				}
			}
		}
	}

	// ------------------------ NO N ------------------------

	protected ArrayList<No> extraNodes;

	public void setNN(No no, int index) {
		switch (index) {
		case 0:
			this.setNo(no);
			break;
		case 1:
			this.setN2(no);
			break;
		case 2:
			this.setN3(no);
			break;
		case 3:
			this.setN4(no);
			break;
		default:
			index -= 4;
			if (this.extraNodes.get(index) != null)
				this.extraNodes.get(index).remove(this);
			this.extraNodes.set(index, no);
			this.extraNodes.get(index).add(this);
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
			if (this.extraNodes.get(index) != null)
				this.extraNodes.get(index).remove(this);
			this.extraNodes.set(index, null);
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
			return this.extraNodes.get(index - 4);
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
			for (No no : this.extraNodes)
				if (v.equals(no))
					return true;
		return false;
	}

	@Override
	public Set<? extends Vertex> getVertices() {
		Set<Vertex> out = new HashSet<>(super.getVertices());
		for (No no : this.extraNodes)
			out.add(no);
		return out;
	}
}
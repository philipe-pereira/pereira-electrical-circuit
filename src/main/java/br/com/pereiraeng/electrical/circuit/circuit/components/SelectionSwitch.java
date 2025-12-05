package br.com.pereiraeng.electrical.circuit.circuit.components;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import br.com.pereiraeng.core.Orientation;
import br.com.pereiraeng.graph.Edge;
import br.com.pereiraeng.graph.Vertex;

public class SelectionSwitch extends Comp3 {

	private transient int state = 0;

	public SelectionSwitch(String label, int nn) {
		super(label);
		setOrientation(0);
		setNs(nn);
	}

	// -------------------------------- COMP --------------------------------

	private transient Orientation orientation;

	@Override
	public void setOrientation(int o) {
		this.orientation = Orientation.values()[o];
	}

	@Override
	public void rotate() {
		this.orientation = this.orientation.next();
	}

	// ---------------------- TABLE EDITABLE ----------------------

	@Override
	public int getFieldCount() {
		return 3;
	}

	@Override
	public String getFieldName(int index) {
		switch (index) {
		case 0:
			return "Etiqueta";
		case 1:
			return "Terminais";
		case 2:
			return "Atual";
		default:
			return null;
		}
	}

	@Override
	public Object getField(int index) {
		Object out = null;
		switch (index) {
		case 0:
			out = toString() != null ? toString() : "";
			break;
		case 1:
			out = getNs();
			break;
		case 2:
			out = state;
			break;
		}
		return out;
	}

	@Override
	public void setField(int index, Object obj) {
		switch (index) {
		case 0:
			setLabel((String) obj);
			break;
		case 1:
			this.setNs((int) obj);
			break;
		case 2:
			setState((int) obj);
			break;
		}
	}

	public void setState(int state) {
		this.state = state;
	}

	// ---------------------- XML ----------------------

	@Override
	public String getXML() {
		StringBuilder t = new StringBuilder(String.format("<term>%s,%d;%s,%d;%s,%d", super.no.toString(), super.pn,
				super.n2.toString(), super.pn2, super.n3.toString(), super.pn3));
		if (getNs() > 3)
			for (int i = 0; i < nn.size(); i++)
				t.append(String.format(";%s,%d", this.nn.get(i).toString(), this.pnn.get(i)));
		t.append("</term>");
		return String.format("<sel label=\"%s\" terms=\"%d\">\n<loc dir=\"%d\">%d,%d</loc>\n%s\n</sel>\n", super.label,
				getNs(), this.orientation.ordinal(), x, y, t);
	}

	// ------------------------ Número de nós ------------------------

	/**
	 * Função que retorna o número de terminais
	 * 
	 * @return número de terminais, no mínimo 3
	 */
	public int getNs() {
		return (nn == null ? 0 : this.nn.size()) + 3;
	}

	public void setNs(int ns) {
		ns -= 3;
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
				if (ns == 0) {
					this.nn = null;
					this.pnn = null;
				} else if (ns > 0)
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
		default:
			index -= 3;
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
		default:
			index -= 3;
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
		default:
			return this.nn.get(index - 3);
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
		default:
			this.pnn.set(index - 3, pn);
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
		default:
			return this.pnn.get(index - 3);
		}
	}

	// ---------------------- DRAWER ----------------------

	@Override
	public void drawObject(Graphics2D g) {
		super.drawObject(g);

		// se for ilegível, ao menos que seja final
		// Philipe Pereira, August 24, 2020

		// conector dos demais terminais
		boolean v = Orientation.VERTICAL.equals(this.orientation);
		super.conector(g, super.n2, super.pn2, v ? -3 : 5, v ? 5 : -3);
		super.conector(g, super.n3, super.pn3, v ? 3 : 5, v ? 5 : 3);
		if (getNs() > 3)
			for (int i = 0; i < nn.size(); i++)
				super.conector(g, nn.get(i), pnn.get(i), v ? (9 + 6 * i) : 5, v ? 5 : (9 + 6 * i));

		// desenhar
		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();

		BasicStroke bs = (BasicStroke) g.getStroke();
		BasicStroke dashed = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[] { 5f },
				0f);

		g.drawOval(x0 + (v ? -15 : 10), y0 + (v ? 10 : -15), 30, 30);
		g.drawLine(x0, y0, x0 + (v ? 0 : 10), y0 + +(v ? 10 : 0));

		int n = getNs();
		for (int i = 0; i < n - 1; i++) {
			int xp = x0 + (v ? -30 + 60 * i : 50), yp = y0 + (v ? 50 : -30 + 60 * i);
			if (state == i) {
				g.setStroke(bs);
				g.fillOval(xp - 2, yp - 2, 4, 4);
			} else {
				g.drawOval(xp - 2, yp - 2, 4, 4);
				g.setStroke(dashed);
			}
			g.drawLine(xp, yp, x0 + (v ? -10 + 20 * (i > 1 ? 1 : i) : 36), y0 + (v ? 36 : -10 + 20 * (i > 1 ? 1 : i)));
		}
		g.setStroke(bs);
	}

	@Override
	public Area getClickableArea() {
		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();
		return new Area(new Rectangle2D.Float(x0, y0, 50, 50));
	}

	// ----------------- INTERFACE SUPER-ARESTA -----------------

	@Override
	public Set<Edge> getEdges(Vertex v) {
		// TODO Auto-generated method stub
		return null;
	}

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

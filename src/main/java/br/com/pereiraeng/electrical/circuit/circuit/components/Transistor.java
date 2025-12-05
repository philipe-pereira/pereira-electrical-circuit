package br.com.pereiraeng.electrical.circuit.circuit.components;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Set;

import br.com.pereiraeng.core.Orientation;
import br.com.pereiraeng.graph.Edge;
import br.com.pereiraeng.graph.Vertex;

public class Transistor extends Comp3 {

	public enum TransistorType {
		BJT, MOS, IGBT
	}

	private TransistorType tt;

	public Transistor(String label, TransistorType tt) {
		super(label);
		this.tt = tt;
	}

	public void setTransistorType(TransistorType tt) {
		this.tt = tt;
	}

	public TransistorType getTransistorType() {
		return tt;
	}

	// ---------------------- HYPEREDGE ----------------------

	@Override
	public Set<Edge> getEdges(Vertex v) {
		// TODO Auto-generated method stub
		return null;
	}

	// ---------------------- TABLE EDITABLE ----------------------

	@Override
	public int getFieldCount() {
		return 2;
	}

	@Override
	public String getFieldName(int index) {
		switch (index) {
		case 0:
			return "Etiqueta";
		case 1:
			return "Tipo";
		default:
			return null;
		}
	}

	@Override
	public Object getField(int i) {
		Object out = null;
		switch (i) {
		case 0:
			out = toString() != null ? toString() : "";
			break;
		case 1:
			out = getTransistorType();
			break;
		default:
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
			setTransistorType((TransistorType) obj);
			break;
		default:
			break;
		}
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

	// ---------------------- DRAWER ----------------------

	@Override
	public void drawObject(Graphics2D g) {
		super.drawObject(g);

		// conector dos demais terminais
		boolean b = Orientation.VERTICAL.equals(this.orientation);
		super.conector(g, this.n2, this.pn2, b ? 5 : 0, b ? 0 : 5);
		super.conector(g, this.n3, this.pn3, b ? 0 : 9, b ? 9 : 0);

		// desenhar
//		int x0 = super.x * super.grade.getWidth();
//		int y0 = super.y * super.grade.getHeight();

		// TODO
	}

	@Override
	public Area getClickableArea() {
		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();
		if (Orientation.VERTICAL.equals(this.orientation))
			x0 -= 25;
		else
			y0 -= 25;
		return new Area(new Rectangle2D.Float(x0, y0, 50, 50));
	}

	// ----------------------------- EXPORT -----------------------------

	@Override
	public String getTikz() {
		String out = super.getTikz();
		boolean b = Orientation.VERTICAL.equals(this.orientation);

		// conector do segundo terminal
		No no = getN2();
		no.setIndex(getConnectionPoint2());
		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(), b ? 0 : 5,
				b ? -5 : 0);

		// TODO

		return out + "\\end{scope}\n";
	}

	@Override
	public String getSVG() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVML() {
		// TODO Auto-generated method stub
		return null;
	}

	// ----------------------------- XML -----------------------------

	public String getXML() {
		return String.format(
				"<tst label=\"%s\" type=\"%s\">\n<loc dir=\"%d\">%d,%d</loc>\n<term>%s,%d;%s,%d</term>\n</tst>\n",
				super.label, this.getTransistorType(), this.orientation.ordinal(), x, y, super.no.toString(), super.pn,
				super.n2.toString(), super.pn2);
	}
}
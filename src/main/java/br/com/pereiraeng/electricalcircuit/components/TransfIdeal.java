package br.com.pereiraeng.electricalcircuit.components;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Locale;
import java.util.Set;

import br.com.pereiraeng.core.Direction;
import br.com.pereiraeng.core.Orientation;
import br.com.pereiraeng.drawing.drawutils.TikZ;
import br.com.pereiraeng.electricalcircuit.CircuitDrawer;
import br.com.pereiraeng.graph.Edge;
import br.com.pereiraeng.graph.Vertex;

public class TransfIdeal extends Comp4 {

	private int ne1, ne2;

	public TransfIdeal(String label, int ne1, int ne2) {
		super(label);
		this.ne1 = ne1;
		this.ne2 = ne2;
		this.orientation = Orientation.HORIZONTAL;
	}

	// ---------------------- GETTER'S N' SETTER'S ----------------------

	public void setNe1(int ne1) {
		this.ne1 = ne1;
	}

	public void setNe2(int ne2) {
		this.ne2 = ne2;
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
			return "Espiras primário";
		case 2:
			return "Espiras secundário";
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
			out = ne1;
			break;
		case 2:
			out = ne2;
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
			setNe1((int) obj);
			break;
		case 2:
			setNe2((int) obj);
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
		boolean v = Orientation.VERTICAL.equals(this.orientation);
		super.conector(g, this.n2, this.pn2, v ? 5 : 0, v ? 0 : 5);
		super.conector(g, this.n3, this.pn3, v ? 0 : 3, v ? 3 : 0);
		super.conector(g, this.n4, this.pn4, v ? 5 : 3, v ? 3 : 5);

		// desenhar
		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();

		// desenhar primário
		CircuitDrawer.drawIndutor(x0, y0 + (v ? 0 : 5 * super.grade.getHeight()), v ? Direction.RIGHT : Direction.UP,
				"", Double.NaN, g);
		// ponto do primário
		g.fillOval(x0 + (v ? 0 : 1) * super.grade.getWidth() - 2, y0 + (v ? 1 : 0) * super.grade.getHeight() - 2, 4, 4);

		// desenhar secundário
		CircuitDrawer.drawIndutor(x0 + (v ? 0 : 3 * super.grade.getWidth()) + (v ? 5 * super.grade.getWidth() : 0),
				y0 + (v ? 3 * super.grade.getHeight() : 0), v ? Direction.LEFT : Direction.DOWN, "", Double.NaN, g);
		// ponto do secundário
		g.fillOval(x0 + (v ? 0 : 2) * super.grade.getWidth() - 2, y0 + (v ? 2 : 0) * super.grade.getHeight() - 2, 4, 4);
	}

	@Override
	public Area getClickableArea() {
		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();
		boolean v = Orientation.VERTICAL.equals(this.orientation);
		return new Area(new Rectangle2D.Float(x0, y0, v ? 50 : 30, v ? 30 : 50));
	}

	// ----------------------------- EXPORT -----------------------------

	@Override
	public String getTikz() {
		String out = super.getTikz();
		boolean v = Orientation.VERTICAL.equals(this.orientation);

		// conector do segundo terminal da primeira bobina
		No no = getN2();
		no.setIndex(getConnectionPoint2());

		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(), v ? 5 : 0,
				v ? 0 : -5);

		// desenhar primário
		out += TikZ.drawIndutor(0, 0, v ? Direction.RIGHT : Direction.DOWN, "", Double.NaN);
		// ponto do acoplamento
		out += String.format(Locale.US, "\\draw (%d,%d) circle (%fcm);\n", v ? 0 : 1, v ? -1 : 0, .2f);

		// conectores do outro terminal
		no = getN3();
		no.setIndex(getConnectionPoint3());
		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(), v ? 0 : 3,
				v ? -3 : 0);

		no = getN4();
		no.setIndex(getConnectionPoint4());
		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(), v ? 5 : 3,
				v ? -3 : -5);

		// desenhar secundário
		out += TikZ.drawIndutor(v ? 0 : 3, v ? -3 : 0, v ? Direction.LEFT : Direction.UP, "", Double.NaN);

		// ponto do acoplamento
		out += String.format(Locale.US, "\\draw (%d,%d) circle (%fcm);\n", v ? 0 : 2, v ? -2 : 0, .2f);

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
				"<trfIde label=\"%s\" ne1=\"%d\" ne2=\"%d\">\n<loc dir=\"%d\">%d,%d</loc>\n<term>%s,%d;%s,%d;%s,%d;%s,%d</term>\n</trfIde>\n",
				super.label, this.ne1, this.ne2, this.orientation.ordinal(), x, y, super.no.toString(), super.pn,
				super.n2.toString(), super.pn2, super.n3.toString(), super.pn3, super.n4.toString(), super.pn4);
	}

	// ----------------- INTERFACE SUPER-ARESTA -----------------

	@Override
	public Set<Edge> getEdges(Vertex v) {
		// TODO Auto-generated method stub
		return null;
	}
}
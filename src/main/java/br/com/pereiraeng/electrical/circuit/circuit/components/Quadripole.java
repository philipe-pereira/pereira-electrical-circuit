package br.com.pereiraeng.electrical.circuit.circuit.components;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Locale;
import java.util.Set;

import br.com.pereiraeng.math.Complex;
import br.com.pereiraeng.math.Vec;
import br.com.pereiraeng.core.Orientation;
import br.com.pereiraeng.graph.Edge;
import br.com.pereiraeng.graph.Vertex;

/**
 * Classe do objeto que representa um quadripolo do circuito
 * 
 * @author Philipe PEREIRA
 *
 */
public class Quadripole extends Comp4 {

	protected String symbol;

	private Complex[][] i2o;

	public Quadripole(String label) {
		super(label);
		this.setOrientation(0);
		this.symbol = "";
	}

	public String getSymbol() {
		return symbol;
	}

	public void setI2o(Complex[][] i2o) {
		this.i2o = i2o;
	}

	public Complex[][] getI2o() {
		return i2o;
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
			return "Símbolos";
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
			out = this.symbol;
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
			this.symbol = (String) obj;
			break;
		}
	}

	// ----------------------------- EXPORT -----------------------------

	@Override
	public String getTikz() {
		String out = super.getTikz();

		boolean v = Orientation.VERTICAL.equals(this.orientation);

		// conector do segundo terminal
		No no = getN2();
		no.setIndex(getConnectionPoint2());
		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(), v ? 5 : 0,
				-(v ? 0 : 5));

		no = getN3();
		no.setIndex(getConnectionPoint3());
		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(), v ? 0 : 9,
				-(v ? 9 : 0));

		no = getN4();
		no.setIndex(getConnectionPoint4());
		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(), v ? 5 : 9,
				-(v ? 9 : 5));

		out += String.format("\\draw (%d,%d) rectangle (%d,%d);\n", v ? -1 : 1, v ? -1 : 1, v ? 6 : 8, v ? -8 : -6);

		// símbolo
		if (!"".equals(symbol))
			out += String.format(Locale.US, "\\draw (%d,%d) node {%s};\n", v ? 1 : 3, v ? -5 : -3, symbol);

		// conectores
		// 1
		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", 0, 0, v ? 0 : 1, v ? -1 : 0);
		// 2
		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", v ? 0 : 9, v ? -9 : 0, v ? 0 : 8, v ? -8 : 0);
		// 3
		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", v ? 5 : 0, v ? 0 : -5, v ? 5 : 1, v ? -1 : -5);
		// 4
		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", v ? 5 : 9, v ? -9 : -5, v ? 5 : 8, v ? -8 : -5);

		return out + "\\end{scope}\n";
	}

	@Override
	public String getSVG() {
		String out = super.getSVG();

		boolean v = Orientation.VERTICAL.equals(this.orientation);

		// conector dos terminais

		// conector 2
		No no = getN2();
		no.setIndex(getConnectionPoint2());
		out += String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"rgb(0,0,0)\"/>\n", v ? 50 : 0,
				v ? 0 : 50, (no.getX() - getX()) * grade.getWidth(), (no.getY() - getY()) * grade.getHeight());
		// conector 3
		no = getN3();
		no.setIndex(getConnectionPoint3());
		out += String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"rgb(0,0,0)\"/>\n", v ? 0 : 90,
				v ? 90 : 0, (no.getX() - getX()) * grade.getWidth(), (no.getY() - getY()) * grade.getHeight());
		// conector 4
		no = getN4();
		no.setIndex(getConnectionPoint4());
		out += String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"rgb(0,0,0)\"/>\n", v ? 50 : 90,
				v ? 90 : 50, (no.getX() - getX()) * grade.getWidth(), (no.getY() - getY()) * grade.getHeight());

		// caixa
		out += String.format(
				"<rect x=\"%d\" y=\"%d\" width=\"70\" height=\"70\" stroke=\"rgb(0,0,0)\" fill=\"none\"/>\n",
				v ? -10 : 10, v ? 10 : -10);

		// símbolo
		if (!"".equals(symbol))
			out += String.format("<text x=\"%d\" y=\"%d\" fill=\"rgb(0,0,0)\" font-size=\"12\">%s</text>", v ? 10 : 30,
					v ? 50 : 30, symbol);

		// conectores
		// 1
		out += String.format("<line x1=\"0\" y1=\"0\" x2=\"%d\" y2=\"%d\" stroke=\"rgb(0,0,0)\"/>\n", v ? 0 : 10,
				v ? 10 : 0);
		// 2
		out += String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"rgb(0,0,0)\"/>\n", v ? 0 : 90,
				v ? 90 : 0, v ? 0 : 80, v ? 80 : 0);
		// 3
		out += String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"rgb(0,0,0)\"/>\n", v ? 50 : 0,
				v ? 0 : 50, v ? 50 : 10, v ? 10 : 50);
		// 4
		out += String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"rgb(0,0,0)\"/>\n", v ? 50 : 90,
				v ? 90 : 50, v ? 50 : 80, v ? 80 : 50);

		return out + "</g>\n";
	}

	@Override
	public String getVML() {
		// TODO Auto-generated method stub
		return null;
	}

	// ----------------------------- XML -----------------------------

	@Override
	public String getXML() {
		String out = String.format(
				"<quad label=\"%s\" symbol=\"%s\">\n<loc dir=\"%d\">%d,%d</loc>\n<term>%s,%d;%s,%d;%s,%d;%s,%d</term>\n",
				super.label, this.symbol, this.orientation.ordinal(), x, y, super.no.toString(), super.pn,
				super.n2.toString(), super.pn2, super.n3.toString(), super.pn3, super.n4.toString(), super.pn4);
		if (i2o != null)
			out += Vec.toXml(i2o);
		return out + "</quad>\n";
	}

	// -------------------------------- DRAWER --------------------------------

	@Override
	public void drawObject(Graphics2D g) {
		super.drawObject(g);

		// conector dos demais terminais
		boolean b = Orientation.VERTICAL.equals(this.orientation);
		super.conector(g, this.n2, this.pn2, b ? 5 : 0, b ? 0 : 5);
		super.conector(g, this.n3, this.pn3, b ? 0 : 9, b ? 9 : 0);
		super.conector(g, this.n4, this.pn4, b ? 5 : 9, b ? 9 : 5);

		// desenhar caixa do quadripolo
		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();

		int size = 7 * super.grade.getWidth();

		g.drawRect(x0 + (b ? -1 : 1) * super.grade.getWidth(), y0 + (b ? 1 : -1) * super.grade.getHeight(), size, size);

		// símbolo
		if (!"".equals(symbol))
			g.drawString(symbol, x0 + (b ? 1 : 3) * super.grade.getWidth(), y0 + (b ? 5 : 3) * super.grade.getHeight());

		// conectores
		// 1
		g.drawLine(x0, y0, x0 + (b ? 0 : 1) * super.grade.getHeight(), y0 + (b ? 1 : 0) * super.grade.getHeight());
		// 2
		g.drawLine(x0 + (b ? 0 : 9) * super.grade.getHeight(), y0 + (b ? 9 : 0) * super.grade.getHeight(),
				x0 + (b ? 0 : 8) * super.grade.getHeight(), y0 + (b ? 8 : 0) * super.grade.getHeight());
		// 3
		g.drawLine(x0 + (b ? 5 : 0) * super.grade.getHeight(), y0 + (b ? 0 : 5) * super.grade.getHeight(),
				x0 + (b ? 5 : 1) * super.grade.getHeight(), y0 + (b ? 1 : 5) * super.grade.getHeight());
		// 4
		g.drawLine(x0 + (b ? 5 : 9) * super.grade.getHeight(), y0 + (b ? 9 : 5) * super.grade.getHeight(),
				x0 + (b ? 5 : 8) * super.grade.getHeight(), y0 + (b ? 8 : 5) * super.grade.getHeight());
	}

	@Override
	public Area getClickableArea() {
		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();

		int size = 7 * super.grade.getWidth();
		boolean v = Orientation.VERTICAL.equals(this.orientation);

		return new Area(new Rectangle2D.Float(x0 + (v ? -1 : 1) * super.grade.getWidth(),
				y0 + (v ? 1 : -1) * super.grade.getHeight(), size, size));
	}

	// -------------------------------- COMP --------------------------------

	protected transient Orientation orientation;

	@Override
	public void setOrientation(int o) {
		this.orientation = Orientation.values()[o];
	}

	@Override
	public void rotate() {
		this.orientation = this.orientation.next();
	}

	public int getOrientation() {
		return this.orientation.ordinal();
	}

	// ----------------- INTERFACE SUPER-ARESTA -----------------

	@Override
	public Set<Edge> getEdges(Vertex v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vertex getOpposite(Vertex v) {
		if (v.equals(no))
			return n2;
		else if (v.equals(n2))
			return no;
		else if (v.equals(n3))
			return n4;
		else if (v.equals(n4))
			return n3;
		return null;
	}
}
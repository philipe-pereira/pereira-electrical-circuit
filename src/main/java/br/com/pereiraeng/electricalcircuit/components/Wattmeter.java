package br.com.pereiraeng.electricalcircuit.components;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Locale;
import java.util.Set;

import br.com.pereiraeng.core.Orientation;
import br.com.pereiraeng.drawing.TikZ;
import br.com.pereiraeng.electricalcircuit.solving.CircuitCalc;
import br.com.pereiraeng.graph.Edge;
import br.com.pereiraeng.graph.Vertex;
import br.com.pereiraeng.math.Complex;
import br.com.pereiraeng.math.Multiplicador;

public class Wattmeter extends Comp3 {

	private transient Number v = Double.NaN, i = Double.NaN;

	public Wattmeter(String label) {
		super(label);
		setOrientation(0);
	}

	public void setVI(Number v, Number i) {
		this.v = v;
		this.i = i;
	}

	public Number getV() {
		return v;
	}

	public Number getI() {
		return i;
	}

	// ---------------------- TABLE EDITABLE ----------------------

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public String getFieldName(int index) {
		switch (index) {
		case 0:
			return "Etiqueta";
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
		super.conector(g, super.n2, super.pn2, v ? 0 : 5, v ? 5 : 0);
		super.conector(g, super.n3, super.pn3, v ? 5 : 3, v ? 3 : 5);

		// desenhar
		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();

		if (v) {
			g.drawOval(x0 - 15, y0 + 10, 30, 30);

			Font f = g.getFont();
			Font f0 = f.deriveFont(20f);
			g.setFont(f0);
			g.drawString("W", x0 - 6, y0 + 32);
			g.setFont(f);

			g.drawLine(x0, y0, x0, y0 + 10);
			g.drawLine(x0, y0 + 40, x0, y0 + 50);
			g.drawLine(x0 + 15, y0 + 30, x0 + 50, y0 + 30);
		} else {
			g.drawOval(x0 + 10, y0 - 15, 30, 30);

			Font f = g.getFont();
			Font f0 = f.deriveFont(20f);
			g.setFont(f0);
			g.drawString("W", x0 + 19, y0 + 7);
			g.setFont(f);

			g.drawLine(x0, y0, x0 + 10, y0);
			g.drawLine(x0 + 40, y0, x0 + 50, y0);
			g.drawLine(x0 + 30, y0 + 15, x0 + 30, y0 + 50);
		}

		Number valueV = getV();
		Number valueI = getI();
		double p, q;
		if (valueV instanceof Complex && valueI instanceof Complex) {
			Complex s = CircuitCalc.getS((Complex) valueV, (Complex) valueI);
			p = s.getRe();
			q = s.getIm();
		} else {
			p = valueV.doubleValue() * valueI.doubleValue();
			q = Double.NaN;
		}

		if (!Double.isNaN(p))
			g.drawString(Multiplicador.getMult(p, 3, Multiplicador.POW3) + "W", x0 + 18, y0 + 30);
		if (!Double.isNaN(q))
			g.drawString(Multiplicador.getMult(q, 3, Multiplicador.POW3) + "VAr", x0 + 18, y0 + 45);
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

		no = getN3();
		no.setIndex(getConnectionPoint3());
		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(), b ? 5 : 3,
				b ? -3 : -5);

		out += String.format(Locale.US, TikZ.CIRC_COMM, x + (b ? 0f : 2.5f), y + (b ? -2.5f : 0), 1.5f);

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
				"<wmt label=\"%s\">\n<loc dir=\"%d\">%d,%d</loc>\n<term>%s,%d;%s,%d;%s,%d</term>\n</wmt>\n",
				super.label, this.orientation.ordinal(), x, y, super.no.toString(), super.pn, super.n2.toString(),
				super.pn2, super.n3.toString(), super.pn3);
	}

	// ----------------- INTERFACE SUPER-ARESTA -----------------

	@Override
	public Set<Edge> getEdges(Vertex v) {
		// TODO Auto-generated method stub
		return null;
	}
}
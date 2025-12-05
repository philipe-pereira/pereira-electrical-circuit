package br.com.pereiraeng.electricalcircuit.components;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Map.Entry;

import br.com.pereiraeng.math.Complex;
import br.com.pereiraeng.core.Orientation;

import java.util.TreeMap;

public class ZfComp extends Zcomp {

	private TreeMap<Double, Complex> zf;

	public ZfComp(String label) {
		this(label, new TreeMap<Double, Complex>());
		this.zf.put(0., new Complex(1, 0));
	}

	public ZfComp(String label, TreeMap<Double, Complex> zf) {
		super(label);
		this.zf = zf;
		super.orientation = Orientation.VERTICAL;
	}

	@Override
	public Complex getZ(double f) {
		Complex out = this.zf.get(f);
		if (out == null)
			out = this.zf.firstEntry().getValue();
		return out;
	}

	public void setZ(double f, Complex z) {
		this.zf.put(f, z);
	}

	public TreeMap<Double, Complex> getZf() {
		return zf;
	}

	public void setZf(TreeMap<Double, Complex> zf) {
		this.zf = zf;
	}

	// ---------------------- DRAWER ----------------------

	@Override
	public void drawObject(Graphics2D g) {
		super.drawObject(g);

		// conector do segundo terminal
		boolean b = Orientation.VERTICAL.equals(super.orientation);
		super.conector(g, super.n2, super.pn2, b ? 0 : 5, b ? 5 : 0);

		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();

		// caixa
		g.drawRect(x0 + (b ? -1 : 1) * super.grade.getWidth(), y0 + (b ? 1 : -1) * super.grade.getHeight(),
				(b ? 2 : 3) * super.grade.getWidth(), (b ? 3 : 2) * super.grade.getHeight());

		// terminar de desenhar os conectores
		g.drawLine(x0, y0, x0 + (b ? 0 : 1) * super.grade.getHeight(), y0 + (b ? 1 : 0) * super.grade.getHeight());
		g.drawLine(x0 + (b ? 0 : 4) * super.grade.getWidth(), y0 + (b ? 4 : 0) * super.grade.getHeight(),
				x0 + (b ? 0 : 5) * super.grade.getHeight(), y0 + (b ? 5 : 0) * super.grade.getHeight());
	}

	@Override
	public Area getClickableArea() {
		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();
		boolean b = Orientation.VERTICAL.equals(super.orientation);

		return new Area(new Rectangle2D.Float(x0 + (b ? -1 : 1) * super.grade.getWidth(),
				y0 + (b ? 1 : -1) * super.grade.getHeight(), (b ? 2 : 3) * super.grade.getWidth(),
				(b ? 3 : 2) * super.grade.getHeight()));
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
			return "Z(f)";
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
			out = zf;
			break;
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setField(int index, Object obj) {
		switch (index) {
		case 0:
			setLabel((String) obj);
			break;
		case 1:
			zf.clear();
			zf.putAll((Map<? extends Double, ? extends Complex>) obj);
			break;
		}
	}

	// ----------------------------- EXPORT -----------------------------

	@Override
	public String getTikz() {
		String out = super.getTikz();
		boolean v = Orientation.VERTICAL.equals(super.orientation);

		// conector do segundo terminal
		No no = getN2();
		no.setIndex(getConnectionPoint2());
		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(), v ? 0 : 5,
				v ? -5 : 0);

		// caixa
		out += String.format("\\draw (%d,%d) rectangle (%d,%d);\n", v ? -1 : 1, v ? -1 : 1, v ? 1 : 4, v ? -4 : -1);

		// terminar de desenhar os conectores
		out += String.format("\\draw (0,0) -- (%d,%d);\n", v ? 0 : 1, v ? -1 : 0);
		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", v ? 0 : 4, v ? -4 : 0, v ? 0 : 5, v ? -5 : 0);

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
		out += String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"rgb(0,0,0)\"/>\n", v ? 0 : 50,
				v ? -50 : 0, (no.getX() - getX()) * grade.getWidth(), (no.getY() - getY()) * grade.getHeight());

		// caixa
		out += String.format(
				"<rect x=\"%d\" y=\"%d\" width=\"70\" height=\"70\" stroke=\"rgb(0,0,0)\" fill=\"none\"/>\n",
				v ? -10 : 10, v ? -10 : 10, v ? 10 : 40, v ? -40 : -10);

		// conectores
		// 1
		out += String.format("<line x1=\"0\" y1=\"0\" x2=\"%d\" y2=\"%d\" stroke=\"rgb(0,0,0)\"/>\n", v ? 0 : 10,
				v ? -10 : 0);
		// 2
		out += String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"rgb(0,0,0)\"/>\n", v ? 0 : 40,
				v ? -40 : 0, v ? 0 : 50, v ? -50 : 0);

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
		String out = String.format("<zfc label=\"%s\">\n<loc dir=\"%d\">%d,%d</loc>\n<term>%s,%d;%s,%d</term>\n",
				super.label, super.orientation.ordinal(), x, y, super.no.toString(), super.pn, super.n2.toString(),
				super.pn2);
		for (Entry<Double, Complex> e : this.zf.entrySet()) {
			Complex c = e.getValue();
			out += String.format("<z f=\"%g\" value=\"%g;%g\"/>\n", e.getKey(), c.getRe(), c.getIm());
		}
		return out + "</zfc>\n";
	}
}

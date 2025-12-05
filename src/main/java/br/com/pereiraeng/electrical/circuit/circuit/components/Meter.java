package br.com.pereiraeng.electrical.circuit.circuit.components;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Locale;

import br.com.pereiraeng.math.Complex;
import br.com.pereiraeng.math.Multiplicador;
import br.com.pereiraeng.core.ExtendedMath;
import br.com.pereiraeng.core.Orientation;
import br.com.pereiraeng.drawing.drawutils.TikZ;

/**
 * Classe do objeto que representa um medidor elétrico de dois terminais
 * (amperímetro ou voltímetro)
 * 
 * @author Philipe PEREIRA
 *
 */
public class Meter extends Comp2 {

	public enum GrandezaMed {
		A, V
	}

	private static final int COMP_LENGTH = 50;

	public Meter(String label) {
		super(label);
	}

	private GrandezaMed type;

	private Orientation orientation;

	private transient Number value = Double.NaN;

	public Meter(String label, GrandezaMed type) {
		super(label);
		this.setType(type);
		this.setOrientation(0);
	}

	public GrandezaMed getType() {
		return type;
	}

	public void setType(GrandezaMed type) {
		this.type = type;
	}

	public void setValue(Number value) {
		this.value = value;
	}

	public Number getValue() {
		return value;
	}

	// ---------------------- ORIENTAÇÃO ----------------------

	@Override
	public void setOrientation(int o) {
		this.orientation = Orientation.values()[o];
	}

	@Override
	public void rotate() {
		this.orientation = orientation.next();
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
	public Object getField(int index) {
		Object out = null;
		switch (index) {
		case 0:
			out = toString() != null ? toString() : "";
			break;
		case 1:
			out = getType();
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
			setType((GrandezaMed) obj);
			break;
		}
	}

	// ---------------------- DRAWER ----------------------

	@Override
	public void drawObject(Graphics2D g) {
		super.drawObject(g);

		// conector do segundo terminal
		boolean v = Orientation.VERTICAL == orientation;
		conector(g, super.n2, super.pn2, v ? 0 : 5, v ? 5 : 0);

		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();

		if (v) {
			g.drawOval(x0 - 15, y0 + 10, 30, 30);

			Font f = g.getFont();
			Font f0 = f.deriveFont(20f);
			g.setFont(f0);
			g.drawString(type.name(), x0 - 6, y0 + 32);
			g.setFont(f);

			g.drawLine(x0, y0, x0, y0 + 10);
			g.drawLine(x0, y0 + 40, x0, y0 + COMP_LENGTH);
		} else {
			g.drawOval(x0 + 10, y0 - 15, 30, 30);

			Font f = g.getFont();
			Font f0 = f.deriveFont(20f);
			g.setFont(f0);
			g.drawString(type.name(), x0 + 19, y0 + 7);
			g.setFont(f);

			g.drawLine(x0, y0, x0 + 10, y0);
			g.drawLine(x0 + 40, y0, x0 + COMP_LENGTH, y0);
		}

		double v0 = Double.NaN;
		if (value instanceof Complex) {
			double arg = ((Complex) value).getArg();
			v0 = (arg < ExtendedMath.PI_2 && arg > -ExtendedMath.PI_2 ? 1 : -1) * ((Complex) value).getMod();
		} else
			v0 = value.doubleValue();

		if (!Double.isNaN(v0)) {
			if (v) {
				g.fillPolygon(new int[] { x0, x0 - 5, x0 + 5 },
						new int[] { y0 + (v0 > 0 ? 8 : 3), y0 + (v0 > 0 ? 3 : 8), y0 + (v0 > 0 ? 3 : 8) }, 3);
				g.drawString(Multiplicador.getMult(Math.abs(v0), 3, Multiplicador.POW3) + this.type, x0 + 18, y0 + 30);
			} else {
				g.fillPolygon(new int[] { x0 + (v0 > 0 ? 8 : 3), x0 + (v0 > 0 ? 3 : 8), x0 + (v0 > 0 ? 3 : 8) },
						new int[] { y0, y0 - 5, y0 + 5 }, 3);
				g.drawString(Multiplicador.getMult(Math.abs(v0), 3, Multiplicador.POW3) + this.type, x0 + 10, y0 + 28);
			}
		}
	}

	@Override
	public Area getClickableArea() {
		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();
		if (Orientation.VERTICAL.equals(orientation))
			x0 -= 25;
		else
			y0 -= 25;
		return new Area(new Rectangle2D.Float(x0, y0, 50, 50));
	}

	// -------------------------- EXPORT --------------------------

	@Override
	public String getTikz() {
		String out = super.getTikz();

		boolean b = Orientation.VERTICAL.equals(orientation);

		// conector do segundo terminal
		No no = getN2();
		no.setIndex(getConnectionPoint2());
		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(), b ? 0 : 5,
				b ? -5 : 0);

		out += String.format(Locale.US, "\\draw (0,0) -- (%d,%d);\n", b ? 0 : 1, b ? -1 : 0);
		out += String.format(Locale.US, TikZ.CIRC_COMM, b ? 0f : 2.5f, b ? -2.5f : 0, 1.5f);
		out += String.format(Locale.US, "\\draw (%d,%d) -- (%d,%d);\n", b ? 0 : 4, b ? -4 : 0, b ? 0 : 5, b ? -5 : 0);

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

	// -------------------------- XML --------------------------

	public String getXML() {
		return "<meter label=\"" + super.label + "\" type=\"" + this.getType().name() + "\">\n<loc dir=\""
				+ this.orientation.ordinal() + "\">" + x + "," + y + "</loc>\n<term>" + super.no.toString() + ","
				+ super.pn + ";" + super.n2.toString() + "," + super.pn2 + "</term>\n</meter>\n";
	}
}

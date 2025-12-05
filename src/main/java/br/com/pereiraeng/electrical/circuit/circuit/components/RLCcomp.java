package br.com.pereiraeng.electrical.circuit.circuit.components;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import br.com.pereiraeng.math.Complex;
import br.com.pereiraeng.core.ExtendedMath;
import br.com.pereiraeng.core.Orientation;
import br.com.pereiraeng.drawing.drawutils.TikZ;
import br.com.pereiraeng.electrical.circuit.circuit.CircuitDrawer;
import br.com.pereiraeng.electrical.circuit.circuit.RLCZ;
import br.com.pereiraeng.electrical.circuit.circuit.RLCZ.LinearType;

/**
 * Classe do objeto que representa um elemento linear do circuito (um resistor,
 * um capacitor ou um indutor)
 * 
 * @author Philipe PEREIRA
 *
 */
public class RLCcomp extends Zcomp {

	private RLCZ rlc;

	public RLCcomp(String label, LinearType type) {
		this(label, new RLCZ(type, Double.NaN));
	}

	public RLCcomp(String label, RLCZ rlc) {
		super(label);
		this.rlc = rlc;
		this.setOrientation(0);
	}

	// -------------------- GETTERS AND SETTERS --------------------

	public RLCZ getRLC() {
		return this.rlc;
	}

	public LinearType getType() {
		return (LinearType) rlc.getType();
	}

	public void setType(LinearType type) {
		this.rlc.setType(type);
	}

	public double getValue() {
		return rlc.getValue();
	}

	public void setValue(double value) {
		this.rlc.setValue(value);
	}

	@Override
	public Complex getZ(double f) {
		return this.rlc.getZ(ExtendedMath.TWO_PI * f);
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
			return "Tipo";
		case 2:
			return "Valor";
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
			out = getType();
			break;
		case 2:
			out = getValue();
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
			setType((LinearType) obj);
			break;
		case 2:
			setValue((double) obj);
			break;
		default:
			break;
		}
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

		switch (this.getType()) {
		case R:
			CircuitDrawer.drawResistor(x0, y0, super.orientation, super.label, this.rlc.getValue(), g);
			break;
		case L:
			CircuitDrawer.drawIndutor(x0, y0, super.orientation, super.label, this.rlc.getValue(), g);
			break;
		case C:
			CircuitDrawer.drawCapacitor(x0, y0, super.orientation, super.label, this.rlc.getValue(), g);
			break;
		default:
			break;
		}
	}

	@Override
	public Area getClickableArea() {
		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();
		if (Orientation.VERTICAL.equals(super.orientation))
			x0 -= 25;
		else
			y0 -= 25;
		return new Area(new Rectangle2D.Float(x0, y0, 50, 50));
	}

	// ----------------------------- EXPORT -----------------------------

	@Override
	public String getTikz() {
		String out = super.getTikz();
		boolean b = Orientation.VERTICAL.equals(super.orientation);

		// conector do segundo terminal
		No no = getN2();
		no.setIndex(getConnectionPoint2());
		int x1 = no.getX() - getX(), y1 = getY() - no.getY(), x2 = b ? 0 : 5, y2 = b ? -5 : 0;
		if (x1 != x2 || y1 != y2)
			out += String.format("\\draw (%d,%d) -- (%d,%d);\n", x1, y1, x2, y2);

		switch (this.getType()) {
		case R:
			out += TikZ.drawResistor(0f, 0f, super.orientation, super.label, this.rlc.getValue());
			break;
		case L:
			out += TikZ.drawIndutor(0f, 0f, super.orientation, super.label, this.rlc.getValue());
			break;
		case C:
			out += TikZ.drawCapacitor(0f, 0f, super.orientation, super.label, this.rlc.getValue());
			break;
		default:
			break;
		}
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
				"<rlc label=\"%s\" type=\"%s\" value=\"%g\">\n<loc dir=\"%d\">%d,%d</loc>\n<term>%s,%d;%s,%d</term>\n</rlc>\n",
				super.label, this.getType().name(), this.rlc.getValue(), super.orientation.ordinal(), x, y,
				super.no.toString(), super.pn, super.n2.toString(), super.pn2);
	}
}
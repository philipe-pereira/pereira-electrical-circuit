package br.com.pereiraeng.electricalcircuit.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import br.com.pereiraeng.math.Complex;
import br.com.pereiraeng.math.Multiplicador;
import br.com.pereiraeng.core.Direction;
import br.com.pereiraeng.core.ExtendedMath;
import br.com.pereiraeng.drawing.drawutils.DrawingUtils;

public class CurtoAberto extends Comp2 {

	/**
	 * <code>true</code> para curto-circuito, <code>false</code> para aberto
	 */
	private boolean curto;

	private transient Number value = Double.NaN;

	/**
	 * Construtor de um curto-circuito ou de um circuito em aberto
	 * 
	 * @param label etiqueta
	 * @param curto <code>true</code> para curto-circuito, <code>false</code> para
	 *              aberto
	 */
	public CurtoAberto(String label, boolean curto) {
		super(label);
		this.curto = curto;
		this.direction = Direction.UP;
	}

	// -------------------- GETTERS AND SETTERS --------------------

	public boolean isCurto() {
		return curto;
	}

	public void setCurto(boolean curto) {
		this.curto = curto;
	}

	public void setValue(Number value) {
		this.value = value;
	}

	public Number getValue() {
		return value;
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
			return "Curto-circuito";
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
			out = isCurto();
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
			setCurto((boolean) obj);
			break;
		default:
			break;
		}
	}

	// -------------------------------- COMP --------------------------------

	private Direction direction;

	@Override
	public void setOrientation(int o) {
		// este elemento não pode assumir os valores ímpares (diagonais)
		this.direction = Direction.values()[o - o % 2];
	}

	@Override
	public void rotate() {
		// este elemento não pode assumir os valores ímpares (diagonais)
		this.direction = direction.next();
	}

	// ---------------------- DRAWER ----------------------

	private static final int LENGTH = 20;

	@Override
	public void drawObject(Graphics2D g) {
		boolean v = this.direction.isVertical();

		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();

		// TODO tudo para CircDrawer

		if (this.isCurto()) { // curto-circuito

			// conector do primeiro terminal
			super.drawObject(g);

			// conector do segundo terminal
			super.conector(g, super.n2, super.pn2, v ? 0 : 2, v ? 2 : 0);

			switch (this.direction) {
			case UP:
			case DOWN:
				boolean u = this.direction == Direction.UP;
				g.drawLine(x0, y0, x0, y0 + LENGTH);
				DrawingUtils.drawArrow(g, x0, y0 + (u ? 15 : 5), u ? Direction.DOWN : Direction.UP);
				break;
			case LEFT:
			case RIGHT:
				u = this.direction == Direction.LEFT;
				g.drawLine(x0, y0, x0 + LENGTH, y0);
				DrawingUtils.drawArrow(g, x0 + (u ? 15 : 5), y0, u ? Direction.RIGHT : Direction.LEFT);
				break;
			default:
				break;
			}

		} else { // aberto
			g.setColor(Color.RED);

			super.no.setIndex(super.pn);
			int x1 = no.getX() * grade.getWidth(), y1 = no.getY() * grade.getHeight();
			super.n2.setIndex(super.pn2);
			int x2 = n2.getX() * grade.getWidth(), y2 = n2.getY() * grade.getHeight();

			{
				int dy = y0 - y1;
				int dx = x1 - x0;
				if (dy == 0 && dx == 0)
					DrawingUtils.drawArrow(g, x1, y1, this.direction);
				else
					DrawingUtils.drawArrow(x1, y1, Math.atan2(dy, dx), g);
			}

			// conector do primeiro terminal
			g.drawLine(x1, y1, x0, y0);

			if (v) {
				g.drawLine(x0, y0, x0, y0 + 3);
				g.drawLine(x0, y0 + 16, x0, y0 + LENGTH);

				// conector do segundo terminal
				g.drawLine(x0, y0 + LENGTH, x2, y2);
			} else {
				g.drawLine(x0, y0, x0 + 3, y0);
				g.drawLine(x0 + 16, y0, x0 + LENGTH, y0);

				// conector do segundo terminal
				g.drawLine(x0 + LENGTH, y0, x2, y2);
			}
		}

		double v0 = Double.NaN;
		if (value instanceof Complex) {
			double arg = ((Complex) value).getArg();
			v0 = (arg < ExtendedMath.PI_2 && arg > -ExtendedMath.PI_2 ? 1 : -1) * ((Complex) value).getMod();
		} else
			v0 = value.doubleValue();

		boolean fv = !Double.isNaN(v0);
		if (v)
			g.drawString(String.format("%s%s", toString(),
					fv ? "=" + Multiplicador.getMult(Math.abs(v0), 3, Multiplicador.POW3) + (this.isCurto() ? "A" : "V")
							: ""),
					x0 + (this.isCurto() ? 4 : -9), y0 + 15);
		else
			g.drawString(String.format("%s%s", toString(),
					fv ? "=" + Multiplicador.getMult(Math.abs(v0), 3, Multiplicador.POW3) + (this.isCurto() ? "A" : "V")
							: ""),
					x0 + 5, y0 + (this.isCurto() ? 14 : 5));

		g.setColor(Color.BLACK);
	}

	@Override
	public Area getClickableArea() {
		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();
		if (this.direction.isVertical())
			x0 -= 10;
		else
			y0 -= 10;
		return new Area(new Rectangle2D.Float(x0, y0, 20, 20));
	}

	// ----------------------------- EXPORT -----------------------------

	@Override
	public String getTikz() {
		String out;
		boolean v = this.direction.isVertical();

		if (this.isCurto()) { // curto-circuito

			// conector do primeiro terminal
			out = super.getTikz();

			// conector do segundo terminal
			No no = getN2();
			no.setIndex(getConnectionPoint2());

			out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(), v ? 0 : 2,
					v ? -2 : 0);
			out += String.format(
					String.format("\\draw%s (0,0) -- (%d,%d);\n", (v ? (direction == Direction.DOWN ? "[<-]" : "[->]")
							: (direction == Direction.LEFT ? "[->]" : "[<-]")), v ? 0 : 2, v ? -2 : 0));
		} else { // aberto
			out = String.format("%% %s\n \\begin{scope}[xshift=%dcm,yshift=%dcm]\n", toString(), getX(), -getY());

			super.no.setIndex(super.pn);
			int x1 = no.getX() - getX(), y1 = getY() - no.getY();
			super.n2.setIndex(super.pn2);
			int x2 = n2.getX() - getX(), y2 = getY() - n2.getY();

			// conector do primeiro terminal
			out += String.format("\\draw[<-,red] (%d,%d) -- (0,0);\n", x1, y1);
			if (v) {
				out += String.format("\\draw[red] (0,0) -- (0,-.3);\n");
				out += String.format("\\draw[red] (0,-1.6) -- (0,-2);\n");

				// conector do segundo terminal
				out += String.format("\\draw[red] (0,-2) -- (%d,%d);\n", x2, y2);
			} else {
				out += String.format("\\draw[red] (0,0) -- (.3,0);\n");
				out += String.format("\\draw[red] (1.6,0) -- (2,0);\n");

				// conector do segundo terminal
				out += String.format("\\draw[red] (2,0) -- (%d,%d);\n", x2, y2);
			}
		}

		double v0 = Double.NaN;
		if (value instanceof Complex) {
			double arg = ((Complex) value).getArg();
			v0 = (arg < ExtendedMath.PI_2 && arg > -ExtendedMath.PI_2 ? 1 : -1) * ((Complex) value).getMod();
		} else
			v0 = value.doubleValue();

		boolean fv = !Double.isNaN(v0);
		out += String.format("\\draw (0,0) node[anchor=%s] {$%s%s$};\n",
				this.isCurto() ? (v ? "north east" : "south west") : (v ? "north" : "west"), toString(),
				fv ? "=" + Multiplicador.getMult(Math.abs(v0), 3, Multiplicador.POW3) + (this.isCurto() ? "A" : "V") : "");

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
				"<cca label=\"%s\" cc=\"%s\">\n<loc dir=\"%d\">%d,%d</loc>\n<term>%s,%d;%s,%d</term>\n</cca>\n",
				super.label, this.isCurto(), this.direction.ordinal(), x, y, super.no.toString(), super.pn,
				super.n2.toString(), super.pn2);
	}
}

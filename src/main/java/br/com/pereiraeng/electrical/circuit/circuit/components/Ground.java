package br.com.pereiraeng.electrical.circuit.circuit.components;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import br.com.pereiraeng.drawing.drawutils.TikZ;
import br.com.pereiraeng.electrical.circuit.circuit.CircuitDrawer;

/**
 * Classe dos objetos que representam um ponto de aterramento (potencial nulo)
 * 
 * @author Philipe PEREIRA
 *
 */
public class Ground extends Comp {

	public Ground(String label) {
		super(label);
		this.setOrientation(0);
	}

	@Override
	public void drawObject(Graphics2D g) {
		super.drawObject(g);

		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();

		CircuitDrawer.drawTerra(x0, y0, g);
	}

	@Override
	public Area getClickableArea() {
		return new Area(new Rectangle2D.Float(super.x * super.grade.getWidth() - 25, super.y * super.grade.getHeight(),
				50, 25));
	}

	// não se roda o terra

	@Override
	public void setOrientation(int o) {
	}

	@Override
	public void rotate() {
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
	public Object getField(int index) {
		Object out = null;
		switch (index) {
		case 0:
			out = toString() != null ? toString() : "";
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
		}
	}

	// -------------------------- EXPORT --------------------------

	@Override
	public String getTikz() {
		String out = super.getTikz();
		out += TikZ.drawTerra(0f, 0f);
		return out + "\\end{scope}\n";
	}

	@Override
	public String getSVG() {
		String out = super.getSVG();

		out += "<line x1=\"0\" y1=\"0\" x2=\"0\" y2=\"10\" stroke=\"rgb(0,0,0)\"/>\n";
		out += "<line x1=\"-21\" y1=\"10\" x2=\"21\" y2=\"10\" stroke=\"rgb(0,0,0)\"/>\n";
		out += "<line x1=\"-13\" y1=\"15\" x2=\"13\" y2=\"15\" stroke=\"rgb(0,0,0)\"/>\n";
		out += "<line x1=\"-5\" y1=\"20\" x2=\"5\" y2=\"20\" stroke=\"rgb(0,0,0)\"/>\n";

		return out + "</g>\n";
	}

	@Override
	public String getVML() {
		// TODO Auto-generated method stub
		return null;
	}

	// -------------------------- XML --------------------------

	public String getXML() {
		return "<gnd label=\"" + super.label + "\">\n<loc>" + x + "," + y + "</loc>\n<term>" + super.no.toString() + ","
				+ super.pn + "</term>\n</gnd>\n";
	}
}

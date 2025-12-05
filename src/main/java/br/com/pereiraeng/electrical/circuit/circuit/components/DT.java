package br.com.pereiraeng.electrical.circuit.circuit.components;

import java.awt.Graphics2D;
import java.awt.geom.Area;

import br.com.pereiraeng.core.Direction;

public class DT extends Comp2 {

	/**
	 * se <code>true</code> for T, senão D
	 */
	private boolean t = false;

	public DT(String label) {
		super(label);
	}

	public void setT(boolean t) {
		this.t = t;
	}

	public boolean isT() {
		return t;
	}

	// ------------------------ DISPOSIÇÃO ------------------------

	private Direction direction;

	@Override
	public void setOrientation(int o) {
		this.direction = Direction.values()[o];
	}

	@Override
	public void rotate() {
		this.direction = direction.next();
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
			return "T";
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
			out = isT();
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
			setT((boolean) obj);
			break;
		}
	}

	// ---------------------- DRAWER ----------------------

	@Override
	public void drawObject(Graphics2D g) {
		super.drawObject(g);
		// TODO Auto-generated method stub
	}

	@Override
	public Area getClickableArea() {
		// TODO Auto-generated method stub
		return null;
	}

	// ---------------------- XML ----------------------

	@Override
	public String getXML() {
		// TODO Auto-generated method stub
		return null;
	}
}

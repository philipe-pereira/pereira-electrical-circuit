package br.com.pereiraeng.electrical.circuit.circuit;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.Collection;

import br.com.pereiraeng.drawing.drawutils.SVG;
import br.com.pereiraeng.electrical.circuit.circuit.components.ElecElem;
import br.com.pereiraeng.math.Scale2Di;
import br.com.pereiraeng.swing.LeafOG;

/**
 * Classe do objeto gráfico que representa uma grade em que elementos de
 * circuito
 * 
 * @author Philipe PEREIRA
 *
 */
public class Schematic extends LeafOG<ElecElem> {
	private static final long serialVersionUID = 1L;

	private boolean editable;

	public Schematic(boolean editable) {
		super(Color.WHITE, 1, 1, true, new Scale2Di(10, 10));
		this.setEditable(editable);
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	private String fore, back;

	public void setForeBack(String fore, String back) {
		this.fore = fore;
		this.back = back;
	}

	public String getFore() {
		return fore;
	}

	public Collection<Object[]> getForeInst() {
		return SVG.svg2insts(getFore());
	}

	public String getBack() {
		return back;
	}

	public Collection<Object[]> getBackInst() {
		return SVG.svg2insts(getBack());
	}

	// ----------------- LEAF -----------------

	private Collection<ElecElem> elems;

	@Override
	public Collection<ElecElem> getList() {
		return elems;
	}

	public void setElems(Collection<ElecElem> elems) {
		this.elems = elems;
	}

	@Override
	protected boolean isDragable() {
		return editable;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		// solicitar o foco eventos do teclado
		super.requestFocus();
	}

	@Override
	protected void drawBackground(Graphics2D g) {
		if (back != null)
			SVG.draw(g, back, 1f, 0, 0, 1, 1);
	}

	@Override
	protected void drawForeground(Graphics2D g) {
		if (fore != null)
			SVG.draw(g, fore, 1f, 0, 0, 1, 1);
	}
}
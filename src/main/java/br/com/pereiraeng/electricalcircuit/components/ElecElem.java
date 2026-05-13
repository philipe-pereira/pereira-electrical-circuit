package br.com.pereiraeng.electricalcircuit.components;

import br.com.pereiraeng.core.EditableFields;
import br.com.pereiraeng.drawing.ExtDrawable;
import br.com.pereiraeng.math.Scale2DiOff;
import br.com.pereiraeng.swing.interfaces.Click;
import br.com.pereiraeng.swing.interfaces.DesG;
import br.com.pereiraeng.xml.XMLserializable;

/**
 * Classe abstrata do objeto que representa os elementos de um grafo elétrico
 * 
 * @author Philipe PEREIRA
 *
 */
public abstract class ElecElem implements DesG, Click, EditableFields, XMLserializable, ExtDrawable {

	protected String label;

	protected Scale2DiOff grade;

	public ElecElem(String label) {
		this.label = label;
	}

	@Override
	public void setGrade(Scale2DiOff grade) {
		this.grade = grade;
	}

	@Override
	public String toString() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}

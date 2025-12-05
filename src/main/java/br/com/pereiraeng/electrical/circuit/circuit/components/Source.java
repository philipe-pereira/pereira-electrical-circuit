package br.com.pereiraeng.electrical.circuit.circuit.components;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import br.com.pereiraeng.math.Complex;
import br.com.pereiraeng.core.Direction;
import br.com.pereiraeng.drawing.drawutils.TikZ;
import br.com.pereiraeng.electrical.circuit.circuit.CircuitDrawer;

public class Source extends Comp2 {

	/**
	 * Forma de onda
	 */
	private GeneratorType1 wf;

	/**
	 * Tensão ou corrente
	 */
	private GeneratorType2 output;

	/**
	 * Usado somente para {@link #wf forma de onda} {@link GeneratorType1#GENERAL}
	 */
	private String exp;

	/**
	 * Usado somente para {@link #wf forma de onda} {@link GeneratorType1#DC} e
	 * {@link GeneratorType1#AC}
	 */
	private Number value;

	/**
	 * Usado somente para {@link #wf forma de onda} {@link GeneratorType1#FOURIER} e
	 * {@link GeneratorType1#LAPLACE}
	 */
//	private LinGen linGen;

	public Source(String label, GeneratorType1 wf, GeneratorType2 output) {
		super(label);
		this.setWf(wf);
		this.setOutput(output);
		this.setOrientation(0);
	}

	// -------------------------- BOUNDARY --------------------------

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public Number getValue() {
		return value;
	}

	public void setValue(Number value) {
		this.value = value;
	}

	private Object getInput() {
		Object value = null;
		switch (getWf()) {
		case GENERAL:
			value = getExp();
			break;
		case AC:
		case DC:
			value = getValue();
			break;
		case FOURIER:
		case LAPLACE:
//			value = getLinGen();
			break;
		}
		return value;
	}

	public void setInput(Object obj) {
		switch (getWf()) {
		case GENERAL:
			setExp((String) obj);
			break;
		case DC:
			setValue(((Number) obj).doubleValue());
			break;
		case AC:
			if (obj instanceof Number) {
				Number n = (Number) obj;
				if (n instanceof Complex)
					setValue((Complex) n);
				else
					setValue(n.doubleValue());
			}
			break;
		case FOURIER:
		case LAPLACE:
//			setLinGen((LinGen) obj);
			break;
		}
	}

	// ------------------------------------------------------

	public GeneratorType1 getWf() {
		return wf;
	}

	public GeneratorType2 getOutput() {
		return output;
	}

	public void setWf(GeneratorType1 wf) {
		GeneratorType1 current = getWf();
		if (current == null) { // primeiro vez que o objeto é instanciado
			this.wf = wf;
			setInput(getNewValue(wf));
		} else {
			// conversão se faz necessária nas passagens AC-DC e Fourier-Laplace
			// pelo valor estocado em 'value' e 'linGen'
			if (wf == GeneratorType1.AC || wf == GeneratorType1.DC) {
				// somente value é usado: converter número real em complexo ou
				// vice-versa
				Number n = getValue();
				if (n == null) // ainda não há valor...
					setValue((Number) getNewValue(wf));
				else { // conversão...
					if (wf == GeneratorType1.AC && current == GeneratorType1.DC)
						setValue(new Complex(n.doubleValue(), 0.));
					if (wf == GeneratorType1.DC && current == GeneratorType1.AC)
						setValue(((Complex) n).doubleValue());
				}
			} else if (wf == GeneratorType1.FOURIER || wf == GeneratorType1.LAPLACE) {
				// somente linGen é usado: converter a expressão da transformada
				// de Laplace em série de Fourier e vice-versa
//				LinGen n = getLinGen();
//				if (n == null) // ainda não há série na frequência...
//					setLinGen((LinGen) getNewValue(wf));
//				else { // conversão...
//					if (wf == GeneratorType1.FOURIER && current == GeneratorType1.LAPLACE)
//						setLinGen(FourierCircInput.toFourier(n.getBc()));
//					if (wf == GeneratorType1.LAPLACE && current == GeneratorType1.FOURIER)
//						setLinGen(LaplaceCircInput.toLaplace(n.getBc()));
//				}
			}
			this.wf = wf;
		}
	}

	private static Object getNewValue(GeneratorType1 wf) {
		switch (wf) {
		case GENERAL:
			return "f(t)";
		case DC:
			return 1.;
		case AC:
			return new Complex(1., 0);
		case FOURIER:
		case LAPLACE:
//			return new The();
		}
		return null;
	}

	public void setOutput(GeneratorType2 output) {
		this.output = output;
	}

	// -------------------------------- COMP --------------------------------

	private Direction direction;

	@Override
	public void setOrientation(int o) {
		this.direction = Direction.values()[o];
	}

	@Override
	public void rotate() {
		this.direction = direction.next();
	}

	// ---------------------- DRAWER ----------------------

	@Override
	public void drawObject(Graphics2D g) {
		super.drawObject(g);

		// conector do segundo terminal
		boolean v = this.direction.isVertical();
		conector(g, super.n2, super.pn2, v ? 0 : 5, v ? 5 : 0);

		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();

		CircuitDrawer.drawGerador(x0, y0, this.wf.ordinal(), this.output.ordinal(), this.direction, super.label,
				getInput(), g);
	}

	@Override
	public Area getClickableArea() {
		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();
		if (this.direction.isVertical())
			x0 -= 25;
		else
			y0 -= 25;
		return new Area(new Rectangle2D.Float(x0, y0, 50, 50));
	}

	// ---------------------- TABLE EDITABLE ----------------------

	@Override
	public int getFieldCount() {
		return 4;
	}

	@Override
	public String getFieldName(int index) {
		switch (index) {
		case 0:
			return "Etiqueta";
		case 1:
			return "Saída";
		case 2:
			return "Forma de onda";
		case 3:
			return "Valor";
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
			out = getOutput();
			break;
		case 2:
			out = getWf();
			break;
		case 3:
			out = getInput();
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
			setOutput((GeneratorType2) obj);
			break;
		case 2:
			setWf((GeneratorType1) obj);
			break;
		case 3:
			setInput(obj);
			break;
		}
	}

	// -------------------------- EXPORT --------------------------

	@Override
	public String getTikz() {
		String out = super.getTikz();

		boolean v = this.direction.isVertical();

		// conector do segundo terminal
		No no = getN2();
		no.setIndex(getConnectionPoint2());
		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(), v ? 0 : 5,
				v ? -5 : 0);

		out += TikZ.drawGerador(0f, 0f, getWf().ordinal(), getOutput() == GeneratorType2.V, direction, super.label,
				getInput());

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
		StringBuilder out = new StringBuilder();
		out.append("<source label=\"");
		out.append(super.label);
		out.append("\" output=\"");
		out.append(this.output.ordinal());
		out.append("\" wf=\"");
		out.append(this.wf.ordinal());
		out.append("\" value=\"");
		switch (getWf()) {
		case GENERAL:
			out.append(exp);
			break;
		case DC:
			out.append(this.value.doubleValue());
			break;
		case AC:
			Complex c = (Complex) this.value;
			out.append(c.getRe());
			out.append(";");
			out.append(c.getIm());
			break;
		case FOURIER:
		case LAPLACE:
//			out.append(linGen.toString());
			break;
		}
		out.append("\">\n<loc dir=\"");
		out.append(this.direction.ordinal());
		out.append("\">");
		out.append(x);
		out.append(",");
		out.append(y);
		out.append("</loc>\n<term>");
		out.append(super.no.toString());
		out.append(",");
		out.append(super.pn);
		out.append(";");
		out.append(super.n2.toString());
		out.append(",");
		out.append(super.pn2);
		out.append("</term>\n</source>\n");
		return out.toString();
	}
}

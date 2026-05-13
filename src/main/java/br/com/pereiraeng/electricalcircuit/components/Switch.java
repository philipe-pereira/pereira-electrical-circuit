package br.com.pereiraeng.electricalcircuit.components;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Locale;

import br.com.pereiraeng.core.Direction;
import br.com.pereiraeng.drawing.TikZ;
import br.com.pereiraeng.electricalcircuit.CircuitDrawer;

/**
 * Classe do objeto que representa dispositivos de chaveamento
 * 
 * @author Philipe PEREIRA
 *
 */
public class Switch extends Comp2 {
	/**
	 * se <code>true</code>, conduz num sentido, senão nos dois
	 */
	private boolean carryOnlyForward;

	public enum TensionBlock {
		REVERSE, DIRECT, BOTH;
	}

	/**
	 * 0 bloqueia reverso, 1 bloqueia direto, 2 ambos
	 */
	private TensionBlock block;

	/**
	 * Construtor do objeto que representa uma chave eletrônica
	 * 
	 * @param label            etiqueta do componente
	 * @param carryOnlyForward
	 * @param block
	 */
	public Switch(String label, boolean carryOnlyForward, TensionBlock block) {
		super(label);
		this.setOrientation(0);

		this.carryOnlyForward = carryOnlyForward;
		this.block = block;
	}

	/**
	 * Construtor do objeto que representa uma chave eletrônica
	 * 
	 * @param label etiqueta do componente
	 * @param sw    inteiro que representa uma dada chave
	 *              <ol start="0">
	 *              <li>diodo;</i>
	 *              <li>BJT;</i>
	 *              <li>GTO;</i>
	 *              <li>FET com diodo antiparalelo;</i>
	 *              <li>ideal.</i>
	 *              </ol>
	 */
	public Switch(String label, int sw) {
		super(label);
		this.setOrientation(0);

		switch (sw) {
		case 0:
			this.setCarryOnlyForward(true);
			this.setBlock(TensionBlock.REVERSE);
			break;
		case 1:
			this.setCarryOnlyForward(true);
			this.setBlock(TensionBlock.DIRECT);
			break;
		case 2:
			this.setCarryOnlyForward(true);
			this.setBlock(TensionBlock.BOTH);
			break;
		case 3:
			this.setCarryOnlyForward(false);
			this.setBlock(TensionBlock.DIRECT);
			break;
		case 4:
			this.setCarryOnlyForward(false);
			this.setBlock(TensionBlock.BOTH);
			break;
		default:
			break;
		}
	}

	public void setCarryOnlyForward(boolean cof) {
		this.carryOnlyForward = cof;
	}

	public boolean isCarryForward() {
		return carryOnlyForward;
	}

	public TensionBlock getBlock() {
		return block;
	}

	public void setBlock(TensionBlock block) {
		this.block = block;
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

	// ------------------------ NÓ CONTROLE ------------------------

	protected No nC;

	public void setNC(No nC) {
		this.nC = nC;
	}

	public No getNC() {
		return this.nC;
	}

	protected int pnC;

	public void setConnectionPointC(int pn) {
		this.pnC = pn;
	}

	public int getConnectionPointC() {
		return this.pnC;
	}

	// ---------------------- DRAWER ----------------------

	@Override
	public void drawObject(Graphics2D g) {
		super.drawObject(g);

		// conector do segundo terminal
		conector(g, super.n2, super.pn2,
				(this.direction == Direction.LEFT ? -5 : (this.direction == Direction.RIGHT ? 5 : 0)),
				(this.direction == Direction.UP ? -5 : (this.direction == Direction.DOWN ? 5 : 0)));

		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();

		CircuitDrawer.drawSwitch(x0, y0, carryOnlyForward, block.ordinal(), this.direction, super.label, g);
	}

	@Override
	public Area getClickableArea() {
		int x0 = (super.x + (Direction.LEFT.equals(this.direction) ? -5 : 0)) * super.grade.getWidth();
		int y0 = (super.y + (Direction.UP.equals(this.direction) ? -5 : 0)) * super.grade.getHeight();
		if (Direction.DOWN.equals(this.direction) || Direction.UP.equals(this.direction))
			x0 -= 25;
		else
			y0 -= 25;
		return new Area(new Rectangle2D.Float(x0, y0, 50, 50));
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
			return "Só direto";
		case 2:
			return "Bloqueio de tensão";
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
			out = isCarryForward();
			break;
		case 2:
			out = getBlock();
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
			setCarryOnlyForward((boolean) obj);
			break;
		case 2:
			setBlock((TensionBlock) obj);
			break;
		}
	}

	// -------------------------- EXPORT --------------------------

	@Override
	public String getTikz() {
		String out = super.getTikz();

		boolean d = Direction.DOWN.equals(this.direction);
		boolean v = d || Direction.UP.equals(this.direction);

		// conector do segundo terminal
		No no = getN2();
		no.setIndex(getConnectionPoint2());
		out += String.format(Locale.US, "\\draw (%d,%d) -- (%f,%f);\n", no.getX() - getX(), getY() - no.getY(),
				v ? 0 : (Direction.RIGHT.equals(this.direction) ? TikZ.COMP_LENGTH : -TikZ.COMP_LENGTH),
				v ? (d ? -TikZ.COMP_LENGTH : TikZ.COMP_LENGTH) : 0);

		out += TikZ.drawSwitch(0f, 0f, carryOnlyForward, block.ordinal(), this.direction, super.label);

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
		return "<switch label=\"" + super.label + "\" type1=\"" + (carryOnlyForward ? "1" : "0") + "\" type2=\""
				+ block.ordinal() + "\">\n<loc dir=\"" + this.direction.ordinal() + "\">" + x + "," + y
				+ "</loc>\n<term>" + super.no.toString() + "," + super.pn + ";" + super.n2.toString() + "," + super.pn2
				+ "</term>\n</switch>\n";
	}
}
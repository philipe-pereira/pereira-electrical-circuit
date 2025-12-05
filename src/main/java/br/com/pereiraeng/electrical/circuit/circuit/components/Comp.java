package br.com.pereiraeng.electrical.circuit.circuit.components;

import java.awt.Graphics2D;
import java.awt.Point;

import br.com.pereiraeng.drawing.drawutils.VML;


/**
 * Classe abstrata do objeto que representa os elementos do circuito do tipo
 * 'arestas'
 * 
 * @author Philipe PEREIRA
 *
 */
public abstract class Comp extends ElecElem {

	public Comp(String label) {
		super(label);
	}

	// --------------------------- NO 1 --------------------------

	protected No no;

	/**
	 * Função que se estabelece um dos nó terminais do componente
	 * 
	 * @param no {@link No Nó} do circuito
	 */
	public void setNo(No no) {
		if (this.no != null)
			if (this instanceof Comp2)
				this.no.remove((Comp2) this);
		this.no = no;
		if (this instanceof Comp2)
			this.no.add((Comp2) this);
	}

	/**
	 * Função que remove um dos nó terminais do componente
	 */
	public void removeNo() {
		if (this instanceof Comp2)
			this.no.remove((Comp2) this);
		this.no = null;
	}

	/**
	 * Função que retorna o nó de um dos terminais do componente
	 * 
	 * @return
	 */
	public No getNo() {
		return this.no;
	}

	@Override
	public String getTikz() {
		StringBuilder out = new StringBuilder(
				String.format("%% %s\n \\begin{scope}[xshift=%dcm,yshift=%dcm]\n", toString(), getX(), -getY()));

		// todo elemento tem um terminal (e ele se localiza nas
		// coordenadas (0;0) da referência do próprio objeto)
		No no = getNo();
		no.setIndex(getConnectionPoint());

		int x = no.getX() - getX(), y = getY() - no.getY();
		if (x != 0 || y != 0)
			out.append(String.format("\\draw (%d,%d) -- (0,0);\n", x, y));

		return out.toString();
	}

	@Override
	public String getSVG() {
		StringBuilder out = new StringBuilder(String.format("<!-- %s -->\n<g transform=\"translate(%d,%d)\">\n",
				toString(), getX() * grade.getWidth(), getY() * grade.getHeight()));

		// todo elemento tem um terminal (e ele se localiza nas coordenadas (0;0) da
		// referência do próprio objeto)
		No no = getNo();
		no.setIndex(getConnectionPoint());

		out.append(String.format("<line x1=\"%d\" y1=\"%d\" x2=\"0\" y2=\"0\" stroke=\"rgb(0,0,0)\"/>\n",
				(no.getX() - getX()) * grade.getWidth(), (no.getY() - getY()) * grade.getHeight()));

		return out.toString();
	}

	@Override
	public String getVML() {// TODO
		StringBuilder out = new StringBuilder(String.format(
				"<v:group id=\"%s\" style='position:absolute;left:%dpt;top:%dpt;width:1000pt;height:1000pt;'>\n",
				toString(), getX() * VML.PT_VML * grade.getWidth(), getY() * VML.PT_VML * grade.getHeight()));

		// todo elemento tem um terminal (e ele se localiza nas coordenadas (0;0) da
		// referência do próprio objeto)
		No no = getNo();
		no.setIndex(getConnectionPoint());

		out.append(String.format(
				"<v:line id=\"Conector_x0020_reto_x0020_39\" o:spid=\"_x0000_s1028\" style='position:absolute;visibility:visible;mso-wrap-style:square' from=\"%d,%d\" to=\"%d,%d\" o:connectortype=\"straight\" strokecolor=\"black\"/>\n",
				0, 0, (no.getX() - getX()) * VML.PT_VML * grade.getWidth(),
				(no.getY() - getY()) * VML.PT_VML * grade.getHeight()));

		return out.toString();
	}

	// --------------------------- DISPOSIÇÃO --------------------------

	/**
	 * Função que rotaciona o elemento exibido
	 */
	public abstract void rotate();

	/**
	 * Função que estabelece a direção de disposição do o elemento exibido
	 * 
	 * @param o índice do tipo da enumeração que indica a disposição (dependendo
	 *          componente pode ser {@link DUtils.Direction} ou
	 *          {@link DUtils.Orientation}).
	 */
	public abstract void setOrientation(int o);

	// --------------------------- DRAWER --------------------------

	@Override
	public void drawObject(Graphics2D g) {
		this.conector(g, no, pn, 0, 0);
	}

	/**
	 * Função que conecta um dos terminais do componente ao nó
	 * 
	 * @param g  objeto {@link Graphics2D}
	 * @param no nó ao qual o componente será conectado
	 * @param pn índice do ponto do nó onde será feita a conexão
	 * @param dx decalagem do ponto de ancoragem horizontal do objeto
	 * @param dy decalagem do ponto de ancoragem vertical do objeto
	 */
	protected void conector(Graphics2D g, No no, int pn, int dx, int dy) {
		no.setIndex(pn);
		g.drawLine(no.getX() * grade.getWidth(), no.getY() * grade.getHeight(), (getX() + dx) * grade.getWidth(),
				(getY() + dy) * grade.getHeight());
	}

	protected int x = 1;
	protected int y = 1;

	/**
	 * Índice do ponto da barra onde o equipamento será conectado
	 */
	protected int pn;

	/**
	 * Função em que se estabelece em qual dos pontos que formam o segmento de reta
	 * do nó o terminal será conectado
	 * 
	 * @param pn índice do ponto do nó
	 */
	public void setConnectionPoint(int pn) {
		this.pn = pn;
	}

	public int getConnectionPoint() {
		return this.pn;
	}

	@Override
	public void setDrawable(boolean drawable) {
	}

	@Override
	public boolean isDrawable() {
		return true;
	}

	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public Point getLocation() {
		return new Point(getX(), getY());
	}

	@Override
	public boolean isOn(int x, int y) {
		return getClickableArea().contains(x, y);
	}
}
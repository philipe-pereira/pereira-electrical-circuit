package br.com.pereiraeng.electricalcircuit.components;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.pereiraeng.math.Scale2Dm;
import br.com.pereiraeng.core.Orientation;
import br.com.pereiraeng.core.collections.ListUtils;
import br.com.pereiraeng.drawing.drawutils.LID;
import br.com.pereiraeng.drawing.drawutils.SVG;
import br.com.pereiraeng.drawing.drawutils.TikZ;
import br.com.pereiraeng.drawing.drawutils.VML;
import br.com.pereiraeng.graph.Edge;
import br.com.pereiraeng.graph.Hyperedge;
import br.com.pereiraeng.graph.Vertex;

/**
 * Classe dos objetos que representam componentes de circuito customizados, cuja
 * descrição genérica (localiza no objeto {@link ElecElemSeed}) pode ser editada
 * no arquivo XML
 * 
 * @author Philipe PEREIRA
 *
 */
public class CompCustom extends Comp implements Hyperedge {

	private ElecElemSeed elecElemSeed;

	private List<Object> attributes;

	public CompCustom(String label, ElecElemSeed elecElemSeed) {
		super(label);
		this.elecElemSeed = elecElemSeed;
		this.attributes = new ArrayList<>(elecElemSeed.getAttribsValues());
		setNs(elecElemSeed.getNumConns());
		this.setOrientation(0);
	}

	/**
	 * Construtor do objeto que representa um componente com um número variável de
	 * terminais no caso em que ele representa um <strong>subsistema</strong>
	 * 
	 * @param label  etiqueta que identifica esse subsistema
	 * @param nn     número de terminais
	 * @param x0
	 * @param y0
	 * @param width
	 * @param height
	 * @param points
	 */
	public CompCustom(String label, int nn, int x0, int y0, int width, int height, List<Point> points) {
		super(label);
		this.elecElemSeed = new ElecElemSeed("ss", "Subsistema", nn);
		this.attributes = new ArrayList<>(0);
		this.setNs(nn);

		this.elecElemSeed.createList();
		this.elecElemSeed.addDraw(new Object[] { LID.DrawAction.RECT.name().toLowerCase(),
				(int) (x0 / ElecElemSeed.MULT), (int) (y0 / ElecElemSeed.MULT), (int) (width / ElecElemSeed.MULT),
				(int) (height / ElecElemSeed.MULT), "black", "none", null });

		this.elecElemSeed.createConn();
		for (Point p : points)
			this.elecElemSeed.addConn(p);

		this.elecElemSeed.setSelection(x0, y0, width, height);
	}

	public String getType() {
		return this.elecElemSeed.getType();
	}

	public int getNs() {
		return (nn == null ? 0 : nn.size()) + 1;
	}

	public void setNs(int ns) {
		ns -= 1;
		if (nn == null) { // ainda não há tabela de nós
			if (ns > 0) {
				this.nn = new ArrayList<>(ns);
				this.pnn = new ArrayList<>(ns);

				for (int i = 0; i < ns; i++) {
					this.nn.add(null);
					this.pnn.add(0);
				}
			}
		} else {
			if (ns > nn.size()) { // mais nós do que já existe
				this.nn.ensureCapacity(ns);
				this.pnn.ensureCapacity(ns);
				for (int i = 0; i < ns - nn.size(); i++) {
					this.nn.add(getNo());
					this.pnn.add(0);
				}
			} else { // menos nós do que já existe
				for (int i = 0; i < nn.size() - ns; i++) {
					this.nn.remove(this.nn.size() - 1);
					this.pnn.remove(this.pnn.size() - 1);
				}
			}
		}
	}

	public Point getTerminalPoint(int i) {
		return this.elecElemSeed.getTerminalPoint(i);
	}

	public List<Object[]> getDrawInstructions() {
		return this.elecElemSeed.getDrawInstructions(this.attributes, this.getOrientation() == Orientation.HORIZONTAL);
	}

	// ---------------------- TABLE EDITABLE ----------------------

	@Override
	public int getFieldCount() {
		return elecElemSeed.getAttribsCount() + 1;
	}

	@Override
	public String getFieldName(int index) {
		if (index == 0)
			return "Etiqueta";
		else
			return ListUtils.getElementAt(elecElemSeed.attribsNames(), index - 1);
	}

	@Override
	public Object getField(int index) {
		Object out = null;
		if (index == 0)
			out = toString() != null ? toString() : "";
		else
			out = attributes.get(index - 1);
		return out;
	}

	@Override
	public void setField(int index, Object obj) {
		if (index == 0)
			setLabel((String) obj);
		else
			attributes.set(index - 1, obj);
	}

	// -------------------------------- COMP --------------------------------

	private transient Orientation orientation;

	@Override
	public void setOrientation(int o) {
		this.orientation = Orientation.values()[o];
	}

	public Orientation getOrientation() {
		return orientation;
	}

	@Override
	public void rotate() {
		this.orientation = this.getOrientation().next();
	}

	// ---------------------- DRAWER ----------------------

	@Override
	public void drawObject(Graphics2D g) {
		super.drawObject(g);

		boolean reverse = this.getOrientation() == Orientation.HORIZONTAL;

		// conector dos demais terminais
		for (int i = 1; i < this.getNs(); i++) {
			Point c = this.elecElemSeed.getTerminalPoint(i - 1);
			super.conector(g, this.getNN(i), this.getConnectionPointN(i), reverse ? c.x : c.y, reverse ? c.y : c.x);
		}

		LID.draw(g, this.getDrawInstructions(), getX(), getY(), new Scale2Dm(ElecElemSeed.MULT, super.grade));
	}

	@Override
	public Area getClickableArea() {
		boolean reverse = this.getOrientation() == Orientation.HORIZONTAL;
		int[] area = this.elecElemSeed.getSelRect();
		return new Area(new Rectangle2D.Float((getX() + area[reverse ? 0 : 1]) * super.grade.getWidth(),
				(getY() + area[reverse ? 1 : 0]) * super.grade.getHeight(),
				area[reverse ? 2 : 3] * super.grade.getWidth(), area[reverse ? 3 : 2] * super.grade.getHeight()));
	}

	// -------------------------- EXPORT --------------------------

	@Override
	public String getTikz() {
		String out = super.getTikz();

		// conector dos demais terminais
		for (int i = 1; i < getNs(); i++) {
			Point tp = getTerminalPoint(i - 1);

			No no = getNN(i);
			no.setIndex(getConnectionPointN(i));

			out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(), tp.x, -tp.y);
		}

		out += TikZ.getTikz(this.getDrawInstructions(), ElecElemSeed.MULT);

		return out + "\\end{scope}\n";
	}

	@Override
	public String getSVG() {
		StringBuilder out = new StringBuilder(super.getSVG());

		boolean reverse = this.getOrientation() == Orientation.HORIZONTAL;

		// conector dos demais terminais
		for (int i = 1; i < getNs(); i++) {
			Point tp = getTerminalPoint(i - 1);

			No no = getNN(i);
			no.setIndex(getConnectionPointN(i));

			out.append(String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"rgb(0,0,0)\"/>\n",
					(no.getX() - getX()) * grade.getWidth(), (no.getY() - getY()) * grade.getHeight(),
					(reverse ? tp.x : tp.y) * grade.getWidth(), (reverse ? tp.y : tp.x) * grade.getHeight()));
		}

		out.append(SVG.getSVG(this.getDrawInstructions()));
		out.append("</g>\n");

		return out.toString();
	}

	@Override
	public String getVML() {
		StringBuilder out = new StringBuilder(super.getVML());

		// conector dos demais terminais
		for (int i = 1; i < getNs(); i++) {
			Point tp = getTerminalPoint(i - 1);

			No no = getNN(i);
			no.setIndex(getConnectionPointN(i));

			out.append(String.format(
					"<v:line id=\"Conector_x0020_reto_x0020_39\" o:spid=\"_x0000_s1028\" style='position:absolute;visibility:visible;mso-wrap-style:square' from=\"%d,%d\" to=\"%d,%d\" o:connectortype=\"straight\" strokecolor=\"black\"/>\n",
					(no.getX() - getX()) * VML.PT_VML * grade.getWidth(),
					(no.getY() - getY()) * VML.PT_VML * grade.getHeight(), tp.x * VML.PT_VML * grade.getWidth(),
					tp.y * VML.PT_VML * grade.getHeight()));
		}

		out.append(VML.getVML(this.getDrawInstructions()));
		out.append("</v:group>\n");

		return out.toString();
	}

	// -------------------------- XML --------------------------

	@Override
	public String getXML() {
		String type = this.elecElemSeed.getType();
		// classe do elemento
		String out = String.format("<%s label=\"%s\"", type, super.label);

		// atributos
		if ("ss".equals(type)) // se for subsistema, não tem atributos e guarda-se as dimensões
			out += String.format(" dim=\"%s\"", this.elecElemSeed.getXML());
		else {
			int i = 1;
			for (String attrib : elecElemSeed.attribsNames())
				out += String.format(" %s=\"%s\"", attrib, this.getField(i++));
		}

		// orientação e localização
		out += String.format(">\n<loc dir=\"%d\">%d,%d</loc>\n", this.getOrientation().ordinal(), getX(), getY());

		// nó -> terminais
		out += String.format("<term>%s,%d", super.getNo().toString(), super.getConnectionPoint());
		for (int i = 1; i < getNs(); i++)
			out += String.format(";%s,%d", this.getNN(i).toString(), this.getConnectionPointN(i));

		return out += String.format("</term>\n</%s>\n", type);
	}

	// ------------------------ NO N ------------------------

	protected ArrayList<No> nn;

	public void setNN(No nn, int index) {
		if (index == 0)
			this.setNo(nn);
		else {
			index -= 1;
			if (this.nn.get(index) != null)
				this.nn.get(index).remove(this);
			this.nn.set(index, nn);
			this.nn.get(index).add(this);
		}
	}

	public void removeNN(int index) {
		if (index == 0)
			this.removeNo();
		else {
			index -= 1;
			if (this.nn.get(index) != null)
				this.nn.get(index).remove(this);
			this.nn.set(index, null);
		}
	}

	public No getNN(int index) {
		if (index == 0)
			return this.getNo();
		else
			return this.nn.get(index - 1);
	}

	protected ArrayList<Integer> pnn;

	public void setConnectionPointN(int pn, int index) {
		if (index == 0)
			this.setConnectionPoint(pn);
		else
			this.pnn.set(index - 1, pn);
	}

	public int getConnectionPointN(int index) {
		if (index == 0)
			return this.getConnectionPoint();
		else
			return this.pnn.get(index - 1);
	}

	// ----------------- INTERFACE SUPER-ARESTA -----------------

	@Override
	public Vertex getOpposite(Vertex v) {
		return null;
	}

	@Override
	public Set<Edge> getEdges(Vertex v) {
		return null;
	}

	@Override
	public boolean contains(Vertex v) {
		if (v.equals(no))
			return true;
		else
			for (No no : this.nn)
				if (v.equals(no))
					return true;
		return false;
	}

	@Override
	public Set<? extends Vertex> getVertices() {
		// TODO Auto-generated method stub
		return null;
	}
}
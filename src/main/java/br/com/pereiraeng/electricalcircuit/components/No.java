package br.com.pereiraeng.electricalcircuit.components;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import br.com.pereiraeng.graph.Edge;
import br.com.pereiraeng.modelling.modelutils.graph.elt.ENoeud;

/**
 * Classe abstrata do objeto que representa os elementos do circuito do tipo
 * 'vértices'
 * 
 * @author Philipe PEREIRA
 *
 */
public class No extends ElecElem implements ENoeud {

	public enum NodeType {
		SIMPLE(9, 1, 0), GROUND(2, 1, 4), BAR_PH(3, 8, 0), BAR_NT(1, 4, 0);

		private int color;
		private int weight;
		private int style;

		private NodeType(int color, int weight, int style) {
			this.color = color;
			this.weight = weight;
			this.style = style;
		}

		public int getColor() {
			return color;
		}

		public int getWeight() {
			return weight;
		}

		public int getStyle() {
			return style;
		}
	}

	private NodeType type;

	public No(String label) {
		super(label);
		type = NodeType.SIMPLE;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	public NodeType getType() {
		return type;
	}

	// -------------------- DRAWER -------------------

	protected int[][] xy = new int[][] { { 1, 1 } };

	@Override
	public void setDrawable(boolean drawable) {
	}

	@Override
	public boolean isDrawable() {
		return true;
	}

	@Override
	public void drawObject(Graphics2D g) {
		if (xy.length > 1) {
			// pegar caminho
			int[][] bar = new int[2][xy.length];
			// multiplicar pelo passo da grade
			for (int i = 0; i < xy.length; i++) {
				bar[0][i] = xy[i][0] * grade.getWidth();
				bar[1][i] = xy[i][1] * grade.getHeight();
			}
			g.drawPolyline(bar[0], bar[1], bar[0].length);
		} else {
			int x = xy[i][0] * grade.getWidth();
			int y = xy[i][1] * grade.getHeight();
			g.fillOval(x - 2, y - 2, 4, 4);
		}
	}

	// -------------------- coordenadas da barra -------------------

	/**
	 * Índice do vetor de posições da barra que será modificado ou retornado
	 */
	private transient int i = 0;

	public void setNumPoints(int newSize) {
		int[][] newArray = new int[newSize][2];
		int trunc = Math.min(newSize, xy.length);
		for (int i = 0; i < trunc; i++)
			newArray[i] = xy[i];
		this.xy = newArray;
	}

	public int getNumPoints() {
		return this.xy.length;
	}

	public void setIndex(int i) {
		if (i >= 0 && i < xy.length)
			this.i = i;
	}

	@Override
	public void setPosition(int x, int y) {
		if (selected >= 0)
			this.i = selected;
		this.xy[i][0] = x;
		this.xy[i][1] = y;
	}

	@Override
	public int getX() {
		return this.xy[i][0] - this.grade.getX();
	}

	@Override
	public int getY() {
		return this.xy[i][1] - this.grade.getY();
	}

	@Override
	public Point getLocation() {
		return new Point(getX(), getY());
	}

	@Override
	public Area getClickableArea() {
		Area a = new Area(new Rectangle2D.Float(xy[0][0] * grade.getWidth(), xy[0][1] * grade.getHeight(), 5, 5));
		for (int i = 1; i < xy.length; i++)
			a.add(new Area(new Rectangle2D.Float(xy[i][0] * grade.getWidth(), xy[i][1] * grade.getHeight(), 5, 5)));
		return a;
	}

	/**
	 * Número do ponto da barra sobre o qual o mouse está
	 */
	private transient int selected = -1;

	@Override
	public boolean isOn(int x, int y) {
		for (int i = 0; i < xy.length; i++) {
			Area a = new Area(
					new Rectangle2D.Float(xy[i][0] * grade.getWidth() - 4, xy[i][1] * grade.getHeight() - 4, 8, 8));
			if (a.contains(x, y)) {
				this.selected = i;
				return true;
			}
		}
		this.selected = -1;
		return false;
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
			return "Tipo";
		}
		return null;
	}

	@Override
	public Object getField(int index) {
		Object out = null;
		switch (index) {
		case 0:
			out = toString() != null ? toString() : "";
			break;
		case 1:
			out = getType();
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
			setType((NodeType) obj);
			break;
		}
	}

	// -------------------------- EXPORT --------------------------

	@Override
	public String getTikz() {
		StringBuilder out = new StringBuilder();

		int np = getNumPoints();
		if (np > 1) {
			setIndex(0);
			out.append("\\draw (");
			out.append(getX());
			out.append(",");
			out.append(-getY());
			out.append(")");
			for (int i = 1; i < np; i++) {
				setIndex(i);
				out.append(" -- (");
				out.append(getX());
				out.append(",");
				out.append(-getY());
				out.append(")");
			}
			out.append(";\n");
		}

		return out.toString();
	}

	@Override
	public String getSVG() {
		StringBuilder out = new StringBuilder();

		int np = getNumPoints();
		if (np > 1) {
			if (np == 2) {
				out.append("<line ");
				for (int i = 0; i < np; i++) {
					setIndex(i);
					out.append("x");
					out.append(i + 1);
					out.append("=\"");
					out.append(getX() * grade.getWidth());
					out.append("\" y");
					out.append(i + 1);
					out.append("=\"");
					out.append(getY() * grade.getHeight());
					out.append("\" ");
				}
				out.append("stroke=\"rgb(0,0,0)\"/>\n");
			} else {
				out.append("<polyline points=\"");
				for (int i = 0; i < np; i++) {
					setIndex(i);
					out.append(getX() * grade.getWidth());
					out.append(",");
					out.append(getY() * grade.getHeight());
					out.append(" ");
				}
				out.append("\" fill=\"none\" stroke=\"rgb(0,0,0)\"/>");
			}
		}

		return out.toString();
	}

	@Override
	public String getVML() {
		String out = "";

		int np = getNumPoints();
		if (np > 1) {
			if (np == 2) {
				// TODO
			} else {
				// TODO
			}
		}

		return out;
	}

	// -------------------------- XML --------------------------

	public String getXML() {
		StringBuilder out = new StringBuilder("<node label=\"");
		out.append(super.label);
		out.append("\"");
		out.append(this.type != NodeType.SIMPLE ? " type=\"" + this.type + "\"" : "");
		out.append(">\n<loc>");
		for (int i = 0; i < xy.length; i++) {
			out.append(xy[i][0]);
			out.append(",");
			out.append(xy[i][1]);
			out.append(";");
		}
		out.setLength(out.length() - 1);
		out.append("</loc>\n</node>\n");
		return out.toString();
	}

	// ----------------- INTERFACE VÉRTICE -----------------

	/**
	 * Conjunto de componentes conectados neste ponto (conjunto de arestas
	 * partindo desse nó)
	 */
	private Set<Edge> comps;

	@Override
	public Set<? extends Edge> getEdges() {
		return comps;
	}

	public void add(Edge comp) {
		if (comps == null)
			comps = new HashSet<>();
		this.comps.add(comp);
	}

	@Override
	public void remove(Edge e) {
		if (e.getOpposite(this) != this)
			this.comps.remove(e);
	}
}
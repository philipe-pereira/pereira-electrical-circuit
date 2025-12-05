package br.com.pereiraeng.electrical.circuit.circuit.components;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import br.com.pereiraeng.core.Direction;
import br.com.pereiraeng.core.Orientation;
import br.com.pereiraeng.drawing.drawutils.TikZ;
import br.com.pereiraeng.electrical.circuit.circuit.CircuitDrawer;
import br.com.pereiraeng.electrical.circuit.circuit.MC;
import br.com.pereiraeng.graph.Edge;
import br.com.pereiraeng.graph.Vertex;

/**
 * Classe dos objetos que representam o acoplamento magnético
 * 
 * @author Philipe PEREIRA
 *
 */
public class MagCouple extends CompN {

	private MC mc;

	protected Orientation orientation;

	// TODO nada é feito com a polaridade das bobinas
	private boolean[] polarity;

	/**
	 * coordenadas dos n-1 enrolamentos (o primeiro está nas coordenadas dadas pelas
	 * funções {@link Comp#getX() x} e {@link Comp#getY() y})
	 */
	private int[][] xyW;

	public MagCouple(String label, int windings) {
		super(label, 2 * windings);

		this.mc = new MC(windings);

		this.polarity = new boolean[windings];

		this.xyW = new int[windings - 1][2];
		for (int i = 0; i < windings - 1; i++)
			this.xyW[i] = new int[] { 3 * (i + 1), 0 };

		this.setOrientation(0);
	}

	private void setWindings(int windings) {
		if (windings > 1) {
			this.mc.setWindings(windings);
			super.setNs(2 * windings);

			this.polarity = Arrays.copyOf(this.polarity, windings);

			int oldLength = this.xyW.length;
			this.xyW = Arrays.copyOf(this.xyW, windings - 1);
			if (xyW.length > oldLength)
				for (int i = xyW.length - oldLength; i < xyW.length; i++)
					this.xyW[i] = new int[2];
		}
	}

	@Override
	public void drawObject(Graphics2D g) {
		super.drawObject(g);

		boolean v = Orientation.VERTICAL.equals(this.orientation);
		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();

		// conector
		super.conector(g, this.n2, this.pn2, v ? 5 : 0, v ? 0 : 5);

		// desenhar indutor 1
		CircuitDrawer.drawIndutor(x0, y0 + (v ? 0 : 5 * super.grade.getHeight()), v ? Direction.RIGHT : Direction.UP,
				"", Double.NaN, g);
		// ponto do acoplamento 1
		g.fillOval(x0 + (v ? 0 : 1) * super.grade.getHeight() - 2, y0 + (v ? 1 : 0) * super.grade.getHeight() - 2, 4,
				4);

		for (int i = 1; i < mc.getWindings(); i++) {
			// conectores
			super.conector(g, super.getNN(2 * i), super.getConnectionPointN(2 * i), this.xyW[i - 1][0],
					this.xyW[i - 1][1]);
			super.conector(g, super.getNN(2 * i + 1), super.getConnectionPointN(2 * i + 1),
					this.xyW[i - 1][0] + (v ? 5 : 0), this.xyW[i - 1][1] + (v ? 0 : 5));

			// desenhar demais indutores
			CircuitDrawer.drawIndutor(
					x0 + this.xyW[i - 1][0] * super.grade.getHeight() + (v ? 5 * super.grade.getWidth() : 0),
					y0 + this.xyW[i - 1][1] * super.grade.getHeight(), v ? Direction.LEFT : Direction.DOWN, "",
					Double.NaN, g);

			// demais ponto do acoplamento
			g.fillOval(x0 + (this.xyW[i - 1][0] + (v ? 0 : -1)) * super.grade.getHeight() - 2,
					y0 + (this.xyW[i - 1][1] + (v ? -1 : 0)) * super.grade.getHeight() - 2, 4, 4);
		}
	}

	@Override
	public Area getClickableArea() {
		// não haverá nada aqui... estará no override da isOn
		return null;
	}

	@Override
	public boolean isOn(int x, int y) {
		int x0 = super.x * super.grade.getWidth();
		int y0 = super.y * super.grade.getHeight();

		int size1 = 5 * super.grade.getWidth(), size2 = super.grade.getWidth();

		boolean v = Orientation.VERTICAL.equals(this.orientation);

		// ver se o mouse está sobre um dos enrolamentos primários
		Area a = new Area(new Rectangle2D.Float(x0, y0, v ? size1 : size2, v ? size2 : size1));
		boolean b = a.contains(x, y);
		if (b) {
			w = 0;
			return true;
		}

		// ver se o mouse está sobre um dos enrolamentos secundários
		for (int i = 1; i < mc.getWindings(); i++) {
			a = new Area(new Rectangle2D.Float(x0 + (xyW[i - 1][0] - (v ? 0 : 1)) * super.grade.getWidth(),
					y0 + (xyW[i - 1][1] - (v ? 1 : 0)) * super.grade.getHeight(), v ? size1 : size2,
					v ? size2 : size1));
			b = a.contains(x, y);
			if (b) {
				w = i;
				return true;
			}
		}
		return false;
	}

	/**
	 * Índice do enrolamento que está selecionado no momento
	 */
	private transient int w;

	@Override
	public void setPosition(int x, int y) {
		if (w == 0)
			super.setPosition(x, y);
		else {
			xyW[w - 1][0] = x - getX();
			xyW[w - 1][1] = y - getY();
		}
	}

	@Override
	public void setOrientation(int o) {
		this.setOrientation(Orientation.values()[o]);
	}

	@Override
	public void rotate() {
		this.setOrientation(this.orientation.next());
	}

	public void setOrientation(Orientation orientation) {
		if (this.orientation != null && orientation != this.orientation) {
			for (int i = 0; i < xyW.length; i++) {
				int swap = xyW[i][0];
				xyW[i][0] = xyW[i][1];
				xyW[i][1] = swap;
			}
		}
		this.orientation = orientation;
	}

	// -------------------------- EXPORT --------------------------

	@Override
	public String getTikz() {
		String out = super.getTikz();
		boolean v = Orientation.VERTICAL.equals(this.orientation);

		// conector do segundo terminal da primeira bobina
		No no = getN2();
		no.setIndex(getConnectionPoint2());

		out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(), v ? 5 : 0,
				v ? 0 : -5);

		// desenhar indutores 1
		out += TikZ.drawIndutor(0, 0, v ? Direction.RIGHT : Direction.DOWN, "", Double.NaN);
		// pontos do acoplamento 1
		out += String.format(Locale.US, "\\draw (%f,%f) circle (%fcm);\n", v ? 0f : 1f, v ? 1f : 0f, .2f);

		// conector dos demais terminais (dois por bobina)
		for (int i = 0; i < xyW.length; i++) {
			no = getNN(2 * i + 2);
			no.setIndex(getConnectionPointN(2 * i + 2));
			out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(), this.xyW[i][0],
					-this.xyW[i][1]);

			no = getNN(2 * i + 3);
			no.setIndex(getConnectionPointN(2 * i + 3));
			out += String.format("\\draw (%d,%d) -- (%d,%d);\n", no.getX() - getX(), getY() - no.getY(),
					this.xyW[i][0] + (v ? 5 : 0), -(this.xyW[i][1] + (v ? 0 : 5)));

			// desenhar demais indutores
			out += TikZ.drawIndutor(this.xyW[i][0], -this.xyW[i][1], v ? Direction.LEFT : Direction.UP, "", Double.NaN);

			// demais pontos do acoplamento
			out += String.format(Locale.US, "\\draw (%f,%d) circle (%fcm);\n", this.xyW[i][0] - 1., -this.xyW[i][1],
					.2f);
		}
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

	@Override
	public String getXML() {
		// localização de cada enrolamento
		String loc = x + "," + y + ";";
		for (int i = 0; i < xyW.length; i++)
			loc += xyW[i][0] + "," + xyW[i][1] + ";";
		loc = loc.substring(0, loc.length() - 1);

		// nós conectados em cada um dos terminais
		String term = "";
		for (int i = 0; i < this.mc.getWindings(); i++) {
			term += super.getNN(2 * i) + "," + super.getConnectionPointN(2 * i) + ";";
			term += super.getNN(2 * i + 1) + "," + super.getConnectionPointN(2 * i + 1) + ";";
		}
		term = term.substring(0, term.length() - 1);

		return String.format("<mag label=\"%s\" windings=\"%d\">\n<loc dir=\"%d\">%s</loc>\n<term>%s</term>\n</mag>\n",
				super.label, this.mc.getWindings(), this.orientation.ordinal(), loc, term);
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
			return "Enrolamentos";
		case 2:
			return "Indutâncias";
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
			out = mc.getWindings();
			break;
		case 2:
			out = mc.getMatrix().getMatriz();
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
			this.setWindings((int) obj);
			break;
		case 2:
			mc.getMatrix().setMatriz((double[][]) obj);
			break;
		}
	}

	// ----------------- INTERFACE SUPER-ARESTA -----------------

	@Override
	public Set<Edge> getEdges(Vertex v) {
		// TODO Auto-generated method stub
		return null;
	}
}

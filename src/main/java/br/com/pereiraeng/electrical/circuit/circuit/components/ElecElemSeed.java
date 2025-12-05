package br.com.pereiraeng.electrical.circuit.circuit.components;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import br.com.pereiraeng.core.collections.ListUtils;
import br.com.pereiraeng.drawing.drawutils.LID;
import br.com.pereiraeng.drawing.drawutils.LID.DrawAction;

/**
 * Classe dos objetos sementes dos componentes customizados. Estes objetos
 * contêm todas as necessárias para caracterizar um tipo genérico de componente
 * (cada um das instâncias de um componente customizado tem uma semente que o
 * caracteriza, que indica como ele deve ser {@link #draw desenhado}, quais são
 * os {@link #attrs campos} que ele deve possuir , etc.).
 * 
 * @author Philipe PEREIRA
 *
 */
public class ElecElemSeed {

	/**
	 * Nome curto genérico
	 */
	private String type;

	/**
	 * Nome completo genérico
	 */
	private String name;

	/**
	 * Número de conexões
	 */
	private int numConns;

	/**
	 * Tabela de dispersão <strong>ordenada</strong> que associa para cada nome do
	 * atributo o seu valor <strong>padrão</strong>
	 */
	private LinkedHashMap<String, Object> attrs;

	/**
	 * Lista de instruções de desenho (que serão utilizados pelo {@link Graphics2D},
	 * pelo SVG, pelo TeX...). As instruções podem ser não-compiladas, o que
	 * significa que algum dos atributos do desenho não estão indicados na lista de
	 * instruções, precisando {@link #getDrawInstructions(List, boolean) receber
	 * informações adicionais} a partir dos atributos do objeto
	 */
	private LID draw;

	/**
	 * Pontos de conexão com os nós
	 */
	private List<Point> conn;

	/**
	 * Vetor de inteiros com quatro posições
	 */
	private int[] selRect;

	/**
	 * Construtor do objeto semente
	 * 
	 * @param type    nome curto genérico
	 * @param name    nome completo genérico
	 * @param numCons número de conexões
	 */
	public ElecElemSeed(String type, String name, int numCons) {
		this.type = type;
		this.name = name;
		this.numConns = numCons;
		this.attrs = new LinkedHashMap<>();
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return name;
	}

	public int getNumConns() {
		return numConns;
	}

	// atributos

	public void putAttr(String name, Object def) {
		attrs.put(name, def);
	}

	/**
	 * Função que retorna os nomes dos atributos
	 * 
	 * @return conjunto <strong>ordenado</strong> com os nomes dos atributos
	 */
	public Set<String> attribsNames() {
		return attrs.keySet();
	}

	public Object getAttribsValue(String key) {
		return attrs.get(key);
	}

	/**
	 * Função que retorna os valores padrões dos atributos
	 * 
	 * @return relação <strong>ordenada</strong> com os valores padrões
	 */
	public Collection<Object> getAttribsValues() {
		return attrs.values();
	}

	/**
	 * Função que retorna o número de atributos
	 * 
	 * @return número de atributos
	 */
	public int getAttribsCount() {
		return attrs.size();
	}

	// desenhos

	/**
	 * Os objetos 'seed' na biblioteca são escritos na forma SVG com suas grandezas
	 * geométricas multiplicadas por um fator dado (10x).
	 */
	public static final float MULT = .1f;

	/**
	 * Função que compila a lista de instruções, repassando à {@link #draw lista
	 * base} os atributos do objeto
	 * 
	 * @param attributes atributos do objeto
	 * @param reverse    <code>true</code> para manter as coordenadas,
	 *                   <code>false</code> para a inversão das coordenadas x e y
	 * @return lista de instruções compilada
	 */
	public List<Object[]> getDrawInstructions(List<Object> attributes, boolean reverse) {
		// copiar a LID, fazendo alterações nos casos em que há atributos
		List<Object[]> out = new ArrayList<>();
		for (Object[] inst : this.draw) {
			Object[] is = Arrays.copyOf(inst, inst.length);
			DrawAction da = DrawAction.valueOf(((String) is[0]).toUpperCase());
			switch (da) {
			case TEXT:
				String text = (String) is[is.length - 1];
				if (text.startsWith("#"))
					is[is.length - 1] = attributes.get(ListUtils.indexOf(this.attrs.keySet(), text.substring(1)));
			case CIRCLE:
				if (!reverse && da == DrawAction.CIRCLE) {
					// reverse não se aplica a textos (fica muito complicado achar uma posição do
					// texto que, ao se aplicar o transverso, fica bom em ambas posições)
					Object temp = is[1];
					is[1] = is[2];
					is[2] = temp;
				}
				String fill = null;
				if (is[5] != null)
					fill = is[5].toString();
				if (fill != null ? fill.startsWith("#") : false) {
					Object att = attributes
							.get(ListUtils.indexOf(this.attrs.keySet(), fill.substring(1, fill.indexOf('?'))));
					is[5] = (boolean) att ? fill.substring(fill.indexOf('?') + 1, fill.indexOf(':'))
							: fill.substring(fill.indexOf(':') + 1);
				}
				break;
			case RECT:
			case ELLIPSE:
			case LINE:
				if (!reverse) {
					Object temp = is[1];
					is[1] = is[2];
					is[2] = temp;
					temp = is[3];
					is[3] = is[4];
					is[4] = temp;
				}

				if (da == DrawAction.RECT) {
					fill = (String) is[6];
					if (fill.startsWith("#")) {
						Object att = attributes
								.get(ListUtils.indexOf(this.attrs.keySet(), fill.substring(1, fill.indexOf('?'))));
						is[6] = (boolean) att ? fill.substring(fill.indexOf('?') + 1, fill.indexOf(':'))
								: fill.substring(fill.indexOf(':') + 1);
					}
				}
				break;
			case POLYLINE:
			case POLYGON:
				if (!reverse) {
					String[] points = (String[]) is[1];
					points = Arrays.copyOf(points, points.length);
					for (int i = 0; i < points.length; i++) {
						String[] xy = points[i].split(",");
						points[i] = xy[1] + "," + xy[0];
					}
					is[1] = points;
				}
				break;
			case PATH:
				String visibility = (String) is[1];
				if (visibility != null ? visibility.startsWith("#") : false) {
					Object att = attributes.get(
							ListUtils.indexOf(this.attrs.keySet(), visibility.substring(1, visibility.indexOf('?'))));
					is[1] = (boolean) att ? visibility.substring(visibility.indexOf('?') + 1, visibility.indexOf(':'))
							: visibility.substring(visibility.indexOf(':') + 1);
				}
				break;
			}
			out.add(is);
		}
		return out;
	}

	/**
	 * Função que cria a lista de instruções de desenho
	 */
	protected void createList() {
		this.draw = new LID();
	}

	/**
	 * Função que adiciona um vetor à lista de instruções de desenho
	 * 
	 * @param data vetor com a instrução
	 */
	protected void addDraw(Object[] data) {
		this.draw.add(data);
	}

	// terminais - conexões com os nós

	protected void createConn() {
		this.conn = new ArrayList<>(numConns - 1);
	}

	protected void addConn(Point xy) {
		this.conn.add(xy);
	}

	protected Point getTerminalPoint(int i) {
		return this.conn.get(i);
	}

	// área de seleção

	/**
	 * Função que estabelece a posição e dimensões do retângulo que determina a área
	 * de seleção
	 * 
	 * @param x offset horizontal
	 * @param y offset vertical
	 * @param w largura
	 * @param h altura
	 */
	protected void setSelection(int x, int y, int w, int h) {
		this.selRect = new int[] { x, y, w, h };
	}

	/**
	 * Função que retorna a posição e dimensões do retângulo que determina a área de
	 * seleção
	 * 
	 * @return vetor com 4 posições, indicando, respectivamente
	 *         <ol>
	 *         <li>offset horizontal;</i>
	 *         <li>offset vertical;</i>
	 *         <li>largura;</i>
	 *         <li>altura.</i>
	 *         </ol>
	 */
	protected int[] getSelRect() {
		return selRect;
	}

	// ----------------------------- XML -----------------------------

	public String getXML() {
		StringBuilder out = new StringBuilder(
				String.format("%d,%d,%d,%d,%d", getNumConns(), selRect[0], selRect[1], selRect[2], selRect[3]));
		for (int i = 0; i < this.conn.size(); i++) {
			Point p = getTerminalPoint(i);
			out.append(String.format(",%d,%d", p.x, p.y));
		}
		return out.toString();
	}
}

package br.com.pereiraeng.electrical.circuit.circuit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

import br.com.pereiraeng.math.Complex;
import br.com.pereiraeng.math.Multiplicador;
import br.com.pereiraeng.math.Vec;
import br.com.pereiraeng.core.Direction;
import br.com.pereiraeng.core.Orientation;
import br.com.pereiraeng.drawing.drawutils.DrawingUtils;

/**
 * Classe com as funções que fazem desenhos de elementos de circuito elétrico
 * 
 * @author Philipe PEREIRA
 *
 */
public class CircuitDrawer {

	/**
	 * Comprimento, em pixels, dos componentes (distância de um terminal a outro do
	 * resistor, capacitor, indutor, gerador, etc.)
	 */
	public static final int COMP_LENGTH = 50;

	/**
	 * 
	 * 
	 * @param x         abscissa da coordenada de referência
	 * @param y         ordenada da coordenada de referência
	 * @param wf
	 *                  <ol start="0">
	 *                  <li>geral;</i>
	 *                  <li>contínuo;</i>
	 *                  <li>alternado.</i>
	 *                  </ol>
	 * @param output
	 *                  <ol start="0">
	 *                  <li>tensão;</i>
	 *                  <li>corrente.</i>
	 *                  </ol>
	 * @param direction
	 * @param label
	 * @param value
	 * @param g         objeto gráfico
	 */
	public static void drawGerador(int x, int y, int wf, int output, Direction direction, String label, Object value,
			Graphics g) {
		g.setColor(Color.GREEN);

		// etiqueta com o nome e valor
		if (label != null)
			g.drawString(label, x + 25, y + 25);

		if (value != null) {
			String str;
			if (value instanceof Number) {
				double dp;
				Number n = (Number) value;
				if (n instanceof Complex)
					dp = ((Complex) n).getMod();
				else
					dp = n.doubleValue();
				str = Multiplicador.getMult(dp, 3, Multiplicador.POW3) + (output == 0 ? "V" : "I");
			} else
				str = value.toString();
			g.drawString(str, x + 25, y + 40);
		}

		g.setColor(Color.BLACK);
		switch (wf) {
		case 0: // geral
			switch (output) {
			case 0: // tensão
				if (direction.isVertical()) {
					g.drawString("+", x - 15, y + 10);
					g.drawOval(x - 15, y + 10, 30, 30);
					g.drawLine(x, y, x, y + 10);
					g.drawLine(x, y + 40, x, y + COMP_LENGTH);
					g.drawString("-", x, y + COMP_LENGTH);
				} else {
					g.drawString("+", x + 10, y - 15);
					g.drawOval(x + 10, y - 15, 30, 30);
					g.drawLine(x, y, x + 10, y);
					g.drawLine(x + 40, y, x + COMP_LENGTH, y);
					g.drawString("-", x + COMP_LENGTH, y);
				}
				break;
			case 1: // corrente
				switch (direction) {
				case RIGHT:
					g.drawPolygon(new int[] { x + 10, x + 10, x + 40 }, new int[] { y - 15, y + 15, y }, 3);
					g.drawLine(x, y, x + COMP_LENGTH, y);
					DrawingUtils.drawArrow(g, x + 40, y, direction);
					break;
				case UP:
					g.drawPolygon(new int[] { x - 15, x + 15, x }, new int[] { y + 40, y + 40, y + 10 }, 3);
					g.drawLine(x, y, x, y + COMP_LENGTH);
					DrawingUtils.drawArrow(g, x, y + 10, direction);
					break;
				case LEFT:
					g.drawPolygon(new int[] { x + 40, x + 40, x + 10 }, new int[] { y - 15, y + 15, y }, 3);
					g.drawLine(x, y, x + COMP_LENGTH, y);
					DrawingUtils.drawArrow(g, x + 10, y, direction);
					break;
				case DOWN:
					g.drawPolygon(new int[] { x - 15, x + 15, x }, new int[] { y + 10, y + 10, y + 40 }, 3);
					g.drawLine(x, y, x, y + COMP_LENGTH);
					DrawingUtils.drawArrow(g, x, y + 40, direction);
					break;
				default:
					break;
				}
				break;
			}
			break;
		case 1: // contínuo
			switch (output) {
			case 0: // tensão
				switch (direction) {
				case RIGHT:
					g.drawLine(x, y, x + 20, y);
					g.drawLine(x + 30, y - 25, x + 30, y + 25);
					g.drawLine(x + 20, y - 15, x + 20, y + 15);
					g.drawLine(x + 30, y, x + COMP_LENGTH, y);
					break;
				case UP:
					g.drawLine(x, y, x, y + 20);
					g.drawLine(x - 25, y + 20, x + 25, y + 20);
					g.drawLine(x - 15, y + 30, x + 15, y + 30);
					g.drawLine(x, y + 30, x, y + COMP_LENGTH);
					break;
				case LEFT:
					g.drawLine(x, y, x + 20, y);
					g.drawLine(x + 20, y - 25, x + 20, y + 25);
					g.drawLine(x + 30, y - 15, x + 30, y + 15);
					g.drawLine(x + 30, y, x + COMP_LENGTH, y);
					break;
				case DOWN:
					g.drawLine(x, y, x, y + 20);
					g.drawLine(x - 15, y + 20, x + 15, y + 20);
					g.drawLine(x - 25, y + 30, x + 25, y + 30);
					g.drawLine(x, y + 30, x, y + COMP_LENGTH);
					break;
				default:
					break;
				}
				break;
			case 1: // corrente
				switch (direction) {
				case RIGHT:
					g.drawRect(x + 10, y - 15, 30, 30);
					g.drawLine(x, y, x + COMP_LENGTH, y);
					DrawingUtils.drawArrow(g, x + 40, y, direction);
					break;
				case UP:
					g.drawRect(x - 15, y + 10, 30, 30);
					g.drawLine(x, y, x, y + COMP_LENGTH);
					DrawingUtils.drawArrow(g, x, y + 10, direction);
					break;
				case LEFT:
					g.drawRect(x + 10, y - 15, 30, 30);
					g.drawLine(x, y, x + COMP_LENGTH, y);
					DrawingUtils.drawArrow(g, x + 10, y, direction);
					break;
				case DOWN:
					g.drawRect(x - 15, y + 10, 30, 30);
					g.drawLine(x, y, x, y + COMP_LENGTH);
					DrawingUtils.drawArrow(g, x, y + 40, direction);
					break;
				default:
					break;
				}
				break;
			}

			break;
		case 2: // alternado
		default:
			switch (output) {
			case 0: // tensão
				if (direction == Direction.UP || direction == Direction.DOWN) {
					drawSync(x, y + 25, g);

					g.drawLine(x, y, x, y + 10);
					g.drawLine(x, y + 40, x, y + COMP_LENGTH);
				} else {
					drawSync(x + 25, y, g);

					g.drawLine(x, y, x + 10, y);
					g.drawLine(x + 40, y, x + COMP_LENGTH, y);
				}
				break;
			case 1: // corrente
				switch (direction) {
				case RIGHT:
					g.drawOval(x + 10, y - 15, 30, 30);
					g.drawLine(x, y, x + COMP_LENGTH, y);
					DrawingUtils.drawArrow(g, x + 40, y, direction);
					break;
				case UP:
					g.drawOval(x - 15, y + 10, 30, 30);
					g.drawLine(x, y, x, y + COMP_LENGTH);
					DrawingUtils.drawArrow(g, x, y + 10, direction);
					break;
				case LEFT:
					g.drawOval(x + 10, y - 15, 30, 30);
					g.drawLine(x, y, x + COMP_LENGTH, y);
					DrawingUtils.drawArrow(g, x + 10, y, direction);
					break;
				case DOWN:
					g.drawOval(x - 15, y + 10, 30, 30);
					g.drawLine(x, y, x, y + COMP_LENGTH);
					DrawingUtils.drawArrow(g, x, y + 40, direction);
					break;
				default:
					break;
				}
				break;
			}
			break;
		}
	}

	/**
	 * Função que desenha uma máquina síncrona
	 * 
	 * @param x abscissa da coordenada de referência
	 * @param y ordenada da coordenada de referência
	 * @param g objeto gráfico
	 */
	public static void drawSync(int x, int y, Graphics g) {
		drawSync(x, y, g, 30);
	}

	/**
	 * Função que desenha uma máquina síncrona
	 * 
	 * @param x abscissa da coordenada de referência
	 * @param y ordenada da coordenada de referência
	 * @param g objeto gráfico
	 * @param d diâmetro, em pixels
	 */
	public static void drawSync(int x, int y, Graphics g, int d) {
		int r = d / 6;
		int r2 = 2 * r;
		g.drawOval(x - d / 2, y - d / 2, d, d);
		g.drawArc(x - r2, y - r, r2, r2, 0, 180);
		g.drawArc(x, y - r, r2, r2, 180, 180);
	}

	/**
	 * Função que desenha um resistor
	 * 
	 * @param x      abscissa da coordenada de referência
	 * @param y      ordenada da coordenada de referência
	 * @param orient sentido para o qual o desenho aponta, com relação às
	 *               coordenadas de referências
	 * @param label
	 * @param value
	 * @param g      objeto gráfico
	 */
	public static void drawResistor(int x, int y, Orientation orient, String label, double value, Graphics g) {
		// etiqueta com o nome e valor
		if (label != null ? !"".equals(label) : false)
			g.drawString(label, x, y - 10);
		if (value >= 0 && !Double.isNaN(value))
			g.drawString(Multiplicador.getMult(value, 3, Multiplicador.POW3) + "\u03A9", x, y + 20);

		// rodar 90 ao se desenhar no vertical
		if (orient == Orientation.VERTICAL) {
			int aux = x;
			x = y;
			y = aux;
		}

		int[] x1 = new int[12];
		int[] y1 = new int[12];

		x1[0] = x;
		x1[1] = x + 5;
		x1[2] = x + 7;

		y1[0] = y;
		y1[1] = y;
		y1[2] = y - 10;

		for (int i = 3; i < x1.length - 2; i++) {
			x1[i] = x1[i - 1] + 5;
			if (y1[i - 1] > y)
				y1[i] = y1[i - 1] - 20;
			else
				y1[i] = y1[i - 1] + 20;
		}

		x1[x1.length - 2] = x1[x1.length - 3] + 3;
		y1[x1.length - 2] = y1[0];

		x1[x1.length - 1] = x1[x1.length - 2] + 5;
		y1[x1.length - 1] = y1[0];

		switch (orient) {
		case HORIZONTAL:
			g.drawPolyline(x1, y1, x1.length);
			break;
		case VERTICAL:
			g.drawPolyline(y1, x1, x1.length);
			break;
		}
	}

	/**
	 * 
	 * @param x           abscissa da coordenada de referência
	 * @param y           ordenada da coordenada de referência
	 * @param orientation sentido para o qual o desenho aponta, com relação às
	 *                    coordenadas de referências
	 * @param label
	 * @param value
	 * @param g           objeto gráfico
	 */
	public static void drawIndutor(int x, int y, Orientation orientation, String label, double value, Graphics g) {
		drawIndutor(x, y, Orientation.VERTICAL == orientation ? Direction.DOWN : Direction.RIGHT, label, value, g);
	}

	/**
	 * 
	 * @param x         abscissa da coordenada de referência
	 * @param y         ordenada da coordenada de referência
	 * @param direction sentido para o qual o desenho aponta, com relação às
	 *                  coordenadas de referências
	 * @param label
	 * @param value
	 * @param g         objeto gráfico
	 */
	public static void drawIndutor(int x, int y, Direction direction, String label, double value, Graphics g) {
		drawIndutor(x, y, direction, label, value, g, true);
	}

	public static void drawIndutor(int x, int y, Direction direction, String label, double value, Graphics g,
			boolean terminals) {
		if (terminals)
			drawConnectors(g, x, y, direction, 5, 45, COMP_LENGTH);

		switch (direction) {
		case LEFT:
			drawTurns(g, x - 45, y - 10, 4, direction);
			break;
		case RIGHT:
			drawTurns(g, x + 5, y, 4, direction);
			break;
		case UP:
			drawTurns(g, x, y - 45, 4, direction);
			break;
		case DOWN:
			drawTurns(g, x - 10, y + 5, 4, direction);
			break;
		default:
			break;
		}

		// etiqueta com o nome e valor
		if (label != null ? !"".equals(label) : false)
			g.drawString(label, x, y - 10);
		if (value >= 0. && !Double.isNaN(value))
			g.drawString(Multiplicador.getMult(value, 3, Multiplicador.POW3) + "H", x, y + 20);
	}

	/**
	 * 
	 * @param x           abscissa da coordenada de referência
	 * @param y           ordenada da coordenada de referência
	 * @param orientation sentido para o qual o desenho aponta, com relação às
	 *                    coordenadas de referências
	 * @param label
	 * @param value
	 * @param g           objeto gráfico
	 */
	public static void drawCapacitor(int x, int y, Orientation orientation, String label, double value, Graphics g) {
		switch (orientation) {
		case HORIZONTAL:
			drawConnectors(g, x, y, Direction.RIGHT, 20, 30, COMP_LENGTH);
			g.drawLine(x + 20, y - 15, x + 20, y + 15);
			g.drawLine(x + 30, y - 15, x + 30, y + 15);
			break;
		case VERTICAL:
			drawConnectors(g, x, y, Direction.DOWN, 20, 30, COMP_LENGTH);
			g.drawLine(x - 15, y + 20, x + 15, y + 20);
			g.drawLine(x - 15, y + 30, x + 15, y + 30);
			break;
		}

		// etiqueta com o nome e valor
		if (label != null ? !"".equals(label) : false)
			g.drawString(label, x, y + 20);
		if (value >= 0 && !Double.isNaN(value))
			g.drawString(Multiplicador.getMult(value, 3, Multiplicador.POW3) + "F", x, y + 40);
	}

	/**
	 * Função que desenha o símbolo de aterramento
	 * 
	 * @param x abscissa da coordenada de referência
	 * @param y ordenada da coordenada de referência
	 * @param g objeto gráfico
	 */
	public static void drawTerra(int x, int y, Graphics g) {
		drawTerra(x, y, g, Direction.UP);
	}

	/**
	 * Função que desenha o símbolo de aterramento
	 * 
	 * @param x         abscissa da coordenada de referência
	 * @param y         ordenada da coordenada de referência
	 * @param g         objeto gráfico
	 * @param direction sentido para o qual o desenho aponta, com relação às
	 *                  coordenadas de referências
	 */
	public static void drawTerra(int x, int y, Graphics g, Direction direction) {
		switch (direction) {
		case DOWN:
			g.drawLine(x, y, x, y - 10);
			g.drawLine(x - 21, y - 10, x + 21, y - 10);
			g.drawLine(x - 13, y - 15, x + 13, y - 15);
			g.drawLine(x - 5, y - 20, x + 5, y - 20);
			break;
		case RIGHT:
			g.drawLine(x, y, x - 10, y);
			g.drawLine(x - 10, y - 21, x - 10, y + 21);
			g.drawLine(x - 15, y - 13, x - 15, y + 13);
			g.drawLine(x - 20, y - 5, x - 20, y + 5);
			break;
		case UP:
			g.drawLine(x, y, x, y + 10);
			g.drawLine(x - 21, y + 10, x + 21, y + 10);
			g.drawLine(x - 13, y + 15, x + 13, y + 15);
			g.drawLine(x - 5, y + 20, x + 5, y + 20);
			break;
		case LEFT:
			g.drawLine(x, y, x + 10, y);
			g.drawLine(x + 10, y - 21, x + 10, y + 21);
			g.drawLine(x + 15, y - 13, x + 15, y + 13);
			g.drawLine(x + 20, y - 5, x + 20, y + 5);
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @param x         abscissa do primeiro ponto
	 * @param y         ordenada do primeiro ponto
	 * @param tamanho
	 * @param direction sentido para o qual o desenho aponta, com relação às
	 *                  coordenadas de referências
	 * @param label
	 * @param valor
	 * @param g
	 */
	public static void drawTensao(int x, int y, int tamanho, Direction direction, String label, double valor,
			Graphics g) {
		g.setColor(Color.GREEN.darker());

		int angulo = 15;
		int posicaoX = 0, posicaoY = 0;
		double R = (tamanho / 2) / Math.sin(Math.toRadians(angulo));

		switch (direction) {
		case RIGHT:
			g.drawArc((int) (x - R - tamanho / 2), (int) (y - R * (1 - Math.cos(Math.toRadians(angulo)))),
					(int) (2 * R), (int) (2 * R), angulo + 90, -30);
			posicaoX = -tamanho / 2;
			posicaoY = -15;
			break;
		case UP:
			g.drawArc((int) (x - R * (1 + Math.cos(Math.toRadians(angulo)))), (int) (y - R + tamanho / 2),
					(int) (2 * R), (int) (2 * R), angulo, -2 * angulo);
			posicaoX = 15;
			posicaoY = tamanho / 2;
			break;
		case LEFT:
			g.drawArc((int) (x - R + tamanho / 2), (int) (y - R * (1 - Math.cos(Math.toRadians(angulo)))),
					(int) (2 * R), (int) (2 * R), angulo + 90, -30);
			posicaoX = tamanho / 2;
			posicaoY = -15;
			break;
		case DOWN:
			g.drawArc((int) (x - R * (1 + Math.cos(Math.toRadians(angulo)))), (int) (y - R - tamanho / 2),
					(int) (2 * R), (int) (2 * R), angulo, -2 * angulo);
			posicaoX = 15;
			posicaoY = -tamanho / 2;
			break;
		default:
			break;
		}

		DrawingUtils.drawArrow(g, x, y, direction);

		// etiqueta com o nome e valor
		if (label != null)
			g.drawString(label, x + posicaoX, y + posicaoY - 15);
		if (valor >= 0)
			g.drawString(Multiplicador.getMult(valor, 3, Multiplicador.POW3) + "V", x + posicaoX, y + posicaoY);
	}

	public static void drawCorrente(int x, int y, Direction direction, String label, double value, Graphics g) {
		g.setColor(Color.BLUE);
		DrawingUtils.drawArrow(g, x, y, direction);

		// etiqueta com o nome e valor
		if (label != null)
			g.drawString(label, x + 8, y - 22);
		if (value >= 0) {
			g.drawString(Multiplicador.getMult(value, 3, Multiplicador.POW3) + "V", x + 8, y - 10);
		}
	}

	private static final int BLOCK_WIDTH = 15;

	/**
	 * 
	 * @param x                abscissa do primeiro ponto
	 * @param y                ordenada do primeiro ponto
	 * @param carryOnlyForward
	 * @param block
	 * @param direction        sentido para o qual o desenho aponta, com relação às
	 *                         coordenadas de referências
	 * @param label
	 * @param g
	 */
	public static void drawSwitch(int x, int y, boolean carryOnlyForward, int block, Direction direction, String label,
			Graphics g) {
		// vertical ou horizontal
		boolean v = direction.isVertical();
		// no sentido decrescente dos pixels
		boolean f = Direction.UP.equals(direction) || Direction.LEFT.equals(direction);

		if (label != null)
			g.drawString(label, x + 25, y + 30);

		// conector
		switch (direction) {
		case UP:
			g.drawLine(x, y, x, y - 20);
			g.drawLine(x, y - 30, x, y - COMP_LENGTH);
			break;
		case DOWN:
			g.drawLine(x, y, x, y + 20);
			g.drawLine(x, y + 30, x, y + COMP_LENGTH);
			break;
		case RIGHT:
			g.drawLine(x, y, x + 20, y);
			g.drawLine(x + 30, y, x + COMP_LENGTH, y);
			break;
		case LEFT:
			g.drawLine(x, y, x - 20, y);
			g.drawLine(x - 30, y, x - COMP_LENGTH, y);
			break;
		default:
			break;
		}

		int[] y0 = new int[] { 30, 20, 30 }, y1 = new int[] { 20, 30, 20 };

		// condução de corrente
		if (carryOnlyForward) {
			int[] xs = new int[] { -6, 0, 6 };

			g.drawPolygon(Vec.shift(v ? xs : (f ? y0 : y1), x - (direction == Direction.LEFT ? COMP_LENGTH : 0)),
					Vec.shift(v ? (f ? y0 : y1) : xs, y - (direction == Direction.UP ? COMP_LENGTH : 0)), 3);
		} else {
			int[] xs = new int[] { -9, -3, 3 };
			g.drawPolygon(Vec.shift(v ? xs : (f ? y0 : y1), x - (direction == Direction.LEFT ? COMP_LENGTH : 0)),
					Vec.shift(v ? (f ? y0 : y1) : xs, y - (direction == Direction.UP ? COMP_LENGTH : 0)), 3);

			xs = new int[] { -3, 3, 9 };
			g.drawPolygon(Vec.shift(v ? xs : (f ? y1 : y0), x - (direction == Direction.LEFT ? COMP_LENGTH : 0)),
					Vec.shift(v ? (f ? y1 : y0) : xs, y - (direction == Direction.UP ? COMP_LENGTH : 0)), 3);
		}

		// bloqueio de tensão
		switch (block) {
		case 0:
			switch (direction) {
			case UP:
				g.drawLine(x - BLOCK_WIDTH, y - 30, x + BLOCK_WIDTH, y - 30);
				break;
			case DOWN:
				g.drawLine(x - BLOCK_WIDTH, y + 30, x + BLOCK_WIDTH, y + 30);
				break;
			case RIGHT:
				g.drawLine(x + 30, y - BLOCK_WIDTH, x + 30, y + BLOCK_WIDTH);
				break;
			case LEFT:
				g.drawLine(x - 30, y - BLOCK_WIDTH, x - 30, y + BLOCK_WIDTH);
				break;
			default:
				break;
			}
			break;
		case 1:
			switch (direction) {
			case UP:
				g.drawLine(x - BLOCK_WIDTH, y - 20, x + BLOCK_WIDTH, y - 20);
				break;
			case DOWN:
				g.drawLine(x - BLOCK_WIDTH, y + 20, x + BLOCK_WIDTH, y + 20);
				break;
			case RIGHT:
				g.drawLine(x + 20, y - BLOCK_WIDTH, x + 20, y + BLOCK_WIDTH);
				break;
			case LEFT:
				g.drawLine(x - 20, y - BLOCK_WIDTH, x - 20, y + BLOCK_WIDTH);
				break;
			default:
				break;
			}
			break;
		case 2:
			switch (direction) {
			case UP:
				g.drawLine(x - BLOCK_WIDTH, y - 20, x + BLOCK_WIDTH, y - 20);
				g.drawLine(x - BLOCK_WIDTH, y - 30, x + BLOCK_WIDTH, y - 30);
				break;
			case DOWN:
				g.drawLine(x - BLOCK_WIDTH, y + 20, x + BLOCK_WIDTH, y + 20);
				g.drawLine(x - BLOCK_WIDTH, y + 30, x + BLOCK_WIDTH, y + 30);
				break;
			case RIGHT:
				g.drawLine(x + 20, y - BLOCK_WIDTH, x + 20, y + BLOCK_WIDTH);
				g.drawLine(x + 30, y - BLOCK_WIDTH, x + 30, y + BLOCK_WIDTH);
				break;
			case LEFT:
				g.drawLine(x - 20, y - BLOCK_WIDTH, x - 20, y + BLOCK_WIDTH);
				g.drawLine(x - 30, y - BLOCK_WIDTH, x - 30, y + BLOCK_WIDTH);
				break;
			default:
				break;
			}
			break;
		}
	}

	/**
	 * 
	 * @param x abscissa do primeiro ponto
	 * @param y ordenada do primeiro ponto
	 * @param g
	 */
	public static void drawInverter(int x, int y, Graphics g) {
		// conector
		g.drawLine(x, y, x, y + 10);

		// inversor
		g.drawRect(x - 20, y + 10, 40, 40);
		g.drawLine(x - 20, y + 10, x + 20, y + 40);

		Font f = g.getFont();
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 30));
		g.drawString("~", x + 5, y + 25);
		g.drawString("=", x - 15, y + 45);
		g.setFont(f);
	}

	/**
	 * 
	 * @param x abscissa do primeiro ponto
	 * @param y ordenada do primeiro ponto
	 * @param g
	 */
	public static void drawSVC(int x, int y, Graphics g) {
		// conector
		g.drawLine(x, y, x, y + 20);
		// paralelo
		g.drawLine(x - 20, y + 20, x + 10, y + 20);

		// /// linha do reator controlado por tiristor
		g.drawLine(x - 20, y + 20, x - 20, y + 30);

		// tiristores paralelo
		int t1x = x - 30, ty = y + 35, t2x = x - 10;

		g.drawPolyline(new int[] { x - 30, x - 30, x - 10, x - 10 }, new int[] { ty, y + 30, y + 30, ty }, 4);

		g.drawPolyline(new int[] { t1x - 5, t1x, t1x - 5, t1x + 5, t1x, t1x + 5 },
				new int[] { ty, ty, ty + 5, ty + 5, ty, ty }, 6);

		g.drawPolyline(new int[] { t2x - 5, t2x, t2x - 5, t2x + 5, t2x, t2x + 5 },
				new int[] { ty + 5, ty + 5, ty, ty, ty + 5, ty + 5 }, 6);

		g.drawPolyline(new int[] { x - 30, x - 30, x - 10, x - 10 }, new int[] { ty + 5, ty + 10, ty + 10, ty + 5 }, 4);

		// linha do reator
		g.drawLine(x - 20, ty + 10, x - 20, ty + 20);

		drawTurns(g, x - 30, ty + 20, 3, Direction.DOWN);

		// /// linha do capacitor
		g.drawLine(x + 10, y + 20, x + 10, y + 60);

		g.drawLine(x, y + 60, x + 20, y + 60);
		g.drawLine(x, y + 65, x + 20, y + 65);
	}

	/**
	 * Função que desenha um transformador de um enrolamento (regulador de tensão,
	 * transformador de aterramento)
	 * 
	 * @param g         objeto gráfico
	 * @param x         abscissa do primeiro ponto
	 * @param y         ordenada do primeiro ponto
	 * @param direction sentido para o qual o desenho aponta, com relação às
	 *                  coordenadas de referências
	 * @param turns     número de semi-círculos
	 * @param radius    tamanho dos semi-círculos, em pixels
	 * @param reg       <code>true</code> para transformador regulador
	 */
	public static void drawTransformer1(Graphics2D g, int x, int y, Direction direction, int turns, int radius,
			boolean reg) {

		int gap = radius / 2;
		int dist = gap + radius;

		if (reg) {
			int xc, yc;
			switch (direction) {
			case RIGHT:
				xc = x + radius;
				yc = y;
				break;
			case DOWN:
				xc = x;
				yc = y + radius;
				break;
			case UP:
				xc = x;
				yc = y - radius;
				break;
			case LEFT:
				xc = x - radius;
				yc = y;
				break;
			default:
				xc = 0;
				yc = 0;
				break;
			}

			int s = 2 * gap;

			int x1 = xc + s;
			int y1 = yc - s;
			int x2 = xc - s;
			int y2 = yc + s;

			g.fillPolygon(new int[] { x1 - 5, x1 + 5, x1 + 5 }, new int[] { y1 - 5, y1 + 5, y1 - 5 }, 3);
			g.drawLine(x1, y1, x2, y2);
		}

		drawConnectors(g, x, y, direction, 2 * radius);
		switch (direction) {
		case UP:
			x -= turns * gap;
			y -= dist;
			break;
		case DOWN:
			x -= turns * gap;
			y += gap;
			break;
		case LEFT:
			y -= turns * gap;
			x -= dist;
			break;
		case RIGHT:
			y -= turns * gap;
			x += gap;
			break;
		default:
			break;
		}
		CircuitDrawer.drawTurns(g, x, y, turns, direction.previous(), radius);

	}

	/**
	 * Função que desenha um transformador de dois enrolamentos
	 * 
	 * @param g         objeto gráfico
	 * @param x         abscissa do primeiro ponto
	 * @param y         ordenada do primeiro ponto
	 * @param direction sentido para o qual o desenho aponta, com relação às
	 *                  coordenadas de referências
	 * @param turns     número de semi-círculos
	 * @param radius    tamanho dos semi-círculos, em pixels
	 * @param prim      <code>true</code> para por uma linha mais espessa no
	 *                  primário, destacando-o
	 * @param reg       <code>true</code> para transformador regulador
	 */
	public static int[] drawTransformer2(Graphics2D g, int x, int y, Direction direction, int turns, int radius,
			boolean prim, boolean reg) {

		int gap = radius / 2;
		int dist = gap + radius;

		int[] out = drawConnectors(g, x, y, direction, gap, 3 * radius, 3 * radius + gap);

		int x2, y2;
		switch (direction) {
		case UP:
			x -= turns * gap;
			y -= dist;
			x2 = x;
			y2 = y - dist;
			break;
		case DOWN:
			x -= turns * gap;
			y += gap;
			x2 = x;
			y2 = y + dist;
			break;
		case LEFT:
			y -= turns * gap;
			x -= dist;
			x2 = x - dist;
			y2 = y;
			break;
		case RIGHT:
			y -= turns * gap;
			x += gap;
			x2 = x + dist;
			y2 = y;
			break;
		default:
			x2 = 0;
			y2 = 0;
			break;
		}

		// enrolamentos
		Direction d = direction.previous();
		BasicStroke b = null;
		if (prim) {
			b = (BasicStroke) g.getStroke();
			g.setStroke(new BasicStroke(2f));
		}
		CircuitDrawer.drawTurns(g, x, y, turns, d, radius);
		if (prim)
			g.setStroke(b);
		CircuitDrawer.drawTurns(g, x2, y2, turns, d.oposite(), radius);

		return out;
	}

	public static int[] drawTransformer3(Graphics2D g, int x, int y, Direction direction, int turns, int radius,
			boolean prim, boolean reg) {

		int gap = radius / 2;
		int dist = gap + radius;

		int[] out = drawConnectors(g, x, y, direction, gap, 3 * radius + 3 * gap, 3 * radius + 4 * gap);

		int x2, y2, x3, y3;
		switch (direction) {
		case UP:
			x -= turns * gap;
			y -= dist;
			x2 = x;
			y2 = y - dist;
			x3 = x;
			y3 = y - 2 * dist;
			break;
		case DOWN:
			x -= turns * gap;
			y += gap;
			x2 = x;
			y2 = y + dist;
			x3 = x;
			y3 = y + 2 * dist;
			break;
		case LEFT:
			y -= turns * gap;
			x -= dist;
			x2 = x - dist;
			y2 = y;
			x3 = x - 2 * dist;
			y3 = y;
			break;
		case RIGHT:
			y -= turns * gap;
			x += gap;
			x2 = x + dist;
			y2 = y;
			x3 = x + 2 * dist;
			y3 = y;
			break;
		default:
			x2 = 0;
			y2 = 0;
			x3 = 0;
			y3 = 0;
			break;
		}

		// enrolamentos
		Direction d = direction.previous();
		BasicStroke b = null;
		if (prim) {
			b = (BasicStroke) g.getStroke();
			g.setStroke(new BasicStroke(2f));
		}
		CircuitDrawer.drawTurns(g, x, y, turns, d, radius);
		if (prim)
			g.setStroke(b);
		Direction d0 = d.oposite();
		CircuitDrawer.drawTurns(g, x2, y2, turns, d0, radius);
		CircuitDrawer.drawTurns(g, x3, y3, turns, d0, radius);

		return out;
	}

	/**
	 * Função que desenha um determinado elemento (determinado pelo ícone enviado)
	 * ligando dois pontos dados
	 * 
	 * @param g    objeto gráfico
	 * @param x1   abscissa do primeiro ponto
	 * @param y1   ordenada do primeiro ponto
	 * @param x2   abscissa do segundo ponto
	 * @param y2   ordenada do segundo ponto
	 * @param icon ícone do elemento a ser desenhado (a altura do ícone determina o
	 *             espaço entre os conectores)
	 */
	public static void drawElementConnector(Graphics2D g, int x1, int y1, int x2, int y2, Icon icon) {
		int dx = x2 - x1, dy = y2 - y1;

		int comp = (int) Math.hypot(dy, dx);
		int resto = (comp - icon.getIconHeight()) / 2;
		if (resto < 0)
			resto = 0;

		double b = Math.atan2(dy, dx);
		g.translate(x1, y1);
		g.rotate(b);

		g.drawLine(0, 0, resto, 0);

		icon.paintIcon(null, g, resto, 0);

		g.drawLine(comp - resto, 0, comp, 0);

		g.rotate(-b);
		g.translate(-x1, -y1);
	}

	// ------------------------- DCA'S -------------------------

	/**
	 * Função que desenha o símbolo de um chave (em posição aberta). De uma
	 * extremidade a outra do componente, há 40 pixels de largura.
	 * 
	 * @param g           objeto gráfico
	 * @param x           abscissa da coordenada de referência
	 * @param y           ordenada da coordenada de referência
	 * @param orientation sentido para o qual o desenho aponta, com relação às
	 *                    coordenadas de referências
	 */
	public static void drawSwitch(Graphics2D g, int x, int y, Orientation orientation) {
		drawSwitch(g, x, y, orientation == Orientation.HORIZONTAL ? Direction.RIGHT : Direction.UP, 0);
	}

	public static void drawSwitch(Graphics2D g, int x, int y, Direction direction, int add) {
		drawSwitch(g, x, y, direction, add, null);
	}

	/**
	 * Função que desenha o símbolo de um chave (em posição aberta). De uma
	 * extremidade a outra do componente, há 40 pixels de largura.
	 * 
	 * @param g           objeto gráfico
	 * @param x           abscissa da coordenada de referência
	 * @param y           ordenada da coordenada de referência
	 * @param orientation sentido para o qual o desenho aponta, com relação às
	 *                    coordenadas de referências
	 * @param add         opcionais:
	 *                    <ol>
	 *                    <li>fusível</i>
	 *                    </ol>
	 * 
	 */
	public static void drawSwitch(Graphics2D g, int x, int y, Direction direction, int add, Color color) {
		boolean v = direction.isVertical();
		x += (direction == Direction.LEFT ? -40 : 0);
		y += (direction == Direction.UP ? -40 : 0);
		if (v) {
			g.drawLine(x, y + 8, x, y);
			g.drawOval(x - 2, y + 8, 4, 4);

			g.drawLine(x - 2, y + 12, x - 17, y + 27);
			if (add > 0) {
				if ((add & 1) > 0) { // chave fúsivel
					g.drawArc(x - 11, y + 11, 10, 10, 45, 180);
					g.drawArc(x - 18, y + 18, 10, 10, 45, -180);
				}
			}

			g.drawOval(x - 2, y + 28, 4, 4);
			g.drawLine(x, y + 32, x, y + 40);

			if (color != null) {
				Color c = g.getColor();
				g.setColor(color);
				g.drawLine(x - 17, y + 27, x - 2, y + 30);
				g.setColor(c);
			}
		} else {
			g.drawLine(x, y, x + 8, y);
			g.drawOval(x + 8, y - 2, 4, 4);

			g.drawLine(x + 12, y - 2, x + 27, y - 17);
			if (add > 0) {
				if ((add & 1) > 0) { // chave fúsivel
					g.drawArc(x + 11, y - 11, 10, 10, 45, 180);
					g.drawArc(x + 18, y - 18, 10, 10, 45, -180);
				}
			}

			g.drawOval(x + 28, y - 2, 4, 4);
			g.drawLine(x + 32, y, x + 40, y);

			if (color != null) {
				Color c = g.getColor();
				g.setColor(color);
				g.drawLine(x + 27, y - 17, x + 30, y - 2);
				g.setColor(c);
			}
		}
	}

	/**
	 * Função que desenha o símbolo de um disjuntor (uma caixa). De uma extremidade
	 * a outra do componente, há 40 pixels de largura.
	 * 
	 * @param g           objeto gráfico
	 * @param x           abscissa da coordenada de referência
	 * @param y           ordenada da coordenada de referência
	 * @param orientation sentido para o qual o desenho aponta, com relação às
	 *                    coordenadas de referências
	 */
	public static void drawDisj(Graphics2D g, int x, int y, Orientation orientation) {
		drawDisj(g, x, y, orientation, false);
	}

	/**
	 * Função que desenha o símbolo de um disjuntor (uma caixa). De uma extremidade
	 * a outra do componente, há 40 pixels de largura.
	 * 
	 * @param g           objeto gráfico
	 * @param x           abscissa da coordenada de referência
	 * @param y           ordenada da coordenada de referência
	 * @param orientation sentido para o qual o desenho aponta, com relação às
	 *                    coordenadas de referências
	 * @param rel         <code>true</code> para disjuntor na função de religador de
	 *                    rede
	 */
	public static void drawDisj(Graphics2D g, int x, int y, Orientation orientation, boolean rel) {
		drawDisj(g, x, y, orientation, rel, null);
	}

	/**
	 * Função que desenha o símbolo de um disjuntor (uma caixa). De uma extremidade
	 * a outra do componente, há 40 pixels de largura.
	 * 
	 * @param g           objeto gráfico
	 * @param x           abscissa da coordenada de referência
	 * @param y           ordenada da coordenada de referência
	 * @param orientation sentido para o qual o desenho aponta, com relação às
	 *                    coordenadas de referências
	 * @param rel         <code>true</code> para disjuntor na função de religador de
	 *                    rede
	 * @param color       cor do interior da caixa do disjuntor
	 */
	public static void drawDisj(Graphics2D g, int x, int y, Orientation orientation, boolean rel, Color color) {
		if (orientation == Orientation.HORIZONTAL) {
			g.drawLine(x, y, x + 10, y);
			if (color != null) {
				Color c = g.getColor();
				g.setColor(color);
				g.fillRect(x + 10, y - 10, 20, 20);
				g.setColor(c);
			}
			g.drawRect(x + 10, y - 10, 20, 20);
			if (rel)
				g.drawString("R", x + 12, y + 2);
			g.drawLine(x + 30, y, x + 40, y);
		} else {
			g.drawLine(x, y, x, y + 10);
			if (color != null) {
				Color c = g.getColor();
				g.setColor(color);
				g.fillRect(x - 10, y + 10, 20, 20);
				g.setColor(c);
			}
			g.drawRect(x - 10, y + 10, 20, 20);
			if (rel)
				g.drawString("R", x - 8, y + 22);
			g.drawLine(x, y + 30, x, y + 40);
		}
	}

	public static void drawDisj(Graphics2D g, int x, int y, boolean nf, Color t, int disjSide, Direction direction) {
		final int sideBy2 = disjSide / 2;
		switch (direction) {
		case UP:
			g.setColor(nf ? Color.RED : Color.GREEN);
			g.fillRect(x - sideBy2, y - sideBy2 - disjSide, disjSide, disjSide);
			g.setColor(t);
			g.drawRect(x - sideBy2, y - sideBy2 - disjSide, disjSide, disjSide);
			break;
		case DOWN:
			g.setColor(nf ? Color.RED : Color.GREEN);
			g.fillRect(x - sideBy2, y + sideBy2, disjSide, disjSide);
			g.setColor(t);
			g.drawRect(x - sideBy2, y + sideBy2, disjSide, disjSide);
			break;
		case RIGHT:
			g.setColor(nf ? Color.RED : Color.GREEN);
			g.fillRect(x + sideBy2, y - sideBy2, disjSide, disjSide);
			g.setColor(t);
			g.drawRect(x + sideBy2, y - sideBy2, disjSide, disjSide);
			break;
		case LEFT:
			g.setColor(nf ? Color.RED : Color.GREEN);
			g.fillRect(x - sideBy2 - disjSide, y - sideBy2, disjSide, disjSide);
			g.setColor(t);
			g.drawRect(x - sideBy2 - disjSide, y - sideBy2, disjSide, disjSide);
			break;
		default:
			break;
		}
	}

	public static void drawJumper(Graphics2D g, int x, int y, Orientation orientation) {
		if (orientation == Orientation.HORIZONTAL) {
			drawConnectors(g, x, y, Direction.RIGHT, 8, 32, 40);
			g.drawOval(x + 8, y - 2, 4, 4);
			g.drawArc(x + 10, y - 2, 20, 20, 45, 90);
			g.drawOval(x + 28, y - 2, 4, 4);
		} else {
			drawConnectors(g, x, y, Direction.DOWN, 8, 32, 40);
			g.drawOval(x - 2, y + 8, 4, 4);
			g.drawArc(x - 2, y + 10, 20, 20, 135, 90);
			g.drawOval(x - 2, y + 28, 4, 4);
		}
	}

	public static void drawSeccionamento(Graphics2D g, int x, int y, Orientation orientation) {
		if (orientation == Orientation.HORIZONTAL) {
			drawConnectors(g, x, y, Direction.RIGHT, 14, 26, 40);
			g.drawLine(x + 12, y - 5, x + 16, y + 5);
			g.drawLine(x + 24, y - 5, x + 28, y + 5);
		} else {
			drawConnectors(g, x, y, Direction.DOWN, 14, 26, 40);
			g.drawLine(x - 5, y + 12, x + 5, y + 16);
			g.drawLine(x - 5, y + 24, x + 5, y + 28);
		}
	}

	/**
	 * Função que desenha o símbolo de uma bobina em volta de um condutor
	 * (transformador de corrente). De uma extremidade a outra do componente, há 50
	 * pixels de largura.
	 * 
	 * @param g objeto gráfico
	 * @param x abscissa da coordenada de referência
	 * @param y ordenada da coordenada de referência
	 */
	public static void drawTC(Graphics2D g, int x, int y) {
		drawTC(g, x, y, Direction.RIGHT);
	}

	/**
	 * Função que desenha o símbolo de uma bobina em volta de um condutor
	 * (transformador de corrente). De uma extremidade a outra do componente, há 50
	 * pixels de largura.
	 * 
	 * @param g         objeto gráfico
	 * @param x         abscissa da coordenada de referência
	 * @param y         ordenada da coordenada de referência
	 * @param direction sentido para o qual o desenho aponta, com relação às
	 *                  coordenadas de referências
	 */
	public static void drawTC(Graphics2D g, int x, int y, Direction direction) {
		drawConnectors(g, x, y, direction, 50);
		switch (direction) {
		case UP:
			g.drawLine(x - 5, y - 40, x - 25, y - 40);
			g.drawLine(x - 5, y - 10, x - 25, y - 10);

			drawTurns(g, x - 5, y - 40, 3, direction);
			break;
		case RIGHT:
			g.drawLine(x + 10, y - 5, x + 10, y - 25);
			g.drawLine(x + 40, y - 5, x + 40, y - 25);

			drawTurns(g, x + 10, y - 5, 3, direction);
			break;
		case DOWN:
			g.drawLine(x + 5, y + 10, x + 25, y + 10);
			g.drawLine(x + 5, y + 40, x + 25, y + 40);

			drawTurns(g, x - 5, y + 10, 3, direction);
			break;
		case LEFT:
			g.drawLine(x - 10, y + 5, x - 10, y + 25);
			g.drawLine(x - 40, y + 5, x - 40, y + 25);

			drawTurns(g, x - 40, y - 5, 3, direction);
			break;
		default:
			break;
		}
	}

	/**
	 * Função que desenha um transformador de potencial
	 * 
	 * @param g          objeto gráfico
	 * @param x          abscissa da coordenada de referência
	 * @param y          ordenada da coordenada de referência
	 * @param direction  sentido para o qual o desenho aponta, com relação às
	 *                   coordenadas de referências
	 * @param capacitivo
	 */
	public static void drawTP(Graphics2D g, int x, int y, Direction direction, boolean capacitivo) {
		if (capacitivo) {
			drawConnectors(g, x, y, direction, 5, 11, 39, 45, 50);
			Direction d = direction.next();
			switch (direction) {
			case UP:
				drawTransformer2(g, x, y - 25, d, 2, 10, false, false);
				g.drawLine(x - 8, y - 5, x + 8, y - 5);
				g.drawLine(x - 8, y - 11, x + 8, y - 11);
				g.drawLine(x - 8, y - 39, x + 8, y - 39);
				g.drawLine(x - 8, y - 45, x + 8, y - 45);
				break;
			case RIGHT:
				drawTransformer2(g, x + 25, y, d, 2, 10, false, false);
				g.drawLine(x + 5, y - 8, x + 5, y + 8);
				g.drawLine(x + 11, y - 8, x + 11, y + 8);
				g.drawLine(x + 39, y - 8, x + 39, y + 8);
				g.drawLine(x + 45, y - 8, x + 45, y + 8);
				break;
			case DOWN:
				drawTransformer2(g, x, y + 25, d, 2, 10, false, false);
				g.drawLine(x - 8, y + 5, x + 8, y + 5);
				g.drawLine(x - 8, y + 11, x + 8, y + 11);
				g.drawLine(x - 8, y + 39, x + 8, y + 39);
				g.drawLine(x - 8, y + 45, x + 8, y + 45);
				break;
			case LEFT:
				drawTransformer2(g, x - 25, y, d, 2, 10, false, false);
				g.drawLine(x - 5, y - 8, x - 5, y + 8);
				g.drawLine(x - 11, y - 8, x - 11, y + 8);
				g.drawLine(x - 39, y - 8, x - 39, y + 8);
				g.drawLine(x - 45, y - 8, x - 45, y + 8);
				break;
			default:
				break;
			}
		} else
			drawTransformer2(g, x, y, direction, 2, 10, false, false);
	}

	/**
	 * Função que desenha o símbolo de uma bobina e um capacitor. De uma extremidade
	 * a outra do componente, há 50 pixels de largura.
	 * 
	 * @param g objeto gráfico
	 * @param x abscissa da coordenada de referência
	 * @param y ordenada da coordenada de referência
	 */
	public static void drawCoil(Graphics2D g, int x, int y) {
		g.drawLine(x, y, x + 50, y);

		// --------- indutor ---------

		drawTurns(g, x + 10, y - 5, 3, Direction.LEFT);

		// --------- capacitor ---------

		// conectores
		g.drawLine(x + 10, y + 5, x + 10, y + 30);
		g.drawLine(x + 40, y + 5, x + 40, y + 30);

		g.drawLine(x + 10, y + 30, x + 20, y + 30);
		g.drawLine(x + 30, y + 30, x + 40, y + 30);

		// placas
		g.drawLine(x + 20, y + 15, x + 20, y + 45);
		g.drawLine(x + 30, y + 15, x + 30, y + 45);
	}

	public static void drawPR(Graphics2D g, int x1, int y1, Direction direction) {
		drawConnectors(g, x1, y1, direction, 10, 40, 50);
		switch (direction) {
		case UP:
			g.drawOval(x1 - 4, y1 - 18, 8, 8);
			g.drawOval(x1 - 4, y1 - 40, 8, 8);
			break;
		case LEFT:
			g.drawOval(x1 - 40, y1 - 4, 8, 8);
			g.drawOval(x1 - 18, y1 - 4, 8, 8);
			break;
		case DOWN:
			g.drawOval(x1 - 4, y1 + 10, 8, 8);
			g.drawOval(x1 - 4, y1 + 32, 8, 8);
			break;
		case RIGHT:
			g.drawOval(x1 + 32, y1 - 4, 8, 8);
			g.drawOval(x1 + 10, y1 - 4, 8, 8);
			break;
		default:
			break;
		}
	}

	// ------------------------------- AUXILIARES -------------------------------

	/**
	 * Função que desenha uma bobina
	 * 
	 * @param g         objeto gráfico
	 * @param x         abscissa da coordenada de referência
	 * @param y         ordenada da coordenada de referência
	 * @param turns     número de semi-círculos
	 * @param direction sentido para o qual o desenho aponta, com relação às
	 *                  coordenadas de referências
	 */
	public static void drawTurns(Graphics g, int x, int y, int turns, Direction direction) {
		drawTurns(g, x, y, turns, direction, 10);
	}

	/**
	 * Função que desenha uma bobina
	 * 
	 * @param g         objeto gráfico
	 * @param x         abscissa da coordenada de referência
	 * @param y         ordenada da coordenada de referência
	 * @param turns     número de semi-círculos
	 * @param direction sentido para o qual o desenho aponta, com relação às
	 *                  coordenadas de referências
	 * @param radius    tamanho dos semi-círculos, em pixels
	 */
	public static void drawTurns(Graphics g, int x, int y, int turns, Direction direction, int radius) {
		boolean v = direction.isVertical();
		int start, w, h;
		if (v) {
			start = 90;
			w = 2 * radius;
			h = radius;
		} else {
			start = 0;
			w = radius;
			h = 2 * radius;
		}
		boolean p = direction == Direction.LEFT || direction == Direction.DOWN;
		int ang;
		if (p)
			ang = 180;
		else {
			ang = -180;
			if (v)
				x -= radius;
			else
				y -= radius;
		}
		for (int i = 0; i < turns; i++) {
			g.drawArc(x, y, w, h, start, ang);
			if (v)
				y += radius;
			else
				x += radius;
		}
	}

	/**
	 * Função que desenha linhas horizontais ou verticais, partindo das coordenadas
	 * de referência, indo na direção indicado, e eventualmente fazendo um salto
	 * 
	 * @param g         objeto gráfico
	 * @param x         abscissa da coordenada de referência
	 * @param y         ordenada da coordenada de referência
	 * @param direction direção indicada
	 * @param pos       vetor com os tamanhos das setas e os gaps
	 */
	public static int[] drawConnectors(Graphics g, int x, int y, Direction direction, int... pos) {
		if (pos.length == 0)
			return null;
		int xo, yo;
		switch (direction) {
		case UP:
			xo = x;
			yo = y - pos[0];
			g.drawLine(x, y, xo, yo);
			for (int i = 1; i < pos.length; i += 2) {
				yo = y - pos[i + 1];
				g.drawLine(x, y - pos[i], xo, yo);
			}
			break;
		case RIGHT:
			xo = x + pos[0];
			yo = y;
			g.drawLine(x, y, xo, yo);
			for (int i = 1; i < pos.length; i += 2) {
				xo = x + pos[i + 1];
				g.drawLine(x + pos[i], y, xo, yo);
			}
			break;
		case DOWN:
			xo = x;
			yo = y + pos[0];
			g.drawLine(x, y, xo, yo);
			for (int i = 1; i < pos.length; i += 2) {
				yo = y + pos[i + 1];
				g.drawLine(x, y + pos[i], xo, yo);
			}
			break;
		case LEFT:
			xo = x - pos[0];
			yo = y;
			g.drawLine(x, y, xo, yo);
			for (int i = 1; i < pos.length; i += 2) {
				xo = x - pos[i + 1];
				g.drawLine(x - pos[i], y, xo, yo);
			}
			break;
		default:
			xo = 0;
			yo = 0;
			break;
		}
		return new int[] { xo, yo };
	}
}

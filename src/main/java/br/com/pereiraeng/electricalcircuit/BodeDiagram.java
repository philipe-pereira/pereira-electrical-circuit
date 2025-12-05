package br.com.pereiraeng.electricalcircuit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import br.com.pereiraeng.io.IOutils;
import br.com.pereiraeng.math.Complex;
import br.com.pereiraeng.math.expression.continuous.Parametro;
import br.com.pereiraeng.math.swing.chart.Chart;
import br.com.pereiraeng.math.swing.chart.ChartPoint;
import br.com.pereiraeng.math.swing.chart.Cloud;
import br.com.pereiraeng.math.swing.chart.Curve;
import br.com.pereiraeng.math.swing.chart.MultiAxis;
import br.com.pereiraeng.math.swing.chart.grid.NumGrid;
import br.com.pereiraeng.math.swing.input.ParametroInput;
import br.com.pereiraeng.swing.Grade;
import br.com.pereiraeng.swing.SwingUtils;
import br.com.pereiraeng.swing.input.ButtonInput;
import br.com.pereiraeng.swing.input.MapInput;
import br.com.pereiraeng.swing.table.EditableTable;
import br.com.pereiraeng.core.ExtendedMath;
import br.com.pereiraeng.electricalcircuit.components.ElecElem;
import br.com.pereiraeng.electricalcircuit.components.No;
import br.com.pereiraeng.electricalcircuit.components.RLCcomp;
import br.com.pereiraeng.electricalcircuit.components.Zcomp;

public class BodeDiagram extends JPanel implements ActionListener, TableModelListener, ChangeListener {
	private static final long serialVersionUID = 1L;

	public static final Dimension DIM = new Dimension(800, 600);

	/**
	 * Lista de objetos que representam o circuito ({@link No} e {@link Comp}).
	 */
	private Collection<ElecElem> network;

	/**
	 * Rede RLC equivalente (o objeto {@link RLCZ} é uma coleção de outros objetos
	 * {@link RLCZ}, sendo que este reúne {@link RLCcomp aqueles} que estão dentro
	 * da lista {@link BodeDiagram#network}.
	 */
	private RLCZ rlcEquiv;

	private static final double RANGE = 100;

	private static final int ROW_HEADER_WIDTH = 60;

	// ------------------------ parte gráfica ------------------------

	private JSpinner infF, supF;

	private SpinnerListModel nodes1, nodes2;

	private Chart<String> chart;

	private EditableTable paramsTable;

	private MapInput<Double, Complex> trf;
	private JTextField err;

	public BodeDiagram() {
		super(new BorderLayout());

		chart = new Chart<>(400, 300, new NumGrid(), true, false);
		chart.setTrack(Color.BLACK);
		add(chart, BorderLayout.CENTER);

		Grade grade = new Grade();
		int i = 0;

		JSpinner term = new JSpinner(nodes1 = new SpinnerListModel());
		grade.add(term, 0, i++, 3, 1);

		term = new JSpinner(nodes2 = new SpinnerListModel());
		grade.add(term, 0, i++, 3, 1);

		JButton b = new JButton("Calcular");
		b.addActionListener(this);
		grade.add(b, 0, i++, 3, 1);

		// parâmetros (que podem ser modificados)
		paramsTable = new EditableTable(0, 1);
		paramsTable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		paramsTable.setTableHeader(null);
		// altura: 20 pixels do ParameterInput + 1 pixel do separador da tabela
		paramsTable.setRowHeight(ParametroInput.TAB_HEIGHT + 1);
		paramsTable.addTableModelListener(this);
		// largura: cabeçalho das linhas + ParameterInput + 17 pixels scroll + 3 pixels
		paramsTable.setPreferredSize(new Dimension(ROW_HEADER_WIDTH + ParametroInput.TAB_WIDTH + 17 + 3, 200));
		grade.add(paramsTable, 0, i++, 3, 1);

		// faixa de frequências
		grade.add(new JLabel("Faixa de frequências"), 0, i++, 3, 1);

		grade.add(new JLabel("Inf. (log(f)):"), 0, i, 1, 1);
		infF = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		infF.setPreferredSize(new Dimension(50, 20));
		infF.addChangeListener(this);
		grade.add(infF, 1, i++, 1, 1);

		grade.add(new JLabel("Sup. (log(f)):"), 0, i, 1, 1);
		supF = new JSpinner(new SpinnerNumberModel(5, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		supF.setPreferredSize(new Dimension(50, 20));
		supF.addChangeListener(this);
		grade.add(supF, 1, i++, 1, 1);

		// comparar com uma respostas em frequência
		grade.add(new JLabel("Comparar com a série de dados"), 0, i++, 3, 1);
		trf = new MapInput<>(true, Double.class, Complex.class);
		trf.addActionListener(this);
		ButtonInput<Map<Double, Complex>> bi = new ButtonInput<>(trf, "Entrar com as frequências");
		grade.add(bi, 0, i++, 3, 1);
		grade.add(new JLabel("Erro rel.:"), 0, i, 1, 1);
		this.err = new JTextField();
		this.err.setPreferredSize(new Dimension(50, 20));
		grade.add(err, 1, i++, 1, 1);

		add(grade, BorderLayout.EAST);
	}

	public void setCircuit(Collection<ElecElem> elements) {
		this.network = elements;

		// separar os nós
		LinkedList<No> nodes = new LinkedList<>();
		for (ElecElem ec : network)
			if (ec instanceof No)
				nodes.add((No) ec);

		// repassá-los aos spinners
		nodes1.setList(nodes);
		nodes2.setList(nodes);
		nodes2.setValue(nodes2.getNextValue());

		// separar os elementos lineares (eles serão parametrizados)
		LinkedList<RLCcomp> params = new LinkedList<>();
		for (ElecElem ec : network)
			if (ec instanceof RLCcomp)
				params.add((RLCcomp) ec);

		// carregar na tabela
		innerChange = true;
		paramsTable.setRowCount(params.size());
		String[] columnHeader = new String[params.size()];
		int j = 0;
		for (RLCcomp rlc : params) {
			Parametro p = new Parametro(rlc.getType().getUnit());
			p.setLog(true);
			columnHeader[j] = rlc.toString();

			double v = rlc.getValue();
			double vmin = v / RANGE;
			double vmax = v * RANGE;

			p.setValor(v);
			p.setMin(vmin);
			p.setMax(vmax);

			paramsTable.setValueAt(p, j++, 0);
		}
		paramsTable.setRowHeaderWidth(ROW_HEADER_WIDTH);
		paramsTable.setRowIdentifiers(columnHeader);
		innerChange = false;
	}

	private void refresh() {
		chart.clear();
		if (rlcEquiv != null) {
			// curva de resposta em frequência do circuito

			// TODO em função de infF e supF values, dá para saber o tamanho da lista...
			ArrayList<ChartPoint> mods = new ArrayList<>(), args = new ArrayList<>();

			// limite inferior, superior e passo
			double inf = Math.pow(10, (int) this.infF.getValue());
			double sup = Math.pow(10, (int) this.supF.getValue());
			double step = Math.pow(10, 0.125 / 2.);
			for (double w = inf; w < sup; w *= step) {
				Complex z = this.rlcEquiv.getZ(w);
				double logW = Math.log10(w);
				mods.add(new ChartPoint(logW, Math.log10(z.getMod())));
				args.add(new ChartPoint(logW, Math.toDegrees(z.getArg())));
			}

			chart.put("MOD", new Curve(mods, Color.BLUE, "MOD"));
			chart.put("ARG", new Curve(args, Color.RED, "ARG"));

			// passa os argumentos para o outro eixo
			MultiAxis<String> multiAxis = new MultiAxis<>(chart);
			multiAxis.addScale(Arrays.asList("ARG"));

			// se for para comparar com uma outra resposta em frequência
			Map<Double, Complex> rf = trf.get();
			if (rf.size() > 0) {
				double error = 0.;

				mods = new ArrayList<>();
				args = new ArrayList<>();

				for (Entry<Double, Complex> e : rf.entrySet()) {
					double w = ExtendedMath.TWO_PI * e.getKey();
					Complex z = e.getValue();

					double logW = Math.log10(w);
					mods.add(new ChartPoint(logW, Math.log10(z.getMod())));
					args.add(new ChartPoint(logW, Math.toDegrees(z.getArg())));

					// calcula o erro com relação ao modelo
					Complex zM = this.rlcEquiv.getZ(w);

					double zMr = zM.getRe(), zMi = zM.getIm(), zr = z.getRe(), zi = z.getIm();

					error += Math.sqrt(Math.pow((zMr - zr) / zr, 2) + Math.pow((zMi - zi) / zi, 2));
				}

				chart.put("MOD1", new Cloud(mods, Color.BLUE, "MOD1"));
				chart.put("ARG1", new Cloud(args, Color.RED, "ARG1"));

				err.setText(String.format("%.4g", error));

				// passa os argumentos para o outro eixo
				multiAxis.putScale("ARG1", 0);
			}

			chart.repaint();
		}
	}

	// ---------------------- LISTENER ----------------------

	private transient boolean innerChange;

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (trf.equals(source)) { // colar valor das impedâncias
			String str = SwingUtils.getClipBoardContent(SwingUtils.getWindow(this));

			TreeMap<Double, Complex> rf = new TreeMap<Double, Complex>();

			String[] rows = str.split("\n");
			for (int i = 0; i < rows.length; i++) {
				String[] cells = rows[i].split("\t");
				double fr = Double.NaN, re = Double.NaN, im = Double.NaN;

				for (int j = 0; j < cells.length; j++) {
					double d = Double.NaN;
					try {
						d = Double.parseDouble(cells[j].replaceAll(",", "."));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					switch (j) {
					case 0:
						fr = d;
						break;
					case 1:
						re = d;
						break;
					case 2:
						im = d;
						break;
					}
				}
				rf.put(fr, new Complex(re, im));
			}

			this.trf.set(rf);
		} else {
			// ------------ atualizar nós escolhidos ------------

			No n1 = (No) nodes1.getValue();
			No n2 = (No) nodes2.getValue();

			if (!n1.equals(n2))
				this.rlcEquiv = Zcomp.getRLC(network, n1, n2);
			else
				this.rlcEquiv = null;

			refresh();
		}
	}

	@Override
	public void tableChanged(TableModelEvent event) {
		if (!innerChange) {
			// coordenadas da tabela onde houve a modificação
			int row = event.getFirstRow(), col = event.getColumn();
			if (row >= 0 && col >= 0) {
				double newValue = ((Parametro) paramsTable.getValueAt(row, event.getColumn())).getValor();
				// nome do elemento
				String name = paramsTable.getElementRH(row);

				for (ElecElem el : network) {
					if (name.equals(el.toString())) {
						// ao se achar o elemento de mesmo nome...
						RLCcomp rlcComp = (RLCcomp) el;
						rlcComp.setValue(newValue);
						refresh();
						break;
					}
				}
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		refresh();
	}
}
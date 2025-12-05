package br.com.pereiraeng.electrical.circuit.circuit;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.tree.DefaultMutableTreeNode;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.pereiraeng.math.Complex;
import br.com.pereiraeng.core.StringUtils;
import br.com.pereiraeng.electrical.circuit.circuit.components.*;
import br.com.pereiraeng.electrical.circuit.circuit.components.No.NodeType;
import br.com.pereiraeng.electrical.circuit.circuit.components.Switch.TensionBlock;
import br.com.pereiraeng.xml.XMLadapter;

public class XMLcircuitReader extends XMLadapter {

	/**
	 * circuito
	 */
	private Map<String, ElecElem> circuit;

	/**
	 * código SVG para o foreground
	 */
	private String fore;

	/**
	 * código SVG para o background
	 */
	private String back;

	/**
	 * parâmetros
	 */
	private Map<String, Double> params;

	/**
	 * bibliotecas (para circuitos apenas)
	 */
	private Map<String, ElecElemSeed> library;

	/**
	 * Construtor do leitor de XML de circuitos
	 * 
	 * @param circ <code>true</code> para circuitos, <code>false</code> para
	 *             parâmetros
	 */
	public XMLcircuitReader() {
		super(true);
	}

	public void setLibraryRoot(DefaultMutableTreeNode libraryRoot) {
		if (library == null)
			library = new HashMap<>();
		for (int i = 0; i < libraryRoot.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) libraryRoot.getChildAt(i);
			if (child.isLeaf()) {
				ElecElemSeed ees = (ElecElemSeed) child.getUserObject();
				library.put(ees.getType(), ees);
			} else
				setLibraryRoot(child);
		}
	}

	public Map<String, ElecElem> getCircuit() {
		return circuit;
	}

	public Map<String, Double> getParams() {
		return params;
	}

	public String getFore() {
		return fore;
	}

	public String getBack() {
		return back;
	}

	// =============================== LEITURA ===============================

	private transient String qName;

	// circuito
	private transient ElecElem elem;

	// parâmetros
	private transient String paramLabel;

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void startElement(String qName, Attributes atts) {
		this.qName = qName;
		switch (this.qName) {
		case "circuit":
			circuit = new HashMap<>();
			break;
		case "params":
			params = new HashMap<>();
			break;
		case "node": // ------------- Nó -------------
			String label = atts.getValue("label");

			No n = new No(label);

			String nodeType = atts.getValue("type");
			if (nodeType != null) {
				NodeType type = NodeType.valueOf(nodeType);
				if (type != NodeType.SIMPLE)
					n.setType(type);
			}

			elem = n;
			circuit.put(label, elem);
			break;
		case "source": // ------------- Fonte -------------
			label = atts.getValue("label");

			String type = atts.getValue("type");
			GeneratorType1 wf = null;
			GeneratorType2 output = null;
			if (type != null) {
				int t = Integer.parseInt(type);
				switch (t) {
				case 0:
					wf = GeneratorType1.GENERAL;
					output = GeneratorType2.V;
					break;
				case 1:
					wf = GeneratorType1.DC;
					output = GeneratorType2.V;
					break;
				case 2:
					wf = GeneratorType1.AC;
					output = GeneratorType2.V;
					break;
				case 3:
					wf = GeneratorType1.GENERAL;
					output = GeneratorType2.I;
					break;
				}
			} else {
				wf = GeneratorType1.values()[Integer.parseInt(atts.getValue("wf"))];
				output = GeneratorType2.values()[Integer.parseInt(atts.getValue("output"))];
			}

			Object input = null; {
			String s = atts.getValue("value");
			switch (wf) {
			case GENERAL:
				input = s;
				break;
			case DC:
				input = Double.parseDouble(atts.getValue("value"));
				break;
			case AC:
				String[] c = s.replace(',', '.').split(";");
				input = new Complex(Double.parseDouble(c[0]), Double.parseDouble(c[1]));
				break;
			case FOURIER:
//				input = FourierCircInput.toFourier(s);
				break;
			case LAPLACE:
//				input = LaplaceCircInput.toLaplace(s);
				break;
			}
		}

			Source src = new Source(label, wf, output);
			src.setInput(input);

			elem = src;
			circuit.put(label, elem);
			break;
		case "switch": // ------------- Chave -------------
			label = atts.getValue("label");
			boolean carryOnlyForward = Integer.parseInt(atts.getValue("type1")) == 1;
			TensionBlock b = TensionBlock.values()[Integer.parseInt(atts.getValue("type2"))];

			Switch sw = new Switch(label, carryOnlyForward, b);

			elem = sw;
			circuit.put(label, elem);
			break;
		case "dt": // ------------- Chave D/T -------------
			label = atts.getValue("label");

			DT dt = new DT(label);

			elem = dt;
			circuit.put(label, elem);
			break;
		case "sel": // ------------- seletor -------------
			label = atts.getValue("label");

			SelectionSwitch ss = new SelectionSwitch(label, Integer.parseInt(atts.getValue("terms")));

			elem = ss;
			circuit.put(label, elem);
			break;
		case "rlc": // ------------- Lineares -------------
			label = atts.getValue("label");
			double v = Double.parseDouble(atts.getValue("value").replace(',', '.'));

			RLCcomp l = new RLCcomp(label, RLCZ.LinearType.valueOf(atts.getValue("type")));
			l.setValue(v);

			elem = l;
			circuit.put(label, elem);
			break;
		case "gnd": // ------------- Terra -------------
			label = atts.getValue("label");

			Ground g = new Ground(label);

			elem = g;
			circuit.put(label, elem);
			break;
		case "quad": // ------------- Quadripolo -------------
			label = atts.getValue("label");

			Quadripole q = new Quadripole(label);
			q.setField(1, atts.getValue("symbol"));

			elem = q;
			circuit.put(label, elem);
			break;
		case "zfc": // ------------- Zfcomp -------------
			label = atts.getValue("label");

			ZfComp z = new ZfComp(label);

			elem = z;
			circuit.put(label, elem);
			break;
		case "cca": // ------------- curto-circuito/aberto -------------
			label = atts.getValue("label");

			CurtoAberto cca = new CurtoAberto(label, Boolean.parseBoolean(atts.getValue("cc")));

			elem = cca;
			circuit.put(label, elem);
			break;
		case "mag": // ------------- Acoplamento magnético -------------
			label = atts.getValue("label");

			MagCouple mc = new MagCouple(label, Integer.parseInt(atts.getValue("windings")));

			elem = mc;
			circuit.put(label, elem);
			break;
		case "meter": // ------------- medidor bipolar -------------
			label = atts.getValue("label");

			Meter me = new Meter(label, Meter.GrandezaMed.valueOf(atts.getValue("type")));

			elem = me;
			circuit.put(label, elem);
			break;
		case "wmt": // ------------- medidor wattímetro -------------
			label = atts.getValue("label");

			Wattmeter wmt = new Wattmeter(label);

			elem = wmt;
			circuit.put(label, elem);
			break;
		case "trfIde": // ------------- medidor bipolar -------------
			label = atts.getValue("label");

			TransfIdeal ti = new TransfIdeal(label, Integer.parseInt(atts.getValue("ne1")),
					Integer.parseInt(atts.getValue("ne2")));

			elem = ti;
			circuit.put(label, elem);
			break;
		case "ss": // ------------- subsistema -------------
			label = atts.getValue("label");

			String[] dim = atts.getValue("dim").split(",");
			int nc = Integer.parseInt(dim[0]);
			List<Point> points = new ArrayList<>(nc - 1);
			for (int i = 0; i < nc - 1; i++)
				points.add(new Point(Integer.parseInt(dim[5 + 2 * i]), Integer.parseInt(dim[6 + 2 * i])));

			CompCustom cc = new CompCustom(label, nc, Integer.parseInt(dim[1]), Integer.parseInt(dim[2]),
					Integer.parseInt(dim[3]), Integer.parseInt(dim[4]), points);

			elem = cc;
			circuit.put(label, elem);
			break;
		case "z": // impedância (só para Zfcomp)
			String[] ri = atts.getValue("value").replace(',', '.').split(";");
			((ZfComp) elem).setZ(Double.parseDouble(atts.getValue("f").replace(',', '.')),
					new Complex(Double.parseDouble(ri[0]), Double.parseDouble(ri[1])));
			break;
		case "loc": // localização e direção
			String dir = atts.getValue("dir");
			if (elem instanceof Comp && dir != null) // se uma direção está
														// indicada
				((Comp) elem).setOrientation(Integer.parseInt(dir));
			break;
		case "param": // valor de parâmetros
			paramLabel = atts.getValue("label");
			break;
		case "foreground":
		case "background":
			super.notParse(this.qName);
			break;
		case "vertices":
		case "edges":
		case "term":
			break;
		default:
			if (library != null) {
				ElecElemSeed ees = library.get(this.qName);
				if (ees != null) { // ------------- elemento customizado
									// -------------
					label = atts.getValue("label");

					cc = new CompCustom(label, ees);

					int i = 1;
					for (String atr : ees.attribsNames()) {
						String vs = atts.getValue(atr);
						Object def = ees.getAttribsValue(atr);
						Object vo = null;
						if (def instanceof Boolean)
							vo = Boolean.parseBoolean(vs);
						else if (def instanceof Integer)
							vo = Integer.parseInt(vs);
						else
							vo = vs;
						cc.setField(i++, vo);
					}

					elem = cc;
					circuit.put(label, elem);
				} else {
					System.err.println("Unknown element:\t" + this.qName);
					elem = null;
				}
			} else {
				System.err.println("A biblioteca não foi carregada:\t" + this.qName);
				elem = null;
			}
			break;
		}
	}

	@Override
	public void characters(String s) {
		if (qName != null) {
			switch (qName) {
			case "term": // ---------- nós dos terminais ----------
				String[] nodes = s.split(";");

				// todo elemento está conectado a pelo menos um nó...
				String[] nCp = nodes[0].split(",");
				Comp c = (Comp) elem;

				c.setNo((No) circuit.get(nCp[0]));
				c.setConnectionPoint(Integer.parseInt(nCp[1]));

				if (nodes.length > 1) {
					// se o elemento estiver conectado a mais de um nó...

					if (elem instanceof CompCustom) {
						// se for um elemento customizado (i.e., com número
						// variável de terminais)
						CompCustom cc = (CompCustom) elem;

						for (int i = 1; i < nodes.length; i++) {
							nCp = nodes[i].split(",");
							cc.setNN((No) circuit.get(nCp[0]), i);
							cc.setConnectionPointN(Integer.parseInt(nCp[1]), i);
						}
					} else {
						// se for um dos elementos padronizados
						nCp = nodes[1].split(",");
						Comp2 c2 = (Comp2) c;

						c2.setN2((No) circuit.get(nCp[0]));
						c2.setConnectionPoint2(Integer.parseInt(nCp[1]));

						if (nodes.length > 2) {
							// 3 ou mais terminais
							nCp = nodes[2].split(",");
							Comp3 c3 = (Comp3) c2;

							c3.setN3((No) circuit.get(nCp[0]));
							c3.setConnectionPoint3(Integer.parseInt(nCp[1]));

							if (nodes.length > 3) {
								// 4 ou mais terminais
								nCp = nodes[3].split(",");
								Comp4 c4 = (Comp4) c3;

								c4.setN4((No) circuit.get(nCp[0]));
								c4.setConnectionPoint4(Integer.parseInt(nCp[1]));

								if (nodes.length > 4) {
									// mais de 4 terminais
									CompN cn = (CompN) c4;

									for (int i = 4; i < nodes.length; i++) {
										nCp = nodes[i].split(",");
										cn.setNN((No) circuit.get(nCp[0]), i);
										cn.setConnectionPointN(Integer.parseInt(nCp[1]), i);
									}
								}
							}
						}
					}
				}
				break;
			case "loc": // ---------- coordenadas ----------
				String[] xys = s.split(";");
				if (elem instanceof No) { // coordenadas do nó
					No n = ((No) elem);
					n.setNumPoints(xys.length);
					for (int j = 0; j < xys.length; j++) {
						n.setIndex(j);
						int[] xy = StringUtils.parseInts(xys[j].split(","));
						n.setPosition(xy[0], xy[1]);
					}
				} else { // coordenadas do elemento
					int[] xy = StringUtils.parseInts(xys[0].split(","));
					elem.setPosition(xy[0], xy[1]);
				}
				break;
			case "param":
				params.put(paramLabel, Double.parseDouble(s.replace(',', '.')));
				break;
			}
		}
	}

	@Override
	public void endElement(String qName) {
		switch (qName) {
		case "foreground":
			this.fore = super.getStock();
			this.fore = "".equals(this.fore) ? null : this.fore;
			break;
		case "background":
			this.back = super.getStock();
			this.back = "".equals(this.back) ? null : this.back;
			break;
		}
		this.qName = null;
	}

	@Override
	public void endDocument() throws SAXException {
	}

	// =============================== ESCRITA ===============================

	public static String writeXML(Collection<ElecElem> elems, String svgFore, String svgBack, Properties props) {
		// cabeçalho
		StringBuilder content = new StringBuilder(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<circuit>\n\t<vertices>\n");

		// nós
		for (ElecElem ec : elems)
			if (ec instanceof No)
				content.append(ec.getXML());
		content.append("\t</vertices>\n\t<edges>\n");

		// arestas
		for (ElecElem ec : elems)
			if (ec instanceof Comp)
				content.append(ec.getXML());
		content.append("\t</edges>\n");

		// SVG
		if (svgFore != null) {
			content.append("<foreground>\n");
			content.append(svgFore);
			content.append("\n</foreground>\n");
		}
		if (svgBack != null) {
			content.append("<background>\n");
			content.append(svgBack);
			content.append("\n</background>\n");
		}

		// params
		if (props != null) {
			content.append("<params>\n");
			Enumeration<?> e = props.propertyNames();
			while (e.hasMoreElements()) {
				String name = (String) e.nextElement();
				content.append(String.format("<param label=\"%s\">%s</param>\n", name, props.getProperty(name)));
			}
			content.append("</params>\n");
		}

		// encerra XML
		content.append("</circuit>");

		return content.toString();
	}

	public static String writeXMLparams(Collection<ElecElem> elems) {
		// cabeçalho
		StringBuilder content = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<params>\n");

		// parametros
		for (ElecElem ec : elems) {
			if (ec instanceof RLCcomp) {
				RLCcomp l = (RLCcomp) ec;
				content.append(String.format("<param label=\"%s\">%g</param>\n", l.toString(), l.getValue()));
			}
		}

		// encerra XML
		content.append("</params>");

		return content.toString();
	}
}

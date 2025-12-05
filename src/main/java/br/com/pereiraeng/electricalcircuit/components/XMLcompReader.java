package br.com.pereiraeng.electricalcircuit.components;

import java.awt.Point;
import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.pereiraeng.core.collections.ArrayUtils;
import br.com.pereiraeng.xml.XMLadapter;

/**
 * Classe do objeto leitor de arquivos XML com informações sobre os elementos de
 * circuito customizados
 * 
 * @author Philipe PEREIRA
 *
 */
public class XMLcompReader extends XMLadapter {

	private DefaultMutableTreeNode root;

	public DefaultMutableTreeNode getRoot() {
		return root;
	}

	public static DefaultMutableTreeNode getRoot(File folder) {
		DefaultMutableTreeNode out = new DefaultMutableTreeNode();
		File[] libraries = folder.listFiles();
		XMLcompReader ee = new XMLcompReader();
		for (int i = 0; i < libraries.length; i++) {
			ee.parse(libraries[i].getAbsolutePath());
			out.add(ee.getRoot());
		}
		return out;
	}

	// =============================== LEITURA ===============================

	private transient ElecElemSeed elemSeed;

	private transient String qName;

	private transient Object[] data;

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void startElement(String qName, Attributes atts) {
		this.qName = qName;

		switch (qName) {
		case "library":
			this.root = new DefaultMutableTreeNode(atts.getValue("name"));
			break;
		case "folder":
			DefaultMutableTreeNode d = new DefaultMutableTreeNode(atts.getValue("name"));
			this.root.add(d);
			this.root = d;
			break;
		case "comp":
			String type = atts.getValue("type");
			String name = atts.getValue("name");
			this.elemSeed = new ElecElemSeed(type, name, Integer.parseInt(atts.getValue("conn")));
			this.root.add(new DefaultMutableTreeNode(elemSeed));
			break;
		case "field":
			type = atts.getValue("type");
			Object def = atts.getValue("default");
			if (type != null) {
				switch (type) {
				case "boolean":
					def = Boolean.parseBoolean((String) def);
					break;
				case "integer":
					def = Integer.parseInt((String) def);
					break;
				case "float":
					def = Float.parseFloat((String) def);
					break;
				}
			}
			elemSeed.putAttr(atts.getValue("name"), def);
			break;
		case "svg":
			elemSeed.createList();
			break;
		case "conn":
			elemSeed.createConn();
			break;
		case "sel":
			elemSeed.setSelection(Integer.parseInt(atts.getValue("x")), Integer.parseInt(atts.getValue("y")),
					Integer.parseInt(atts.getValue("w")), Integer.parseInt(atts.getValue("h")));
			break;
		// --------------- SVG ---------------
		case "rect":
			Object[] di = new Object[8];
			di[0] = "rect";
			di[1] = Integer.parseInt(atts.getValue("x"));
			di[2] = Integer.parseInt(atts.getValue("y"));
			di[3] = Integer.parseInt(atts.getValue("width"));
			di[4] = Integer.parseInt(atts.getValue("height"));
			di[5] = atts.getValue("stroke");
			di[6] = atts.getValue("fill");
			di[7] = atts.getValue("stroke-dasharray");
			elemSeed.addDraw(di);
			break;
		case "line":
			di = new Object[8];
			di[0] = "line";
			di[1] = Integer.parseInt(atts.getValue("x1"));
			di[2] = Integer.parseInt(atts.getValue("y1"));
			di[3] = Integer.parseInt(atts.getValue("x2"));
			di[4] = Integer.parseInt(atts.getValue("y2"));
			di[5] = atts.getValue("stroke");
			di[6] = atts.getValue("stroke-dasharray");
			elemSeed.addDraw(di);
			break;
		case "text":
			data = new Object[] { "text", Integer.parseInt(atts.getValue("x")), Integer.parseInt(atts.getValue("y")),
					atts.getValue("fill"), Float.parseFloat(atts.getValue("font-size")) };
			break;
		case "path":
			elemSeed.addDraw(new Object[] { "path", atts.getValue("visibility"), atts.getValue("d"),
					atts.getValue("stroke"), atts.getValue("fill") });
			break;
		case "circle":
			elemSeed.addDraw(new Object[] { "circle", Integer.parseInt(atts.getValue("cx")),
					Integer.parseInt(atts.getValue("cy")), Integer.parseInt(atts.getValue("r")),
					atts.getValue("stroke"), atts.getValue("fill") });
			break;
		case "ellipse":
			elemSeed.addDraw(new Object[] { "ellipse", Integer.parseInt(atts.getValue("cx")),
					Integer.parseInt(atts.getValue("cy")), Integer.parseInt(atts.getValue("rx")),
					Integer.parseInt(atts.getValue("ry")), atts.getValue("stroke"), atts.getValue("fill") });
			break;
		case "polygon":
			elemSeed.addDraw(new Object[] { "polygon", atts.getValue("points").split("\\s+"), atts.getValue("stroke"),
					atts.getValue("fill") });
			break;
		case "polyline":
			elemSeed.addDraw(
					new Object[] { "polyline", atts.getValue("points").split("\\s+"), atts.getValue("stroke") });
			break;
		default:
			System.err.println("Comando SVG desconhecido: " + qName);
			break;
		}
	}

	@Override
	public void characters(String s) {
		if (qName != null) {
			switch (qName) {
			case "conn":
				String[] points = s.split(";");
				for (int i = 0; i < points.length; i++) {
					String[] xy = points[i].split(",");
					elemSeed.addConn(new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
				}
				break;
			case "text":
				data = ArrayUtils.concatArray(data, new Object[] { s });
				break;
			}
		}
	}

	@Override
	public void endElement(String qName) {
		switch (qName) {
		case "text":
			elemSeed.addDraw(data);
			break;
		case "folder":
			this.root = (DefaultMutableTreeNode) this.root.getParent();
			break;
		}
		this.qName = null;
	}

	@Override
	public void endDocument() throws SAXException {
	}
}

/**
 * Copyright (c) 2006-2012, JGraph Ltd */
package org.processmining.cachealignment.algorithms.ocel.occl;

import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.processmining.cachealignment.algorithms.analysis.mxAnalysisGraph;
import org.processmining.cachealignment.algorithms.analysis.mxGraphGenerator;
import org.processmining.cachealignment.algorithms.analysis.mxGraphProperties;
import org.processmining.cachealignment.algorithms.editor.ActEditorPalette;
import org.processmining.cachealignment.algorithms.editor.BasicGraphEditor;
import org.processmining.cachealignment.algorithms.editor.objEditorPalette;
import org.processmining.cachealignment.algorithms.io.mxCodec;
import org.processmining.cachealignment.algorithms.layout.mxFastOrganicLayout;
import org.processmining.cachealignment.algorithms.model.mxCell;
import org.processmining.cachealignment.algorithms.model.mxGeometry;
import org.processmining.cachealignment.algorithms.model.mxICell;
import org.processmining.cachealignment.algorithms.model.mxIGraphModel;
import org.processmining.cachealignment.algorithms.swing.mxGraphComponent;
import org.processmining.cachealignment.algorithms.swing.util.mxGraphTransferable;
import org.processmining.cachealignment.algorithms.util.mxEvent;
import org.processmining.cachealignment.algorithms.util.mxEventObject;
import org.processmining.cachealignment.algorithms.util.mxEventSource;
import org.processmining.cachealignment.algorithms.util.mxPoint;
import org.processmining.cachealignment.algorithms.util.mxUtils;
import org.processmining.cachealignment.algorithms.view.mxGraph;
import org.w3c.dom.Document;

public class GraphEditor extends BasicGraphEditor
{
	/**
	 *
	 */
	private static final long serialVersionUID = -4601740824088314699L;


	protected mxAnalysisGraph aGraph = new mxAnalysisGraph();
	protected boolean arrows = false;
	protected boolean weighted = false;

	private HashSet orangeColorList = new HashSet(Arrays.asList("#FABA5F","#FEBA4F","#F28F1C"));
	/**
	 * Holds the shared number formatter.
	 *
	 * @see NumberFormat#getInstance()
	 */
	public static final NumberFormat numberFormat = NumberFormat.getInstance();

	/**
	 * Holds the URL for the icon to be used as a handle for creating new
	 * connections. This is currently unused.
	 */
	public static URL url = null;

	//GraphEditor.class.getResource("/com/mxgraph/examples/swing/images/connector.gif");

//	public GraphEditor()
//	{
//		this("mxGraph Editor", new CustomGraphComponent(new CustomGraph()));
//	}

	/**
	 *
	 */
	public GraphEditor()
	{
		super("mxGraph Editor", new CustomGraphComponent(new CustomGraph()));
		mxGraph graph = graphComponent.getGraph();

		mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
		layout.setForceConstant(150);
		layout.setMinDistanceLimit(5);


		ArrayList<String> objTypeList = new ArrayList<>();
		ArrayList<String> actList = new ArrayList<>();

		objTypeList.add("1");
		objTypeList.add("2");
		objTypeList.add("3");
		objTypeList.add("4");
		objTypeList.add("5");
		objTypeList.add("6");
		objTypeList.add("7");
		objTypeList.add("8");
		objTypeList.add("9");
		objTypeList.add("10");

		actList.add("1");
		actList.add("2");
		actList.add("3");
		actList.add("4");
		actList.add("5");
		actList.add("6");
		actList.add("7");
		actList.add("8");
		actList.add("9");
		actList.add("10");
		actList.add("11");

		// Creates the shapes palette
		objEditorPalette objPalette = insertObjPalette("Object Type", objTypeList.size());
		ActEditorPalette actPalette = insertActPalette("Activity", actList.size());

		// Sets the edge template to be used for creating new edges if an edge
		// is clicked in the shape palette
		objPalette.addListener(mxEvent.SELECT, new mxEventSource.mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				Object tmp = evt.getProperty("transferable");

				if (tmp instanceof mxGraphTransferable)
				{
					mxGraphTransferable t = (mxGraphTransferable) tmp;
					Object cell = t.getCells()[0];

					if (graph.getModel().isEdge(cell))
					{
						((org.processmining.cachealignment.algorithms.ocel.constraint.GraphEditor.CustomGraph) graph).setEdgeTemplate(cell);
					}
				}
			}

		});

		actPalette.addListener(mxEvent.SELECT, new mxEventSource.mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				Object tmp = evt.getProperty("transferable");

				if (tmp instanceof mxGraphTransferable)
				{
					mxGraphTransferable t = (mxGraphTransferable) tmp;
					Object cell = t.getCells()[0];

					if (graph.getModel().isEdge(cell))
					{
						((org.processmining.cachealignment.algorithms.ocel.constraint.GraphEditor.CustomGraph) graph).setEdgeTemplate(cell);
					}
				}
			}

		});

		int i = 0;
		String idx;
		for (String objType: objTypeList) {
			idx = ((Integer)(i % 6+1)).toString();
			System.out.println(idx);
			// Adds some template cells for dropping into the graph
			objPalette
					.addObjTemplate(
							objType,
							new ImageIcon(
									org.processmining.cachealignment.algorithms.ocel.constraint.GraphEditor.class
											.getResource("/com/mxgraph/examples/swing/images/"+"orange"+
													idx+".png")),
							"ellipse", 80, 80, objType);
			i += 1;
		}

		for (String actType: actList) {
			actPalette
					.addActTemplate(
							actType,
							new ImageIcon(
									org.processmining.cachealignment.algorithms.ocel.constraint.GraphEditor.class
											.getResource("/com/mxgraph/examples/swing/images/activity.png")),
							"rectangle",
							80, 80, actType);
		}
	}

	public GraphEditor(ArrayList<String> objTypeList, HashSet actList)
	{
		super("mxGraph Editor", new CustomGraphComponent(new CustomGraph()));
		final mxGraph graph = graphComponent.getGraph();

		// Creates the shapes palette
		objEditorPalette objPalette = insertObjPalette("Object Type", objTypeList.size());
		ActEditorPalette actPalette = insertActPalette("Activity", actList.size());

		// Sets the edge template to be used for creating new edges if an edge
		// is clicked in the shape palette
		objPalette.addListener(mxEvent.SELECT, new mxEventSource.mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				Object tmp = evt.getProperty("transferable");

				if (tmp instanceof mxGraphTransferable)
				{
					mxGraphTransferable t = (mxGraphTransferable) tmp;
					Object cell = t.getCells()[0];

					if (graph.getModel().isEdge(cell))
					{
						((org.processmining.cachealignment.algorithms.ocel.constraint.GraphEditor.CustomGraph) graph).setEdgeTemplate(cell);
					}
				}
			}

		});

		actPalette.addListener(mxEvent.SELECT, new mxEventSource.mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				Object tmp = evt.getProperty("transferable");

				if (tmp instanceof mxGraphTransferable)
				{
					mxGraphTransferable t = (mxGraphTransferable) tmp;
					Object cell = t.getCells()[0];

					if (graph.getModel().isEdge(cell))
					{
						((org.processmining.cachealignment.algorithms.ocel.constraint.GraphEditor.CustomGraph) graph).setEdgeTemplate(cell);
					}
				}
			}

		});

		for (String objType: objTypeList) {
			// Adds some template cells for dropping into the graph
			objPalette
					.addObjTemplate(
							objType,
							new ImageIcon(
									org.processmining.cachealignment.algorithms.ocel.constraint.GraphEditor.class
											.getResource("/com/mxgraph/examples/swing/images/objectType.png")),
							"rectangle", 100, 60, objType);

		}

		for (Object actType: actList) {
			actPalette
					.addActTemplate(
							actType.toString(),
							new ImageIcon(
									org.processmining.cachealignment.algorithms.ocel.constraint.GraphEditor.class
											.getResource("/com/mxgraph/examples/swing/images/activity.png")),
							"rectangle",
							80, 80, actType);
		}
	}

	public void configAnalysisGraph(mxGraph graph, mxGraphGenerator generator, Map<String, Object> props)
	{
		this.aGraph.setGraph(graph);

		if (generator == null)
		{
			this.aGraph.setGenerator(new mxGraphGenerator(null, null));
		}
		else
		{
			this.aGraph.setGenerator(generator);
		}

		if(props == null)
		{
			Map<String, Object> properties = new HashMap<String, Object>();
			mxGraphProperties.setDirected(properties, false);
			this.aGraph.setProperties(properties);
		}
		else
		{
			this.aGraph.setProperties(props);
		}
	}

	/**
	 *
	 */
	public static class CustomGraphComponent extends mxGraphComponent
	{

		/**
		 *
		 */
		private static final long serialVersionUID = -6833603133512882012L;

		/**
		 *
		 * @param graph
		 */
		public CustomGraphComponent(mxGraph graph)
		{
			super(graph);

			// Sets switches typically used in an editor
			setPageVisible(true);
//			setGridVisible(true);
			setToolTips(true);
			getConnectionHandler().setCreateTarget(true);

			// Loads the defalt stylesheet from an external file
			mxCodec codec = new mxCodec();
			Document doc = mxUtils.loadDocument(GraphEditor.class.getResource(
							"/com/mxgraph/examples/swing/resources/default-style.xml")
					.toString());
			codec.decode(doc.getDocumentElement(), graph.getStylesheet());

			// Sets the background to white
			getViewport().setOpaque(true);
			getViewport().setBackground(Color.WHITE);
		}

		/**
		 * Overrides drop behaviour to set the cell style if the target
		 * is not a valid drop target and the cells are of the same
		 * type (eg. both vertices or both edges).
		 */
		@SuppressWarnings("finally")
		public Object[] importCells(Object[] cells, double dx, double dy,
									Object target, Point location)
		{
			if (target == null && cells.length == 1 && location != null)
			{
				target = getCellAt(location.x, location.y);

				if (target instanceof mxICell && cells[0] instanceof mxICell)
				{
					mxICell targetCell = (mxICell) target;
					mxICell dropCell = (mxICell) cells[0];

					if (targetCell.isVertex() == dropCell.isVertex()
							|| targetCell.isEdge() == dropCell.isEdge())
					{
						mxIGraphModel model = graph.getModel();
						model.setStyle(target, model.getStyle(cells[0]));
						graph.setSelectionCell(target);

						return null;
					}
				}
			}

			// get the cell to add
			Object[] cellToAdd = super.importCells(cells, dx, dy, target, location);
			graph.getModel().beginUpdate();
			try {
				if (((mxCell) cellToAdd[0]).getType().equals("objType")) {
					// add port to the element
//					ArrayList<mxCell> ports = getObjTypePorts();
//					graph.addCell(ports.get(0), cellToAdd[0]);
//					graph.addCell(ports.get(1), cellToAdd[0]);
//					graph.addCell(ports.get(2), cellToAdd[0]);
//					graph.addCell(ports.get(3), cellToAdd[0]);
//					mw.updatePortParent(ports.get(0).getId(), (mxCell) cellToAdd[0]);
//					mw.updatePortParent(ports.get(1).getId(), (mxCell) cellToAdd[0]);
//					mw.updatePortParent(ports.get(2).getId(), (mxCell) cellToAdd[0]);
//					mw.updatePortParent(ports.get(3).getId(), (mxCell) cellToAdd[0]);
//					mw.updatePortSemantic(ports.get(0).getId(), "before");
//					mw.updatePortSemantic(ports.get(1).getId(), "after");
//					mw.updatePortSemantic(ports.get(2).getId(), "during");
//					mw.updatePortSemantic(ports.get(3).getId(), "during");
//					((mxCell) cellToAdd[0]).setConnectable(false);

					// begin configure the object property in period state
					graph.getModel().endUpdate();
					graph.refresh();

				}
				else if (((mxCell) cellToAdd[0]).getType().equals("activity")) {
					ArrayList<mxCell> ports = getActivityPorts();
					graph.addCell(ports.get(0), cellToAdd[0]);
					graph.addCell(ports.get(1), cellToAdd[0]);
					graph.addCell(ports.get(2), cellToAdd[0]);
					graph.addCell(ports.get(3), cellToAdd[0]);
					graph.addCell(ports.get(4), cellToAdd[0]);
					graph.addCell(ports.get(5), cellToAdd[0]);
					graph.getModel().endUpdate();
					graph.refresh();
				}
			}
			catch (Exception exception){

			}
			finally {
				((mxCell) cellToAdd[0]).setConnectable(false);
				return cellToAdd;
			}



//			return super.importCells(cells, dx, dy, target, location);
		}

	}


	public static ArrayList<mxCell> getActivityPorts(){
		int PORT_DIAMETER = 12;

		int PORT_RADIUS = PORT_DIAMETER / 2;

		ArrayList<mxCell> ports = new ArrayList<>();

		mxGeometry geo1 = new mxGeometry(0, 0.2, PORT_DIAMETER,
				PORT_DIAMETER);
		geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo1.setRelative(true);
		mxCell port1 = new mxCell(null, geo1,
				"triangle;direction=west","actToActBeforePort",
				"connection to another activity");
		port1.setVertex(true);

		mxGeometry geo2 = new mxGeometry(0, 0.5, PORT_DIAMETER,
				PORT_DIAMETER);
		geo2.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo2.setRelative(true);
		mxCell port2 = new mxCell(null, geo2,
				"triangle;direction=west","actToActBeforePort", "connection to another activity");
		port2.setVertex(true);

		mxGeometry geo3 = new mxGeometry(0, 0.8, PORT_DIAMETER,
				PORT_DIAMETER);
		geo3.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo3.setRelative(true);
		mxCell port3 = new mxCell(null, geo3,
				"triangle;direction=west","actToActBeforePort", "connection to another activity");
		port3.setVertex(true);

		mxGeometry geo4 = new mxGeometry(1, 0.2, PORT_DIAMETER,
				PORT_DIAMETER);
		geo4.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo4.setRelative(true);
		mxCell port4 = new mxCell(null, geo4,
				"triangle;timeRefPort","actToActAfterPort", "connection to another object type");
		port4.setVertex(true);

		mxGeometry geo5 = new mxGeometry(1, 0.5, PORT_DIAMETER,
				PORT_DIAMETER);
		geo5.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo5.setRelative(true);
		mxCell port5 = new mxCell(null, geo5,
				"triangle;timeRefPort","actToActAfterPort", "connection to another object type");
		port5.setVertex(true);

		mxGeometry geo6 = new mxGeometry(1, 0.8, PORT_DIAMETER,
				PORT_DIAMETER);
		geo6.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo6.setRelative(true);
		mxCell port6 = new mxCell(null, geo6,
				"triangle;timeRefPort","actToActAfterPort", "connection to another object type");
		port6.setVertex(true);

		ports.add(port1);
		ports.add(port2);
		ports.add(port3);
		ports.add(port4);
		ports.add(port5);
		ports.add(port6);

		return ports;
	}

	/**
	 * A graph that creates new edges from a given template edge.
	 */
	public static class CustomGraph extends mxGraph
	{
		/**
		 * Holds the edge to be used as a template for inserting new edges.
		 */
		protected Object edgeTemplate;

		/**
		 * Custom graph that defines the alternate edge style to be used when
		 * the middle control point of edges is double clicked (flipped).
		 */
		public CustomGraph()
		{
			setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
		}

		/**
		 * Sets the edge template to be used to inserting edges.
		 */
		public void setEdgeTemplate(Object template)
		{
			edgeTemplate = template;
		}

		/**
		 * Prints out some useful information about the cell in the tooltip.
		 */
		public String getToolTipForCell(Object cell)
		{
			return ((mxCell)cell).getTip();
		}

		/**
		 * Overrides the method to use the currently selected edge template for
		 * new edges.
		 *
		 * @param graph
		 * @param parent
		 * @param id
		 * @param value
		 * @param source
		 * @param target
		 * @param style
		 * @return
		 */
		public Object createEdge(Object parent, String id, Object value,
								 Object source, Object target, String style)
		{
			if (edgeTemplate != null)
			{
				mxCell edge = (mxCell) cloneCells(new Object[] { edgeTemplate })[0];
				edge.setId(id);

				return edge;
			}

			return super.createEdge(parent, id, value, source, target, style);
		}

	}

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{

		JFrame frame = new JFrame();
		System.out.print("\n changshacall the visualizer");

		//		model.setModelEnvironment(mxConstants.model_editor);
		GraphEditor editor = new GraphEditor();

		frame.add(editor);

		// get the screen size
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());

		// enlarge to the whole page
		frame.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);

		frame.setVisible(true);



		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}

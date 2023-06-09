/**
 * Copyright (c) 2006-2012, JGraph Ltd */
package org.processmining.objectcentricconstraintchecking.algorithms.ocel.constraint;

import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.processmining.objectcentricconstraintchecking.algorithms.analysis.mxAnalysisGraph;
import org.processmining.objectcentricconstraintchecking.algorithms.analysis.mxGraphGenerator;
import org.processmining.objectcentricconstraintchecking.algorithms.analysis.mxGraphProperties;
import org.processmining.objectcentricconstraintchecking.algorithms.editor.BasicGraphEditor;
import org.processmining.objectcentricconstraintchecking.algorithms.io.mxCodec;
import org.processmining.objectcentricconstraintchecking.algorithms.model.mxCell;
import org.processmining.objectcentricconstraintchecking.algorithms.model.mxGeometry;
import org.processmining.objectcentricconstraintchecking.algorithms.model.mxICell;
import org.processmining.objectcentricconstraintchecking.algorithms.model.mxIGraphModel;
import org.processmining.objectcentricconstraintchecking.algorithms.swing.mxGraphComponent;
import org.processmining.objectcentricconstraintchecking.algorithms.util.mxPoint;
import org.processmining.objectcentricconstraintchecking.algorithms.util.mxUtils;
import org.processmining.objectcentricconstraintchecking.algorithms.view.mxCellState;
import org.processmining.objectcentricconstraintchecking.algorithms.view.mxGraph;
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

	public GraphEditor()
	{
		this("mxGraph Editor", new CustomGraphComponent(new CustomGraph()));
	}

	/**
	 *
	 */
	public GraphEditor(String appTitle, mxGraphComponent component)
	{
		super(appTitle, component);
		final mxGraph graph = graphComponent.getGraph();

//		graph.getModel().beginUpdate();
//		graph.selectAll();
//		graph.removeCells();
//
//		mxGraphGenerator generator = new mxGraphGenerator(mxGraphGenerator.getGeneratorFunction(graph, false, 0, 10),
//				new mxDoubleValCostFunction());
//		Map<String, Object> props = new HashMap<String, Object>();
//		mxGraphProperties.setDirected(props, false);
//		configAnalysisGraph(graph, generator, props);
//
//		generator.getSimpleRandomTree(aGraph, 6);
//
//		mxGraphProperties.setDirected(props, true);
//		mxGraphStructure.setDefaultGraphStyle(aGraph, false);
//		setVisible(false);
//		mxCompactTreeLayout layout = new mxCompactTreeLayout(graph, false);
//		layout.execute(graph.getDefaultParent());
//		graph.getModel().endUpdate();

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

			return super.importCells(cells, dx, dy, target, location);
		}

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
			String tip = "<html>";
			mxGeometry geo = getModel().getGeometry(cell);
			mxCellState state = getView().getState(cell);

			if (getModel().isEdge(cell))
			{
				tip += "points={";

				if (geo != null)
				{
					List<mxPoint> points = geo.getPoints();

					if (points != null)
					{
						Iterator<mxPoint> it = points.iterator();

						while (it.hasNext())
						{
							mxPoint point = it.next();
							tip += "[x=" + numberFormat.format(point.getX())
									+ ",y=" + numberFormat.format(point.getY())
									+ "],";
						}

						tip = tip.substring(0, tip.length() - 1);
					}
				}

				tip += "}<br>";
				tip += "absPoints={";

				if (state != null)
				{

					for (int i = 0; i < state.getAbsolutePointCount(); i++)
					{
						mxPoint point = state.getAbsolutePoint(i);
						tip += "[x=" + numberFormat.format(point.getX())
								+ ",y=" + numberFormat.format(point.getY())
								+ "],";
					}

					tip = tip.substring(0, tip.length() - 1);
				}

				tip += "}";
			}
			else
			{
				tip += "geo=[";

				if (geo != null)
				{
					tip += "x=" + numberFormat.format(geo.getX()) + ",y="
							+ numberFormat.format(geo.getY()) + ",width="
							+ numberFormat.format(geo.getWidth()) + ",height="
							+ numberFormat.format(geo.getHeight());
				}

				tip += "]<br>";
				tip += "state=[";

				if (state != null)
				{
					tip += "x=" + numberFormat.format(state.getX()) + ",y="
							+ numberFormat.format(state.getY()) + ",width="
							+ numberFormat.format(state.getWidth())
							+ ",height="
							+ numberFormat.format(state.getHeight());
				}

				tip += "]";
			}

			mxPoint trans = getView().getTranslate();

			tip += "<br>scale=" + numberFormat.format(getView().getScale())
					+ ", translate=[x=" + numberFormat.format(trans.getX())
					+ ",y=" + numberFormat.format(trans.getY()) + "]";
			tip += "</html>";

			return tip;
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
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//		try
//		{
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		}
//		catch (Exception e1)
//		{
//			e1.printStackTrace();
//		}
//
//		mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
//		mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";
//
//		GraphEditor editor = new GraphEditor();
//		editor.createFrame(new EditorMenuBar(editor)).setVisible(true);
	}
}

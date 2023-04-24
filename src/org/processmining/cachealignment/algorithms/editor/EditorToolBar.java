package org.processmining.cachealignment.algorithms.editor;

import org.processmining.cachealignment.algorithms.swing.mxGraphComponent;
import org.processmining.cachealignment.algorithms.swing.util.mxGraphActions;
import org.processmining.cachealignment.algorithms.util.*;
import org.processmining.cachealignment.algorithms.view.mxGraphView;
import org.processmining.cachealignment.algorithms.editor.EditorActions.*;
import org.processmining.cachealignment.algorithms.util.mxConstants;
import org.processmining.cachealignment.algorithms.util.mxEvent;
import org.processmining.cachealignment.algorithms.util.mxEventObject;
import org.processmining.cachealignment.algorithms.util.mxResources;
import org.processmining.cachealignment.algorithms.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditorToolBar extends JToolBar
{

	/**
	 *
	 */
	private static final long serialVersionUID = -8015443128436394471L;

	private JComboBox<String> zoomCombo;
	private JComboBox<String> activityCombo;
	private JComboBox<String> sourceAlwaysCombo;
	private JComboBox<String> sourceEventuallyCombo;
	private JComboBox<String> targetAlwaysCombo;
	private JComboBox<String> targetEventuallyCombo;
	private JComboBox<String> edgeStyleCombo;
	/**
	 *
	 * @param frame
	 * @param orientation
	 */
	private boolean ignoreZoomChange = false;

	public JComboBox<String> getZoomBox(){
		return zoomCombo;
	}
	/**
	 *
	 */
	public EditorToolBar(final BasicGraphEditor editor, int orientation)
	{
		super(orientation);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createEmptyBorder(3, 3, 3, 3), getBorder()));
		setFloatable(false);

		add(editor.bind("New", new NewAction(),
				"/org/processmining/mxgraph/examples/swing/images/new.gif"));
		add(editor.bind("Open", new OpenAction(),
				"/org/processmining/mxgraph/examples/swing/images/open.gif"));
		add(editor.bind("Save", new SaveAction(false),
				"/org/processmining/mxgraph/examples/swing/images/save.gif"));

//		addSeparator();
//
//		add(editor.bind("Save to workspace", new SaveAction(false),
//				"/org/processmining/mxgraph/examples/swing/images/workspace1.png"));

		addSeparator();

		add(editor.bind("Print", new PrintAction(),
				"/org/processmining/mxgraph/examples/swing/images/print.gif"));

		addSeparator();

		add(editor.bind("Cut", TransferHandler.getCutAction(),
				"/org/processmining/mxgraph/examples/swing/images/cut.gif"));
		add(editor.bind("Copy", TransferHandler.getCopyAction(),
				"/org/processmining/mxgraph/examples/swing/images/copy.gif"));
		add(editor.bind("Paste", TransferHandler.getPasteAction(),
				"/org/processmining/mxgraph/examples/swing/images/paste.gif"));

		addSeparator();

		add(editor.bind("Delete", mxGraphActions.getDeleteAction(),
				"/org/processmining/mxgraph/examples/swing/images/delete.gif"));

		addSeparator();

		add(editor.bind("Undo", new HistoryAction(true),
				"/org/processmining/mxgraph/examples/swing/images/undo.gif"));
		add(editor.bind("Redo", new HistoryAction(false),
				"/org/processmining/mxgraph/examples/swing/images/redo.gif"));

		addSeparator();
//
//		// Gets the list of available fonts from the local graphics environment
//		// and adds some frequently used fonts at the beginning of the list
//		GraphicsEnvironment env = GraphicsEnvironment
//				.getLocalGraphicsEnvironment();
//		List<String> fonts = new ArrayList<String>();
//		fonts.addAll(Arrays.asList(new String[] { "Helvetica", "Verdana",
//				"Times New Roman", "Garamond", "Courier New", "-" }));
//		fonts.addAll(Arrays.asList(env.getAvailableFontFamilyNames()));
//
//		final JComboBox fontCombo = new JComboBox(fonts.toArray());
//		fontCombo.setEditable(true);
//		fontCombo.setMinimumSize(new Dimension(120, 0));
//		fontCombo.setPreferredSize(new Dimension(120, 0));
//		fontCombo.setMaximumSize(new Dimension(120, 100));
//		add(fontCombo);
//
//		fontCombo.addActionListener(new ActionListener()
//		{
//			/**
//			 *
//			 */
//			public void actionPerformed(ActionEvent e)
//			{
//				String font = fontCombo.getSelectedItem().toString();
//
//				if (font != null && !font.equals("-"))
//				{
//					mxGraph graph = editor.getGraphComponent().getGraph();
//					graph.setCellStyles(mxConstants.STYLE_FONTFAMILY, font);
//				}
//			}
//		});
//
//		final JComboBox sizeCombo = new JComboBox(new Object[] { "6pt", "8pt",
//				"9pt", "10pt", "12pt", "14pt", "18pt", "24pt", "30pt", "36pt",
//				"48pt", "60pt" });
//		sizeCombo.setEditable(true);
//		sizeCombo.setMinimumSize(new Dimension(65, 0));
//		sizeCombo.setPreferredSize(new Dimension(65, 0));
//		sizeCombo.setMaximumSize(new Dimension(65, 100));
//		add(sizeCombo);
//
//		sizeCombo.addActionListener(new ActionListener()
//		{
//			/**
//			 *
//			 */
//			public void actionPerformed(ActionEvent e)
//			{
//				mxGraph graph = editor.getGraphComponent().getGraph();
//				graph.setCellStyles(mxConstants.STYLE_FONTSIZE, sizeCombo
//						.getSelectedItem().toString().replace("pt", ""));
//			}
//		});
//
//		addSeparator();
//
//		add(editor.bind("Bold", new FontStyleAction(true),
//				"/org/processmining/mxgraph/examples/swing/images/bold.gif"));
//		add(editor.bind("Italic", new FontStyleAction(false),
//				"/org/processmining/mxgraph/examples/swing/images/italic.gif"));
//
//		addSeparator();
//
//		add(editor.bind("Left", new KeyValueAction(mxConstants.STYLE_ALIGN,
//				mxConstants.ALIGN_LEFT),
//				"/org/processmining/mxgraph/examples/swing/images/left.gif"));
//		add(editor.bind("Center", new KeyValueAction(mxConstants.STYLE_ALIGN,
//				mxConstants.ALIGN_CENTER),
//				"/org/processmining/mxgraph/examples/swing/images/center.gif"));
//		add(editor.bind("Right", new KeyValueAction(mxConstants.STYLE_ALIGN,
//				mxConstants.ALIGN_RIGHT),
//				"/org/processmining/mxgraph/examples/swing/images/right.gif"));
//
//		addSeparator();
//
//		add(editor.bind("Font", new ColorAction("Font",
//				mxConstants.STYLE_FONTCOLOR),
//				"/org/processmining/mxgraph/examples/swing/images/fontcolor.gif"));
//		add(editor.bind("Stroke", new ColorAction("Stroke",
//				mxConstants.STYLE_STROKECOLOR),
//				"/org/processmining/mxgraph/examples/swing/images/linecolor.gif"));
//		add(editor.bind("Fill", new ColorAction("Fill",
//				mxConstants.STYLE_FILLCOLOR),
//				"/org/processmining/mxgraph/examples/swing/images/fillcolor.gif"));
//
//		addSeparator();

		final mxGraphView view = editor.getGraphComponent().getGraph()
				.getView();
		zoomCombo = new JComboBox(new Object[] { "400%",
				"200%", "150%", "100%", "75%", "50%", mxResources.get("page"),
				mxResources.get("width"), mxResources.get("actualSize") });
		zoomCombo.setEditable(true);
		zoomCombo.setMinimumSize(new Dimension(75, 0));
		zoomCombo.setPreferredSize(new Dimension(75, 0));
		zoomCombo.setMaximumSize(new Dimension(75, 100));
		zoomCombo.setMaximumRowCount(9);
//		zoomCombo.setSelectedIndex(2);
		add(zoomCombo);

		// Sets the zoom in the zoom combo the current value
		mxEventSource.mxIEventListener scaleTracker = new mxEventSource.mxIEventListener()
		{
			/**
			 *
			 */
			public void invoke(Object sender, mxEventObject evt)
			{
				ignoreZoomChange = true;

				try
				{
					zoomCombo.setSelectedItem((int) Math.round(100 * view
							.getScale())
							+ "%");

				}
				finally
				{
					ignoreZoomChange = false;
				}
			}
		};

		// Installs the scale tracker to update the value in the combo box
		// if the zoom is changed from outside the combo box
		view.getGraph().getView().addListener(mxEvent.SCALE, scaleTracker);
		view.getGraph().getView().addListener(mxEvent.SCALE_AND_TRANSLATE,
				scaleTracker);

		// Invokes once to sync with the actual zoom value
		scaleTracker.invoke(null, null);

		zoomCombo.addActionListener(new ActionListener()
		{
			/**
			 *
			 */
			public void actionPerformed(ActionEvent e)
			{
				mxGraphComponent graphComponent = editor.getGraphComponent();

				// Zoomcombo is changed when the scale is changed in the diagram
				// but the change is ignored here
				if (!ignoreZoomChange)
				{
					String zoom = zoomCombo.getSelectedItem().toString();

					if (zoom.equals(mxResources.get("page")))
					{
						graphComponent.setPageVisible(true);
						graphComponent
								.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_PAGE);
					}
					else if (zoom.equals(mxResources.get("width")))
					{
						graphComponent.setPageVisible(true);
						graphComponent
								.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_WIDTH);
					}
					else if (zoom.equals(mxResources.get("actualSize")))
					{
						graphComponent.zoomActual();
					}
					else
					{
						try
						{
							zoom = zoom.replace("%", "");
							double scale = Math.min(16, Math.max(0.01,
									Double.parseDouble(zoom) / 100));
							System.out.println("call the zoom function");
							graphComponent.zoomTo(scale, graphComponent
									.isCenterZoom());
						}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog(editor, ex
									.getMessage());
						}
					}
				}
			}
		});

		//add my tools
		addSeparator();

		//set the edge style
		edgeStyleCombo = new JComboBox<String>(new String[] {
				mxConstants.SHAPE_LINE, mxConstants.EDGESTYLE_ELBOW, mxConstants.EDGESTYLE_SEGMENT,
				mxConstants.EDGESTYLE_LOOP, mxConstants.EDGESTYLE_SIDETOSIDE, mxConstants.EDGESTYLE_ENTITY_RELATION,
				mxConstants.EDGESTYLE_TOPTOBOTTOM});

		edgeStyleCombo.setEditable(true);
		edgeStyleCombo.setEnabled(false);
		add(edgeStyleCombo);


//		addSeparator();

//		showConnectorButton = new JButton("connetor");
//		showConnectorButton.setBorder(BorderFactory.createRaisedBevelBorder());
////		add(showConnectorButton);
//		showConnectorButton.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				mxGraph graph = editor.getGraphComponent().getGraph();
//				Object[] edgeCells = graph.getChildEdges(graph.getDefaultParent());
//
//				//hide the connector
//				if(showConnectorButton.getBorder().equals(BorderFactory.createLoweredBevelBorder())){
//					showConnectorButton.setBorder(BorderFactory.createRaisedBevelBorder());
//					for(int j=0; j<edgeCells.length; j++){
//						mxCell edgeCell = (mxCell) edgeCells[j];
//						String edgeType = edgeCell.getAttributes().get(mxConstants.TYPE);
//						System.out.println("cancel edge type: " + edgeCell);
//						if(edgeType.equals(mxConstants.ACTIVITYRELATION) || edgeType.equals(mxConstants.CLASSRELATION)){
//							Object[] nodeCells = graph.getChildVertices(edgeCell);
//							for(int i=0; i<nodeCells.length; i++){
//								mxCell nodeCell = (mxCell) nodeCells[i];
//								String nodeType = nodeCell.getAttributes().get(mxConstants.TYPE);
//								System.out.println("cancel node type: " + nodeType);
//								if(nodeType != null && nodeType.equals(mxConstants.CONNECTOR)){
//									System.out.println("cancel node connector");
//									editor.getGraphComponent().getGraph().getModel().setStyle(nodeCell, mxConstants.CONNECTOR_TRANSPARENT);
//									editor.getGraphComponent().getGraph().refresh();
//								}
//							}
//						}
//					}
//				}
//				//show the connector
//				else{
//					showConnectorButton.setBorder(BorderFactory.createLoweredBevelBorder());
//					for(int j=0; j<edgeCells.length; j++){
//						mxCell edgeCell = (mxCell) edgeCells[j];
//						String edgeType = edgeCell.getAttributes().get(mxConstants.TYPE);
//						System.out.println("cancel edge type: " + edgeCell);
//						if(edgeType.equals(mxConstants.ACTIVITYRELATION) || edgeType.equals(mxConstants.CLASSRELATION)){
//							Object[] nodeCells = graph.getChildVertices(edgeCell);
//							for(int i=0; i<nodeCells.length; i++){
//								mxCell nodeCell = (mxCell) nodeCells[i];
//								String nodeType = nodeCell.getAttributes().get(mxConstants.TYPE);
//								System.out.println("cancel node type: " + nodeType);
//								if(nodeType != null && nodeType.equals(mxConstants.CONNECTOR)){
//									System.out.println("cancel node connector");
//									editor.getGraphComponent().getGraph().getModel().setStyle(nodeCell, mxConstants.CONNECTOR_FOCUSED);
//									editor.getGraphComponent().getGraph().refresh();
//								}
//							}
//						}
//					}
//				}
//			}
//		});
//
////		addSeparator();
//
//		//check if the graph is legal (it has all elements an OCBC model requires)
//		checkButton = new JButton("check");
//		checkButton.setBorder(BorderFactory.createRaisedBevelBorder());
////		add(checkButton);
//		checkButton.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				mxGraph graph = editor.getGraphComponent().getGraph();
//				Object[] edgeCells = graph.getChildEdges(graph.getDefaultParent());
//				Object[] nodeCells = graph.getChildVertices(graph.getDefaultParent());
//
//				if(checkButton.getBorder().equals(BorderFactory.createRaisedBevelBorder())){
//					checkButton.setBorder(BorderFactory.createLoweredBevelBorder());
//					//check if the activity nodes have aoc relation and highlight the labels of violating nodes
//					for(int j=0; j<nodeCells.length; j++){
//						mxCell nodeCell = (mxCell) nodeCells[j];
//						String nodeType = nodeCell.getAttributes().get(mxConstants.TYPE);
//						System.out.println("cancel edge type: " + nodeType);
//						if(nodeType.equals(mxConstants.ACTIVITYCELL)){
//							int linkedAOCEdgeNumber = 0;
//							for(int k=0; k<nodeCell.getEdgeCount(); k++){
//								mxCell linkedEdgeCell = (mxCell) nodeCell.getEdgeAt(k);
//								String linkEdgeType = linkedEdgeCell.getAttributes().get(mxConstants.TYPE);
//								if(linkEdgeType.equals(mxConstants.AOCRELATION)){
//									linkedAOCEdgeNumber ++;
//								}
//							}
//							if(linkedAOCEdgeNumber == 0){
//								Style.addStyleElementToCell(nodeCell, mxConstants.STYLE_FONTCOLOR, "red");
//								editor.getGraphComponent().getGraph().refresh();
//							}
//						}
//					}
//					//check if the activity edges have crel relation and highlight the violating connector
//					for(int j=0; j<edgeCells.length; j++){
//						mxCell edgeCell = (mxCell) edgeCells[j];
//						String edgeType = edgeCell.getAttributes().get(mxConstants.TYPE);
//						System.out.println("cancel edge type: " + edgeCell);
//						if(edgeType.equals(mxConstants.ACTIVITYRELATION)){
//							Object[] connectorCells = graph.getChildVertices(edgeCell);
//							mxCell connectorCell = (mxCell) connectorCells[0];
//							int linkedEdgeNumber = connectorCell.getEdgeCount();
//							if(linkedEdgeNumber != 1){
//								editor.getGraphComponent().getGraph().getModel().setStyle(connectorCell, mxConstants.CONNECTOR_VIOLATING);
//								editor.getGraphComponent().getGraph().refresh();
//							}
//						}
//					}
//				}
//				//hide the connector
//				else{
//					checkButton.setBorder(BorderFactory.createRaisedBevelBorder());
//					for(int j=0; j<edgeCells.length; j++){
//						mxCell edgeCell = (mxCell) edgeCells[j];
//						String edgeType = edgeCell.getAttributes().get(mxConstants.TYPE);
//						System.out.println("cancel edge type: " + edgeCell);
//						if(edgeType.equals(mxConstants.ACTIVITYRELATION)){
//							Object[] childNodeCells = graph.getChildVertices(edgeCell);
//							for(int i=0; i<childNodeCells.length; i++){
//								mxCell nodeCell = (mxCell) childNodeCells[i];
//								String nodeType = nodeCell.getAttributes().get(mxConstants.TYPE);
//								System.out.println("cancel node type: " + nodeType);
//								if(nodeType != null && nodeType.equals(mxConstants.CONNECTOR)){
//									System.out.println("cancel node connector");
//									editor.getGraphComponent().getGraph().getModel().setStyle(nodeCell, mxConstants.CONNECTOR_TRANSPARENT);
//									editor.getGraphComponent().getGraph().refresh();
//								}
//							}
//						}
//					}
//
//					for(int j=0; j<nodeCells.length; j++){
//						mxCell nodeCell = (mxCell) nodeCells[j];
//						String nodeType = nodeCell.getAttributes().get(mxConstants.TYPE);
//						System.out.println("cancel edge type: " + nodeType);
//						if(nodeType.equals(mxConstants.ACTIVITYCELL)){
//							Style.addStyleElementToCell(nodeCell, mxConstants.STYLE_FONTCOLOR, "black");
//							editor.getGraphComponent().getGraph().refresh();
//						}
//					}
//				}
//			}
//		});
//
////		addSeparator();
//
//		//check if the graph is legal (it has all elements an OCBC model requires)
//		refreshButton = new JButton("refresh");
//		refreshButton.setBorder(BorderFactory.createRaisedBevelBorder());
////		add(refreshButton);
//		refreshButton.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e){
//				editor.refreshGraph();
//			}
//		});
//
//
////		addSeparator();
//
//		//check if the graph is legal (it has all elements an OCBC model requires)
//		swapSTButton = new JButton("swap S/T");
//		swapSTButton.setBorder(BorderFactory.createRaisedBevelBorder());
////		add(swapSTButton);
//		swapSTButton.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e){
//				mxGraph graph = editor.getGraphComponent().getGraph();
//				mxCell selectedCell = (mxCell) graph.getSelectionCell();
//
//				if(selectedCell != null){
//					String selectedCellType = selectedCell.getAttributes().get("type");
//					System.out.println("type: " + selectedCellType);
//
//					if(selectedCellType.equals(mxConstants.CLASSRELATION)){
//						System.out.println("swap");
//						mxCell sourceCell = (mxCell) selectedCell.getSource();
//						mxCell targetCell = (mxCell) selectedCell.getTarget();
//						selectedCell.setSource(targetCell);
//						selectedCell.setTarget(sourceCell);
//						editor.addModificationFlag();
//						editor.refreshGraph();
//					}
//				}
//			}
//		});
//
////		addSeparator();
//
//		//check if the graph is legal (it has all elements an OCBC model requires)
//		pageNumButton = new JButton("page zoom");
//		pageNumButton.setBorder(BorderFactory.createRaisedBevelBorder());
////		add(pageNumButton);
//		pageNumButton.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e){
//				mxGraph graph = editor.getGraphComponent().getGraph();
//				mxCell selectedCell = (mxCell) graph.getSelectionCell();
////				int height = editor.getGraphComponent().getSize().height;
////				int width = editor.getGraphComponent().getSize().width;
////				System.out.println("height: " + height);
////				System.out.println("width: " + width);
////				Object v1 = graph.insertVertex(graph.getDefaultParent(), null, "Hello", width*2, height*2, 80,
////				30);
//
//				System.out.println("getHorizontalPageCount(): " + editor.getGraphComponent().getHorizontalPageCount());
//				System.out.println("getVerticalPageCount: " + editor.getGraphComponent().getVerticalPageCount());
//				int horizontalCount = editor.getGraphComponent().getHorizontalPageCount();
//				int verticalCount = editor.getGraphComponent().getVerticalPageCount();
//
//				editor.getGraphComponent().setHorizontalPageCount(horizontalCount+1);
//				editor.getGraphComponent().setVerticalPageCount(verticalCount+1);
//
//			}
//		});

	}
}

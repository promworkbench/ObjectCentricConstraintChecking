package org.processmining.cachealignment.algorithms.editor;//package org.processmining.editor;
//
//import java.awt.BorderLayout;
//import java.awt.Point;
//import java.awt.Toolkit;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.ComponentAdapter;
//import java.awt.event.ComponentEvent;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseMotionListener;
//import java.awt.event.MouseWheelEvent;
//import java.awt.event.MouseWheelListener;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.text.NumberFormat;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.swing.AbstractAction;
//import javax.swing.Action;
//import javax.swing.BorderFactory;
//import javax.swing.ImageIcon;
//import javax.swing.JCheckBoxMenuItem;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JPopupMenu;
//import javax.swing.JScrollPane;
//import javax.swing.JSplitPane;
//import javax.swing.JTabbedPane;
//import javax.swing.SwingUtilities;
//import javax.swing.UIManager;
//
//import org.processmining.framework.plugin.PluginContext;
//import org.processmining.ocel.constraint.GraphEditor.CustomGraph;
//import org.processmining.ocel.constraint.GraphEditor.CustomGraphComponent;
//import org.processmining.cachealignment.algorithms.layout.mxCircleLayout;
//import org.processmining.cachealignment.algorithms.layout.mxCompactTreeLayout;
//import org.processmining.cachealignment.algorithms.layout.mxEdgeLabelLayout;
//import org.processmining.cachealignment.algorithms.layout.mxIGraphLayout;
//import org.processmining.cachealignment.algorithms.layout.mxOrganicLayout;
//import org.processmining.cachealignment.algorithms.layout.mxParallelEdgeLayout;
//import org.processmining.cachealignment.algorithms.layout.mxPartitionLayout;
//import org.processmining.cachealignment.algorithms.layout.mxStackLayout;
//import org.processmining.cachealignment.algorithms.layout.hierarchical.mxHierarchicalLayout;
//import org.processmining.model.mxCell;
//import org.processmining.swing.mxGraphComponent;
//import org.processmining.swing.mxGraphOutline;
//import org.processmining.swing.handler.mxKeyboardHandler;
//import org.processmining.swing.handler.mxRubberband;
//import org.processmining.swing.util.mxMorphing;
//import org.processmining.util.mxConstants;
//import org.processmining.util.mxEvent;
//import org.processmining.util.mxEventObject;
//import org.processmining.util.mxEventSource.mxIEventListener;
//import org.processmining.util.mxRectangle;
//import org.processmining.util.mxResources;
//import org.processmining.util.mxUndoManager;
//import org.processmining.util.mxUndoableEdit;
//import org.processmining.util.mxUndoableEdit.mxUndoableChange;
//import org.processmining.view.mxGraph;
//
//public class BasicGraphPanel extends JPanel
//{
//
//	/**
//	 *
//	 */
//	private static final long serialVersionUID = -6561623072112577140L;
//
//	protected static final String diamondSymbol = null;
//
//	/**
//	 * Adds required resources for i18n
//	 */
//	static
//	{
//		try
//		{
//			mxResources.add("org.processmining/mxgraph/examples/swing/resources/editor");
//		}
//		catch (Exception e)
//		{
//			// ignore
//		}
//	}
//
//	/**
//	 * save the model in the workspace
//	 */
//	protected PluginContext context;
//
//	/**
//	 *
//	 */
//	protected mxGraphComponent graphComponent;
//
////	protected JTabbedPane multipleGraphPanel;
//
//	/**
//	 *
//	 */
//	protected mxGraphOutline graphOutline;
//
//	/**
//	 *
//	 */
//	protected JTabbedPane libraryPane;
//
//	/**
//	 *
//	 */
//	protected mxUndoManager undoManager;
//
//	/**
//	 *
//	 */
//	protected String appTitle;
//
//	/**
//	 *
//	 */
//	protected JLabel statusBar;
//
//	/**
//	 *
//	 */
//	protected File currentFile;
//
//	/**
//	 * each graph component corresponds to one file
//	 */
//	protected Map<mxGraphComponent, File> graphComponentVSFileMap = new HashMap<mxGraphComponent, File>();
//
//	/**
//	 * Flag indicating whether the current graph has been modified
//	 */
//	protected boolean modified = false;
//
//	/**
//	 *
//	 */
//	protected mxRubberband rubberband;
//
//	/**
//	 *
//	 */
//	protected mxKeyboardHandler keyboardHandler;
//
//	protected EditorToolBar toolBar;
//
//	protected ResultModelView resultModelView;
//
//	public static final NumberFormat numberFormat = NumberFormat.getInstance();
//
//	/**
//	 *
//	 */
//	protected mxIEventListener undoHandler = new mxIEventListener()
//	{
//		public void invoke(Object source, mxEventObject evt)
//		{
//			undoManager.undoableEditHappened((mxUndoableEdit) evt
//					.getProperty("edit"));
//		}
//	};
//
//    private double souceLabelPosition = -0.6;
//    private double targetLabelPosition = 0.4;
//    private double connectorSize = 5;
//    private double connectorX = 0.2;
//
//    protected mxIEventListener changeTracker;
//
//	/**
//	 *
//	 */
//    /*
//	protected mxIEventListener changeTracker = new mxIEventListener()
//	{
//		public void invoke(Object source, mxEventObject evt)
//		{
//			setModified(true);
//			//change name in multiple graph panel
////			addModificationFlag();
//
//
//			//add source and target labels for the new created edge
//			Object changes = evt.getProperties().get("changes");
//            if (changes instanceof ArrayList){
//                ArrayList list = (ArrayList) changes;
//                //judge the chagne type
//                for (Object o : list){
//                    if (o instanceof mxGeometryChange){
//                        Object value = ((mxCell) ((mxGeometryChange) o).getCell()).getValue();
//                        System.out.println("mxGeometryChange");
//                    }
//                    else if (o instanceof mxChildChange)
//                    {
//                    	mxCell newCell = (mxCell) ((mxChildChange) o).getChild();
//                    	//if the graph contain the cell and the cell is not imported
////                    	&& !newCell.getAttributes().containsKey(mxConstants.CELLSTATE )
//                    	if(graphComponent.getGraph().getModel().contains(newCell)){
//                    		if(newCell.isVertex()){
//
//                        		System.out.println("add node");
//                        		System.out.println("newCell.getValue().toString()" + newCell.getValue().toString());
//
//                        		graphComponent.getGraph().refresh();
//                        		if(newCell.getValue().toString().equals(mxConstants.CLASSCELL)){
//                        			//add necessary attribute for nodes
//                        			newCell.getAttributes().put(mxConstants.TYPE, mxConstants.CLASSCELL);
//                            		newCell.getAttributes().put(mxConstants.TABLE, "table_name");
//                            		newCell.getAttributes().put(mxConstants.ROWID, mxConstants.PK);
//                        		}
//                        		else if(newCell.getValue().toString().equals(mxConstants.ACTIVITYCELL)){
//                        			newCell.getAttributes().put(mxConstants.TYPE, mxConstants.ACTIVITYCELL);
//                        			newCell.getAttributes().put(mxConstants.TABLE, "table_name");
//                        			newCell.getAttributes().put(mxConstants.ACTIVITY, "column-name");
//                        		}
//                        		else if(newCell.getAttributes().containsKey(mxConstants.TYPE) &&
//                        				newCell.getAttributes().get(mxConstants.TYPE).equals(mxConstants.CONNECTOR)){
//                        			System.out.println("connector");
//                        			//if the connector has no parent edge, remove it
//                        			mxCell parentCell = (mxCell) newCell.getParent();
//                        			if(parentCell.getAttributes().get(mxConstants.TYPE) == null){
//                        				System.out.println("getParent().type == null");
//                        				graphComponent.getGraph().removeCells(new Object[]{newCell});
//                        			}
//                        			graphComponent.getGraph().refresh();
//
//                        		}
//
//                        	}
//                    		//if the edge's type is null, it is a new created edge. Other wise, it is copied
//                        	else if(newCell.isEdge() && !newCell.getAttributes().containsKey(mxConstants.TYPE)){
//                        		Object value = newCell.getValue();
//                                System.out.println("add edge");
//                                System.out.println(value);
//
//                                mxCell sourceCell = (mxCell) newCell.getSource();
//                                mxCell targetCell = (mxCell) newCell.getTarget();
//                                String sourceType = sourceCell.getAttributes().get(mxConstants.TYPE);
//                                String targetType = targetCell.getAttributes().get(mxConstants.TYPE);
//                                String sourceName = (String) sourceCell.getValue();
//                                String targetName = (String) targetCell.getValue();
//
//                                mxCell sourceParentCell = (mxCell) sourceCell.getParent();
//                            	mxCell targetParentCell = (mxCell) targetCell.getParent();
//                            	String sourceParentType = sourceParentCell.getAttributes().get(mxConstants.TYPE);
//                                String targetParentType = targetParentCell.getAttributes().get(mxConstants.TYPE);
//
//                                System.out.println(targetCell.getAttributes().get("type"));
//                                System.out.println(sourceCell.getAttributes().get("type"));
//
//                                //class relation
//                                if(sourceType.equals(mxConstants.CLASSCELL) && targetType.equals(mxConstants.CLASSCELL)){
//
//                                	//insert value (class relation type)
//                                	newCell.setValue("name");
//                                	//insert attributes of type and cardinalities
//                                	newCell.getAttributes().put(mxConstants.TYPE, mxConstants.CLASSRELATION);
//                                	newCell.getAttributes().put(mxConstants.RELATIONSHIPTYPE, sourceName + "-name-" + targetName);
//                                	newCell.getAttributes().put(mxConstants.SOURCEALWAYSCARDINALITY, "*");
//                                	newCell.getAttributes().put(mxConstants.SOURCEEVENTUALLYCARDINALITY, "*");
//                                	newCell.getAttributes().put(mxConstants.TARGETALWAYSCARDINALITY, "*");
//                                	newCell.getAttributes().put(mxConstants.TARGETEVENTUALLYCARDINALITY, "*");
//
//                                	System.out.println("create class relation");
//                                	//class relation
//
//                        			//close to the start of edges
//                        			mxGeometry geoSource = new mxGeometry(souceLabelPosition, souceLabelPosition, 0, 0);
//             		    			mxCell sourceLabel = new mxCell(mxConstants.SQUARESYMBOL + "*" +  mxConstants.DIAMONDSYMBOL + "*", geoSource,
//             		    					mxConstants.LABEL_NORMAL);
//             		    			geoSource.setRelative(true);
//             		    			sourceLabel.setVertex(true);
//             		    			sourceLabel.getAttributes().put(mxConstants.TYPE, mxConstants.LABLE);
//             		    			graphComponent.getGraph().addCell(sourceLabel, newCell);
//
//             		    			//close to the end of edges
//             		    			mxGeometry geoTarget = new mxGeometry(targetLabelPosition, targetLabelPosition, 0, 0);
//             		    			mxCell targetLabel = new mxCell(mxConstants.SQUARESYMBOL + "*" + mxConstants.DIAMONDSYMBOL + "*", geoTarget,
//             		    					mxConstants.LABEL_NORMAL);
//             		    			geoTarget.setRelative(true);
//             		    			targetLabel.setVertex(true);
//             		    			targetLabel.getAttributes().put(mxConstants.TYPE, mxConstants.LABLE);
//             		    			graphComponent.getGraph().addCell(targetLabel, newCell);
//
//             		    			//add the connector
//             		    			mxGeometry middlePositon = new mxGeometry(connectorX, 0, connectorSize, connectorSize);
//             		    			mxCell middleConnecor = new mxCell(" ", middlePositon, mxConstants.CONNECTOR_TRANSPARENT);
//             		    			middleConnecor.getAttributes().put(mxConstants.TYPE, mxConstants.CONNECTOR);
//             		    			middlePositon.setRelative(true);
//             		    			middleConnecor.setVertex(true);
//             		    			graphComponent.getGraph().addCell(middleConnecor, newCell);
//
//                                	graphComponent.getGraph().getModel().setStyle(newCell, mxConstants.CLASSRELATION);
//                                	graphComponent.getGraph().refresh();
//                                }
//                                //aoc relation
//                                else if(sourceType.equals(mxConstants.CLASSCELL) && targetType.equals(mxConstants.ACTIVITYCELL) ||
//                                		sourceType.equals(mxConstants.ACTIVITYCELL) && targetType.equals(mxConstants.CLASSCELL)){
//                                	newCell.getAttributes().put(mxConstants.TYPE, mxConstants.AOCRELATION);
//                                   	newCell.getAttributes().put(mxConstants.SOURCEALWAYSCARDINALITY, "*");
//                                	newCell.getAttributes().put(mxConstants.SOURCEEVENTUALLYCARDINALITY, "*");
//                                	newCell.getAttributes().put(mxConstants.TARGETALWAYSCARDINALITY, "*");
//
//
//                                	System.out.println("create AOCRelation");
//                        			System.out.println(mxConstants.DIAMONDSYMBOL);
//
//                        			mxGeometry geoSource = new mxGeometry(souceLabelPosition, souceLabelPosition, 0, 0);
//             		    			mxGeometry geoTarget = new mxGeometry(targetLabelPosition, targetLabelPosition, 0, 0);
//            		    			geoTarget.setRelative(true);
//            		    			geoSource.setRelative(true);
//
//             		    			mxCell sourceLabel;
//             		    			mxCell targetLabel;
//
//             		    			if(!sourceType.equals(mxConstants.ACTIVITYCELL)){
//             		    				newCell.setSource(targetCell);
//             		    				newCell.setTarget(sourceCell);
//             		    			}
//
//         		    				sourceLabel = new mxCell(mxConstants.SQUARESYMBOL + "*" + mxConstants.DIAMONDSYMBOL +"*", geoSource,
//         		    						mxConstants.LABEL_NORMAL);
//         		    				targetLabel = new mxCell("*", geoTarget,
//         		    						mxConstants.LABEL_NORMAL);
//         		    				sourceLabel.setVertex(true);
//         		    				targetLabel.setVertex(true);
//         		    				//the first cell added into the graph is the label close to the activity side
//         		    				graphComponent.getGraph().addCell(sourceLabel, newCell);
//             		    			graphComponent.getGraph().addCell(targetLabel, newCell);
//
//               		    			sourceLabel.getAttributes().put(mxConstants.TYPE, mxConstants.LABLE);
//               		    			targetLabel.getAttributes().put(mxConstants.TYPE, mxConstants.LABLE);
//                                	//AOC relation
//                                	graphComponent.getGraph().getModel().setStyle(newCell, mxConstants.AOCRELATION);
//                                	String style = graphComponent.getGraph().getModel().getStyle(newCell);
////                                	String style = newCell.getStyle();
//                                	System.out.println("aoc styleName: " + style);
//                                	graphComponent.getGraph().refresh();
//                                }
//                                //activity relation
//                                else if(sourceType.equals(mxConstants.ACTIVITYCELL) && targetType.equals(mxConstants.ACTIVITYCELL)){
//                                	newCell.getAttributes().put(mxConstants.TYPE, mxConstants.ACTIVITYRELATION);
//                                	newCell.getAttributes().put(mxConstants.CARDINALITYTYPE, mxConstants.UNARYRESPONSE);
//
//                                	graphComponent.getGraph().getModel().beginUpdate();
//                                	mxGeometry middlePositon = new mxGeometry(connectorX, 0, connectorSize, connectorSize);
//             		    			mxCell middleConnecor = new mxCell(" ", middlePositon, mxConstants.CONNECTOR_TRANSPARENT);
//             		    			middleConnecor.getAttributes().put(mxConstants.TYPE, mxConstants.CONNECTOR);
//             		    			middlePositon.setRelative(true);
//             		    			middleConnecor.setVertex(true);
//             		    			graphComponent.getGraph().addCell(middleConnecor, newCell);
//
//
//                                	System.out.println("create ActivityRelation");
////                                	System.out.println("type: " + newCell.getAttributes().get("type"));
//                                	//activity relation
//                                	graphComponent.getGraph().getModel().setStyle(newCell, mxConstants.UNARYRESPONSE);
//                                	graphComponent.getGraph().refresh();
//                                	graphComponent.getGraph().getModel().endUpdate();
//                                }
//
//                    			//crel rt relation (two nodes are of connector types and
//                                //one's father is of class edge type and another one is of activity edge type)
//                                else if(sourceType.equals(mxConstants.CONNECTOR) && targetType.equals(mxConstants.CONNECTOR) &&
//                                		(!sourceParentType.equals(targetParentType))){
//                                	//make sure the crel relation is from activity edge (source) to class/class edge
//                                	if(!sourceParentType.equals(mxConstants.ACTIVITYRELATION)){
//                                		newCell.setSource(targetCell);
//             		    				newCell.setTarget(sourceCell);
//                                	}
//                                	newCell.getAttributes().put(mxConstants.TYPE, mxConstants.CRELRTRELATION);
//                                	System.out.println("create connect rt relation");
//                                	graphComponent.getGraph().getModel().setStyle(newCell, mxConstants.CRELRELATION);
//                                	graphComponent.getGraph().refresh();
//                                }
//
//                                //crel oc relation
//                                else if((sourceType.equals(mxConstants.CONNECTOR) && sourceParentType.equals(mxConstants.ACTIVITYRELATION) && targetType.equals(mxConstants.CLASSCELL))
//                                		|| (sourceType.equals(mxConstants.CLASSCELL) && targetParentType.equals(mxConstants.ACTIVITYRELATION) && targetType.equals(mxConstants.CONNECTOR))){
//                                	//make sure the crel relation is from activity edge (source) to class/class edge
//                                	if(!sourceParentType.equals(mxConstants.ACTIVITYRELATION)){
//                                		newCell.setSource(targetCell);
//             		    				newCell.setTarget(sourceCell);
//                                	}
//                                	newCell.getAttributes().put(mxConstants.TYPE, mxConstants.CRELOCRELATION);
//                                	System.out.println("create connect oc relation");
//                                	graphComponent.getGraph().getModel().setStyle(newCell, mxConstants.CRELRELATION);
//                                	graphComponent.getGraph().refresh();
//                                }
//                                //forbid other relations
//                                else{
//                                	graphComponent.getGraph().removeCells(new Object[]{newCell});
//                                	graphComponent.getGraph().refresh();
//                                }
//         		    			graphComponent.validateGraph();
//                        	}
//                    	}
//                    }
//                }
//           }
//		}
//	};
//     */
//
//	/**
//	 *
//	 */
//	public BasicGraphPanel(String appTitle, ResultModelView resultModelView, mxGraphComponent component)
//	{
//		// Stores and updates the frame title
//		this.appTitle = appTitle;
//
//		// Stores and updates the (father) result model view
//		this.resultModelView = resultModelView;
//
//		// Stores a reference to the graph and creates the command history
//		graphComponent = component;
//
//
//		final mxGraph graph = graphComponent.getGraph();
//		undoManager = createUndoManager();
//
//		//add Guangming's style
//		Style.createStyle(graph);
//		graph.setAllowDanglingEdges(false);
//		graph.setSplitEnabled(false);
//		graph.setDropEnabled(false);
////		graph.setCellsMovable(false);
////		graph.setVertexLabelsMovable(true);
//		graph.setCellsEditable(false);
//		graphComponent.setConnectable(false);
//
//		// Do not change the scale and translation after files have been loaded
//		graph.setResetViewOnRootChange(false);
//
//		// Updates the modified flag if the graph model changes
//		GraphChangeListener graphChangeListener = new GraphChangeListener(this);
//		changeTracker = graphChangeListener.getChangeTracker();
//		graph.getModel().addListener(mxEvent.CHANGE, changeTracker);
//
//		// Adds the command history to the model and view
//		graph.getModel().addListener(mxEvent.UNDO, undoHandler);
//		graph.getView().addListener(mxEvent.UNDO, undoHandler);
//
//		// Keeps the selection in sync with the command history
//		mxIEventListener undoHandler = new mxIEventListener()
//		{
//			public void invoke(Object source, mxEventObject evt)
//			{
//				List<mxUndoableChange> changes = ((mxUndoableEdit) evt
//						.getProperty("edit")).getChanges();
//				graph.setSelectionCells(graph
//						.getSelectionCellsForChanges(changes));
//			}
//		};
//
//		undoManager.addListener(mxEvent.UNDO, undoHandler);
//		undoManager.addListener(mxEvent.REDO, undoHandler);
//
//		//creates the attribute component of classes
//		AttributePanel attributePanel = new AttributePanel(graphComponent);
//
//		// Creates the graph outline component
//		graphOutline = new mxGraphOutline(graphComponent);
//
//		JSplitPane leftBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT,attributePanel, graphOutline);
//		leftBottom.setDividerLocation(320);
//		leftBottom.setResizeWeight(1);
//		leftBottom.setDividerSize(6);
//		leftBottom.setBorder(null);
//
//		// Creates the library pane that contains the tabs with the palettes
//		libraryPane = new JTabbedPane();
//
//		// Creates the inner split pane that contains the library with the
//		// palettes and the graph outline on the left side of the window
//
//		JSplitPane inner = new JSplitPane(JSplitPane.VERTICAL_SPLIT, libraryPane, leftBottom);
//		inner.setDividerLocation(320);
//		inner.setResizeWeight(1);
//		inner.setDividerSize(6);
//		inner.setBorder(null);
//
//		// Creates the outer split pane that contains the inner split pane and
//		// the graph component on the right side of the window
//		JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inner,
////				multipleGraphPanel);
//				graphComponent);
//		outer.setOneTouchExpandable(true);
//		outer.setDividerLocation(200);
//		outer.setDividerSize(6);
//		outer.setBorder(null);
//
//		// Creates the status bar
//		statusBar = createStatusBar();
//
//		// Display some useful information about repaint events
//		installRepaintListener();
//
//		// Puts everything together
//		setLayout(new BorderLayout());
//		add(outer, BorderLayout.CENTER);
//		add(statusBar, BorderLayout.SOUTH);
////		installToolBar();
//
//		// Installs rubberband selection and handling for some special
//		// keystrokes such as F2, Control-C, -V, X, A etc.
//		installHandlers();
//		installListeners();
//		updateTitle();
//		//set the default horizontal page count as 2
//		graphComponent.setHorizontalPageCount(2);
////		graphComponent.zoomAndCenter();
//	}
//
//	protected  void addListenersToGraphComponent(mxGraphComponent graphComponent){
//		final mxGraph graph = graphComponent.getGraph();
//		System.out.println("enter add listeners");
//		// Do not change the scale and translation after files have been loaded
//		graph.setResetViewOnRootChange(false);
//
//		// Updates the modified flag if the graph model changes
//		graph.getModel().addListener(mxEvent.CHANGE, changeTracker);
//
//		// Adds the command history to the model and view
//		graph.getModel().addListener(mxEvent.UNDO, undoHandler);
//		graph.getView().addListener(mxEvent.UNDO, undoHandler);
//
//		// Keeps the selection in sync with the command history
//		mxIEventListener undoHandler = new mxIEventListener()
//		{
//			public void invoke(Object source, mxEventObject evt)
//			{
//				List<mxUndoableChange> changes = ((mxUndoableEdit) evt
//						.getProperty("edit")).getChanges();
//				graph.setSelectionCells(graph
//						.getSelectionCellsForChanges(changes));
//			}
//		};
//
//		undoManager.addListener(mxEvent.UNDO, undoHandler);
//		undoManager.addListener(mxEvent.REDO, undoHandler);
//
//		// Display some useful information about repaint events
//		installRepaintListener();
//
//
//		// Installs rubberband selection and handling for some special
//		// keystrokes such as F2, Control-C, -V, X, A etc.
//		installHandlers();
//		installListeners();
//		updateTitle();
//	}
//
//
//	public void updateEditor(){
//    	graphComponent.getGraph().getModel().beginUpdate();
//    	graphComponent.getGraph().getModel().endUpdate();
//		this.updateUI();
//	}
//
//	//refresh the graph model, such as labels. It is the same as the refreshGraph() in BasicGraphEditor.java
//	public void refreshGraph(){
//		System.out.println("refresh");
//		mxGraph graph = graphComponent.getGraph();
//		Object[] edgeCells = graph.getChildEdges(graph.getDefaultParent());
//		Object[] nodeCells = graph.getChildVertices(graph.getDefaultParent());
//		for(int j=0; j<nodeCells.length; j++){
//			mxCell childCell = (mxCell) nodeCells[j];
//			String type = childCell.getAttributes().get(mxConstants.TYPE);
//			System.out.println("type" + type);
////			if(type.equals(mxConstants.CLASSCELL)){
////				if(!childCell.getAttributes().containsKey(mxConstants.ROWID)){
////					//assume the primary id is "rowid";could be changed in future
////					childCell.getAttributes().put(mxConstants.ROWID, mxConstants.PK + "-" + "rowid");
////				}
////				if(!childCell.getAttributes().containsKey(mxConstants.TABLE)){
////					//assume the primary id is "rowid";could be changed in future
////					childCell.getAttributes().put(mxConstants.TABLE, "table" + "-" + "");
////				}
////			}
////			else if(type.equals(mxConstants.ACTIVITYCELL)){
////				if(!childCell.getAttributes().containsKey(mxConstants.ACTIVITY)){
////					//assume the primary id is "rowid";could be changed in future
////					childCell.getAttributes().put(mxConstants.ACTIVITY, "column" + "-" + "name");
////				}
////				if(!childCell.getAttributes().containsKey(mxConstants.TABLE)){
////					//assume the primary id is "rowid";could be changed in future
////					childCell.getAttributes().put(mxConstants.TABLE, "table" + "-" + "");
////				}
////
////			}
//		}
//
//		for(int j=0; j<edgeCells.length; j++){
//			mxCell childCell = (mxCell) edgeCells[j];
//			String type = childCell.getAttributes().get(mxConstants.TYPE);
//			//update class relations
//			if(type.equals(mxConstants.CLASSRELATION)){
//				System.out.println("update the label of class relations");
//				//update the cardinality label of class relations
//				mxCell sourceCellChildAlways = (mxCell) childCell.getChildAt(0);
//				mxCell sourceCellChildEventually = (mxCell) childCell.getChildAt(1);
//				mxCell targetCellChildAlways = (mxCell) childCell.getChildAt(2);
//				mxCell targetCellChildEventually = (mxCell) childCell.getChildAt(3);
//				String sourceAlwaysCardinality = childCell.getAttributes().get(mxConstants.SOURCEALWAYSCARDINALITY);
//				String sourceEventuallyCardinality = childCell.getAttributes().get(mxConstants.SOURCEEVENTUALLYCARDINALITY);
//				String targetAlwaysCardinality = childCell.getAttributes().get(mxConstants.TARGETALWAYSCARDINALITY);
//				String targetEventuallyCardinality = childCell.getAttributes().get(mxConstants.TARGETEVENTUALLYCARDINALITY);
//				sourceCellChildAlways.setValue(mxConstants.SQUARESYMBOL + sourceAlwaysCardinality);
//				sourceCellChildEventually.setValue(mxConstants.DIAMONDSYMBOL + sourceEventuallyCardinality);
//				targetCellChildAlways.setValue(mxConstants.SQUARESYMBOL + targetAlwaysCardinality);
//				targetCellChildEventually.setValue(mxConstants.DIAMONDSYMBOL + targetEventuallyCardinality);
//
//				//refresh the edge name in the class relationship type
//				mxCell sourceCell = (mxCell) childCell.getSource();
//				mxCell targetCell = (mxCell) childCell.getTarget();
//				String edgeName = (String) childCell.getValue();
//				childCell.getAttributes().put(mxConstants.RELATIONSHIPTYPE, sourceCell.getValue() + "-" + edgeName + "-" + targetCell.getValue());
//				//refresh or add the fk attribute in the target class
//				targetCell.getAttributes().put(edgeName, mxConstants.FK + "-" + sourceCell.getValue());
//        	}
//			else if(type.equals(mxConstants.AOCRELATION)){
//				System.out.println("update aoc relations");
//				//refresh or add the activity attribute in the activity node and class node
//				mxCell sourceCell = (mxCell) childCell.getSource();//activity node
//				mxCell targetCell = (mxCell) childCell.getTarget();//class node
//				mxCell sourceCellChildAlways = (mxCell) childCell.getChildAt(0);
//				mxCell sourceCellChildEventually = (mxCell) childCell.getChildAt(1);
//				mxCell targetCellChildAlways = (mxCell) childCell.getChildAt(2);
//				String sourceAlwaysCardinality = childCell.getAttributes().get(mxConstants.SOURCEALWAYSCARDINALITY);
//				String sourceEventuallyCardinality = childCell.getAttributes().get(mxConstants.SOURCEEVENTUALLYCARDINALITY);
//				String targetAlwaysCardinality = childCell.getAttributes().get(mxConstants.TARGETALWAYSCARDINALITY);
//
//				sourceCellChildAlways.setValue(mxConstants.SQUARESYMBOL + sourceAlwaysCardinality);
//				sourceCellChildEventually.setValue(mxConstants.DIAMONDSYMBOL + sourceEventuallyCardinality);
//				targetCellChildAlways.setValue(targetAlwaysCardinality);
//
//				String edgeName = (String) childCell.getValue();
//				String activityName = (String) sourceCell.getValue();
//				String activityTableName = sourceCell.getAttributes().get(mxConstants.TABLE);
//				String classTableName = targetCell.getAttributes().get(mxConstants.TABLE);
////				//the edge name is empty; currently it indicate it is not a source aoc relation
////				if(edgeName == null || edgeName.equals("")){
////
////				}
//				System.out.println("update activity and class based on aoc relations");
//				System.out.println("classTableName: " + classTableName);
//				sourceCell.getAttributes().put(mxConstants.TABLE, classTableName + "-" + classTableName);
//				sourceCell.getAttributes().put(mxConstants.ACTIVITY, edgeName + "-" + activityName);
//				targetCell.getAttributes().put(edgeName, mxConstants.ACTIVITY + "-" + activityName);
//			}
//			graph.refresh();
//		}
//	}
//
//
//	/**
//	 *
//	 */
//	protected mxUndoManager createUndoManager()
//	{
//		return new mxUndoManager();
//	}
//
//	/**
//	 *
//	 */
//	protected void installHandlers()
//	{
//		rubberband = new mxRubberband(graphComponent);
//		keyboardHandler = new EditorKeyboardHandler(graphComponent);
//	}
//
//	/**
//	 *
//	 */
////	protected void installToolBar()
////	{
////		EditorToolBar editorToolBar = new EditorToolBar(this, JToolBar.HORIZONTAL);
////		toolBar = editorToolBar;
////		toolBar.getCardinalityTypeBox().setEnabled(false);
////		toolBar.getCardinalitySourceAlwaysBox().setEnabled(false);
////		toolBar.getCardinalitySourceEventuallyBox().setEnabled(false);
////		toolBar.getCardinalityTargetAlwaysBox().setEnabled(false);
////		toolBar.getCardinalityTargetEventuallyBox().setEnabled(false);
//////		editorToolBar.getZoomBox().setSelectedIndex(2);
////		add(editorToolBar, BorderLayout.NORTH);
////	}
//
//	/**
//	 *
//	 */
//	protected JLabel createStatusBar()
//	{
//		JLabel statusBar = new JLabel(mxResources.get("ready"));
//		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
//
//		return statusBar;
//	}
//
//	/**
//	 *
//	 */
//	protected void installRepaintListener()
//	{
//		graphComponent.getGraph().addListener(mxEvent.REPAINT,
//				new mxIEventListener()
//				{
//					public void invoke(Object source, mxEventObject evt)
//					{
//						String buffer = (graphComponent.getTripleBuffer() != null) ? ""
//								: " (unbuffered)";
//						mxRectangle dirty = (mxRectangle) evt
//								.getProperty("region");
//
//						if (dirty == null)
//						{
//							status("Repaint all" + buffer);
//						}
//						else
//						{
//							status("Repaint: x=" + (int) (dirty.getX()) + " y="
//									+ (int) (dirty.getY()) + " w="
//									+ (int) (dirty.getWidth()) + " h="
//									+ (int) (dirty.getHeight()) + buffer);
//						}
//					}
//				});
//	}
//
//	/**
//	 *
//	 */
//	public EditorPalette insertPalette(String title)
//	{
//		final EditorPalette palette = new EditorPalette();
//		final JScrollPane scrollPane = new JScrollPane(palette);
//		scrollPane
//				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		scrollPane
//				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		libraryPane.add(title, scrollPane);
//
//		// Updates the widths of the palettes if the container size changes
//		libraryPane.addComponentListener(new ComponentAdapter()
//		{
//			/**
//			 *
//			 */
//			public void componentResized(ComponentEvent e)
//			{
//				int w = scrollPane.getWidth()
//						- scrollPane.getVerticalScrollBar().getWidth();
//				palette.setPreferredWidth(w);
//			}
//
//		});
//
//		return palette;
//	}
//
//	/**
//	 *
//	 */
//	protected void mouseWheelMoved(MouseWheelEvent e)
//	{
//		if (e.getWheelRotation() < 0)
//		{
//			graphComponent.zoomIn();
//		}
//		else
//		{
//			graphComponent.zoomOut();
//		}
//
//		status(mxResources.get("scale") + ": "
//				+ (int) (100 * graphComponent.getGraph().getView().getScale())
//				+ "%");
//	}
//
//	/**
//	 *
//	 */
//	protected void showOutlinePopupMenu(MouseEvent e)
//	{
//		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
//				graphComponent);
//		JCheckBoxMenuItem item = new JCheckBoxMenuItem(
//				mxResources.get("magnifyPage"));
//		item.setSelected(graphOutline.isFitPage());
//
//		item.addActionListener(new ActionListener()
//		{
//			/**
//			 *
//			 */
//			public void actionPerformed(ActionEvent e)
//			{
//				graphOutline.setFitPage(!graphOutline.isFitPage());
//				graphOutline.repaint();
//			}
//		});
//
//		JCheckBoxMenuItem item2 = new JCheckBoxMenuItem(
//				mxResources.get("showLabels"));
//		item2.setSelected(graphOutline.isDrawLabels());
//
//		item2.addActionListener(new ActionListener()
//		{
//			/**
//			 *
//			 */
//			public void actionPerformed(ActionEvent e)
//			{
//				graphOutline.setDrawLabels(!graphOutline.isDrawLabels());
//				graphOutline.repaint();
//			}
//		});
//
//		JCheckBoxMenuItem item3 = new JCheckBoxMenuItem(
//				mxResources.get("buffering"));
//		item3.setSelected(graphOutline.isTripleBuffered());
//
//		item3.addActionListener(new ActionListener()
//		{
//			/**
//			 *
//			 */
//			public void actionPerformed(ActionEvent e)
//			{
//				graphOutline.setTripleBuffered(!graphOutline.isTripleBuffered());
//				graphOutline.repaint();
//			}
//		});
//
//		JPopupMenu menu = new JPopupMenu();
//		menu.add(item);
//		menu.add(item2);
//		menu.add(item3);
//		menu.show(graphComponent, pt.x, pt.y);
//
//		e.consume();
//	}
//
//	/**
//	 *
//	 */
////	protected void showGraphPopupMenu(MouseEvent e)
////	{
////		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
////				graphComponent);
////		EditorPopupMenu menu = new EditorPopupMenu(BasicGraphPanel.this);
////		System.out.println("show pop up menu");
////		menu.show(graphComponent, pt.x, pt.y);
////
////		e.consume();
////	}
//
//	/**
//	 *
//	 */
//	protected void mouseLocationChanged(MouseEvent e)
//	{
//		status(e.getX() + ", " + e.getY());
//	}
//
//	/**
//	 *
//	 */
//	protected void installListeners()
//	{
//
//		// Installs mouse wheel listener for zooming
//		MouseWheelListener wheelTracker = new MouseWheelListener()
//		{
//			/**
//			 *
//			 */
//			public void mouseWheelMoved(MouseWheelEvent e)
//			{
//				if (e.getSource() instanceof mxGraphOutline
//						|| e.isControlDown())
//				{
//					BasicGraphPanel.this.mouseWheelMoved(e);
//				}
//			}
//
//		};
//
//		// Handles mouse wheel events in the outline and graph component
//		graphOutline.addMouseWheelListener(wheelTracker);
//		graphComponent.addMouseWheelListener(wheelTracker);
//
//		// Installs the popup menu in the outline
//		graphOutline.addMouseListener(new MouseAdapter()
//		{
//
//			/**
//			 *
//			 */
//			public void mousePressed(MouseEvent e)
//			{
//				// Handles context menu on the Mac where the trigger is on mousepressed
//				mouseReleased(e);
//			}
//
//			/**
//			 *
//			 */
//			public void mouseReleased(MouseEvent e)
//			{
//				if (e.isPopupTrigger())
//				{
//					showOutlinePopupMenu(e);
//				}
//
//			}
//
//		});
//
//
//
//
//
//
//		// Installs a mouse motion listener to display the mouse location
//		graphComponent.getGraphControl().addMouseMotionListener(
//				new MouseMotionListener()
//				{
//
//					/*
//					 * (non-Javadoc)
//					 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
//					 */
//					public void mouseDragged(MouseEvent e)
//					{
//						System.out.println("graphComponent draged");
//						mouseLocationChanged(e);
//
////	            	    int dragEndX = e.getX();
////	            	    int dragEndY = e.getY();
////
////
////	            	    e.getComponent().setLocation(panelX + dragEndX - dragStartX, panelY + dragEndY - dragStartY);
////	    				System.out.println("graphComponent dragEndX: " + dragEndX);
////	    				System.out.println("graphComponent dragEndY: " + dragEndY);
////
////
////	    				System.out.println("graphComponent draged X: " + panelX);
////	    				System.out.println("graphComponent draged Y: " + panelY);
//
////						if(!cellSelectedFlag){
////		                    final Component t = e.getComponent();
////		                    mxGraph graph = graphComponent.getGraph();
////		                    e.translatePoint(getLocation().x + t.getLocation().x - dragStartX, getLocation().y + t.getLocation().y - dragStartY);
//////		                    graphComponent.setLocation(e.getX(), e.getY());
////		                    graphComponent.getGraphControl().setLocation(e.getX(), e.getY());
//////		                    graphOutline.setLocation(e.getX(), e.getY());
//////		                    e.getComponent().setLocation(e.getX(), e.getY());
////
////		                    Object[] nodes = graph.getChildVertices(graph.getDefaultParent());
////		                    for(int i=0; i<nodes.length; i++){
////		                    	mxCell cell = (mxCell) nodes[i];
////		                    	mxGeometry geo = cell.getGeometry();
////		                    	double newX = geo.getX() + e.getX() - t.getLocation().x;
////		                    	double newY = geo.getY() + e.getY() - t.getLocation().y;;
////		                    	double width = geo.getWidth();
////		                    	double height = geo.getHeight();
////
////		                    	mxGeometry geoNew = new mxGeometry(newX, newY, width,height);
////
////		                    	cell.setGeometry(geoNew);
////		                    }
////						}
//
//					}
//
//					/*
//					 * (non-Javadoc)
//					 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
//					 */
//					public void mouseMoved(MouseEvent e)
//					{
////						System.out.println("moved");
////						mouseDragged(e);
//					}
//
//				});
//	}
//
//
//
//	public void blackAllLabels(){
//		Object[] childCells = graphComponent.getGraph().getChildCells(graphComponent.getGraph().getDefaultParent());
//		for(int i=0; i<childCells.length; i++){
//			Object[] labelCells = graphComponent.getGraph().getChildCells(childCells[i]);
////			System.out.println("child cell type: " + ((mxCell)childCells[i]).getAttributes().get(mxConstants.TYPE));
//			for(int j=0; j<labelCells.length; j++){
////				System.out.println("black child cell");
//				mxCell labelCell = (mxCell) labelCells[j];
//				String cellType = labelCell.getAttributes().get(mxConstants.TYPE);
////				System.out.println("cell type: " + cellType);
//				if(cellType != null && cellType.equals(mxConstants.LABLE)){
//					System.out.println("label style : " + labelCell.getStyle());
//					Style.addStyleElementToCell(labelCell, mxConstants.STYLE_FONTCOLOR, "black");
//				}
//			}
//		}
//		graphComponent.getGraph().refresh();
//	}
//	/**
//	 *
//	 */
//	public void setCurrentFile(File file)
//	{
//		File oldValue = currentFile;
//		currentFile = file;
//
//		firePropertyChange("currentFile", oldValue, file);
//
//		if (oldValue != file)
//		{
//			updateTitle();
//		}
//	}
//
//	/**
//	 *
//	 */
//	public File getCurrentFile()
//	{
//		return currentFile;
//	}
//
//	/**
//	 *
//	 */
//	public void setGraphComponentFile(mxGraphComponent graphComponent, File file)
//	{
//		File oldValue = graphComponentVSFileMap.get(graphComponent);
//		if(oldValue == null){
//			graphComponentVSFileMap.put(graphComponent, file);
//		}
//		else{
//			graphComponentVSFileMap.remove(graphComponent);
//			graphComponentVSFileMap.put(graphComponent, file);
//		}
//	}
//
//	/**
//	 *
//	 */
//	public File getGraphComponentFile(mxGraphComponent graphComponent)
//	{
//		return graphComponentVSFileMap.get(graphComponent);
//	}
//
//	/**
//	 *
//	 * @param modified
//	 */
//	public void setModified(boolean modified)
//	{
//		boolean oldValue = this.modified;
//		this.modified = modified;
//
//		firePropertyChange("modified", oldValue, modified);
//
//		if (oldValue != modified)
//		{
//			updateTitle();
//		}
//	}
//
//	/**
//	 *
//	 * @return whether or not the current graph has been modified
//	 */
//	public boolean isModified()
//	{
//		return modified;
//	}
//
//	/**
//	 *
//	 */
//	public mxGraphComponent getGraphComponent()
//	{
//		return graphComponent;
//	}
//
//	/**
//	 *
//	 */
//	public void setGraphComponent(mxGraphComponent graphComponent)
//	{
//		this.graphComponent = graphComponent;
//	}
//
//	/**
//	 *
//	 */
//	public PluginContext getContext()
//	{
//		return context;
//	}
//
//	/**
//	 *
//	 */
//	public void setContext(PluginContext context)
//	{
//		this.context = context;
//	}
//
//	/**
//	 * create a panel to hold multiple graph components
//	 */
////	public JTabbedPane getMultipleGraphPanel()
////	{
////		return multipleGraphPanel;
////	}
//
//	/**
//	 *
//	 */
//	public mxGraphOutline getGraphOutline()
//	{
//		return graphOutline;
//	}
//
//	/**
//	 *
//	 */
//	public JTabbedPane getLibraryPane()
//	{
//		return libraryPane;
//	}
//
//	/**
//	 *
//	 */
//	public mxUndoManager getUndoManager()
//	{
//		return undoManager;
//	}
//
//	/**
//	 *
//	 * @param name
//	 * @param action
//	 * @return a new Action bound to the specified string name
//	 */
//	public Action bind(String name, final Action action)
//	{
//		return bind(name, action, null);
//	}
//
//	/**
//	 *
//	 * @param name
//	 * @param action
//	 * @return a new Action bound to the specified string name and icon
//	 */
//	@SuppressWarnings("serial")
//	public Action bind(String name, final Action action, String iconUrl)
//	{
//		AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
//				BasicGraphPanel.class.getResource(iconUrl)) : null)
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				action.actionPerformed(new ActionEvent(getGraphComponent(), e
//						.getID(), e.getActionCommand()));
//			}
//		};
//
//		newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));
//
//		return newAction;
//	}
//
//	/**
//	 *
//	 * @param msg
//	 */
//	public void status(String msg)
//	{
//		statusBar.setText(msg);
//	}
//
//	/**
//	 *
//	 */
//	public void updateTitle()
//	{
//		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);
//
//		if (frame != null)
//		{
//			String title = (currentFile != null) ? currentFile
//					.getAbsolutePath() : mxResources.get("newDiagram");
//
//			if (modified)
//			{
//				title += "*";
//			}
//
//			frame.setTitle(title + " - " + appTitle);
//		}
//	}
//
//	/**
//	 *
//	 */
//	public void about()
//	{
//		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);
//
//		if (frame != null)
//		{
//			EditorAboutFrame about = new EditorAboutFrame(frame);
//			about.setModal(true);
//
//			// Centers inside the application frame
//			int x = frame.getX() + (frame.getWidth() - about.getWidth()) / 2;
//			int y = frame.getY() + (frame.getHeight() - about.getHeight()) / 2;
//			about.setLocation(x, y);
//
//			// Shows the modal dialog and waits
//			about.setVisible(true);
//		}
//	}
//
//	/**
//	 *
//	 */
//	public void exit()
//	{
//		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);
//
//		if (frame != null)
//		{
//			frame.dispose();
//		}
//	}
//
//	/**
//	 *
//	 */
//	public void setLookAndFeel(String clazz)
//	{
//		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);
//
//		if (frame != null)
//		{
//			try
//			{
//				UIManager.setLookAndFeel(clazz);
//				SwingUtilities.updateComponentTreeUI(frame);
//
//				// Needs to assign the key bindings again
//				keyboardHandler = new EditorKeyboardHandler(graphComponent);
//			}
//			catch (Exception e1)
//			{
//				e1.printStackTrace();
//			}
//		}
//	}
//
//	/**
//	 *
//	 */
//	public JFrame createFrame()
//	{
//		JFrame frame = new JFrame();
////		CustomGraphComponent graphComponent = new CustomGraphComponent(new CustomGraph());
////		frame.getContentPane().add(graphComponent);
//		frame.getContentPane().add(this);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////		frame.setJMenuBar(menuBar);
////		frame.setSize(870, 640);
//		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
//
//		// Updates the frame title
//		updateTitle();
//
//		return frame;
//	}
//
//	/**
//	 * Creates an action that executes the specified layout.
//	 *
//	 * @param key Key to be used for getting the label from mxResources and also
//	 * to create the layout instance for the commercial graph editor example.
//	 * @return an action that executes the specified layout
//	 */
//	@SuppressWarnings("serial")
//	public Action graphLayout(final String key, boolean animate)
//	{
//		final mxIGraphLayout layout = createLayout(key, animate);
//
//		if (layout != null)
//		{
//			return new AbstractAction(mxResources.get(key))
//			{
//				public void actionPerformed(ActionEvent e)
//				{
//					final mxGraph graph = graphComponent.getGraph();
//					Object cell = graph.getSelectionCell();
//
//					if (cell == null
//							|| graph.getModel().getChildCount(cell) == 0)
//					{
//						cell = graph.getDefaultParent();
//					}
//
//					graph.getModel().beginUpdate();
//					try
//					{
//						long t0 = System.currentTimeMillis();
//						layout.execute(cell);
//						status("Layout: " + (System.currentTimeMillis() - t0)
//								+ " ms");
//					}
//					finally
//					{
//						mxMorphing morph = new mxMorphing(graphComponent, 20,
//								1.2, 20);
//
//						morph.addListener(mxEvent.DONE, new mxIEventListener()
//						{
//
//							public void invoke(Object sender, mxEventObject evt)
//							{
//								graph.getModel().endUpdate();
//							}
//
//						});
//
//						morph.startAnimation();
//					}
//
//				}
//
//			};
//		}
//		else
//		{
//			return new AbstractAction(mxResources.get(key))
//			{
//
//				public void actionPerformed(ActionEvent e)
//				{
//					JOptionPane.showMessageDialog(graphComponent,
//							mxResources.get("noLayout"));
//				}
//
//			};
//		}
//	}
//
//	/**
//	 * Creates a layout instance for the given identifier.
//	 */
//	protected mxIGraphLayout createLayout(String ident, boolean animate)
//	{
//		mxIGraphLayout layout = null;
//
//		if (ident != null)
//		{
//			mxGraph graph = graphComponent.getGraph();
//
//			if (ident.equals("verticalHierarchical"))
//			{
//				layout = new mxHierarchicalLayout(graph);
//			}
//			else if (ident.equals("horizontalHierarchical"))
//			{
//				layout = new mxHierarchicalLayout(graph, JLabel.WEST);
//			}
//			else if (ident.equals("verticalTree"))
//			{
//				layout = new mxCompactTreeLayout(graph, false);
//			}
//			else if (ident.equals("horizontalTree"))
//			{
//				layout = new mxCompactTreeLayout(graph, true);
//			}
//			else if (ident.equals("parallelEdges"))
//			{
//				layout = new mxParallelEdgeLayout(graph);
//			}
//			else if (ident.equals("placeEdgeLabels"))
//			{
//				layout = new mxEdgeLabelLayout(graph);
//			}
//			else if (ident.equals("organicLayout"))
//			{
//				layout = new mxOrganicLayout(graph);
//			}
//			if (ident.equals("verticalPartition"))
//			{
//				layout = new mxPartitionLayout(graph, false)
//				{
//					/**
//					 * Overrides the empty implementation to return the size of the
//					 * graph control.
//					 */
//					public mxRectangle getContainerSize()
//					{
//						return graphComponent.getLayoutAreaSize();
//					}
//				};
//			}
//			else if (ident.equals("horizontalPartition"))
//			{
//				layout = new mxPartitionLayout(graph, true)
//				{
//					/**
//					 * Overrides the empty implementation to return the size of the
//					 * graph control.
//					 */
//					public mxRectangle getContainerSize()
//					{
//						return graphComponent.getLayoutAreaSize();
//					}
//				};
//			}
//			else if (ident.equals("verticalStack"))
//			{
//				layout = new mxStackLayout(graph, false)
//				{
//					/**
//					 * Overrides the empty implementation to return the size of the
//					 * graph control.
//					 */
//					public mxRectangle getContainerSize()
//					{
//						return graphComponent.getLayoutAreaSize();
//					}
//				};
//			}
//			else if (ident.equals("horizontalStack"))
//			{
//				layout = new mxStackLayout(graph, true)
//				{
//					/**
//					 * Overrides the empty implementation to return the size of the
//					 * graph control.
//					 */
//					public mxRectangle getContainerSize()
//					{
//						return graphComponent.getLayoutAreaSize();
//					}
//				};
//			}
//			else if (ident.equals("circleLayout"))
//			{
//				layout = new mxCircleLayout(graph);
//			}
//		}
//
//		return layout;
//	}
//
//	public static void main(String[] args) throws IOException{
//		FileInputStream input = new FileInputStream("D:\\otc.ocbcmml");
//		ImportOCBCModel importer = new ImportOCBCModel();
//		OCBCModel model = importer.generateOCBCModel(input);
//		OCBCModel  inputModel = (OCBCModel) model.clone();
////		inputModel.setModelEnvironment(mxConstants.conformance_checking_plugin);
//
//
//
//		BasicGraphPanel graphPanel = new BasicGraphPanel("mxGraph Editor", null, new CustomGraphComponent(new CustomGraph()));
//		mxGraph graph = graphPanel.getGraphComponent().getGraph();
//
//		//create my styles
//
//		mxGraph graphInput = inputModel.getGraphX();
//
//		for(Object objectCell : graphInput.getOffspringCells()){
//			mxCell cell = (mxCell) objectCell;
//			graph.addCell(cell);
//		}
////		mxCell[] cells =  (mxCell[]) graphInput.getOffspringCells().toArray();
//
//
////		BasicGraphPanel graphPanel = new BasicGraphPanel("mxGraph Editor", new CustomGraphComponent(model.getGraphX()));
//		graphPanel.createFrame().setVisible(true);;
//	}
//}

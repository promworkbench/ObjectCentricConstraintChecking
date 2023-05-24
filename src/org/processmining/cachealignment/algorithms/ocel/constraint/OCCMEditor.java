package org.processmining.cachealignment.algorithms.ocel.constraint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ProgressMonitor;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.processmining.cachealignment.algorithms.editor.ActEditorPalette;
import org.processmining.cachealignment.algorithms.editor.EditorAboutFrame;
import org.processmining.cachealignment.algorithms.editor.EditorKeyboardHandlerForOCCLEditor;
import org.processmining.cachealignment.algorithms.editor.EditorPopupMenu;
import org.processmining.cachealignment.algorithms.editor.PerformanceEditorPalette;
import org.processmining.cachealignment.algorithms.editor.objEditorPalette;
import org.processmining.cachealignment.algorithms.io.mxCodec;
import org.processmining.cachealignment.algorithms.layout.mxCircleLayout;
import org.processmining.cachealignment.algorithms.layout.mxCompactTreeLayout;
import org.processmining.cachealignment.algorithms.layout.mxEdgeLabelLayout;
import org.processmining.cachealignment.algorithms.layout.mxFastOrganicLayout;
import org.processmining.cachealignment.algorithms.layout.mxIGraphLayout;
import org.processmining.cachealignment.algorithms.layout.mxOrganicLayout;
import org.processmining.cachealignment.algorithms.layout.mxParallelEdgeLayout;
import org.processmining.cachealignment.algorithms.layout.mxPartitionLayout;
import org.processmining.cachealignment.algorithms.layout.mxStackLayout;
import org.processmining.cachealignment.algorithms.layout.hierarchical.mxHierarchicalLayout;
import org.processmining.cachealignment.algorithms.model.mxCell;
import org.processmining.cachealignment.algorithms.model.mxGeometry;
import org.processmining.cachealignment.algorithms.model.mxICell;
import org.processmining.cachealignment.algorithms.model.mxIGraphModel;
import org.processmining.cachealignment.algorithms.ocel.occl.GraphEditor;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEvent;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEventComparator;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEventLog;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelObject;
import org.processmining.cachealignment.algorithms.swing.mxGraphComponent;
import org.processmining.cachealignment.algorithms.swing.mxGraphOutline;
import org.processmining.cachealignment.algorithms.swing.handler.mxKeyboardHandlerForOCCLEditor;
import org.processmining.cachealignment.algorithms.swing.handler.mxRubberband;
import org.processmining.cachealignment.algorithms.swing.util.mxGraphTransferable;
import org.processmining.cachealignment.algorithms.swing.util.mxMorphing;
import org.processmining.cachealignment.algorithms.util.mxEvent;
import org.processmining.cachealignment.algorithms.util.mxEventObject;
import org.processmining.cachealignment.algorithms.util.mxEventSource;
import org.processmining.cachealignment.algorithms.util.mxPoint;
import org.processmining.cachealignment.algorithms.util.mxRectangle;
import org.processmining.cachealignment.algorithms.util.mxResources;
import org.processmining.cachealignment.algorithms.util.mxUndoManager;
import org.processmining.cachealignment.algorithms.util.mxUndoableEdit;
import org.processmining.cachealignment.algorithms.util.mxUtils;
import org.processmining.cachealignment.algorithms.util.svg.ParseException;
import org.processmining.cachealignment.algorithms.view.mxGraph;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.w3c.dom.Document;

public class OCCMEditor extends JPanel
{
	Timer timer;

	protected int selectedRow = -1;

	protected HashSet<String> selectedObjSet = new HashSet<>();


//	TimePerformance gpf = new TimePerformance();

	ConstraintModel cm = ConstraintModel.getInstance();

	static String timePerformanceCons;

	static String objTypeSelected;

	static ArrayList<String> objTypeList1;
	
	JComboBox<String> constraintJCB;
	/**
	 *
	 */
	private static final long serialVersionUID = -6561623072112577140L;

	/**
	 * Adds required resources for i18n
	 */
	static
	{
		try
		{
			mxResources.add("com/mxgraph/examples/swing/resources/editor");
		}
		catch (Exception e)
		{
			// ignore
		}
	}

	/**
	 *
	 */
	protected mxGraphComponent graphComponent;

	protected JPanel violationTablePanel = new JPanel();

	/**
	 *
	 */
	protected mxGraphOutline graphOutline;

	/**
	 *
	 */
	protected JTabbedPane objPane;
	protected JTabbedPane actPane;
	protected JTabbedPane performancePane;
	protected JTabbedPane restPane;
	protected JSplitPane outer3;
	protected JSplitPane resultPanel2;

	/**
	 *
	 */
	protected mxUndoManager undoManager;

	/**
	 *
	 */
	protected String appTitle;

	/**
	 *
	 */
	protected JLabel statusBar;

	/**
	 *
	 */
	protected File currentFile;

	/**
	 * Flag indicating whether the current graph has been modified
	 */
	protected boolean modified = false;

	/**
	 *
	 */
	protected mxRubberband rubberband;

	/**
	 *
	 */
	protected mxKeyboardHandlerForOCCLEditor keyboardHandler;


	/**
	 *
	 */
	protected mxEventSource.mxIEventListener undoHandler = new mxEventSource.mxIEventListener()
	{
		public void invoke(Object source, mxEventObject evt)
		{
			undoManager.undoableEditHappened((mxUndoableEdit) evt
					.getProperty("edit"));
		}
	};

	/**
	 *
	 */
	protected mxEventSource.mxIEventListener changeTracker = new mxEventSource.mxIEventListener()
	{
		public void invoke(Object source, mxEventObject evt)
		{
			setModified(true);
		}
	};

	/**
	 *
	 */

	public OCCMEditor(UIPluginContext context,
					  Map<String, Map> leadObjMap,
					  Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peMap,
					  OcelEventLog ocel,
					  ArrayList<String> objTypeList,
					  HashSet actList) {

		// Stores and updates the frame title
		this.appTitle = "Object-Centric Constraint Model Editor";
		objTypeList1 = objTypeList;
		CaseExtraction ce = new CaseExtraction();
		List<Map> map1= ce.extractCaseWithLeadingType(ocel, cm.leadObjType, cm.revObjTypes);
		cm.peMap = map1.get(3);

		// Stores a reference to the graph and creates the command history
		graphComponent = new CustomGraphComponent(new CustomGraph());
		graphComponent.setPageScale(1.5);
		final mxGraph graph = graphComponent.getGraph();

		// disallow the edge disconnecting with vertex.
		graph.setDisconnectOnMove(false);
		undoManager = createUndoManager();

		// Do not change the scale and translation after files have been loaded
		graph.setResetViewOnRootChange(false);

		// Updates the modified flag if the graph model changes
		graph.getModel().addListener(mxEvent.CHANGE, changeTracker);

		// Adds the command history to the model and view
		graph.getModel().addListener(mxEvent.UNDO, undoHandler);
		graph.getView().addListener(mxEvent.UNDO, undoHandler);

		// Keeps the selection in sync with the command history
		mxEventSource.mxIEventListener undoHandler = new mxEventSource.mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {
				List<mxUndoableEdit.mxUndoableChange> changes = ((mxUndoableEdit) evt
						.getProperty("edit")).getChanges();
				graph.setSelectionCells(graph
						.getSelectionCellsForChanges(changes));
			}
		};

		undoManager.addListener(mxEvent.UNDO, undoHandler);
		undoManager.addListener(mxEvent.REDO, undoHandler);

		// Creates the graph outline component, å�¯ä»¥æ‹½æ�¥æ‹½åŽ»
		graphOutline = new mxGraphOutline(graphComponent);

		Box hBox = Box.createHorizontalBox();
		JButton refreshBtn = new JButton("Refresh");
		JButton resetBtn = new JButton("Reset");
		JButton checkBtn = new JButton("Check");

		refreshBtn.setMaximumSize(new Dimension(80, 20));
		refreshBtn.setMinimumSize(new Dimension(80, 20));
		refreshBtn.setPreferredSize(new Dimension(80, 20));
		resetBtn.setMaximumSize(new Dimension(80, 20));
		resetBtn.setMinimumSize(new Dimension(80, 20));
		resetBtn.setPreferredSize(new Dimension(80, 20));
		checkBtn.setMaximumSize(new Dimension(80, 20));
		checkBtn.setMinimumSize(new Dimension(80, 20));
		checkBtn.setPreferredSize(new Dimension(80, 20));

		hBox.add(Box.createHorizontalStrut(10));
		hBox.add(refreshBtn);
		hBox.add(Box.createHorizontalStrut(10));
		hBox.add(resetBtn);
		hBox.add(Box.createHorizontalStrut(10));
		hBox.add(checkBtn);


		// -------- pane for process execution
		JTable table = getEvtTable(
				graphComponent.getGraph(),
				peMap,
				leadObjMap
		);
		JPanel jpObj = new JPanel();
		Integer rowNum = 2 + cm.allObjectTyps.size();
		jpObj.setLayout((new GridLayout(rowNum, 1, 0, 20)));
		JLabel jblHighlight = new JLabel("Highlight the selected object types:");
		Font font = new Font("Arial", Font.BOLD, 18);
		jblHighlight.setFont(font);
		jpObj.add(jblHighlight);
		HashSet<String> selectedTypes = new HashSet<>();
		selectedTypes.addAll(cm.revObjTypes);
		selectedTypes.add(cm.leadObjType);

		for (String obj:selectedTypes){
			JCheckBox jcb = new JCheckBox(obj);
			jcb.addItemListener(e -> {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					selectedObjSet.add(jcb.getText());
					if (selectedRow > -1){
						graph.getModel().beginUpdate();
						graph.selectAll();
						graph.removeCells();
						graph.getModel().endUpdate();
						Object parent = graph.getDefaultParent();
						String leadingObj = (String) table.getModel().getValueAt(
								selectedRow,
								0);

						// get all the node
						Set<OcelEvent> evtLst =  peMap.get(leadingObj).keySet();
						List<OcelEvent> mainList = new ArrayList<>();
						mainList.addAll(evtLst);
						Collections.sort(mainList, new OcelEventComparator());
						Map<String, Integer> edgeIdxToEvtId = new HashMap<>();
						HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>> evtDfMap = peMap.get(leadingObj);
						Set<OcelEvent> allEvt = new HashSet<>();
						for (OcelEvent evt : mainList) {
							allEvt.add(evt);
							HashMap<OcelEvent,HashSet<OcelObject>> dfEvtMap = evtDfMap.get(evt);
							for(OcelEvent dfEvt: dfEvtMap.keySet()) {
								allEvt.add(dfEvt);
							}
						}
						List<OcelEvent> allEvtList = new ArrayList<OcelEvent>();
						allEvtList.addAll(allEvt);
						Collections.sort(allEvtList, new OcelEventComparator());
						Object[] vertices = new Object[allEvt.size()];
						int i = 0;

						Map<OcelEvent,Integer> evtToNodeMap = new HashMap<>();
						// first get the node
						for (OcelEvent evt : allEvtList) {
							vertices[i] = graph.insertVertex(parent,
									evt.id,
									evt.activity+"\n"+evt.timestamp,
									200, 0,
									120,
									60,
									"ellipse;fontSize=12"
							);
							edgeIdxToEvtId.put(evt.id, i);
							evtToNodeMap.put(evt, i);
							i += 1;
						}

						// then get the edge, iterate every event
						for(OcelEvent evt:mainList){

							// get all direct follow events
							HashMap<OcelEvent,HashSet<OcelObject>> dfEvtMap = evtDfMap.get(evt);

							// add an edge them if there is a shared object
							for(OcelEvent dfEvt: dfEvtMap.keySet()) {
								HashSet<OcelObject> objSet = dfEvtMap.get(dfEvt);
								// iterate every obj

								int idx = 0;
								for (OcelObject objInSet:objSet) {
									if (cm.revObjTypes.contains(objInSet.objectType.name) ||
											objInSet.id.equals(leadingObj)) {
										if (!selectedObjSet.contains(objInSet.objectType.name)) {
											graph.insertEdge(parent,
													null,
													objInSet.id,
													vertices[evtToNodeMap.get(evt)],
													vertices[evtToNodeMap.get(dfEvt)],
													"startArrow=none;endArrow=classic;fontSize=8;strokeWidth=1");
										} else {
											graph.insertEdge(parent,
													null,
													objInSet.id,
													vertices[evtToNodeMap.get(evt)],
													vertices[evtToNodeMap.get(dfEvt)],
													"startArrow=none;endArrow=classic;fontSize=12;strokeWidth=3;" +
															"strokeColor=#330000;fontColor=#330000");
										}
										idx += 1;
									}
								}
							}
						}

						// set layout of the process exe
						mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
						layout.setParallelEdgeSpacing(120);
						layout.setIntraCellSpacing(150);
						layout.setInterRankCellSpacing(60);
						layout.setInterHierarchySpacing(120);
						layout.execute(graph.getDefaultParent());

						double width = graph.getGraphBounds().getWidth();
						double height = graph.getGraphBounds().getHeight();
						double widthLayout = graphComponent.getLayoutAreaSize().getWidth();
						double heightLayout = graphComponent.getLayoutAreaSize().getHeight();
						graph.getModel().setGeometry(graph.getDefaultParent(),
								new mxGeometry((widthLayout - width)/2,
										(heightLayout - height)/2,
										widthLayout, heightLayout));
					

						System.out.println("set the layout of p1"+graph.getView().getScale());
					    
						graph.getModel().endUpdate();
					
					
					}
				}
				else if (e.getStateChange() == ItemEvent.DESELECTED) {

					selectedObjSet.remove(jcb.getText());
					if (selectedRow > -1){
						graph.getModel().beginUpdate();
						graph.selectAll();
						graph.removeCells();
						Object parent = graph.getDefaultParent();
						String leadingObj = (String) table.getModel().getValueAt(selectedRow, 1);

						// get all the node
						Set<OcelEvent> evtLst =  peMap.get(leadingObj).keySet();
						List<OcelEvent> mainList = new ArrayList<OcelEvent>();
						mainList.addAll(evtLst);
						Collections.sort(mainList, new OcelEventComparator());
						Map<String, Integer> edgeIdxToEvtId = new HashMap<>();
						HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>> evtDfMap = peMap.get(leadingObj);
						Set<OcelEvent> allEvt = new HashSet<>();
						for (OcelEvent evt : mainList) {
							allEvt.add(evt);
							HashMap<OcelEvent,HashSet<OcelObject>> dfEvtMap = evtDfMap.get(evt);
							for(OcelEvent dfEvt: dfEvtMap.keySet()) {
								allEvt.add(dfEvt);
							}
						}
						List<OcelEvent> allEvtList = new ArrayList<OcelEvent>();
						allEvtList.addAll(allEvt);
						Collections.sort(allEvtList, new OcelEventComparator());
						Object[] vertices = new Object[allEvt.size()];
//				ArrayList<Object[]> vertices = new ArrayList<>();
						int i = 0;
						Map<OcelEvent,Integer> evtToNodeMap = new HashMap<>();
						// first get the node
						for (OcelEvent evt : allEvtList) {
							vertices[i] = graph.insertVertex(parent,
									evt.id,
									evt.activity+"\n"+evt.timestamp,
									200, 0,
									120,
									50,
									"ellipse;fontSize=12",
									"");
							edgeIdxToEvtId.put(evt.id, i);
							evtToNodeMap.put(evt, i);
							i += 1;
						}

						// then get the edge, iterate every event
						for(OcelEvent evt:mainList){

							// get all direct follow events
							HashMap<OcelEvent,HashSet<OcelObject>> dfEvtMap = evtDfMap.get(evt);

							// add an edge them if there is a shared object
							for(OcelEvent dfEvt: dfEvtMap.keySet()) {

								HashSet<OcelObject> objSet = dfEvtMap.get(dfEvt);
								// iterate every obj

								int idx = 0;
								for (OcelObject objInSet:objSet) {
									if (cm.revObjTypes.contains(objInSet.objectType.name) ||
											objInSet.id.equals(leadingObj)) {
										if (!selectedObjSet.contains(objInSet.objectType.name)) {
											graph.insertEdge(parent,
													null,
													objInSet.id,
													vertices[evtToNodeMap.get(evt)],
													vertices[evtToNodeMap.get(dfEvt)],
													"startArrow=none;endArrow=classic;fontSize=8;strokeWidth=1");
										} else {
											graph.insertEdge(parent,
													null,
													objInSet.id,
													vertices[evtToNodeMap.get(evt)],
													vertices[evtToNodeMap.get(dfEvt)],
													"startArrow=none;endArrow=classic;" +
															"fontSize=12;strokeWidth=3;" +
															"strokeColor=#330000;fontColor=#330000");
										}
										idx += 1;
									}
								}
							}
						}

						// set layout of the process exe
						mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
						layout.setParallelEdgeSpacing(120);
						layout.setIntraCellSpacing(150);
						layout.setInterRankCellSpacing(80);
						layout.setInterHierarchySpacing(120);
						System.out.println("set the layout of p2 "+graph.getView().getScale());

						double width = graph.getGraphBounds().getWidth();
						double height = graph.getGraphBounds().getHeight();
						double widthLayout = graphComponent.getLayoutAreaSize().getWidth();
						double heightLayout = graphComponent.getLayoutAreaSize().getHeight();
						graph.getModel().setGeometry(graph.getDefaultParent(),
								new mxGeometry((widthLayout - width)/2,
										(heightLayout - height)/2,
										widthLayout, heightLayout));
//						double widthLayout = graphComponent.getLayoutAreaSize().getWidth();
//						double heightLayout = graphComponent.getLayoutAreaSize().getHeight();
//						double width = mxGraph.getGraphBounds().getWidth();
//						double height = mxGraph.getGraphBounds().getHeight();
						graph.getModel().endUpdate();
					}

				}
			});
			jcb.setSelected(false);
			jpObj.add(jcb);
		}

		JScrollPane scrollPaneObj = new JScrollPane();
		scrollPaneObj.getViewport().add(jpObj);
		scrollPaneObj
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneObj
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(table);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JSplitPane innerLeft0 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				scrollPaneObj,
				scrollPane);
		innerLeft0.setResizeWeight(0.2);
		innerLeft0.setDividerSize(3);
		innerLeft0.setBorder(null);

		JSplitPane innerLeft1 = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT,
				innerLeft0,
				graphOutline);
		innerLeft1.setResizeWeight(0.2);
		innerLeft1.setDividerSize(3);
		innerLeft1.setBorder(null);
		// -------- pane for pe

		// ---------- library pane that contains the tabs with the palettes
		objPane = new JTabbedPane();
		actPane = new JTabbedPane();
		performancePane = new JTabbedPane();
		restPane = new JTabbedPane();

		JSplitPane inner0 = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT,
				objPane,
				actPane);
		inner0.setResizeWeight(0.5);
		inner0.setDividerSize(6);
		inner0.setBorder(null);

		JSplitPane inner1 = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT,
				inner0,
				performancePane);
		inner1.setResizeWeight(0.8);
		inner1.setDividerSize(6);
		inner1.setBorder(null);

		JSplitPane inner2 = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT,
				inner1,
				restPane);
		inner2.setResizeWeight(0.8);
		inner2.setDividerSize(6);
		inner2.setBorder(null);
		// Creates the inner split pane that contains the library with the
		// palettes and the graph outline on the left side of the window
		JSplitPane inner4 = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT,
				inner2,
				hBox);
		inner4.setResizeWeight(0.8);
		inner4.setDividerSize(6);
		inner4.setBorder(null);

		// Creates the outer split pane that contains the inner split pane and
		// the graph component on the right side of the window
		JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				innerLeft1,
				inner4);
		outer.setResizeWeight(0.5);
		outer.setDividerSize(6);
		outer.setBorder(null);
		outer.setOneTouchExpandable(true);

		JSplitPane outer2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				inner4,
				graphComponent);
//		outer.setOneTouchExpandable(true);
		outer2.setResizeWeight(0.2);
		outer2.setOneTouchExpandable(true);
		outer2.setDividerSize(25);
		outer2.setBorder(null);

		Box hBox1 = Box.createVerticalBox();
		constraintJCB = new JComboBox<String>();
		constraintJCB.setMaximumSize(new Dimension(100, 20));
		constraintJCB.setMinimumSize(new Dimension(100, 20));
		constraintJCB.setPreferredSize(new Dimension(100, 20));
		constraintJCB.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                // Perform actions when an item is selected in the JComboBox
	                JComboBox<String> source = (JComboBox<String>) e.getSource();
	                int selectedItemIdx = (int) source.getSelectedIndex();
	                
	                Object[] title = {"Primary object", "Event id", "Activity name", "Diagnosis"};
					Vector titlesV = new Vector(); // save title
					Vector<Vector> dataV = new Vector<Vector>(); // save data

					Collections.addAll(titlesV, title);
					int count = 0;
					while (cm.vs.violatedRules.size()>count) {
						if((int)cm.vs.violatedRules.get(count).get(4) == selectedItemIdx){
							Vector<Object> violations = new Vector<Object>();
							violations.add(cm.vs.violatedRules.get(count).get(0));
							violations.add(cm.vs.violatedRules.get(count).get(1));
							violations.add(cm.vs.violatedRules.get(count).get(2));
							violations.add(cm.vs.violatedRules.get(count).get(3));
							dataV.add(violations);
						}
						count += 1;
					}
					TableModel model = new DefaultTableModel(dataV, titlesV){
						//
						@Override
						public boolean isCellEditable(int row, int column) {
							//all cells false
							return false;
						}
					};

					double dividerLocation = 0.1;
					Component[] components = resultPanel2.getComponents();
					for (Component c : components) {
						if (c instanceof JPanel) {
							resultPanel2.remove(c);
						}
					}
					JPanel newChild = new JPanel(new BorderLayout());
					try {
						JTable table = new JTable(model);
						TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
						table.setRowSorter(sorter);
						List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
						sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
						sorter.setSortKeys(sortKeys);
						JScrollPane scrollPane=new JScrollPane(table);
						newChild.add(scrollPane);
					} catch (ParseException ex) {
						throw new RuntimeException(ex);
					}
					resultPanel2.add(newChild);
					resultPanel2.setDividerLocation(dividerLocation);
					newChild.revalidate();
					newChild.repaint();
	            }
	        });
		
		hBox1.add(Box.createVerticalStrut(20));
		Box vConsBox = Box.createHorizontalBox();
		vConsBox.add(Box.createHorizontalStrut(20));
		vConsBox.add(new JLabel("Select constraint: "));
		vConsBox.add(Box.createHorizontalStrut(20));
		vConsBox.add(constraintJCB);
		vConsBox.add(Box.createHorizontalStrut(20));

		// the split pane for resultPanel1 and violation table
		resultPanel2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				vConsBox, violationTablePanel
		);
		resultPanel2.setResizeWeight(0.1);
		resultPanel2.setOneTouchExpandable(true);
		resultPanel2.setDividerSize(5);
		resultPanel2.setBorder(null);

		outer3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				outer2,
				resultPanel2);
//		outer.setOneTouchExpandable(true);
//		inner.setResizeWeight(0.3);
		outer3.setResizeWeight(0.7);
		outer3.setOneTouchExpandable(true);
		outer3.setDividerSize(25);
		outer3.setBorder(null);

		// Creates the status bar
		statusBar = createStatusBar();

		// Display some useful information about repaint events
		installRepaintListener();

		// Puts everything together
		setLayout(new BorderLayout());
		add(outer3, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);

		mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
		layout.setForceConstant(150);
		layout.setMinDistanceLimit(5);
		
		// Creates the shapes palette
		objEditorPalette objPalette = insertObjPalette("Object Type", cm.objTypeList.size());
		ActEditorPalette actPalette = insertActPalette("Event type", actList.size());
		PerformanceEditorPalette performancePalette = insertPerformancePalette("Performance Type", 2);
//		RestEditorPalette restPalette = insertRestPalette("Restriction", restrictionList.size());

		// Sets the edge template to be used for creating new edges if an edge
		// is clicked in the shape palette
		objPalette.addListener(mxEvent.SELECT, new mxEventSource.mxIEventListener() {
			public void invoke(Object sender, mxEventObject evt) {
				Object tmp = evt.getProperty("transferable");

				if (tmp instanceof mxGraphTransferable) {
					mxGraphTransferable t = (mxGraphTransferable) tmp;
					Object cell = t.getCells()[0];

					if (graph.getModel().isEdge(cell)) {
						((GraphEditor.CustomGraph) graph).setEdgeTemplate(cell);
					}
				}
			}

		});

		actPalette.addListener(mxEvent.SELECT, new mxEventSource.mxIEventListener() {
			public void invoke(Object sender, mxEventObject evt) {
				Object tmp = evt.getProperty("transferable");

				if (tmp instanceof mxGraphTransferable) {
					mxGraphTransferable t = (mxGraphTransferable) tmp;
					Object cell = t.getCells()[0];

					if (graph.getModel().isEdge(cell)) {
						((GraphEditor.CustomGraph) graph).setEdgeTemplate(cell);
					}
				}
			}

		});

		performancePalette.addListener(mxEvent.SELECT, new mxEventSource.mxIEventListener() {
			public void invoke(Object sender, mxEventObject evt) {
				Object tmp = evt.getProperty("transferable");

				if (tmp instanceof mxGraphTransferable) {
					mxGraphTransferable t = (mxGraphTransferable) tmp;
					Object cell = t.getCells()[0];

					if (graph.getModel().isEdge(cell)) {
						((GraphEditor.CustomGraph) graph).setEdgeTemplate(cell);
					}
				}
			}

		});

		int i = 0;
		String idx;
		for (String objType : cm.objTypeList) {
			idx = ((Integer) (i % 6 + 1)).toString();
			// Adds some template cells for dropping into the graph
			objPalette
					.addObjTemplate(
							objType,
							null,
//							new ImageIcon(
//									GraphEditor.class
//											.getResource("/com/mxgraph/examples/swing/images/" + "orange" +
//													idx + ".png")),
							"ellipse;fillColor=#ffffff;strokeColor=#FF4500;strokeWidth=3;fontSize=16",
							80, 80, objType);
			i += 1;
//			installHandlers();
//			installListeners();
//			updateTitle();
		}

		for (Object actType : actList) {
			actPalette
					.addActTemplate(
							actType.toString(),
							null,
//							new ImageIcon(
//									GraphEditor.class
//											.getResource("/com/mxgraph/examples/swing/images/activity.png")),
							"rectangle;" +
									"fillColor=#ffffff;strokeColor=#000000;strokeWidth=3;fontSize=18" +
									"",
							120, 80, actType);
		}
		performancePalette
				.addTimePerformanceTemplate(
						"Time",
						null,
//						new ImageIcon(
//								GraphEditor.class
//										.getResource("/com/mxgraph/examples/swing/images/activity.png")),
						"ellipse;fillColor=#ffffff;strokeColor=#8B4513;strokeWidth=3;fontSize=16",
						100, 70, "Time");

		performancePalette
				.addFrequencyPerformanceTemplate(
						"Frequency",
						null,
//						new ImageIcon(
//								GraphEditor.class
//										.getResource("/com/mxgraph/examples/swing/images/activity.png")),
						"ellipse;fillColor=#ffffff;strokeColor=#8B4513;strokeWidth=3;fontSize=16",
						100, 70, "Frequency");
		installHandlers();
		installListeners();

		updateTitle();


		refreshBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				graph.getModel().beginUpdate();
				graph.getModel().endUpdate();
			}
		});

		// reset the graph
		resetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cm.consLst = new ArrayList<>();
				Integer constraintNum = constraintJCB.getItemCount();
				Integer constraintId = 0;
				while(constraintId < constraintNum){
					System.out.print("add item to jcombo box");
					constraintJCB.removeItemAt(constraintId++);
				}
				graph.getModel().beginUpdate();
				graph.selectAll();
				graph.removeCells();
				graph.refresh();
				graph.getModel().endUpdate();
			}
		});
		
		checkBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cm.vs.violatedRules = new ArrayList<>();

				System.out.println("get the output constraint list"+cm.consLst);

				ProgressMonitor pm = new ProgressMonitor(null,
						"Waiting for results","Already done:",
						0,cm.consLst.size());

				// get the amount of constraints to check
				// start the child thread
				int constraintNum = cm.consLst.size();
				
				ConstraintMonitorEngine simulaterActivity = new ConstraintMonitorEngine(constraintNum);
				
				
//				constraintJCB.setMaximumSize(new Dimension(100, 20));
//				constraintJCB.setMinimumSize(new Dimension(100, 20));
//				constraintJCB.setPreferredSize(new Dimension(100, 20));
//				
				Integer constraintId = constraintJCB.getItemCount();;
				while(constraintId < constraintNum){
					constraintJCB.addItem("Constraint "+(++constraintId).toString());
				}
				revalidate();
				
				simulaterActivity.setCurrent(0);
				new Thread(simulaterActivity).start();

				timer = new Timer(0, new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						// set progress of the
						int current = simulaterActivity.getCurrent();
						pm.setProgress(current);
						if(pm.isCanceled()){
							timer.stop();
							pm.close();
							System.exit(0);
						}
						if (current == cm.consLst.size()) {
							violationTablePanel.setBorder(BorderFactory.createEmptyBorder());
							violationTablePanel.setBackground(new Color(100, 100, 100));
							violationTablePanel.setLayout(new BorderLayout());

							Object[] title = {"Primary object", "Event id", "Activity name", "Diagnosis"};
							Vector titlesV = new Vector(); // save title
							Vector<Vector> dataV = new Vector<Vector>(); // save data

							Collections.addAll(titlesV, title);
							int count = 0;
							while (cm.vs.violatedRules.size()>count) {
								if((int)cm.vs.violatedRules.get(count).get(4) == 0){
									Vector<Object> violations = new Vector<Object>();
									violations.add(cm.vs.violatedRules.get(count).get(0));
									violations.add(cm.vs.violatedRules.get(count).get(1));
									violations.add(cm.vs.violatedRules.get(count).get(2));
									violations.add(cm.vs.violatedRules.get(count).get(3));
									dataV.add(violations);
								}
								count += 1;
							}
							TableModel model = new DefaultTableModel(dataV, titlesV){
								//
								@Override
								public boolean isCellEditable(int row, int column) {
									//all cells false
									return false;
								}
							};

							double dividerLocation = 0.1;
							Component[] components = resultPanel2.getComponents();
							for (Component c : components) {
								if (c instanceof JPanel) {
									resultPanel2.remove(c);
								}
							}
							JPanel newChild = new JPanel(new BorderLayout());
							try {
								JTable table = new JTable(model);
//								table.addMouseListener(new MouseAdapter() {
////									@Override
////									public void mouseClicked(MouseEvent e) {
////
////										// get selected row
////										selectedRow = table.getSelectedRow();
////										graph.getModel().beginUpdate();
////										graph.selectAll();
////										graph.removeCells();
////
////										Object parent = graph.getDefaultParent();
////										String leadingObj = (String) table.getModel().getValueAt(
////												selectedRow, 0);
////
////										if (leadingObj.equals("")){
////											return;
////										}
////										// get all the node
////										Set<OcelEvent> evtLst =  peMap.get(leadingObj).keySet();
////
////										List<OcelEvent> mainList = new ArrayList<OcelEvent>();
////										mainList.addAll(evtLst);
////
////										Collections.sort(mainList, new OcelEventComparator());
////										Map<String, Integer> edgeIdxToEvtId = new HashMap<>();
////
////										HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>> evtDfMap = peMap.get(leadingObj);
////
////										Set<OcelEvent> allEvt = new HashSet<>();
////										for (OcelEvent evt : mainList) {
////											allEvt.add(evt);
////											HashMap<OcelEvent,HashSet<OcelObject>> dfEvtMap = evtDfMap.get(evt);
////											for(OcelEvent dfEvt: dfEvtMap.keySet()) {
////												allEvt.add(dfEvt);
////											}
////										}
////										List<OcelEvent> allEvtList = new ArrayList<OcelEvent>();
////										allEvtList.addAll(allEvt);
////
////										Collections.sort(allEvtList, new OcelEventComparator());
////
////
////										Object[] vertices = new Object[allEvt.size()];
//////				ArrayList<Object[]> vertices = new ArrayList<>();
////
////										int i = 0;
////
////										Map<OcelEvent,Integer> evtToNodeMap = new HashMap<>();
////
////										ArrayList evtIdLst = (ArrayList) table.getModel().getValueAt(
////												selectedRow, 1);
////
////										// first get the node
////										for (OcelEvent evt : allEvtList) {
////
////											if (evtIdLst.contains(evt.id)){
////												vertices[i] = graph.insertVertex(parent,
////														evt.id,
////														evt.activity + "\n" + evt.timestamp,
////														200, 0,
////														120,
////														50,
////														"ellipse;fontSize=14;fillColor=#FF0000;fontColor=#000000");
////												edgeIdxToEvtId.put(evt.id, i);
////												evtToNodeMap.put(evt, i);
////											}
////											else {
////												vertices[i] = graph.insertVertex(parent,
////														evt.id,
////														evt.activity + "\n" + evt.timestamp,
////														200, 0,
////														120,
////														50,
////														"ellipse;fontSize=12");
////												edgeIdxToEvtId.put(evt.id, i);
////												evtToNodeMap.put(evt, i);
////											}
////											i += 1;
////										}
////
////										// then get the edge, iterate every event
////										for(OcelEvent evt:mainList){
////
////											// get all direct follow events
////											HashMap<OcelEvent,HashSet<OcelObject>> dfEvtMap = evtDfMap.get(evt);
////
////											// add an edge them if there is a shared object
////											for(OcelEvent dfEvt: dfEvtMap.keySet()) {
////
////
////												HashSet<OcelObject> objSet = dfEvtMap.get(dfEvt);
////												// iterate every obj
////												for (OcelObject obj:objSet) {
////													if (cm.revObjTypes.contains(obj.objectType.name)||
////															obj.id.equals(leadingObj)){
////														graph.insertEdge(parent,
////																null,
////																obj.id,
////																vertices[evtToNodeMap.get(evt)],
////																vertices[evtToNodeMap.get(dfEvt)],
////																"startArrow=none;endArrow=classic;fontSize=8;strokeWidth=1");
////													}
////
////												}
////											}
////										}
////
////										// set layout of the process exe
////										mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
////										layout.setParallelEdgeSpacing(120);
////										layout.setIntraCellSpacing(150);
////										layout.setInterRankCellSpacing(80);
////										layout.setInterHierarchySpacing(120);
////										layout.execute(graph.getDefaultParent());
////
////										double width = graph.getGraphBounds().getWidth();
////										double height = graph.getGraphBounds().getHeight();
////										double widthLayout = graphComponent.getLayoutAreaSize().getWidth();
////										double heightLayout = graphComponent.getLayoutAreaSize().getHeight();
////										graph.getModel().setGeometry(graph.getDefaultParent(),
////												new mxGeometry((widthLayout - width)/2,
////														(heightLayout - height)/2,
////														widthLayout, heightLayout));
////
////										graph.getModel().endUpdate();
////									}
////								});
//
								TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
								table.setRowSorter(sorter);
								List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
								sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
								sorter.setSortKeys(sortKeys);
								JScrollPane scrollPane=new JScrollPane(table);
								newChild.add(scrollPane);
							} catch (ParseException ex) {
								throw new RuntimeException(ex);
							}
							resultPanel2.add(newChild);
							resultPanel2.setDividerLocation(dividerLocation);
							newChild.revalidate();
							newChild.repaint();

							// stop the progress
							timer.stop();
							pm.close();
//							System.exit(0);
						}
					}
				});
				timer.start();
			}
		});
	}

	private JTable getEvtTable(mxGraph graph,
							   Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peMap,
							   Map<String, Map> leadObjMap
							   ) {

		Object [] title = {"Id (Primary Object)", "Secondary Objects"};
		Vector titlesV = new Vector(); // save title
		titlesV.add(title[0]);
		titlesV.add(title[1]);

		Vector<Vector> dataV = new Vector<Vector>(); // save data

		int count = 0;
		HashSet<Object> objSet2;
		for (Object key : peMap.keySet()) {
			Vector<Object> t = new Vector<Object>();
//			t.add(count);
			t.add(key);

			HashSet<OcelObject> objSet = (HashSet<OcelObject>) leadObjMap.get(key);
			objSet2 = new HashSet<>();
			for (OcelObject obj:objSet){
				objSet2.add(obj.id);
			}
			t.add(objSet2);

//			t.add(idMap.get(count));
			dataV.add(t);
			count += 1;
		}
		TableModel model = new DefaultTableModel(dataV, titlesV){
			//
			@Override
			public boolean isCellEditable(int row, int column) {
				//all cells false
				return false;
			}
		};
		JTable jtl = new JTable(model);

		// add selection listener
		jtl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				// get selected row
				selectedRow = jtl.getSelectedRow();
				graph.getModel().beginUpdate();
				graph.selectAll();
				graph.removeCells();

				Object parent = graph.getDefaultParent();
				String leadingObj = (String) jtl.getModel().getValueAt(
						selectedRow, 0);
				// get all the node
				Set<OcelEvent> evtLst =  peMap.get(leadingObj).keySet();

				List<OcelEvent> mainList = new ArrayList<OcelEvent>();
				mainList.addAll(evtLst);

				Collections.sort(mainList, new OcelEventComparator());
				Map<String, Integer> edgeIdxToEvtId = new HashMap<>();

				HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>> evtDfMap = peMap.get(leadingObj);

				Set<OcelEvent> allEvt = new HashSet<>();
				for (OcelEvent evt : mainList) {
					allEvt.add(evt);
					HashMap<OcelEvent,HashSet<OcelObject>> dfEvtMap = evtDfMap.get(evt);
					for(OcelEvent dfEvt: dfEvtMap.keySet()) {
						allEvt.add(dfEvt);
					}
				}
				List<OcelEvent> allEvtList = new ArrayList<OcelEvent>();
				allEvtList.addAll(allEvt);

				Collections.sort(allEvtList, new OcelEventComparator());


				Object[] vertices = new Object[allEvt.size()];
//				ArrayList<Object[]> vertices = new ArrayList<>();

				int i = 0;

				Map<OcelEvent,Integer> evtToNodeMap = new HashMap<>();
				// first get the node
				for (OcelEvent evt : allEvtList) {
					vertices[i] = graph.insertVertex(parent,
							evt.id,
							evt.activity+"\n"+evt.timestamp,
							200, 0,
							120,
							50,
							"ellipse;fontSize=12");
					edgeIdxToEvtId.put(evt.id, i);
					evtToNodeMap.put(evt, i);
					i += 1;
				}


				// then get the edge, iterate every event
				for(OcelEvent evt:mainList){

					// get all direct follow events
					HashMap<OcelEvent,HashSet<OcelObject>> dfEvtMap = evtDfMap.get(evt);

					// add an edge them if there is a shared object
					for(OcelEvent dfEvt: dfEvtMap.keySet()) {


						HashSet<OcelObject> objSet = dfEvtMap.get(dfEvt);
						// iterate every obj
						for (OcelObject obj:objSet) {
							if (cm.revObjTypes.contains(obj.objectType.name)||
									obj.id.equals(leadingObj)){
								graph.insertEdge(parent,
										null,
										obj.id,
										vertices[evtToNodeMap.get(evt)],
										vertices[evtToNodeMap.get(dfEvt)],
										"startArrow=none;endArrow=classic;fontSize=8;strokeWidth=1");
							}

						}
					}
				}

				// set layout of the process exe
				mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
				layout.setParallelEdgeSpacing(40);
				layout.setIntraCellSpacing(150);
				layout.setInterRankCellSpacing(80);
				layout.setInterHierarchySpacing(120);
				layout.execute(graph.getDefaultParent());

				double width = graph.getGraphBounds().getWidth();
				double height = graph.getGraphBounds().getHeight();
				double widthLayout = graphComponent.getLayoutAreaSize().getWidth();
				double heightLayout = graphComponent.getLayoutAreaSize().getHeight();
				graph.getModel().setGeometry(graph.getDefaultParent(),
						new mxGeometry((widthLayout - width)/2,
								(heightLayout - height)/2,
								widthLayout, heightLayout));

				System.out.println("set the layout of p3 "+ width
						+ " "+ height +" "+widthLayout +" "+heightLayout);
				


				graph.getModel().endUpdate();
			}
		});
		return jtl;
	}

	/**
	 *
	 */
	protected mxUndoManager createUndoManager()
	{
		return new mxUndoManager();
	}

	/**
	 *
	 */
	protected void installHandlers()
	{
		rubberband = new mxRubberband(graphComponent);
		keyboardHandler = new EditorKeyboardHandlerForOCCLEditor(graphComponent);
	}


	protected JLabel createStatusBar()
	{
		JLabel statusBar = new JLabel(mxResources.get("ready"));
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

		return statusBar;
	}

	/**
	 *
	 */
	protected void installRepaintListener()
	{
		graphComponent.getGraph().addListener(mxEvent.REPAINT,
				new mxEventSource.mxIEventListener()
				{
					public void invoke(Object source, mxEventObject evt)
					{
						String buffer = (graphComponent.getTripleBuffer() != null) ? ""
								: " (unbuffered)";
						mxRectangle dirty = (mxRectangle) evt
								.getProperty("region");

						if (dirty == null)
						{
							status("Repaint all" + buffer);
						}
						else
						{
							status("Repaint: x=" + (int) (dirty.getX()) + " y="
									+ (int) (dirty.getY()) + " w="
									+ (int) (dirty.getWidth()) + " h="
									+ (int) (dirty.getHeight()) + buffer);
						}
					}
				});
	}

	/**
	 *
	 */
	public objEditorPalette insertObjPalette(String title, Integer objNum)
	{
		final objEditorPalette palette = new objEditorPalette(objNum);
		final JScrollPane scrollPane = new JScrollPane(palette);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		objPane.add(title, scrollPane);
		return palette;
	}

	public PerformanceEditorPalette insertPerformancePalette(String title, Integer attachNum)
	{
		final PerformanceEditorPalette palette = new PerformanceEditorPalette(attachNum);
		final JScrollPane scrollPane = new JScrollPane(palette);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		performancePane.add(title, scrollPane);
		return palette;
	}

	public ActEditorPalette insertActPalette(String title, Integer actNum)
	{
		final ActEditorPalette palette = new ActEditorPalette(actNum);
		final JScrollPane scrollPane = new JScrollPane(palette);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		actPane.add(title, scrollPane);
		return palette;
	}


	/**
	 *
	 */
	protected void mouseWheelMoved(MouseWheelEvent e)
	{
		if (e.getWheelRotation() < 0)
		{
			graphComponent.zoomIn();
		}
		else
		{
			graphComponent.zoomOut();
		}

		status(mxResources.get("scale") + ": "
				+ (int) (10 * graphComponent.getGraph().getView().getScale())
				+ "%");
	}

	/**
	 *
	 */
	protected void showOutlinePopupMenu(MouseEvent e)
	{
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
				graphComponent);
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(
				mxResources.get("magnifyPage"));
		item.setSelected(graphOutline.isFitPage());

		item.addActionListener(new ActionListener()
		{
			/**
			 *
			 */
			public void actionPerformed(ActionEvent e)
			{
				graphOutline.setFitPage(!graphOutline.isFitPage());
				graphOutline.repaint();
			}
		});

		JCheckBoxMenuItem item2 = new JCheckBoxMenuItem(
				mxResources.get("showLabels"));
		item2.setSelected(graphOutline.isDrawLabels());

		item2.addActionListener(new ActionListener()
		{
			/**
			 *
			 */
			public void actionPerformed(ActionEvent e)
			{
				graphOutline.setDrawLabels(!graphOutline.isDrawLabels());
				graphOutline.repaint();
			}
		});

		JCheckBoxMenuItem item3 = new JCheckBoxMenuItem(
				mxResources.get("buffering"));
		item3.setSelected(graphOutline.isTripleBuffered());

		item3.addActionListener(new ActionListener()
		{
			/**
			 *
			 */
			public void actionPerformed(ActionEvent e)
			{
				graphOutline.setTripleBuffered(!graphOutline.isTripleBuffered());
				graphOutline.repaint();
			}
		});

		JPopupMenu menu = new JPopupMenu();
		menu.add(item);
		menu.add(item2);
		menu.add(item3);
		menu.show(graphComponent, pt.x, pt.y);

		e.consume();
	}


	/**
	 *
	 */
	protected void mouseLocationChanged(MouseEvent e)
	{
		status(e.getX() + ", " + e.getY());
	}

	/**
	 *
	 */
	protected void installListeners()
	{
		// Installs mouse wheel listener for zooming
		MouseWheelListener wheelTracker = new MouseWheelListener()
		{
			/**
			 *
			 */
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				if (e.getSource() instanceof mxGraphOutline
						|| e.isControlDown())
				{
					OCCMEditor.this.mouseWheelMoved(e);
				}
			}

		};

		// Handles mouse wheel events in the outline and graph component
		graphOutline.addMouseWheelListener(wheelTracker);
		graphComponent.addMouseWheelListener(wheelTracker);

		// Installs the popup menu in the outline
		graphOutline.addMouseListener(new MouseAdapter()
		{

			/**
			 *
			 */
			public void mousePressed(MouseEvent e)
			{
				// Handles context menu on the Mac where the trigger is on mousepressed
				mouseReleased(e);
			}

			/**
			 *
			 */
			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					showOutlinePopupMenu(e);
				}
			}

		});

		// Installs the popup menu in the graph component
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{

			/**
			 *
			 */
			public void mousePressed(MouseEvent e)
			{
				// Handles context menu on the Mac where the trigger is on mousepressed
				mouseReleased(e);
			}

			/**
			 *
			 */
			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					showGraphPopupMenu(e);
				}
			}

		});

		// Installs a mouse motion listener to display the mouse location
		// æ²¡æœ‰å¿…è¦�å�§ï¼Ÿ
		graphComponent.getGraphControl().addMouseMotionListener(
				new MouseMotionListener()
				{

					/*
					 * (non-Javadoc)
					 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
					 */
					public void mouseDragged(MouseEvent e)
					{
						mouseLocationChanged(e);
					}

					/*
					 * (non-Javadoc)
					 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
					 */
					public void mouseMoved(MouseEvent e)
					{
						// å�¯èƒ½æ²¡æœ‰å¿…è¦�
						graphComponent.getGraph().refresh();
					}

				});
	}

	protected void showGraphPopupMenu(MouseEvent e)
	{
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
				graphComponent);
		EditorPopupMenu menu = new EditorPopupMenu(OCCMEditor.this);
		menu.show(graphComponent, pt.x, pt.y);

		e.consume();
	}
	/**
	 *
	 */
	public void setCurrentFile(File file)
	{
		File oldValue = currentFile;
		currentFile = file;

		firePropertyChange("currentFile", oldValue, file);

		if (oldValue != file)
		{
			updateTitle();
		}
	}

	/**
	 *
	 */
	public File getCurrentFile()
	{
		return currentFile;
	}

	/**
	 *
	 * @param modified
	 */
	public void setModified(boolean modified)
	{
		boolean oldValue = this.modified;
		this.modified = modified;

		firePropertyChange("modified", oldValue, modified);

		if (oldValue != modified)
		{
			updateTitle();
		}
	}

	/**
	 *
	 * @return whether or not the current graph has been modified
	 */
	public boolean isModified()
	{
		return modified;
	}

	/**
	 *
	 */
	public mxGraphComponent getGraphComponent()
	{
		graphComponent.DEFAULT_PAGESCALE = 1;
		return graphComponent;
	}

	/**
	 *
	 */
	public mxGraphOutline getGraphOutline()
	{
		return graphOutline;
	}

	/**
	 *
	 */
	public JTabbedPane getObjPane()
	{
		return objPane;
	}

	public JTabbedPane getActPane()
	{
		return actPane;
	}


	/**
	 *
	 */
	public mxUndoManager getUndoManager()
	{
		return undoManager;
	}

	/**
	 *
	 * @param name
	 * @param action
	 * @return a new Action bound to the specified string name
	 */
	public Action bind(String name, final Action action)
	{
		return bind(name, action, null);
	}

	/**
	 *
	 * @param name
	 * @param action
	 * @return a new Action bound to the specified string name and icon
	 */
	@SuppressWarnings("serial")
	public Action bind(String name, final Action action, String iconUrl)
	{
		System.out.println("url:"+iconUrl);
		AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
				OCCMEditor.class.getResource(iconUrl)) : null)
		{
			public void actionPerformed(ActionEvent e)
			{
				action.actionPerformed(new ActionEvent(getGraphComponent(), e
						.getID(), e.getActionCommand()));
			}
		};

		newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));

		return newAction;
	}

	/**
	 *
	 * @param msg
	 */
	public void status(String msg)
	{
		statusBar.setText(msg);
	}

	/**
	 *
	 */
	public void updateTitle()
	{
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null)
		{
			String title = (currentFile != null) ? currentFile
					.getAbsolutePath() : mxResources.get("newDiagram");

			if (modified)
			{
				title += "*";
			}

			frame.setTitle(title + " - " + appTitle);
		}
	}

	/**
	 *
	 */
	public void about()
	{
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null)
		{
			EditorAboutFrame about = new EditorAboutFrame(frame);
			about.setModal(true);

			// Centers inside the application frame
			int x = frame.getX() + (frame.getWidth() - about.getWidth()) / 2;
			int y = frame.getY() + (frame.getHeight() - about.getHeight()) / 2;
			about.setLocation(x, y);

			// Shows the modal dialog and waits
			about.setVisible(true);
		}
	}

	/**
	 *
	 */
	public void exit()
	{
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null)
		{
			frame.dispose();
		}
	}

	/**
	 *
	 */
	public void setLookAndFeel(String clazz)
	{
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null)
		{
			try
			{
				UIManager.setLookAndFeel(clazz);
				SwingUtilities.updateComponentTreeUI(frame);

				// Needs to assign the key bindings again
				keyboardHandler = new EditorKeyboardHandlerForOCCLEditor(graphComponent);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
	}

	/**
	 *
	 */
	public JFrame createFrame(JMenuBar menuBar)
	{
		JFrame frame = new JFrame();
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		frame.setSize(870, 640);

		// Updates the frame title
		updateTitle();

		return frame;
	}

	/**
	 * Creates an action that executes the specified layout.
	 *
	 * @param key Key to be used for getting the label from mxResources and also
	 * to create the layout instance for the commercial graph editor example.
	 * @return an action that executes the specified layout
	 */
	@SuppressWarnings("serial")
	public Action graphLayout(final String key, boolean animate)
	{
		final mxIGraphLayout layout = createLayout(key, animate);

		if (layout != null)
		{
			return new AbstractAction(mxResources.get(key))
			{
				public void actionPerformed(ActionEvent e)
				{
					final mxGraph graph = graphComponent.getGraph();
					Object cell = graph.getSelectionCell();

					if (cell == null
							|| graph.getModel().getChildCount(cell) == 0)
					{
						cell = graph.getDefaultParent();
					}

					graph.getModel().beginUpdate();
					try
					{
						long t0 = System.currentTimeMillis();
						layout.execute(cell);
						status("Layout: " + (System.currentTimeMillis() - t0)
								+ " ms");
					}
					finally
					{
						mxMorphing morph = new mxMorphing(graphComponent, 20,
								1.2, 20);

						morph.addListener(mxEvent.DONE, new mxEventSource.mxIEventListener()
						{

							public void invoke(Object sender, mxEventObject evt)
							{
								graph.getModel().endUpdate();
							}

						});

						morph.startAnimation();
					}

				}

			};
		}
		else
		{
			return new AbstractAction(mxResources.get(key))
			{

				public void actionPerformed(ActionEvent e)
				{
					JOptionPane.showMessageDialog(graphComponent,
							mxResources.get("noLayout"));
				}

			};
		}
	}

	/**
	 * Creates a layout instance for the given identifier.
	 */
	protected mxIGraphLayout createLayout(String ident, boolean animate)
	{
		mxIGraphLayout layout = null;

		if (ident != null)
		{
			mxGraph graph = graphComponent.getGraph();

			if (ident.equals("verticalHierarchical"))
			{
				layout = new mxHierarchicalLayout(graph);
			}
			else if (ident.equals("horizontalHierarchical"))
			{
				layout = new mxHierarchicalLayout(graph, JLabel.WEST);
			}
			else if (ident.equals("verticalTree"))
			{
				layout = new mxCompactTreeLayout(graph, false);
			}
			else if (ident.equals("horizontalTree"))
			{
				layout = new mxCompactTreeLayout(graph, true);
			}
			else if (ident.equals("parallelEdges"))
			{
				layout = new mxParallelEdgeLayout(graph);
			}
			else if (ident.equals("placeEdgeLabels"))
			{
				layout = new mxEdgeLabelLayout(graph);
			}
			else if (ident.equals("organicLayout"))
			{
				layout = new mxOrganicLayout(graph);
			}
			if (ident.equals("verticalPartition"))
			{
				layout = new mxPartitionLayout(graph, false)
				{
					/**
					 * Overrides the empty implementation to return the size of the
					 * graph control.
					 */
					public mxRectangle getContainerSize()
					{
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("horizontalPartition"))
			{
				layout = new mxPartitionLayout(graph, true)
				{
					/**
					 * Overrides the empty implementation to return the size of the
					 * graph control.
					 */
					public mxRectangle getContainerSize()
					{
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("verticalStack"))
			{
				layout = new mxStackLayout(graph, false)
				{
					/**
					 * Overrides the empty implementation to return the size of the
					 * graph control.
					 */
					public mxRectangle getContainerSize()
					{
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("horizontalStack"))
			{
				layout = new mxStackLayout(graph, true)
				{
					/**
					 * Overrides the empty implementation to return the size of the
					 * graph control.
					 */
					public mxRectangle getContainerSize()
					{
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("circleLayout"))
			{
				layout = new mxCircleLayout(graph);
			}
		}

		return layout;
	}

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
							"/org/processmining/cachealignment/algorithms/resources/default-style.xml")
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
					// begin configure the object property in period state
					ArrayList<mxCell> ports = getObjTypePorts();
					graph.addCell(ports.get(0), cellToAdd[0]);
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
					graph.refresh();
				}
				else if (((mxCell) cellToAdd[0]).getType().equals("freqPerf")) {
					ArrayList<mxCell> ports = getFreqPerformancePorts();
					graph.addCell(ports.get(0), cellToAdd[0]);
					graph.getModel().endUpdate();
					graph.refresh();
				}
				else if (((mxCell) cellToAdd[0]).getType().equals("timePerf")) {
					ArrayList<mxCell> ports = getTimePerformancePorts();
					graph.addCell(ports.get(0), cellToAdd[0]);
//					getTimePerformanceDialog(((mxCell) cellToAdd[0]), graph);
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
		}

	}

	public static void getTimePerformanceDialog(mxCell cell,mxGraph graph){
		JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);
		dialog.setTitle("Time Performance Constraint");
		dialog.setSize(new Dimension(400,100));
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);  // set to the center of the screen
		Box hBox = Box.createHorizontalBox();
		Box vBox = Box.createVerticalBox();

		// constraint panel
		JPanel constraintPanel = new JPanel(new GridLayout(8, 1, 10, 0));
		List<String> names = Arrays.asList(
				"Waiting time",
				"Synchronization time",
				"Lagging time",
				"Response time",
				"Throughput time",
				"Clogging time",
				"Pooling time");
		JComboBox<String> jcb = new JComboBox<>();

		for (String name : names) {
			jcb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					timePerformanceCons = name;
				}
			});

			jcb.addItem(name);
		}
		constraintPanel.add(jcb);
		jcb.setMaximumSize(new Dimension(150,30));
		jcb.setMinimumSize(new Dimension(150,30));
		jcb.setPreferredSize(new Dimension(150,30));
		hBox.add(jcb);
		//-----------

		// btn panel
		JPanel btnPanel = new JPanel();
		btnPanel.setMaximumSize(new Dimension(150,70));
		btnPanel.setMinimumSize(new Dimension(150,70));
		btnPanel.setPreferredSize(new Dimension(150,70));

		// add a confirm button
		JButton jbConfirm = new JButton("Confirm");
		jbConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cell.setValue(timePerformanceCons);
				if (timePerformanceCons.equals("pooling time") || timePerformanceCons.equals("clogging time")){
					cell.setTip(objTypeSelected);
				}
				// set the constraint time
				graph.getModel().beginUpdate();
				graph.getModel().endUpdate();
				dialog.dispose();
				cell.setStyle("ellipse;fillColor=#ffffff;strokeColor=#8B4513;strokeWidth=3;fontSize=16");
			}
		});

		// add a cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// remove the edge
				graph.getModel().beginUpdate();
				graph.removeCells(new Object[]{cell});
				graph.getModel().endUpdate();
				dialog.dispose();
			}
		});
		Box hBox1 = Box.createHorizontalBox();
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(jbConfirm);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(jbCancel);
		vBox.add(hBox);
		vBox.add(hBox1);
		dialog.getContentPane().add(vBox);
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
				"triangle;direction=west;fillColor=#808080",
				"actToActBeforeCardPort",
				"For activity-activity cardinality constraint");
		port1.setVertex(true);

		mxGeometry geo2 = new mxGeometry(0.5, 0, PORT_DIAMETER,
				PORT_DIAMETER);
		geo2.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo2.setRelative(true);
		mxCell port2 = new mxCell(null, geo2,
				"circle;direction=north;fillColor=#FF4500",
				"actToObjPort",
				"For object involvement constraint");
		port2.setVertex(true);

		mxGeometry geo3 = new mxGeometry(1, 0.2, PORT_DIAMETER,
				PORT_DIAMETER);
		geo3.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo3.setRelative(true);
		mxCell port3 = new mxCell(null, geo3,
				"triangle;direction=east;fillColor=#808080",
				"actToActAfterCardPort",
				"For activity-activity cardinality constraint");
		port3.setVertex(true);

		mxGeometry geo4 = new mxGeometry(0.5, 1, PORT_DIAMETER,
				PORT_DIAMETER);
		geo4.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo4.setRelative(true);
		mxCell port4 = new mxCell(null, geo4,
				"ellipse;fillColor=#8B4513",
				"actPerfPort",
				"For performance constraint");
		port4.setVertex(true);

		mxGeometry geo5 = new mxGeometry(0, 0.8, PORT_DIAMETER,
				PORT_DIAMETER);
		geo5.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo5.setRelative(true);
		mxCell port5 = new mxCell(null, geo5,
				"triangle;direction=west;timeRefPort;fillColor=#008000",
				"actToActBeforeTimePort",
				"For activity-activity temporal constraint");
		port5.setVertex(true);

		mxGeometry geo6 = new mxGeometry(1, 0.8, PORT_DIAMETER,
				PORT_DIAMETER);
		geo6.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo6.setRelative(true);
		mxCell port6 = new mxCell(null, geo6,
				"triangle;direction=east;timeRefPort;fillColor=#008000",
				"actToActAfterTimePort",
				"For activity-activity temporal constraint");
		port6.setVertex(true);

		ports.add(port1);
		ports.add(port2);
		ports.add(port3);
		ports.add(port4);
		ports.add(port5);
		ports.add(port6);
		return ports;
	}

	public static ArrayList<mxCell> getFreqPerformancePorts(){
		int PORT_DIAMETER = 12;

		int PORT_RADIUS = PORT_DIAMETER / 2;

		ArrayList<mxCell> ports = new ArrayList<>();

		mxGeometry geo1 = new mxGeometry(0.5, 0, PORT_DIAMETER,
				PORT_DIAMETER);
		geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo1.setRelative(true);
		mxCell port1 = new mxCell(null, geo1,
				"ellipse;fillColor=#8B4513","perfFreqPort",
				"Frequency performance for activity");
		port1.setVertex(true);
		ports.add(port1);
		return ports;
	}

	public static ArrayList<mxCell> getTimePerformancePorts(){
		int PORT_DIAMETER = 12;

		int PORT_RADIUS = PORT_DIAMETER / 2;

		ArrayList<mxCell> ports = new ArrayList<>();

		mxGeometry geo1 = new mxGeometry(0.5, 0, PORT_DIAMETER,
				PORT_DIAMETER);
		geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo1.setRelative(true);
		mxCell port1 = new mxCell(null, geo1,
				"ellipse;fillColor=#8B4513","perfTimePort",
				"Time performance");
		port1.setVertex(true);
		ports.add(port1);
		return ports;
	}


	public static ArrayList<mxCell> getObjTypePorts(){
		int PORT_DIAMETER = 12;

		int PORT_RADIUS = PORT_DIAMETER / 2;

		ArrayList<mxCell> ports = new ArrayList<>();

		mxGeometry geo1 = new mxGeometry(0.5, 1, PORT_DIAMETER,
				PORT_DIAMETER);
		geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo1.setRelative(true);
		mxCell port1 = new mxCell(null, geo1,
				"rectangle;fillColor=#FF4500","objPort",
				"Connect to activity for cardinality");
		port1.setVertex(true);

		ports.add(port1);

		return ports;
	}

//	public static void main(String[] args) {
//		System.getProperty("user.dir")
//	}


//	public static void main(String[] args)
//	{
//
//		JFrame frame = new JFrame();
//		//		model.setModelEnvironment(mxConstants.model_editor);
//		OCCMEditor editor = new OCCMEditor();
//
//		frame.add(editor);
//
//		// get the screen size
//		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
//
//		// enlarge to the whole page
//		frame.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
//
//		frame.setVisible(true);
//
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//	}
}

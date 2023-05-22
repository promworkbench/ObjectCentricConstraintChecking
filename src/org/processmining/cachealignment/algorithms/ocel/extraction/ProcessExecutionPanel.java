package org.processmining.cachealignment.algorithms.ocel.extraction;

import org.processmining.cachealignment.algorithms.editor.*;
import org.processmining.cachealignment.algorithms.io.mxCodec;
import org.processmining.cachealignment.algorithms.layout.hierarchical.mxHierarchicalLayout;
import org.processmining.cachealignment.algorithms.layout.*;
import org.processmining.cachealignment.algorithms.model.mxCell;
import org.processmining.cachealignment.algorithms.model.mxGeometry;
import org.processmining.cachealignment.algorithms.model.mxICell;
import org.processmining.cachealignment.algorithms.model.mxIGraphModel;
import org.processmining.cachealignment.algorithms.ocel.occl.GraphEditor;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEvent;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEventComparator;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEventLog;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelObject;
import org.processmining.cachealignment.algorithms.swing.handler.mxKeyboardHandlerForOCCLEditor;
import org.processmining.cachealignment.algorithms.swing.handler.mxRubberband;
import org.processmining.cachealignment.algorithms.swing.mxGraphComponent;
import org.processmining.cachealignment.algorithms.swing.mxGraphOutline;
import org.processmining.cachealignment.algorithms.swing.util.mxMorphing;
import org.processmining.cachealignment.algorithms.util.*;
import org.processmining.cachealignment.algorithms.view.mxGraph;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.w3c.dom.Document;

import javax.swing.Timer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.*;

public class ProcessExecutionPanel extends JPanel
{
	Timer timer;

	protected int selectedRow = -1;

	protected HashSet<String> selectedObjSet = new HashSet<>();

	List<String> colorLst = new ArrayList<>(Arrays.asList(
			"#FFDAB9","#96CDCD","#668B8B","#1E90FF","#458B74","#9BCD9B","#8470FF",
			"#696969","#000080","#6495ED","#008B00","#4682B4","#698B22","#B0C4DE",
			"#FFF68F","#EEEE00","#FFC125","#8B6914","#CD5C5C","#EE6363","#7A8B8B",
			"#D2691E","#FFE7BA","#CD2626","#9932CC","#FF34B3","#90EE90"));

	Map<String, Map> objToColorMap = new HashMap<>();

	int colorIdx = 0;

//	TimePerformance gpf = new TimePerformance();

	PEModel pem = PEModel.getInstance();

	static String timePerformanceCons;

	static String objTypeSelected;

	static ArrayList<String> objTypeList1;
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

	private static String actName;

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

	public ProcessExecutionPanel(UIPluginContext context,
								 Map<String, Map> leadObjMap,
								 Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peMap,
								 OcelEventLog ocel,
								 ArrayList<String> objTypeList,
								 HashSet actList) {

		// Stores and updates the frame title
		this.appTitle = "Object-Centric Constraint Model Editor";
		objTypeList1 = objTypeList;
		CaseExtraction ce = new CaseExtraction();
		List<Map> map1= ce.extractCaseWithLeadingType(ocel, pem.leadObjType, pem.revObjTypes);
		pem.peMap = map1.get(3);

		// Stores a reference to the graph and creates the command history
		graphComponent = new CustomGraphComponent(new CustomGraph());
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

		// -------- pane for process execution
		JTable table = getEvtTable(
				graphComponent.getGraph(),
				peMap,
				leadObjMap
		);
		JPanel jpObj = new JPanel();
		Integer rowNum = 2 + pem.allObjectTyps.size();
		jpObj.setLayout((new GridLayout(rowNum, 1, 0, 20)));
		JLabel jblHighlight = new JLabel("Highlight the selected object types:");
		Font font = new Font("Arial", Font.BOLD, 18);
		jblHighlight.setFont(font);
		jpObj.add(jblHighlight);
		HashSet<String> selectedTypes = new HashSet<>();
		selectedTypes.addAll(pem.revObjTypes);
		selectedTypes.add(pem.leadObjType);

		for (String obj:selectedTypes){
			JCheckBox jcb = new JCheckBox(obj);
			jcb.addItemListener(e -> {
				if (e.getStateChange() == ItemEvent.SELECTED) {

					selectedObjSet.add(jcb.getText());
					Map<String,String> colorMap = new HashMap<>();
					objToColorMap.put(jcb.getText(),colorMap);

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
								for (OcelObject objInSet:objSet) {

									// if obj is the selected type
									if (pem.revObjTypes.contains(objInSet.objectType.name) ||
											objInSet.id.equals(leadingObj)) {
										if (!selectedObjSet.contains(objInSet.objectType.name)) {
											graph.insertEdge(parent,
													null,
													objInSet.id,
													vertices[evtToNodeMap.get(evt)],
													vertices[evtToNodeMap.get(dfEvt)],
													"startArrow=none;endArrow=classic;fontSize=8;strokeWidth=1");
										} else {
											String colToUse;
											if (objToColorMap.get(objInSet.objectType.name).containsKey(objInSet.id)) {
												colToUse = (String) objToColorMap.get(objInSet.objectType.name).get(objInSet.id);
											}
											else{
												colToUse = colorLst.get(colorIdx);
												objToColorMap.get(objInSet.objectType.name).put(objInSet.id, colToUse);

												colorIdx = colorIdx+1;
											}
											graph.insertEdge(parent,
													null,
													objInSet.id,
													vertices[evtToNodeMap.get(evt)],
													vertices[evtToNodeMap.get(dfEvt)],
													"startArrow=none;endArrow=classic;fontSize=12;strokeWidth=3;" +
															"strokeColor="+colToUse+";fontColor="+colToUse);

										}
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

						graph.getModel().endUpdate();
					}
				}
				else if (e.getStateChange() == ItemEvent.DESELECTED) {
					selectedObjSet.remove(jcb.getText());

					//remove the color colorIdx from the map
					colorIdx = colorIdx - objToColorMap.get(jcb.getText()).size();

					System.out.println("before deselect"+ objToColorMap.size());
					objToColorMap.remove(jcb.getText());  //remove the selected object type
					System.out.println("after deselect"+ objToColorMap.size());

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
								for (OcelObject objInSet:objSet) {

									// if obj is the selected type
									if (pem.revObjTypes.contains(objInSet.objectType.name) ||
											objInSet.id.equals(leadingObj)) {
										if (!selectedObjSet.contains(objInSet.objectType.name)) {
											graph.insertEdge(parent,
													null,
													objInSet.id,
													vertices[evtToNodeMap.get(evt)],
													vertices[evtToNodeMap.get(dfEvt)],
													"startArrow=none;endArrow=classic;fontSize=8;strokeWidth=1");
										} else {
											String colToUse;
											if (objToColorMap.get(objInSet.objectType.name).containsKey(objInSet.id)) {
												colToUse = (String) objToColorMap.get(objInSet.objectType.name).get(objInSet.id);
											}
											else{
												colToUse = colorLst.get(colorIdx);
												objToColorMap.get(objInSet.objectType.name).put(objInSet.id, colToUse);

												colorIdx = colorIdx+1;
											}
											graph.insertEdge(parent,
													null,
													objInSet.id,
													vertices[evtToNodeMap.get(evt)],
													vertices[evtToNodeMap.get(dfEvt)],
													"startArrow=none;endArrow=classic;fontSize=12;strokeWidth=3;" +
															"strokeColor="+colToUse+";fontColor="+colToUse);

										}
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

						graph.getModel().endUpdate();
//						graph.removeCells();
//						graph.getModel().beginUpdate();
//						graph.selectAll();
//						Object parent = graph.getDefaultParent();
//						String leadingObj = (String) table.getModel().getValueAt(selectedRow, 1);
//
//						// get all the node
//						Set<OcelEvent> evtLst =  peMap.get(leadingObj).keySet();
//						List<OcelEvent> mainList = new ArrayList<OcelEvent>();
//						mainList.addAll(evtLst);
//						Collections.sort(mainList, new OcelEventComparator());
//						Map<String, Integer> edgeIdxToEvtId = new HashMap<>();
//						HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>> evtDfMap = peMap.get(leadingObj);
//						Set<OcelEvent> allEvt = new HashSet<>();
//						for (OcelEvent evt : mainList) {
//							allEvt.add(evt);
//							HashMap<OcelEvent,HashSet<OcelObject>> dfEvtMap = evtDfMap.get(evt);
//							for(OcelEvent dfEvt: dfEvtMap.keySet()) {
//								allEvt.add(dfEvt);
//							}
//						}
//						List<OcelEvent> allEvtList = new ArrayList<OcelEvent>();
//						allEvtList.addAll(allEvt);
//						Collections.sort(allEvtList, new OcelEventComparator());
//						Object[] vertices = new Object[allEvt.size()];
////				ArrayList<Object[]> vertices = new ArrayList<>();
//						int i = 0;
//						Map<OcelEvent,Integer> evtToNodeMap = new HashMap<>();
//						// first get the node
//						for (OcelEvent evt : allEvtList) {
//							vertices[i] = graph.insertVertex(parent,
//									evt.id,
//									evt.activity+"\n"+evt.timestamp,
//									200, 0,
//									120,
//									50,
//									"ellipse;fontSize=12",
//									"");
//							edgeIdxToEvtId.put(evt.id, i);
//							evtToNodeMap.put(evt, i);
//							i += 1;
//						}
//
//						// then get the edge, iterate every event
//						for(OcelEvent evt:mainList){
//
//							// get all direct follow events
//							HashMap<OcelEvent,HashSet<OcelObject>> dfEvtMap = evtDfMap.get(evt);
//
//							// add an edge them if there is a shared object
//							for(OcelEvent dfEvt: dfEvtMap.keySet()) {
//
//								HashSet<OcelObject> objSet = dfEvtMap.get(dfEvt);
//								// iterate every obj
//
//								for (OcelObject objInSet:objSet) {
//									if (pem.revObjTypes.contains(objInSet.objectType.name) ||
//											objInSet.id.equals(leadingObj)) {
//										if (!selectedObjSet.contains(objInSet.objectType.name)) {
//											graph.insertEdge(parent,
//													null,
//													objInSet.id,
//													vertices[evtToNodeMap.get(evt)],
//													vertices[evtToNodeMap.get(dfEvt)],
//													"startArrow=none;endArrow=classic;fontSize=8;strokeWidth=1");
//										} else {
//											String colorToUse = (String) objToColorMap.get(objInSet.objectType.name).get(objInSet.id);
//											graph.insertEdge(parent,
//													null,
//													objInSet.id,
//													vertices[evtToNodeMap.get(evt)],
//													vertices[evtToNodeMap.get(dfEvt)],
//													"startArrow=none;endArrow=classic;" +
//															"fontSize=12;strokeWidth=3;" +
//															"strokeColor="+colorToUse+";fontColor="+colorToUse);
//										}
//
//									}
//								}
//							}
//						}
//
//						// set layout of the process exe
//						mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
//						layout.setParallelEdgeSpacing(120);
//						layout.setIntraCellSpacing(150);
//						layout.setInterRankCellSpacing(80);
//						layout.setInterHierarchySpacing(120);
//						System.out.println("set the layout of p2 "+graph.getView().getScale());
//
//						double width = graph.getGraphBounds().getWidth();
//						double height = graph.getGraphBounds().getHeight();
//						double widthLayout = graphComponent.getLayoutAreaSize().getWidth();
//						double heightLayout = graphComponent.getLayoutAreaSize().getHeight();
//						graph.getModel().setGeometry(graph.getDefaultParent(),
//								new mxGeometry((widthLayout - width)/2,
//										(heightLayout - height)/2,
//										widthLayout, heightLayout));
//
//						graph.getModel().endUpdate();
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


		JSplitPane outer2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				innerLeft1,
				graphComponent);
		outer2.setResizeWeight(0.2);
		outer2.setOneTouchExpandable(true);
		outer2.setDividerSize(25);
		outer2.setBorder(null);

		// Creates the status bar
		statusBar = createStatusBar();

		// Display some useful information about repaint events
		installRepaintListener();

		// Puts everything together
		setLayout(new BorderLayout());
		add(outer2, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);

		mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
		layout.setForceConstant(150);
		layout.setMinDistanceLimit(5);

		installHandlers();
		installListeners();
		updateTitle();
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
							if (pem.revObjTypes.contains(obj.objectType.name)||
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
				+ (int) (100 * graphComponent.getGraph().getView().getScale())
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
					ProcessExecutionPanel.this.mouseWheelMoved(e);
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
		EditorPopupMenu menu = new EditorPopupMenu(ProcessExecutionPanel.this);
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
				ProcessExecutionPanel.class.getResource(iconUrl)) : null)
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

	}


}

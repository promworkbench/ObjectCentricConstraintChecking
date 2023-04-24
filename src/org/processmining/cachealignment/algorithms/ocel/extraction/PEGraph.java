package org.processmining.cachealignment.algorithms.ocel.extraction;

import org.processmining.cachealignment.algorithms.analysis.mxAnalysisGraph;
import org.processmining.cachealignment.algorithms.analysis.mxGraphGenerator;
import org.processmining.cachealignment.algorithms.analysis.mxGraphProperties;
import org.processmining.cachealignment.algorithms.editor.EditorAboutFrame;
import org.processmining.cachealignment.algorithms.editor.EditorKeyboardHandlerForOCCLEditor;
import org.processmining.cachealignment.algorithms.editor.EditorPopupMenu;
import org.processmining.cachealignment.algorithms.layout.*;
import org.processmining.cachealignment.algorithms.model.mxCell;
import org.processmining.cachealignment.algorithms.model.mxGeometry;
import org.processmining.cachealignment.algorithms.model.mxICell;
import org.processmining.cachealignment.algorithms.model.mxIGraphModel;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEvent;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEventComparator;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEventLog;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelObject;
import org.processmining.cachealignment.algorithms.swing.mxGraphComponent;
import org.processmining.cachealignment.algorithms.swing.mxGraphOutline;
import org.processmining.cachealignment.algorithms.swing.util.mxMorphing;
import org.processmining.cachealignment.algorithms.view.mxGraph;
import org.processmining.cachealignment.algorithms.editor.*;
import org.processmining.cachealignment.algorithms.io.mxCodec;
import org.processmining.cachealignment.algorithms.layout.hierarchical.mxHierarchicalLayout;
import org.processmining.cachealignment.algorithms.layout.*;
import org.processmining.cachealignment.algorithms.swing.handler.mxKeyboardHandlerForOCCLEditor;
import org.processmining.cachealignment.algorithms.swing.handler.mxRubberband;
import org.processmining.cachealignment.algorithms.util.*;
import org.processmining.cachealignment.algorithms.util.*;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.cachealignment.algorithms.ocel.constraint.ConstraintModel;
import org.processmining.cachealignment.algorithms.ocel.occl.GraphEditor;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class PEGraph extends JPanel
{

	private JProgressBar bar = new JProgressBar(JProgressBar.HORIZONTAL,0,100);

	protected mxAnalysisGraph aGraph = new mxAnalysisGraph();


	ConstraintModel cm = ConstraintModel.getInstance();


	private String[] colorMap = {
			"#800000","#A52A2A",
			"#DC143C","#FF0000",
			"#FF7F50","#F08080",
			"#E9967A","#FF8C00",
			"#B8860B","#DAA520"};

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

	protected HashSet<String> selectedObjSet = new HashSet<>();

	/**
	 *
	 */
	protected mxGraphOutline graphOutline;

	/**
	 *
	 */
	protected JTabbedPane processExecutionPane;


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

	protected int selectedRow = -1;
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
	public PEGraph() {
//		// Stores and updates the frame title
//		this.appTitle = "Process Execution";
//
//		// Stores a reference to the graph and creates the command history
//		graphComponent = new CustomGraphComponent(new CustomGraph());
//		final mxGraph graph = graphComponent.getGraph();
//
//		graph.setDisconnectOnMove(false);
//
//		undoManager = createUndoManager();
//
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
//		mxIEventListener undoHandler = new mxIEventListener() {
//			public void invoke(Object source, mxEventObject evt) {
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
//		// Creates the graph outline component, å�¯ä»¥æ‹½æ�¥æ‹½åŽ»
//		JTable table = getEvtTable(graphComponent.getGraph());
//
//		// Creates the outer split pane that contains the inner split pane and
//		// the graph component on the right side of the window
//		JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
//				table,
//				graphComponent);
//		outer.setResizeWeight(0.1);
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
//
//		add(statusBar, BorderLayout.SOUTH);
//
//		mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
//		layout.setForceConstant(150);
//		layout.setMinDistanceLimit(5);
//
//		installHandlers();
//		installListeners();
//		updateTitle();
	}

	public PEGraph(UIPluginContext context,
				   Map<String, Map> leadObjMap,
				   Map<String, HashSet> nodeMap,
				   Map<OcelObject, Map> edgeMap,
				   String primaryObj,
				   HashSet<String> supplementaryObjs,
				   OcelEventLog ocel
				   ) {
		// Stores and updates the frame title
		this.appTitle = "Process execution";

		// Stores a reference to the graph and creates the command history
		graphComponent = new CustomGraphComponent(new CustomGraph());
		bar.setIndeterminate(true);
		bar.setStringPainted(true);
		bar.setBorderPainted(true);
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
		// Creates the graph outline component
		graphOutline = new mxGraphOutline(graphComponent);
		JTable table = getEvtTable(
				graphComponent.getGraph(),
				leadObjMap,
				nodeMap,
				edgeMap,
				ocel
				);

		JPanel jpObj = new JPanel();

		// 3 + supplementary obj num
		Integer rowNum = 2 + supplementaryObjs.size();
		jpObj.setLayout((new GridLayout(rowNum, 1, 0, 20)));
		jpObj.add(new JLabel("Highlight events with the selected:"));
		for (String obj:supplementaryObjs){
			JCheckBox jcb = new JCheckBox(obj);
			jcb.addItemListener(e -> {
				if (e.getStateChange() == ItemEvent.SELECTED) {

					selectedObjSet.add(jcb.getText());
					if (selectedRow > -1){
						graph.getModel().beginUpdate();
						graph.selectAll();
						graph.removeCells();
						Object parent = graph.getDefaultParent();
						String leadingObj = (String) table.getModel().getValueAt(selectedRow, 1);

						// get all the node
						ArrayList<OcelEvent> evtList =new ArrayList<>(); //Creation of ArrayList
						evtList.addAll(nodeMap.get(leadingObj)); //HashSet to ArrayList

						Collections.sort(evtList, new OcelEventComparator());
						Map<String, Integer> edgeIdxToEvtId = new HashMap<>();
						Object[] vertices = new Object[evtList.size()];

						int i = 0;
						for (OcelEvent evt : evtList) {
							vertices[i] = graph.insertVertex(
									parent,
									evt.id,
									evt.activity+"\n"+evt.timestamp,
									200,
									0,
									50,
									50,
									"ellipse;fontSize=12"
									);

							edgeIdxToEvtId.put(evt.id, i);
							i += 1;
						}
						Map<OcelObject, List<OcelEvent>> edgeMapForObj = edgeMap.get(leadingObj);

						int idx = 0;
						for (OcelObject k2 : edgeMapForObj.keySet())
						{
							List<OcelEvent> edgeMapLst = edgeMapForObj.get(k2);
							if(!selectedObjSet.contains(k2.objectType.name)){
								for (int j = 0; j < edgeMapLst.size()-1; j++){
									graph.insertEdge(parent,
											null,
											k2.id,
											vertices[edgeIdxToEvtId.get(edgeMapLst.get(j).id)],
											vertices[edgeIdxToEvtId.get(edgeMapLst.get(j+1).id)],
											"startArrow=none;endArrow=classic;fontSize=8;strokeWidth=1");
								}
							}
							else {
								for (int j = 0; j < edgeMapLst.size() - 1; j++) {
									graph.insertEdge(parent,
											null,
											k2.id,
											vertices[edgeIdxToEvtId.get(edgeMapLst.get(j).id)],
											vertices[edgeIdxToEvtId.get(edgeMapLst.get(j + 1).id)],
											"startArrow=none;endArrow=classic;fontSize=12;strokeWidth=3;strokeColor=" +
													colorMap[idx % 10] + ";fontColor=" + colorMap[idx % 10]);
								}
							}
							idx += 1;

						}
						// set layout of the process exe
						mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
						layout.setParallelEdgeSpacing(80);
						layout.setIntraCellSpacing(150);
						layout.setInterRankCellSpacing(60);
						layout.setInterHierarchySpacing(20);
						layout.execute(graph.getDefaultParent());
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
						ArrayList<OcelEvent> evtList =new ArrayList<>(); //Creation of ArrayList
						evtList.addAll(nodeMap.get(leadingObj)); //HashSet to ArrayList

						Collections.sort(evtList, new OcelEventComparator());
						Map<String, Integer> edgeIdxToEvtId = new HashMap<>();
						Object[] vertices = new Object[evtList.size()];

						int i = 0;
						for (OcelEvent evt : evtList) {
							vertices[i] = graph.insertVertex(parent,
									evt.id,
									evt.activity+"\n"+evt.timestamp,
									200, 0,
									50,
									50,
									"ellipse;fontSize=12");
							edgeIdxToEvtId.put(evt.id, i);
							i += 1;
						}
						Map<OcelObject, List<OcelEvent>> edgeMapForObj = edgeMap.get(leadingObj);

						int idx = 0;
						for (OcelObject k2 : edgeMapForObj.keySet())
						{
							List<OcelEvent> edgeMapLst = edgeMapForObj.get(k2);
							if(!selectedObjSet.contains(k2.objectType.name)){
								for (int j = 0; j < edgeMapLst.size()-1; j++){
									graph.insertEdge(parent,
											null,
											k2.id,
											vertices[edgeIdxToEvtId.get(edgeMapLst.get(j).id)],
											vertices[edgeIdxToEvtId.get(edgeMapLst.get(j+1).id)],
											"startArrow=none;endArrow=classic;fontSize=8;strokeWidth=1");
								}
							}
							else {
								for (int j = 0; j < edgeMapLst.size() - 1; j++) {
									graph.insertEdge(parent,
											null,
											k2.id,
											vertices[edgeIdxToEvtId.get(edgeMapLst.get(j).id)],
											vertices[edgeIdxToEvtId.get(edgeMapLst.get(j + 1).id)],
											"startArrow=none;endArrow=classic;fontSize=12;strokeWidth=3;strokeColor=" +
													colorMap[idx % 10] + ";fontColor=" + colorMap[idx % 10]);
								}
							}
							idx += 1;

						}
						// set layout of the process exe
						mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
						layout.setParallelEdgeSpacing(80);
						layout.setIntraCellSpacing(150);
						layout.setInterRankCellSpacing(60);
						layout.setInterHierarchySpacing(20);
						layout.execute(graph.getDefaultParent());
						graph.getModel().endUpdate();
					}

				}
			});
			jcb.setSelected(false);
			jpObj.add(jcb);
		}
		JCheckBox jcb = new JCheckBox(primaryObj);
		jcb.setSelected(false);
		jcb.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {

				selectedObjSet.add(jcb.getText());
				if (selectedRow > -1){
					graph.getModel().beginUpdate();
					graph.selectAll();
					graph.removeCells();
					Object parent = graph.getDefaultParent();
					String leadingObj = (String) table.getModel().getValueAt(selectedRow, 1);

					// get all the node
					ArrayList<OcelEvent> evtList =new ArrayList<>(); //Creation of ArrayList
					evtList.addAll(nodeMap.get(leadingObj)); //HashSet to ArrayList

					Collections.sort(evtList, new OcelEventComparator());
					Map<String, Integer> edgeIdxToEvtId = new HashMap<>();
					Object[] vertices = new Object[evtList.size()];

					int i = 0;
					for (OcelEvent evt : evtList) {
						vertices[i] = graph.insertVertex(parent,
								evt.id,
								evt.activity,
								200, 0,
								50,
								50,
								"ellipse;fontSize=12");
						edgeIdxToEvtId.put(evt.id, i);
						i += 1;
					}
					Map<OcelObject, List<OcelEvent>> edgeMapForObj = edgeMap.get(leadingObj);

					int idx = 0;
					for (OcelObject k2 : edgeMapForObj.keySet())
					{
						List<OcelEvent> edgeMapLst = edgeMapForObj.get(k2);
						if(!selectedObjSet.contains(k2.objectType.name)){
							for (int j = 0; j < edgeMapLst.size()-1; j++){
								graph.insertEdge(parent,
										null,
										k2.id,
										vertices[edgeIdxToEvtId.get(edgeMapLst.get(j).id)],
										vertices[edgeIdxToEvtId.get(edgeMapLst.get(j+1).id)],
										"startArrow=none;endArrow=classic;fontSize=8;strokeWidth=1");
							}
						}
						else {
							for (int j = 0; j < edgeMapLst.size() - 1; j++) {
								graph.insertEdge(parent,
										null,
										k2.id,
										vertices[edgeIdxToEvtId.get(edgeMapLst.get(j).id)],
										vertices[edgeIdxToEvtId.get(edgeMapLst.get(j + 1).id)],
										"startArrow=none;endArrow=classic;fontSize=12;strokeWidth=3;strokeColor=" +
												colorMap[idx % 10] + ";fontColor=" + colorMap[idx % 10]);
							}
						}
						idx += 1;

					}
					// set layout of the process exe
					mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
					layout.setParallelEdgeSpacing(80);
					layout.setIntraCellSpacing(150);
					layout.setInterRankCellSpacing(60);
					layout.setInterHierarchySpacing(20);
					layout.execute(graph.getDefaultParent());
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
					ArrayList<OcelEvent> evtList =new ArrayList<>(); //Creation of ArrayList
					evtList.addAll(nodeMap.get(leadingObj)); //HashSet to ArrayList

					Collections.sort(evtList, new OcelEventComparator());
					Map<String, Integer> edgeIdxToEvtId = new HashMap<>();
					Object[] vertices = new Object[evtList.size()];

					int i = 0;
					for (OcelEvent evt : evtList) {
						vertices[i] = graph.insertVertex(parent,
								evt.id,
								evt.activity,
								200, 0,
								50,
								50,
								"ellipse;fontSize=12");
						edgeIdxToEvtId.put(evt.id, i);
						i += 1;
					}
					Map<OcelObject, List<OcelEvent>> edgeMapForObj = edgeMap.get(leadingObj);

					int idx = 0;
					for (OcelObject k2 : edgeMapForObj.keySet())
					{
						List<OcelEvent> edgeMapLst = edgeMapForObj.get(k2);
						if(!selectedObjSet.contains(k2.objectType.name)){
							for (int j = 0; j < edgeMapLst.size()-1; j++){
								graph.insertEdge(parent,
										null,
										k2.id,
										vertices[edgeIdxToEvtId.get(edgeMapLst.get(j).id)],
										vertices[edgeIdxToEvtId.get(edgeMapLst.get(j+1).id)],
										"startArrow=none;endArrow=classic;fontSize=8;strokeWidth=1");
							}
						}
						else {
							for (int j = 0; j < edgeMapLst.size() - 1; j++) {
								graph.insertEdge(parent,
										null,
										k2.id,
										vertices[edgeIdxToEvtId.get(edgeMapLst.get(j).id)],
										vertices[edgeIdxToEvtId.get(edgeMapLst.get(j + 1).id)],
										"startArrow=none;endArrow=classic;fontSize=12;strokeWidth=3;strokeColor=" +
												colorMap[idx % 10] + ";fontColor=" + colorMap[idx % 10]);
							}
						}
						idx += 1;

					}
					// set layout of the process exe
					mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
					layout.setParallelEdgeSpacing(80);
					layout.setIntraCellSpacing(150);
					layout.setInterRankCellSpacing(60);
					layout.setInterHierarchySpacing(20);
					layout.execute(graph.getDefaultParent());
					graph.getModel().endUpdate();
				}

			}
		});
		jpObj.add(jcb);

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

		JSplitPane inner = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				scrollPaneObj,
				scrollPane);
		inner.setResizeWeight(0.2);
		inner.setDividerSize(6);
		inner.setBorder(null);

		// Creates the outer split pane that contains the inner split pane and
		// the graph component on the right side of the window
		JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				inner,
				graphComponent);
		outer.setResizeWeight(0.2);
		outer.setDividerSize(6);
		outer.setBorder(null);
		// Creates the status bar
		statusBar = createStatusBar();
		// Display some useful information about repaint events
		installRepaintListener();

		// Puts everything together
		setLayout(new BorderLayout());
		add(outer, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);
		mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
		layout.setForceConstant(150);
		layout.setMinDistanceLimit(5);
		installHandlers();
		installListeners();
		updateTitle();
	}


	private JTable getEvtTable(mxGraph graph,
							   Map<String, Map> leadObjMap,
							   Map<String, HashSet> nodeMap,
							   Map<OcelObject, Map> edgeMap,
							   OcelEventLog ocel) {

		Object [] title = {"Process Execution Id","Primary Object", "Secondary Objects"};
		Vector titlesV = new Vector(); // save title
		titlesV.add(title[0]);
		titlesV.add(title[1]);
		titlesV.add(title[2]);

		Vector<Vector> dataV = new Vector<Vector>(); // save data

		int count = 0;
		HashSet<Object> objSet2;
		for (Object key : leadObjMap.keySet()) {
			Vector<Object> t = new Vector<Object>();
			t.add(count);
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
				String leadingObj = (String) jtl.getModel().getValueAt(selectedRow, 1);

				// get all the node
				ArrayList<OcelEvent> evtList =new ArrayList<>(); //Creation of ArrayList
				evtList.addAll(nodeMap.get(leadingObj)); //HashSet to ArrayList

				Collections.sort(evtList, new OcelEventComparator());
				Map<String, Integer> edgeIdxToEvtId = new HashMap<>();
				Object[] vertices = new Object[evtList.size()];

				int i = 0;
				for (OcelEvent evt : evtList) {
					vertices[i] = graph.insertVertex(parent,
							evt.id,
							evt.activity,
							200, 0,
							50,
							50,
							"ellipse;fontSize=12");
					edgeIdxToEvtId.put(evt.id, i);
					i += 1;
				}
				Map<OcelObject, List<OcelEvent>> edgeMapForObj = edgeMap.get(leadingObj);

				int idx = 0;
				for (OcelObject k2 : edgeMapForObj.keySet())
					{
						List<OcelEvent> edgeMapLst = edgeMapForObj.get(k2);
						if(!selectedObjSet.contains(k2.objectType.name)){
							for (int j = 0; j < edgeMapLst.size()-1; j++){
								graph.insertEdge(parent,
										null,
										k2.id,
										vertices[edgeIdxToEvtId.get(edgeMapLst.get(j).id)],
										vertices[edgeIdxToEvtId.get(edgeMapLst.get(j+1).id)],
										"startArrow=none;endArrow=classic;fontSize=8;strokeWidth=1");
							}
						}
						else {
							for (int j = 0; j < edgeMapLst.size() - 1; j++) {
								graph.insertEdge(parent,
										null,
										k2.id,
										vertices[edgeIdxToEvtId.get(edgeMapLst.get(j).id)],
										vertices[edgeIdxToEvtId.get(edgeMapLst.get(j + 1).id)],
										"startArrow=none;endArrow=classic;fontSize=12;strokeWidth=3;strokeColor=" +
												colorMap[idx % 10] + ";fontColor=" + colorMap[idx % 10]);
							}
						}
						idx += 1;

					}
				// set layout of the process exe
				mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
				layout.setParallelEdgeSpacing(80);
				layout.setIntraCellSpacing(150);
				layout.setInterRankCellSpacing(60);
				layout.setInterHierarchySpacing(20);
				layout.execute(graph.getDefaultParent());
				graph.getModel().endUpdate();
			}
		});
		return jtl;
	}

	public static Map<String, Integer> sortMap(Map<String, Integer> map) {
		//利用Map的entrySet方法，转化为list进行排序
		List<Map.Entry<String, Integer>> entryList = new ArrayList<>(map.entrySet());
		//利用Collections的sort方法对list排序
		Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				//正序排列，倒序反过来
				return o1.getValue() - o2.getValue();
			}
		});
		//遍历排序好的list，一定要放进LinkedHashMap，因为只有LinkedHashMap是根据插入顺序进行存储
		LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String,Integer> e : entryList
		) {
			linkedHashMap.put(e.getKey(),e.getValue());
		}
		return linkedHashMap;
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

	/**
	 *
	 */
//	protected void installToolBar()
//	{
//		add(new EditorToolBar(this, JToolBar.HORIZONTAL), BorderLayout.NORTH);
//	}

	/**
	 *
	 */
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
					PEGraph.this.mouseWheelMoved(e);
				}
			}

		};


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
		EditorPopupMenu menu = new EditorPopupMenu(PEGraph.this);
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
		AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
				PEGraph.class.getResource(iconUrl)) : null)
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

			// get the cell to add
			Object[] cellToAdd = super.importCells(cells, dx, dy, target, location);
			graph.getModel().beginUpdate();
			try {
				if (((mxCell) cellToAdd[0]).getType().equals("objType")) {

					// Show a dialog to configure this object type
					getDialogForObjType(graph, (mxCell) cellToAdd[0], ((mxCell) cellToAdd[0]).getValue().toString());

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
					graph.addCell(ports.get(6), cellToAdd[0]);
					graph.addCell(ports.get(7), cellToAdd[0]);
					graph.addCell(ports.get(8), cellToAdd[0]);

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

	public static void getDialogForObjType(mxGraph graph, mxCell cell, String refObj){
		ArrayList<String> actList = new ArrayList<>();
		actList.add("Unspecified");
		actList.add("Any activity");
		actList.add("1");
		actList.add("2");
		actList.add("3");
		actList.add("4");
		actList.add("5");

		double startX = cell.getGeometry().getX();
		double startY = cell.getGeometry().getY();

		JDialog dialog = new JDialog();
		dialog.setTitle("Setup object type " + refObj);
		dialog.setMinimumSize(new Dimension(400,420));
		dialog.setMaximumSize(new Dimension(400,420));
		dialog.setPreferredSize(new Dimension(400,420));

		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);  // set to the center of the screen
		Box vBox = Box.createVerticalBox();
		vBox.setMinimumSize(new Dimension(400,380));
		vBox.setMaximumSize(new Dimension(400,380));
		vBox.setPreferredSize(new Dimension(400,380));
		Box hBox = Box.createHorizontalBox();


		// Select from the list of activities
		JPanel refActivityPanel = new JPanel(new GridLayout(actList.size(), 1, 0, 10));
		refActivityPanel.setMaximumSize(new Dimension(170,300));
		refActivityPanel.setMinimumSize(new Dimension(170,300));
		refActivityPanel.setPreferredSize(new Dimension(170,300));
		ButtonGroup refActivityBg = new ButtonGroup();
		for (String name : actList) {
			JRadioButton jrb = new JRadioButton(name);
			refActivityBg.add(jrb);
			refActivityPanel.add(jrb);
			jrb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					actName = name;
				}
			});
		}

		refActivityPanel.setBorder(BorderFactory.createTitledBorder("Reference activity"));

		JScrollPane jsp1 = new JScrollPane(refActivityPanel);
		hBox.add(jsp1);

		// Aggregation function in constraint panel
		JPanel constraintPanel = new JPanel();
		constraintPanel.setMaximumSize(new Dimension(150,300));
		constraintPanel.setMinimumSize(new Dimension(150,300));
		constraintPanel.setPreferredSize(new Dimension(150,300));

		Box hBoxTimeToTime = Box.createHorizontalBox();
		JTextField minAmountJtf = new JTextField();
		JTextField maxAmountJtf = new JTextField();
		minAmountJtf.setAlignmentX(JTextField.LEFT);

		minAmountJtf.setMaximumSize(new Dimension(30,20));
		minAmountJtf.setMinimumSize(new Dimension(30,20));
		minAmountJtf.setPreferredSize(new Dimension(30,20));
		maxAmountJtf.setMaximumSize(new Dimension(30,20));
		maxAmountJtf.setMinimumSize(new Dimension(30,20));
		maxAmountJtf.setPreferredSize(new Dimension(30,20));
		hBoxTimeToTime.add(minAmountJtf);
		hBoxTimeToTime.add(new JLabel(" to "));
		hBoxTimeToTime.add(maxAmountJtf);
		constraintPanel.setBorder(BorderFactory.createTitledBorder("The number of objects"));
		constraintPanel.add(hBoxTimeToTime);
		hBox.add(new JScrollPane(constraintPanel));

		// add a confirm button
		JButton jbConfirm = new JButton("Confirm");
		jbConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				graph.getModel().beginUpdate();
				cell.setStyle("ellipse;fillColor=#FFA836");
				// add port to the element
				ArrayList<mxCell> ports = getObjTypePorts();

//
//				if (actName.equals("Unspecified")){
////					graph.addCell(ports.get(0), cell);
////					graph.addCell(ports.get(1), cell);
//					graph.addCell(ports.get(3), cell);
//					graph.getModel().endUpdate();
//					dialog.dispose();
//					return;
//				}

				graph.addCell(ports.get(0), cell);
				graph.addCell(ports.get(1), cell);
				graph.addCell(ports.get(2), cell);
				graph.addCell(ports.get(3), cell);

				String valForRefAct;
				String minAmount = minAmountJtf.getText();
				String maxAmount = maxAmountJtf.getText();

				// insert conditions
				if (!minAmount.equals("") &&maxAmount.equals("")){
					valForRefAct = "â‰¥"+minAmount;
				}
				else if (minAmount.equals("") && !maxAmount.equals("")){
					valForRefAct = "â‰¤"+ maxAmount;
				}

				else if (!minAmount.equals("") && !maxAmount.equals("")){
					valForRefAct ="â‰¥"+minAmount+" and "+"â‰¤"+ maxAmount;
				}
				else {
					valForRefAct = "";
				}

				if (!valForRefAct.equals("")){
				// insert ref activity
					mxCell objAmountVertex = (mxCell) graph.insertVertex(
							graph.getDefaultParent(),
							"",
							"Amount:\n"+valForRefAct,
							"conditionForObj",
							startX-120, startY-60,
							80, 80,
							"rhombus;fillColor=#C0C0C0;fontSize=12");
					graph.insertEdge(graph.getDefaultParent(),
							null, "condition",
							cell.getChildAt(2),
							objAmountVertex,
							"straight;strokeWidth=2;endArrow=none;dashed=1;strokeColor=#C0C0C0");
				}

				mxCell refActVertex = (mxCell) graph.insertVertex(graph.getDefaultParent(),
						"",
						"Activity:\n"+actName,
						"refActForObj",
						startX-120, startY+60,
						80, 80,
						"rhombus;fillColor=#C0C0C0;fontSize=12");
				graph.insertEdge(graph.getDefaultParent(),
						null, "ref",
						cell.getChildAt(2),
						refActVertex,
						"straight;strokeWidth=2;endArrow=none;dashed=1;strokeColor=#C0C0C0");

				graph.getModel().endUpdate();
				dialog.dispose();
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
//
		Box hBox1 = Box.createHorizontalBox();
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(jbConfirm);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(jbCancel);

		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox1);
		vBox.add(Box.createVerticalStrut(10));

		cell.setStyle("strokeWidth=2;straight;startArrow=none;endArrow=none;strokeColor=#FFA836;dashed=1");

		dialog.getContentPane().add(vBox);
	}


	public static ArrayList<mxCell> getActivityPorts(){
		int PORT_DIAMETER = 12;

		int PORT_RADIUS = PORT_DIAMETER / 2;

		ArrayList<mxCell> ports = new ArrayList<>();

		mxGeometry geo1 = new mxGeometry(0, 0.3, PORT_DIAMETER,
				PORT_DIAMETER);
		geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo1.setRelative(true);
		mxCell port1 = new mxCell(null, geo1,
				"triangle;direction=west;fillColor=#71C562","actToActBeforePort",
				"Connect to activities occur before");
		port1.setVertex(true);

		mxGeometry geo2 = new mxGeometry(0, 0.7, PORT_DIAMETER,
				PORT_DIAMETER);
		geo2.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo2.setRelative(true);
		mxCell port2 = new mxCell(null, geo2,
				"triangle;direction=west;fillColor=#71C562","actToActBeforePort",
				"Connect to activities occur before");
		port2.setVertex(true);

		mxGeometry geo3 = new mxGeometry(1, 0.3, PORT_DIAMETER,
				PORT_DIAMETER);
		geo3.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo3.setRelative(true);
		mxCell port3 = new mxCell(null, geo3,
				"triangle;direction=east;fillColor=#71C562","actToActAfterPort",
				"Connect to activities occur afterward");
		port3.setVertex(true);

		mxGeometry geo4 = new mxGeometry(1, 0.7, PORT_DIAMETER,
				PORT_DIAMETER);
		geo4.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo4.setRelative(true);
		mxCell port4 = new mxCell(null, geo4,
				"triangle;direction=east;timeRefPort;fillColor=#71C562","" +
				"actToActAfterPort", "Connect to activities occur afterward");
		port4.setVertex(true);


		mxGeometry geo5 = new mxGeometry(0.3, 0, PORT_DIAMETER,
				PORT_DIAMETER);
		geo5.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo5.setRelative(true);
		mxCell port5 = new mxCell(null, geo5,
				"triangle;direction=north;timeRefPort;fillColor=#FFA836",
				"actToObjBeforePort", "Connect to object types occur before");
		port5.setVertex(true);

		mxGeometry geo6 = new mxGeometry(0.7, 0, PORT_DIAMETER,
				PORT_DIAMETER);
		geo6.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo6.setRelative(true);
		mxCell port6 = new mxCell(null, geo6,
				"triangle;direction=north;timeRefPort;fillColor=#FFA836",
				"actToObjBeforePort", "Connect to object types occur before");
		port6.setVertex(true);

		mxGeometry geo7 = new mxGeometry(0.3, 1, PORT_DIAMETER,
				PORT_DIAMETER);
		geo7.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo7.setRelative(true);
		mxCell port7 = new mxCell(null, geo7,
				"triangle;direction=south;fillColor=#FFA836",
				"actToObjAfterPort", "Connect to object types occur afterward");
		port7.setVertex(true);

		mxGeometry geo8 = new mxGeometry(0.7, 1, PORT_DIAMETER,
				PORT_DIAMETER);
		geo8.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo8.setRelative(true);
		mxCell port8 = new mxCell(null, geo8,
				"triangle;direction=south;fillColor=#FFA836","actToObjAfterPort",
				"Connect to object types occur afterward");
		port8.setVertex(true);

		mxGeometry geo9 = new mxGeometry(0.5, 0.5, PORT_DIAMETER,
				PORT_DIAMETER);
		geo9.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo9.setRelative(true);
		mxCell port9 = new mxCell(null, geo9,
				"ellipse;fillColor=#7F00FF","actCardPort",
				"Connect to build cardinality");
		port9.setVertex(true);



		ports.add(port1);
		ports.add(port2);
		ports.add(port3);
		ports.add(port4);
		ports.add(port5);
		ports.add(port6);
		ports.add(port7);
		ports.add(port8);
		ports.add(port9);

		return ports;
	}

	public static ArrayList<mxCell> getObjTypePorts(){
		int PORT_DIAMETER = 12;

		int PORT_RADIUS = PORT_DIAMETER / 2;

		ArrayList<mxCell> ports = new ArrayList<>();

		mxGeometry geo1 = new mxGeometry(0.5, 0, PORT_DIAMETER,
				PORT_DIAMETER);
		geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo1.setRelative(true);
		mxCell port1 = new mxCell(null, geo1,
				"triangle;direction=north;fillColor=#FFA836",
				"objToActBeforePort",
				"Connect to activities occur before");
		port1.setVertex(true);

		mxGeometry geo2 = new mxGeometry(0.5, 1, PORT_DIAMETER,
				PORT_DIAMETER);
		geo2.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo2.setRelative(true);
		mxCell port2 = new mxCell(null, geo2,
				"triangle;direction=south;fillColor=#FFA836",
				"objToActAfterPort", "Connect to activities occur afterward");
		port2.setVertex(true);

		mxGeometry geo3 = new mxGeometry(0, 0.5, PORT_DIAMETER,
				PORT_DIAMETER);
		geo3.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo3.setRelative(true);
		mxCell port3 = new mxCell(null, geo3,
				"rhombus;fillColor=#C0C0C0",
				"objConditionPort", "Connect to reference activity and conditions");
		port3.setVertex(true);

		mxGeometry geo4 = new mxGeometry(0.5, 0.5, PORT_DIAMETER,
				PORT_DIAMETER);
		geo4.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo4.setRelative(true);
		mxCell port4 = new mxCell(null, geo4,
				"ellipse;fillColor=#7F00FF","objCardPort",
				"Connect to build cardinality");
		port4.setVertex(true);
		ports.add(port1);
		ports.add(port2);
		ports.add(port3);
		ports.add(port4);
		return ports;
	}


	public static void main(String[] args)
	{

		JFrame frame = new JFrame();

		//		model.setModelEnvironment(mxConstants.model_editor);
		PEGraph editor = new PEGraph();

		frame.add(editor);

		// get the screen size
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());

		// enlarge to the whole page
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		frame.setVisible(true);



		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
}

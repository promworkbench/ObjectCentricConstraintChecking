package org.processmining.cachealignment.algorithms.editor;

import org.processmining.cachealignment.algorithms.layout.*;
import org.processmining.cachealignment.algorithms.layout.*;
import org.processmining.cachealignment.algorithms.listener.GraphComponentMouseMotionListener;
import org.processmining.cachealignment.algorithms.listener.GraphComponentMouseWheelListener;
import org.processmining.cachealignment.algorithms.swing.mxGraphComponent;
import org.processmining.cachealignment.algorithms.swing.mxGraphOutline;
import org.processmining.cachealignment.algorithms.swing.util.mxMorphing;
import org.processmining.cachealignment.algorithms.view.mxGraph;
import org.processmining.cachealignment.algorithms.layout.hierarchical.mxHierarchicalLayout;
import org.processmining.cachealignment.algorithms.swing.handler.mxKeyboardHandler;
import org.processmining.cachealignment.algorithms.swing.handler.mxRubberband;
import org.processmining.cachealignment.algorithms.util.*;
import org.processmining.cachealignment.algorithms.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.List;

public class BasicGraphEditor extends JPanel
{
	protected EditorToolBar toolBar;

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

	/**
	 * 
	 */
	protected mxGraphOutline graphOutline;

	/**
	 * 
	 */
	protected JTabbedPane objPane;
	protected JTabbedPane actPane;

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
	protected mxKeyboardHandler keyboardHandler;

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
	public BasicGraphEditor(String appTitle, mxGraphComponent component)
	{
		// Stores and updates the frame title
		this.appTitle = appTitle;

		// Stores a reference to the graph and creates the command history
		graphComponent = component;
		final mxGraph graph = graphComponent.getGraph();
		undoManager = createUndoManager();

		// Do not change the scale and translation after files have been loaded
		graph.setResetViewOnRootChange(false);

		// Updates the modified flag if the graph model changes
		graph.getModel().addListener(mxEvent.CHANGE, changeTracker);

		// Adds the command history to the model and view
		graph.getModel().addListener(mxEvent.UNDO, undoHandler);
		graph.getView().addListener(mxEvent.UNDO, undoHandler);

		// Keeps the selection in sync with the command history
		mxEventSource.mxIEventListener undoHandler = new mxEventSource.mxIEventListener()
		{
			public void invoke(Object source, mxEventObject evt)
			{
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

		Box hBox = Box.createHorizontalBox();
		Box vBoxBtn = Box.createVerticalBox();
		JButton refreshBtn = new JButton("Refresh");
		JButton resetBtn = new JButton("Reset");
		JButton checkBtn = new JButton("Check");

		refreshBtn.setMaximumSize(new Dimension(80,20));
		refreshBtn.setMinimumSize(new Dimension(80,20));
		refreshBtn.setPreferredSize(new Dimension(80,20));
		resetBtn.setMaximumSize(new Dimension(80,20));
		resetBtn.setMinimumSize(new Dimension(80,20));
		resetBtn.setPreferredSize(new Dimension(80,20));
		checkBtn.setMaximumSize(new Dimension(80,20));
		checkBtn.setMinimumSize(new Dimension(80,20));
		checkBtn.setPreferredSize(new Dimension(80,20));

		vBoxBtn.add(Box.createVerticalStrut(3));
		vBoxBtn.add(refreshBtn);
		vBoxBtn.add(Box.createVerticalStrut(3));
		vBoxBtn.add(resetBtn);
		vBoxBtn.add(Box.createVerticalStrut(3));
		vBoxBtn.add(checkBtn);
		hBox.add(Box.createHorizontalStrut(10));
		hBox.add(vBoxBtn);

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
//				ConstraintModelChecker cmc = new ConstraintModelChecker();
//				cmc.checkOrderRelation();
			}
		});

		// Creates the library pane that contains the tabs with the palettes
		objPane = new JTabbedPane();

//		objPane.add(new JScrollPane(teamPanel),0);

		actPane = new JTabbedPane();

		JSplitPane inner1 = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT,
				objPane,
				actPane);
//		inner.setDividerLocation(0.60);
		inner1.setResizeWeight(0.5);
		inner1.setDividerSize(6);
		inner1.setBorder(null);

		// Creates the inner split pane that contains the library with the
		// palettes and the graph outline on the left side of the window
		JSplitPane inner2 = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT,
				inner1,
				graphOutline);
		inner2.setResizeWeight(0.8);
		inner2.setDividerSize(6);
		inner2.setBorder(null);

		JSplitPane inner3 = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT,
				inner2,
				hBox);
		inner3.setResizeWeight(0.95);
		inner3.setDividerSize(6);
		inner3.setBorder(null);

		// Creates the outer split pane that contains the inner split pane and
		// the graph component on the right side of the window
		JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				inner3,
				graphComponent);
//		outer.setOneTouchExpandable(true);
//		inner.setResizeWeight(0.3);
		outer.setResizeWeight(0.05);
		outer.setDividerSize(6);
		outer.setBorder(null);

		// Creates the status bar
		statusBar = createStatusBar();

		// Display some useful information about repaint events

		// Puts everything together
		setLayout(new BorderLayout());
		add(outer, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);

		installRepaintListener();

		// 建立tool bar
		installToolBar();

		// Installs rubberband selection and handling for some special
		// keystrokes such as F2, Control-C, -V, X, A etc.
		installHandlers();
		installListeners();
		updateTitle();
	}

	protected  void addListenersToGraphComponent(mxGraphComponent graphComponent){
		final mxGraph graph = graphComponent.getGraph();
		System.out.println("enter add listeners");
		// Do not change the scale and translation after files have been loaded
		graph.setResetViewOnRootChange(false);

		// Updates the modified flag if the graph model changes
		graph.getModel().addListener(mxEvent.CHANGE, changeTracker);

		// Adds the command history to the model and view
		graph.getModel().addListener(mxEvent.UNDO, undoHandler);
		graph.getView().addListener(mxEvent.UNDO, undoHandler);

		// Keeps the selection in sync with the command history
		mxEventSource.mxIEventListener undoHandler = new mxEventSource.mxIEventListener()
		{
			public void invoke(Object source, mxEventObject evt)
			{
				List<mxUndoableEdit.mxUndoableChange> changes = ((mxUndoableEdit) evt
						.getProperty("edit")).getChanges();
				graph.setSelectionCells(graph
						.getSelectionCellsForChanges(changes));
			}
		};

		undoManager.addListener(mxEvent.UNDO, undoHandler);
		undoManager.addListener(mxEvent.REDO, undoHandler);

//		// Creates the graph outline component
//		graphOutline = new mxGraphOutline(graphComponent);

				// Display some useful information about repaint events
		installRepaintListener();


		// Installs rubberband selection and handling for some special
		// keystrokes such as F2, Control-C, -V, X, A etc.
		installHandlers();
		installListeners();
		updateTitle();
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
		keyboardHandler = new EditorKeyboardHandler(graphComponent);
	}

	/**
	 * 
	 */
	protected void installToolBar()
	{
		EditorToolBar editorToolBar = new EditorToolBar(this, JToolBar.HORIZONTAL);
		toolBar = editorToolBar;
		add(editorToolBar, BorderLayout.NORTH);
	}

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
	protected void showGraphPopupMenu(MouseEvent e)
	{
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
				graphComponent);
		EditorPopupMenu menu = new EditorPopupMenu(BasicGraphEditor.this);
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
//	protected void installListeners()
//	{
////		// Installs mouse wheel listener for zooming
////		MouseWheelListener wheelTracker = new MouseWheelListener()
////		{
////			/**
////			 *
////			 */
////			public void mouseWheelMoved(MouseWheelEvent e)
////			{
////				if (e.getSource() instanceof mxGraphOutline
////						|| e.isControlDown())
////				{
////					BasicGraphEditor.this.mouseWheelMoved(e);
////				}
////			}
////
////		};
////
////		// Handles mouse wheel events in the outline and graph component
////		graphOutline.addMouseWheelListener(wheelTracker);
////		graphComponent.addMouseWheelListener(wheelTracker);
//		GraphComponentMouseWheelListener wheelTracker = new GraphComponentMouseWheelListener(this);
//		// Handles mouse wheel events in the outline and graph component
//		graphOutline.addMouseWheelListener(wheelTracker);
//		graphComponent.addMouseWheelListener(wheelTracker);
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
//			}
//
//		});
//
//		// Installs the popup menu in the graph component
//		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
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
//					showGraphPopupMenu(e);
//				}
//			}
//
//		});
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
//						mouseLocationChanged(e);
//					}
//
//					/*
//					 * (non-Javadoc)
//					 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
//					 */
//					public void mouseMoved(MouseEvent e)
//					{
//						// 可能没有必要
//						graphComponent.getGraph().refresh();
//					}
//
//				});
//	}

	protected void installListeners()
	{
		System.out.println("enter the install func");
		//create the mouse wheel event listener
		GraphComponentMouseWheelListener wheelTracker = new GraphComponentMouseWheelListener(this);
		// Handles mouse wheel events in the outline and graph component
		graphOutline.addMouseWheelListener(wheelTracker);
		graphComponent.addMouseWheelListener(wheelTracker);

		// Installs the popup menu in the graph component
//		GraphComponentMouseListener graphComponentMouseListener = new GraphComponentMouseListener(model, graphComponent);
//		graphComponent.getGraphControl().addMouseListener(graphComponentMouseListener);
//		graphComponentMouseListener.setModelEditor(this);

		// Installs a mouse motion listener to display the mouse location
		graphComponent.getGraphControl().addMouseMotionListener(new GraphComponentMouseMotionListener(this));
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
		AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
				BasicGraphEditor.class.getResource(iconUrl)) : null)
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
				keyboardHandler = new EditorKeyboardHandler(graphComponent);
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

	public EditorToolBar getToolBar(){
		return toolBar;
	}

//	public static void main(String[] args) {
//		BasicGraphEditor bs = new BasicGraphEditor("123", new mxGraphComponent(new mxGraph()));
//		bs.setVisible(true);
//	}

	public void setGraphComponent(mxGraphComponent graphComponent)
	{
		this.graphComponent = graphComponent;
	}
}

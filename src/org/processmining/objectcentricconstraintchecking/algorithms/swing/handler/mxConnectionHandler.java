/**
 * Copyright (c) 2008, Gaudenz Alder
 */
package org.processmining.objectcentricconstraintchecking.algorithms.swing.handler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.processmining.objectcentricconstraintchecking.algorithms.model.mxCell;
import org.processmining.objectcentricconstraintchecking.algorithms.model.mxGeometry;
import org.processmining.objectcentricconstraintchecking.algorithms.model.mxIGraphModel;
import org.processmining.objectcentricconstraintchecking.algorithms.ocel.constraint.ConstraintModel;
import org.processmining.objectcentricconstraintchecking.algorithms.ocel.constraint.ConstraintTPCM;
import org.processmining.objectcentricconstraintchecking.algorithms.swing.mxGraphComponent;
import org.processmining.objectcentricconstraintchecking.algorithms.swing.util.mxMouseAdapter;
import org.processmining.objectcentricconstraintchecking.algorithms.util.mxConstants;
import org.processmining.objectcentricconstraintchecking.algorithms.util.mxEvent;
import org.processmining.objectcentricconstraintchecking.algorithms.util.mxEventObject;
import org.processmining.objectcentricconstraintchecking.algorithms.util.mxEventSource;
import org.processmining.objectcentricconstraintchecking.algorithms.util.mxPoint;
import org.processmining.objectcentricconstraintchecking.algorithms.util.mxRectangle;
import org.processmining.objectcentricconstraintchecking.algorithms.view.mxCellState;
import org.processmining.objectcentricconstraintchecking.algorithms.view.mxGraph;
import org.processmining.objectcentricconstraintchecking.algorithms.view.mxGraphView;

/**
 * Connection handler creates new connections between cells. This control is used to display the connector
 * icon, while the preview is used to draw the line.
 * 
 * mxEvent.CONNECT fires between begin- and endUpdate in mouseReleased. The <code>cell</code>
 * property contains the inserted edge, the <code>event</code> and <code>target</code> 
 * properties contain the respective arguments that were passed to mouseReleased.
 */
public class mxConnectionHandler extends mxMouseAdapter
{
	private double startX;
	private double startY;
	private double targetX;
	private double targetY;
	
	protected String timeType;
	
	private String actToActName;

	private String refActForObjToObjCardinality;
	private String objRef;

	private String startActPortType;
	private String targetActPortType;
//	private HashMap objSelectedMap = new HashMap();

	private List<String> objTypeSelectedLst = new ArrayList();

	private String temporalCons;

	private String timePerformanceCons;
	ConstraintModel cm = ConstraintModel.getInstance();

	ConstraintTPCM tp = new ConstraintTPCM();

	protected static List<String> timeSymbol = Arrays.asList(
			"",
			"鈮�",
			">",
			"鈮�",
			"<");


	protected static List<String> timeUnit = Arrays.asList(
			"seconds",
			"minutes",
			"hours",
			"days",
			"weeks",
			"months");

	protected static List<String> card = Arrays.asList(
			"0",
			"1",
			"n"
			);

	/**
	 * 
	 */
	private static final long serialVersionUID = -2543899557644889853L;

	/**
	 * 
	 */
	public static Cursor CONNECT_CURSOR = new Cursor(Cursor.HAND_CURSOR);

	/**
	 * 
	 */
	protected mxGraphComponent graphComponent;

	/**
	 * Holds the event source.
	 */
	protected mxEventSource eventSource = new mxEventSource(this);

	/**
	 * 
	 */
	protected mxConnectPreview connectPreview;

	/**
	 * Specifies the icon to be used for creating new connections. If this is
	 * specified then it is used instead of the handle. Default is null.
	 */
	protected ImageIcon connectIcon = null;

	/**
	 * Specifies the size of the handle to be used for creating new
	 * connections. Default is mxConstants.CONNECT_HANDLE_SIZE. 
	 */
	protected int handleSize = mxConstants.CONNECT_HANDLE_SIZE;

	/**
	 * Specifies if a handle should be used for creating new connections. This
	 * is only used if no connectIcon is specified. If this is false, then the
	 * source cell will be highlighted when the mouse is over the hotspot given
	 * in the marker. Default is mxConstants.CONNECT_HANDLE_ENABLED.
	 */
	protected boolean handleEnabled = mxConstants.CONNECT_HANDLE_ENABLED;

	/**
	 * 
	 */
	protected boolean select = true;

	/**
	 * Specifies if the source should be cloned and used as a target if no
	 * target was selected. Default is false.
	 */
	protected boolean createTarget = false;

	/**
	 * Appearance and event handling order wrt subhandles.
	 */
	protected boolean keepOnTop = true;

	/**
	 * 
	 */
	protected boolean enabled = true;

	/**
	 * 
	 */
	protected transient Point first;

	/**
	 * 
	 */
	protected transient boolean active = false;

	/**
	 * 
	 */
	protected transient Rectangle bounds;

	/**
	 * 
	 */
	protected transient mxCellState source;

	/**
	 * 
	 */
	protected transient mxCellMarker marker;

	/**
	 * 
	 */
	protected transient String error;

	/**
	 * 
	 */
	protected transient mxEventSource.mxIEventListener resetHandler = new mxEventSource.mxIEventListener()
	{
		public void invoke(Object source, mxEventObject evt)
		{
			reset();
		}
	};

	/**
	 * 
	 * @param graphComponent
	 */
	public mxConnectionHandler(mxGraphComponent graphComponent)
	{
		this.graphComponent = graphComponent;

		// Installs the paint handler
		graphComponent.addListener(mxEvent.AFTER_PAINT, new mxEventSource.mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				Graphics g = (Graphics) evt.getProperty("g");
				paint(g);
			}
		});

		connectPreview = createConnectPreview();

		mxGraphComponent.mxGraphControl graphControl = graphComponent.getGraphControl();
		graphControl.addMouseListener(this);
		graphControl.addMouseMotionListener(this);

		// Installs the graph listeners and keeps them in sync
		addGraphListeners(graphComponent.getGraph());

		graphComponent.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt.getPropertyName().equals("graph"))
				{
					removeGraphListeners((mxGraph) evt.getOldValue());
					addGraphListeners((mxGraph) evt.getNewValue());
				}
			}
		});

		marker = new mxCellMarker(graphComponent)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 103433247310526381L;

			// Overrides to return cell at location only if valid (so that
			// there is no highlight for invalid cells that have no error
			// message when the mouse is released)
			protected Object getCell(MouseEvent e)
			{
				Object cell = super.getCell(e);

				if (isConnecting())
				{
					if (source != null)
					{
						error = validateConnection(source.getCell(), cell);

						if (error != null && error.length() == 0)
						{
							cell = null;

							// Enables create target inside groups
							if (createTarget)
							{
								error = null;
							}
						}
					}
				}
				else if (!isValidSource(cell))
				{
					cell = null;
				}

				return cell;
			}

			// Sets the highlight color according to isValidConnection
			protected boolean isValidState(mxCellState state)
			{
				if (isConnecting())
				{
					return error == null;
				}
				else
				{
					return super.isValidState(state);
				}
			}

			// Overrides to use marker color only in highlight mode or for
			// target selection
			protected Color getMarkerColor(MouseEvent e, mxCellState state,
					boolean isValid)
			{
				return (isHighlighting() || isConnecting()) ? super
						.getMarkerColor(e, state, isValid) : null;
			}

			// Overrides to use hotspot only for source selection otherwise
			// intersects always returns true when over a cell
			protected boolean intersects(mxCellState state, MouseEvent e)
			{
				if (!isHighlighting() || isConnecting())
				{
					return true;
				}

				return super.intersects(state, e);
			}
		};

		marker.setHotspotEnabled(true);
	}

	/**
	 * Installs the listeners to update the handles after any changes.
	 */
	protected void addGraphListeners(mxGraph graph)
	{
		// LATER: Install change listener for graph model, view
		if (graph != null)
		{
			mxGraphView view = graph.getView();
			view.addListener(mxEvent.SCALE, resetHandler);
			view.addListener(mxEvent.TRANSLATE, resetHandler);
			view.addListener(mxEvent.SCALE_AND_TRANSLATE, resetHandler);

			graph.getModel().addListener(mxEvent.CHANGE, resetHandler);
		}
	}

	/**
	 * Removes all installed listeners.
	 */
	protected void removeGraphListeners(mxGraph graph)
	{
		if (graph != null)
		{
			mxGraphView view = graph.getView();
			view.removeListener(resetHandler, mxEvent.SCALE);
			view.removeListener(resetHandler, mxEvent.TRANSLATE);
			view.removeListener(resetHandler, mxEvent.SCALE_AND_TRANSLATE);

			graph.getModel().removeListener(resetHandler, mxEvent.CHANGE);
		}
	}

	/**
	 * 
	 */
	protected mxConnectPreview createConnectPreview()
	{
		return new mxConnectPreview(graphComponent);
	}

	/**
	 * 
	 */
	public mxConnectPreview getConnectPreview()
	{
		return connectPreview;
	}

	/**
	 * 
	 */
	public void setConnectPreview(mxConnectPreview value)
	{
		connectPreview = value;
	}

	/**
	 * Returns true if the source terminal has been clicked and a new
	 * connection is currently being previewed.
	 */
	public boolean isConnecting()
	{
		return connectPreview.isActive();
	}

	/**
	 * 
	 */
	public boolean isActive()
	{
		return active;
	}
	
	/**
	 * Returns true if no connectIcon is specified and handleEnabled is false.
	 */
	public boolean isHighlighting()
	{
		return connectIcon == null && !handleEnabled;
	}

	/**
	 * 
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * 
	 */
	public void setEnabled(boolean value)
	{
		enabled = value;
	}

	/**
	 * 
	 */
	public boolean isKeepOnTop()
	{
		return keepOnTop;
	}

	/**
	 * 
	 */
	public void setKeepOnTop(boolean value)
	{
		keepOnTop = value;
	}

	/**
	 * 
	 */
	public void setConnectIcon(ImageIcon value)
	{
		connectIcon = value;
	}

	/**
	 * 
	 */
	public ImageIcon getConnecIcon()
	{
		return connectIcon;
	}

	/**
	 * 
	 */
	public void setHandleEnabled(boolean value)
	{
		handleEnabled = value;
	}

	/**
	 * 
	 */
	public boolean isHandleEnabled()
	{
		return handleEnabled;
	}

	/**
	 * 
	 */
	public void setHandleSize(int value)
	{
		handleSize = value;
	}

	/**
	 * 
	 */
	public int getHandleSize()
	{
		return handleSize;
	}

	/**
	 * 
	 */
	public mxCellMarker getMarker()
	{
		return marker;
	}

	/**
	 * 
	 */
	public void setMarker(mxCellMarker value)
	{
		marker = value;
	}

	/**
	 * 
	 */
	public void setCreateTarget(boolean value)
	{
		createTarget = value;
	}

	/**
	 * 
	 */
	public boolean isCreateTarget()
	{
		return createTarget;
	}

	/**
	 * 
	 */
	public void setSelect(boolean value)
	{
		select = value;
	}

	/**
	 * 
	 */
	public boolean isSelect()
	{
		return select;
	}

	/**
	 * 
	 */
	public void reset()
	{
		connectPreview.stop(false);
		setBounds(null);
		marker.reset();
		active = false;
		source = null;
		first = null;
		error = null;
	}

	/**
	 * 
	 */
	public Object createTargetVertex(MouseEvent e, Object source)
	{
		mxGraph graph = graphComponent.getGraph();
		Object clone = graph.cloneCells(new Object[] { source })[0];
		mxIGraphModel model = graph.getModel();
		mxGeometry geo = model.getGeometry(clone);

		if (geo != null)
		{
			mxPoint point = graphComponent.getPointForEvent(e);
			geo.setX(graph.snap(point.getX() - geo.getWidth() / 2));
			geo.setY(graph.snap(point.getY() - geo.getHeight() / 2));
		}

		return clone;
	}

	/**
	 * 
	 */
	public boolean isValidSource(Object cell)
	{
		return graphComponent.getGraph().isValidSource(cell);
	}

	/**
	 * Returns true. The call to mxGraph.isValidTarget is implicit by calling
	 * mxGraph.getEdgeValidationError in validateConnection. This is an
	 * additional hook for disabling certain targets in this specific handler.
	 */
	public boolean isValidTarget(Object cell)
	{
		return true;
	}

	/**
	 * Returns the error message or an empty string if the connection for the
	 * given source target pair is not valid. Otherwise it returns null.
	 */
	public String validateConnection(Object source, Object target)
	{
		if (target == null && createTarget)
		{
			return null;
		}

		if (!isValidTarget(target))
		{
			return "";
		}

		return graphComponent.getGraph().getEdgeValidationError(
				connectPreview.getPreviewState().getCell(), source, target);
	}


	/**
	 * 
	 */
	public void start(MouseEvent e, mxCellState state)
	{
		first = e.getPoint();
		mxCell c1 = (mxCell) graphComponent.getCellAt(e.getX(),e.getY());
		cm.startEntityName = c1.getParent().getValue().toString();
		startActPortType = c1.getType();
		startX = c1.getParent().getGeometry().getX();
		startY = c1.getParent().getGeometry().getY();
		connectPreview.start(e, state, "");
	}


	/**
	 * 
	 */
	public void mouseReleased(MouseEvent e)
	{

		if (isActive())
		{
			if (error != null)
			{
				if (error.length() > 0)
				{
					JOptionPane.showMessageDialog(graphComponent, error);
				}
			}
			else if (first != null)
			{
				mxGraph graph = graphComponent.getGraph();
				double dx = first.getX() - e.getX();
				double dy = first.getY() - e.getY();

				mxCell c1 = (mxCell) graphComponent.getCellAt(e.getX(),e.getY());
				cm.targetEntityName = c1.getParent().getValue().toString();
				targetActPortType = c1.getType();

				targetX = c1.getParent().getGeometry().getX();
				targetY = c1.getParent().getGeometry().getY();

				if (connectPreview.isActive()
						&& (marker.hasValidState() || isCreateTarget() || graph
						.isAllowDanglingEdges()))
				{
					graph.getModel().beginUpdate();

					try
					{
						Object dropTarget = null;

						if (!marker.hasValidState() && isCreateTarget())
						{
							Object vertex = createTargetVertex(e, source.getCell());

							dropTarget = graph.getDropTarget(
									new Object[] { vertex }, e.getPoint(),
									graphComponent.getCellAt(e.getX(), e.getY()));

							if (vertex != null)
							{
								// Disables edges as drop targets if the target cell was created
								if (dropTarget == null
										|| !graph.getModel().isEdge(dropTarget))
								{
									mxCellState pstate = graph.getView().getState(
											dropTarget);

									if (pstate != null)
									{
										mxGeometry geo = graph.getModel()
												.getGeometry(vertex);

										mxPoint origin = pstate.getOrigin();
										geo.setX(geo.getX() - origin.getX());
										geo.setY(geo.getY() - origin.getY());
									}
								}
								else
								{
									dropTarget = graph.getDefaultParent();
								}

								graph.addCells(new Object[] { vertex }, dropTarget);
							}

							// FIXME: Here we pre-create the state for the vertex to be
							// inserted in order to invoke update in the connectPreview.
							// This means we have a cell state which should be created
							// after the model.update, so this should be fixed.
							mxCellState targetState = graph.getView().getState(
									vertex, true);
							connectPreview.update(e, targetState, e.getX(),
									e.getY());
						}

						Object cell = connectPreview.stop(
								graphComponent.isSignificant(dx, dy), e);

						// if the cell is the line connecting two activities
						if (cell != null)
						{
							// add port to the connection
							actToActName = cm.startEntityName+cm.targetEntityName;


							System.out.println(startActPortType + targetActPortType);

							// if two activities are already connected by an edge, then delete cell
							if (cm.alreadySelected.containsKey(actToActName)){
								((mxCell) cell).setStyle("straight;strokeColor=#71C562;strokeWidth=4");
								graph.getModel().remove(cell);
								graph.getModel().endUpdate();
								return;
							}

							//---------------time performance constraint for activity-------
							else if (startActPortType.equals("actPerfPort")&&
									targetActPortType.equals("perfTimePort")){
								((mxCell) cell).setStyle("straight;startArrow=None;endArrow=None;" +
										"strokeColor=#8B4513;strokeWidth=3");
								System.out.println(startActPortType + targetActPortType);
								String actName = cm.startEntityName;
								getActivityTimePerformanceDialog(cell, graph, actName);
							}

							else if (startActPortType.equals("perfTimePort")&&
									targetActPortType.equals("actPerfPort")){
								((mxCell) cell).setStyle("straight;startArrow=None;endArrow=None;" +
										"strokeColor=#8B4513;strokeWidth=3");
								String actName = c1.getParent().getValue().toString();
								System.out.print("Time type: "+timeType);
								getActivityTimePerformanceDialog(cell, graph, actName);
							}
							//-------------------------------------------------

							//----activity to activity cardinality constraint------------
							else if (startActPortType.equals("actToActBeforeCardPort")&&
									targetActPortType.equals("actToActAfterCardPort")){
								String firstActName = c1.getParent().getValue().toString();
								String secondActName = cm.startEntityName;
								getActToActCardDialog(cell,graph,firstActName,secondActName);
							}
							else if (startActPortType.equals("actToActAfterCardPort")&&
									targetActPortType.equals("actToActBeforeCardPort")){
								String secondActName = c1.getParent().getValue().toString();
								String firstActName = cm.startEntityName;
								getActToActCardDialog(cell,graph,firstActName,secondActName);
							}
							//--------------------------------------------------------

							//----activity to activity temporal constraint------------
							else if (startActPortType.equals("actToActBeforeTimePort")&&
									targetActPortType.equals("actToActAfterTimePort")){
								String firstActName = c1.getParent().getValue().toString();
								String secondActName = cm.startEntityName;
								getActToActTimeDialog(cell,graph,firstActName,
										secondActName);
							}
							else if (startActPortType.equals("actToActAfterTimePort")&&
									targetActPortType.equals("actToActBeforeTimePort")){
								String secondActName = c1.getParent().getValue().toString();
								String firstActName = cm.startEntityName;
								getActToActTimeDialog(cell,graph,firstActName,
										secondActName);
							}
							//--------------------------------------------------------

							//----activity to object type cardinality constraint------------
							else if (startActPortType.equals("objPort")&&
									targetActPortType.equals("actToObjPort")){
								String refObjType = c1.getParent().getValue().toString();
								String actName = cm.startEntityName;
								((mxCell) cell).setStyle("straight;startArrow=none;endArrow=none;strokeColor=#FF4500;strokeWidth=4");
								getActToObjDialog(cell,graph,actName,refObjType);
							}
							else if (startActPortType.equals("actToObjPort")&&
									targetActPortType.equals("objPort")){
								String actName = c1.getParent().getValue().toString();
								String refObjType = cm.startEntityName;
								((mxCell) cell).setStyle("straight;startArrow=none;endArrow=none;strokeColor=#FF4500;strokeWidth=4");
								getActToObjDialog(cell,graph,refObjType,actName);
							}
							//--------------------------------------------------------

							//----activity to frequency constraint------------
							else if (startActPortType.equals("perfFreqPort")&&
									targetActPortType.equals("actPerfPort")){
								((mxCell) cell).setStyle("straight;startArrow=None;endArrow=None;" +
										"strokeColor=#8B4513;strokeWidth=3");
								String secondActName = cm.startEntityName;
								getActFreqDialog(cell,graph,secondActName);
							}
							else if (startActPortType.equals("actPerfPort")&&
									targetActPortType.equals("perfFreqPort")){
								((mxCell) cell).setStyle("straight;startArrow=None;endArrow=None;" +
										"strokeColor=#8B4513;strokeWidth=3");
								String firstActName = cm.startEntityName;
								getActFreqDialog(cell,graph,firstActName);
							}
							//--------------------------------------------------------
							graphComponent.getGraph().setSelectionCell(cell);
							eventSource.fireEvent(new mxEventObject(
									mxEvent.CONNECT, "cell", cell, "event", e,
									"target", dropTarget));
						}

						e.consume();
					}  finally
					{
						graph.getModel().endUpdate();
					}
				}
			}
		}

		reset();
	}



	public void getActToActCardDialog(Object cell,
									  mxGraph graph,
									  String firstAct,
									  String secondAct) {
		JDialog dialog = new JDialog();
		dialog.setTitle("Activity-activity cardinality setting");
		dialog.setMinimumSize(new Dimension(250,400));
		dialog.setMaximumSize(new Dimension(250,300));
		dialog.setPreferredSize(new Dimension(250,300));
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);  // set to the center of the screen

		Box vBox = Box.createVerticalBox();
		vBox.setMinimumSize(new Dimension(250,320));
		vBox.setMaximumSize(new Dimension(250,320));
		vBox.setPreferredSize(new Dimension(250,320));

		Box hBox = Box.createHorizontalBox();
		hBox.setMinimumSize(new Dimension(200,250));
		hBox.setMaximumSize(new Dimension(200,250));
		hBox.setPreferredSize(new Dimension(200,250));

		List<JCheckBox> jcbLst = new ArrayList<>();
		// ---set object type---
//		JPanel revolvingObjTypePanel = new JPanel(new GridLayout(10, 1, 0, 10));
//		for (String objName : cm.allObjectTyps) {
//			JCheckBox jrb = new JCheckBox(objName);
//			jcbLst.add(jrb);
//			revolvingObjTypePanel.add(jrb);
//		}
//		revolvingObjTypePanel.setMaximumSize(new Dimension(250,220));
//		revolvingObjTypePanel.setMinimumSize(new Dimension(250,220));
//		revolvingObjTypePanel.setPreferredSize(new Dimension(250,220));
//		revolvingObjTypePanel.setBorder(BorderFactory.createTitledBorder(
//				"object type"));
//		JScrollPane revObjScrollPane = new JScrollPane(revolvingObjTypePanel);
//		revObjScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		hBox.add(revObjScrollPane);
//		JScrollPane jsp3 = new JScrollPane(revolvingObjTypePanel);
//		hBox.add(jsp3);
		//------------------------------------------------------

		// set horizontal box for setting cardinality
		JLabel jbnForFirstAct = new JLabel(firstAct);
		jbnForFirstAct.setMinimumSize(new Dimension(150,20));
		jbnForFirstAct.setMaximumSize(new Dimension(150,20));
		jbnForFirstAct.setPreferredSize(new Dimension(150,20));
		JLabel jbnForSecondAct = new JLabel(secondAct);
		jbnForSecondAct.setMinimumSize(new Dimension(150,20));
		jbnForSecondAct.setMaximumSize(new Dimension(150,20));
		jbnForSecondAct.setPreferredSize(new Dimension(150,20));

		JTextField preActCardMinTextField = new JTextField("0");
		preActCardMinTextField.setMinimumSize(new Dimension(20,20));
		preActCardMinTextField.setMaximumSize(new Dimension(20,20));
		preActCardMinTextField.setPreferredSize(new Dimension(20,20));
		JLabel jlbPreAct = new JLabel("..");
		jlbPreAct.setMinimumSize(new Dimension(10,20));
		jlbPreAct.setMaximumSize(new Dimension(10,20));
		jlbPreAct.setPreferredSize(new Dimension(10,20));
		JTextField preActCardMaxTextField = new JTextField("1");
		preActCardMaxTextField.setMinimumSize(new Dimension(20,20));
		preActCardMaxTextField.setMaximumSize(new Dimension(20,20));
		preActCardMaxTextField.setPreferredSize(new Dimension(20,20));

		JTextField sucActCardMinTextField = new JTextField("0");
		sucActCardMinTextField.setMinimumSize(new Dimension(20,20));
		sucActCardMinTextField.setMaximumSize(new Dimension(20,20));
		sucActCardMinTextField.setPreferredSize(new Dimension(20,20));
		JLabel jlbSucAct = new JLabel("..");
		jlbSucAct.setMinimumSize(new Dimension(10,20));
		jlbSucAct.setMaximumSize(new Dimension(10,20));
		jlbSucAct.setPreferredSize(new Dimension(10,20));
		JTextField sucActCardMaxTextField = new JTextField("1");
		sucActCardMaxTextField.setMinimumSize(new Dimension(20,20));
		sucActCardMaxTextField.setMaximumSize(new Dimension(20,20));
		sucActCardMaxTextField.setPreferredSize(new Dimension(20,20));

		// ----- the vertical box for textfield
		Box vBox1 = Box.createVerticalBox();
		Box vBoxHbox1 = Box.createHorizontalBox();
		vBoxHbox1.add(preActCardMinTextField);
		vBoxHbox1.add(jlbPreAct);
		vBoxHbox1.add(preActCardMaxTextField);
		vBox1.add(vBoxHbox1);

		vBox1.add(Box.createVerticalStrut(100));

		Box vBoxHbox2 = Box.createHorizontalBox();
		vBoxHbox2.add(sucActCardMinTextField);
		vBoxHbox2.add(jlbSucAct);
		vBoxHbox2.add(sucActCardMaxTextField);
		vBox1.add(vBoxHbox2);

		JLabel label = new JLabel();

		label.setMinimumSize(new Dimension(50,100));
		label.setMaximumSize(new Dimension(50,100));
		label.setPreferredSize(new Dimension(50,100));
		// --- the vertical box for textfield

		// --- the vertical box for activity labels

		// Load the image file that you want to insert into the box
		BufferedImage image = null;
		try {
			
			image = ImageIO.read(new File(System.getProperty("user.dir")+"\\src\\org\\processmining\\cachealignment\\algorithms\\resources\\arrow.png"));
			Image dimg = image.getScaledInstance(50, 100, Image.SCALE_SMOOTH);
			ImageIcon icon = new ImageIcon(dimg);
			label.setIcon(icon);
		} catch (Exception ex) {
			System.out.println("Error loading image file: " + ex.getMessage());
		}

		Box hBoxForSecondLabel = Box.createHorizontalBox();
		hBoxForSecondLabel.add(Box.createHorizontalStrut(30));
		hBoxForSecondLabel.add(jbnForSecondAct);

		Box vBox2 = Box.createVerticalBox();
		Box hBoxForFirstLabel = Box.createHorizontalBox();
		hBoxForFirstLabel.add(Box.createHorizontalStrut(30));
		hBoxForFirstLabel.add(jbnForFirstAct);
		vBox2.add(hBoxForFirstLabel);
		vBox2.add(Box.createVerticalStrut(20));
		vBox2.add(hBoxForSecondLabel);

		JPanel jp1 = new JPanel();
		jp1.setMinimumSize(new Dimension(160,150));
		jp1.setMaximumSize(new Dimension(160,150));
		jp1.setPreferredSize(new Dimension(160,150));
		BoxLayout layout = new BoxLayout(jp1, BoxLayout.Y_AXIS);
		jp1.setLayout(layout);

		// Add the components to the JPanel object
		jp1.add(jbnForFirstAct);
		jp1.add(label);
		jp1.add(jbnForSecondAct);

		Box hBoxForAll = Box.createHorizontalBox();
		hBoxForAll.add(vBox1);
		hBoxForAll.add(Box.createHorizontalStrut(20));
		hBoxForAll.add(jp1);

		JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cardPanel.add(hBoxForAll);
		cardPanel.setMaximumSize(new Dimension(200,220));
		cardPanel.setMinimumSize(new Dimension(200,220));
		cardPanel.setPreferredSize(new Dimension(200,220));
		cardPanel.setBorder(BorderFactory.createTitledBorder("cardinality constraint"));
		JScrollPane cardScrollPane = new JScrollPane(cardPanel);
		cardScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane jsp4 = new JScrollPane(cardPanel);
		hBox.add(jsp4);
		//------------------------------------------------------

		// add a confirm button
		JButton jbConfirm = new JButton("Confirm");
		jbConfirm.setMaximumSize(new Dimension(80,30));
		jbConfirm.setMinimumSize(new Dimension(80,30));
		jbConfirm.setPreferredSize(new Dimension(80,30));
		jbConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (preActCardMinTextField.getText().equals("")||
					preActCardMaxTextField.getText().equals("")||
					sucActCardMinTextField.getText().equals("")||
					sucActCardMaxTextField.getText().equals("")){
					return;
				}

				String firstCard = preActCardMinTextField.getText()+".."+ preActCardMaxTextField.getText();
				String secondCard = sucActCardMinTextField.getText()+".."+ sucActCardMaxTextField.getText();
				objTypeSelectedLst = new ArrayList();
				for (JCheckBox cb : jcbLst)
					if (cb.isSelected()) {
						objTypeSelectedLst.add(String.valueOf(cb.getText()));
					}

				int preActCardMin = Integer.valueOf(preActCardMinTextField.getText());
				int preActCardMax = Integer.valueOf(preActCardMaxTextField.getText());
				int sucActCardMin = Integer.valueOf(sucActCardMinTextField.getText());
				int sucActCardMax =Integer.valueOf(sucActCardMaxTextField.getText());
				setActToActCardConstraint((mxCell)cell,
						graph,
						objTypeSelectedLst,
						firstAct,
						preActCardMin,
						preActCardMax,
						secondAct,
						sucActCardMin,
						sucActCardMax,
						firstCard,
						secondCard
						);
				graph.getModel().beginUpdate();
				graph.getModel().endUpdate();
				dialog.dispose();
			}
		});

		// add a cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.setMaximumSize(new Dimension(80,30));
		jbCancel.setMinimumSize(new Dimension(80,30));
		jbCancel.setPreferredSize(new Dimension(80,30));
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
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox1);
		dialog.getContentPane().add(vBox);
	}

	

	public void getActToActTimeDialog(Object cell,
									  mxGraph graph,
									  String firstAct,
									  String secondAct) {
		JDialog dialog = new JDialog();
		dialog.setTitle("Activity-activity temporal setting");
		dialog.setMinimumSize(new Dimension(400,350));
		dialog.setMaximumSize(new Dimension(400,350));
		dialog.setPreferredSize(new Dimension(400,350));
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);  // set to the center of the screen

		Box vBox = Box.createVerticalBox();
		vBox.setMinimumSize(new Dimension(350,320));
		vBox.setMaximumSize(new Dimension(350,320));
		vBox.setPreferredSize(new Dimension(350,320));

		Box hBox = Box.createHorizontalBox();
		hBox.setMinimumSize(new Dimension(350,250));
		hBox.setMaximumSize(new Dimension(350,250));
		hBox.setPreferredSize(new Dimension(350,250));

		List<JCheckBox> jcbLst = new ArrayList<>();
		// ---set object type---
//		JPanel revolvingObjTypePanel = new JPanel(new GridLayout(10, 1, 0, 10));
//		for (String objName : cm.allObjectTyps) {
//			JCheckBox jrb = new JCheckBox(objName);
//			jcbLst.add(jrb);
//			revolvingObjTypePanel.add(jrb);
//		}
//		revolvingObjTypePanel.setMaximumSize(new Dimension(250,220));
//		revolvingObjTypePanel.setMinimumSize(new Dimension(250,220));
//		revolvingObjTypePanel.setPreferredSize(new Dimension(250,220));
//		revolvingObjTypePanel.setBorder(BorderFactory.createTitledBorder(
//				"object type"));
//		JScrollPane revObjScrollPane = new JScrollPane(revolvingObjTypePanel);
//		revObjScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		hBox.add(revObjScrollPane);
//		JScrollPane jsp3 = new JScrollPane(revolvingObjTypePanel);
//		hBox.add(jsp3);
		//------------------------------------------------------

		// set horizontal box for setting temporal constraint
		Box vBoxForCard = Box.createVerticalBox();
		Box hBoxForCard1 = Box.createHorizontalBox();
		Box hBoxForCard2 = Box.createHorizontalBox();
		JLabel jlbForFirstAct = new JLabel(firstAct);
		jlbForFirstAct.setMinimumSize(new Dimension(150,20));
		jlbForFirstAct.setMaximumSize(new Dimension(150,20));
		jlbForFirstAct.setPreferredSize(new Dimension(150,20));
		JLabel jlbForSecondAct = new JLabel(secondAct);
		jlbForSecondAct.setMinimumSize(new Dimension(150,20));
		jlbForSecondAct.setMaximumSize(new Dimension(150,20));
		jlbForSecondAct.setPreferredSize(new Dimension(150,20));
		JComboBox<String> comboBox1=new JComboBox<String>();
		comboBox1.setMinimumSize(new Dimension(80,20));
		comboBox1.setMaximumSize(new Dimension(80,20));
		comboBox1.setPreferredSize(new Dimension(80,20));
		comboBox1.addItem("First");
		comboBox1.addItem("Last");
		comboBox1.addItem("All");

		JComboBox<String> comboBox2=new JComboBox<String>();
		comboBox2.setMinimumSize(new Dimension(80,20));
		comboBox2.setMaximumSize(new Dimension(80,20));
		comboBox2.setPreferredSize(new Dimension(80,20));
		comboBox2.addItem("First");
		comboBox2.addItem("Last");
		comboBox2.addItem("All");
		JLabel picLabel= new JLabel();
		picLabel.setMaximumSize(new Dimension(200,100));
		picLabel.setMinimumSize(new Dimension(200,100));
		picLabel.setPreferredSize(new Dimension(200,100));

		
		// Load the image file that you want to insert into the box
		BufferedImage image = null;
		try {
			
			image = ImageIO.read(new File(System.getProperty("user.dir")+"\\src\\org\\processmining\\cachealignment\\algorithms\\resources\\singleArrow.png"));
			Image dimg = image.getScaledInstance(50, 100, Image.SCALE_SMOOTH);
			ImageIcon scaledIcon = new ImageIcon(dimg);
			picLabel.setIcon(scaledIcon);
			picLabel.setVisible(true);
		} catch (Exception ex) {
			System.out.println("Error loading image file: " + ex.getMessage());
		}

		hBoxForCard1.add(Box.createHorizontalStrut(5));
		hBoxForCard1.add(comboBox1);
		hBoxForCard1.add(Box.createHorizontalStrut(5));
		hBoxForCard1.add(jlbForFirstAct);
		hBoxForCard2.add(Box.createHorizontalStrut(5));
		hBoxForCard2.add(comboBox2);
		hBoxForCard2.add(Box.createHorizontalStrut(5));
		hBoxForCard2.add(jlbForSecondAct);

		Box hBoxForCard3 = Box.createHorizontalBox();
		JLabel jlbTime1 = new JLabel("Temporal threshold: ");
		JTextField minTimeJtf = new JTextField();
		minTimeJtf.setMaximumSize(new Dimension(40,20));
		minTimeJtf.setMinimumSize(new Dimension(40,20));
		minTimeJtf.setPreferredSize(new Dimension(40,20));
		JLabel jlbTime2 = new JLabel(" to ");
		JTextField maxTimeJtf = new JTextField();
		maxTimeJtf.setMaximumSize(new Dimension(50,20));
		maxTimeJtf.setMinimumSize(new Dimension(50,20));
		maxTimeJtf.setPreferredSize(new Dimension(50,20));
		JComboBox<String> jcb_time_symbol=new JComboBox<String>();
		for(String timeUnit:timeUnit){
			jcb_time_symbol.addItem(timeUnit);
		}
		jcb_time_symbol.setMinimumSize(new Dimension(80,20));
		jcb_time_symbol.setMaximumSize(new Dimension(80,20));
		jcb_time_symbol.setPreferredSize(new Dimension(80,20));
		hBoxForCard3.add(Box.createHorizontalStrut(10));
		hBoxForCard3.add(jlbTime1);
		hBoxForCard3.add(Box.createHorizontalStrut(5));
		hBoxForCard3.add(minTimeJtf);
		hBoxForCard3.add(Box.createHorizontalStrut(5));
		hBoxForCard3.add(jlbTime2);
		hBoxForCard3.add(Box.createHorizontalStrut(5));
		hBoxForCard3.add(maxTimeJtf);
		hBoxForCard3.add(Box.createHorizontalStrut(5));
		hBoxForCard3.add(jcb_time_symbol);

		vBoxForCard.add(hBoxForCard1);
		vBoxForCard.add(Box.createVerticalStrut(10));
		vBoxForCard.add(picLabel);
		vBoxForCard.add(Box.createVerticalStrut(10));
		vBoxForCard.add(hBoxForCard2);
		vBoxForCard.add(Box.createVerticalStrut(10));
		vBoxForCard.add(hBoxForCard3);
		JPanel cardPanel = new JPanel(new BorderLayout());
		vBoxForCard.setMaximumSize(new Dimension(320,210));
		vBoxForCard.setMinimumSize(new Dimension(320,210));
		vBoxForCard.setPreferredSize(new Dimension(320,210));
		cardPanel.add(vBoxForCard);
		cardPanel.setMaximumSize(new Dimension(450,220));
		cardPanel.setMinimumSize(new Dimension(450,220));
		cardPanel.setPreferredSize(new Dimension(450,220));
		cardPanel.setBorder(BorderFactory.createTitledBorder("temporal constraint"));
		JScrollPane cardScrollPane = new JScrollPane(cardPanel);
		cardScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane jsp4 = new JScrollPane(cardPanel);
		hBox.add(jsp4);
		//------------------------------------------------------

		// add a confirm button
		JButton jbConfirm = new JButton("Confirm");
		jbConfirm.setMaximumSize(new Dimension(80,30));
		jbConfirm.setMinimumSize(new Dimension(80,30));
		jbConfirm.setPreferredSize(new Dimension(80,30));
		jbConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String timeUnit = jcb_time_symbol.getSelectedItem().toString();
				String minTime = minTimeJtf.getText();
				String maxTime = maxTimeJtf.getText();
				long minTimeInSeconds = getMinTime(minTime, timeUnit);
				long maxTimeInSeconds = getMaxTime(maxTime, timeUnit);

				String firstPattern = comboBox1.getSelectedItem().toString();
				String secondPatten = comboBox2.getSelectedItem().toString();

				objTypeSelectedLst = new ArrayList();
				for (JCheckBox cb : jcbLst)
					if (cb.isSelected()) {
						objTypeSelectedLst.add(String.valueOf(cb.getText()));
					}

				Map<String, String> consTypeMap = new HashMap<>();
				consTypeMap.put("constraintType","temporalConstraint");
				Map<String, String> preActMap = new HashMap<>();
				preActMap.put("preAct",firstAct);
				Map<String, String> sucActMap = new HashMap<>();
				sucActMap.put("sucAct",secondAct);
				Map<String, Long> minTimeMap = new HashMap<>();
				minTimeMap.put("timeMin",minTimeInSeconds);
				Map<String, Long> maxTimeMap = new HashMap<>();
				maxTimeMap.put("timeMax",maxTimeInSeconds);
				Map<String, String> firstPatternMap = new HashMap<>();
				firstPatternMap.put("firstPattern",firstPattern);
				Map<String, String> secondPatternMap = new HashMap<>();
				secondPatternMap.put("secondPatten",secondPatten);
//				Map<String, List> objTypeMap = new HashMap<>();
//				objTypeMap.put("objLst",objTypeSelectedLst);
				ArrayList<Map> consLst = new ArrayList<>();
				consLst.add(consTypeMap);
				consLst.add(preActMap);
				consLst.add(sucActMap);
				consLst.add(minTimeMap);
				consLst.add(maxTimeMap);
				consLst.add(firstPatternMap);
				consLst.add(secondPatternMap);
				cm.consLst.add(consLst);

				graph.getModel().beginUpdate();
				setActToActTimeConstraint(
						(mxCell)cell,
						graph,
						objTypeSelectedLst,
						minTime,
						maxTime,
						firstPattern,
						secondPatten,
						timeUnit
						);
				graph.getModel().endUpdate();
				dialog.dispose();
			}
		});

		// add a cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.setMaximumSize(new Dimension(80,30));
		jbCancel.setMinimumSize(new Dimension(80,30));
		jbCancel.setPreferredSize(new Dimension(80,30));
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
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox1);
		dialog.getContentPane().add(vBox);
	}

	public void getActFreqDialog(Object cell,
								  mxGraph graph,
								  String actName) {

		System.out.println("act name"+actName);

		JDialog dialog = new JDialog();
		dialog.setMinimumSize(new Dimension(500,200));
		dialog.setMaximumSize(new Dimension(500,200));
		dialog.setPreferredSize(new Dimension(500,200));

		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);  // set to the center of the screen
		Box vBox = Box.createVerticalBox();
		vBox.setMinimumSize(new Dimension(500,200));
		vBox.setMaximumSize(new Dimension(500,200));
		vBox.setPreferredSize(new Dimension(500,200));

		// The number of objects of type + refObjType + for activity:
		Box hBox1 = Box.createHorizontalBox();
		Box hBox2 = Box.createHorizontalBox();
		Box hBox3 = Box.createHorizontalBox();

		JLabel jlb1 = new JLabel("The number of activity ");
		Font font = new Font("Arial", Font.BOLD, 14);
		JLabel jlb2 = new JLabel(actName);
		jlb2.setFont(font);
		JLabel jlb3 = new JLabel(" in the process execution is:");
		hBox1.add(jlb1);
		hBox1.add(jlb2);
		hBox1.add(jlb3);

		JTextField preActFreqMinTextField = new JTextField("0");
		preActFreqMinTextField.setMinimumSize(new Dimension(20,20));
		preActFreqMinTextField.setMaximumSize(new Dimension(20,20));
		preActFreqMinTextField.setPreferredSize(new Dimension(20,20));
		JLabel jlbPreAct = new JLabel(" to ");
		jlbPreAct.setMinimumSize(new Dimension(10,20));
		jlbPreAct.setMaximumSize(new Dimension(10,20));
		jlbPreAct.setPreferredSize(new Dimension(10,20));
		JTextField preActFreqMaxTextField = new JTextField("1");
		preActFreqMaxTextField.setMinimumSize(new Dimension(20,20));
		preActFreqMaxTextField.setMaximumSize(new Dimension(20,20));
		preActFreqMaxTextField.setPreferredSize(new Dimension(20,20));
		hBox2.add(preActFreqMinTextField);
		hBox2.add(Box.createHorizontalStrut(10));
		hBox2.add(jlbPreAct);
		hBox2.add(Box.createHorizontalStrut(10));
		hBox2.add(preActFreqMaxTextField);

		Box hBox4 = Box.createHorizontalBox();
		JLabel jlb4 = new JLabel("* denotes an arbitrary number");
		hBox3.add(jlb4);

		// add a confirm button
		JButton jbConfirm = new JButton("Confirm");
		jbConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				try{
					String preActFreqMin = preActFreqMinTextField.getText();
					String preActFreqMax = preActFreqMaxTextField.getText();
					int minFreq = Integer.parseInt(preActFreqMin);
					if(preActFreqMax.equals("*")) {
						preActFreqMax = "99999";
					}
					else{
						int maxFreq = Integer.parseInt(preActFreqMax);
						if(maxFreq < minFreq){
							return;
						}
					}
					// if the input is not integer
					setObjToActConstraint((mxCell)cell,
							graph,
							preActFreqMin,
							preActFreqMax);

					Map<String, String> consTypeMap = new HashMap<>();
					consTypeMap.put("constraintType","freqConstraint");
					Map<String, String> actMap = new HashMap<>();
					actMap.put("actName", actName);
					Map<String, String> minFreqMap = new HashMap<>();
					minFreqMap.put("minFreq",preActFreqMin);
					Map<String, String> maxFreqMap = new HashMap<>();
					maxFreqMap.put("maxFreq",preActFreqMax);

					ArrayList<Map> consLst = new ArrayList<>();
					consLst.add(consTypeMap);
					consLst.add(actMap);
					consLst.add(minFreqMap);
					consLst.add(maxFreqMap);
					cm.consLst.add(consLst);
					graph.getModel().beginUpdate();
					graph.getModel().endUpdate();
					dialog.dispose();}
				catch (NumberFormatException numEx){
					return;
				}
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

		hBox4.add(Box.createHorizontalStrut(10));
		hBox4.add(jbConfirm);
		hBox4.add(Box.createHorizontalStrut(10));
		hBox4.add(jbCancel);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox1);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox2);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox3);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox4);
		dialog.getContentPane().add(vBox);
	}
	
	public void getActToObjDialog(Object cell,
								  mxGraph graph,
								  String refObjType,
								  String actName) {
		JDialog dialog = new JDialog();
		dialog.setMinimumSize(new Dimension(500,200));
		dialog.setMaximumSize(new Dimension(500,200));
		dialog.setPreferredSize(new Dimension(500,200));

		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);  // set to the center of the screen
		Box vBox = Box.createVerticalBox();
		vBox.setMinimumSize(new Dimension(500,200));
		vBox.setMaximumSize(new Dimension(500,200));
		vBox.setPreferredSize(new Dimension(500,200));

		// The number of objects of type + refObjType + for activity:
		Box hBox1 = Box.createHorizontalBox();
		Box hBox2 = Box.createHorizontalBox();
		Box hBox3 = Box.createHorizontalBox();
		Box hBox4 = Box.createHorizontalBox();

		JLabel jlb1 = new JLabel("The number of objects of type ");
		JLabel jlb2 = new JLabel(refObjType);
		Font font = new Font("Arial", Font.BOLD, 14);
		jlb2.setFont(font);
		JLabel jlb3 = new JLabel(" for activity ");
		JLabel jlb4 = new JLabel(actName);
		jlb4.setFont(font);
		JLabel jlb5 = new JLabel(" is:");
		hBox1.add(jlb1);
		hBox1.add(jlb2);
		hBox1.add(jlb3);
		hBox1.add(jlb4);
		hBox1.add(jlb5);

		JTextField preActCardMinTextField = new JTextField("0");
		preActCardMinTextField.setMinimumSize(new Dimension(20,20));
		preActCardMinTextField.setMaximumSize(new Dimension(20,20));
		preActCardMinTextField.setPreferredSize(new Dimension(20,20));
		JLabel jlbPreAct = new JLabel("..");
		jlbPreAct.setMinimumSize(new Dimension(10,20));
		jlbPreAct.setMaximumSize(new Dimension(10,20));
		jlbPreAct.setPreferredSize(new Dimension(10,20));
		JTextField preActCardMaxTextField = new JTextField("1");
		preActCardMaxTextField.setMinimumSize(new Dimension(20,20));
		preActCardMaxTextField.setMaximumSize(new Dimension(20,20));
		preActCardMaxTextField.setPreferredSize(new Dimension(20,20));
		hBox2.add(preActCardMinTextField);
		hBox2.add(Box.createHorizontalStrut(10));
		hBox2.add(jlbPreAct);
		hBox2.add(Box.createHorizontalStrut(10));
		hBox2.add(preActCardMaxTextField);

		JLabel jlb6 = new JLabel("* denotes an arbitrary number");
		hBox3.add(jlb6);

		// add a confirm button
		JButton jbConfirm = new JButton("Confirm");
		jbConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String preActFreqMin = preActCardMinTextField.getText();
				String preActFreqMax = preActCardMaxTextField.getText();
				int minFreq = Integer.parseInt(preActFreqMin);
				if(preActFreqMax.equals("*")) {
					preActFreqMax = "99999";
				}
				else{
					int maxFreq = Integer.parseInt(preActFreqMax);
					if(maxFreq < minFreq){
						return;
					}
				}


				setObjToActConstraint((mxCell)cell,
						graph,
						preActCardMinTextField.getText(),
						preActCardMaxTextField.getText());

				Map<String, String> consTypeMap = new HashMap<>();
				consTypeMap.put("constraintType","objConstraint");
				Map<String, String> objTypeMap = new HashMap<>();
				objTypeMap.put("refObjType",refObjType);
				Map<String, String> actMap = new HashMap<>();
				actMap.put("actName",actName);
				Map<String, String> minCardMap = new HashMap<>();
				minCardMap.put("minCard",preActCardMinTextField.getText());
				Map<String, String> maxCardMap = new HashMap<>();
				maxCardMap.put("maxCard",preActCardMaxTextField.getText());

				ArrayList<Map> consLst = new ArrayList<>();
				consLst.add(consTypeMap);
				consLst.add(objTypeMap);
				consLst.add(actMap);
				consLst.add(minCardMap);
				consLst.add(maxCardMap);
				cm.consLst.add(consLst);
				graph.getModel().beginUpdate();
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

		hBox3.add(Box.createHorizontalStrut(10));
		hBox3.add(jbConfirm);
		hBox3.add(Box.createHorizontalStrut(10));
		hBox3.add(jbCancel);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox1);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox2);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox3);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox4);
		dialog.getContentPane().add(vBox);
	}

	public void getActivityTimePerformanceDialog(Object cell, mxGraph graph, String actName){
		JDialog dialog = new JDialog();
		dialog.setTitle("Time Performance Constraint for activity "+ actName);
		dialog.setSize(new Dimension(600,540));
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);  // set to the center of the screen
		Box hBox = Box.createHorizontalBox();
		Box vBox = Box.createVerticalBox();

		// ButtonGroup group=new ButtonGroup();
		List<JCheckBox> jcbLst = new ArrayList<>();
		// ---set object type---
		JPanel revolvingObjTypePanel = new JPanel(new GridLayout(10, 1, 0, 10));
		for (String objName : cm.revObjTypes) {
			JCheckBox jrb = new JCheckBox(objName);
			jcbLst.add(jrb);
			revolvingObjTypePanel.add(jrb);
		}
		JCheckBox jrb = new JCheckBox(cm.leadObjType);
		jcbLst.add(jrb);
		revolvingObjTypePanel.add(jrb);
		
		revolvingObjTypePanel.setMaximumSize(new Dimension(200,400));
		revolvingObjTypePanel.setMinimumSize(new Dimension(200,400));
		revolvingObjTypePanel.setPreferredSize(new Dimension(200,400));
		revolvingObjTypePanel.setBorder(BorderFactory.createTitledBorder(
				"object type"));
		JScrollPane revObjScrollPane = new JScrollPane(revolvingObjTypePanel);
		revObjScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane jsp3 = new JScrollPane(revolvingObjTypePanel);
		hBox.add(jsp3);

		// constraint panel
		JPanel constraintPanel = new JPanel();

		// --- time window---
		Box vBoxTimeToTime = Box.createVerticalBox();
		Box hBoxTimeToTime = Box.createHorizontalBox();

		List<String> names = Arrays.asList(
				"flow time",
				"waiting time",
				"synchronization time",
				"pooling time",
				"lagging time");
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
		jcb.setMaximumSize(new Dimension(150,30));
		jcb.setMinimumSize(new Dimension(150,30));
		jcb.setPreferredSize(new Dimension(150,30));
		JLabel jlb1 = new JLabel("The ");
		JLabel jlb2 = new JLabel(" for activity ");
		JLabel jlb3 = new JLabel(actName);
		Font font = new Font("Arial", Font.BOLD, 14);
		jlb3.setFont(font);
		JLabel jlb4 = new JLabel(" is:");

		Box hBoxTimeToTime0 = Box.createHorizontalBox();
		hBoxTimeToTime0.add(jlb1);
		hBoxTimeToTime0.add(jcb);
		hBoxTimeToTime0.add(jlb2);
		hBoxTimeToTime0.add(jlb3);
		hBoxTimeToTime0.add(jlb4);
		vBoxTimeToTime.add(hBoxTimeToTime0);

		hBoxTimeToTime.add(Box.createHorizontalStrut(20));
		JTextField minTimeJtf = new JTextField();
		JTextField maxTimeJtf = new JTextField();
		minTimeJtf.setMaximumSize(new Dimension(50,20));
		minTimeJtf.setMinimumSize(new Dimension(50,20));
		minTimeJtf.setPreferredSize(new Dimension(50,20));
		maxTimeJtf.setMaximumSize(new Dimension(80,20));
		maxTimeJtf.setMinimumSize(new Dimension(80,20));
		maxTimeJtf.setPreferredSize(new Dimension(80,20));
		hBoxTimeToTime.add(minTimeJtf);
		hBoxTimeToTime.add(new JLabel(" to "));
		hBoxTimeToTime.add(maxTimeJtf);
		JComboBox<String> jcb_time_symbol = new JComboBox<String>();
		for (String ts: timeUnit) {
			jcb_time_symbol.addItem(ts);
		}
		jcb_time_symbol.setSize(new Dimension(80, 20));
		jcb_time_symbol.setMaximumSize(new Dimension(80, 20));
		jcb_time_symbol.setPreferredSize(new Dimension(80, 20));
		hBoxTimeToTime.add(Box.createHorizontalStrut(5));
		hBoxTimeToTime.add(jcb_time_symbol);
		vBoxTimeToTime.add(Box.createVerticalStrut(15));
		vBoxTimeToTime.add(hBoxTimeToTime);

		hBox.add(vBoxTimeToTime);
		constraintPanel.add(vBoxTimeToTime);
		constraintPanel.setMaximumSize(new Dimension(300,400));
		constraintPanel.setMinimumSize(new Dimension(300,400));
		constraintPanel.setPreferredSize(new Dimension(300,400));
		constraintPanel.setBorder(BorderFactory.createTitledBorder("Time performance constraint"));
		JScrollPane constraintScrollPane = new JScrollPane(constraintPanel);
		constraintScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		hBox.add(constraintScrollPane);

		// btn panel
		JPanel btnPanel = new JPanel();
		btnPanel.setMaximumSize(new Dimension(420,60));
		btnPanel.setMinimumSize(new Dimension(420,60));
		btnPanel.setPreferredSize(new Dimension(420,60));

		JButton jbReset = new JButton("Reset");
		jbReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		// add a confirm button
		JButton jbConfirm = new JButton("Confirm");
		jbConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String timeUnit = jcb_time_symbol.getSelectedItem().toString();
				String minTime = minTimeJtf.getText();
				String maxTime = maxTimeJtf.getText();

				boolean objFlag = false;
				objTypeSelectedLst=new ArrayList<>();
				for (JCheckBox cb : jcbLst) {
					if (cb.isSelected()) {
						objTypeSelectedLst.add(String.valueOf(cb.getText()));
						objFlag = true;
					}
				}
			
				
				if (objFlag) {
					timeType = jcb.getSelectedItem().toString();
					if(timeType.equals("pooling time") && objTypeSelectedLst.size()!=1){
							return;
						}
										
					setActivityTimePerformanceConstraint((mxCell) cell, graph, timeType, actName, objTypeSelectedLst, minTime, maxTime, timeUnit);
					
					long minTimeInSeconds = getMinTime(minTime, timeUnit);
					long maxTimeInSeconds = getMaxTime(maxTime, timeUnit);

					Map<String, String> consTypeMap = new HashMap<>();
					consTypeMap.put("constraintType", "activityTimeConstraint");
					Map<String, String> actMap = new HashMap<>();
					actMap.put("targetActivity", actName);
					Map<String, String> sucActMap = new HashMap<>();
					sucActMap.put("timeType", timeType);
					Map<String, Long> preActCardMinMap = new HashMap<>();
					preActCardMinMap.put("minTime", minTimeInSeconds);
					Map<String, Long> preActCardMaxMap = new HashMap<>();
					preActCardMaxMap.put("maxTime", maxTimeInSeconds);
					Map<String, String> sucActCardMinMap = new HashMap<>();
					sucActCardMinMap.put("timeUnit", timeUnit);
					Map<String, List> objTypeMap = new HashMap<>();
					objTypeMap.put("objLst", objTypeSelectedLst);

					ArrayList<Map> consLst = new ArrayList<>();
					consLst.add(consTypeMap);
					consLst.add(actMap);
					consLst.add(sucActMap);
					consLst.add(preActCardMinMap);
					consLst.add(preActCardMaxMap);
					consLst.add(sucActCardMinMap);
					consLst.add(objTypeMap);
					cm.consLst.add(consLst);

					graph.getModel().beginUpdate();
					graph.getModel().endUpdate();
					dialog.dispose();
				}
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
		hBox1.add(Box.createHorizontalStrut(20));
		hBox1.add(jbReset);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(jbConfirm);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(jbCancel);
		
		vBox.add(hBox);
		vBox.add(hBox1);
		dialog.getContentPane().add(vBox);
	}

	public long getMinTime(String minTime, String timeUnit){
		long minTimeInSeconds = 0;
		switch (timeUnit){
			case "seconds":
				minTimeInSeconds = Long.parseLong(minTime);
				return minTimeInSeconds;
			case "minutes":
				minTimeInSeconds =   Long.parseLong(minTime);
				return minTimeInSeconds * 60;
			case "hours":
				minTimeInSeconds =   Long.parseLong(minTime);
				return minTimeInSeconds * 60 * 60;
			case "days":
				minTimeInSeconds =   Long.parseLong(minTime);
				return minTimeInSeconds * 60 * 60* 24;
			case "weeks":
				minTimeInSeconds =   Long.parseLong(minTime);
				return minTimeInSeconds * 60* 60* 24*7;
			default:
				return minTimeInSeconds;
		}
	}

	public long getMaxTime(String maxTime, String timeUnit){
		long maxTimeInSeconds = 0;
		switch (timeUnit){
			case "seconds":
				maxTimeInSeconds = Long.parseLong(maxTime);
				return maxTimeInSeconds;
			case "maxutes":
				maxTimeInSeconds =   Long.parseLong(maxTime);
				return maxTimeInSeconds * 60;
			case "hours":
				maxTimeInSeconds =   Long.parseLong(maxTime);
				return maxTimeInSeconds * 60 * 60;
			case "days":
				maxTimeInSeconds =   Long.parseLong(maxTime);
				return maxTimeInSeconds * 60 * 60* 24;
			case "weeks":
				maxTimeInSeconds =   Long.parseLong(maxTime);
				return maxTimeInSeconds * 60* 60* 24*7;
			default:
				return maxTimeInSeconds;
		}
	}

	public void setObjToActConstraint(mxCell cell,
									  mxGraph graph,
									  String minCard,
									  String maxCard
									  ){
		mxGeometry geo2 = new mxGeometry(0, 0, 0,0);
		// position the center of port correctly
		geo2.setOffset(new mxPoint(14, 0));
		geo2.setRelative(true);
		mxCell port2 = new mxCell(minCard+".."+maxCard, geo2,
				"shape=ellipse;perimeter=ellipsePerimeter");
		port2.setVertex(true);
		graph.addCell(port2, cell);
		port2.setVisible(true);
	}

//	public void setTimePerfConstraint(mxCell cell,
//									  mxGraph graph){
//		cell.setStyle("straight;startArrow=none;endArrow=none;" +
//				"strokeColor=#8B4513;strokeWidth=3");
//		mxGeometry geo2 = new mxGeometry(0, 0, 0,0);
//		geo2.setOffset(new mxPoint(14, 0));
//		geo2.setRelative(true);
//		mxCell port2 = new mxCell("鈮�1 day", geo2,
//				"shape=ellipse;perimeter=ellipsePerimeter");
//		port2.setVertex(true);
//		graph.addCell(port2, cell);
//		port2.setVisible(true);
//	}


	public void setActivityTimePerformanceConstraint(mxCell cell,
										 mxGraph graph,
										 String timeType,
										 String targetAct,
										 List objTypeSelectedLst,
										 String minTime,
										 String maxTime,
										 String timeUnit){

		cell.setStyle("straight;startArrow=none;endArrow=none;strokeColor=#8B4513;strokeWidth=3");
		cell.setValue(objTypeSelectedLst.toString() +"\n(" + minTime + "," + maxTime + ", "+timeUnit+")");
		cell.setTip("The " + timeType + " time for " + targetAct + " should be greater than " + minTime + " and less than "  + maxTime + timeUnit);
	}

	public void setActToActTimeConstraint(mxCell cell,
										 mxGraph graph,
										 List<String> objTypeSelectedLst,
										 String minTime,
										 String maxTime,
										 String prePattern,
										 String sucPattern,
										 String timeUnit){
		cell.setStyle("straight;strokeColor=#008000;strokeWidth=3");
		String portVal = "(" + minTime + "," + maxTime + ", "+timeUnit+")";
		// insert middle port between two ref obj
		mxGeometry geo1 = new mxGeometry(0, -1, 0, 0);
		// position the center of port correctly
		geo1.setOffset(new mxPoint(0, -12));
		geo1.setRelative(true);
		mxCell port1 = new mxCell(portVal, geo1,
				"shape=ellipse;perimeter=ellipsePerimeter");
		port1.setVertex(true);
		graph.addCell(port1, cell);
		port1.setVisible(true);

		mxGeometry geo2 = new mxGeometry(-0.7, 0, 1,1);
		// position the center of port correctly
		geo2.setOffset(new mxPoint(0, 13));
		geo2.setRelative(true);
		mxCell port2 = new mxCell(prePattern, geo2,
				"shape=ellipse;perimeter=ellipsePerimeter");
		port2.setVertex(true);
		graph.addCell(port2, cell);
		port2.setVisible(true);

		mxGeometry geo3 = new mxGeometry(0.7, 0, 1,1);
		// position the center of port correctly
		geo3.setOffset(new mxPoint(0, 13));
		geo3.setRelative(true);
		mxCell port3 = new mxCell(sucPattern, geo3,
				"shape=ellipse;perimeter=ellipsePerimeter");
		port3.setVertex(true);
		graph.addCell(port3, cell);
		port3.setVisible(true);

	}


	public void setActToActCardConstraint(mxCell cell,
										  mxGraph graph,
										  List<String> objTypeSelectedLst,
										  String firstAct,
										  int preActCardMin,
										  int preActCardMax,
										  String secondAct,
										  int sucActCardMin,
										  int sucActCardMax,
										  String preActCard,
										  String sucActCard){

		System.out.println("start cardinality checking");
		cell.setStyle("straight;strokeColor=#808080;strokeWidth=3");

		// insert middle port between two ref obj
		mxGeometry geo1 = new mxGeometry(0, -1, 0,0);
		// position the center of port correctly
		geo1.setOffset(new mxPoint(0, 0));
		geo1.setRelative(true);
		mxCell port1 = new mxCell(objTypeSelectedLst.toString(), geo1,
				"shape=ellipse;perimeter=ellipsePerimeter");
		port1.setVertex(true);
		graph.addCell(port1, cell);
		port1.setVisible(false);

		mxGeometry geo2 = new mxGeometry(-0.7, 0, 0,0);
		// position the center of port correctly
		geo2.setOffset(new mxPoint(0, -10));
		geo2.setRelative(true);
		mxCell port2 = new mxCell(preActCardMin+".."+preActCardMax, geo2,
				"shape=ellipse;perimeter=ellipsePerimeter");
		port2.setVertex(true);
		graph.addCell(port2, cell);
		port2.setVisible(true);

		mxGeometry geo3 = new mxGeometry(0.7, 0, 0,0);
		// position the center of port correctly
		geo3.setOffset(new mxPoint(0, -10));
		geo3.setRelative(true);
		mxCell port3 = new mxCell(sucActCardMin+".."+sucActCardMax, geo3,
				"shape=ellipse;perimeter=ellipsePerimeter");
		port3.setVertex(true);
		graph.addCell(port3, cell);
		port3.setVisible(true);

		Map<String, String> consTypeMap = new HashMap<>();
		consTypeMap.put("constraintType","cardinalityConstraint");
		Map<String, String> preActMap = new HashMap<>();
		preActMap.put("preAct",firstAct);
		Map<String, String> sucActMap = new HashMap<>();
		sucActMap.put("sucAct",secondAct);
		Map<String, Integer> preActCardMinMap = new HashMap<>();
		preActCardMinMap.put("preActCardMin",preActCardMin);
		Map<String, Integer> preActCardMaxMap = new HashMap<>();
		preActCardMaxMap.put("preActCardMax",preActCardMax);
		Map<String, Integer> sucActCardMinMap = new HashMap<>();
		sucActCardMinMap.put("sucActCardMin",sucActCardMin);
		Map<String, Integer> sucActCardMaxMap = new HashMap<>();
		sucActCardMaxMap.put("sucActCardMax",sucActCardMax);
		Map<String, List> objTypeMap = new HashMap<>();
		objTypeMap.put("objLst",objTypeSelectedLst);

		ArrayList<Map> consLst = new ArrayList<>();
		consLst.add(consTypeMap);
		consLst.add(preActMap);
		consLst.add(sucActMap);
		consLst.add(preActCardMinMap);
		consLst.add(preActCardMaxMap);
		consLst.add(sucActCardMinMap);
		consLst.add(sucActCardMaxMap);
		consLst.add(objTypeMap);
		cm.consLst.add(consLst);
	}

	/**
	 * 
	 */
	public void setBounds(Rectangle value)
	{
		if ((bounds == null && value != null)
				|| (bounds != null && value == null)
				|| (bounds != null && value != null && !bounds.equals(value)))
		{
			Rectangle tmp = bounds;

			if (tmp != null)
			{
				if (value != null)
				{
					tmp.add(value);
				}
			}
			else
			{
				tmp = value;
			}

			bounds = value;

			if (tmp != null)
			{
				graphComponent.getGraphControl().repaint(tmp);
			}
		}
	}

	/**
	 * Adds the given event listener.
	 */
	public void addListener(String eventName, mxEventSource.mxIEventListener listener)
	{
		eventSource.addListener(eventName, listener);
	}

	/**
	 * Removes the given event listener.
	 */
	public void removeListener(mxEventSource.mxIEventListener listener)
	{
		eventSource.removeListener(listener);
	}

	/**
	 * Removes the given event listener for the specified event name.
	 */
	public void removeListener(mxEventSource.mxIEventListener listener, String eventName)
	{
		eventSource.removeListener(listener, eventName);
	}

	/**
	 * 
	 */
	public void paint(Graphics g)
	{
		if (bounds != null)
		{
			if (connectIcon != null)
			{
				g.drawImage(connectIcon.getImage(), bounds.x, bounds.y,
						bounds.width, bounds.height, null);
			}
			else if (handleEnabled)
			{
				g.setColor(Color.BLACK);
				g.draw3DRect(bounds.x, bounds.y, bounds.width - 1,
						bounds.height - 1, true);
				g.setColor(Color.GREEN);
				g.fill3DRect(bounds.x + 1, bounds.y + 1, bounds.width - 2,
						bounds.height - 2, true);
				g.setColor(Color.BLUE);
				g.drawRect(bounds.x + bounds.width / 2 - 1, bounds.y
						+ bounds.height / 2 - 1, 1, 1);
			}
		}
	}

	/**
	 *
	 */
	public void mouseDragged(MouseEvent e)
	{
		if (!e.isConsumed() && graphComponent.isEnabled() && isEnabled())
		{
			// Activates the handler
			if (!active && first != null)
			{
				double dx = Math.abs(first.getX() - e.getX());
				double dy = Math.abs(first.getY() - e.getY());
				int tol = graphComponent.getTolerance();

				if (dx > tol || dy > tol)
				{
					active = true;
				}
			}

			if (e.getButton() == 0 || (isActive() && connectPreview.isActive()))
			{
				mxCellState state = marker.process(e);

				if (connectPreview.isActive())
				{
					connectPreview.update(e, marker.getValidState(), e.getX(),
							e.getY());
					setBounds(null);
					e.consume();
				}
				else
				{
					source = state;
				}
			}
		}
	}

	/**
	 *
	 */
	public void mouseMoved(MouseEvent e)
	{
		mouseDragged(e);

		if (isHighlighting() && !marker.hasValidState())
		{
			source = null;
		}

		if (!isHighlighting() && source != null)
		{
			int imgWidth = handleSize;
			int imgHeight = handleSize;

			if (connectIcon != null)
			{
				imgWidth = connectIcon.getIconWidth();
				imgHeight = connectIcon.getIconHeight();
			}

			int x = (int) source.getCenterX() - imgWidth / 2;
			int y = (int) source.getCenterY() - imgHeight / 2;

			if (graphComponent.getGraph().isSwimlane(source.getCell()))
			{
				mxRectangle size = graphComponent.getGraph().getStartSize(
						source.getCell());

				if (size.getWidth() > 0)
				{
					x = (int) (source.getX() + size.getWidth() / 2 - imgWidth / 2);
				}
				else
				{
					y = (int) (source.getY() + size.getHeight() / 2 - imgHeight / 2);
				}
			}

			setBounds(new Rectangle(x, y, imgWidth, imgHeight));
		}
		else
		{
			setBounds(null);
		}

		if (source != null && (bounds == null || bounds.contains(e.getPoint())))
		{
			graphComponent.getGraphControl().setCursor(CONNECT_CURSOR);
			e.consume();
		}
	}

	/**
	 *
	 */
	public void mousePressed(MouseEvent e)
	{
		if (!graphComponent.isForceMarqueeEvent(e)
				&& !graphComponent.isPanningEvent(e)
				&& !e.isPopupTrigger()
				&& graphComponent.isEnabled()
				&& isEnabled()
				&& !e.isConsumed()
				&& ((isHighlighting() && marker.hasValidState()) || (!isHighlighting()
				&& bounds != null && bounds.contains(e.getPoint()))))
		{

			start(e, marker.getValidState());
			e.consume();
		}
	}

//	public void getObjAfterToActBeforeDialog(Object cell, mxGraph graph){
//		JDialog dialog = new JDialog();
//		cm.alreadySelected.put(actToActName,0);
//		dialog.setTitle("Temporal constraint for object type "+ cm.startEntityName + " and "+cm.targetEntityName);
//		dialog.setSize(new Dimension(1000,540));
//		dialog.setVisible(true);
//		dialog.setLocationRelativeTo(null);  // set to the center of the screen
//
//		Box hBox = Box.createHorizontalBox();
//		Box vBox = Box.createVerticalBox();
//
//
//		JPanel picPanel = new JPanel();
//		JLabel picLabel = new JLabel();
//		picLabel.setMaximumSize(new Dimension(400,400));
//		picLabel.setMinimumSize(new Dimension(400,400));
//		picLabel.setPreferredSize(new Dimension(400,400));
//
//		// constraint panel
//		JPanel constraintPanel = new JPanel(new GridLayout(8, 1, 0, 10));
//		List<JRadioButton> constraintList = new ArrayList<>();
//		ButtonGroup constraintBg = new ButtonGroup();
//		List<String> names = Arrays.asList(
//				"Responded response (to the first A)",
//				"Responded response (to the last A)",
//				"Coexistence",
//				"Non coexistence",
//				"Responded existence",
//				"Responded non-existence");
//
//		String objToAct = "objToAct";
//
//		constraintPanel.setMaximumSize(new Dimension(200,400));
//		constraintPanel.setMinimumSize(new Dimension(200,400));
//		constraintPanel.setPreferredSize(new Dimension(200,400));
//
//		constraintPanel.setBorder(BorderFactory.createTitledBorder("Constraint type"));
//		hBox.add(new JScrollPane(constraintPanel));
//		picPanel.setMaximumSize(new Dimension(400,450));
//		picPanel.setMinimumSize(new Dimension(400,450));
//		picPanel.setPreferredSize(new Dimension(400,450));
//		ImageIcon myPicture = new ImageIcon("C:\\PromTest\\Packages\\OCCM\\src\\images\\"+objToAct+"pattern.png");
//		Image img = myPicture.getImage();
//		Image imgScale = img.getScaledInstance(400,400, Image.SCALE_SMOOTH);
//		ImageIcon scaledIcon = new ImageIcon(imgScale);
//		picLabel.setIcon(scaledIcon);
//		picLabel.setVisible(false);
//		picPanel.add(picLabel);
//
//		picPanel.setBorder(BorderFactory.createTitledBorder("Constraint pattern for "+cm.startEntityName+" and "+cm.targetEntityName));
//		JScrollPane jsp3 = new JScrollPane(picPanel);
//		hBox.add(jsp3);
//
//		// For throughput time
//		JPanel temporalPanel = new JPanel();
//		temporalPanel.setMaximumSize(new Dimension(170,400));
//		temporalPanel.setMinimumSize(new Dimension(170,400));
//		temporalPanel.setPreferredSize(new Dimension(170,400));
//		temporalPanel.setBorder(BorderFactory.createTitledBorder("Time constraint"));
//
//		// First is a hbox show the pic, then show the name of activity
//		Box hBoxActToAct = Box.createHorizontalBox();
//		hBoxActToAct.setMaximumSize(new Dimension(160,360));
//		hBoxActToAct.setMinimumSize(new Dimension(160,360));
//		hBoxActToAct.setPreferredSize(new Dimension(160,360));
//		ImageIcon aToBImg = new ImageIcon("C:\\PromTest\\Packages\\OCCM\\src\\images\\atob.png");
//		Image img2 = aToBImg.getImage();
//		Image imgScale2 = img2.getScaledInstance(80,360, SCALE_SMOOTH);
//		ImageIcon scaledIcon2 = new ImageIcon(imgScale2);
//		JLabel picLabel2= new JLabel();
//		picLabel2.setMaximumSize(new Dimension(80,360));
//		picLabel2.setMinimumSize(new Dimension(80,360));
//		picLabel2.setPreferredSize(new Dimension(80,360));
//		picLabel2.setIcon(scaledIcon2);
//		picLabel2.setVisible(true);
//		hBoxActToAct.add(picLabel2);
//		Box vBoxActToAct = Box.createVerticalBox();
//		vBoxActToAct.add(Box.createVerticalStrut(10));
//
////		JLabel startActLabel = new JLabel();
////		startActLabel.setText("<html><p style=\"width:40px\">"+cm.startEntityName+"</p></html>");
////
////		JLabel targetActLabel = new JLabel();
////		targetActLabel.setText("<html><p style=\"width:40px\">"+cm.targetEntityName+"</p></html>");
////		vBoxActToAct.add(startActLabel);
////		vBoxActToAct.add(Box.createVerticalStrut(280));
////		vBoxActToAct.add(targetActLabel);
////		hBoxActToAct.add(vBoxActToAct);
//
//		Box vBoxTemp = Box.createVerticalBox();
//		Box hBoxTimeToTime = Box.createHorizontalBox();
//		JTextField minTimeJtf = new JTextField();
//		JTextField maxTimeJtf = new JTextField();
//		minTimeJtf.setMaximumSize(new Dimension(40,20));
//		minTimeJtf.setMinimumSize(new Dimension(40,20));
//		minTimeJtf.setPreferredSize(new Dimension(40,20));
//		maxTimeJtf.setMaximumSize(new Dimension(80,20));
//		maxTimeJtf.setMinimumSize(new Dimension(80,20));
//		maxTimeJtf.setPreferredSize(new Dimension(80,20));
//		hBoxTimeToTime.add(minTimeJtf);
//		hBoxTimeToTime.add(new JLabel(" to "));
//		hBoxTimeToTime.add(maxTimeJtf);
//		JComboBox<String> jcb_time_symbol = new JComboBox<String>();
//		for (String ts: timeUnit) {
//			jcb_time_symbol.addItem(ts);
//		}
//		jcb_time_symbol.setSize(new Dimension(80, 20));
//		jcb_time_symbol.setMaximumSize(new Dimension(80, 20));
//		jcb_time_symbol.setPreferredSize(new Dimension(80, 20));
//		hBoxTimeToTime.add(Box.createHorizontalStrut(5));
//		hBoxTimeToTime.add(jcb_time_symbol);
//
//		vBoxTemp.add(hBoxTimeToTime);
//		vBoxTemp.add(hBoxActToAct);
//		vBoxTemp.setMaximumSize(new Dimension(180,400));
//		vBoxTemp.setMinimumSize(new Dimension(180,400));
//		vBoxTemp.setPreferredSize(new Dimension(180,400));
//		vBoxTemp.setVisible(false);
//
//		temporalPanel.add(vBoxTemp);
//		JScrollPane jsp4 = new JScrollPane(temporalPanel);
//		hBox.add(jsp4);
//
//
//		// btn panel
//		JPanel btnPanel = new JPanel();
//		btnPanel.setMaximumSize(new Dimension(420,60));
//		btnPanel.setMinimumSize(new Dimension(420,60));
//		btnPanel.setPreferredSize(new Dimension(420,60));
//
//		for (String name : names) {
//			JRadioButton jrb = new JRadioButton(name);
//			jrb.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					ImageIcon myPicture = new ImageIcon("C:\\PromTest\\Packages\\OCCM\\src\\images\\"+objToAct+name+".png");
//					Image img = myPicture.getImage();
//					Image imgScale = img.getScaledInstance(400,400, Image.SCALE_SMOOTH);
//					ImageIcon scaledIcon = new ImageIcon(imgScale);
//					picLabel.setIcon(scaledIcon);
//					picLabel.setVisible(true);
//					picPanel.repaint();
//
//					if (name.equals("Chain response")
//							|| name.equals("Alternate response")
//							||name.equals("First-last response")){
//						vBoxTemp.setVisible(true);
//						vBoxTemp.repaint();
//					}
//					else{
//						vBoxTemp.setVisible(false);
//						vBoxTemp.repaint();
//					}
//					temporalCons = name;
//				}
//			});
//
//			constraintList.add(jrb);
//			constraintBg.add(jrb);
//			constraintPanel.add(jrb);
//		}
//
//
//		// add a confirm button
//		JButton jbConfirm = new JButton("Confirm");
//		jbConfirm.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//
//				// set the reference objtype
//				List<String> objTypeSelected = new ArrayList<>(objSelectedMap.keySet());
//				String firstRefObj = "";
//				String secondRefObj = "";
//				if (objTypeSelected.size() == 0){
//					JDialog jdWarn = new JDialog();
//					jdWarn.add(new JLabel("Reference object type not selected"));
//				}
//				else if (objTypeSelected.size() == 1) {
//					firstRefObj = objTypeSelected.get(0);
//				}
//				else{
//					firstRefObj = objTypeSelected.get(0);
//					secondRefObj = objTypeSelected.get(1);
//				}
//
//				// set the constraint time
//				String minTime = minTimeJtf.getText();
//				String maxTime = maxTimeJtf.getText();
//
//				// set the temporal constraint
//				cm.setActToAct(
//						cm.startEntityName,
//						cm.targetEntityName,
//						firstRefObj,
//						secondRefObj,
//						temporalCons,
//						minTime,
//						maxTime,
//						jcb_time_symbol.getSelectedItem().toString()
//				);
//
////				String selectedTimeUnit = jcb_time_symbol.getSelectedItem().toString();
////				setConstraint((mxCell)cell, graph, firstRefObj, secondRefObj, minTime, maxTime, selectedTimeUnit);
//				((mxCell)cell).setType("constraint");
//
//
//				String startTip = "";
//				((mxCell)cell).setTip("Given " +
//						startTip +
//						", activity " + cm.targetEntityName +
//						"should follow " + temporalCons);
//
//				graph.getModel().beginUpdate();
//				graph.getModel().endUpdate();
//				dialog.dispose();
//			}
//		});
//
//		// add a cancel button
//		JButton jbCancel = new JButton("Cancel");
//		jbCancel.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// remove the edge
//				graph.getModel().beginUpdate();
//				graph.removeCells(new Object[]{cell});
//				graph.getModel().endUpdate();
//				dialog.dispose();
//			}
//		});
////
//		Box hBox1 = Box.createHorizontalBox();
//		hBox1.add(Box.createHorizontalStrut(20));
//		hBox1.add(jbConfirm);
//		hBox1.add(Box.createHorizontalStrut(10));
//		hBox1.add(jbCancel);
//
//		vBox.add(hBox);
//		vBox.add(hBox1);
//		dialog.getContentPane().add(vBox);
//	}

//	public void getObjToObjCardDialog(Object cell,
//									  mxGraph graph,
//									  String firstObjType,
//									  String secondObjType) {
//		JDialog dialog = new JDialog();
//		dialog.setTitle("Cardinality setting");
//		dialog.setMinimumSize(new Dimension(670,370));
//		dialog.setMaximumSize(new Dimension(670,370));
//		dialog.setPreferredSize(new Dimension(670,370));
//		dialog.setVisible(true);
//		dialog.setLocationRelativeTo(null);  // set to the center of the screen
//
//		Box hBoxForAll = Box.createHorizontalBox();
//
//		ArrayList<String> actList = new ArrayList<>();
//		actList.add("Any activity");
//		actList.add("1");
//		actList.add("2");
//		actList.add("3");
//		actList.add("4");
//		actList.add("5");
//		// Select from the list of activities
//		JPanel refActivityPanel = new JPanel(new GridLayout(actList.size(), 1, 0, 10));
//		refActivityPanel.setMaximumSize(new Dimension(170,280));
//		refActivityPanel.setMinimumSize(new Dimension(170,280));
//		refActivityPanel.setPreferredSize(new Dimension(170,280));
//		ButtonGroup refActivityBg = new ButtonGroup();
//		for (String name : actList) {
//			JRadioButton jrb = new JRadioButton(name);
//			refActivityBg.add(jrb);
//			refActivityPanel.add(jrb);
//			jrb.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					refActForObjToObjCardinality = name;
//				}
//			});
//		}
//		refActivityPanel.setBorder(BorderFactory.createTitledBorder("Reference activity"));
//
//		JScrollPane jsp1 = new JScrollPane(refActivityPanel);
//		hBoxForAll.add(jsp1);
//
//
//		Box vBox = Box.createVerticalBox();
//		vBox.setMinimumSize(new Dimension(490,280));
//		vBox.setMaximumSize(new Dimension(490,280));
//		vBox.setPreferredSize(new Dimension(490,280));
//
//		Box hBox = Box.createHorizontalBox();
//		hBox.setMinimumSize(new Dimension(430,160));
//		hBox.setMaximumSize(new Dimension(430,160));
//		hBox.setPreferredSize(new Dimension(430,160));
//		// set horizontal box for setting cardinality
//		Box vBoxForBtn1 = Box.createVerticalBox();
//		JButton jbnForFirstObjType = new JButton(firstObjType);
//		jbnForFirstObjType.setMinimumSize(new Dimension(80,30));
//		jbnForFirstObjType.setMaximumSize(new Dimension(80,30));
//		jbnForFirstObjType.setPreferredSize(new Dimension(80,30));
//
//		vBoxForBtn1.add(Box.createVerticalStrut(20));
//		vBoxForBtn1.add(jbnForFirstObjType);
//		vBoxForBtn1.setMinimumSize(new Dimension(80,100));
//		vBoxForBtn1.setMaximumSize(new Dimension(80,100));
//		vBoxForBtn1.setPreferredSize(new Dimension(80,100));
//
//		Box vBoxForSetting = Box.createVerticalBox();
//		Box hBoxForSetting = Box.createHorizontalBox();
//		Box hBoxForSetting2 = Box.createHorizontalBox();
//
//		vBoxForSetting.setMinimumSize(new Dimension(220,100));
//		vBoxForSetting.setMaximumSize(new Dimension(220,100));
//		vBoxForSetting.setPreferredSize(new Dimension(220,100));
//		hBoxForSetting.setMinimumSize(new Dimension(220,100));
//		hBoxForSetting.setMaximumSize(new Dimension(220,100));
//		hBoxForSetting.setPreferredSize(new Dimension(220,100));
//		hBoxForSetting2.setMinimumSize(new Dimension(220,10));
//		hBoxForSetting2.setMaximumSize(new Dimension(220,10));
//		hBoxForSetting2.setPreferredSize(new Dimension(220,10));
//
//		JComboBox<String> jcb_card_first = new JComboBox<String>();
//		for (String ts: card) {
//			jcb_card_first.addItem(ts);
//		}
//
//		jcb_card_first.setMinimumSize(new Dimension(50,20));
//		jcb_card_first.setMaximumSize(new Dimension(50,20));
//		jcb_card_first.setPreferredSize(new Dimension(50,20));
//
//		JComboBox<String> jcb_card_second = new JComboBox<String>();
//		for (String ts: card) {
//			jcb_card_second.addItem(ts);
//		}
//		jcb_card_second.setMinimumSize(new Dimension(50,20));
//		jcb_card_second.setMaximumSize(new Dimension(50,20));
//		jcb_card_second.setPreferredSize(new Dimension(50,20));
//
//		hBoxForSetting.add(jcb_card_first);
//		hBoxForSetting.add(Box.createHorizontalStrut(120));
//		hBoxForSetting.add(jcb_card_second);
//
//		JLabel jlbForSetting = new JLabel("--------------------------------------------------");
//		jlbForSetting.setAlignmentX(JLabel.CENTER_ALIGNMENT);
//		jlbForSetting.setMinimumSize(new Dimension(200,10));
//		jlbForSetting.setMaximumSize(new Dimension(200,10));
//		jlbForSetting.setPreferredSize(new Dimension(200,10));
//
//		hBoxForSetting2.add(jlbForSetting);
//		vBoxForSetting.add(Box.createVerticalStrut(20));
//		vBoxForSetting.add(hBoxForSetting2);
//		vBoxForSetting.add(Box.createVerticalStrut(-20));
//		vBoxForSetting.add(hBoxForSetting);
//
//		Box vBoxForBtn2 = Box.createVerticalBox();
//		JButton jbnForSecondObjType = new JButton(secondObjType);
//		jbnForSecondObjType.setMinimumSize(new Dimension(80,30));
//		jbnForSecondObjType.setMaximumSize(new Dimension(80,30));
//		jbnForSecondObjType.setPreferredSize(new Dimension(80,30));
//		vBoxForBtn2.add(Box.createVerticalStrut(20));
//		vBoxForBtn2.add(jbnForSecondObjType);
//		vBoxForBtn2.setMinimumSize(new Dimension(80,100));
//		vBoxForBtn2.setMaximumSize(new Dimension(80,100));
//		vBoxForBtn2.setPreferredSize(new Dimension(80,100));
//
//		hBox.add(Box.createHorizontalStrut(10));
//		hBox.add(vBoxForBtn1);
//		hBox.add(Box.createHorizontalStrut(10));
//		hBox.add(vBoxForSetting);
//		hBox.add(Box.createHorizontalStrut(10));
//		hBox.add(vBoxForBtn2);
//
//		// add a confirm button
//		JButton jbConfirm = new JButton("Confirm");
//		jbConfirm.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				graph.getModel().beginUpdate();
//				((mxCell) cell).setStyle("strokeWidth=2;straight;startArrow=none;endArrow=none;strokeColor=#7F00FF");
//				((mxCell) cell).setType("constraint");
//
//				// get the first cardinality
//				String firsCard = jcb_card_first.getSelectedItem().toString();
//
//				// get the second cardinality
//				String secondCard = jcb_card_second.getSelectedItem().toString();
//
//				// set the tip
//				String tipCard = "The cardinality between object type "+firstObjType+
//						" and " + secondObjType +" is " + firsCard +".."+secondCard;
//
//				// get the selected cardinality type
//				((mxCell) cell).setTip(tipCard);
//
//				// add two ports to show the cardinality
//				mxGeometry geo1 = new mxGeometry(-0.6, 0, 0,
//						0);
//				geo1.setOffset(new mxPoint(0, 10));
//				geo1.setRelative(true);
//				mxCell port1 = new mxCell(null, geo1,
//						"",
//						"objCardPort",
//						"");
//				port1.setVertex(true);
//				port1.setValue(firsCard);
//				graph.addCell(port1, cell);
//
//				mxGeometry geo2 = new mxGeometry(0.6, 0, 0,
//						0);
//				geo2.setOffset(new mxPoint(0, 10));
//				geo2.setRelative(true);
//				mxCell port2 = new mxCell(null, geo2,
//						"",
//						"objCardPort",
//						"");
//				port2.setVertex(true);
//				port2.setValue(secondCard);
//				graph.addCell(port2, cell);
//
//				if (!refActForObjToObjCardinality.equals("Any activity")){
//					mxGeometry geo3 = new mxGeometry(0, 0, 6,
//							6);
//					geo3.setOffset(new mxPoint(-3, -3));
//					geo3.setRelative(true);
//					mxCell port3 = new mxCell(null, geo3,
//							"",
//							"objCardPort",
//							"");
//					port3.setVertex(true);
//					graph.addCell(port3, cell);
//					port3.setVisible(false);
//
//					double xForRefAct = startX + (targetX - startX)/2 + (targetY - startY)/2;
//					double yForRefAct = startY + (targetY - startY)/2 + (targetX - startX)/2;
//
//					// 娣诲姞涓�涓猺ef act
//					mxCell refActVertex = (mxCell) graph.insertVertex(graph.getDefaultParent(),
//							"",
//							"Activity:\n"+refActForObjToObjCardinality,
//							"refActForObj",
//							xForRefAct, yForRefAct,
//							80, 80,
//							"rhombus;fillColor=#C0C0C0;fontSize=12");
//					graph.insertEdge(graph.getDefaultParent(),
//							null, "ref",
//							port3,
//							refActVertex,
//							"straight;strokeWidth=2;endArrow=none;dashed=1;strokeColor=#C0C0C0",
//							"Reference activity for the cardinality");
//
//				}
//
//				graph.getModel().endUpdate();
//				dialog.dispose();
//			}
//		});
//
//		// add a cancel button
//		JButton jbCancel = new JButton("Cancel");
//		jbCancel.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// remove the edge
//				graph.getModel().beginUpdate();
//				graph.removeCells(new Object[]{cell});
//				graph.getModel().endUpdate();
//				dialog.dispose();
//			}
//		});
//
//		Box hBox1 = Box.createHorizontalBox();
//		hBox1.add(Box.createHorizontalStrut(10));
//		hBox1.add(jbConfirm);
//		hBox1.add(Box.createHorizontalStrut(10));
//		hBox1.add(jbCancel);
//
//		JPanel cardPanel = new JPanel();
//		cardPanel.setMaximumSize(new Dimension(440,280));
//		cardPanel.setMinimumSize(new Dimension(440,280));
//		cardPanel.setPreferredSize(new Dimension(440,280));
//		cardPanel.setBorder(BorderFactory.createTitledBorder("Cardinality constraint"));
//		cardPanel.add(hBox);
//		JScrollPane jsp2 = new JScrollPane(cardPanel);
//		hBoxForAll.add(jsp2);
//
//		vBox.add(hBoxForAll);
//		vBox.add(Box.createVerticalStrut(10));
//		vBox.add(hBox1);
//		dialog.getContentPane().add(vBox);
//	}

	
//	public void getTimePerfDialog(Object cell,
//			 mxGraph graph,
//			 String refAct) {
//System.out.println(refAct);
//JDialog dialog = new JDialog();
//dialog.setAlwaysOnTop(true);
//dialog.setTitle("Time Performance Constraint");
//dialog.setSize(new Dimension(400,100));
//dialog.setVisible(true);
//dialog.setLocationRelativeTo(null);  // set to the center of the screen
//Box hBox = Box.createHorizontalBox();
//Box vBox = Box.createVerticalBox();
//
//JPanel constraintPanel = new JPanel(new GridLayout(8, 1, 10, 0));
//List<String> names = Arrays.asList(
//"Waiting time",
//"Synchronization time",
//"Lagging time",
//"Response time",
//"Throughput time",
//"Clogging time",
//"Pooling time");
//JComboBox<String> jcb = new JComboBox<>();
//
//for (String name : names) {
//jcb.addActionListener(new ActionListener() {
//@Override
//public void actionPerformed(ActionEvent e) {
//timePerformanceCons = name;
//}
//});
//jcb.addItem(name);
//}
//constraintPanel.add(jcb);
//jcb.setMaximumSize(new Dimension(150,30));
//jcb.setMinimumSize(new Dimension(150,30));
//jcb.setPreferredSize(new Dimension(150,30));
//hBox.add(jcb);
//
//// btn panel
//JPanel btnPanel = new JPanel();
//btnPanel.setMaximumSize(new Dimension(150,70));
//btnPanel.setMinimumSize(new Dimension(150,70));
//btnPanel.setPreferredSize(new Dimension(150,70));
//
//// add a confirm button
//JButton jbConfirm = new JButton("Confirm");
//jbConfirm.addActionListener(new ActionListener() {
//@Override
//public void actionPerformed(ActionEvent e) {
//// set the constraint time
//graph.getModel().beginUpdate();
//graph.getModel().endUpdate();
//dialog.dispose();
//}
//});
//
//// add a cancel button
//JButton jbCancel = new JButton("Cancel");
//jbCancel.addActionListener(new ActionListener() {
//@Override
//public void actionPerformed(ActionEvent e) {
//// remove the edge
//graph.getModel().beginUpdate();
//graph.removeCells(new Object[]{cell});
//graph.getModel().endUpdate();
//dialog.dispose();
//}
//});
//Box hBox1 = Box.createHorizontalBox();
//hBox1.add(Box.createHorizontalStrut(10));
//hBox1.add(jbConfirm);
//hBox1.add(Box.createHorizontalStrut(10));
//hBox1.add(jbCancel);
//vBox.add(hBox);
//vBox.add(hBox1);
//dialog.getContentPane().add(vBox);
//}

}

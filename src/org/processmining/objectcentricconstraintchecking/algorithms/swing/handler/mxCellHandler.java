/**
 * Copyright (c) 2008-2012, JGraph Ltd
 */
package org.processmining.objectcentricconstraintchecking.algorithms.swing.handler;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.processmining.objectcentricconstraintchecking.algorithms.model.mxCell;
import org.processmining.objectcentricconstraintchecking.algorithms.model.mxGraphModel;
import org.processmining.objectcentricconstraintchecking.algorithms.swing.mxGraphComponent;
import org.processmining.objectcentricconstraintchecking.algorithms.swing.util.mxSwingConstants;
import org.processmining.objectcentricconstraintchecking.algorithms.util.mxRectangle;
import org.processmining.objectcentricconstraintchecking.algorithms.view.mxCellState;
import org.processmining.objectcentricconstraintchecking.algorithms.view.mxGraph;

/**
 * @author Administrator
 * 
 */
public class mxCellHandler
{
	/**
	 * Reference to the enclosing graph component.
	 */
	protected mxGraphComponent graphComponent;

	/**
	 * Holds the cell state associated with this handler.
	 */
	protected mxCellState state;

	/**
	 * Holds the rectangles that define the handles.
	 */
	protected Rectangle[] handles;

	/**
	 * Specifies if the handles should be painted. Default is true.
	 */
	protected boolean handlesVisible = true;

	/**
	 * Holds the bounding box of the handler.
	 */
	protected transient Rectangle bounds;

	/**
	 * Holds the component that is used for preview.
	 */
	protected transient JComponent preview;

	/**
	 * Holds the start location of the mouse gesture.
	 */
	protected transient Point first;

	/**
	 * Holds the index of the handle that was clicked.
	 */
	protected transient int index;

	boolean flagUsePreciseTime = false;


	protected static List<String> timeSymbol = Arrays.asList(
			"=",
			"≥",
			">",
			"≤",
			"<");

	protected static List<String> timeUnit = Arrays.asList(
			"seconds",
			"minutes",
			"hours",
			"days",
			"weeks",
			"months",
			"years");

	/**
	 * Constructs a new cell handler for the given cell state.
	 * 
	 * @param graphComponent Enclosing graph component.
	 * @param state Cell state for which the handler is created.
	 */
	public mxCellHandler(mxGraphComponent graphComponent, mxCellState state)
	{
		this.graphComponent = graphComponent;
		refresh(state);
	}

	/**
	 * 
	 */
	public boolean isActive()
	{
		return first != null;
	}

	/**
	 * Refreshes the cell handler.
	 */
	public void refresh(mxCellState state)
	{
		this.state = state;
		handles = createHandles();
		mxGraph graph = graphComponent.getGraph();
		mxRectangle tmp = graph.getBoundingBox(state.getCell());

		if (tmp != null)
		{
			bounds = tmp.getRectangle();

			if (handles != null)
			{
				for (int i = 0; i < handles.length; i++)
				{
					if (isHandleVisible(i))
					{
						bounds.add(handles[i]);
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	public mxGraphComponent getGraphComponent()
	{
		return graphComponent;
	}

	/**
	 * Returns the cell state that is associated with this handler.
	 */
	public mxCellState getState()
	{
		return state;
	}

	/**
	 * Returns the index of the current handle.
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * Returns the bounding box of this handler.
	 */
	public Rectangle getBounds()
	{
		return bounds;
	}

	/**
	 * Returns true if the label is movable.
	 */
	public boolean isLabelMovable()
	{
		mxGraph graph = graphComponent.getGraph();
		String label = graph.getLabel(state.getCell());

		return graph.isLabelMovable(state.getCell()) && label != null
				&& label.length() > 0;
	}

	/**
	 * Returns true if the handles should be painted.
	 */
	public boolean isHandlesVisible()
	{
		return handlesVisible;
	}

	/**
	 * Specifies if the handles should be painted.
	 */
	public void setHandlesVisible(boolean handlesVisible)
	{
		this.handlesVisible = handlesVisible;
	}

	/**
	 * Returns true if the given index is the index of the last handle.
	 */
	public boolean isLabel(int index)
	{
		return index == getHandleCount() - 1;
	}

	/**
	 * Creates the rectangles that define the handles.
	 */
	protected Rectangle[] createHandles()
	{
		return null;
	}

	/**
	 * Returns the number of handles in this handler.
	 */
	protected int getHandleCount()
	{
		return (handles != null) ? handles.length : 0;
	}

	/**
	 * Hook for subclassers to return tooltip texts for certain points on the
	 * handle.
	 */
	public String getToolTipText(MouseEvent e)
	{
		return null;
	}

	/**
	 * Returns the index of the handle at the given location.
	 * 
	 * @param x X-coordinate of the location.
	 * @param y Y-coordinate of the location.
	 * @return Returns the handle index for the given location.
	 */
	public int getIndexAt(int x, int y)
	{
		if (handles != null && isHandlesVisible())
		{
			int tol = graphComponent.getTolerance();
			Rectangle rect = new Rectangle(x - tol / 2, y - tol / 2, tol, tol);

			for (int i = handles.length - 1; i >= 0; i--)
			{
				if (isHandleVisible(i) && handles[i].intersects(rect))
				{
					return i;
				}
			}
		}

		return -1;
	}

	/**
	 * Processes the given event.
	 */
	public void mousePressed(MouseEvent e)
	{
		try {

			// cell
			if (!e.isConsumed() && e.getClickCount() == 2) {
				mxGraph graph = graphComponent.getGraph();
				graph.getModel().beginUpdate();

				try {
					mxCell c1 = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());

					String cellId = c1.getId();

					if (c1.getType().equals("activity")) {
						JDialog dialog = new JDialog();
						dialog.setModal(true);
						dialog.setTitle("Activity setting");
						dialog.setSize(new Dimension(250, 150));
						dialog.setLocationRelativeTo(null);  // set to the center of the screen
						Box vBox = Box.createVerticalBox();
						Box hBox1 = Box.createHorizontalBox();
						Box hBox2 = Box.createHorizontalBox();
						JComboBox<String> jcb = new JComboBox<String>();
						jcb.setSize(new Dimension(200, 50));
						jcb.setMaximumSize(new Dimension(200, 50));
						jcb.setPreferredSize(new Dimension(200, 50));
						hBox1.add(jcb);
						hBox1.setAlignmentX(Component.CENTER_ALIGNMENT);
						vBox.add(Box.createVerticalStrut(20));
						vBox.add(hBox1);
						JButton jbConfirm = new JButton("Confirm");
						jbConfirm.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								String actSelected = String.valueOf(jcb.getSelectedItem());
								dialog.dispose();
								((mxCell) ((mxGraphModel) graph.getModel()).getCell(cellId)).setValue(actSelected);
							}
						});
						JButton jbCancel = new JButton("Cancel");
						jbCancel.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
//								jcb.setSelectedIndex(0);
								dialog.dispose();
							}
						});

						hBox2.add(jbConfirm);
						hBox2.add(Box.createHorizontalStrut(30));
						hBox2.add(jbCancel);
						hBox2.setAlignmentX(Component.CENTER_ALIGNMENT);
						vBox.add(Box.createVerticalStrut(30));
						vBox.add(hBox2);
						vBox.add(Box.createVerticalStrut(30));
						dialog.add(vBox);
						dialog.setVisible(true);
						graph.getModel().endUpdate();
						graph.refresh();
						}
					else if (c1.getType().equals("objType")) {
						JDialog dialog = new JDialog();
						dialog.setModal(true);
						dialog.setTitle("Object type setting");
						dialog.setSize(new Dimension(250, 150));
						dialog.setLocationRelativeTo(null);  // set to the center of the screen
						Box vBox = Box.createVerticalBox();
						Box hBox1 = Box.createHorizontalBox();
						Box hBox2 = Box.createHorizontalBox();
						JComboBox<String> jcb = new JComboBox<String>();
						jcb.setSize(new Dimension(200, 50));
						jcb.setMaximumSize(new Dimension(200, 50));
						jcb.setPreferredSize(new Dimension(200, 50));
						hBox1.add(jcb);
						hBox1.setAlignmentX(Component.CENTER_ALIGNMENT);
						vBox.add(Box.createVerticalStrut(20));
						vBox.add(hBox1);
						JButton jbConfirm = new JButton("Confirm");
						jbConfirm.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								String actSelected = String.valueOf(jcb.getSelectedItem());
								dialog.dispose();
								((mxCell) ((mxGraphModel) graph.getModel()).getCell(cellId)).setValue(actSelected);
							}
						});
						JButton jbCancel = new JButton("Cancel");
						jbCancel.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
//								jcb.setSelectedIndex(0);
								dialog.dispose();
							}
						});

						hBox2.add(jbConfirm);
						hBox2.add(Box.createHorizontalStrut(30));
						hBox2.add(jbCancel);
						hBox2.setAlignmentX(Component.CENTER_ALIGNMENT);
						vBox.add(Box.createVerticalStrut(30));
						vBox.add(hBox2);
						vBox.add(Box.createVerticalStrut(30));
						dialog.add(vBox);
						dialog.setVisible(true);
						graph.getModel().endUpdate();
						graph.refresh();
					}
					else if (c1.getType().equals("tcForActToAct")) {
						getTempConsDialog(c1);
						graph.getModel().beginUpdate();

						graph.getModel().endUpdate();
						graph.refresh();

//						JDialog dialog = new JDialog();
//						dialog.setModal(true);
//						dialog.setTitle("Temporal constraint setting");
//						dialog.setSize(new Dimension(350, 250));
//						dialog.setLocationRelativeTo(null);  // set to the center of the screen
//						Box vBox = Box.createVerticalBox();
//						Box hBox1 = Box.createHorizontalBox();
//						hBox1.add(Box.createHorizontalStrut(10));
//						JLabel jl_relation = new JLabel("relationship: ");
//						hBox1.add(jl_relation);
//						JComboBox<String> jcbRel = new JComboBox<String>();
//						jcbRel.addItem("");
//						jcbRel.addItem("1");
//						jcbRel.addItem("2");
//						jcbRel.addItem("3");
//						jcbRel.setSize(new Dimension(200, 20));
//						jcbRel.setMaximumSize(new Dimension(200, 20));
//						jcbRel.setPreferredSize(new Dimension(200, 20));
//						hBox1.add(jcbRel);
//
//						// use checkbox
//						JCheckBox jCheckBox = new JCheckBox();
//
//
//						JComboBox<String> jcbSymbol = new JComboBox<String>();
//						for (String ts: timeSymbol) {
//							jcbSymbol.addItem(ts);
//						}
//
//						JComboBox<String> jcbTimeUnit = new JComboBox<String>();
//						for (String unit: timeUnit) {
//							jcbTimeUnit.addItem(unit);
//						}
//
//						Box hBox2 = Box.createHorizontalBox();
//
//						jcbSymbol.setSize(new Dimension(60, 20));
//						jcbSymbol.setMaximumSize(new Dimension(60, 20));
//						jcbSymbol.setPreferredSize(new Dimension(60, 20));
//						jcbTimeUnit.setSize(new Dimension(80, 20));
//						jcbTimeUnit.setMaximumSize(new Dimension(80, 20));
//						jcbTimeUnit.setPreferredSize(new Dimension(80, 20));
//						JLabel jlb_time = new JLabel("time: ");
//						jlb_time.setToolTipText("The time duration of the waiting time for the current activity");
//
//						JTextField jtf_time = new JTextField();
//						jtf_time.setSize(new Dimension(60, 20));
//						jtf_time.setMaximumSize(new Dimension(60, 20));
//						jtf_time.setPreferredSize(new Dimension(60, 20));
//
//
//						jCheckBox.addItemListener(new ItemListener() {
//							@Override
//							public void itemStateChanged(ItemEvent e) {
//								if(e.getStateChange() == ItemEvent.SELECTED) {
//									//checkbox has been selected
//									flagUsePreciseTime = true;
//								} else {
//									//checkbox has been deselected
//									flagUsePreciseTime = false;
//								};
//							}
//						});
//
//						hBox2.add(jCheckBox);
//						hBox2.add(jlb_time);
//						hBox2.add(jcbSymbol);
//						hBox2.add(jtf_time);
//						hBox2.add(jcbTimeUnit);
//
//						Box hBox3 = Box.createHorizontalBox();
//
//						vBox.add(Box.createVerticalStrut(20));
//						vBox.add(hBox1);
//						JButton jbConfirm = new JButton("Confirm");
//						jbConfirm.addActionListener(new ActionListener() {
//							@Override
//							public void actionPerformed(ActionEvent e) {
//								if (flagUsePreciseTime) {
//									// save the precise time
//									String actSelected = String.valueOf(jcbRel.getSelectedItem());
//
//									String selSymbol = (String) jcbSymbol.getSelectedItem();
//									String selUnit = (String) jcbTimeUnit.getSelectedItem();
//									Double timeGap = 1.0;
//
//									switch (selUnit){
//										case "seconds":
//											timeGap = Double.valueOf(jtf_time.getText());
//											break;
//										case "minutes":
//											timeGap = Double.valueOf(jtf_time.getText())*60;
//											break;
//										case "hours":
//											timeGap = Double.valueOf(jtf_time.getText())*3600;
//											break;
//										case "days":
//											timeGap = Double.valueOf(jtf_time.getText())*864000;
//											break;
//										case "weeks":
//											timeGap = Double.valueOf(jtf_time.getText())*864000*7;
//											break;
//										case "months":
//											timeGap = Double.valueOf(jtf_time.getText())*864000*7*30;
//											break;
//										case "years":
//											timeGap = Double.valueOf(jtf_time.getText())*864000*7*30*365;
//											break;
//										default:
//											break;
//									}
//									dialog.dispose();
//
//									//设定middleware里的时间
//
//
//									// 更新order的value
//									((mxCell) ((mxGraphModel) graph.getModel()).getCell(cellId)).setValue(actSelected+
//											"\n"+selSymbol+timeGap+selUnit);
//								}
//								else {
//									// not save the precise time
//									String actSelected = String.valueOf(jcbRel.getSelectedItem());
//									dialog.dispose();
//									((mxCell) ((mxGraphModel) graph.getModel()).getCell(cellId)).setValue(actSelected);
//								}
//							}
//						});
//
//						JButton jbReset = new JButton("Reset");
//						jbReset.addActionListener(new ActionListener() {
//							@Override
//							public void actionPerformed(ActionEvent e) {
//								jtf_time.setText("");
//								jCheckBox.setSelected(false);
//								jcbRel.setSelectedIndex(0);
//								jcbSymbol.setSelectedIndex(0);
//								jcbTimeUnit.setSelectedIndex(0);
//							}
//						});
//
//						JButton jbCancel = new JButton("Cancel");
//						jbCancel.addActionListener(new ActionListener() {
//							@Override
//							public void actionPerformed(ActionEvent e) {
//								jtf_time.setText("");
//								jcbRel.setSelectedIndex(0);
//								jcbSymbol.setSelectedIndex(0);
//								jcbTimeUnit.setSelectedIndex(0);
//								dialog.dispose();
//							}
//						});
//
//						hBox3.add(jbConfirm);
//						hBox3.add(Box.createHorizontalStrut(30));
//
//						hBox3.add(jbReset);
//						hBox3.add(Box.createHorizontalStrut(30));
//
//						hBox3.add(jbCancel);
//						hBox3.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//						// setup the whole layout
//						vBox.add(Box.createVerticalStrut(30));
//						vBox.add(hBox2);
//						vBox.add(Box.createVerticalStrut(30));
//						vBox.add(hBox3);
//						vBox.add(Box.createVerticalStrut(30));
//						dialog.add(vBox);
//						dialog.setVisible(true);
//						graph.getModel().endUpdate();
//						graph.refresh();
					}
					else if (c1.getType().equals("constraint")) {
						Component[] components=graphComponent.getParent().getComponents();
						for(Component c:components){
							if (c instanceof JTextField){
								((JTextField) c).setText(c1.getTip());
							};
						}
					}

					}


				catch (Exception exception){

				}
//					System.out.println(c1.getValue());
				}

			}
		catch (Exception exception){

		}
	}

	public void getTempConsDialog(mxCell cell){
		JDialog dialog = new JDialog();
		dialog.setTitle("Temporal constraint setting");
		dialog.setSize(new Dimension(500,150));
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);  // set to the center of the screen

		Box vBox2 = Box.createVerticalBox();

		JComboBox<String> jcb_waiting_symbol = new JComboBox<String>();
		for (String ts: timeSymbol) {
			jcb_waiting_symbol.addItem(ts);
		}
		jcb_waiting_symbol.setSize(new Dimension(40, 20));
		jcb_waiting_symbol.setMaximumSize(new Dimension(40, 20));
		jcb_waiting_symbol.setPreferredSize(new Dimension(40, 20));

		JLabel jlb_waiting = new JLabel("Temporal constraint:");

		JTextField jtf_day = new JTextField();
		jtf_day.setSize(new Dimension(30, 20));
		jtf_day.setMaximumSize(new Dimension(30, 20));
		jtf_day.setPreferredSize(new Dimension(30, 20));

		JTextField jtf_hour = new JTextField();
		jtf_hour.setSize(new Dimension(25, 20));
		jtf_hour.setMaximumSize(new Dimension(25, 20));
		jtf_hour.setPreferredSize(new Dimension(25, 20));

		JTextField jtf_minute = new JTextField();
		jtf_minute.setSize(new Dimension(25, 20));
		jtf_minute.setMaximumSize(new Dimension(25, 20));
		jtf_minute.setPreferredSize(new Dimension(25, 20));

		JTextField jtf_second = new JTextField();
		jtf_second.setSize(new Dimension(25, 20));
		jtf_second.setMaximumSize(new Dimension(25, 20));
		jtf_second.setPreferredSize(new Dimension(25, 20));

		JLabel jlb_day = new JLabel("days");
		JLabel jlb_hour = new JLabel("hours");
		JLabel jlb_minute = new JLabel("minutes");
		JLabel jlb_second = new JLabel("seconds");
		jlb_day.setSize(new Dimension(30, 20));
		jlb_day.setMaximumSize(new Dimension(30, 20));
		jlb_day.setPreferredSize(new Dimension(30, 20));
		jlb_hour.setSize(new Dimension(40, 20));
		jlb_hour.setMaximumSize(new Dimension(40, 20));
		jlb_hour.setPreferredSize(new Dimension(40, 20));
		jlb_minute.setSize(new Dimension(50, 20));
		jlb_minute.setMaximumSize(new Dimension(50, 20));
		jlb_minute.setPreferredSize(new Dimension(50, 20));
		jlb_second.setSize(new Dimension(50, 20));
		jlb_second.setMaximumSize(new Dimension(50, 20));
		jlb_second.setPreferredSize(new Dimension(50, 20));


		// use hBox waiting to take
		Box hBox_waiting = Box.createHorizontalBox();
		hBox_waiting.add(Box.createHorizontalStrut(5));
		hBox_waiting.add(jlb_waiting);
		hBox_waiting.add(Box.createHorizontalStrut(10));
		hBox_waiting.add(jcb_waiting_symbol);

		hBox_waiting.add(Box.createHorizontalStrut(5));
		hBox_waiting.add(jtf_day);
		hBox_waiting.add(Box.createHorizontalStrut(3));
		hBox_waiting.add(jlb_day);

		hBox_waiting.add(Box.createHorizontalStrut(5));
		hBox_waiting.add(jtf_hour);
		hBox_waiting.add(Box.createHorizontalStrut(3));
		hBox_waiting.add(jlb_hour);

		hBox_waiting.add(Box.createHorizontalStrut(5));
		hBox_waiting.add(jtf_minute);
		hBox_waiting.add(Box.createHorizontalStrut(3));
		hBox_waiting.add(jlb_minute);

		hBox_waiting.add(Box.createHorizontalStrut(5));
		hBox_waiting.add(jtf_second);
		hBox_waiting.add(Box.createHorizontalStrut(3));
		hBox_waiting.add(jlb_second);

		hBox_waiting.add(Box.createHorizontalStrut(5));
		vBox2.add(Box.createVerticalStrut(20));
		vBox2.add(hBox_waiting);

		// add a confirm button
		JButton jbConfirm = new JButton("Confirm");
		jbConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// get the time gap
				// To be done

				cell.setTip("The temporal constraint is " +
						jtf_day.getText() + " days, " +
						jtf_hour.getText() + " hours, " +
						jtf_minute.getText() + "minutes, " +
						jtf_second.getText() + "seconds.");


				String timeVal = jcb_waiting_symbol.getSelectedItem().toString() +jtf_day.getText() + " days, " +
						jtf_hour.getText() + " hours, " +
						jtf_minute.getText() + "minutes, " +
						jtf_second.getText() + "seconds.";

				cell.setValue(timeVal);

				dialog.dispose();
			}
		});

		// add a cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				dialog.dispose();
			}
		});

		Box hBoxBtn = Box.createHorizontalBox();
		hBoxBtn.add(jbConfirm);
		hBoxBtn.add(Box.createHorizontalStrut(20));
		hBoxBtn.add(jbCancel);
		vBox2.add(hBoxBtn);
		dialog.add(vBox2);
	}

	/**
	 * Processes the given event.
	 */
	public void mouseMoved(MouseEvent e)
	{
		if (!e.isConsumed() && handles != null)
		{
			int index = getIndexAt(e.getX(), e.getY());

			if (index >= 0 && isHandleEnabled(index))
			{
				Cursor cursor = getCursor(e, index);

				if (cursor != null)
				{
					graphComponent.getGraphControl().setCursor(cursor);
					e.consume();
				}
				else
				{
					graphComponent.getGraphControl().setCursor(
							new Cursor(Cursor.HAND_CURSOR));
				}
			}
		}
	}

	/**
	 * Processes the given event.
	 */
	public void mouseDragged(MouseEvent e)
	{
		// empty
	}

	/**
	 * Processes the given event.
	 */
	public void mouseReleased(MouseEvent e)
	{
		reset();
	}

	/**
	 * Starts handling a gesture at the given handle index.
	 */
	public void start(MouseEvent e, int index)
	{
		this.index = index;
		first = e.getPoint();
		preview = createPreview();

		if (preview != null)
		{
			graphComponent.getGraphControl().add(preview, 0);
		}
	}

	/**
	 * Returns true if the given event should be ignored.
	 */
	protected boolean isIgnoredEvent(MouseEvent e)
	{
		return graphComponent.isEditEvent(e);
	}

	/**
	 * Creates the preview for this handler.
	 */
	protected JComponent createPreview()
	{
		return null;
	}

	/**
	 * Resets the state of the handler and removes the preview.
	 */
	public void reset()
	{
		if (preview != null)
		{
			preview.setVisible(false);
			preview.getParent().remove(preview);
			preview = null;
		}

		first = null;
	}

	/**
	 * Returns the cursor for the given event and handle.
	 */
	protected Cursor getCursor(MouseEvent e, int index)
	{
		return null;
	}

	/**
	 * Paints the visible handles of this handler.
	 */
	public void paint(Graphics g)
	{
		if (handles != null && isHandlesVisible())
		{
			for (int i = 0; i < handles.length; i++)
			{
				if (isHandleVisible(i)
						&& g.hitClip(handles[i].x, handles[i].y,
								handles[i].width, handles[i].height))
				{
					g.setColor(getHandleFillColor(i));
					g.fillRect(handles[i].x, handles[i].y, handles[i].width,
							handles[i].height);

					g.setColor(getHandleBorderColor(i));
					g.drawRect(handles[i].x, handles[i].y,
							handles[i].width - 1, handles[i].height - 1);
				}
			}
		}
	}

	/**
	 * Returns the color used to draw the selection border. This implementation
	 * returns null.
	 */
	public Color getSelectionColor()
	{
		return null;
	}

	/**
	 * Returns the stroke used to draw the selection border. This implementation
	 * returns null.
	 */
	public Stroke getSelectionStroke()
	{
		return null;
	}

	/**
	 * Returns true if the handle at the specified index is enabled.
	 */
	protected boolean isHandleEnabled(int index)
	{
		return true;
	}

	/**
	 * Returns true if the handle at the specified index is visible.
	 */
	protected boolean isHandleVisible(int index)
	{
		return !isLabel(index) || isLabelMovable();
	}

	/**
	 * Returns the color to be used to fill the handle at the specified index.
	 */
	protected Color getHandleFillColor(int index)
	{
		if (isLabel(index))
		{
			return mxSwingConstants.LABEL_HANDLE_FILLCOLOR;
		}

		return mxSwingConstants.HANDLE_FILLCOLOR;
	}

	/**
	 * Returns the border color of the handle at the specified index.
	 */
	protected Color getHandleBorderColor(int index)
	{
		return mxSwingConstants.HANDLE_BORDERCOLOR;
	}
	
	/**
	 * Invoked when the handler is no longer used. This is an empty
	 * hook for subclassers.
	 */
	protected void destroy()
	{
		// nop
	}

}

/**
 * Copyright (c) 2007-2012, JGraph Ltd
 */
package org.processmining.cachealignment.algorithms.editor;

import org.processmining.cachealignment.algorithms.model.mxCell;
import org.processmining.cachealignment.algorithms.model.mxGeometry;
import org.processmining.cachealignment.algorithms.swing.util.mxGraphTransferable;
import org.processmining.cachealignment.algorithms.swing.util.mxSwingConstants;
import org.processmining.cachealignment.algorithms.util.*;
import org.processmining.cachealignment.algorithms.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class objEditorPalette extends JPanel
{

	/**
	 *
	 */
	private static final long serialVersionUID = 7771113885935187066L;

	/**
	 *
	 */
	protected JLabel selectedEntry = null;

	/**
	 *
	 */
	protected mxEventSource eventSource = new mxEventSource(this);

	/**
	 *
	 */
	protected Color gradientColor = new Color(255, 255, 255);


	public objEditorPalette(Integer rowNum)
	{
		Font font = new Font("Arial", Font.PLAIN, 14);
		setFont(font);
		setBackground(new Color(255, 255, 255));
		setLayout(new GridLayout(0, 1, 0, 20));
		setSize(new Dimension(40,200));
		setMaximumSize(new Dimension(40,200));
		setMinimumSize(new Dimension(40,200));

		// Clears the current selection when the background is clicked
		addMouseListener(new MouseListener()
		{

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
			 */
			public void mousePressed(MouseEvent e)
			{
				clearSelection();
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
			 */
			public void mouseEntered(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
			 */
			public void mouseExited(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
			 */
			public void mouseReleased(MouseEvent e)
			{
			}

		});

		// Shows a nice icon for drag and drop but doesn't import anything
		setTransferHandler(new TransferHandler()
		{
			public boolean canImport(JComponent comp, DataFlavor[] flavors)
			{
				return true;
			}
		});
	}



	/**
	 * 
	 */
	public void setGradientColor(Color c)
	{
		gradientColor = c;
	}

	/**
	 * 
	 */
	public Color getGradientColor()
	{
		return gradientColor;
	}

	/**
	 * 
	 */
	public void paintComponent(Graphics g)
	{
		if (gradientColor == null)
		{
			super.paintComponent(g);
		}
		else
		{
			Rectangle rect = getVisibleRect();

			if (g.getClipBounds() != null)
			{
				rect = rect.intersection(g.getClipBounds());
			}

			Graphics2D g2 = (Graphics2D) g;

			g2.setPaint(new GradientPaint(0, 0, getBackground(), getWidth(), 0,
					gradientColor));
			g2.fill(rect);
		}
	}

	/**
	 * 
	 */
	public void clearSelection()
	{
		setSelectionEntry(null, null);
	}

	/**
	 * 
	 */
	public void setSelectionEntry(JLabel entry, mxGraphTransferable t)
	{
		JLabel previous = selectedEntry;
		selectedEntry = entry;

		if (previous != null)
		{
			previous.setBorder(null);
			previous.setOpaque(false);
		}

		if (selectedEntry != null)
		{
			selectedEntry.setBorder(ShadowBorder.getSharedInstance());
			selectedEntry.setOpaque(true);
		}

		eventSource.fireEvent(new mxEventObject(mxEvent.SELECT, "entry",
				selectedEntry, "transferable", t, "previous", previous));
	}

	/**
	 * 
	 */
	public void setPreferredWidth(int width)
	{
		int cols = Math.max(1, width / 55);
		setPreferredSize(new Dimension(width,
				(getComponentCount() * 55 / cols) + 30));
		revalidate();
	}

	/**
	 * 
	 * @param name
	 * @param icon
	 * @param style
	 * @param width
	 * @param height
	 * @param value
	 */
	public void addEdgeTemplate(final String name, ImageIcon icon,
			String style, int width, int height, Object value)
	{
		mxGeometry geometry = new mxGeometry(0, 0, width, height);
		geometry.setTerminalPoint(new mxPoint(0, height), true);
		geometry.setTerminalPoint(new mxPoint(width, 0), false);
		geometry.setRelative(true);

		mxCell cell = new mxCell(value, geometry, style);
		cell.setEdge(true);

		addObjTemplate(name, icon, cell);
		addActTemplate(name, icon, cell);

	}

//	/**
//	 *
//	 * @param name
//	 * @param icon
//	 * @param style
//	 * @param width
//	 * @param height
//	 * @param value
//	 */
//	public void addObjTemplate(final String name, ImageIcon icon, String style,
//			int width, int height, Object value)
//	{
//		mxCell cell = new mxCell(value, new mxGeometry(0, 0, width, height),
//				style, "objType", "Object type \nis"+value);
//		cell.setVertex(true);
//
//		addObjTemplate(name, icon, cell);
//	}


	/**
	 *
	 * @param name
	 * @param icon
	 * @param style
	 * @param width
	 * @param height
	 * @param value
	 */
	public void addObjTemplate(final String name, ImageIcon icon, String style,
							   int width, int height, Object value)
	{
		mxCell cell = new mxCell(value, new mxGeometry(0, 0, width, height),
				style, "objType", "Object type \nis"+value);
		cell.setVertex(true);

		addObjTemplate(name, icon, cell);
	}

	/**
	 * 
	 * @param name
	 * @param icon
	 * @param cell
	 */
	public void addObjTemplate(final String name, ImageIcon icon, mxCell cell)
	{
		mxRectangle bounds = (mxGeometry) cell.getGeometry().clone();
		final mxGraphTransferable t = new mxGraphTransferable(
				new Object[] { cell }, bounds);

		// Scales the image if it's too large for the library
		if (icon != null)
		{
			icon = new ImageIcon(icon.getImage().getScaledInstance(80, 20,
					0));
		}

		final JLabel entry = new JLabel(icon);
		entry.setPreferredSize(new Dimension(80, 20));
		entry.setBackground(new Color(219, 247, 215));
		entry.setFont(new Font(entry.getFont().getFamily(), 0, 15));

		entry.setVerticalTextPosition(JLabel.CENTER);
		entry.setHorizontalTextPosition(JLabel.CENTER);
		entry.setIconTextGap(0);

		entry.setToolTipText(name);
		entry.setText(name);

		entry.addMouseListener(new MouseListener()
		{

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
			 */
			public void mousePressed(MouseEvent e)
			{
				setSelectionEntry(entry, t);
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
			 */
			public void mouseEntered(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
			 */
			public void mouseExited(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
			 */
			public void mouseReleased(MouseEvent e)
			{
			}

		});

		// Install the handler for dragging nodes into a graph
		DragGestureListener dragGestureListener = new DragGestureListener()
		{
			/**
			 * 
			 */
			public void dragGestureRecognized(DragGestureEvent e)
			{
				e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(),
								t, null);
			}

		};

		DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(entry,
				DnDConstants.ACTION_COPY, dragGestureListener);

		add(entry);
	}


	public void addActTemplate(final String name, ImageIcon icon, String style,
							   int width, int height, Object value)
	{
		mxCell cell = new mxCell(value, new mxGeometry(0, 0, width, height),
				style, "activity", "Activity \nis"+value);
		cell.setVertex(true);

		addActTemplate(name, icon, cell);
	}

//	public void addActTemplate(final String name, ImageIcon icon, String style,
//							   int width, int height, Object value)
//	{
//		mxCell cell = new mxCell(value, new mxGeometry(0, 0, width, height),
//				style);
//		cell.setVertex(true);
//
//		addObjTemplate(name, icon, cell);
//	}

	/**
	 *
	 * @param name
	 * @param icon
	 * @param cell
	 */
	public void addActTemplate(final String name, ImageIcon icon, mxCell cell)
	{
		mxRectangle bounds = (mxGeometry) cell.getGeometry().clone();
		final mxGraphTransferable t = new mxGraphTransferable(
				new Object[] { cell }, bounds);

		// Scales the image if it's too large for the library
		if (icon != null)
		{
			icon = new ImageIcon(icon.getImage().getScaledInstance(80, 30,
					0));
		}

		final JLabel entry = new JLabel(icon);
		entry.setPreferredSize(new Dimension(80, 30));
		entry.setBackground(objEditorPalette.this.getBackground().brighter());
		entry.setFont(new Font(entry.getFont().getFamily(), 0, 15));

		entry.setVerticalTextPosition(JLabel.CENTER);
		entry.setHorizontalTextPosition(JLabel.CENTER);
		entry.setIconTextGap(0);

		entry.setToolTipText(name);
		entry.setText(name);

		entry.addMouseListener(new MouseListener()
		{

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
			 */
			public void mousePressed(MouseEvent e)
			{
				setSelectionEntry(entry, t);
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
			 */
			public void mouseEntered(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
			 */
			public void mouseExited(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
			 */
			public void mouseReleased(MouseEvent e)
			{
			}

		});

		// Install the handler for dragging nodes into a graph
		DragGestureListener dragGestureListener = new DragGestureListener()
		{
			/**
			 *
			 */
			public void dragGestureRecognized(DragGestureEvent e)
			{
				e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(),
						t, null);
			}

		};

		DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(entry,
				DnDConstants.ACTION_COPY, dragGestureListener);

		add(entry);
	}

	/**
	 * @param eventName
	 * @param listener
	 * @see mxEventSource#addListener(String, mxEventSource.mxIEventListener)
	 */
	public void addListener(String eventName, mxEventSource.mxIEventListener listener)
	{
		eventSource.addListener(eventName, listener);
	}

	/**
	 * @return whether or not event are enabled for this palette
	 * @see mxEventSource#isEventsEnabled()
	 */
	public boolean isEventsEnabled()
	{
		return eventSource.isEventsEnabled();
	}

	/**
	 * @param listener
	 * @see mxEventSource#removeListener(mxEventSource.mxIEventListener)
	 */
	public void removeListener(mxEventSource.mxIEventListener listener)
	{
		eventSource.removeListener(listener);
	}

	/**
	 * @param eventName
	 * @param listener
	 * @see mxEventSource#removeListener(String, mxEventSource.mxIEventListener)
	 */
	public void removeListener(mxEventSource.mxIEventListener listener, String eventName)
	{
		eventSource.removeListener(listener, eventName);
	}

	/**
	 * @param eventsEnabled
	 * @see mxEventSource#setEventsEnabled(boolean)
	 */
	public void setEventsEnabled(boolean eventsEnabled)
	{
		eventSource.setEventsEnabled(eventsEnabled);
	}

}

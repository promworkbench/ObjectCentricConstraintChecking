/**
 * Copyright (c) 2008, Gaudenz Alder
 */
package org.processmining.cachealignment.algorithms.swing.handler;

import org.processmining.cachealignment.algorithms.swing.mxGraphComponent;
import org.processmining.cachealignment.algorithms.swing.util.mxGraphActionsForOCCLEditor;

import javax.swing.*;

/**
 * @author Administrator
 * 
 */
public class mxKeyboardHandlerForOCCLEditor
{

	/**
	 * 
	 * @param graphComponent
	 */
	public mxKeyboardHandlerForOCCLEditor(mxGraphComponent graphComponent)
	{
		installKeyboardActions(graphComponent);
	}

	/**
	 * Invoked as part from the boilerplate install block.
	 */
	protected void installKeyboardActions(mxGraphComponent graphComponent)
	{
		InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		SwingUtilities.replaceUIInputMap(graphComponent,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);

		inputMap = getInputMap(JComponent.WHEN_FOCUSED);
		SwingUtilities.replaceUIInputMap(graphComponent,
				JComponent.WHEN_FOCUSED, inputMap);
		SwingUtilities.replaceUIActionMap(graphComponent, createActionMap());
	}

	/**
	 * Return JTree's input map.
	 */
	protected InputMap getInputMap(int condition)
	{
		InputMap map = null;

		if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		{
			map = (InputMap) UIManager.get("ScrollPane.ancestorInputMap");
		}
		else if (condition == JComponent.WHEN_FOCUSED)
		{
			map = new InputMap();

			map.put(KeyStroke.getKeyStroke("F2"), "edit");
			map.put(KeyStroke.getKeyStroke("DELETE"), "delete");
			map.put(KeyStroke.getKeyStroke("UP"), "selectParent");
			map.put(KeyStroke.getKeyStroke("DOWN"), "selectChild");
			map.put(KeyStroke.getKeyStroke("RIGHT"), "selectNext");
			map.put(KeyStroke.getKeyStroke("LEFT"), "selectPrevious");
			map.put(KeyStroke.getKeyStroke("PAGE_DOWN"), "enterGroup");
			map.put(KeyStroke.getKeyStroke("PAGE_UP"), "exitGroup");
			map.put(KeyStroke.getKeyStroke("HOME"), "home");
			map.put(KeyStroke.getKeyStroke("ENTER"), "expand");
			map.put(KeyStroke.getKeyStroke("BACK_SPACE"), "collapse");
			map.put(KeyStroke.getKeyStroke("control A"), "selectAll");
			map.put(KeyStroke.getKeyStroke("control D"), "selectNone");
			map.put(KeyStroke.getKeyStroke("control X"), "cut");
			map.put(KeyStroke.getKeyStroke("CUT"), "cut");
			map.put(KeyStroke.getKeyStroke("control C"), "copy");
			map.put(KeyStroke.getKeyStroke("COPY"), "copy");
			map.put(KeyStroke.getKeyStroke("control V"), "paste");
			map.put(KeyStroke.getKeyStroke("PASTE"), "paste");
			map.put(KeyStroke.getKeyStroke("control G"), "group");
			map.put(KeyStroke.getKeyStroke("control U"), "ungroup");
			map.put(KeyStroke.getKeyStroke("control ADD"), "zoomIn");
			map.put(KeyStroke.getKeyStroke("control SUBTRACT"), "zoomOut");
		}

		return map;
	}

	/**
	 * Return the mapping between JTree's input map and JGraph's actions.
	 */
	protected ActionMap createActionMap()
	{
		ActionMap map = (ActionMap) UIManager.get("ScrollPane.actionMap");

		map.put("edit", mxGraphActionsForOCCLEditor.getEditAction());
		map.put("delete", mxGraphActionsForOCCLEditor.getDeleteAction());
		map.put("home", mxGraphActionsForOCCLEditor.getHomeAction());
		map.put("enterGroup", mxGraphActionsForOCCLEditor.getEnterGroupAction());
		map.put("exitGroup", mxGraphActionsForOCCLEditor.getExitGroupAction());
		map.put("collapse", mxGraphActionsForOCCLEditor.getCollapseAction());
		map.put("expand", mxGraphActionsForOCCLEditor.getExpandAction());
		map.put("toBack", mxGraphActionsForOCCLEditor.getToBackAction());
		map.put("toFront", mxGraphActionsForOCCLEditor.getToFrontAction());
		map.put("selectNone", mxGraphActionsForOCCLEditor.getSelectNoneAction());
		map.put("selectAll", mxGraphActionsForOCCLEditor.getSelectAllAction());
		map.put("selectNext", mxGraphActionsForOCCLEditor.getSelectNextAction());
		map.put("selectPrevious", mxGraphActionsForOCCLEditor.getSelectPreviousAction());
		map.put("selectParent", mxGraphActionsForOCCLEditor.getSelectParentAction());
		map.put("selectChild", mxGraphActionsForOCCLEditor.getSelectChildAction());
		map.put("cut", TransferHandler.getCutAction());
		map.put("copy", TransferHandler.getCopyAction());
		map.put("paste", TransferHandler.getPasteAction());
		map.put("group", mxGraphActionsForOCCLEditor.getGroupAction());
		map.put("ungroup", mxGraphActionsForOCCLEditor.getUngroupAction());
		map.put("zoomIn", mxGraphActionsForOCCLEditor.getZoomInAction());
		map.put("zoomOut", mxGraphActionsForOCCLEditor.getZoomOutAction());

		return map;
	}

}

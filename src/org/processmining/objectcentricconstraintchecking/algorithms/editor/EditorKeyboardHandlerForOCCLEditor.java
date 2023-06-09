/**
 * Copyright (c) 2008, Gaudenz Alder
 */
package org.processmining.objectcentricconstraintchecking.algorithms.editor;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.processmining.objectcentricconstraintchecking.algorithms.swing.mxGraphComponent;
import org.processmining.objectcentricconstraintchecking.algorithms.swing.handler.mxKeyboardHandlerForOCCLEditor;
import org.processmining.objectcentricconstraintchecking.algorithms.swing.util.mxGraphActions;

/**
 * @author Administrator
 * 
 */
public class EditorKeyboardHandlerForOCCLEditor extends mxKeyboardHandlerForOCCLEditor
{

	/**
	 *
	 * @param graphComponent
	 */
	public EditorKeyboardHandlerForOCCLEditor(mxGraphComponent graphComponent)
	{
		super(graphComponent);
	}

	/**
	 * Return JTree's input map.
	 */
	protected InputMap getInputMap(int condition)
	{
		InputMap map = super.getInputMap(condition);

		if (condition == JComponent.WHEN_FOCUSED && map != null)
		{
			map.put(KeyStroke.getKeyStroke("control S"), "save");
			map.put(KeyStroke.getKeyStroke("control shift S"), "saveAs");
			map.put(KeyStroke.getKeyStroke("control N"), "new");
			map.put(KeyStroke.getKeyStroke("control O"), "open");

			map.put(KeyStroke.getKeyStroke("control Z"), "undo");
			map.put(KeyStroke.getKeyStroke("control Y"), "redo");
			map
					.put(KeyStroke.getKeyStroke("control shift V"),
							"selectVertices");
			map.put(KeyStroke.getKeyStroke("control shift E"), "selectEdges");
		}

		return map;
	}

	/**
	 * Return the mapping between JTree's input map and JGraph's actions.
	 */
	protected ActionMap createActionMap()
	{
		ActionMap map = super.createActionMap();

		map.put("save", new EditorActions.SaveAction(false));
		map.put("saveAs", new EditorActions.SaveAction(true));
		map.put("new", new EditorActions.NewAction());
		map.put("open", new EditorActions.OpenAction());
		map.put("undo", new EditorActions.HistoryAction(true));
		map.put("redo", new EditorActions.HistoryAction(false));
		map.put("selectVertices", mxGraphActions.getSelectVerticesAction());
		map.put("selectEdges", mxGraphActions.getSelectEdgesAction());

		return map;
	}

}

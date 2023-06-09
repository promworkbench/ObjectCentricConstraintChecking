/**
 * Copyright (c) 2010, Gaudenz Alder, David Benson
 */
package org.processmining.objectcentricconstraintchecking.algorithms.shape;

import java.util.Map;

import org.processmining.objectcentricconstraintchecking.algorithms.canvas.mxGraphics2DCanvas;
import org.processmining.objectcentricconstraintchecking.algorithms.view.mxCellState;

public interface mxITextShape
{
	/**
	 * 
	 */
	void paintShape(mxGraphics2DCanvas canvas, String text, mxCellState state,
			Map<String, Object> style);

}

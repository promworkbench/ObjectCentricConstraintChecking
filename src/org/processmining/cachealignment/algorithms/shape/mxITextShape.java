/**
 * Copyright (c) 2010, Gaudenz Alder, David Benson
 */
package org.processmining.cachealignment.algorithms.shape;

import org.processmining.cachealignment.algorithms.canvas.mxGraphics2DCanvas;
import org.processmining.cachealignment.algorithms.view.mxCellState;

import java.util.Map;

public interface mxITextShape
{
	/**
	 * 
	 */
	void paintShape(mxGraphics2DCanvas canvas, String text, mxCellState state,
			Map<String, Object> style);

}

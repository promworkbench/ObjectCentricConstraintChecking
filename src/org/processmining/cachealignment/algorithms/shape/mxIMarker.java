package org.processmining.cachealignment.algorithms.shape;

import org.processmining.cachealignment.algorithms.canvas.mxGraphics2DCanvas;
import org.processmining.cachealignment.algorithms.util.mxPoint;
import org.processmining.cachealignment.algorithms.view.mxCellState;

public interface mxIMarker
{
	/**
	 * 
	 */
	mxPoint paintMarker(mxGraphics2DCanvas canvas, mxCellState state, String type,
			mxPoint pe, double nx, double ny, double size, boolean source);

}

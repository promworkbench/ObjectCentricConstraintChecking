package org.processmining.objectcentricconstraintchecking.algorithms.shape;

import org.processmining.objectcentricconstraintchecking.algorithms.canvas.mxGraphics2DCanvas;
import org.processmining.objectcentricconstraintchecking.algorithms.util.mxPoint;
import org.processmining.objectcentricconstraintchecking.algorithms.view.mxCellState;

public interface mxIMarker
{
	/**
	 * 
	 */
	mxPoint paintMarker(mxGraphics2DCanvas canvas, mxCellState state, String type,
			mxPoint pe, double nx, double ny, double size, boolean source);

}

package org.processmining.objectcentricconstraintchecking.algorithms.shape;

import java.awt.Rectangle;

import org.processmining.objectcentricconstraintchecking.algorithms.canvas.mxGraphics2DCanvas;
import org.processmining.objectcentricconstraintchecking.algorithms.util.mxConstants;
import org.processmining.objectcentricconstraintchecking.algorithms.util.mxUtils;
import org.processmining.objectcentricconstraintchecking.algorithms.view.mxCellState;

public class mxDoubleRectangleShape extends mxRectangleShape
{

	/**
	 * 
	 */
	public void paintShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		super.paintShape(canvas, state);

		int inset = (int) Math.round((mxUtils.getFloat(state.getStyle(),
				mxConstants.STYLE_STROKEWIDTH, 1) + 3)
				* canvas.getScale());

		Rectangle rect = state.getRectangle();
		int x = rect.x + inset;
		int y = rect.y + inset;
		int w = rect.width - 2 * inset;
		int h = rect.height - 2 * inset;
		
		canvas.getGraphics().drawRect(x, y, w, h);
	}

}

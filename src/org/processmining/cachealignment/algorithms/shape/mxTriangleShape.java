package org.processmining.cachealignment.algorithms.shape;

import org.processmining.cachealignment.algorithms.canvas.mxGraphics2DCanvas;
import org.processmining.cachealignment.algorithms.util.mxConstants;
import org.processmining.cachealignment.algorithms.util.mxUtils;
import org.processmining.cachealignment.algorithms.view.mxCellState;

import java.awt.*;

public class mxTriangleShape extends mxBasicShape
{

	/**
	 * 
	 */
	public Shape createShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		Rectangle temp = state.getRectangle();
		int x = temp.x;
		int y = temp.y;
		int w = temp.width;
		int h = temp.height;
		String direction = mxUtils.getString(state.getStyle(),
				mxConstants.STYLE_DIRECTION, mxConstants.DIRECTION_EAST);
		Polygon triangle = new Polygon();

		if (direction.equals(mxConstants.DIRECTION_NORTH))
		{
			triangle.addPoint(x, y + h);
			triangle.addPoint(x + w / 2, y);
			triangle.addPoint(x + w, y + h);
		}
		else if (direction.equals(mxConstants.DIRECTION_SOUTH))
		{
			triangle.addPoint(x, y);
			triangle.addPoint(x + w / 2, y + h);
			triangle.addPoint(x + w, y);
		}
		else if (direction.equals(mxConstants.DIRECTION_WEST))
		{
			triangle.addPoint(x + w, y);
			triangle.addPoint(x, y + h / 2);
			triangle.addPoint(x + w, y + h);
		}
		else
		// EAST
		{
			triangle.addPoint(x, y);
			triangle.addPoint(x + w, y + h / 2);
			triangle.addPoint(x, y + h);
		}

		return triangle;
	}

}

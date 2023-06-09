package org.processmining.objectcentricconstraintchecking.algorithms.shape;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.processmining.objectcentricconstraintchecking.algorithms.canvas.mxGraphics2DCanvas;
import org.processmining.objectcentricconstraintchecking.algorithms.view.mxCellState;

public class mxEllipseShape extends mxBasicShape
{

	/**
	 * 
	 */
	public Shape createShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		Rectangle temp = state.getRectangle();

		return new Ellipse2D.Float(temp.x, temp.y, temp.width, temp.height);
	}

}

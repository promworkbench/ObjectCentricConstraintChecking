/**
 * Copyright (c) 2007-2010, Gaudenz Alder, David Benson
 */
package org.processmining.objectcentricconstraintchecking.algorithms.shape;

import java.awt.Color;
import java.awt.Rectangle;

import org.processmining.objectcentricconstraintchecking.algorithms.canvas.mxGraphics2DCanvas;
import org.processmining.objectcentricconstraintchecking.algorithms.util.mxConstants;
import org.processmining.objectcentricconstraintchecking.algorithms.util.mxUtils;
import org.processmining.objectcentricconstraintchecking.algorithms.view.mxCellState;

/**
 * A rectangular shape that contains a single image. See mxImageBundle for
 * creating a lookup table with images which can then be referenced by key.
 */
public class mxImageShape extends mxRectangleShape
{

	/**
	 * 
	 */
	public void paintShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		super.paintShape(canvas, state);

		boolean flipH = mxUtils.isTrue(state.getStyle(),
				mxConstants.STYLE_IMAGE_FLIPH, false);
		boolean flipV = mxUtils.isTrue(state.getStyle(),
				mxConstants.STYLE_IMAGE_FLIPV, false);

		canvas.drawImage(getImageBounds(canvas, state),
				getImageForStyle(canvas, state),
				mxGraphics2DCanvas.PRESERVE_IMAGE_ASPECT, flipH, flipV);
	}

	/**
	 * 
	 */
	public Rectangle getImageBounds(mxGraphics2DCanvas canvas, mxCellState state)
	{
		return state.getRectangle();
	}

	/**
	 * 
	 */
	public boolean hasGradient(mxGraphics2DCanvas canvas, mxCellState state)
	{
		return false;
	}

	/**
	 * 
	 */
	public String getImageForStyle(mxGraphics2DCanvas canvas, mxCellState state)
	{
		return canvas.getImageForStyle(state.getStyle());
	}

	/**
	 * 
	 */
	public Color getFillColor(mxGraphics2DCanvas canvas, mxCellState state)
	{
		return mxUtils.getColor(state.getStyle(),
				mxConstants.STYLE_IMAGE_BACKGROUND);
	}

	/**
	 * 
	 */
	public Color getStrokeColor(mxGraphics2DCanvas canvas, mxCellState state)
	{
		return mxUtils.getColor(state.getStyle(),
				mxConstants.STYLE_IMAGE_BORDER);
	}

}

package org.processmining.cachealignment.algorithms.listener;

import org.processmining.cachealignment.algorithms.swing.mxGraphComponent;
import org.processmining.cachealignment.algorithms.swing.mxGraphOutline;
import org.processmining.cachealignment.algorithms.editor.BasicGraphEditor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class GraphComponentMouseMotionListener implements MouseMotionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private BasicGraphEditor modelEditor;
    private mxGraphComponent graphComponent;
    private mxGraphOutline graphOutline;
    
	public GraphComponentMouseMotionListener(BasicGraphEditor modelEditor) {
		this.modelEditor = modelEditor;
		this.graphComponent = modelEditor.getGraphComponent();
		this.graphOutline = modelEditor.getGraphOutline();
	}
	

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("graphComponent draged");
		mouseLocationChanged(e);
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
//		System.out.println("graphComponent mouse moved");
	}
	
	/**
	 * 
	 */
	protected void mouseLocationChanged(MouseEvent e)
	{
		modelEditor.status(e.getX() + ", " + e.getY());
	}

}
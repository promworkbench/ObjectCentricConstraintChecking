package org.processmining.objectcentricconstraintchecking.algorithms.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import org.processmining.objectcentricconstraintchecking.algorithms.editor.BasicGraphEditor;
import org.processmining.objectcentricconstraintchecking.algorithms.swing.mxGraphComponent;
import org.processmining.objectcentricconstraintchecking.algorithms.swing.mxGraphOutline;

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
package org.processmining.cachealignment.algorithms.listener;

import org.processmining.cachealignment.algorithms.swing.mxGraphComponent;
import org.processmining.cachealignment.algorithms.swing.mxGraphOutline;
import org.processmining.cachealignment.algorithms.util.mxResources;
import org.processmining.cachealignment.algorithms.editor.BasicGraphEditor;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class GraphComponentMouseWheelListener implements MouseWheelListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private BasicGraphEditor modelEditor; 
    private mxGraphComponent graphComponent;
    private mxGraphOutline graphOutline;
    
	public GraphComponentMouseWheelListener(BasicGraphEditor modelEditor) {
		this.modelEditor = modelEditor;
		this.graphComponent = modelEditor.getGraphComponent();
		this.graphOutline = modelEditor.getGraphOutline();
	}
	
	public void mouseWheelMoved(MouseWheelEvent e){
		if (e.getSource() instanceof mxGraphOutline
				|| e.isControlDown()){
			if (e.getWheelRotation() < 0){
				graphComponent.zoomIn();
			}
			else{
				graphComponent.zoomOut();
			}
			double scale = graphComponent.getGraph().getView().getScale();
			modelEditor.status(mxResources.get("scale") + ": "
					+ (int) (100 * scale)
					+ "%");
			
			//update the value in the zoom combobox
			System.out.print("mouse wheel moved");
			modelEditor.getToolBar().getZoomBox().setSelectedItem((int) (100 * scale) + "%");
		}
	}
	

}
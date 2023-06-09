package org.processmining.objectcentricconstraintchecking.algorithms.listener;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.processmining.objectcentricconstraintchecking.algorithms.editor.BasicGraphEditor;
import org.processmining.objectcentricconstraintchecking.algorithms.swing.mxGraphComponent;
import org.processmining.objectcentricconstraintchecking.algorithms.swing.mxGraphOutline;
import org.processmining.objectcentricconstraintchecking.algorithms.util.mxResources;

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
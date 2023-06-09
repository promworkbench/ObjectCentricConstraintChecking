//package org.processmining.listener;//package org.processmining.objectcentricconstraintchecking.algorithms.listener;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//
//import org.processmining.editor.BasicGraphEditor;
//import org.processmining.model.mxCell;
//import org.processmining.swing.mxGraphComponent;
//import org.processmining.util.mxConstants;
//
//
//public class GraphComponentMouseListener extends MouseAdapter{
//
//	/**
//	 *
//	 */
//	private static final long serialVersionUID = 1L;
//
//	private mxGraphComponent graphComponent;
////	private BasicGraphEditor graphEditor;
//    private double souceLabelPosition = -0.6;
//    private double targetLabelPosition = 0.4;
//    private double connectorSize = 5;
//    private double connectorX = 0.2;
//
//    private BasicGraphEditor modelEditor;
//
//
//	public void setModelEditor(BasicGraphEditor modelEditor){
//		this.modelEditor = modelEditor;
//
//	}
//
//
//	/**
//	 *
//	 */
//	public void mousePressed(MouseEvent e)
//	{
//		// Handles context menu on the Mac where the trigger is on mousepressed
//		.println("mousePressed in GraphComponentMouseListener of org.processmining.listener");
//
//		int dragStartX = e.getX();
//		int dragStartY = e.getY();
//
//		System.out.println("graphComponent dragStartX: " + dragStartX);
//		System.out.println("graphComponent dragStartY: " + dragStartY);
//	}
//
//	/**
//	 *
//	 */
//	public void mouseReleased(MouseEvent e)
//	{
//		System.out.println("mouseReleased in GraphComponentMouseListener of org.processmining.listener");
//		if(model.getModelEnvironment().equals(mxConstants.model_editor)){
//			modelEditor.updateEditor();
//
//			System.out.println("released in BasicGraphEditor");
//			if (e.isPopupTrigger())
//			{
//				modelEditor.showGraphPopupMenu(e);
//			}
//		}
//
//	}
//
//	/**
//	 * monitor the clicking event
//	 */
//    public void mouseClicked(MouseEvent e) {
//    	System.out.println("mouseClicked in GraphComponentMouseListener of org.processmining.listener");
//    	String modelEnvironment = model.getModelEnvironment();
//    	System.out.println("modelEnvironment: " + modelEnvironment);
//        // TODO Auto-generated method stub
//    	mxCell selectedCell = (mxCell) graphComponent.getGraph().getSelectionCell();
//    	if(modelEnvironment.equals(mxConstants.model_editor) || modelEnvironment.equals(mxConstants.model_miner_noise)){
//    		System.out.println("in the model_editor mode");
//    		modelEditor.restoreAllCells();
//    		modelEditor.disableAllBoxes();
//    		modelEditor.getAttributePanel().disableAttributePanel();
//	    	if(selectedCell != null){
//	    		//highlight the selected cell
//	    		modelEditor.highlightCell(selectedCell);
//	    		//update the style box
//				String edgeStyle = Style.getStyleElementValueFromCell(graphComponent.getGraph(), selectedCell, mxConstants.STYLE_EDGE);
//				if(edgeStyle != null && selectedCell.isEdge()){
//					modelEditor.getToolBar().getEdgeStyleBox().setEnabled(true);
//					modelEditor.getToolBar().getEdgeStyleBox().setSelectedItem(edgeStyle);
//				}
//				//update the constraint boxes
//				modelEditor.updateConstraintBoxes(selectedCell);
//				//update the attribute panel
//				modelEditor.getAttributePanel().updateAttributePanel(selectedCell);
//	    		//update the constraint instance and distribution panel for the selected cell
//				modelEditor.updateConstraintPanel(selectedCell);
//	    	}
//	    	graphComponent.getGraph().refresh();
////	    	//if the model is designed (e.g., in the model editor environment), the graph is refreshed;
////	    	//in the model is discovered, it is not refreshed
////	    	if(modelEnvironment.equals(mxConstants.model_editor)){
////
////	    	}
//	    	modelEditor.refreshGraph();
//
//    	}
//    	else if(modelEnvironment.equals(mxConstants.conformance_checking_plugin)){
//    		System.out.println("in the conformance_checking_plugin mode");
//		}
//    }
//}
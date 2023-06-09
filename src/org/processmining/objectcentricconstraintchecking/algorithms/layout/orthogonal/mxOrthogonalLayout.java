/**
 * Copyright (c) 2008-2009, JGraph Ltd
 */
package org.processmining.objectcentricconstraintchecking.algorithms.layout.orthogonal;

import org.processmining.objectcentricconstraintchecking.algorithms.layout.mxGraphLayout;
import org.processmining.objectcentricconstraintchecking.algorithms.layout.orthogonal.model.mxOrthogonalModel;
import org.processmining.objectcentricconstraintchecking.algorithms.view.mxGraph;

/**
 *
 */
/**
*
*/
public class mxOrthogonalLayout extends mxGraphLayout
{

  /**
   * 
   */
  protected mxOrthogonalModel orthModel;

  /**
   * Whether or not to route the edges along grid lines only, if the grid
   * is enabled. Default is false
   */
  protected boolean routeToGrid = false;
  
  /**
   * 
   */
  public mxOrthogonalLayout(mxGraph graph)
  {
     super(graph);
     orthModel = new mxOrthogonalModel(graph);
  }

  /**
   * 
   */
  public void execute(Object parent)
  {
     // Create the rectangulation
     
  }

}

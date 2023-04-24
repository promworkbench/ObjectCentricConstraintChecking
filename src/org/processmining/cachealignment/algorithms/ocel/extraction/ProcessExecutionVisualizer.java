//package org.processmining.ocel.extraction;
//
//import org.processmining.util.svg.ParseException;
//import org.processmining.contexts.uitopia.UIPluginContext;
//import org.processmining.contexts.uitopia.annotations.Visualizer;
//import org.processmining.framework.plugin.annotations.Plugin;
//import org.processmining.framework.plugin.annotations.PluginVariant;
//
//import javax.swing.*;
//
//
//@Plugin(name = "Object-centric Process Execution Visualization",
//        returnLabels = { "PE Visualization" },
//        returnTypes = { JComponent.class },
//        parameterLabels = { "Object-centric Process Execution Visualization" },
//        userAccessible = true)
//@Visualizer
//public class ProcessExecutionVisualizer{
//    protected JPanel root;
//
//    @PluginVariant(requiredParameterLabels = {0})
//    public JComponent visProcessExecution(UIPluginContext context, PEGraph peGraph) throws ParseException {
//
//        JFrame frame = new JFrame();
//        frame.getContentPane().add(peGraph);
//        return (JComponent) frame.getContentPane();
//    }
//}
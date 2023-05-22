package org.processmining.cachealignment.algorithms.ocel.extraction;

import org.processmining.cachealignment.algorithms.util.svg.ParseException;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import javax.swing.*;


@Plugin(name = "Object-centric Process Execution",
        returnLabels = { "Process Execution Visualizer" },
        returnTypes = { JComponent.class },
        parameterLabels = { "OCCM Editor for Object-Centric Event Log" },
        userAccessible = true)
@org.processmining.contexts.uitopia.annotations.Visualizer
public class Visualizer {
    protected JPanel root;

    @PluginVariant(requiredParameterLabels = {0})
    public JComponent visPE(UIPluginContext context, ProcessExecutionPanel ge) throws ParseException {

        JFrame frame = new JFrame();
        frame.getContentPane().add(ge);
        return (JComponent) frame.getContentPane();
    }
}

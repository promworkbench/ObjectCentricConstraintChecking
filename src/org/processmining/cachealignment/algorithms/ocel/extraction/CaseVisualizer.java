package org.processmining.cachealignment.algorithms.ocel.extraction;

import org.processmining.cachealignment.algorithms.util.svg.ParseException;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import javax.swing.*;


@Plugin(name = "Object-centric Case Visualization",
        returnLabels = { "Case Visualization" },
        returnTypes = { JComponent.class },
        parameterLabels = { "Object-centric Process Execution Visualization" },
        userAccessible = true)
@Visualizer
public class CaseVisualizer {
    protected JPanel root;

    @PluginVariant(requiredParameterLabels = {0})
    public JComponent visCase(UIPluginContext context, CaseGraph caseGraph) throws ParseException {
        JFrame frame = new JFrame();
        frame.getContentPane().add(caseGraph);
        return (JComponent) frame.getContentPane();
    }
}
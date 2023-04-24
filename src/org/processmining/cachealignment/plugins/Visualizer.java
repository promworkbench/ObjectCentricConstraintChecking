package org.processmining.cachealignment.plugins;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.processmining.cachealignment.algorithms.ocel.constraint.OCCMEditor;
import org.processmining.cachealignment.algorithms.util.svg.ParseException;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;


@Plugin(name = "Object-centric Constraint Language Editor",
        returnLabels = { "Object-centric Constraint Model Editor" },
        returnTypes = { JComponent.class },
        parameterLabels = { "OCCM Editor for Object-Centric Event Log" },
        userAccessible = true)
@org.processmining.contexts.uitopia.annotations.Visualizer
public class Visualizer {
    protected JPanel root;

    @PluginVariant(requiredParameterLabels = {0})
    public JComponent visOCCLEditor(UIPluginContext context, OCCMEditor ge) throws ParseException {

        JFrame frame = new JFrame();
        frame.getContentPane().add(ge);
        return (JComponent) frame.getContentPane();
    }
}

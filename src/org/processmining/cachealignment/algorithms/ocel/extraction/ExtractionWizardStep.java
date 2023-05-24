package org.processmining.cachealignment.algorithms.ocel.extraction;


import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEventLog;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class ExtractionWizardStep extends ProMPropertiesPanel implements ProMWizardStep<ExtractionWizardParameters> {
    OcelEventLog ocelLog;

    PEModel cm = PEModel.getInstance();

    private static final String TITLE = "Extract Case";

    private final ProMComboBox<String> objectTypeList;

    private HashSet<String> revObjSet = new HashSet<>();

    public ExtractionWizardStep(OcelEventLog ocelLog) {
        super(TITLE);
        this.ocelLog = ocelLog;
        List<String> objectTypes = new ArrayList<String>(this.ocelLog.objectTypes.keySet());

        cm.allObjectTyps = objectTypes;
        
        objectTypeList = addComboBox("Primary object type", objectTypes);

        JPanel jp2 = new JPanel();
        jp2.setLayout(new GridLayout(objectTypes.size(),1,0,0));
        for (String objType:objectTypes){
            JCheckBox objBox = new JCheckBox(objType);
            if(objType.equals((String)objectTypeList.getSelectedItem())){
            	objBox.setEnabled(false);
            }
                        
            objBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    revObjSet.add(objBox.getText());
                    }
                else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    revObjSet.remove(objBox.getText());

                }
                });
            jp2.add(objBox);
        }
        objectTypeList.addItemListener(e -> {
        	Component[] components = jp2.getComponents();
        	for (int i = 0; i < components.length; i++) {
        	    if (components[i] instanceof JCheckBox) {
        	        if(((JCheckBox) components[i]).getText().equals((String)objectTypeList.getSelectedItem())){
        	        	((JCheckBox) components[i]).setEnabled(false);
       				}
        	        else{
        	        	((JCheckBox) components[i]).setEnabled(true);
        	        }
        	    }
        	}
        });
        addProperty("Secondary object type(s)",jp2);
    }

    public ExtractionWizardParameters apply(ExtractionWizardParameters model, JComponent component) {
        // TODO Auto-generated method stub
        if (canApply(model, component)) {
            ExtractionWizardStep step = (ExtractionWizardStep) component;
            model.setLeadObjectType((String)objectTypeList.getSelectedItem());
            revObjSet.remove((String)objectTypeList.getSelectedItem());
            model.setRevObjectType(revObjSet);
        }
        return model;
    }

    public boolean canApply(ExtractionWizardParameters model, JComponent component) {
        // TODO Auto-generated method stub
        return component instanceof ExtractionWizardStep;
    }

    public JComponent getComponent(ExtractionWizardParameters model) {
        // TODO Auto-generated method stub
        return this;
    }

    public String getTitle() {
        // TODO Auto-generated method stub
        return TITLE;
    }
}

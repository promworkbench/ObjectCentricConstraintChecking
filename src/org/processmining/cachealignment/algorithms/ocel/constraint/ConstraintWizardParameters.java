package org.processmining.cachealignment.algorithms.ocel.constraint;

import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

import java.util.HashSet;

public class ConstraintWizardParameters extends PluginParametersImpl {
    public String leadObjType;
    public HashSet<String> revObjTypes;

    public ConstraintWizardParameters() {

    }

    public ConstraintWizardParameters(String leadObjType, HashSet<String> revObjTypes) {
        this.leadObjType = leadObjType;
        this.revObjTypes = revObjTypes;
    }

    public String getLeadObjectType() {
        return this.leadObjType;
    }

    public void setLeadObjectType(String leadObjType) {
        this.leadObjType = leadObjType;
    }

    public String getRevObjectType() {
        return this.leadObjType;
    }

    public void setRevObjectType(HashSet<String> revObjTypes) {
        this.revObjTypes = revObjTypes;
    }

}

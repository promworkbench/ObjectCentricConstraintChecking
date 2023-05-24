package org.processmining.cachealignment.algorithms.ocel.constraint;

import java.util.ArrayList;


public class ViolatedSet {

    public ArrayList<ArrayList> violatedRules = new ArrayList<ArrayList>();

    public ViolatedSet(){

    }

    public void appendViolatedRule(String caseId,
                                   ArrayList<String> eventId,
                                   String activity,
                                   String diagnosis,
                                   int constraintId){
        ArrayList violatedRule = new ArrayList<>();
        violatedRule.add(caseId);
        violatedRule.add(eventId);
        violatedRule.add(activity);
        violatedRule.add(diagnosis);
        violatedRule.add(constraintId);
        violatedRules.add(violatedRule);
    }

    public void mergeViolatedRule(ViolatedSet vs) {
        if (violatedRules.size() == 0) {
            this.violatedRules = vs.violatedRules;
        }
        else {
            violatedRules.addAll(vs.violatedRules);
        }
    }

}
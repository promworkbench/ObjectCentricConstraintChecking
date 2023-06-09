package org.processmining.objectcentricconstraintchecking.algorithms.ocel.constraint;

import java.text.ParseException;
import java.util.ArrayList;

import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelEvent;
import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelEventLog;
import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelObject;

// class for object cardinality constraint model

public class ConstraintOCCM implements Runnable{
    ViolatedSet vs = new ViolatedSet();
    private volatile int current;
    private int amount;
    public int minCard;
    public int maxCard;
    public int constraintId;
    int objCount = 0;
    public String refObjType;
    public String targetActivity;

    public OcelEventLog ocel;

    public ConstraintOCCM(){

    }

    public ConstraintOCCM(OcelEventLog ocelEventLog,
                          String targetActivity,
                          String refObjType,
                          int minCard,
                          int maxCard,
                          int constraintId){
        this.ocel = ocelEventLog;
        this.targetActivity = targetActivity;
        this.refObjType = refObjType;
        this.minCard = minCard;
        this.maxCard = maxCard;
        this.constraintId = constraintId;
    }


    public int getAmount(){
        return amount;
    }

    public int getCurrent(){
        return (int)(current/amount*100);
    }

    public ViolatedSet getViolationSet() throws ParseException {
        return getObjCardConstraint(ocel,
                targetActivity,
                refObjType,
                minCard,
                maxCard,
                constraintId);
    }

    public ViolatedSet getObjCardConstraint(OcelEventLog ocel,
                                            String targetActivity,
                                            String refObjType,
                                            int minCard,
                                            int maxCard,
                                            int constraintId) throws ParseException {
        this.amount = ocel.events.size() - 1;
        this.current = 0;

        for (OcelEvent evt: ocel.events.values()) {
            // get evt that executes the target activity
            if (evt.activity.equals(targetActivity)) {
                for (OcelObject ocelObject : evt.relatedObjects) {
                    if (ocelObject.objectType.name.equals(refObjType)) {
                        objCount += 1;
                    }
                }
                if (objCount > maxCard || objCount < minCard){
                    ArrayList<String> evtLst = new ArrayList();
                    evtLst.add(evt.id);
                    vs.appendViolatedRule(
                            "",
                            evtLst,
                            evt.activity,
                            "The number of objects of type "+
                            refObjType+" related to "+targetActivity +" is: "+objCount,
                            constraintId);
                }
            }
            objCount = 0;

        }
        this.current = this.amount;
        return vs;
    }

    @Override
    public void run() {
        try {
            getObjCardConstraint(
                    ocel,
                    targetActivity,
                    refObjType,
                    minCard,
                    maxCard,
                    constraintId);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

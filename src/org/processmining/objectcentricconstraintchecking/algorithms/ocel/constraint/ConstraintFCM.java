package org.processmining.objectcentricconstraintchecking.algorithms.ocel.constraint;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelEvent;
import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelObject;

// class for frequency constraint

public class ConstraintFCM implements Runnable{
    ViolatedSet vs = new ViolatedSet();
    private volatile int current;
    private int amount;
    public int minFreq;
    public int maxFreq;
    public int constraintId;
   
    int actCount = 0;
    
    public String targetActivity;
    public Map<String, HashMap<OcelEvent, HashMap<OcelEvent, HashSet<OcelObject>>>> peMap;

    public ConstraintFCM(){

    }

    public ConstraintFCM(
            Map<String, HashMap<OcelEvent, HashMap<OcelEvent, HashSet<OcelObject>>>> peMap,
            String targetActivity,
            int minFreq,
            int maxFreq,
            int constraintId){
        this.peMap = peMap;
        this.targetActivity = targetActivity;
        this.minFreq = minFreq;
        this.maxFreq = maxFreq;
        this.constraintId = constraintId;
    }

    public int getAmount(){
        return amount;
    }

    public int getCurrent(){
        return (int)(current/amount*100);
    }

    public ViolatedSet getViolationSet() throws ParseException {
        return getObjCardConstraint(peMap,
                targetActivity,
                minFreq,
                maxFreq,
                constraintId);
    }

    public ViolatedSet getObjCardConstraint(
            Map<String, HashMap<OcelEvent, HashMap<OcelEvent, HashSet<OcelObject>>>> peMap,
            String targetActivity,
            int minFreq,
            int maxFreq,
            int constraintId) throws ParseException {
        this.amount = peMap.size() - 1;
        this.current = 0;

        // iterate each process execution, count the frequency of an event type
        for (String key : peMap.keySet()) {
            actCount = 0;

            ArrayList<String> evtIdList = new ArrayList<>();
            // get the list of event map
            Set<OcelEvent> evtMap = peMap.get(key).keySet();
            // iterate the event map
            for(OcelEvent evt:evtMap){
                if(evt.activity.equals(targetActivity)){
                    actCount += 1;
                    evtIdList.add(evt.id);
                }
            }

            if (actCount>maxFreq){
                vs.appendViolatedRule(
                        key,
                        evtIdList,
                        targetActivity,
                        "The number of "+targetActivity +" is "+actCount+" (exceeds the required frequency)",
                        constraintId);
            }
            else if (actCount<minFreq){
                vs.appendViolatedRule(
                        key,
                        evtIdList,
                        targetActivity,
                        "The number of "+targetActivity +" is "+actCount+" (below the required frequency)",
                        constraintId);
            }

        }

        this.current = this.amount;
        return vs;
    }

    @Override
    public void run() {
        try {
            getObjCardConstraint(
                    peMap,
                    targetActivity,
                    minFreq,
                    maxFreq,
                    constraintId);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

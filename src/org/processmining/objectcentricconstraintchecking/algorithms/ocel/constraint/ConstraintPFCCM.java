package org.processmining.objectcentricconstraintchecking.algorithms.ocel.constraint;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelEvent;
import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelObject;

/*
 * @param null:
  * @return null
 * @description This is the constraint model for process flow cardinality constraint
 * @date 2/3/2023 12:28 PM
 */

public class ConstraintPFCCM implements Runnable{

    ViolatedSet vs = new ViolatedSet();
    private volatile int current;
    private int amount;
    private Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peMap;
    public String preAct;
    public int preActCardMin;
    public int preActCardMax;
    public String sucAct;
    public int sucActCardMin;
    public int sucActCardMax;
    public List<String> objTypeLst;
    public int constraintId;

    public ConstraintPFCCM(){
    }

    public ConstraintPFCCM(
                           String preAct,
                           int preActCardMin,
                           int preActCardMax,
                           String sucAct,
                           int sucActCardMin,
                           int sucActCardMax,
                           Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peMap,
                           int constraintId){
        this.preAct = preAct;
        this.preActCardMin = preActCardMin;
        this.preActCardMax = preActCardMax;
        this.sucAct = sucAct;
        this.sucActCardMin = sucActCardMin;
        this.sucActCardMax = sucActCardMax;
        this.peMap = peMap;
        this.constraintId = constraintId;
    }


    public int getAmount(){
        return amount;
    }

    public int getCurrent(){
        return (int)(current/amount*100);
    }

    public ViolatedSet getViolationSet() throws ParseException {
        return getCardinalityConstraint(
                preAct,
                preActCardMin,
                preActCardMax,
                sucAct,
                sucActCardMin,
                sucActCardMax,
                peMap,
                constraintId);
    }

    public ViolatedSet getCardinalityConstraint(
                                                String preAct,
                                                int preActCardMin,
                                                int preActCardMax,
                                                String sucAct,
                                                int sucActCardMin,
                                                int sucActCardMax,
                                                Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peMap,
                                                int constraintId) throws ParseException {
        this.current = 0;
        // iterate the process execution
        for (String peId: peMap.keySet()) {
            // get the process execution
            HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>> evtMap = peMap.get(peId);
            // for each event that executes preAct
            Set<OcelEvent> evtSet = evtMap.keySet();
            for(OcelEvent evt: evtSet){
                if (evt.activity.equals(preAct)){
                    Set<OcelEvent> sucActSet = new HashSet<>();
                    // get the number of sucAct
                    sucActSet = getSucTarActSet(
                            sucActSet,
                            evt,
                            evtMap,
                            sucAct);
                    if (sucActSet.size()>sucActCardMax || sucActSet.size()<sucActCardMin){
                        // add to violation set
                        ArrayList<String> evtLst = new ArrayList<>();

                        evtLst.add(evt.id);
                        for(OcelEvent sucEvt: sucActSet){
                            evtLst.add(sucEvt.id);
                        }

                        vs.appendViolatedRule(
                                peId,
                                evtLst,
                                preAct+","+sucAct,
                                "The number of succeeding activity " + sucAct + " does not match the cardinality constraint",
                                constraintId);
                    }
                }
                else if (evt.activity.equals("sucAct")){
                    // get the number of preAct
                }
            }
            this.current += 1;
        }
        this.current = this.amount;
        return vs;
    }

    public Set<OcelEvent> getSucTarActSet(
            Set<OcelEvent> sucActSet,
            OcelEvent preEvt,
            HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>> evtMap,
            String sucAct){
        HashMap<OcelEvent,HashSet<OcelObject>> evtObjSet = evtMap.get(preEvt);
        // iterate each event in the evtObjSet
        if (evtObjSet != null) {
            for(OcelEvent sucEvt:evtObjSet.keySet()){
                if(sucEvt.activity.equals(sucAct)){
                    sucActSet.add(sucEvt);
                }
                sucActSet = getSucTarActSet(sucActSet,
                        sucEvt, evtMap, sucAct);
            }
        }
        return sucActSet;
    }

//    public Set<OcelEvent> getSucTarActSet(
//            Set<OcelEvent> sucActSet,
//            OcelEvent preEvt,
//            HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>> evtMap,
//            String sucAct,
//            List objTypeLst){
//
//        HashMap<OcelEvent,HashSet<OcelObject>> evtObjSet = evtMap.get(preEvt);
//        // iterate each event in the evtObjSet
//
//        if (evtObjSet != null) {
//            System.out.println("evtObj size" + evtObjSet.size());
//            for(OcelEvent sucEvt:evtObjSet.keySet()){
//
//                // check whether the path exist
//                Set<OcelObject> objBeforeSet = preEvt.relatedObjects;
//                Set<OcelObject> objCurrentSet = sucEvt.relatedObjects;
//                HashSet<OcelObject> resSet = new HashSet<>();
//                resSet.addAll(objBeforeSet);
//                resSet.retainAll(objCurrentSet);
//
//                for(OcelObject obj: resSet){
//                    // if the list contains the obj type
//                    if(objTypeLst.contains(obj.objectType.name)){
//
//                        // add the sucEvt to the check set if it executes the suc activity
//                        if(sucEvt.activity.equals(sucAct)){
//                            sucActSet.add(sucEvt);
//                        }
//                        sucActSet = getSucTarActSet(sucActSet, sucEvt, evtMap, sucAct, objTypeLst);
//                        // no need to continue
//                        break;
//                    }
//                }
//            }
//        }
//        return sucActSet;
//    }


    @Override
    public void run() {
        try {
            getCardinalityConstraint(
                    preAct,
                    preActCardMin,
                    preActCardMax,
                    sucAct,
                    sucActCardMin,
                    sucActCardMax,
                    peMap,
                    constraintId);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

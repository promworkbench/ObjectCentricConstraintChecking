package org.processmining.objectcentricconstraintchecking.algorithms.ocel.constraint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelEvent;
import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelEventComparator;
import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelObject;

/*
 * @param null:
  * @return null
 * @description This is the constraint model for process flow cardinality constraint
 * @date 2/3/2023 12:28 PM
 */

public class ConstraintPFTCM implements Runnable{

    ViolatedSet vs = new ViolatedSet();

    private volatile int current;
    private int amount;
    private Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peMap;

    public String preAct;
    public String sucAct;

    public long timeMin;

    public long timeMax;
    
    public int constraintId;

    public String firstPattern;

    public String secondPatten;
    
    public List objTypeLst;

    public ConstraintPFTCM(){

    }

    public ConstraintPFTCM(String preAct,
                           String sucAct,
                           long timeMin,
                           long timeMax,
                           String firstPattern,
                           String secondPatten,
                           Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peMap,
                           int constraintId){
        this.preAct = preAct;
        this.sucAct = sucAct;
        this.timeMin = timeMin;
        this.timeMax = timeMax;
        this.firstPattern = firstPattern;
        this.secondPatten = secondPatten;
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

        return getTemporalConstraint(
                preAct,
                sucAct,
                timeMin,
                timeMax,
                firstPattern,
                secondPatten,
                peMap,
                constraintId);
    }
    
    public ViolatedSet getTemporalConstraint(
            String preAct,
            String sucAct,
            long timeMin,
            long timeMax,
            String firstPattern,
            String secondPatten,
            Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peMap,
            int constraintId
    ) throws ParseException {
        if (firstPattern.equals("First")&&secondPatten.equals("First")){
            return getFirstToFirstConstraint(
                    preAct,
                    sucAct,
                    timeMin,
                    timeMax,
                    peMap,
                    constraintId);
        }

        else if (firstPattern.equals("First")&&secondPatten.equals("Last")){
           return getFirstToLastConstraint(
                    preAct,
                    sucAct,
                    timeMin,
                    timeMax,
                    peMap,
                    constraintId);
        }

        else if (firstPattern.equals("Last")&&secondPatten.equals("First")){
            return getLastToFirstConstraint(
                    preAct,
                    sucAct,
                    timeMin,
                    timeMax,
                    peMap,
                    constraintId);
        }

        else if (firstPattern.equals("Last")&&secondPatten.equals("Last")){
           return  getLastToLastConstraint(
                    preAct,
                    sucAct,
                    timeMin,
                    timeMax,
                    peMap,
                    constraintId);
        }
        return null;
    }


    public ViolatedSet getFirstToFirstConstraint(
            String preAct,
            String sucAct,
            long timeMin,
            long timeMax,
            Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peMap,
            int constraintId) throws ParseException
    {
        this.amount = peMap.keySet().size() - 1;
        this.current = 0;

        // iterate the process execution
        for (String peId : peMap.keySet()) {

            // get the process execution
            HashMap<OcelEvent, HashMap<OcelEvent, HashSet<OcelObject>>> evtMap = peMap.get(peId);

            // for each each that executes preAct
            Set<OcelEvent> evtSet = evtMap.keySet();

            List<OcelEvent> preEvtLst = new ArrayList<>();
            List<OcelEvent> sucEvtLst = new ArrayList<>();

            for (OcelEvent evt : evtSet) {

                // get the first preceding activity
                if (evt.activity.equals(preAct)) {
                    preEvtLst.add(evt);
                } else if (evt.activity.equals(sucAct)) {
                    sucEvtLst.add(evt);
                }
            }

            // compare the timestamp of preEvt and sucEvt
            if (preEvtLst.size() == 0) {
                break;
            } else {
                preEvtLst.sort(new OcelEventComparator());
                if (sucEvtLst.size() == 0) {
                    OcelEvent firstPreEvt = preEvtLst.get(0);
                    ArrayList<String> evtLst = new ArrayList<>();
                    evtLst.add(firstPreEvt.id);
                    vs.appendViolatedRule(
                            peId,
                            evtLst,
                            preAct,
                            "No succeeding activity " + sucAct + " after " +preAct+".",
                            constraintId
                            );
                } else {
                    sucEvtLst.sort(new OcelEventComparator());

                    // get both first events
                    OcelEvent firstPreEvt = preEvtLst.get(0);
                    OcelEvent firstSucEvt = sucEvtLst.get(0);

                    // get the time gap
                    SimpleDateFormat df = new SimpleDateFormat("EEE MMM d H:mm:ss zzzz yyyy", Locale.ENGLISH);
                    Date dateFirstAct = df.parse(firstPreEvt.timestamp.toString());
                    Date dateSecondAct = df.parse(firstSucEvt.timestamp.toString());
                    long throughputTime = (dateSecondAct.getTime() - dateFirstAct.getTime())/1000;

                    // switch based on time stamp
                    if (throughputTime <  timeMin || throughputTime > timeMax) {
                        // add to violation set

                        ArrayList<String> evtLst = new ArrayList<>();
                        evtLst.add(firstPreEvt.id);
                        evtLst.add(firstSucEvt.id);

                        vs.appendViolatedRule(
                                peId,
                                evtLst,
                                firstPreEvt.activity+","+firstSucEvt.activity,
                                "The throughput time for first " + preAct + " and the first " +
                                sucAct + " is not satisfied",
                                constraintId);
                    }
                }
            }
            this.current += 1;
        }
        this.current = this.amount;
        return vs;
    }

    public ViolatedSet getFirstToLastConstraint(
            String preAct,
            String sucAct,
            long timeMin,
            long timeMax,
            Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peMap,
            int constraintId) throws ParseException
    {
        this.amount = peMap.keySet().size() - 1;
        this.current = 0;

        // iterate the process execution
        for (String peId : peMap.keySet()) {

            // get the process execution
            HashMap<OcelEvent, HashMap<OcelEvent, HashSet<OcelObject>>> evtMap = peMap.get(peId);

            // for each each that executes preAct
            Set<OcelEvent> evtSet = evtMap.keySet();

            List<OcelEvent> preEvtLst = new ArrayList<>();
            List<OcelEvent> sucEvtLst = new ArrayList<>();

            for (OcelEvent evt : evtSet) {

                // get the first preceding activity
                if (evt.activity.equals(preAct)) {
                    preEvtLst.add(evt);
                } else if (evt.activity.equals(sucAct)) {
                    sucEvtLst.add(evt);
                }
            }

            // compare the timestamp of preEvt and sucEvt
            if (preEvtLst.size() == 0) {
                break;
            } else {
                preEvtLst.sort(new OcelEventComparator());
                if (sucEvtLst.size() == 0) {
                    OcelEvent firstPreEvt = preEvtLst.get(0);

                    ArrayList<String> evtLst = new ArrayList<>();
                    evtLst.add(firstPreEvt.id);

                    vs.appendViolatedRule(
                            peId,
                            evtLst,
                            preAct,
                            "No succeeding activity " + sucAct + " after " +preAct+".",
                            constraintId);
                } else {
                    sucEvtLst.sort(new OcelEventComparator());

                    // get both first events
                    OcelEvent firstPreEvt = preEvtLst.get(0);
                    OcelEvent lastSucEvt = sucEvtLst.get(sucEvtLst.size()-1);

                    // get the time gap
                    SimpleDateFormat df = new SimpleDateFormat("EEE MMM d H:mm:ss zzzz yyyy", Locale.ENGLISH);
                    Date dateFirstAct = df.parse(firstPreEvt.timestamp.toString());
                    Date dateSecondAct = df.parse(lastSucEvt.timestamp.toString());
                    long throughputTime = (dateSecondAct.getTime() - dateFirstAct.getTime())/1000;

                    // switch based on time stamp
                    if (throughputTime <  timeMin || throughputTime > timeMax) {

                        System.out.println("it is violated");
                        ArrayList<String> evtLst = new ArrayList<>();
                        evtLst.add(firstPreEvt.id);
                        evtLst.add(lastSucEvt.id);

                        // add to violation set
                        vs.appendViolatedRule(
                                peId,
                                evtLst,
                                firstPreEvt.activity+","+lastSucEvt.activity,
                                "The throughput time between the first " + preAct + 
                                " and the last " + sucAct + " is not satisfied",
                                constraintId);
                    }
                }
            }
            this.current += 1;
        }
        this.current = this.amount;
        return vs;
    }


    public ViolatedSet getLastToFirstConstraint(
            String preAct,
            String sucAct,
            long timeMin,
            long timeMax,
            Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peMap,
            int constraintId) throws ParseException
    {
        this.amount = peMap.keySet().size() - 1;
        this.current = 0;

        // iterate the process execution
        for (String peId : peMap.keySet()) {

            // get the process execution
            HashMap<OcelEvent, HashMap<OcelEvent, HashSet<OcelObject>>> evtMap = peMap.get(peId);

            // for each each that executes preAct
            Set<OcelEvent> evtSet = evtMap.keySet();

            List<OcelEvent> preEvtLst = new ArrayList<>();
            List<OcelEvent> sucEvtLst = new ArrayList<>();

            for (OcelEvent evt : evtSet) {

                // get the first preceding activity
                if (evt.activity.equals(preAct)) {
                    preEvtLst.add(evt);
                } else if (evt.activity.equals(sucAct)) {
                    sucEvtLst.add(evt);
                }
            }

            // compare the timestamp of preEvt and sucEvt
            if (preEvtLst.size() == 0) {
                break;
            } else {
                preEvtLst.sort(new OcelEventComparator());
                if (sucEvtLst.size() == 0) {
                    OcelEvent firstPreEvt = preEvtLst.get(preEvtLst.size()-1);

                    ArrayList<String> evtLst = new ArrayList<>();
                    evtLst.add(firstPreEvt.id);

                    vs.appendViolatedRule(
                            peId,
                            evtLst,
                            preAct,
                            "No succeeding activity " + sucAct + " after " +preAct+".",constraintId);
                } else {
                    sucEvtLst.sort(new OcelEventComparator());

                    // get both first events
                    OcelEvent lastPreEvt = preEvtLst.get(preEvtLst.size()-1);
                    OcelEvent firstSucEvt = sucEvtLst.get(0);

                    // get the time gap
                    SimpleDateFormat df = new SimpleDateFormat("EEE MMM d H:mm:ss zzzz yyyy", Locale.ENGLISH);
                    Date dateFirstAct = df.parse(lastPreEvt.timestamp.toString());
                    Date dateSecondAct = df.parse(firstSucEvt.timestamp.toString());
                    long throughputTime = (dateSecondAct.getTime() - dateFirstAct.getTime())/1000;

                    // switch based on time stamp
                    if (throughputTime <  timeMin || throughputTime > timeMax) {

                        ArrayList<String> evtLst = new ArrayList<>();
                        evtLst.add(lastPreEvt.id);
                        evtLst.add(firstSucEvt.id);

                        // add to violation set
                        vs.appendViolatedRule(
                                peId,
                                evtLst,
                                lastPreEvt.activity+","+firstSucEvt.activity,
                                "The throughput time between the last " + preAct 
                                + " and the first " + sucAct + " is not satisfied",
                                constraintId);
                    }
                }
            }
            this.current += 1;
        }
        this.current = this.amount;
        return vs;
    }


    public ViolatedSet getLastToLastConstraint(
            String preAct,
            String sucAct,
            long timeMin,
            long timeMax,
            Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peMap,
            int constraintId) throws ParseException
    {
        this.amount = peMap.keySet().size() - 1;
        this.current = 0;

        // iterate the process execution
        for (String peId : peMap.keySet()) {

            // get the process execution
            HashMap<OcelEvent, HashMap<OcelEvent, HashSet<OcelObject>>> evtMap = peMap.get(peId);

            // for each each that executes preAct
            Set<OcelEvent> evtSet = evtMap.keySet();

            List<OcelEvent> preEvtLst = new ArrayList<>();
            List<OcelEvent> sucEvtLst = new ArrayList<>();

            for (OcelEvent evt : evtSet) {

                // get the last preceding activity
                if (evt.activity.equals(preAct)) {
                    preEvtLst.add(evt);
                } else if (evt.activity.equals(sucAct)) {
                    sucEvtLst.add(evt);
                }
            }

            // compare the timestamp of preEvt and sucEvt
            if (preEvtLst.size() == 0) {
                break;
            } else {
                preEvtLst.sort(new OcelEventComparator());
                if (sucEvtLst.size() == 0) {
                    OcelEvent lastPreEvt = preEvtLst.get(preEvtLst.size()-1);

                    ArrayList<String> evtLst = new ArrayList<>();
                    evtLst.add(lastPreEvt.id);

                    vs.appendViolatedRule(
                            peId,
                            evtLst,
                            preAct,
                            "No succeeding activity " + sucAct + " after " +preAct+".",constraintId);
                } else {
                    sucEvtLst.sort(new OcelEventComparator());

                    // get both last events
                    OcelEvent lastPreEvt = preEvtLst.get(preEvtLst.size()-1);
                    OcelEvent lastSucEvt = sucEvtLst.get(sucEvtLst.size()-1);

                    // get the time gap
                    SimpleDateFormat df = new SimpleDateFormat("EEE MMM d H:mm:ss zzzz yyyy", Locale.ENGLISH);
                    Date dateFirstAct = df.parse(lastPreEvt.timestamp.toString());
                    Date dateSecondAct = df.parse(lastSucEvt.timestamp.toString());
                    long throughputTime = (dateSecondAct.getTime() - dateFirstAct.getTime())/1000;

                    // switch based on time stamp
                    if (throughputTime <  timeMin || throughputTime > timeMax) {

                        ArrayList<String> evtLst = new ArrayList<>();
                        evtLst.add(lastPreEvt.id);
                        evtLst.add(lastSucEvt.id);

                        // add to violation set
                        vs.appendViolatedRule(
                                peId,
                                evtLst,
                                lastPreEvt.activity+","+lastSucEvt.activity,
                                "The throughput time between the last " + preAct + 
                                " and the last " + sucAct + " is not satisfied",constraintId);
                    }
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
            String sucAct,
            List<String> objTypeLst){

        HashMap<OcelEvent,HashSet<OcelObject>> evtObjSet = evtMap.get(preEvt);
        // iterate each event in the evtObjSet
        if (evtObjSet != null) {
            for (OcelEvent sucEvt : evtObjSet.keySet()) {

                // check whether the path exist
                Set<OcelObject> objBeforeSet = preEvt.relatedObjects;
                Set<OcelObject> objCurrentSet = sucEvt.relatedObjects;
                HashSet<OcelObject> resSet = new HashSet<>();
                resSet.addAll(objBeforeSet);
                resSet.retainAll(objCurrentSet);
                for (OcelObject obj : resSet) {
                    // if the list contains the obj type
                    if (objTypeLst.contains(obj.objectType.name)) {
                        // add the sucEvt to the check set if it executes the suc activity
                        if (sucEvt.activity.equals(sucAct)) {
                            sucActSet.add(sucEvt);
                        }
                        sucActSet = getSucTarActSet(sucActSet, sucEvt, evtMap, sucAct, objTypeLst);
                        // no need to continue
                        break;
                    }
                }
            }
        }
        return sucActSet;
    }


    @Override
    public void run() {
        try {
            getTemporalConstraint(
                    preAct,
                    sucAct,
                    timeMin,
                    timeMax,
                    firstPattern,
                    secondPatten,
                    peMap,
                    constraintId);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

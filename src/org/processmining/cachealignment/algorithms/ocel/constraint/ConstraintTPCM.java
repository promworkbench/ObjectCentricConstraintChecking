package org.processmining.cachealignment.algorithms.ocel.constraint;

import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEvent;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEventComparator;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEventLog;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConstraintTPCM implements Runnable{

    ViolatedSet vs = new ViolatedSet();

    private volatile int current;
    private int amount;

    public String timeType;

    public long minTime;

    public long maxTime;

    public String timeUnit;

    public String targetActivity;

    public OcelEventLog ocel;

    public Map<String, HashMap<OcelEvent, HashMap<OcelEvent, HashSet<OcelObject>>>> peMap;
    public List objTypeLst;
    public ConstraintTPCM(){

    }

    public ConstraintTPCM(
            OcelEventLog ocel,
            String targetActivity,
            String timeType,
            long minTime,
            long maxTime,
            String timeUnit,
            List objTypeLst){
        this.ocel = ocel;
        this.targetActivity = targetActivity;
        this.timeType = timeType;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.timeUnit = timeUnit;
        this.objTypeLst = objTypeLst;
    }


    public int getAmount(){
        return amount;
    }

    public int getCurrent(){
        return (int)(current/amount*100);
    }

    public ViolatedSet getViolationSet() throws ParseException {
        System.out.println("time type:"+timeType);
        switch(timeType){
            case "Waiting time":
                System.out.println("start check waiting time");
                return getWaitingTime(ocel, targetActivity, minTime, maxTime, timeUnit, objTypeLst);
//            case "Lagging time":
//                return getLaggingTime(ocel, targetActivity, minTime, maxTime, objTypeLst);
            default:
                break;
        }
        return vs;
    }


    public ViolatedSet getWaitingTime(
            OcelEventLog ocel,
            String targetActivity,
            long minTime,
          long maxTime,
          String timeUnit,
          List objTypeLst) throws ParseException {
        this.amount = ocel.events.size() - 1;
        this.current = 0;

        // iterate each process execution, and iterate each event that match the target activity

            for (OcelEvent evt : ocel.events.values()) {
                this.current += 1;
                // get evt that executes the target activity
                if (evt.activity.equals(targetActivity)) {
                    List<OcelEvent> evtBefore = new ArrayList<>();
                    // get the list of related obj
                    ArrayList<OcelObject> objLst = new ArrayList<OcelObject>();

                    for (OcelObject ocelObject : evt.relatedObjects) {
                        if (objTypeLst.contains(ocelObject.objectType.name)) {
                            objLst.add(ocelObject);
                            int evtIdx = ocelObject.sortedRelatedEvents.indexOf(evt);
                            if (evtIdx - 1 >= 0) {
                                evtBefore.add(ocelObject.sortedRelatedEvents.get(evtIdx - 1));
                            }
                        }
                    }
                    // get the list of events before
                    evtBefore.sort(new OcelEventComparator());
                    if (evtBefore.size() > 1) {

                        // get the problematic obj
                        Set<OcelObject> objBeforeSet = evtBefore.get(evtBefore.size() - 1).relatedObjects;
                        Set<OcelObject> objCurrentSet = evt.relatedObjects;
                        HashSet<OcelObject> resSet = new HashSet<>();
                        resSet.addAll(objBeforeSet);
                        resSet.retainAll(objCurrentSet);

                        Set<String> objSet = new HashSet<>();
                        for (OcelObject obj : resSet) {
                            if (objTypeLst.contains(obj.objectType.name)) {
                                objSet.add(obj.id);
                            }
                        }

                        // get the time
                        long waitingTime = (evt.timestamp.getTime() - evtBefore.get(evtBefore.size() - 1).timestamp.getTime())/1000;
                        switch (timeUnit) {
                            case "minutes":
                                waitingTime = waitingTime / 60;
                                break;
                            case "hours":
                                waitingTime = waitingTime / 3600;
                                break;
                            case "days":
                                waitingTime = waitingTime / 86400;
                                break;
                            case "weeks":
                                waitingTime = waitingTime / 604800;
                                break;
                            default:
                                break;
                        }

                        if (waitingTime < minTime || waitingTime > maxTime) {
                            vs.appendViolatedRule(
                                    evt.id,
                                    new ArrayList<>(),
                                    objSet.toString(),
                                    "The waiting time is " + waitingTime + " " + timeUnit + " , does not match the constraint " + "(" + minTime + "," + maxTime + ") " + timeUnit);
                        }
                    }
                }
            }
            this.current = this.amount;
            return vs;
    }

    public ViolatedSet getPoolingTime(
            OcelEventLog ocel,
            String targetActivity,
        long minTime, long maxTime, List objTypeLst) throws ParseException {
        this.amount = ocel.events.size() - 1;
        this.current = 0;
        for (OcelEvent evt: ocel.events.values()) {
            // get evt that executes the target activity
            if (evt.activity.equals(targetActivity)) {
                List<OcelEvent> evtBefore = new ArrayList<>();
                // get the list of related obj
                ArrayList<OcelObject>  objLst = new ArrayList<OcelObject>();

                for (OcelObject ocelObject : evt.relatedObjects) {
                    if (objTypeLst.contains(ocelObject.objectType.name)) {
                        objLst.add(ocelObject);
                        int evtIdx = ocelObject.sortedRelatedEvents.indexOf(evt);
                        evtBefore.add(ocelObject.sortedRelatedEvents.get(evtIdx-1));
                    }
                }
                // get the list of events before
                evtBefore.sort(new OcelEventComparator());
                System.out.println(evtBefore);

                if(evtBefore.size()<=1) {
                }
                else {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date dateFirstAct = df.parse(evtBefore.get(0).timestamp.toString());
                    Date dateSecondAct = df.parse(evtBefore.get(evtBefore.size()-1).timestamp.toString());
                    long PoolingTime = dateSecondAct.getTime() - dateFirstAct.getTime();
                    if (PoolingTime >= minTime && PoolingTime <= maxTime) {
                    } else {
                        vs.appendViolatedRule(
                                evt.id,
                                new ArrayList<>(),
                                objLst.toString(),
                                "The Pooling time (" + PoolingTime + ") does not match the constraint " + "(" + minTime + "," + maxTime + ")");
                    }
                }
            }
            this.current+=1;

        }
        this.current = this.amount;
        return vs;
    }


    public ViolatedSet getSynchronizationTime(OcelEventLog ocel, String targetActivity, long minTime, long maxTime, List objTypeLst) throws ParseException {
        this.amount = ocel.events.size() - 1;
        this.current = 0;
        for (OcelEvent evt: ocel.events.values()) {
            // get evt that executes the target activity
            if (evt.activity.equals(targetActivity)) {
                List<OcelEvent> evtBefore = new ArrayList<>();
                // get the list of related obj
                ArrayList<OcelObject> objLst = new ArrayList<OcelObject>();

                for (OcelObject ocelObject : evt.relatedObjects) {
                    if (objTypeLst.contains(ocelObject.objectType.name)) {
                        objLst.add(ocelObject);
                        int evtIdx = ocelObject.sortedRelatedEvents.indexOf(evt);
                        evtBefore.add(ocelObject.sortedRelatedEvents.get(evtIdx-1));
                    }
                }
                // get the list of events before
                evtBefore.sort(new OcelEventComparator());
                System.out.println(evtBefore);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date dateFirstAct = df.parse(evtBefore.get(0).timestamp.toString());
                Date dateSecondAct = df.parse(evtBefore.get(-1).timestamp.toString());
                long SynchronizationTime = dateSecondAct.getTime() - dateFirstAct.getTime();
                if (SynchronizationTime >= minTime && SynchronizationTime <= maxTime) {}
                else {
                    vs.appendViolatedRule(
                            evt.id,
                            new ArrayList<>(),
                            objLst.toString(),
                            "The Synchronization time (" + SynchronizationTime + ") does not match the constraint " + "(" + minTime + "," + maxTime + ")");
                }

            }
            this.current+=1;
        }
        this.current = this.amount;

        return vs;
    }



    public ViolatedSet getLaggingTime(OcelEventLog ocel, String targetActivity, long minTime, long maxTime, List objTypeLst) throws ParseException {
        this.amount = ocel.events.size() - 1;
        this.current = 0;
        for (OcelEvent evt: ocel.events.values()) {
            // get evt that executes the target activity
            if (evt.activity.equals(targetActivity)) {
                List<OcelEvent> evtBefore = new ArrayList<>();
                // get the list of related obj
                ArrayList<OcelObject>  objLst = new ArrayList<OcelObject>();

                for (OcelObject ocelObject : evt.relatedObjects) {
                    if (objTypeLst.contains(ocelObject.objectType.name)) {
                        objLst.add(ocelObject);
                        int evtIdx = ocelObject.sortedRelatedEvents.indexOf(evt);
                        evtBefore.add(ocelObject.sortedRelatedEvents.get(evtIdx-1));
                    }
                }
                // get the list of events before
                evtBefore.sort(new OcelEventComparator());
                System.out.println(evtBefore);

//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//                Date dateFirstAct = df.parse(evtBefore.get(0).timestamp.toString());
//                Date dateSecondAct = df.parse(evt.timestamp.toString());
//                long LaggingTime = dateSecondAct.getTime() - dateFirstAct.getTime();
                long LaggingTime = evt.timestamp.getTime() - evtBefore.get(0).timestamp.getTime();
                if (LaggingTime >= minTime && LaggingTime <= maxTime){}
                else{
                    vs.appendViolatedRule(
                            evt.id,
                            new ArrayList<>(),
                            objLst.toString(),
                            "The Lagging time ("+LaggingTime +") does not match the constraint "+"("+ minTime+ "," + maxTime+")");
                }
            }
            this.current+=1;

        }
        this.current = this.amount;

        return vs;
    }


    public ViolatedSet getResponseTime(OcelEventLog ocel, String targetActivity, long minTime, long maxTime, List objTypeLst) throws ParseException {
        this.amount = ocel.events.size() - 1;
        this.current = 0;
        for (OcelEvent evt: ocel.events.values()) {
            // get evt that executes the target activity
            if (evt.activity.equals(targetActivity)) {
                Set<OcelEvent> evtAfter = new HashSet<>();
                // get the list of related obj
                ArrayList<OcelObject>  objLst = new ArrayList<OcelObject>();

                for (OcelObject ocelObject : evt.relatedObjects) {
                    if (objTypeLst.contains(ocelObject.objectType.name)) {
                        objLst.add(ocelObject);
                        int evtIdx = ocelObject.sortedRelatedEvents.indexOf(evt);
                        if (evtIdx+1<ocelObject.sortedRelatedEvents.size()) {
                            evtAfter.add(ocelObject.sortedRelatedEvents.get(evtIdx + 1));
                        }
                    }
                }
                // get the list of events before

                ArrayList<OcelEvent> evtAfterLst = new ArrayList<>();
                evtAfterLst.addAll(evtAfter);
                evtAfterLst.sort(new OcelEventComparator());
                System.out.println(evtAfterLst);

                if (evtAfterLst.size()>0) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date dateFirstAct = df.parse(evtAfterLst.get(evtAfterLst.size()-1).timestamp.toString());
                    Date dateSecondAct = df.parse(evt.timestamp.toString());
                    long ResponseTime = dateSecondAct.getTime() - dateFirstAct.getTime();
                    if (ResponseTime >= minTime && ResponseTime <= maxTime) {
                    } else {
                        vs.appendViolatedRule(
                                evt.id,
                                new ArrayList<>(),
                                objLst.toString(),
                                "The Response time (" + ResponseTime + ") does not match the constraint " + "(" + minTime + "," + maxTime + ")");
                    }
                }
            }
            this.current+=1;

        }
        this.current = this.amount;

        return vs;
    }

    public ViolatedSet getThroughputTime(OcelEventLog ocel, String targetActivity, long minTime, long maxTime, List objTypeLst) throws ParseException {
        this.amount = ocel.events.size() - 1;
        this.current = 0;
        for (OcelEvent evt: ocel.events.values()) {
            // get evt that executes the target activity
            if (evt.activity.equals(targetActivity)) {
                List<OcelEvent> evtAfter = new ArrayList<>();
                // get the list of related obj
                ArrayList<OcelObject>  objLst = new ArrayList<OcelObject>();

                for (OcelObject ocelObject : evt.relatedObjects) {
                    if (objTypeLst.contains(ocelObject.objectType.name)) {
                        objLst.add(ocelObject);
                        int evtIdx = ocelObject.sortedRelatedEvents.indexOf(evt);
                        evtAfter.add(ocelObject.sortedRelatedEvents.get(evtIdx+1));
                    }
                }
                // get the list of events before
                evtAfter.sort(new OcelEventComparator());
                System.out.println(evtAfter);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date dateFirstAct = df.parse(evtAfter.get(-1).timestamp.toString());
                Date dateSecondAct = df.parse(evt.timestamp.toString());
                long ThroughputTime = dateSecondAct.getTime() - dateFirstAct.getTime();
                if (ThroughputTime >= minTime && ThroughputTime <= maxTime){}
                else{
                    vs.appendViolatedRule(
                            evt.id,
                            new ArrayList<>(),
                            objLst.toString(),
                            "The Throughput time ("+ThroughputTime +") does not match the constraint "+"("+ minTime+ "," + maxTime+")");
                }
            }
        }
        this.current = this.amount;

        return vs;
    }


    public ViolatedSet getCloggingTime(OcelEventLog ocel, String targetActivity, long minTime, long maxTime, List objTypeLst) throws ParseException {
        this.amount = ocel.events.size() - 1;
        this.current = 0;
        for (OcelEvent evt: ocel.events.values()) {
            // get evt that executes the target activity
            if (evt.activity.equals(targetActivity)) {
                List<OcelEvent> evtAfter = new ArrayList<>();
                // get the list of related obj
                ArrayList<OcelObject>  objLst = new ArrayList<OcelObject>();

                for (OcelObject ocelObject : evt.relatedObjects) {
                    if (objTypeLst.contains(ocelObject.objectType.name)) {
                        objLst.add(ocelObject);
                        int evtIdx = ocelObject.sortedRelatedEvents.indexOf(evt);
                        evtAfter.add(ocelObject.sortedRelatedEvents.get(evtIdx+1));
                    }
                }
                // get the list of events before
                evtAfter.sort(new OcelEventComparator());
                System.out.println(evtAfter);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date dateFirstAct = df.parse(evtAfter.get(-1).timestamp.toString());
                Date dateSecondAct = df.parse(evt.timestamp.toString());
                long CloggingTime = dateSecondAct.getTime() - dateFirstAct.getTime();
                if (CloggingTime >= minTime && CloggingTime <= maxTime){}
                else{
                    vs.appendViolatedRule(
                            evt.id,
                            new ArrayList<>(),
                            objLst.toString(),
                            "The Clogging time ("+CloggingTime +") does not match the constraint "+"("+ minTime+ "," + maxTime+")");
                }
            }
        }
        this.current = this.amount;

        return vs;
    }


    @Override
    public void run() {
        switch(timeType){
            case "Waiting time":
                try {
                    getWaitingTime(ocel, targetActivity, minTime, maxTime, timeUnit, objTypeLst);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "Lagging time":
                try {
                    getLaggingTime(ocel, targetActivity, minTime, maxTime, objTypeLst);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "Synchronization time":
                try {
                    getSynchronizationTime(ocel, targetActivity, minTime, maxTime, objTypeLst);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "Response time":
                try {
                    getResponseTime(ocel, targetActivity, minTime, maxTime, objTypeLst);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "Throughput time":
                try {
                    getThroughputTime(ocel, targetActivity, minTime, maxTime, objTypeLst);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "Pooling time":
                try {
                    getPoolingTime(ocel, targetActivity, minTime, maxTime, objTypeLst);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "Clogging time":
                try {
                    getCloggingTime(ocel, targetActivity, minTime, maxTime, objTypeLst);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                break;
        }
    }
}

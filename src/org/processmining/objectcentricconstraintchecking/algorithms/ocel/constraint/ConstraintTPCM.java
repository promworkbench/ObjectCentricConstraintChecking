package org.processmining.objectcentricconstraintchecking.algorithms.ocel.constraint;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelEvent;
import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelEventComparator;
import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelEventLog;
import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelObject;

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
    
    public int constraintId;

    public Map<String, HashMap<OcelEvent, HashMap<OcelEvent, HashSet<OcelObject>>>> peMap;
    public List<String> objTypeLst;
    public ConstraintTPCM(){

    }

    public ConstraintTPCM(
            OcelEventLog ocel,
            String targetActivity,
            String timeType,
            long minTime,
            long maxTime,
            String timeUnit,
            List<String> objTypeLst,
            int constraintId){
        this.ocel = ocel;
        this.targetActivity = targetActivity;
        this.timeType = timeType;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.timeUnit = timeUnit;
        this.objTypeLst = objTypeLst;
        this.constraintId = constraintId;
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
            case "waiting time":
                return getWaitingTime(ocel, targetActivity, minTime, maxTime, timeUnit, objTypeLst);
            case "flow time":
                return getFlowTime(ocel, targetActivity, minTime, maxTime, timeUnit, objTypeLst);
            case "synchronization time":
                return getSynchronizationTime(ocel, targetActivity, minTime, maxTime, timeUnit, objTypeLst);
            case "pooling time":
                return getPoolingTime(ocel, targetActivity, minTime, maxTime, timeUnit, objTypeLst);
            case "laggine time":
                return getLaggingTime(ocel, targetActivity, minTime, maxTime, timeUnit, objTypeLst); 
            default:
                break;
        }
        return vs;
    }

    // the last pre event before the target event
    public ViolatedSet getWaitingTime(
            OcelEventLog ocel,
            String targetActivity,
            long minTime,
          long maxTime,
          String timeUnit,
          List<String> objTypeLst) throws ParseException {
        this.amount = ocel.events.size() - 1;
        this.current = 0;

        // iterate each event that match the target activity
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
                    if (evtBefore.size() >= 1) {

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
                                    "The waiting time is " + waitingTime + " " + timeUnit + " , does not match the constraint " + "(" + minTime + "," + maxTime + ") " + timeUnit,
                                    constraintId);
                        }
                    }
                }
            }
            this.current = this.amount;
            return vs;
    }

    // the first pre and last pre event before the target event
    public ViolatedSet getFlowTime(
            OcelEventLog ocel,
            String targetActivity,
            long minTime,
          long maxTime,
          String timeUnit,
          List<String> objTypeLst) throws ParseException {
        this.amount = ocel.events.size() - 1;
        this.current = 0;

        // iterate each event that match the target activity
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
                    if (evtBefore.size() >= 1) {

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
                        long waitingTime = (evt.timestamp.getTime() - evtBefore.get(0).timestamp.getTime())/1000;
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
                                    "The waiting time is " + waitingTime + " " + timeUnit + " , does not match the constraint " + "(" + minTime + "," + maxTime + ") " + timeUnit,
                                    constraintId);
                        }
                    }
                }
            }
            this.current = this.amount;
            return vs;
    }

    // the first pre and last pre event before the target event

    public ViolatedSet getPoolingTime(
            OcelEventLog ocel,
            String targetActivity,
            long minTime,
          long maxTime,
          String timeUnit,
          List<String> objTypeLst) throws ParseException {
        this.amount = ocel.events.size() - 1;
        this.current = 0;

        // iterate each event that match the target activity
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
                        long poolingTime = (evtBefore.get(evtBefore.size() - 1).timestamp.getTime() - evtBefore.get(0).timestamp.getTime())/1000;
                        switch (timeUnit) {
                            case "minutes":
                                poolingTime = poolingTime / 60;
                                break;
                            case "hours":
                                poolingTime = poolingTime / 3600;
                                break;
                            case "days":
                                poolingTime = poolingTime / 86400;
                                break;
                            case "weeks":
                                poolingTime = poolingTime / 604800;
                                break;
                            default:
                                break;
                        }

                        if (poolingTime < minTime || poolingTime > maxTime) {
                            vs.appendViolatedRule(
                                    evt.id,
                                    new ArrayList<>(),
                                    objSet.toString(),
                                    "The waiting time is " + poolingTime + " " + timeUnit + " , does not match the constraint " + "(" + minTime + "," + maxTime + ") " + timeUnit,
                                    constraintId);
                        }
                    }
                }
            }
            this.current = this.amount;
            return vs;
    }

    public ViolatedSet getLaggingTime(
            OcelEventLog ocel,
            String targetActivity,
            long minTime,
          long maxTime,
          String timeUnit,
          List<String> objTypeLst) throws ParseException {
        this.amount = ocel.events.size() - 1;
        this.current = 0;

        // iterate each event that match the target activity
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
                        long laggingTime = (evtBefore.get(evtBefore.size() - 1).timestamp.getTime() - evtBefore.get(0).timestamp.getTime())/1000;
                        switch (timeUnit) {
                            case "minutes":
                                laggingTime = laggingTime / 60;
                                break;
                            case "hours":
                                laggingTime = laggingTime / 3600;
                                break;
                            case "days":
                                laggingTime = laggingTime / 86400;
                                break;
                            case "weeks":
                                laggingTime = laggingTime / 604800;
                                break;
                            default:
                                break;
                        }

                        if (laggingTime < minTime || laggingTime > maxTime) {
                            vs.appendViolatedRule(
                                    evt.id,
                                    new ArrayList<>(),
                                    objSet.toString(),
                                    "The waiting time is " + laggingTime + " " + timeUnit + " , does not match the constraint " + "(" + minTime + "," + maxTime + ") " + timeUnit,
                                    constraintId);
                        }
                    }
                }
            }
            this.current = this.amount;
            return vs;
    }

    public ViolatedSet getSynchronizationTime(
            OcelEventLog ocel,
            String targetActivity,
            long minTime,
          long maxTime,
          String timeUnit,
          List<String> objTypeLst) throws ParseException {
        this.amount = ocel.events.size() - 1;
        this.current = 0;

        // iterate each event that match the target activity
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
                        long syncTime = (evtBefore.get(evtBefore.size() - 1).timestamp.getTime() - evtBefore.get(0).timestamp.getTime())/1000;
                        switch (timeUnit) {
                            case "minutes":
                                syncTime = syncTime / 60;
                                break;
                            case "hours":
                                syncTime = syncTime / 3600;
                                break;
                            case "days":
                                syncTime = syncTime / 86400;
                                break;
                            case "weeks":
                                syncTime = syncTime / 604800;
                                break;
                            default:
                                break;
                        }

                        if (syncTime < minTime || syncTime > maxTime) {
                            vs.appendViolatedRule(
                                    evt.id,
                                    new ArrayList<>(),
                                    objSet.toString(),
                                    "The waiting time is " + syncTime + " " + timeUnit + " , does not match the constraint " + "(" + minTime + "," + maxTime + ") " + timeUnit,
                                    constraintId);
                        }
                    }
                }
            }
            this.current = this.amount;
            return vs;
    }


    @Override
    public void run() {
        switch(timeType){
            case "waiting time":
                try {
                    getWaitingTime(ocel, targetActivity, minTime, maxTime, timeUnit, objTypeLst);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "lagging time":
                try {
                    getLaggingTime(ocel, targetActivity, minTime, maxTime, timeUnit, objTypeLst);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "flow time":
                try {
                    getFlowTime(ocel, targetActivity, minTime, maxTime, timeUnit, objTypeLst);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "pooling time":
                try {
                    getPoolingTime(ocel, targetActivity, minTime, maxTime, timeUnit, objTypeLst);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "synchronization time":
                try {
                    getSynchronizationTime(ocel, targetActivity, minTime, maxTime, timeUnit, objTypeLst);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                break;
        }
    }
}

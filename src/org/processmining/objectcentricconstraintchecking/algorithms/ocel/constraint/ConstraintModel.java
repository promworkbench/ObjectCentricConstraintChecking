package org.processmining.objectcentricconstraintchecking.algorithms.ocel.constraint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelEvent;
import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelEventLog;
import org.processmining.objectcentricconstraintchecking.algorithms.ocel.ocelobjects.OcelObject;

public class ConstraintModel {


    // -----------The parameter for the mxConnectionHandler--------
    public String startEntityName;
    public String targetEntityName;

    public Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peDirectBeforeMap;
    public List<String> allObjectTyps;

    public List<String> selObjTypeLst;

    public ArrayList<String> consNameLst = new ArrayList<>();

    public ArrayList<ArrayList> consLst = new ArrayList<>();

    public HashMap<String, Integer> alreadySelected = new HashMap<>();
    // -------------------------------------------------------------


    //------ The parameter for activity-activity constraints -------
    public HashMap<String,Map> actToActMap = new HashMap<>();  // use act1+act2 as key

    public HashMap<String,Map> objToObjCardinality = new HashMap<>();  // use act1+act2 as key

    public HashMap<String,List> timePerformance = new HashMap<>();  // use act+timetype as key

    ViolatedSet vs = new ViolatedSet();
    //--------------------------------------------------------------
    public ArrayList<String> objTypeList;

    public ArrayList<String> objTypeToConsider;

    public String leadObjType;

    public HashSet<String> revObjTypes;

    public OcelEventLog ocelEventLog;

    public Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peMap;

    private static ConstraintModel instance = new ConstraintModel();

    private ConstraintModel(){

    }

    public static ConstraintModel getInstance(){
        return instance;
    }


    // During checking, save the violation set to constraint model
    public ViolatedSet getViolatedSet(){
        return vs;
    }

    // During checking, add the violation set to constraint model
    public void appendViolatedSet(ViolatedSet newVs){
        vs.mergeViolatedRule(newVs);
    }


    public void resetAll(){
        this.vs = new ViolatedSet();
        this.alreadySelected = new HashMap<>();
        this.actToActMap = new HashMap<>();
    }


    // set the objTypeList
    public void setObjTypeList(ArrayList<String> objTypeList){
        this.objTypeList = objTypeList;
    }

    public void setOCEL(OcelEventLog ocelEventLog){
        this.ocelEventLog = ocelEventLog;
    }

    public OcelEventLog getOcel(){
        return ocelEventLog;
    }

    /**
     * @param firstAct: the first activity
     * @param secondAct: the second activity
     * @param firstRefObj: the ref object type
     * @param secondRefObj: the ref object type
     * @param tempCons: the temporal constraint
     * @param minTime: the min time in between
     * @param maxTime: the max time in between
     * @return void
     * @description TODO
     * @date 11/29/2022 11:23 AM
     */
    public void setActToAct(String firstAct,
                            String secondAct,
                            String firstRefObj,
                            String secondRefObj,
                            String tempCons,
                            String minTime,
                            String maxTime,
                            String timeUnit
                            ){
        HashMap<String,String> actToActDetail = new HashMap<>();
        actToActDetail.put("firstAct", firstAct);
        actToActDetail.put("secondAct", secondAct);
        actToActDetail.put("firstRefObj", firstRefObj);
        actToActDetail.put("secondRefObj", secondRefObj);
        actToActDetail.put("tempConstraint", tempCons);
        actToActDetail.put("minTime", minTime);
        actToActDetail.put("maxTime", maxTime);
        actToActDetail.put("timeUnit", timeUnit);

        String actToActString = firstAct + secondAct;
        actToActMap.put(actToActString, actToActDetail);
    }

    /**
     * @param targetActivity: the first object type
     * @param timeType: the second object type
     * @param minTime: the ref object type
     * @param maxTime: the ref object type
     * @param timeUnit: the temporal constraint
     * @param objToConsider: the temporal constraint
     * @return void
     * @description TODO
     * @date 11/29/2022 11:23 AM
     */
    public void setTimePerformance(
            String targetActivity,
            String timeType,
            long minTime,
            long maxTime,
            List<String> objToConsider
    ){
        List timePerformanceDetail = new ArrayList<>();
        timePerformanceDetail.add(targetActivity);
        timePerformanceDetail.add(timeType);
        timePerformanceDetail.add(String.valueOf(minTime));
        timePerformanceDetail.add(String.valueOf(maxTime));
        timePerformanceDetail.add(objToConsider);
        String timePerformanceId = targetActivity + timeType;
        timePerformance.put(timePerformanceId, timePerformanceDetail);
    }
}

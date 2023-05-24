package org.processmining.cachealignment.algorithms.ocel.constraint;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConstraintMonitorEngine implements Runnable{

    // use cme to implement the checking of constraint

    private int amount;

    String constraintType;
    private volatile int current;

    ConstraintModel cm = ConstraintModel.getInstance();


    public ConstraintMonitorEngine(int amount){
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    @Override
    public void run() {

        ConstraintTPCM tpcm;

        ConstraintOCCM occm;

        ConstraintFCM fcm;

        ConstraintPFTCM pftcm;

        ConstraintPFCCM pfccm;

        //子线程的耗时操作
        while (current < amount) {
            // initialise ViolatedSet vs
            ViolatedSet vs = new ViolatedSet();

            // get the corresponding constraint
            ArrayList<Map> consMap = cm.consLst.get(current);

            List<String> objTypeLst;

            // get the corresponding constraint type
            String consType = consMap.get(0).get("constraintType").toString();

            String preAct;
            String sucAct;
            // select the corresponding constraint checker
            switch(consType){
                case "activityTimeConstraint":
                    String tarAct = consMap.get(1).get("targetActivity").toString();
                    String timeType = consMap.get(2).get("timeType").toString();
                    long minTime = (long) consMap.get(3).get("minTime");
                    long maxTime = (long) consMap.get(4).get("maxTime");
                    String timeUnit = consMap.get(5).get("timeUnit").toString();
                    objTypeLst = (List) consMap.get(6).get("objLst");
                    tpcm = new ConstraintTPCM(
                            cm.ocelEventLog,
                            tarAct,
                            timeType,
                            minTime,
                            maxTime,
                            timeUnit,
                            objTypeLst,
                            current);
                    try {
                        vs = tpcm.getViolationSet();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "objConstraint":
                    String refObjType = consMap.get(1).get("refObjType").toString();
                    String actName = consMap.get(2).get("actName").toString();
                    int minCard = Integer.parseInt(consMap.get(3).get("minCard").toString());
                    int maxCard = Integer.parseInt(consMap.get(4).get("maxCard").toString());
                    occm = new ConstraintOCCM(
                            cm.ocelEventLog,
                            actName,
                            refObjType,
                            minCard,
                            maxCard,
                            current);
                    try {
                        vs = occm.getViolationSet();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "freqConstraint":
                    String refActName = consMap.get(1).get("actName").toString();
                    int minFreq = Integer.parseInt(consMap.get(2).get("minFreq").toString());
                    int maxFreq = Integer.parseInt(consMap.get(3).get("maxFreq").toString());
                    fcm = new ConstraintFCM(
                            cm.peMap,
                            refActName,
                            minFreq,
                            maxFreq,
                            current);
                    try {
                        vs = fcm.getViolationSet();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "cardinalityConstraint":
                    preAct = consMap.get(1).get("preAct").toString();
                    sucAct = consMap.get(2).get("sucAct").toString();
                    int preActCardMin = (int) consMap.get(3).get("preActCardMin");
                    int preActCardMax = (int) consMap.get(4).get("preActCardMax");
                    int sucActCardMin = (int) consMap.get(5).get("sucActCardMin");
                    int sucActCardMax = (int) consMap.get(6).get("sucActCardMax");
//                    objTypeLst = (List) consMap.get(7).get("objLst");
                    pfccm = new ConstraintPFCCM(
                            preAct,
                            preActCardMin,
                            preActCardMax,
                            sucAct,
                            sucActCardMin,
                            sucActCardMax,
                            cm.peMap,
                            current);
                    try {
                        vs = pfccm.getViolationSet();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "temporalConstraint":
                    preAct = consMap.get(1).get("preAct").toString();
                    sucAct = consMap.get(2).get("sucAct").toString();
                    long timeMin = (long) consMap.get(3).get("timeMin");
                    long timeMax = (long) consMap.get(4).get("timeMax");
                    System.out.println("max time:"+timeMax);
                    String firstPattern = consMap.get(5).get("firstPattern").toString();
                    String secondPatten = consMap.get(6).get("secondPatten").toString();
                    pftcm = new ConstraintPFTCM(
                            preAct,
                            sucAct,
                            timeMin,
                            timeMax,
                            firstPattern,
                            secondPatten,
                            cm.peMap,
                            current);
                    try {
                        vs = pftcm.getViolationSet();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    break;
            }
            //add the violation set to cm
            cm.appendViolatedSet(vs);
            // add the counter
            current++;
        }
    }
}

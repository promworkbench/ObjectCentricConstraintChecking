package org.processmining.cachealignment.algorithms.ocel.constraint;

import org.processmining.cachealignment.algorithms.ocel.extraction.CaseGraph;
import org.processmining.cachealignment.algorithms.ocel.constraint.ConstraintWizardParameters;
import org.processmining.cachealignment.algorithms.ocel.constraint.ConstraintWizardStep;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEvent;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEventLog;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelObject;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelObjectType;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.wizard.ListWizard;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

import javax.swing.*;
import java.util.*;

public class CaseExtraction {

    protected JPanel root;

    ConstraintModel cm = ConstraintModel.getInstance();

    public CaseGraph extractCase(UIPluginContext context,
                                 OcelEventLog ocel) throws InterruptedException {

        ConstraintWizardStep wizStep = new ConstraintWizardStep(ocel);
        List<ProMWizardStep<ConstraintWizardParameters>> wizStepList = new ArrayList<>();
        wizStepList.add(wizStep);
        ListWizard<ConstraintWizardParameters> listWizard = new ListWizard<>(wizStepList);
        ConstraintWizardParameters parameters = ProMWizardDisplay.show(context, listWizard, new ConstraintWizardParameters());

        cm.leadObjType=parameters.leadObjType;
        cm.revObjTypes=parameters.revObjTypes;

        List<Map> lstMap = extractCaseWithLeadingType(ocel, parameters.leadObjType, parameters.revObjTypes);
        cm.peDirectBeforeMap = reversePeMap(lstMap.get(3));

        CaseGraph caseGraph = new CaseGraph(context,
                lstMap.get(0),
                lstMap.get(1),
                lstMap.get(2),
                parameters.leadObjType,
                parameters.revObjTypes,
                lstMap.get(3),
                ocel);
        return caseGraph;
    }


    public List<Map> extractCaseWithLeadingType(OcelEventLog ocel,
                                                String leadObjType,
                                                HashSet<String> revObjectTypes) {

        // leadObjMap key: one obj from lead, val: set of secondary obj
        Map<String, HashSet<OcelObject>> leadObjMap = new HashMap<>();

        // leadObjToEvtMap:
        Map<String, Map<OcelObject, List<OcelEvent>>> objToRelatedEventsEdge = new HashMap<>();
        Map<String, HashSet<OcelEvent>> objToRelatedEventsNode= new HashMap<>();
        Map<OcelObject, List<OcelEvent>> tempObjToRelatedEventsEdge;

        // use a map to store the direct follow relationship in the process execution
        // key is the event, value is the event and related objects
        HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>> evtDfMap;

        // iterate all ocelObject from the leading type
        Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peMap= new HashMap<>();
        HashSet<String> desiredObjType = new HashSet<>();
        desiredObjType.add(cm.leadObjType);
        desiredObjType.addAll(cm.revObjTypes);

        for (OcelObject ocelObject : ocel.objectTypes.get(leadObjType).objects) {

            evtDfMap = new HashMap<>();
            tempObjToRelatedEventsEdge = new HashMap<>();
            // get all the supplementary obj that should be considered
            HashSet<OcelObject> secObjSet = getSupplementaryObj(ocelObject, revObjectTypes);

            HashSet<OcelEvent> relatedEvents = new HashSet<>();

            // -----------------------for leading obj type--------------------------------
            // add the events related to the primary objects
            relatedEvents.addAll(ocelObject.sortedRelatedEvents);
            int count = 0;

            // add event to the process execution, key is event, value is the list of direct follow events
            while (count < ocelObject.sortedRelatedEvents.size()-1) {
                evtDfMap.put(ocelObject.sortedRelatedEvents.get(count), new HashMap<>());

                Set<OcelObject> objBeforeSet = ocelObject.sortedRelatedEvents.get(count).relatedObjects;
                Set<OcelObject> objCurrentSet = ocelObject.sortedRelatedEvents.get(count + 1).relatedObjects;
                HashSet<OcelObject> resSet = new HashSet<>();
                resSet.addAll(objBeforeSet);
                resSet.retainAll(objCurrentSet);

                HashSet<OcelObject> objSetForDf = new HashSet<>();
                // remove objects not belong to the desire primary and second type
                objSetForDf.add(ocelObject);


                // add to df event
                evtDfMap.get(ocelObject.sortedRelatedEvents.get(count)).put(
                        ocelObject.sortedRelatedEvents.get(count + 1),
                        objSetForDf);
                count += 1;
            }
            if(ocelObject.sortedRelatedEvents.size()>0) {
                evtDfMap.put(
                        ocelObject.sortedRelatedEvents.get(ocelObject.sortedRelatedEvents.size() - 1),
                        new HashMap<>());
            }
            else{
                break;
            }

            //------get the direct before map for------

            // -----------------------for leading obj type--------------------------------

            // -----------------------for supplementary obj type--------------------------------

            // iterate each obj in supplementary objects set
            for (OcelObject secObj : secObjSet) {
                // use evtListForObj to store all suc evt
                ArrayList<ArrayList<OcelEvent>> arrayListForObj = new ArrayList<>();
                ArrayList<OcelEvent> aEvt = new ArrayList<>();
                boolean flag;

                for (OcelEvent evt : secObj.sortedRelatedEvents) {

                    // use flag to indicate that the event is ok to use
                    flag = true;

                    for (OcelObject obj : evt.relatedObjects) {
                        // if there is another leading type object
                        if (obj.objectType.name.equals(cm.leadObjType) &&
                            !obj.equals(ocelObject) &&
                            !evt.relatedObjects.contains(ocelObject)) {
                            flag = false;
                        }
                    }

                    // if there is no other object of leading type, add evt to aEvt
                    if (flag) {
                        aEvt.add(evt);
                    }
                    // if there are other object of leading type, add evt to aEvt
                }
                if (aEvt.size()>0) {
                    arrayListForObj.add(aEvt);
                }

                int aEvtIdx = 0;
                // iterate each event in the evtListForObj
                while (aEvtIdx < arrayListForObj.size()) {
                    ArrayList<OcelEvent> subListForObj = arrayListForObj.get(aEvtIdx);

                    // check if conjunction of objects exist
                    Set<OcelEvent> firstEvtSet = new HashSet<OcelEvent>(subListForObj);
                    Set<OcelEvent> secondEvtSet = evtDfMap.keySet();
                    HashSet<OcelEvent> conjSet = new HashSet<>();
                    conjSet.addAll(firstEvtSet);
                    conjSet.retainAll(secondEvtSet);
                    if (conjSet.size() > 0) {
                        count = 0;
                        while (count < subListForObj.size()-1) {
                            // if the evt is already in evtdfMap
                            if (evtDfMap.containsKey(subListForObj.get(count))) {

                                // add the succ event if it is not in evtDfMap
                                if (!evtDfMap.get(subListForObj.get(count)).containsKey(subListForObj.get(count + 1))) {
//                                    Set<OcelObject> objBeforeSet = subListForObj.get(count).relatedObjects;
//                                    Set<OcelObject> objCurrentSet = subListForObj.get(count + 1).relatedObjects;
//                                    HashSet<OcelObject> resSet = new HashSet<>();
//                                    resSet.addAll(objBeforeSet);
//                                    resSet.retainAll(objCurrentSet);
                                    HashSet<OcelObject> objSetForDf = new HashSet<>();
                                    objSetForDf.add(secObj);
                                    evtDfMap.get(subListForObj.get(count)).put(
                                                subListForObj.get(count + 1),
                                                objSetForDf);
                                }
                                // if the succ event is already in the evtDfMap, add the obj
                                else{
                                    evtDfMap.get(subListForObj.get(count)).
                                            get(subListForObj.get(count + 1)).add(secObj);
                                }
                            }

                            //if the event is not in evtDfMap
                            else {
                                // put the event in evtDfMap
                                evtDfMap.put(subListForObj.get(count), new HashMap<>());
//                                Set<OcelObject> objBeforeSet = subListForObj.get(count).relatedObjects;
//                                Set<OcelObject> objCurrentSet = subListForObj.get(count + 1).relatedObjects;
//                                objBeforeSet.retainAll(objCurrentSet);
                                // add ocel objects to objSetForDf
                                HashSet<OcelObject> objSetForDf = new HashSet<>();
                                objSetForDf.add(secObj);
                                // add to df event
                                evtDfMap.get(subListForObj.get(count)).put(
                                        subListForObj.get(count + 1),
                                        objSetForDf);
                            }
                            count += 1;
                        }
                        relatedEvents.addAll(arrayListForObj.get(aEvtIdx));
                        tempObjToRelatedEventsEdge.put(secObj, arrayListForObj.get(aEvtIdx));
                    }
                    aEvtIdx += 1;
                }
            }
            // peMap stores the process execution
            peMap.put(ocelObject.id, evtDfMap);
            tempObjToRelatedEventsEdge.put(ocelObject, ocelObject.sortedRelatedEvents);
            objToRelatedEventsEdge.put(ocelObject.id, tempObjToRelatedEventsEdge);
            objToRelatedEventsNode.put(ocelObject.id, relatedEvents);
            leadObjMap.put(ocelObject.id, secObjSet);
        }

        // build graph from the event set
        List<Map> lstMap = new ArrayList<>();
        lstMap.add(leadObjMap);
        lstMap.add(objToRelatedEventsNode);
        lstMap.add(objToRelatedEventsEdge);
        lstMap.add(peMap);
        return lstMap;
    }

    public Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> reversePeMap(Map<String, HashMap<OcelEvent, HashMap<OcelEvent, HashSet<OcelObject>>>> peMap) {

        HashMap<OcelEvent, HashMap<OcelEvent, HashSet<OcelObject>>> directBeforeMap;

        Map<String, HashMap<OcelEvent, HashMap<OcelEvent,HashSet<OcelObject>>>> peDirectBeforeMap= new HashMap<>();

        // iterate every process execution
        for (String key : peMap.keySet()) {

            // get all the events
            Set<OcelEvent> evtMap = peMap.get(key).keySet();

            // save the event and the set of events before
            directBeforeMap = new HashMap<>();

//            HashMap<OcelEvent, HashSet<OcelObject>> tempBeforeMap = new HashMap<>();

            // iterate every event in the evtMap
            for (OcelEvent evt : evtMap) {

                HashMap<OcelEvent, HashMap<OcelEvent, HashSet<OcelObject>>> processExecution = peMap.get(key);

                // for each evt, get the direct after hashset of events
                HashMap<OcelEvent, HashSet<OcelObject>> directAfterMap = processExecution.get(evt);

                for (OcelEvent aftEvt : directAfterMap.keySet()) {

//                    tempBeforeMap = new HashMap<>();
//                    tempBeforeMap.put(evt, directAfterMap.get(aftEvt));
                    directBeforeMap.get(aftEvt).put(evt, directAfterMap.get(aftEvt));
                }
            }
            peDirectBeforeMap.put(key,directBeforeMap);
        }
        return peDirectBeforeMap;
    }

    public static HashSet<OcelObject> getSupplementaryObj(OcelObject obj,
                                                          HashSet<String> objTypeSet){
        // obj to consider for the obj
        HashSet<OcelObject> objSet = new HashSet<>();
        // get the distance of object type
        HashMap<OcelObjectType, Integer> objTypeDistMap = new HashMap();
        HashMap<OcelObject,Integer> objDistMap = new HashMap();
        Set<OcelObject> objVisited = new HashSet<>();

        objDistMap.put(obj,0);
        Queue<OcelObject> queueForObjs = new LinkedList<>();
        queueForObjs.add(obj);

        while(queueForObjs.size()>0){
            OcelObject objFromQ = queueForObjs.remove();

            for (OcelEvent ocelEvent : objFromQ.sortedRelatedEvents) {

                // iterate related object list
                for(OcelObject eachObj : ocelEvent.relatedObjects) {
                    if (objVisited.contains(eachObj)){
                        continue;
                    }
                    else{
                        objVisited.add(eachObj);
                    }
                    // if the object is what we want
                    if (objTypeSet.contains(eachObj.objectType.name)) {
                        if (objTypeDistMap.containsKey(eachObj.objectType)) {
                            if (objDistMap.get(objFromQ) + 1 <= objTypeDistMap.get(eachObj.objectType)) {
                                objSet.add(eachObj);
                                queueForObjs.add(obj);
                            }
                        } else {
                            objTypeDistMap.put(eachObj.objectType, objDistMap.get(objFromQ) + 1);
                            objSet.add(eachObj);
                            queueForObjs.add(obj);
                        }
                    }
                }
            }
        }
        return objSet;
    }
}

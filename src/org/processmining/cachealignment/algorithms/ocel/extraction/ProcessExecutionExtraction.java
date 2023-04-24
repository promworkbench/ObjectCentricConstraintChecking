//package org.processmining.ocel.extraction;
//
//
//import java.util.*;
//
//import javax.swing.JPanel;
//
//import org.processmining.contexts.uitopia.UIPluginContext;
//import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
//import org.processmining.framework.plugin.annotations.Plugin;
//import org.processmining.framework.plugin.annotations.PluginVariant;
//import org.processmining.framework.util.ui.wizard.ListWizard;
//import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
//import org.processmining.framework.util.ui.wizard.ProMWizardStep;
//import org.processmining.ocel.ocelobjects.*;
//
//@Plugin(name = "Extract Process Execution from OCEL",
//        returnLabels = { "Process Execution Extraction" },
//        returnTypes = { PEGraph.class },
//        parameterLabels = { "Object-Centric Event Log" },
//        help = "Object-Centric Event Log",
//        userAccessible = true)
//public class ProcessExecutionExtraction {
//
//    protected JPanel root;
//
//    @PluginVariant(requiredParameterLabels = {0})
//    @UITopiaVariant(
//            affiliation = "PADS RWTH",
//            author = "Tian",
//            email = "tian.li@pads.rwth-aachen.de")
//    public PEGraph extractProcessExecution(UIPluginContext context,
//                                            OcelEventLog ocel) throws InterruptedException {
//
//        ExtractionWizardStep wizStep = new ExtractionWizardStep(ocel);
//        List<ProMWizardStep<ExtractionWizardParameters>> wizStepList = new ArrayList<>();
//        wizStepList.add(wizStep);
//        ListWizard<ExtractionWizardParameters> listWizard = new ListWizard<>(wizStepList);
//        ExtractionWizardParameters parameters = ProMWizardDisplay.show(context, listWizard, new ExtractionWizardParameters());
//
//        List<Map> lstMap = extractWithLeadingType(ocel, parameters.leadObjType, parameters.revObjTypes);
//
//
//        PEGraph peGraph = new PEGraph(context,
//                lstMap.get(0),
//                lstMap.get(1),
//                lstMap.get(2),
//                parameters.leadObjType,
//                parameters.revObjTypes,
//                ocel);
//        return peGraph;
//    }
//
//    public static List<Map> extractWithLeadingType(OcelEventLog ocel, String leadObjType, HashSet<String> revObjectTypes) {
//
//        // leadObjMap key: one obj from lead, val: set of secondary obj
//        Map<String, HashSet<OcelObject>> leadObjMap = new HashMap<>();
//
//        // leadObjToEvtMap:
//        Map<String, Map<OcelObject, List<OcelEvent>>> objToRelatedEventsEdge = new HashMap<>();
//        Map<String, HashSet<OcelEvent>> objToRelatedEventsNode= new HashMap<>();
//        Map<OcelObject, List<OcelEvent>> tempobjToRelatedEventsEdge;
//
//
//        // iterate all ocelObject from the leading type
//        for (OcelObject ocelObject : ocel.objectTypes.get(leadObjType).objects) {
//
//            tempobjToRelatedEventsEdge = new HashMap<>();
//            HashSet<OcelObject> objSet = getSupplementaryObj(ocelObject,revObjectTypes);
//
//            HashSet<OcelEvent> relatedEvents = new HashSet<>();
//            // iterate each obj in objset
//            List<OcelEvent> evtListForObj;
//            for (OcelObject objInSet : objSet){
//                evtListForObj = new ArrayList<>();
//                for(OcelEvent evt: objInSet.sortedRelatedEvents) {
//                    boolean flag = true;
//                    for (OcelObject obj: evt.relatedObjects){
//                        if (obj.objectType.name.equals(leadObjType) && !evt.relatedObjects.contains(ocelObject)){
//                            flag = false;
//                        }
//                    }
//                    if (flag){
//                        evtListForObj.add(evt);
//                    }
//                }
//                relatedEvents.addAll(evtListForObj);
//                tempobjToRelatedEventsEdge.put(objInSet, evtListForObj);
//            }
//
//            relatedEvents.addAll(ocelObject.sortedRelatedEvents);
//            tempobjToRelatedEventsEdge.put(ocelObject, ocelObject.sortedRelatedEvents);
//            objToRelatedEventsEdge.put(ocelObject.id, tempobjToRelatedEventsEdge);
//            objToRelatedEventsNode.put(ocelObject.id, relatedEvents);
//            leadObjMap.put(ocelObject.id, objSet);
//        }
//
//        // build graph from the event set
//        List<Map> lstMap = new ArrayList<>();
//        lstMap.add(leadObjMap);
//        lstMap.add(objToRelatedEventsNode);
//        lstMap.add(objToRelatedEventsEdge);
//        return lstMap;
//    }
//
//    // use a recursive function to get all the supplementary objetcs
//    public static HashSet<OcelObject> getSupplementaryObj(OcelObject obj,
//                                                       Set<String> objTypeSet){
//        // obj to consider for the obj
//        HashSet<OcelObject> objSet = new HashSet<>();
//        // get the distance of object type
//        HashMap <OcelObjectType, Integer> objTypeDistMap = new HashMap();
//        HashMap<OcelObject,Integer> objDistMap = new HashMap();
//        Set<OcelObject> objVisited = new HashSet<>();
//
//        objDistMap.put(obj,0);
//        Queue<OcelObject> queueForObjs = new LinkedList<>();
//        queueForObjs.add(obj);
//
//        while(queueForObjs.size()>0){
//            OcelObject objFromQ = queueForObjs.remove();
//
//            for (OcelEvent ocelEvent : objFromQ.sortedRelatedEvents) {
//
//                // iterate related object list
//                for(OcelObject eachObj : ocelEvent.relatedObjects) {
//                    if (objVisited.contains(eachObj)){
//                        continue;
//                    }
//                    else{
//                        objVisited.add(eachObj);
//                    }
//                    // if the object is what we want
//                    if (objTypeSet.contains(eachObj.objectType.name)) {
//                        if (objTypeDistMap.containsKey(eachObj.objectType)) {
//                            if (objDistMap.get(objFromQ) + 1 <= objTypeDistMap.get(eachObj.objectType)) {
//                                objSet.add(eachObj);
//                                queueForObjs.add(obj);
//                            }
//                        } else {
//                            objTypeDistMap.put(eachObj.objectType, objDistMap.get(objFromQ) + 1);
//                            objSet.add(eachObj);
//                            queueForObjs.add(obj);
//                        }
//                    }
//                }
//            }
//        }
//        return objSet;
//    }
//
//}
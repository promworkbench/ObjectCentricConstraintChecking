package org.processmining.cachealignment.algorithms.ocel.constraint;

import java.util.*;

import javax.swing.JPanel;

import org.processmining.cachealignment.algorithms.ocel.extraction.ExtractionWizardParameters;
import org.processmining.cachealignment.algorithms.ocel.extraction.ExtractionWizardStep;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEvent;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEventLog;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelObject;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelObjectType;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.wizard.ListWizard;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.*;

@Plugin(name = "OCCL Constraints Monitor",
returnLabels = { "OCCM Editor" },
returnTypes = { OCCMEditor.class },
parameterLabels = { "Object-Centric Event Log" },
help = "Object-Centric Event Log",
userAccessible = true)
public class ConstraintEditor {

	ConstraintModel cm = ConstraintModel.getInstance();

	protected JPanel root;

	@PluginVariant(requiredParameterLabels = {0})
	@UITopiaVariant(
			affiliation = "PADS RWTH",
			author = "Tian",
			email = "tian.li@rwth-aachen.de")
	public OCCMEditor getConstraintFromOCEL(UIPluginContext context,
											OcelEventLog ocel) throws InterruptedException {

		ExtractionWizardStep wizStep = new ExtractionWizardStep(ocel);
		List<ProMWizardStep<ExtractionWizardParameters>> wizStepList = new ArrayList<>();
		wizStepList.add(wizStep);
		ListWizard<ExtractionWizardParameters> listWizard = new ListWizard<>(wizStepList);
		ExtractionWizardParameters parameters = ProMWizardDisplay.show(context, listWizard, new ExtractionWizardParameters());
		ConstraintModel cm = ConstraintModel.getInstance();
		ArrayList<String> objTypeList = new ArrayList<>();
		objTypeList.add(parameters.leadObjType);
		for(String revObjType: parameters.revObjTypes){
			objTypeList.add(revObjType);
		}
		cm.objTypeToConsider = objTypeList;
		cm.leadObjType = parameters.leadObjType;
		cm.revObjTypes =  parameters.revObjTypes;
		List<Map> lstMap = extractCaseWithLeadingType(ocel, parameters.leadObjType, parameters.revObjTypes);
		HashSet<String> actList = new HashSet<>();

		for (OcelEvent eachAct: ocel.events.values()) {
			actList.add(eachAct.activity);
		}

		cm.setOCEL(ocel);
		cm.setObjTypeList(objTypeList);
		OCCMEditor ge = new OCCMEditor(
				context,
				lstMap.get(0),
				lstMap.get(3),
				ocel,
				objTypeList,
				actList);
		return ge;
	}
	
	public Set<String> getActivityList(OcelEventLog ocel) {
		Set<String> activityLst =  new HashSet<>();
		for (OcelEvent evt : ocel.events.values()) {
			activityLst.add(evt.activity);
		}
		return activityLst;
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
//	public static List<Map> extractWithLeadingType(OcelEventLog ocel, String leadObjType, HashSet<String> revObjectTypes) {
//
//		// leadObjMap key: one obj from lead, val: set of secondary obj
//		Map<String, HashSet<OcelObject>> leadObjMap = new HashMap<>();
//
//		// leadObjToEvtMap:
//		Map<String, Map<OcelObject, List<OcelEvent>>> objToRelatedEventsEdge = new HashMap<>();
//		Map<String, HashSet<OcelEvent>> objToRelatedEventsNode= new HashMap<>();
//		Map<OcelObject, List<OcelEvent>> tempobjToRelatedEventsEdge;
//
//
//		// iterate all ocelObject from the leading type
//		for (OcelObject ocelObject : ocel.objectTypes.get(leadObjType).objects) {
//			tempobjToRelatedEventsEdge = new HashMap<>();
//			// store sorted list of evt for each obj
//			HashSet<OcelObject> objSet = new HashSet<>();
//
//			// iterate all event related to this primary object
//			for (OcelEvent ocelEvent : ocelObject.sortedRelatedEvents) {
//				for(OcelObject eachObj : ocelEvent.relatedObjects){
//					if (revObjectTypes.contains(eachObj.objectType.name)) {
//						objSet.add(eachObj);
//					}
//				}
//			}
//
//			HashSet<OcelEvent> relatedEvents = new HashSet<>();
//			// iterate each obj in objSet
//			List<OcelEvent> evtListForObj;
//			for (OcelObject objInSet : objSet){
//				evtListForObj = new ArrayList<>();
//				for(OcelEvent evt: objInSet.sortedRelatedEvents) {
//					boolean flag = true;
//					for (OcelObject obj: evt.relatedObjects){
//						if (obj.objectType.name.equals(leadObjType) && !evt.relatedObjects.contains(ocelObject)){
//							flag = false;
//						}
//					}
//					if (flag){
//						evtListForObj.add(evt);
//					}
//				}
//				relatedEvents.addAll(evtListForObj);
//				tempobjToRelatedEventsEdge.put(objInSet, evtListForObj);
//			}
//
//			relatedEvents.addAll(ocelObject.sortedRelatedEvents);
//			tempobjToRelatedEventsEdge.put(ocelObject, ocelObject.sortedRelatedEvents);
//			objToRelatedEventsEdge.put(ocelObject.id, tempobjToRelatedEventsEdge);
//			objToRelatedEventsNode.put(ocelObject.id, relatedEvents);
//			leadObjMap.put(ocelObject.id, objSet);
//		}
//
//		// build graph from the event set
//		List<Map> lstMap = new ArrayList<>();
//		lstMap.add(leadObjMap);
//		lstMap.add(objToRelatedEventsNode);
//		lstMap.add(objToRelatedEventsEdge);
//		return lstMap;
//	}


}
 
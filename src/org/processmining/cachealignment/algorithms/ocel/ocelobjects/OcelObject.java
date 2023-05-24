package org.processmining.cachealignment.algorithms.ocel.ocelobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OcelObject {
	public OcelEventLog eventLog;
	public String id;
	public OcelObjectType objectType;
	public Set<OcelEvent> relatedEvents;
	public List<OcelEvent> sortedRelatedEvents;
	public Map<String, Object> attributes;
	
	public OcelObject(OcelEventLog eventLog) {
		this.eventLog = eventLog;
		this.relatedEvents = new HashSet<OcelEvent>();
		this.attributes = new HashMap<String, Object>();
		this.sortedRelatedEvents = new ArrayList<OcelEvent>();
	}
	
	public void register() {
		this.objectType.objects.add(this);
		this.sortEvents();
	}
	
	public void sortEvents() {
		this.sortedRelatedEvents = new ArrayList<OcelEvent>(relatedEvents);
		Collections.sort(this.sortedRelatedEvents, new OcelEventComparator());
		
	}
	
	public void printRelatedEvents() {
		StringBuilder ret = new StringBuilder();
		ret.append("\nO=");
		ret.append(this.id);
		ret.append("\nA=[");
		
		for (OcelEvent eve : sortedRelatedEvents) {
			ret.append(eve.activity);
			ret.append(", ");
		}
		ret.append("]");
		ret.append("\nE=[");
		
		for (OcelEvent eve : sortedRelatedEvents) {
			ret.append(eve.id);
			ret.append(", ");
		}
		ret.append("]");

		ret.append("\nT=[");
		for (OcelEvent eve : sortedRelatedEvents) {
			ret.append(eve.timestamp.toString());
			ret.append(", ");
		}
		ret.append("]");
		
		System.out.println(ret.toString());
	}
}

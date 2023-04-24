package org.processmining.cachealignment.algorithms.ocel.ocelobjects;

import java.util.Comparator;

public class OcelEventComparator implements Comparator<OcelEvent> {

	public int compare(OcelEvent o1, OcelEvent o2) {
		// TODO Auto-generated method stub
		if (o1.timestamp.getTime() < o2.timestamp.getTime()) {
			return -1;
		}
		else if (o1.timestamp.getTime() > o2.timestamp.getTime()) {
			return 1;
		}
		return o1.id.compareTo(o2.id);
	}

}

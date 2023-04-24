package org.processmining.cachealignment.algorithms.ocel.constraint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.ParseException;
import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEvent;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEventComparator;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelEventLog;
import org.processmining.cachealignment.algorithms.ocel.ocelobjects.OcelObject;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;


@Plugin(name = "Visualize violated rules in table",
        returnLabels = { "Violation Table" },
        returnTypes = { JComponent.class },
        parameterLabels = { "Violations in Object-Centric Event Log" },
        userAccessible = true)
@Visualizer
public class ViolationTable{
    protected JPanel root;

    @PluginVariant(requiredParameterLabels = {0})
    public JComponent visualizeRules(UIPluginContext context, ViolatedSet v1) throws ParseException {

        JFrame frame = new JFrame();
        root = new JPanel();
        root.setBorder(BorderFactory.createEmptyBorder());
        root.setBackground(new Color(100, 100, 100));
        root.setLayout(new BorderLayout());

        Object [] title = {"Case  Id","Event Id", "Activity", "Diagnosis"};
        Vector titlesV = new Vector(); // save title
        Vector<Vector> dataV = new Vector<Vector>(); // save data

        for (int i=0; i<title.length;i++){
            titlesV.add(title[i]);
        }
        int count = 0;
        while (v1.violatedRules.size()>count) {
            Vector<Object> t = new Vector<Object>();
            t.add(v1.violatedRules.get(count).get(0));
            t.add(v1.violatedRules.get(count).get(1));
            t.add(v1.violatedRules.get(count).get(2));
            t.add(v1.violatedRules.get(count).get(3));
            dataV.add(t);
            count += 1;
        }

        TableModel model = new DefaultTableModel(dataV, titlesV);
        JTable table = new JTable(model);
        TableColumn col0 = table.getColumn(title[0]);
        col0.setMaxWidth(120);
        TableColumn col1 = table.getColumn(title[1]);
        col1.setMinWidth(150);
        col1.setMaxWidth(150);
        TableColumn col2 = table.getColumn(title[2]);
        col2.setMinWidth(120);
        col2.setMaxWidth(120);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
        table.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        JScrollPane jsp = new JScrollPane(table);
        root.add(jsp);
        return root;
    }


    public List<Map> extractWithLeadingType(OcelEventLog ocel, String activity, String objectType) {

        // sort events based on time
        List<OcelEvent> evtlist = new ArrayList<>(ocel.events.values());
        evtlist.sort(new OcelEventComparator());
        String targetAct = activity;
        Map<String, Set<OcelObject>> evtObjMap = new HashMap<>();
        Map<Integer, String> idMap = new HashMap();
        Map<String, Map> processExecutionIdMap = new HashMap();
        Map<String, Map> processExecutionActMap = new HashMap();
        Map<String, Map> processExecutionEdgeMap = new HashMap();
        Map<String, ArrayList<ArrayList<Integer>>> currentEdgeMap;

        int peCount = 0;
        boolean objTypeExistFlag;

        // iterate each event in the Ocel
        for (OcelEvent evt : evtlist) {
            // if the evt refers to the activity we set
            if (evt.activity.equals(targetAct)) {
                objTypeExistFlag = false;
                HashSet<OcelObject> objSet = new HashSet<>();

                // first check if obj of type item is in this event
                for (OcelObject obj : evt.relatedObjects) {
                    if (obj.objectType.name.equals(objectType)) {
                        objTypeExistFlag = true;
                        objSet.add(obj);
                    }
                }

                // if obj exist
                if (objTypeExistFlag) {

                    // save obj to this event
                    evtObjMap.put(evt.id, objSet);

                    // save edge list
                    currentEdgeMap= new HashMap<>();

                    int evtNum = 0;
                    Map<String, Integer> evtIdMap = new HashMap();
                    Map<String, String> evtActMap = new HashMap();

                    for (OcelObject ocelObject : objSet) {
                        // We can get the list of events related to this obj
                        int idx = 0;
                        // Start from the activity twe define as precondition
                        for (OcelEvent oclEvt: ocelObject.sortedRelatedEvents){
                            if (!oclEvt.equals(evt)){
                                idx += 1;
                            }
                            else{
                                break;
                            }
                        }
                        // get the part of event list that use targetAct as starting point
                        List<OcelEvent> trimRelatedEvents = ocelObject.sortedRelatedEvents.subList(
                                idx, ocelObject.sortedRelatedEvents.size());

                        for (int j=0; j<trimRelatedEvents.size(); j++){
                            // get evet id
                            String evtId = trimRelatedEvents.get(j).id;
                            if (!evtIdMap.containsKey(evtId)){
                                evtIdMap.put(evtId, evtNum);
                                evtActMap.put(evtId, trimRelatedEvents.get(j).activity);
                                evtNum += 1;
                            }
                        }
                        ArrayList<Integer> tempEdgeLst;
                        ArrayList<ArrayList<Integer>> edgeLst = new ArrayList<>();

                        for (int k=0;k<trimRelatedEvents.size()-1;k++){
                            // get the connection between two subsequent event
                            tempEdgeLst = new ArrayList<>();
                            tempEdgeLst.add(evtIdMap.get(trimRelatedEvents.get(k).id));
                            tempEdgeLst.add(evtIdMap.get(trimRelatedEvents.get(k+1).id));
                            edgeLst.add(tempEdgeLst);
                        }
                        currentEdgeMap.put(ocelObject.id, edgeLst);
                    }

                    processExecutionIdMap.put(Integer.toString(peCount), evtIdMap);
                    processExecutionActMap.put(Integer.toString(peCount), evtActMap);
                    processExecutionEdgeMap.put(Integer.toString(peCount), currentEdgeMap);
                    idMap.put(peCount, evt.id);
                    peCount += 1;
                }
            }
        }
        Map<String, Map> map1 = new HashMap<>();
        map1.put("processExeMap1",processExecutionIdMap);
        Map<String, Map> map2 = new HashMap<>();
        map2.put("processExeMap2",processExecutionActMap);
        Map<String, Map> map3 = new HashMap<>();
        map3.put("edgeMap",processExecutionEdgeMap);
        Map<String, Map> map4 = new HashMap<>();
        map4.put("idMap",idMap);

        List<Map> lstMap = new ArrayList<>();
        lstMap.add(map1);
        lstMap.add(map2);
        lstMap.add(map3);
        lstMap.add(map4);

        return lstMap;
    }


}

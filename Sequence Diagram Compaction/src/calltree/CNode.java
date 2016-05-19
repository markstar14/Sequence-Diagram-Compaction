/*
 * Aum Amriteswaryai Namah
 *
 * File: CNode.java
 * Description: Defines node of a compacted call tree
 *
 */

package calltree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ConcurrentModificationException;

import regex.*;
import utilities.Util;

public class CNode extends Node {

	boolean isCompacted;
	String label;
	String encoding;

	LinkedHashMap<String,Character> map = new LinkedHashMap<String,Character>();
	LinkedHashMap<Character,String> rMap = new LinkedHashMap<Character,String>();
	ArrayList<CNode> removedChildren = new ArrayList<CNode>();

	CNode() {}

	CNode(String o, String m, int s, int e, boolean c,
						String co, String cm, PlantUML p) {
		
		object = o;
		method = m;
		startEvent = s;
		endEvent = e;
		current = c;
		
		callerObject = co;
		callerMethod = cm;

		isCompacted = false;
		label = null;
		encoding = null;
		pl = p;
	}

	CNode(String o, String m, int s, int e, boolean c,
						String co, String cm, boolean isComp, PlantUML p) {
		
		object = o;
		method = m;
		startEvent = s;
		endEvent = e;
		current = c;
		
		callerObject = co;
		callerMethod = cm;

		isCompacted = isComp;
		label = null;
		encoding = null;
		pl = p;
	}

	String getCallerObject(){
		return callerObject;
	}

	String getCallerMethod(){
		return callerMethod;
	}

	void printNode() {

		System.out.print(object + "," + method 
							+ "," + startEvent 
							+ "-" + endEvent
							+ "," + isCompacted);
		System.out.println();
	}

	void setCompact(boolean isComp) {
		isCompacted = isComp;
	}

	boolean getCompact() {
		return isCompacted;
	}

	void setEncoding(String e) {
		encoding = e;
	}

	String getEncoding() {
		return encoding;
	}
	
	int countNodes(int nodeCount) {
		nodeCount++;
		if ( !(Util.cLifelines.contains(object)) && !(object.contains(";")) )
			Util.cLifelines.add(object);
		
		Iterator itr = children.iterator();
		while ( itr.hasNext() ) {
			CNode c = (CNode) itr.next();
			nodeCount = c.countNodes(nodeCount);
		}
		return nodeCount;
	}

	void compact(PlantUML p) {

		String call;
		String sequence = "";

		Iterator itr = children.iterator();
		while ( itr.hasNext() ) {
			CNode c = (CNode) itr.next();

			// Invoke compact on children node (for bottom-up)
			c.compact(p);

			// Strip ':n' from method call
			String mtd = Util.stripColon(c.getMethod());

			// Concat object and method
			call = new String(c.getObject() + "." + mtd);

			// Assign unique symbol (hash map) & append to the sequence
			sequence = new String(sequence + updateMap(call));
		}
		//printMap();

		// Find Minimal regex (to return array of [i,alpha,k])
		MinimalRegex mr = new MinimalRegex();
		TandemRepeat[] labels = mr.findMinimalRegex(sequence);
		/*for (int i=0; i<labels.length; i++) 
			labels[i].printTandemRepeat();*/

		// Reverse the map to lookup by symbol
		reverseMap();
		//printRMap();

		// Replace tandem repeat symbols with object-method
		CallRepeat[] callLabels = replaceTRwithOM(labels);
		/*for (int i=0; i<callLabels.length; i++) 
			callLabels[i].printCallRepeat();*/

		// Replace labeled nodes with a singe compacted node
		replaceChildren(callLabels, p);

	}

	char updateMap(String call) {

		if ( ! (map.containsKey(call)) )
			map.put(call,Util.getNextSymbol());

		return map.get(call);
	}

	void reverseMap() {
		
		for (Map.Entry<String,Character> entry : map.entrySet()) {
  			String key = entry.getKey();
  			char value = entry.getValue();
			rMap.put(value,key);
		}
	}

	CallRepeat[] replaceTRwithOM(TandemRepeat[] labels) {

		CallRepeat[] cr = new CallRepeat[labels.length];

		for (int i=0; i<labels.length; i++) {
			StringBuffer cSeq = new StringBuffer();
			TandemRepeat tr = labels[i];

			if ( tr.getAlpha().length() > 1 ) {
				cSeq.append("(");
				for (int j=0; j<tr.getAlpha().length(); j++) {
					String objmtd = rMap.get(tr.getAlpha().charAt(j));
					cSeq.append(objmtd);
					cSeq.append(";");
				}
				cSeq.append(")");
				cr[i] = new CallRepeat(tr.getStartPos(), cSeq.toString(), 
									tr.getAlpha().length(), tr.getRepeats());
			}
			else {
				String objmtd = rMap.get(tr.getAlpha().charAt(0));
				cSeq.append(objmtd);
				cr[i] = new CallRepeat(tr.getStartPos(), cSeq.toString(),
										 1, tr.getRepeats());
			}
		}
		return cr;
	}

	void replaceChildren(CallRepeat[] cr, PlantUML p) {


		for (int i=cr.length-1; i>=0; i--) {
			//cr[i].printCallRepeat();
			int pos = cr[i].getStartPos();
			String objmtd = cr[i].getObjMtd();
			int omlen = cr[i].getOMLen();
			int repeats = cr[i].getRepeats();

			if ( omlen * repeats == 1 ) {	// do nothing
				//((CNode)children.get(pos)).printTree();
			}
			else if ( (omlen == 1) && (repeats > 1) ) {
				// Remove children from index 'pos' till 'pos+repeats-1'
				// And insert the new node at 'pos'
				CNode c = (CNode) children.get(pos);
				String o = c.getObject();
				String m = c.getMethod() + "^" + repeats;
				int se = c.getStartEvent();
				String co = c.getCallerObject();
				String cm = c.getCallerMethod();

				c = (CNode) children.get(pos+repeats-1);
				int ee = c.getEndEvent();

				CNode cn = new CNode(o, m, se, ee, false, co, cm, true, p);
				for (int j=pos+repeats-1; j>=pos; j--) {// remove from the back
					CNode rm = (CNode) children.remove(j);
					removedChildren.add(0,rm);
				}
				p.rmc.put(m.toString(),removedChildren);
				//for (int k=0; k<removedChildren.size(); k++)
					//removedChildren.get(k).printNode();
				children.add(pos,cn);
				//((CNode)children.get(pos)).printNode();
			}
			else {
				// Remove children from index 'pos' till 'pos+omlen*repeats-1'
				// And insert the new node at 'pos'
				CNode c = (CNode) children.get(pos);
				StringBuffer o = new StringBuffer();
				StringBuffer m = new StringBuffer();  // Modified below to store concatenated object.method.sev.eev
				StringBuffer m2 = new StringBuffer(); // Just stores the concatenated method names. Needed to update rmc properly
				StringBuffer co = new StringBuffer();
				StringBuffer cm = new StringBuffer();
				int se = -1;
				int ee = -1;

				m.append("(");
				m2.append("(");
				for (int j=pos; j<pos+omlen; j++) {
					c = (CNode) children.get(j);
					o.append(c.getObject() + ";");
					m.append(c.getObject() + "?" + c.getMethod() + "?" + c.getStartEvent() + "?" + c.getEndEvent() + ";");
					m2.append(c.getMethod() + ";");	// Note: m2 holds just the method names - for rmc
					if (j == pos) {
						se = c.getStartEvent();
						co.append(c.getCallerObject()); // caller remains the same
						cm.append(c.getCallerMethod());
					}
				}

				c = (CNode) children.get(pos + (omlen*repeats-1));
				ee = c.getEndEvent();
				m.append(")^");
				m.append(repeats);
				m2.append(")^");	// Do whatever is done for m to m2 too - for rmc
				m2.append(repeats);	

				CNode cn = new CNode(o.toString(), m.toString(), se, ee, 
								false, co.toString(), cm.toString(), true, p);

				for (int j=pos+(omlen*repeats-1); j>=pos; j--) { // remove from the back
					CNode rm = (CNode) children.remove(j);
					removedChildren.add(0,rm);	// store them as part of removedChildren
				}
				p.rmc.put(m2.toString(),removedChildren);	// Note: m changed to m2
				//System.out.println("rmc.put(m) : " + m + " : ");
				//System.out.println("rmc.put(m2) : " + m2 + " : ");

				//for (int k=0; k<removedChildren.size(); k++)
					//removedChildren.get(k).printNode();
				children.add(pos,cn);	// Add the compacted node to children
				//((CNode)children.get(pos)).printNode();
			}
		}
	}

	void dfsC(String color, int startCount, int endCount) {

		// Check if objects are a group

		if ( method.contains("^") ) {

			String objects[] = object.split(";");

			if ( Util.countDistinctObjects(objects) == 1 ) {
				// Delete from the right up to ' )' (i.e. )^NUM)
				int pos = method.indexOf('^');
				int times = Integer.parseInt(method.substring(pos+1));
				String tmethod = null;
				
				if (method.startsWith("("))	{ // Deals with the case (o.m1;o.m2;o.m3)^n
					tmethod = method.substring(1, pos-1);
					//System.out.println("tmethod, times =  " + tmethod + ", " + times);
					StringBuffer regex = new StringBuffer("(");
					StringTokenizer st = new StringTokenizer(tmethod, ";");
					if ( ((startEvent > startCount) && (endEvent < endCount)) 
						|| ((startEvent > startCount) && (startEvent < endCount))
							|| ((endEvent > startCount) && (endEvent < endCount)) 
						) {
						
						while (st.hasMoreTokens()) {
							StringTokenizer ste = new StringTokenizer(st.nextToken(), "?");
							String obj = ste.nextToken();
							String mtd = ste.nextToken();
							int sev = Integer.parseInt(ste.nextToken());
							int eev = Integer.parseInt(ste.nextToken());
							regex.append(mtd + ";");
						
							pl.tmc.put(sev, "\"" + callerObject + "\"" 
									+ " -> " + "\"" + objects[0] + "\"" 
									+ " : " + mtd + "     "   // Five white spaces are used to identify compacted events
									+ " \n" + "activate " + "\"" + objects[0] + "\"" 
									+ " " + color);

							pl.tmc.put(eev, "\"" + callerObject + "\"" 
									//+ " : " + "\"" + callerMethod + "\"" 
									+ " <-- " + "\"" + objects[0] + "\"" 
									+ "     " 	// Five white spaces are used to identify compacted events 
									+ " \n" + "deactivate " + "\"" + objects[0] + "\"" );
						}
						regex.append(")^" + times);
						//System.out.println("regex = " + regex);
						pl.tmc.put(startEvent-1, "loop " + regex);
						pl.tmc.put(endEvent+1, "end");
					}
				}
				else {					// Deals with the case o.m^n
					if ( ((startEvent > startCount) && (endEvent < endCount)) 
							|| ((startEvent > startCount) && (startEvent < endCount))
							|| ((endEvent > startCount) && (endEvent < endCount)) 
						) {
						tmethod = method.substring(0, pos);
						//System.out.println("tmethod, times =  " + tmethod + ", " + times);
						pl.tmc.put(startEvent-1, "loop " + method);
						pl.tmc.put(startEvent, "\"" + callerObject + "\"" 
								+ " -> " + "\"" + objects[0] + "\"" 
								+ " : " + tmethod + "     "	// Five white spaces are used to identify compacted events
								+ " \n" + "activate " + "\"" + objects[0] + "\"" 
								+ " " + color);

						pl.tmc.put(endEvent, "\"" + callerObject + "\"" 
								//+ " : " + "\"" + callerMethod + "\"" 
								+ " <-- " + "\"" + objects[0] + "\"" 
								+ "     " // Five white spaces are used to identify compacted events
								+ " \n" + "deactivate " + "\"" + objects[0] + "\"" );
						pl.tmc.put(endEvent+1, "end");
					}
				}
			}
			else {		// Deals with the case (o1.m1;o2.m2;o3.m3)^n
				int pos = method.indexOf(')');
				int times = Integer.parseInt(method.substring(pos+2));
				// Remove from the right up to ' )' (i.e. ")^NUM")
				String tmethod = method.substring(1, pos);  // Delete the leading '(' too
				//System.out.println("tmethod, times =  " + tmethod + ", " + times);
				StringBuffer regex = new StringBuffer("(");
				StringTokenizer st = new StringTokenizer(tmethod, ";");
				
				if ( ((startEvent > startCount) && (endEvent < endCount)) 
				|| ((startEvent > startCount) && (startEvent < endCount))
					|| ((endEvent > startCount) && (endEvent < endCount)) 
				) {

					while (st.hasMoreTokens()) {
						StringTokenizer ste = new StringTokenizer(st.nextToken(), "?");
						String obj = ste.nextToken();
						String mtd = ste.nextToken();
						int sev = Integer.parseInt(ste.nextToken());
						int eev = Integer.parseInt(ste.nextToken());
						regex.append(mtd + ";");
					
						pl.tmc.put(sev, "\"" + callerObject + "\"" 
								+ " -> " + "\"" + obj + "\"" 
								+ " : " + mtd + "     "	// Five white spaces are used to identify compacted events
								+ " \n" + "activate " + "\"" + obj + "\"" 
								+ " " + color);

						pl.tmc.put(eev, "\"" + callerObject + "\"" 
								//+ " : " + "\"" + callerMethod + "\"" 
								+ " <-- " + "\"" + obj + "\"" 
								+ "     " // Five white spaces are used to identify compacted events
								+ " \n" + "deactivate " + "\"" + obj + "\"" );
					}
					regex.append(")^" + times);
					//System.out.println("regex = " + regex);
					pl.tmc.put(startEvent-1, "loop " + regex);  // Change regex to remove object 
					pl.tmc.put(endEvent+1, "end");
				}
			}
		}
		else {		// Deals with non-compacted cases
			if ( ((startEvent > startCount) && (endEvent < endCount)) 
					|| ((startEvent > startCount) && (startEvent < endCount))
					|| ((endEvent > startCount) && (endEvent < endCount)) 
				) {
				pl.tmc.put(startEvent, "\"" + callerObject + "\"" 
							+ " -> " + "\"" + object + "\"" 
							+ " : " + method 
							+ " \n" + "activate " + "\"" + object + "\"" 
							+ " " + color);

				//if ( (endEvent > 2000000) )  // Put only when method finishes
					pl.tmc.put(endEvent, "\"" + callerObject + "\"" 
							//+ " : " + "\"" + callerMethod + "\"" 
							+ " <-- " + "\"" + object + "\"" 
							//+ " : " + "\"" + method + "\"" 
							+ " \n" + "deactivate " + "\"" + object + "\"" );
			}
			
		}

		Iterator itr = children.iterator();
		while ( itr.hasNext() ) {
			CNode c = (CNode) itr.next();
			c.dfsC(color, startCount, endCount);
		}
	}


	void printMap() {

		for (Map.Entry<String,Character> entry : map.entrySet()) {
  			String key = entry.getKey();
  			char value = entry.getValue();
  			System.out.println(key + " -> " + value);
		}
	}

	void printRMap() {

		for (Map.Entry<Character,String> entry : rMap.entrySet()) {
  			char key = entry.getKey();
  			String value = entry.getValue();
  			System.out.println(key + " -> " + value);
		}
	}

	void printTree() {
		String status = null;
		if ( isCompacted )
			status = "compacted";
		else
			status = "ordinary";

		System.out.println(object + "," + method 
								+ "," + startEvent 
								+ "-" + endEvent
								+ "," + current
								+ "," + callerObject 
								+ "," + callerMethod
								+ "," + status);

		Iterator itr = children.iterator();
		while ( itr.hasNext() ) {
			Node c = (Node) itr.next();
			c.printTree();
		}
	}
	
	void alterTree(TreeMap<String,String> instances) {
		String cObj, tObj;
		
		if ( callerObject.contains(":") )
			cObj = callerObject.substring(0, callerObject.indexOf(':'));
		else
			cObj = callerObject;
		if ( object.contains(":") )
			tObj = object.substring(0,object.indexOf(':'));
		else
			tObj = object;
		//System.out.println("cObj, tObj = " + cObj + ", " + tObj);
		
		if ( instances.containsKey(cObj) ) {
			callerMethod = callerObject + "#" + callerMethod;
			callerObject = instances.get(cObj);
		}
		if ( instances.containsKey(tObj) ) {
			method = object + "#" + method;
			object = instances.get(tObj);
		}
		//System.out.println(callerObject + "," + callerMethod + "," + object + "," + method);
		
		Iterator itr = children.iterator();
		while ( itr.hasNext() ) {
			CNode c = (CNode) itr.next();
			c.alterTree(instances);
		}	
	}

	void exportToQSDE() {

	}

	void label() {

	}

	void determineSpan() {

	}

}


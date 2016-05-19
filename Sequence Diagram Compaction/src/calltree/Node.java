/*
 * Aum Amriteswaryai Namah
 *
 * File: Node.java
 * Description: Define node of a call tree
 *
 */

package calltree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Set;

public class Node {

	String object;
	String method;
	int startEvent;
	int endEvent;
	boolean current;

	String callerObject;
	String callerMethod;

	Node parent;
	ArrayList<Node> children = new ArrayList<Node>();
	PlantUML pl;

	Node() {}

	Node(String o, String m, int s, int e, boolean c,
						String co, String cm, PlantUML p) {
		
		object = o;
		method = m;
		startEvent = s;
		endEvent = e;
		current = c;
		
		callerObject = co;
		callerMethod = cm;		
		
		pl = p;
	}

	// This method added for PlantUML
	void setCaller(String co, String cm) {
		callerObject = co;
		callerMethod = cm;
	}

	String getCallerObject(){
		return callerObject;
	}

	String getCallerMethod(){
		return callerMethod;
	}

	void printNode() {

		System.out.print(object + "," + method + ","
							+ startEvent + "-" + endEvent + ","
							+ callerObject + "," + callerMethod);
		System.out.println();
	}

	String getObject() {
		return object;
	}

	void setObject(String o) {
		object = o;
	}

	String getMethod() {
		return method;
	}

	void setMethod(String m) {
		method = m;
	}

	int getStartEvent() {
		return startEvent;
	}

	void setStartEvent(int s) {
		startEvent = s;
	}

	int getEndEvent() {
		return endEvent;
	}

	void setEndEvent(int e) {
		endEvent = e;
	}

	void addChild(Node c) {
		children.add(c);
		c.setParent(this);
	}

	void setParent(Node p) {
		parent = p;
	}

	Node getParent() {
		return parent;
	}

	boolean getCurrent() {
		return current;
	}

	void setCurrent(boolean c) {
		current = c;
	}

	void insert(Node n) {

		// First find the current node and insert c
		// as the child node for the current
		// and mark c as the new current node

		if ( current == true) {
			current = false;
			children.add(n);
		}
		else {
			Iterator itr = children.iterator();
			while ( itr.hasNext() ) {
				Node c = (Node) itr.next();
				c.insert(n);
			}
		}
	}

	void resetCurrent(int ee) {

		if ( current == true ) {
			endEvent = ee;
			current = false;
			return;
		}			

		// If any of the children is current,
		// then 'this' node should be the 'new' current.
		// We set it 'true' beforehand and then confirm
		// Bcoz we can move in a top-down fashion only.
		current = true;

		// We loop once to check and assign this
		Iterator itr = children.iterator();
		while ( itr.hasNext() ) {
			Node c = (Node) itr.next();
			if ( (c.getCurrent()) == true ) {
				c.setEndEvent(ee);
				c.setCurrent(false);
				return;
			}
		}
		// Once we confirm none of the children is current,
		// then 'this' node cannot be the 'new' current.
		// So we set current to false and repeat this
		// method with every child. Thus every level is covered.
		current = false; 

		itr = children.iterator();
		while ( itr.hasNext() ) {
			Node c = (Node) itr.next();
			c.resetCurrent(ee);
		}
	}

	void printTree() {
		System.out.println(object + "," + method 
								+ "," + startEvent 
								+ "-" + endEvent
								+ "," + current
								+ "," + callerObject 
								+ "," + callerMethod);

		Iterator itr = children.iterator();
		while ( itr.hasNext() ) {
			Node c = (Node) itr.next();
			c.printTree();
		}
	}

	void dfs(String color) {

		/*if (callerObject.equals("SYSTEM")) {
			PlantUML.tm.put(startEvent, 
						  "[-> " + "\"" + object + "\"" 
						+ " : " + "\"" + method + "\"" 
						+ "\n" + "activate " + "\"" + object + "\"" 
						+ " " + color);

			PlantUML.tm.put(endEvent,
						  "[<-- " + "\"" + object + "\"" 
						//+ " : " + "\"" + method + "\"" 
						+ "\n" + "deactivate " + "\"" + object + "\"" );
		}
		else {*/
			pl.tm.put(startEvent, "\"" + callerObject + "\"" 
						+ " -> " + "\"" + object + "\"" 
						+ " : " + method 
						+ "\n" + "activate " + "\"" + object + "\"" 
						+ " " + color);

			pl.tm.put(endEvent, "\"" + callerObject + "\"" 
						//+ " : " + "\"" + callerMethod + "\"" 
						+ " <-- " + "\"" + object + "\"" 
						//+ " : " + "\"" + method + "\"" 
						+ "\n" + "deactivate " + "\"" + object + "\"" );
		//}

		Iterator itr = children.iterator();
		while ( itr.hasNext() ) {
			Node c = (Node) itr.next();
			c.dfs(color);
		}
	}

	void exportToQSDE() {

	}

}


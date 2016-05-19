/*
 * Aum Amriteswaryai Namah
 *
 * File: CompactCallTrees.java
 * Description: Constructs compact call trees
 * 
 */

package calltree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.StringTokenizer;
import java.util.HashSet;

import utilities.Util;
import events.*;

public class CompactCallTrees {

	static LinkedHashMap<String,CNode> tcct;// = new LinkedHashMap<String,CNode>();
	static ArrayList<String> activeThreads;// = new ArrayList<String>();
	static ArrayList<String> objects;// = new ArrayList<String>();
	static TreeMap<String,String> instances;// = new TreeMap<String,String>();

	static int endevent = 2000000;
	
	public static LinkedHashMap<String,CNode> construct(EventSequence events,
													PlantUML p) {
		
		tcct = new LinkedHashMap<String,CNode>();
		activeThreads  = new ArrayList<String>();
		objects = new ArrayList<String>();
		instances = new TreeMap<String,String>();
			
		Event e = null;
		Iterator<Event> itr = events.iterator();
		while ( itr.hasNext() ) {
			e = (Event) itr.next();
			updateTree(e, p);
		}
		//printTrees();
		return tcct;
	}
	
	public static void updateTree(Event e, PlantUML p) {

			switch ( e.getEventType() ) {

				case ("System Start"):
					break;

				case ("System End"):
					break;

				case ("Thread Start"):
					String tt;
					int tse;
					tt = e.getThread();
					tse = e.getEventId();
					CNode master = new CNode("SYSTEM", "SYSTEM", tse, endevent++, true, "SYSTEM", "SYSTEM", p);
					tcct.put(e.getThread(), master);
					
					//tcct.put(e.getThread(), null);
					activeThreads.add(e.getThread());
					break;

				case ("Thread End"):
					String tts = e.getThread();
					CNode troot = (CNode)(tcct.get(tts));
					troot.resetCurrent(e.getEventId());
					activeThreads.remove(e.getThread());
					break;

				case ("Type Load"):
					break;

				case ("New Object"):
					//objects.add( ((NewObjectEvent)e).getNewObject() );
					break;

				case ("Context Shift"):
					break;

				case ("Method Call"):
					String t, o, m;
					int se;
					String co, cm; // for PlantUML

					t = e.getThread();
					o = ((MethodCallEvent) e).getTargetObject();
					if ( !(objects.contains(o)) ) objects.add(o);
					m = ((MethodCallEvent) e).getTargetMethod();
					se = ((MethodCallEvent) e).getEventId();

					co = ((MethodCallEvent) e).getCallerObject(); // for PlantUML
					cm = ((MethodCallEvent) e).getCallerMethod(); // for PlantUML		
					if ( !(objects.contains(co)) ) objects.add(co);

					if ( (CNode)(tcct.get(t)) == null ) {					
						// create master node to deal with multiple calls from SYSTEM
						//CNode master = new CNode("SYSTEM", "SYSTEM", se-1, 2000000, true, "SYSTEM", "SYSTEM", p);
						//tcct.put(t, master);
						
						//CNode mas = (CNode)(tcct.get(t));
						// create root node with event details
						CNode root = new CNode(o, m, se, endevent++, true, co, cm, p);
						tcct.put(t, root);
						//mas.insert(root);
					}
					else {
						// create new node with event details and 
						// insert it as a child (to current node)
						CNode root = (CNode)(tcct.get(t));
						CNode n = new CNode(o, m, se, endevent++, true, co, cm, p);
						root.insert(n);
					}
					
					//printTrees();
					//System.out.println("--------------------");

					break;

				case ("Method Exit"):
					String tthrd = e.getThread();
					CNode root = (CNode)(tcct.get(tthrd));
					root.resetCurrent(e.getEventId());

					//printTrees();
					//System.out.println("--------------------");

					break;

				case ("Field Read"):
					break;

				case ("Field Write"):
					break;

				case ("Variable Write"):
					break;

				case ("Variable Delete"):
					break;

				case ("Line Step"):
					break;

				default:
					break;

			}
	}

	public static void compact(PlantUML p) {
		
		Set<String> threads = tcct.keySet();
		for (String t:threads) {
			CNode n = tcct.get(t);
			n.compact(p);
		}
	}
	
	public static int countNodes() {
		int nodeCount = 0;
		Util.cLifelines = new HashSet<String>();
		Set<String> threads = tcct.keySet();
		for (String t:threads) {
			CNode n = tcct.get(t);
			nodeCount = n.countNodes(nodeCount-1); // Don't count the dummy root node
		}
		return nodeCount;
	}

	public static void printTrees() {
        Set<String> threads = tcct.keySet();
        for (String t:threads) {
            System.out.println(t+" : ");
			tcct.get(t).printTree();
		}
	}

	public static void printActiveThreads() {

		Iterator<String> itr = activeThreads.iterator();
		while ( itr.hasNext() ) 
			System.out.println( itr.next() );
	}

	public static void printObjects() {

		Iterator<String> itr = objects.iterator();
		while ( itr.hasNext() ) 
			System.out.println( itr.next() );
	}
	
	public static void mergeLifelines(int mergeCount) {
		
		Iterator<String> itr = objects.iterator();
		while ( itr.hasNext() )  {
			String object = itr.next();
			if ( !(object.contains(":")) )
				continue;
			StringTokenizer st = new StringTokenizer(object, ":");
			String name = st.nextToken();
			int numInstance = Integer.parseInt(st.nextToken());
			
			if (numInstance > mergeCount) 
				instances.put(name, name + ":1~" + numInstance);
		}
		//printInstances();
		
        Set<String> threads = tcct.keySet();
        for (String t:threads) {
			tcct.get(t).alterTree(instances);
		}	
	}
	
	public static void printInstances() {
		Set<String> objInsts = instances.keySet();
	    for (String o:objInsts) {
	    	System.out.println(o + " , " + instances.get(o));
	    }
	}	
}


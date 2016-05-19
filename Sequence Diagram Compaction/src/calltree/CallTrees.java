/*
 * Aum Amriteswaryai Namah
 * 
 * File: CallTrees.java
 * Description: Reads the event sequence and constructs a call tree
 *              for each thread.
 *
 */

package calltree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;
import events.*;

public class CallTrees {

	static LinkedHashMap<String,Node> tct = new LinkedHashMap<String,Node>();
	static ArrayList<String> activeThreads = new ArrayList<String>();
	static ArrayList<String> objects = new ArrayList<String>();

	public static LinkedHashMap<String,Node> construct(EventSequence events, PlantUML p) {
		
		Iterator<Event> itr = events.iterator();
		while ( itr.hasNext() ) {
			Event e = (Event) itr.next();
			
			switch ( e.getEventType() ) {

				case ("System Start"):
					break;

				case ("System End"):
					break;

				case ("Thread Start"):
					tct.put(e.getThread(), null);
					activeThreads.add(e.getThread());
					break;

				case ("Thread End"):
					activeThreads.remove(e.getThread());
					break;

				case ("Type Load"):
					break;

				case ("New Object"):
					objects.add( ((NewObjectEvent)e).getNewObject() );
					break;

				case ("Context Shift"):
					break;

				case ("Method Call"):
					String t, o, m;
					int se;
					String co, cm; // for PlantUML

					t = e.getThread();
					o = ((MethodCallEvent) e).getTargetObject();
					m = ((MethodCallEvent) e).getTargetMethod();
					se = ((MethodCallEvent) e).getEventId();

					co = ((MethodCallEvent) e).getCallerObject(); // for PlantUML
					cm = ((MethodCallEvent) e).getCallerMethod(); // for PlantUML				

					if ( (Node)(tct.get(t)) == null ) {					
						// create root node with event details
						Node root = new Node(o, m, se, 0, true, co, cm, p);
						tct.put(t, root);
					}
					else {
						// create new node with event details and 
						// insert it as a child (to current node)
						Node root = (Node)(tct.get(t));
						Node n = new Node(o, m, se, 0, true, co, cm, p);
						root.insert(n);
					}

					//printTrees();
					//System.out.println("--------------------");

					break;

				case ("Method Exit"):
					String thrd = e.getThread();
					Node root = (Node)(tct.get(thrd));
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
		//printTrees();
		return tct;
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

	public static void printTrees() {
        Set<String> threads = tct.keySet();
        for (String t:threads) {
            System.out.println(t+" : ");
			tct.get(t).printTree();
		}
	}
}

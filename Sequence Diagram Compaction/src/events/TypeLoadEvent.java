/*
 * Aum Amriteswaryai Namah
 *
 * File: TypeLoadEvent.java
 * Description: Defines type load event. Extends Event class.
 *
 */

package events;

public class TypeLoadEvent extends Event {

	String classType;
	
	public TypeLoadEvent(String t, int eId, String s,
							String eType, String ct) {
		this.thread = t;
		this.eventId = eId;
		this.source = s;
		this.eventType = eType;
		this.classType = ct;
	}

	public void printEvent() {
		System.out.println(thread + "," + eventId + "," + source
								+ "," + eventType + "," + classType);
	}

}

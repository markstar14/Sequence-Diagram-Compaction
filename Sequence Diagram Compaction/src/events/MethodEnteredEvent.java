/*
 * Aum Amriteswaryai Namah
 *
 * File: MethodEnteredEvent.java
 * Description: Defines method entered event. Extends Event class.
 *
 */

package events;

public class MethodEnteredEvent extends Event {

	String cObject;
	String cMethod;
	String tObject;
	String tMethod;
	
	public MethodEnteredEvent(String t, int eId, String s,
							int l, String eType) {
		this.thread = t;
		this.eventId = eId;
		this.source = s;
		this.lineNumber = l;
		this.eventType = eType;
	}

	public void printEvent() {
		System.out.println(thread + "," + eventId + "," + source
								+ "," + lineNumber + "," + eventType);
	}

}

/*
 * Aum Amriteswaryai Namah
 *
 * File: ContextShiftEvent.java
 * Description: Defines context shift event. Extends Event class.
 *
 */
package events;

public class ContextShiftEvent extends Event {

	
	public ContextShiftEvent(String t, int eId, String s,
							int l, String eType) {
		this.thread = t;    // Holds thread before context shift
		this.eventId = eId;
		this.source = s;   	// Holds thread after context shift
		this.lineNumber = l;
		this.eventType = eType;
	}

	public void printEvent() {
		System.out.println(thread + "->" + source
								+ "," + eventType);
	}

}

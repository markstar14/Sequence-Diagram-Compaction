/*
 * Aum Amriteswaryai Namah
 *
 * File: MethodReturnedEvent.java
 * Description: Defines method returned event. Extends Event class.
 *
 */

package events;

public class MethodReturnedEvent extends Event {

	public MethodReturnedEvent(String t, int eId, String s,
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

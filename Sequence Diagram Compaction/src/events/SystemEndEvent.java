/*
 * Aum Amriteswaryai Namah
 *
 * File: SystemEndEvent.java
 * Description: Defines system end event. Extends Event class.
 *
 */

package events;

public class SystemEndEvent extends Event {
	
	public SystemEndEvent(String t, int eId, String s,
							String eType) {
		this.thread = t;
		this.eventId = eId;
		this.source = s;
		this.eventType = eType;
	}

	public void printEvent() {
		System.out.println(thread + "," + eventId + "," + source
								+ "," + eventType);
	}

}

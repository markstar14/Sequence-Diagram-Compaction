/*
 * Aum Amriteswaryai Namah
 *
 * File: SystemStartEvent.java
 * Description: Defines system start event. Extends Event class.
 *
 */

package events;

public class SystemStartEvent extends Event {
	
	public SystemStartEvent(String t, int eId, String s,
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

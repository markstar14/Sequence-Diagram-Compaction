/*
 * Aum Amriteswaryai Namah
 *
 * File: ThreadStartEvent.java
 * Description: Defines thread start event. Extends Event class.
 *
 */

package events;

public class ThreadStartEvent extends Event {
	
	public ThreadStartEvent(String t, int eId, String s,
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

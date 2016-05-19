/*
 * Aum Amriteswaryai Namah
 *
 * File: ThreadEndEvent.java
 * Description: Defines thread end event. Extends Event class.
 *
 */

package events;

public class ThreadEndEvent extends Event {
	


	public ThreadEndEvent(String t, int eId, String s,
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

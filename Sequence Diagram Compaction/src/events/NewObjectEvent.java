/*
 * Aum Amriteswaryai Namah
 *
 * File: NewObjectEvent.java
 * Description: Defines new object event. Extends Event class.
 *
 */

package events;

public class NewObjectEvent extends Event {

	String newObject;
	
	public NewObjectEvent(String t, int eId, String s,
							String eType, String no) {
		this.thread = t;
		this.eventId = eId;
		this.source = s;
		this.eventType = eType;
		this.newObject = no;
	}

	public String getNewObject() {
		return newObject;
	}

	public void printEvent() {
		System.out.println(thread + "," + eventId + "," + source
								+ "," + eventType + "," + newObject);
	}

}

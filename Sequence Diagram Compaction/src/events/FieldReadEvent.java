/*
 * Aum Amriteswaryai Namah
 *
 * File: FieldReadEvent.java
 * Description: Defines field read event. Extends Event class.
 *
 */
package events;


public class FieldReadEvent extends Event {

	String frObject;
	String fieldRead;
	
	public FieldReadEvent(String t, int eId, String s,
							int l, String eType,
							String frOb, String fRead) {
		this.thread = t;
		this.eventId = eId;
		this.source = s;
		this.lineNumber = l;
		this.eventType = eType;
		this.frObject = frOb;
		this.fieldRead = fRead;
	}

	public void printEvent() {
		System.out.println(thread + "," + eventId + "," + source
								+ "," + lineNumber + "," + eventType
								+ "," + frObject + "." + fieldRead);
	}

}

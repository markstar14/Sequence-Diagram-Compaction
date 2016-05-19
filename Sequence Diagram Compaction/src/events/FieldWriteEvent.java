/*
 * Aum Amriteswaryai Namah
 *
 * File: FieldWriteEvent.java
 * Description: Defines field write event. Extends Event class.
 *
 */
package events;


public class FieldWriteEvent extends Event {

	String fwObject;
	String fieldWritten;
	String valueWritten;

	
	public FieldWriteEvent(String t, int eId, String s,
							int l, String eType,
							String fwOb,
							String fWritten, String vWritten) {
		this.thread = t;
		this.eventId = eId;
		this.source = s;
		this.lineNumber = l;
		this.eventType = eType;
		this.fwObject = fwOb;
		this.fieldWritten = fWritten;
		this.valueWritten = vWritten;
	}

	public void printEvent() {
		System.out.println(thread + "," + eventId + "," + source
								+ "," + lineNumber + "," + eventType
								+ "," + fwObject
								+ "." + fieldWritten + "=" + valueWritten);
	}

}

/*
 * Aum Amriteswaryai Namah
 *
 * File: Event.java
 * Description: Defines Event interface
 *
 */
package events;

public class Event {

	String thread = null;
	int eventId = -1;
	String source = null;
	int lineNumber = -1;
	String eventType = null;
	
	/*public Event(String t, int eid, String s, int l, String ety) {
		thread = t;
		eventId = eid;
		source = s;
		lineNumber = l;
		eventType = ety;
	}*/

	public String getThread() { 
		return thread; 
	} 

	public int getEventId() {
		return eventId;
	}

	public String getSource() {
		return source;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getEventType() {
		return eventType;
	}

	public void printEvent() {
		System.out.println(thread + "," + eventId + "," + source
								+ "," + lineNumber + "," + eventType);
	}
}

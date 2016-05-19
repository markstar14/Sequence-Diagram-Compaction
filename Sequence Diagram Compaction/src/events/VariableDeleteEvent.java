/*
 * Aum Amriteswaryai Namah
 *
 * File: VariableDeleteEvent.java
 * Description: Defines variable delete event. Extends Event class.
 *
 */

package events;

public class VariableDeleteEvent extends Event {

	String cObject;
	String cMethod;
	String tObject;
	String tMethod;
	
	public VariableDeleteEvent(String t, int eId, String s,
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

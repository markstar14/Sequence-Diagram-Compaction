/*
 * Aum Amriteswaryai Namah
 *
 * File: MethodExitEvent.java
 * Description: Defines method exit event. Extends Event class.
 *
 */

package events;

public class MethodExitEvent extends Event {

	String rObject;
	String rMethod;
	String rValue;
	
	public MethodExitEvent(String t, int eId, String s,
							int l, String eType,
							String rObj, String rMtd,
							String rVal) {
		this.thread = t;
		this.eventId = eId;
		this.source = s;
		this.lineNumber = l;
		this.eventType = eType;
		this.rObject = rObj;
		this.rMethod = rMtd;
		this.rValue = rVal;
	}

	public void printEvent() {
		System.out.println(thread + "," + eventId + "," + source
								+ "," + lineNumber + "," + eventType
								+ "," + rObject + "," + rMethod
								+ "," + rValue);
	}

}

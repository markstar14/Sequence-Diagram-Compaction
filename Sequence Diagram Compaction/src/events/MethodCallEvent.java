/*
 * Aum Amriteswaryai Namah
 *
 * File: MethodCallEvent.java
 * Description: Defines method call event. Extends Event class.
 *
 */
package events;


public class MethodCallEvent extends Event {

	String cObject;
	String cMethod;
	String tObject;
	String tMethod;
	
	public MethodCallEvent(String t, int eId, String s,
							int l, String eType,
							String cObj, String cMtd,
							String tObj, String tMtd) {
		this.thread = t;
		this.eventId = eId;
		this.source = s;
		this.lineNumber = l;
		this.eventType = eType;
		this.cObject = cObj;
		this.cMethod = cMtd;
		this.tObject = tObj;
		this.tMethod = tMtd;
	}

	public void printEvent() {
		System.out.println(thread + "," + eventId + "," + source
								+ "," + lineNumber + "," + eventType
								+ "," + cObject + "#" + cMethod
								+ "->" + tObject + "#" + tMethod);
	}

	public String getCallerObject() {
		return cObject;
	}

	public String getTargetObject() {
		return tObject;
	}

	public String getCallerMethod() {
		return cMethod;
	}

	public String getTargetMethod() {
		return tMethod;
	}


}

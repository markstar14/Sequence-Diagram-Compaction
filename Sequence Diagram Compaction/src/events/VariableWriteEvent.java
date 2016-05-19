/*
 * Aum Amriteswaryai Namah
 *
 * File: VariableWriteEvent.java
 * Description: Defines variable write event. Extends Event class.
 *
 */

package events;

public class VariableWriteEvent extends Event {

	String vwObject;
	String vwMethod;
	String variable;
	String value;
	
	public VariableWriteEvent(String t, int eId, String s,
							int l, String eType,
							String vwOb, String vwMtd,
							String var, String val) {
		this.thread = t;
		this.eventId = eId;
		this.source = s;
		this.lineNumber = l;
		this.eventType = eType;
		this.vwObject = vwOb;
		this.vwMethod = vwMtd;
		this.variable = var;
		this.value = val;
	}

	public void printEvent() {
		System.out.println(thread + "," + eventId + "," + source
								+ "," + lineNumber + "," + eventType
								+ "," + vwObject + "#" + vwMethod
								+ "," + variable + "=" + value);
	}

}

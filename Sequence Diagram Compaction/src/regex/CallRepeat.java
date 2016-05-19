/*
 * Aum Amriteswaryai Namah
 *
 * File: CallRepeat.java
 * Description: Defines call repeat of the form [i, alpha, k]
 *              Replaces symbols with actual calls - object.method
 *              Analogous to tandem repeats
 *
 */

package regex;

public class CallRepeat {

	int index;		// start position of tandem repeat in the array
	String objmtd; 	// regex label - symbols replaced
	int omlen;	// pattern length
	int repeats;	// number of repetitions

	public CallRepeat(int i, String om, int len, int r) {
		index = i;
		objmtd = om;
		omlen = len;
		repeats = r;
	}

	public int getStartPos() {
		return index;
	}

	public String getObjMtd() {
		return objmtd;
	}

	public int getOMLen() {
		return omlen;
	}

	public int getRepeats() {
		return repeats;
	}

	public void printCallRepeat() {
		System.out.println("[" + index + ","
								+ objmtd + ","
								+ omlen + ","
								+ repeats + "]");
	}
}

/*
 * Aum Amriteswaryai Namah
 *
 * File: TandemRepeat.java
 * Description: Defines tandem repeat of the form [i, alpha, k]
 * 
 */

package regex;

public class TandemRepeat {

	int index;		// start position of tandem repeat in the array
	String alpha; 	// regex label
	int repeats;	// number of repetitions

	TandemRepeat(int i, String a, int r) {
		index = i;
		alpha = a;
		repeats = r;
	}

	public int getStartPos() {
		return index;
	}

	public String getAlpha() {
		return alpha;
	}

	public int getRepeats() {
		return repeats;
	}

	public void printTandemRepeat() {
		System.out.println("[" + index + ","
								+ alpha + ","
								+ repeats + "]");
	}
}


/* 
 * Aum Amriteswaryai Namah
 */

package regex;

import java.io.*;
import java.util.*;

public class MinimalRegex {

	static final int ALLREPEATS = 1; // Set to 0 for only primitive tandem repeats
	static String sequence;		// Input sequence
	static int pi[] = new int[15000];	// KMP Prefix function output
	static String filename = "graph.txt"; // Adjacency list is dumped in this file
	static String filename2 = "tandemrepeats.txt"; // [i, alpha, k]
	static FileWriter tr;
	static String path;

	static StringBuffer graph = new StringBuffer();
	static StringBuffer tandemrepeats = new StringBuffer();

	public TandemRepeat[] findMinimalRegex(String arg) {

		//System.out.println("Aum Amma");

		//MinimalRegex minimalRegex = new MinimalRegex();
		try {
			sequence = new String(arg);	// Store the input string to 'sequence' field
		} catch (ArrayIndexOutOfBoundsException e) { 
			System.out.println("Usage: java MinimalRegex <string-pattern>"); 
			return null;
		}
		
		int len = sequence.length();	// Determine the length of the input string

		createAdjacencyList(len);

		// Find minimal regular expressions for every suffix sequence
		// For a string: ababb, suffix sequences would be: b, bb, abb, babb, ababb
		for (int i = 1; i <= len; i++) {
			findTandemRepeatsOnPrefix( sequence.substring(len - i), len-i );
		}
		
/*		System.out.println("===========================================================");
		System.out.println("Adjacency list is dumped to the file: " + filename);
		System.out.println("cat " + filename + " to display");
		System.out.println("Primitive tandem repeats are dumped to: " + filename2);
		System.out.println("cat " + filename2 + " to display");
		System.out.println("===========================================================");
*/

		Dijkstra dij = new Dijkstra();
		path = dij.createGraph(graph.toString(), len);
		//path = dij.createGraph(filename, len);

		//System.out.println(tandemrepeats);
		//System.out.println(graph);

		TandemRepeat[] labels = findMinRE( path.substring(1, path.length() - 1) );

		tandemrepeats = new StringBuffer(); // Reset tandemrepeats
		graph = new StringBuffer();  // Reset graph
		
		return labels;
	}

	// Find the minimal regular expression for the suffix sequence
	void findTandemRepeatsOnPrefix(String suffixSequence, int index) {
		int k, q;
		StringBuffer P = new StringBuffer(suffixSequence);
		int patternLen = 0;
		int repeatCount = 1;
		int suffixLen = P.length();
		pi[0] = 0;
		k = 0;
		
		// Find the prefix function for a given suffix sequence
		for( q = 1 ; q < suffixLen ; q++ ) {
			while( ( k > 0 ) && ((P.toString()).charAt(k) != (P.toString()).charAt(q)))
				k = pi[k - 1];
				if ((P.toString()).charAt(k) == (P.toString()).charAt(q))
					k = k + 1;

				pi[q] = k;
		}	

		if (ALLREPEATS == 1) {
			patternLen = findTandemRepeats(index, suffixLen);	// Look for all tandem repeats
		}
		else {

			// Look for a primitive tandem repeats
			patternLen = findPrimitiveTandemRepeat(suffixLen);

			// Determine how many times the pattern repeats
			if (patternLen > 0) {
				repeatCount = findRepeatCount(patternLen, suffixLen);
			}

			addAdjacencyList(index, patternLen, repeatCount);
			
			if (repeatCount > 1)
				addTandemRepeat(index, patternLen, repeatCount);
		}
	}

	// Find the primitive tandem repeat that involves the first char
	int findPrimitiveTandemRepeat(int suffixLen) {
		int patternLen = 0;
		int q = 0;

		// Check the # of chars before step pattern starts
		// Increment as you see 0's
		while ( q < suffixLen/2 ) {
				if ( pi[q] == 0 )  {
					patternLen++;
					q++;
				}
				else
					break;
		}

		// Increment if the step pattern has 2 consequtive 1's or 2's
		// If the pattern reduces break out of the loop. We are only
		// concerned about the prefix pattern.
		while ( q < suffixLen/2 ) {
			if ( pi[q+1] < pi[q] )
				break;
			else if ( (pi[q+1] == pi[q]) ) {
				patternLen++;
				q++;
			}
			else if ( pi[q] == pi[q-1] + 1 ) {
				q++;
			}
			else
				break;
		}
		return patternLen;
	}

	// Find all tandem repeats not only primitive. For example, in
	// ababbbababbb, primitive tandem repeat is ab whereas all tandem
	// repeats include ab & ababbb. CURRENTLY NOT IN USE.
	int findTandemRepeats(int index, int suffixLen) {
		int patternLen = 0;
		int repeatCount = 1;
		int q = 0;
/*
		// Check the # of chars before step pattern starts
		// Increment as you see 0's
		while ( q < suffixLen/2 ) {
				if ( pi[q] == 0 )  {
					patternLen++;
					q++;
				}
				else
					break;
		}
*/
		// Continue to check till half-way point. If the value is non-zero,
		// move on. If the value is 0, check if the value at 2*index position
		// is equal to index. If so, re-assign patternLen to index.
		// Since our index q starts from 0, a slight different math is required.
		while ( q < suffixLen/2 ) {
//			if ( (pi[q] == 0) || (pi[q] == 1) ) {
				if ( pi[2*q+1] == (q+1) ) {
					patternLen = q+1;
					repeatCount = findRepeatCount(patternLen, suffixLen);
					addTandemRepeat(index, patternLen, repeatCount);
					addAdjacencyList(index, patternLen, repeatCount);
					q++;
					continue;
				} else
					q++;
			}
//			else
//				q++;
//		}
		return patternLen;
	}

	// Determine if the pattern repeats. If so find the repeat count
	int findRepeatCount(int patternLen, int suffixLen) {

		int repeatCount = 1;	// No repetition
		int q = 2;

		// If string length is 1, no repeats can be found
		if (suffixLen == 1)
			return repeatCount;

		// Check if the pattern repeats. If so increase the repeat count.
		while ( (q * patternLen) <= suffixLen ) {
			if ( pi[q*patternLen - 1] == ((q-1) * patternLen) ) {
				q++;
				repeatCount++;
			}
			else
				break;
		}
		return repeatCount;
	}

	void createAdjacencyList(int index) {
		graph.append(index + "->$\n");
	}

	void addAdjacencyList(int index, int patternLen, int repeatCount) {

		if (patternLen == 0)
			graph.append(index + "->" + (index+1) + "->");
		else {
			if (repeatCount == 1) 
				graph.append(index + "->" + (index+1) + "->");
			else {
				graph.append(index + "->");
				for (int i=2; i <= repeatCount; i++) 	// MVC problem - changing i from 1 to 2 worked
					graph.append((index+(patternLen*i)) + "->");
			}
		}
		graph.append("$\n");
	}

	void addTandemRepeat(int index, int patternLen, int repeatCount) {

		for (int q = 2; q <= repeatCount; q++) 
			tandemrepeats.append(index + "," + sequence.substring(index, index+patternLen) + "," + q + "\n");
	}

	// Note: This is a sub-optimal implementation
	TandemRepeat[] findMinRE(String path) {
		int prev;
		int next;

		int numTokens = 0; // new

		StringBuffer re = new StringBuffer();
		//System.out.println("path = " + path);
		StringTokenizer st = new StringTokenizer(path, ", ");

		numTokens = st.countTokens();		// new
		//System.out.println("Num tokens = " + numTokens);	// new
		TandemRepeat[] labels = new TandemRepeat[numTokens-1]; // new

		next = Integer.parseInt( st.nextToken() );

		int i = 0;	// new
		while ( st.hasMoreTokens() ) {
			prev = next;
			next = Integer.parseInt(st.nextToken());
			if (next - prev > 1) {
				labels[i] = findSubRE(prev, next);
				re.append("(" + labels[i].getAlpha() + ")^" 
							+ labels[i].getRepeats() + " ");
				i++;	// new
			}
			else {
				re.append(sequence.charAt(prev));
				labels[i] = new TandemRepeat(prev,sequence.substring(prev,prev+1),1);	// new
				i++;	// new
			}
		}
		//return re.toString();
		return labels;
	}

	TandemRepeat findSubRE(int prev, int next) {
		String strLine = new String();
		int index;
		String pattern;
		int patternLen;
		int numRepeats;

		StringTokenizer sttr = new StringTokenizer(tandemrepeats.toString(), "\n");

		while ( sttr.hasMoreTokens() ) {
			strLine = sttr.nextToken();
			StringTokenizer st = new StringTokenizer(strLine, ",");
			index = Integer.parseInt( st.nextToken() );
			if (index == prev) {
				pattern = st.nextToken();
				patternLen = pattern.length();
				numRepeats = Integer.parseInt( st.nextToken() );

				if (next == index + patternLen * numRepeats) {
					String subre = new String("(" + pattern + ")^" + numRepeats + " ");  // extra space needed for further processing
					TandemRepeat tr = new TandemRepeat(index, pattern, numRepeats);  // new
					return tr;  // new
					//return subre;
				}
			}
		}
		return null;
	}
	
	public static void main(String args[]) {
	    MinimalRegex m = new MinimalRegex();
	    TandemRepeat tr[] = m.findMinimalRegex("bbebeebebebbebee");
		for (int i=0; i<tr.length; i++)
			tr[i].printTandemRepeat();
		//System.out.println(m.findMinimalRegex("bbebeebebebbebee"));
		//System.out.println(m.findMinimalRegex("abacabadabacaba"));
		//System.out.println(m.findMinimalRegex("abcdbddbdddbdddde")); // MVC problem simulated using this string sequence
		//System.out.println(m.findMinimalRegex("[\]o\oo\ooo\ooooǞ"));
	}
	
}
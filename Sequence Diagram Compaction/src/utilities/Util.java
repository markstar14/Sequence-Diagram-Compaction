/*
 * Aum Amriteswaryai Namah
 *
 * File: Util.java
 * Description: Defines utility functions
 *
 */

package utilities;


import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.util.HashSet;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

public class Util {

	public static HashSet<String> cLifelines;
	
	static char c = 'A';
	static int nextSymPos = 0;
	static char symbol[] = { 'a', 'b', 'c', 'd', 'e', 'f', 'g'
						, 'h', 'i', 'j', 'k', 'l', 'm', 'n'		
						, 'o', 'p', 'q', 'r', 's', 't', 'u'
						, 'v', 'w', 'x', 'y', 'z', 'A', 'B'
						, 'C', 'D', 'E', 'F', 'G', 'H', 'I'
						, 'J', 'K', 'L', 'M', 'N', 'O', 'Q'
						, 'R', 'S', 'T', 'U', 'V', 'W', 'X'
						, 'Y', 'Z'
						};

	public static BufferedReader openInFile(String inFile) {

		try {
			FileInputStream fstream = new FileInputStream(inFile);
 			DataInputStream in = new DataInputStream(fstream);
  			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			return br;
		} 
		catch (Exception e) {
			System.out.println("Exception: Unable to open the file " + inFile);
		}
		return null;
	}

	public static String readLine(BufferedReader br,  String inFile) {
		
		try {
			return br.readLine();
		}
		catch (Exception e) {
			System.out.println("Exception: Unable to read the file " + inFile);
		}
		return null;
	}

	public static void closeInFile(BufferedReader br, String inFile) {

		try {
			br.close();
		}
		catch (Exception e) {
			System.out.println("Exception: Unable to close the file " + inFile);
		}
	}

	public static FileWriter openOutFile(String outFile) {
		
		IPath path = ResourcesPlugin.getPlugin().getStateLocation();
		File seqDiagFile = new File(path.toFile().getPath() + File.separator + outFile);
		System.out.println("Temp location = " + seqDiagFile.getPath());
		
		try {
			FileWriter out = new FileWriter(seqDiagFile);
			out.close();
			out = new FileWriter(seqDiagFile, true);
			return out;
		}
		catch (Exception e) {
			System.out.println("Unable to open " + outFile);
		}
		return null;
	}

	public static void writeToOutFile(String s, FileWriter out, String outFile) {
		try {
			out.write(s + "\n");
		} 
		catch (Exception e) { 
			System.out.println("Unable to write to " + outFile);
		}
	}
	
	public static void closeOutFile(FileWriter out, String outFile) {
		try {
			out.close();
		}
		catch (Exception e) {
			System.out.println("Unable to close " + outFile);
		}
	}

	public static String stripColon(String method) {
		
		int index = method.indexOf(':');
		if (index == -1)	// Sometimes method may not contain ':'
			index = method.length()-1;  // Set index to method length
		String mtd = method.substring(0,index);
		return mtd;
	}

	public static char getNextSymbol() {
		//return symbol[nextSymPos++];
		return c++;  // Should some symbols be skipped?
	}

	public static int countDistinctObjects(String[] objects) {
		
		HashSet<String> O = new HashSet<String>();
		for (int i=0; i<objects.length; i++)
			O.add(objects[i]);

		return O.size();	
	}
	
	public static String splitCompactedEvents(String compEvent) {
		String[] tokens = compEvent.split(" ");
		String cobject = tokens[0];
		String  object = tokens[2];
		String  method = tokens[4];
		
		//System.out.println(compEvent);
		//System.out.println(cobject + " " + object + " " + method);
 
		return null;
	}
	
}

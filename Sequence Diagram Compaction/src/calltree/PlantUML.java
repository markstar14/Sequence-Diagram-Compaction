/*
 * Aum Amriteswaryai Namah
 *
 * File: PlantUML.java
 * Description: Define utility methods for exporting to PlantUML
 *
 */

package calltree;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.HashSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;

import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.FileFormat;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PlantUML  {

	TreeMap<Integer,String> tm = new TreeMap<Integer,String>();
	TreeMap<Integer,String> tmc = new TreeMap<Integer,String>();
	TreeMap<String,ArrayList<CNode>> rmc = 	// Regex <--> Removed Nodes mapping
								new TreeMap<String,ArrayList<CNode>>();
	ArrayList<String> loops = new ArrayList<String>();

	static String outFile1 = "sequence.txt";
	static String outFile2 = "compact.txt";
	static FileWriter out1 = null; //Util.openOutFile(outFile1);
	static FileWriter out2 = null; //Util.openOutFile(outFile2);
	
	// To hold the plantuml loop texts
	StringBuffer  sb = new StringBuffer();
	StringBuffer csb = new StringBuffer();
	

	public void export(LinkedHashMap<String,Node> tct) {

		String color[] = {"#E9967A", "#9ACD32", "#DAA520", "#FFA500"
						, "#228B22", "#66CDAA", "#7FFFD4"
						, "#7FFFD4", "#00BFFF", "#8A2BE2"
						, "#6A5ACD", "#BA55D3", "#DDA0DD"
						, "#DB7093", "#F5DEB3", "#D2691E"
						, "#CD853F", "#BC8F8F", "#708090"
						, "#B0C4DE", "#C0C0C0", "#FFFACD"
						};
		int i = 0;

		Set<String> threads = tct.keySet();
        for (String t:threads) {
			Node n = tct.get(t);
			n.dfs(color[i]);
			i++;
		}
		//print();
	}

	public void print() {
		
		/*out1 = Util.openOutFile(outFile1);
		Util.writeToOutFile("@startuml", out1, outFile1);
		Util.writeToOutFile("hide footbox", out1, outFile1);
		
		Set<Integer> eventIds = tm.keySet();
		for (Integer e:eventIds) {
			Util.writeToOutFile(tm.get(e), out1, outFile1);
		}
		Util.writeToOutFile("@enduml", out1, outFile1);
		Util.closeOutFile(out1, outFile1);*/
		
		IPath path = ResourcesPlugin.getPlugin().getStateLocation();
		OutputStream png = null;
		try {
			png = new FileOutputStream(path.toFile().getPath() + File.separator + "sequence.png");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		sb = new StringBuffer();
		sb.append("@startuml\n");
		sb.append("hide footbox\n");
		Set<Integer> eIds = tm.keySet();
		for (Integer e:eIds) {
			sb.append(tm.get(e)+"\n");
		}
		sb.append("@enduml\n");
		String source = sb.toString();
		
		SourceStringReader reader = new SourceStringReader(source);
		try {
			String desc = reader.generateImage(png);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	
	/*
	 * exportC is used by the original compaction plug-in. It establishes a set of colors that may be used in the diagram, but more importantly it calls printC which creates 
	 * a file with all of the drawing instructions.
	 */
	public void exportC(LinkedHashMap<String,CNode> tcct,
			int startCount, int endCount, String svgFile) {

		String color[] = {"#77CCFF", "#00AAAA", "#7F00FF"
						, "#22FF22", "#FF8000", "#FFA500"
						, "#E9967A", "#9ACD32", "#DAA520"
						, "#228B22", "#66CDAA", "#7FFFD4"
						, "#7FFFD4", "#00BFFF", "#8A2BE2"
						, "#6A5ACD", "#BA55D3", "#DDA0DD"
						, "#DB7093", "#F5DEB3", "#D2691E"
						, "#CD853F", "#BC8F8F", "#708090"
						, "#B0C4DE", "#C0C0C0", "#FFFACD"
						};
		int i = 0;

		Set<String> threads = tcct.keySet();
        for (String t:threads) {
			CNode n = tcct.get(t);
			//n.printTree();
			n.dfsC(color[i], startCount, endCount);
			i++;
		}
        //printTMC();
		printC(svgFile);
	}
	
	
	/*
	 * Slightly modifies version of exportC that is meant for creating the drawing instructions for a sub-diagram pop up.The hashMap holds all of the compacted term names and their 
	 * respective drawing instructions that were made during an drawMin execution
	 */
	public HashMap<String, StringBuffer> exportMinSect(LinkedHashMap<String,CNode> tcct, int startCount, int endCount, String svgFile, String startTerm, HashMap<String, StringBuffer> map) {

		String color[] = {"#77CCFF", "#00AAAA", "#7F00FF"
						, "#22FF22", "#FF8000", "#FFA500"
						, "#E9967A", "#9ACD32", "#DAA520"
						, "#228B22", "#66CDAA", "#7FFFD4"
						, "#7FFFD4", "#00BFFF", "#8A2BE2"
						, "#6A5ACD", "#BA55D3", "#DDA0DD"
						, "#DB7093", "#F5DEB3", "#D2691E"
						, "#CD853F", "#BC8F8F", "#708090"
						, "#B0C4DE", "#C0C0C0", "#FFFACD"
						};
		int i = 0;

		Set<String> threads = tcct.keySet();
        for (String t:threads) {
			CNode n = tcct.get(t);
			//n.printTree();
			n.dfsC(color[i], startCount, endCount);
			i++;
		}
        //printTMC();
		return printMinSect(svgFile, startTerm, map);
	}
	
	
	
	/*
	 * exportMin is used during the execution of the minimizeMainDrawButtonAction method. It establishes a color set, and calls printMin which both creates a file with 
	 * the drawing instructions for the smaller sequence diagram and also returns the hashMap with the drawing instructions for the sub diagrams
	 */
	public HashMap<String, StringBuffer> exportMin(LinkedHashMap<String,CNode> tcct,
			int startCount, int endCount, String svgFile, String endObject) {

		String color[] = {"#77CCFF", "#00AAAA", "#7F00FF"
						, "#22FF22", "#FF8000", "#FFA500"
						, "#E9967A", "#9ACD32", "#DAA520"
						, "#228B22", "#66CDAA", "#7FFFD4"
						, "#7FFFD4", "#00BFFF", "#8A2BE2"
						, "#6A5ACD", "#BA55D3", "#DDA0DD"
						, "#DB7093", "#F5DEB3", "#D2691E"
						, "#CD853F", "#BC8F8F", "#708090"
						, "#B0C4DE", "#C0C0C0", "#FFFACD"
						};
		int i = 0;

		Set<String> threads = tcct.keySet();
        for (String t:threads) {
			CNode n = tcct.get(t);
			//n.printTree();
			n.dfsC(color[i], startCount, endCount);
			i++;
		}
        //printTMC();
		return printMin(svgFile, endObject);
	}
	
/*
 * The original method for creating the drawing instructions for a compacted sequence diagram
 */
	public void printC(String svgFile) {
		
		/*** Change this function later to print to file ***/
		IPath path = ResourcesPlugin.getPlugin().getStateLocation();
		System.out.println("Resource Location: " + path);
		OutputStream png = null;
		try {
			//png = new FileOutputStream(svgFile);
			png = new FileOutputStream(path.toFile().getPath() + File.separator + "compact.png");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		csb = new StringBuffer();
		//System.out.println("csb = " + csb);
		csb.append("@startuml\n");
		csb.append("hide footbox\n");
		csb.append("skinparam sequenceParticipantBackgroundColor #FFFFBB\n");
		csb.append("skinparam sequenceParticipantBorderColor #101010\n");
		csb.append("skinparam sequenceArrowColor #000000\n");
		csb.append("skinparam sequenceLifeLineBorderColor #808080\n");
		
		//System.out.println("@startuml");
		//System.out.println("hide footbox");
		
		//out2 = Util.openOutFile(outFile2);
		//Util.writeToOutFile("@startuml", out2, outFile2);
		//Util.writeToOutFile("hide footbox", out2, outFile2);

		StringBuffer concurrentLabels = new StringBuffer();
		StringBuffer eventsBuffer = new StringBuffer();
		boolean loopFlag = false;
		int numLoops = 0;
//		HashMap<String,String> hm = new HashMap<String,String>();
		
		Set<Integer> eventIds = tmc.keySet();
		for (Integer e:eventIds) {
			//System.out.println(tmc.get(e));
			// First ignore SYSTEM to SYSTEM calls and returns (MVC problem)
			if ( tmc.get(e).contains("\"SYSTEM\" -> \"SYSTEM\" : SYSTEM")
					|| tmc.get(e).contains("\"SYSTEM\" <-- \"SYSTEM\"")
					|| tmc.get(e).contains("activate \"SYSTEM\"") ) {
				//System.out.println(tmc.get(e));
				continue;
			}
			
			if (tmc.get(e).startsWith("loop")) { // Print "loop ..." if there
				//System.out.println(tmc.get(e));
				String[] tokens = tmc.get(e).split(" ");
				String regex = tokens[1];
				//System.out.println(regex);
				if (numLoops == 0) {	// Loop encountered for the first time
					concurrentLabels.append(regex);
					loopFlag = true;
				}
				else
					concurrentLabels.append(" " + regex); 	// \u2297 otimes
				numLoops++;
				continue;
			}
			
			if (tmc.get(e).startsWith("end")) {
				numLoops--;
				if (numLoops == 0) {
					printInteractions(concurrentLabels.toString(),
											eventsBuffer.toString());

					concurrentLabels = new StringBuffer(); 	// Reset
					eventsBuffer = new StringBuffer();  	// Reset
					loopFlag = false;
				}
				continue;
			}
			
			if (numLoops > 0)
				eventsBuffer.append(tmc.get(e) + "\n"); // Accumulate the event
			else {
				csb.append(tmc.get(e)+"\n");
			}
		}
		csb.append("@enduml\n");
		//System.out.println("@enduml");
		String source = csb.toString();
		
		SourceStringReader reader = new SourceStringReader(source);
		try {
			//FileOutputStream os = new FileOutputStream(svgFile);
			//String success = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
			//os.close();
			
			FileFormatOption option = new FileFormatOption(FileFormat.PNG);
			String desc = reader.generateImage(png, option);  
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (OutOfMemoryError om) { //OutOfMemoryError om
			
			MessageDialog.openError(new Shell(Display.getCurrent()), 
					"Error", "OutOfMemoryError: Unable to generate huge image.\n"
							+ "Either limit the events or start eclipse with more heap space");
		}

		//printRMC();
	}

	/*
	 * printMinSect is used to produce the drawing instructions for one of the smaller sub-sections of the diagram. It takes in the 
	 * hashMap produced by printMin and simply appends the correct StringBuffer to the drawing instructions from the hashmap
	 */
	public HashMap<String, StringBuffer> printMinSect(String svgFile, String startTerm, HashMap<String, StringBuffer> preMadeMap) {
		
		/*** Change this function later to print to file ***/
		IPath path = ResourcesPlugin.getPlugin().getStateLocation();
		System.out.println("Resource Location: " + path);
		OutputStream png = null;
		
		HashMap<String, StringBuffer> minSectionMap = new HashMap<String, StringBuffer>();
		
		try {
			//png = new FileOutputStream(svgFile);
			png = new FileOutputStream(path.toFile().getPath() + File.separator + "compact.png");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		csb = new StringBuffer();
		//System.out.println("csb = " + csb);
		csb.append("@startuml\n");
		csb.append("hide footbox\n");
		csb.append("skinparam sequenceParticipantBackgroundColor #ffb3b3\n"); //800080\n"); //FFFFBB\n");
		csb.append("skinparam sequenceParticipantBorderColor #800080\n"); //101010\n");
		csb.append("skinparam sequenceArrowColor #0000b3\n"); //000000\n");
		csb.append("skinparam sequenceLifeLineBorderColor #b300b3\n");//808080\n");
		csb.append(preMadeMap.get(startTerm).toString()+"\n");	
//		System.out.println("@enduml");
		csb.append("@enduml\n");
		String source = csb.toString();
//		System.out.println(source);

		SourceStringReader reader = new SourceStringReader(source);
		try {
			
			FileFormatOption option = new FileFormatOption(FileFormat.PNG);
			String desc = reader.generateImage(png, option);  
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (OutOfMemoryError om) { //OutOfMemoryError om
			
			MessageDialog.openError(new Shell(Display.getCurrent()), 
					"Error", "OutOfMemoryError: Unable to generate huge image.\n"
							+ "Either limit the events or start eclipse with more heap space");
		}

		return minSectionMap;
	}
	
	
	/*
	 * printMin is used to construct the main minimized sequence diagram with the specified components taken out. 
	 * It is called by exportMin and returns a hashmap that holds the names of each minimized section as the keys and each key corresponds 
	 * to the complete drawing inputs as a stringBuffer for each term. 
	 * 
	 *  The loop works by looking at each entry, and checking to see if a requested term is on the receiving side of an arrow. If it is, 
	 *  then the term is stored and the entry is no longer added to the draw instructions for the main diagram. They are instead added to 
	 *  the map that will be returned. Once the detected term is found on the sending side of the arrow and deactivated, then the entries 
	 *  again added to the main String Buffer containing the drawing instructions. The hashMap of minimized sections is then returned at the end.
	 */
	public HashMap<String, StringBuffer> printMin(String svgFile, String endObject) {
		
		/*** Change this function later to print to file ***/
		IPath path = ResourcesPlugin.getPlugin().getStateLocation();
		System.out.println("Resource Location: " + path);
		OutputStream png = null;
		
		HashMap<String, StringBuffer> minSectionMap = new HashMap<String, StringBuffer>();
		
		try {
			//png = new FileOutputStream(svgFile);
			png = new FileOutputStream(path.toFile().getPath() + File.separator + "compact.png");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		csb = new StringBuffer();
		//System.out.println("csb = " + csb);
		csb.append("@startuml\n");
		csb.append("hide footbox\n");
		csb.append("skinparam sequenceParticipantBackgroundColor #ffb3b3\n"); //800080\n"); //FFFFBB\n");
		csb.append("skinparam sequenceParticipantBorderColor #800080\n"); //101010\n");
		csb.append("skinparam sequenceArrowColor #0000b3\n"); //000000\n");
		csb.append("skinparam sequenceLifeLineBorderColor #b300b3\n");//808080\n");
		
		
//		System.out.println(endObject);
		Set<Integer> eventIds = tmc.keySet();
		
		ArrayList<Integer> RemoveIndexList = new ArrayList<Integer>();
		boolean inMinimizedZone = false;
		
		String ObjectToBeTerminated = "";
		
		ArrayList<String> termList = createTermList(endObject);
		
		StringBuffer minimizedSectionString = new StringBuffer();
		ArrayList<StringBuffer> minimizedSections = new ArrayList<StringBuffer>();
		ArrayList<String> minimizedSectionNames = new ArrayList<String>();
		int numTerms = 0;
		String completeTerm = "";
		String parentTerm = "";
		for (Integer e:eventIds) {
			String entry = tmc.get(e);
			// First ignore SYSTEM to SYSTEM calls and returns (MVC problem)
			if ( tmc.get(e).contains("\"SYSTEM\" -> \"SYSTEM\" : SYSTEM")
					|| tmc.get(e).contains("\"SYSTEM\" <-- \"SYSTEM\"")
					|| tmc.get(e).contains("activate \"SYSTEM\"") ) {
				//System.out.println(tmc.get(e));
				continue;
			}
			
			if(inMinimizedZone){
				if(entry.toLowerCase().contains(ObjectToBeTerminated.toLowerCase()) && entry.toLowerCase().contains("deactivate " + '"' +ObjectToBeTerminated.toLowerCase() + '"') && (entry.toLowerCase().contains(parentTerm.toLowerCase()))){
					inMinimizedZone = false;
					csb.append(tmc.get(e)+"\n");
					minimizedSectionString.append(tmc.get(e)+"\n");
					minimizedSections.add(minimizedSectionString);
					
					numTerms++;
					completeTerm = "Sub-diagram "+numTerms+ "--: " + completeTerm;
					minSectionMap.put(completeTerm, minimizedSectionString);
					
					
					
					minimizedSectionString = new StringBuffer();
					parentTerm = "";
				}
				else{
					minimizedSectionString.append(tmc.get(e)+"\n");
				}
				
			}
			else{
				if((termFoundInEntry(termList, entry)) && (entry.toLowerCase().contains("->")) && !(endObject.toLowerCase().equals(""))){
//					System.out.println("contains startObject");
					parentTerm = findParentTerm(entry);
					RemoveIndexList.add(e);
					inMinimizedZone = true;
					
					
					String newEntry = "";
					for(int i = 0; i < entry.length(); i++){
//						System.out.println(entry.charAt(i));
						if(entry.charAt(i)=='#'){
							newEntry = newEntry + "#ff3399";
							i = i + 6;
						}
						else{
							newEntry = newEntry + entry.charAt(i);
						}
					}

					csb.append(newEntry+"\n");
					minimizedSectionString.append(newEntry+"\n");
					int termIndex = entry.toLowerCase().indexOf(FindMinTerm(termList, entry));
//					System.out.println(termIndex);
					completeTerm = "";
					boolean completedTerm = false;
					while(completedTerm == false){
//						System.out.println(entry.charAt(termIndex));
						if(entry.charAt(termIndex) != '"' && entry.charAt(termIndex) != '\n'){
							completeTerm = completeTerm + entry.charAt(termIndex);
						}
						else{
							completedTerm = true;
						}
						termIndex++;
					}
					
					minimizedSectionNames.add(completeTerm);
					
					ObjectToBeTerminated = findObjectName(entry);
				}
				else{
					csb.append(tmc.get(e)+"\n");
//					System.out.println(tmc.get(e) + "\n");
				}
			}	
		}
		
		csb.append("@enduml\n");
		//System.out.println("@enduml");
		String source = csb.toString();
		
		SourceStringReader reader = new SourceStringReader(source);
		try {
			//FileOutputStream os = new FileOutputStream(svgFile);
			//String success = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
			//os.close();
			
			FileFormatOption option = new FileFormatOption(FileFormat.PNG);
			String desc = reader.generateImage(png, option);  
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (OutOfMemoryError om) { //OutOfMemoryError om
			/*final ByteArrayOutputStream os = new ByteArrayOutputStream();
			FileFormatOption option = new FileFormatOption(FileFormat.SVG);
			try {
				FileOutputStream os = new FileOutputStream(svgFile);
				String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
				os.close();
				// The XML is stored into svg
				//final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
			} catch (Exception e) { System.out.println("Problem in generating svg image too!"); }
			*/
			MessageDialog.openError(new Shell(Display.getCurrent()), 
					"Error", "OutOfMemoryError: Unable to generate huge image.\n"
							+ "Either limit the events or start eclipse with more heap space");
		}

		//printRMC();
		
		return minSectionMap;
	}


	
	/*
	 * Small helper function used to find out what the activating object is in an entry
	 */
	public String findParentTerm(String entry){
		String parent = "";
		int quoteCount = 0;
		for(int i = 0; i < entry.length(); i++){
			if(entry.charAt(i) == '"'){
				quoteCount++;
				if(quoteCount == 2){
					return parent;
				}
			}
			else{
				parent = parent + entry.charAt(i);
			}
		}
		
		
		return parent;
	}

	
	/*
	 * Helper method used to find the full name of the object that begins a to-be-compacted sub-diagram. Useful in the case that the user enters a more
	 * generic name instead of specifying an object or method number.
	 */
	public static String findObjectName(String fullString){
		String name = "";
		int quoteCount = 0;
		for(int i = 0; i < fullString.length(); i++){
			if((quoteCount == 3) && (fullString.charAt(i) != '"')){
				name = name + fullString.charAt(i);
			}
			if(fullString.charAt(i) == '"'){
				quoteCount++;
			}
		}
		
		return name;
	}	
	
	
	/*
	 * Helper method used to find if the drawing command entry contains a object or method that the user requested be a compaction point.
	 */
	public boolean termFoundInEntry(ArrayList<String> termList, String entry){
		
		for(String term : termList){
			if(entry.toLowerCase().contains(term.toLowerCase())){
				return true;
			}
		}
		
		return false;
	}
	
	
	/*
	 * Helper method used to find which of the user requested terms is within the drawing command entry.
	 */
	public String FindMinTerm(ArrayList<String> termList, String entry){
		for(String term : termList){
			if(entry.toLowerCase().contains(term.toLowerCase())){
				return term;
			}
		}
		return "";
		
	}
	
	
	
	/*
	 * Helper method to parse apart the list of object and method names that the user types in and put each term into an ArrayList for easy access.
	 */
	public ArrayList<String> createTermList(String terms){
		ArrayList<String> termList = new ArrayList<String>();
		
		String singleTerm = "";
		for(int i = 0; i < terms.length(); i++){
			if (terms.charAt(i) == ','){
				if(singleTerm.contains(":")){
					termList.add(singleTerm);
				}
				else{
					termList.add(singleTerm + ":");
					termList.add(singleTerm + '"');
				}
				
				singleTerm = "";
			}
			else if(terms.charAt(i) != ' '){
				singleTerm = singleTerm + terms.charAt(i);
			}
		}
		
		if(singleTerm.contains(":")){
			termList.add(singleTerm);
		}
		else{
			singleTerm = singleTerm + ":";
			termList.add(singleTerm + '"');

			termList.add(singleTerm);
		}
		
		
		return termList;
	}

	
	public void printInteractions(String cl, String eb) {

		//System.out.println(cl);
		//System.out.print("* " + eb);

		HashMap<String,HashSet<String>> map 
							= new HashMap<String,HashSet<String>>();

		StringTokenizer st = new StringTokenizer(eb, "\n");
		while (st.hasMoreTokens()) {

			String line = st.nextToken();
			if ( !(line.endsWith("     ")) )	// Five white spaces are used to identify compacted events
				continue; // skip line and continue the while loop

			String[] tokens = line.split(" ");
			String caller = tokens[0];
			String object = tokens[2];

			if ( map.containsKey(caller) ) {
				HashSet<String> objects = map.get(caller);
				objects.add(object);
				map.put(caller,objects);
			}
			else {
				HashSet<String> objects = new HashSet<String>();
				objects.add(object);
				map.put(caller,objects);
			}
		}
		if ( checkDisjoint(map) ) {
			cl = cl.replace(" ", " \u2297 ");
		}
		else {
			//cl = cl.replace("\u2297", "\u22C8"); // \u2295 oplus
			if ( checkInteference(cl) ) {	
				cl = cl.replace(" ", " \u22C8  ");
			}
			else {
				cl = cl.replace(" ", " \u2295  ");
			}
		}
		
		csb.append("\nloop " + cl + "\n");
		csb.append(eb + "\n");
		csb.append("end\n\n");
		
	}

	public boolean checkDisjoint(HashMap<String,HashSet<String>> map) {
		
		Set<String> callers = map.keySet();
		// Calculate the size individually
		int indvSize = 0, totalSize = 0;
		HashSet<String> mt = new HashSet<String>();

		for (String c:callers) {
			HashSet<String> t = map.get(c);
			indvSize += t.size();
			mt.addAll(t);
			//System.out.println("objects = " + c + "  " + t.toString());
			//System.out.println("indvSize = " + indvSize);
		}
		totalSize = mt.size();
		//System.out.println("totalSize = " + totalSize);
		//System.out.println("Merged set = " + mt.toString());

		if ( totalSize == indvSize)
			return true;
		else 
			return false;
	}

	public boolean checkInteference(String cl) {

		boolean flag = false; // Initially assume no interference

		// Tokenize cl and extract labels
		String[] labels = cl.split(" ");

		// Look up and fetch entries from rmc
		for (int i=0; i<labels.length; i++) {
			ArrayList<CNode> rmi = rmc.get(labels[i]);

			for (int j=0; j<labels.length; j++) {
				//System.out.println(labels[j] + "   " + rmc.get(labels[j]));
				if (i != j) {
					ArrayList<CNode> rmj = rmc.get(labels[j]);
					for (int m=0; m<rmi.size(); m++) {
						int sei = ((CNode) rmi.get(m)).getStartEvent();
						int eei = ((CNode) rmi.get(m)).getEndEvent();
						String oi = ((CNode) rmi.get(m)).getObject();

						for (int n=0; n<rmj.size(); n++) {
							int sej = ((CNode) rmj.get(n)).getStartEvent();
							int eej = ((CNode) rmj.get(n)).getEndEvent();
							String oj = ((CNode) rmj.get(n)).getObject();

							if ( (sei<sej) && (sej<eei) && (oi.equals(oj)) )
								return true;
						}
					}
				}
			}
		}
		return false;
	}

	public void printRMC() {

		Set<String> regex = rmc.keySet();
		for (String r:regex) {
			System.out.println(r + " --------> ");
			ArrayList<CNode> rmnodes = rmc.get(r);
			for (int i=0; i<rmnodes.size(); i++)
				rmnodes.get(i).printNode();
		}
	}

	public void printTMC() {

		Set<Integer> eventIds = tmc.keySet();
		for (Integer e:eventIds) {			
			System.out.println(tmc.get(e));
		}
	}

	public static void drawSequenceDiagram() {

		IPath path = ResourcesPlugin.getPlugin().getStateLocation();
		File seqDiagFile = new File(path.toFile().getPath() + File.separator + "sequence.txt");
		//System.out.println("Temp location = " + path.toFile().getPath());
		
		String s = null;
		String command = "java -jar plugins" + File.separator
								+ "plantuml.jar "
								+ "-o /home/icts/Downloads/ "
								//+ "-o \"" + path.toFile().getPath() + "\" "
								+ "\""
								+ seqDiagFile.getPath() 
								+ "\""
								+ " -encoding=utf8";
		System.out.println(command);

		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader stdout = new BufferedReader(
										new InputStreamReader(p.getInputStream()));

			BufferedReader stderr = new BufferedReader(
										new InputStreamReader(p.getErrorStream()));

			// Read the output from the command
			while ( (s=stdout.readLine()) != null) {
				System.out.println(s);
			}
			while ( (s=stderr.readLine()) != null) {
				System.out.println(s);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static void drawCompactSequenceDiagram() {

		String s = null;
		String command = "java -jar plantuml.jar compact.txt -encoding=utf8";

		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader stdout = new BufferedReader(
										new InputStreamReader(p.getInputStream()));

			BufferedReader stderr = new BufferedReader(
										new InputStreamReader(p.getErrorStream()));

			// Readthe output from the command
			while ( (s=stdout.readLine()) != null) {
				System.out.println(s);
			}
			while ( (s=stderr.readLine()) != null) {
				System.out.println(s);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}

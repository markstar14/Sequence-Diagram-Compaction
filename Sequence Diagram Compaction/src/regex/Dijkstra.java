/*
 * Aum Amriteswaryai Namah
 *
 * File: Dijkstra.java
 * Description: Implements Dijkstra's ssp algorithm
 *
 */

package regex;

import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.*;
import java.util.StringTokenizer;

class Vertex implements Comparable<Vertex>
{
    public final String name;
    public Edge[] adjacencies;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;
    public Vertex(String argName) { name = argName; }
    public String toString() { return name; }
    public int compareTo(Vertex other)
    {
        return Double.compare(minDistance, other.minDistance);
    }
}

class Edge
{
    public final Vertex target;
    public final double weight;
    public Edge(Vertex argTarget, double argWeight) { 
		target = argTarget; 
		weight = argWeight; 
	}
}

public class Dijkstra
{
    public static void computePaths(Vertex source)
    {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
      	vertexQueue.add(source);

		while (!vertexQueue.isEmpty()) {
	    	Vertex u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u.adjacencies)
            {
				try { 
	                Vertex v = e.target;
	                double weight = e.weight;
	                double distanceThroughU = u.minDistance + weight;
					if (distanceThroughU < v.minDistance) {
					    vertexQueue.remove(v);
					    v.minDistance = distanceThroughU ;
					    v.previous = u;
					    vertexQueue.add(v);
					}
				} catch (NullPointerException npe) { }
            }
        }
    }

    public static List<Vertex> getShortestPathTo(Vertex target)
    {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);
        Collections.reverse(path);
        return path;
    }

    //public static String createGraph(String filename, int len)
    public static String createGraph(String graph, int len)
    {
		int numEdges = 0;
		Vertex v[] = new Vertex[len+1];
		for (int i=0; i<=len; i++) {
			v[i] = new Vertex(Integer.toString(i)); // create new vertex
			v[i].adjacencies = new Edge[len-i]; // To connect to all following vertices
		}
		for (int i=0; i<len; i++) {	// No edge to be added from the last vertex
			//System.out.println("i="+i+", i+1="+(i+1));
			v[i].adjacencies[0] = new Edge(v[i+1], 1); 
			numEdges++;
		}

		String strLine;
		String token;

		StringTokenizer stg = new StringTokenizer(graph, "\n");

		while ( stg.hasMoreTokens() ) {
			strLine = stg.nextToken();
			StringTokenizer st = new StringTokenizer(strLine, "->");
			token = st.nextToken();
			int src = Integer.parseInt( token );

			int flag = 0; 	// Set to 1 when end of line is reached
			int j = 1;		// Adjacency list counter starts

			do {
				token = st.nextToken();
				if ( token.equals("$") )
					flag = 1;
				else {
					int dest = Integer.parseInt(token);
					if (dest != src+1) {
						//System.out.println("src="+src+", dest="+dest);
						v[src].adjacencies[j] = new Edge(v[dest], 1);
						j++;
						numEdges++;
					}
				}
			} while (flag == 0);
		}
		//System.out.println("Number of edges added = " + numEdges);

        computePaths(v[0]);
		//System.out.println("Distance from " + v[0] + " to " + v[len] + ": " + v[len].minDistance);
	    List<Vertex> path = getShortestPathTo(v[len]);

		// System.out.println(path.toString());
		return path.toString();
    }
}

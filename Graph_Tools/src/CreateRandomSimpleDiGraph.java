import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import Graph.Graph;
import Graph.Vertex;

/**
 * This program creates a new random simple di-graph
 * 
 * It guarantees no self-loops and guarantees that every vertex
 * can reach every other vertex.
 * 
 * graphs/Graph_File.txt
 * -------------------------
 * <vertex id> <xpos> <ypox>
 * ...
 * $
 * <v1> <v2> <weight>
 * ...
 * -------------------------
 * 
 * @author Thomas Ortiz (tdortiz)
 */
public class CreateRandomSimpleDiGraph {

    public static void main(String[] args) {        
        int maxNodes = 5000;
        int maxExtraEdges = 10;
        
        // Get user input
        Scanner in = new Scanner(System.in);
        try { 
            System.out.print("Number Of Nodes? ");
            maxNodes = in.nextInt();
            System.out.print("Max Extra Edges To Add Per Vertex? ");
            maxExtraEdges = in.nextInt();
        } catch( InputMismatchException e){
            System.out.println("Invald input - insert a number");
            System.exit(1);
        }
        in.close();
        
        // Create the random graph
        Graph g = new Graph();
        createRandomGraph(g, maxNodes, maxExtraEdges);
        
        // Print the new graph to an output file
        System.out.println("Writing Graph with " + maxNodes + " nodes to " + "graphs/graph_" + maxNodes + ".txt" );
		printGraphToFile(g, "graphs/graph_" + maxNodes + ".txt");
        System.out.println("Done Creating " + "graphs/graph_" + maxNodes + ".txt");
    }
    
    /**
     * Adds random vertices and edges to the graph. 
     * 
     * Vertices are guaranteed to be linked both ways to each other: a-->b and b--a
     * 
     * @param maxVertices max amount of vertices you want in the graph
     * @param minWeight minimum weight of an edge
     * @param maxWeight maximum weight of an edge
     */
    public static void createRandomGraph(Graph g, int maxVertices, int maxExtraEdges) {
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        vertices.addAll( g.graph.vertexSet() );
        DecimalFormat df = new DecimalFormat("#.##");
        
        Random r = new Random();
        int id = 0;
        
        // Ensure we have at least 1 vertex in the graph before we go in
        g.addVertex("" + id, (int) r.nextInt(600), (int) r.nextInt(600));
        vertices.add(g.idToVertex.get(""+id));
        id++;
        
        // Create up to <maxVertices> vertices all vertices are reachable from every other vertex
        for(int i = g.graph.vertexSet().size(); i < maxVertices; i++){
            // get a random vertex for the vertex to be a neighbor to
            Vertex v = vertices.get( r.nextInt(vertices.size()) );
            
            g.addVertex("" + id, r.nextInt(600), r.nextInt(600));
            Vertex newVertex = g.idToVertex.get(""+id);
            vertices.add(newVertex);
            
            id++;
            
            
            // Weight the new edge
            double newWeight = (float) Math.hypot(newVertex.x - v.x, newVertex.y - v.y);
            newWeight = Double.parseDouble(df.format(newWeight));
            
            // Add the edge to the graph with a weight
            g.addEdgeWithWeight(newVertex, v, newWeight);
            g.addEdgeWithWeight(v, newVertex, newWeight);
        }
        
        // Add extra edges randomly!
        for(Vertex v : vertices){
        	for(int i = 1; i <= r.nextInt(maxExtraEdges)+1; i++){
        		int randomIndex = r.nextInt(vertices.size());
        		Vertex neighbor = g.idToVertex.get(""+randomIndex);
               
        		// get another neighbor if we picked ourselves
        		while( neighbor.id.equals(v.id) ){
        			neighbor = g.idToVertex.get(""+r.nextInt(vertices.size()));
        		}
        		
        		// Weight the new edge
        		double newWeight = (float) Math.hypot(v.x - neighbor.x, v.y - neighbor.y);
        		newWeight = Double.parseDouble(df.format(newWeight));
        		
        		// Add the edge to the graph with a weight
        		g.addEdgeWithWeight(v, neighbor, newWeight);
        		g.addEdgeWithWeight(neighbor, v, newWeight);
        	}
    	}
        
    }
    
    /**
     * Prints the graph out to the command line
     * @throws IOException
     */
    public static void printGraph(Graph g){
        Set<Vertex> vertices = g.graph.vertexSet();
        Set<DefaultWeightedEdge> edges = g.graph.edgeSet();
        
        for( Vertex v : vertices ){
            System.out.println(v.id + " <" + v.x + ", " + v.y + ">" );
        }
        
        for( DefaultWeightedEdge e : edges ){
            System.out.println( e );
        }
    }
    
    /**
     * Prints out a graph to a user specified file
     * 
	 * File is expected to be like so:
	 * 
	 * vertex_id x y
	 * ...
	 * $
	 * VertexA VertexB Weight
	 * ...
	 * 
     * @param fileName to write file to
     * @throws IOException
     */
    public static void printGraphToFile(Graph g, String fileName){
        BufferedWriter bw = null;
        FileWriter fw = null;
        File file = new File(fileName);

        // if file doesn't exists, then create it
        try { 
        	if (!file.exists()) {
                file.createNewFile();
            }

            // true = append file, false = create new file
            fw = new FileWriter(file.getAbsoluteFile(), false);
            bw = new BufferedWriter(fw);
            
            Set<Vertex> vertices = g.graph.vertexSet();
            Set<DefaultWeightedEdge> edges = g.graph.edgeSet();
            
            // Print out each vertex
            for( Vertex v : vertices ){
                bw.write(v.id + " " + (int) v.x + " " + (int) v.y + "\n" );
            }
            
            bw.write("$\n");
            
            // Print out each edge
            for( DefaultWeightedEdge e : edges ){
                bw.write( g.graph.getEdgeSource(e) + " " + g.graph.getEdgeTarget(e) + " " + g.graph.getEdgeWeight(e) + "\n" );
            }
            
            // Close writers
            bw.close();
        } catch (IOException e){
        	System.out.println(e.getMessage());
        }
    }
}
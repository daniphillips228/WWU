/* 
 * Routes.java
 *
 * The driver program for CSCI 241's Programming Assignment 2
 *
 * Reads two text files and takes source, destination, and travel choice from the user
 * 
 * Moushumi Sharmin
 * Nov 2015.  
 *
 *
 * ----------------------------------------------------------------------------
 *
 * usage:
 *
 * java Routes vertexFilename edgeFilename
 *
 * where the arguments are
 * 
 *   vertexFileName        a plaintext file from which your program will read vertices
 *   edgeFileName          a plaintext file from which your program will read edges and weights
 *   
 *
 * After reading in the graph, it will prompt the user for source and destination and preferred optimization parameter
 * 1 = shortest route, 2 = cheapest route, 3 = fastest route, 4 = display all option 
 * 
 */



import java.util.*;
import java.io.*;
import java.util.Scanner;

public class Routes {
	
	public static void main(String[] args) {
		if(args.length != 2) {
			System.err.println("USAGE: java Routes <vertex_file> <edge_file>");
			System.exit(1);
		}

		MyGraph g = readGraph(args[0],args[1]);

		Collection<Vertex> v = g.loadVertices();
      
      // Enter code for printing or displaying the graph
      
      System.out.println("The graph");
      Iterator<Vertex> StartNodes = v.iterator();
      while (StartNodes.hasNext()){
         Collection<Vertex> DirectedEdges;
         Vertex next = StartNodes.next();
         DirectedEdges = g.findAdjacentVertices(next);
         System.out.println(next+": "+DirectedEdges);
      }
      System.out.println();
      
      // Your code for displaying the graph ends here
      
		Scanner console = new Scanner(System.in);
		for(;;) {
			System.out.print("Please specify the Start vertex: ");
         String startVertex = console.nextLine();
			Vertex start_point = new Vertex(startVertex, 0, startVertex);
			
			if(!v.contains(start_point)) {
				System.out.println("No such vertex in the graph.");
				System.exit(0);
			}
			
			
			System.out.print("Please specify the Destination vertex: ");
			Vertex end_point = new Vertex(console.nextLine(), 10000000, null);
			
			if(!v.contains(end_point)) {
				System.out.println("No such vertex in the graph.");
				System.exit(1);
			}
			
			System.out.print("Optimization parameter? (1 = shortest route, 2 = cheapest route, 3 = fastest route, 4 = display all options) ");
			Scanner in = new Scanner(System.in);
			int choice = in.nextInt();
			if(choice < 1 && choice > 4) {
				System.out.println("Invalid option");
				System.exit(2);
			}
			
         System.out.println();
			List<Vertex> route = new ArrayList<Vertex>();
			int length;
			if (choice<4){
				length = g.findRoute(start_point, end_point, route, choice);
				for(Vertex hop : route)
	   				System.out.print(hop+" ");
	   			System.out.println(length);
			}
			else {
				for (int i=1; i<=3; i++){
					length = g.findRoute(start_point, end_point, route, i);
					for(Vertex hop : route)
		   				System.out.print(hop+" ");
                     System.out.println();
		   			System.out.println(length);
                  System.out.println();
				}
			}
		}
	}

   
   /* Reads vertex and edge information
   */
		
	public static MyGraph readGraph(String f1, String f2) {
		Scanner s = null;
		try {
			s = new Scanner(new File(f1));
		} catch(FileNotFoundException e1) {
			System.err.println("FILE NOT FOUND: "+f1);
			System.exit(2);
		}

		Collection<Vertex> v = new ArrayList<Vertex>();
		while(s.hasNext())
			v.add(new Vertex(s.next(), 100000, ""));

		try {
			s = new Scanner(new File(f2));
		} catch(FileNotFoundException e1) {
			System.err.println("FILE NOT FOUND: "+f2);
			System.exit(2);
		}

		Collection<Edge> e = new ArrayList<Edge>();
		while(s.hasNext()) {
			try {
				Vertex a = new Vertex(s.next(), 1000000, null);
				Vertex b = new Vertex(s.next(), 1000000, null);
				int distance = s.nextInt();
            int time_needed = s.nextInt();
            int ticket_price = s.nextInt();
				e.add(new Edge(a,b,distance,time_needed,ticket_price));
			} catch (NoSuchElementException e2) {
				System.err.println("EDGE FILE FORMAT INCORRECT");
				System.exit(3);
			}
		}

		return new MyGraph(v,e);
	}
}
/* 
 * MyGraph.java
 *
 * A representation of a graph.
 * Assumes that we do not have negative cost edges in the graph.
 *
 * Students may only use functionality provided in the packages
 *     java.util 
 *     java.io
 * 
 * Use of any additional Java Class Library components is not permitted 
 * 
 * Gavin Harris
 */

import java.util.*;
import java.io.*;


public class MyGraph implements Graph {

   	private Collection<Vertex> myVertices;	//the vertices in this graph
   	private Collection<Edge> myEdges;	//the edges in this graph
      private Set<Vertex> newRoute;
      private Set<Vertex> exploreSet;
      private List<Vertex> NodesToRemove;

   	/**
   	 * Creates a MyGraph object with the given collection of vertices
   	 * and the given collection of edges.
   	 * @param v a collection of the vertices in this graph
   	 * @param e a collection of the edges in this graph
   	 */
   	public MyGraph(Collection<Vertex> v, Collection<Edge> e) {

   	   myVertices = v;
         myEdges = e;
                
   	}   
   
   	/** 
   	 * Return the collection of vertices of this graph
   	 * @return the vertices as a collection (which is anything iterable)
   	 */
   	public Collection<Vertex> loadVertices() {

   		return myVertices;   
   	}
   
   	/**
   	 * Return the collection of edges of this graph
   	 * @return the edges as a collection
   	 */
   	public Collection<Edge> loadEdges() {

   		return myEdges;   
   	}
   
   	/** 
   	 * Return a collection of vertices adjacent to a given vertex v.
   	 *   i.e., the set of all vertices w where edges v -> w exist in the graph.
   	 * @param v one of the vertices in the graph
   	 * @return a collection of vertices adjacent to v in the graph
   	 */
   	public Collection<Vertex> findAdjacentVertices(Vertex a) {

         ArrayList<Vertex> VerticesAdjacent = new ArrayList<Vertex>();

         Iterator<Edge> Edges = myEdges.iterator();
         while(Edges.hasNext()){
            Edge edge = Edges.next();
            if(edge.from.equals(a)){
               VerticesAdjacent.add(edge.to);
            }
         }
         return VerticesAdjacent;   
   	}
   
   	 /**
      * Test whether vertex end_point is adjacent to vertex start_point (i.e. start_point -> end_point) in a directed graph.
      * @param start_point, one vertex
      * @param end_point, another vertex
      * @return an array which will contain distance, time_needed, and ticket_price of edge if there is a directed edge from start_point 
      * to end_point in the graph 
      * Return -1 otherwise.
      * (Including returning -1 if one of the two vertices does not exist.)
      * Assumes that we do not have negative cost edges in the graph.
      */
   	public int[] checkIsAdjacent(Vertex a, Vertex b) {

         Collection<Vertex> ADJvert;
         int[] val=new int[10];

         ADJvert = findAdjacentVertices(a);
         if(ADJvert.contains(b)){
            Iterator<Edge> Edges = myEdges.iterator();
            while(Edges.hasNext()){
               Edge edge = Edges.next();
               if(edge.from.equals(a) && edge.to.equals(b)){
                  //val[1]=(edge.distance);
                  val[1]=(edge.time_needed);
                  val[2]=(edge.ticket_price);
                  val[3]=(edge.distance);
                  //val[3]=(edge.time_needed);
               }
            }
         }else{
             val[1]=(-1);
             val[2]=(-1);
             val[3]=(-1);
            return val;
         }
   		return val;   
   	}
   	/**
   	 * Returns the shortest route from start_point to end_point in the graph.  
   	 * Assumes positive edge weights.
   	 * @param start_point the starting vertex
   	 * @param end_point the destination vertex
   	 * @param route a list in which the route will be stored, in order, the first
   	 * being the start vertex and the last being the destination vertex.  the
   	 * list will be empty if no such route exists.  
   	 * @param choice (1 = shortest route, 2 = cheapest route, 3 = fastest route)
   	 * NOTE: the list will be cleared of any previous data.
   	 * @return the length of the shortest route from start_point to end_point, -1 if no such path
   	 * exists.
   	 */
   	public int findRoute(Vertex start_point, Vertex end_point, List<Vertex> route, int choice) {

         //Dijsktra
         
         Collection<Vertex> neighbors;
         newRoute = new HashSet<Vertex>();
         exploreSet = new HashSet<Vertex>();
         
         //find the neighbors for startpoint and add to exploreSet to begin dijkstra
         neighbors = findAdjacentVertices(start_point);
         exploreSet.add(start_point);
         
         while (exploreSet.size() > 0) {
            
            //find lowest value of neighbor to explore first
            Vertex node = getMinimum(exploreSet);
            //build new route with node we are exploring
            newRoute.add(node);
            //remove node from the explore set so we don't find it again
            exploreSet.remove(node);
            //find the minimal distance using users choice of what is wanted
            findMinimalDistances(node, choice);
   
         }
         int lowestValue = 100000;
         String lowestValueRoute = "";
         int secondLowest = 100000;
         String secondLowestRoute = "";
         int thirdLowest = 100000;
         String thirdLowestRoute = "";
         
         System.out.println("All possible routes: ");
         System.out.println();
         //search through all routes and seperate them by which ones end at endpoint
         for (Vertex x : newRoute){
            if (x.equals(end_point)){
               System.out.println(x.route+ " = "+x.value);
               if (lowestValue > x.value){
                  thirdLowest = secondLowest;
                  thirdLowestRoute = secondLowestRoute;
                  secondLowest = lowestValue;
                  secondLowestRoute = lowestValueRoute;                  
                  lowestValue = x.value;
                  lowestValueRoute = x.route;
               }else{
                  if(secondLowest > x.value){
                     thirdLowest = secondLowest;
                     thirdLowestRoute = secondLowestRoute;
                     secondLowest = x.value;
                     secondLowestRoute = x.route;
                     
                  }else{
                     if(thirdLowest > x.value){
                        thirdLowest = x.value;
                        thirdLowestRoute = x.route;
                     }
                  }
               }
            }
         }
         
         if ( lowestValue != 100000){
            System.out.println();
            System.out.println("Best route: "+lowestValue + " "+lowestValueRoute);
         }else{
            System.out.println("No possbile routes");
            return -1;
         }
         
         if ( secondLowest != 100000){
            System.out.println("Second best route: "+secondLowest+ " "+secondLowestRoute);
         }
         
         if ( thirdLowest != 100000){
            System.out.println("Third best route: "+thirdLowest+ " "+thirdLowestRoute);
         }
         System.out.println();

   		return lowestValue;   
   	}   

      //looking at the set of adjacent vertexs and seeing which one has the lower .value
      //we will choose the node that has the lower value to explore   
      private Vertex getMinimum(Set<Vertex> vertexes) {
         
         Vertex minimum = null;
         
         for (Vertex vertex : vertexes) {
   
            if (minimum == null) {
               
               minimum = vertex;
            
            }else {

               if (vertex.value < minimum.value){
                  minimum = vertex;
               }
            }
         }
         //return the adjacent node that is the minimum
         return minimum;
      }	
      
      //check to see if we can improve the value of the node we are visting and update the score.
      //if we can improve, we add the target node with its new information to the exploreSet
      private void findMinimalDistances(Vertex node, int choice) {
       
         NodesToRemove = new ArrayList();
       
         List<Vertex> adjacentNodes = new ArrayList(findAdjacentVertices(node));
         
          //search adjacent nodes for a vertex that no longer needs to be checked
          //if found add to a list that we will remove from the adjacentNodes
          for (Vertex getVertex : adjacentNodes){
            for (Vertex checkRoute : newRoute){

               if (checkRoute.equals(getVertex)){
                  NodesToRemove.add(getVertex);

               }
            }
          }
          //removing adjacent Vertexs that we don't want to check
          for (int x = 0; x < NodesToRemove.size(); x++){
            adjacentNodes.remove(NodesToRemove.get(x));
          }

          //for each adjacent node
          for (Vertex target : adjacentNodes) {
            
            //making sure we don't search for something already in the route
            if (!newRoute.contains(target)){
               
               //checking to see if we can improve the target nodes value and make it lower
               if (target.value > (node.value + checkIsAdjacent(node, target)[choice])) {
                  
                  //creating vertex with new correct info we will add to set
                  Vertex updatedInfo = new Vertex(target.toString(), (node.value + checkIsAdjacent(node, target)[choice]), node.route +" "+ target.toString());// , (node + target)
                  exploreSet.add(updatedInfo);

               }
             }
          }
      }
}
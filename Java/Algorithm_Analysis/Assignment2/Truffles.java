/* 
 * Gavin Harris
 * Assignment2
 * CSCI405, 2pm
 */

import java.util.*;
import java.io.*;
import java.util.Scanner;

public class Truffles {
	private static Node[] nodes;

	
	/* MAIN */
	public static void main(String[] args) {
      if (args.length == 1){
         
         //setup nodes
         int size = setupNodes(args[0]);
         bestCount(nodes, size);
         print(nodes, size);
         
      }else{
         //if no argument is given
         Scanner input = new Scanner(System.in);
         System.out.println("Enter file name to get data from");
         String filename = input.nextLine();
         
         //setup nodes
         int mIntv = setupNodes(filename);
         bestCount(nodes, mIntv);
         print(nodes, mIntv);
      }
	}
   
/*
 *   Algorithm for searching for the best path:
 */
 
   static void bestCount (Node[] nodes, int mIntv){
      
      //update first positions up till minInterval since nothing else can change these values
      for(int x=0;x<mIntv;x++){
         nodes[x].setTotal(nodes[x].getValue());
         nodes[x].setPath(Integer.toString(x));
         nodes[x].setPath2(Integer.toString(nodes[x].getValue()));
      }
      
      //positions up to length - minInterval will look to update values
      for(int x=0;x<(nodes.length -mIntv);x++){
         //try to update positions from minInterval away to 2*minInterval away
         for(int i=0; (i+x+mIntv)<nodes.length;i++){

            if (nodes[i+x+mIntv].getTotal() < nodes[x].getTotal() + nodes[i+x+mIntv].getValue()){
               nodes[i+x+mIntv].setTotal(nodes[x].getTotal() + nodes[i+x+mIntv].getValue());
               nodes[i+x+mIntv].updatePath(nodes[x].getPath(),(i+x+mIntv));
               nodes[i+x+mIntv].updatePath2(nodes[x].getPath2(),nodes[i+x+mIntv].getValue());
            }
         }
      }
      
      //print out results
      int max = -1;
      int position=0;
      for(int y=0;y<nodes.length;y++){
         if (nodes[y].getTotal() > max){
            max = nodes[y].getTotal();
            position = y;
         }
      }
      System.out.println("Optimal Subsequence is...");
      System.out.println(nodes[position].getPath());
      System.out.println(nodes[position].getPath2()+" = "+max);
   }
   
   //Look into file that contains the data, organize into a 2D array of nodes, return the size of dimensions
   static int setupNodes(String filename){
      Scanner s = null;
   		try {
   			s = new Scanner(new File(filename));
   		} catch(FileNotFoundException e1) {
   			System.err.println("FILE NOT FOUND: "+filename);
   			System.exit(2);
   		}

         int mIntv = Integer.parseInt(s.nextLine());
         String line = s.nextLine();
         String[]nuggets = line.split(" ");
         
         nodes = new Node[nuggets.length];
                 
         for(int x=0;x<nuggets.length;x++){        
            nodes[x]= new Node(Integer.parseInt(nuggets[x]));
         }
         
      return mIntv;
   }

//print out the totals of each node
   static void print(Node[] nodes, int mIntv){
      
      System.out.println();
      System.out.println("Min Interval: "+mIntv);
      System.out.print("Pos: ");
      for(int x=0;x<nodes.length;x++){
         if(nodes[x].getTotal() > 9){
            System.out.print(x+"   ");
         }else{
            System.out.print(x+"  ");
         }
      }
      
      System.out.println(); 
      System.out.print("Val: "); 
      for(int x=0;x<nodes.length;x++){
         if(nodes[x].getTotal() > 9){
            System.out.print(nodes[x].getValue()+"   ");
         }else{
            System.out.print(nodes[x].getValue()+"  ");
         }
      }  
      
      System.out.println(); 
      System.out.print("Max: "); 
      for(int x=0;x<nodes.length;x++){
         System.out.print(nodes[x].getTotal()+"  ");
      }    
   }
}
/* 
 * Gavin Harris
 * Assignment1
 * CSCI405, 2pm
 */

import java.util.*;
import java.io.*;
import java.util.Scanner;

public class Assignment1 {
	private static Node[][] nodes;;

	
	/* MAIN */
	public static void main(String[] args) {
      if (args.length == 1){
         
         //setup nodes
         int size = setupNodes(args[0]);
         int max = bestCount(nodes, size);
         print(nodes, size);
         System.out.println(nodes[size-1][size-1].getPath());
         System.out.println("Max Total = "+max);
         
      }else{
         //if no argument is given
         Scanner input = new Scanner(System.in);
         System.out.println("Enter file name to get data from");
         String filename = input.nextLine();
         
         //setup nodes
         int size = setupNodes(filename);
         int max = bestCount(nodes, size);
         print(nodes, size);
         System.out.println(nodes[size-1][size-1].getPath());
         System.out.println("Max Total = "+max);
      }
	}
   
/*
 *   Algorithm for searching for the max total we can get:
 *   Start at (0,0) and look down col 0 to find improvements to the total
 *   Look right at row 0 to find improvements
 *   Go down col 0 and look right at col 1 for improvements
 *   Go right on row 0 and look down at row 1 for improvements
 *   Repeat pattern at (1,1), (2,2), (3,3) ... etc instead of (0,0)
 */
 
   static int bestCount (Node[][] nodes, int size){
      
      int index = 0;

      //setting the total at pos 0,0 to zero instead of the default -1
      nodes[0][0].setTotal(0);

      while(index<size){
      
         //searching downwards at position (0,0),(1,1),(2,2) ... etc
         for (int y=index; y<(size-1); y++){
            if (nodes[y+1][index].getTotal() < nodes[y][index].getTotal() + nodes[y][index].getDown()){
               nodes[y+1][index].setPath(nodes[y][index].getPath(), (y+1), index);
               nodes[y+1][index].setTotal(nodes[y][index].getTotal() + nodes[y][index].getDown());
            }
         }
      
         //searching rightwards at position (0,0),(1,1),(2,2) ... etc
         for (int x=index; x<(size-1); x++){
            if (nodes[index][x+1].getTotal() < nodes[index][x].getTotal() + nodes[index][x].getRight()){
               nodes[index][x+1].setPath(nodes[index][x].getPath(), index, (x+1));
               nodes[index][x+1].setTotal(nodes[index][x].getTotal() + nodes[index][x].getRight());
            }
         }
         
         index++;

         //going right but looking down
         for (int y=index; y<size; y++){
            if (nodes[y][index].getTotal() < nodes[y][index-1].getTotal() + nodes[y][index-1].getRight()){
               nodes[y][index].setPath(nodes[y][index-1].getPath(), y, index);
               nodes[y][index].setTotal(nodes[y][index-1].getTotal() + nodes[y][index-1].getRight());
            }
         }
         //going down but looking right
         for (int x=index; x<size; x++){
            if (nodes[index][x].getTotal() < nodes[index-1][x].getTotal() + nodes[index-1][x].getDown()){
               nodes[index][x].setPath(nodes[index-1][x].getPath(), index, x);
               nodes[index][x].setTotal(nodes[index-1][x].getTotal() + nodes[index-1][x].getDown());
            }
         }
      }
      return nodes[size-1][size-1].getTotal();
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
         
         int i=0;
         int c=0;
         int size = 0;
         String right, down, line;
         String[] nodeList, Right, Down;
         
         //creating the structure of input data
         line = s.nextLine();
         nodeList = line.split("\\(");
         size = nodeList.length -1;
        
         nodes = new Node[size][size];

         for (int x=0; x<size; x++){
            Right = nodeList[x+1].split(" ");
            right = Right[0];
            Down = Right[1].split("\\)");
            down = Down[0];
            
            if (right.equals("_")){
               if (down.equals("_")){
                  nodes[0][x]= (new Node(-10000, -10000));
                  //Done with data
               }else{
                  nodes[0][x]= (new Node(-1, Integer.parseInt(down)));
               }
            }else{ 
               if (down.equals("_")){
                  nodes[0][x]= (new Node(Integer.parseInt(right), -10000));
               }else{
                  nodes[0][x]= (new Node(Integer.parseInt(right), Integer.parseInt(down)));
               }
            }
         }
         c++;
         
         //rest of the lines
         while(s.hasNextLine()){
            line = s.nextLine();
            nodeList = line.split("\\(");
            
            for (int x=0; x<size; x++){
               Right = nodeList[x+1].split(" ");
               right = Right[0];
               Down = Right[1].split("\\)");
               down = Down[0];
               
               if (right.equals("_")){
                  if (down.equals("_")){
                     nodes[c][x]= (new Node(-10000, -10000));
                     //Done with data
                  }else{
                     nodes[c][x]= (new Node(-10000, Integer.parseInt(down)));
                  }
               }else{ 
                  if (down.equals("_")){
                     nodes[c][x]= (new Node(Integer.parseInt(right), -10000));
                  }else{
                     nodes[c][x]= (new Node(Integer.parseInt(right), Integer.parseInt(down)));
                  }
               }
            }
            c++;
         }
      return size;
   }

//print out the totals of each node
   static void print(Node[][] nodes, int size){

      for(int y=0;y<size;y++){
         for(int x=0;x<size;x++){
            if (nodes[y][x].getTotal() < 10){
               System.out.print(" "+nodes[y][x].getTotal()+"  ");
            }else{
               System.out.print(nodes[y][x].getTotal()+"  ");
            }
         }
         System.out.println();
      }
      System.out.println();
   }
}
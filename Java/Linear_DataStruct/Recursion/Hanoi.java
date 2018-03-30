/*
Gavin Harris
Lab 4, Thursday 
12pm
Towers of Hanoi

* * * * * * * * * * * * * * * * * * * 
* compile instructions              *
*                                   *
* javac Hanoi.java                  *
*                                   *
* java Hanoi (1-64)                 *
* run argument any int between 1-64 *
* * * * * * * * * * * * * * * * * * * 

*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Hanoi {
   
   static File inputFile;
   static int disks;
   
   public static void main(String[] args) throws FileNotFoundException{

      if(!handleArguments(args)){
         System.out.println("Error");
      
      }else{
         
         disks = Integer.parseInt(args[0]);
         recursiveHanoi(disks, 's', 'a', 'd');

      }
   }
   /*
      Check to see if arguments were entered corrctly, if true we convert args to integer and
      set to disks variable
   */
   
   static boolean handleArguments(String[] args) {

      if (args.length != 1) {
         System.out.println("Wrong number of command line arguments.");
         return false;
         
      }else{
         disks = Integer.parseInt(args[0]);
         if (disks > 0 && disks <=64){
            return true;
         }else{
            return false;
         }
      }
   }
   
   /*
      Our parameters are the amount of disks, and the char variables for needles.
      If there is one disk we will move it to our destination. Else we will use recursion
      to move n-1 disks from the source to the aux needle. Then we will move the one 
      remaining disk to the destination. Then recursively move n-1 disk to the destination
      then we are done.
   */
   
   static void recursiveHanoi(int disks, char source, char aux, char destination){
  
      if (disks == 1){
         
         System.out.println("Move disk " + disks + " from " + source + " to " + destination);

      }else{
      
         recursiveHanoi(disks-1, source, destination, aux);
         System.out.println("Move disk "+ disks +" from " + source +" to "+ destination);
         recursiveHanoi(disks-1, aux, source, destination);
      }  
   }
}
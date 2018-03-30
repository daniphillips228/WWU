/*
Gavin Harris
Lab 4, Thursday 12pm
Fibonacci Numbers

javac Fibonacci.java                
java Fibonacci

*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Fibonacci {
   
   static File inputFile;
   static int fib_num;
   
   public static void main(String[] args){
      
      Scanner input = new Scanner(System.in);
      System.out.print("Please enter a n value between (0<= n <= 46)");
      int n = input.nextInt();
      if(!handleInput(n)){
         System.out.println("Error, integer not between 0 and 46");
      }else{
         int num = fib(n);
         System.out.println(num);
      }
   }
   
   /*
      Parameter is number entered by user, check to see if it is
      between 0 and 46, return true or false.
   */
   
   static boolean handleInput(int n) {

      if (n >= 0 && n <=46){
         return true;
      }else{
         return false;
      }
   }
   
   /*
      Return 0 for special case, or call the fibaux function
      and return the value from the array.
   */
   
   static int fib(int n){
      
      //special case of n == 0
      if (n == 0){
         return 0;
      }else{
         return fibaux(n)[0];
      }
    }
    
    /*
      Parameter of value entered by user, return base case if 
      n ==1, else use recursion to completion and return array
      of with newest fib number and previous one.
    */
      
   static int[] fibaux(int n){
   
      //base case
      if (n == 1){
         return new int[] {1, 0};
      
      //recursion step
      }else{
         int[] nums = fibaux(n-1);
         int num1 = nums[1];
         int num2 = nums[0];
         
         return new int[]{num2 + num1, num2};
      }   
   }

}
/*
Gavin Harris, CSCI322, 10am, lab4
Files used:
   UpToNoGood.java, JavaThreads.java
   By: Filip Jagodzinski
*/

// imports
import java.util.concurrent.*;
import java.util.*;

// class declaration
public class UpToNoGood {

    // main method declaration
    public static void main(String[] args) {
   
   	/* DO NOT MODIFY -- START
   *************************************************************/
   	// number of accounts. MUST be 100 million
   	int numAccounts = 100000000;
   	
   	// "simulate" an array of doubles ... accountBalances
   	double[] accountBalances = new double[numAccounts];
   	for (int i=0; i<accountBalances.length; i++){
   	    Random rand = new Random();
   	    accountBalances[i] = (double) rand.nextInt(10000);
   	}
   /*************************************************************
      DO NOT MODIFY -- END */ 
   	
   	// get system nanosecond time - start
   	double nonThreadStart = System.nanoTime();
   	
   	// decrease each "account balance" by a tiny amount; x^{0.99}
   	for(int i=0; i<accountBalances.length; i++) {
   	    accountBalances[i] = Math.pow(accountBalances[i], 0.999);
   	}
   	
   	// get system nanosecond time - end
   	double nonThreadEnd = System.nanoTime();
      
      int numThreads = 8;
      double timeStart = 0;
   	double timeEnd = 0;
      int chunkSize = numAccounts/numThreads;

   	try{
       
   	    ExecutorService ex = Executors.newFixedThreadPool(numThreads);
   
   	    // get clock time
   	    timeStart = System.nanoTime();
   	    
   	    // create some number of threads
   	    for (int i = 0; i < numThreads; i++) {
      		
      		// for each iteration of the body's for loop,
      		// calculate the starting and ending indexes
      		int indexStart = i * chunkSize;
      		int indexEnd = (i + 1) * chunkSize;
      		
      		ex.execute(new Runnable() {

      			@Override
      			public void run() {

         			 for (int k = indexStart; k < indexEnd; k++){

   	               accountBalances[k] = Math.pow(accountBalances[k], 0.999);
            
      			    } // end for  
      			} // end run
      		}
            );
   	    } //end for
   
   	    // shut down ExecutorServices. If you issue this command, any and all
   	    // threads that are managed by THIS executor are killed
   	    ex.shutdown();
   	    ex.awaitTermination(1, TimeUnit.MINUTES);
   
   	    // get clock time
   	    timeEnd = System.nanoTime();
   	    
   	 
   	}catch (InterruptedException e){
   
   	    // print out custom error messages
   	    System.out.println("Something went wrong with the threading.");
   	    System.out.println("Sorry, quitting");
   	    System.out.println("Inspect the stack to see what went wrong");
   	    
   	} // end try-catch

   	// output the time needed to perform calculations

   	System.out.println("\nNum of threads        " + numThreads);
   	System.out.println("Non threaded time     " + ((nonThreadEnd - nonThreadStart)/1000000000.0));
    System.out.println("Threaded time         " + ((timeEnd - timeStart)/1000000000.0) + "\n");
   
    }
}
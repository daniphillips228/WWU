//Gavin Harris
//CSCI 145, Winter 2015, Thur 12pm lab
//Lab #3, 1/30/15

/* Borrowed code from
 * Grid Writer program.
 * Author: Chris Reedy (Chris.Reedy@wwu.edu)
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class GridEncrypt {

   // Grid Width
   static int gridWidth;

   
   // Input file object.
   static File inputFile;

   public static void main(String[] args) throws FileNotFoundException {
      if (!handleArguments(args)) {
         System.out.println("Error");
      }
      else {
         Scanner input = new Scanner(inputFile);
         processInput(input);
      }
   }
   
   static final String usage = "Usage: GridEncrypt gridWidth input_file_name";

   /*
    * Validate the command line arguments and do setup based on the
    * arguments. Two command line arguments are expected:
    *   1. A positive integer that sets gridWidth
    *   2. A String that names the input file.
    *
    * Return true if processing was sucessful and false otherwise.
    */
   static boolean handleArguments(String[] args) {
      // Check for correct number of arguments
      if (args.length != 2) {
         //System.out.println(args.length);
         System.out.println("Wrong number of command line arguments.");
         System.out.println(usage);
         return false;
      }
      
      // Get the gridWidth from the first command line argument.
      try {
         gridWidth = Integer.parseInt(args[0]);
      
      } 
      catch (NumberFormatException ex) {
         System.out.println("gridWidth must be an integer.");
         System.out.println(usage);
         return false;
      }
      
      // Open the input file and get its length
      inputFile = new File(args[1]);
      if (!inputFile.canRead()) {
         System.out.println("The file " + args[1] + " cannot be opened for input.");
         return false;
      }
      
      return true;
   }
   
   /*
    * Get and process the input. For the gridWidth call loadUnloadGrid.
    */
   static void processInput(Scanner input) {
      String line = input.nextLine();
    
         // Determine heigth of grid
      int height = line.length() / gridWidth;
        
         // Add one to height if there's a partial last row and
         // set the longColumn
      int longColumn = line.length() % gridWidth;
     
      if (longColumn != 0) {
         height += 1;
        
      }
      else {
         longColumn = gridWidth;
      }
         
      char[][] grid = loadGrid(line, gridWidth, height, longColumn);
   
      unloadGrid(grid, gridWidth, height, longColumn);
     
   }  
   /*
    * Create a new grid and load the input into the grid column by
    * column. Return the created grid.
    * Parameters:
    *   line -- the input string
    *   gridWidth -- the number of columns in the grid
    *   height -- the number of rows in the grid
    *   longColumn -- the number of valid columns in the last row
    */
   static char[][] loadGrid(String line, int gridWidth, int height, int longColumn) {
      char grid[][] = new char[height][gridWidth];
         
      //Load the input data into the grid by ROW
      int charCount = 0;
      for (int row = 0; row < height; row++) {
         for (int column = 0; column < gridWidth; column++) {
            if (row < height - 1 || column < longColumn) {
               grid[row][column] = line.charAt(charCount);
               charCount += 1;
            }
         }
      }
      
      return grid;
   }    
   /*
    * Unload the characters in grid row by row. Characters are 
    * printed on System.out.
    * Parameters:
    *   line -- the input string
    *   gridWidth -- the number of columns in the grid
    *   height -- the number of rows in the grid
    *   longColumn -- the number of valid columns in the last row
    */
   static void unloadGrid(char[][] grid, int gridWidth, int height, int longColumn) {
      for (int column = 0; column < gridWidth; column++) {
         
         if (column <= longColumn-1){
            
            for (int row = 0; row < height; row++) {
               
               System.out.print(grid[row][column]);
            }
         }
         else{   
            
            for (int row = 0; row < height-1; row++) {
               
               System.out.print(grid[row][column]);
            }
         }
      }
   
   }
}
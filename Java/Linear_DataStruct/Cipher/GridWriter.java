/*
 * Grid Writer program.
 * Author: Chris Reedy (Chris.Reedy@wwu.edu)
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class GridWriter {

   // Minimum and maximum widths for the grid.
   static int minGridWidth;
   static int maxGridWidth;
   
   // Input file object.
   static File inputFile;

   public static void main(String[] args) throws FileNotFoundException {
      if (!handleArguments(args)) {
         System.out.println("Error");
      }else {
         Scanner input = new Scanner(inputFile);
         processInput(input);
      }
   }
   
   static final String usage = "Usage: GridWriter min_width max_width input_file_name";

   /*
    * Validate the command line arguments and do setup based on the
    * arguments. Three command line arguments are expected:
    *   1. A positive integer that sets minGridWidth
    *   2. A positive integer that sets maxGridWidth
    *   3. A String that names the input file.
    *
    * Return true if processing was sucessful and false otherwise.
    */
   static boolean handleArguments(String[] args) {
      // Check for correct number of arguments
      if (args.length != 3) {
         System.out.println("Wrong number of command line arguments.");
         System.out.println(usage);
         return false;
      }
      
      // Get the minimum and maximum grid width from the first two
      // command line arguments.
      try {
         minGridWidth = Integer.parseInt(args[0]);
         maxGridWidth = Integer.parseInt(args[1]);
      } catch (NumberFormatException ex) {
         System.out.println("min_width and max_width must be integers.");
         System.out.println(usage);
         return false;
      }
      
      // Open the input file and get its length
      inputFile = new File(args[2]);
      if (!inputFile.canRead()) {
         System.out.println("The file " + args[2] + " cannot be opened for input.");
         return false;
      }
      

      return true;
   }
   
   /*
    * Get and process the input. For each width call loadUnloadGrid.
    */
   static void processInput(Scanner input) {
      String line = input.nextLine();
      
      // Try each width in the appropriate range
      for (int width = minGridWidth; width <= maxGridWidth; width++) {
         // Determine heigth of grid
         int height = line.length() / width;
         
         // Add one to height if there's a partial last row and
         // set the longColumn
         int longColumn = line.length() % width;
         if (longColumn != 0) {
            height += 1;
         } else {
            longColumn = width;
         }
         
         char[][] grid = loadGrid(line, width, height, longColumn);
         System.out.printf("Grid width %d: \"", width);
         unloadGrid(grid, width, height, longColumn);
         System.out.println("\"");
      }
   }
   
   /*
    * Create a new grid and load the input into the grid column by
    * column. Return the created grid.
    * Parameters:
    *   line -- the input string
    *   width -- the number of columns in the grid
    *   height -- the number of rows in the grid
    *   longColumn -- the number of valid columns in the last row
    */
   static char[][] loadGrid(String line, int width, int height, int longColumn) {
      char grid[][] = new char[height][width];
      
      // System.out.printf("line length: %d, height: %d, width: %d, longColumn: %d%n",
      //       line.length(), height, width, longColumn);
         
      //Load the input data into the grid by column
      int charCount = 0;
      for (int c = 0; c < width; c++) {
         for (int r = 0; r < height; r++) {
            if (r < height - 1 || c < longColumn) {
               grid[r][c] = line.charAt(charCount);
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
    *   width -- the number of columns in the grid
    *   height -- the number of rows in the grid
    *   longColumn -- the number of valid columns in the last row
    */
   static void unloadGrid(char[][] grid, int width, int height, int longColumn) {
      for (int r = 0; r < height - 1; r++) {
         for (int c = 0; c < width; c++) {
            System.out.print(grid[r][c]);
         }
      }
      // Special handling for last row
      for (int c = 0; c < longColumn; c++) {
         System.out.print(grid[height - 1][c]);
      }
   }
}
   
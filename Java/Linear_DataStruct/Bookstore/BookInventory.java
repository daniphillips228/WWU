/* Gavin Harris, Thursday 12pm lab
 * WWU: CS145 Winter 2015, Assignment 3
 *
 * BookInventory.java
 *
 * Test the functions of BookCollection class.
 *
 * Dependencies:
 *    Book.java
 *    BookCollection.java
 */
import java.io.*;
import java.util.Scanner;

public class BookInventory {

   public static void main(String args[]) {
   
      // check for the number of command line arguments
      if(args.length != 2){
         System.out.println("Please enter two file names: Books.txt Transactions.txt");
         return; // Terminate program.
      }
   
      BookCollection collection1 = new BookCollection(150);  
   
      /* open the first file and add each book to collection1 */
      getBooks(collection1, args[0]);
      
      /* make some changes to the first book collection */
      changeCollection(collection1, args[1]);
   }

   /* Add new inventory to the stock.
    *
    * The stock on hand of the book with the given ISBN number in the
    * given collection is increased by the given quantity.
    */

   public static void tryNewStock(BookCollection collection, String isbn, int quantity) {
      try {
      
         collection.changeStock(isbn, quantity);
         
      } 
      catch (BookCollection.BookNotFound ex) {
         System.out.println("ISBN " + isbn + " not found in the collection. Cannot change stock.");
      }
   }
  
   public static void ProcessStock(BookCollection collection, String isbn, int quantity) {
      try {
         collection.changeStock(isbn, quantity);
         
      } 
      catch (BookCollection.BookNotFound ex) {
         System.out.println("ISBN " + isbn + " not found in the collection. Cannot change stock.");
      }
   }
   
   
   public static void ProcessOrder(BookCollection collection, String isbn, int quantity, int customer) {
      try {
         collection.changeOrder(isbn, quantity, customer);
         
      } 
      catch (BookCollection.BookNotFound ex) {
         System.out.println("ISBN " + isbn + " not found in the collection. Cannot change stock.");
      }
   } 

   /* Add books from input file to collection. */
   public static void getBooks(BookCollection collection, String fileName) {
      Scanner input = null;
      try {
         input = new Scanner(new File(fileName));
      } 
      catch (FileNotFoundException ex) {
         System.out.println("Error: File " + fileName + " not found. Exiting program.");
         System.exit(1);
      }
   
      while (input.hasNextLine()) {
         String line = input.nextLine();
         Scanner lineData = new Scanner(line);
      
         try {
         
            String isbn = lineData.next();
            double price = lineData.nextDouble();
            int stock = lineData.nextInt();
            Book book = new Book(isbn, price, stock);
            try {
               collection.addBook(book);
            } 
            catch (BookCollection.CollectionFull ex) {
               System.out.println("The collection is full. No more books can be added.");
            } 
            catch (BookCollection.DuplicateBook ex) {
               System.out.println("Duplicate book " + isbn + " not added to the collection.");
            }
         } 
         catch (java.util.InputMismatchException ex) {
            System.out.println("Line: " + line);
            System.out.println("Mismatched token: " + lineData.next());
            throw ex;
         }
      }
   }

   /*    
   search for whether action is stock or order and processes accordingly
   display total value of orders at very end  
   */
   public static void changeCollection(BookCollection collection, String changeFile) {
      Scanner input = null;
      try {
         input = new Scanner(new File(changeFile));
      } 
      catch (FileNotFoundException ex) {
         System.out.println("Error: File " + changeFile + " not found. Exiting program.");
         System.exit(1);
      }
      
      int temp_stock; 
      int customer;
      
      while (input.hasNext()) {
         String type = input.next();
         String isbn = input.next();
         temp_stock = input.nextInt();
         
         if (type.equals("STOCK")) {
            
            ProcessStock(collection, isbn, temp_stock);           
            
         } 
         else if (type.equals("ORDER")) {
         
            customer = input.nextInt();
            
            ProcessOrder(collection, isbn,-temp_stock, customer);
         
         }        
      }
            
      System.out.println();
      System.out.print("Total value of orders filled is $");
      collection.DisplayFinal();
   }    
}
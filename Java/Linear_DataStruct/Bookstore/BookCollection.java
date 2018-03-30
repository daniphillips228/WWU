/* Gavin Harris, Thurs 12pm Lab
 * WWU: CS145 Winter 2015, Assignment 3
 *
 * BookCollection.java
 *
 * Maintain a collection of books.
 *
 * Dependencies:
 *    Book.java, provided
 */

public class BookCollection{

   public class BookNotFound extends IllegalArgumentException {
      public BookNotFound(String msg) {
         super(msg);
      }
   }

   public class DuplicateBook extends IllegalArgumentException {
      public DuplicateBook(String msg) {
         super(msg);
      }
   }

   public class CollectionFull extends IllegalStateException {
      public CollectionFull(String msg) {
         super(msg);
      }
   }

   // Max Limit on size of collection.
   public static final int LIMIT = 200;
   
   //Private members
   private Book[] books;
   private int size = 0;
   private int maxSize = 0;

   //BookCollection Constructor
   public BookCollection(int capacity){
      maxSize = capacity;
      if (maxSize <= LIMIT){
         books = new Book[maxSize];
      }
   }

   /*
      search for book in collection with ISBN, used by addBook and merge
      return null if not found in collection
   */
   private Book findBook(String isbn) {
   
      for (int i = 0; i <= size-1; i++){
      
         if (books[i].getIsbn().equals(isbn)){
            return books[i];
         }
      }
      return null;
   }

   /*
   search the collection for a book with the given ISBn and if found add quantity
   to its stock
   */
   public void changeStock(String isbn, int qty){
   
      Book checkBook = findBook(isbn);
      if (checkBook == null) {
         throw new BookNotFound(isbn);
      }
      checkBook.processStock(qty);
   }
   
   //makes sure book is in collection, calls processOrder
   public void changeOrder(String isbn, int qty, int customer){
   
      Book checkBook = findBook(isbn);
      if (checkBook == null) {
         throw new BookNotFound(isbn);
      }
   
      checkBook.processOrder(qty, customer);
   }

   //return actual number of books in the collection
   public int getSize(){
      return size;
   }
   
   //returns total dollar value of the books in the collection
   public double getStockValue() {
   
      double total = 0;
      for (int x = 0; x <= size-1; x++){
         double StockValue = books[x].getStockValue();
         total = total + StockValue;
      }
      return total;
   }
   
   /*
   adds a new book to the collection, provided there is room in the collection
   and the book is not already there, determined by ISBN
   */
   public void addBook(Book book) {
      
      String is_it_here = book.getIsbn();
      Book check_Book = findBook(is_it_here);
            
      if (check_Book == null){      
         if (size < maxSize){
            books[size] = book;
            size+=1;
         }
      }   
   }
   
   //return the book at the  given index
   public Book objectAt(int index) {
   
      Book current_book = books[index];
      return current_book;
   }
   
   //display the total value of all orders
   public void DisplayFinal (){
   
      books[0].getTotalValue();
   }
}
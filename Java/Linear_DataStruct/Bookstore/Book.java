/* Gavin Harris, Thurs 12pm Lab
 * WWU: CS145 Winter 2015, Assignment 3
 *
 * Book.java
 *
 *
 * A basic class for a Book object.
 */

public class Book{

   public class InsufficientStock extends IllegalArgumentException {
      public InsufficientStock(String msg) {
         super(msg);
      }
   }
    
    // Fields for the Book class

   private String isbn; 
   private double price; 
   private int stock;
   private Queue<BackOrder> backOrders;
   static double totalPrice; 
    
    /*
    * Construct a new book.
    * 
    * Construct a Book object with the given data. The stock for the book
    * is set to zero.
    */
   public Book(String isbn, double price, int stock) {
   
      this.isbn = isbn; 
      this.price = price; 
      this.stock = stock;        
      
      backOrders = new Queue<BackOrder>();    
   }
   
   /*
    * Construct a new Book object by copying the data from an existing
    * Book object.
    */
   public Book(Book other) {
   
      this.isbn = other.isbn; 
      this.price = other.price; 
      this.stock = other.stock;
   
      this.backOrders = other.backOrders;
   
   }
    
    /* Return International Standard Book Number (ISBN) */
   public String getIsbn() {
      return isbn; 
   }
    
    /* Return book price */
   public double getPrice() {
      return price; 
   }
    
    /* Return book stock on hand */
   public int getStock() {
      return stock; 
   }
   
   //print the total value of all orders
   public void getTotalValue(){ 
      System.out.print(totalPrice);  
   }

    /* Return the value of book stock on hand.
    *
    * The value is computed as the stock on hand times
    * the price.
    */  
   public double getStockValue() {
      return stock * price; 
   } 
    // Return a string representation of the book
   public String toString() {
      String s = String.format(
                "%-10s %5.2f %-2d",
                isbn, price, stock);    
      return s; 
   }   
    
    // Set the price 
   public void setPrice(double newPrice){
      price = newPrice; 
   }
    
    // Change the stock on hand.
   public void changeStock(int change) {
      stock+=change;
   }
   
   // increments stock, checks backorders to fill, if found calls processOrder
   public void processStock(int newStock){
      
      int oldStock = stock;
      
      //step 1 increment the stock
      
      stock+= newStock;
      System.out.println("Stock for book "+isbn+" increased from "+oldStock+" to "+stock);
   
      //step 2 search for backorders to fill
      
      while (backOrders.peek()!=null && stock != 0){
         //fill previous backorder
      
         processBackOrder(backOrders.peek().getQty(), backOrders.peek().getCustomer());
      
         backOrders.remove();
      
      }      
   }
    
   /* 
      determines if there is enough stock for the order, if not finds out if
      it can partially order books. Creates backorder if not enough books for
      customers order 
   */
   public void processOrder(int qty, int customer){
    
      if (stock == 0) {
      
         //case 3: add entire order to backorder
      
         System.out.println("Back order for customer: "+customer +" for " + (-qty) + " copies of book " + isbn);            
         backOrders.add(new BackOrder(customer, qty));
      
      }else{
         
         if ((-qty) <= stock){
            //case 1: Can completely fill order
            totalPrice+= (this.price)*(-qty);
            System.out.println("Order filled for customer "+customer+" for "+(-qty)+ " copies of book "+isbn);        
            stock+=qty;
         }else{
         
            //Case 2: can partially fill order
            totalPrice+= ((this.price)*stock);
            System.out.println("Order filled for customer: "+customer+" for "+ stock +" copies of book "+isbn);
            System.out.println("Back order for customer: "+customer +" for " + ((-qty) - stock) + " copies of book " + isbn);
            
            backOrders.add(new BackOrder(customer, (qty+stock)));
            stock=0; 
         }
      }      
   }
   
   /*
      processes the back orders, uses same method as processOrder
   */
   public void processBackOrder(int qty, int customer){
    
      if (stock == 0) {
      
         //case 3: add entire order to backorder
 
         System.out.println("Back order for customer: "+customer +" for " + (-qty) + " copies of book " + isbn);            
         backOrders.add(new BackOrder(customer, qty));
      }else{
         
         if ((-qty) <= stock){
         
            //case 1: Can completely fill order
            
            totalPrice+= ((this.price)*(-qty));
            System.out.println("Back order filled for customer "+customer+" for "+(-qty)+ " copies of book "+isbn);        
            stock+=qty;
         }else{
         
            //Case 2: can partially fill order
            
            totalPrice+= ((this.price)*stock);
            System.out.println("Back order filled for customer: "+customer+" for "+ stock+" copies of book "+isbn);
         
            backOrders.add(new BackOrder(customer, (qty+stock)));
            stock=0; 
         }
      }      
   }
}
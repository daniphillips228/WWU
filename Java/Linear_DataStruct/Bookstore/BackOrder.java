/*
   Gavin Harris, Thursday 12pm Lab
   Assignment 3
*/
public class BackOrder {

   //fields for BackOrder class
   public int customer;
   public int qty;

   public BackOrder(int customer, int qty){
      this.customer = customer;
      this.qty = qty;
   }
   
   /*
   * Construct a new BackOrder object by copying the data from an existing
   * BackOrder object.
   */
   public BackOrder(BackOrder other) {
   
      this.customer = other.customer; 
      this.qty = other.qty;
   }
   
   //return customer number
   public int getCustomer() {
      return customer; 
   }
  
    // Return backorder qty
   public int getQty() {
      return qty; 
   }
}
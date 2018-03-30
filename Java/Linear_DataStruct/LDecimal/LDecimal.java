/*
   Gavin Harris, Thurs lab 12pm
   Assignment 2
*/

public class LDecimal {
   private class LDecimalNode{     
      
      int data;
      LDecimalNode next;
      LDecimalNode prev;
      
      //data is the last digit
      public LDecimalNode(int data){
         this.data = data;
         this.next = null;
         this.prev = null;
      }      
      
      public LDecimalNode(int data, LDecimalNode next, LDecimalNode prev){
         this.data = data;
         this.next = next;
         this.prev = prev;
      }      
      // getters and setters      
      public void setNext(LDecimalNode nextValue){
         this.next = nextValue;         
      }
      
      public LDecimalNode getNext() {
         return this.next;
      }
      
      public void setPrev(LDecimalNode prev){
         this.prev = prev;
      }
   
      public LDecimalNode getLast(){
         return this.prev;
      }      
   }  
   //members
   private int numberOfDigits;
   private LDecimalNode head;
   private LDecimalNode tail;
   private int sign;
   
   //contructions
   public LDecimal(){
      //constuct LDecimal object. Object will have value zero
      head = new LDecimalNode(0);
      numberOfDigits = 0;   
   }
   
   public LDecimal(int n){   
      //construct an LDecimal object whose value is the integer n
   
      int t = n;
      sign = 1;
      if(t<0){
         t = -t;
         sign = -1;
      }
      if (t == 0){
         sign = 0;
      }
      while (t != 0){
         int d = t%10;
         t = t/10;
         
         // d is the new most significant digit
         // insert d at front of the digits
         if( d < 0){
            d = -d;
         }
         LDecimalNode test = new LDecimalNode(d,head,null);
         if (head != null){
            head.prev = test;
         }
         head = test;
         if (tail == null){
            tail = test;
         }
         numberOfDigits++;
      }
   }
   
   //methods
   public int digits(){
      return numberOfDigits;
   }   
   /*
      return the sign of the LDecimal
   */
   public int signum(){  
      return sign;
   }
   /*
      return a printable version of the LDecimal
   */
   public String toString(){
      StringBuilder result = new StringBuilder("");
      LDecimalNode current = head;
      if (current == null){
         String temp = "0";
         return temp;
      }
      if (sign < 0){
         result.append("-");
      }
      while (current != null){
         result.append(current.data);
         current = current.next;
      }
      return result.toString();
   }   
   /*
      compare the LDecimal to the parameter other.
      Return 0 if the same, 1 if LDecimal is bigger
      and -1 if parameter is bigger
   */   
   public int compareTo(LDecimal other){
   
      if (other.toString().equals(this.toString())){
         return 0;
      }else{
         if (Integer.parseInt(other.toString()) < Integer.parseInt(this.toString())){
            return 1;
         }
         else{
            return -1;
         }
      }
   }
   /*
      return true if the LDecimal is equal to the parameter
      other, false if different. 
   */
   @Override
   public boolean equals(Object other){

      return other.toString().equals(this.toString());
   }   
   /*
      return the sum of this LDecimal and the other LDecimal
   */
   public LDecimal add(LDecimal val){
   
      if (Integer.parseInt(val.toString()) == 0){ 
         return this;
      }
      if (Integer.parseInt(this.toString()) == 0){
         return val;
      }
      int val_int = Integer.parseInt(val.toString());
      int this_int = Integer.parseInt(this.toString());
      int added_num = val_int + this_int;
      
      LDecimal result = new LDecimal(added_num);   
      return result;
   }   
   /*
      return the difference of this LDecimal and LDecimal
      other.
   */
   public LDecimal subtract(LDecimal val){
      
      if (Integer.parseInt(val.toString()) == 0){ 
         return this;
      }

      int val_int2 = Integer.parseInt(val.toString());
      int this_int2 = Integer.parseInt(this.toString());
      int subtract_num = this_int2 - val_int2;
      
      LDecimal result2 = new LDecimal(subtract_num);
         
      return result2;
   }   
   /*
      return the product of this LDecimal and int val
   */
   public LDecimal multiply(int val){

      LDecimal zero = new LDecimal(0);
      if (this.toString().equals("0")){
         return zero;
      }
      if (val == 0){
         return zero;
      }
      int this_l = Integer.parseInt(this.toString());
      int final_multiply = this_l * val;
      LDecimal result_mult = new LDecimal(final_multiply);   
      
      return result_mult;
   }   
   /*
      return the difference of dividing this LDecimal by
      the int val   
   */
   public LDecimal divide(int val){

      LDecimal zero = new LDecimal(0);
      if (this.toString().equals("0")){
         return zero;
      }
      if (val == 0){
         throw new ArithmeticException("Divide by zero");
      }
      int this_l = Integer.parseInt(this.toString());
      int final_multiply = this_l / val;
      LDecimal result_mult = new LDecimal(final_multiply);   
      
      return result_mult;
   }
}

/*
Gavin Harris
Lab 6, Thursday 12pm
*/

public class LinkedList<E> {
   private class ListNode {

      public E data;
      public ListNode next;

      public ListNode(E data) {
         this.data = data;
         next = null;
      }
      public ListNode(E data, ListNode next) {
         this.data = data;
         this.next = next;
      }
      public void setNext(ListNode nextValue){
         this.next = nextValue;
      }

      public ListNode getNext() {
         return this.next;
      }
   }

   private ListNode head;
   private int size;

   //constructor
   public LinkedList() {
      head = null;
      size = 0;
   }

   //returns size
   public int size(){
      return size;
   }

   //returns true if empty, false if not
   public boolean isEmpty(){

      return(head == null);
   }
   //adds E value to the end of the list
   public void add(E value) {
      if (head == null) {
         head = new ListNode(value);
      }
      else {
         ListNode current = head;
         while (current.next != null) {
            current = current.next;
         }
         current.next = new ListNode(value);
      }
      size ++;
   }

   //adds E value to the list at the given index
   public void add(int index, E value){
      if (index == 0){
         //adding as first in list
         head = new ListNode(value, head);
      }
      else{
         //inserting into an existing list
         ListNode current = head;
         for (int i = 0; i < index -1; i++){
            current = current.next;
         }
         current.next = new ListNode(value, current.next);
      }
      size++;
   }

   //returns element of the list at the given index
   public E get(int index){

      if ( 0 <= index && index < size){
         ListNode current = head;
         for (int i = 0; i<index; i++){
            current = current.next;
         }
         return current.data;
      }
      else{
         return null;
      }
   }

   //removes the first element from the list
   public E remove(){
      if (head == null){
         throw new IllegalArgumentException();

      }
      else{
         E result = head.data;
         head = head.next;
         size--;
         return result;
      }
   }

   //removes the element at the given index
   public E remove(int index){

      if (index == 0) {
         // special case: removing first element
         ListNode result = head;
         head = head.next;
         size--;
         return result.data;
      }
      else {
         // removing from elsewhere in the list
         ListNode result = head;
         ListNode current = head;
         for (int i = 0; i < index - 1; i++) {
            current = current.next;

         }
         result = current.next;
         current.next = current.next.next;
         size--;
         return result.data;
      }
   }
}
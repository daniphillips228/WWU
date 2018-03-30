import java.util.*;
import java.io.*;
import java.io.FileWriter;
import java.util.Formatter;


public class BinaryTree {

   public Node root;
   public static Node maximum;
   
   public void addNode(String name, int numMale, int numFemale) {
      Node check = findNode(name);
      if (check == null){
         // Create a new Node and initialize it
         Node newNode = new Node(name, numMale, numFemale);
         // If there is no root this becomes root
         if (root == null) {
            root = newNode;
         } else {
            // Set root as the Node we will start
            // with as we traverse the tree
            Node focusNode = root;
            // Future parent for our new Node
            Node parent;
            while (true) {
               // root is the top parent so we start there
               parent = focusNode;
               // Check if the new node should go on
               // the left side of the parent node
               // System.out.println(focusNode.name.compareTo(name));
               if (name.compareTo(focusNode.name) < 0) {
                  // Switch focus to the left child
                  focusNode = focusNode.leftChild;
                  // If the left child has no children
                  if (focusNode == null) {
                     // then place the new node on the left of it
                     parent.leftChild = newNode;
                     return; // All Done
                  }
               } else { // If we get here put the node on the right
                  focusNode = focusNode.rightChild;
                  // If the right child has no children
                  if (focusNode == null) {
                  // then place the new node on the right of it
                     parent.rightChild = newNode;
                     return; // All Done
                  }
               }
            }
         }
      } else {
         check.numMale += numMale;
         check.numFemale += numFemale;
         check.total += numMale + numFemale;
      }
   }

   // All nodes are visited in ascending order
   // Recursion is used to go to one node and
   // then go to its child nodes and so forth
   public void inOrderTraverseTree(Node focusNode, FileWriter outFile) {
      if (focusNode != null) {
      // Traverse the left node
         inOrderTraverseTree(focusNode.leftChild, outFile);
         // Visit the currently focused on node
         try{
            String formatStr = "%-12s %-8s %-8s %-8s\n";
            outFile.write(String.format(formatStr, focusNode.name, focusNode.numMale, focusNode.numFemale, focusNode.total));
         } catch (IOException e1){
            System.out.println("Output file 'Names.txt' not found.");
            System.exit(2);
         }
         // Traverse the right node
         inOrderTraverseTree(focusNode.rightChild, outFile);
      }
   }

   public static Node preorderTraverseTree(Node focusNode, Node maxNode, int prevMax, String gender) {
      if (focusNode != null){
         if (gender.equals("female")){
            if ((focusNode.numFemale > maxNode.numFemale) && (focusNode.numFemale < prevMax)) {
               maximum = focusNode;
               preorderTraverseTree(focusNode.leftChild, focusNode, prevMax, gender);
               preorderTraverseTree(focusNode.rightChild, focusNode, prevMax, gender);
            } else {

               Node temp1 = preorderTraverseTree(focusNode.leftChild, maxNode, prevMax, gender);
               Node temp2 = preorderTraverseTree(focusNode.rightChild, maxNode, prevMax, gender);
            }
         }
      }
      return maximum;
   }

   /*public void postOrderTraverseTree(Node focusNode) {
      if (focusNode != null) {
         postOrderTraverseTree(focusNode.leftChild);
         postOrderTraverseTree(focusNode.rightChild);
         System.out.println(focusNode);
      }
   }*/

   public Node findNode(String name) {
      if (root != null){
         // Start at the top of the tree
         Node focusNode = root;
         // While we haven't found the Node
         // keep looking
         while (!focusNode.name.equals(name)) {
            // If we should search to the left
            if (name.compareTo(focusNode.name) < 0) {
               // Shift the focus Node to the left child
               focusNode = focusNode.leftChild;
            } else {
               // Shift the focus Node to the right child
               focusNode = focusNode.rightChild;
            }
            // The node wasn't found
            if (focusNode == null){
               return null;
            }
         }
         return focusNode;
      } else {
         return null;
      }
   }
   
   public static Node findMostPop(BinaryTree tree, int max, String gender){
      Node temp = tree.root;
      for (int i = 0; i < 3; i++){
         temp = tempNode(temp);
      }
      Node maxNode = preorderTraverseTree(tree.root, temp, max, gender);
      return maxNode;
   }
   
   public static Node tempNode(Node temp){
      if (temp.leftChild != null){
         temp = temp.leftChild;
      }
      return temp;
   }

   public static int traverse(BinaryTree tree, String check){
      return nodes(tree.root, check);
   }
   
   private static int nodes(Node current, String check) {   
     // if it's null, it doesn't exist, return 0 
      if (current == null){
         return 0;
      }
      if (check.equals("numMale")){
         if (current.numMale != 0){
            return 1 + nodes(current.leftChild, check) + nodes(current.rightChild, check);
         } else {
            return 0 + nodes(current.leftChild, check) + nodes(current.rightChild, check);
         }
      } else {
         if (current.numFemale != 0){
            return 1 + nodes(current.leftChild, check) + nodes(current.rightChild, check);
         } else {
            return 0 + nodes(current.leftChild, check) + nodes(current.rightChild, check);
         }
      }
   }
   
   public void inOrderPrint(BinaryTree tree, String year){
      try {
         FileWriter outFile = new FileWriter("Names.txt");System.out.println();
         outFile.write("Names in alphabetical order\n");
         outFile.write("Year: " + year + "\n");
         String formatStr = "%-12s %-8s %-8s %-8s\n";
         outFile.write(String.format(formatStr, "Name", "Male", "Female", "Total"));
         inOrderTraverseTree(tree.root, outFile);
         outFile.close();
      } catch (IOException e1){
         System.out.println("Output file 'Names.txt' could not be created.");
         System.exit(2);
      }
   }
}

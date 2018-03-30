import java.util.*;
import java.io.*;
import java.util.Scanner;
import java.io.FileWriter;
import java.util.Formatter;

public class Route {

   public static BinaryTree overall = new BinaryTree();
	
	public static void main(String[] args) {
		if(args.length != 5) {
			System.err.println("USAGE: java Route <yobYYYY.txt> <yobYYYY.txt> <yobYYYY.txt> <yobYYYY.txt> <yobYYYY.txt>");
			System.exit(1);
		}
      
      BinaryTree tree1 = new BinaryTree();
      BinaryTree tree2 = new BinaryTree();
      BinaryTree tree3 = new BinaryTree();
      BinaryTree tree4 = new BinaryTree();
      BinaryTree tree5 = new BinaryTree();
      tree1 = readFiles(args[0]);
      tree2 = readFiles(args[1]);
      tree3 = readFiles(args[2]);
      tree4 = readFiles(args[3]);
      tree5 = readFiles(args[4]);
      
      while (true){
         System.out.println("Enter a number specifying which information you want to know.");
         System.out.println("   (1 = Search for a name, 2 = Most-Popular Name, 3 = Least Popular Name,");
         System.out.println("    4 = Unique Name, 5 = Display Name)");
         
         Scanner in = new Scanner(System.in);
         try {
   		   int choice = in.nextInt();
      		if(choice < 1 || choice > 5) {
      			System.out.println("Invalid option");
      			System.exit(2);
      		}
            
            if(choice == 1){
               System.out.println("Search for a name");
               System.out.print("Enter a name to find:  ");
               Scanner input = new Scanner(System.in);
      		   String choiceName = input.next();
               System.out.println();
               System.out.println("Name: " + choiceName);
               System.out.printf("%-8s %-8s %-8s %-8s\n", "Year", "Male", "Female", "Total");
               nameSearch(tree1, choiceName, args[0].substring(3,7));
               nameSearch(tree2, choiceName, args[1].substring(3,7));
               nameSearch(tree3, choiceName, args[2].substring(3,7));
               nameSearch(tree4, choiceName, args[3].substring(3,7));
               nameSearch(tree5, choiceName, args[4].substring(3,7));
               nameSearch(overall, choiceName, "Overall");
               System.out.println();
               System.out.println();
               
            } else if (choice == 2){
               System.out.println("Most popular female names");
               System.out.printf("%-7s %-15s %-15s %-15s %-15s %-15s\n", "Year", "Most Popular",
                  "------------", "------------", "----------->", "Least Popular");
               mostPopFemale(tree1, args[0].substring(3,7));
               mostPopFemale(tree2, args[1].substring(3,7));
               mostPopFemale(tree3, args[2].substring(3,7));
               mostPopFemale(tree4, args[3].substring(3,7));
               mostPopFemale(tree5, args[4].substring(3,7));
               mostPopFemale(overall, "Overall");
               System.out.println();
               
               /*System.out.println("Most popular male names");
               System.out.printf("%-7s %-15s %-15s %-15s %-15s %-15s\n", "Year", "Most Popular",
                  "------------", "------------", "----------->", "Least Popular");
               System.out.println();
               System.out.println();
               */
            } else if (choice == 3){
               System.out.println("Least popular name");
               
            } else if (choice == 4){
               System.out.println("Unique names");
               System.out.printf("%-8s %-8s %-8s %-8s\n", "Year", "Male", "Female", "Total");
               distinctNames(tree1, args[0].substring(3,7));
               distinctNames(tree2, args[1].substring(3,7));
               distinctNames(tree3, args[2].substring(3,7));
               distinctNames(tree4, args[3].substring(3,7));
               distinctNames(tree5, args[4].substring(3,7));
               distinctNames(overall, "Overall");
               System.out.println();
               System.out.println();
                
            } else {
               System.out.println("Display name");
               System.out.print("Enter a year to display names:  ");
               Scanner input = new Scanner(System.in);
      		   String choiceYear = input.next();
               
               System.out.println("Year: " + choiceYear);
               System.out.println("Names output to file 'Names.txt'");
               
               if (choiceYear.equals(args[0].substring(3,7))){
                  displayName(tree1,args[0].substring(3,7));
               } else if (choiceYear.equals(args[1].substring(3,7))){
                  displayName(tree2,args[1].substring(3,7));
               } else if (choiceYear.equals(args[2].substring(3,7))){
                  displayName(tree3,args[2].substring(3,7));
               } else if (choiceYear.equals(args[3].substring(3,7))){
                  displayName(tree4, args[3].substring(3,7));
               } else if (choiceYear.equals(args[4].substring(3,7))){
                  displayName(tree5,args[4].substring(3,7));
               } else {
                  System.out.println("Invalid year");
      			   System.exit(2);
               }
               
               System.out.println();
            }
         } catch (InputMismatchException e1){
            System.out.println("Wrong input type");
            System.exit(2);
         }
      }
   }
   
   
   public static void nameSearch(BinaryTree tree, String searchName, String year){
      int totalMale = 0;
      int totalFemale = 0;
      int totalName = 0;
      Node temp = tree.findNode(searchName);
      if (temp == null){
         System.out.println("The name " + searchName + " does not exist in the database");
         System.exit(0);
      } else {
         totalMale += temp.numMale;
         totalFemale += temp.numFemale;
         totalName += temp.total;
         System.out.printf("%-8s %-8s %-8s %-8s\n", year, temp.numMale, temp.numFemale, temp.total);
      }
   }
   
   public static void mostPopFemale(BinaryTree tree, String year){
      Node max1 = tree.findMostPop(tree, 1000000, "female");
      Node max2 = tree.findMostPop(tree, max1.numFemale, "female");
      Node max3 = tree.findMostPop(tree, max2.numFemale, "female");
      Node max4 = tree.findMostPop(tree, max3.numFemale, "female");
      Node max5 = tree.findMostPop(tree, max4.numFemale, "female");
      System.out.printf("%-7s %-15s %-15s %-15s %-15s %-15s\n", year, max1.name + "," + max1.numFemale,
         max2.name + "," + max2.numFemale, max3.name + "," + max3.numFemale, max4.name + "," + max4.numFemale,
         max5.name + "," + max5.numFemale);
   }
   
   /*public static void mostPopMale(BinaryTree tree, String year){
      
   }*/
   
   public static void distinctNames(BinaryTree tree, String year){
      int maleNames = tree.traverse(tree, "numMale");
      int femaleNames = tree.traverse(tree, "numFemale");
      int totalNames = maleNames + femaleNames;
      System.out.printf("%-8s %-8s %-8s %-8s\n", year, maleNames, femaleNames, totalNames);
   }
   
   
   public static void displayName(BinaryTree tree, String year){
      tree.inOrderPrint(tree, year);
   }
   
   
   public static BinaryTree readFiles(String f1) {
		Scanner s = null;
		try {
			s = new Scanner(new File(f1));
		} catch(FileNotFoundException e1) {
			System.err.println("FILE NOT FOUND: "+f1);
			System.exit(2);
		}

		BinaryTree tree = new BinaryTree();
      String[] lineSplit = null;
		while(s.hasNext()){
         lineSplit = s.next().split(",");
         if (lineSplit[1].equals("M")){
            tree.addNode(lineSplit[0], Integer.parseInt(lineSplit[2]), 0);
            overall.addNode(lineSplit[0], Integer.parseInt(lineSplit[2]), 0);
         } else {
            tree.addNode(lineSplit[0], 0, Integer.parseInt(lineSplit[2]));
            overall.addNode(lineSplit[0], 0, Integer.parseInt(lineSplit[2]));
         }
      }
		return tree;
	}
}
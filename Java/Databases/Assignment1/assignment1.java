/* 
   Gavin Harris
   CSCI 330, 9am
   Assignment 1
*/
import java.util.*;
import java.io.*;
import java.util.Scanner;

public class assignment1 
{	
	public static void main(String[] args) 
   {
		if(args.length != 1) 
      {
			System.err.println("USAGE: need Stockmarket-1990-2015.txt");
			System.exit(1);
		}

      String file = args[0];
      Scanner s = null;
		try {
			s = new Scanner(new File(file));
		} catch(FileNotFoundException e1) {
			System.err.println("FILE NOT FOUND: "+file);
			System.exit(2);
		}
      process_info(s);
   }
   
   public static void process_info(Scanner s){
      String Ticker_Symbol = " "; 
      String Ticker_SymbolAux, Date;
      double High_Price, Low_Price, Closing_Price, Adjust_Closing;
      int Num_Shares;
      int Crazy_Count = 0;
      double Max = 0;
      double Opening_Price = 0;
      double prev_oprice = 0;
      int first_week = -1;
      List<String> split_list = new ArrayList<String>();
      
      while(s.hasNextLine()){

         Ticker_SymbolAux = s.next();
         
         /*
            If ticker symbol is the same keep processing.
            Else found a new ticker, display info for old ticker and update 
         */
         if (Ticker_SymbolAux.equals(Ticker_Symbol)){
         
            //if company is still the same, no need to display info yet
         
         }else{
            
            //found new company, start to display info found
            
            if (first_week == -1){
               //special print statement needed for week 1
                  System.out.println("Processing "+Ticker_SymbolAux);
                  System.out.println("=====================");
                  System.out.println();
                  Ticker_Symbol = Ticker_SymbolAux;  
            
            }else{
               
               //found no crazy days
               
               if (Max == 0){
                  System.out.println("Total Crazy days: "+Crazy_Count);
                  System.out.println();
                  
                  if (split_list.isEmpty()){
                     //no splits to print
                  
                  }else{
                     //print splits
                     for (int i = 0; i < split_list.size(); i++){
                        System.out.println(split_list.get(i));
                     }
                  }
                  
                  System.out.println("Total number of splits: "+split_list.size());
                  split_list.clear();
                  System.out.println();
                  System.out.println("Processing "+Ticker_SymbolAux);
                  System.out.println("=====================");
                  System.out.println();
                  
                  //new info for first week of new company
                  Ticker_Symbol = Ticker_SymbolAux;
                  first_week = -1;
                  
               }else{
                  System.out.println("Craziest Crazy day was "+Max);
                  System.out.println("Total Crazy days: "+Crazy_Count);
                  System.out.println();
                  
                  if (split_list.isEmpty()){
                     //no splits to print
                  
                  }else{ 
                     //print splits
                     
                     for (int i = 0; i < split_list.size(); i++){
                        System.out.println(split_list.get(i));
                     }
                  }
                  
                  System.out.println("Total number of Splits: "+split_list.size());
                  split_list.clear();
                  System.out.println();
                  System.out.println("Processing "+Ticker_SymbolAux);
                  System.out.println("=====================");
                  System.out.println();
                  
                  //re-assign variables for first week of new company found
                  Ticker_Symbol = Ticker_SymbolAux;
                  Max = 0;
                  Crazy_Count = 0;
                  first_week = -1; 
               }
            }
         }
         
         //assign variables found from text file
         if (first_week == -1){      
            //special case of first week so we don't have a previous opening day to calc splits
            
            Date = s.next();
            Opening_Price = Double.parseDouble(s.next());
            High_Price = Double.parseDouble(s.next());
            Low_Price = Double.parseDouble(s.next());
            Closing_Price = Double.parseDouble(s.next());
            Num_Shares = Integer.parseInt(s.next());
            Adjust_Closing = Double.parseDouble(s.next());
     
         } else{
            //we have a previous opening price so we can calculate splits
            
            prev_oprice = Opening_Price;
            Date = s.next();
            Opening_Price = Double.parseDouble(s.next());
            High_Price = Double.parseDouble(s.next());
            Low_Price = Double.parseDouble(s.next());
            Closing_Price = Double.parseDouble(s.next());
            Num_Shares = Integer.parseInt(s.next());
            Adjust_Closing = Double.parseDouble(s.next());
         }

         //do crazy day calculations
         double Crazy_Num = (((High_Price - Low_Price) / High_Price)*100);

         if (Crazy_Num >= 15){
            String rounded = String.format("%1.2f", Crazy_Num);
            System.out.println("Crazy day: "+ Date + " "+ rounded);
            Crazy_Count++;
            if (Crazy_Num > Max){
               Max = Double.parseDouble(rounded);
            }
         }
         
         //Do Stock Split info
         if (first_week == -1){
            //Can't find split on first week
         } else{
            //do calculations
              double Split = (Closing_Price / prev_oprice);
              if (Math.abs(Split-2) < .05){
               String Data = ("2-1 Split on "+Date+" "+Closing_Price+ " ---> " + prev_oprice).toString();
               split_list.add(Data);
              }
              if (Math.abs(Split-3) < .05){
               String Data = ("3-1 Split on "+Date+" "+Closing_Price+ " ---> " + prev_oprice).toString();
               split_list.add(Data);
              }
              if (Math.abs(Split-1.5) < .05){
               String Data = ("3-2 Split on "+Date+" "+Closing_Price+ " ---> " + prev_oprice).toString();
               split_list.add(Data);
              }
         } 
         first_week = 0;    
        }
        
        //print out all the info for the last company that we had in while loop
        System.out.println("Craziest Crazy day was "+Max);
        System.out.println("Total Crazy days: "+Crazy_Count);
        System.out.println();
        
        if (split_list.isEmpty()){
         //no splits
        }else{
            for (int i = 0; i < split_list.size(); i++){
               System.out.println(split_list.get(i));
            }
        }
        System.out.println("Total number of Splits: "+split_list.size());       
	}
}
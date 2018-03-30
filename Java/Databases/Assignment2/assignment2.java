/* 
   Gavin Harris
   CSCI 330, 9am
   Assignment 2
*/

import java.util.Properties;
import java.io.FileInputStream;
import java.util.Scanner;
import java.sql.*;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class assignment2 
{	
   static Connection conn = null;
	public static void main(String[] args) throws Exception 
   {

      String paramsFile = "ConnectionParameters.txt";
      if (args.length >= 1) {
         paramsFile = args[0];
      }
      Properties connectprops = new Properties();
      connectprops.load(new FileInputStream(paramsFile));
      
      try{
         //create connection
         Class.forName("com.mysql.jdbc.Driver");
         String dburl = connectprops.getProperty("dburl");
         String username = connectprops.getProperty("user");
         conn = DriverManager.getConnection(dburl, connectprops);
         System.out.println("Database connection established "+ dburl+" "+ username);
         
         //call function to get user input
         Input();

         conn.close();
      }catch (SQLException ex) {
         System.out.printf("SQLException: %s%nSQLState: %s%nVendorError: %s%n", 
         ex.getMessage(), ex.getSQLState(), ex.getErrorCode());
      }
   }
   static void Input(){
      try{
         //get user input
         Scanner console = new Scanner(System.in);
         System.out.print("Enter a ticker symbol [start/end dates]: ");
         String TickerSymbol = console.nextLine();
         
         //if no input or just spaces end program
         if (TickerSymbol.equals("") || !(TickerSymbol.matches(".*\\w.*")) ){
            System.out.println("Database connection closed.");   
         }else{
            String[] input;
   
            input = TickerSymbol.split("\\s+");
            
            //if no dates are specified
            if(input.length == 1){
               showCompany(input[0]);
               splitCounterNoDates(input[0]);
            }else{
               //if dates are specified
               showCompany(input[0]);
               splitCounterWithDates(input[0], input[1], input[2]);
            }
         }
      }catch (SQLException ex) {
         System.out.printf("SQLException: %s%nSQLState: %s%nVendorError: %s%n", 
         ex.getMessage(), ex.getSQLState(), ex.getErrorCode());
      }
   }
   
   //print out company name
   static void showCompany(String TickerSymbol) throws SQLException { 

      PreparedStatement Pstmt = conn.prepareStatement("select Name from Company where Ticker = ?");
      Pstmt.setString(1, TickerSymbol);
      ResultSet results = Pstmt.executeQuery();
      
      if (results.next()) {
         System.out.println(results.getString("Name"));
      }else{
         System.out.println(TickerSymbol+" not found in database");
         System.out.println();
         Input();
      }
      Pstmt.close();
   }
   
   //find splits, and transactions between specified dates
   static void splitCounterWithDates(String ticker, String StartDate, String EndDate) throws SQLException { 
      PreparedStatement pstmt = conn.prepareStatement("select * from PriceVolume where Ticker = ? and TransDate >= ? and TransDate <= ? order by TransDate DESC"); 
      pstmt.setString(1, ticker);
      pstmt.setString(2, StartDate);
      pstmt.setString(3, EndDate);
      ResultSet results = pstmt.executeQuery();   

      Deque deque = new LinkedList<>();
      Deque runningAverage = new LinkedList<>();
      String Date;
      double High_Price, Low_Price, Closing_Price, Adjust_Closing;
      int num_splits = 0;
      int trading_days = 0;
      int transactionCount = 0;
      int shareCount = 0;
      double Opening_Price = 0;
      double prev_oprice = 0;
      int first_week = -1;
      double divisor = 1;
      double average = 0;
      double netCash = 0;
      int buyIndicator = -1;
      
      while (results.next()) {
         if (first_week == -1){      
            //special case of first week so we don't have a previous opening day to calc splits
            
            Date = results.getString("TransDate");
            Opening_Price = Double.parseDouble(results.getString("OpenPrice"));
            High_Price = Double.parseDouble(results.getString("HighPrice"));
            Low_Price = Double.parseDouble(results.getString("LowPrice"));
            Closing_Price = Double.parseDouble(results.getString("ClosePrice"));     
         } else{
            //we have a previous opening price so we can calculate splits
            
            prev_oprice = Opening_Price;
            Date = results.getString("TransDate");
            Opening_Price = Double.parseDouble(results.getString("OpenPrice"));
            High_Price = Double.parseDouble(results.getString("HighPrice"));
            Low_Price = Double.parseDouble(results.getString("LowPrice"));
            Closing_Price = Double.parseDouble(results.getString("ClosePrice"));
         }
         
         
         //Do Stock Split info
         if (first_week == -1){
            //Can't find a split on first week
            first_week = 0;
         } else{
            //do calculations
              double Split = (Closing_Price / prev_oprice);
              if (Math.abs(Split-2) < .2){
               String Data = ("2-1 Split on "+Date+" "+Closing_Price+ " ---> " + prev_oprice).toString();
               System.out.println(Data);
               num_splits++;
               divisor = divisor * 2;
              }
              if (Math.abs(Split-3) < .3){
               String Data = ("3-1 Split on "+Date+" "+Closing_Price+ " ---> " + prev_oprice).toString();
               System.out.println(Data);
               num_splits++;
               divisor = divisor * 3;
              }
              if (Math.abs(Split-1.5) < .15){
               String Data = ("3-2 Split on "+Date+" "+Closing_Price+ " ---> " + prev_oprice).toString();
               System.out.println(Data);
               num_splits++;
               divisor = divisor * 1.5;
              }
         }
         trading_days++;       
         
         //adjust prices
         double AClosing_Price = Closing_Price / divisor;
         double AOpening_Price = Opening_Price / divisor;
         double Aprev_oprice = prev_oprice/ divisor;
         double AHigh_Price = High_Price/divisor;
         double ALow_Price = Low_Price/divisor;
         
         //add this to deque
         deque.addFirst(Date + " Open: "+AOpening_Price+ " high: " + AHigh_Price + " low: "+ ALow_Price+ " close: "+ AClosing_Price);
      }
      System.out.println(num_splits + " splits in " + trading_days + " trading days");
      System.out.println();
      System.out.println("Executing investment strategy");
      
      String[] Split;
      double prevOprice = 0;
      double Open_Price; 
      double prevCprice = 0;

      //get first 50 days, and process info
      if (trading_days > 50){
         
         //build runningTotal to 50 dates 
         for (int i = 0; i < 50; i++){
            //Split [0] = date, [2] = openPrice, [4] = highPrice, [6] = lowPrice, [8] closePrice
            Split = deque.pop().toString().split("\\s+");
            average +=  Double.parseDouble(Split[8]);
            runningAverage.addFirst(Double.parseDouble(Split[8]));
            prevOprice = Double.parseDouble(Split[2]);

         }
         //first average
         average= average / 50;
         
         //loop through rest of data and look for transactions, stop on second to last day
         while (deque.peek() != null && deque.size()> 1){
            
            //Key for info stored in deque and how it is split
            //Split [0] = date, [2] = openPrice, [4] = highPrice, [6] = lowPrice, [8] closePrice
            Split = deque.pop().toString().split("\\s+");
            Date = Split[0];
            Open_Price = Double.parseDouble(Split[2]);
            High_Price = Double.parseDouble(Split[4]);
            Low_Price = Double.parseDouble(Split[6]);
            Closing_Price = Double.parseDouble(Split[8]);

            //if we decided to buy stock
            if (buyIndicator == 1){
               netCash = netCash - (Open_Price * 100) - 8;
               shareCount+=100;
               transactionCount++;
               buyIndicator = -1;
            }
            
            //buy
            if((Closing_Price < average) & (Closing_Price / Open_Price) < 0.97000001){
               //set an indicator to buy 100 stocks at opening price of next day
               buyIndicator = 1;
               
            } else if((shareCount >= 100) && (Open_Price > average) && ((Open_Price / prevCprice) > 1.00999999)){ //sell

               //sell 100 shares at price (Opening_Price + Closing_Price)/2
               netCash = (netCash + (((Open_Price + Closing_Price) / 2)*100)) -8;
               shareCount-=100;
               transactionCount++;           
            }
            
            prevOprice = Open_Price;
            prevCprice = Closing_Price;
            
            //calcualate new average
            runningAverage.addFirst(Closing_Price);
            runningAverage.removeLast();            
            double newAvg = 0;
            Iterator calcAvg = runningAverage.iterator();
            while(calcAvg.hasNext()){
               newAvg += Double.parseDouble(calcAvg.next().toString());
            }
            average = newAvg / 50;
         }
         
         //Split [0] = date, [2] = openPrice, [4] = highPrice, [6] = lowPrice, [8] closePrice
         Split = deque.pop().toString().split("\\s+");         
         Date = Split[0];
         Open_Price = Double.parseDouble(Split[2]);
         netCash += (Open_Price * shareCount);
      }else{
         System.out.println("Not enough days to have transactions");
      }
      
      System.out.println("Transactions executed: "+transactionCount);
      System.out.printf("Net Cash: %.2f", netCash);
      System.out.println();
      System.out.println();
      pstmt.close();
      Input();
   }
   
   //same as above but without specified dates
   static void splitCounterNoDates(String ticker) throws SQLException {

      PreparedStatement pstmt = conn.prepareStatement("select * from PriceVolume where Ticker = ? order by TransDate DESC"); 
      pstmt.setString(1, ticker); 
      ResultSet results = pstmt.executeQuery();   

      Deque deque = new LinkedList<>();
      Deque runningAverage = new LinkedList<>();
      String Date;
      double High_Price, Low_Price, Closing_Price, Adjust_Closing;
      int num_splits = 0;
      int trading_days = 0;
      int transactionCount = 0;
      int shareCount = 0;
      double Opening_Price = 0;
      double prev_oprice = 0;
      int first_week = -1;
      double divisor = 1;
      double average = 0;
      double netCash = 0;
      int buyIndicator = -1;
      
      while (results.next()) {
         
         if (first_week == -1){      
            //special case of first week so we don't have a previous opening day to calc splits
            
            Date = results.getString("TransDate");
            Opening_Price = Double.parseDouble(results.getString("OpenPrice"));
            High_Price = Double.parseDouble(results.getString("HighPrice"));
            Low_Price = Double.parseDouble(results.getString("LowPrice"));
            Closing_Price = Double.parseDouble(results.getString("ClosePrice"));     
         } else{
            //we have a previous opening price so we can calculate splits
            
            prev_oprice = Opening_Price;
            Date = results.getString("TransDate");
            Opening_Price = Double.parseDouble(results.getString("OpenPrice"));
            High_Price = Double.parseDouble(results.getString("HighPrice"));
            Low_Price = Double.parseDouble(results.getString("LowPrice"));
            Closing_Price = Double.parseDouble(results.getString("ClosePrice"));
         }
         
         
         //Do Stock Split info
         if (first_week == -1){
            //Can't find split on first week
            first_week = 0;
         } else{
            //do calculations
              double Split = (Closing_Price / prev_oprice);
              if (Math.abs(Split-2) < .2){
               String Data = ("2-1 Split on "+Date+" "+Closing_Price+ " ---> " + prev_oprice).toString();
               System.out.println(Data);
               num_splits++;
               divisor = divisor * 2;
              }
              if (Math.abs(Split-3) < .3){
               String Data = ("3-1 Split on "+Date+" "+Closing_Price+ " ---> " + prev_oprice).toString();
               System.out.println(Data);
               num_splits++;
               divisor = divisor * 3;
              }
              if (Math.abs(Split-1.5) < .15){
               String Data = ("3-2 Split on "+Date+" "+Closing_Price+ " ---> " + prev_oprice).toString();
               System.out.println(Data);
               num_splits++;
               divisor = divisor * 1.5;
              }
         }
         trading_days++;       
         
         //adjust prices
         double AClosing_Price = Closing_Price / divisor;
         double AOpening_Price = Opening_Price / divisor;
         double Aprev_oprice = prev_oprice/ divisor;
         double AHigh_Price = High_Price/divisor;
         double ALow_Price = Low_Price/divisor;
         
         //add this to deque
         deque.addFirst(Date + " Open: "+AOpening_Price+ " high: " + AHigh_Price + " low: "+ ALow_Price+ " close: "+ AClosing_Price);
      }
      System.out.println(num_splits + " splits in " + trading_days + " trading days");
      System.out.println();
      System.out.println("Executing investment strategy");
      
      String[] Split;
      double prevOprice = 0;
      double Open_Price; 
      double prevCprice = 0;

      //get first 50 days
      if (trading_days > 50){
         
         //build runningTotal to 50 dates 
         for (int i = 0; i < 50; i++){
            //Split [0] = date, [2] = openPrice, [4] = highPrice, [6] = lowPrice, [8] closePrice
            Split = deque.pop().toString().split("\\s+");
            average +=  Double.parseDouble(Split[8]);
            runningAverage.addFirst(Double.parseDouble(Split[8]));
            prevOprice = Double.parseDouble(Split[2]);

         }
         average= average / 50;
         //look for transactions
         while (deque.peek() != null && deque.size()> 1){
            
            //Split [0] = date, [2] = openPrice, [4] = highPrice, [6] = lowPrice, [8] closePrice
            Split = deque.pop().toString().split("\\s+");
            Date = Split[0];
            Open_Price = Double.parseDouble(Split[2]);
            High_Price = Double.parseDouble(Split[4]);
            Low_Price = Double.parseDouble(Split[6]);
            Closing_Price = Double.parseDouble(Split[8]);

            //if we want to buy stock
            if (buyIndicator == 1){
               netCash = netCash - (Open_Price * 100) - 8;
               shareCount+=100;
               transactionCount++;
               buyIndicator = -1;
            }
            
            //buy
            if((Closing_Price < average) & (Closing_Price / Open_Price) < 0.97000001){
               //set an indicator to buy 100 stocks at opening price of next day
               buyIndicator = 1;
               
            } else if((shareCount >= 100) && (Open_Price > average) && ((Open_Price / prevCprice) > 1.00999999)){ //sell

               //sell 100 shares at price (Opening_Price + Closing_Price)/2
               netCash = (netCash + (((Open_Price + Closing_Price) / 2)*100)) -8;
               shareCount-=100;
               transactionCount++;           
            }
            
            prevOprice = Open_Price;
            prevCprice = Closing_Price;
            
            //calcualate new average,
            runningAverage.addFirst(Closing_Price);
            runningAverage.removeLast();            
            double newAvg = 0;
            Iterator calcAvg = runningAverage.iterator();
            while(calcAvg.hasNext()){
               newAvg += Double.parseDouble(calcAvg.next().toString());
            }
            average = newAvg / 50;
         }
         
         //Split [0] = date, [2] = openPrice, [4] = highPrice, [6] = lowPrice, [8] closePrice
         Split = deque.pop().toString().split("\\s+");         
         Date = Split[0];
         Open_Price = Double.parseDouble(Split[2]);
         netCash += (Open_Price * shareCount);
      }else{
         System.out.println("Not enough days to have transactions");
      }
      
      System.out.println("Transactions executed: "+transactionCount);
      System.out.printf("Net Cash: %.2f", netCash);
      System.out.println();
      System.out.println();
      pstmt.close();
      Input();
   }
}
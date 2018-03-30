import java.util.Properties;
import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;

/*
   Gavin Harris
   Assignment 3
   CSCI 330
   Apprx run time 2 minutes 40 seconds
*/

public class Assignment3 
{	
   static Connection conn = null;
   static Connection Wconn = null;
   
	public static void main(String[] args) throws Exception 
   {

      String paramsFile = "readerparams.txt";
      String writeParams = "writerparams.txt";
      
      if (args.length >= 2) {
         paramsFile = args[0];
         writeParams = args[1];
      }
      
      Properties connectprops = new Properties();
      connectprops.load(new FileInputStream(paramsFile));
      
      Properties connectWrite = new Properties();
      connectWrite.load(new FileInputStream(writeParams));
      
      try{     
         //create reader connection
         Class.forName("com.mysql.jdbc.Driver");
         String dburl = connectprops.getProperty("dburl");
         String username = connectprops.getProperty("user");
         conn = DriverManager.getConnection(dburl, connectprops);
         System.out.println("Reader connection established "+ dburl+" "+ username);
         
         //create writer connection
         Class.forName("com.mysql.jdbc.Driver");
         String Wdburl = connectWrite.getProperty("dburl");
         String Wusername = connectWrite.getProperty("user");
         Wconn = DriverManager.getConnection(Wdburl, connectWrite);
         System.out.println("Writer connection established "+ Wdburl+" "+ Wusername);
         
         //drop table if it exists
         PreparedStatement dropStmt = Wconn.prepareStatement("drop table if exists Performance");
         dropStmt.executeUpdate();
         
         //create table
         PreparedStatement createStmt = Wconn.prepareStatement("create table Performance (Industry char(30), Ticker char(6), StartDate char(10), EndDate char(10), TickerReturn char(12), IndustryReturn char(12))");
         createStmt.executeUpdate();
         
         //start analysis of all industries
         allIndustries();
         //processIndustry("Telecommunications Services");

         conn.close();
         Wconn.close();
         System.out.println();
         System.out.println("Database connections closed");
      
      }catch (SQLException ex) {
         System.out.printf("SQLException: %s%nSQLState: %s%nVendorError: %s%n", 
         ex.getMessage(), ex.getSQLState(), ex.getErrorCode());
      }
   }
   
   //process all tickers and intervals in the Industry given
   static void processIndustry(String Industry) throws SQLException { 
      
      int tradingDays = 100000000;
      int count = 0;
      
      //query for all start and end dates for each ticker
      PreparedStatement Pstmt = conn.prepareStatement("select Ticker, min(TransDate), max(TransDate), count(distinct TransDate) as TradingDays from Company natural join PriceVolume where Industry = ? group by Ticker having TradingDays >= 150 order by Ticker");
      Pstmt.setString(1, Industry);
      ResultSet results = Pstmt.executeQuery();
      
      String MinDate = "1";
      String MaxDate = "2017.12.31";
      
      //cycle through all start dates and find max start date, and cycle through all end dates and find min end date
      while (results.next()) {

         if(MinDate.compareTo(results.getString("min(TransDate)")) < 0){
            MinDate = results.getString("min(TransDate)");
         }

         if(MaxDate.compareTo(results.getString("max(TransDate)")) > 0){
            MaxDate = results.getString("max(TransDate)");
         }
      }
      Pstmt.close();
      
      //calculate trading days to make sure we have enough for analysis
      PreparedStatement PSTMT = conn.prepareStatement("select Ticker, min(TransDate), max(TransDate), count(distinct TransDate) as TradingDays from Company natural join PriceVolume where Industry = ? and TransDate >= ? and TransDate <= ? group by Ticker having TradingDays >= 150 order by Ticker");
      PSTMT.setString(1, Industry);
      PSTMT.setString(2, MinDate);
      PSTMT.setString(3, MaxDate);
      ResultSet Results = PSTMT.executeQuery();

      while (Results.next()) {

         if (Integer.parseInt(Results.getString("TradingDays")) < tradingDays){
            tradingDays = Integer.parseInt(Results.getString("TradingDays"));
         }
         count++;
      }
      PSTMT.close();
      
      //make sure we have enough trading days, then implement analysis
      if (tradingDays == 100000000){
         System.out.println();
         System.out.println("Insufficient data for "+Industry+" => no analysis");
      }else{
         //print out progress reports
         System.out.println();
         System.out.println("Processing "+Industry);
         System.out.print(count + " accepted tickers for " + Industry);
         System.out.print(" (" + MinDate + " -  " + MaxDate + ")");
         System.out.print(", "+tradingDays+" common dates, ");
         System.out.println(tradingDays / 60 + " trading intervals"); 
        
        //find interval dates to use for industry
         PreparedStatement PSTMT4 = conn.prepareStatement("select Ticker, min(TransDate), max(TransDate), count(distinct TransDate) as TradingDays from Company natural join PriceVolume where Industry = ? and TransDate >= ? and TransDate <= ? group by Ticker having TradingDays >= 150 order by Ticker");

         PSTMT4.setString(1, Industry);
         PSTMT4.setString(2, MinDate);
         PSTMT4.setString(3, MaxDate);
         ResultSet Results4 = PSTMT4.executeQuery();
         
         String Ticker = " ";
         
         //just get first alphabetical ticker, use these interval dates for each ticker afterwards
         if(Results4.next()){
            Ticker = Results4.getString("Ticker");
         }
         PSTMT4.close();
            
         //find interval dates
         PreparedStatement PSTMT2 = conn.prepareStatement("select P.TransDate, P.openPrice, P.closePrice from PriceVolume P where Ticker = ? and TransDate >= ?  and TransDate <= ?");
         PSTMT2.setString(1, Ticker);
         PSTMT2.setString(2, MinDate);
         PSTMT2.setString(3, MaxDate);
         ResultSet Results2 = PSTMT2.executeQuery();
         
         int IntervalCount = 1;
         double openPrice = 0;
         double closePrice = 0;
         double tickerReturn = 0;
         double TotalIndustryReturn = 0;
         String startDate = " ";
         String endDate = " ";

         while(Results2.next()){
         
            //if first day of interval, set opening price
            if (IntervalCount == 1){
               startDate = Results2.getString("P.TransDate");
               openPrice = Double.parseDouble(Results2.getString("P.openPrice"));
            }
            
            //if last day of interval, set close price and calculate returns
            if (IntervalCount == 60){
               
               //reset count
               IntervalCount = 1;
               
               //find info for last day of interval
               endDate = Results2.getString("P.TransDate");
               closePrice = Double.parseDouble(Results2.getString("P.closePrice"));
               tickerReturn = (closePrice  /openPrice)-1;
               
               //call function to find total industry return over the interval
               TotalIndustryReturn = industryReturn(Industry, Ticker, startDate, endDate);
               
               //subtract current ticker return from total return and do formula
               double printIndustryReturn = ((TotalIndustryReturn - (closePrice/openPrice)) / (count-1))-1;
               
               //add to performance table
               PreparedStatement addStmt1 = Wconn.prepareStatement("insert into Performance (Industry, Ticker, StartDate, EndDate, TickerReturn, IndustryReturn) values(?,?,?,?,?,?)");
               addStmt1.setString(1, Industry);
               addStmt1.setString(2, Ticker);
               addStmt1.setString(3, startDate);
               addStmt1.setString(4, endDate);
               addStmt1.setString(5, String.format("%10.7f", tickerReturn));
               addStmt1.setString(6, String.format("%10.7f", printIndustryReturn));
               addStmt1.executeUpdate();

               //System.out.println(Industry + "  "+ Ticker +" StartDate: " + startDate +" EndDate: "+ endDate +" Ticker Return: "+tickerReturn+" Industry Return: "+printindustryReturn);
               
               //find ticker, industry return for each ticker during same interval
               PreparedStatement PSTMT3 = conn.prepareStatement("select P.Ticker, P.TransDate, P.openPrice, P.closePrice from PriceVolume P natural join Company where Industry = ? and (TransDate = ?  or TransDate = ?) order by Ticker, TransDate");      

               PSTMT3.setString(1, Industry);
               PSTMT3.setString(2, startDate);
               PSTMT3.setString(3, endDate);
               ResultSet Results3 = PSTMT3.executeQuery();
      
               //calculate industry return
               while(Results3.next()){
                  if(Results3.getString("P.Ticker").equals(Ticker)){
                     //nothing, we already calculated this industry return
                  }else{
                  
                     //if the start date of interval, set open price to opening price of this date
                     if(Results3.getString("P.TransDate").equals(startDate)){
                        openPrice = Double.parseDouble(Results3.getString("P.openPrice"));
                        startDate = Results3.getString("P.TransDate");
                     }else{
                     
                        //else it will be the end date, so set closing price, and do calculations for tickerReturn, industry return
                        closePrice = Double.parseDouble(Results3.getString("P.closePrice"));                      
                        tickerReturn = (closePrice  /openPrice)-1;
                        
                        //subtract current ticker return from total return and do formula
                        printIndustryReturn = ((TotalIndustryReturn - (closePrice/openPrice)) / (count-1))-1;

                        //add to performance table
                        PreparedStatement addStmt = Wconn.prepareStatement("insert into Performance (Industry, Ticker, StartDate, EndDate, TickerReturn, IndustryReturn) values(?,?,?,?,?,?)");
                        addStmt.setString(1, Industry);
                        addStmt.setString(2, Results3.getString("P.Ticker"));
                        addStmt.setString(3, startDate);
                        addStmt.setString(4, Results3.getString("P.TransDate"));
                        addStmt.setString(5, String.format("%10.7f", tickerReturn));
                        addStmt.setString(6, String.format("%10.7f", printIndustryReturn));
                        addStmt.executeUpdate();

                        //System.out.println(Industry +"  "+Results3.getString("P.Ticker")+" StartDate: "+startDate+" EndDate:"+Results3.getString("P.TransDate")+ " Ticker Return: "+tickerReturn+" Industry Return: "+printindustryReturn);
                     }
                  }
               }         
               PSTMT3.close();                  
            }else{
               //not to the end of interval yet, keep counting
               IntervalCount++;
            }
         }            
         PSTMT2.close();
      }
   }
   
   //query to find all industries, call function to process each industry one at a time
   static void allIndustries() throws SQLException { 

      PreparedStatement Pstmt = conn.prepareStatement("select Industry from Company group by Industry order by Industry");
      ResultSet results = Pstmt.executeQuery();
      ArrayList<String> IndustryList = new ArrayList<String>();

      //saves a couple seconds to put all the tickers into list... maybe lol
      while (results.next()) {
         IndustryList.add(results.getString("Industry"));
      }      
      Pstmt.close();
      
      //process each industry we found from query
      for (int i =0; i < IndustryList.size(); i++){
         processIndustry(IndustryList.get(i));
      }     
   }
 
   //calculate the industry return of all tickers during an interval, and return that value
   static double industryReturn(String Industry, String Ticker, String StartDate, String EndDate) throws SQLException {
      
      //gets ticker, date, and opening and closing prices for only interval dates
      PreparedStatement PSTMT5 = conn.prepareStatement("select P.Ticker, P.TransDate, P.openPrice, P.closePrice from PriceVolume P natural join Company where Industry = ? and (TransDate = ?  or TransDate = ?) order by Ticker, TransDate");
      PSTMT5.setString(1, Industry);
      PSTMT5.setString(2, StartDate);
      PSTMT5.setString(3, EndDate);
      ResultSet Results5 = PSTMT5.executeQuery();
      
      int count = 0;
      double returnValue = 0;
      double openPrice = 0;
      double closePrice = 0;
      
      //set open and close price, then calculate returnValue
      while (Results5.next()) {
         if (Results5.getString("P.TransDate").equals(StartDate)){
            openPrice = Double.parseDouble(Results5.getString("P.openPrice"));
         }else{
            closePrice = Double.parseDouble(Results5.getString("P.closePrice"));
            returnValue = returnValue + (closePrice / openPrice);
         }
      }
      PSTMT5.close();

      return returnValue;
   }
}
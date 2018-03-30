/*
   Gavin Harris, Sam Shinn
   CSCI345, Deadwood part 3
     
   Implements
   Deadwood GUI helper file
   Author: Moushumi Sharmin
   This file shows how to create a simple GUI using Java Swing and Awt Library
   Classes Used: JFrame, JLabel, JButton, JLayeredPane

*/


import java.awt.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.event.*;


public class BoardLayersListener extends JFrame {

  // Private Attributes
  Player activePlayer;
  
  // JLabels
  JLabel boardlabel;
  JLabel mLabel;
  JLabel wLabel;
  JLabel stats;
  JLabel pMSG1;
  JLabel pMSG2;
  JLabel pName = new JLabel();
  JLabel pRank = new JLabel();
  JLabel pDollars = new JLabel();
  JLabel pCredits = new JLabel();
  JLabel pLocation = new JLabel();
  JLabel pSuccess = new JLabel();
  JLabel pFailure = new JLabel();
  
  //JButtons
  JButton[] buttons;
  JButton[] subButtons;

  
  // JLayered Pane
  JLayeredPane bPane;
  
  // Constructor
  
  public BoardLayersListener() {
      
       // Set the title of the JFrame
       super("Deadwood");
       // Set the exit option for the JFrame
       setDefaultCloseOperation(EXIT_ON_CLOSE);
      
       // Create the JLayeredPane to hold the display, cards, role dice and buttons

       bPane = getLayeredPane();
    
       // Create the deadwood board
       boardlabel = new JLabel();
       ImageIcon icon =  new ImageIcon("./images/board.jpg");
       boardlabel.setIcon(icon); 
       boardlabel.setBounds(0,0,icon.getIconWidth(),icon.getIconHeight());
      
       // Add the board to the lower layer
       bPane.add(boardlabel, new Integer(0));
      
       // Set the size of the GUI
       
       //Width = 1200, Height = 900
       setSize(icon.getIconWidth()+200,icon.getIconHeight()+38);

       // Create the Menu for action buttons
       mLabel = new JLabel("MENU");
       mLabel.setBounds(icon.getIconWidth()+40,0,100,20);
       bPane.add(mLabel,new Integer(2));
			
			// Create Action buttons
			buttons = new JButton[6];
			String[] bTitles = {"MOVE", "TAKE ROLE", "ACT", "REHEARSE", "UPGRADE", "END TURN"};
			for (int i=0; i<6; i++) {
				buttons[i] = new JButton(bTitles[i]);
				buttons[i].setBackground(Color.white);
				buttons[i].setBounds(icon.getIconWidth()+10,(i+1)*30,150, 20);
				buttons[i].addMouseListener(new boardMouseListener());
				bPane.add(buttons[i], new Integer(2));
			}
			
			// Create Move buttons
			subButtons = new JButton[7];
			for (int i=0; i < subButtons.length; i++) {
				subButtons[i] = new JButton();
				subButtons[i].setBackground(Color.white);
				subButtons[i].setBounds(icon.getIconWidth()+10,(i+13)*30,150, 20);
				subButtons[i].addMouseListener(new boardMouseListener());
				bPane.add(subButtons[i], new Integer(2));
				subButtons[i].setVisible(false);
			}
			
			stats = new JLabel("Current Player Stats:");
			stats.setBounds(icon.getIconWidth()+10,210,150, 20);
			bPane.add(stats, new Integer(2));
			pName.setVisible(false);
			pRank.setVisible(false);
			pDollars.setVisible(false);
			pCredits.setVisible(false);
			pLocation.setVisible(false);
  }
  
  //display the winning player on the GUI
  public void displayWinners(String msg){
  
       pLocation.setVisible(false);
       pSuccess.setVisible(false);
       pFailure.setVisible(false);
       
       pMSG1 = new JLabel("And the winner is...");
       pMSG1.setBounds(1210,390,150, 20);
       bPane.add(pMSG1, new Integer(2));
       
       pMSG2 = new JLabel(msg);
       pMSG2.setBounds(1210,420,150, 20);
       bPane.add(pMSG2, new Integer(2));

  
  }
  
  //display the current players stats on the menu sidebar
  public void displayStats(Player player){
  
       //Width = 1200, Height = 900
       if (pName.isVisible()){
          pName.setVisible(false);
          pRank.setVisible(false);
          pDollars.setVisible(false);
          pCredits.setVisible(false);
          pLocation.setVisible(false);
          pSuccess.setVisible(false);
          pFailure.setVisible(false);
       }
       
       pName = new JLabel("Name: "+player.getName());
       pName.setBounds(1210,240,150, 20);
       bPane.add(pName, new Integer(2));
       
       pRank = new JLabel("Rank: "+player.getRank());
       pRank.setBounds(1210,270,150, 20);
       bPane.add(pRank, new Integer(2));
       
       pDollars = new JLabel("Dollars: "+player.getDollars());
       pDollars.setBounds(1210,300,150, 20);
       bPane.add(pDollars, new Integer(2));
       
       pCredits = new JLabel("Credits: "+player.getCredits());
       pCredits.setBounds(1210,330,150, 20);
       bPane.add(pCredits, new Integer(2));
       
       pLocation = new JLabel("At "+player.getLocation().getTitle());
       pLocation.setBounds(1210,360,150, 20);
       bPane.add(pLocation, new Integer(2));
       
       activePlayer = player;
  }
  
  //display whether acting was succesful or not on the menu sidebar
  public void displaySuccess(){
       pLocation.setVisible(false);
       pSuccess = new JLabel("Acting Success");
       pSuccess.setBounds(1210,360,150, 20);
       bPane.add(pSuccess, new Integer(2));

  }
  
  //display whether acting was succesful
  public void displayFailure(){
  
       pLocation.setVisible(false);
       pFailure = new JLabel("Acting Failure");
       pFailure.setBounds(1210,360,150, 20);
       bPane.add(pFailure, new Integer(2));

  }
  
  //place the player on a location on the GUI
  public void place_player_at(Player player, Space location){
		JLabel label = player.getLabel(bPane);
		int newx = location.getX() -20 + (int)(Math.random()*100);
		int newy = location.getY() -20 + (int)(Math.random()*20);
		label.setLocation(newx, newy);
  }
  
  //place the player on a role on the GUI
  public void place_player_at(Player player, Role role){
	    JLabel label = player.getLabel(bPane);
		int newx = role.getX() + (int)(Math.random()*5);
		int newy = role.getY() + (int)(Math.random()*5);
		label.setLocation(newx, newy);
  }
  
  public void place_player_at(Player player, Space location, Role role){
		JLabel label = player.getLabel(bPane);
		int newx = location.getX() + role.getX() + (int)(Math.random()*5);
		int newy = location.getY() + role.getY() + (int)(Math.random()*5);
		label.setLocation(newx, newy);
  }
  
  public void updateScene(Set set) {
		JLabel slabel = set.getSceneLabel(bPane);
		slabel.setBounds(set.getX(),set.getY(),set.getW(), set.getH());
  }
  
  // This class implements Mouse Events
  
  volatile String commandString;
  volatile String subCommandStr;
  class boardMouseListener implements MouseListener{
			
		// Code for the different button clicks
		public void mouseClicked(MouseEvent e) {
			if (e.getSource()== buttons[0]){
				subCommandStr = "";
				commandString = "move";
			}
			else if (e.getSource()== buttons[1]){
				subCommandStr = "";
				commandString = "work";
			}
			else if (e.getSource()== buttons[2]){
				subCommandStr = "";
				commandString = "act";
			}
			else if (e.getSource()== buttons[3]){
				subCommandStr = "";
				commandString = "rehearse";
			}
			else if (e.getSource()== buttons[4]){
				subCommandStr = "";
				commandString = "upgrade";
			}
			else if (e.getSource()== buttons[5]){
				subCommandStr = "";
				commandString = "end";
			}
			else
				for (int i=0; i<subButtons.length; i++)
					if (e.getSource()== subButtons[i])
						subCommandStr =  subButtons[i].getText();
		}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
	}
	
	//get a command string from when a button is clicked
	public String getCommandString(Boolean[] available) {
		for (int i=0; i<available.length; i++)
			buttons[i].setEnabled(available[i]);
		while (commandString == null);
		String temp = commandString;
		commandString = null;
		subCommandStr = null;
		return temp;
	}
	
	//show options
	public String subSelection(String[] opts) {
		subCommandStr = null;
		for (int i=0; i < opts.length; i++) {
			subButtons[i].setText(opts[i]);
			subButtons[i].setEnabled(true);
			subButtons[i].setVisible(true);
		}
		while (subCommandStr == null);
		for (int i=0; i < subButtons.length; i++)
			subButtons[i].setVisible(false);
		return subCommandStr;
	}
	
	//show availible options
	public String subSelection(String[] opts, Boolean[] available) {
		subCommandStr = null;
		for (int i=0; i < opts.length; i++) {
			subButtons[i].setText(opts[i]);
			subButtons[i].setEnabled(available[i]);
			subButtons[i].setVisible(true);
		}
		while (subCommandStr == null);
		for (int i=0; i < subButtons.length; i++)
			subButtons[i].setVisible(false);
		return subCommandStr;
	}

  public static void main(String[] args) {
    BoardLayersListener board = new BoardLayersListener();
    board.setVisible(true);
  }
}

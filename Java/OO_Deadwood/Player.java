// Representation of a player

import javax.swing.*;
import javax.imageio.ImageIO;

public class Player {
	private String name;
	private int rank;
	private int credits;
	private int dollars;
	private Space location;
	private Role working;
	private int rehearsalChips;
	private int playerNumber;
	private JLabel label;

	public Player(String name, int playerNum) {
		this.name = name;
		rank = 1;
		credits = 0;
		dollars = 0;
		rehearsalChips = 0;
		playerNumber = playerNum;
	}

	public String getName() {
		return name;
	}
	
	public int getRank() {
		return rank;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
		if (label != null)
			label.setIcon(new ImageIcon(String.format("./images/dice/%s%d.png", name, rank)));
	}

	public int getCredits() {
		return credits;
	}	
	
	public void chCredits(int change) {
		credits += change;
		/*
		if (credits < 0)
			throw exception
		*/
	}

	public int getDollars() {
		return dollars;
	}
	
	public void chDollars(int change) {
		dollars += change;
		/*
		if (dollars < 0)
			throw exception
		*/
	}
	
	public Space getLocation() {
		return location;
	}
	
	public void setLocation(Space location) {
      //change GUI board?
		this.location = location;
	}
	
	public void workRole(Role role) {
		working = role;
	}
	
	public Role getRole() {
		return working;
	}
	
	public void leaveRole(){
		working = null;
		rehearsalChips = 0;
		if (label != null) {
			int newx = location.getX() -20 + (int)(Math.random()*100);
			int newy = location.getY() -20 + (int)(Math.random()*20);
			label.setLocation(newx, newy);
		}
	}
	
	public int getChips() {
		return rehearsalChips;
	}
   
   public int getPlayerNumber() {
		return playerNumber;
	}
	
	public void rehearse() {
		rehearsalChips++;
	}
	
	public JLabel getLabel(JLayeredPane GUI) {
		if (label == null) {
			label = new JLabel();
			label.setIcon(new ImageIcon(String.format("./images/dice/%s%d.png", name, rank)));
			label.setBounds(1100,600,40,40); // somewhere off to the side
			label.setVisible(true);
			GUI.add(label,new Integer(5));
		}
		return label;
	}
	
	public String toString() {
		String str = String.format("%-10s", name);
		str += "   rank: " + rank;
		str += "   credits: " + credits;
		str += "   dollars: " + dollars;
		if (location != null)
			str += "   location: " + location.getTitle();
		if (working != null)
			str += "   working: " + working.getTitle();
		return str;
	}
}

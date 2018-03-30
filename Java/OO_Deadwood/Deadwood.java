/* 
* Deadwood.java
* The driver program
*/

import java.util.Scanner;
import org.xml.sax.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.event.*;

public class Deadwood {
	private static Player[] players;
	private static Space[] board;
   private static BoardLayersListener GUIboard;
	private static int daysRemaining;
	private static SceneDeck deck;
	
	/* MAIN */
	public static void main(String[] args) {
      setup(args);
		playGame();
		gameOver();
	}	
	
	/* --- GAME SETUP --- */
	private static void setup(String[] args) {
		// initialize players
		if (args.length > 0)
			playerCountSetup(args[0]); // setup with provided arg
		else
			playerCountSetup(); // setup with input
		
		// create players
		for (int i=0; i < players.length; i++)
			players[i] = makePlayer(i+1);
	
		// initialize board
		boardSetup();
		
		//create the GUI
        GUIboard = new BoardLayersListener(); 
        GUIboard.setVisible(true);
		
		// initialize scenes
		sceneSetup();
      
		// player count adaptations
		if (players.length <= 3)
			daysRemaining = 3;
		else
			daysRemaining = 4;
		
		if (players.length == 5)
			for (Player player : players)
				player.chCredits(2);
		else if (players.length == 6)
			for (Player player : players)
				player.chCredits(4);
		else if (players.length >= 7)
			for (Player player : players)
				player.setRank(2);
		
		playersToTrailers();
	} 
	
	// END MAIN SETUP
	
	
	/* PLAYER SETUP */
	// PLAYER COUNT SETUP (using args)
	private static void playerCountSetup(String arg) {
		try {
			int count = Integer.parseInt(arg);
			if (count < 2 || count > 8) {
				System.out.println("Deadwood only supports 2-8 players");
				playerCountSetup(); // use input setup
				return;
			}
			players = new Player[count];
		}
		catch (NumberFormatException e) {
			System.out.println("argument must be an integer");
			playerCountSetup(); // use input setup
		}
	}
	// PLAYER COUNT SETUP (using input)
	private static void playerCountSetup() {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter the number of players: ");
		while (input.hasNext()) {
			try {
				int count = input.nextInt();
				if (count < 2 || count > 8)
					System.out.println("Deadwood only supports 2-8 players");
				else {
					players = new Player[count];
					return;
				}
			}
			catch (java.util.InputMismatchException e) {
				System.out.println("value must be an integer");
				input.nextLine();
			}
			System.out.print("Enter the number of players: ");
		}
	}
	
   // MAKE PLAYER
	private static Player makePlayer(int i) {
		Scanner input = new Scanner(System.in);
      //Check for valid names, but can save this for later
		System.out.printf("Enter a name for player %d, please select b, c, g, o, p, r, v, or y: ", i);
		String name = input.nextLine();
		return new Player(name, i);
	} 
	
	// END PLAYER SETUP
	
	
	/* BOARD SETUP */
	private static void boardSetup() {
		// initialize board
		board = new Space[12];
		
		// get xmlDoc from file
		Document xmlDoc = getXML("./data files/board.xml");
		
		// make Trailers and Casting Office
		int[] area = getArea((Element) xmlDoc.getElementsByTagName("trailer").item(0));
		board[0] = new Trailers(area[0], area[1], area[2], area[3]);
		area = getArea((Element) xmlDoc.getElementsByTagName("office").item(0));
		board[1] = new CastingOffice(area[0], area[1], area[2], area[3]);
		
		// search xml Documnet structure to make sets, store in board[2...11]
		makeSets(xmlDoc);
		
		// make connections
		connectSpaces(xmlDoc);
	}
   	
	// GET XML
	private static Document getXML(String filename) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(filename));
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	// GET AREA OF NODE
	private static int[] getArea(Element location) {
		NodeList list = location.getElementsByTagName("area");
		Node node = list.item(0);
		Element areaNode = (Element) node;
		int area[] = new int[4];
		area[0] = Integer.parseInt(areaNode.getAttribute("x"));
		area[1] = Integer.parseInt(areaNode.getAttribute("y"));
		area[2] = Integer.parseInt(areaNode.getAttribute("h"));
		area[3] = Integer.parseInt(areaNode.getAttribute("w"));
		return area;
	}
	
	// MAKE SETS (multiple)
	private static void makeSets(Document xmlDoc) {
		// get set nodes
		NodeList listOfSets = xmlDoc.getElementsByTagName("set");
		
		// make each set using a node
		for (int i=0; i < listOfSets.getLength(); i++) {
			board[i+2] = makeSet((Element)listOfSets.item(i));
		}
	}
	
	// MAKE SET (individual)
	private static Space makeSet(Element set) {
		// retrieve nodelists of shots and roles for the set
		NodeList shotNodes = nodesOfNodeOfElement("take", "takes", set);
		NodeList roleNodes = nodesOfNodeOfElement("part", "parts", set);

		// data for set
		String setTitle = set.getAttribute("name");
		int[] area = getArea(set);
		int[][] shotAreas = new int[shotNodes.getLength()][4];
		for (int i=0; i < shotAreas.length; i++) {
			int[] oneShotArea = getArea((Element)shotNodes.item(i));
			for (int j=0; j < 4; j++)
				shotAreas[i][j] = oneShotArea[j];
		}
		Role[] roles = new Role[roleNodes.getLength()];
		
		// data for roles
		for (int j=0; j < roleNodes.getLength(); j++)
			roles[j] = makeRole((Element)roleNodes.item(j));
		
		return new Set(setTitle, shotNodes.getLength(), roles, area[0], area[1], area[2], area[3], shotAreas);
	}
	
	// MAKE ROLE
	private static Role makeRole(Element roleElement) {
		String roleTitle = roleElement.getAttribute("name");
		int roleRank = Integer.parseInt(roleElement.getAttribute("level"));
		String roleFlavor = ((Element)roleElement.getElementsByTagName("line").item(0)).getFirstChild().getNodeValue();
		int[] area = getArea(roleElement);
		return new Role(roleTitle, roleRank, roleFlavor, area[0], area[1], area[2], area[3]);
	}
	
	// CONNECT SPACES (multiple)
	private static void connectSpaces(Document xmlDoc) {
		Element boardRoot = xmlDoc.getDocumentElement();
		Element spaceElement = nextElement(boardRoot.getFirstChild());
		
		while (spaceElement != null) {
			connectSpaces(spaceElement);
			spaceElement = nextElement(spaceElement.getNextSibling());
		}
	}
	
	// CONNECT SPACES (individual)
	private static void connectSpaces(Element node) {
		// the name of the space to recieve new connections
		Space recipient;
		if (node.hasAttributes())
			recipient = findSpace(node.getAttribute("name"));
		else
			recipient = findSpace(node.getNodeName());
		
		// retrieve nodelists of neighbors for the space
		NodeList neighborNodes = nodesOfNodeOfElement("neighbor", "neighbors", node);
		
		// initialize array to pass to recipient
		Space[] connections = new Space[neighborNodes.getLength()];
		
		// find neighboring spaces using neighborNodes
		for (int i=0; i < neighborNodes.getLength(); i++)
			connections[i] = findSpace(((Element)neighborNodes.item(i)).getAttribute("name"));
		
		recipient.connectSpaces(connections);
	}
	
	// NODES OF NODE OF ELEMENT (helper function)
	private static NodeList nodesOfNodeOfElement(String nodes, String node, Element element) {
		Element ofElement = (Element)element.getElementsByTagName(node).item(0);
		NodeList ofNode = ofElement.getElementsByTagName(nodes);
		return ofNode;
	}
	
	// FIND SET (by title, helper function)
	private static Space findSpace(String name) {
		if (name.equals("Trailers") || name.equals("trailer"))
			return board[0];
		if (name.equals("Casting Office") || name.equals("office"))
			return board[1];
		for (Space space : board)
			if (space.getTitle().equals(name))
				return space;
		return null;
	}
	
	// NEXT ELEMENT (helper function)
	private static Element nextElement(Node prev) {
		while (prev != null && prev.getNodeType() != Node.ELEMENT_NODE)
			prev = prev.getNextSibling();
		if (prev == null)
			return null;
		return (Element)prev;
	}
	
	// END BOARD SETUP
	
	
	/* SCENE SETUP */
	private static void sceneSetup() {
		// get xmlDoc from file
		Document xmlDoc = getXML("./data files/cards.xml");
		makeDeck(xmlDoc);
		dealScenes();
	}
	
	// MAKE DECK
	private static void makeDeck(Document xmlDoc) {
		NodeList listOfScenes = xmlDoc.getElementsByTagName("card");
		Scene[] scenes = new Scene[listOfScenes.getLength()];
		for (int i=0; i < listOfScenes.getLength(); i++) {
			scenes[i] = makeScene((Element)listOfScenes.item(i), i+1);
		}
		deck = new SceneDeck(scenes);
	}
	
	// MAKE SCENE (individual)
	private static Scene makeScene(Element scene, int ID) {		
		String sceneTitle = scene.getAttribute("name");
		int budget = Integer.parseInt(scene.getAttribute("budget"));
		String flavorText = ((Element)scene.getElementsByTagName("scene").item(0)).getFirstChild().getNodeValue().trim();
		
		NodeList roleNodes = scene.getElementsByTagName("part");
		Role[] roles = new Role[roleNodes.getLength()];
		for (int j=0; j < roleNodes.getLength(); j++)
			roles[j] = makeRole((Element)roleNodes.item(j));
			
		return new Scene(sceneTitle, budget, flavorText, roles, ID);
	}
	
	// END SCENE SETUP
	
	
	// DEAL SCENES
	private static void dealScenes() {
		// scenes are in slots 2...11
		for (int i=2; i < 12; i++){
			Scene newScene = deck.draw();
			((Set)board[i]).setScene(newScene);
			GUIboard.updateScene((Set)board[i]);
		}
	}
	
	// PLAYERS TO TRAILERS
	private static void playersToTrailers() {
		int count = 1;
		for (Player each : players){
			each.setLocation(board[0]);
			GUIboard.place_player_at(each, board[0]);
		}
	}
	
	// END ALL SETUP 
	
	
	/* --- PLAY GAME --- */
	private static void playGame() {
		int turn_tracker = 0;

		while(daysRemaining > 0) {
			new PlayerTurn(players[turn_tracker], GUIboard);
			turn_tracker = (turn_tracker+1)%players.length;
			if (countScenes() <= 1)
				newDay();
		}
	}
	
	// COUNT SCENES
	private static int countScenes() {
		int count = 0;
		for (int i=2; i < board.length; i++)
			if (((Set)board[i]).hasScene())
				count++;
		return count;
	}
	
	// NEW DAY
	private static void newDay() {
		daysRemaining--;
		playersToTrailers();
		dealScenes();
	}
	
	// END PLAY GAME
	
	 /* GAME OVER */
	private static void gameOver() {
		System.out.println("Game Over!");
      int score = 0;
      int highScore = 0;
      String winningMessage ="";
      int numPlayers = players.length;

      for(int i=0; i< numPlayers; i++){
         
         //add up score total
         score += players[i].getDollars();
         score += players[i].getCredits();
         score += (players[i].getRank() * 5);
         
         //find out if they are a winner
         if (score >= highScore){
            if (score == highScore){
               winningMessage = players[i].getName() +", "+ winningMessage;
            }else{
               winningMessage = players[i].getName() + " with "+score+" points";
               highScore = score;
            }
         }
         score = 0;
      }
      //display the winner(s)
      System.out.println("And the winner(s) is...");
      System.out.println(winningMessage);
      GUIboard.displayWinners(winningMessage);
	}
}

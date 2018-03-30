// Represents a Player Turn

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;

public class PlayerTurn {
	private Player activePlayer;
	private BoardLayersListener GUIboard;
	private boolean canMove;
	private boolean canTakeRole;
	private boolean canUpgrade;
	private boolean canAct;
	private boolean canRehearse;
	
	public PlayerTurn(Player newActivePlayer, BoardLayersListener newGUIboard) {
		activePlayer = newActivePlayer;
        GUIboard = newGUIboard;
        GUIboard.displayStats(activePlayer);
		findActions(); // determine options available to player
		selectActions(); // prompt player for choice of action
	}
	
	private void findActions() {
		Space location = activePlayer.getLocation();
		if (location instanceof Set) {
			// Player is on a Set Space
			Role working = activePlayer.getRole();
			if (working !=null) {
				// Player is on set and working a role
				canAct = true;
				canMove = canTakeRole = canUpgrade = false;
				if (((Set)location).getScene() != null && activePlayer.getChips()+1 >= ((Set)location).getScene().getBudget())
					canRehearse = false;
				else
					canRehearse = true;
			}
			else {
				// Player is on set and not working a role
				canAct = canRehearse = canUpgrade = false;
				canMove = true;
				if (((Set)location).getScene() != null)
					canTakeRole = true;
				else
					canTakeRole = false;
			}
		}
		else if (location instanceof CastingOffice) {
			// Player is on a (the) CastingOffice Space
			canMove = canUpgrade = true;
			canTakeRole = canAct = canRehearse = false;
		}
		else if (location instanceof Trailers) {
			// Player is on a (the) Trailers Space
			canMove = true;
			canTakeRole = canAct = canRehearse = canUpgrade = false;
		}
		else
			System.out.println("You aren't on the board.\nSomething has gone horribly wrong.");
	} // END FINDACTIONS()
	
	private void selectActions() {
		// Scanner input = new Scanner(System.in);
		
		while(true) {

			Boolean[] available = {canMove,canTakeRole,canAct,canRehearse,canUpgrade};
			String line = GUIboard.getCommandString(available);
			
			if (line.equals("who"))
				who();
			else if (line.equals("where"))
				where();
			else if (line.startsWith("move")) {
				if (line.length() > 4)
					move(line.substring(4).trim()); // location given
				else
					move(); // location still needed
			}
			else if (line.startsWith("work")) {
				if (line.length() > 4)
					work(line.substring(4).trim()); // role given
				else
					work(); // role still needed
			}
			else if (line.startsWith("upgrade")) {
				if (line.length() > 7) {
					upgrade(line.substring(7).trim()); // upgrade given
				}
				else
					upgrade(); // upgrade selection still needed
			}
			else if (line.equals("rehearse")) 
				rehearse();
			else if (line.equals("act")) 
				act();
			else if (line.equals("end")) 
				return; // complete the turn
			else
				showHelp();
		} // END WHILE LOOP
	} // END SELECshowActions()TACTIONS()
	
	private void who() {
		System.out.println(activePlayer);
	}
	
	private void where() {
		System.out.println(activePlayer.getLocation());
	}
	
	private void move() {
		if (!canMove) {
			//System.out.println("You cannot move right now");
			return;
		}
		//System.out.println("Nearby spaces: ");
		Space[] cons = activePlayer.getLocation().getConnections();
		String[] sCons = new String[cons.length];
		for (int i=0; i < sCons.length; i++)
			sCons[i] = cons[i].getTitle();

		String target = GUIboard.subSelection(sCons);
		
		move(target);
	}
	
	private void move(String arg) {
		if (!canMove) {
			System.out.println("You cannot move right now");
			return;
		}
		Space[] adjacentSpaces = activePlayer.getLocation().getConnections();
		for (Space space : adjacentSpaces)
			if (arg.trim().equalsIgnoreCase(space.getTitle())) {
				if (space instanceof Set) {
					((Set)space).revealScene();
					GUIboard.updateScene((Set)space);
				}
				activePlayer.setLocation(space);
				findActions();
				canMove = false;
				//System.out.println("You are now at the " + space.getTitle() + "!!");
				GUIboard.place_player_at(activePlayer, space);
				GUIboard.displayStats(activePlayer);
				return;
			}
		// named location not found
		System.out.println("No adjacent spaces with that name");
	}
	
	private void work() {
		if (!canTakeRole) {
			System.out.println("You cannot take a role right now");
			return;
		}

		ArrayList<Role> roles = new ArrayList<Role>();
		roles.addAll(Arrays.asList(((Set)activePlayer.getLocation()).getRoles()));
		roles.addAll(Arrays.asList(((Set)activePlayer.getLocation()).getScene().getRoles()));
		String[] titles = new String[roles.size()];
		Boolean[] available = new Boolean[roles.size()];
		for (int i=0; i < roles.size(); i++) {
			titles[i] = roles.get(i).getTitle();
			available[i] = (roles.get(i).getPlayer() == null && activePlayer.getRank() >= roles.get(i).getRank());
		}
		
		String target = GUIboard.subSelection(titles, available);
		
		work(target);
	}
	
	private void work(String arg) {
		if (!canTakeRole) {
			System.out.println("You cannot take a role right now");
			return;
		}
		ArrayList<Role> roles = new ArrayList<Role>();
		roles.addAll(Arrays.asList(((Set)activePlayer.getLocation()).getRoles()));
		int sceneMarker = roles.size();
		roles.addAll(Arrays.asList(((Set)activePlayer.getLocation()).getScene().getRoles()));
		for (int i=0; i < roles.size(); i++)
			if (arg.equalsIgnoreCase(roles.get(i).getTitle())) {
				if (roles.get(i).getPlayer() == null)
					if (activePlayer.getRank() >= roles.get(i).getRank()) {
						roles.get(i).attachPlayer(activePlayer);
						activePlayer.workRole(roles.get(i));
						canMove = canTakeRole = false;
						//System.out.println("You are now working " + roles.get(i).getTitle() + "!");
						if (i < sceneMarker)
							GUIboard.place_player_at(activePlayer, roles.get(i));
						else
							GUIboard.place_player_at(activePlayer, activePlayer.getLocation(), roles.get(i));
						return;
					}
					else {
						System.out.println("You are not qualified to take that role");
						return;
					}
				else {
					System.out.println("Someone is already working that role");
					return;
				}
			}
		// named location not found
		//System.out.println("No available roles with that name");
	}
	
	private void upgrade() {
		if (!canUpgrade) {
			System.out.println("You cannot upgrade your rank right now");
			return;
		}
		
		String[] opts = {"pay with dollars","pay with credits"};
		String selection = GUIboard.subSelection(opts);
		int up_i;
		
		if (selection.equals("pay with dollars"))
			{selection = "$ "; up_i = 1;}
		else {selection = "cr "; up_i = 2;}
			
		int[][] upgrades = {{2, 4, 5}, {3, 10, 10}, {4, 18, 15}, {5, 28, 20},	{6, 40, 25}};
		
		String[] ranks = {"Rank 2", "Rank 3", "Rank 4", "Rank 5", "Rank 6"};
		Boolean[] available = new Boolean[5];
		int wealth;
		if (up_i == 1)
			wealth = activePlayer.getDollars();
		else
			wealth = activePlayer.getCredits();
			
		for (int i=0; i<5; i++)
			available[i] = (wealth >= upgrades[i][up_i]);
		
		String nextstr = GUIboard.subSelection(ranks, available);
		if (nextstr.length() > 5)
			selection += nextstr.substring(5);
		else
			selection = "";
		
		upgrade(selection);
        GUIboard.displayStats(activePlayer);
	}
	
	private void upgrade(String arg) {
		if (!canUpgrade) {
			System.out.println("You cannot upgrade your rank right now");
			return;
		}
		String[] input = arg.split(" ");
		if (input.length != 2) {
			System.out.println("cannot understand command");
			return;
		}
		int[][] upgrades = {{2, 4, 5}, {3, 10, 10}, {4, 18, 15}, {5, 28, 20},	{6, 40, 25}};
		int rank = 0;
		try {
			rank = Integer.parseInt(input[1]);
		} catch (NumberFormatException e) {
			System.out.println("cannot understand command");
			return;
		}
		if (rank < 2 || rank > 6) {
			System.out.println("Only ranks 2-6 are valid");
			return;
		}
		int payment_index;
		if (input[0].equals("$")) {
			if (activePlayer.getDollars() >= upgrades[rank-2][1]) {
				activePlayer.chDollars(0-upgrades[rank-2][1]);
				activePlayer.setRank(rank);
                GUIboard.displayStats(activePlayer);
				//System.out.println("You are now rank " + rank + "!");
			}
			else
				System.out.println("You cannot afford that");
		}
		else if (input[0].equalsIgnoreCase("cr")) {
				if (activePlayer.getCredits() >= upgrades[rank-2][2]) {
				activePlayer.chCredits(0-upgrades[rank-2][2]);
				activePlayer.setRank(rank);
                GUIboard.displayStats(activePlayer);
				//System.out.println("You are now rank " + rank + "!");
			}
			else
				System.out.println("You cannot afford that");
		}
		else {
			System.out.println("cannot understand command");
			return;
		}
	}
	
	private void rehearse() {
		if (!canRehearse) {
			System.out.println("You cannot rehearse right now");
			return;
		}
		activePlayer.rehearse();
		canRehearse = canAct = false;
		//System.out.println("You are a little more prepared for your role!");
	}
	
	private void act() {
		if (!canAct) {
			System.out.println("You cannot act right now");
			return;
		}
		canAct = canRehearse = false;
		int dice = (int)((Math.random()*6)+1);
		Set set = (Set)activePlayer.getLocation();
		boolean extra = oneOf(activePlayer.getRole(), set.getRoles());
		if (dice + activePlayer.getChips() >= set.getScene().getBudget()) {
			if (extra) {
				activePlayer.chDollars(1);
				activePlayer.chCredits(1);
			}
			else {
				activePlayer.chCredits(2);
			}
            GUIboard.displaySuccess();
			//System.out.println("That was some acting!");
			set.shotComplete();
			GUIboard.updateScene((Set)activePlayer.getLocation());
			return;
		}
		else if (extra) {
			activePlayer.chDollars(1);
		}
        GUIboard.displayFailure();
		//System.out.println("Your performance failed to impress anybody");
	}
	
	private boolean oneOf(Role target, Role[] list) {
		for (Role each : list)
			if (target == each)
				return true;
		return false;
	}
	
	private void showHelp() {
		String help = "commands:\n"
			+ "move -- presents move options and prompts for name of space to move to\n"
			+ "move [space] -- move to the named space\n"
			+ "work -- presents role info and prompts for name of a role to work\n"
			+ "work [role] -- start working the named role\n"
			+ "upgrade -- presents upgrade options and prompts for payment and rank\n"
			+ "upgrade [$/cr] [rank] -- purchase rank with dollars or credits\n"
			+ "rehearse -- rehearse for your current role\n"
			+ "act -- act for your current role";
		System.out.println(help);
	}
}
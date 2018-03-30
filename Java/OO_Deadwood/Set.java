// Set

import java.util.Random;
import java.util.Arrays;
import javax.swing.*;
import javax.imageio.ImageIO;

public class Set extends Space {
	private String title;
	private boolean sceneRevealed;
	private Scene scene;
	private JLabel label;
	private Role[] roles;
	private int shotTotal;
	private int shotCurrent;
	private int[][] shotAreas;
	private JLabel[] shotLabels;
	
	public Set(String title, int shotcount, Role[] setRoles, int x, int y, int h, int w, int[][] shotAreas) {
		super(x, y, h ,w);
		this.title = title;
		sceneRevealed = false;
		roles = setRoles;
		scene = null;
		shotTotal = shotCurrent = shotcount;
		this.shotAreas = shotAreas;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setScene(Scene newScene) {
		scene = newScene;
		sceneRevealed = false;
		shotCurrent = shotTotal;
		for (Role eachRole : roles)
			eachRole.removePlayer();
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public boolean hasScene() {
		return shotCurrent > 0;
	}
	
	public void revealScene() {
		sceneRevealed = true;
	}
	
	public Role[] getRoles() {
		return roles;
	}
	
	public void shotComplete() {
		shotCurrent--;
		if (shotCurrent == 0) {
			System.out.println("It's a wrap!");
			Role[] sceneRoles = scene.getRoles();
			if (cardHasPlayer(sceneRoles)) {
				onCardPayout(sceneRoles);
				offCardPayout();
				leaveRoles(sceneRoles);
			}
			leaveRoles(roles); // class attr set roles
			scene = null;
		}
	}
	
	private void onCardPayout(Role[] sceneRoles) {
		int[] payout = makePayout(scene.getBudget());
		sort(sceneRoles);
		for (int i=0; i < payout.length; i++){
			if (sceneRoles[i%sceneRoles.length].getPlayer() != null){
				Player activePlayer = sceneRoles[i%sceneRoles.length].getPlayer();
				System.out.println("Player: " + activePlayer.getName() + " gets "+payout[i]);
				activePlayer.chDollars(payout[i]);
			}
		}
	}
	private void offCardPayout() {
		for (Role extra : roles) {
			if (extra.getPlayer() != null)
				extra.getPlayer().chDollars(extra.getRank());
		}
	}
	private void leaveRoles(Role[] roles) {
		for (Role role : roles)
			role.removePlayer();
	}
	
	private boolean cardHasPlayer(Role[] sceneRoles) {
		for (int i=0; i < sceneRoles.length; i++){
				if(sceneRoles[i].getPlayer() != null)
					return true;
		}
		return false;
	}
	
	private int[] makePayout(int budget) {
		int[] payout = new int[budget];
		for (int i=0; i < budget; i++)
			payout[i] = (int)((Math.random()*6)+1);
		sort(payout);
		return payout;
	}
	
	private void sort(int[] arr) {
		int len = arr.length;
		for (int i=1; i < len; i++) {
			int j = i;
			while (j > 0 && arr[j-1] < arr[j])
				swap(arr, j-1, j--);
		}
	}
	private void swap(int[] arr, int i, int j) {
		int aux = arr[i];
		arr[i] = arr[j];
		arr[j] = aux;
	}
	
	private void sort(Role[] arr) {
		int len = arr.length;
		for (int i=1; i < len; i++) {
			int j = i;
			while (j > 0 && arr[j-1].getRank() < arr[j].getRank())
				swap(arr, j-1, j--);
		}
	}
	private void swap(Role[] arr, int i, int j) {
		Role aux = arr[i];
		arr[i] = arr[j];
		arr[j] = aux;
	}
	
	public JLabel getSceneLabel(JLayeredPane GUI) {
		if (label == null) {
			createShotLabels(GUI);
			label = new JLabel();
			GUI.add(label,new Integer(4));
		}
		if (scene != null) {
			ImageIcon cIcon;
			if (sceneRevealed) {
				int ID = scene.getID();
				if (scene.getID() > 9)
					cIcon =  new ImageIcon(String.format(("./images/cards/%d.png"), ID));
				else
					cIcon =  new ImageIcon(String.format(("./images/cards/0%d.png"), ID));
			}
			else
				cIcon =  new ImageIcon("./images/cardback.png");
			label.setIcon(cIcon);
			label.setVisible(true);
		}
		else
			label.setVisible(false);
		updateShotLabels();
		return label;
	}
	
	public void createShotLabels(JLayeredPane GUI) {
		shotLabels = new JLabel[shotTotal];
		for (int i=0; i < shotTotal; i++) {
			shotLabels[i] = new JLabel();
			shotLabels[i].setIcon(new ImageIcon("./images/shot.png"));
			shotLabels[i].setBounds(shotAreas[i][0], shotAreas[i][1], shotAreas[i][2], shotAreas[i][3]);
			shotLabels[i].setVisible(true);
			GUI.add(shotLabels[i],new Integer(4));
		}
	}
	
	public void updateShotLabels() {
		if (shotLabels != null) {
			int i = shotTotal-1;
			while (i >= shotCurrent)
				shotLabels[i--].setVisible(false);
			while (i >= 0)
				shotLabels[i--].setVisible(true);
		}
	}
	
	public String toString() {
		String str = String.format("%-16s", title);
		str += "shots: " + shotCurrent + " of " + shotTotal + " remaining\n";
		if (scene != null)
			str += "  scene: " + (sceneRevealed ? scene.toString() : "hidden") + "\n";
		else
			str += "  scene: completed for today\n";
		str += "  set roles:";
		for (Role role : roles)
			str += "\n    " + role.toString();
		return str;
	}
}

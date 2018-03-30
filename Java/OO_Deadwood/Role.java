// Role

public class Role {
	private String title;
	private int rank;
	private String flavor;
	private Player attachedPlayer;
   private int x;
   private int y;
   private int h;
   private int w;
	
	public Role (String title, int rank, String flavor, int x, int y, int h, int w) {
		this.title = title;
		this.rank = rank;
		this.flavor = flavor;
		this.x = x;
		this.y = y;
		this.h = h;
		this.w = w;
	}
	
	// attach player
	public void attachPlayer(Player player) {
		attachedPlayer = player;
	}
	
	// get title
	public String getTitle() {
		return title;
	}
   
   public int getX() {
		return x;
	}
   public int getY() {
		return y;
	}
   public int getH() {
		return h;
	}
   public int getW() {
		return w;
	}
	
	// get rank
	public int getRank() {
		return rank;
	}
	
	// get flavor
	public String getFlavor() {
		return flavor;
	}
	
	// get player
	public Player getPlayer() {
		return attachedPlayer;
	}
	
	// remove player
	public void removePlayer() {
		if (attachedPlayer != null) {
			attachedPlayer.leaveRole();
			attachedPlayer = null;
		}
	}
	
	public String toString() {
		String str = String.format("%-20s", title);
		str += "   rank: " + rank;
		str += "   " + flavor;
		if (attachedPlayer != null)
			str += attachedPlayer.getName() + " is working this role";
		return str;
	}
}

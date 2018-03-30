/* Space
	The Space is either of type t, c, or s.
	trailers, castingoffice, or set.
	
	order of connectedSpaces here is not important
*/

public class Space {
	private Space[] connectedSpaces;
   private int x;
   private int y;
   private int h;
   private int w;
	
	// Constructor for space
	public Space(int x, int y, int h, int w) {
		this.x = x;
		this.y = y;
		this.h = h;
		this.w = w;
	}

	// connect adjacent spaces
	public void connectSpaces(Space[] connections) {
		connectedSpaces = connections;
	}
	
	// checks if connection is not already in connectedSpaces
	private boolean newConnection(Space connection) {
		for (int i = 0; i < 6; i++)
			if (connectedSpaces[i] == connection)
				return false;
		if (connection == this)
			return false;
		return true;
	}
	
	// get Connections
	public Space[] getConnections() {
		return connectedSpaces;
	}
	
	public void printConnections() {
		String str = "connections:";
		for (Space each : connectedSpaces)
			str += " " + each.getTitle()+",";
		String str2 = str.substring(0, str.length()-1);
      System.out.println(str2);
	}
	
	public String getTitle() {
		return "Generic Space";
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
	
	public String toString() {
		return "Generic Space";
	}
}

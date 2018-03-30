// Representation of a node object
class Node {
	private int total;
	private int right;
	private int down;
	private String path;

   //initialize a node
	public Node(int right, int down) {
		total = -1;
		this.right = right;
		this.down = down;
      this.path = "(0,0)";
	}

	public int getTotal() {
		return total;
	}
	
	public int getRight() {
		return right;
	}
   
   public int getDown() {
		return down;
	}

   public String getPath() {
		return path;
	}
	
	public void setTotal(int total) {
		this.total = total;
	}

	public void setPath(String Path, int y, int x) {
		   this.path = Path + " to ("+y+","+x+")";
	}
}
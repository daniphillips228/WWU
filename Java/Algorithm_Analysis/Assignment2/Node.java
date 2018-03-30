// Representation of a node object
class Node {
	private int total;
	private int value;
	private String path;
   private String path2;

   //initialize a node
	public Node(int value) {
		total = 0;
		this.value = value;
	}

	public int getTotal() {
		return total;
	}
 
   public int getValue() {
		return value;
	}

   public String getPath() {
		return path;
	}
   
   public String getPath2() {
		return path2;
	}
	
	public void setTotal(int total) {
		this.total = total;
	}

	public void setPath(String Path) {
		   this.path = Path;
	}
  	public void setPath2(String Path) {
		   this.path2 = Path;
	}
   public void updatePath(String Path, int x) {
		   this.path = Path + " to "+x;
	}
   public void updatePath2(String Path, int x) {
		   this.path2 = Path + " +  "+x;
	}   
}
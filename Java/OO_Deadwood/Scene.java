//Representation of a Scene
public class Scene {
	private String title;
	private int budget;
	private String flavor;
	private Role[] roles;
   private int ID;
	
	public Scene(String title, int budget, String flavor, Role[] roles, int ID){
		this.title = title;
		this.budget = budget;
		this.flavor = flavor;
		this.roles = roles;
		this.ID = ID;
	}
	
	public Role[] getRoles() {
		return roles;
	}
	
	public int getBudget() {
		return budget;
	}
	public String getTitle() {
      return title;
   }
   public int getID() {
      return ID;
   }
   
	public String toString() {
		String str = String.format("%16s   ", title);
		str += "budget: " + budget + "\n";
		str += "      " + flavor + "\n";
		str += "  scene roles:";
		for (Role role : roles)
			str += "\n    " + role.toString();
		return str;
	}
}

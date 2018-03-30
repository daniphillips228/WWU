/**
 * Representation of a graph vertex
 */
public class Vertex {
	private final String label;   // label attached to this vertex
   public final int value;
   public final String route;

	/**
	 * Construct a new vertex
	 * @param label the label attached to this vertex
	 */
	//public Vertex(String label, int value) { 
   public Vertex(String label, int value, String route) {
		if(label == null)
			throw new IllegalArgumentException("null");
		this.label = label;
      this.value = value;
      this.route = route;
	}

	/**
	 * Get a vertex label
	 * @return the label attached to this vertex
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * A string representation of this object
	 * @return the label attached to this vertex
	 */
	public String toString() {
		return label;
	}

 	/**
   * Compares labels
   */
   
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Vertex other = (Vertex) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
}
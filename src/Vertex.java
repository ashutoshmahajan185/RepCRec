import java.util.ArrayList;

public class Vertex {

	String vertex_id;
	ArrayList<Vertex> adjacent_vertices = new ArrayList<Vertex>();

	/**
	 * @author Ashutosh Mahajan
	 * @param vertex_id
	 */
	Vertex(String vertex_id) {
		this.vertex_id = vertex_id;
	}

	/**
	 * @author Ashutosh Mahajan
	 * @param v
	 */
	void addAdjacentVertex(Vertex v) {
		this.adjacent_vertices.add(v);
	}

	/**
	 * @author Ashutosh Mahajan
	 */
	public void printAdjacentVertices() {

		System.out.println("Adjacent Vertices --> " + this.adjacent_vertices.size());
		for(int i = 0; i < this.adjacent_vertices.size(); i ++) {
			System.out.println(this.adjacent_vertices.get(i).vertex_id + " ");
		}

	}
	
	/**
	 * @author Ashutosh Mahajan
	 */
	public String toString() {
		String result = "Vertex: " + vertex_id + "\nAdjacent Vertices: ";
		for(int i = 0 ; i < adjacent_vertices.size(); i ++) {
			result = result + adjacent_vertices.get(i).vertex_id + " ";
		}
		result = result + "\nEdges: ";

		return result;
	}
	
}
import java.util.ArrayList;

public class Vertex {

	String vertex_id;
	//ArrayList<Edge> edges = new ArrayList<Edge>();
	ArrayList<Vertex> adjacent_vertices = new ArrayList<Vertex>();

	Vertex(String vertex_id) {
		this.vertex_id = vertex_id;
	}

	String getID() {
		return this.vertex_id;
	}

	void addAdjacentVertex(Vertex v) {
		//printAdjacentVertices();
		this.adjacent_vertices.add(v);
		//printAdjacentVertices();
	}

	//void addEdge(Edge e) {
	//System.out.println(e);
	//this.edges.add(e);
	//}
	public String toString() {
		String result = "Vertex: " + vertex_id + "\nAdjacent Vertices: ";
		for(int i = 0 ; i < adjacent_vertices.size(); i ++) {
			result = result + adjacent_vertices.get(i).vertex_id + " ";
		}
		result = result + "\nEdges: ";
		//for(int i = 0 ; i < edges.size(); i ++) {
		//result = result + edges.get(i) + "; ";
		//}
		return result;
	}

	public void printAdjacentVertices() {

		System.out.println("Adjacent Vertices----------------" + this.adjacent_vertices.size());
		for(int i = 0; i < this.adjacent_vertices.size(); i ++) {
			System.out.println(this.adjacent_vertices.get(i).vertex_id + " ");
		}

	}

	//public void printEdges() {
	//System.out.print("Edges--------------" + edges.size());
	//for(int i = 0; i < edges.size(); i ++) {
	//System.out.println(edges.get(i));
	//System.out.println(edges.get(i).start_vertex + "-->" + edges.get(i).end_vertex);
	//}
	//}

}

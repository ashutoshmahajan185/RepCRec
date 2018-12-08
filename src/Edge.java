public class Edge {

	Vertex start_vertex;
	Vertex end_vertex;
	
	/**
	 * @author Ashutosh Mahajan
	 * @param start_vertex
	 * @param end_vertex
	 */
	Edge(Vertex start_vertex, Vertex end_vertex) {
		
		this.start_vertex = start_vertex;
		this.end_vertex = end_vertex;
		//this.start_vertex.addAdjacentVertex(this.end_vertex);
		//this.start_vertex.addEdges(this);
		
	}
	
	public String toString() {
		return start_vertex.vertex_id + "-->" + end_vertex.vertex_id;
	}
	
}
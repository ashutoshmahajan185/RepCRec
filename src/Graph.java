import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Graph {

	ArrayList<Edge> graph_edges = new ArrayList<Edge>();
	ArrayList<Vertex> graph_vertices = new ArrayList<Vertex>();
	ArrayList<Vertex> white_set = new ArrayList<Vertex>(); // Set of all unvisited nodes
	ArrayList<Vertex> black_set = new ArrayList<Vertex>(); // Set of all visited nodes
	ArrayList<Vertex> gray_set = new ArrayList<Vertex>();

	/**
	 * @author Ashutosh Mahajan
	 */
	void addEdge(String start_vertex, String end_vertex) {

		Vertex sv = new Vertex(start_vertex);
		Vertex ev = new Vertex(end_vertex);	
		graph_edges.add(new Edge(new Vertex(start_vertex), new Vertex(end_vertex)));
		if(!contains(sv)) {
			graph_vertices.add(graph_edges.get(graph_edges.size() - 1).start_vertex);
		}
		if(!contains(ev)) {
			graph_vertices.add(graph_edges.get(graph_edges.size() - 1).end_vertex);
		}

	}
	
	/**
	 * @author Ashutosh Mahajan
	 */
	public void reverseEdge(String start_vertex, String end_vertex) {

		Vertex temp_sv = new Vertex(start_vertex);
		Vertex temp_ev = new Vertex(end_vertex);
		Edge current_edge = graph_edges.get(findEdge(temp_sv, temp_ev));
		current_edge.start_vertex = temp_ev;
		current_edge.end_vertex = temp_sv;

	}

	/**
	 * @author Ashutosh Mahajan
	 */
	private boolean contains(Vertex sv) {
		boolean flag = false;
		for(int i = 0; i < graph_vertices.size(); i ++) {
			if(graph_vertices.get(i).vertex_id.equals(sv.vertex_id)) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * @author Ashutosh Mahajan
	 */
	int findEdge(Vertex tsv, Vertex tev) {

		int index = -1;
		for(int i = 0; i < graph_edges.size(); i ++) {
			Edge current_edge = graph_edges.get(i);
			if(current_edge.start_vertex.vertex_id.equals(tsv.vertex_id)) {
				if(current_edge.end_vertex.vertex_id.equals(tev.vertex_id)) {
					index = i;
				}
			}
		}

		return index;

	}

	@SuppressWarnings("unchecked")
	/**
	 * @author Ashutosh Mahajan
	 */
	public boolean detectDeadlock() {

		//System.out.println("Detecting Deadlock" + this);
		
		clearAdjacentVertices();
		clearColorSets();
		updateAdjacentVertices();
		
		white_set = (ArrayList<Vertex>) graph_vertices.clone();

		while(white_set.size() > 0) {
			Vertex current = white_set.iterator().next();
			if(dfs(current)) {
				return true;
			}
		}
		return false;


	}

	/**
	 * @author Ashutosh Mahajan
	 */
	private boolean dfs(Vertex current) {

		moveVertexFromWhiteToGray(current);
		for(Vertex neighbour: current.adjacent_vertices) {	

			if(getIndex(neighbour) != -1) {
				neighbour = graph_vertices.get(getIndex(neighbour));
				
				if(presentInBlackSet(neighbour)) {
					continue;
				}
				if(presentInGraySet(neighbour)) {
					return true;
				}
				if(dfs(neighbour)) {
					return true;
				}
			}
		}
		moveVertexFromGrayToBlack(current);
		return false;

	}

	/**
	 * @author Ashutosh Mahajan
	 */
	private void moveVertexFromGrayToBlack(Vertex current) {
		black_set.add(current);
		gray_set.remove(current);
	}

	/**
	 * @author Ashutosh Mahajan
	 */
	private void moveVertexFromWhiteToGray(Vertex current) {

		gray_set.add(current);
		white_set.remove(current);

	}
	
	/**
	 * @author Ashutosh Mahajan
	 */
	private boolean presentInGraySet(Vertex neighbour) {
		boolean flag = false;
		for(int i = 0; i <  gray_set.size(); i ++) {
			if(gray_set.get(i).vertex_id.equals(neighbour.vertex_id)) {
				flag = true;
			}
		}
		return flag;
	}
	/**
	 * @author Ashutosh Mahajan
	 */

	private boolean presentInBlackSet(Vertex neighbour) {

		boolean flag = false;
		for(int i = 0; i <  black_set.size(); i ++) {
			if(black_set.get(i).vertex_id.equals(neighbour.vertex_id)) {
				flag = true;
			}
		}
		return flag;

	}

	/**
	 * @author Ashutosh Mahajan
	 */
	private void updateAdjacentVertices() {

		clearAdjacentVertices();
		for(int i = 0; i < graph_edges.size(); i ++) {
			int index = getIndex(graph_edges.get(i).start_vertex);
			graph_vertices.get(index).addAdjacentVertex(graph_edges.get(i).end_vertex);

		}
	}
	
	/**
	 * @author Ashutosh Mahajan
	 */
	private void clearAdjacentVertices() {
		for(int i = 0; i < graph_vertices.size(); i ++) {
			graph_vertices.get(i).adjacent_vertices.clear();

		}
	}
	/**
	 * @author Ashutosh Mahajan
	 */
	private void clearColorSets() {
		white_set = new ArrayList<Vertex>(); // Set of all unvisited nodes
		black_set = new ArrayList<Vertex>(); // Set of all visited nodes
		gray_set = new ArrayList<Vertex>();
	}

	/**
	 * @author Ashutosh Mahajan
	 */
	private int getIndex(Vertex start_vertex) {

		int index = -1;
		for(int i = 0; i < graph_vertices.size(); i ++) {
			if(graph_vertices.get(i).vertex_id.equals(start_vertex.vertex_id)) {
				index = i;
			}
		}
		return index;
	}

	/**
	 * @author Ashutosh Mahajan
	 */
	public void removeEdge(String vertex) {

		for(int i = 0; i < graph_edges.size(); i ++) {
			if(graph_edges.get(i).start_vertex.vertex_id.equals(vertex)) {
				graph_edges.remove(i);
			} else if(graph_edges.get(i).end_vertex.vertex_id.equals(vertex)) {
				graph_edges.remove(i);
			}
		}

		for(int i = 0; i < graph_vertices.size(); i ++) {
			for(int j = 0; j < graph_vertices.get(i).adjacent_vertices.size(); j ++) {
				if(graph_vertices.get(i).adjacent_vertices.get(j).vertex_id.equals(vertex)) {
					graph_vertices.get(i).adjacent_vertices.remove(j);
				}
			}
			if(graph_vertices.get(i).vertex_id.equals(vertex)) {
				graph_vertices.remove(i);
			}
		}

		HashSet<String> hs = new HashSet<String>();
		for(Edge e: graph_edges) {
			hs.add(e.start_vertex.vertex_id);
			hs.add(e.end_vertex.vertex_id);
		}
		for(int i = 0; i < graph_vertices.size(); i ++) {
			if(!hs.contains(graph_vertices.get(i).vertex_id)) {
				graph_vertices.remove(i);
			}
		}

	}
	
	/**
	 * @author Ashutosh Mahajan
	 */
	boolean checkDegrees() {
		
		HashMap<String, Integer[]> result = new HashMap<String, Integer[]>();
		for(int i = 0; i < graph_vertices.size(); i ++) {
			if(graph_vertices.get(i).vertex_id.substring(0, 1).equals("x")) {
				String current_vertex = graph_vertices.get(i).vertex_id;
				int in = 0;
				int out = 0;
				for(Edge e: graph_edges) {
					if(e.end_vertex.vertex_id.equals(current_vertex)) {
						in ++;
					}
					if(e.start_vertex.vertex_id.equals(current_vertex)) {
						out ++;
					}
				}
				Integer[] inout = {in, out};
				result.put(graph_vertices.get(i).vertex_id, inout);
			}
		}
				
		for(String s: result.keySet()) {
			if (result.get(s)[0]-result.get(s)[1]>0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @author Ashutosh Mahajan
	 */
	public boolean containsMirrorEdges() {
		
		for(Edge e: graph_edges) {
			String sv = e.start_vertex.vertex_id;
			String ev = e.end_vertex.vertex_id;
			for(Edge e1: graph_edges) {
				if(e1.start_vertex.vertex_id.equals(ev) && e1.end_vertex.vertex_id.equals(sv)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @author Ashutosh Mahajan
	 */
	public String toString() {

		String result = "Graph:\n";

		for(int i = 0; i < graph_edges.size(); i ++) {
			result = result + graph_edges.get(i).start_vertex.vertex_id + " --> " + graph_edges.get(i).end_vertex.vertex_id + "\n";
		}
		result = result + "\nVertices:\n";
		for(int i = 0; i < graph_vertices.size(); i ++) {
			result = result + graph_vertices.get(i).vertex_id + " ";
		}		
		return result;

	}

}
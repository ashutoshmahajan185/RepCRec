import java.util.ArrayList;
import java.util.HashSet;

public class Graph {
	
	ArrayList<Edge> graph_edges = new ArrayList<Edge>();
	ArrayList<Vertex> graph_vertices = new ArrayList<Vertex>();
	
	Graph() {
		
	}
	
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
	
	private boolean contains(Vertex sv) {
		boolean flag = false;
		for(int i = 0; i < graph_vertices.size(); i ++) {
			if(graph_vertices.get(i).vertex_id.equals(sv.vertex_id)) {
				flag = true;
			}
		}
		return flag;
	}

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

	public void reverseEdge(String start_vertex, String end_vertex) {
		
		Vertex temp_sv = new Vertex(start_vertex);
		Vertex temp_ev = new Vertex(end_vertex);
		Edge current_edge = graph_edges.get(findEdge(temp_sv, temp_ev));
		current_edge.start_vertex = temp_ev;
		current_edge.end_vertex = temp_sv;
		
	}
	
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

	ArrayList<Vertex> white_set = new ArrayList<Vertex>(); // Set of all unvisited nodes
	ArrayList<Vertex> black_set = new ArrayList<Vertex>(); // Set of all visited nodes
	ArrayList<Vertex> gray_set = new ArrayList<Vertex>();
	
	@SuppressWarnings("unchecked")
	public boolean detectDeadlock() {
		
		//System.out.println("Detecting Deadlock" + this);
		clearAdjacentVertices();
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

	private void clearAdjacentVertices() {
		
		for(int i = 0; i < graph_edges.size(); i ++) {
			int index = getIndex(graph_edges.get(i).start_vertex);
			graph_vertices.get(index).adjacent_vertices.clear();
		
		}
		
	}

	private boolean dfs(Vertex current) {
		
		moveVertexFromWhiteToGray(current);
		for(Vertex neighbour: current.adjacent_vertices) {	
			
			if(getIndex(neighbour) != -1) {
				neighbour = graph_vertices.get(getIndex(neighbour));
			}
			
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
		moveVertexFromGrayToBlack(current);
		return false;
	
	}

	private void moveVertexFromGrayToBlack(Vertex current) {
		black_set.add(current);
		gray_set.remove(current);
	}

	private boolean presentInGraySet(Vertex neighbour) {
		boolean flag = false;
		for(int i = 0; i <  gray_set.size(); i ++) {
			if(gray_set.get(i).vertex_id.equals(neighbour.vertex_id)) {
				flag = true;
			}
		}
		return flag;
	}

	private boolean presentInBlackSet(Vertex neighbour) {

		boolean flag = false;
		for(int i = 0; i <  black_set.size(); i ++) {
			if(black_set.get(i).vertex_id.equals(neighbour.vertex_id)) {
				flag = true;
			}
		}
		return flag;
		
	}

	private void updateAdjacentVertices() {
		
		for(int i = 0; i < graph_edges.size(); i ++) {
			int index = getIndex(graph_edges.get(i).start_vertex);
			graph_vertices.get(index).addAdjacentVertex(graph_edges.get(i).end_vertex);
		
		}
	}

	private int getIndex(Vertex start_vertex) {
		
		int index = -1;
		for(int i = 0; i < graph_vertices.size(); i ++) {
			if(graph_vertices.get(i).vertex_id.equals(start_vertex.vertex_id)) {
				index = i;
			}
		}
		return index;
	}

	private void moveVertexFromWhiteToGray(Vertex current) {
		
		gray_set.add(current);
		white_set.remove(current);

	}

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
	
}

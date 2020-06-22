package edu.southwestern.tasks.gvgai.zelda.level;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import edu.southwestern.util.datastructures.Graph;

public class GraphGrammar<T extends Grammar> {
	private Graph<T>.Node start;
	private List<Graph<T>.Node> nodesBetween;
	private List<Graph<T>.Node> nodesToStart;
	private Graph<T>.Node end;
	private Graph<T> graph;
	
	// Never used?
	//private boolean removeEdge = false;
	
	public GraphGrammar() {
		this.graph = new Graph<>();
		this.nodesBetween = new ArrayList<Graph<T>.Node>();
		this.nodesToStart = new ArrayList<Graph<T>.Node>();

	}
	
	public GraphGrammar(T start) {
		this();
		Graph<T>.Node s = graph.addNode(start);
		this.start = s;
	}
	
	public GraphGrammar(T start, T end) {
		this(start);
		Graph<T>.Node e = graph.addNode(end);
		this.end = e;
	}
	
	/**
	 * Set the start node for the grammar
	 * @param data Data for the new start node
	 */
	public void setStart(T data) {
		Graph<T>.Node s = graph.addNode(data);
		this.start = s;
	}
	
	/**
	 * Set the end node for the grammar
	 * @param data Data to replace the end node in the rule
	 */
	public void setEnd(T data) {
		Graph<T>.Node e = graph.addNode(data);
		this.end = e;
	}
	
	/**
	 * Add a new node with an adjacency to the start node
	 * @param data T data for the node
	 */
	public void addNodeToStart(T data) {
		Graph<T>.Node newNode = graph.addNode(data);
		graph.addUndirectedEdge(start, newNode);
		nodesToStart.add(newNode);
		//this.addToStart = newNode;
	}
	
	/**
	 * Add a new node between the start and end nodes, leaving the adjacency between start and end
	 * @param data
	 */
	public void addNodeBetween(T data) {
		Graph<T>.Node newNode = graph.addNode(data);
		graph.addUndirectedEdge(start, newNode);
		if(end != null)
			graph.addUndirectedEdge(newNode, end);
		nodesBetween.add(newNode);
		//this.addNodeBetween = newNode;
	}
	public List<Graph<T>.Node> getNodesBetween(){
		return nodesBetween;
	}
	public List<Graph<T>.Node> getNodesToStart(){
		return nodesToStart;
	}
//	public Graph<T>.Node getStartNode(){
//		return start;
//	}
	
//	public void addEdge(T data) {
//		Graph<T>.Node newNode = graph.addNode(data);
//		graph.addEdge(start, newNode);
//		end=null;
//		//end = newNode;
//		//graph.addEdge(newNode, end);
////		if(end != null)
////			graph.addEdge(newNode, end);
//	}
	// setNodeBetween complicates things when copying the nodes from the mini-graph to the backbone. Nodes in the mini-graph wouldn't have the adjs from the other graph.
	// Using this method, nodes from the mini-graph would have to have nodes from the other graph and vice versa. This caused problems such as each edge having duplicates
	// causing the dungeon generation to go in an infinite loop
	
//	/**
//	 * Set a new node between the start and end nodes, removing the adjacency between start and end when applying the rule
//	 * @param data
//	 */
//	public void setNodeBetween(T data) {
//		Graph<T>.Node newNode = graph.addNode(data);
//		graph.addEdge(start, newNode);
//		if(end != null) {
//			graph.addEdge(newNode, end);
//			removeEdge = true;
//		}
//	}
	
	public void setOtherGraph(Graph<T>.Node newStart, 
			Graph<T>.Node newEnd, Graph<T> otherG) {
		newStart.copy(start);
		if(end != null) {
			newEnd.copy(end);
		}

	}
	
	public Graph<T> getGraph(){
		return this.graph;
	}
	
	public Graph<T>.Node getGraphStart(){
		return this.start;
	}
	public Graph<T>.Node getGraphEnd(){
		return this.end;
	}
	
	/**
	 * Generates a string based on the graph that could be read in the DOT format
	 * @return string representing the contents of a file in DOT format
	 */
	public String getDOTString() {
		String r = "";
		List<Graph<T>.Node> visited = new LinkedList<>();
		Queue<Graph<T>.Node> queue = new LinkedList<>();
		
		r += "graph {\n";
		
		r += start.getID() + " [label=\"" + start.getData().getLevelType() + ", GS\"]\n";
		visited.add(start);
		
		if(end != null) {
			r += end.getID() + " [label=\"" + end.getData().getLevelType() + ", GE\"]\n";
			visited.add(end);
		}
		
		queue.add(start);
		while(!queue.isEmpty()) {
			Graph<T>.Node n = queue.poll();
			if(!visited.contains(n))
				r += n.getID() + " [label=\"" + n.getData().getLevelType() + "\"]\n";
			queue.addAll(n.adjacentNodes().stream().filter(a -> !visited.contains(a)).collect(Collectors.toList()));
			visited.add(n);
			
		}
		
		List<Graph<T>.Node> v = new ArrayList<>();
		queue = new LinkedList<>();
		queue.add(start);
		while(!queue.isEmpty()) {
			Graph<T>.Node node = queue.poll();
			v.add(node);
			for(Graph<T>.Node a : node.adjacentNodes()) {
				if(!v.contains(a)) {
					r += node.getID() + " -- " + a.getID() +"\n";
					queue.add(a);				
				}
			}
			
		}
		
		r += "}";
		return r;
	}
	
	/**
	 * Construct a new GraphGrammar based on the file input
	 * @param file File object leading the the file to be loaded and read
	 * @throws FileNotFoundException
	 */
	public GraphGrammar(File file) throws FileNotFoundException {
		this();
		Scanner scanner = new Scanner(file);
		scanner.nextLine();
		HashMap<String, Graph<T>.Node> nodes = new HashMap<>();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if(line.indexOf("}") != -1) {
				scanner.close();
				return;
			} else if(line.indexOf("--") != -1) {
				addEdge(line, nodes);
			} else {
				try {
					addNode(line, nodes);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		scanner.close();
	}

	/**
	 * Add undirected edge to the graph
	 * @param line Line containing the two nodes to be connected
	 * @param nodes HashMap of the name of the nodes to the node itself
	 */
	private void addEdge(String line, HashMap<String, Graph<T>.Node> nodes) {
		Scanner s = new Scanner(line);
		String fromName = s.next();
		s.next(); // --
		String toName = s.next();
		s.close();
		
		Graph<T>.Node from = nodes.get(fromName);
		Graph<T>.Node to = nodes.get(toName);
		graph.addUndirectedEdge(from, to);
	}

	/**
	 * Add node from file
	 * @param line Line that has info about node
	 * @param nodes HashMap to map string to nodes to use for edges
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private void addNode(String line, HashMap<String, Graph<T>.Node> nodes) throws Exception {
		Scanner s = new Scanner(line);
		String name = s.next();
		s.close();
		
		String[] values = StringUtils.substringsBetween(line, "\"", "\"")[0].split(", ");
		
		T data = null;
		
		for(String value : values) {
			T d = (T) ZeldaGrammar.getByType(value);
			if(d != null) {
				data = d;
				break;
			}
		}
		
		Graph<T>.Node n = graph.addNode(data);
		nodes.put(name, n);
		setStartEnd(values, n);
	}

	/**
	 * Set either the start or the end for the graph grammar based on the values from the node
	 * @param values String array of values in "label" of the DOT file
	 * @param n Node to be set to start or end if values contain the right value
	 */
	private void setStartEnd(String[] values, Graph<T>.Node n) {
		for(String value : values) {
			System.out.println(value);
			if(value.equals("GS")) {
				System.out.println("START SET ----------");
				this.start = n;
				break;
			} else if (value.equals("GE")) {
				this.end = n;
				break;
			}
		}
	}
}

package edu.southwestern.util.datastructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;

import edu.southwestern.util.random.RandomNumbers;

public class Graph<T>{
	
	private Set<Node> nodes;
	private Node root;
	
	/**
	 * New empty graph
	 */
	public Graph() {
		setNodes(new HashSet<>());
		root = null;
	}
	
	/**
	 * Create a linear graph with undirected edges based off of a list.
	 * Each value in the list is inserted into a Graph Node, and is connected
	 * to the adjacent list elements.
	 * @param list List of items for graph backbone
	 */
	public Graph(List<T> list) {
		this();
		Node previousNode = null;
		for(T item : list) {
			if(previousNode == null) {
				previousNode = addNode(item);
				root = previousNode;
			}
			else {
				Node newNode = addNode(item);
				addUndirectedEdge(previousNode, newNode);
				previousNode = newNode;
			}
		}
	}
	
	/**
	 * Returns a deep copy of the graph
	 * @return Deep copy of graph 
	 */
	public Graph<T> deepCopy(){
		
		// Something wrong with dealing with references.
		// Instead, reduce everything to String IDs,
		// and then reconstruct based on that.
		
//		Graph<T> graph = new Graph<T>();
//		Set<Graph<T>.Node> nodes = getNodes();
//		HashMap<Node, Node> oldToNew = new HashMap<>();
//		// Add all of the nodes
//		for(Node n : nodes) {
//			Node newNode = graph.addNode(n.getData());
//			newNode.setID(n.getID()); // Required for equality to work
//			oldToNew.put(n, newNode);
//		}
//		// Add all of the edges
//		for(Node n : nodes) {
//			Node sourceInNew = oldToNew.get(n);
//			for(Pair<Node, Double> p : n.adjacencies()) {
//				Node targetInNew = oldToNew.get(p.t1);
//				graph.addDirectedEdge(sourceInNew, targetInNew, p.t2);
//			}
//		}
//		graph.root = oldToNew.get(this.root);
//		return graph;

		// Extract graph data
		Set<Graph<T>.Node> nodes = getNodes();
		ArrayList<Pair<T,String>> nodeData = new ArrayList<>(nodes.size());
		for(Node n : nodes) {
			nodeData.add(new Pair<T,String>(n.getData(),n.getID()));
		}
		ArrayList<Triple<String,String,Double>> adjacencyData = new ArrayList<>();
		for(Node n : nodes) {
			for(Pair<Node, Double> p : n.adjacencies()) {
				adjacencyData.add(new Triple<>(n.getID(),p.t1.getID(),p.t2));
			}
		}

		// Create new Graph
		Graph<T> graph = new Graph<T>();
		for(Pair<T,String> p : nodeData) {
			graph.addNode(new Node(p.t1,p.t2));
		}
		for(Triple<String,String,Double> t : adjacencyData) {
			graph.addDirectedEdge(t.t1, t.t2, t.t3);
		}

		if(root != null) {
			String rootID = root.getID();
			graph.root = graph.getNode(rootID);
		} else {
			graph.root = null;
		}
		
		return graph;
	}
	
	public String toString() {
		String result = "Size = " + size() + "\n";
		List<Node> orderedNodes = new LinkedList<>();
		orderedNodes.addAll(nodes);
		Collections.sort(orderedNodes, new Comparator<Node>() {
			@Override
			public int compare(Graph<T>.Node o1, Graph<T>.Node o2) {
				return o1.getID().compareTo(o2.getID());
			}			
		});
		for(Node n : orderedNodes) {
			result += "From "+n+"\n";
			for(Pair<Node,Double> p : n.adjacenciesSortedByEdgeCost()) {
				result += "\tto "+p.t1+" for "+p.t2+"\n";
			}
		}
		return result;
	}

	public Set<Graph<T>.Node> getNodes() {
		return nodes;
	}
	
	/**
	 * Get Node with a particular String ID
	 * @param id ID to look for
	 * @return Node that has that ID
	 */
	public Node getNode(String id) {
		for(Node n : nodes) {
			if(n.getID().equals(id)) return n;
		}
		return null;
	}

	public void setNodes(Set<Graph<T>.Node> nodes) {
		this.nodes = nodes;
	}
	
	/**
	 * Add predefined Node to Graph. May already have adjacencies defined
	 * @param n a Node
	 */
	public void addNode(Node n) {
		nodes.add(n);
		if(root == null)
			root = n;
	}
	
	/**
	 * Add new Node with the designated data
	 * @param data Data to place in a new Node
	 * @return Reference to the Node that is added
	 */
	public Node addNode(T data) {
		Node n = new Node(data);
		nodes.add(n);
		if(root == null)
			root = n;
		return n;
	}
	
	/**
	 * Remove all links to n from any node in the graph
	 * @param n A node to remove
	 * @return If there actually was a Node removed
	 */
	public boolean removeNode(Node n) {		
		for(Node v : nodes) {
			removeEdge(v,n);
		}		
//		System.out.println(this);
//		System.out.println("Remove: "+n);
//		System.out.println(nodes);
		boolean result = nodes.remove(n);
//		System.out.println("AFTER:\n" + nodes);
//		System.out.println(result);
		return result;
	}

	/**
	 * Remove all links to Node with given String ID, and
	 * also remove the Node.
	 * @param id Unique String ID of Node
	 * @return IF Node was actually removed
	 */
	public boolean removeNode(String id) {
		for(Node v : nodes) {
			removeEdge(v.getID(),id);
		}
		Iterator<Node> itr = nodes.iterator();
		while(itr.hasNext()) {
			if(itr.next().getID().equals(id)) {
				itr.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds directed edge from n1 to n2 with specified cost
	 * @param n1 Source node
	 * @param n2 Target node
	 * @param cost Edge cost
	 */
	public void addDirectedEdge(Node n1, Node n2, double cost) {
		n1.adjacencies.add(new Pair<>(n2,cost));
	}

	/**
	 * Add directed edge between nodes with given String IDs
	 * and use the indicated edge cost 
	 * @param sourceID ID of source Node
	 * @param targetID ID of target Node
	 * @param cost Edge cost
	 */
	public void addDirectedEdge(String sourceID, String targetID, Double cost) {
		Node source = getNode(sourceID);
		Node target = getNode(targetID);
		addDirectedEdge(source,target,cost);
	}
	
	/**
	 * Add edges in both directions (undirected) with default cost of 1.0
	 * @param n1 One node
	 * @param n2 Other node
	 */
	public void addUndirectedEdge(Node n1, Node n2) {
		addUndirectedEdge(n1, n2, 1.0);
	}
	
	/**
	 * Add edges in both directions with designated cost
	 * @param n1 One node
	 * @param n2 Other node
	 * @param cost Cost from n1 to n2, AND from n2 to n1
	 */
	public void addUndirectedEdge(Node n1, Node n2, double cost) {
		n1.adjacencies.add(new Pair<>(n2,cost));
		n2.adjacencies.add(new Pair<>(n1,cost));
	}
	
	/**
	 * Remove edges in both directions between two nodes, without regard for cost
	 * @param n1
	 * @param n2
	 */
	public void removeEdge(Node n1, Node n2) {
		removeDirectedEdge(n1, n2);
		removeDirectedEdge(n2, n1);
		
//		Set<Pair<Node,Double>> l1 = n1.adjacencies;
//		Set<Pair<Node,Double>> l2 = n2.adjacencies;
//		System.out.println(l1);
//		System.out.println(l2);
//		if(l1 != null)
//			l1.remove(n2); // <-- This is wrong now ... Pair not Node
//		if(l2 != null)
//			l2.remove(n1); // <-- This is wrong now ... Pair not Node
//		System.out.println(l1);
//		System.out.println(l2);

	}

	/**
	 * Remove edges in both directions between Nodes that have the given String IDs
	 * @param id String ID of one Node
	 * @param id2 String ID of another Node
	 */
	public void removeEdge(String id, String id2) {
		removeDirectedEdge(id, id2);
		removeDirectedEdge(id2, id);
	}
	
	/**
	 * Remove edge in one direction from Node with given String id to 
 	 * Node with String id2 without regard for cost on edge
	 * @param id ID of source Node
	 * @param id2 ID of target Node
	 * @return If removal occurred
	 */
	public boolean removeDirectedEdge(String id, String id2) {
		Set<Pair<Node,Double>> l1 = getNode(id).adjacencies;
		if(l1 != null) {
			Iterator<Pair<Node,Double>> itr = l1.iterator();
			while(itr.hasNext()) { // Loop through edges from n1
				Pair<Node,Double> p = itr.next();
				if(p.t1.getID().equals(id2)) { // Edge from n1 to n2 found
					itr.remove();
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Verify that any Node referenced in an adjacency list is actually
	 * in the nodes list.
	 * @return If graph is valid
	 */
	public boolean checkIntegrity() {
		ArrayList<String> ids = new ArrayList<>(nodes.size());
		// All valid IDs
		for(Node n : nodes) {
			ids.add(n.getID());
		}
		// Check all adjacencies
		for(Node n : nodes) {
			for(Pair<Node,Double> p : n.adjacencies) {
				// Edge to a Node that is not in the Node list
				if(!ids.contains(p.t1.getID())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Remove edge in one direction from n1 to n2 without regard for cost on edge
	 * @param n1 Source edge
	 * @param n2 Target edge
	 * @return Whether an edge was actually removed
	 */
	public boolean removeDirectedEdge(Node n1, Node n2) {
		Set<Pair<Node,Double>> l1 = n1.adjacencies;
		if(l1 != null) {
			Iterator<Pair<Node,Double>> itr = l1.iterator();
			while(itr.hasNext()) { // Loop through edges from n1
				Pair<Node,Double> p = itr.next();
				if(p.t1.equals(n2)) { // Edge from n1 to n2 found
					itr.remove();
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Count the total number of edges in the graph.
	 * An "undirected" edge counts as 2: one in each direction.
	 * @return Total edge count
	 */
	public int totalEdges() {
		int total = 0;
		for(Node n : nodes) {
			for(@SuppressWarnings("unused") Pair<Node,Double> p : n.adjacencies) {
				total++;
			}
		}
		return total;
	}
	
	// Somehow used for Zelda graph grammar
	public List<Node> breadthFirstTraversal(){
		return breadthFirstTraversal(root);
	}
	
	// Somehow used for Zelda graph grammar
	public List<Node> breadthFirstTraversal(Node n){
		List<Node> visited = new ArrayList<>();
		Queue<Node> queue = new LinkedList<>();
		queue.add(n);
		visited.add(n);
		while(!queue.isEmpty()) {
			Node node = queue.poll();
			for(Pair<Node,Double> p : node.adjacencies) {
				Node v = p.t1;
				if(!visited.contains(v)) {
					visited.add(v);
					queue.add(v);
				}
			}
			
		}
		return visited;
	}

	/**
	 * Internal Node class of the Graph
	 * @author Jake G.
	 */
	public class Node{
		private T data;
		// Set of (Node,Cost) pairs for the adjacent edges
		Set<Pair<Node, Double>> adjacencies;
		// A unique ID that is used for equality and hashing
		public String id;
		
		/**
		 * New Node containing the data, but with
		 * no adjacencies. Assigns a new random String
		 * ID based on RandomNumbers.randomGenerator.
		 * 
		 * @param d Data to place in Node
		 */
		public Node(T d){
			setData(d);
			adjacencies = new HashSet<>();
			// The random method replaced a call to randomAlphabetic. This was needed, since the more general random
			// method is the only one that allows a random generator to be supplied, allowing reproducibility.
			id = RandomStringUtils.random(4,'A','Z',true,false,null,RandomNumbers.randomGenerator);
		}
		
		
		private Node(T d, String id){
			setData(d);
			adjacencies = new HashSet<>();
			this.id = id;
		}

		
		public Set<Pair<Graph<T>.Node,Double>> adjacencies() {
			return adjacencies;
		}
		
		/**
		 * Return just the adjacent Nodes without their costs
		 * @return Set of neighboring Nodes
		 */
		public Set<Graph<T>.Node> adjacentNodes() {
			Set<Graph<T>.Node> set = new HashSet<>();
			for(Pair<Graph<T>.Node,Double> p : adjacencies) {
				set.add(p.t1);
			}
			return set;
		}
		
		/**
		 * Sorts the adjacencies for a node by their edge costs from
		 * cheapest neighbor to most expensive neighbor.
		 *  
		 * @return Sorted list of adjacencies
		 */
		public List<Pair<Graph<T>.Node, Double>> adjacenciesSortedByEdgeCost(){
			List<Pair<Graph<T>.Node, Double>> list = new ArrayList<>();
			list.addAll(this.adjacencies);
			Collections.sort(list, new Comparator<Pair<Graph<T>.Node, Double>>(){
				@Override
				public int compare(Pair<Graph<T>.Node, Double> o1, Pair<Graph<T>.Node, Double> o2) {
					// Compares the costs to determine sort order
					return (int) Math.signum(o1.t2-o2.t2);
				}
			});
			return list;
		}
		
		/**
		 * Sorts adjacencies first by decreasing number of
		 * outgoing links, and breaks ties favoring cheapest
		 * links.
		 *  
		 * @return Sorted list of adjacencies
		 */
		public List<Pair<Graph<T>.Node, Double>> adjacenciesSortedByDecreasingOutCount(){
			List<Pair<Graph<T>.Node, Double>> list = new ArrayList<>();
			list.addAll(this.adjacencies);
			Collections.sort(list, new Comparator<Pair<Graph<T>.Node, Double>>(){
				@Override
				public int compare(Pair<Graph<T>.Node, Double> o1, Pair<Graph<T>.Node, Double> o2) {
					// o2 comes first because we want decreasing order of out-count
					int compareOutCount = o2.t1.adjacencies.size() - o1.t1.adjacencies.size();
					if(compareOutCount == 0) // Break the tie with increasing order of cost
						return (int) Math.signum(o1.t2-o2.t2);
					else // sort based on out-count
						return compareOutCount;
				}
			});
			return list;
		}
		
		public void setAdjacencies(Set<Pair<Graph<T>.Node,Double>> a) {
			adjacencies = a;
		}
		public void setData(T data) {
			this.data = data;
		}
		public T getData() {
			return data;
		}
		public String getID() {
			return id;
		}
		
		/**
		 * Change the ID of the Node. 
		 * 
		 * IMPORTANT!
		 * 
		 * If the id is changed after insertion of the item in the HashSet of nodes, it becomes 
		 * impossible to remove it from the HashSet. This is because the hashCode depends on the ID, 
		 * and the item will not be found in the HashSet if its hash code changes. Therefore, we must
		 * remove and reinsert the item into the HashSet
		 * 
		 * @param id New ID (should not be possessed by any other Node)
		 */
		public void setID(String id) {
			nodes.remove(this);
			this.id = id;
			nodes.add(this);
		}
		
		// Schrum: I don't like the way this method is named and used.
		//         It is confusing for the parameter to overwrite the contents of this Node.
		public void copy(Node other) {
			this.data = other.data;
			for(Pair<Node,Double> n : other.adjacencies) {
				adjacencies.add(new Pair<>(n.t1,n.t2));
			}
			this.id = other.id;
		}
		
		/**
		 * Only checks id and nothing else
		 */
		@Override
		public boolean equals(Object other) {
			if(!(other instanceof Graph.Node)) return false;
			@SuppressWarnings("unchecked")
			Node on = (Node) other;
			if(on.id == null && this.id == null)
				return true;
			else if(on.id != null)
				return on.id.equals(this.id);	
			return false;
		}
		
		/**
		 * Based only on id
		 */
		@Override
		public int hashCode() {
			return id.hashCode();
		}
		
		public String toString() {
			return data.toString() + ": \"" + id + "\"";
		}
	}

	public Node root() {
		return root;
	}

	public int size() {
		return nodes.size();
	}
}

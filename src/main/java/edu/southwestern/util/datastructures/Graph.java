package edu.southwestern.util.datastructures;

import java.util.ArrayList;
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
	
	public Graph() {
		setNodes(new HashSet<>());
		root = null;
	}
	
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

	public Set<Graph<T>.Node> getNodes() {
		return nodes;
	}

	public void setNodes(Set<Graph<T>.Node> nodes) {
		this.nodes = nodes;
	}
	
	public void addNode(Node n) {
		nodes.add(n);
		if(root == null)
			root = n;
	}
	
	public Node addNode(T data) {
		Node n = new Node(data);
		nodes.add(n);
		return n;
	}
	
	/**
	 * Remove all links to n from any node in the graph
	 * @param n A node to remove
	 */
	public void removeNode(Node n) {
		for(Node v : nodes) {
			removeEdge(v,n);
		}
		nodes.remove(n);
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
	 * Remove edge in one direction from n1 to n2 without regard for cost on edge
	 * @param n1 Source edge
	 * @param n2 Target edge
	 */
	public void removeDirectedEdge(Node n1, Node n2) {
		Set<Pair<Node,Double>> l1 = n1.adjacencies;
		if(l1 != null) {
			Iterator<Pair<Node,Double>> itr = l1.iterator();
			while(itr.hasNext()) { // Loop through edges from n1
				Pair<Node,Double> p = itr.next();
				if(p.t1.equals(n2)) { // Edge from n1 to n2 found
					itr.remove();
					break;
				}
			}
		}
	}
	
	public List<Node> breadthFirstTraversal(){
		return breadthFirstTraversal(root);
	}
	
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

	public class Node{
		private T data;
		Set<Pair<Node, Double>> adjacencies;
		public String id;
		public Node(T d){
			setData(d);
			adjacencies = new HashSet<>();
			// The random method replaced a call to randomAlphabetic. This was needed, since the more general random
			// method is the only one that allows a random generator to be supplied, allowing reproducibility.
			id = RandomStringUtils.random(4,'A','Z',true,false,null,RandomNumbers.randomGenerator);
		}
		public Set<Pair<Graph<T>.Node,Double>> adjacencies() {
			return adjacencies;
		}
		
		public Set<Graph<T>.Node> adjacentNodes() {
			Set<Graph<T>.Node> set = new HashSet<>();
			for(Pair<Graph<T>.Node,Double> p : adjacencies) {
				set.add(p.t1);
			}
			return set;
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
		public void setID(String id) {
			this.id = id;
		}
		
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
			return data.toString() + ": " + id;
		}
	}

	public Node root() {
		return root;
	}

	public int size() {
		return nodes.size();
	}


}

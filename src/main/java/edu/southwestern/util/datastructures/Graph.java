package edu.southwestern.util.datastructures;

import java.util.ArrayList;
import java.util.HashSet;
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
				addEdge(previousNode, newNode);
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
	
	public void removeNode(Node n) {
		for(Node v : nodes) {
			v.adjacencies().remove(n);
		}
		nodes.remove(n);
	}
	
	public void addEdge(Node n1, Node n2) {
		n1.adjacencies.add(n2);
		n2.adjacencies.add(n1);
	}
	
	public void removeEdge(Node n1, Node n2) {
		Set<Node> l1 = n1.adjacencies;
		Set<Node> l2 = n2.adjacencies;
		System.out.println(l1);
		System.out.println(l2);
		if(l1 != null)
			l1.remove(n2);
		if(l2 != null)
			l2.remove(n1);
		System.out.println(l1);
		System.out.println(l2);

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
			for(Node v : node.adjacencies) {
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
		Set<Node> adjacencies;
		public String id;
		public Node(T d){
			setData(d);
			adjacencies = new HashSet<>();
			// The random method replaced a call to randomAlphabetic. This was needed, since the more general random
			// method is the only one that allows a random generator to be supplied, allowing reproducibility.
			id = RandomStringUtils.random(4,'A','Z',true,false,null,RandomNumbers.randomGenerator);
		}
		public Set<Graph<T>.Node> adjacencies() {
			return adjacencies;
		}
		
		public void setAdjacencies(Set<Node> a) {
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
			for(Node n : other.adjacencies) {
				adjacencies.add(n);
			}
			this.id = other.id;
		}
		
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

package edu.southwestern.util.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Graph<T>{
	
	private List<Node> nodes;
	private Node root;
	
	public Graph() {
		setNodes(new LinkedList<>());
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

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	
	public void addNode(Node n) {
		if(!nodes.contains(n))
			nodes.add(n);
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
		List<Node> l1 = n1.adjacencies;
		List<Node> l2 = n2.adjacencies;
		if(l1 != null)
			l1.remove(n2);
		if(l2 != null)
			l2.remove(n1);
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
		List<Node> adjacencies;
		public Node(T d){
			setData(d);
			adjacencies = new LinkedList<>();
		}
		public List<Node> adjacencies() {
			return adjacencies;
		}
		public void setData(T data) {
			this.data = data;
		}
		public T getData() {
			return data;
		}
		public void copy(Node other) {
			this.data = other.data;
			for(Node n : other.adjacencies)
				adjacencies.add(n);
		}
	}

	public Node root() {
		return root;
	}


}

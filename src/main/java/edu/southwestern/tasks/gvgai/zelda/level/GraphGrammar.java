package edu.southwestern.tasks.gvgai.zelda.level;

import java.util.List;

import edu.southwestern.util.datastructures.Graph;
import edu.southwestern.util.datastructures.Graph.Node;

public class GraphGrammar<T extends Grammar> {
	private Graph<T>.Node start;
	private Graph<T>.Node end;
	private Graph<T> graph;
	
	public GraphGrammar() {
		this.graph = new Graph<>();
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
	
	public void setStart(T data) {
		Graph<T>.Node s = graph.addNode(data);
		this.start = s;
	}
	
	public void setEnd(T data) {
		Graph<T>.Node e = graph.addNode(data);
		this.end = e;
	}
	
	public void addNodeToStart(T data) {
		Graph<T>.Node newNode = graph.addNode(data);
		graph.addEdge(start, newNode);
	}
	
	public void addNodeBetween(T data) {
		Graph<T>.Node newNode = graph.addNode(data);
		graph.addEdge(start, newNode);
		if(end != null)
			graph.addEdge(newNode, end);
	}
	
	public void setOtherGraph(Graph<T>.Node newStart, 
			Graph<T>.Node newEnd, Graph<T> otherG) {
		
		System.out.println("START--------");
		System.out.println(otherG.size());
		for(Graph<T>.Node n : start.adjacencies())
			otherG.addNode(n);
		
		newStart.copy(start);

		if(end != null) {
			for(Graph<T>.Node n : end.adjacencies())
				otherG.addNode(n);

			newEnd.copy(end);
		}
		
		
		System.out.println(otherG.size());
		System.out.println("END--------");
	}
	
	public Graph<T> getGraph(){
		return this.graph;
	}
	
	public Graph<T>.Node getGraphStart(){
		return this.start;
	}
}

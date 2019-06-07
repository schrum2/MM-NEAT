package edu.southwestern.tasks.gvgai.zelda.level;

import edu.southwestern.util.datastructures.Graph;

public class GraphRule<T extends Grammar> {
	private T symbolStart;
	private T symbolEnd;
	private GraphGrammar<T> grammar;
	
	public GraphRule(T symbolStart) {
		this.grammar = new GraphGrammar<>();
		this.symbolStart = symbolStart;
	}
	
	public GraphRule(T symbolStart, T symbolEnd) {
		this.grammar = new GraphGrammar<>();
		this.symbolStart = symbolStart;
		this.symbolEnd = symbolEnd;
	}
	
	public GraphGrammar<T> grammar(){
		return this.grammar;
	}
	
	public void setStart(T data) {
		grammar.setStart(data);
	}
	
	public void setEnd(T data) {
		grammar.setEnd(data);
	}
	
	public T getSymbolStart() {
		return this.symbolStart;
	}
	
	public T getSymbolEnd() {
		return this.symbolEnd;
	}

	public Graph<T> getGraph() {
		return grammar.getGraph();
	}

	public Graph<T>.Node getStart() {
		return grammar.getGraphStart();
	}
}

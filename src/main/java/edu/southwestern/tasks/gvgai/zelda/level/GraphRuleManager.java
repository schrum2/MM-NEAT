package edu.southwestern.tasks.gvgai.zelda.level;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import edu.southwestern.util.datastructures.Graph;
import edu.southwestern.util.datastructures.Graph.Node;

abstract public class GraphRuleManager<T extends Grammar> {
	protected List<GraphRule<T>> graphRules;
	
	public GraphRuleManager() {
		graphRules = new LinkedList<>();
	}
	
	public List<GraphRule<T>> findRule(T start) {
		List<GraphRule<T>> rules = new LinkedList<>();
		
		for(GraphRule<T> r : graphRules) {
			if(r.getSymbolStart().equals(start))
				rules.add(r);
		}
		
		return rules;
	}
	
	public List<GraphRule<T>> findRule(T start, T end){
		List<GraphRule<T>> rules = new LinkedList<>();
		
		for(GraphRule<T> r : graphRules) {
			if(r.getSymbolStart().equals(start) &&
					r.getSymbolEnd().equals(end))
				rules.add(r);
		}
		
		if(rules.size() == 0)
			return findRule(start);
		
		return rules;
	}
	
	public Graph<T> applyRules(Graph<T> graph) {
		boolean symbols = true;
		boolean appliedRule = false;
		Stack<Graph<T>.Node> visited = new Stack<>();
		Node node = graph.root();
		visited.push(node);
		while(!visited.isEmpty()) {
			Graph<T>.Node current = visited.pop();
			List<Graph<T>.Node> adj = current.adjacencies();
			for(Graph<T>.Node n : adj) {
				applyRule(graph, current, n);
				visited.push(n);
			}
		}
//		while(symbols) {
//			symbols = false;
//			List<Graph<T>.Node> nodes = graph.breadthFirstTraversal();
//			for(Graph<T>.Node n : nodes)
//				System.out.println(n.getData().toString());
//			System.out.println();
//			
//			for(int i = 0; i < nodes.size() - 1; i++) {
//				Graph<T>.Node node = nodes.get(i);
//				if(!appliedRule) {
//
//					if(!node.getData().isSymbol()) continue;
//					Graph<T>.Node nextNode = nodes.get(i + 1);
//					applyRule(graph, node, nextNode);
//				}
//
//				if(symbols == false)
//					symbols = node.getData().isSymbol();
//				
//			}
//		}
		
		return graph;
	}
	
	public void applyRule(Graph<T> graph, Graph<T>.Node node, Graph<T>.Node nextNode) {
		List<GraphRule<T>> rules = findRule(node.getData(), nextNode.getData());
		if(rules.size() > 0) {
			GraphRule<T> ruleToApply = rules.get((int) Math.random() * rules.size());
			if(ruleToApply != null) {
				ruleToApply.grammar().setOtherGraph(node, nextNode, graph);
			}
		}
	}
}
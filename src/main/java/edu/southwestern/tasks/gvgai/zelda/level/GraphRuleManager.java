package edu.southwestern.tasks.gvgai.zelda.level;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import edu.southwestern.util.datastructures.Graph;
import edu.southwestern.util.datastructures.GraphUtil;
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
			if(r.getSymbolStart().equals(start) && r.getSymbolEnd() != null &&
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
		int i = 0;
		while(symbols) {
			symbols = false;
			List<Graph<T>.Node> visited = new ArrayList<>();
			Queue<Graph<T>.Node> queue = new LinkedList<>();
			Graph<T>.Node node = graph.root();
			visited.add(node);
			queue.add(node);
			while(!queue.isEmpty()) {
				Graph<T>.Node current = queue.poll();
				symbols = symbols || current.getData().isSymbol();
				System.out.println(current);
				List<Graph<T>.Node> adj = new LinkedList<>(current.adjacencies());
				for(Graph<T>.Node n : adj) {
					if(!visited.contains(n)) {
						applyRule(graph, current, n, i++);
						visited.add(n);
						queue.add(n);
					}
				}
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
	
	public void applyRule(Graph<T> graph, Graph<T>.Node node, Graph<T>.Node nextNode, int i) {
		List<GraphRule<T>> rules = findRule(node.getData(), nextNode.getData());
		System.out.println("Found rules " + rules.size());
		System.out.println(node + "->" + nextNode);
		if(rules.size() > 0) {
			Random r = new Random();
			GraphRule<T> ruleToApply = rules.get(r.nextInt(rules.size()));
			if(ruleToApply != null) {
				ruleToApply.grammar().setOtherGraph(node, nextNode, graph);
			}
		}
	}
}
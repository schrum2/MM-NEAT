package edu.southwestern.tasks.gvgai.zelda.level;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.io.FileUtils;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Graph;
import edu.southwestern.util.random.RandomNumbers;

abstract public class GraphRuleManager<T extends Grammar> {
	protected List<GraphRule<T>> graphRules;
	
	public GraphRuleManager() {
		graphRules = new LinkedList<>();
	}
	
	public GraphRuleManager(File directory) {
		this();
		loadRules(directory);
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
	
	public Graph<T> applyRules(Graph<T> graph) throws Exception {
		boolean symbols = true;
		int i = 0;
		int times = 0;
		int maxTries = 5;
		while(symbols && times <= maxTries) {
			symbols = false;
			List<Graph<T>.Node> visited = new ArrayList<>();
			Queue<Graph<T>.Node> queue = new LinkedList<>();
			Graph<T>.Node node = graph.root();
			visited.add(node);
			queue.add(node);
			while(!queue.isEmpty()) {
				Graph<T>.Node current = queue.poll();
				visited.add(current);
				symbols = symbols || current.getData().isSymbol();
				List<Graph<T>.Node> adj = new LinkedList<>(current.adjacencies());
				boolean appliedRule = false;
				for(Graph<T>.Node n : adj) {
					if(!visited.contains(n) && !queue.contains(n)) {
						appliedRule = applyRule(graph, current, n, i++);
						queue.add(n);
					}
				}
				if(!appliedRule) {
					applyRule(graph, current, null, i++);
				}
				
			}
			times++;
		}
		
		if(times > maxTries)
			throw new Exception("Graph chouldn't be completed");
		
		return graph;
	}
	
	public boolean applyRule(Graph<T> graph, Graph<T>.Node node, Graph<T>.Node nextNode, int i) {
		boolean appliedRule = false;
		List<GraphRule<T>> rules;
		if(nextNode != null)
			rules = findRule(node.getData(), nextNode.getData());
		else
			rules = findRule(node.getData());
		if(rules.size() > 0) {
			GraphRule<T> ruleToApply = rules.get(RandomNumbers.randomGenerator.nextInt(rules.size()));
			if(ruleToApply != null) {
//				if(nextNode != null) graph.removeEdge(node, nextNode);
				ruleToApply.grammar().setOtherGraph(node, nextNode, graph);
				if(Parameters.parameters != null && Parameters.parameters.booleanParameter("rogueLikeDebugMode")) {
					System.out.println(node.id);
					System.out.println(node.adjacencies());
					if(nextNode != null) {
						System.out.println(nextNode.id);
						System.out.println(nextNode.adjacencies());
					}
					System.out.println("--------------------------------------");
					System.out.println(ruleToApply.getSymbolStart().getLevelType());
					System.out.println(ruleToApply.grammar().getDOTString());
					if(ruleToApply.getSymbolEnd() != null)
						System.out.println(ruleToApply.getSymbolEnd().getLevelType());
				}
				appliedRule = true;
			}
		}
//		if(appliedRule) {
//			try {
//				GraphUtil.saveGrammarGraph(graph, "data/VGLC/Zelda/GraphDOTs/graph_" + i + ".dot");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		return appliedRule;
	}
	
	/**
	 * Save the graph grammar and rules to a directory
	 * @param file Directory to save the rules
	 * @throws IOException
	 */
	public void saveRules(File file) throws IOException{
		Files.createDirectories(file.toPath());
		FileUtils.cleanDirectory(file);
		int i = 0;
		for(GraphRule<T> rule : graphRules)
			rule.saveToFile(i++, file);
	}
	
	/**
	 * Load graph grammar + rules from directory
	 * @param file Directory to load the rules
	 */
	public void loadRules(File file) {
		for(File f : file.listFiles()) {
			graphRules.add(new GraphRule<>(f));
		}
	}
}
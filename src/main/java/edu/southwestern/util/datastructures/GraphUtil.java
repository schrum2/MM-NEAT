package edu.southwestern.util.datastructures;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.southwestern.tasks.gvgai.zelda.level.Grammar;
import edu.southwestern.util.datastructures.Graph.Node;

public class GraphUtil {
	public static void saveGrammarGraph(Graph<? extends Grammar> graph, String file) throws IOException {
		File f = new File(file);
		BufferedWriter w = new BufferedWriter(new FileWriter(f.getAbsolutePath()));
		w.write("graph {\n");
		
		Graph<? extends Grammar>.Node n = graph.root();
		List<Graph<? extends Grammar>.Node> visited = new ArrayList<>();
		Queue<Graph<? extends Grammar>.Node> queue = new LinkedList<>();
		queue.add(n);
		visited.add(n);
		while(!queue.isEmpty()) {
			Graph<? extends Grammar>.Node node = queue.poll();
			w.write(node.getID() + "[label=\"" + node.getData().getLevelType() + "\"]\n");
			for(Graph<? extends Grammar>.Node v : node.adjacencies()) {
				if(!visited.contains(v)) {
					visited.add(v);
					queue.add(v);
				}
			}
			
		}
	
		n = graph.root();
		visited = new ArrayList<>();
		queue = new LinkedList<>();
		queue.add(n);
		while(!queue.isEmpty()) {
			Graph<? extends Grammar>.Node node = queue.poll();

			visited.add(node);
			for(Graph<? extends Grammar>.Node v : node.adjacencies()) {
				if(!visited.contains(v)) {
					w.write(node.getID() + " -- " + v.getID() +"\n");
					queue.add(v);				
				}
			}
			
		}
		
		
		w.write("}");
		w.close();
	}
}

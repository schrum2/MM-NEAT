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
import java.util.Scanner;

import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.level.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.level.Grammar;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGrammar;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaDungeon.Level;
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
	
	public static Dungeon convertToDungeon(Graph<? extends Grammar> graph) throws FileNotFoundException {
		Dungeon dungeon = new Dungeon();
		String[][] levelThere = new String[100][100];
		int x = (levelThere.length - 1) / 2;
		int y = levelThere.length - 1;

		Graph<? extends Grammar>.Node n = graph.root();
		Level l = loadLevel(n);
		List<Graph<? extends Grammar>.Node> visited = new ArrayList<>();
		Queue<Graph<? extends Grammar>.Node> queue = new LinkedList<>();
		queue.add(n);
		while(!queue.isEmpty()) {
			if(x >= 0 && x < levelThere[0].length && y >= 0 && y < levelThere.length ) {
				Graph<? extends Grammar>.Node node = queue.poll();
				visited.add(node);
				levelThere[y][x] = node.getID();
				Level level = loadOneLevel(new File("data/VGLC/Zelda/n.txt"));
				
			}

		}
		return dungeon;
	}
	
	private static Level loadLevel(Graph<? extends Grammar>.Node n) throws FileNotFoundException {
		Level l = loadOneLevel(new File("data/VGLC/Zelda/n.txt"));
		
		return null;
	}

	private static Level loadOneLevel(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		String[] levelString = new String[16];
		int i = 0;
		while(scanner.hasNextLine())
			levelString[i++] = scanner.nextLine();
			
		List<List<Integer>> levelInt = ZeldaVGLCUtil.convertZeldaLevelVGLCtoRoomAsList(levelString);
		Level level = new Level(levelInt);
		scanner.close();
		return level;
	}
}

package edu.southwestern.util.datastructures;

import java.awt.Point;
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
import java.util.Random;
import java.util.Scanner;

import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.level.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.level.Grammar;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaDungeon;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGrammar;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaDungeon.Level;
import edu.southwestern.util.datastructures.Graph.Node;
import me.jakerg.rougelike.Tile;

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
	
	public static Dungeon convertToDungeon(Graph<? extends Grammar> graph) throws Exception {
		Dungeon dungeon = new Dungeon();
		String[][] levelThere = new String[100][100];
		int x = (levelThere.length - 1) / 2;
		int y = (levelThere.length - 1) / 2;

		Graph<? extends Grammar>.Node n = graph.root();
		
		Level l = loadLevel(n, dungeon);
		Dungeon.Node dNode = dungeon.newNode(n.getID(), l);
		levelThere[y][x] = dNode.name;
		dungeon.setCurrentLevel(dNode.name);
		
		List<Graph<? extends Grammar>.Node> visited = new ArrayList<>();
		Queue<Graph<? extends Grammar>.Node> queue = new LinkedList<>();
		queue.add(n);
		while(!queue.isEmpty()) {
			Graph<? extends Grammar>.Node node = queue.poll();
			Dungeon.Node dN = dungeon.getNode(node.getID());
			visited.add(node);
			Point p = getCoords(levelThere, node.getID());
			if(p == null)
				throw new Exception("Node : " + node.getID() + " not found in level there");
			for(Graph<? extends Grammar>.Node adjNode : node.adjacencies()) {
				if(!visited.contains(adjNode)) {
					System.out.println("Going to node: " + adjNode.getID());
					Point legal = getNextLegalPoint(p, levelThere);
					if(legal != null) {
						System.out.println("Placing " + adjNode.getID() + " at (" + legal.x + ", " + legal.y + ")");
						levelThere[legal.y][legal.x] = adjNode.getID();
						Level newLevel = loadLevel(adjNode, dungeon);
						Dungeon.Node newNode = dungeon.newNode(adjNode.getID(), newLevel);
						int tile = (node.getData().getLevelType() == "l") ? Tile.LOCKED_DOOR.getNum() : Tile.DOOR.getNum();
						setAdjacencies(dN, p, legal, newNode.name, tile);
						setAdjacencies(newNode, legal, p, dN.name, tile);
						queue.add(adjNode);
					} else {
						print2DArray(ZeldaLevelUtil.trimLevelThere(levelThere));
						throw new Exception("Didn't get a legal point for node: " + adjNode.getID() + " from node : " + node.getID());
					}
				}
			}

		}
		dungeon.setLevelThere(ZeldaLevelUtil.trimLevelThere(levelThere));
		return dungeon;
	}
	
	private static void print2DArray(String[][] array) {
		for(String[] row : array) {
			for(String s : row) {
				System.out.println(s +",");
			}
			System.out.println();
		}
	}

	private static void setAdjacencies(Dungeon.Node fromNode, Point from,
			Point to, String whereTo, int tile) throws Exception {
		String direction = getDirection(from, to);

		switch(direction) {
		case "UP":
			ZeldaLevelUtil.addUpAdjacencies(fromNode, whereTo);
			break;
		case "DOWN":
			ZeldaLevelUtil.addDownAdjacencies(fromNode, whereTo);
			break;
		case "LEFT":
			ZeldaLevelUtil.addLeftAdjacencies(fromNode, whereTo);
			break;
		case "RIGHT":
			ZeldaLevelUtil.addRightAdjacencies(fromNode, whereTo);
			break;
		default:
			throw new Exception ("DIRECTION AINT HEREE");
		}
		
		ZeldaLevelUtil.setDoors(direction, fromNode.level.intLevel, tile);
	}
	
	

	private static String getDirection(Point from, Point to) {
		int dX = from.x - to.x;
		int dY = from.y - to.y;
		if(dX == -1 && dY == 0)
			return "RIGHT";
		else if(dX == 1 && dY == 0)
			return "LEFT";
		else if(dX == 0 && dY == -1)
			return "DOWN";
		else if(dX == 0 && dY == 1)
			return "UP";
		else
			return null;
	}

	private static Point getNextLegalPoint(Point p, String[][] levelThere) {
		int y = p.y;
		int x = p.x;
		int tries = 0;
		while(tries < 4) {
			switch(tries) {
			case 0: //UP
				y--;
				break;
			case 2: //DOWN
				y++;
				break;
			case 1: // Left
				x--;
				break;
			case 3:
				x++;
				break;
			}
			
			if(x >= 0 && x < levelThere[0].length && y >= 0 && y < levelThere.length) {
				if(levelThere[y][x] == null) {
					System.out.println(levelThere[y][x]);
					return new Point(x, y);
				}
					
			}
			tries++;
			y = p.y;
			x = p.x;
		}
		return null;
	}

	private static Point getCoords(String[][] levelThere, String n) {
		for(int y = levelThere.length - 1; y >= 0; y--) {
			for(int x = 0; x < levelThere[0].length; x++) {
				if(levelThere[y][x] == n)
					return new Point(x, y);
			}
		}
		return null;
	}
	
	private static Level loadLevel(Graph<? extends Grammar>.Node n, Dungeon dungeon) throws FileNotFoundException {
		Level level = loadOneLevel(new File("data/VGLC/Zelda/n.txt"));
		switch(n.getData().getLevelType()) {
		case "k":
			ZeldaLevelUtil.placeRandomKey(level.intLevel);
			break;
		case "n":
		case "l":
			break;
		case "e":
			ZeldaLevelUtil.addRandomEnemy(level.intLevel);
			break;
		case "t":
			level = level.placeTriforce(dungeon);
			dungeon.setGoal(n.getID());
			break;
		}
		return level;
	}

	private static Level loadOneLevel(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		String[] levelString = new String[11];
		int i = 0;
		while(scanner.hasNextLine())
			levelString[i++] = scanner.nextLine();
			
		List<List<Integer>> levelInt = ZeldaVGLCUtil.convertZeldaLevelVGLCtoRoomAsList(levelString);
		Level level = new Level(levelInt);
		scanner.close();
		return level;
	}
}

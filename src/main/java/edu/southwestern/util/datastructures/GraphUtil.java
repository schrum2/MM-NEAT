package edu.southwestern.util.datastructures;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	/**
	 * Save a graph of type that extends Grammar, will be saved as a DOT file
	 * @param graph Graph instance with type extending Grammar
	 * @param file File location as a string, including the .dot
	 * @throws IOException
	 */
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
	
	/**
	 * Take a graph of type grammar and make a dungeon out of it using BFS
	 * @param graph Graph to use
	 * @return Dungeon from the graph
	 * @throws Exception
	 */
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
		
		Queue<Graph<? extends Grammar>.Node> backlog = new LinkedList<>();
		
		List<Graph<? extends Grammar>.Node> visited = new ArrayList<>();
		Queue<Graph<? extends Grammar>.Node> queue = new LinkedList<>();
		queue.add(n);
		while(!queue.isEmpty()) {
			Graph<? extends Grammar>.Node node = queue.poll();
			Dungeon.Node dN = dungeon.getNode(node.getID());
			visited.add(node);
			Point p = getCoords(levelThere, node.getID());
			
			handleBacklog(levelThere, dungeon, backlog);
			if(p == null)
				throw new Exception("Node : " + node.getID() + " not found in level there");
			for(Graph<? extends Grammar>.Node adjNode : node.adjacencies()) {
				if(!visited.contains(adjNode) && !queue.contains(adjNode)) {
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
						backlog.add(adjNode);
						print2DArray(ZeldaLevelUtil.trimLevelThere(levelThere));
//						throw new Exception("Didn't get a legal point for node: " + adjNode.getID() + " from node : " + node.getID());
					}
				}
				print2DArray(ZeldaLevelUtil.trimLevelThere(levelThere));
				System.out.println();
			}

		}
		dungeon.setLevelThere(ZeldaLevelUtil.trimLevelThere(levelThere));
		return dungeon;
	}
	
	/**
	 * Backlog is where there are too many adjacencies for a node to add, which the additional adjacencies get added to the backlog.
	 * The backlog looks for the adjacencies and attempts to add to an adjacency if it's already there in the graph
	 * @param levelThere 2D representation of the level where each cell is the name of the level
	 * @param dungeon Dungeon instance
	 * @param backlog Queue of the adjancencies to take care of
	 * @throws Exception
	 */
	private static void handleBacklog(String[][] levelThere, Dungeon dungeon, 
			Queue<Graph<? extends Grammar>.Node> backlog) throws Exception {
		while(!backlog.isEmpty()) {
			Graph<? extends Grammar>.Node node = backlog.poll();
			for(Graph<? extends Grammar>.Node adjNode : node.adjacencies()) {
				Point p = getCoords(levelThere, adjNode.getID());
				if(p != null) {
					Point legal = getNextLegalPoint(p, levelThere);
					if(legal != null) {
						levelThere[legal.y][legal.x] = node.getID();
						Level newLevel = loadLevel(node, dungeon);
						Dungeon.Node newNode = dungeon.newNode(node.getID(), newLevel);
						Dungeon.Node dN = dungeon.getNode(adjNode.getID());
						int tile = (node.getData().getLevelType() == "l") ? Tile.LOCKED_DOOR.getNum() : Tile.DOOR.getNum();
						System.out.println(dN.name);
						System.out.println(p);
						System.out.println(legal);
						System.out.println(newNode.name);
						System.out.println(tile);
						
						setAdjacencies(dN, p, legal, newNode.name, tile);
						setAdjacencies(newNode, legal, p, dN.name, tile);
					}
				}
			}
		}
	}

	/**
	 * Print any 2D array, for debugging purposes
	 * @param array
	 */
	private static void print2DArray(String[][] array) {
		for(String[] row : array) {
			for(String s : row) {
				System.out.print(s +",");
			}
			System.out.println();
		}
	}

	/**
	 * Set the adjacencies, the exit and starting points
	 * @param fromNode Node where the ajancencie originates
	 * @param from exit Point
	 * @param to starting Point
	 * @param whereTo Name of the room the starting point is going to
	 * @param tile Tile to place the at exit point as a number
	 * @throws Exception
	 */
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
	
	
	/**
	 * Get the direction as a string based on the from and to point, must be next to each other
	 * @param from The origin point
	 * @param to The point to get the direction 
	 * @return Direction as a string
	 */
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

	/**
	 * Checks neighboring coordinates, randomly, based off of p
	 * @param p The origin as a Point
	 * @param levelThere 2D representation of the dungeon
	 * @return Point where the next level is going to be place
	 */
	private static Point getNextLegalPoint(Point p, String[][] levelThere) {
		int y = p.y;
		int x = p.x;
		List<Point> options = new LinkedList<>(Arrays.asList(new Point(x - 1, y), new Point(x + 1, y), new Point(x, y - 1), new Point(x, y - 1)));
		while(!options.isEmpty()) {
			Random r = new Random();
			Point opt = options.remove(r.nextInt(options.size()));
			x = opt.x;
			y = opt.y;
			
			if(x >= 0 && x < levelThere[0].length && y >= 0 && y < levelThere.length) {
				if(levelThere[y][x] == null) {
					System.out.println(levelThere[y][x]);
					return new Point(x, y);
				}
					
			}
		
		}
		return null;
	}

	/**
	 * Get the coordinates of a name
	 * @param levelThere 2D representation of a dungeon
	 * @param n name to check for
	 * @return Point of where the name is in the dungeon, null if it wasn't found
	 */
	private static Point getCoords(String[][] levelThere, String n) {
		for(int y = levelThere.length - 1; y >= 0; y--) {
			for(int x = 0; x < levelThere[0].length; x++) {
				if(levelThere[y][x] == n)
					return new Point(x, y);
			}
		}
		return null;
	}
	
	/**
	 * Load one empty level and populate based on tile type
	 * @param n Node to load for
	 * @param dungeon Dungeon to add to
	 * @return Modified level based off of n
	 * @throws FileNotFoundException
	 */
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

	/**
	 * Load a level based off of the file, assumes using the normal dungeon layout
	 * @param file File to load 
	 * @return Level representation of the file
	 * @throws FileNotFoundException
	 */
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

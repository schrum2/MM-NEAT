package edu.southwestern.tasks.gvgai.zelda.dungeon;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon.Level;
import edu.southwestern.tasks.gvgai.zelda.level.Grammar;
import edu.southwestern.tasks.gvgai.zelda.level.LevelLoader;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGrammar;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction;
import edu.southwestern.tasks.gvgai.zelda.study.HumanSubjectStudy2019Zelda;
import edu.southwestern.util.datastructures.Graph;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;
import me.jakerg.rougelike.Creature;
import me.jakerg.rougelike.CreatureFactory;
import me.jakerg.rougelike.Item;
import me.jakerg.rougelike.Log;
import me.jakerg.rougelike.Tile;
import me.jakerg.rougelike.TileUtil;
import me.jakerg.rougelike.World;

public class DungeonUtil {
	// The CPPN to GAN generation process does not make dungeons with a grammar, but using the A* check can cause problems because of this.
	// Basically, the way it tells if a room has a start location as a point of interest is by looking at the grammar description for the room.
	public static boolean NO_GRAMMAR_AT_ALL = false;
	
	public static void addCycles(Dungeon dungeon) throws Exception {
		String[][] levels = dungeon.getLevelThere();
		for(int y = 0; y < levels.length; y++) {
			for(int x = 0; x < levels[y].length; x++) {
				if(levels[y][x] != null) {		
					Stack<Point> options = new Stack<>();
					options.addAll(Arrays.asList(new Point(x - 1, y), new Point(x + 1, y), new Point(x, y - 1), new Point(x, y + 1)));
					Point p = DungeonUtil.pointToCheck(dungeon, x, y, options);
					while(p != null) {
						Dungeon.Node n = dungeon.getNodeAt(x, y);
						Dungeon.Node adj = dungeon.getNodeAt(p.x, p.y);
						DungeonUtil.setAdjacencies(n, new Point(x, y), p, adj.name, Tile.DOOR.getNum());
						DungeonUtil.setAdjacencies(adj, p, new Point(x, y), n.name, Tile.DOOR.getNum());
						p = DungeonUtil.pointToCheck(dungeon, x, y, options);
					}
				}
				
			}
		}
	}

	public static void addExitPoints(List<Point> points, List<List<Integer>> intLevel) {
		Point[] doors = new Point[] {new Point(8, 1), new Point(7, 9), new Point(1, 5), new Point(14, 5)};
		Point[] dirs = new Point[] {new Point(0, 1), new Point(0, -1), new Point(1, 0), new Point(-1, 0)};
		
		for(int i = 0; i < dirs.length; i++) {
			Point p = new Point();
			p.x = doors[i].x + dirs[i].x;
			p.y = doors[i].y + dirs[i].y;
			dirs[i] = p;
		}
		
		for(int i = 0; i < doors.length; i++) {
			Point p = doors[i];
			Tile t = Tile.findNum(intLevel.get(p.y).get(p.x));
			if(t.isDoor()) {
				Point dir = dirs[i];
//				intLevel.get(dir.y).set(dir.x, Tile.FLOOR.getNum());
				points.add(dir);
			}
			
		}
	}

	/**
	 * Get the items of interest in the level
	 * @param points
	 * @param intLevel
	 */
	public static void addInterestPoints(List<Point> points, List<List<Integer>> intLevel) {
		for(int y = 0; y < intLevel.size(); y++) {
			for(int x = 0; x < intLevel.get(y).size(); x++) {
				Tile t = Tile.findNum(intLevel.get(y).get(x));
				if(t == null) continue;
				if(t.isInterest()) {
					points.add(new Point(x, y));
//					System.out.println("Added to interests : " + t);
				}
				if(t.isMovable()) {
					int newX = x + t.getDirection().getPoint().x * 2;
					int newY = y + t.getDirection().getPoint().y * 2;
					points.add(new Point(newX, newY));
				}
				
				if(intLevel.get(y).get(x) == -6)
					points.add(new Point(x, y));
					
			}
		}
	}

	/**
	 * Get the tile to place the door as representated as an int based on the Grammar label
	 * @param node Node of where the doors need to be placed
	 * @return Number representing the tile
	 */
	private static <T extends Grammar> int getTile(Graph<T>.Node node) {
		return getTile(node.getData());
		
	}
	
	private static <T extends Grammar> int getTile(T grammar) {
		String type = grammar.getLevelType();
		switch(type) {
		case "l":
			return Tile.LOCKED_DOOR.getNum();
		case "b":
//			System.out.println("Placing hidden wall");
			return Tile.HIDDEN.getNum();
		case "sl":
			return Tile.SOFT_LOCK_DOOR.getNum();
		case "p":
			return Tile.PUZZLE_LOCKED.getNum();	
		default:
			if(RandomNumbers.randomCoin(0.4) && !grammar.equals(ZeldaGrammar.START))
				return Tile.HIDDEN.getNum();
			
			return Tile.DOOR.getNum();
		}
	}

	/**
	 * Backlog is where there are too many adjacencies for a node to add, which the additional adjacencies get added to the backlog.
	 * The backlog looks for the adjacencies and attempts to add to an adjacency if it's already there in the graph
	 * @param levelThere 2D representation of the level where each cell is the name of the level
	 * @param dungeon Dungeon instance
	 * @param backlog Queue of the adjancencies to take care of
	 * @param visited 
	 * @throws Exception
	 */
	public static void handleBacklog(String[][] levelThere, Dungeon dungeon, 
			Queue<Graph<? extends Grammar>.Node> backlog, List<Graph<? extends Grammar>.Node> visited, LevelLoader loader) throws Exception {
		while(!backlog.isEmpty()) {
			Graph<? extends Grammar>.Node node = backlog.poll();
			for(Graph<? extends Grammar>.Node adjNode : node.adjacencies()) {
				Point p = getCoords(levelThere, adjNode.getID());
				if(p != null) {
					Point legal = getNextLegalPoint(p, levelThere);
					if(legal != null) {
//						System.out.println("Placing from backlog: " + node.getID() + " at (" + legal.x + ", " + legal.y + ")");
						levelThere[legal.y][legal.x] = node.getID();
						int tile = getTile(node);
						Level newLevel = loadLevel(node, dungeon, loader, Tile.findNum(tile));
						Dungeon.Node newNode = dungeon.newNode(node.getID(), newLevel);
						newNode.grammar = (ZeldaGrammar) node.getData();
						Dungeon.Node dN = dungeon.getNode(adjNode.getID());
						
						DungeonUtil.setAdjacencies(dN, p, legal, newNode.name, tile);
						DungeonUtil.setAdjacencies(newNode, legal, p, dN.name, tile);
					}
				}
			}
			visited.add(node);
		}
	}

	/**
	 * Print any 2D array, for debugging purposes
	 * @param array
	 */
	public static void print2DArray(String[][] array) {
		for(String[] row : array) {
			for(String s : row) {
				System.out.print(s +",");
			}
			System.out.println();
		}
	}

	/**
	 * Get the direction as a string based on the from and to point, must be next to each other
	 * @param from The origin point
	 * @param to The point to get the direction 
	 * @return Direction as a string
	 */
	public static String getDirection(Point from, Point to) {
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
		List<Point> options = new LinkedList<>(Arrays.asList(new Point(x - 1, y), new Point(x + 1, y), new Point(x, y - 1), new Point(x, y + 1)));
		while(!options.isEmpty()) {
			Point opt = options.remove(RandomNumbers.randomGenerator.nextInt(options.size()));
			x = opt.x;
			y = opt.y;
			
			if(x >= 0 && x < levelThere[0].length && y >= 0 && y < levelThere.length) {
				if(levelThere[y][x] == null) {
//					System.out.println(levelThere[y][x]);
					return new Point(x, y);
				}
					
			}
		
		}
		return null;
	}
	
	// THIS IS NEVER USED. WHAT IS IT FOR?
//	private static int getAvailableSpace(Point p, String[][] levelThere) {
//		int space = 0;
//		int y = p.y;
//		int x = p.x;
//		List<Point> options = new LinkedList<>(Arrays.asList(new Point(x - 1, y), new Point(x + 1, y), new Point(x, y - 1), new Point(x, y + 1)));
//		for(Point option : options) {
//			if(x >= 0 && x < levelThere[0].length && y >= 0 && y < levelThere.length) {
//				if(levelThere[option.y][option.x] == null) {
//					space++;
//				}
//					
//			}
//		}
//		return space;
//	}

	/**
	 * Get the coordinates of a name
	 * @param levelThere 2D representation of a dungeon
	 * @param n name to check for
	 * @return Point of where the name is in the dungeon, null if it wasn't found
	 */
	public static Point getCoords(String[][] levelThere, String n) {
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
	private static Level loadLevel(Graph<? extends Grammar>.Node n, Dungeon dungeon, LevelLoader loader, Tile tile) throws FileNotFoundException {
		Level level = loadOneLevel(loader);
		switch(n.getData().getLevelType()) {
		case "n":
		case "l":
			break;
		case "k":
//			System.out.println("Putting key for: " + n.getID());
			ZeldaLevelUtil.placeRandomKey(level.intLevel);
			break;
		case "e":
			if(tile == null || (tile != null && !tile.equals(Tile.SOFT_LOCK_DOOR)))
				ZeldaLevelUtil.addRandomEnemy(level.intLevel);
			break;
		case "t":
			level = level.placeTriforce(dungeon);
			dungeon.setGoal(n.getID());
			break;
		case "s":
			dungeon.setCurrentLevel(n.getID());
			break;
		case "p":
			ZeldaLevelUtil.addRandomEnemy(level.intLevel);
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
	public static Level loadOneLevel(LevelLoader loader) throws FileNotFoundException {
		List<List<List<Integer>>> levels = loader.getLevels();
		List<List<Integer>> randomLevel = levels.get(RandomNumbers.randomGenerator.nextInt(levels.size()));
		randomLevel = remove(randomLevel);
	
		return new Level(randomLevel);
	}

	private static List<List<Integer>> remove(List<List<Integer>> randomLevel) {
		return ZeldaLevelUtil.copyList(randomLevel);
	}

	/**
	 * Get the unexplored rooms of the dungeon
	 * @param visited Visited set of ZeldaStates
	 * @return HashMap of nodes to list of points
	 */
	public static ZeldaState makePlayable(HashSet<ZeldaState> visited) {
		HashMap<Dungeon.Node, List<Point>> nodes = new HashMap<>();
		
		// Initialize hashmap
		for(ZeldaState state : visited) {
			Dungeon.Node n = state.currentNode;
			if(!nodes.containsKey(n))
				nodes.put(n, n.level.getFloorTiles());
		}
		
		// Remove visited spots from hashmap
		for(ZeldaState state : visited) {
			Dungeon.Node n = state.currentNode;
			
			Point p = new Point(state.x, state.y);
			if(!nodes.get(n).isEmpty() && nodes.get(n).remove(p));
		}
		
		for(ZeldaState state : visited) {
			Dungeon.Node n = state.currentNode;
			
			Point p = cleanUpRoom(n, nodes.get(n));
			if(p != null)
				return new ZeldaState(state, p);
		}
		throw new IllegalArgumentException("Somehow it was impossible to make this dungeon beatable given these visited states: " + visited);
	}

	/**
	 * Check the exit points of the level, see if there's a door, and see if the agent has been there
	 * @param n Node of the level to check
	 * @param list List of points of where the player has not been
	 * @return
	 */
	private static Point cleanUpRoom(Dungeon.Node n, List<Point> list) {
//		System.out.println("Unvisited points: ");
//		for(Point p : list) {
//			System.out.println("\t" + p);
//		}
		// TODO : One of the points of interest should be one that's been visited
		List<Point> interest = getPointsOfInterest(n);
		List<Point> unvisitedI = new LinkedList<>();
		for(Point unvisited : list) {
			if(interest.contains(unvisited)) {
				unvisitedI.add(unvisited);
				interest.remove(unvisited);
			}
		}
//		System.out.println(n.name + " unvisited intersts: ");
//		for(Point p : unvisitedI) {
//			System.out.println("\t" + p);
//		}
//		System.out.println(n.name + " visited intersts: ");
//		for(Point p : interest) {
//			System.out.println("\t" + p);
//		}
		
		Point a = null, b = null;
		
		Point resumePoint = null;
		
		if(unvisitedI.size() == 0)
			return null;
		else {
			if(interest.size() == 0 && unvisitedI.size() >= 2) {
				a = unvisitedI.remove(RandomNumbers.randomGenerator.nextInt(unvisitedI.size()));
				b = unvisitedI.remove(RandomNumbers.randomGenerator.nextInt(unvisitedI.size()));
			} else {
				a = unvisitedI.remove(RandomNumbers.randomGenerator.nextInt(unvisitedI.size()));
				b = interest.remove(RandomNumbers.randomGenerator.nextInt(interest.size()));
				resumePoint = b;
			}
		}
		
		List<Point> pointsToFloor = bresenham(a, b);
//		System.out.println("Applying floors to : " + n.name);
		for(Point p : pointsToFloor) {
//			System.out.println("\t" + p);
			Tile t = Tile.findNum(n.level.intLevel.get(p.y).get(p.x));
			if(t != null && !t.isInterest() && !t.equals(Tile.FLOOR) && !t.isMovable()) {
				if(resumePoint == null) resumePoint = p;
				n.level.intLevel.get(p.y).set(p.x, Tile.FLOOR.getNum());
			}
		}
		if(resumePoint == null) {
			throw new IllegalStateException("Were there no points on the line? " + pointsToFloor + "\nOne should be converted to a floor tile.");
		}
		return resumePoint;
	}

	public static List<Point> bresenhamLow(Point a, Point b){
		List<Point> pointsToFloor = new LinkedList<>();
		int dx = b.x - a.x;
		int dy = b.y - a.y;
		int yi = 1;
		if (dy < 0){
			yi = -1;
			dy = -dy;
		}
		int D = 2 * dy - dx;
		int y = a.y;
		
		for(int x = a.x; x < b.x; x++) {
			pointsToFloor.add(new Point(x, y));
			if (D > 0) {
				y += yi;
				D -= 2 * dx;
				pointsToFloor.add(new Point(x, y));
			}
			D += 2 * dy;
		}
		return pointsToFloor;
	}

	private static List<Point> bresenhamHigh(Point a, Point b){
		List<Point> pointsToFloor = new LinkedList<>();
		int dx = b.x - a.x;
		int dy = b.y - a.y;
		int xi = 1;
		if (dx < 0){
			xi = -1;
			dx = -dx;
		}
		int D = 2 * dx - dy;
		int x = a.x;
		
		for(int y = a.y; y < b.y; y++) {
			pointsToFloor.add(new Point(x, y));
			if (D > 0) {
				x += xi;
				D -= 2 * dy;
				pointsToFloor.add(new Point(x, y));
			}
			D += 2 * dx;
		}
		return pointsToFloor;
	}

	private static List<Point> bresenham(Point a, Point b){
		if (Math.abs(b.y - a.y) < Math.abs(b.x - a.x)) {
			if(a.x > b.x)
				return bresenhamLow(b, a);
			else
				return bresenhamLow(a, b);
		} else {
			if(a.y > b.y)
				return bresenhamHigh(b, a);
			else
				return bresenhamHigh(a, b);
		}
	}

	private static List<Point> getPointsOfInterest(Dungeon.Node n) {
		List<List<Integer>> intLevel = n.level.intLevel;
		List<Point> points = new LinkedList<>();
		addExitPoints(points, intLevel);
		addInterestPoints(points, intLevel);
		
		if(!NO_GRAMMAR_AT_ALL && n.grammar.equals(ZeldaGrammar.START))
			points.add(new Point(5, 5));

		return points;
	}

	/**
	 * Set the tile to visited if the agent has visited the tile
	 * @param visited Visited states
	 */
	// THIS IS NEVER USED. WHAT IS IT FOR?
//	private static void setFloorTiles(HashSet<ZeldaState> visited) {
//		for(ZeldaState state : visited) {
//			Tile t = Tile.findNum(state.currentNode.level.intLevel.get(state.y).get(state.x));
//			if(t != null && t.equals(Tile.FLOOR))
//				state.currentNode.level.intLevel.get(state.y).set(state.x, Tile.VISITED.getNum());
//		}
//		
//	}

	/**
	 * Generate a world from the rouge-like and draw the terminal panel
	 * @param panel AsciiPanel to draw to
	 * @param node Individual level
	 * @param dungeon Dungeon
	 */
	public static void drawToPanel(AsciiPanel panel, Dungeon.Node node, Dungeon dungeon) {
		World world = null;
		Log log = new Log(0);
		CreatureFactory cf = new CreatureFactory(world, log);
		Creature p = cf.newDungeonPlayer(dungeon);
		world = TileUtil.makeWorld(node.level.intLevel, p, log);
		world.forceKey();
		boolean isStart = dungeon.getCurrentlevel().equals(node);
		if(isStart) {
			p.x = 5;
			p.y = 5;
		}
		for (int y = 0; y < world.getHeight(); y++){
	        for (int x = 0; x < world.getWidth(); x++){
	        	
	        	// If there's a creature at that position display it
	        	Creature c = world.creature(x, y);
	        	Item i = world.item(x, y);
	        	if (c != null && (!c.isPlayer() || isStart) )
	        		panel.write(c.glyph(), c.x, c.y, c.color());
	        	else if(i != null)
	        		panel.write(i.glyph(), i.x, i.y, i.color());
	        	else
	        		panel.write(world.glyph(x, y), x, y, world.color(x, y));
	        }
	    }
	}

	/**
	 * Take a graph of type grammar and make a dungeon out of it using BFS
	 * @param graph Graph to use
	 * @return Dungeon from the graph
	 * @throws Exception
	 */
	public static Dungeon convertToDungeon(Graph<? extends Grammar> graph, LevelLoader loader) throws Exception {
		Dungeon dungeon = new Dungeon();
		String[][] levelThere = new String[100][100];
		int x = (levelThere.length - 1) / 2;
		int y = (levelThere.length - 1) / 2;

		Graph<? extends Grammar>.Node n = graph.root();
		
		Level l = loadLevel(n, dungeon, loader, null);
		Dungeon.Node dNode = dungeon.newNode(n.getID(), l);
		dNode.grammar = (ZeldaGrammar) n.getData();
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
			graph.addNode((Graph.Node) node);
			Point p = getCoords(levelThere, node.getID());
			
			handleBacklog(levelThere, dungeon, backlog, visited, loader);
			if(p == null)
				throw new Exception("Node : " + node.getID() + " not found in level there");
			
			List<Graph<? extends Grammar>.Node> adjs = new LinkedList<>(node.adjacencies());
			
			while(!adjs.isEmpty()) {
				Graph<? extends Grammar>.Node adjNode = adjs.remove(RandomNumbers.randomGenerator.nextInt(adjs.size()));
				
				if(!visited.contains(adjNode) && !queue.contains(adjNode)) {
					Point legal = getNextLegalPoint(p, levelThere);
					if(legal != null) {
						System.out.println("Placing " + adjNode.getID() + " at (" + legal.x + ", " + legal.y + ") " + adjNode.getData().getLabelName());
						levelThere[legal.y][legal.x] = adjNode.getID();
						int tile = getTile(node);
						Level newLevel = loadLevel(adjNode, dungeon, loader, Tile.findNum(tile));
						Dungeon.Node newNode = dungeon.newNode(adjNode.getID(), newLevel);
						newNode.grammar = (ZeldaGrammar) adjNode.getData();
						DungeonUtil.setAdjacencies(dN, p, legal, newNode.name, tile);
						DungeonUtil.setAdjacencies(newNode, legal, p, dN.name, tile);
						queue.add(adjNode);
					} else {
						print2DArray(ZeldaLevelUtil.trimLevelThere(levelThere));
						throw new Exception("Didn't get a legal point for node: " + adjNode.getID() + " from node : " + node.getID());
					}
				} else if (visited.contains(adjNode) && node.getData().isCyclable()
						&& adjNode.getData().isCyclable()) {
					Dungeon.Node newNode = dungeon.getNode(adjNode.getID());
					int tile = Tile.DOOR.getNum();
					Point to = getCoords(levelThere, adjNode.getID());
					DungeonUtil.setAdjacencies(dN, p, to, newNode.name, tile);
					DungeonUtil.setAdjacencies(newNode, to, p, dN.name, tile);
				}
//				print2DArray(ZeldaLevelUtil.trimLevelThere(levelThere));
//				System.out.println();
			}

		}
		dungeon.setLevelThere(ZeldaLevelUtil.trimLevelThere(levelThere));
		addCycles(dungeon);
		return dungeon;
	}
	
	public static <T extends Grammar> Dungeon recursiveGenerateDungeon(Graph<T> graph, LevelLoader loader) throws Exception {
		try {
			FileUtils.forceDelete(new File("data/VGLC/Zelda/Dungeons"));
			FileUtils.forceMkdir(new File("data/VGLC/Zelda/Dungeons"));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			FileUtils.forceMkdir(new File("data/VGLC/Zelda/Dungeons"));
			e2.printStackTrace();
		}
		
		Dungeon dungeon = new Dungeon();
		Deque<Pair<Graph<T>.Node, Graph<T>.Node>> pending = getGraphNodes(graph);
		Stack<Graph<T>.Node> placed = new Stack<>();
		HashMap<String, Point> locations = new HashMap<>();
		
		String[][] levelThere = new String[100][100];
		
		// catch boolean for error check
		recursiveGenerateDungeon(graph, loader, dungeon, pending, placed, levelThere, locations, 0);
		dungeon.setLevelThere(ZeldaLevelUtil.trimLevelThere(levelThere));
//		addCycles(dungeon);
		return dungeon;
	}
	
	
	private static <T extends Grammar> boolean recursiveGenerateDungeon(Graph<T> graph, LevelLoader loader, Dungeon dungeon,
			Deque<Pair<Graph<T>.Node, Graph<T>.Node>> pending, Stack<Graph<T>.Node> placed, String[][] levelThere,
			HashMap<String, Point> locations, int times) throws Exception {
		
		if(pending.isEmpty()) return true;
		
		Pair<Graph<T>.Node, Graph<T>.Node> pair = pending.pop();
//		System.out.println(pending);
		Graph<T>.Node next = pair.t1;
		if(HumanSubjectStudy2019Zelda.DEBUG)
			System.out.println("Got " + next.getID() + " from list (" + next + ")");
		Graph<T>.Node parent = pair.t2;
		Point location = null;
		if(parent == null)
			location = new Point(levelThere.length / 2, levelThere[0].length / 2);
		else
			location = locations.get(parent.getID());
		
		placed.push(next);
		int x = location.x;
		int y = location.y;
		List<Point> options = new LinkedList<>(Arrays.asList(new Point(x - 1, y), new Point(x + 1, y), new Point(x, y - 1), new Point(x, y + 1)));
		Collections.shuffle(options, RandomNumbers.randomGenerator);
		for(Point p : options) {
			if (levelThere[p.y][p.x] == null) {
				levelThere[p.y][p.x] = next.getID();
				
				Level l = loadLevel(next, dungeon, loader, (parent != null) ? Tile.findNum(getTile(parent)) : null);
				Dungeon.Node dNode = dungeon.newNode(next.getID(), l);
				dNode.grammar = (ZeldaGrammar) next.getData();
				
				if(parent != null) {
					int tile = getTile(parent);
					Dungeon.Node parentDN = dungeon.getNode(parent.getID());
					DungeonUtil.setAdjacencies(parentDN, location, p, dNode.name, tile);
					DungeonUtil.setAdjacencies(dNode, p, location, parentDN.name, tile);
				}

				locations.put(next.getID(), p);
				
//				BufferedImage image = imageOfDungeon(dungeon);
//				File file = new File("data/VGLC/Zelda/Dungeons/dungeon_" + times + ".png");
//				ImageIO.write(image, "png", file);
				
				boolean success = recursiveGenerateDungeon(graph, loader, dungeon, pending, placed, levelThere, locations, ++times);
				if(success)
					return true;
				else {
					locations.remove(next.getID());
					dungeon.removeNode(next.getID());
					if(parent != null) {
						Dungeon.Node parentDN = dungeon.getNode(parent.getID());
						DungeonUtil.setAdjacencies(parentDN, location, p, dNode.name, Tile.WALL.getNum());
					}
					levelThere[p.y][p.x] = null;
				}
			}
		}
		
		 placed.pop();
		 pending.addFirst(pair);
		 return false;
		
	}

	private static <T extends Grammar> Deque<Pair<Graph<T>.Node, Graph<T>.Node>> getGraphNodes(Graph<T> graph) {
		Deque<Pair<Graph<T>.Node, Graph<T>.Node>> deque = new LinkedList<>();
		List<String> visited = new LinkedList<>();
		Queue<Graph<T>.Node> queue = new LinkedList<>();
		queue.add(graph.root());
		visited.add(graph.root().getID());
		deque.add(new Pair<Graph<T>.Node, Graph<T>.Node>(graph.root(), null));
		while(!queue.isEmpty()) {
			Graph<T>.Node node = queue.poll();
			for(Graph<T>.Node v : node.adjacencies()) {
				if(!visited.contains(v.getID())) {
					visited.add(v.getID());
					queue.add(v);
					deque.add(new Pair<Graph<T>.Node, Graph<T>.Node>(v, node));
				}
			}
		}
		return deque;
		
	}

	public static Point pointToCheck(Dungeon dungeon, int x, int y, Stack<Point> options) {
		Dungeon.Node n = dungeon.getNodeAt(x, y);
		if(n.hasLock()) return null;
		while(options.size() > 0) {
			Point check = options.pop();
			int cX = check.x;
			int cY = check.y;
			boolean hasAdj = false;
			Dungeon.Node cN = dungeon.getNodeAt(cX, cY);
			if(cN == null)
				continue;
			
			for(Pair<String, Point> values : n.adjacency.values())
				if(values.t1 == cN.name)
					hasAdj = true;
			
//			System.out.println("hasAdj " + hasAdj);
			if(!hasAdj && !cN.hasLock() && cN.grammar.isCyclable()) {
//				System.out.println("Returning point : " + new Point(cX, cY));
				return new Point(cX, cY);
			}
	
			
		}
		
		return null;
	}

	/**
	 * Generate a buffered image of a dungeon as a rouge-like
	 * @param dungeon Dungeon to generate an image with
	 * @param visited 
	 * @return BufferedImage representing dungeon
	 */
	public static BufferedImage imageOfDungeon(Dungeon dungeon, HashSet<ZeldaState> visited) {
		boolean debug = false;
		
		int BLOCK_HEIGHT = dungeon.getCurrentlevel().level.intLevel.size() * 16;
		int BLOCK_WIDTH = dungeon.getCurrentlevel().level.intLevel.get(0).size() * 16;
		String[][] levelThere = dungeon.getLevelThere();
		int width = levelThere[0].length * BLOCK_WIDTH;
		int height = levelThere.length * BLOCK_HEIGHT;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		
		g2d.setRenderingHint(
			    RenderingHints.KEY_ANTIALIASING,
			    RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(
			    RenderingHints.KEY_TEXT_ANTIALIASING,
			    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		image = g2d.getDeviceConfiguration().createCompatibleImage(width, height);
		Graphics g = image.getGraphics();
		
		Font f = new Font("Trebuchet MS", Font.PLAIN, BLOCK_HEIGHT / 4);
		g.setFont(f);
		
		HashMap<Dungeon.Node, List<Point>> nodes = null;
		
		if(visited != null)
			setUnvisited(visited);
	
		for(int y = 0; y < levelThere.length; y++) {
			for(int x = 0; x < levelThere[y].length; x++) {
				Dungeon.Node n = dungeon.getNodeAt(x, y);
				int oX = x * BLOCK_WIDTH;
				int oY = y * BLOCK_HEIGHT;
				if(n != null) {
					
					BufferedImage bi = getLevelImage(n, dungeon);
					g.setColor(Color.GRAY);
					g.fillRect(oX, oY, oX + BLOCK_WIDTH, oY + BLOCK_HEIGHT);
					g.drawImage(bi, oX, oY, null);
					if(debug) {
						g.setColor(Color.WHITE);
						oX = (oX + BLOCK_WIDTH) - (BLOCK_WIDTH / 2) - (BLOCK_WIDTH / 4);
						oY = (oY + BLOCK_HEIGHT) - (BLOCK_HEIGHT / 2) + (BLOCK_HEIGHT / 4);
						if(n.grammar != null)
							g.drawString(n.grammar.getLevelType(), oX, oY);
						
						if(nodes != null && nodes.containsKey(n))
							g.setColor(Color.RED);
						
						oX = (oX) + (BLOCK_WIDTH / 4);
						g.drawString(n.name, oX, oY);
					}
				} else {
					g.setColor(Color.BLACK);
					g.fillRect(oX, oY, oX + BLOCK_WIDTH, oY + BLOCK_HEIGHT);
				}
			}
		}
		
		g.dispose();
		g2d.dispose();
		
		return image;
	}

	private static void setUnvisited(HashSet<ZeldaState> visited) {
		for(ZeldaState state : visited) {
			Tile t = Tile.findNum(state.currentNode.level.intLevel.get(state.y).get(state.x));
			if(t.equals(Tile.FLOOR))
				state.currentNode.level.intLevel.get(state.y).set(state.x, Tile.VISITED.getNum());
		}
		
	}

	/**
	 * Use A* agent to to see if it's playable, if it's not playable change layout of room. Do this over and over
	 * until dungeon is playable
	 * 
	 * Dr. Schrum, look at this function
	 * @param dungeon Generated dungeon
	 */
	public static void makeDungeonPlayable(Dungeon dungeon) {
		Search<GridAction,ZeldaState> search = new AStarSearch<>(ZeldaLevelUtil.manhattan);
		ZeldaState state = new ZeldaState(5, 5, 0, dungeon);
		boolean reset = true;
		while(true) {			
			ArrayList<GridAction> result = ((AStarSearch<GridAction, ZeldaState>) search).search(state, reset);
			// Would prefer not to start from scratch when resuming the search after a fix, but currently
			// we get an infinite loop if this is changed to false.
			// Leaving it to false occasionally leads to errors
			reset = true; 
			HashSet<ZeldaState> visited = ((AStarSearch<GridAction, ZeldaState>) search).getVisited();
//			setUnvisited(visited);
			if(HumanSubjectStudy2019Zelda.DEBUG)
				System.out.println(result);
			if(result == null) {
				// Warning: visited tiles will be replaced with X (Could affect keys)
//				setUnvisited(visited);
//				viewDungeon(dungeon, visited);
				//viewDungeon(dungeon, new HashSet<>());
				//MiscUtil.waitForReadStringAndEnterKeyPress();
				// Resume search from new state: but is this actually the state if should be?
				state = makePlayable(visited); 
//				state = new ZeldaState(5, 5, 0, dungeon);
				if(HumanSubjectStudy2019Zelda.DEBUG)
					System.out.println(state);
			}
			else break;
		}
	}

	public static void viewDungeon(Dungeon dungeon, HashSet<ZeldaState> visited) {
		BufferedImage image = imageOfDungeon(dungeon, visited);
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		JLabel label = new JLabel(new ImageIcon(image.getScaledInstance(image.getWidth() / 2, image.getHeight() / 2, Image.SCALE_FAST)));
		panel.add(label);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Get an individual level image from a dungeon
	 * @param node Dungeon node as the level
	 * @param dungeon Dungeon where the level is from
	 * @return Image of level
	 */
	public static BufferedImage getLevelImage(Dungeon.Node node, Dungeon dungeon) {
		int lHeight = node.level.intLevel.size();
		int lWidth = node.level.intLevel.get(0).size();
		
		AsciiPanel panel = new AsciiPanel(lWidth, lHeight, AsciiFont.CP437_16x16);
		
		drawToPanel(panel, node, dungeon);
		
		int w = panel.getCharWidth() * lWidth;
		int h = panel.getCharHeight() * lHeight;
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		panel.paint(g);
		g.dispose();
		return image;
		
	}

	/**
	 * Set the adjacencies, the exit and starting points
	 * @param fromNode Node where the ajancency originates
	 * @param from exit Point
	 * @param to starting Point
	 * @param whereTo Name of the room the starting point is going to
	 * @param tile Tile to place the at exit point as a number
	 * @throws Exception
	 */
	public static void setAdjacencies(Dungeon.Node fromNode, Point from,
			Point to, String whereTo, int tile) throws Exception {
		String direction = getDirection(from, to);
//		System.out.println("From node " + fromNode.name + " going " + direction + " to " + whereTo);s
		if(direction == null) return;
		if(!Tile.findNum(tile).equals(Tile.WALL)) {
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
		}

		ZeldaLevelUtil.setDoors(direction, fromNode, tile);
	}

	public static BufferedImage imageOfDungeon(Dungeon dungeon) {
		return imageOfDungeon(dungeon, null);
	}

	public static void viewDungeon(Dungeon d) {
		DungeonUtil.viewDungeon(d, new HashSet<>());
	}

	/**
	 * Convert a grid of rooms in the List of Lists of Integers format produced by json into
	 * a grid of Levels, which correspond to individual Zelda rooms.
	 * @param grid 2D array of List representations of rooms in dungeon
	 * @return 2D array of Level representations of rooms in dungeon
	 */
	public static Level[][] roomGridFromJsonGrid(List<List<Integer>>[][] grid) {
		Level[][] levelGrid = new Level[grid.length][grid[0].length];
		for(int y = 0; y < grid.length; y++) {
			for(int x = 0; x < grid[0].length; x++) {
				levelGrid[y][x] = grid[y][x] == null ? null : new Level(grid[y][x]);
			}
		}
		return levelGrid;
	}
}

package edu.southwestern.util.datastructures;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon.Level;
import edu.southwestern.tasks.gvgai.zelda.level.Grammar;
import edu.southwestern.tasks.gvgai.zelda.level.LevelLoader;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGrammar;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState;
import edu.southwestern.util.random.RandomNumbers;
import me.jakerg.rougelike.Creature;
import me.jakerg.rougelike.CreatureFactory;
import me.jakerg.rougelike.Item;
import me.jakerg.rougelike.Log;
import me.jakerg.rougelike.Tile;
import me.jakerg.rougelike.TileUtil;
import me.jakerg.rougelike.World;

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
	public static Dungeon convertToDungeon(Graph<? extends Grammar> graph, LevelLoader loader) throws Exception {
		Dungeon dungeon = new Dungeon();
		String[][] levelThere = new String[100][100];
		int x = (levelThere.length - 1) / 2;
		int y = (levelThere.length - 1) / 2;

		Graph<? extends Grammar>.Node n = graph.root();
		
		Level l = loadLevel(n, dungeon, loader);
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
			dN.grammar = (ZeldaGrammar) node.getData();
			visited.add(node);
			graph.addNode((Graph.Node) node);
			Point p = getCoords(levelThere, node.getID());
			
			handleBacklog(levelThere, dungeon, backlog, visited, loader);
			if(p == null)
				throw new Exception("Node : " + node.getID() + " not found in level there");
			
			List<Graph<? extends Grammar>.Node> adjs = new LinkedList<>(node.adjacencies);
			
			while(!adjs.isEmpty()) {
				Graph<? extends Grammar>.Node adjNode = adjs.remove(RandomNumbers.randomGenerator.nextInt(adjs.size()));
				
				if(!visited.contains(adjNode) && !queue.contains(adjNode)) {
					Point legal = getNextLegalPoint(p, levelThere);
					if(legal != null) {
						System.out.println("Placing " + adjNode.getID() + " at (" + legal.x + ", " + legal.y + ") " + adjNode.getData().getLabelName());
						levelThere[legal.y][legal.x] = adjNode.getID();
						Level newLevel = loadLevel(adjNode, dungeon, loader);
						Dungeon.Node newNode = dungeon.newNode(adjNode.getID(), newLevel);
						int tile = getTile(node);
						setAdjacencies(dN, p, legal, newNode.name, tile);
						setAdjacencies(newNode, legal, p, dN.name, tile);
						queue.add(adjNode);
					} else {
//						backlog.add(adjNode);
						print2DArray(ZeldaLevelUtil.trimLevelThere(levelThere));
//						throw new Exception("Didn't get a legal point for node: " + adjNode.getID() + " from node : " + node.getID());
					}
				} else if (visited.contains(adjNode) && node.getData().isCyclable()
						&& adjNode.getData().isCyclable()) {
					Dungeon.Node newNode = dungeon.getNode(adjNode.getID());
					int tile = Tile.DOOR.getNum();
					Point to = getCoords(levelThere, adjNode.getID());
					setAdjacencies(dN, p, to, newNode.name, tile);
					setAdjacencies(newNode, to, p, dN.name, tile);
				}
				print2DArray(ZeldaLevelUtil.trimLevelThere(levelThere));
				System.out.println();
			}

		}
		dungeon.setLevelThere(ZeldaLevelUtil.trimLevelThere(levelThere));
		addCycles(dungeon);
		return dungeon;
	}
	
	private static void addCycles(Dungeon dungeon) throws Exception {
		String[][] levels = dungeon.getLevelThere();
		for(int y = 0; y < levels.length; y++) {
			for(int x = 0; x < levels[y].length; x++) {
				if(levels[y][x] != null) {		
					Stack<Point> options = new Stack<>();
					options.addAll(Arrays.asList(new Point(x - 1, y), new Point(x + 1, y), new Point(x, y - 1), new Point(x, y + 1)));
					Point p = pointToCheck(dungeon, x, y, options);
					while(p != null) {
						System.out.println("Checking point");
						Dungeon.Node n = dungeon.getNodeAt(x, y);
						Dungeon.Node adj = dungeon.getNodeAt(p.x, p.y);
						setAdjacencies(n, new Point(x, y), p, adj.name, Tile.DOOR.getNum());
						setAdjacencies(adj, p, new Point(x, y), n.name, Tile.DOOR.getNum());
						p = pointToCheck(dungeon, x, y, options);
					}
				}
			}
		}
	}

	private static Point pointToCheck(Dungeon dungeon, int x, int y, Stack<Point> options) {
		Dungeon.Node n = dungeon.getNodeAt(x, y);
		if(n.hasLock()) return null;
		while(options.size() > 0) {
			Point check = options.pop();
			int cX = x + check.x;
			int cY = y + check.y;
			boolean hasAdj = false;
			Dungeon.Node cN = dungeon.getNodeAt(cX, cY);
			if(cN == null)
				continue;
			
			for(Pair<String, Point> values : n.adjacency.values())
				if(values.t1 == cN.name)
					hasAdj = true;
			
			System.out.println("hasAdj " + hasAdj);
			if(!hasAdj && !cN.hasLock() && cN.grammar.isCyclable()) {
				System.out.println("Returning point : " + new Point(cX, cY));
				return new Point(cX, cY);
			}

			
		}
		
		return null;
	}

	private static int getTile(Graph<? extends Grammar>.Node node) {
		String type = node.getData().getLevelType();
		switch(type) {
		case "l":
			return Tile.LOCKED_DOOR.getNum();
		case "b":
			System.out.println("Placing hidden wall");
			return Tile.HIDDEN.getNum();
		case "sl":
			return Tile.SOFT_LOCK_DOOR.getNum();
		default:
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
	private static void handleBacklog(String[][] levelThere, Dungeon dungeon, 
			Queue<Graph<? extends Grammar>.Node> backlog, List<Graph<? extends Grammar>.Node> visited, LevelLoader loader) throws Exception {
		while(!backlog.isEmpty()) {
			Graph<? extends Grammar>.Node node = backlog.poll();
			for(Graph<? extends Grammar>.Node adjNode : node.adjacencies()) {
				Point p = getCoords(levelThere, adjNode.getID());
				if(p != null) {
					Point legal = getNextLegalPoint(p, levelThere);
					if(legal != null) {
						System.out.println("Placing from backlog: " + node.getID() + " at (" + legal.x + ", " + legal.y + ")");
						levelThere[legal.y][legal.x] = node.getID();
						Level newLevel = loadLevel(node, dungeon, loader);
						Dungeon.Node newNode = dungeon.newNode(node.getID(), newLevel);
						newNode.grammar = (ZeldaGrammar) node.getData();
						Dungeon.Node dN = dungeon.getNode(adjNode.getID());
						
						int tile = getTile(node);
						
						setAdjacencies(dN, p, legal, newNode.name, tile);
						setAdjacencies(newNode, legal, p, dN.name, tile);
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
		
		if(direction == null) return;

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
		List<Point> options = new LinkedList<>(Arrays.asList(new Point(x - 1, y), new Point(x + 1, y), new Point(x, y - 1), new Point(x, y + 1)));
		while(!options.isEmpty()) {
			Point opt = options.remove(RandomNumbers.randomGenerator.nextInt(options.size()));
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
	private static Level loadLevel(Graph<? extends Grammar>.Node n, Dungeon dungeon, LevelLoader loader) throws FileNotFoundException {
		Level level = loadOneLevel(loader);
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
	private static Level loadOneLevel(LevelLoader loader) throws FileNotFoundException {
		List<List<List<Integer>>> levels = loader.getLevels();
		List<List<Integer>> randomLevel = levels.get(RandomNumbers.randomGenerator.nextInt(levels.size()));
		randomLevel = remove(randomLevel);

		return new Level(randomLevel);
	}
	
	private static List<List<Integer>> remove(List<List<Integer>> levelInt) {
		List<List<Integer>> level = new LinkedList<>(levelInt);
		for(int y = 0; y < level.size(); y++) {
			for(int x = 0; x < level.get(y).size(); x++) {
				int num = level.get(y).get(x);
				Tile tile = Tile.findNum(num);
				if(tile.equals(Tile.DOOR) || tile.equals(Tile.LOCKED_DOOR))
					num = Tile.WALL.getNum();
				else if(tile.equals(Tile.TRIFORCE) || tile.equals(Tile.KEY) || num == 2)
					num = Tile.FLOOR.getNum();
				level.get(y).set(x, num);
					
			}
		}
		
		return level;
	}
	
	/**
	 * Generate a buffered image of a dungeon as a rouge-like
	 * @param dungeon Dungeon to generate an image with
	 * @return BufferedImage representing dungeon
	 */
	public static BufferedImage imageOfDungeon(Dungeon dungeon, HashSet<ZeldaState> visited) {
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
		
		if(visited != null) {
			setFloorTiles(visited);
			nodes = getUnexplored(visited);
		}

		for(int y = 0; y < levelThere.length; y++) {
			for(int x = 0; x < levelThere[y].length; x++) {
				Dungeon.Node n = dungeon.getNodeAt(x, y);
				System.out.println("(" + x + ", " + y + ")");
				int oX = x * BLOCK_WIDTH;
				int oY = y * BLOCK_HEIGHT;
				if(n != null) {
					
					BufferedImage bi = getLevelImage(n, dungeon);
					g.setColor(Color.GRAY);
					g.fillRect(oX, oY, oX + BLOCK_WIDTH, oY + BLOCK_HEIGHT);
					g.drawImage(bi, oX, oY, null);
					g.setColor(Color.WHITE);
					oX = (oX + BLOCK_WIDTH) - (BLOCK_WIDTH / 2) - (BLOCK_WIDTH / 4);
					oY = (oY + BLOCK_HEIGHT) - (BLOCK_HEIGHT / 2) + (BLOCK_HEIGHT / 4);
					g.drawString(n.grammar.getLevelType(), oX, oY);
					
					if(nodes != null && nodes.containsKey(n))
						g.setColor(Color.RED);
					
					oX = (oX) + (BLOCK_WIDTH / 4);
					g.drawString(n.name, oX, oY);
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
	
	/**
	 * Get the unexplored rooms of the dungeon
	 * @param visited Visited set of ZeldaStates
	 * @return HashMap of nodes to list of points
	 */
	private static HashMap<Dungeon.Node, List<Point>> getUnexplored(HashSet<ZeldaState> visited) {
		HashMap<Dungeon.Node, List<Point>> nodes = new HashMap<>();
		
		for(ZeldaState state : visited) {
			Dungeon.Node n = state.currentNode;
			if(!nodes.containsKey(n))
				nodes.put(n, n.level.getFloorTiles());
			
			Point p = new Point(state.x, state.y);
			nodes.get(n).remove(p);

			if(nodes.get(n).size() == 0)
				nodes.remove(n);
			
		}
		
		cleanRoom(nodes);
		
		for(Dungeon.Node n : nodes.keySet()) {
			System.out.println(n.name);
		}
		
		return nodes;
	}

	/**
	 * Go through rooms and see if the doors are visited, if not all of the doors are visited, remvoe them from the set
	 * @param nodes HashMap of nodes to list of points where the agent has not visited
	 */
	private static void cleanRoom(HashMap<Dungeon.Node, List<Point>> nodes) {
		for(Dungeon.Node n : nodes.keySet()) {
			cleanUpRoom(n, nodes.get(n));
		}
		
	}

	/**
	 * Check the exit points of the level, see if there's a door, and see if the agent has been there
	 * @param n Node of the level to check
	 * @param list List of points of where the player has not been
	 * @return
	 */
	private static void cleanUpRoom(Dungeon.Node n, List<Point> list) {
		List<Point> interest = getPointsOfInterest(n);
		List<Point> unvisitedI = new LinkedList<>();
		for(Point unvisited : list) {
			if(interest.contains(unvisited)) {
				unvisitedI.add(unvisited);
				interest.remove(unvisited);
			}
		}
		System.out.println(n.name + " unvisited intersts: ");
		for(Point p : unvisitedI) {
			System.out.println("\t" + p);
		}
		System.out.println(n.name + " visited intersts: ");
		for(Point p : interest) {
			System.out.println("\t" + p);
		}
		if(unvisitedI.size() == 0)
			return;
		Point uI = unvisitedI.get(RandomNumbers.randomGenerator.nextInt(unvisitedI.size()));
		Point iP = interest.get(RandomNumbers.randomGenerator.nextInt(interest.size()));
		List<Point> pointsToFloor = bresenham(uI, iP);
		System.out.println("Applying floors to : " + n.name);
		for(Point p : pointsToFloor) {
			System.out.println("\t" + p);
			n.level.intLevel.get(p.y).set(p.x, Tile.CURRENT.getNum());
		}
		
	}
	
	private static List<Point> bresenhamToList(Point a, Point b) {
		List<Point> pointsToFloor = new LinkedList<>();
		int mNew = 2 * (b.y - a.y);
		int slopeErr = mNew - (b.x - a.x);
		for(int x = a.x, y = a.y; x < b.x; x++) {
			pointsToFloor.add(new Point(x, y));
			slopeErr += mNew;
			if(slopeErr >= 0) {
				y++;
				slopeErr -= 2 * (b.x - a.x);
				pointsToFloor.add(new Point(x, y));
			}
		}
//		double dX = b.x - a.x;
//		double dY = b.y - a.y;
//		if(dX == 0) {
//			for(int y = a.y; y < b.y; y++) {
//				pointsToFloor.add(new Point(a.x, y));
//			}
//			return pointsToFloor;
//		}
//		double dError = Math.abs(dY / dX);
//		double err = 0.0;
//		int y = a.y;
//		for(int x = a.x; x < b.x; x++) {
//			pointsToFloor.add(new Point(x, y));
//			err += dError;
//			if (err >= 0.5) {
//				y += Math.signum(dError) * 1;
//				err -= 1;
//				pointsToFloor.add(new Point(x, y));
//			}
//		}
		return pointsToFloor;
	}
	
	private static List<Point> bresenhamLow(Point a, Point b){
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
		
		for(int y = a.y; y < b.x; y++) {
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
		System.out.println(n.name + " point of interest: ");
		for(Point p : points) {
			System.out.println("\t" + p);
		}
		return points;
	}

	/**
	 * Get the items of interest in the level
	 * @param points
	 * @param intLevel
	 */
	private static void addInterestPoints(List<Point> points, List<List<Integer>> intLevel) {
		for(int y = 0; y < intLevel.size(); y++) {
			for(int x = 0; x < intLevel.get(y).size(); x++) {
				Tile t = Tile.findNum(intLevel.get(y).get(x));
				if(t != null && t.isInterest()) {
					points.add(new Point(x, y));
					System.out.println("Added to interests : " + t);
				}
					
			}
		}
	}

	private static void addExitPoints(List<Point> points, List<List<Integer>> intLevel) {
		Point[] doors = new Point[] {new Point(8, 1), new Point(7, 9), new Point(1, 4), new Point(14, 6)};
		Point[] dirs = new Point[] {new Point(0, 1), new Point(0, -1), new Point(1, 0), new Point(0, 1)};
		
		for(int i = 0; i < dirs.length; i++) {
			Point p = new Point();
			p.x = doors[i].x + dirs[i].x;
			p.y = doors[i].y + dirs[i].y;
			dirs[i] = p;
		}
		
		for(int i = 0; i < doors.length; i++) {
			Point p = doors[i];
			Tile t = Tile.findNum(intLevel.get(p.y).get(p.x));
			if(t.isDoor())
				points.add(dirs[i]);
			
		}
	}

	/**
	 * Set the tile to visited if the agent has visited the tile
	 * @param visited Visited states
	 */
	private static void setFloorTiles(HashSet<ZeldaState> visited) {
		for(ZeldaState state : visited) {
			state.currentNode.level.intLevel.get(state.y).set(state.x, Tile.VISITED.getNum());
		}
		
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
	 * Generate a world from the rouge-like and draw the terminal panel
	 * @param panel AsciiPanel to draw to
	 * @param node Individual level
	 * @param dungeon Dungeon
	 */
	private static void drawToPanel(AsciiPanel panel, Dungeon.Node node, Dungeon dungeon) {
		World world = null;
		Log log = new Log(0);
		CreatureFactory cf = new CreatureFactory(world, log);
		Creature p = cf.newDungeonPlayer(dungeon);
		world = TileUtil.makeWorld(node.level.intLevel, p, log);
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
	
}

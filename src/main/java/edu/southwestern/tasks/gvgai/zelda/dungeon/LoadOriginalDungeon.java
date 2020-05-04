package edu.southwestern.tasks.gvgai.zelda.dungeon;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon.Level;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.Search;
import me.jakerg.rougelike.RougelikeApp;
import me.jakerg.rougelike.Tile;

public class LoadOriginalDungeon {
	
	public static final int ZELDA_ROOM_ROWS = 11; // This is actually the room height from the original game, since VGLC rotates rooms
	public static final int ZELDA_ROOM_COLUMNS = 16;
	private static final boolean ROUGE_DEBUG = false;
	private static HashMap<String, Stack<Pair<String, String>>> directional;
						  // Node name        direct, whereTo
	
	public static boolean RANDOM_KEY = false;
	
	// Some levels have additional parts that aren't included that have keys in those parts leading to doors that can't
	// be opened since you can't get a key for it. So keep track of the number of keys to balance it out later
	private static int numKeys = 0;
	private static int numDoors = 0;
	                      
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		Parameters.initializeParameterCollections(new String[] {"rougeEnemyHealth:2"});
		
		String title = "tloz3_1_flip";
		Dungeon dungeon = loadOriginalDungeon(title, false);
		
		dungeon.printLevelThere();
		if (false) {
			Point goalPoint = dungeon.getCoords(dungeon.getGoal());
			int gDX = goalPoint.x;
			int gDY = goalPoint.y;
			
			Point g = dungeon.getGoalPoint();
			int gX = g.x;
			int gY = g.y;
			
			Heuristic<GridAction,ZeldaState> manhattan = new Heuristic<GridAction,ZeldaState>() {

				@Override
				public double h(ZeldaState s) {
					int i = Math.abs(s.x - gX) + Math.abs(s.y - gY);
					int j = Math.abs(gDX - s.dX) * ZELDA_ROOM_COLUMNS + Math.abs(gDY - s.dY) * ZELDA_ROOM_ROWS;
					return i + j; 
				}
			};
			
			ZeldaState initial = new ZeldaState(5, 5, 0, dungeon);
			
			Search<GridAction,ZeldaState> search = new AStarSearch<>(manhattan);
			ArrayList<GridAction> result = search.search(initial);
				
			System.out.println(result);
			if(result != null) {
				for(GridAction a : result)
					System.out.println(a.getD().toString());
				
				System.out.println("Lenght of path : " + result.size());
			}
		}

		RougelikeApp.startDungeon(dungeon, ROUGE_DEBUG); // start game
	}
	
	/**
	 * Loads a dungeon given the name with random key placement
	 * @param name Name of dungeon (title of dir and dot file)
	 * @return Dungeon instance
	 * @throws Exception
	 */
	public static Dungeon loadOriginalDungeon(String name) {
		return loadOriginalDungeon(name, false);
	}
	
	public static Dungeon loadOriginalDungeon(String name, boolean randomKey) {
		RANDOM_KEY = randomKey;
		String graphFile = "data/VGLC/Zelda/Graph Processed/" + name + ".dot";
		String levelPath = "data/VGLC/Zelda/Processed/" + name;
		Dungeon dungeon = new Dungeon(); // Make new dungeon instance
		directional = new HashMap<>(); // Make directional hashmap
		HashMap<Integer, String> numberToString = new HashMap<>(); // Map the numbers to strings (node name)
		System.out.println("Loading .txt levels");
		loadLevels(dungeon, numberToString, levelPath); // Load the levels (txt files) to dungeon
		for(Entry<Integer, String> entry : numberToString.entrySet()) {
			System.out.println(entry.getKey() + " --- " + entry.getValue());
		}
		System.out.println("Loading levels from graph");
		loadGraph(dungeon, numberToString, graphFile); // Load the graph representation to dungeon
		System.out.println("Generating 2D map");
		dungeon.setLevelThere(generateLevelThere(dungeon, numberToString)); // Generate the 2D map of the dungeon
		System.out.println("Num Keys : " + numKeys + " | numDoors : " + numDoors / 2);
		numDoors /= 2;
//		balanceKeyToDoors(dungeon, numberToString);
		for(Entry<Integer, String> set : numberToString.entrySet()) {
			String n = set.getValue();
			System.out.println(set.getKey() + " -> " + n.substring(n.length() - 4, n.length()));
		}
		return dungeon;
	}

	/**
	 * If the number of locked doors (by half) exceed the number of keys, add keys to levels that initially don't have keys
	 * @param dungeon
	 * @param numberToString 
	 */
	// NEVER USED?
//	private static void balanceKeyToDoors(Dungeon dungeon, HashMap<Integer, String> numberToString) {
//		while(numKeys < numDoors) {
//			int i = RandomNumbers.randomGenerator.nextInt(numberToString.size() - 1);
//			Node currentNode = dungeon.getNode(numberToString.get(i));
//			if(currentNode != null && !haveKey(currentNode)) {
//				if(RANDOM_KEY)
//					ZeldaLevelUtil.placeRandomKey(currentNode.level.intLevel);
//				else
//					ZeldaDungeon.placeNormalKey(currentNode.level.intLevel);
//				numKeys++;
//				System.out.println("Added key! Now has : " + numKeys + " keys");
//			}
//		}
//	}

	// NEVER USER?
//	private static boolean haveKey(Node currentNode) {
//		if(currentNode == null) return false;
//		List<List<Integer>> level = currentNode.level.intLevel;
//		for(List<Integer> row : level)
//			for(Integer cell : row)
//				if(cell == Tile.KEY.getNum() || cell == Tile.TRIFORCE.getNum())
//					return true;
//		return false;
//	}

	/**
	 * Starting function to recursively generate the 2D map
	 * @param dungeon Dungeon instance
	 * @param numberToString Number to string representation
	 * @return 2D String array of where the levels are
	 * @throws Exception 
	 */
	private static String[][] generateLevelThere(Dungeon dungeon, HashMap<Integer, String> numberToString) {
		String[][] levelThere = new String[numberToString.size() * 2][numberToString.size() * 2];
		
		String node = dungeon.getCurrentlevel().name; // Starting point of recursive funciton
		
		directional.entrySet().removeIf(e -> e.getValue().size() == 0);
		
		if(node == null)
			throw new IllegalStateException("The Dungeon's current level wasn't set, make sure that it is set in the .dot file.");
		
		int y = (levelThere.length - 1) / 2;
		int x = (levelThere.length - 1) / 2;
		levelThere[y][x] = node;
		int tX, tY;
		// Visited stack to keep track of where we have been
		Stack<String> visited = new Stack<>();
		Queue<String> queue = new LinkedList<>();
		queue.add(node);
		
		while(!directional.isEmpty()) {
			String n = queue.poll();
			
			if(n == null) {
				queue.add(directional.keySet().iterator().next());
				continue;
			}
				
			
			
			System.out.println("Got from queue: " + n);
			Point p = getCoords(n, levelThere);
			if(p == null) continue;
			visited.add(n);
			y = p.y;
			x = p.x;
			
			Stack<Pair<String, String>> st = directional.get(n);

			System.out.println(st);
			if(st != null) {
				while(!st.isEmpty()) {
					Pair<String, String> pair = st.pop();
					String direction = pair.t1;
					String whereTo = pair.t2;
					
					if(visited.contains(whereTo) || queue.contains(whereTo)) continue;
					queue.add(whereTo);
					
					System.out.println(n + " - " + direction + " - " + whereTo);
					tY = y;
					tX = x;
					switch(direction) {
					case "UP":
					case "U":
						tY--;
						break;
					case "DOWN":
					case "D":
						tY++;
						break;
					case "RIGHT":
					case "R":
						tX++;
						break;
					case "LEFT":
					case "L":
						tX--;
						break;
					default:
						continue;
					}
					

					levelThere[tY][tX] = whereTo;
				}
				directional.remove(n);
				System.out.println(directional);
			}

		}
		
		
		return ZeldaLevelUtil.trimLevelThere(levelThere); // Trim the levelThere and return
	}
	
	private static Point getCoords(String name, String[][] levelThere) {
		for(int y = 0; y < levelThere.length; y++)
			for(int x = 0; x < levelThere[y].length; x++) 
				if(levelThere[y][x] == name)
					return new Point(x, y);
		
		return null;
	}

	/**
	 * Function that takes the graph .dot file and adds the necessary edges and converts the room to it's label
	 * @param dungeon Dungeon instance to add it to
	 * @param numberToString map to keep track of the numbered rooms and node names
	 * @throws FileNotFoundException
	 */
	private static void loadGraph(Dungeon dungeon, HashMap<Integer, String> numberToString, String graph) {
		File graphFile = new File(graph);
		Scanner scanner;
		try {
			scanner = new Scanner(graphFile);
		} catch (FileNotFoundException e) {
			scanner = null;
			System.out.println(graphFile.getName() + " does not exist");
			e.printStackTrace();
			System.exit(1);
		}
		scanner.nextLine(); // "digraph" crap
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.indexOf("}") != -1) {
				System.out.println("Got to end of graph file");
				scanner.close();
				return;
			}; // If the line contains the ending bracked get out
			if(line.indexOf("->") != -1) { // if the line contains an arrow, it's an edge
				System.out.println("Found edge : " + line);
				addEdge(dungeon, numberToString, line);
			} else { // otherwise the line contains the room data
				System.out.println("Found level : " + line);
				convertRoom(dungeon, numberToString, line);
			}
		}
		scanner.close();
	}

	/**
	 * Add necessary information to the room
	 * @param dungeon Dungeon instance the room is a part of
	 * @param numberToString map to keep track of the numbered rooms and node names
	 * @param line String of the room information
	 */
	private static void convertRoom(Dungeon dungeon, HashMap<Integer, String> numberToString, String line) {
		Scanner scanner = new Scanner(line);
		
		int nodeNumber = scanner.nextInt();
		String nodeName = numberToString.get(nodeNumber);
		if(nodeName == null) {
			System.out.println("Nodename " + nodeName + " not found.");
			scanner.close();
			return;
		}
		Node node = dungeon.getNode(nodeName);
		
		String[] values = getLabelValues(scanner.next());
		System.out.println("Got values : " + values);
		for(String value : values) {
			switch(value) {
			case "m":
			case "e": // Room has enemies
			case "b":
				ZeldaLevelUtil.addRandomEnemy(node.level.intLevel);
				System.out.println("Adding enemy | " + value);
				break;
			case "k": // Room has a key in it
				numKeys++;
				if(RANDOM_KEY)
					ZeldaLevelUtil.placeRandomKey(node.level.intLevel, RandomNumbers.randomGenerator);
				else
					ZeldaDungeon.placeNormalKey(node.level.intLevel);
				break;
			case "s": // Room is starting point
				dungeon.setCurrentLevel(nodeName);
				break;
			case "t":
				addTriforce(node, dungeon);
				break;
			}
		}
		scanner.close();
	}

	private static void addTriforce(Node node, Dungeon dungeon) {
		System.out.println("Set triforce");
		List<List<Integer>> level = node.level.intLevel;
		int y = level.size() / 2;
		int x = level.get(y).size() / 2;
		level.get(y).set(x, Tile.TRIFORCE.getNum());
		dungeon.setGoalPoint(new Point(x, y));
		dungeon.setGoal(node.name);
	}

	/**
	 * Add the edge given the line
	 * @param dungeon Dungeon instance
	 * @param numberToString map to keep track of the numbered rooms and node names
	 * @param line String of necessary edge information
	 */
	private static void addEdge(Dungeon dungeon, HashMap<Integer, String> numberToString, String line) {
		Scanner scanner = new Scanner(line);
		
		int nodeNumber = scanner.nextInt();
		String nodeName = numberToString.get(nodeNumber);
		
		scanner.next();
		
		int whereToNumber = scanner.nextInt();
		String whereTo = numberToString.get(whereToNumber);
		if(nodeName == null || whereTo == null) {
			scanner.close();
			return;
		}
		
		String[] values = getLabelValues(scanner.next());
		if(values.length > 0)
			addAdjacency(values, dungeon, nodeName, whereTo);
		
		scanner.close();
	}

	/**
	 * Add the edge to the node
	 * @param values Values from label in .dot
	 * @param dungeon Dungeon instance
	 * @param nodeName Node's name to add the edge
	 * @param whereTo Where the edge is going to 
	 */
	private static void addAdjacency(String[] values, Dungeon dungeon, String nodeName, String whereTo) {
		String direction = getDirection(values);
		if(direction == null) return;
		Node node = dungeon.getNode(nodeName);
		if(values[0] != direction) {
			String action = values[0];
			switch(action) {
			case "l": // Soft lock, treat as open door for now
				setLevels(direction, node, Tile.SOFT_LOCK_DOOR);
				break;
			case "k": // Locked door
				numDoors++;
				setLevels(direction, node, Tile.LOCKED_DOOR);
				break;
			case "b": // Hidden door
				setLevels(direction, node, Tile.HIDDEN);
				break;
			case "s":
				setLevels(direction, node, Tile.PUZZLE_LOCKED);
				break;
			}
		} else {
			setLevels(direction, node, Tile.DOOR);
		}
		
		// Add the necessary starting and exit points
		switch(direction) {
		case "UP":
		case "AcrossU":
			addUpAdjacencies(node, whereTo);
			break;
		case "DOWN":
		case "AcrossD":
			addDownAdjacencies(node, whereTo);
			break;
		case "LEFT":
		case "AcrossL":
			addLeftAdjacencies(node, whereTo);
			break;
		case "RIGHT":
		case "AcrossR":
			addRightAdjacencies(node, whereTo);
			break;
		}
		
		if(!directional.containsKey(nodeName)) // Add the node's list for creating the 2D map
			directional.put(nodeName, new Stack<Pair<String,String>>());
		if(!direction.startsWith("Across"))
			directional.get(nodeName).push(new Pair<String, String>(direction, whereTo));
	}

	/**
	 * Find the x and y coordinates of where the node is based on levelThere
	 * If there's not a node in the 2D array add it and return it's location
	 * 
	 * @param nodeName Node to find coords of
	 * @param levelThere 2D map
	 * @return Point of coords
	 */
	// NEVER USED?
//	private static Point findNodeName(String nodeName, String[][] levelThere) {
//		for(int y = 0; y < levelThere.length; y++)
//			for(int x = 0; x < levelThere[y].length; x++)
//				if(levelThere[y][x] == nodeName)
//					return new Point(x, y);
//		
//		int x = levelThere[0].length / 2;
//		int y = levelThere.length / 2;
//		levelThere[y][x] = nodeName;
//		return new Point(x, y);
//	}

	/**
	 * Set edges when you're going UP
	 * @param newNode Node to add the edge too
	 * @param whereTo String representation of the room you're going to
	 */
	private static void addUpAdjacencies(Node newNode, String whereTo) {
		int y = 1;
		for(int x = 7; x <= 8; x++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(x, 8);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	}
	
	/**
	 * Set edges when you're going DOWN
	 * @param newNode Node to add the edge too
	 * @param whereTo String representation of the room you're going to
	 */
	private static void addDownAdjacencies(Node newNode, String whereTo) {
		int y = 9;
		for(int x = 7; x <= 8; x++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(x, 2);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	}
	
	/**
	 * Set edges when you're going RIGHT
	 * @param newNode Node to add the edge too
	 * @param whereTo String representation of the room you're going to
	 */
	private static void addRightAdjacencies(Node newNode, String whereTo) {
		int x = 14;
		for(int y = 4; y <= 6; y++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(2, y);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	}
	
	/**
	 * Set edges when you're going LEFT
	 * @param newNode Node to add the edge too
	 * @param whereTo String representation of the room you're going to
	 */
	private static void addLeftAdjacencies(Node newNode, String whereTo) {
		int x = 1;
		for(int y = 4; y <= 6; y++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(13, y);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	}

	/**
	 * Get the direction from the string of values
	 * @param values String array of values
	 * @return Direction string
	 */
	private static String getDirection(String[] values) {
		for(String value : values) {
			switch(value) {
			case "UP":
			case "DOWN":
			case "LEFT":
			case "RIGHT":
			case "AcrossU":
			case "AcrossD":
			case "AcrossL":
			case "AcrossR":
			case "L":
			case "R":
			case "U":
			case "D":
				return value;
			}
				
					
		}
		return null;
	}
	
	/**
	 * Set the int level doors based on direction
	 * @param direction Direction of where the exit point is
	 * @param node Node to add the doors to
	 * @param tile Tile type
	 */
	private static void setLevels(String direction, Node node, Tile tile) {
		switch(direction) {
		case "AcrossU":
			direction = "UP";
			break;
		case "AcrossD":
			direction = "DOWN";
			break;
		case "AcrossL":
			direction = "LEFT";
			break;
		case "AcrossR":
			direction = "RIGHT";
			break;
		}
		int num = tile.getNum();
		List<List<Integer>> level = node.level.intLevel;
		if(direction.equals("UP")  || direction.equals("DOWN")) { // Add doors at top or bottom
			int y = (direction.equals("UP")) ? 1 : 9; // Set x based on side 1 if left 9 if right
			for(int x = 7; x <=8; x++) {
				level.get(y).set(x, num);
			}
		} else if (direction.equals("LEFT") || direction.equals("RIGHT") ) { // Add doors at left or right
			int x = (direction.equals("LEFT")) ? 1 : 14; // Set y based on side 1 if up 14 if bottom
			for(int y = 4; y <= 6; y++) {
				level.get(y).set(x, num);
			}
		}
	}

	/**
	 * Get the label values 
	 * @param next String of where the label values are
	 * @return String array of values
	 */
	private static String[] getLabelValues(String next) {
		String[] valuesInQuotes = StringUtils.substringsBetween(next, "\"", "\"");
		return StringUtils.split(valuesInQuotes[0], ',');
	}

	/**
	 * Load the text levels based on the folder of where they are stored
	 * @param dungeon Dungeon instance
	 * @param numberToString number to string name
	 * @throws Exception 
	 */
	private static void loadLevels(Dungeon dungeon, HashMap<Integer, String> numberToString, String levelPath)  {
		File levelFolder = new File(levelPath);
		for(File entry : levelFolder.listFiles()) {
			String fileName = entry.getName();
			int number = Integer.valueOf(fileName.substring(0, fileName.indexOf('.')));
			// The random method replaced a call to randomAlphabetic. This was needed, since the more general random
			// method is the only one that allows a random generator to be supplied, allowing reproducibility.
			numberToString.put(number, RandomStringUtils.random(4,'A','Z',true,false,null,RandomNumbers.randomGenerator));
			loadOneLevel(entry, dungeon, numberToString.get(number));
		}
	}

	/**
	 * Load one specific level from text to int
	 * @param file File instance of individual level
	 * @param dungeon Dungeon instance
	 * @param name Node name
	 * @throws Exception 
	 */
	private static void loadOneLevel(File file, Dungeon dungeon, String name) {
		String[] levelString = new String[ZELDA_ROOM_ROWS];
		Scanner scanner;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			scanner = null;
			System.out.println(file.getName() + " does not exist.");
			e.printStackTrace();
			System.exit(1);
		}
		int i = 0;
		while(scanner.hasNextLine())
			levelString[i++] = scanner.nextLine();
			
		List<List<Integer>> levelInt = ZeldaVGLCUtil.convertZeldaLevelVGLCtoRoomAsList(levelString);
		Level level = new Level(levelInt);
		dungeon.newNode(name, level);
		scanner.close();
	}
}

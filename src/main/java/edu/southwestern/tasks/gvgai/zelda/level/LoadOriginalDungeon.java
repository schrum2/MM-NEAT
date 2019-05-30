package edu.southwestern.tasks.gvgai.zelda.level;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.level.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaDungeon.Level;
import edu.southwestern.util.datastructures.Pair;
import me.jakerg.rougelike.RougelikeApp;

public class LoadOriginalDungeon {
	
	public static final int ZELDA_ROOM_ROWS = 11; // This is actually the room height from the original game, since VGLC rotates rooms
	public static final int ZELDA_ROOM_COLUMNS = 16;
	private static String ORIGINAL_FILE = "tloz2_1_flip";
	private static String GRAPH_FILE = "data/VGLC/Zelda/Graph Processed/" + ORIGINAL_FILE + ".dot";
	private static String LEVEL_PATH = "data/VGLC/Zelda/Processed/" + ORIGINAL_FILE;
	private static HashMap<String, Stack<Pair<String, String>>> directional;
	                      // Node name  direct, whereTo
	public static void main(String[] args) throws FileNotFoundException {
		Dungeon dungeon = new Dungeon(); // Make new dungeon instance
		directional = new HashMap<>(); // Make directional hashmap
		HashMap<Integer, String> numberToString = new HashMap<>(); // Map the numbers to strings (node name)
		System.out.println("Loading .txt levels");
		loadLevels(dungeon, numberToString); // Load the levels (txt files) to dungeon
		System.out.println("Loading levels from graph");
		loadGraph(dungeon, numberToString); // Load the graph representation to dungeon
		System.out.println("Generating 2D map");
		dungeon.setLevelThere(generateLevelThere(dungeon, numberToString)); // Generate the 2D map of the dungeon
		RougelikeApp.startDungeon(dungeon); // start game
	}

	/**
	 * Starting function to recursively generate the 2D map
	 * @param dungeon Dungeon instance
	 * @param numberToString Number to string representation
	 * @return 2D String array of where the levels are
	 */
	private static String[][] generateLevelThere(Dungeon dungeon, HashMap<Integer, String> numberToString) {
		String[][] levelThere = new String[numberToString.size()][numberToString.size()];
		
		String node = dungeon.getCurrentlevel().name; // Starting point of recursive funciton
		
		// Visited stack to keep track of where we have been
		Stack<String> visited = new Stack<>();
		visited.push(node);
		
		// Start recursive funciton
		recursiveLevelThere(levelThere, visited);
		return trimLevelThere(levelThere); // Trim the levelThere and return
	}

	/**
	 * Recursive function to generate the 2D string map
	 * @param levelThere 2D string map
	 * @param visited Visited stack
	 */
	private static void recursiveLevelThere(String[][] levelThere, Stack<String> visited) {
		String node = "";
		while(!visited.isEmpty()) { // While there is still a visited stack to keep track of
			node = visited.peek(); // Get the top of stack
			if(directional.containsKey(node) && !directional.get(node).isEmpty()) // If the directional map has the node and the node's list is not empty: use it
				break;
			else
				visited.pop(); // Otherwise go on to the next node
		}
		if(visited.isEmpty() || directional.size() == 0) return; // If the visited stack is empty or the directional map is empty return
		Point p = findNodeName(node, levelThere); // Get the point of the node on levelthere
		int x = p.x;
		int y = p.y;
		
		if(directional.containsKey(node) && !directional.get(node).isEmpty()) {// ensure that there's still an existing list to use
			Pair<String, String> pair = directional.get(node).pop(); // Get the top of the node list
			switch(pair.t1) { // Get where to place the whereTo node based on the direction
			case "UP":
				y--;
				break;
			case "DOWN":
				y++;
				break;
			case "LEFT":
				x--;
				break;
			case "RIGHT":
				x++;
				break;
			default: return;
			}
			levelThere[y][x] = pair.t2; // Place node
			visited.push(pair.t2); // Push the node to visited
			recursiveLevelThere(levelThere, visited); // keep on truckin

		} else if(directional.get(node).isEmpty()) { // If the node's list is empty, remove the node from the directional map
			directional.remove(node);
		}
		
	}

	/**
	 * Since levelThere is a huge 2D array, trim it to the necessary parts
	 * @param levelThere Large 2D level array
	 * @return Trimmed level array
	 */
	private static String[][] trimLevelThere(String[][] levelThere) {
		int minY = 0, maxY = 0, minX = 0, maxX = 0;
		
		// Get the min y value 
		for(int y = 0; y < levelThere.length; y++)
			for(int x = 0; x < levelThere[y].length; x++)
				if(levelThere[y][x] != null) {
					minY = y + 1;
					break;
				}
		
		// Get the min x value
		for(int x = 0; x < levelThere[0].length; x++)
			for(int y = 0; y < levelThere.length; y++)
				if(levelThere[y][x] != null) {
					minX = x + 1;
					break;
				}
		
		// Get the max Y value
		for(int y = levelThere.length - 1; y >= 0; y--)
			for(int x = levelThere[y].length - 1; x >= 0; x--)
				if(levelThere[y][x] != null) {
					maxY = y;
					break;
				}
		
		// Get the max x value
		for(int x = levelThere[0].length - 1; x >= 0; x--)
			for(int y = levelThere.length - 1; y >= 0; y--)
				if(levelThere[y][x] != null) {
					maxX = x;
					break;
				}
		
		// Calculate size of trimmed down array
		int newY = minY - maxY;
		int newX = minX - maxX;
		
		// Make new level array
		String[][] newLevelThere = new String[newY][newX];
		
		// transfer contents from old to new
		for(int i = 0; i < newLevelThere.length; i++)
			for(int j = 0; j < newLevelThere[i].length; j++)
				newLevelThere[i][j] = levelThere[maxY + i][maxX + j];
			
		return newLevelThere;
	}

	/**
	 * Function that takes the graph .dot file and adds the necessary edges and converts the room to it's label
	 * @param dungeon Dungeon instance to add it to
	 * @param numberToString map to keep track of the numbered rooms and node names
	 * @throws FileNotFoundException
	 */
	private static void loadGraph(Dungeon dungeon, HashMap<Integer, String> numberToString) throws FileNotFoundException {
		File graphFile = new File(GRAPH_FILE);
		Scanner scanner = new Scanner(graphFile);
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
	 * @param line String of the roop information
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
				addRandomEnemy(node);
				System.out.println("Adding enemy | " + value);
				break;
			case "k": // Room has a key in it
				ZeldaDungeon.placeRandomKey(node.level.intLevel);
				break;
			case "s": // Room is starting point
				dungeon.setCurrentLevel(nodeName);
				break;
			}
		}
		scanner.close();
	}

	/**
	 * Add 1 - 3 enemies at random locations
	 * @param node Node to add the enemies to
	 */
	private static void addRandomEnemy(Node node) {
		List<List<Integer>> level = node.level.intLevel;
		Random r = new Random();
		int numEnemies = r.nextInt(3) + 1;
		for(int i = 0; i < numEnemies; i++) {
			int x, y;
			
			do {
		        x = (int)(Math.random() * level.get(0).size());
		        y = (int)(Math.random() * level.size());
		    }
		    while (level.get(y).get(x) != 0);
			
			level.get(y).set(x, 2); 
			System.out.println("Added enemy number " + (i + 1));
		}
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
		Node node = dungeon.getNode(nodeName);
		if(values[0] != direction) {
			String action = values[0];
			switch(action) {
			case "l": // Soft lock, treat as open door for now
				setLevels(direction, node, 3);
			case "k": // Locked door
				setLevels(direction, node, 5);
				break;
			case "b": // Hidden door
				setLevels(direction, node, 7);
				break;
			}
		}
		
		// Add the necessary starting and exit points
		switch(direction) {
		case "UP":
			addUpAdjacencies(node, whereTo);
			break;
		case "DOWN":
			addDownAdjacencies(node, whereTo);
			break;
		case "LEFT":
			addLeftAdjacencies(node, whereTo);
			break;
		case "RIGHT":
			addRightAdjacencies(node, whereTo);
			break;
		}
		
		if(!directional.containsKey(nodeName)) // Add the node's list for creating the 2D map
			directional.put(nodeName, new Stack<Pair<String,String>>());
		
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
	private static Point findNodeName(String nodeName, String[][] levelThere) {
		for(int y = 0; y < levelThere.length; y++)
			for(int x = 0; x < levelThere[y].length; x++)
				if(levelThere[y][x] == nodeName)
					return new Point(x, y);
		
		int x = levelThere[0].length / 2;
		int y = levelThere.length / 2;
		levelThere[y][x] = nodeName;
		return new Point(x, y);
	}

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
			if(value.equals("UP") || value.equals("DOWN") ||
					value.equals("LEFT") || value.equals("RIGHT"))
				return value;
		}
		return null;
	}
	
	/**
	 * Set the int level doors based on direction
	 * @param direction Direction of where the exit point is
	 * @param node Node to add the doors to
	 * @param tile Specific door (5 for locked, 7 for hidden)
	 */
	private static void setLevels(String direction, Node node, int tile) {
		List<List<Integer>> level = node.level.intLevel;
		if(direction.equals("UP")  || direction.equals("DOWN")) { // Add doors at top or bottom
			int y = (direction.equals("UP")) ? 1 : 9; // Set x based on side 1 if left 9 if right
			for(int x = 7; x <=8; x++) {
				level.get(y).set(x, tile);
			}
		} else if (direction.equals("LEFT") || direction.equals("RIGHT") ) { // Add doors at left or right
			int x = (direction.equals("LEFT")) ? 1 : 14; // Set y based on side 1 if up 14 if bottom
			for(int y = 4; y <= 6; y++) {
				level.get(y).set(x, tile);
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
	 * @throws FileNotFoundException
	 */
	private static void loadLevels(Dungeon dungeon, HashMap<Integer, String> numberToString) throws FileNotFoundException {
		File levelFolder = new File(LEVEL_PATH);
		for(File entry : levelFolder.listFiles()) {
			String fileName = entry.getName();
			int number = Integer.valueOf(fileName.substring(0, fileName.indexOf('.')));
			numberToString.put(number, UUID.randomUUID().toString());
			loadOneLevel(entry, dungeon, numberToString.get(number));
		}
	}

	/**
	 * Load one specific level from text to int
	 * @param file File instance of individual level
	 * @param dungeon Dungeon instance
	 * @param name Node name
	 * @throws FileNotFoundException
	 */
	private static void loadOneLevel(File file, Dungeon dungeon, String name) throws FileNotFoundException {
		String[] levelString = new String[ZELDA_ROOM_ROWS];
		Scanner scanner = new Scanner(file);
		int i = 0;
		while(scanner.hasNextLine())
			levelString[i++] = scanner.nextLine();
			
		List<List<Integer>> levelInt = ZeldaVGLCUtil.convertZeldaLevelVGLCtoRoomAsList(levelString);
		Level level = new Level(levelInt);
		dungeon.newNode(name, level);
		scanner.close();
	}
}

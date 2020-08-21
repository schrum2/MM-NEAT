package edu.southwestern.tasks.gvgai.zelda.dungeon;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon.Level;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGrammar;
import edu.southwestern.util.datastructures.Pair;
import me.jakerg.rougelike.RougelikeApp;
import me.jakerg.rougelike.Tile;

public class Dungeon {

	private HashMap<String, Node> levels;
	private String currentLevel;
	private String[][] levelThere;
	private String goal;
	private Point goalPoint;
	private int levelWidth = -1;
	private int levelHeight = -1;
	private Set<String> levelsVisited;
	private boolean markReachableRoomsEverCalled = false;

	public Dungeon() {
		levels = new HashMap<>();
		levelThere = null;
		levelsVisited = new HashSet<>();
	}

	/**
	 * Helper function to return a dungeon instance from a json file
	 * 
	 * @param filePath Path to JSON file
	 * @return Dungeon filled with info from JSON file
	 */
	public static Dungeon loadFromJson(String filePath) {
		Gson gson = new Gson();
		try {
			FileInputStream stream = new FileInputStream(filePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			Dungeon d = gson.fromJson(reader, Dungeon.class);
			reader.close();
			stream.close();
			return d;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void saveToJson(String filePath) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		FileWriter writer = new FileWriter(filePath);
		gson.toJson(this, writer);
		writer.flush();
		writer.close();

	}

	public HashMap<String, Node> getLevels() {
		return this.levels;
	}

	public List<Node> getNodes() {
		return new LinkedList<>(levels.values());
	}

	public Node newNode(String name, Level level) {
		Node node = new Node(name, level);
		if (levels.get(name) != null)
			throw new IllegalStateException("Unable to place new node : " + name);
		levels.put(name, node);
		return node;
	}

	public Node getNode(String name) {
		return levels.get(name);
	}

	public void setCurrentLevel(String name) {
		this.levelsVisited.add(name);
		RougelikeApp.PD.distinctRoomsVisited = Math.max(levelsVisited.size(), RougelikeApp.PD.distinctRoomsVisited);
		this.currentLevel = name;
	}

	public void setLevelThere(String[][] levelThere) {
		this.levelThere = levelThere;
	}

	public String[][] getLevelThere() {
		return this.levelThere;
	}

	/**
	 * Set the next node based on the exit point
	 * 
	 * @param exitPoint Exit Point of level based on string
	 * @return Point of where to start the new level
	 */
	public Point getNextNode(String exitPoint) {
		Node n = getCurrentlevel();
		HashMap<String, Pair<String, Point>> adjacency = n.adjacency;
		Pair<String, Point> next = adjacency.get(exitPoint);
		setCurrentLevel(next.t1);
		return next.t2;
	}

	public Point getCoords(String name) {
		
		if (!levels.containsKey(name)) {
			System.out.println(levels);
			System.out.println("SET:"+levels.keySet());
			System.out.println("GET:"+levels.get(name));
			System.out.println("CHECK:"+levels.containsKey(name));
			System.out.println("currentLevel:"+currentLevel);
			System.out.println(this.goal);
			
			System.out.println("Name isn't in list : " + name);
			return null;
		}

		for (int y = 0; y < levelThere.length; y++)
			for (int x = 0; x < levelThere[y].length; x++)
				if (name.equals(levelThere[y][x]))
					return new Point(x, y);

		System.out.println("Couldnt find name : " + name);
		return null;
	}

	public Pair<String, Point> getNextLevel(Node node, String exitPoint) {
		HashMap<String, Pair<String, Point>> adjacency = node.adjacency;
		Pair<String, Point> r = adjacency.get(exitPoint);
		return r;
	}

	public Point getCoords(Node node) {
		return getCoords(node.name);
	}

	public Node getCurrentlevel() {
		return levels.get(currentLevel);
	}

	/**
	 * Helper function to get a 2D array of levels based on the strings in
	 * levelThere
	 * 
	 * @return 2D array of levels
	 */
	public Level[][] getLevelArrays() {
		Level[][] r = new Level[levelThere.length][levelThere[0].length];

		for (int y = 0; y < levelThere.length; y++)
			for (int x = 0; x < levelThere[y].length; x++)
				if (levelThere[y][x] != null)
					r[y][x] = levels.get(levelThere[y][x]).level;
				else
					r[y][x] = null;

		return r;
	}

	public Node getNodeAt(int x, int y) {
		if (x < 0 || x >= levelThere[0].length || y < 0 || y >= levelThere.length)
			return null;

		return getNode(levelThere[y][x]);
	}

	public void setGoal(String g) {
		this.goal = g;
	}

	public String getGoal() {
		return goal;
	}

	public Point getGoalPoint() {
		return goalPoint;
	}

	public void setGoalPoint(Point goalPoint) {
		this.goalPoint = goalPoint;
	}

	public int getLevelWidth() {
		if (levelWidth == -1)
			levelWidth = getCurrentlevel().level.intLevel.get(0).size();

		return levelWidth;
	}

	public int getLevelHeight() {
		if (levelHeight == -1)
			levelHeight = getCurrentlevel().level.intLevel.size();

		return levelHeight;
	}

	public void removeNode(String name) {
		Node n = getNode(name);
		// Remove adjacencies from the node adjacencies
		n.adjacency.values().forEach(p -> getNode(p.t1).adjacency.entrySet().removeIf(e -> e.getValue().t1 == name));

		levels.remove(name);
	}

	public class Node {
		// Change this value by calling markReachableRooms
		public transient boolean reachable = false;
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		public Level level;
		public String name;
		public HashMap<String, Pair<String, Point>> adjacency;
		// Exit point Node Starting point
		public ZeldaGrammar grammar;

		public Node(String name, Level level) {
			this.name = name;
			this.level = level;
			adjacency = new HashMap<>();
		}

		public void setAdjacency(String exitPoint, String whereTo, Point startPoint) {
			adjacency.put(exitPoint, new Pair<String, Point>(whereTo, startPoint));
		}

		public String toString() {
			return this.name;
		}

		public boolean hasLock() {
			List<List<Integer>> ints = level.intLevel;
			for (int y = 0; y < ints.size(); y++)
				for (int x = 0; x < ints.get(y).size(); x++)
					if (ints.get(y).get(x).equals(Tile.LOCKED_DOOR.getNum()))
						return true;

			return false;
		}
		
		
		
		@Override
		public int hashCode() {
			return name.hashCode();
			
		}
	}

	public void printLevelThere() {
		for (int y = 0; y < levelThere.length; y++) {
			for (int x = 0; x < levelThere[y].length; x++) {
				String n = levelThere[y][x];
				System.out.print(n + " ");
			}
			System.out.println();
		}
	}

	/**
	 * Put data into room Nodes indicating whether the room is actually reachable from the start room
	 * in terms of door connectivity (does not consider walls)
	 */
	public void markReachableRooms() {
		markReachableRoomsEverCalled = true;
		Node startNode = levels.get(currentLevel);
		startNode.reachable = true; // Obviously reachable
		Stack<Node> stack = new Stack<Node>();
		stack.push(startNode);
		// Exhaustive depth-first search
		while(!stack.isEmpty()) { // As long as nodes can be reached
			Node currentNode = stack.pop();
			// Loops through the room's exit doors
			for(Pair<String,Point> nextRoomInfo: currentNode.adjacency.values()) {
				// Each pair is a destination room name and a location in that room, but we only need the name
				String neighborName = nextRoomInfo.t1;
				Node neighborNode = levels.get(neighborName); // Look up Node based on name
				if(!neighborNode.reachable) {
					// Wasn't reached yet, so add to search
					stack.push(neighborNode);
				}
				// Has been reached, so don't search again
				neighborNode.reachable = true;
			}
		}
	}
	
	/**
	 * Returns the number of rooms that are connected by doorways to the start,
	 * but does NOT assure that keys exist to get through locked doors, or that
	 * walls don't impede progress between doors.
	 * 
	 * @return Count of rooms that are "reachable" or at least possibly reachable
	 */
	public int numberOfPossiblyReachableRooms() {
		if(!markReachableRoomsEverCalled) markReachableRooms();
		int numRoomsReachable = 0;
		for(Node room: getLevels().values()) {
			if(room.reachable) { 
				numRoomsReachable++;
			}
		}
		return numRoomsReachable;
	}

}

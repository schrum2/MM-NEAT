package edu.southwestern.tasks.gvgai.zelda.level;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashMap;
import com.google.gson.Gson;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaDungeon.Level;
import edu.southwestern.util.datastructures.Pair;


public class Dungeon {

	private HashMap<String, Node> levels;
	private String currentLevel;
	private String[][] levelThere;
	
	public Dungeon() {
		levels = new HashMap<>();
		levelThere = null;
	}

	/**
	 * Helper function to return a dungeon instance from a json file
	 * @param filePath Path to JSON file
	 * @return Dungeon filled with info from JSON file
	 */
	public static Dungeon loadFromJson(String filePath) {
		Gson gson = new Gson();
		try {
			FileInputStream stream = new FileInputStream(filePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			return gson.fromJson(reader, Dungeon.class);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public HashMap<String, Node> getLevels(){
		return this.levels;
	}

	public Node newNode(String name, Level level) {
		Node node = new Node(name, level);
		levels.put(name, node);
		return node;
	}
	
	public Node getNode(String name) {
		return levels.get(name);
	}
	
	public void setCurrentLevel(String name) {
		this.currentLevel = name;
	}
	
	public void setLevelThere(String[][] levelThere) {
		this.levelThere = levelThere;
	}
	
	public String[][] getLevelThere(){
		return this.levelThere;
	}

	/**
	 * Set the next node based on the exit point
	 * @param exitPoint Exit Point of level based on string
	 * @return Point of where to start the new level
	 */
	@SuppressWarnings("unused")
	public Point getNextNode(String exitPoint) {
		System.out.println("Exit point   " + exitPoint);
		Node n = getCurrentlevel();
		System.out.println("Node : " + n);
		HashMap<String, Pair<String, Point>> adjacency = n.adjacency;
		Pair<String, Point> next = adjacency.get(exitPoint);
		if (next == null) {
			System.out.println("No next, returning null");
			return null;
		}
		System.out.println("Next thingy : " + next.t1);
		setCurrentLevel(next.t1);
		return next.t2;
	}
	
	public Node getCurrentlevel() {
		return levels.get(currentLevel);
	}
	
	/**
	 * Helper function to get a 2D array of levels based on the strings in levelThere
	 * @return 2D array of levels
	 */
	public Level[][] getLevelArrays() {
		Level[][] r = new Level[levelThere.length][levelThere[0].length];
		
		for(int y = 0; y < levelThere.length; y++)
			for(int x = 0; x < levelThere[y].length; x++)
				if(levelThere[y][x] != null)
					r[y][x] = levels.get(levelThere[y][x]).level;
				else
					r[y][x] = null;

		return r;
	}
	
	public class Node{
		public Level level;
		public String name;
		public HashMap<String, Pair<String, Point>> adjacency;
		
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
	}



}


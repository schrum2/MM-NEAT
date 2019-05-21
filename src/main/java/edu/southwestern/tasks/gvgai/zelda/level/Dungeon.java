package edu.southwestern.tasks.gvgai.zelda.level;

import java.awt.Point;
import java.util.HashMap;

import edu.southwestern.tasks.gvgai.zelda.level.ZeldaDungeon.Level;
import edu.southwestern.util.datastructures.Pair;

public class Dungeon {

	private HashMap<String, Node> levels;
	private Node currentLevel;
	
	public Dungeon() {
		levels = new HashMap<>();
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
		this.currentLevel = levels.get(name);
	}
	
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
		return this.currentLevel;
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


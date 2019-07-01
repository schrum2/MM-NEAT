package edu.southwestern.tasks.gvgai.zelda.level;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang.SerializationUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.gvgai.GVGAIUtil;
import edu.southwestern.tasks.gvgai.GVGAIUtil.GameBundle;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction.DIRECTION;
import edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask;
import edu.southwestern.util.datastructures.Triple;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.Search;
import me.jakerg.rougelike.Move;
import me.jakerg.rougelike.RougelikeApp;
import me.jakerg.rougelike.Tile;

/**
 * 
 * @author Jake Gutierrez
 *
 */
public class ZeldaLevelUtil {
	
	/**
	 * Find the longest shortest path distance given a 2D array and start points
	 * @param level 2D int array representing the level, passable = 0
	 * @param startX Where to start on the x axis
	 * @param startY Where to start on the y axis
	 * @return int longest shortest distance
	 */
	public static int findMaxDistanceOfLevel(int[][] level, int startX, int startY) {
		int max = 0;
		LinkedList<Node> visited = uniformCostSearch(level, startX, startY);
		for(Node n : visited) {
			max = Math.max(max, n.gScore);
		}
		return max;
	}
	
	public static LinkedList<Node> uniformCostSearch(int[][] level, int startX, int startY) {
		// List of all the points we have visited included distance
		LinkedList<Node> visited = new LinkedList<>();
	
		Node source = new Node(startX, startY, 0); // use manhattan
		source.fScore = 0;
		
		PriorityQueue<Node> queue = new PriorityQueue<Node>(new Comparator<Node>(){
                         //override compare method
			         public int compare(Node i, Node j){
			        	 return (int) Math.signum(i.fScore - j.fScore);
			         }
                }
		);	
		
		// Push the initial point, startX and startY with a distance of 0
		queue.add(source);

		while((!queue.isEmpty())) {
			Node current = queue.poll();
			visited.add(current);
			
			checkPoint(level, queue, visited, current.point.x + 1, current.point.y, current);
			checkPoint(level, queue, visited, current.point.x, current.point.y + 1, current);
			checkPoint(level, queue, visited, current.point.x - 1, current.point.y, current);
			checkPoint(level, queue, visited, current.point.x, current.point.y - 1, current);
		}


		for(Node n : visited) {
			System.out.println(n);
		}
		
		return visited;
	}

	private static void checkPoint(int[][] level, PriorityQueue<Node> queue, LinkedList<Node> visited, int x, int y,
			Node current) {
		// TODO Auto-generated method stub
		if(x < 0 || x >= level[0].length || y < 0 || y >= level.length) return;
		
		if(level[y][x] != 0) return;
		
		int newGScore = current.gScore + 1; 
		int newFScore = newGScore;
		
		Node newNode = new Node(x, y, newGScore);
		newNode.hScore = 0;
		newNode.fScore = newFScore;
		
		if(visited.contains(newNode)) return;
		else if(!queue.contains(newNode) || newFScore < current.fScore) {
			if(queue.contains(newNode))
				queue.remove(newNode);
			
			queue.add(newNode);
		}
	}
	
	private static boolean hasPoint(ArrayList<Node> visited, Node node) {
		for(Node n : visited)
			if(node.point.x == n.point.x && node.point.y == n.point.y)
				return true;
		
		return false;
	}

	/**
	 * Figure out if we need to add the given point or not if it's not out of bounds
	 * if it hasn't been visited, and if it's not already in the visited list
	 * @param level 2D representation of the level
	 * @param dist 2D array of where we have visited
	 * @param visited List of all points w/ distances that have been visited so far
	 * @param x point to check on x
	 * @param y point to check on y
	 * @param d distance to be added
	 */
	private static void checkPointToAdd(int[][] level, int[][] dist,
			LinkedList<Triple<Integer, Integer, Integer>> visited, int x, int y, int d) {
		
		// Out of bounds check
		if(x < 0 || x >= level[0].length || y < 0 || y >= level.length) return;

		// If haven't been visited check
		if(dist[y][x] != -1) return;
		
		// If the point is possible
		if(level[y][x] != 0) return;
		
		// loop through visited, and return early if the x,y coordinates are present
		for(Triple<Integer, Integer, Integer> point : visited)
			if(point.t1 == x && point.t2 == y) return;
		
		// Finally add point
		visited.add(new Triple<Integer, Integer, Integer>(x, y, d));
		
	}

	/**
	 * Helper function to convert 2D list of ints to 2d array of ints
	 * @param level 2D list representation of given level
	 * @return 2D int array of level
	 */
	public static int[][] listToArray(List<List<Integer>> level) {
		int[][] lev = new int[level.size()][level.get(0).size()];
		for(int i = 0; i < lev.length; i++)
			for(int j = 0; j < lev[i].length; j++)
				lev[i][j] = level.get(i).get(j);
		
		return lev;
	}
	
	private static class Node{
		public Point point;
		public int gScore;
		public int hScore;
		public int fScore = 0;
		
		public Node(int x, int y, int dist) {
			point = new Point(x, y);
			gScore = dist;
		}
		
		@Override
		public boolean equals(Object other){
			boolean r = false;
			if(other instanceof Node) {
				Node node = (Node) other;
				r = this.point.x == node.point.x && this.point.y == node.point.y;
			}
			return r;
		}
		
		public void copy(Node other) {
			this.point = other.point;
			this.gScore = other.gScore;
			this.hScore = other.hScore;
			this.fScore = other.fScore;
		}
		
		public String toString() {
			return "(" + point.x +", " + point.y + "), f = " + fScore + " = (h:" + hScore + " + g:" + gScore +")";
		}
	}

	/**
	 * Place a random key tile on the floor
	 * @param level
	 */
	public static void placeRandomKey(List<List<Integer>> level) {
		int x, y;
		
		do {
			x = (int) RandomNumbers.boundedRandom(0, level.get(0).size());
			y = (int) RandomNumbers.boundedRandom(0, level.size());
	    }
	    while (!Tile.findNum(level.get(y).get(x)).equals(Tile.FLOOR));
		
		System.out.println("Put key at " + x + ", " + y);
		level.get(y).set(x, Tile.KEY.getNum()); 
	}

	public static void setDoors(String direction, Dungeon.Node fromNode, int tile) {
		List<List<Integer>> level = fromNode.level.intLevel;
		if(Parameters.parameters.booleanParameter("zeldaGANUsesOriginalEncoding")) {
			if(direction == "UP" || direction == "DOWN") { // Add doors at top or bottom
				int y = (direction == "UP") ? 1 : 14; // Set y based on side 1 if up 14 if bottom
				int dy = (direction.equals("UP")) ? 1 : -1;
				for(int x = 4; x <= 6; x++) {
					level.get(y).set(x, tile);
					if(!Tile.findNum(level.get(y + dy).get(x)).playerPassable())
						level.get(y + dy).set(x, Tile.FLOOR.getNum());
				}
			} else if (direction == "LEFT" || direction == "RIGHT") { // Add doors at left or right
				int x = (direction == "LEFT") ? 1 : 9; // Set x based on side 1 if left 9 if right
				int dx = (direction.equals("LEFT")) ? 1 : -1;
				for(int y = 7; y <=8; y++) {
					level.get(y).set(x, tile);
					if(!Tile.findNum(level.get(y).get(x + dx)).playerPassable())
						level.get(y).set(x + dx, Tile.FLOOR.getNum());
				}
			}
		} else {
			if(direction.equals("UP")  || direction.equals("DOWN")) { // Add doors at top or bottom
				int y = (direction.equals("UP")) ? 1 : 9; // Set x based on side 1 if left 9 if right
				int dy = (direction.equals("UP")) ? 1 : -1;
				for(int x = 7; x <= 8; x++) {
					level.get(y).set(x, tile);
					if(!Tile.findNum(level.get(y + dy).get(x)).playerPassable())
						level.get(y + dy).set(x, Tile.FLOOR.getNum());
				}
			} else if (direction.equals("LEFT") || direction.equals("RIGHT") ) { // Add doors at left or right
				int x = (direction.equals("LEFT")) ? 1 : 14; // Set y based on side 1 if up 14 if bottom
				int dx = (direction.equals("LEFT")) ? 1 : -1;
				for(int y = 4; y <= 6; y++) {
					level.get(y).set(x, tile);
					if(!Tile.findNum(level.get(y).get(x + dx)).playerPassable())
						level.get(y).set(x + dx, Tile.FLOOR.getNum());
				}
			}
		}
		
		Tile t = Tile.findNum(tile);
		if(t == null || fromNode.grammar == null) return;
		if(t.equals(Tile.SOFT_LOCK_DOOR) && fromNode.grammar.equals(ZeldaGrammar.ENEMY))
			handleSoftLock(direction, level);
		else if(fromNode.grammar.equals(ZeldaGrammar.PUZZLE))
			placePuzzle(direction, level);
	}

	private static void placePuzzle(String direction, List<List<Integer>> level) {
		List<Point> points = getVisitedPoints(direction, level);
		Move d = Move.getByString(direction);
		Point rP = points.get(RandomNumbers.randomGenerator.nextInt(points.size()));
		
		level.get(rP.y).set(rP.x, Tile.FLOOR.getNum());
		rP.y += d.getPoint().y;
		rP.x += d.getPoint().x;
		level.get(rP.y).set(rP.x, Tile.findByMove(d).getNum());
		rP.y += d.getPoint().y;
		rP.x += d.getPoint().x;
		level.get(rP.y).set(rP.x, Tile.FLOOR.getNum());
	}

	private static void handleSoftLock(String direction, List<List<Integer>> level) {
		List<Point> points = getVisitedPoints(direction, level);
		int r = RandomNumbers.randomGenerator.nextInt(3) + 1;
		for(int i = 0; i < r && points.size() > 0; i++) {
			Point rP = points.remove(RandomNumbers.randomGenerator.nextInt(points.size()));
			level.get(rP.y).set(rP.x, 2);
		}
	}

	public static List<Point> getVisitedPoints(int x, int y, List<List<Integer>> level) {
		List<Point> visited = new LinkedList<>();
		Queue<Point> queue = new LinkedList<>();
		queue.add(new Point(x, y));
		while(!queue.isEmpty()) {
			Point p = queue.poll();
			visited.add(p);
			for(Move m : Move.values()) {
				Point d = m.getPoint();
				int dx = p.x + d.x;
				int dy = p.y + d.y;
				Point c = new Point(dx, dy);
				Tile t = Tile.findNum(level.get(dy).get(dx));
				if(t.playerPassable() && !visited.contains(c))
					queue.add(c);
			}
		}
		return visited;
	}
	
	public static List<Point> getVisitedPoints(String direction, List<List<Integer>> level){
		int x, y;
		switch(direction) {
		case "UP":
			x = 7;
			y = 2;
			break;
		case "DOWN":
			x = 8;
			y = 8;
			break;
		case "LEFT":
			x = 2;
			y = 5;
			break;
		case "RIGHT":
			x = 13;
			y = 5;
			break;
		default:
			return null;
		}
		
		return getVisitedPoints(x, y, level);
	}

	/**
	 * Set edges when you're going UP
	 * @param newNode Node to add the edge too
	 * @param whereTo String representation of the room you're going to
	 */
	public static void addUpAdjacencies(Dungeon.Node newNode, String whereTo) {
		int y, minX, maxX = 0, startY;
		if(Parameters.parameters.booleanParameter("zeldaGANUsesOriginalEncoding")) {
			y = 1;
			minX = 4;
			minX = 6;
			startY = 13;
		} else {
			y = 1;
			minX = 7;
			maxX = 8;
			startY = 8;			
		}
	
		for(int x = minX; x <= maxX; x++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(x, startY);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	}

	/**
	 * Set edges when you're going LEFT
	 * @param newNode Node to add the edge too
	 * @param whereTo String representation of the room you're going to
	 */
	public static void addLeftAdjacencies(Dungeon.Node newNode, String whereTo) {
		int x, minY, maxY = 0, startX;
		if(Parameters.parameters.booleanParameter("zeldaGANUsesOriginalEncoding")){
			x = 1;
			minY = 7;
			minY = 8;
			startX = 8;
		} else {
			x = 1;
			minY = 4;
			maxY = 6;
			startX = 13;
		}
		for(int y = minY; y <= maxY; y++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(startX, y);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	
	}

	/**
	 * Set edges when you're going RIGHT
	 * @param newNode Node to add the edge too
	 * @param whereTo String representation of the room you're going to
	 */
	public static void addRightAdjacencies(Dungeon.Node newNode, String whereTo) {
		int x, minY, maxY;
		if(Parameters.parameters.booleanParameter("zeldaGANUsesOriginalEncoding")) {
			x = 9;
			minY = 7;
			maxY = 8;
		} else {
			x = 14;
			minY = 4;
			maxY = 6;
		}
		
		for(int y = minY; y <= maxY; y++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(2, y);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	}

	/**
	 * Set edges when you're going DOWN
	 * @param newNode Node to add the edge too
	 * @param whereTo String representation of the room you're going to
	 */
	public static void addDownAdjacencies(Dungeon.Node newNode, String whereTo) {
		int y, minX, maxX;
		if(Parameters.parameters.booleanParameter("zeldaGANUsesOriginalEncoding")) {
			y = 14;
			minX = 4;
			maxX = 6;
		} else {
			y = 9;
			minX = 7;
			maxX = 8;
	
		}
		for(int x = minX; x <= maxX; x++) {
			Point exitPoint = new Point(x, y);
			Point startPoint = new Point(x, 2);
			newNode.setAdjacency(exitPoint.toString(), whereTo, startPoint);
		}
	}

	/**
	 * Add 1 - 3 enemies at random locations
	 * @param node Node to add the enemies to
	 */
	public static void addRandomEnemy(List<List<Integer>> level) {
		int numEnemies = RandomNumbers.randomGenerator.nextInt(3) + 1;
		for(int i = 0; i < numEnemies; i++) {
			int x, y;
			
			do {
				x = (int) RandomNumbers.boundedRandom(0, level.get(0).size());
				y = (int) RandomNumbers.boundedRandom(0, level.size());
		    }
		    while (level.get(y).get(x) != 0);
			
			level.get(y).set(x, 2); 
		}
	}

	/**
	 * Since levelThere is a huge 2D array, trim it to the necessary parts
	 * @param levelThere Large 2D level array
	 * @return Trimmed level array
	 */
	public static String[][] trimLevelThere(String[][] levelThere) {
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
	
	// TODO: deep copy of linkedlist
	@SuppressWarnings("unchecked")
	public static <E> List<E> copyList(List<E> list){
		List<E> copy = new LinkedList<>();
		for(E obj : list) {
			if(obj instanceof List) {
				List<E> l = (List<E>) obj;
				l = copyList(l);
				copy.add((E) l);
			} else {
				copy.add(obj);
			}
		}
		
		return copy;
	}
	
	public static Heuristic<GridAction,ZeldaState> manhattan = new Heuristic<GridAction,ZeldaState>() {

		@Override
		public double h(ZeldaState s) {
			Dungeon d = s.getDungeon();
			Point goalPoint = d.getCoords(d.getGoal());
			int gDX = goalPoint.x;
			int gDY = goalPoint.y;
			
			int w = s.getDungeon().getLevelWidth();
			int h = s.getDungeon().getLevelHeight();
			
			Point g = d.getGoalPoint();
			int gX = g.x;
			int gY = g.y;
			int i = Math.abs(s.x - gX) + Math.abs(s.y - gY);
			int j = Math.abs(gDX - s.dX) * w + Math.abs(gDY - s.dY) * h;
			
			
			
			return i + j; 
		}
	};
	
}

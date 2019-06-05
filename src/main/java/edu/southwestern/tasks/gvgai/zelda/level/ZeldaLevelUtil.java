package edu.southwestern.tasks.gvgai.zelda.level;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import edu.southwestern.scores.Score;
import edu.southwestern.util.datastructures.Triple;

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
}

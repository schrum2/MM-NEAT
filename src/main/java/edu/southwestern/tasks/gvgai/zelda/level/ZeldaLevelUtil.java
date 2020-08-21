package edu.southwestern.tasks.gvgai.zelda.level;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon.Level;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.search.Heuristic;
import me.jakerg.rougelike.Creature;
import me.jakerg.rougelike.Ladder;
import me.jakerg.rougelike.Move;
import me.jakerg.rougelike.Tile;

/**
 * 
 * @author Jake Gutierrez
 *
 */
public class ZeldaLevelUtil {

	public static final int FAR_LONG_EDGE_DOOR_COORDINATE = 14;
	public static final int FAR_SHORT_EDGE_DOOR_COORDINATE = 9;
	public static final int ZELDA_FLOOR_SPACE_ROWS = 7;
	public static final int ZELDA_FLOOR_SPACE_COLUMNS = 12;
	public static final int CLOSE_EDGE_DOOR_COORDINATE = 1;
	public static final int BIG_DOOR_COORDINATE_START = 4;
	public static final int BIG_DOOR_COORDINATE_END = 6;
	public static final int SMALL_DOOR_COORDINATE_START = 7;
	public static final int SMALL_DOOR_COORDINATE_END = 8;

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
	/**
	 * performs uniformCostSearch on the level. It checks the cost of all locations adjacent to it
	 * and compares it to the cost it has already calculated through traversal. 
	 * @param level 2D int array representing the level, passable = 0
	 * @param startX Where to start on the x axis
	 * @param startY Where to start on the y axis
	 * @return visited List of all the points we have visited
	 */
	public static LinkedList<Node> uniformCostSearch(int[][] level, int startX, int startY) {
		// List of all the points we have visited included distance
		LinkedList<Node> visited = new LinkedList<>();

		Node source = new Node(startX, startY, 0); // use manhattan
		source.fScore = 0;
		//queue for the nodes
		PriorityQueue<Node> queue = new PriorityQueue<Node>(new Comparator<Node>(){
			//override compare method
			public int compare(Node i, Node j){
				return (int) Math.signum(i.fScore - j.fScore);
			}
		}
				);	

		// Push the initial point, startX and startY with a distance of 0
		queue.add(source);
		//add the appropriate items to the queue
		while((!queue.isEmpty())) {
			Node current = queue.poll();
			visited.add(current);

			checkPoint(level, queue, visited, current.point.x + 1, current.point.y, current); //x up 1
			checkPoint(level, queue, visited, current.point.x, current.point.y + 1, current); //y up 1
			checkPoint(level, queue, visited, current.point.x - 1, current.point.y, current); //x down 1
			checkPoint(level, queue, visited, current.point.x, current.point.y - 1, current); //y down 1
		}


		for(Node n : visited) { //for each node in visited, print the node
			System.out.println(n);
		}

		return visited;
	}
	/**
	 * checks if the next point should be looked at, then adds it to the queue
	 * If the entry is out of bounds, return. If the entry is not zero, return.
	 * If the node had been visited, return
	 * If the queue contains the newNode and
	 * @param level 2D int array representing the level, passable = 0
	 * @param queue the queue containing the points
	 * @param visited List of all the points we have visited 
	 * @param x the x coordinate of the 2D int array
	 * @param y the y coordinate of the 2D int array
	 * @param current the current node
	 * @return void
	 */
	private static void checkPoint(int[][] level, PriorityQueue<Node> queue, LinkedList<Node> visited, int x, int y,
			Node current) {
		// checks bounds
		if(x < 0 || x >= level[0].length || y < 0 || y >= level.length) return;
		//if the entry is zero, return
		if(level[y][x] != 0) return;

		int newGScore = current.gScore + 1; 
		int newFScore = newGScore;

		Node newNode = new Node(x, y, newGScore);
		newNode.hScore = 0;
		newNode.fScore = newFScore;
		//if it's been visited, return
		if(visited.contains(newNode)) return;
		else if(!queue.contains(newNode) || newFScore < current.fScore) { //if the queue contains the node or the score is less
			//than the current score, then add to the queue
			if(queue.contains(newNode)) //if it is already in the queue, then remove it
				queue.remove(newNode);

			queue.add(newNode); //add the new node to the queue
		}
	}

	// NOT USED?
	//	private static boolean hasPoint(ArrayList<Node> visited, Node node) {
	//		for(Node n : visited)
	//			if(node.point.x == n.point.x && node.point.y == n.point.y)
	//				return true;
	//		
	//		return false;
	//	}

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
	// NOT USED?
	//	private static void checkPointToAdd(int[][] level, int[][] dist,
	//			LinkedList<Triple<Integer, Integer, Integer>> visited, int x, int y, int d) {
	//		
	//		// Out of bounds check
	//		if(x < 0 || x >= level[0].length || y < 0 || y >= level.length) return;
	//
	//		// If haven't been visited check
	//		if(dist[y][x] != -1) return;
	//		
	//		// If the point is possible
	//		if(level[y][x] != 0) return;
	//		
	//		// loop through visited, and return early if the x,y coordinates are present
	//		for(Triple<Integer, Integer, Integer> point : visited)
	//			if(point.t1 == x && point.t2 == y) return;
	//		
	//		// Finally add point
	//		visited.add(new Triple<Integer, Integer, Integer>(x, y, d));
	//		
	//	}

	/**
	 * Helper function to convert 2D list of ints to 2d array of ints
	 * @param level 2D list representation of given level
	 * @return lev 2D int array of level
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
		//constructor
		public Node(int x, int y, int dist) {
			point = new Point(x, y);
			gScore = dist;
		}

		@Override
		/**
		 * determines if a node contains the same data as another node
		 * making it equal
		 * @param other the other node
		 */
		public boolean equals(Object other){
			boolean r = false;
			if(other instanceof Node) { //if the other is a node
				Node node = (Node) other;
				r = this.point.x == node.point.x && this.point.y == node.point.y; //compare the x and y
			}
			return r;
		}

		// NOT USED?
		//		public void copy(Node other) {
		//			this.point = other.point;
		//			this.gScore = other.gScore;
		//			this.hScore = other.hScore;
		//			this.fScore = other.fScore;
		//		}
		/**
		 * converts the node into a string
		 */
		public String toString() {
			return "(" + point.x +", " + point.y + "), f = " + fScore + " = (h:" + hScore + " + g:" + gScore +")";
		}
	}

	/**
	 * Place a random key tile on the floor
	 * @param level List representation of level
	 * @param rand Generator determining where key is placed
	 */
	public static void placeRandomKey(List<List<Integer>> level, Random rand) {
		int x, y;

		do {
			x = rand.nextInt(level.get(0).size());
			y = rand.nextInt(level.size());
		}
		while (!Tile.findNum(level.get(y).get(x)).equals(Tile.FLOOR)); //while the given tile is not the floor tile
		//System.out.println("Put key at " + x + ", " + y);
		level.get(y).set(x, Tile.KEY.getNum()); 
	}
	/**
	 * counts the distinct rooms in a dungeon
	 * @param dungeon the dungeon
	 * @param numRoomsReachable the number of rooms reachable
	 * @param START that Start point
	 * @param k a hash set to contain distinct rooms
	 * @return numDistinctRooms the number of distinct rooms
	 */
	public static int countDiscreteRooms(Dungeon dungeon, int numRoomsReachable, Point START, /*ArrayList<ArrayList<Integer>> compareRooms,*/ HashSet<ArrayList<ArrayList<Integer>>> k) {
		for(edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node room: dungeon.getLevels().values()) {
			// TODO: This only applies to water/wall percentage calculation, not distinct room count
			//System.out.println("ROOM:");
			ArrayList<ArrayList<Integer>> compareRooms = new ArrayList<ArrayList<Integer>>();

				for(int x = START.x; x < START.x+ZELDA_FLOOR_SPACE_ROWS; x++) {
					
					ArrayList<Integer> a = new ArrayList<Integer>();
					for(int y = START.y; y < START.y+ZELDA_FLOOR_SPACE_COLUMNS; y++) {
						Tile tile = room.level.rougeTiles[y][x];
//						if(tile.equals(Tile.WALL)) {
//							a.add(Tile.WALL.getNum());
//						}else {
//							a.add(Tile.FLOOR.getNum());
//						}
						if(tile.getNum()==Tile.KEY.getNum()||
						   tile.getNum()==Ladder.INT_CODE||
					  	   tile.getNum()==Creature.ENEMY_INT_CODE||
						   tile.getNum()==Tile.TRIFORCE.getNum()) { 

							a.add(Tile.FLOOR.getNum());
						}else {
							a.add(tile.getNum());
						}
						//System.out.print(a.get(y-START.y));

					}
					compareRooms.add(a);

					//System.out.println();
				}
				
				//System.out.print(compareRooms.toString());

			
				k.add(compareRooms);
				//System.out.println("NUMBER OF DISTINCT ROOMS: "+k.size());
			
		}
		
		
		return k.size();
	}

	/**
	 * Places doors appropriately in each room 
	 * @param direction Direction being moved out of the room
	 * @param fromNode Allows you to get the level from the current node
	 * @param tile Specifies the type of tile to be placed 
	 */
	public static void setDoors(String direction, Dungeon.Node fromNode, int tile) {
		List<List<Integer>> level = fromNode.level.intLevel;
		if(Parameters.parameters.booleanParameter("zeldaGANUsesOriginalEncoding")) {
			if(direction.equals("UP") || direction.equals("DOWN")) { // Add doors at top or bottom
				int y = (direction.equals("UP")) ? CLOSE_EDGE_DOOR_COORDINATE : FAR_LONG_EDGE_DOOR_COORDINATE; // Set y based on side 1 if up 14 if bottom
				int dy = (direction.equals("UP")) ? 1 : -1;
				for(int x = BIG_DOOR_COORDINATE_START; x <= BIG_DOOR_COORDINATE_END; x++) {
					level.get(y).set(x, tile);
					if(!Tile.findNum(level.get(y + dy).get(x)).playerPassable())
						level.get(y + dy).set(x, Tile.FLOOR.getNum());
				}
			} else if (direction.equals("LEFT") || direction.equals("RIGHT")) { // Add doors at left or right
				int x = (direction.equals("LEFT")) ? CLOSE_EDGE_DOOR_COORDINATE : FAR_SHORT_EDGE_DOOR_COORDINATE; // Set x based on side 1 if left 9 if right
				int dx = (direction.equals("LEFT")) ? 1 : -1;
				for(int y = SMALL_DOOR_COORDINATE_START; y <=SMALL_DOOR_COORDINATE_END; y++) {
					level.get(y).set(x, tile);
					if(!Tile.findNum(level.get(y).get(x + dx)).playerPassable())
						level.get(y).set(x + dx, Tile.FLOOR.getNum());
				}
			}
		} else {
			if(direction.equals("UP")  || direction.equals("DOWN")) { // Add doors at top or bottom
				int y = (direction.equals("UP")) ? CLOSE_EDGE_DOOR_COORDINATE : FAR_SHORT_EDGE_DOOR_COORDINATE; // Set x based on side 1 if left 9 if right
				int dy = (direction.equals("UP")) ? 1 : -1;
				for(int x = SMALL_DOOR_COORDINATE_START; x <= SMALL_DOOR_COORDINATE_END; x++) {
					level.get(y).set(x, tile);
					if(!Tile.findNum(level.get(y + dy).get(x)).playerPassable())
						level.get(y + dy).set(x, Tile.FLOOR.getNum());
				}
			} else if (direction.equals("LEFT") || direction.equals("RIGHT") ) { // Add doors at left or right
				int x = (direction.equals("LEFT")) ? CLOSE_EDGE_DOOR_COORDINATE : FAR_LONG_EDGE_DOOR_COORDINATE; // Set y based on side 1 if up 14 if bottom
				int dx = (direction.equals("LEFT")) ? 1 : -1;
				for(int y = BIG_DOOR_COORDINATE_START; y <= BIG_DOOR_COORDINATE_END; y++) {
					level.get(y).set(x, tile);
					if(!Tile.findNum(level.get(y).get(x + dx)).playerPassable())
						level.get(y).set(x + dx, Tile.FLOOR.getNum());
				}
			}
		}
		Tile t = Tile.findNum(tile);
		if(t == null || fromNode.grammar == null) return;
		// TODO: This is the method of raft placement that assumes it appears in the first soft-locked room. This is restrictive.
		if(t.equals(Tile.SOFT_LOCK_DOOR) && fromNode.grammar.equals(ZeldaGrammar.ENEMY))
			placeReachableEnemiesAndRaft(direction, fromNode, 3); // Place a raft and 1-3 enemies in the room
		else if(!t.equals(Tile.PUZZLE_LOCKED) && fromNode.grammar.equals(ZeldaGrammar.PUZZLE))
			placePuzzle(direction, level, RandomNumbers.randomGenerator);
		else if(fromNode.grammar.equals(ZeldaGrammar.KEY))
			placeReachableEnemies(direction, level, 2); // Place 1 or 2 enemies in the room
	}

	/**
	 * Places the raft (only if specified in the parameter) and enemies.
	 * @param direction The way you are traveling as the player
	 * @param fromNode Where you are coming from 
	 * @param maxEnemies max number of enemies allowed in that room 
	 */
	private static void placeReachableEnemiesAndRaft(String direction, Dungeon.Node fromNode, int maxEnemies) {
		// Get random floor tile: TODO: Restrict to reachable floor tiles
		if(Parameters.parameters.booleanParameter("firstSoftLockedRoomHasRaft")) {
			List<Point> points = fromNode.level.getFloorTiles();
			Point p = points.get(RandomNumbers.randomGenerator.nextInt(points.size()));
			// Replace with raft
			fromNode.level.intLevel.get(p.y).set(p.x, Ladder.INT_CODE); // -6 is the RAFT/Ladder
		}

		// Place enemies
		placeReachableEnemies(direction, fromNode.level.intLevel, maxEnemies);
	}

	/**
	 * Places a raft in a random room in the dungeon when allowed
	 * @param currentNode The room to place the raft
	 * @param rand So the raft is placed in a random place in the room, and maintains consistency
	 */
	public static void placeRandomRaft(Dungeon.Node currentNode,  Random rand) {
		List<Point> points = currentNode.level.getFloorTiles();
		Point p = points.get(rand.nextInt(points.size()));
		// Replace with raft
		currentNode.level.intLevel.get(p.y).set(p.x, Ladder.INT_CODE); // -6 is the RAFT/Ladder 
	}
	
	/**
	 * Places a raft in a random room in the dungeon when allowed
	 * @param level the intLevel
	 * @param rand So the raft is placed in a random place in the room, and maintains consistency
	 */
	public static void placeRandomRaft(List<List<Integer>> level,  Random rand) {
		int x, y;
		
		do {
			x = rand.nextInt(level.get(0).size());
			y = rand.nextInt(level.size());
	    }
	    while (!Tile.findNum(level.get(y).get(x)).equals(Tile.FLOOR)); //while the given tile is not the floor tile
		// Replace with raft
		level.get(y).set(x, Ladder.INT_CODE); // -6 is the RAFT/Ladder 
	}

	/**
	 * This method placed a puzzle block in a random location in the room if there is a puzzle door
	 * @param direction Direction being moved out of the room
	 * @param level The dungeon 
	 * @param rand A random number generator 
	 */
	public static void placePuzzle(String direction, List<List<Integer>> level, Random rand) {
		if(restictToOnePuzzleBlock(level)) {
			return; 
		}
		List<Point> points = getVisitedPoints(direction, level);
		Move d = Move.getByString(direction).opposite(); //the move is the opposite to the direction
		Point rP = null;
		points.removeIf(p -> !withinBounds(p, d));
		rP = points.remove(rand.nextInt(points.size()));
		level.get(rP.y).set(rP.x, Tile.FLOOR.getNum());
		rP.y += d.getPoint().y;
		rP.x += d.getPoint().x;
		level.get(rP.y).set(rP.x, Tile.findByMove(d).getNum());
		rP.y += d.getPoint().y;
		rP.x += d.getPoint().x;
		level.get(rP.y).set(rP.x, Tile.FLOOR.getNum());
		if(Parameters.parameters.booleanParameter("zeldaALlowPuzzleDoorUglyHack")) placeAround(level, rP, Tile.WATER);
	}

	/**
	 * Loops to find puzzle blocks, if there is already a puzzle block in the room it returns true 
	 * @param level A room 
	 * @return True if there there is already a puzzle block in the room 
	 */
	private static boolean restictToOnePuzzleBlock(List<List<Integer>> level) {
		//loop through the level and exit method if there is already a movable block
		Level rooms = new Level(level); //convert dungeon to Level object
		List<List<Integer>> dungeon = rooms.intLevel;
		for(int i = 0; i < dungeon.size(); i++) {
			for(int j = 0; j < dungeon.get(i).size(); j++) {
				if(dungeon.get(i).get(j).equals(Tile.MOVABLE_BLOCK_DOWN.getNum()) || dungeon.get(i).get(j).equals(Tile.MOVABLE_BLOCK_UP.getNum()) ||
						dungeon.get(i).get(j).equals(Tile.MOVABLE_BLOCK_RIGHT.getNum()) || dungeon.get(i).get(j).equals(Tile.MOVABLE_BLOCK_LEFT.getNum())) {
					return true; 
				}
			}
		}
		return false;
	}

	/**
	 * Will place the tile type perpendicular to the move direction (eg: if d is UP, then it will black LEFT and RIGHT)
	 * @param level Int array to place the block in
	 * @param rP point to start to place
	 * @param tile Tile to place
	 * @param d Move
	 */
	private static void placeAround(List<List<Integer>> level, Point rP, Tile tile) {
		for(Move move : Move.values()) {
			Point check = new Point(rP.x + move.getPoint().x, rP.y + move.getPoint().y);
			if(withinBounds(check)) { //if it's within bounds
				if(Tile.findNum(level.get(check.y).get(check.x)).equals(Tile.WALL)) { //if the tile is the wall tile
					level.get(check.y).set(check.x, tile.getNum()); 
				}
			}
			Point cw = move.clockwise().getPoint();
			check = new Point(check.x + cw.x, check.y + cw.y);
			if(withinBounds(check)) {
				if(Tile.findNum(level.get(check.y).get(check.x)).equals(Tile.WALL)) {
					level.get(check.y).set(check.x, tile.getNum());
				}
			}
		}
	}
	/**
	 * checks if a point and a direction is in bounds or not
	 * @param rP the point in question
	 * @param direction the direction being moved
	 * @return true if within bounds, false otherwise
	 */
	private static boolean withinBounds(Point rP, Move direction) {
		if(direction.equals(Move.UP))
			if(rP.y >= 4)
				return true;
		if(direction.equals(Move.DOWN))
			if(rP.y <= 6)
				return true;
		if(direction.equals(Move.LEFT))
			if(rP.x >= 4)
				return true;
		if(direction.equals(Move.RIGHT))
			if(rP.x <= 11)
				return true;

		return false;
	}

	/**
	 * Checks if the point is inside the play area
	 * @param p Point to check
	 * @return True if the point is within the bounds of the play area (not the door or walls), false otherwise
	 */
	private static boolean withinBounds(Point p) {
		if(p.x >= 2 && p.x <= 13 && p.y >= 2 && p.y <= 8)
			return true;
		return false;
	}

	/**
	 * places reachable enemies
	 * 
	 * @param direction up down left or right 
	 * @param intLevel the level
	 * @param max the max number of enemies
	 */
	private static void placeReachableEnemies(String direction, List<List<Integer>> intLevel, int max) {
		List<Point> points = getVisitedPoints(direction, intLevel);
		points.removeIf(p -> !Tile.findNum(intLevel.get(p.y).get(p.x)).equals(Tile.FLOOR));
		int r = RandomNumbers.randomGenerator.nextInt(max) + 1; // At least 1: [1,max]
		for(int i = 0; i < r && points.size() > 0; i++) {
			Point rP = points.remove(RandomNumbers.randomGenerator.nextInt(points.size()));
			intLevel.get(rP.y).set(rP.x, Creature.ENEMY_INT_CODE); // 2 is for an enemy
		}
	}
	/**
	 * gets the visited points
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param intLevel the level
	 * @return visited, the points visited
	 */
	public static List<Point> getVisitedPoints(int x, int y, List<List<Integer>> intLevel) {
		assert y >=0 && y < intLevel.size() : "y = "+y+ " not in bounds of "+intLevel;
		assert x >=0 && x < intLevel.get(0).size() : "x = "+x+ " not in bounds of "+intLevel;
		
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
				assert dy >=0 && dy < intLevel.size() : "Move:"+m+" from "+p+": dy = "+dy+ " not in bounds of "+intLevel;
				assert dx >=0 && dx < intLevel.get(0).size() : "Move:"+m+" from "+p+": dx = "+dx+ " not in bounds of "+intLevel;
				Tile t = Tile.findNum(intLevel.get(dy).get(dx));
				if(t.playerPassable() && !visited.contains(c))
					queue.add(c);
			}
		}
		return visited;
	}
	/**
	 * gets the visited points
	 * @param direction UP, DOWN, LEFT, or RIGHT
	 * @param intLevel the level
	 * @return visited, the points visited (depending on direction)
	 */
	public static List<Point> getVisitedPoints(String direction, List<List<Integer>> intLevel){
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

		return getVisitedPoints(x, y, intLevel);
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
	public static void addRandomEnemy(List<List<Integer>> intLevel) {
		int numEnemies = RandomNumbers.randomGenerator.nextInt(3) + 1;
		for(int i = 0; i < numEnemies; i++) {
			int x, y;

			do {
				x = RandomNumbers.randomGenerator.nextInt(intLevel.get(0).size());
				y = RandomNumbers.randomGenerator.nextInt(intLevel.size());
			}
			while (intLevel.get(y).get(x) != 0);

			intLevel.get(y).set(x, Creature.ENEMY_INT_CODE); // 2 is the code for enemies
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
	/**
	 * Copies a list
	 *
	 * @param list the list to be copied
	 * @return copy the copied list
	 */
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
	/**
	 * converts a list to an arrayList
	 * 
	 * @param list the list to be converted
	 * @return copy the copied arrayList matching list
	 */
	public static <E> ArrayList<ArrayList<E>> listToArrayList(List<List<E>> list){
		ArrayList<ArrayList<E>> copy = new ArrayList<>();
		for(int i = 0; i < list.size(); i++) {
			copy.add(new ArrayList<>());
			ArrayList<E> al = copy.get(i);
			for(int j = 0; j < list.get(i).size(); j++) {
				al.add(list.get(i).get(j));
			}
		}

		return copy;
	}

	@SuppressWarnings("unchecked")
	/**
	 * copies an arrayList
	 * 
	 * @param list the arrayList to be copied
	 * @return copy the copy of the arrayList
	 */
	public static <E> ArrayList<E> copyList(ArrayList<E> list){
		ArrayList<E> copy = new ArrayList<>();
		for(E obj : list) {
			if(obj instanceof ArrayList) {
				ArrayList<E> l = (ArrayList<E>) obj;
				l = copyList(l);
				copy.add((E) l);
			} else {
				copy.add(obj);
			}
		}

		return copy;
	}
	/**
	 * makes an empty room
	 * @param levelAsListsGrid the level as list grid
	 * @param x1 x coord
	 * @param y1 y coord
	 */
	public static void makeEmptyRoom(List<List<Integer>>[][] levelAsListsGrid, int x1, int y1) {
		levelAsListsGrid[x1][y1] = new ArrayList<List<Integer>>();
		// Make totally empty room
		for(int y = 0; y < ZeldaVGLCUtil.ZELDA_ROOM_ROWS; y++) {
			ArrayList<Integer> row = new ArrayList<>();
			for(int x = 0; x < ZeldaVGLCUtil.ZELDA_ROOM_COLUMNS; x++) {
				row.add(Tile.FLOOR.getNum());
			}
			levelAsListsGrid[x1][y1].add(row);
		}
		// Set left/right walls
		for(int y = 0; y < ZeldaVGLCUtil.ZELDA_ROOM_ROWS; y++) {
			for(int x = 0; x <= 1; x++) {
				levelAsListsGrid[x1][y1].get(y).set(x,Tile.WALL.getNum());
				levelAsListsGrid[x1][y1].get(y).set(levelAsListsGrid[x1][y1].get(y).size() - 1 - x,Tile.WALL.getNum());
			}
		}
		// Set top/bottom walls
		for(int y = 0; y <= 1; y++) {
			for(int x = 0; x < ZeldaVGLCUtil.ZELDA_ROOM_COLUMNS; x++) {
				levelAsListsGrid[x1][y1].get(y).set(x,Tile.WALL.getNum());
				levelAsListsGrid[x1][y1].get(ZeldaVGLCUtil.ZELDA_ROOM_ROWS-y-1).set(levelAsListsGrid[x1][y1].get(y).size() - 1 - x,Tile.WALL.getNum());
			}
		}
	}
	public static Heuristic<GridAction,ZeldaState> manhattan = new Heuristic<GridAction,ZeldaState>() {

		@Override
		/**
		 * takes in a zeldaState
		 * @param s a zeldaState where the character is now
		 * @return i+j the difference between the point and the goal plus
		 * the x-difference from the goal times the width plus the y-difference from the 
		 * goal times the height
		 */
		public double h(ZeldaState s) {
			Dungeon d = s.getDungeon();
			Point goalPoint = d.getCoords(d.getGoal());
			int gDX = goalPoint.x; //x coordinate of the goal point
			int gDY = goalPoint.y; //y coordinate of the goal point

			int w = s.getDungeon().getLevelWidth(); //level width
			int h = s.getDungeon().getLevelHeight(); //level height

			Point g = d.getGoalPoint(); //goal point
			int gX = g.x; //x coordinate of the goal point
			int gY = g.y; //y coordinate of the goal point
			int i = Math.abs(s.x - gX) + Math.abs(s.y - gY);
			int j = Math.abs(gDX - s.dX) * w + Math.abs(gDY - s.dY) * h;



			return i + j; 
		}
	};
	/**
	 * copies an arrayList to a list
	 * 
	 * @param intLevel the level arrayList to be copied
	 * @return copy the list copied from arrayList intLevel
	 */
	public static <E> List<List<E>> arrayListToList(ArrayList<ArrayList<E>> intLevel) {
		List<List<E>> copy = new LinkedList<>(intLevel);

		return copy;
	}

}

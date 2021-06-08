package edu.southwestern.tasks.gvgai.zelda.level;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.search.Action;
import edu.southwestern.util.search.State;
import me.jakerg.rougelike.Ladder;
import me.jakerg.rougelike.Tile;

public class ZeldaState extends State<ZeldaState.GridAction>{
	
	// Coordinates of agent in room
	public int x;
	public int y;
	// Coordinates of room in dungeon
	public int dX;
	public int dY;
	private int numKeys = 0;
	private boolean hasLadder = false;
	private HashMap<String, Set<String>> unlocked;
	private HashMap<String, Set<String>> bombed;
	private HashMap<String, Set<Point>> keys;
	private Dungeon dungeon;
	public Node currentNode;
	
	public ZeldaState(int x, int y, int numKeys, Dungeon dungeon) {
		this.x = x;
		this.y = y;
		this.numKeys = 0;
		unlocked = new HashMap<>();
		bombed = new HashMap<>();
		keys = new HashMap<>();
		this.dungeon = dungeon;
		currentNode = dungeon.getCurrentlevel();
		Point p = dungeon.getCoords(currentNode);
		this.dX = p.x;
		this.dY = p.y;
	}
	
	public ZeldaState(int x, int y, int numKeys, Dungeon dungeon, String node, boolean hasLadder,
			HashMap<String, Set<String>> unlocked, HashMap<String, Set<String>> bombed, HashMap<String, Set<Point>> keys) {
		this.x = x;
		this.y = y;
		this.numKeys = numKeys;
		this.dungeon = dungeon;
		this.currentNode = dungeon.getNode(node);
		this.unlocked = unlocked;
		this.bombed = bombed;
		this.keys = keys;
		Point p = dungeon.getCoords(node);
		this.dX = p.x;
		this.dY = p.y;
		this.hasLadder = hasLadder;
		pickupItems();
	}

	/**
	 * Copying state but changing location within room
	 * @param state State to copy
	 * @param p Point to change to
	 */
	public ZeldaState(ZeldaState state, Point p) {
		this(p.x,p.y, state.numKeys, state.dungeon, state.currentNode.name, state.hasLadder, null, null, new HashMap<>());
		this.unlocked = this.getNewHashMapString(state.unlocked);
		this.bombed = this.getNewHashMapString(state.bombed);
		this.keys = this.getNewHashMapPoint(state.keys);
		pickupItems();
	}

	/**
	 * Function to pickup items (key or ladder) when zelda state is initialized
	 */
	private void pickupItems() {
		int tileNum = currentNode.level.intLevel.get(y).get(x);
		if(tileNum == Tile.KEY.getNum()) {
			pickUpKey(currentNode.name, new Point(x, y));
		} else if (tileNum == Ladder.INT_CODE) { // Ladder number
			hasLadder = true; // "pickup" ladder
		}
	}

	/**
	 * Get the next state based on the direction, includes going to the next level through hidden and locked doors
	 */
	@Override
	public State<ZeldaState.GridAction> getSuccessor(GridAction a) {
		int newX = x;
		int newY = y;
		String nextRoom = null;
		switch(a.direction) {
		case UP:
			newY -= 1;
			break;
		case DOWN:
			newY += 1;
			break;
		case LEFT:
			newX -= 1;
			break;
		case RIGHT:
			newX += 1;
			break;
		default:
			assert false : "Illegal action! " + a;
		}
		assert newY >= 0;
		assert newX >= 0;
		
		Pair<String, Point> newRoom = dungeon.getNextLevel(currentNode, new Point(newX, newY).toString());
		if(newRoom != null) {
			Tile tile = Tile.findNum(currentNode.level.intLevel.get(newY).get(newX));
			if(tile.equals(Tile.HIDDEN)) {
				if(!bombed.containsKey(currentNode.name))
					bombed.put(currentNode.name, new HashSet<>());
				
				bombed.get(currentNode.name).add(a.direction.toString());
				
				if(!bombed.containsKey(newRoom.t1))
					bombed.put(newRoom.t1, new HashSet<>());
				
				bombed.get(newRoom.t1).add(oppositeDirection(a.direction).toString());
			} else if(tile.equals(Tile.LOCKED_DOOR)) {
				// If we've been through that door dont take off keys
				if(unlocked.containsKey(currentNode.name) && 
						unlocked.get(currentNode.name).contains(a.direction.toString())) {
				} else if(numKeys > 0) {
					// If we havent visited and the number of keys is high enough
					if(!unlocked.containsKey(currentNode.name))
						unlocked.put(currentNode.name, new HashSet<>());
					
					numKeys--;

					unlocked.get(currentNode.name).add(a.direction.toString());

					if(!unlocked.containsKey(newRoom.t1))
						unlocked.put(newRoom.t1, new HashSet<>());
						
					unlocked.get(newRoom.t1).add(oppositeDirection(a.direction).toString());
				} else return null; // No keys and we havent vistied door yet
					
			}

			// Set variables for the agent's new position
			newX = newRoom.t2.x;
			newY = newRoom.t2.y;
			nextRoom = newRoom.t1;
			
		} else {
			nextRoom = currentNode.name;
		}
		
		// Deep copy of hashmaps
		HashMap<String, Set<String>> newUnlocked = getNewHashMapString(unlocked);
		HashMap<String, Set<String>> newBombed = getNewHashMapString(bombed);
		HashMap<String, Set<Point>> newKeys = getNewHashMapPoint(keys);
		
		ZeldaState zs = new ZeldaState(newX, newY, numKeys, dungeon, nextRoom, 
				hasLadder, newUnlocked, newBombed, newKeys);
		
//		if(true) System.exit(0);
		
		return zs;
	}

	/**
	 * Deep copy of a hashmap w the key being a string and the value being a set of points
	 * @param obj Hashmap with string key and set of points as values
	 * @return Copy of obj
	 */
	private HashMap<String, Set<Point>> getNewHashMapPoint(HashMap<String, Set<Point>> obj) {
		HashMap<String, Set<Point>> newObj = new HashMap<>();
		
		for(String name : obj.keySet()) {
			Set<Point> newSet = new HashSet<>();
			for(Point p : obj.get(name)) {
				newSet.add(p);
			}
			newObj.put(name, newSet);
		}
		
		return newObj;
	}
	
	/**
	 * Deep copy of a hashmap w the key being a string and the value being a set of strings
	 * @param obj Hashmap with string key and set of strings as values
	 * @return Copy of obj
	 */
	private HashMap<String, Set<String>> getNewHashMapString(HashMap<String, Set<String>> obj) {
		HashMap<String, Set<String>> newObj = new HashMap<>();
		
		for(String name : obj.keySet()) {
			Set<String> newSet = new HashSet<>();
			for(String s : obj.get(name)) {
				newSet.add(s);
			}
			newObj.put(name, newSet);
		}
		
		return newObj;
	}

	/**
	 * Pick up the key if we haven't done so already and increment keys
	 * @param name Room name of the key
	 * @param point X, Y coordinates of key in the room
	 */
	private void pickUpKey(String name, Point point) {
		if(!keys.containsKey(name))
			keys.put(name, new HashSet<>());
		
		if(!keys.get(name).contains(point)) {
			numKeys++;
			keys.get(name).add(point);
		}

	}

	/**
	 * Get the opposite direction (UP -> DOWN) based on direction
	 * @param direction Direction enum
	 * @return Opposite direction of direction
	 */
	private Object oppositeDirection(GridAction.DIRECTION direction) {
		switch(direction) {
		case UP:
			return GridAction.DIRECTION.DOWN;
		case DOWN:
			return GridAction.DIRECTION.UP;
		case LEFT:
			return GridAction.DIRECTION.RIGHT;
		case RIGHT:
			return GridAction.DIRECTION.LEFT;
		default:
			throw new IllegalArgumentException("Not a valid direction: "+ direction);
		}
	}

	/**
	 * Get legal actions based off of all four directions
	 */
	@Override
	public ArrayList<GridAction> getLegalActions(State<GridAction> s) {
		ArrayList<GridAction> legal = new ArrayList<GridAction>();
		for(GridAction.DIRECTION a : GridAction.DIRECTION.values()) {
			GridAction possible = new GridAction(a);
			ZeldaState result = (ZeldaState) getSuccessor(possible);
			if(result == null) continue;
			if(result != null) {
				List<List<Integer>> level = result.currentNode.level.intLevel;
				if(result.x >= 0 && result.x < level.get(0).size() && result.y >= 0 && result.y < level.size()) {
					Tile tile = Tile.findNum(level.get(y).get(x));
					Tile nextTile = Tile.findNum(level.get(result.y).get(result.x));
					if(nextTile.playerPassable() && !nextTile.isMovable()) {
						legal.add(possible);
					}
					else if (nextTile.equals(Tile.WATER) && hasLadder && !tile.equals(Tile.WATER))
						legal.add(possible);
				
				}
				

			}
				
		}
		return legal;
	}

	@Override
	public boolean isGoal() {
		// Getting the triforce is the goal
		return currentNode.level.intLevel.get(y).get(x).equals(Tile.TRIFORCE.getNum());
	}

	@Override
	public double stepCost(State<GridAction> s, GridAction a) {
		return 1; // Each cost is 1 in a grid
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bombed == null) ? 0 : bombed.hashCode());
		result = prime * result + ((currentNode == null) ? 0 : currentNode.hashCode());
		result = prime * result + dX;
		result = prime * result + dY;
		result = prime * result + ((dungeon == null) ? 0 : dungeon.hashCode());
		result = prime * result + ((keys == null) ? 0 : keys.hashCode());
		result = prime * result + numKeys;
		result = prime * result + ((unlocked == null) ? 0 : unlocked.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZeldaState other = (ZeldaState) obj;
		if (bombed == null) {
			if (other.bombed != null)
				return false;
		} else if (!bombed.equals(other.bombed))
			return false;
		if (currentNode == null) {
			if (other.currentNode != null)
				return false;
		} else if (currentNode.name != other.currentNode.name)
			return false;
		if (dX != other.dX)
			return false;
		if (dY != other.dY)
			return false;
		if (keys == null) {
			if (other.keys != null)
				return false;
		} else if (!keys.equals(other.keys))
			return false;
		if (numKeys != other.numKeys)
			return false;
		if (unlocked == null) {
			if (other.unlocked != null)
				return false;
		} else if (!unlocked.equals(other.unlocked))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (hasLadder != other.hasLadder)
			return false;
		return true;
	}


	public static class GridAction implements Action {
		public enum DIRECTION {UP, DOWN, LEFT, RIGHT};
		private DIRECTION direction;
		public GridAction(DIRECTION d) {
			this.direction = d;
		}
		
		public DIRECTION getD() {
			return direction;
		}
		
		/**
		 * Needed to verify results
		 */
		public boolean equals(Object other) {
			if(other instanceof GridAction) {
				return ((GridAction) other).direction.equals(this.direction);
			}
			return false;
		}
		
		public String toString() {
			return direction.toString();
		}
	}
	
	public String toString() {
		return "Level: " + currentNode.name  + " (" + x + ", " + y + ")" + 
				numKeys + "x Keys;";
	}

	public Dungeon getDungeon() {
		return dungeon;
	}

}

package edu.southwestern.tasks.gvgai.zelda.level;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.tasks.gvgai.zelda.level.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction.DIRECTION;
import edu.southwestern.util.search.Action;
import edu.southwestern.util.search.State;
import edu.southwestern.util.datastructures.Pair;
import me.jakerg.rougelike.Tile;

public class ZeldaState extends State<ZeldaState.GridAction>{
	
	public int x;
	public int y;
	public int dX;
	public int dY;
	private int numKeys = 0;
	private int numBombs = 0;
	private HashMap<String, List<String>> unlocked;
	private HashMap<String, List<String>> bombed;
	private Dungeon dungeon;
	private Node currentNode;
	
	public ZeldaState(int x, int y, int numKeys, int numBombs, Dungeon dungeon) {
		this.x = x;
		this.y = y;
		this.numBombs = 999999;
		this.numKeys = 0;
		unlocked = new HashMap<>();
		bombed = new HashMap<>();
		this.dungeon = dungeon;
		currentNode = dungeon.getCurrentlevel();
	}
	
	public ZeldaState(int x, int y, int numKeys, int numBombs, Dungeon dungeon, String node,
			HashMap<String, List<String>> unlocked, HashMap<String, List<String>> bombed) {
		this.x = x;
		this.y = y;
		this.numKeys = numKeys;
		this.numBombs = numBombs;
		this.dungeon = dungeon;
		this.currentNode = dungeon.getNode(node);
		this.unlocked = unlocked;
		this.bombed = bombed;
		Point p = dungeon.getCoords(node);
		this.dX = p.x;
		this.dY = p.y;
	}

	@Override
	public State<ZeldaState.GridAction> getSuccessor(GridAction a) {
		int newX = x;
		int newY = y;
		switch(a.direction) {
		case UP:
			newY -= 1;
		case DOWN:
			newY += 1;
		case LEFT:
			newX -= 1;
		case RIGHT:
			newX += 1;
		}
		
		Pair<String, Point> newRoom = dungeon.getNextLevel(currentNode, new Point(newX, newY).toString());
		if(newRoom != null) {
			Tile tile = Tile.findNum(currentNode.level.intLevel.get(newY).get(newX));
			if(tile.equals(Tile.HIDDEN)) {
				if(!bombed.containsKey(currentNode.name))
					bombed.put(currentNode.name, new LinkedList<>());
				
				if(!bombed.get(currentNode.name).contains(a.direction.toString())) {
					if(numBombs < 0) return null;
					
					numBombs--;
					
					bombed.get(currentNode.name).add(a.direction.toString());
					
					if(!bombed.containsKey(newRoom.t1))
						bombed.put(newRoom.t1, new LinkedList<>());
					
					if(!bombed.get(newRoom.t1).contains(oppositeDirection(a.direction).toString()))
						bombed.get(newRoom.t1).add(oppositeDirection(a.direction).toString());
				}
				
			} else if(tile.equals(Tile.LOCKED_DOOR)) {
				if(!unlocked.containsKey(currentNode.name))
					unlocked.put(currentNode.name, new LinkedList<>());
				
				if(!unlocked.get(currentNode.name).contains(a.direction.toString())) {
					if(numKeys < 0) return null;
					numKeys--;

					unlocked.get(currentNode.name).add(a.direction.toString());

					if(!unlocked.containsKey(newRoom.t1))
						unlocked.put(newRoom.t1, new LinkedList<>());
					
					if(!unlocked.get(newRoom.t1).contains(oppositeDirection(a.direction).toString()))
						unlocked.get(newRoom.t1).add(oppositeDirection(a.direction).toString());
				}

			}
			if(dungeon.getNode(newRoom.t1).level.intLevel.get(newRoom.t2.y).get(newRoom.t2.x).equals(Tile.KEY.getNum()))
				numKeys++;
			return new ZeldaState(newRoom.t2.x, newRoom.t2.y, numKeys, numBombs, dungeon, newRoom.t1, unlocked, bombed);
		} else {
			if(currentNode.level.intLevel.get(newY).get(newX).equals(Tile.KEY.getNum()))
				numKeys++;
			return new ZeldaState(newX, newY, numKeys, numBombs, dungeon, currentNode.name, unlocked, bombed);
		}
			
		
	}

	private Object oppositeDirection(DIRECTION direction) {
		switch(direction) {
		case UP:
			return DIRECTION.DOWN;
		case DOWN:
			return DIRECTION.UP;
		case LEFT:
			return DIRECTION.RIGHT;
		case RIGHT:
			return DIRECTION.LEFT;
		default:
			throw new IllegalArgumentException("Not a valid direction: "+ direction);
		}
	}

	@Override
	public ArrayList<GridAction> getLegalActions(State<GridAction> s) {
		ArrayList<GridAction> legal = new ArrayList<GridAction>();
		for(DIRECTION a : DIRECTION.values()) {
			GridAction possible = new GridAction(a);
			ZeldaState result = (ZeldaState) getSuccessor(possible);
			if(result == null) continue;
			List<List<Integer>> level = result.currentNode.level.intLevel;
			if(result.x >= 0 && result.x < level.get(0).size() && result.y >= 0 && result.y < level.size())
				if(Tile.findNum(level.get(result.y).get(result.x)).playerPassable())
					legal.add(possible);
				
		}
		return legal;
	}

	@Override
	public boolean isGoal() {
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
		result = prime * result + dX;
		result = prime * result + dY;
		result = prime * result + numBombs;
		result = prime * result + numKeys;
		result = prime * result + ((unlocked == null) ? 0 : unlocked.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ZeldaState)) {
			return false;
		}
		ZeldaState other = (ZeldaState) obj;
		if (bombed == null) {
			if (other.bombed != null) {
				return false;
			}
		} else if (!bombed.equals(other.bombed)) {
			return false;
		}
		if (dX != other.dX) {
			return false;
		}
		if (dY != other.dY) {
			return false;
		}
		if (numBombs != other.numBombs) {
			return false;
		}
		if (numKeys != other.numKeys) {
			return false;
		}
		if (unlocked == null) {
			if (other.unlocked != null) {
				return false;
			}
		} else if (!unlocked.equals(other.unlocked)) {
			return false;
		}
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}


	public static class GridAction implements Action {
		public enum DIRECTION {UP, DOWN, LEFT, RIGHT}
		private DIRECTION direction;;
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
	}

}

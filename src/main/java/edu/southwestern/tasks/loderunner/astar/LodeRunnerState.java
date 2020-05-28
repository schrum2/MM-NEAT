package edu.southwestern.tasks.loderunner.astar;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.loderunner.LodeRunnerVGLCUtil;
import edu.southwestern.tasks.mario.level.MarioState;
import edu.southwestern.tasks.mario.level.MarioState.MarioAction;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Action;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.Search;
import edu.southwestern.util.search.State;

public class LodeRunnerState extends State<LodeRunnerState.LodeRunnerAction>{
	private List<List<Integer>> level;
	private HashSet<Point> goldLeft; //set containing the points with gold 
	public int currentX; 
	public int currentY;
	
	public static void main(String args[]) {
		List<List<Integer>> level = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevel(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH+"Level 1.txt"); //converts to JSON
		//System.out.println(level);
		HashSet<Point> gold = fillGold(level);
		//System.out.println(gold);
		LodeRunnerState start = new LodeRunnerState(level,gold,1,29);
		int tile = level.get(1).get(29);
		System.out.println(tile);
		Search<LodeRunnerAction,LodeRunnerState> search = new AStarSearch<>(LodeRunnerState.manhattanToFarthestGold);
		HashSet<LodeRunnerState> mostRecentVisited = null;
		ArrayList<LodeRunnerAction> actionSequence = null;
		try {
			actionSequence = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).search(start);
		} catch(Exception e) {
			System.out.println("no search");
		}
		mostRecentVisited = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).getVisited();
		System.out.println(mostRecentVisited);
		System.out.println("actionSequence: " + actionSequence);
	}

	private static HashSet<Point> fillGold(List<List<Integer>> level) {
		HashSet<Point> gold = new HashSet<>();
		int tile = -1; 
		for(int i = 0; i < level.size(); i++) {
			for(int j = 0; j < level.get(i).size(); j++) {
				tile = level.get(i).get(j);
				//System.out.println("The tile at " + j + "," + i + " = " +tile);
				if(tile == 1) {
					gold.add(new Point(j,i));
				}
				
			}
		}
		return gold;
	}
	
	//may add a way to track the enemies in the future, but we are using a simple version right now 
	public LodeRunnerState(List<List<Integer>> level, HashSet<Point> goldLeft, int currentX, int currentY) {
		this.level = level;
		this.goldLeft = goldLeft;
		this.currentX = currentX;
		this.currentY = currentY;
	}
	
	/**
	 * Defines the Actions that can be used by a player for Lode Runner 
	 * @author kdste
	 *
	 */
	public static class LodeRunnerAction implements Action{
		public enum MOVE {RIGHT,LEFT,UP,DOWN,DIG_LEFT,DIG_RIGHT,NOTHING}
		private MOVE movement;
		
		public LodeRunnerAction(MOVE m) {
			this.movement = m;
		}
		
		/**
		 * Gets the current action 
		 * @return The current action 
		 */
		public MOVE getMove() {
			return movement;
		}
		
		/**
		 * Checks if the current action is equal to the parameter
		 * @return True if they are equal, false otherwise 
		 */
		public boolean equals(Object other) {
			if(other instanceof LodeRunnerAction) {
				return ((LodeRunnerAction) other).movement.equals(this.movement); 
			}
			return false;
		}
		
		/**
		 * @return String representation of the action
		 */
		public String toString() {
			return movement.toString();
		}
	}
	
	public static Heuristic<LodeRunnerAction,LodeRunnerState> manhattanToFarthestGold = new Heuristic<LodeRunnerAction,LodeRunnerState>(){

		/**
		 * Calculates the Manhattan distance from the player to the farthest gold coin
		 * @return Manhattan distance from play to farthest coin 
		 */
		@Override
		public double h(LodeRunnerState s) {
			int maxDistance = 0;
			HashSet<Point> goldLeft = s.goldLeft;
			for(Point p: goldLeft) {
				int xDistance = Math.abs(s.currentX - p.x);
				int yDistance = Math.abs(s.currentY - p.y);
				Math.max(maxDistance, (xDistance+yDistance));
			}
			return maxDistance;
		}
		
	};

	/**
	 * Gets the next state from the current state
	 * @return The next state, if null the state was not legal at that time 
	 */
	@Override
	public State<LodeRunnerAction> getSuccessor(LodeRunnerAction a) {
		int newX = currentX;
		int newY = currentY;
		
		if(a.getMove().equals(LodeRunnerAction.MOVE.RIGHT)) {
			if(passable(newX+1, newY)) {
				newX++;
//				if(tileAtPosition(newX, newY)==1) { //if the tile at the new position is gold, then it removes it from the set 
//					Point gold = new Point(newX, newY);
//					goldLeft.remove(gold);
//				}
			} else if(currentY == newY) return null;
		}
		if(a.getMove().equals(LodeRunnerAction.MOVE.LEFT)) {
			if(passable(newX-1,newY)) {
				newX--;
//				if(tileAtPosition(newX, newY)==1) {//if the tile at the new position is gold, then it removes it from the set 
//					Point gold = new Point(newX, newY);
//					goldLeft.remove(gold);
//				}
			} else if(currentY == newY) return null; 
		}
		if(a.getMove().equals(LodeRunnerAction.MOVE.NOTHING)) {
			//if the tile at the new position is gold, then it removes it from the set
			//you can still collect gold while in free fall 
//			if(tileAtPosition(newX, newY)==1) { 
//				Point gold = new Point(newX, newY);
//				goldLeft.remove(gold);
//			}
			//no new action, if the action is nothing you are most likely in a free fall and have to wait until you hit the ground to make a new action
			return null;  
		}
		if(a.getMove().equals(LodeRunnerAction.MOVE.UP)) {
			if(passable(newX, newY+1)) {
				newY++;
//				if(tileAtPosition(newX, newY)==1) {//if the tile at the new position is gold, then it removes it from the set 
//					Point gold = new Point(newX, newY);
//					goldLeft.remove(gold);
//				}
			}else if(currentX == newX) return null; 
		}
		if(a.getMove().equals(LodeRunnerAction.MOVE.DOWN)) {
			if(passable(newX, newY-1)) {
				newY--;
//				if(tileAtPosition(newX, newY)==1) {//if the tile at the new position is gold, then it removes it from the set
//					Point gold = new Point(newX, newY);
//					goldLeft.remove(gold);
//				}
			}else if(currentY == newY) return null;
		}
		if(a.getMove().equals(LodeRunnerAction.MOVE.DIG_LEFT)) {
			int tile = tileAtPosition(newX-1,newY-1); //tile down and to the left 
			//if the tile is diggable ground than it becomes an empty space 
			if(tile == 3) {
				tile = 0;
			} else return null; 
		}
		if(a.getMove().equals(LodeRunnerAction.MOVE.DIG_RIGHT)) {
			int tile = tileAtPosition(newX+1,newY-1);//tile down and to the right
			//if the tile is diggable ground than it becomes an empty space 
			if(tile == 3) {
				tile = 0;
			} else return null;
		}
		if(tileAtPosition(newX, newY)==1) { //if the tile at the new position is gold, then it removes it from the set 
			Point gold = new Point(newX, newY);
			goldLeft.remove(gold);
		}
		return new LodeRunnerState(level, goldLeft, newX, newY);
	}

	/**
	 * Gets a list of all of the lode runner actions
	 * @return List of valid lode runner actions
	 */
	@Override
	public ArrayList<LodeRunnerAction> getLegalActions(State<LodeRunnerAction> s) {
		ArrayList<LodeRunnerAction> vaildActions = new ArrayList<>();
		for(LodeRunnerAction.MOVE move: LodeRunnerAction.MOVE.values()) {
			if(s.getSuccessor(new LodeRunnerAction(move)) != null) {
				vaildActions.add(new LodeRunnerAction(move));
			}
		}
		return vaildActions;
	}
	
	/**
	 * Determines if a tile is passable or not 
	 * @param x Current X coordinate
	 * @param y Current Y coordinate
	 * @return True if you can pass that tile, false otherwise 
	 */
	public boolean passable(int x, int y) {
		int tile = tileAtPosition(x,y);
		//0 is empty, 1 is gold, 2 is an enemy(simplified version that ignores enemies), 4 is a ladder, 5 is a rope 
		if(inBounds(x,y) && (tile == 0 || tile == 1 || tile == 2 || tile == 4 || tile==5))
			return true;
		
		return false; 
	}
	
	private boolean inBounds(int x, int y) {
		
		return y>=0 && x>=0 && y<level.size() && x<level.get(0).size();
	}

	/**
	 * Easy access to the given tile integer at given (x,y) coordinates.
	 * 
	 * @param x horizontal position 
	 * @param y vertical position 
	 * @return tile int at those coordinates
	 */
	public int tileAtPosition(int x, int y) {
		return level.get(x).get(y);
	}

	/**
	 * Determines if you have won the level
	 * @return True if there is no gold left, false otherwise
	 */
	@Override
	public boolean isGoal() {
		//when the hash set is empty 
		return goldLeft.isEmpty();
	}

	/**
	 * It moves on square at a time 
	 * @return Number of tiles for every action 
	 */
	@Override
	public double stepCost(State<LodeRunnerAction> s, LodeRunnerAction a) {
		return 1;
	}
	
	@Override
	public String toString() {
		return "(" + currentX + ", " + currentY + ")";
	}
	
}

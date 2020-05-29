package edu.southwestern.tasks.loderunner.astar;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.tasks.loderunner.LodeRunnerRenderUtil;
import edu.southwestern.tasks.loderunner.LodeRunnerVGLCUtil;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Action;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.Search;
import edu.southwestern.util.search.State;

public class LodeRunnerState extends State<LodeRunnerState.LodeRunnerAction>{
	public static final int LODE_RUNNER_TILE_EMPTY = 0;
	public static final int LODE_RUNNER_TILE_GOLD = 1;
	public static final int LODE_RUNNER_TILE_ENEMY = 2;
	public static final int LODE_RUNNER_TILE_DIGGABLE = 3;
	public static final int LODE_RUNNER_TILE_LADDER = 4;
	public static final int LODE_RUNNER_TILE_ROPE = 5;
	public static final int LODE_RUNNER_TILE_GROUND = 6;
	public static final int LODE_RUNNER_TILE_SPAWN = 7;
	private List<List<Integer>> level;
	private HashSet<Point> goldLeft; //set containing the points with gold 
	public int currentX; 
	public int currentY;

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
	 * Defines the Actions that can be used by a player for Lode Runner 
	 * @author kdste
	 *
	 */
	public static class LodeRunnerAction implements Action{
		public enum MOVE {RIGHT,LEFT,UP,DOWN}//removed dig up and dig down while testing on level 1
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

	public static void main(String args[]) {
		//6 tile mapping 
		List<List<Integer>> level = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH+"Level 1.txt"); //converts to JSON
		//System.out.println(level);
		//HashSet<Point> gold = fillGold(level);
		//		System.out.println(gold);
		//		System.out.println(level);
		LodeRunnerState start = new LodeRunnerState(level);
		Search<LodeRunnerAction,LodeRunnerState> search = new AStarSearch<>(LodeRunnerState.manhattanToFarthestGold);
		HashSet<LodeRunnerState> mostRecentVisited = null;
		ArrayList<LodeRunnerAction> actionSequence = null;
		try {
			actionSequence = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).search(start, true, 30);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		mostRecentVisited = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).getVisited();
		System.out.println(mostRecentVisited.toString());
		System.out.println("actionSequence: " + actionSequence);
	}

	/**
	 * 
	 * @param level
	 * @return
	 */
	private static HashSet<Point> fillGold(List<List<Integer>> level) {
		HashSet<Point> gold = new HashSet<>();
		int tile = -1; 
		for(int i = 0; i < level.size(); i++) {
			for(int j = 0; j < level.get(i).size(); j++) {
				tile = level.get(i).get(j);
				//System.out.println("The tile at " + j + "," + i + " = " +tile);
				if(tile == LODE_RUNNER_TILE_GOLD) { 
					gold.add(new Point(j,i));//saves reference to that gold in the 
					level.get(i).set(j, LODE_RUNNER_TILE_EMPTY);//removes gold and places an empty tile 
				}

			}
		}
		return gold;
	}

	/**
	 * 
	 * @param level
	 * @return
	 */
	private static Point getSpawnFromVGLC(List<List<Integer>> level) {
		Point start = new Point();
		int tile = -1;
		for(int i = 0; i < level.size(); i++) {
			for(int j = 0; j < level.get(i).size(); j++){
				tile = level.get(i).get(j);
				//System.out.println("The tile at " + j + "," + i + " = " +tile);
				if(tile == LODE_RUNNER_TILE_SPAWN) {//7 maps to spawn point  
					start = new Point(j, i);
					level.get(i).set(j, LODE_RUNNER_TILE_EMPTY);//removes gold and places an empty tile 
				}
			}
		}
		return start;
	}

	/**
	 * 
	 * @param level
	 */
	public LodeRunnerState(List<List<Integer>> level) {
		this(level, getSpawnFromVGLC(level));
	}

	/**
	 * 
	 * @param level
	 * @param start
	 */
	public LodeRunnerState(List<List<Integer>> level, Point start) {
		this(level, getGoldLeft(level), start.x, start.y);
	}

	/**
	 * 
	 * @param level
	 * @return
	 */
	private static HashSet<Point> getGoldLeft(List<List<Integer>> level) {
		HashSet<Point> gold = fillGold(level);
		return gold;
	}


	/**
	 * 
	 * may add a way to track the enemies in the future, but we are using a simple version right now 
	 * @param level
	 * @param goldLeft
	 * @param currentX
	 * @param currentY
	 */
	private LodeRunnerState(List<List<Integer>> level, HashSet<Point> goldLeft, int currentX, int currentY) {
		this.level = level;
		this.goldLeft = goldLeft;
		this.currentX = currentX;
		this.currentY = currentY;
	}

	/**
	 * Gets the next state from the current state
	 * @return The next state, if null the state was not legal at that time 
	 */
	@Override
	public State<LodeRunnerAction> getSuccessor(LodeRunnerAction a) {
		int newX = currentX;
		int newY = currentY;
		if(a.getMove().equals(LodeRunnerAction.MOVE.RIGHT)) {
			if(tileAtPosition(newX,newY+1) == LODE_RUNNER_TILE_DIGGABLE ||
					tileAtPosition(newX,newY+1) == LODE_RUNNER_TILE_GROUND)//checks if there is ground under the player
				return null;//fall down 
			else if(passable(newX+1, newY)) {
				//System.out.println("right");
				newX++;
			} else return null; 
		}
		else if(a.getMove().equals(LodeRunnerAction.MOVE.LEFT)) {
			if(tileAtPosition(newX,newY+1) == LODE_RUNNER_TILE_DIGGABLE ||
					tileAtPosition(newX,newY+1) == LODE_RUNNER_TILE_GROUND)//checks if there is ground under the player
				return null;//fall down 
			else if(passable(newX-1,newY)) {
				//System.out.println("left");
				newX--;
			} else return null; 
		}
		//		if(a.getMove().equals(LodeRunnerAction.MOVE.NOTHING)) {
		//			//if the tile at the new position is gold, then it removes it from the set
		//			//you can still collect gold while in free fall 
		//			//no new action, if the action is nothing you are most likely in a free fall and have to wait until you hit the ground to make a new action
		//			return null;  
		//		}
		else if(a.getMove().equals(LodeRunnerAction.MOVE.UP)) {
			if(tileAtPosition(newX, newY-1)==LODE_RUNNER_TILE_LADDER) {
				//System.out.println("up");
				newY--;
			} else return null; 
		}
		else if(a.getMove().equals(LodeRunnerAction.MOVE.DOWN)) {

			if(tileAtPosition(newX,newY+1) == LODE_RUNNER_TILE_DIGGABLE ||
					tileAtPosition(newX,newY+1) == LODE_RUNNER_TILE_GROUND) {
				//System.out.println("down");
				newY++;
			} else return null;
		}
		//have these two actions return null while testing on level one because it doesn't require digging to win 
		//		else if(a.getMove().equals(LodeRunnerAction.MOVE.DIG_LEFT)) {
		////			int tile = tileAtPosition(newX-1,newY-1); //tile down and to the left 
		////			//if the tile is diggable ground than it becomes an empty space 
		////			if(tile == 3) {
		////				tile = 0;
		////			} else
		//			return null; 
		//		}
		//		else if(a.getMove().equals(LodeRunnerAction.MOVE.DIG_RIGHT)) {
		////			int tile = tileAtPosition(newX+1,newY-1);//tile down and to the right
		////			//if the tile is diggable ground than it becomes an empty space 
		////			if(tile == 3) {
		////				tile = 0;
		////			} else 
		//			return null;
		//		}
		//check if it is in teh set, then create a new set that contains all but that one
		HashSet<Point> newGoldLeft = new HashSet<>();
		for(Point p : goldLeft) {
			if(!p.equals(new Point(newX, newY))){
				newGoldLeft.add(p);
			}
		}
		//System.out.println(newGoldLeft);
		//System.out.println(goldLeft);
		//		System.out.println(newX);
		//		System.out.println(newY);
		return new LodeRunnerState(level, newGoldLeft, newX, newY);
	}

	/**
	 * Gets a list of all of the lode runner actions
	 * @return List of valid lode runner actions
	 */
	@Override
	public ArrayList<LodeRunnerAction> getLegalActions(State<LodeRunnerAction> s) {
		ArrayList<LodeRunnerAction> vaildActions = new ArrayList<>();
		//System.out.println(level);
		for(LodeRunnerAction.MOVE move: LodeRunnerAction.MOVE.values()) {
			//Everything besides the if statement is for debugging purposes, delete later 
			LodeRunnerAction a = new LodeRunnerAction(move);
			System.out.println(s+"\t"+move+"\t"+s.getSuccessor(a));
			try {
				LodeRunnerState theState = ((LodeRunnerState) s);
				List<List<Integer>> copy = ListUtil.deepCopyListOfLists(theState.level );
				copy.get(theState.currentY).set(theState.currentX, LODE_RUNNER_TILE_SPAWN); 
				for(Point t : theState.goldLeft) {
					copy.get(t.y).set(t.x, LODE_RUNNER_TILE_GOLD); 
				}
				LodeRunnerRenderUtil.getBufferedImage(copy);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MiscUtil.waitForReadStringAndEnterKeyPress();
			if(s.getSuccessor(new LodeRunnerAction(move)) != null) {
				vaildActions.add(new LodeRunnerAction(move));
			}
		}
		//System.out.println(vaildActions);
		
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
		if(inBounds(x,y) && (tile==LODE_RUNNER_TILE_EMPTY || tile==LODE_RUNNER_TILE_ENEMY || 
				tile==LODE_RUNNER_TILE_LADDER || tile==LODE_RUNNER_TILE_ROPE)) {
			return true;
		}
		return false; 
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
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

	/**
	 * 
	 */
	@Override
	public String toString() {
		return "Size: " + goldLeft.size() + " (" + currentX + ", " + currentY + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + currentX;
		result = prime * result + currentY;
		result = prime * result + ((goldLeft == null) ? 0 : goldLeft.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof LodeRunnerState))
			return false;
		LodeRunnerState other = (LodeRunnerState) obj;
		if (currentX != other.currentX)
			return false;
		if (currentY != other.currentY)
			return false;
		if (goldLeft == null) {
			if (other.goldLeft != null)
				return false;
		} else if (!goldLeft.equals(other.goldLeft))
			return false;
		return true;
	}
	
	

}

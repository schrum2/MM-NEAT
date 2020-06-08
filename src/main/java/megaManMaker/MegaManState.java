package megaManMaker;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.util.search.Action;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.State;

public class MegaManState extends State<MegaManState.MegaManAction>{
	public static final int MEGA_MAN_TILE_EMPTY = 0;
	public static final int MEGA_MAN_TILE_GROUND = 1;
	public static final int MEGA_MAN_TILE_LADDER = 2;
	public static final int MEGA_MAN_TILE_HAZARD = 3;
	public static final int MEGA_MAN_TILE_BREAKABLE = 4;
	public static final int MEGA_MAN_TILE_MOVING_PLATFORM = 5;
	public static final int MEGA_MAN_TILE_CANNON = 6;
	public static final int MEGA_MAN_TILE_ORB = 7;
	public static final int MEGA_MAN_TILE_NULL = 17;
	public static final int MEGA_MAN_TILE_SPAWN = 11;
	
	
	private List<List<Integer>> level;
	private Point orb; 
	//private HashSet<Point> dugHoles; // Too expensive to track the dug up spaces in the state. Just allow the agent to move downward through diggable blocks
	public int currentX; 
	public int currentY;
	private int jumpVelocity;

	public static Heuristic<MegaManAction,MegaManState> manhattanToOrb = new Heuristic<MegaManAction,MegaManState>(){

		@Override
		public double h(MegaManState s) {
			int maxDistance = 0;
			Point orb = s.orb;
			int xDistance = Math.abs(s.currentX - orb.x);
			int yDistance = Math.abs(s.currentY - orb.y);
			Math.max(maxDistance, (xDistance+yDistance));
			return maxDistance;
		}
	
	};

	
	
	public  static class MegaManAction implements Action{
		public enum MOVE {RIGHT,LEFT,UP,DOWN, JUMP}; //, DIG_RIGHT, DIG_LEFT} // Too computationally expensive to model digging as actions that change the state
		private MOVE movement;
		/**
		 * Constructor for a Mega Man action
		 * @param m A move the player can make 
		 */
		public MegaManAction(MOVE m) {
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
			if(other instanceof MegaManAction) {
				return ((MegaManAction) other).movement.equals(this.movement); 
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
	
	/**
	 * Fills a set with points, to keep a reference of where the gold is in the level
	 * then removes them and makes them emtpy spaces 
	 * @param level A level 
	 * @return Set of points 
	 */
	private static Point getOrb(List<List<Integer>> level) {
		Point orb = new Point();
		int tile; 
		for(int i = 0; i < level.size(); i++) {
			for(int j = 0; j < level.get(i).size(); j++) {
				tile = level.get(i).get(j);
				if(tile == MEGA_MAN_TILE_ORB) { 
					orb.setLocation(j,i);
					break;
				}

			}
		}
		return orb;
	}
	/**
	 * Constructor that only takes a level, 
	 * this makes it so that it grabs the original spawn point and fills the gold set with
	 * the locations of the gold for that level 
	 * @param level A level in JSON form 
	 */
	public MegaManState(List<List<Integer>> level) {
		this(level, getSpawnFromVGLC(level));
	}
	/**
	 * Constructor that takes a level and a start point. 
	 * This construct is can be used to specify a starting point for easier testing 
	 * @param level Level in JSON form 
	 * @param start The spawn point 
	 */
	public MegaManState(List<List<Integer>> level, Point start) {
		this(level, getJumpVelocity(), getOrb(level), start.x, start.y);
	}
	private static int getJumpVelocity() {
		return 0;
	}
	public MegaManState(List<List<Integer>> level, int jumpVelocity, Point orb, int currentX, int currentY) {
		this.level = level;
		this.orb = orb;
		this.jumpVelocity =jumpVelocity;
		this.currentX = currentX;
		this.currentY = currentY;
	}

	@Override
	public State<MegaManAction> getSuccessor(MegaManAction a) {
		int newJumpVelocity = jumpVelocity;
		int newX = currentX;
		int newY = currentY;

		// Falling off bottom of screen (into a gap). No successor (death)
		if(!inBounds(currentX,currentY+1)) return null;
		
		if(newJumpVelocity == 0) { // Not mid-Jump
			//int beneath = tileAtPosition(newX,newY+1);
			if(passable(newX,newY+1)) { // Falling
				newY++; // Fall down
			} else if(a.getMove().equals(MegaManAction.MOVE.JUMP)) { // Start jump
				newJumpVelocity = 3; // Accelerate up
			} 
		} else if(a.getMove().equals(MegaManAction.MOVE.JUMP)) {
			return null; // Can't jump mid-jump. Reduces search space.
		}

		if(newJumpVelocity > 0) { // Jumping up
			if(passable(newX,newY-1)||tileAtPosition(newX, newY+1)==MEGA_MAN_TILE_MOVING_PLATFORM) {
				newY--; // Jump up
				newJumpVelocity--; // decelerate
			} else {
				newJumpVelocity = 0; // Can't jump if blocked above
			}
			// TODO: Add breakable case
		}

		// Right movement
		if(a.getMove().equals(MegaManAction.MOVE.RIGHT)) {
			if(passable(newX+1,newY)) {
				newX++;
			} else if(currentY == newY) { // vertical position did not change
				// This action does not change the state. Neither jumping up nor falling down, and could not move right, so there is no NEW state to go to
				return null;
			}
		}

		// Left movement
		if(a.getMove().equals(MegaManAction.MOVE.LEFT)) {
			if(passable(newX-1,newY)) {
				newX--;
			} else if(currentY == newY) { // vertical position did not change
				// This action does not change the state. Neither jumping up nor falling down, and could not move left, so there is no NEW state to go to
				return null;
			}
		}
		//up movement (on ladder)
		if(a.getMove().equals(MegaManAction.MOVE.UP)) {
			if(inBounds(newX, newY-1) && tileAtPosition(newX, newY)==MEGA_MAN_TILE_LADDER) 
				newY--;
			else return null; 
		}
		//down movement(on ladder)
		if(a.getMove().equals(MegaManAction.MOVE.DOWN)) {
			if(inBounds(newX, newY+1) && tileAtPosition(newX,newY+1) != MEGA_MAN_TILE_GROUND) 
					newY++;
			else return null;
		}
		
		if(!inBounds(newX, newY)){
			return null;
		}
		return new MegaManState(level, newJumpVelocity, orb, newX, newY);
	}

	private boolean passable(int x, int y) {
		if(!inBounds(x,y)) return false; // fail for bad bounds before tileAtPosition check
		int tile = tileAtPosition(x,y);
		if((	tile==MEGA_MAN_TILE_EMPTY  || 
				tile==MEGA_MAN_TILE_LADDER)) {
			return true;
		}
		return false; 
	}

	private boolean inBounds(int currentX2, int i) {
		// TODO Auto-generated method stub
		return level.get(i).get(currentX2)!=7;
	}
	
	/**
	 * Loops through the level to find the spawn point from the original level 
	 * @param level
	 * @return The spawn point 
	 */
	private static Point getSpawnFromVGLC(List<List<Integer>> level) {
		Point start = new Point();
		int tile;
		boolean done = false;
		for(int i = 0; !done && i < level.size(); i++) {
			for(int j = 0; !done && j < level.get(i).size(); j++){
				tile = level.get(i).get(j);
				//System.out.println("The tile at " + j + "," + i + " = " +tile);
				if(tile == MEGA_MAN_TILE_SPAWN) {//7 maps to spawn point  
					start = new Point(j, i);
					level.get(i).set(j, MEGA_MAN_TILE_EMPTY);//removes spawn point and places an empty tile 
					done = true;
				}
			}
		}
		return start;
	}
	/**
	 * Easy access to the given tile integer at given (x,y) coordinates.
	 * 
	 * @param x horizontal position 
	 * @param y vertical position 
	 * @return tile int at those coordinates
	 */
	public int tileAtPosition(int x, int y) {
		return level.get(y).get(x);
	}
	@Override
	public ArrayList<MegaManAction> getLegalActions(State<MegaManAction> s) {
		ArrayList<MegaManAction> vaildActions = new ArrayList<>();
		for(MegaManAction.MOVE move: MegaManAction.MOVE.values()) {
			if(s.getSuccessor(new MegaManAction(move)) != null) {
				vaildActions.add(new MegaManAction(move));
			}
		}
		return vaildActions;
	}

	@Override
	public boolean isGoal() {
		// TODO Auto-generated method stub
		return currentX==orb.x&&currentY==orb.y;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + jumpVelocity;
		result = prime * result + currentX;
		result = prime * result + currentX;
		return result;
	}
	@Override
	public double stepCost(State<MegaManAction> s, MegaManAction a) {
		return 1;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MegaManState other = (MegaManState) obj;
		if (currentX != other.currentX)
			return false;
		if (currentY != other.currentY)
			return false;
		if (jumpVelocity != other.jumpVelocity)
			return false;
		if (orb == null) {
			if (other.orb != null)
				return false;
		} else if (!orb.equals(other.orb))
			return false;
		return true;
	}
	
	@Override
	public String toString(){
		return "("+currentX + "," + currentY +")";		

	}
}

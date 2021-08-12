package edu.southwestern.tasks.mario.level;

import static edu.southwestern.tasks.mario.level.LevelParser.BUFFER_WIDTH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.southwestern.util.search.Action;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.State;

public class MarioState extends State<MarioState.MarioAction> {

	public static Heuristic<MarioAction,MarioState> moveRight = new Heuristic<MarioAction,MarioState>() {
		@Override
		public double h(MarioState s) {
			return s.level.get(0).size() - s.marioX;
		}
	};

	/**
	 * Defines the actions that can be used in Mario by a player
	 *
	 */
	public static class MarioAction implements Action {
		public enum DIRECTION {JUMP, LEFT, RIGHT};
		private DIRECTION direction;
		public MarioAction(DIRECTION d) {
			this.direction = d;
		}

		/**
		 * Gets current action
		 * @return Current action
		 */
		public DIRECTION getD() {
			return direction;
		}

		/**
		 * determines if two actions are equal 
		 * @return True if the current action is equal to the parameter 
		 */
		public boolean equals(Object other) {
			if(other instanceof MarioAction) {
				return ((MarioAction) other).direction.equals(this.direction);
			}
			return false;
		}

		/**
		 * @return String representation of the action
		 */
		public String toString() {
			return direction.toString();
		}
	}

	private ArrayList<List<Integer>> level;
	private int jumpVelocity;
	public int marioX; // Should use getters for these instead
	public int marioY;

	/**
	 * Constructor for a Mario State 
	 * @param level A Mario Level 
	 * @param jumpVelocity Current jump velocity
	 * @param marioX Current X coordinate
	 * @param marioY Current Y coordinate 
	 */
	public MarioState(ArrayList<List<Integer>> level, int jumpVelocity, int marioX, int marioY) {
		this.level = level;
		//		int height = level.size();
		//		int width = level.get(0).size();
		/*for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				System.out.print(this.tileAtPosition(x, y));
			}
			System.out.print("\n");
		}
		System.out.println();*/
		this.jumpVelocity = jumpVelocity;
		this.marioX = marioX;
		this.marioY = marioY;
	}

	/**
	 * Default constructor for a Mario State
	 * @param level A Mario level 
	 */
	public MarioState(ArrayList<List<Integer>> level) {
		this(level, 0, 0, level.size() - 2);
	}

	/**
	 * Add opening/closing buffer spaces to the level. Also fixes pipes and bullet bills
	 * 
	 * @param level List of lists representation
	 * @return List of lists representation with buffer and fixed elements.
	 */
	public static ArrayList<List<Integer>> preprocessLevel(List<List<Integer>> level){
		int extraStones = BUFFER_WIDTH;
		int height = level.size();
		int width = level.get(0).size();
		ArrayList<List<Integer>> tmpLevel = new ArrayList<>();
		for(int i=0; i<height; i++){
			int tile = 2;
			if(i==height-1){
				tile=0;
			}
			ArrayList<Integer> row = new ArrayList<>(Collections.nCopies(extraStones,tile));
			row.addAll(level.get(i));
			row.addAll(new ArrayList<>(Collections.nCopies(extraStones,tile)));
			tmpLevel.add(i, row);
		}


		for(int y=height-1; y>=0; y--){
			for(int x=width-1; x>=0; x--){
				int tile = level.get(y).get(x);
				if((tile == 8) && (y+1<height && level.get(y+1).get(x) == 2)){
					setTileAtPosition(tmpLevel, x+extraStones, y+1, tile);
					for(int i=y+2; i<height; i++){
						if(level.get(i).get(x)==2){
							setTileAtPosition(tmpLevel, x+extraStones, i, tile);
						}else{
							break;
						}
					}
				}
				if((tile == 6 || tile == 7) && (y+1<height && level.get(y+1).get(x) == 2)){
					setTileAtPosition(tmpLevel, x+extraStones+1, y, tile);
					setTileAtPosition(tmpLevel, x+extraStones, y+1, tile);
					setTileAtPosition(tmpLevel, x+extraStones+1, y+1, tile);
					for(int i=y+2; i<height; i++){
						if(level.get(i).get(x)==2){
							setTileAtPosition(tmpLevel, x+extraStones, i, tile);
							setTileAtPosition(tmpLevel, x+extraStones+1, i, tile);
						}else{
							break;
						}
					}
				}
			}
		}           
		return tmpLevel;
	}


	/**
	 * Easy access to the given tile integer at given (x,y) coordinates.
	 * 
	 * @param x horizontal position from left
	 * @param y vertical position from top
	 * @return tile int at those coordinates
	 */
	private int tileAtPosition(int x, int y) {
		return level.get(y).get(x);
	}

	/***
	 * Sets a tile at a specified position
	 * @param level A Mario level
	 * @param x X coordinate to set the tile 
	 * @param y Y coordinate to set the tile
	 * @param tile The tile to set as an integer 
	 */
	private static void setTileAtPosition(ArrayList<List<Integer>> level, int x, int y, int tile){
		List<Integer> newRow = level.get(y);
		newRow.set(x, tile);
		level.set(y, newRow);
	}


	/**
	 * If the coordinates are inside of the level bounds.
	 *
	 * @param x horizontal position from left
	 * @param y vertical position from top
	 * @return true if in bounds
	 */
	private boolean inBounds(int x, int y) {
		return 0 <= y && y < level.size() && 0 <= x && x < level.get(0).size();
	}

	/**
	 * If the x coordinate is at right edge
	 * @param x horizontal position from left
	 * @return true if off right edge
	 */
	private boolean isGoal(int x) {
		return x == level.get(0).size()-1;
	}

	/**
	 * A position is passable if it is not a blockage, and does not contain an enemy
	 * @param x horizontal position from left
	 * @param y vertical position from top
	 * @return true if location is passable
	 */
	private boolean passable(int x, int y) {
		if(!inBounds(x,y)){
			return false;
		}
		int tile = tileAtPosition(x,y);
		return LevelParser.negativeSpaceTiles.get(tile) == 0 && LevelParser.leniencyTiles.get(tile) == 0;
	}

	/**
	 * This method gets the next state in the search 
	 * @return The next state, null means that it is not a legal state at the moment 
	 */
	@Override
	public State<MarioAction> getSuccessor(MarioAction a) {
		int newJumpVelocity = jumpVelocity;
		int newMarioX = marioX;
		int newMarioY = marioY;

		// Falling off bottom of screen (into a gap). No successor (death)
		if(!inBounds(marioX,marioY+1)) return null;
		
		if(newJumpVelocity == 0) { // Not mid-Jump
			if(passable(newMarioX,newMarioY+1)) { // Falling
				newMarioY++; // Fall down
			} else if(a.getD().equals(MarioAction.DIRECTION.JUMP)) { // Start jump
				newJumpVelocity = 4; // Accelerate up
			} 
		} else if(a.getD().equals(MarioAction.DIRECTION.JUMP)) {
			return null; // Can't jump mid-jump. Reduces search space.
		}

		if(newJumpVelocity > 0) { // Jumping up
			if(passable(newMarioX,newMarioY-1)) {
				newMarioY--; // Jump up
				newJumpVelocity--; // decelerate
			} else {
				newJumpVelocity = 0; // Can't jump if blocked above
			}
			// TODO: Add breakable case
		}

		// Right movement
		if(a.getD().equals(MarioAction.DIRECTION.RIGHT)) {
			if(passable(newMarioX+1,newMarioY)) {
				newMarioX++;
			} else if(marioY == newMarioY) { // vertical position did not change
				// This action does not change the state. Neither jumping up nor falling down, and could not move right, so there is no NEW state to go to
				return null;
			}
		}

		// Left movement
		if(a.getD().equals(MarioAction.DIRECTION.LEFT)) {
			if(passable(newMarioX-1,newMarioY)) {
				newMarioX--;
			} else if(marioY == newMarioY) { // vertical position did not change
				// This action does not change the state. Neither jumping up nor falling down, and could not move left, so there is no NEW state to go to
				return null;
			}
		}
		if(!inBounds(newMarioX, newMarioY)){
			return null;
		}
		return new MarioState(level, newJumpVelocity, newMarioX, newMarioY);
	}

	/**
	 * Gets a list of the valid actions for playing Mario levels 
	 * @return A list of valid actions
	 */
	@Override
	public ArrayList<MarioAction> getLegalActions(State<MarioAction> s) {
		ArrayList<MarioAction> possible = new ArrayList<MarioAction>();
		for(MarioAction.DIRECTION act: MarioAction.DIRECTION.values()){
			if(s.getSuccessor(new MarioAction(act))!=null){
				possible.add(new MarioAction(act));
			}
		}
		/*// About to fall off bottom edge: no actions
		if(!inBounds(marioX+1,marioY)) return possible;
		// Can move right if it is the goal or possable
		if(inBounds(marioX+1,marioY) && (isGoal(marioX+1) || passable(marioX+1,marioY))) possible.add(new MarioAction(MarioAction.DIRECTION.RIGHT));
		// Can move left if passable
		if(inBounds(marioX-1,marioY) &&  passable(marioX-1,marioY)) possible.add(new MarioAction(MarioAction.DIRECTION.RIGHT));
		// Can jump if on ground
		if(inBounds(marioX,marioY-1) && passable(marioX,marioY-1) && !passable(marioX,marioY+1)) possible.add(new MarioAction(MarioAction.DIRECTION.JUMP));*/

		// Add death from enemies?

		return possible;
	}

	/**
	 * determines if you have won the level 
	 * @return True if you have reach the last x coordinate in the level 
	 */
	@Override
	public boolean isGoal() {
		return isGoal(marioX);
	}

	@Override
	public double stepCost(State<MarioAction> s, MarioAction a) {
		return 1;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 * 
	 * IF WE EVER MODIFY THE level (by breaking bricks or killing enemies) WE WILL NEED TO CHANGE THIS!
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + jumpVelocity;
		result = prime * result + marioX;
		result = prime * result + marioY;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 * IF WE EVER MODIFY THE level (by breaking bricks or killing enemies) WE WILL NEED TO CHANGE THIS!
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MarioState other = (MarioState) obj;
		if (jumpVelocity != other.jumpVelocity)
			return false;
		if (marioX != other.marioX)
			return false;
		if (marioY != other.marioY)
			return false;
		return true;
	}

	/**
	 * @return String representation of current player coordinates 
	 */
	@Override
	public String toString(){
		return "("+marioX + "," + marioY +")";		

	}

}

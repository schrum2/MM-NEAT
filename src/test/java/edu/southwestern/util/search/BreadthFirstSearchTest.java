package edu.southwestern.util.search;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

/**
 * Basically a copy of AStarSearchTest, but testing BreadthFirstSearch.
 * The answers are the same for this simple test because all movement step
 * costs are 1.
 * @author schrum2
 *
 */
public class BreadthFirstSearchTest {

	/**
	 * Actions in the Grid World include up/down/left/right
	 * @author schrum2
	 *
	 */
	private static class GridAction implements Action {
		public enum DIRECTION {UP, DOWN, LEFT, RIGHT}
		private DIRECTION direction;;
		public GridAction(DIRECTION d) {
			this.direction = d;
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
	
	/**
	 * Simple Grid World state is just the location,
	 * but some kind of reference to the maze layout is
	 * needed to know which directions can be moved in.
	 * @author schrum2
	 *
	 */
	private static class GridState extends State<GridAction> {

		private int x;
		private int y;
		// For efficiency, important to only maintain this as reference
		private final char[][] maze; 

		public GridState(int x, int y, char[][] maze) {
			this.x = x;
			this.y = y;
			this.maze = maze;
		}

		/**
		 * This method assumes it will not be called with an illegal
		 * action, and thus does not check the walls first
		 * @param a
		 * @return
		 */
		@Override
		public State<GridAction> getSuccessor(GridAction a) {
			switch(a.direction) {
			case UP:
				return new GridState(x,y-1,maze);
			case DOWN:
				return new GridState(x,y+1,maze);
			case LEFT:
				return new GridState(x-1,y,maze);
			case RIGHT:
				return new GridState(x+1,y,maze);
			default:
				throw new IllegalArgumentException("Not a valid Action: "+ a);
			}
		}

		@Override
		public ArrayList<GridAction> getLegalActions(State<GridAction> s) {
			ArrayList<GridAction> legal = new ArrayList<GridAction>();
			// Locations with a char for space ' ' are considered passable, but so is 'G', the goal
			for(GridAction.DIRECTION a : GridAction.DIRECTION.values()) {
				GridAction possibleAction = new GridAction(a);
				GridState result = (GridState) s.getSuccessor(possibleAction);
				// Check if in bounds
				if(result.x >= 0 && result.x < maze[0].length && result.y >= 0 && result.y < maze.length) {
					// If is an open space
					if(maze[result.y][result.x] == ' ' || maze[result.y][result.x] == 'G') {
						legal.add(possibleAction);
					}
				}
			}
			return legal; // Only the legal actions
		}

		@Override
		public boolean isGoal() {
			// Assumes we will never define a state that is out of bounds
			return maze[y][x] == 'G';
		}

		@Override
		public double stepCost(State<GridAction> s, GridAction a) {
			return 1; // Easy: All actions have a cost of 1
		}
		
		/**
		 * Needed for State comparison in HashSet
		 */
		public boolean equals(Object other) {
			if(other instanceof GridState) {
				GridState gs = (GridState) other;
				return gs.x == this.x && gs.y == this.y; // Assume the mazes are the same 
			}
			return false;
		}
		
		/**
		 * Hash code needed because HashSet is used to track States
		 */
		public int hashCode() {
			return x + 7 * y;
		}
	}
	
	public static final int KNOWN_GOAL_X = 4;
	public static final int KNOWN_GOAL_Y = 3; 
	
	@Test
	public void simpleGridWorldTest() {
		Search<GridAction,GridState> search = new BreadthFirstSearch<>();
		ArrayList<GridAction> result = search.search(new GridState(0,0,new char[][] {
			{' ',' ',' ',' ',' ',' ','X'},  // Y = 0   <-- Start here
			{' ','X',' ','X','X',' ',' '},  // Y = 1
			{' ','X','X','X','X','X',' '},  // Y = 2
			{' ',' ','X',' ','G','X',' '},  // Y = 3   <-- Goal here
			{'X','X','X',' ','X','X',' '},  // Y = 4
			{' ',' ',' ',' ',' ',' ',' '},  // Y = 5
		}));
			
		Iterator<GridAction> itr = result.iterator();
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.DOWN), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.DOWN), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.DOWN), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.DOWN), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.DOWN), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		
	}
}

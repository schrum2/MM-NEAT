/**
 * Class to help evaluate the after-states in the Tetris project. This class may be moved later.
 * 
 * @author Jacob Schrum, Gabby Gonzalez
 */
package edu.utexas.cs.nn.tasks.rlglue.tetris;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.rlcommunity.environments.tetris.TetrisState;

import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class TertisAfterStateGenerator {
	/**
	 * Takes in the current Tetris state and finds each of the possible after states, as well as the actions to get to that state.
	 * The after states will be states for each possible orientation of the falling piece as they hit the bottom from each placement across the width.
	 * @param ts
	 * @return arraylist of pairs (evaluated after-states and arraylist of actions)
	 */
	public static HashSet<TetrisStateActionPair> generateAfterStates(TetrisState ts){ 
		int[] actOrder = {4, 0, 1, 2, 3}; // Replace with actual action constants (magic numbers!)
		HashSet<TetrisStateActionPair> evaluated = new HashSet<TetrisStateActionPair>(); //only ever as big as # of orientations times the width
		HashSet<TetrisState> seen = new HashSet<TetrisState>();
		Queue<TetrisStateActionPair> fringe = new LinkedList<TetrisStateActionPair>();
		fringe.add(new TetrisStateActionPair(ts));
		seen.add(new TetrisState(ts));
		
		while(!fringe.isEmpty()) {
			//System.out.println("Looping");
			TetrisStateActionPair pair = fringe.poll();
			//System.out.println(pair.t1);
			//MiscUtil.waitForReadStringAndEnterKeyPress();
			for(int i = 0; i < actOrder.length; i++) {
				int action = actOrder[i];
				//System.out.println("Consider " + action);
				TetrisStateActionPair copy = new TetrisStateActionPair(pair);
				if(copy.t1.take_action(action)) {
					copy.t2.add(action); // add action to list
					int scoreBefore = copy.t1.score;
					copy.t1.update(); // result of action in the given state
					if(!copy.t1.blockMobile) {
						boolean added = evaluated.add(copy);
//						if(added) {
//							System.out.println("Add to end state");
//							System.out.println(copy.t1);
//							System.out.println("Before: " + scoreBefore);
//							System.out.println("After: " + copy.t1.score);
//							System.out.println(copy.t2);
//							MiscUtil.waitForReadStringAndEnterKeyPress();
//						}
					} else if(!seen.contains(copy.t1)) {
						//System.out.println("Add move " + action + " for " + copy.t1.currentX + " " + copy.t1.currentY + " " + copy.t1.currentRotation);
						fringe.add(copy);
						seen.add(copy.t1);
					}
				} else {
					//System.out.println("Action not allowed " + action);
				}
			}
		}

//		for(TetrisState t: seen) {
//			System.out.println(t);
//			MiscUtil.waitForReadStringAndEnterKeyPress();
//		}
//		System.out.println("--------------------");
		
		return evaluated;
		
//		for(int i = 0; i < TetrisState.BLOCK_ROTATIONS[ts.currentBlockId]; i++){ // Try each orientation for the given block id, this is to skip duplicate orientations for certain blocks
//			
//			TetrisState copy = new TetrisState(ts);	// a copy of ts to use for afterstate generation		
//			List<Integer> actionList = new LinkedList<Integer>(); // Logs the actions to reach the desired "falling" spot on the bottom
//			
//			while(copy.currentRotation != i){// Try to rotate (loop until rotation achieved)
//				if(canRotate(copy)){// Can we rotate? - helper method
//					copy.update(); // If yes, then do so and record it
//					actionList.add(TetrisState.CW); // add action to actionList
//				} else { // If no, figure out how to get unstuck - helper method
//					getUnstuck(copy); //TODO: actually write this one
//				}
//			}
//			for(int j = -2; j < TetrisState.worldWidth; j++){ // Try each position across the width (-2 used because of prior testing of block positions)
//				if(spotAvailable(copy, j)){
//					while(copy.currentX != j){ // tests for x position
//						if(j < copy.currentX){ // if the target is to the left of the current position
//							if(canMove(copy, true)){ // true calls for left
//								copy.update(); // If yes, then do so and record it
//								actionList.add(TetrisState.LEFT); // add action to actionList
//							} else { // If no, figure out how to get unstuck - helper method
//								//WRITE A DIFFERENT METHOD TO UNSTICK LEFT/RIGHT
//								//getUnstuck(copy); //TODO: actually write this one
//							}
//						} else {
//							if(canMove(copy, false)){ // false calls for right
//								copy.update(); // If yes, then do so and record it
//								actionList.add(TetrisState.RIGHT); // add action to actionList
//							} else { // If no, figure out how to get unstuck - helper method
//								//WRITE A DIFFERENT METHOD TO UNSTICK LEFT/RIGHT
//								//getUnstuck(copy); //TODO: actually write this one
//							}
//						}
//					}
//					// Now Fall
//					copy.take_action(TetrisState.FALL); // move down all the way
//					copy.update(); // Update until it has reached the bottom
//					actionList.add(TetrisState.FALL); // add action to actionList
//				}
//			}
//		}	
//		return evaluated;
	}

//	/**
//	 * This method takes in the current TetrisState and finds the next action to get the piece from being "stuck"
//	 * This is used for rotation, when against a wall or the ceiling
//	 * @param copy
//	 */
//	private static void getUnstuck(TetrisState copy) {
//		int x = copy.currentX;
//		int y = copy.currentY;
//		int rotation = copy.currentRotation;
//		int block = copy.currentBlockId;
//		System.out.println("Block is stuck!"); // for testing only
//		System.out.println("[x = " + x + ", y = " + y + ", rotation = " + rotation + ", block = " + block + "]"); 
//		
//		//TODO: needs to be actually implemented
//	}
//	
//	/**
//	 * This method determinds if the current block in the TetrisState can move left or right where it is currently
//	 * @param copy TetrisState
//	 * @param left (true when left calls, false when right calls)
//	 * @return true if the piece can move, false if not
//	 */
//	private static boolean canMove(TetrisState copy, boolean left) {
//		int tempX = copy.currentX;
//		
//		if(left){
//			copy.take_action(TetrisState.LEFT);
//			if(copy.currentX != tempX){ // can we move?
//				return true;
//			}	
//		} else {
//			copy.take_action(TetrisState.RIGHT);
//			if(copy.currentX != tempX){ // can we move?
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	/**
//	 * This method determines if the current block in the TetrisState can rotate where it is currently
//	 * @param copy TetrisState
//	 * @return true is the piece can rotate, false if not
//	 */
//	private static boolean canRotate(TetrisState copy) {
//		//perhaps look at the code for checking boundaries, or simply take the action and check numbers here?
//		int tempRotation = copy.currentRotation;
//		copy.take_action(TetrisState.CW);
//		if(copy.currentRotation != tempRotation){ // can we rotate?
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * This method takes in the current TetrisState and current column value returns whether or not the spot is available
//	 * @param copy TetrisState
//	 * @return true if spot not taken
//	 */
//	private static boolean spotAvailable(TetrisState copy, int column){
//		int x = copy.currentX;
//		int y = copy.currentY;
//		TetrisState temp = new TetrisState(copy); //for testing
//		temp.currentX = column;
//		temp.currentY = temp.getHeight();
//		//TODO: finish this
//		return false;
//	}
}

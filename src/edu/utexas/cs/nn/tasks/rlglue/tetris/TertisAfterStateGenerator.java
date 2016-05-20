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
		int[] actOrder = {TetrisState.NONE, TetrisState.LEFT, TetrisState.RIGHT, TetrisState.CW, TetrisState.CCW}; // Replace with actual action constants (magic numbers!)
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
					copy.t1.update(); // result of action in the given state
					if(!copy.t1.blockMobile) {
//						System.out.println("Before:");
//						System.out.println(copy.t1.toString(false));
//						copy.t1.writeCurrentBlock();
//						System.out.println("After:");
//						System.out.println(copy.t1.toString(false));
//						MiscUtil.waitForReadStringAndEnterKeyPress();
						evaluated.add(copy);
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
		
		return evaluated;	
	}
}

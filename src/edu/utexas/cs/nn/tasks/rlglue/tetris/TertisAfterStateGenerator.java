/**
 * Class to help evaluate the after-states in the Tetris project. This class may be moved later.
 * 
 * @author Jacob Schrum, Gabby Gonzalez
 */
package edu.utexas.cs.nn.tasks.rlglue.tetris;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

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
		//System.out.println("[Entered evaluateAfterStates]");
		
	// Take in the current falling piece and figure out number or possible orientations (use TetrisPiece for this)
		int possibleOrientations = TetrisState.TETRIS_STATE_NUMBER_POSSIBLE_BLOCKS_ORIENTATIONS; 
		//System.out.println("Block is " + ts.currentBlockId);
		HashSet<TetrisStateActionPair> evaluated = new HashSet<TetrisStateActionPair>(); //only ever as big as # of orientations times the width
		
		for(int i = 0; i < possibleOrientations; i++){ // Try each orientation
			//System.out.println("[Entered Orientation Loop #" + i + "]");
			for(int j = -2; j < TetrisState.worldWidth; j++){ // Try each position across the width, and the -2 is still a magic number, but this is a general idea *fix later -Gab
				//System.out.println("[Entered Position Loop #" + j + "]");
				TetrisState copy = new TetrisState(ts);				
				List<Integer> actionList = new LinkedList<Integer>(); // Logs the actions to reach the desired "falling" spot on the bottom
				
				//System.out.println("[Going to take actions for i = " + i + " and j = " + j + "]");
				while(copy.blockMobile){ // while we can move 
					if(copy.currentRotation != i){ // do we need to rotate?
						int tempRotation = copy.currentRotation;
						copy.take_action(TetrisState.CW);
						if(copy.currentRotation != tempRotation){ // can we rotate?
							copy.update();
							actionList.add(TetrisState.CW); // add action to actionList
						}
					}
					if(copy.currentX > j){ // do we need to move left?
						int tempX = copy.currentX;
						copy.take_action(TetrisState.LEFT);
						if(copy.currentX != tempX){ // can we move left?
							copy.update();
							actionList.add(TetrisState.LEFT); // add action to actionList
						}else{ // move back
							copy.take_action(TetrisState.RIGHT); // move right
							copy.update(); // update
							actionList.add(TetrisState.RIGHT); // add action to actionList
						}
					}
					else if(copy.currentX < j){ // do we need to move right?
						int tempX = copy.currentX;
						copy.take_action(TetrisState.RIGHT);
						if(copy.currentX != tempX){ // can we move right?
							copy.update();
							actionList.add(TetrisState.RIGHT); // add action to actionList
						}else{ // move back
							copy.take_action(TetrisState.LEFT); // move left
							copy.update(); // update
							actionList.add(TetrisState.LEFT); // add action to actionList
						}
					}else{
						copy.take_action(TetrisState.FALL); // move down all the way
						copy.update(); // Update until it has reached the bottom
						actionList.add(TetrisState.FALL); // add action to actionList
					}
				}
				copy.currentY = 0;
				copy.update(); // Update this
				
				evaluated.add(new TetrisStateActionPair(copy, actionList)); // Adds the finished state and actions pair to the final arraylist				
			}
		}
		return evaluated;
	}
	
	
	
	/**
	 * This is a testing environment for this after state evaluator, using two different worldStates to check the long piece
	 * @param args
	 */
	public static void main(String[] args) {
		
		TetrisViewer testView = new TetrisViewer(); //make a TetrisViewer
		TetrisState testState = new TetrisState();//make a tetris state

		//create the test space for 1
		/*
		testState.worldState[178] = 1;
		testState.worldState[181] = 1;
		testState.worldState[187] = 1;
		testState.worldState[188] = 1;
		testState.worldState[191] = 1;
		testState.worldState[192] = 1;
		testState.worldState[193] = 1;
		testState.worldState[197] = 1;
		*/
		//create test space for 2
		testState.worldState[160] = 1;
		testState.worldState[170] = 1;
		testState.worldState[180] = 1;
		testState.worldState[181] = 1;
		testState.worldState[182] = 1;
		testState.worldState[183] = 1;
		testState.worldState[189] = 1;
		testState.worldState[190] = 1;
		testState.worldState[191] = 1;
		testState.worldState[192] = 1;
		testState.worldState[193] = 1;
		testState.worldState[194] = 1;
		testState.worldState[195] = 1;
		testState.worldState[197] = 1;
		testState.worldState[198] = 1;
		testState.worldState[199] = 1;
		//Create simple test space
		/*
		testState.worldState[190] = 1;
		testState.worldState[191] = 1;
		testState.worldState[192] = 1;
		testState.worldState[193] = 1;
		testState.worldState[194] = 1;
		testState.worldState[195] = 1;
		testState.worldState[196] = 1;
		testState.worldState[197] = 1;
		testState.worldState[198] = 1;
		testState.worldState[184] = 1;
		testState.worldState[185] = 1;
		testState.worldState[186] = 1;
		testState.worldState[187] = 1;
		testState.worldState[188] = 1;
		testState.worldState[189] = 1;
		*/
		testView.update(testState);
		
		MiscUtil.waitForReadStringAndEnterKeyPress();
		HashSet<TetrisStateActionPair> holder = generateAfterStates(testState);
		for(Pair<TetrisState, List<Integer>> p: holder){
			testView.update(p.t1);
			//System.out.println("Y is " + p.t1.currentY + ", X is " + p.t1.currentX + ", and rotation is " + p.t1.currentRotation);
			//System.out.println(p.t2);
			MiscUtil.waitForReadStringAndEnterKeyPress();			
		}
	}
}

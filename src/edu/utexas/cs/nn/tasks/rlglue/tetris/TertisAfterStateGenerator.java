/**
 * Class to help evaluate the after-states in the Tetris project. This class may be moved later.
 * 
 * @author Jacob Schrum, Gabby Gonzalez
 */
package edu.utexas.cs.nn.tasks.rlglue.tetris;
import java.util.ArrayList;
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
	public static ArrayList<Pair<TetrisState, ArrayList<Integer>>> evaluateAfterStates(TetrisState ts){ 
	// Take in the current falling piece and figure out number or possible orientations (use TetrisPiece for this)
		int possibleOrientations = 4; //sorry for the magic number <3 -Gab TODO:
				
		ArrayList<Pair<TetrisState, ArrayList<Integer>>> evaluated = new ArrayList<Pair<TetrisState, ArrayList<Integer>>>(possibleOrientations*TetrisState.worldWidth); //only ever as big as # of orientations times the width
		
		for(int i = 0; i < possibleOrientations; i++){ // Try each orientation
			for(int j = -5; j < TetrisState.worldWidth; j++){ // Try each position across the width, also sorry for magic number, the -5 is to cover all our bases TODO:
				TetrisState copy = new TetrisState(ts);				
				ArrayList<Integer> actionList = new ArrayList<Integer>(); // Logs the actions to reach the desired "falling" spot on the bottom
								
				while(copy.currentRotation != i){ // Rotate piece until it reach the desired orientation, and update/add accordingly
					copy.take_action(TetrisState.CW);
					copy.update();
					actionList.add(TetrisState.CW); //add action to actionList
				}
				
				int lastX = -100; // also sorry for this magic number TODO:
				while(copy.currentX != j && copy.currentX != lastX){ // Move the piece to the left or right until it is in the desired spot
					lastX = copy.currentX;
					if(copy.currentX > j){ // The desired spot is to the left of the current placement
						copy.take_action(TetrisState.LEFT); //move left
						copy.update(); //update
						actionList.add(TetrisState.LEFT); //add action to actionList
					}else{ //(copy.currentX < j) The desired spot is to the right of the current placement
						copy.take_action(TetrisState.RIGHT); //move right
						copy.update(); //update
						actionList.add(TetrisState.RIGHT); //add action to actionList
					}
				}
				
				while(copy.blockMobile){ // While the block as not yet hit something (uses "onSomething" from TetrisState)
					copy.update(); // Update until it has reached the bottom
					actionList.add(TetrisState.FALL); //add action to actionList
				}
				
				copy.spawn_block(); // Spawn a new block to clear the last piece and show just the board (extractor can blot this piece out for us)
				copy.update(); // Update this
				
				evaluated.add(new Pair<TetrisState, ArrayList<Integer>>(copy, actionList)); // Adds the finished state and actions pair to the final arraylist				
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
		ArrayList<Pair<TetrisState, ArrayList<Integer>>> holder = evaluateAfterStates(testState);
		for(Pair<TetrisState, ArrayList<Integer>> p: holder){
			testView.update(p.t1);
			MiscUtil.waitForReadStringAndEnterKeyPress();			
		}
	}
}

package edu.utexas.cs.nn.tasks.rlglue.tetris;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.rlcommunity.environments.tetris.TetrisPiece;
import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.rlglue.RLGlueAgent;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

public class TetrisAfterStateAgent<T extends Network> extends RLGlueAgent<T>{

	public List<Integer> currentActionList;
	public Observation lastObs;

	public TetrisAfterStateAgent() {
		super();
		currentActionList = new LinkedList<Integer>();
	}

	/**
	 * Troubleshooting method used to look at a Tetris observation
	 * before converting it to a Tetris state.
	 * @param o RL Glue Observation containing Tetris information
	 * @return String output describing Tetris world
	 */
	public static String tetrisObservationToString(Observation o) {
    	String result = "";
        for (int y = 0; y < TetrisState.worldHeight; y++) {
            for (int x = 0; x < TetrisState.worldWidth; x++) {
            	result += (o.intArray[y * TetrisState.worldWidth + x]);
            }
            result += ("\n");
        }
        result += ("-------------");
        return result;
    }    
	
	public Action getAction(Observation o) {
		
		//System.out.println(tetrisObservationToString(o));
		
		lastObs = o; //saves the current observation for later
				
		if(currentActionList.isEmpty()){ // if we don't already have a list of actions to follow

			//call obs to ts
			TetrisState tempState = observationToTetrisState(o);

	
			boolean currentWatch = CommonConstants.watch;
			CommonConstants.watch = false;

			HashSet<TetrisStateActionPair> tetrisStateHolder = TertisAfterStateGenerator.generateAfterStates(tempState);
			CommonConstants.watch = currentWatch;

			ArrayList<Pair<Double, List<Integer>>> outputPairs = new ArrayList<Pair<Double, List<Integer>>>(); //arraylist to hold the actions and outputs for later


			double[] outputs;
			//for(pairs in the set){
			for(TetrisStateActionPair i : tetrisStateHolder){

				//	outputs = constultPolicy(features) REMEMBER outputs is an array of 1
				double[] inputs = MMNEAT.rlGlueExtractor.extract(i.t1.get_observation()); 

				double[] inputsScaled = scaleInputs(inputs);

				outputs = this.consultPolicy(inputsScaled); 

				//	array(list?).add(outputs[0], first action*) 
				Pair<Double, List<Integer>> tempPair = new Pair<Double, List<Integer>>(outputs[0], i.t2);
				outputPairs.add(tempPair);

			}

			double[] outputForArgmax = new double[outputPairs.size()];
			for(int i = 0; i < outputForArgmax.length; i++){
				outputForArgmax[i] = outputPairs.get(i).t1;
			}

			int index = StatisticsUtilities.argmax(outputForArgmax); //action = argmax(list)

			for(int k = 0; k < outputPairs.get(index).t2.size(); k++){
				currentActionList.add(outputPairs.get(index).t2.get(k)); //this should add the next action to the linked list in the proper order
			}	
		}

		Action action = new Action(TSO.getNumDiscreteActionDims(), TSO.getNumContinuousActionDims()); //moved this from before the action call, because both actions may need it -Gab
		action.intArray[0] = currentActionList.get(0);
		currentActionList.remove(0);

		return action;
	}

	/**
	 * This method takes over the job of converting an observation to a TetrisState
	 * @param o Observation
	 * @return ts TetrisState
	 */
	private TetrisState observationToTetrisState(Observation o) {
		TetrisState ts = new TetrisState();
		ts.currentX = o.intArray[TetrisState.TETRIS_STATE_CURRENT_X_INDEX]; // adds the Observation's X to tempState
		ts.currentY = o.intArray[TetrisState.TETRIS_STATE_CURRENT_Y_INDEX]; // adds the Observation's Y to tempState
		ts.currentRotation = o.intArray[TetrisState.TETRIS_STATE_CURRENT_ROTATION_INDEX]; // adds the Observation's rotation to tempState
		for(int p = 0; p < TetrisState.TETRIS_STATE_NUMBER_POSSIBLE_BLOCKS; p++){
			if(o.intArray[ts.worldState.length + p] == 1){ // this checks for the current block Id
				ts.currentBlockId = p;
			}
		}
		for (int i = 0; i < ts.worldState.length; i++) { // replaces the new TetrisState's worldState with the Observation's worldState
			ts.worldState[i] = o.intArray[i];
		}
		blotMobilePiece(ts); // blots out the mobile piece on the board
		return ts;
	}

	/**
	 * Returns a 1 for the number of outputs needed (action)
	 */
	public int getNumberOutputs() {
		return 1; // Utility of evaluated state
	}

	/**
	 * Takes in block information to erase the mobile piece from the observation tetris state, so that no collisions occur
	 * @param ts TetrisState
	 */
	public static void blotMobilePiece(TetrisState ts) {
		Vector<TetrisPiece> possibleBlocks = ts.possibleBlocks;
		int[][] mobilePiece = possibleBlocks.get(ts.currentBlockId).getShape(ts.currentRotation);
		for (int x = 0; x < mobilePiece.length; x++) {
            for (int y = 0; y < mobilePiece[x].length; y++) {
            	if (mobilePiece[x][y] != 0) {
            		int linearIndex = (ts.currentY + y) * ts.worldWidth + (ts.currentX + x);
            		if (linearIndex < 0) {
                        System.err.printf("Bogus linear index %d for %d + %d, %d + %d\n", linearIndex, ts.currentX, x, ts.currentY, y);
                        Thread.dumpStack();
                        System.exit(1);
                    }
                    ts.worldState[linearIndex] = 0;
            	}
            }
        }
	}
	
	/**
	 * Takes in the current inputs and scales them appropriately for the BertsekasTsitsiklis Feature Extractor
	 * @param inputs
	 * @return scaled inputs
	 */
	public static double[] scaleInputs(double[] inputs) {
		double[] next = new double[inputs.length];
		int height_features = TetrisState.worldWidth + (TetrisState.worldWidth - 1) + 1; // height values (10), height differences (9), and max height (1)
		for(int i = 0; i < height_features; i++) { 
			next[i] = inputs[i] / TetrisState.worldHeight; 
		}
		next[inputs.length - 2] = inputs[inputs.length - 2] / TetrisState.TETRIS_STATE_NUMBER_WORLD_GRID_BLOCKS; // scales down the number of holes in relation to the whole of the board
		next[next.length - 1] = 1.0; // bias is 1, so not scaling
		return next; 
	}

	/**
	 * This method takes in the last observation and returns the number of blocks left in the game over screen
	 * This is used for a feature of the state
	 * @return blockCount, number of blocks on the screen upon game over
	 */
	public int getNumberOfBlocksInLastState() {
		int blockCount = 0;
		int worldLength = TetrisState.worldHeight * TetrisState.worldWidth;
		for (int i = 0; i < worldLength; i++) { // replaces tempState's worldState with the Observation's worldState
			if(lastObs.intArray[i] > 0){
				blockCount++;
			}
		}
		return blockCount;
	}
}

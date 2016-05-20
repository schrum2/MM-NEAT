package edu.utexas.cs.nn.tasks.rlglue.tetris;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

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
		//TODO: I think there's an issue with converting Observations to TetrisStates, because there is missing information we cannot get from the obsservation
		//We need to find a way around this or a way to fix this.

		//System.out.println(tetrisObservationToString(o));
		
		if(currentActionList.isEmpty()){ // if we don't already have a list of actions to follow

			//convert Observation to TetrisState
			TetrisState tempState = new TetrisState();
			tempState.currentX = o.intArray[TetrisState.TETRIS_STATE_CURRENT_X_INDEX]; // adds the Observation's X to tempState
			tempState.currentY = o.intArray[TetrisState.TETRIS_STATE_CURRENT_Y_INDEX]; // adds the Observation's Y to tempState
			tempState.currentRotation = o.intArray[TetrisState.TETRIS_STATE_CURRENT_ROTATION_INDEX]; // adds the Observation's rotation to tempState
			for(int p = 0; p < TetrisState.TETRIS_STATE_NUMBER_POSSIBLE_BLOCKS; p++){
				if(o.intArray[tempState.worldState.length + p] == 1){ // this checks for the current block Id
					tempState.currentBlockId = p;
				}
			}

			for (int i = 0; i < tempState.worldState.length; i++) { // replaces tempState's worldState with the Observation's worldState
				tempState.worldState[i] = o.intArray[i];
			}

			boolean currentWatch = CommonConstants.watch;
			CommonConstants.watch = false;
			//make a Set of evalAfterStates(testState)
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

	public int getNumberOutputs() {
		return 1; // Utility of evaluated state
	}

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


}

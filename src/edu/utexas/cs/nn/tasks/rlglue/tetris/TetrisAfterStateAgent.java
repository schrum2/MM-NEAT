package edu.utexas.cs.nn.tasks.rlglue.tetris;

import java.util.ArrayList;
import java.util.HashSet;

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

	public TetrisAfterStateAgent() {
		super();
	}

	public Action getAction(Observation o) {
		//TODO: I think there's an issue with converting Observations to TetrisStates, because there is missing information we cannot get from the obsservation
		//We need to find a way around this or a way to fix this.

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
		HashSet<Pair<TetrisState, ArrayList<Integer>>> tetrisStateHolder = TertisAfterStateGenerator.evaluateAfterStates(tempState);
		CommonConstants.watch = currentWatch;

		ArrayList<Pair<Double, Integer>> outputPairs = new ArrayList<Pair<Double, Integer>>(); //arraylist to hold the actions and outputs for later


		double[] outputs;
		//for(pairs in the set){
		for(Pair<TetrisState, ArrayList<Integer>> i : tetrisStateHolder){

			//	outputs = constultPolicy(features) REMEMBER outputs is an array of 1
			double[] inputs = MMNEAT.rlGlueExtractor.extract(i.t1.get_observation()); 

			double[] inputsScaled = scaleInputs(inputs);

			outputs = this.consultPolicy(inputsScaled); 

			//	array(list?).add(outputs[0], first action*) 
			Pair<Double, Integer> tempPair = new Pair<Double, Integer>(outputs[0], i.t2.get(0));
			outputPairs.add(tempPair);

		}

		double[] outputForArgmax = new double[outputPairs.size()];
		for(int i = 0; i < outputForArgmax.length; i++){
			outputForArgmax[i] = outputPairs.get(i).t1;
		}
		//action = argmax(list)
		int index = StatisticsUtilities.argmax(outputForArgmax);

		Action action = new Action(TSO.getNumDiscreteActionDims(), TSO.getNumContinuousActionDims());
		action.intArray[0]= outputPairs.get(index).t2;
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

package edu.southwestern.tasks.rlglue.tetris;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.rlcommunity.environments.tetris.TetrisPiece;
import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.nsga2.bd.characterizations.RemembersObservations;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.dl4j.DL4JNetworkWrapper;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.rlglue.RLGlueAgent;
import edu.southwestern.tasks.rlglue.RLGlueTask;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.stats.StatisticsUtilities;


public class TetrisAfterStateAgent<T extends Network> extends RLGlueAgent<T> {

	// Used for TD learning
	private boolean backprop;
	private double gamma;
	private int minibatchSize;
	// Used for TD learning to collect history into learning batches
	private double[][] batchInputs;
	private double[][] batchOutputs;
	private int currentBatchPointer;
	private boolean rlEpsilonGreedy;
	private double rlEpsilon;
	
	// Saved in order to replay actions to a desired afterstate. Gets refilled
	// once the list of actions run out.
	public List<Integer> currentActionList;
	// Saved for certain fitness calculations
	public Observation lastObs;

	public TetrisAfterStateAgent() {
		super();
		currentActionList = new LinkedList<Integer>();
		backprop = Parameters.parameters.booleanParameter("rlBackprop");
		gamma = Parameters.parameters.doubleParameter("rlGamma");
		minibatchSize = Parameters.parameters.integerParameter("rlBatchSize");
		rlEpsilonGreedy = Parameters.parameters.booleanParameter("rlEpsilonGreedy");
		rlEpsilon = Parameters.parameters.doubleParameter("rlEpsilon");
		batchInputs = new double[minibatchSize][];
		batchOutputs = new double[minibatchSize][];
		currentBatchPointer = 0;
	}

	/**
	 * Troubleshooting method used to look at a Tetris observation before
	 * converting it to a Tetris state.
	 *
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

	/**
	 * Given an observation, it returns the action to be taken. Do breadth first
	 * search to figure out all possible placements of a piece, and ask the
	 * policy which after-state it likes best. Then save the sequence of actions
	 * leading to that state so that future calls to this method can simply grab
	 * the next action from a saved list. Once the list runs out, the search
	 * process is repeated.
	 *
	 * @param o Observation
	 * @return action Action
	 */
	@Override
	public Action getAction(double r, Observation o) {

		lastObs = o; // saves the current observation for later

		if (currentActionList.isEmpty()) { // if we don't already have a list of
											// actions to follow
			
			// call obs to ts
			TetrisState tempState = observationToTetrisState(o);
			// For TD: network inputs in the current state.
			// Not used for decision making ... only for learning updates with backprop.
			double[] inputsInS = RLGlueTask.rlGlueExtractor.extract(tempState.get_observation(false));
			
			// System.out.println("Start state");
			// System.out.println(tempState);
			// ArrayList<TetrisStateActionPair> forDebugging = new
			// ArrayList<TetrisStateActionPair>();

			boolean currentWatch = CommonConstants.watch;
			CommonConstants.watch = false;

			HashSet<TetrisStateActionPair> tetrisStateHolder = TetrisAfterStateGenerator.generateAfterStates(tempState);
			CommonConstants.watch = currentWatch;
			
			// arraylist to hold the actions and outputs for later
			ArrayList<Pair<Double, List<Integer>>> outputPairs = new ArrayList<Pair<Double, List<Integer>>>(); 

			double value;
			// for(pairs in the set){
			for (TetrisStateActionPair i : tetrisStateHolder) {
				value = valueOfState(i.t1);
				
				if(Parameters.parameters.booleanParameter("stepByStep")){
					System.out.println("Utility:" + value);
					System.out.print("Press enter to continue");
					MiscUtil.waitForReadStringAndEnterKeyPress();
				}
				// Associate the policy's score for the after state with the
				// list of actions leading to it
				Pair<Double, List<Integer>> tempPair = new Pair<Double, List<Integer>>(value, i.t2);
				outputPairs.add(tempPair);
				// forDebugging.add(i);
			}

			double[] outputForArgmax = new double[outputPairs.size()];
			for (int i = 0; i < outputForArgmax.length; i++) {
				outputForArgmax[i] = outputPairs.get(i).t1;
			}

			// Stores index of move to take
			int index;
			if(rlEpsilonGreedy && RandomNumbers.randomCoin(rlEpsilon)) { // Explore random action
				index = RandomNumbers.randomGenerator.nextInt(outputForArgmax.length);
			} else { // Exploit best known action
				index = StatisticsUtilities.argmax(outputForArgmax); // action = argmax(list)
			}
			
			double valueOfSPrime = outputPairs.get(index).t1;
			
			if(backprop) {
				// TD learning target:
				// V(s) should equal (r + gamma*V(s'))
				double backpropTarget = r + gamma*valueOfSPrime;
				// Input features
				batchInputs[currentBatchPointer] = inputsInS;
				// and associated target
				batchOutputs[currentBatchPointer] = new double[]{backpropTarget};
				currentBatchPointer++;
				
				// Full batch of experience accumulated
				if(currentBatchPointer == minibatchSize) {
					// Unwrap the network from the policy and call fit method to do backprop
					DL4JNetworkWrapper dl4jNet = (DL4JNetworkWrapper) policy; // Policy must be DL4JNetworkWrapper if backprop is used
					dl4jNet.fit(batchInputs, batchOutputs);
					// Reset pointer afterward
					currentBatchPointer = 0;
				}
			}
			
			List<Integer> moveSequence = outputPairs.get(index).t2;
			currentActionList.addAll(moveSequence);
			// Let the block settle and a new one spawns
			currentActionList.add(TetrisState.NONE); 
		}

		Action action = new Action(RLGlueTask.tso.getNumDiscreteActionDims(), RLGlueTask.tso.getNumContinuousActionDims()); 
		// Current action is next one in list
		action.intArray[0] = currentActionList.get(0);
		currentActionList.remove(0);

		return action;
	}

	/**
	 * Bundles up the steps for getting a state value for a particular TetrisState.
	 * Also useful for TD learning
	 * @param s A TetrisState
	 * @return Estimated long-term value of state
	 */
	private double valueOfState(TetrisState s) {
		// Basic features
		double[] inputs = RLGlueTask.rlGlueExtractor.extract(s.get_observation(false));
		// Scaled to range [0,1] for the neural network
		double[] inputsScaled = RLGlueTask.rlGlueExtractor.scaleInputs(inputs);
		if(Parameters.parameters.booleanParameter("rememberObservations")) {
			((RemembersObservations) MMNEAT.task).addObservation(inputsScaled);
		}
		policy.flush(); // remove recurrent activation
		// outputs is an array of length 1
		double[] outputs = this.consultPolicy(inputsScaled);
		assert !Double.isNaN(outputs[0]) : "Tetris eval result is NaN";
		return outputs[0];
	}
	
	
	/**
	 * This method takes over the job of converting an observation to a
	 * TetrisState
	 *
	 * @param o Observation
	 * @return ts TetrisState
	 */
	public static TetrisState observationToTetrisState(Observation o) {
		TetrisState ts = new TetrisState();		
		// adds the Observation's X to tempState
		ts.currentX = o.intArray[TetrisState.TETRIS_STATE_CURRENT_X_INDEX]; 
		// adds the Observation's Y to tempState
		ts.currentY = o.intArray[TetrisState.TETRIS_STATE_CURRENT_Y_INDEX]; 
		// adds the Observation's rotation to tempState
		ts.currentRotation = o.intArray[TetrisState.TETRIS_STATE_CURRENT_ROTATION_INDEX]; 
		
		for (int p = 0; p < TetrisState.TETRIS_STATE_NUMBER_POSSIBLE_BLOCKS; p++) { 
			// this checks for the current block Id
			if (o.intArray[ts.worldState.length + p] == 1) {
				ts.currentBlockId = p;
			}
		}
		// replaces the new TetrisState's worldState with the Observation's
		// worldState
		System.arraycopy(o.intArray, 0, ts.worldState, 0, ts.worldState.length);
		blotMobilePiece(ts); // blots out the mobile piece on the board
		return ts;
	}

	/**
	 * Number of outputs is one because the network returns a utility score.
	 * 
	 * @return always 1
	 */
	@Override
	public int getNumberOutputs() {
		return 1; // Utility of evaluated state
	}

	/**
	 * Takes in block information to erase the mobile piece from the observation
	 * tetris state, so that no collisions occur
	 *
	 * @param ts
	 *            TetrisState
	 */
	public static void blotMobilePiece(TetrisState ts) {
		Vector<TetrisPiece> possibleBlocks = TetrisState.POSSIBLE_BLOCKS;
		int[][] mobilePiece = possibleBlocks.get(ts.currentBlockId).getShape(ts.currentRotation);
		for (int x = 0; x < mobilePiece.length; x++) {
			for (int y = 0; y < mobilePiece[x].length; y++) {
				if (mobilePiece[x][y] != 0) {
					int linearIndex = (ts.currentY + y) * TetrisState.worldWidth + (ts.currentX + x);
					if (linearIndex < 0) {
						System.err.printf("Bogus linear index %d for %d + %d, %d + %d\n", linearIndex, ts.currentX, x,
								ts.currentY, y);
						Thread.dumpStack();
						System.exit(1);
					}
					ts.worldState[linearIndex] = 0;
				}
			}
		}
	}

	/**
	 * This method takes in the last observation and returns the number of
	 * blocks left in the game over screen. This is used for a fitness function
	 * at the end of the game.
	 *
	 * @return blockCount, number of blocks on the screen upon game over
	 */
	public int getNumberOfBlocksInLastState() {
		int blockCount = 0;
		int worldLength = TetrisState.worldHeight * TetrisState.worldWidth;
		for (int i = 0; i < worldLength; i++) { // replaces tempState's
												// worldState with the
												// Observation's worldState
			if (lastObs.intArray[i] > 0) {
				blockCount++;
			}
		}
		return blockCount;
	}
}

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

public class TetrisAfterStateAgent<T extends Network> extends RLGlueAgent<T> {

    // Saved in order to replay actions to a desired afterstate. Gets refilled
    // once the list of actions run out.
    public List<Integer> currentActionList;
    // Saved for certain fitness calculations
    public Observation lastObs;

    public TetrisAfterStateAgent() {
        super();
        currentActionList = new LinkedList<Integer>();
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
    public Action getAction(Observation o) {

        lastObs = o; //saves the current observation for later

        if (currentActionList.isEmpty()) { // if we don't already have a list of actions to follow

            //call obs to ts
            TetrisState tempState = observationToTetrisState(o);
        	
//			System.out.println("Start state");
//			System.out.println(tempState);
//        	ArrayList<TetrisStateActionPair> forDebugging = new ArrayList<TetrisStateActionPair>();

            boolean currentWatch = CommonConstants.watch;
            CommonConstants.watch = false;

            HashSet<TetrisStateActionPair> tetrisStateHolder = TetrisAfterStateGenerator.generateAfterStates(tempState);
            CommonConstants.watch = currentWatch;

            ArrayList<Pair<Double, List<Integer>>> outputPairs = new ArrayList<Pair<Double, List<Integer>>>(); //arraylist to hold the actions and outputs for later

            double[] outputs;
            //for(pairs in the set){
            for (TetrisStateActionPair i : tetrisStateHolder) {
                // Basic features
                double[] inputs = MMNEAT.rlGlueExtractor.extract(i.t1.get_observation());
                // Scaled to range [0,1] for the neural network
                double[] inputsScaled = MMNEAT.rlGlueExtractor.scaleInputs(inputs);

                policy.flush(); // remove recurrent activation
                //	outputs = constultPolicy(features) REMEMBER outputs is an array of 1
                outputs = this.consultPolicy(inputsScaled);
                // Associate the policy's score for the after state with the list of actions leading to it
                Pair<Double, List<Integer>> tempPair = new Pair<Double, List<Integer>>(outputs[0], i.t2);
                outputPairs.add(tempPair);
//                forDebugging.add(i);
            }

            double[] outputForArgmax = new double[outputPairs.size()];
            for (int i = 0; i < outputForArgmax.length; i++) {
                outputForArgmax[i] = outputPairs.get(i).t1;
            }

            int index = StatisticsUtilities.argmax(outputForArgmax); //action = argmax(list)

//            System.out.println("Chosen state");
//            System.out.println("Score: " + outputPairs.get(index).t1);
//            System.out.println(forDebugging.get(index).t1.toString(false));
//            System.out.println("Actions1: " + forDebugging.get(index).t2);
//            System.out.println("Actions2: " + outputPairs.get(index).t2);
//            MiscUtil.waitForReadStringAndEnterKeyPress();
            
            for (int k = 0; k < outputPairs.get(index).t2.size(); k++) {
                currentActionList.add(outputPairs.get(index).t2.get(k)); //this should add the next action to the linked list in the proper order
            }
            currentActionList.add(TetrisState.NONE); // Let the block settle and a new one spawns
        }

        Action action = new Action(TSO.getNumDiscreteActionDims(), TSO.getNumContinuousActionDims()); //moved this from before the action call, because both actions may need it -Gab
        // Current action is next one in list
        action.intArray[0] = currentActionList.get(0);
        currentActionList.remove(0);

        return action;
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
        ts.currentX = o.intArray[TetrisState.TETRIS_STATE_CURRENT_X_INDEX]; // adds the Observation's X to tempState
        ts.currentY = o.intArray[TetrisState.TETRIS_STATE_CURRENT_Y_INDEX]; // adds the Observation's Y to tempState
        ts.currentRotation = o.intArray[TetrisState.TETRIS_STATE_CURRENT_ROTATION_INDEX]; // adds the Observation's rotation to tempState
        for (int p = 0; p < TetrisState.TETRIS_STATE_NUMBER_POSSIBLE_BLOCKS; p++) {
            if (o.intArray[ts.worldState.length + p] == 1) { // this checks for the current block Id
                ts.currentBlockId = p;
            }
        }
        // replaces the new TetrisState's worldState with the Observation's worldState
        System.arraycopy(o.intArray, 0, ts.worldState, 0, ts.worldState.length); 
        blotMobilePiece(ts); // blots out the mobile piece on the board
        return ts;
    }

    /**
     * Number of outputs is one because the network returns a utility score.
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
     * @param ts TetrisState
     */
    public static void blotMobilePiece(TetrisState ts) {
        Vector<TetrisPiece> possibleBlocks = ts.possibleBlocks;
        int[][] mobilePiece = possibleBlocks.get(ts.currentBlockId).getShape(ts.currentRotation);
        for (int x = 0; x < mobilePiece.length; x++) {
            for (int y = 0; y < mobilePiece[x].length; y++) {
                if (mobilePiece[x][y] != 0) {
                    int linearIndex = (ts.currentY + y) * TetrisState.worldWidth + (ts.currentX + x);
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
     * This method takes in the last observation and returns the number of
     * blocks left in the game over screen. This is used for a fitness function
     * at the end of the game.
     *
     * @return blockCount, number of blocks on the screen upon game over
     */
    public int getNumberOfBlocksInLastState() {
        int blockCount = 0;
        int worldLength = TetrisState.worldHeight * TetrisState.worldWidth;
        for (int i = 0; i < worldLength; i++) { // replaces tempState's worldState with the Observation's worldState
            if (lastObs.intArray[i] > 0) {
                blockCount++;
            }
        }
        return blockCount;
    }
}

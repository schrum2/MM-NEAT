/*
 * Feature extraction based on Bertsekas and Tsitsiklis.
 * Doesn't really suit my needs, because it depends on afterstate
 * representation. Need to either modify my RL-Glue usage
 * to handle after states, or come up with better features
 */
package edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris;

import edu.utexas.cs.nn.tasks.rlglue.featureextractors.FeatureExtractor;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import java.util.ArrayList;
import org.rlcommunity.environments.tetris.TetrisPiece;
import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 *
 * @author Jacob Schrum, Gabby Gonzalez
 */
public class BertsekasTsitsiklisTetrisExtractor implements FeatureExtractor {

    protected final int worldWidth;
    protected final int worldHeight;

//    public final int NUM_COLUMN_FEATURES;
//    public final int NUM_DIFF_FEATURES;
//    public final int NUM_TOTAL_FEATURES;
    
    ArrayList<TetrisPiece> possibleBlocks = new ArrayList<TetrisPiece>(7);

    /**
     * Calls for the width and height to initialize
     */
    public BertsekasTsitsiklisTetrisExtractor() {
        this(TetrisState.worldWidth, TetrisState.worldHeight);
    }

    /**
     * Initializes the extractor with the given world height and width
     * Also adds all possible block shapes to the array list PossibleBlocks
     * @param width
     * @param height
     */
    public BertsekasTsitsiklisTetrisExtractor(int width, int height) {
        this.worldWidth = width;
        this.worldHeight = height;
//        this.NUM_COLUMN_FEATURES = width;
//        this.NUM_DIFF_FEATURES = width - 1;
//        this.NUM_TOTAL_FEATURES = NUM_COLUMN_FEATURES + NUM_DIFF_FEATURES + 3;
        

        possibleBlocks.add(TetrisPiece.makeLine());
        possibleBlocks.add(TetrisPiece.makeSquare());
        possibleBlocks.add(TetrisPiece.makeTri());
        possibleBlocks.add(TetrisPiece.makeSShape());
        possibleBlocks.add(TetrisPiece.makeZShape());
        possibleBlocks.add(TetrisPiece.makeLShape());
        possibleBlocks.add(TetrisPiece.makeJShape());
    }

    /**
     * Calculate the linear array position from (x,y) components based on
     * worldWidth. Package level access so we can use it in tests.
     *
     * @param x
     * @param y
     * @return
     */
    protected int calculateLinearArrayPosition(int x, int y) {
        int returnValue = y * worldWidth + x;
        return returnValue;
    }

    /**
     * Calculates the total number of features. 
     * Equation is set up in such a way to allow for an array of inputs to take in certain inputs.
     */
    public int numFeatures() {
        return worldWidth // column heights 
        		+ (worldWidth - 1) // column differences 
        		+ 3; // MaxHeight, Holes, Bias
    }

    /**
     * Extract focuses on finding the holes in the current world state. A hole being a non occupied space beneath an occupied space.
     * @param observation o
     * @return array of
     */
    public double[] extract(Observation o) {
        double[] inputs = new double[numFeatures()]; // numFeatures gives us "worldWidth + (worldWidth - 1) + 3"

        int[] worldState = new int[worldWidth * worldHeight]; // creates the linear array version of the game world
        System.arraycopy(o.intArray, 0, worldState, 0, worldWidth * worldHeight);
        double[] blockIndicator = new double[possibleBlocks.size()];
        for (int i = 0; i < possibleBlocks.size(); i++) { // for each possible block, add whether or not it is falling to the blockIndicator array
            blockIndicator[i] = o.intArray[worldState.length + i]; // this sets the block indicator spots as either 0 or 1 according to which block is currently falling (1)
        }
        //blotMobilePiece(worldState, StatisticsUtilities.argmax(blockIndicator), o.intArray[TetrisState.TETRIS_STATE_CURRENT_X_INDEX], o.intArray[TetrisState.TETRIS_STATE_CURRENT_Y_INDEX], o.intArray[TetrisState.TETRIS_STATE_CURRENT_ROTATION_INDEX]); 
        //This is commented out because it messes up the scores of afterstates where the mobile piece is at the bottome and in the process of being written. -Gab
        
        int in = 0;

        double holes = 0;
        int firstHeightIndex = in;
        double maxHeight = 0;
        for (int i = 0; i < worldWidth; i++) {
            double h = columnHeight(i, worldState);
            holes += columnHoles(i, worldState, (int) h);
            maxHeight = Math.max(h, maxHeight);
            inputs[in++] = h;
        }

        for (int i = 0; i < worldWidth - 1; i++) {
            inputs[in++] = Math.abs(inputs[firstHeightIndex + i] - inputs[firstHeightIndex + i + 1]);
        }

        inputs[in++] = maxHeight;

        // Should heights then be normalized wrt worldHeight?

        inputs[in++] = holes;
        inputs[in++] = 1; //bias

        // Scaled to range [0,1] for the neural network
        
        return inputs;
    }

    public String[] featureLabels() {
        String[] labels = new String[numFeatures()];
        int in = 0;
        for (int i = 0; i < worldWidth; i++) {
            labels[in++] = "Column " + i + " Height";
        }
        for (int i = 0; i < worldWidth - 1; i++) {
            labels[in++] = "Columns " + i + "/" + (i + 1) + " Difference";
        }
        labels[in++] = "Max Column Height";
        labels[in++] = "Number of Holes";
        labels[in++] = "Bias";

        return labels;
    }

    protected int columnHeight(int x, int[] intArray) {
        int y = 0;
        while (y < worldHeight && intArray[calculateLinearArrayPosition(x, y)] == 0) {
            y++;
        }
        return worldHeight - y;
    }

    protected int columnHoles(int x, int[] intArray, int height) {
        int holes = 0;
        for (int y = worldHeight - height; y < worldHeight; y++) {
            if (intArray[calculateLinearArrayPosition(x, y)] == 0) {
                holes++;
            }
        }
        return holes;
    }

    /**
     * This method removes the current falling block from the world state in order to gather information on features based solely on the placed blocks, not the currently falling one.
     * @param worldState
     * @param blockId
     * @param blockX
     * @param blockY
     * @param blockRotation
     */
    protected void blotMobilePiece(int[] worldState, int blockId, int blockX, int blockY, int blockRotation) {
        int[][] mobilePiece = this.possibleBlocks.get(blockId).getShape(blockRotation);
        for (int x = 0; x < mobilePiece.length; x++) {
            for (int y = 0; y < mobilePiece[x].length; y++) {
                if (mobilePiece[x][y] != 0) {
                    int linearIndex = calculateLinearArrayPosition(blockX + x, blockY + y);
                    if (linearIndex < 0) {
                        System.err.printf("Bogus linear index %d for %d + %d, %d + %d\n", linearIndex, blockX, x, blockY, y);
                        Thread.dumpStack();
                        System.exit(1);
                    }
                    worldState[linearIndex] = 0;
                }
            }
        }
    }

    /**
     * Takes raw features and scales them to range [0,1] for neural network input.
     *
     * @param inputs
     * @return scaled inputs
     */
	@Override
	public double[] scaleInputs(double[] inputs) {
		double[] next = new double[inputs.length];
        int height_features = TetrisState.worldWidth + (TetrisState.worldWidth - 1) + 1; // height values (10), height differences (9), and max height (1)
        for (int i = 0; i < height_features; i++) {
            next[i] = inputs[i] / TetrisState.worldHeight;
        }
        next[height_features] = inputs[height_features] / TetrisState.TETRIS_STATE_NUMBER_WORLD_GRID_BLOCKS; // scales down the number of holes in relation to the whole of the board
        next[height_features + 1] = 1.0; // bias is 1, so not scaling
        return next;
	}
}

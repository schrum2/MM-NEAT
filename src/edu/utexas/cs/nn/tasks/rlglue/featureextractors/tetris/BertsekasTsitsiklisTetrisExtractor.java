package edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris;

import edu.utexas.cs.nn.tasks.rlglue.featureextractors.FeatureExtractor;
import java.util.ArrayList;
import org.rlcommunity.environments.tetris.TetrisPiece;
import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Observation;

/*
 * Feature extraction based on Bertsekas and Tsitsiklis.
 * Depends on afterstates.
 * @author Jacob Schrum, Gabby Gonzalez
 */
public class BertsekasTsitsiklisTetrisExtractor implements FeatureExtractor {

	protected final int worldWidth;
	protected final int worldHeight;

	ArrayList<TetrisPiece> possibleBlocks = new ArrayList<TetrisPiece>(7);

	/**
	 * Calls for the width and height to initialize
	 */
	public BertsekasTsitsiklisTetrisExtractor() {
		this(TetrisState.worldWidth, TetrisState.worldHeight);
	}

	/**
	 * Initializes the extractor with the given world height and width Also adds
	 * all possible block shapes to the array list PossibleBlocks
	 * 
	 * @param width
	 * @param height
	 */
	public BertsekasTsitsiklisTetrisExtractor(int width, int height) {
		this.worldWidth = width;
		this.worldHeight = height;

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
	 * Calculates the total number of features. Equation is set up in such a way
	 * to allow for an array of inputs to take in certain inputs.
	 */
        @Override
	public int numFeatures() {//TODO change if hnt true, remove bias
		return worldWidth // column heights
		     + (worldWidth - 1) // column differences
		     + 3; // MaxHeight, Holes, Bias
	}

	/**
	 * Extract focuses on finding the holes in the current world state. A hole
	 * being a non occupied space beneath an occupied space.
	 * 
	 * @param o Observation 
	 * @return array of inputs
	 */
        @Override
	public double[] extract(Observation o) {//TODO change based on hnt
		// numFeatures gives us "worldWidth + (worldWidth - 1) + 3"
		double[] inputs = new double[numFeatures()]; 

		// creates the linear array version of the game world
		int[] worldState = new int[worldWidth * worldHeight]; 
		System.arraycopy(o.intArray, 0, worldState, 0, worldWidth * worldHeight);

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
		inputs[in++] = holes;
		inputs[in++] = 1; // bias

		return inputs;
	}

	/**
	 * Returns an array of feature labels given the current extractor
	 */
        @Override
	public String[] featureLabels() {//TODO 
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

	/**
	 * Finds the height of a column based on the current row and worldstate
	 * @param x
	 * @param intArray
	 * @return world height
	 */
	protected int columnHeight(int x, int[] intArray) {
		int y = 0;
		while (y < worldHeight && intArray[calculateLinearArrayPosition(x, y)] == 0) {
			y++;
		}
		return worldHeight - y;
	}

	/**
	 * Finds the number of holes in a given column for the worldstate
	 * @param x
	 * @param intArray
	 * @param height
	 * @return holes in a given column
	 */
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
	 * This method removes the current falling block from the world state in
	 * order to gather information on features based solely on the placed
	 * blocks, not the currently falling one.
	 * 
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
	 * Takes raw features and scales them to range [0,1] for neural network
	 * input.
	 *
	 * @param inputs
	 * @return scaled inputs
	 */
	@Override
	public double[] scaleInputs(double[] inputs) {//TODO 
		double[] next = new double[inputs.length];
		// height values (10), height differences (9), and max height (1)
		int height_features = TetrisState.worldWidth + (TetrisState.worldWidth - 1) + 1; 
		for (int i = 0; i < height_features; i++) {
			next[i] = inputs[i] / TetrisState.worldHeight;
		}
		// scales down the number of holes in relation to the whole of the board
		next[height_features] = inputs[height_features] / TetrisState.TETRIS_STATE_NUMBER_WORLD_GRID_BLOCKS; 
		next[height_features + 1] = 1.0; // bias is 1, so not scaling
		return next;
	}
}

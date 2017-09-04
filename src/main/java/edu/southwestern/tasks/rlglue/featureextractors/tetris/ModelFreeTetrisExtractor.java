package edu.southwestern.tasks.rlglue.featureextractors.tetris;

import edu.southwestern.util.stats.StatisticsUtilities;

import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * This feature extractor was supposes to help learn Tetris without doing
 * the after-state calculations, but it doesn't work very well.
 * 
 * @author Jacob Schrum
 */
public class ModelFreeTetrisExtractor extends BertsekasTsitsiklisTetrisExtractor {

	/**
	 * Returns the number of features for this extractor
         * @return features from parent, plus 5 more about the falling block
	 */
	@Override
	public int numFeatures() {
		return super.numFeatures() + 5; // may need to change the 5 here
	}

	/**
	 * Scales the given inputs to fit the range [0 to 1]
	 * @param inputs double[]
	 * @return scaled inputs double[]
	 */
	@Override
	public double[] scaleInputs(double[] inputs) {
		double[] original = super.scaleInputs(inputs);
		int originalFeatures = TetrisState.worldWidth + (TetrisState.worldWidth - 1) + 3; 
		for (int i = originalFeatures; i < inputs.length; i++) {
			original[i] = inputs[i] / TetrisState.worldHeight;
		}
		return original;
	}

	/**
	 * Extract extends from BertsekasTsitsiklisTetrisExtractor and adds 
	 * piece distances from the written blocks below
	 * 
	 * @param o Observation 
	 * @return array of inputs
	 */
	@Override
	public double[] extract(Observation o) {
		double[] base = super.extract(o);

		int[] worldState = new int[worldWidth * worldHeight];
		System.arraycopy(o.intArray, 0, worldState, 0, worldWidth * worldHeight);
		double[] blockIndicator = new double[possibleBlocks.size()];
		for (int i = 0; i < possibleBlocks.size(); i++) {
			blockIndicator[i] = o.intArray[worldState.length + i];
		}

		int blockId = StatisticsUtilities.argmax(blockIndicator);
		int blockX = o.intArray[TetrisState.TETRIS_STATE_CURRENT_X_INDEX];
		int blockY = o.intArray[TetrisState.TETRIS_STATE_CURRENT_Y_INDEX];
		int blockRotation = o.intArray[TetrisState.TETRIS_STATE_CURRENT_ROTATION_INDEX];

		double[] added = new double[5];
		for (int i = 0; i < added.length; i++) {
			added[i] = pieceDistanceFromBlocks(worldState, blockId, blockX, blockY, blockRotation, i);
		}

		double[] combined = new double[super.numFeatures() + added.length];
		System.arraycopy(base, 0, combined, 0, super.numFeatures());
		System.arraycopy(added, 0, combined, super.numFeatures(), added.length);
	
		return combined;
	}
	
	/**
	 * The current worldState needs to blot out the floating piece so it
	 * does not interfere with scoring
	 * 
	 * @param worldState
	 * @param blockId
	 * @param blockX
	 * @param blockY
	 * @param blockRotation
	 * @param pieceColumn
	 * @return
	 */
	protected int pieceDistanceFromBlocks(int[] worldState, int blockId, int blockX, int blockY, int blockRotation, int pieceColumn) {
		int[][] mobilePiece = this.possibleBlocks.get(blockId).getShape(blockRotation);
		boolean hasBottom = false;
		int bottom = 0;
		for (int i = mobilePiece[pieceColumn].length - 1; i >= 0; i--) {
			if (mobilePiece[pieceColumn][i] != 0) {
				hasBottom = true;
				bottom = i;
				break;
			}
		}

		if (!hasBottom) {
			return this.worldHeight * 2;
		}

		int absoluteBottom = blockY + bottom;
		int h = columnHeight(blockX + pieceColumn, worldState);

		return (worldHeight - h) - absoluteBottom;
	}

}

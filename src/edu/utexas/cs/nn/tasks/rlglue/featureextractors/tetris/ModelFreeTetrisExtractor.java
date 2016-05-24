/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris;

import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 *
 * @author Jacob Schrum
 */
public class ModelFreeTetrisExtractor extends BertsekasTsitsiklisTetrisExtractor {

	@Override
	public int numFeatures() {
		return super.numFeatures() + 5; //may need to change the 5 here
	}

	@Override
	public double[] scaleInputs(double[] inputs) {
		double[] original = super.scaleInputs(inputs);
		int originalFeatures = TetrisState.worldWidth + (TetrisState.worldWidth - 1) + 3; //Number of original features
		for (int i = originalFeatures; i < inputs.length; i++) {
			original[i] = inputs[i] / TetrisState.worldHeight;
		}
		return original;
	}

	
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
		//blotMobilePiece(worldState, blockId, blockX, blockY, blockRotation);
		//This needs to be commented here too! Causes problems with the afterstate. -Gab
		
		double[] added = new double[5];
		for (int i = 0; i < added.length; i++) {
			added[i] = pieceDistanceFromBlocks(worldState, blockId, blockX, blockY, blockRotation, i);
		}

		double[] combined = new double[super.numFeatures() + added.length];
		System.arraycopy(base, 0, combined, 0, super.numFeatures());
		System.arraycopy(added, 0, combined, super.numFeatures(), added.length);

		//System.out.println(Arrays.toString(combined));
        
		return combined;
	}

	/*
	 * worldState used needs to be after the floating piece is blotted out
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

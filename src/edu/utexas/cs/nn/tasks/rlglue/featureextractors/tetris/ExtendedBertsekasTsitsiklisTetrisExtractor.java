package edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris;

import java.util.Arrays;

import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Observation;

import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

public class ExtendedBertsekasTsitsiklisTetrisExtractor extends BertsekasTsitsiklisTetrisExtractor {
    
	@Override
	public int numFeatures() {
		return super.numFeatures() + TetrisState.worldWidth;
		// column heights + column differences + Max Height + total holes + bias + column holes
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
	  public String[] featureLabels() {
        String[] labels = super.featureLabels();
        int originalFeatures = TetrisState.worldWidth + (TetrisState.worldWidth - 1) + 3; //Number of original features
        for (int i = originalFeatures; i < labels.length; i++) {
            labels[i] = "Column " + i + " Holes";
        }
        return labels;
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

//		int blockId = StatisticsUtilities.argmax(blockIndicator);
//		int blockX = o.intArray[TetrisState.TETRIS_STATE_CURRENT_X_INDEX];
//		int blockY = o.intArray[TetrisState.TETRIS_STATE_CURRENT_Y_INDEX];
//		int blockRotation = o.intArray[TetrisState.TETRIS_STATE_CURRENT_ROTATION_INDEX];
		//blotMobilePiece(worldState, blockId, blockX, blockY, blockRotation);
		//This needs to be commented here too! Causes problems with the afterstate. -Gab

		double[] added = new double[worldWidth];
        for (int i = 0; i < added.length; i++) { // finds the number of holes for the current column and adds that to Added
            double h = columnHeight(i, worldState);
            added[i] = columnHoles(i, worldState, (int) h);
        }

		double[] combined = new double[super.numFeatures() + added.length];
		System.arraycopy(base, 0, combined, 0, super.numFeatures());
		System.arraycopy(added, 0, combined, super.numFeatures(), added.length);

		//System.out.println(Arrays.toString(combined));
		return combined;
	}
}

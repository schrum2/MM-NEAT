package edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris;

import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Observation;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.rlglue.featureextractors.FeatureExtractor;

/**
 * Primarily designed to be used by HyperNEAT.
 * Simple provides raw information about the world state as features.
 * 
 * @author Lauren Gillespie
 */
public class RawTetrisStateExtractor implements FeatureExtractor {

	/**
	 * One feature for each block in the world state
	 * @return 
	 */
	@Override
	public int numFeatures() {
		return CommonConstants.splitHyperNEATTetrisInputs ? 
				TetrisState.worldHeight * TetrisState.worldWidth * 2 :
					TetrisState.worldHeight * TetrisState.worldWidth;
	}

	/**
	 * An array containing a 1 if a block is present, and a 0 otherwise.
	 * @param o
	 * @return inputs
	 */
	@Override
	public double[] extract(Observation o) {
		boolean negative = Parameters.parameters.booleanParameter("absenceNegative");
		boolean senseHoles = Parameters.parameters.booleanParameter("senseHolesDifferently");
		boolean split = CommonConstants.splitHyperNEATTetrisInputs;
		int worldSize = TetrisState.worldWidth * TetrisState.worldHeight;
		if(negative && senseHoles) {
			System.out.println("can't have absenceNegative and senseHoles in the same experiment!");
			System.exit(1);
		} else if(split && !senseHoles){
			System.out.println("Split must be used with sense holes!");
			System.exit(1);
		}
		// o.intArray below contains the worldState in the first worldSize indices.
		// No index after that point should be accessed.
		double[] result = new double[split ? worldSize * 2 : worldSize];
		assert (split && result.length == 2*worldSize) || result.length == worldSize : "result array was not instantiated properly!";
		for (int i = 0; i < worldSize; i++) {
			if(senseHoles && TetrisExtractorUtil.isHole(i, o.intArray)){
				if(split) {
					result[i] = 0;
					result[i + worldSize] = -1;
				} else {
					result[i] = -1;
				}
			} else if(Math.signum(o.intArray[i]) == 0){
				int temp = negative ? -1 : 0;
				result[i] = temp;
			} else if(o.intArray[i] > 0){
				result[i] = Math.signum(o.intArray[i]);
			}
		}
		return result;
	}

	/**
	 * Features are simply named after their coordinates on the screen
	 * @return array of feature labels
	 */
	@Override
	public String[] featureLabels() {
		String[] labels = new String[numFeatures()];
		int in = 0;
		for (int i = 0; i < TetrisState.worldHeight; i++) {
			for (int j = 0; j < TetrisState.worldWidth; j++) {
				labels[in++] = "(" + j + ", " + i + ") occupied?";
			}
		}
		if(CommonConstants.splitHyperNEATTetrisInputs) {
			for(int i1 =  0; i1 < TetrisState.worldHeight; i1++) {
				for(int j = 0; j < TetrisState.worldWidth; j++) {
					labels[in++] = "(" + j + ", " + i1 + ") hole?";
				}
			}
		}
		return labels;
	}

	/**
	 * No scaling needed since all values are 0 or 1
	 * @param inputs original inputs
	 * @return original inputs (identity function)
	 */
	@Override
	public double[] scaleInputs(double[] inputs) {
		return inputs;
	}

}

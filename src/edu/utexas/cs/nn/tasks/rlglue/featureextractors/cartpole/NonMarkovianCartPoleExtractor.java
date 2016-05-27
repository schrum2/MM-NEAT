package edu.utexas.cs.nn.tasks.rlglue.featureextractors.cartpole;

import edu.utexas.cs.nn.tasks.rlglue.featureextractors.FeatureExtractor;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 *
 * @author Jacob Schrum
 */
public class NonMarkovianCartPoleExtractor implements FeatureExtractor {

    @Override
    public int numFeatures() {
        return 2;
    }

    @Override
    public double[] extract(Observation o) {
        // Only use x and theta, and not the derivatives
        return new double[]{o.doubleArray[0], o.doubleArray[2]};
    }

    @Override
    public String[] featureLabels() {
        return new String[]{"Position", "Angle"};
    }

	@Override
	public double[] scaleInputs(double[] inputs) {
		// TODO Auto-generated method stub
		return null;
	}
}

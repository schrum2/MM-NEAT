/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.rlglue.featureextractors.cartpole;

import edu.utexas.cs.nn.tasks.rlglue.featureextractors.FeatureExtractor;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 *
 * @author Jacob Schrum
 */
public class NonMarkovianCartPoleExtractor implements FeatureExtractor {

    public int numFeatures() {
        return 2;
    }

    public double[] extract(Observation o) {
        // Only use x and theta, and not the derivatives
        return new double[]{o.doubleArray[0], o.doubleArray[2]};
    }

    public String[] featureLabels() {
        return new String[]{"Position", "Angle"};
    }
}

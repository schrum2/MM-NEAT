/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.rlglue.featureextractors;

import org.rlcommunity.rlglue.codec.types.Observation;

/**
 *
 * @author Jacob Schrum
 */
public interface FeatureExtractor {

    public int numFeatures();

    public double[] extract(Observation o);

    public String[] featureLabels();
}

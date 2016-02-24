/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.ensemble;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
 *
 * @author Jacob Schrum
 */
public class PreferenceNeuronArbitrator extends MsPacManEnsembleArbitrator {

    public PreferenceNeuronArbitrator() {
        Parameters.parameters.setBoolean("externalPreferenceNeurons", true);
    }

    /**
     * The last index in each preference list will be the actual preference
     * neuron output. Pick action for whichever of these is the largest.
     */
    public double[] newDirectionalPreferences(GameFacade game, double[][] preferences) {
        double[] preferenceOutputs = ArrayUtil.column(preferences, preferences.length - 1);
        int netChoice = StatisticsUtilities.argmax(preferenceOutputs);
        double[] actionPreferences = ArrayUtil.portion(preferences[netChoice], 0, preferences[netChoice].length - 2);
        //int action = StatisticsUtilities.argmax(actionPreferences);
        return actionPreferences;
    }
}

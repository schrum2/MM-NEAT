/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.objectives.fitnessassignment;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;

/**
 *
 * @author Jacob Schrum
 */
public class SpecificGhostsMap implements FitnessToModeMap {

    public int[] associatedFitnessScores() {
        int[] result = new int[CommonConstants.numActiveGhosts];
        System.arraycopy(SPECIFIC_GHOSTS, 0, result, 0, CommonConstants.numActiveGhosts);
        return result;
    }
}

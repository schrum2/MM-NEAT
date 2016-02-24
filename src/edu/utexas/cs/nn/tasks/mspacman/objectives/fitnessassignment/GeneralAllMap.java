/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.objectives.fitnessassignment;

import edu.utexas.cs.nn.parameters.Parameters;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class GeneralAllMap implements FitnessToModeMap {

    private final int members;

    public GeneralAllMap() {
        this(Parameters.parameters.integerParameter("numCoevolutionSubpops"));
    }

    public GeneralAllMap(int members) {
        this.members = members;
    }

    public int[] associatedFitnessScores() {
        int[] result = new int[members];
        Arrays.fill(result, NO_PREFERENCE);
        return result;
    }
}

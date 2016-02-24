/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.objectives.fitnessassignment;

/**
 *
 * @author Jacob Schrum
 */
public class ActiveGhostsPillsMap implements FitnessToModeMap {

    public int[] associatedFitnessScores() {
        return new int[]{ACTIVE_GHOST_SCORE, ACTIVE_PILL_SCORE};
    }
}

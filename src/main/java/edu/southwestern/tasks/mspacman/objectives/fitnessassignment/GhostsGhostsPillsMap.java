/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.objectives.fitnessassignment;

/**
 *
 * @author Jacob Schrum
 */
public class GhostsGhostsPillsMap implements FitnessToModeMap {

	public int[] associatedFitnessScores() {
		return new int[] { GHOST_SCORE, GHOST_SCORE, PILL_SCORE };
	}
}

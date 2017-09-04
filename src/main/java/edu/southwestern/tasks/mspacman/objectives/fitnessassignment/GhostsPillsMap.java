/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.objectives.fitnessassignment;

import edu.southwestern.parameters.Parameters;

/**
 *
 * @author Jacob Schrum
 */
public class GhostsPillsMap implements FitnessToModeMap {

	private final boolean levelObjective;

	public GhostsPillsMap() {
		levelObjective = Parameters.parameters.booleanParameter("levelObjective");
	}

	public int[] associatedFitnessScores() {
		return new int[] { levelObjective ? GHOST_AND_LEVEL_COMBO : GHOST_SCORE, PILL_SCORE };
	}
}

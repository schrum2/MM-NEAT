/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.objectives.fitnessassignment;

/**
 *
 * @author Jacob Schrum
 */
public interface FitnessToModeMap {

    public static final int NO_PREFERENCE = 0;
    public static final int GAME_SCORE = 1;
    public static final int PILL_SCORE = 2;
    public static final int GHOST_SCORE = 3;
    public static final int ACTIVE_PILL_SCORE = 4;
    public static final int ACTIVE_GHOST_SCORE = 5;
    public static final int[] SPECIFIC_GHOSTS = new int[]{6, 7, 8, 9};
    public static final int PROPER_POWER_PILL_SCORE = 10;
    public static final int IMPROPER_POWER_PILL_SCORE = 11;
    // Ghost score and proper power pill scores separately
    public static final int PROPER_POWER_PILL_GHOST_COMBO = 12;
    // Ghost score and improper power pill scores separately
    public static final int IMPROPER_POWER_PILL_GHOST_COMBO = 13;
    public static final int LURING_FITNESS = 14;
    public static final int[] SPECIFIC_LEVELS = new int[]{15, 16, 17, 18};
    // Pills, but punished for power pills eaten when ghosts are far away
    public static final int PILL_AND_NO_POWER_PILL_COMBO = 19;
    public static final int LEVEL_SCORE = 20;
    // Combines level with ghost score since there is low pressure to beat levels otherwise
    public static final int GHOST_AND_LEVEL_COMBO = 21;

    public int[] associatedFitnessScores();
}

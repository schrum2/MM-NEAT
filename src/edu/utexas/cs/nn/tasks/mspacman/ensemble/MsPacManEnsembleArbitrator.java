/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.ensemble;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
 * An ensemble arbitrator takes the directional preferences of several agents,
 * and arbitrates between them to choose which action to perform.
 *
 * @author Jacob Schrum
 */
public abstract class MsPacManEnsembleArbitrator {

    /**
     * Given the outputs of several different networks (or modes),
     * decide one (Ms. Pac-Man) action to perform based on all of their
     * preferences.
     * 
     * @param preferences outputs of several networks and/or modes: preferences[mode][direction]
     * @return a Pac-Man action (0=UP, 1=RIGHT, 2=DOWN, 3=LEFT)
     */
    public int choose(GameFacade game, double[][] preferences) {
        return StatisticsUtilities.argmax(newDirectionalPreferences(game, preferences));
    }
    
    public abstract double[] newDirectionalPreferences(GameFacade game, double[][] preferences);
}

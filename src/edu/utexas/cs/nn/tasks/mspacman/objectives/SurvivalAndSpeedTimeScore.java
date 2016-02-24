/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;

/**
 *
 * @author Jacob Schrum
 */
public class SurvivalAndSpeedTimeScore<T extends Network> extends MsPacManObjective<T> {
    private final int pacmanMaxLevel;

    public SurvivalAndSpeedTimeScore(){
        pacmanMaxLevel = Parameters.parameters.integerParameter("pacmanMaxLevel");
    }
    
    public double fitness(Organism<T> individual) {
        int level = g.getCurrentLevel();
        if(g.getPacmanNumberOfLivesRemaining() == 0){
            // Pacman died, so surviving longer is rewarded
            return -(CommonConstants.pacManLevelTimeLimit * pacmanMaxLevel) + (level*CommonConstants.pacManLevelTimeLimit) + g.getCurrentLevelTime();
        } else if(level < pacmanMaxLevel) {
            // Ran out of time before beating levels
            return 0;
        } else {
            // Beat all levels, so reward speed
            return (CommonConstants.pacManLevelTimeLimit * pacmanMaxLevel) - g.getTotalTime();
        }
    }
}

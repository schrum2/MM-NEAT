/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class EdibleTimeParameter<T extends Network> extends MsPacManObjective<T> {

    public double fitness(Organism<T> individual) {
        return Constants.EDIBLE_TIME;
    }
}

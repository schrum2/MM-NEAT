/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public abstract class MsPacManObjective<T extends Network> implements FitnessFunction<T> {

    protected GameFacade g;

    public double score(GameFacade g, Organism<T> o) {
        this.g = g;
        return fitness(o);
    }

    public double minScore() {
        return 0;
    }
}

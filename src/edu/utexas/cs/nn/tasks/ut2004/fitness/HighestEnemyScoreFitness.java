/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.ut2004.fitness;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;

/**
 * The best enemy score, as a negative fitness
 *
 * @author Jacob Schrum
 */
public class HighestEnemyScoreFitness<T extends Network> extends UT2004FitnessFunction<T> {

    public double fitness(Organism<T> individual) {
        return -game.bestEnemyScore();
    }
}

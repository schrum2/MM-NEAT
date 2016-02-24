/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.util.random.RandomNumbers;

/**
 *
 * @author Jacob Schrum
 */
public class RandomScore<T extends Network> extends MsPacManObjective<T> {

    public double fitness(Organism<T> individual) {
        return RandomNumbers.randomGenerator.nextDouble();
    }
}

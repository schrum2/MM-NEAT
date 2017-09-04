/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;
import edu.southwestern.util.random.RandomNumbers;

/**
 *
 * @author Jacob Schrum
 */
public class RandomScore<T extends Network> extends MsPacManObjective<T> {

	public double fitness(Organism<T> individual) {
		return RandomNumbers.randomGenerator.nextDouble();
	}
}

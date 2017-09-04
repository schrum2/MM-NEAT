/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.ut2004.fitness;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 *
 * @author Jacob Schrum
 */
public class ScoreFitness<T extends Network> extends UT2004FitnessFunction<T> {

	public double fitness(Organism<T> individual) {
		return game.getScore();
	}
}

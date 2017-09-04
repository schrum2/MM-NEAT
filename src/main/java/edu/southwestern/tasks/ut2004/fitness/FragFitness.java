/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.ut2004.fitness;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;

/**
 *
 * @author Jacob Schrum
 */
public class FragFitness<T extends Network> extends UT2004FitnessFunction<T> {

	public double fitness(Organism<T> individual) {
		return game.getFrags();
	}
}

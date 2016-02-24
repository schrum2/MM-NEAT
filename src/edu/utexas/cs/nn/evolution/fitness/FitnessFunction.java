package edu.utexas.cs.nn.evolution.fitness;

import edu.utexas.cs.nn.evolution.Organism;

/**
 *
 * @author Jacob Schrum
 */
public interface FitnessFunction<T> {

    public double fitness(Organism<T> individual);
}

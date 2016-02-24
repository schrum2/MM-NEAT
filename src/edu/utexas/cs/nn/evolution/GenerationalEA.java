package edu.utexas.cs.nn.evolution;

import edu.utexas.cs.nn.tasks.Task;

/**
 *
 * @author Jacob Schrum
 */
public interface GenerationalEA {

    public int currentGeneration();

    /**
     * Number of times that an individual is evaluated, where a single
     * evaluation can possibly consist of several trials.
     * @return number of evals in the generation
     */
    public int evaluationsPerGeneration();
    
    public Task getTask();
}

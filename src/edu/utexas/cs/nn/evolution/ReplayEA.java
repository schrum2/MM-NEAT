package edu.utexas.cs.nn.evolution;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.log.FitnessLog;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.tasks.Task;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class ReplayEA<T> implements SinglePopulationGenerationalEA<T> {

    public SinglePopulationTask<T> task;
    public int generation;
    protected FitnessLog<T> parentLog;

    public ReplayEA(SinglePopulationTask<T> task, int gen) {
        this.task = task;
        this.generation = gen;
        parentLog = new FitnessLog<T>("parents");
    }

    public Task getTask() {
        return task;
    }

    public int currentGeneration() {
        return generation;
    }

    // Just evaluates. Doesn't actually get next generation, which is loaded from disk
    public ArrayList<Genotype<T>> getNextGeneration(ArrayList<Genotype<T>> parents) {
        ArrayList<Score<T>> parentScores = task.evaluateAll(parents);
        parentLog.log(parentScores, generation);
        generation++;
        return null;
    }

    public void close(ArrayList<Genotype<T>> population) {
        this.parentLog.close();
    }

    // Should never be called
    public ArrayList<Genotype<T>> initialPopulation(Genotype<T> example) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int evaluationsPerGeneration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

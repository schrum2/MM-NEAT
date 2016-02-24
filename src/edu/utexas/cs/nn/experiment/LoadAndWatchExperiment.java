package edu.utexas.cs.nn.experiment;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.nsga2.NSGA2Score;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.util.PopulationUtil;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public final class LoadAndWatchExperiment<T> implements Experiment {

    protected ArrayList<Genotype<T>> population;
    protected SinglePopulationTask<T> task;

    public void init() {
        String lastSavedDir = Parameters.parameters.stringParameter("lastSavedDirectory");
        this.task = (SinglePopulationTask<T>) MMNEAT.task;
        if (lastSavedDir == null || lastSavedDir.equals("")) {
            System.out.println("Nothing to load");
            System.exit(1);
        } else {
            System.out.println("Loading: " + lastSavedDir);
            population = PopulationUtil.load(lastSavedDir);
            if (Parameters.parameters.booleanParameter("onlyWatchPareto")) {
                NSGA2Score<T>[] scores = null;
                try {
                    scores = PopulationUtil.loadScores(Parameters.parameters.integerParameter("lastSavedGeneration"));
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
                PopulationUtil.pruneDownToParetoFront(population, scores);
            }
        }
    }

    public void run() {
        System.out.println("Looking at results for " + task);
        task.evaluateAll(population);
    }

    /*
     * Never called
     */
    public boolean shouldStop() {
        return true;
    }
}

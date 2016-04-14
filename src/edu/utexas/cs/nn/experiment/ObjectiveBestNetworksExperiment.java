package edu.utexas.cs.nn.experiment;

import java.util.ArrayList;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.LonerTask;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.file.FileUtilities;

/**
 * General evolution experiments are meant to save the
 * best genome in each objective to a directory bestObjectives.
 * This experiment loads those genomes and evaluates them.
 *
 * @author Jacob Schrum
 */
public class ObjectiveBestNetworksExperiment<T> implements Experiment {

    private ArrayList<Genotype<T>> genotypes;

    /**
     * Load best performer in each objective (previously saved)
     */
    public void init() {
        String dir = FileUtilities.getSaveDirectory() + "/bestObjectives";
        genotypes = PopulationUtil.load(dir);
    }

    /**
     * Evaluate each individual.
     * Only works for Loner Tasks
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void run() {
        for(int i = 0; i < genotypes.size(); i++) {
            System.out.println("Best in Objective " + i + ": " + genotypes.get(i).getId());
            Score s = ((LonerTask) MMNEAT.task).evaluateOne(genotypes.get(i));
            System.out.println(s);
        }
    }

    public boolean shouldStop() {
        // Will never be called
        return true;
    }
}

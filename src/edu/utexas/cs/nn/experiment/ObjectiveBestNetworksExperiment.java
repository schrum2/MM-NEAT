package edu.utexas.cs.nn.experiment;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.LonerTask;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.file.FileUtilities;
import java.util.ArrayList;
import wox.serial.Easy;

/**
 * Load saved results from coevolution experiment and evaluate every possible
 * team combination to get their scores.
 *
 * @author Jacob Schrum
 */
public class ObjectiveBestNetworksExperiment<T> implements Experiment {

    private ArrayList<Genotype<T>> nets;

    public void init() {
        String dir = FileUtilities.getSaveDirectory() + "/bestObjectives";
        nets = PopulationUtil.load(dir);
    }

    public void run() {
        for(int i = 0; i < nets.size(); i++) {
            System.out.println("Best in Objective " + i + ": " + nets.get(i).getId());
            Score s = ((LonerTask) MMNEAT.task).evaluateOne(nets.get(i));
            System.out.println(s);
        }
    }

    public boolean shouldStop() {
        // Will never be called
        return true;
    }
}

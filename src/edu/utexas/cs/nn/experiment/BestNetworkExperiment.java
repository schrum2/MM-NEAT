package edu.utexas.cs.nn.experiment;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.LonerTask;
import edu.utexas.cs.nn.util.file.FileUtilities;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import wox.serial.Easy;

/**
 * Load saved results from coevolution experiment and evaluate every possible
 * team combination to get their scores.
 *
 * @author Jacob Schrum
 */
public class BestNetworkExperiment implements Experiment {

    private Genotype net;

    public void init() {
        String dir = FileUtilities.getSaveDirectory() + "/bestPacMan";
        net = (Genotype) Easy.load(dir + "/bestPacMan.xml");
    }

    public void run() {
        RandomNumbers.reset();
        Score s = ((LonerTask) MMNEAT.task).evaluateOne(net);
        System.out.println(s);
    }

    public boolean shouldStop() {
        // Will never be called
        return true;
    }
}

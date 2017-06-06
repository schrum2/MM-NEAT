package edu.utexas.cs.nn.experiment.post;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.experiment.Experiment;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.LonerTask;
import edu.utexas.cs.nn.util.file.FileUtilities;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import wox.serial.Easy;

/**
 * This really only works for Ms. Pac-Man (rename it?).
 * 
 * The evolved Ms. Pac-Man network with the best game score from an evolutionary
 * run is loaded and evaluated. This experiment is distinct in that it loads the
 * highest scoring agent, even if evolution was based on other objectives, like
 * pill and ghost score.
 *
 * @author Jacob Schrum
 */
public class BestNetworkExperiment implements Experiment {

	// Will probably always be a TWEANNGenotype, but it doesn't need to be
	@SuppressWarnings("rawtypes")
	private Genotype net;

	@SuppressWarnings("rawtypes")
	@Override
	public void init() {
		String dir = FileUtilities.getSaveDirectory() + "/bestPacMan";
		net = (Genotype) Easy.load(dir + "/bestPacMan.xml");
	}

	// Will always be running the Ms. Pac-Man experiment
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void run() {
		RandomNumbers.reset();
		Score s = ((LonerTask) MMNEAT.task).evaluateOne(net);
		System.out.println(s);
	}

	@Override
	public boolean shouldStop() {
		// Will never be called
		return true;
	}
}

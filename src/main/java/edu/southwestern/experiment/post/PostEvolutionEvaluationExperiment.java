package edu.southwestern.experiment.post;

import edu.southwestern.evolution.ReplayEA;
import edu.southwestern.experiment.evolution.SinglePopulationGenerationalEAExperiment;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.SinglePopulationTask;
import edu.southwestern.util.file.FileUtilities;
import java.io.File;

/**
 * Load and evaluate all members of the final population
 * of a single population experiment.
 * 
 * @author Jacob Schrum
 * @param <T> phenotype
 */
public class PostEvolutionEvaluationExperiment<T> extends SinglePopulationGenerationalEAExperiment<T> {

	String exactLoadDir;
	String loadDirectory;

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		int lastSavedGen = Parameters.parameters.integerParameter("lastSavedGeneration");
		this.ea = new ReplayEA<T>((SinglePopulationTask<T>) MMNEAT.task, lastSavedGen);
		saveDirectory = FileUtilities.getSaveDirectory();
		loadDirectory = Parameters.parameters.stringParameter("base") + "/"
				+ Parameters.parameters.stringParameter("loadFrom")
				+ Parameters.parameters.integerParameter("runNumber");
		if (lastSavedGen == 0) {
			exactLoadDir = loadDirectory + "/initial";
		} else {
			exactLoadDir = loadDirectory + "/gen" + lastSavedGen;
		}
	}

	@Override
	public void run() {
		while (!shouldStop()) {
			this.load(exactLoadDir);
			// Just evaluate/log without worrying about what is returned. Next
			// gen will be loaded from file
			ea.getNextGeneration(population);
			int gen = ea.currentGeneration();
			Parameters.parameters.setInteger("lastSavedGeneration", gen);
			Parameters.parameters.saveParameters();
			// Now load next generation
			exactLoadDir = loadDirectory + "/gen" + gen;
		}
		ea.close(population);
		System.out.println("Finished evolving");
	}

	@Override
	public boolean shouldStop() {
		System.out.println(exactLoadDir + " exists?");
		return !(new File(exactLoadDir).exists());
	}
}

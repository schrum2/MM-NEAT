package edu.southwestern.experiment.post;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.util.file.FileUtilities;
import wox.serial.Easy;

public class ExploreMAPElitesExperiment<T> implements Experiment {
	
	private Genotype<T> genotype;

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		String dir = FileUtilities.getSaveDirectory() + "/archive/" + Parameters.parameters.stringParameter("mapElitesArchiveFile");
		genotype = (Genotype<T>) Easy.load(dir);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void run() {
		Score score = ((LonerTask) MMNEAT.task).evaluateOne(genotype);
		System.out.println(score);
	}

	@Override
	public boolean shouldStop() {
		// TODO Auto-generated method stub
		return true;
	}

}

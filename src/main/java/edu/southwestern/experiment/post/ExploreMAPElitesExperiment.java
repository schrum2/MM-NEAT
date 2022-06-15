package edu.southwestern.experiment.post;

import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.file.Serialization;

/**
 * Launch a single elite from MAP Elites and observe it. This is launched by
 * the postMAPElitesWatch.bat batch file.
 * 
 * @author schrum2
 *
 * @param <T>
 */
public class ExploreMAPElitesExperiment<T> implements Experiment {
	
	private Genotype<T> genotype;
	private ArrayList<Genotype<T>> genotypes;

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		String dir = FileUtilities.getSaveDirectory() + "/archive/" + Parameters.parameters.stringParameter("mapElitesArchiveFile");
		genotype = (Genotype<T>) Serialization.load(dir);
		genotypes.add(genotype);
		
		// save network
		PopulationUtil.saveGraphVizNetworks(genotypes);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void run() {
		Score score = ((LonerTask) MMNEAT.task).evaluateOne(genotype);
		System.out.println(score);
	}

	@Override
	public boolean shouldStop() {
		return true;
	}

}

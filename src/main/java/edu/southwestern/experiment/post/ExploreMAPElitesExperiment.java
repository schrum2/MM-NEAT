package edu.southwestern.experiment.post;

import java.io.FileNotFoundException;
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
	private ArrayList<Genotype<T>> genotypes = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		String dir = FileUtilities.getSaveDirectory() + "/archive/" + Parameters.parameters.stringParameter("mapElitesArchiveFile");
		System.out.println("Load: "+dir);
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

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main("runNumber:0 parallelEvaluations:false base:minecraftaccumulate log:MinecraftAccumulate-VectorCount saveTo:VectorCount shortTimeBetweenMinecraftReads:1000 trials:2 mapElitesArchiveFile:18Blocks-elite.xml io:false netio:false onlyWatchPareto:true printFitness:true animateNetwork:false monitorInputs:true experiment:edu.southwestern.experiment.post.ExploreMAPElitesExperiment logLock:true watchLastBest:false monitorSubstrates:true showCPPN:true substrateGridSize:10 showWeights:true watch:true showNetworks:true inheritFitness:false marioLevelAgent:ch.idsia.ai.agents.human.HumanKeyboardAgent marioStuckTimeout:99999 smartLodeRunnerEnemies:false".split(" "));
	}
}

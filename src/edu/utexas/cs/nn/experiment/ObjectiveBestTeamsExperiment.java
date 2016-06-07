package edu.utexas.cs.nn.experiment;

import java.util.ArrayList;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.CooperativeTask;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.file.FileUtilities;

/**
 * General evolution experiments are meant to save the best genomes in each
 * objective to the bestObjective directories for each population. 
 * This experiment loads those genomes and evaluates them.
 * This experiment also assumes the task is a CooperativeTask
 *
 * @author rollinsa
 * @param <T> Type of evolved phenotype
 */
public class ObjectiveBestTeamsExperiment<T> implements Experiment {

	private ArrayList<ArrayList<Genotype<T>>> genotypes;

	/**
	 * Load best performer in each objective (previously saved),
	 * or load entire past lineage
	 * This is hard coded to work only for Coevolution CooperativeTasks
	 */
	@Override
	public void init() {
		if (Parameters.parameters.booleanParameter("watchLastBestOfTeams")) {
			genotypes = new ArrayList<ArrayList<Genotype<T>>>();
			//loop through each population
			for(int i = 0; i < ((CooperativeTask) MMNEAT.task).numberOfPopulations(); i++){
				genotypes.add(new ArrayList<Genotype<T>>());
				//go for the number of objectives for this population
				for(int j = 0; j < ((CooperativeTask) MMNEAT.task).objectivesPerPopulation()[i]; j++) {
					int lastGen = Parameters.parameters.integerParameter("lastSavedGeneration");
					String file = FileUtilities.getSaveDirectory() + "/pop" + i + "_bestObjectives/gen" + lastGen + "_bestIn"+j+".xml";
					genotypes.get(i).add((Genotype<T>) PopulationUtil.extractGenotype(file));
				}
			}
		}else {
			for(int i = 0; i < ((CooperativeTask) MMNEAT.task).numberOfPopulations(); i++){
				String dir = FileUtilities.getSaveDirectory() + "/pop" + i + "_bestObjectives";
				genotypes.add(PopulationUtil.load(dir));
			}
		}
	}

	/**
	 * Evaluate each individual. 
	 * Hard coded to work only for CooperativeTasks
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void run() {
		for (int i = 0; i < genotypes.size(); i++) {
			for(int j = 0; j < genotypes.get(i).size(); j++){
				System.out.println("Best in Objective " + i + ": " + (genotypes.get(i).get(j).getId()));
				ArrayList<Score> s = ((CooperativeTask) MMNEAT.task).evaluate((Genotype[]) genotypes.get(i).toArray());
				//go through each individual in the team and print their score
				for(int k = 0; k < s.size(); k++)
					System.out.println(s);
			}
		}
		((CooperativeTask) MMNEAT.task).finalCleanup(); // domain cleanup if necessary
	}

	/**
	 * method not used
	 */
	@Override
	public boolean shouldStop() {
		// Will never be called
		return true;
	}
}

package edu.southwestern.experiment.post;

import java.util.ArrayList;
import java.util.Arrays;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.GroupTask;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.CombinatoricUtilities;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.stats.Statistic;

/**
 * General evolution experiments are meant to save the best genomes in each
 * objective to the bestObjective directories for each population. 
 * This experiment loads those genomes and evaluates them.
 * This experiment also assumes the task is a GroupTask
 *
 * @author rollinsa
 */
public class ObjectiveBestTeamsExperiment implements Experiment {

	@SuppressWarnings("rawtypes")
	private ArrayList<ArrayList<Genotype>> genotypes;

	/**
	 * Load best performer in each objective (previously saved),
	 * or load entire past lineage
	 * This is hard coded to work only for Coevolution CooperativeTasks
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void init() {
		genotypes = new ArrayList<ArrayList<Genotype>>();
		//this is for the batch file which specifies the team being evaluated. Must specify same number of team members as number of populations
		if(!(Parameters.parameters.stringParameter("coevolvedNet1").isEmpty())){
			for(int i = 0; i < ((GroupTask) MMNEAT.task).numberOfPopulations(); i++){
				genotypes.add(new ArrayList<Genotype>());
				String file = FileUtilities.getSaveDirectory() + "/pop" + i + "_bestObjectives/" + Parameters.parameters.stringParameter("coevolvedNet" + (i+1));
				genotypes.get(i).add((Genotype) PopulationUtil.extractGenotype(file));
			}
		}else{

			if (Parameters.parameters.booleanParameter("watchLastBestOfTeams")) {
				//loop through each population
				for(int i = 0; i < ((GroupTask) MMNEAT.task).numberOfPopulations(); i++){
					genotypes.add(new ArrayList<Genotype>());
					//go for the number of objectives for this population
					for(int j = 0; j < ((GroupTask) MMNEAT.task).objectivesPerPopulation()[i]; j++) {
						int lastGen = Parameters.parameters.integerParameter("lastSavedGeneration");
						String file = FileUtilities.getSaveDirectory() + "/pop" + i + "_bestObjectives/gen" + lastGen + "_bestIn"+j;
						genotypes.get(i).add((Genotype) PopulationUtil.extractGenotype(file));
					}
				}
			}else {
				for(int i = 0; i < ((GroupTask) MMNEAT.task).numberOfPopulations(); i++){
					String dir = FileUtilities.getSaveDirectory() + "/pop" + i + "_bestObjectives";
					genotypes.add(PopulationUtil.removeListGenotypeType(PopulationUtil.load(dir)));
				}
			}
		}
	}

	/**
	 * Evaluate each individual. 
	 * Hard coded to work only for GroupTasks
	 * (This experiment only works for cooperative, competitive or competitive and cooperative coevolution)
	 */
	@SuppressWarnings({ "rawtypes" })
	@Override
	public void run() {

		// This seems inappropriate here. Strictly speaking, Cooperative Tasks
		// don't need to be noisy, but most probably are in practice. This works
		// here for now, but may need to relocate later.
		Statistic stat = null;
		try {
			stat = (Statistic) ClassCreation.createObject("noisyTaskStat");
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
			System.exit(1);
		}

		int[] numObjectives = ((GroupTask) MMNEAT.task).objectivesPerPopulation();
		int[] numOtherScores = ((GroupTask) MMNEAT.task).otherStatsPerPopulation();
		int numPopulations = ((GroupTask) MMNEAT.task).numberOfPopulations();

		ArrayList<Integer> lengths = new ArrayList<Integer>();
		if(Parameters.parameters.stringParameter("coevolvedNet1").isEmpty()){
			lengths = ArrayUtil.intListFromArray(numObjectives);		
		} else {
			for(int i = 0; i < genotypes.size(); i++) {
				lengths.add(1); // only one option per population
			}
		}
		ArrayList<ArrayList<Integer>> combos = CombinatoricUtilities.getAllCombinations(lengths);

		//loop through each possible combination of fitness objectives from each population,
		//making a team of agents from each population with each of those possible fitness objectives
		for(ArrayList<Integer>combo : combos){
			Genotype[] team = new Genotype[numPopulations];
			for(int i = 0; i < team.length; i++){
				team[i] = genotypes.get(i).get(combo.get(i));
			}

			//double[][] objectiveScores = new double[CommonConstants.trials][numObjectives[0]];
			// Ignore, because all needed information should be in other scores
			double[][] objectiveScores = new double[CommonConstants.trials][0];
			
			// Rather than worry about the different objectives used by each population, we
			// will put all objectives for each population into the other scores of the first
			// population. This will allow us to know how all populations are performing
			// by only looking at the other scores from the first population.
			double[][] otherScores = new double[CommonConstants.trials][numOtherScores[0]];

			for(int t = 0; t < CommonConstants.trials; t++){
				DrawingPanel[] networks = null;
				if(CommonConstants.showNetworks)
					networks = GroupTask.drawNetworks(team);
				ArrayList<Score> s = ((GroupTask) MMNEAT.task).evaluate(team);
				if(networks != null)
					GroupTask.disposePanels(networks);

				if (Parameters.parameters.booleanParameter("printFitness")) {
//					System.out.println(Arrays.toString(s.get(0).scores) + Arrays.toString(s.get(0).otherStats));
					System.out.println(Arrays.toString(s.get(0).otherStats));
//					if (s.get(0).individual instanceof TWEANNGenotype) {
//						System.out.println("Module Usage: " + Arrays.toString(((TWEANNGenotype) s.get(0).individual).getModuleUsage()));
//					}
				}
				//objectiveScores[t] = s.get(0).scores; // fitness scores
				otherScores[t] = s.get(0).otherStats; // other scores
			}

			//print scoreSummary
			double[] fitness = new double[0];
//			for (int i = 0; i < fitness.length; i++) {
//				fitness[i] = stat.stat(ArrayUtil.column(objectiveScores, i));
//			}
			double[] other = new double[numOtherScores[0]];
			for (int i = 0; i < other.length; i++) {
				other[i] = stat.stat(ArrayUtil.column(otherScores, i));
			}
			if (Parameters.parameters.booleanParameter("printFitness")) {
				System.out.print("Team: ");
				//Loop through team and print out genotype IDs
				for(Genotype g : team){
					System.out.print("["+g.getId()+"]");
				}
				System.out.println();
				System.out.println("\t" + NoisyLonerTask.scoreSummary(objectiveScores, otherScores, fitness, other, numObjectives[0]));
			}

		}

		((GroupTask) MMNEAT.task).finalCleanup(); // domain cleanup if necessary
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

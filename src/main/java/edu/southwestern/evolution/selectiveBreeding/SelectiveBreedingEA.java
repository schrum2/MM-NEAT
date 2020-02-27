package edu.southwestern.evolution.selectiveBreeding;

import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.SinglePopulationGenerationalEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.lineage.Offspring;
import edu.southwestern.log.FitnessLog;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.SinglePopulationTask;
import edu.southwestern.tasks.Task;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Selective breeding elite algorithm for picbreeder task
 * 
 * @author Lauren Gillespie
 *
 * @param <T>
 */
public class SelectiveBreedingEA<T> implements SinglePopulationGenerationalEA<T> {

	public static int MUTATION_RATE;
	
	private SinglePopulationTask<T> task;
	private int parentPop;
	private int generation;
	private boolean mating;
	private double crossoverRate;

	protected FitnessLog<T> keeperLog;
	
	public static ArrayList<Offspring> offspring;
	/**
	 * default Constructor
	 */
	@SuppressWarnings("unchecked")
	public SelectiveBreedingEA() { 
		this((SinglePopulationTask<T>) MMNEAT.task, Parameters.parameters.integerParameter("mu"));
	}
	/**
	 * Constructor
	 * @param task given task
	 * @param parentPop size of initial population
	 */
	public SelectiveBreedingEA(SinglePopulationTask<T> task, int parentPop) {
		MUTATION_RATE = 1;
		mating = Parameters.parameters.booleanParameter("mating");
		crossoverRate = Parameters.parameters.doubleParameter("crossoverRate");
		this.task = task;
		this.parentPop = parentPop;
		this.generation = Parameters.parameters.integerParameter("lastSavedGeneration");
		if (Parameters.parameters.booleanParameter("io")) {
			keeperLog = new FitnessLog<T>("parents");
		}
	}

	/**
	 * gets current generation number
	 * @return current generation
	 */
	@Override
	public int currentGeneration() {
		return generation;
	}

	/**
	 * number of evaluations performed per generation
	 * @return parent population size
	 */
	@Override
	public int evaluationsPerGeneration() {
		return parentPop;
	}

	/**
	 * returns task in question
	 * @return task
	 */
	@Override
	public Task getTask() {
		return task;
	}

	/**
	 * gets the initial population for evaluation
	 * @param example genotype 
	 * @return whole initial population
	 */
	@Override
	public ArrayList<Genotype<T>> initialPopulation(Genotype<T> example) {
		return PopulationUtil.initialPopulation(example, parentPop);
	}

	protected void logInfo(ArrayList<Score<T>> scores) {
		if(keeperLog != null) {
			keeperLog.log(scores, generation);
		}
		Genotype<T> example = scores.get(0).individual;
		if (example instanceof TWEANNGenotype) {
			ArrayList<TWEANNGenotype> tweanns = new ArrayList<TWEANNGenotype>(scores.size());
			for (Score<T> g : scores) {
				tweanns.add((TWEANNGenotype) g.individual);
			}
			EvolutionaryHistory.logTWEANNData(tweanns, generation);
		}
	}
	/**
	 * gets next generation of genotypes
	 * @param population parent genotypes
	 * @return children genotypes
	 */
	@Override
	public ArrayList<Genotype<T>> getNextGeneration(ArrayList<Genotype<T>> population) {
		
		int size = population.size();
		ArrayList<Genotype<T>> children = new ArrayList<Genotype<T>>();
		ArrayList<Score<T>> scores = task.evaluateAll(population);

		offspring = new ArrayList<Offspring>();
		for(int i = scores.size() - 1; i >= 0 ; i--) {
			if(scores.get(i).scores[0] < 1.0) {//not sure if able to assume only one score in array
				scores.remove(i); 
			}
		}
		for(Score<T> score : scores) {
			children.add(score.individual);
		}
		for(int i = scores.size(); i < size; i++) {
			long parentId1 = -1;
			long parentId2 = -1;
			Genotype<T> parent1 = scores.get(RandomNumbers.randomGenerator.nextInt(scores.size())).individual;
			parentId1 = parent1.getId();
			Genotype<T> g1 = parent1.copy();
			if (mating && RandomNumbers.randomGenerator.nextDouble() < crossoverRate) {
				Genotype<T> parent2 = scores.get(RandomNumbers.randomGenerator.nextInt(scores.size())).individual;
				parentId2 = parent2.getId();
				Genotype<T> g2 = parent2.copy();
				Genotype<T> offspring1 = g1.crossover(g2);
				offspring1.mutate();
				children.add(offspring1);
				i++;
				EvolutionaryHistory.logLineageData(parentId1,parentId2,offspring1);
				offspring.add(new Offspring(offspring1.getId(), parentId1, parentId2, generation));
			}
			if(i < size) {
				for(int z = InteractiveEvolutionTask.MPG_DEFAULT; z < MUTATION_RATE; z++) {
					g1.mutate();
				}
				children.add(g1);
				if (parentId2 == -1) {
					EvolutionaryHistory.logLineageData(parentId1,g1);
					offspring.add(new Offspring(g1.getId(), parentId1, generation));
				} else {
					EvolutionaryHistory.logLineageData(parentId1,parentId2,g1);
					offspring.add(new Offspring(g1.getId(), parentId1, parentId2, generation));
				}
			}
		}
		logInfo(scores);
		if(CommonConstants.netio) {
			PopulationUtil.saveCurrentGen(scores);
		}
		EvolutionaryHistory.logMutationData("---Gen " + generation + " Over-----------------");
		EvolutionaryHistory.logLineageData("---Gen " + generation + " Over-----------------");
		generation++;
		return children;
	}

	/**
	 * 
	 * @param population
	 */
	@Override
	public void close(ArrayList<Genotype<T>> population) {
		if(Parameters.parameters.booleanParameter("io") && keeperLog != null) {
			keeperLog.close();
		}
	}

}

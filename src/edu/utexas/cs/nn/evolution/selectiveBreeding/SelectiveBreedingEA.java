package edu.utexas.cs.nn.evolution.selectiveBreeding;

import java.util.ArrayList;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.SinglePopulationGenerationalEA;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.log.FitnessLog;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.tasks.Task;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.random.RandomNumbers;

/**
 * Selective breeding elite algorithm for picbreeder task
 * 
 * @author Lauren Gillespie
 *
 * @param <T>
 */
public class SelectiveBreedingEA<T> implements SinglePopulationGenerationalEA<T> {

	private SinglePopulationTask<T> task;
	private int parentPop;
	private int generation;
	private boolean mating;
	private double crossoverRate;
	
	protected FitnessLog<T> keeperLog;
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
		mating = Parameters.parameters.booleanParameter("mating");
		crossoverRate = Parameters.parameters.doubleParameter("crossoverRate");
		this.task = task;
		this.parentPop = parentPop;
		this.generation = Parameters.parameters.integerParameter("lastSavedGeneration");
		if (Parameters.parameters.booleanParameter("io")) {
			keeperLog = new FitnessLog<T>("keepers");
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
		keeperLog.log(scores, generation);
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
		for(int i = scores.size() - 1; i >= 0 ; i--) {
			if(scores.get(i).scores[0] < 1.0) {//not sure if able to assume only one score in array
				scores.remove(i); 
			}
		}
		for(Score<T> score : scores) {
			children.add(score.individual);
		}
		for(int i = scores.size(); i < size; i++) {
			Genotype<T> g1 = scores.get(RandomNumbers.randomGenerator.nextInt(scores.size())).individual.copy();
			if (mating && RandomNumbers.randomGenerator.nextDouble() < crossoverRate) {
				Genotype<T> g2 = scores.get(RandomNumbers.randomGenerator.nextInt(scores.size())).individual.copy();
				Genotype<T> offspring1 = g1.crossover(g2);
				offspring1.mutate();
				children.add(offspring1);
				i++;
			}
			if(i < size) {
				g1.mutate();
				children.add(g1);
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
		if(Parameters.parameters.booleanParameter("writeOutput") && keeperLog != null) {
			keeperLog.close();
		}
	}

}

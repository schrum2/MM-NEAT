package edu.southwestern.evolution.mulambda;

import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.MultiplePopulationGenerationalEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.nsga2.NSGA2;
import edu.southwestern.evolution.nsga2.NSGA2Score;
import edu.southwestern.log.FitnessLog;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.GroupTask;
import edu.southwestern.tasks.MultiplePopulationTask;
import edu.southwestern.tasks.Task;
import edu.southwestern.tasks.mspacman.multitask.DangerousAreaModeSelector;
import edu.southwestern.tasks.mspacman.sensors.directional.scent.VariableDirectionKStepDeathScentBlock;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.random.RandomNumbers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * A class which supports cooevolution. Contains many extensions of
 * the MuLambda class, but with some changes which will allow for a different
 * genotype for each subPopulation
 * 
 * @author Jacob Schrum
 */
public abstract class CoevolutionMuLambda implements MultiplePopulationGenerationalEA {

	protected boolean mating;
	public int generation;
	public int[] mu;
	public int[] lambda;
	private final int mltype;
	private final MultiplePopulationTask task;
	protected final double crossoverRate;
	@SuppressWarnings("rawtypes")
	protected FitnessLog[] parentLogs;
	@SuppressWarnings("rawtypes")
	protected FitnessLog[] childLogs;
	protected final boolean writeOutput;
	private HashMap<Long, ArrayList<Long>> recentOffspring = new HashMap<Long, ArrayList<Long>>();
	public int successfulOffspringSearches = 0;
	public int totalOffspringSearches = 0;
	public boolean evaluatingParents = false;

	/**
	 * returns a random id number for the offspring of this parent
	 * 
	 * @param parentId
	 *            of this parent (long)
	 * @return random id (Long)
	 */
	public Long getRandomOffspringId(long parentId) {
		totalOffspringSearches++;
		if (!recentOffspring.containsKey(parentId)) {
			return null;
		} else {
			successfulOffspringSearches++;
			ArrayList<Long> offspring = recentOffspring.get(parentId);
			return offspring.get(RandomNumbers.randomGenerator.nextInt(offspring.size()));
		}
	}

	/**
	 * Initializes this MuLambda class according to various parameter values and
	 * types
	 */
	public CoevolutionMuLambda() {
		this(MuLambda.MLTYPE_PLUS, Parameters.parameters.integerParameter("mu"),
				Parameters.parameters.integerParameter("mu"), (MultiplePopulationTask) MMNEAT.task,
				MMNEAT.genotypeExamples.size());
	}

	/**
	 * create fitness logs depending on the population and number in population.
	 * Assigns class variables according to command line parameters
	 * 
	 * @param mltype
	 * @param mu
	 * @param lambda
	 * @param task
	 * @param numPopulations
	 */
	@SuppressWarnings("rawtypes")
	public CoevolutionMuLambda(int mltype, int mu, int lambda, MultiplePopulationTask task, int numPopulations) {
		this.mltype = mltype;
		this.task = task;
		this.mu = new int[numPopulations];
		this.lambda = new int[numPopulations];
		for (int i = 0; i < numPopulations; i++) {
			this.mu[i] = mu;
			this.lambda[i] = lambda;
		}
		this.generation = Parameters.parameters.integerParameter("lastSavedGeneration");
		mating = Parameters.parameters.booleanParameter("mating");
		crossoverRate = Parameters.parameters.doubleParameter("crossoverRate");
		writeOutput = Parameters.parameters.booleanParameter("io");

		if (writeOutput) {
			parentLogs = new FitnessLog[numPopulations];
			if (CommonConstants.logChildScores) {
				childLogs = new FitnessLog[numPopulations];
			}
			for (int i = 0; i < numPopulations; i++) {
				parentLogs[i] = new FitnessLog("pop" + i + "parents", i);
				if (CommonConstants.logChildScores) {
					childLogs[i] = new FitnessLog("pop" + i + "children", i);
				}
			}
		}
	}

	/**
	 * If it is requested by the user, will log the parent info by creating text
	 * files with corresponding information
	 * 
	 * @param parentScores
	 *            the scores of the parent
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void logParentInfo(ArrayList<ArrayList<Score>> parentScores) {
		if (writeOutput) {
			for (int i = 0; i < parentLogs.length; i++) {
				parentLogs[i].log(parentScores.get(i), generation);
			}
			// Only do TWEANN log of first population.
                        // TODO: Should all be tracked?
			Genotype example = parentScores.get(0).get(0).individual;
			if (example instanceof TWEANNGenotype) {
				ArrayList<TWEANNGenotype> tweanns = new ArrayList<TWEANNGenotype>(parentScores.size());
				for (Score g : parentScores.get(0)) {
					tweanns.add((TWEANNGenotype) g.individual);
				}
				EvolutionaryHistory.logTWEANNData(tweanns, generation);
			}
		}
	}

	/**
	 * Creates the initial populations
	 * 
	 * @param examples,
	 *            an arrayList of genotypes
	 * @return startingPopulations, an arrayList of arrayLists of genotypes
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ArrayList<ArrayList<Genotype>> initialPopulations(ArrayList<Genotype> examples) {
		ArrayList<ArrayList<Genotype>> startingPopulations = new ArrayList<ArrayList<Genotype>>(examples.size() + 1);
		boolean seedCoevolutionPops = Parameters.parameters.booleanParameter("seedCoevolutionPops");
		for (int i = 0; i < examples.size(); i++) {
			String popDir = Parameters.parameters.stringParameter("multinetworkPopulation" + (i + 1));
			if (seedCoevolutionPops && popDir != null && !popDir.isEmpty()) {
				System.out.println("Seed subpop " + i + " from " + popDir);
				// Load pop
				ArrayList<Genotype> pop = PopulationUtil.removeListGenotypeType(PopulationUtil.load(popDir));
				long highestId = -1;
				long highestInnovation = -1;
				for (Genotype g : pop) {
					((TWEANNGenotype) g).archetypeIndex = i;
					highestId = Math.max(highestId, g.getId());
					highestInnovation = Math.max(highestInnovation, ((TWEANNGenotype) g).biggestInnovation());
				}
				if (EvolutionaryHistory.largestUnusedGenotypeId < highestId) {
					EvolutionaryHistory.setHighestGenotypeId(highestId + 1);
				}
				if (EvolutionaryHistory.largestUnusedInnovationNumber < highestInnovation) {
					EvolutionaryHistory.setInnovation(highestInnovation + 1);
				}
				startingPopulations.add(pop);
			} else {
				System.out.println("Fresh subpop " + i);
                                // Type manipulations required for full generality
				ArrayList<Genotype> temp = PopulationUtil.removeListGenotypeType(PopulationUtil.initialPopulation(examples.get(i), mu[i]));
				startingPopulations.add(temp);
			}
		}
		return startingPopulations;
	}

	/**
	 * The subpopulations are parent populations of each type. They need to be
	 * evaluated in the multi-population task to get their scores. Then these
	 * populations need to generate children, who are also evaluated. From
	 * combined parent/child populations of each type, new parent populations
	 * are derived, and returned.
	 *
	 * @param populations
	 *            list of subpopulations
	 * @return list of all the genotypes to keep after a generation
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public ArrayList<ArrayList<Genotype>> getNextGeneration(ArrayList<ArrayList<Genotype>> populations) {
		evaluatingParents = true;
		long start = System.currentTimeMillis();
		System.out.println("Eval parents: "); 
		ArrayList<ArrayList<Score>> parentScores = task.evaluateAllPopulations(populations);
		long end = System.currentTimeMillis();
		System.out.println("Done parents: " + TimeUnit.MILLISECONDS.toMinutes(end - start) + " minutes");
		logParentInfo(parentScores);

		evaluatingParents = false;
		start = System.currentTimeMillis();
		System.out.println("Eval children: "); 
		ArrayList<ArrayList<Score>> childrenScores = processChildren(parentScores);
		end = System.currentTimeMillis();
		System.out.println("Done children: " + TimeUnit.MILLISECONDS.toMinutes(end - start) + " minutes");

		if (writeOutput && ((GroupTask) task).teamLog != null) {
			((GroupTask) task).teamLog.log("---Gen " + generation + " Over-----------------");
		}

		ArrayList<ArrayList<Genotype>> finalKeepers = new ArrayList<ArrayList<Genotype>>(parentScores.size());
		for (int i = 0; i < parentScores.size(); i++) {
			ArrayList<Score> sourcePopulation = prepareSourcePopulation(i, parentScores.get(i), childrenScores.get(i), mltype);
			ArrayList<Genotype> selectedPopulation = selection(i, mu[i], sourcePopulation);
			finalKeepers.add(selectedPopulation);
		}

		generation++;
		CommonConstants.trialsByGenerationUpdate(generation);
		VariableDirectionKStepDeathScentBlock.updateScentMaps(); // For pacman
		DangerousAreaModeSelector.updateScentMaps(); // For pacman
		return finalKeepers;
	}

	/**
	 * This function is identical to a method of the same name in MuLambda,
	 * except that it has no type parameters, which is necessary to allow for
	 * the possibility of each subpopulation having a different type of
	 * Genotype.
	 *
	 * @param popIndex
	 * @param parentScores
	 *            parent scores
	 * @param childrenScores
	 *            child scores
	 * @param mltype
	 *            type of selection (+ vs ,)
	 * @return population to select from for next generation.
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<Score> prepareSourcePopulation(int popIndex, ArrayList<Score> parentScores, ArrayList<Score> childrenScores, int mltype) {
		ArrayList<Score> population = null;
		switch (mltype) {
		case MuLambda.MLTYPE_PLUS:
			population = parentScores;
			population.addAll(childrenScores);
			break;
		case MuLambda.MLTYPE_COMMA:
			population = childrenScores;
			break;
		}
		return population;
	}

	/**
	 * Given the parent scores for all populations, generate new child
	 * populations, and then evaluate them all and return their scores.
	 *
	 * @param parentScores
	 *            scores for all parent sub-populations
	 * @return scores for all child sub-populations
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<ArrayList<Score>> processChildren(ArrayList<ArrayList<Score>> parentScores) {
		ArrayList<ArrayList<Genotype>> children = new ArrayList<ArrayList<Genotype>>(parentScores.size());
		recentOffspring = new HashMap<Long, ArrayList<Long>>();
		successfulOffspringSearches = 0;
		totalOffspringSearches = 0;
		for (int i = 0; i < parentScores.size(); i++) {
			children.add(generateChildren(lambda[i], parentScores.get(i)));
		}

		ArrayList<ArrayList<Score>> childrenScores = task.evaluateAllPopulations(children);
		if (CommonConstants.logChildScores) {
			// Log child scores
			for (int i = 0; i < childLogs.length; i++) {
				childLogs[i].log(childrenScores.get(i), generation);
			}
		}
		return childrenScores;
	}

	/**
	 * Based on parent scores of one population, generate new child population.
	 * First converts each Score into an NSGA2 Score, and then generates new
	 * child population as NSGA2 does (selection, mutation, crossover).
	 *
	 * @param numChildren
	 *            number of children to make
	 * @param parentScores
	 *            scores from one parent sub-population
	 * @return genotypes of new child population
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<Genotype> generateChildren(int numChildren, ArrayList<Score> parentScores) {
		NSGA2Score[] scoresArray = NSGA2.staticNSGA2Scores(PopulationUtil.addListScoreType(parentScores));
		return PopulationUtil.removeListGenotypeType(
				NSGA2.generateNSGA2Children(numChildren, scoresArray, generation, mating, crossoverRate));
	}

	/**
	 * Write final output and close all logs.
	 *
	 * @param populations
	 */
	@SuppressWarnings("rawtypes")
        @Override
	public void close(ArrayList<ArrayList<Genotype>> populations) {
		ArrayList<ArrayList<Score>> parentScores = task.evaluateAllPopulations(populations);
		logParentInfo(parentScores);
		if (writeOutput) {
			for (int i = 0; i < parentLogs.length; i++) {
				this.parentLogs[i].close();
				if (CommonConstants.logChildScores) {
					this.childLogs[i].close();
				}
			}
			if (((GroupTask) task).teamLog != null) {
				((GroupTask) task).teamLog.close();
			}
		}
	}

	/**
	 * @return generation, the current generation as an integer
	 */
        @Override
	public int currentGeneration() {
		return generation;
	}

	/**
	 * @return task, the current task as a Task object
	 */
        @Override
	public Task getTask() {
		return task;
	}

	/**
	 * Given a single sub-population, perform selection to keep the best toKeep
	 * individuals, which will become the next parent population
	 *
         * @param popIndex
         *            particular population to perform selection on
	 * @param toKeep
	 *            number of individuals to keep from sub-pop
	 * @param sourcePopulation
	 *            sub-population to select from
	 * @return sub-population for next generation
	 */
	@SuppressWarnings("rawtypes")
	public abstract ArrayList<Genotype> selection(int popIndex, int toKeep, ArrayList<Score> sourcePopulation);

	/**
	 * Assumes all mu and lambda values are the same
	 * 
	 * @return
	 */
        @Override
	public int evaluationsPerGeneration() {
		int teams = Parameters.parameters.integerParameter("teams");
		return (mu[0] * teams) + (lambda[0] * teams);
	}
}

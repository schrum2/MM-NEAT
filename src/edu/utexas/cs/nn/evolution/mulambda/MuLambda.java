package edu.utexas.cs.nn.evolution.mulambda;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.SinglePopulationGenerationalEA;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.log.FitnessLog;
import edu.utexas.cs.nn.log.PlotLog;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.tasks.Task;
import edu.utexas.cs.nn.tasks.mspacman.CooperativeMsPacManTask;
import edu.utexas.cs.nn.tasks.mspacman.MsPacManTask;
import edu.utexas.cs.nn.tasks.mspacman.multitask.DangerousAreaModeSelector;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.scent.VariableDirectionKStepDeathScentBlock;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 * General evolutionary algorithm for one population using either a (mu +
 * lambda) or (mu,lambda) strategy.
 * 
 * @author Jacob Schrum
 * @param <T>
 *            Type of phenotype evolved
 */
public abstract class MuLambda<T> implements SinglePopulationGenerationalEA<T> {

	// Use one of these to define the mltype
	public static final int MLTYPE_PLUS = 0;
	public static final int MLTYPE_COMMA = 1;

	private final int mltype;
	public int mu;
	public int lambda;
	public SinglePopulationTask<T> task;
	public int generation;
	protected FitnessLog<T> parentLog;
	protected FitnessLog<T> childLog;
	protected PlotLog modeLog;
	protected boolean writeOutput;
	private final int MAX_MODE_OF_LOG_INTEREST = 5;
	public boolean evaluatingParents = false;
        public boolean msPacMan;
        
	/**
	 * Initialize evolutionary algorithm.
	 * 
	 * @param mltype
	 *            MLTYPE_PLUS or MLTYPE_COMMA
	 * @param task
	 *            Task to evolve in
	 * @param mu
	 *            Parent population size
	 * @param lambda
	 *            Child population size
	 * @param io
	 *            Whether to write file output
	 */
	public MuLambda(int mltype, SinglePopulationTask<T> task, int mu, int lambda, boolean io) {
		this.mltype = mltype;
		this.task = task;
		this.mu = mu;
		this.lambda = lambda;
		this.generation = Parameters.parameters.integerParameter("lastSavedGeneration");
		writeOutput = Parameters.parameters.booleanParameter("io");
                msPacMan = task instanceof MsPacManTask || task instanceof CooperativeMsPacManTask;
                
		if (writeOutput && io) {
			parentLog = new FitnessLog<T>("parents");
			if (CommonConstants.logChildScores) {
				childLog = new FitnessLog<T>("child");
			}
			if (task instanceof MsPacManTask && (MMNEAT.modesToTrack > 1 || TWEANN.preferenceNeuron()
					|| Parameters.parameters.booleanParameter("ensembleModeMutation"))) {
				ArrayList<String> labels = new ArrayList<String>();
				labels.add("Max Modes of Best");
				labels.add("Min Modes of Best");
				labels.add("Avg Modes of Best");
				labels.add("Max Modes of Worst");
				labels.add("Min Modes of Worst");
				labels.add("Avg Modes of Worst");
				int i;
				for (i = 1; i <= MAX_MODE_OF_LOG_INTEREST; i++) {
					labels.add(i + " Mode Best");
				}
				labels.add("More Than " + i + " Modes Best");
				modeLog = new PlotLog("ModeUsage", labels);
			}
		}
	}

	/**
	 * Get task being evolved in
	 * 
	 * @return Task instance
	 */
	@Override
	public Task getTask() {
		return task;
	}

	/**
	 * Current generation
	 * 
	 * @return generation
	 */
	@Override
	public int currentGeneration() {
		return generation;
	}

	/**
	 * The initial parent population is of size mu, and is randomly generated
	 * based on an example genotype.
	 *
	 * @param example
	 *            = used to generate random genotypes
	 * @return = the initial parent population
	 */
	@Override
	public ArrayList<Genotype<T>> initialPopulation(Genotype<T> example) {
		return PopulationUtil.initialPopulation(example, mu);
	}

	/**
	 * Write info to file about parent population
	 * 
	 * @param parentScores
	 *            Score details of all parents after evaluation
	 */
	public void logParentInfo(ArrayList<Score<T>> parentScores) {
		if (writeOutput) {
			parentLog.log(parentScores, generation);
			Genotype example = parentScores.get(0).individual;
			if (example instanceof TWEANNGenotype) {
				ArrayList<TWEANNGenotype> tweanns = new ArrayList<TWEANNGenotype>(parentScores.size());
				for (Score<T> g : parentScores) {
					tweanns.add((TWEANNGenotype) g.individual);
				}
				EvolutionaryHistory.logTWEANNData(tweanns, generation);
				if (modeLog != null) {
					modeLogging(parentScores);
				}
			}
		}
	}

	private void modeLogging(ArrayList<Score<T>> parentScores) {
		// Find out how many modes the best and worst nets had
		double[] modeBests = new double[MAX_MODE_OF_LOG_INTEREST + 1];
		double max = 0;
		double min = Double.MAX_VALUE;
		int minMaxModes = 0;
		int minMinModes = Integer.MAX_VALUE;
		int maxMaxModes = 0;
		int maxMinModes = Integer.MAX_VALUE;
		ArrayList<Double> modeCountsOfBest = new ArrayList<Double>();
		ArrayList<Double> modeCountsOfWorst = new ArrayList<Double>();
		for (Score<T> g : parentScores) {
			double score = g.otherStats[0]; // Game score should always be first
											// "other" stat
			int modes = ((TWEANNGenotype) g.individual).numModules;
			if (modes <= MAX_MODE_OF_LOG_INTEREST) {
				modeBests[modes - 1] = Math.max(modeBests[modes - 1], score);
			} else {
				modeBests[MAX_MODE_OF_LOG_INTEREST] = Math.max(modeBests[MAX_MODE_OF_LOG_INTEREST], score);
			}
			if (score == max) {
				modeCountsOfBest.add((double) modes);
				maxMaxModes = Math.max(maxMaxModes, modes);
				maxMinModes = Math.min(maxMinModes, modes);
			} else if (score > max) {
				modeCountsOfBest = new ArrayList<Double>();
				modeCountsOfBest.add((double) modes);
				maxMaxModes = modes;
				maxMinModes = modes;
			}
			max = Math.max(score, max);

			if (score == min) {
				modeCountsOfWorst.add((double) modes);
				minMaxModes = Math.max(minMaxModes, modes);
				minMinModes = Math.min(minMinModes, modes);
			} else if (score < min) {
				modeCountsOfWorst = new ArrayList<Double>();
				modeCountsOfWorst.add((double) modes);
				minMaxModes = modes;
				minMinModes = modes;
			}
			min = Math.min(score, min);
		}
		// System.out.println("modeCountsOfBest:"+modeCountsOfBest);
		double maxAvgModes = StatisticsUtilities.average(ArrayUtil.doubleArrayFromList(modeCountsOfBest));
		double minAvgModes = StatisticsUtilities.average(ArrayUtil.doubleArrayFromList(modeCountsOfWorst));
		// System.out.println("avgModes:"+avgModes);
		// Log the data
		ArrayList<Double> logValues = new ArrayList<Double>(8);
		logValues.add((double) maxMaxModes);
		logValues.add((double) maxMinModes);
		logValues.add(maxAvgModes);
		logValues.add((double) minMaxModes);
		logValues.add((double) minMinModes);
		logValues.add(minAvgModes);
		// <= because the last slot is for that many modes or more
		for (int i = 0; i <= MAX_MODE_OF_LOG_INTEREST; i++) {
			logValues.add(modeBests[i]);
		}
		modeLog.log(generation, logValues);
	}

	/**
	 * Given parent scores/genotypes, generate children, evaluate them, and
	 * return their scores.
	 * 
	 * @param parentScores
	 *            Scores of evaluated parents (contains genotypes)
	 * @return Scores of children (contains genotypes)
	 */
	public ArrayList<Score<T>> processChildren(ArrayList<Score<T>> parentScores) {
		// Get offspring from parents
		ArrayList<Genotype<T>> children = performDeltaCoding(generation)
				? PopulationUtil.getBestAndDeltaCode(parentScores) : generateChildren(lambda, parentScores);
		// Evaluate the children
		ArrayList<Score<T>> childrenScores = task.evaluateAll(children);
		// Log child information to file
		if (writeOutput && CommonConstants.logChildScores) {
			childLog.log(childrenScores, generation);
		}
		return childrenScores;
	}

	/**
	 * Whether or not to perform delta coding on this generation. Depends on if
	 * setting is turned on, and how frequently it is done.
	 * 
	 * @param generation
	 *            Current generation
	 * @return Whether or not delta coding should happen
	 */
	public static boolean performDeltaCoding(int generation) {
		boolean periodicDeltaCoding = Parameters.parameters.booleanParameter("periodicDeltaCoding");
		boolean result = (periodicDeltaCoding && generation != 0
				&& generation % Parameters.parameters.integerParameter("deltaCodingFrequency") == 0);
		if (result) {
			System.out.println("Delta code children");
		}
		return result;
	}

	/**
	 * Given the scores of evaluated parents and the scores of evaluated
	 * children, generate the next parent population via selection.
	 * 
	 * @param parentScores
	 *            Parent scores (contains genotypes as well)
	 * @param childrenScores
	 *            Child scores (contains genotypes as well)
	 * @return New parent population
	 */
	public ArrayList<Genotype<T>> selectAndAdvance(ArrayList<Score<T>> parentScores, ArrayList<Score<T>> childrenScores) {
		ArrayList<Score<T>> population = prepareSourcePopulation(parentScores, childrenScores);
		ArrayList<Genotype<T>> newParents = selection(mu, population);
		EvolutionaryHistory.logMutationData("---Gen " + generation + " Over-----------------");
		EvolutionaryHistory.logLineageData("---Gen " + generation + " Over-----------------");
		generation++;
		EvolutionaryHistory.frozenPreferenceVsPolicyStatusUpdate(newParents, generation);
		CommonConstants.trialsByGenerationUpdate(generation);
                if(msPacMan) { // Pacman-specific updates
                    if (Parameters.parameters.booleanParameter("scalePillsByGen")) { // For pacman
                            Parameters.parameters.setDouble("preEatenPillPercentage", 1.0 - ((generation * 1.0) / Parameters.parameters.integerParameter("maxGens")));
                    }
                    if (Parameters.parameters.booleanParameter("incrementallyDecreasingEdibleTime")) { // For pacman
                            MMNEAT.setEdibleTimeBasedOnGeneration(generation);
                    }
                    if (Parameters.parameters.booleanParameter("incrementallyDecreasingLairTime")) { // For pacman
                            MMNEAT.setLairTimeBasedOnGeneration(generation);
                    }
                    VariableDirectionKStepDeathScentBlock.updateScentMaps(); // For pacman
                    DangerousAreaModeSelector.updateScentMaps(); // For pacman
                }
		return newParents;
	}

	/**
	 * Figure out which genotypes are actually being selected from to create
	 * next generation. This method can be overridden by other EAs to do extra
	 * work to prepare the population
	 * 
	 * @param parentScores
	 *            Parent scores (contains genotypes as well)
	 * @param childrenScores
	 *            Child scores (contains genotypes as well)
	 * @return One combined list of scores of only individuals being selected
	 *         from
	 */
	public ArrayList<Score<T>> prepareSourcePopulation(ArrayList<Score<T>> parentScores, ArrayList<Score<T>> childrenScores) {
		return prepareSourcePopulation(parentScores, childrenScores, mltype);
	}

	/**
	 * Standard preparation based on Mu and Lambda
	 * 
	 * @param <T>
	 *            Type of phenotype being evolved
	 * @param parentScores
	 *            Parent scores (contains genotypes as well)
	 * @param childrenScores
	 *            Child scores (contains genotypes as well)
	 * @param mltype
	 *            MLTYPE_PLUS or MLTYPE_COMMA
	 * @return List of combined scores to perform selection on for next
	 *         generation
	 */
	public static <T> ArrayList<Score<T>> prepareSourcePopulation(ArrayList<Score<T>> parentScores, ArrayList<Score<T>> childrenScores, int mltype) {
		ArrayList<Score<T>> population = null;
		switch (mltype) {
		case MLTYPE_PLUS: // select the best from the combined parent and child population
			population = parentScores;
			population.addAll(childrenScores);
			break;
		case MLTYPE_COMMA: // select best from children alone
			population = childrenScores;
			break;
		default: // should not happen
			System.out.println("Invalid selection type for MuLambda!");
			System.exit(1);
		}
		return population;
	}

	/**
	 * Given the current parent population, return the next parent population
	 *
	 * @param parents
	 *            = current parent population
	 * @return = next parent population
	 */
	@Override
	public ArrayList<Genotype<T>> getNextGeneration(ArrayList<Genotype<T>> parents) {
		evaluatingParents = true;
		long start = System.currentTimeMillis();
		System.out.println("Eval parents: ");
		ArrayList<Score<T>> parentScores = task.evaluateAll(parents);
		long end = System.currentTimeMillis();
		System.out.println("Done parents: " + TimeUnit.MILLISECONDS.toMinutes(end - start) + " minutes");

		// Get some info about modes, if doing mode mutation
		if (TWEANN.preferenceNeuron()) {
			EvolutionaryHistory.maxModes = 0;
			EvolutionaryHistory.minModes = Integer.MAX_VALUE;

			if (parentScores.get(0).individual instanceof TWEANNGenotype) {
				for (Score<T> g : parentScores) {
					TWEANNGenotype tg = (TWEANNGenotype) g.individual;
					EvolutionaryHistory.maxModes = Math.max(tg.numModules, EvolutionaryHistory.maxModes);
					EvolutionaryHistory.minModes = Math.min(tg.numModules, EvolutionaryHistory.minModes);
				}
			}
		}

		evaluatingParents = false;
		start = System.currentTimeMillis();
		System.out.println("Eval children: ");
		ArrayList<Score<T>> childrenScores = processChildren(parentScores);
		end = System.currentTimeMillis();
		System.out.println("Done children: " + TimeUnit.MILLISECONDS.toMinutes(end - start) + " minutes");

		// Parent logging occurs after child evals to decrease odds of logs
		// getting out of sync.
		// This way, all logs are updated at once, along with the generation
		// param being advanced.
		logParentInfo(parentScores);
		if (writeOutput) {
			ArrayList<Score<T>> combined = new ArrayList<Score<T>>(mu + lambda);
			combined.addAll(parentScores);
			combined.addAll(childrenScores);
			MMNEAT.logPerformanceInformation(combined, generation);
		}
		return selectAndAdvance(parentScores, childrenScores);
	}

	/**
	 * Cleanup needed at end of evolution
	 * 
	 * @param population
	 *            Final population
	 */
	@Override
	public void close(ArrayList<Genotype<T>> population) {
		// Evaluate final parents (haven't technically been evaluated yet)
		ArrayList<Score<T>> parentScores = task.evaluateAll(population);
		logParentInfo(parentScores);
		if (writeOutput) {
			parentLog.close();
			if (childLog != null) {
				childLog.close();
			}
			if (modeLog != null) {
				modeLog.close();
			}
		}
	}

	/**
	 * Because each parent is evaluated once, and each child is evaluated once
	 *
	 * @return number of evaluations per generation
	 */
	@Override
	public int evaluationsPerGeneration() {
		return mu + lambda;
	}

	/**
	 * Generate a certain number of children based on parent scores/genotypes
	 * 
	 * @param numChildren
	 *            Number of children to generate
	 * @param parentScores
	 *            List of evaluated parent scores (contains genotypes)
	 * @return numChildren of child genotypes
	 */
	public abstract ArrayList<Genotype<T>> generateChildren(int numChildren, ArrayList<Score<T>> parentScores);

	/**
	 * From the provided population of scores, filter down to a certain number
	 * of parent genotypes for the next generation.
	 * 
	 * @param numParents
	 *            Number of parents to filter down to
	 * @param scores
	 *            Scores of agents to select from (contains genotypes)
	 * @return Genotypes of filtered/selected individuals.
	 */
	public abstract ArrayList<Genotype<T>> selection(int numParents, ArrayList<Score<T>> scores);
}

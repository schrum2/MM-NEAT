package edu.utexas.cs.nn.evolution.nsga2.tug;

import java.util.ArrayList;
import java.util.Arrays;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.nsga2.NSGA2;
import edu.utexas.cs.nn.evolution.nsga2.NSGA2Score;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.util.BooleanUtil;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.stats.Statistic;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
 * Implementation of an idea from my dissertation:
 * http://nn.cs.utexas.edu/?schrum:phd2014 Although, the idea first appeared in
 * GECCO 2010: http://nn.cs.utexas.edu/?schrum:gecco10 Basically, NSGA2 is used,
 * but objectives are switched on and off based on how well the population is
 * doing in those objectives. The purpose is to temporarily turn off objectives
 * the population is already doing well in so evolution can focus on objectives
 * that the population is doing poorly in.
 * 
 * @author Jacob Schrum
 * @param <T> phenotype
 */
public class TUGNSGA2<T> extends NSGA2<T> {

	protected double[] goals;
	protected double[] recencyWeightedAverages;
	protected boolean[] useObjective;
	protected boolean[] justDeactivated;
	protected boolean[] justActivated;
	protected boolean[] initialClimb;
	protected double[] lastTUGDelta;
	protected double alpha;
	protected double eta;
	protected double tugMomentum;
	protected TUGLog tugLog;
	protected int timeLimit;
	private final boolean printFitness;
	private Statistic performanceStat;
	private Statistic goalTargetStat;

	public TUGNSGA2() {
		this(Parameters.parameters.booleanParameter("io"));
	}

	/**
	 * creates an instance of TUGNSGA2
	 * @param io, boolean indicating whether logging is desired or not
	 */
	public TUGNSGA2(boolean io) {
		super(io);
		alpha = Parameters.parameters.doubleParameter("tugAlpha");
		eta = Parameters.parameters.doubleParameter("tugEta");
		tugMomentum = Parameters.parameters.doubleParameter("tugMomentum");
		printFitness = Parameters.parameters.booleanParameter("printFitness");
		timeLimit = Parameters.parameters.integerParameter("tugAdvancementTimeLimit");

		this.useObjective = new boolean[task.numObjectives() + MMNEAT.metaheuristics.size()];
		this.justDeactivated = new boolean[useObjective.length];
		this.justActivated = new boolean[useObjective.length];
		for (int i = 0; i < useObjective.length; i++) {
			useObjective[i] = true;
			justDeactivated[i] = false;
			justActivated[i] = true;
		}
		resetRecencyWeightedAverages();
		this.goals = new double[this.recencyWeightedAverages.length];
		this.lastTUGDelta = new double[this.recencyWeightedAverages.length];
		System.arraycopy(recencyWeightedAverages, 0, this.goals, 0, recencyWeightedAverages.length);

		if (Parameters.parameters.booleanParameter("setInitialTUGGoals")) {
			for (int i = 0; i < goals.length; i++) {
				goals[i] = Parameters.parameters.doubleParameter("initialTUGGoal" + i);
			}
		}
		// TUG log loading overwrites outdated initial goals if necessary
		if (writeOutput) {
			tugLog = new TUGLog("TUG", this);
		}

		try {
			performanceStat = (Statistic) ClassCreation.createObject("tugPerformanceStat");
			goalTargetStat = (Statistic) ClassCreation.createObject("tugGoalTargetStat");
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
			System.out.println("TUG needs stats to define performance and goal targets");
			System.exit(1);
		}
	}

	/**
	 * Loads important state variables associated with TUG
	 * @param usage whether or not each objective is being used
	 * @param rwas recency-weighted averages that chase the real performance
	 * @param loadedGoals current goal level for each objective
	 * @param deltas changes in goals
	 * @param climb whether each objective is in initial climb period
	 */
	public void loadTugState(boolean[] usage, double[] rwas, double[] loadedGoals, double[] deltas, boolean[] climb) {
		this.useObjective = usage;
		this.recencyWeightedAverages = rwas;
		this.goals = loadedGoals;
		this.lastTUGDelta = deltas;
		this.initialClimb = climb;
	}

	/**
	 * Bit more efficient than resetting the goals individually
	 */
	private void resetRecencyWeightedAverages() {
		if (CommonConstants.tugResetsToPreviousGoals && goals != null && goals.length > 0) {
			// This reset should happen right before goals are increased
			System.arraycopy(goals, 0, recencyWeightedAverages, 0, goals.length);
		} else {
			double[] actualGoals = ((TUGTask) task).startingGoals();
			double[] metaGoals = new double[MMNEAT.metaheuristics.size()];
			for (int i = 0; i < metaGoals.length; i++) {
				metaGoals[i] = MMNEAT.metaheuristics.get(i).startingTUGGoal();
			}

			this.recencyWeightedAverages = new double[actualGoals.length + metaGoals.length];
			System.arraycopy(actualGoals, 0, this.recencyWeightedAverages, 0, actualGoals.length);
			System.arraycopy(metaGoals, 0, this.recencyWeightedAverages, actualGoals.length, metaGoals.length);
		}
		// Initial climb is the period when the RWAs are increasing,
		// before they reach the newly set goal for the first time
		initialClimb = new boolean[recencyWeightedAverages.length];
		Arrays.fill(initialClimb, true);
	}

	/**
	 * Reset just one Recency Weighted Average
	 *
	 * @param i
	 *            index of objective
	 */
	private void resetRecencyWeightedAverage(int i) {
		if (CommonConstants.tugResetsToPreviousGoals && goals != null && goals.length > 0) {
			recencyWeightedAverages[i] = goals[i];
		} else {
			double[] actualGoals = ((TUGTask) task).startingGoals();
			if (i < actualGoals.length) {
				recencyWeightedAverages[i] = actualGoals[i];
			} else {
				recencyWeightedAverages[i] = MMNEAT.metaheuristics.get(i - actualGoals.length).startingTUGGoal();
			}
		}
		// Initial climb is the period when the RWAs are increasing,
		// before they reach the newly set goal for the first time
		initialClimb[i] = true;
	}

	/**
	 * Disables scores in objectives that are currently disabled.
	 * @param scores List of complete scores in each objective.
	 * @return score array where certain objectives may be disabled
	 */
	public NSGA2Score<T>[] getTUGScores(ArrayList<Score<T>> scores) {
		@SuppressWarnings("unchecked")
		NSGA2Score<T>[] result = new NSGA2Score[scores.size()];
		if (generation > Parameters.parameters.integerParameter("endTUGGeneration")
				|| generation < Parameters.parameters.integerParameter("startTUGGeneration")) { 
			// Using plain NSGA2 instead of TUG
			boolean[] all = new boolean[useObjective.length];
			Arrays.fill(all, true);
			for (int i = 0; i < scores.size(); i++) {
				result[i] = new TUGNSGA2Score<T>(scores.get(i), all);
			}
		} else {
			for (int i = 0; i < scores.size(); i++) {
				result[i] = new TUGNSGA2Score<T>(scores.get(i), useObjective);
			}
		}
		return result;
	}

	/**
	 * Given the objective scores of the whole population, compute two things:
	 * 1) Some measure of the overall performance of the population in an
	 * objective. The default for this is the average. 2) Some high performance
	 * point that the objective goal is working towards reaching. The default
	 * for this is the max value of the objective in the population.
	 *
	 * @param scores
	 *            scores of whole population in all objectives
	 * @return array of performance in first index and goal targets in second
	 *         index
	 */
	protected double[][] performanceAndGoalTargets(ArrayList<Score<T>> scores) {
		int objectives = scores.get(0).scores.length;
		double[][] scoresArray = new double[objectives][scores.size()];
		double[][] stats = new double[2][objectives];

		// Put all scores in arrays
		for (int i = 0; i < scores.size(); i++) {
			double[] values = scores.get(i).scores;
			for (int j = 0; j < objectives; j++) {
				scoresArray[j][i] = values[j];
			}
		}

		for (int j = 0; j < objectives; j++) {
			// Get population performance measure
			stats[0][j] = performanceStat.stat(scoresArray[j]);
			// Get goal target
			stats[1][j] = goalTargetStat.stat(scoresArray[j]);
		}

		return stats; // [0] = performance, [1] = goal target
	}

	/**
	 * moves the recency weighted averages toward the averages
	 * @param averages, an array of doubles consisting of the averages
	 */
	private void moveRecencyWeightedAveragesTowardsAverages(double[] averages) {
		for (int i = 0; i < averages.length; i++) {
			recencyWeightedAverages[i] += alpha * (averages[i] - recencyWeightedAverages[i]);
		}
	}

	/**
	 * Determines whether each goal is currently being archieved, and updates
	 * goal values if needed.
	 * @param averages average score in each objective
	 * @param maxes maximum score in each objective (not currently used)
	 * @return boolean array indicated whether each objective is currently achieved
	 */
	private boolean[] goalAchievement(double[] averages, double[] maxes) {
		boolean[] achieved = new boolean[goals.length];
		for (int i = 0; i < achieved.length; i++) {
			boolean before = achieved[i];
			achieved[i] = (recencyWeightedAverages[i] > goals[i]) && (averages[i] > goals[i]);
			if (!before && achieved[i]) { // Wasn't achieved, but is now
				initialClimb[i] = false;
			} else if (Parameters.parameters.booleanParameter("tugGoalsIncreaseWhenThrashing") 
					&& !initialClimb[i]
							&& !achieved[i]) {
				// Too big of an increase
				// increaseGoal(i, maxes[i]);
				goals[i] *= Parameters.parameters.doubleParameter("increasingTUGGoalRatio");
				initialClimb[i] = true;
			}
		}
		return achieved;
	}

	/**
	 * Adjusts the goals and objectives based on recent weights which are 
	 * determined by the achievement or unachievement of the objectives
	 * @param averages
	 * @param maxes
	 */
	protected void adjustGoalsAndObjectives(double[] averages, double[] maxes) {
		moveRecencyWeightedAveragesTowardsAverages(averages);
		boolean[] achieved = goalAchievement(averages, maxes);
		int numAchieved = 0;
		boolean timeUp = false;
		for (int i = 0; i < achieved.length; i++) {
			if (achieved[i]) {
				numAchieved++;
			} else if (generation - Parameters.parameters.integerParameter("genOfLastTUGGoalIncrease") > timeLimit) {
				timeUp = true; // taking too long to achieve a goal
			}
		}
		if (numAchieved == achieved.length) {
			if (Parameters.parameters.booleanParameter("stopTUGGoalDropAfterAchievement")) {
				Parameters.parameters.setBoolean("tugGoalDropPossible", false);
			}
			Parameters.parameters.setInteger("genOfLastTUGGoalIncrease", this.generation);
			increaseGoals(maxes);
		} else {
			// If goals aren't achieved in a timely manner, reset the RWAs
			// to prevent one objective from hogging the attention of evolution
			if (timeUp) {
				System.out.println("Time Up: Reset RWAs");
				for (int i = 0; i < achieved.length; i++) {
					if (achieved[i]) {
						resetRecencyWeightedAverage(i);
						Parameters.parameters.setInteger("genOfLastTUGGoalIncrease", this.generation);
						increaseGoal(i, maxes[i]);
					}
				}
			}
			targetUnachievedGoals(achieved);
		}
		if (printFitness) {
			System.out.println("Using Objectives: " + Arrays.toString(useObjective));
		}

	}

	/**
	 * Indicate how many generations have passed since all TUG goals were last
	 * increased
	 *
	 * @return number of such generations
	 */
	public int gensSinceLastTUGGoalIncrease() {
		return this.generation - Parameters.parameters.integerParameter("genOfLastTUGGoalIncrease");
	}

	/**
	 * Adds some data to the logs if writing output was requested
	 * @param averages
	 * @param maxes
	 */
	protected void logOut(double[] averages, double[] maxes) {
		if (writeOutput) {
			ArrayList<double[]> vStats = new ArrayList<double[]>(2);
			vStats.add(averages);
			vStats.add(maxes);
			tugLog.log(vStats, generation);
		}
	}

	/**
	 * Increase goals in each objective
	 * @param maxes Current max scores in each objective
	 */
	private void increaseGoals(double[] maxes) {
		System.out.println("Increase goals");
		resetRecencyWeightedAverages();
		for (int i = 0; i < goals.length; i++) {
			increaseGoal(i, maxes[i]);
		}
	}

	/**
	 * Increase one objective
	 * @param objective index of objective
	 * @param target 
	 */
	private void increaseGoal(int objective, double target) {
		if (CommonConstants.constantTUGGoalIncrements) {
			goals[objective] += Parameters.parameters.doubleParameter("tugGoalIncrement" + objective);
		} else {
			// Momentum term similar to momentum in backpropogation
			lastTUGDelta[objective] = (tugMomentum * lastTUGDelta[objective]) + (eta * (target - goals[objective]));
			goals[objective] += lastTUGDelta[objective];
		}
		useObjective[objective] = true;
		justDeactivated[objective] = false;
		justActivated[objective] = true;
	}

	/**
	 * "target" the unachieved goals by using the objectives that have not yet been
	 * acheived yet.
	 * @param achieved, a boolean array holding true if the corresponding objective has been achieved
	 * 			and false if the objective has not been achieved yet
	 */
	private void targetUnachievedGoals(boolean[] achieved) {
		for (int i = 0; i < achieved.length; i++) {
			justDeactivated[i] = useObjective[i] && achieved[i];
			justActivated[i] = !useObjective[i] && !achieved[i];
			useObjective[i] = !achieved[i];
			if (useObjective[i] && Parameters.parameters.booleanParameter("tugGoalDropPossible")) {
				goals[i] *= CommonConstants.remainingTUGGoalRatio;
			}
		}
	}

	/**
	 * Provides a chance for changes to be made to the population based on the
	 * state of TUG.
	 *
	 * @param parents
	 *            Parent population of genotypes
	 * @return child population of genotypes
	 */
	@Override
	public ArrayList<Genotype<T>> getNextGeneration(ArrayList<Genotype<T>> parents) {
		ArrayList<Genotype<T>> nextGen = super.getNextGeneration(parents);
		// This feature isn't used much. The purpose was to see if explicitly
		// associating a module with each objective would help. It didn't
		// really, but if it had helped, then this code would have frozen modules for
		// deactivated objectives to make sure performance was maintained even
		// though the objective was switched off.

		// Freeze modes whose objectives are deactivated.
		if (CommonConstants.tugObjectiveModeLinkage) {
			assert nextGen.get(0) instanceof TWEANNGenotype : "TUG Objective/Mode linkage only makes sense with TWEANNs";
			assert((TWEANNGenotype) nextGen.get(0)).numModules == useObjective.length : "TUG Objective/Mode linkage requires number of modes and objectives to match";
			boolean melted = false;
			// If objectives are reactivated,
			// then the networks need to be frozen before modes for inactive (if
			// any) objectives are re-frozen.
			if (BooleanUtil.any(justActivated)
					|| (CommonConstants.tugObjectiveUsageLinkage && BooleanUtil.any(justDeactivated))) {
				for (Genotype<T> tg : nextGen) {
					((TWEANNGenotype) tg).meltNetwork();
				}
				melted = true;
				System.out.println("Melting all networks");
			}
			// Now freeze specific modes that need freezing
			int modesFrozen = 0;
			for (int i = 0; i < useObjective.length; i++) {
				// Previously frozen modes will remain frozen, unless a
				// melt changed that
				if (justDeactivated[i] || (melted && !useObjective[i])) {
					for (Genotype<T> tg : nextGen) {
						int target;
						if (CommonConstants.tugObjectiveUsageLinkage) {
							// Even though nextGen has been altered since
							// evaluation, the modeUsage is saved and corresponds 
							// to how the parents used their modes.
							int[] moduleUsage = ((TWEANNGenotype) tg).getModuleUsage();
							// Mode with next highest usage
							target = StatisticsUtilities.argmax(moduleUsage, modesFrozen);
						} else {
							target = i;
						}
						((TWEANNGenotype) tg).freezeModule(target);
					}
					System.out.println("Freezing mode " + (CommonConstants.tugObjectiveUsageLinkage ? modesFrozen + " most used" : i));
					modesFrozen++;
				}
			}
		}
		return nextGen;
	}

	@Override
	/**
	 * Selects the genotypes to be used in the next generation based off of the scores of the parents
	 * and the children and TUG
	 * Does not remove the achieved goals
	 * 
	 * @param numParents, an integer indicating the number of parents
	 * @param listScores, an arrayList of Scores
	 */
	public ArrayList<Genotype<T>> selection(int numParents, ArrayList<Score<T>> listScores) {
		if (CommonConstants.tugKeepsParetoFront) {
			// Achieved goals are not removed
			NSGA2Score<T>[] scoresArray = super.getNSGA2Scores(listScores);
			ArrayList<NSGA2Score<T>> paretoFront = getParetoFront(scoresArray);
			ArrayList<Genotype<T>> keepers = new ArrayList<Genotype<T>>(numParents);

			for (NSGA2Score<T> s : paretoFront) {
				keepers.add(s.individual);
			}

			if (paretoFront.size() == numParents) {
				return keepers;
			} else if (paretoFront.size() > numParents) {
				System.out.println("Just portion of Pareto front: " + numParents + " out of " + paretoFront.size());
				// Keep members of Pareto front that best satisfy TUG scores
				ArrayList<Score<T>> scoreFront = new ArrayList<Score<T>>(paretoFront.size());
				for (NSGA2Score<T> s : paretoFront) {
					scoreFront.add(s);
				}
				NSGA2Score<T>[] scores = getTUGScores(scoreFront);
				return staticSelection(numParents, scores);
			} else {
				// Save Pareto front and use TUG on the rest
				ArrayList<Score<T>> reducedScores = new ArrayList<Score<T>>(scoresArray.length);
				// Check all members of original population
				for (NSGA2Score<T> s : scoresArray) {
					boolean inFront = false;
					// See if they are in the Pareto front
					for (Genotype<T> g : keepers) {
						// Don't pass Pareto front to next level of selection
						if (g.getId() == s.individual.getId()) {
							inFront = true;
							// System.out.println("\t" + s);
							break;
						}
					}
					// Only do further selection if not in the front
					if (!inFront) {
						reducedScores.add(s);
					}
				}
				assert reducedScores.size() + paretoFront.size() == scoresArray.length : "Scores array should be split into Pareto front and everything else";
				// Super selection will be done with TUG scores
				NSGA2Score<T>[] scores = getTUGScores(reducedScores);
				ArrayList<Genotype<T>> tugResults = staticSelection(numParents - paretoFront.size(), scores);
				for (Genotype<T> g : tugResults) {
					keepers.add(g);
				}
				return keepers;
			}
		} else {
			// Default NSGA2 plus TUG:
			// Possible disable certain objectives
			NSGA2Score<T>[] scores = getTUGScores(listScores);
			// Then use NSGA2 like usual
			return staticSelection(numParents, scores);
		}
	}

	@Override
	/**
	 * Creates the children based off of the parents and the parents' scores
	 * 
	 * @param numChildren, the number of children to include
	 * @param parentScores, an arrayList of the Scores of the parents
	 */
	public ArrayList<Genotype<T>> generateChildren(int numChildren, ArrayList<Score<T>> parentScores) {
		// Now only the parents are used to calculate average population scores
		// Should there be an option to use children too?
		double[][] stats = performanceAndGoalTargets(parentScores);
		double[] performance = stats[0];
		double[] targets = stats[1];
		adjustGoalsAndObjectives(performance, targets);
		logOut(performance, targets);

		NSGA2Score<T>[] scoresArray = getTUGScores(parentScores);
		return generateNSGA2Children(numChildren, scoresArray, currentGeneration(), mating, crossoverRate);
	}

	@Override
	/**
	 * finishes up with logging and closes related io file content and logs
	 * @param population, an arrayList of Genotypes of the population
	 */
	public void close(ArrayList<Genotype<T>> population) {
		super.close(population);
		if (writeOutput) {
			this.tugLog.close();
		}
	}
}

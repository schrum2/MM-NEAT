package edu.utexas.cs.nn.evolution.nsga2.tug;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.RealValuedGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.nsga2.NSGA2;
import edu.utexas.cs.nn.evolution.nsga2.NSGA2Score;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.MultiObjectiveScore;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.mspacman.MsPacManTask;
import edu.utexas.cs.nn.util.BooleanUtil;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.stats.Statistic;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

import java.util.ArrayList;
import java.util.Arrays;

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
	public void close(ArrayList<Genotype<T>> population) {
		super.close(population);
		if (writeOutput) {
			this.tugLog.close();
		}
	}

	// Some unit tests
	// Should turn into proper JUnit Tests
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "io:false", "tugKeepsParetoFront:false" });
		MMNEAT.loadClasses();
		MMNEAT.task = new MsPacManTask();
		TUGNSGA2 ea = new TUGNSGA2<ArrayList<Double>>();

		ArrayList<Score<ArrayList<Double>>> scores = new ArrayList<Score<ArrayList<Double>>>();

		ArrayList<Long> layer0 = new ArrayList<Long>();
		RealValuedGenotype g0_10 = new RealValuedGenotype(new double[] { 0, 10 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g0_10, new double[] { 0, 10 }, null));
		layer0.add(scores.get(0).individual.getId());
		RealValuedGenotype g5_5 = new RealValuedGenotype(new double[] { 5, 5 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g5_5, new double[] { 5, 5 }, null));
		layer0.add(scores.get(1).individual.getId());
		RealValuedGenotype g10_0 = new RealValuedGenotype(new double[] { 10, 0 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g10_0, new double[] { 10, 0 }, null));
		layer0.add(scores.get(2).individual.getId());
		RealValuedGenotype g2_6 = new RealValuedGenotype(new double[] { 2, 6 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g2_6, new double[] { 2, 6 }, null));
		layer0.add(scores.get(3).individual.getId());

		ArrayList<Long> layer1 = new ArrayList<Long>();
		RealValuedGenotype g0_4 = new RealValuedGenotype(new double[] { 0, 4 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g0_4, new double[] { 0, 4 }, null));
		layer1.add(scores.get(4).individual.getId());
		RealValuedGenotype g3_3 = new RealValuedGenotype(new double[] { 3, 3 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g3_3, new double[] { 3, 3 }, null));
		layer1.add(scores.get(5).individual.getId());
		RealValuedGenotype g4_0 = new RealValuedGenotype(new double[] { 4, 0 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g4_0, new double[] { 4, 0 }, null));
		layer1.add(scores.get(6).individual.getId());

		RealValuedGenotype g1_1 = new RealValuedGenotype(new double[] { 1, 1 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g1_1, new double[] { 1, 1 }, null));

		System.out.println("Low goals should behave just like NSGA2");
		// ea.goals = new double[2];
		// System.out.println("Select 1: " + ea.selection(1, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 2: " + ea.selection(2, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 3: " + ea.selection(3, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result0 = ea.selection(4, scores);
		// System.out.println("Select 4: " + result0 + ", Use: " +
		// Arrays.toString(((TUGNSGA2) ea).useObjective));

		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result0), layer0) ? "PASSED 0"
				: "FAILED 0 " + layer0 + " AND " + result0);

		// ea.goals = new double[2];
		// System.out.println("Select 5: " + ea.selection(5, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 6: " + ea.selection(6, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result1 = ea.selection(7, scores);
		// System.out.println("Select 7: " + result1 + ", Use: " +
		// Arrays.toString(((TUGNSGA2) ea).useObjective));

		layer1.addAll(layer0);
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result1), layer1) ? "PASSED 1"
				: "FAILED 1 " + layer1 + " AND " + result1);

		ea.useObjective = new boolean[] { true, false };
		System.out.println("TUG towards objective 0");
		ea.goals = new double[] { 20, 0 };
		ArrayList<Genotype<ArrayList<Double>>> result2 = ea.selection(1, scores);
		ArrayList<Long> best = new ArrayList<Long>();
		best.add(g10_0.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result2), best) ? "PASSED 2"
				: "FAILED 2 " + best + " AND " + result2);
		// System.out.println("Select 1: " + ea.selection(1, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.goals = new double[] { 20, 0 };
		ArrayList<Genotype<ArrayList<Double>>> result3 = ea.selection(2, scores);
		best.add(g5_5.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result3), best) ? "PASSED 3"
				: "FAILED 3 " + best + " AND " + result3);
		// System.out.println("Select 2: " + ea.selection(2, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.goals = new double[] { 20, 0 };
		ArrayList<Genotype<ArrayList<Double>>> result4 = ea.selection(3, scores);
		best.add(g4_0.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result4), best) ? "PASSED 4"
				: "FAILED 4 " + best + " AND " + result4);
		// System.out.println("Select 3: " + ea.selection(3, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.goals = new double[] { 20, 0 };
		ArrayList<Genotype<ArrayList<Double>>> result5 = ea.selection(4, scores);
		best.add(g3_3.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result5), best) ? "PASSED 5"
				: "FAILED 5 " + best + " AND " + result5);
		// System.out.println("Select 4: " + ea.selection(4, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.goals = new double[] { 20, 0 };
		ArrayList<Genotype<ArrayList<Double>>> result6 = ea.selection(5, scores);
		best.add(g2_6.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result6), best) ? "PASSED 6"
				: "FAILED 6 " + best + " AND " + result6);
		// System.out.println("Select 5: " + ea.selection(5, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.goals = new double[] { 20, 0 };
		ArrayList<Genotype<ArrayList<Double>>> result6a = ea.selection(6, scores);
		best.add(g1_1.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result6a), best) ? "PASSED 6a"
				: "FAILED 6a " + best + " AND " + result6a);

		// System.out.println("Select 6: " + ea.selection(6, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[]{20, 0};
		// System.out.println("Select 7: " + ea.selection(7, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.useObjective = new boolean[] { false, true };
		System.out.println("TUG towards objective 1");
		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result2b = ea.selection(1, scores);
		best = new ArrayList<Long>();
		best.add(g0_10.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result2b), best) ? "PASSED 2b"
				: "FAILED 2b " + best + " AND " + result2b);

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result3b = ea.selection(2, scores);
		best.add(g2_6.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result3b), best) ? "PASSED 3b"
				: "FAILED 3b " + best + " AND " + result3b);

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result4b = ea.selection(3, scores);
		best.add(g5_5.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result4b), best) ? "PASSED 4b"
				: "FAILED 4b " + best + " AND " + result4b);

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result5b = ea.selection(4, scores);
		best.add(g0_4.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result5b), best) ? "PASSED 5b"
				: "FAILED 5b " + best + " AND " + result5b);

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result6b = ea.selection(5, scores);
		best.add(g3_3.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result6b), best) ? "PASSED 6b"
				: "FAILED 6b " + best + " AND " + result6b);

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result7b = ea.selection(6, scores);
		best.add(g1_1.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result7b), best) ? "PASSED 7b"
				: "FAILED 7b " + best + " AND " + result7b);

		Parameters.parameters.setBoolean("tugKeepsParetoFront", true);
		CommonConstants.tugKeepsParetoFront = true;

		System.out.println("Low goals should behave just like NSGA2, even with tugKeepsParetoFront on");

		ea.useObjective = new boolean[] { true, true };
		ArrayList<Genotype<ArrayList<Double>>> result7 = ea.selection(4, scores);
		ea.goals = new double[2];
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result7), layer0) ? "PASSED 7"
				: "FAILED 7 " + layer0 + " AND " + result7);

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result8 = ea.selection(7, scores);
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result8), layer1) ? "PASSED 8"
				: "FAILED 8 " + layer1 + " AND " + result8);

		// ea.goals = new double[2];
		// System.out.println("Select 1: " + ea.selection(1, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 2: " + ea.selection(2, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 3: " + ea.selection(3, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 4: " + ea.selection(4, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 5: " + ea.selection(5, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 6: " + ea.selection(6, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 7: " + ea.selection(7, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.useObjective = new boolean[] { true, false };
		System.out.println("TUG towards objective 0, but favor Pareto front first");

		ArrayList<Genotype<ArrayList<Double>>> result9 = ea.selection(4, scores);
		ea.goals = new double[] { 20, 0 };
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result9), layer0) ? "PASSED 9"
				: "FAILED 9 " + layer0 + " AND " + result9);

		ArrayList<Genotype<ArrayList<Double>>> result10 = ea.selection(5, scores);
		ea.goals = new double[] { 20, 0 };
		layer0.add(g4_0.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result10), layer0) ? "PASSED 10"
				: "FAILED 10 " + layer0 + " AND " + result10);

		ArrayList<Genotype<ArrayList<Double>>> result11 = ea.selection(6, scores);
		ea.goals = new double[] { 20, 0 };
		layer0.add(g3_3.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result11), layer0) ? "PASSED 11"
				: "FAILED 11 " + layer0 + " AND " + result11);

		ArrayList<Genotype<ArrayList<Double>>> result12 = ea.selection(7, scores);
		ea.goals = new double[] { 20, 0 };
		layer0.add(g1_1.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result12), layer0) ? "PASSED 12"
				: "FAILED 12 " + layer0 + " AND " + result12);

		// System.out.println("Select 1: " + ea.selection(1, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[]{20, 0};
		// System.out.println("Select 2: " + ea.selection(2, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[]{20, 0};
		// System.out.println("Select 3: " + ea.selection(3, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[]{20, 0};
		// System.out.println("Select 4: " + ea.selection(4, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[]{20, 0};
		// System.out.println("Select 5: " + ea.selection(5, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[]{20, 0};
		// System.out.println("Select 6: " + ea.selection(6, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[]{20, 0};
		// System.out.println("Select 7: " + ea.selection(7, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		scores = new ArrayList<Score<ArrayList<Double>>>();

		layer0 = new ArrayList<Long>();
		RealValuedGenotype x1000_11 = new RealValuedGenotype(new double[] { 1000, 11 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x1000_11, new double[] { 1000, 11 }, null));
		layer0.add(scores.get(0).individual.getId());

		layer1 = new ArrayList<Long>();
		RealValuedGenotype x0_10 = new RealValuedGenotype(new double[] { 0, 10 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x0_10, new double[] { 0, 10 }, null));
		layer1.add(scores.get(1).individual.getId());
		RealValuedGenotype x5_7 = new RealValuedGenotype(new double[] { 5, 7 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x5_7, new double[] { 5, 7 }, null));
		layer1.add(scores.get(2).individual.getId());
		RealValuedGenotype x50_5 = new RealValuedGenotype(new double[] { 50, 5 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x50_5, new double[] { 50, 5 }, null));
		layer1.add(scores.get(3).individual.getId());
		RealValuedGenotype x100_2 = new RealValuedGenotype(new double[] { 100, 2 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x100_2, new double[] { 100, 2 }, null));
		layer1.add(scores.get(4).individual.getId());
		RealValuedGenotype x500_0 = new RealValuedGenotype(new double[] { 500, 0 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x500_0, new double[] { 500, 0 }, null));
		layer1.add(scores.get(5).individual.getId());

		ArrayList<Long> layer2 = new ArrayList<Long>();
		RealValuedGenotype x10_5 = new RealValuedGenotype(new double[] { 10, 5 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x10_5, new double[] { 10, 5 }, null));
		layer2.add(scores.get(6).individual.getId());
		RealValuedGenotype x11_4 = new RealValuedGenotype(new double[] { 11, 4 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x11_4, new double[] { 11, 4 }, null));
		layer2.add(scores.get(7).individual.getId());
		RealValuedGenotype x40_3 = new RealValuedGenotype(new double[] { 40, 3 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x40_3, new double[] { 40, 3 }, null));
		layer2.add(scores.get(8).individual.getId());
		RealValuedGenotype x75_1 = new RealValuedGenotype(new double[] { 75, 1 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x75_1, new double[] { 75, 1 }, null));
		layer2.add(scores.get(9).individual.getId());

		ArrayList<Long> layer3 = new ArrayList<Long>();
		RealValuedGenotype x40_2 = new RealValuedGenotype(new double[] { 40, 2 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x40_2, new double[] { 40, 2 }, null));
		layer3.add(scores.get(10).individual.getId());
		RealValuedGenotype x50_1 = new RealValuedGenotype(new double[] { 50, 1 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x50_1, new double[] { 50, 1 }, null));
		layer3.add(scores.get(11).individual.getId());

		ArrayList<Long> layer4 = new ArrayList<Long>();
		RealValuedGenotype x10_2 = new RealValuedGenotype(new double[] { 10, 2 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x10_2, new double[] { 10, 2 }, null));
		layer4.add(scores.get(12).individual.getId());
		RealValuedGenotype x40_1 = new RealValuedGenotype(new double[] { 40, 1 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x40_1, new double[] { 40, 1 }, null));
		layer4.add(scores.get(13).individual.getId());

		ArrayList<Long> layer5 = new ArrayList<Long>();
		RealValuedGenotype x5_2 = new RealValuedGenotype(new double[] { 5, 2 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x5_2, new double[] { 5, 2 }, null));
		layer5.add(scores.get(14).individual.getId());
		RealValuedGenotype x10_1 = new RealValuedGenotype(new double[] { 10, 1 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x10_1, new double[] { 10, 1 }, null));
		layer5.add(scores.get(15).individual.getId());

		/// Start testing ///////////////////////

		Parameters.parameters.setBoolean("tugKeepsParetoFront", false);
		CommonConstants.tugKeepsParetoFront = false;

		System.out.println("Using a different set of points");
		System.out.println("Low goals should behave just like NSGA2");

		ea.useObjective = new boolean[] { true, true };
		ArrayList<Genotype<ArrayList<Double>>> result13 = ea.selection(1, scores);
		ea.goals = new double[2];
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result13), layer0) ? "PASSED 13"
				: "FAILED 13 " + layer0 + " AND " + result13);

		layer1.addAll(layer0);

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result14 = ea.selection(6, scores);
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result14), layer1) ? "PASSED 14"
				: "FAILED 14 " + layer1 + " AND " + result14);

		layer2.addAll(layer1);

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result15 = ea.selection(10, scores);
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result15), layer2) ? "PASSED 15"
				: "FAILED 15 " + layer2 + " AND " + result15);

		layer3.addAll(layer2);

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result16 = ea.selection(12, scores);
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result16), layer3) ? "PASSED 16"
				: "FAILED 16 " + layer3 + " AND " + result16);

		layer4.addAll(layer3);

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result17 = ea.selection(14, scores);
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result17), layer4) ? "PASSED 17"
				: "FAILED 17 " + layer4 + " AND " + result17);

		layer5.addAll(layer4);

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result18 = ea.selection(16, scores);
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result18), layer5) ? "PASSED 18"
				: "FAILED 18 " + layer5 + " AND " + result18);

		// Now change the goals

		ea.useObjective = new boolean[] { false, true };
		System.out.println("TUG towards objective 1");
		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result19 = ea.selection(1, scores);
		best = new ArrayList<Long>();
		best.add(x1000_11.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result19), best) ? "PASSED 19"
				: "FAILED 19 " + best + " AND " + result19);

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result20 = ea.selection(2, scores);
		best.add(x0_10.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result20), best) ? "PASSED 20"
				: "FAILED 20 " + best + " AND " + result20);

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result21 = ea.selection(3, scores);
		best.add(x5_7.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result21), best) ? "PASSED 21"
				: "FAILED 21 " + best + " AND " + result21);

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result22 = ea.selection(5, scores);
		best.add(x10_5.getId());
		best.add(x50_5.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result22), best) ? "PASSED 22"
				: "FAILED 22 " + best + " AND " + result22);

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result23 = ea.selection(6, scores);
		best.add(x11_4.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result23), best) ? "PASSED 23"
				: "FAILED 23 " + best + " AND " + result23);

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result24 = ea.selection(7, scores);
		best.add(x40_3.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result24), best) ? "PASSED 24"
				: "FAILED 24 " + best + " AND " + result24);

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result25 = ea.selection(11, scores);
		best.add(x5_2.getId());
		best.add(x10_2.getId());
		best.add(x40_2.getId());
		best.add(x100_2.getId());
		System.out.println(ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result25), best) ? "PASSED 25"
				: "FAILED 25 " + best + " AND " + result25);
	}
}

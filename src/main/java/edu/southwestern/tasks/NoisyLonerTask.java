package edu.southwestern.tasks;

import edu.southwestern.evolution.GenerationalEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.MultiObjectiveScore;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.mspacman.agentcontroller.pacman.NNCheckEachDirectionPacManController;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.stats.Average;
import edu.southwestern.util.stats.Statistic;
import edu.southwestern.util.stats.StatisticsUtilities;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Any task in which multiple trials are needed because evaluations are noisy.
 * The final fitness score of an individual is some statistic of the scores from
 * all evaluations, such as average or maximum.
 *
 * @author Jacob Schrum
 */
public abstract class NoisyLonerTask<T> extends LonerTask<T> {

	public Statistic stat;
	public final boolean printFitness;

	/**
	 * constructor for a noisy loner task. Assigns fitness according to
	 * specifications provided in the parameters of the command line
	 */
	public NoisyLonerTask() {
		this.printFitness = Parameters.parameters.booleanParameter("printFitness");
		try {
			stat = (Statistic) ClassCreation.createObject("noisyTaskStat");
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Any domain-specific prep that needs to be done before starting the
	 * sequence of evals
	 */
	public void prep() {
	}

	/**
	 * Any domain-specific cleanup that needs to be done after evaluations
	 */
	public void cleanup() {
	}

	/**
	 * All actions performed in a single evaluation of a genotype. Must override EITHER this method
	 * or the version below that does NOT include the behaviorCharacteristics. If you want to use your
	 * task with MAP-Elites, then override this version and fill the behaviorCharacteristics. Otherwise,
	 * override the version below.
	 *
	 * @param individual genotype to be evaluated
	 * @param num which evaluation is currently being performed
	 * @param behaviorCharacteristics Place to store various details about the behavior of resulting genotype (for MAP-Elites)
	 * @return Combination of fitness scores (multiobjective possible), and
	 *         other scores (for tracking non-fitness data)
	 */
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num, HashMap<String, Object> behaviorCharacteristics) {
		return oneEval(individual, num);
	}

	/**
	 * This exists both for backwards compatibility, and for tasks that don't bother using MAP-Elites. Older tasks did not
	 * make use of a HashMap of behavior characteristic data, so they already override this method. If this method is overridden
	 * and the one above is not, then this one will be called and provide score information for traditional objective-based
	 * evolution.
	 * 
	 * @param individual genotype to be evaluated
	 * @param num which evaluation is currently being performed
	 * @return Combination of fitness scores (multiobjective possible), and
	 *         other scores (for tracking non-fitness data)
	 */
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		throw new UnsupportedOperationException("This task is supposed to define a behavior characteristic for MAP Elites using a HashMap. "+
												"Call oneEval(Genotype<T> individual, int num, HashMap<String, Object> behaviorCharacteristics) instead.");
	}

	
	/**
	 * Evaluate an agent by subjecting it to several separate evaluations/trials
	 * in the domain. Return the fitness score(s)
	 *
	 * @param individual Genotype of individual to be evaluated
	 * @return score instance containing the fitness scores, and other necessary
	 *         data
	 */
	@Override
	public Score<T> evaluate(Genotype<T> individual) {
		prep();
		int numTrials;
		// Determine the number of trials to evaluate the agent for
		if(Parameters.parameters.booleanParameter("scaleTrials")){
			numTrials = (int) Math.ceil((((double) ((GenerationalEA) MMNEAT.ea).currentGeneration() + 0.01) / 
					Parameters.parameters.integerParameter("maxGens")) * CommonConstants.trials);
			numTrials = Math.min(numTrials, CommonConstants.trials);
		} else {
			numTrials = CommonConstants.trials; // Standard approach
		}

		// One set of scores for each trial
		double[][] objectiveScores = new double[numTrials][this.numObjectives()];
		double[][] otherScores = new double[numTrials][this.numOtherScores()];
		double evalTimeSum = 0;
		
		// Carry out all trials and save all scores
		HashMap<String,Object> behaviorMap = new HashMap<>();
		for (int i = 0; i < numTrials; i++) {
			long before = System.currentTimeMillis();
			if (MMNEAT.evalReport != null) {
				MMNEAT.evalReport.log("Eval " + i + ":");
			}
			Pair<double[], double[]> result = oneEval(individual, i, behaviorMap);
			if (printFitness) {
				System.out.println(Arrays.toString(result.t1) + Arrays.toString(result.t2));
				if (individual instanceof TWEANNGenotype) {
					System.out.println(
							"Module Usage: " + Arrays.toString(((TWEANNGenotype) individual).getModuleUsage()));
				}
			}
			long after = System.currentTimeMillis();
			evalTimeSum += (after - before);
			objectiveScores[i] = result.t1; // fitness scores
			// ScoreHistory.add(individual.getId(), result.t1);
			otherScores[i] = result.t2; // other scores
		}
//		System.out.println(Arrays.deepToString(objectiveScores));
//		System.out.println(Arrays.deepToString(otherScores));
		double averageEvalTime = evalTimeSum / numTrials;
		// Combine scores acorss evals into one score in each obejctive
		Pair<double[],double[]> aggregated = aggregateResults(stat, objectiveScores, otherScores);
		double[] fitness = aggregated.t1;
		double[] other = aggregated.t2;		
		
		if (printFitness) {
			System.out.println("Individual: " + individual.getId());
			System.out.println("\t" + scoreSummary(objectiveScores, otherScores, fitness, other));
		}
		if (MMNEAT.evalReport != null) {
			MMNEAT.evalReport.log(scoreSummary(objectiveScores, otherScores, fitness, other));
			// save information about various pacman variables/values in the eval report
			if (NNCheckEachDirectionPacManController.totalChosenDirectionModeUsageCounts != null) {
				MMNEAT.evalReport.log("Usage: "
						+ Arrays.toString(StatisticsUtilities
								.distribution(NNCheckEachDirectionPacManController.totalChosenDirectionModeUsageCounts))
						+ Arrays.toString(StatisticsUtilities.distribution(
								NNCheckEachDirectionPacManController.totalChosenDirectionJunctionModeUsageCounts))
						+ Arrays.toString(StatisticsUtilities.distribution(
								NNCheckEachDirectionPacManController.totalChosenDirectionEdibleModeUsageCounts))
						+ Arrays.toString(StatisticsUtilities.distribution(
								NNCheckEachDirectionPacManController.totalChosenDirectionThreatModeUsageCounts))
						+ Arrays.toString(StatisticsUtilities.distribution(
								NNCheckEachDirectionPacManController.totalChosenDirectionJunctionEdibleModeUsageCounts))
						+ Arrays.toString(StatisticsUtilities.distribution(
								NNCheckEachDirectionPacManController.totalChosenDirectionJunctionThreatModeUsageCounts)));
				MMNEAT.evalReport.log("Total Mode Usage Across Evals");
				MMNEAT.evalReport.log("\tMode Usage For Chosen Direction Networks: "
						+ Arrays.toString(NNCheckEachDirectionPacManController.totalChosenDirectionModeUsageCounts)
						+ ":" + Arrays.toString(StatisticsUtilities.distribution(
								NNCheckEachDirectionPacManController.totalChosenDirectionModeUsageCounts)));
				NNCheckEachDirectionPacManController.totalChosenDirectionModeUsageCounts = null;
				MMNEAT.evalReport.log("\tMode Usage At Junctions For Chosen Direction Networks: "
						+ Arrays.toString(
								NNCheckEachDirectionPacManController.totalChosenDirectionJunctionModeUsageCounts)
						+ ":" + Arrays.toString(StatisticsUtilities.distribution(
								NNCheckEachDirectionPacManController.totalChosenDirectionJunctionModeUsageCounts)));
				NNCheckEachDirectionPacManController.totalChosenDirectionJunctionModeUsageCounts = null;
				MMNEAT.evalReport.log("\tEdible Mode Usage For Chosen Direction Networks: "
						+ Arrays.toString(
								NNCheckEachDirectionPacManController.totalChosenDirectionEdibleModeUsageCounts)
						+ ":" + Arrays.toString(StatisticsUtilities.distribution(
								NNCheckEachDirectionPacManController.totalChosenDirectionEdibleModeUsageCounts)));
				NNCheckEachDirectionPacManController.totalChosenDirectionEdibleModeUsageCounts = null;
				MMNEAT.evalReport.log("\tThreat Mode Usage For Chosen Direction Networks: "
						+ Arrays.toString(
								NNCheckEachDirectionPacManController.totalChosenDirectionThreatModeUsageCounts)
						+ ":" + Arrays.toString(StatisticsUtilities.distribution(
								NNCheckEachDirectionPacManController.totalChosenDirectionThreatModeUsageCounts)));
				NNCheckEachDirectionPacManController.totalChosenDirectionThreatModeUsageCounts = null;
				MMNEAT.evalReport.log("\tEdible Mode Usage At Junctions For Chosen Direction Networks: "
						+ Arrays.toString(
								NNCheckEachDirectionPacManController.totalChosenDirectionJunctionEdibleModeUsageCounts)
						+ ":" + Arrays.toString(StatisticsUtilities.distribution(
								NNCheckEachDirectionPacManController.totalChosenDirectionJunctionEdibleModeUsageCounts)));
				NNCheckEachDirectionPacManController.totalChosenDirectionJunctionEdibleModeUsageCounts = null;
				MMNEAT.evalReport.log("\tThreat Mode Usage At Junctions For Chosen Direction Networks: "
						+ Arrays.toString(
								NNCheckEachDirectionPacManController.totalChosenDirectionJunctionThreatModeUsageCounts)
						+ ":" + Arrays.toString(StatisticsUtilities.distribution(
								NNCheckEachDirectionPacManController.totalChosenDirectionJunctionThreatModeUsageCounts)));
				NNCheckEachDirectionPacManController.totalChosenDirectionJunctionThreatModeUsageCounts = null;
			}
		}
		cleanup();
		// creates the score based off of the multiple objective score
		Score<T> s = new MultiObjectiveScore<T>(individual, fitness, getBehaviorVector(), other);
		s.assignMAPElitesBehaviorMapAndScore(behaviorMap);
		// set the average time
		s.averageEvalTime = averageEvalTime;
		return s;
	}

	/**
	 * Aggregates objective/fitness scores and other scores by averaging them.
	 * @param objectiveScores fitness scores: affect selection. 
	 * 						  Each objectiveScores[i] contains scores in different objectives from one eval.
	 * @param otherScores other scores that do not affect selection
	 * 					  Each otherScores[i] contains scores from different other scores from one eval
	 * @return Pair of averaged scores: first item is from each fitness objective, and second item is from each other score.
	 */
	public static Pair<double[], double[]> averageResults(double[][] objectiveScores, double[][] otherScores) {
		return aggregateResults(new Average(), objectiveScores,otherScores);
	}	
	
	/**
	 * Given several objective/fitness score results and other score results, aggregate them
	 * using the specified default Statistic (although individual fitness functions can have
	 * their aggregation method overridden).
	 * 
	 * @param stat Aggregation method
	 * @param objectiveScores fitness scores: affect selection. 
	 * 						  Each objectiveScores[i] contains scores in different objectives from one eval.
	 * @param otherScores other scores that do not affect selection
	 * 					  Each otherScores[i] contains scores from different other scores from one eval
	 * @return Pair of aggregated scores: first item is from each fitness objective, and second item is from each other score.
	 */
	public static Pair<double[], double[]> aggregateResults(Statistic stat, double[][] objectiveScores, double[][] otherScores) {
		double[] fitness = new double[objectiveScores[0].length];
		// Aggregate each fitness score across all trials
		for (int i = 0; i < fitness.length; i++) {
			if (MMNEAT.aggregationOverrides.get(i) == null) {
				fitness[i] = stat.stat(ArrayUtil.column(objectiveScores, i));
			} else {
				// Override aggregation statistic for a specific fitness function
				fitness[i] = MMNEAT.aggregationOverrides.get(i).stat(ArrayUtil.column(objectiveScores, i));
			}
		}
		double[] other = new double[otherScores[0].length];
		// Aggregate each other score across all trials
		for (int i = 0; i < other.length; i++) {
			if (MMNEAT.aggregationOverrides.get(fitness.length + i) == null) {
				other[i] = stat.stat(ArrayUtil.column(otherScores, i));
			} else {
				// Override aggregation statistic for a specific other function
				other[i] = MMNEAT.aggregationOverrides.get(fitness.length + i).stat(ArrayUtil.column(otherScores, i));
			}
		}
		return new Pair<>(fitness,other);
	}

	public static String scoreSummary(double[][] objectiveScores, double[][] otherScores, double[] fitness, double[] other) {
		return scoreSummary(objectiveScores, otherScores, fitness, other, 0);
	}	
	
	/**
	 * obtain a summary of the fitness and other scores
	 *
	 * @param objectiveScores
	 * @param otherScores
	 * @param fitness
	 * @param other
	 * @param starting index of the scores 
	 * @return the summary in a string
	 */
	public static String scoreSummary(double[][] objectiveScores, double[][] otherScores, double[] fitness, double[] other, int fitnessStart) {
		String nl = System.getProperty("line.separator");
		String result = "";
		result += "Fitness scores:" + nl;
		int globalFitnessFunctionIndex = fitnessStart;
		for (int i = 0; i < fitness.length; i++) {
			Statistic fitnessStat = MMNEAT.aggregationOverrides.get(globalFitnessFunctionIndex);
			boolean includeStdev = fitnessStat == null || fitnessStat instanceof Average;
			String fitnessFunctionName = MMNEAT.fitnessFunctions.get(0).get(globalFitnessFunctionIndex)
					+ (includeStdev ? "" : "[" + fitnessStat.getClass().getSimpleName() + "]");
			globalFitnessFunctionIndex++;
			double[] xs = ArrayUtil.column(objectiveScores, i);
			double stdev = StatisticsUtilities.sampleStandardDeviation(xs);
			result += "\t" + fitnessFunctionName + ":\t" + Arrays.toString(xs) + ":" + fitness[i]
					+ (includeStdev ? " +/- " + stdev : "") + nl;
		}
		result += "\tOther scores:" + nl;
		for (int i = 0; i < other.length; i++) {
			Statistic fitnessStat = MMNEAT.aggregationOverrides.get(globalFitnessFunctionIndex);
			boolean includeStdev = fitnessStat == null || fitnessStat instanceof Average;
			String otherScoreName = MMNEAT.fitnessFunctions.get(0).get(globalFitnessFunctionIndex)
					+ (includeStdev ? "" : "[" + fitnessStat.getClass().getSimpleName() + "]");
			globalFitnessFunctionIndex++;
			double[] xs = ArrayUtil.column(otherScores, i);
			double stdev = StatisticsUtilities.sampleStandardDeviation(xs);
			result += "\t" + otherScoreName + ":\t" + Arrays.toString(xs) + ":" + other[i]
					+ (includeStdev ? " +/- " + stdev : "") + nl;
		}
		result += "Fitness:" + Arrays.toString(fitness) + nl;
		result += "OtherScores:" + Arrays.toString(other) + nl;
		return result;
	}
}

/*
 * Haven't decided how to fully apply this yet. At some point in selection,
 * the scores here need to replace the ones currently used, but keeping a
 * record of the actual rather than aggregate scores is still a good idea.
 */
package edu.utexas.cs.nn.evolution;

import edu.utexas.cs.nn.util.stats.Statistic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * TODO: Actually use these methods at an appropriate place in the code,
 *       if a given command line parameter is set.
 *
 * @author Jacob Schrum
 */
public class ScoreHistory {

	// For tracking past history of scores for a given genotype ID
	private static HashMap<Long, ArrayList<double[]>> allScores = new HashMap<Long, ArrayList<double[]>>();
	// Track access to entries in allScores
	private static HashMap<Long, Boolean> accessed = new HashMap<Long, Boolean>();

	/**
	 * Indicate that none of the scores have been accessed
	 * in the current cycle of access.
	 */
	public static void resetAccess() {
		accessed = new HashMap<Long, Boolean>();
		for (Long id : allScores.keySet()) {
			accessed.put(id, Boolean.FALSE);
		}
	}

	/**
	 * Add scores for a given genotype ID
	 * @param id Genotype ID
	 * @param scores Scores of agent
	 */
	public static void add(long id, double[] scores) {
		if (!allScores.containsKey(id)) {
			allScores.put(id, new ArrayList<double[]>());
		}
		allScores.get(id).add(scores);
		accessed.put(id, Boolean.TRUE);
	}

	/**
	 * Retrieve individual genotype's history of scores in a particular objective.
	 * @param id of genotype
	 * @param objective Index of objective in score array
	 * @return array of all scores by genotype in that objective
	 */
	private static double[] scoresInObjective(long id, int objective) {
		ArrayList<double[]> ss = allScores.get(id);
		double[] result = new double[ss.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = ss.get(i)[objective];
		}
		return result;
	}

	/**
	 * Apply given statistic across history of all scores in a given objective
	 * 
	 * @param id of genotype
	 * @param objective index of objective
	 * @param s statistic for aggregation
	 * @return result of applying statistic to all scores in the objective
	 */
	private static double applyStat(long id, int objective, Statistic s) {
		return s.stat(scoresInObjective(id, objective));
	}

	/**
	 * Apply statistic to history of agent's scores in all objectives
	 * 
	 * @param id of genotype
	 * @param s statistic for aggregation
	 * @return array of aggregate stats in each objective
	 */
	public static double[] applyStat(long id, Statistic s) {
		int numObjectives = allScores.get(id).get(0).length;
		double[] result = new double[numObjectives];
		for (int i = 0; i < numObjectives; i++) {
			result[i] = applyStat(id, i, s);
		}
		return result;
	}
	
	/**
	 * This is the approach used for fitness inheritance as in LEEA.
	 * Just get the most recent fitness score logged by the parent.
	 * @param id Genotype ID
	 * @return Score received by genotype on previous generation
	 */
	public static double[] getLast(long id) {
		accessed.put(id, Boolean.TRUE);
		ArrayList<double[]> scores = allScores.get(id);
		return scores.get(scores.size() - 1);
	}

	/**
	 * Remove score information for ids that were not accessed
	 * recently. Do not store lots of unnecessary data.
	 * 
	 * Also resets access to all remaining scores.
	 */
	public static void clean() {
		Iterator<Long> itr = allScores.keySet().iterator();
		while (itr.hasNext()) {
			Long id = itr.next();
			if (!accessed.get(id)) {
				itr.remove();
				accessed.remove(id);
			}
		}
		resetAccess();
	}
}

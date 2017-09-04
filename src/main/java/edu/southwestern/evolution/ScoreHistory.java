package edu.southwestern.evolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.stats.Statistic;

/**
 * Tracks scores of all individuals still in population.
 * Used in different ways depending on certain settings.
 * 
 * @author Jacob Schrum
 */
public class ScoreHistory {

	// For tracking past history of scores for a given genotype ID
	private static HashMap<Long, ArrayList<double[]>> allScores = new HashMap<Long, ArrayList<double[]>>();
	// Track access to entries in allScores
	private static HashMap<Long, Boolean> accessed = new HashMap<Long, Boolean>();

	public static void save() {
		if(!allScores.isEmpty()) {
			String base = Parameters.parameters.stringParameter("base");
			int runNumber = Parameters.parameters.integerParameter("runNumber");
			String saveTo = Parameters.parameters.stringParameter("saveTo");
			String historyFile = base + "/" + saveTo + runNumber + "/scoreHistory.txt";
			try {
				PrintStream ps = new PrintStream(new File(historyFile));
				for(Long key: allScores.keySet()){
					ps.println(key);
					for(double[] scores: allScores.get(key)) {
						ps.print("\t");
						for(double score: scores) {
							ps.print(score + " ");
						}
						ps.println();
					}
				}
				ps.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	public static void load() {
		if(CommonConstants.inheritFitness || CommonConstants.averageScoreHistory) {
			String base = Parameters.parameters.stringParameter("base");
			int runNumber = Parameters.parameters.integerParameter("runNumber");
			String saveTo = Parameters.parameters.stringParameter("saveTo");
			String historyFile = base + "/" + saveTo + runNumber + "/scoreHistory.txt";
			if(new File(historyFile).exists()) { // Will not exist in first generation
				allScores = new HashMap<Long, ArrayList<double[]>>();

				try {
					Scanner scan = new Scanner(new File(historyFile));
					while(scan.hasNextLine()) {
						long id = scan.nextLong();
						scan.nextLine();
						ArrayList<double[]> scoreHistory = new ArrayList<double[]>();
						while(scan.hasNextDouble() && ! scan.hasNextLong()) { // doubles that are not longs (decimal point)
							String line = scan.nextLine();
							String[] stringScores = line.split(" ");
							double[] scores = new double[stringScores.length];
							for(int i = 0; i < scores.length; i++) {
								scores[i] = Double.parseDouble(stringScores[i]);
							}
							scoreHistory.add(scores);
						}
						allScores.put(id, scoreHistory);
					}
					scan.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}
	
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
		try {
			return scores.get(scores.size() - 1);
		} catch(NullPointerException e) {
			System.out.println("get id: " + id);
			for(Long key: allScores.keySet()){
				System.out.println(key);
				for(double[] scores1: allScores.get(key)) {
					System.out.println(Arrays.toString(scores1));
				}
			}
			throw e;
		}
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
			if (accessed.get(id) == null || !accessed.get(id)) {
				itr.remove();
				accessed.remove(id);
			}
		}
		resetAccess();
	}
}

package edu.southwestern.evolution.nsga2.tug;

import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.nsga2.NSGA2Score;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.scores.Score;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;
import java.util.ArrayList;
import java.util.Random;

/**
 * I think this was a test class used to troubleshoot TUG. The idea was to run
 * some rounds of selection with NSGA2 both with and without TUG to track
 * exactly what the differences were in which individuals were selected.
 *
 * @author Jacob Schrum
 */
public class CompareNSGA2ToTUG<T> extends TUGNSGA2<T> {

	public static final boolean GHOSTS_ONLY = true;
	MMNEATLog compareLog;

	/**
	 * Creates an instance of CompareNSGA2ToTUG
	 */
	public CompareNSGA2ToTUG() {
		super();
		compareLog = new MMNEATLog("CompareNSGA2ToTUG", true);
	}

	@Override
	/**
	 * Selects the genotypes to be used in the next generation based off of the scores of the parents
	 * and the children and TUG
	 * Does not remove the achieved goals with TUG
	 * 
	 * @param numParents, an integer indicating the number of parents
	 * @param scores, an arrayList of Scores
	 */
	public ArrayList<Genotype<T>> selection(int numParents, ArrayList<Score<T>> scores) {
		long seed = RandomNumbers.randomGenerator.nextLong();
		RandomNumbers.randomGenerator = new Random(seed);
		ArrayList<Genotype<T>> tugResult = super.selection(numParents, scores);

		// Temporary testing for how NSGA2 with just first objective behaves
		// Have to act as if ghost objective is only score, before crowding
		// distance, etc. calculated
		if (GHOSTS_ONLY) {
			for (Score<T> s : scores) {
				s.dropLastScore(); // drops pill score
			}
		}
		NSGA2Score<T>[] staticScores = staticNSGA2Scores(scores);
		// Reset seed for other selection method
		RandomNumbers.randomGenerator = new Random(seed);
		ArrayList<Genotype<T>> nsga2Result = staticSelection(numParents, staticScores);

		logResultDifferences(tugResult, nsga2Result, GHOSTS_ONLY, staticScores, "Combined");

		return tugResult;
	}

	@Override
	/**
	 * Creates the children based off of the parents and the parents' scores
	 * 
	 * @param numChildren, the number of children to include
	 * @param parentScores, an arrayList of the Scores of the parents
	 */
	public ArrayList<Genotype<T>> generateChildren(int numChildren, ArrayList<Score<T>> parentScores) {
		long seed = RandomNumbers.randomGenerator.nextLong();
		RandomNumbers.randomGenerator = new Random(seed);
		ArrayList<Genotype<T>> tugResult = super.generateChildren(numChildren, parentScores);

		// The generated children will have different ids, but the parent ids
		// should be the same in each case. The two groups are separated by this line.
		EvolutionaryHistory.logLineageData("---Line between comparisons----------------");

		ArrayList<Score<T>> copyScores = new ArrayList<Score<T>>();
		for (Score<T> s : parentScores) {
			double[] copyScoreArray = new double[s.scores.length];
			System.arraycopy(s.scores, 0, copyScoreArray, 0, copyScoreArray.length);
			double[] copyOtherArray = new double[s.otherStats.length];
			System.arraycopy(s.otherStats, 0, copyOtherArray, 0, copyOtherArray.length);
			copyScores.add(new Score<T>(s.individual, copyScoreArray, null, copyOtherArray));
		}

		// Temporary testing for how NSGA2 with just first objective behaves
		// Have to act as if ghost objective is only score, before crowding
		// distance, etc. calculated
		if (GHOSTS_ONLY) {
			for (Score<T> s : copyScores) {
				s.dropLastScore(); // drops pill score
			}
		}
		//NSGA2Score<T>[] scoresArray = staticNSGA2Scores(copyScores);
		// Reset seed for other selection method
		//RandomNumbers.randomGenerator = new Random(seed);
		//ArrayList<Genotype<T>> nsga2Result = generateNSGA2Children(numChildren, scoresArray, currentGeneration(), mating, crossoverRate);

		// Just realized, this comparison simply doesn't work for child
		// generation because each child has a newly generated id number. What matters here
		// is which parents are chosen to reproduce and mate
		// logResultDifferences(tugResult,nsga2Result,GHOSTS_ONLY,scoresArray,"Child");

		return tugResult;
	}

	/**
	 * Logs the differences between NSGA2 and NSGA2 with TUG
	 * @param tugResult, the resulting list of genotypes (population) with TUG
	 * @param nsga2Result, the resulting list of genotypes (population) without TUG (using NSGA2)
	 * @param ghostOnly, whether or not it is only ghosts
	 * @param staticScores, an array of NSGA2Score instances
	 * @param stage, a string indicating what stage in the generation the selection is in
	 */
	private void logResultDifferences(ArrayList<Genotype<T>> tugResult, ArrayList<Genotype<T>> nsga2Result,
			boolean ghostOnly, NSGA2Score<T>[] staticScores, String stage) {
		Pair<ArrayList<Genotype<T>>, ArrayList<Genotype<T>>> result = PopulationUtil.populationDifferences(nsga2Result,
				tugResult);
		compareLog.log("--Generation " + this.generation + " " + stage + " Selection-----------------");
		compareLog.log("  In " + (ghostOnly ? "Ghost Selection" : "NSGA2") + " but not TUG:");
		for (Genotype<T> g : result.t1) {
			compareLog.log("\t" + g.getId() + ": " + PopulationUtil.scoreWithId(g.getId(), staticScores));
		}
		compareLog.log("  In TUG but not " + (ghostOnly ? "Ghost Selection" : "NSGA2") + ":");
		for (Genotype<T> g : result.t2) {
			compareLog.log("\t" + g.getId() + ": " + PopulationUtil.scoreWithId(g.getId(), staticScores));
		}
	}
}

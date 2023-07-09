package edu.southwestern.util;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.nsga2.NSGA2;
import edu.southwestern.evolution.nsga2.NSGA2Score;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import jmetal.qualityIndicator.Hypervolume;
import java.util.Arrays;

/**
 * Contains methods for computing Pareto fronts. Based on jmetal.qualityIndicator.Hypervolume
 * hypervolume is the volume behind the pareto front
 * @author lewisj
 *
 */
public class MultiobjectiveUtil {

	/**
	 * Calculates the hypervolume from a list of scores. Assumes that the list is the current pareto front.
	 * Turns the list of scores into scoresArrayForHypervolume[number of points][number of objectives].
	 * calles calculateHypervolume for hypervolume calculations.
	 * Assumes all scores have the same number of objective scores.
	 * Assumes the objectives are used for selection, and thus the min scores
	 * come from the min scores of the task.
	 * 
	 * @param <S> any type of Score, whether a regular Score or NSGA2Score
	 * @param scores the list of scores for a pareto front being evaluated
	 * @return the hypervolume of the pareto front.
	 */
	public static <S extends Score<?>> double hypervolumeFromParetoFront(List<S> scores) {
		double[] minScoreForEachObjective = MMNEAT.task.minScores();
		return hypervolumeFromParetoFront(scores, minScoreForEachObjective);
	}
	
	/**
	 * Like above, but the min scores can be specified in any way.
	 * @param scores scores the list of scores for a pareto front being evaluated
	 * @param minScoreForEachObjective min scores for each objective
	 * @return hypervolume of Pareto front
	 */
	public static <S extends Score<?>> double hypervolumeFromParetoFront(List<S> scores, double[] minScoreForEachObjective) {
//		System.out.println("score scores length:" + scores.get(0).scores.length);
		assert scores.size() > 0 : "scores size is less than 0";
		if(scores.size() <= 0) {
			System.out.println("score scores length:" + scores.size());
			//System.out.println("scores.scores" + scores.get(0).toString());
			throw new IllegalStateException("Empty bin should not be possible here: " + scores);
		}
		int numberOfObjectives = scores.get(0).scores.length;
		int numberOfPoints = scores.size();
		double[][] scoresArrayForHypervolume = new double[numberOfPoints][numberOfObjectives];
		int i = 0;
		
		
		for (S individualScore : scores) {		//adds the scores to the double array of scores
			scoresArrayForHypervolume[i] = Arrays.copyOf(individualScore.scores,individualScore.scores.length);
			for (int j = 0; j < scoresArrayForHypervolume[i].length; j++) {		//this should subtract the minimum score value for each objective from its score
				scoresArrayForHypervolume[i][j] -= minScoreForEachObjective[j]; //before passing to hypervolume, adjust scores for the minimum 
				scoresArrayForHypervolume[i][j] += Parameters.parameters.doubleParameter("hypervolumeMinimumOffset"); // small boost to all scores
				assert scoresArrayForHypervolume[i][j] >= 0 : "scoresArrayForHypervolume["+i+"]["+j+"]= "+scoresArrayForHypervolume[i][j]+", minScoreForEachObjective="+Arrays.toString(minScoreForEachObjective);
			}
			i++;
		}
		
		Hypervolume hv = new Hypervolume();	//instantiates an instance of the class for access to the calculateHypervolume method
		return hv.calculateHypervolume(scoresArrayForHypervolume, numberOfPoints, numberOfObjectives);
		//doubleScores[numberOfIndividualScoresInList][numberOfObjectives]
	}
	
	/**
	 * Calculates the hypervolume of a List of Scores. Does not assume that it has
	 * been passed a pareto front. Creates a new pareto front first and then passes that list
	 * to calculate the hypervolume.
	 * Assumes the objectives are used for selection, and thus the min scores
	 * @param scores a list of scores to be evaluated 
	 * @return the hypervolume of the pareto front of the list of score
	 */
	public static <T> double hypervolumeFromPopulation(List<Score<T>> scores) {
		double[] minScoreForEachObjective = MMNEAT.task.minScores();
		return hypervolumeFromPopulation(scores, minScoreForEachObjective);
	}
	
	/**
	 * Like above, but the min scores can be specified in any way.
	 * @param scores scores the list of scores being evaluated (may not be Pareto front)
	 * @param minScoreForEachObjective min scores for each objective
	 * @return hypervolume of Pareto front
	 */
	public static <T> double hypervolumeFromPopulation(List<Score<T>> scores, double[] minScoreForEachObjective) {
		List<NSGA2Score<T>> paretoFront = NSGA2.getParetoFront(NSGA2.staticNSGA2Scores(scores)); //get pareto front
		List<Score<T>> scoresParetoList = new ArrayList<Score<T>>(scores.size());
		for (NSGA2Score<T> score : paretoFront) {		//turn NSGA2Score pareto front into List of Scores
			scoresParetoList.add(score);
		}
		return hypervolumeFromParetoFront(scoresParetoList, minScoreForEachObjective);
	}

}

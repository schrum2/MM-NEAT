package edu.southwestern.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import edu.southwestern.evolution.nsga2.NSGA2;
import edu.southwestern.evolution.nsga2.NSGA2Score;
import edu.southwestern.scores.Score;
import jmetal.qualityIndicator.Hypervolume;

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
	 * calles calculateHypervolume for hypervolume calculations
	 * Assumes all scores have the same number of objective scores.
	 * @param scores the list of scores for a pareto front being evaluated
	 * @return the hypervolume of the pareto front.
	 */
	public static <T> double hypervolumeFromParetoFront(List<Score<T>> scores) {
		int numberOfObjectives = scores.get(0).scores.length;
		int numberOfPoints = scores.size();
		double[][] scoresArrayForHypervolume = new double[numberOfPoints][numberOfObjectives];
		int i = 0;
		for (Score<T> individualScore : scores) {		//adds the scores to the double array of scores
			scoresArrayForHypervolume[i] = individualScore.scores;
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
	 * @param scores a list of scores to be evaluated 
	 * @return the hypervolume of the pareto front of the list of score
	 */
	public static <T> double hypervolumeFromPopulation(List<Score<T>> scores) {
		List<NSGA2Score<T>> paretoFront = NSGA2.getParetoFront(NSGA2.staticNSGA2Scores(scores)); //get pareto front
		List<Score<T>> scoresParetoList = new ArrayList<Score<T>>(scores.size());
		for (NSGA2Score<T> score : paretoFront) {		//turn NSGA2Score pareto front into List of Scores
			scoresParetoList.add(score);
		}
		return hypervolumeFromParetoFront(scoresParetoList);
	}

}

package edu.southwestern.util;

import java.util.List;

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
	 * 
	 * @param scores
	 * @return
	 */
	public static <T> double hypervolumeFromParetoFront(List<Score<T>> scores) {
		// use calculate hypervolume
		//numberOfObjectives = scores[i].scores.size()
		int numberOfObjectives = scores.get(0).scores.length;
		double[][] hypervolumeScoresArray = new double[scores.size()][numberOfObjectives];
		for (Score<T> individualScore : scores) {
			hypervolumeScoresArray[number of scores][number of objectives]
		}
		double result = Hypervolume; //how do I call the hypervolume function?
		//get a double[numberInPop][numberOfObjectives] then calculate
		return 0;
	}
	
//population may not be pareto front
	/**
	 * does not assume that the scores have been properly sorted into pareto fronts
	 * @param scores
	 * @return
	 */
	public static <T> double hypervolumeFromPopulation(List<Score<T>> scores) {
		//calculate pareto front NSGA2.paretofront
		//then convert to double[][]
		//calculate hypervolume
		return 0;
	}

}

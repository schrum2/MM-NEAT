package edu.southwestern.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.nsga2.NSGA2;
import edu.southwestern.evolution.nsga2.NSGA2Score;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.file.Serialization;
import jmetal.qualityIndicator.Hypervolume;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Contains methods for computing Pareto fronts. Based on jmetal.qualityIndicator.Hypervolume
 * hypervolume is the volume behind the pareto front
 * @author lewisj
 *
 */
public class MultiobjectiveUtil {

	// For sorting Pareto fronts before printing
	public static final Comparator<Score<?>> PARETO_SCORE_COMPARATOR = new Comparator<Score<?>>() {
		// Sort lexicographically: first score, then second score, etc
		@Override
		public int compare(Score<?> o1, Score<?> o2) {
			for(int i = 0; i < o1.scores.length; i++) {
				if(o1.scores[i] < o2.scores[i]) return -1;
				if(o1.scores[i] > o2.scores[i]) return 1;
			}
			return 0; // All were equal
		}

	};
	
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
		return hypervolumeAndParetoFrontFromPopulation(scores, minScoreForEachObjective).t1;
	}
	
	/**
	 * Like above, but also returns the Pareto front itself, since it is calculated along the way
	 * @param scores scores the list of scores being evaluated (may not be Pareto front)
	 * @param minScoreForEachObjective min scores for each objective
	 * @return Pair of hypervolume and Pareto front
	 */
	public static <T> Pair<Double,List<Score<T>>> hypervolumeAndParetoFrontFromPopulation(List<Score<T>> scores, double[] minScoreForEachObjective) {
		List<NSGA2Score<T>> paretoFront = NSGA2.getParetoFront(NSGA2.staticNSGA2Scores(scores)); //get pareto front
		List<Score<T>> scoresParetoList = new ArrayList<Score<T>>(scores.size());
		for (NSGA2Score<T> score : paretoFront) {		//turn NSGA2Score pareto front into List of Scores
			scoresParetoList.add(score);
		}
		return new Pair<>(hypervolumeFromParetoFront(scoresParetoList, minScoreForEachObjective),scoresParetoList);
	}

	
	/**
	 * Save all scores in Pareto front and a plot file for displaying them,
	 * along with an xml file of each member of the Pareto front.
	 * 
	 * @param PARETO_SCORE_COMPARATOR Comparator that makes sure the scores are sorted for display
	 * @param scoreFileName Name of text file that will hold the scores
	 * @param paretoFront List that must contain a Pareto front
	 * @param xmlSaveDirectory directory where xml files of each genotype are saved.
	 * 			if xmlSaveDirectory is null, it prevents saving of xml files
	 */
	public static <T> void logParetoFrontGenotypesAndScorePlot(String scoreFileName,
			List<Score<T>> paretoFront, String xmlSaveDirectory) {
		//this makes the directory folder for pareto fronts
		String directory = FileUtilities.getSaveDirectory();// retrieves file directory
		String saveDirectoryParetoFronts = directory + "/ParetoFronts";
		File directoryParetoFile = new File(saveDirectoryParetoFronts);
		if(!directoryParetoFile.exists()) {
			directoryParetoFile.mkdir();
		}
		System.out.println("pareto front directory name: "+saveDirectoryParetoFronts);

		//logging aggregate file
		File paretoFrontAggregateOutput = new File(saveDirectoryParetoFronts + "/"+scoreFileName+".txt");

		PrintStream ps;
		try {
			int numberOfObjectives = MMNEAT.task.numObjectives();
			ps = new PrintStream(paretoFrontAggregateOutput);

			///AGGREGATE LOGGING

			if(CommonConstants.netio && xmlSaveDirectory != null) {
				// Save global Pareto front at each logging event
				for(Score<T> candidate : paretoFront) {
					
					Serialization.save(candidate.individual, xmlSaveDirectory + "/" + scoreFileName + "-elite-"+Arrays.toString(candidate.scores));
				}
			}

			Collections.sort(paretoFront, PARETO_SCORE_COMPARATOR);
			//go through score for row
			//column is objectives
			for (Score<T> score : paretoFront) {
				String scoreString = "";
				for (int i = 0; i < numberOfObjectives; i++) {
					scoreString = scoreString + score.scores[i] + "\t";
				}
				ps.println(scoreString);
			}
			ps.close();
		} catch (FileNotFoundException e) {
			System.out.println("Logging of "+scoreFileName+".txt failed");
			e.printStackTrace();
		}
		System.out.println("about to make aggregate .plt");
		String logTitle = saveDirectoryParetoFronts+"/"+scoreFileName+".txt";
		System.out.println("logTitle: " + logTitle);
		String plotFilename = saveDirectoryParetoFronts+"/"+scoreFileName+".plt";
		System.out.println("plotFilename: " + plotFilename);


		File plotFile = new File(plotFilename);
		try {
			// Non-PDF version
			ps = new PrintStream(plotFile);
			ps.println("unset key");
			ps.println("set title \""+scoreFileName+" Pareto Front\"");
			ps.println("plot \"" + scoreFileName + ".txt" + "\" w linespoints t \"Pareto front\"");
			ps.close();

		} catch (FileNotFoundException e) {
			System.out.println("Error creating plt log file");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
}

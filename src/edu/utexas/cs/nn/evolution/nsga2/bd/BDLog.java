package edu.utexas.cs.nn.evolution.nsga2.bd;

import edu.utexas.cs.nn.log.StatisticsLog;
import java.util.ArrayList;

/**
 * Creates log file to keep track of BD statistics
 * @author Jacob Schrum
 * @commented Lauren Gillespie
 */
public class BDLog extends StatisticsLog<Double> {

	/**
	 * Returns a list of strings with label of string
	 * @return list of strings
	 */
    public static ArrayList<String> getLabels() {
        ArrayList<String> result = new ArrayList<String>(1);
        result.add("Behavioral Diversity");
        return result;
    }

    /**
     * Constructor for BDLog, uses inherited MONELog
     * @param prefix label for log files
     */
    public BDLog(String prefix) {
        super(prefix, getLabels());
    }
    
    /**
     * Logs scores from current generation
     * @param scores arrayList containing all scores from current generation
     * @param generation current generation
     */
    @Override
    public void log(ArrayList<Double> scores, int generation) {
        double[][] arrayScores = new double[scores.size()][1];
        for (int i = 0; i < arrayScores.length; i++) {
            arrayScores[i][0] = scores.get(i);
        }
        logAverages(arrayScores, generation);//also logs average scores from generation
    }
}

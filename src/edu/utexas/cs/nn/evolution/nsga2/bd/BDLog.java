package edu.utexas.cs.nn.evolution.nsga2.bd;

import edu.utexas.cs.nn.log.StatisticsLog;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class BDLog<T extends Double> extends StatisticsLog<T> {

    public static ArrayList<String> getLabels() {
        ArrayList<String> result = new ArrayList<String>(1);
        result.add("Behavioral Diversity");
        return result;
    }

    public BDLog(String prefix) {
        super(prefix, getLabels());
    }

    @Override
    public void log(ArrayList<T> scores, int generation) {
        double[][] arrayScores = new double[scores.size()][1];
        for (int i = 0; i < arrayScores.length; i++) {
            arrayScores[i][0] = scores.get(i);
        }
        logAverages(arrayScores, generation);
    }
}

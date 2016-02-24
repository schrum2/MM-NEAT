/*
 * Haven't decided how to fully apply this yet. At some point in selection,
 * the scores here need to replace the ones currently used, but keeping a
 * record of the actual rather than aggregate scores is still a good idea.
 */
package edu.utexas.cs.nn.evolution;

import edu.utexas.cs.nn.util.stats.Statistic;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Jacob Schrum
 */
public class ScoreHistory {

    private static HashMap<Long, ArrayList<double[]>> allScores = new HashMap<Long, ArrayList<double[]>>();
    private static HashMap<Long, Boolean> accessed = new HashMap<Long, Boolean>();

    public static void resetAccess() {
        accessed = new HashMap<Long, Boolean>();
        for (Long id : allScores.keySet()) {
            accessed.put(id, Boolean.FALSE);
        }
    }

    public static void add(long id, double[] scores) {
        if (!allScores.containsKey(id)) {
            allScores.put(id, new ArrayList<double[]>());
        }
        allScores.get(id).add(scores);
        accessed.put(id, Boolean.TRUE);
    }

    public static void remove(long id) {
        allScores.remove(id);
        accessed.remove(id);
    }

    private static double[] scoresInObjective(long id, int objective) {
        ArrayList<double[]> ss = allScores.get(id);
        double[] result = new double[ss.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = ss.get(i)[objective];
        }
        return result;
    }

    private static double applyStat(long id, int objective, Statistic s) {
        return s.stat(scoresInObjective(id, objective));
    }

    public static double[] applyStat(long id, Statistic s) {
        int numObjectives = allScores.get(id).get(0).length;
        double[] result = new double[numObjectives];
        for (int i = 0; i < numObjectives; i++) {
            result[i] = applyStat(id, i, s);
        }
        return result;
    }

    public static void clean() {
        for (Long id : allScores.keySet()) {
            if (!accessed.get(id)) {
                remove(id);
            }
        }
        resetAccess();
    }
}

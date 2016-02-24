/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.ensemble;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class AverageArbitrator extends MsPacManEnsembleArbitrator {

    /**
     * preferences[net][out]
     * 
     * @param game
     * @param preferences
     * @return 
     */
    public double[] newDirectionalPreferences(GameFacade game, double[][] preferences) {
        double[] averages = new double[preferences[0].length];
        for (int i = 0; i < preferences[0].length; i++) {
            averages[i] = StatisticsUtilities.average(ArrayUtil.column(preferences, i));
        }
        //System.out.println("\t\tAverages: " + Arrays.toString(averages));
        return averages;
    }
    
    public static void main(String[] args){
        double[] x1 = new double[]{0, 0.25, 0.5, 0.75};
        double[] x2 = new double[]{0.1, 0.0, 0.5, 1};
        double[][] combined = new double[][]{x1, x2};
        AverageArbitrator a = new AverageArbitrator();
        System.out.println(Arrays.toString(a.newDirectionalPreferences(null, combined)));
    }
}

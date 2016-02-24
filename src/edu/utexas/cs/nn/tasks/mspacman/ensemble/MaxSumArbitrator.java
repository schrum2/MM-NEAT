/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.ensemble;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class MaxSumArbitrator extends MsPacManEnsembleArbitrator {
    /**
     * @param preferences outputs of several networks and/or modes: preferences[mode][direction]
     **/
    public double[] newDirectionalPreferences(GameFacade game, double[][] preferences) {
        double[] sum = new double[preferences[0].length]; // num dirs
        for (int i = 0; i < preferences.length; i++) {  // for each mode
            for (int j = 0; j < preferences[i].length; j++) { // for each direction
                sum[j] += preferences[i][j];
            }
        }
        return sum;
    }
    
    public static void main(String[] args) {
        double[] x1 = new double[]{0, 0.25, 0.5, 0.75};
        double[] x2 = new double[]{0.1, 0.0, 0.5, 1};
        double[][] combined = new double[][]{x1, x2};
        MaxSumArbitrator a = new MaxSumArbitrator();
        System.out.println(Arrays.toString(a.newDirectionalPreferences(null, combined)));
    }
}

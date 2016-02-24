/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.util.stats;

/**
 *
 * @author Jacob Schrum
 */
public class Mode implements Statistic {

    public double stat(double[] xs) {
        return StatisticsUtilities.mode(xs);
    }
}

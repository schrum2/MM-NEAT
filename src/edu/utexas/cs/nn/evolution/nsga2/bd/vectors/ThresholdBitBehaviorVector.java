/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution.nsga2.bd.vectors;

import java.util.BitSet;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class ThresholdBitBehaviorVector extends BitBehaviorVector {

    public ThresholdBitBehaviorVector(ArrayList<Double> xs) {
        super(thresholdAll(xs));
    }

    public static BitSet thresholdAll(ArrayList<Double> xs) {
        BitSet bs = new BitSet(xs.size());
        for (int i = 0; i < xs.size(); i++) {
            bs.set(i, xs.get(i) > 0);
        }
        return bs;
    }
}

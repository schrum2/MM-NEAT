/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution.nsga2.bd.vectors;

import edu.utexas.cs.nn.util.CartesianGeometricUtilities;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class RealBehaviorVector implements BehaviorVector {

    private final ArrayList<Double> v;

    public RealBehaviorVector(ArrayList<Double> v) {
        this.v = v;
    }

    public RealBehaviorVector(int[] i) {
        this.v = new ArrayList<Double>(i.length);
        for (int j = 0; j < i.length; j++) {
            v.add(new Double(i[j]));
        }
    }

    public RealBehaviorVector(double[] d) {
        this.v = new ArrayList<Double>(d.length);
        for (int j = 0; j < d.length; j++) {
            v.add(new Double(d[j]));
        }
    }

    public double distance(BehaviorVector rhs) {
        ArrayList<Double> shorter = v;
        ArrayList<Double> longer = ((RealBehaviorVector) rhs).v;
        if (shorter.size() != longer.size()) {
            if (shorter.size() > longer.size()) {
                shorter = longer;
                longer = v;
            }

            for (int i = shorter.size(); i < longer.size(); i++) {
                shorter.add(0.0);
            }
        }
        return CartesianGeometricUtilities.euclideanDistance(shorter, longer);
    }
}

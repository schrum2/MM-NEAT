package edu.utexas.cs.nn.tasks.motests.testfunctions;

import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class ZDT3Function extends ZDTFunction {

    public ZDT3Function(boolean second) {
        super(second);
    }

    public double f2(ArrayList<Double> values) {
        double g = g(values);
        double x = values.get(0);
        double q1 = Math.sqrt(x / g);
        double q2 = (x / g) * Math.sin(10 * Math.PI * x);
        return g * (1 - q1 - q2);
    }
}

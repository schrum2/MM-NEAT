package edu.utexas.cs.nn.tasks.motests.testfunctions;

import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class ZDT2Function extends ZDTFunction {

    public ZDT2Function(boolean second) {
        super(second);
    }

    public double f2(ArrayList<Double> values) {
        double g = g(values);
        double q = values.get(0) / g;
        return g * (1 - (q * q));
    }
}

package edu.utexas.cs.nn.tasks.motests.testfunctions;

import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class ZDT6Function extends ZDTFunction {

    public ZDT6Function(boolean second) {
        super(second);
    }

    @Override
    public double f1(ArrayList<Double> values) {
        double x1 = values.get(0);
        return 1 - (Math.exp(-4 * x1) * Math.pow(Math.sin(6 * Math.PI * x1), 6));
    }

    public double f2(ArrayList<Double> values) {
        double g = g(values);
        double f = f1(values);
        return g * (1 - Math.pow(f / g, 2));
    }

    @Override
    protected Double g(ArrayList<Double> values) {
        double sum = 0.0;
        for (int i = 1; i < values.size(); i++) {
            sum += values.get(i);
        }
        return 1.0 + 9.0 * Math.pow(sum / (values.size() - 1.0), 0.25);
    }
}

package edu.utexas.cs.nn.tasks.motests.testfunctions;

import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class ZDT4Function extends ZDT1Function {

    public ZDT4Function(boolean second) {
        super(second);
    }

    @Override
    protected Double g(ArrayList<Double> values) {
        double sum = 0.0;
        for (int i = 1; i < values.size(); i++) {
            double x = values.get(i);
            sum += Math.pow(x, 2) - (10 * Math.cos(4 * Math.PI * x));
        }
        return 1.0 + (10 * (values.size() - 1.0)) + sum;
    }
}

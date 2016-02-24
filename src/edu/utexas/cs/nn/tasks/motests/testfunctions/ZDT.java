package edu.utexas.cs.nn.tasks.motests.testfunctions;

/**
 *
 * @author Jacob Schrum
 */
public abstract class ZDT implements FunctionOptimizationSet {

    protected int decisionVars;

    public ZDT(int decisionVars) {
        this.decisionVars = decisionVars;
    }

    public ZDT() {
        this(30);
    }

    public double[] getLowerBounds() {
        return new double[decisionVars];
    }

    public double[] getUpperBounds() {
        double[] result = new double[decisionVars];
        for (int i = 0; i < result.length; i++) {
            result[i] = 1;
        }
        return result;
    }

    public double[] frontDecisionValuesBoundsOfFirst() {
        return new double[]{0, 1};
    }

    public double[] frontDecisionValuesInTermsOfFirst(double x1) {
        double[] bounds = new double[decisionVars];
        bounds[0] = x1;
        return bounds;
    }
}

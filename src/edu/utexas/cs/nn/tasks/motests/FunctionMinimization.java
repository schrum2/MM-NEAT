package edu.utexas.cs.nn.tasks.motests;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.tasks.motests.testfunctions.FunctionOptimizationSet;

/**
 *
 * @author Jacob Schrum
 */
public class FunctionMinimization extends FunctionOptimization {

    public FunctionMinimization() {
        this(MMNEAT.fos);
    }

    public FunctionMinimization(FunctionOptimizationSet fos) {
        super(fos, -1);
    }
}

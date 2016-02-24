package edu.utexas.cs.nn.tasks.motests;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.tasks.motests.testfunctions.FunctionOptimizationSet;

/**
 *
 * @author Jacob Schrum
 */
public class FunctionMaximization extends FunctionOptimization {

    public FunctionMaximization() {
        this(MMNEAT.fos);
    }

    public FunctionMaximization(FunctionOptimizationSet fos) {
        super(fos, 1);
    }
}

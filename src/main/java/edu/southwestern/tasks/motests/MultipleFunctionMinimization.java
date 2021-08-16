package edu.southwestern.tasks.motests;

import edu.southwestern.tasks.motests.testfunctions.FunctionOptimizationSet;

/**
 *
 * @author Jacob Schrum
 */
public class MultipleFunctionMinimization extends MultipleFunctionOptimization {

	public MultipleFunctionMinimization() {
		this(MultipleFunctionOptimization.setupFunctionOptimization());
	}

	public MultipleFunctionMinimization(FunctionOptimizationSet fos) {
		super(fos, -1);
	}
}

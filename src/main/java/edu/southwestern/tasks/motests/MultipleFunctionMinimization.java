package edu.southwestern.tasks.motests;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.tasks.motests.testfunctions.FunctionOptimizationSet;

/**
 *
 * @author Jacob Schrum
 */
public class MultipleFunctionMinimization extends MultipleFunctionOptimization {

	public MultipleFunctionMinimization() {
		this(MMNEAT.fos);
	}

	public MultipleFunctionMinimization(FunctionOptimizationSet fos) {
		super(fos, -1);
	}
}

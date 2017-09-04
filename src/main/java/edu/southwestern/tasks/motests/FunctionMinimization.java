package edu.southwestern.tasks.motests;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.tasks.motests.testfunctions.FunctionOptimizationSet;

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

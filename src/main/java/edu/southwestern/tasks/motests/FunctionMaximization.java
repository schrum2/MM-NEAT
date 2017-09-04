package edu.southwestern.tasks.motests;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.tasks.motests.testfunctions.FunctionOptimizationSet;

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

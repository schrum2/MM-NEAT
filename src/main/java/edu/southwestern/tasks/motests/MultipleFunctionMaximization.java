package edu.southwestern.tasks.motests;

import java.io.FileNotFoundException;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.tasks.motests.testfunctions.FunctionOptimizationSet;

/**
 *
 * @author Jacob Schrum
 */
public class MultipleFunctionMaximization extends MultipleFunctionOptimization {

	public MultipleFunctionMaximization() {
		this(MMNEAT.fos);
	}

	public MultipleFunctionMaximization(FunctionOptimizationSet fos) {
		super(fos, 1);
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main(new String[] {"runNumber:0", "io:false", "netio:false", "watch:true", "task:edu.southwestern.tasks.motests.MultipleFunctionMaximization",
				"fos:edu.southwestern.tasks.motests.testfunctions.POL", "genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype"});
	}
}

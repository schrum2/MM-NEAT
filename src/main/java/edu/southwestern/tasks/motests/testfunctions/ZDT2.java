package edu.southwestern.tasks.motests.testfunctions;

import java.util.ArrayList;

import edu.southwestern.evolution.fitness.FitnessFunction;

/**
 *
 * @author Jacob Schrum
 */
public class ZDT2 extends ZDT {

	@SuppressWarnings("unchecked")
	public FitnessFunction<ArrayList<Double>>[] getFitnessFunctions() {
		return new FitnessFunction[] { new ZDT2Function(false), new ZDT2Function(true) };
	}
}

package edu.southwestern.tasks.motests.testfunctions;

import java.util.ArrayList;

import edu.southwestern.evolution.fitness.FitnessFunction;

/**
 *
 * @author Jacob Schrum
 */
public class ZDT3 extends ZDT {

	@SuppressWarnings("unchecked")
	public FitnessFunction<ArrayList<Double>>[] getFitnessFunctions() {
		return new FitnessFunction[] { new ZDT3Function(false), new ZDT3Function(true) };
	}
}

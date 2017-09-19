package edu.southwestern.tasks.motests.testfunctions;

import java.util.ArrayList;

import edu.southwestern.evolution.fitness.FitnessFunction;

/**
 *
 * @author Jacob Schrum
 */
public class ZDT6 extends ZDT {

	public ZDT6() {
		super(10);
	}

	@SuppressWarnings("unchecked")
	public FitnessFunction<ArrayList<Double>>[] getFitnessFunctions() {
		return new FitnessFunction[] { new ZDT6Function(false), new ZDT6Function(true) };
	}
}

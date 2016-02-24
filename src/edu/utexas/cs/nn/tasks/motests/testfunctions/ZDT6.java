package edu.utexas.cs.nn.tasks.motests.testfunctions;

import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;

/**
 *
 * @author Jacob Schrum
 */
public class ZDT6 extends ZDT {

    public ZDT6() {
        super(10);
    }

    public FitnessFunction[] getFitnessFunctions() {
        return new FitnessFunction[]{new ZDT6Function(false), new ZDT6Function(true)};
    }
}

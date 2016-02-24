package edu.utexas.cs.nn.tasks.motests.testfunctions;

import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;

/**
 *
 * @author Jacob Schrum
 */
public class ZDT3 extends ZDT {

    public FitnessFunction[] getFitnessFunctions() {
        return new FitnessFunction[]{new ZDT3Function(false), new ZDT3Function(true)};
    }
}

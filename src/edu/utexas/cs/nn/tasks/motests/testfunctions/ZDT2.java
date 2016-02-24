package edu.utexas.cs.nn.tasks.motests.testfunctions;

import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;

/**
 *
 * @author Jacob Schrum
 */
public class ZDT2 extends ZDT {

    public FitnessFunction[] getFitnessFunctions() {
        return new FitnessFunction[]{new ZDT2Function(false), new ZDT2Function(true)};
    }
}

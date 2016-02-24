package edu.utexas.cs.nn.tasks.motests.testfunctions;

import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;

/**
 *
 * @author Jacob Schrum
 */
public class ZDT1 extends ZDT {

    public FitnessFunction[] getFitnessFunctions() {
        return new FitnessFunction[]{new ZDT1Function(false), new ZDT1Function(true)};
    }
}

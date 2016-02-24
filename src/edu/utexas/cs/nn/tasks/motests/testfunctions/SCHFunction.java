package edu.utexas.cs.nn.tasks.motests.testfunctions;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class SCHFunction implements FitnessFunction<ArrayList<Double>> {

    private boolean second;

    public SCHFunction(boolean second) {
        this.second = second;
    }

    public double fitness(Organism<ArrayList<Double>> individual) {
        ArrayList<Double> values = individual.getGenotype().getPhenotype();
        double x = values.get(0);
        return second ? f2(x) : f1(x);
    }

    public double f1(double x) {
        return x * x;
    }

    public double f2(double x) {
        return (x - 2) * (x - 2);
    }
}

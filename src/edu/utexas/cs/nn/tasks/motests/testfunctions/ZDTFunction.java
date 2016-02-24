package edu.utexas.cs.nn.tasks.motests.testfunctions;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public abstract class ZDTFunction implements FitnessFunction<ArrayList<Double>> {

    private boolean second;

    public ZDTFunction(boolean second) {
        this.second = second;
    }

    public double fitness(Organism<ArrayList<Double>> individual) {
        ArrayList<Double> values = individual.getGenotype().getPhenotype();
        return second ? f2(values) : f1(values);
    }

    public double f1(ArrayList<Double> values) {
        return values.get(0);
    }

    public abstract double f2(ArrayList<Double> values);

    protected Double g(ArrayList<Double> values) {
        double sum = 0.0;
        for (int i = 1; i < values.size(); i++) {
            sum += values.get(i);
        }
        return 1.0 + 9.0 * (sum / (values.size() - 1.0));
    }
}

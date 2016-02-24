package edu.utexas.cs.nn.tasks.motests.testfunctions;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class KURFunction implements FitnessFunction<ArrayList<Double>> {

    private boolean second;

    public KURFunction(boolean second) {
        this.second = second;
    }

    public double fitness(Organism<ArrayList<Double>> individual) {
        ArrayList<Double> values = individual.getGenotype().getPhenotype();
        return second ? f2(values) : f1(values);
    }

    public double f1(ArrayList<Double> values) {
        double sum = 0;
        for (int i = 0; i < values.size() - 1; i++) {
            double xi = values.get(i);
            double xi1 = values.get(i + 1);
            sum += -10 * Math.exp(-0.2 * Math.sqrt(xi * xi + xi1 * xi1));
        }
        return sum;
    }

    public double f2(ArrayList<Double> values) {
        double sum = 0;
        for (int i = 0; i < values.size(); i++) {
            double xi = values.get(i);
            sum += Math.pow(Math.abs(xi), 0.8) + 5 * Math.sin(Math.pow(xi, 3));
        }
        return sum;
    }
}

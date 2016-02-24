package edu.utexas.cs.nn.tasks.motests.testfunctions;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class POLFunction implements FitnessFunction<ArrayList<Double>> {

    private boolean second;

    public POLFunction(boolean second) {
        this.second = second;
    }

    public double fitness(Organism<ArrayList<Double>> individual) {
        ArrayList<Double> values = individual.getGenotype().getPhenotype();
        double x1 = values.get(0);
        double x2 = values.get(1);
        return second ? f2(x1, x2) : f1(x1, x2);
    }

    public double f1(double x1, double x2) {
        double A1 = 0.5 * Math.sin(1) - 2 * Math.cos(1) + Math.sin(2) - 1.5 * Math.cos(2);
        double A2 = 1.5 * Math.sin(1) - Math.cos(1) + 2 * Math.sin(2) - 0.5 * Math.cos(2);
        double B1 = 0.5 * Math.sin(x1) - 2 * Math.cos(x1) + Math.sin(x2) - 1.5 * Math.cos(x2);
        double B2 = 1.5 * Math.sin(x1) - Math.cos(x1) + 2 * Math.sin(x2) - 0.5 * Math.cos(x2);

        return 1 + Math.pow(A1 - B1, 2) + Math.pow(A2 - B2, 2);
    }

    public double f2(double x1, double x2) {
        return Math.pow(x1 + 3, 2) + Math.pow(x2 + 1, 2);
    }
}

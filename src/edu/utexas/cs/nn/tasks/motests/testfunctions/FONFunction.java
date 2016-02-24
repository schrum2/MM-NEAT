package edu.utexas.cs.nn.tasks.motests.testfunctions;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.fitness.FitnessFunction;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class FONFunction implements FitnessFunction<ArrayList<Double>> {

    private double sign;

    public FONFunction(double sign) {
        this.sign = Math.signum(sign);
    }

    public double f(ArrayList<Double> x) {
        double sum = 0;
        for (int i = 0; i < x.size(); i++) {
            sum += Math.pow(x.get(i) + sign * (1.0 / Math.sqrt(3.0)), 2.0);
        }
        return (1.0 - Math.exp(-sum));
    }

    public double fitness(Organism<ArrayList<Double>> individual) {
        ArrayList<Double> values = individual.getGenotype().getPhenotype();
        return f(values);
    }
}

package edu.utexas.cs.nn.tasks.mspacman;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 * One pacman eval consists of two separate evals:
 *
 * @author Jacob Schrum
 * @param <T>
 */
public abstract class MsPacManIsolatedMultitask<T extends Network> extends MsPacManTask<T> {
    
        /**
         * Code to execute before task 1
         */
	public void task1Pre() {
	}

        /**
         * Code to execute after task 1
         * @param task1Results  evaluation results of agent
         */
	public void task1Post(Pair<double[], double[]> task1Results) {

	}

        /**
         * Code to execute before task 2
         */
	public void task2Pre() {

	}

        /**
         * Code to execute after task 2
         * @param task2Results  evaluation results of agent
         */
	public void task2Post(Pair<double[], double[]> task2Results) {

	}

        /**
         * Prepares for each individual task and combines results afterward
         * @param individual genotype to evaluate
         * @param num evaluation number
         * @return combined evaluation results
         */
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		task1Pre();
		Pair<double[], double[]> task1Results = super.oneEval(individual, num);
		task1Post(task1Results);

		task2Pre();
		Pair<double[], double[]> task2Results = super.oneEval(individual, num);
		task2Post(task2Results);

		double[] combinedScores = new double[task1Results.t1.length];
		for (int i = 0; i < combinedScores.length; i++) {
			combinedScores[i] = task1Results.t1[i] + task2Results.t1[i];
		}
		double[] combinedOthers = new double[task1Results.t2.length];
		for (int i = 0; i < combinedOthers.length; i++) {
			combinedOthers[i] = task1Results.t2[i] + task2Results.t2[i];
		}

		Pair<double[], double[]> combo = new Pair<double[], double[]>(combinedScores, combinedOthers);
		return combo;
	}
}

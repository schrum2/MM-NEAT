package edu.utexas.cs.nn.tasks.mspacman;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.nsga2.tug.TUGTask;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.CooperativeTask;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import java.util.ArrayList;

/**
 * Variant of the pacman task that takes several networks and combines them into
 * one organism. This is very similar to having a single organism that is a
 * hierarchical network, but since each component network needs to come from a
 * separate subpopulation that is evolving, fitness information needs to be
 * propagated back to each network, which requires this different setup and
 * inheritance pattern.
 *
 * @author Jacob Schrum
 * @param <T> phenotype
 */
public abstract class CooperativeMsPacManTask<T extends Network> extends CooperativeTask implements NetworkTask, SinglePopulationTask<T>, TUGTask {

	public MsPacManTask<T> task;

	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		throw new UnsupportedOperationException(
                                  "This method should not actually be called when using coevolution. "
				+ "It is merely here to satisfy type requirements and allow the code " 
                                + "to compile");
	}

	public double[] startingGoals() {
		return task.startingGoals();
	}

	public CooperativeMsPacManTask() {
		task = new MsPacManTask<T>();
	}

	public int numObjectives() {
		return task.numObjectives();
	}

	public double getTimeStamp() {
		return task.getTimeStamp();
	}

	public String[] sensorLabels() {
		return task.sensorLabels();
	}

	public String[] outputLabels() {
		return task.outputLabels();
	}

	public abstract int numberOfPopulations();

	public void finalCleanup() {
	}
}

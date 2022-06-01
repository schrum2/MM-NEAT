package edu.southwestern.tasks.evocraft;

import java.util.HashMap;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.util.datastructures.Pair;

/**
 * MAPElites only works with LonerTasks because it is a steady-state algorithm.
 * MinecraftShapeTask is parallelized for evolving populations, but this
 * version of the task cannot take advantage of that (though there may be a work-around
 * using multiple emitters). However, some of the code from MinecraftShapeTask is
 * re-used.
 * 
 * @author schrum2
 *
 * @param <T>
 */
public class MinecraftLonerShapeTask<T> extends NoisyLonerTask<T> implements NetworkTask {

	private MinecraftShapeTask<T> internalMinecraftShapeTask;
	
	public MinecraftLonerShapeTask() {
		internalMinecraftShapeTask = new MinecraftShapeTask<T>();
	}
	
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num, HashMap<String, Object> behaviorCharacteristics) {
		
	}
	
	@Override
	public int numObjectives() {
		return internalMinecraftShapeTask.numObjectives();
	}

	@Override
	public double getTimeStamp() {
		return 0;
	}

	@Override
	public void postConstructionInitialization() {
		internalMinecraftShapeTask.postConstructionInitialization();
	}

	@Override
	public String[] sensorLabels() {
		return internalMinecraftShapeTask.sensorLabels();
	}

	@Override
	public String[] outputLabels() {
		return internalMinecraftShapeTask.outputLabels();
	}

    @Override
	public void finalCleanup() {
    	internalMinecraftShapeTask.finalCleanup();
	}
}

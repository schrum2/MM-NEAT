package edu.southwestern.tasks.molecules;

import java.util.HashMap;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.util.datastructures.Pair;

public class MoleculeTask<T> extends NoisyLonerTask<T> {

	public MoleculeTask() {
		// TODO: Init objective
	}
	
	@Override
	public void postConstructionInitialization() {
		// Nothing
	}
	
	@Override
	public int numObjectives() {
		return 1;
	}

	@Override
	public double getTimeStamp() {
		// Not used
		return 0;
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num, HashMap<String,Object> behaviorCharacteristics) {
		return null; // TODO
	}
	
	
}

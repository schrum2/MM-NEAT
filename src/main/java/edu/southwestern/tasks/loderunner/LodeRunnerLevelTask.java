package edu.southwestern.tasks.loderunner;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.util.datastructures.Pair;

public abstract class LodeRunnerLevelTask<T> extends NoisyLonerTask<T> {
	
	public LodeRunnerLevelTask() {
		
	}

	@Override
	public int numObjectives() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	/**
	 * Different level generators use the genotype to generate a level in different ways
	 * @param individual Genotype 
	 * @return List of lists of integers corresponding to tile types
	 */
	public abstract List<List<Integer>> getLodeRunnerLevelListRepresentationFromGenotype(Genotype<T> individual);

	@Override
	public double getTimeStamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		// TODO Auto-generated method stub
		
		return null;
	}

}

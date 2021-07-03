package edu.southwestern.tasks.export;

import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.util.datastructures.Pair;

/**
 * Any task where a json representation of a level can be evaluated.
 * 
 * @author Jacob Schrum
 *
 */
public interface JsonLevelGenerationTask<T> {

	/**
	 * Evaluates one level, fills the behaviorMap with behavior characteristic
	 * information, and returns a pair of fitness scores and other scores.
	 * 
	 * @param level Level to evaluate as list of lists of integers (each int is a tile and each sub-list is a row in a 2D level)
	 * @param psuedoRandomSeed Some tasks need a random seed for placement of certain additional objects in the level
	 * @param individual Genotype that produced the level. Might be a dummy genotype
	 * @param behaviorMap Map that accumulates behavior characteristic information from the evaluation.
	 * @return Pairing of fitness and other scores (could be ignored if behaviorMap has needed information instead)
	 */
	public Pair<double[], double[]> evaluateOneLevel(List<List<Integer>> level, double psuedoRandomSeed, Genotype<T> individual, HashMap<String,Object> behaviorMap);
}

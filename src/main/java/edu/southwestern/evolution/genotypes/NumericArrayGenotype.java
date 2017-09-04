package edu.southwestern.evolution.genotypes;

import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.crossover.ArrayCrossover;

import java.util.ArrayList;

/**
 * Genotype can represent a string of values of an arbitrary type. The name
 * indicates that the types should be numeric, though the code does not actually
 * impose any kind of restriction that would force the individual genes to be
 * numbers.
 *
 * @author Jacob Schrum
 */
public abstract class NumericArrayGenotype<T> implements Genotype<ArrayList<T>> {

	ArrayList<T> genes;
	private long id = EvolutionaryHistory.nextGenotypeId();

	@SuppressWarnings("unchecked")
	public NumericArrayGenotype(ArrayList<T> genes) {
		this.genes = (ArrayList<T>) genes.clone();
	}

	public NumericArrayGenotype(T[] genes) {
		this.genes = new ArrayList<T>(genes.length);
		for (int i = 0; i < genes.length; i++) {
			this.genes.add(genes[i]);
		}
	}

	public void setValue(int pos, T value) {
		genes.set(pos, value);
	}

	public Genotype<ArrayList<T>> crossover(Genotype<ArrayList<T>> g) {
		return new ArrayCrossover<T>().crossover(this, g);
	}

	public ArrayList<T> getPhenotype() {
		return genes;
	}

	@Override
	public String toString() {
		return getId() + ":" + genes.toString();
	}

	public long getId() {
		return id;
	}
	
	public ArrayList<T> getGenes() {
		return genes;
	}
}

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

	@SuppressWarnings("rawtypes")
	private static final ArrayCrossover ARRAY_CROSSOVER = new ArrayCrossover();
	
	ArrayList<T> genes;
	private long id = EvolutionaryHistory.nextGenotypeId();
	
	/**
	 * Creates an evolvable genotype that is an arraylist of numbers
	 * @param genes An arraylist of numbers to initialize the genotype
	 */
	@SuppressWarnings("unchecked")
	public NumericArrayGenotype(ArrayList<T> genes) {
		this.genes = (ArrayList<T>) genes.clone();
	}

	/**
	 * Creates an evolvable genotype that is an arraylist of numbers
	 * @param genes An array of numbers to initialize the genotype
	 */
	public NumericArrayGenotype(T[] genes) {
		this.genes = new ArrayList<T>(genes.length);
		for (int i = 0; i < genes.length; i++) {
			this.genes.add(genes[i]);
		}
	}

	/**
	 * Sets the value of a specific gene in the genotype
	 * @param pos An integer specifying the gene's location
	 * @param value A number to set the gene's value to
	 */
	public void setValue(int pos, T value) {
		genes.set(pos, value);
	}

	/**
	 * Crossover this genotype with another
	 * @param g Another genotype to crossover with
	 * @return The resulting genotype post-crossover
	 */
	@SuppressWarnings("unchecked")
	public Genotype<ArrayList<T>> crossover(Genotype<ArrayList<T>> g) {
		return ARRAY_CROSSOVER.crossover(this, g);
	}

	/**
	 * Returns an arraylist of the gene values
	 */
	public ArrayList<T> getPhenotype() {
		return genes;
	}
	
	/**
	 * Returns the genotype id and genes as a string
	 */
	@Override
	public String toString() {
		return getId() + ":" + genes.toString();
	}

	/**
	 * Returns the genotype id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Returns an arraylist of the genes
	 */
	public ArrayList<T> getGenes() {
		return genes;
	}
}

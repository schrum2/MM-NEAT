package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mutation.real.PerturbMutation;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Genotype for evolving real-valued vectors.
 *
 * @author Jacob Schrum
 */
public class RealValuedGenotype extends NumericArrayGenotype<Double> {

	public RealValuedGenotype() {
		// Not using bounds themselves, but using length of bounds array to know how many variables are in solution vector
		this(MMNEAT.getLowerBounds().length);
	}
	
	/**
	 * New genotype derived from list of doubles
	 * @param genes ArrayList of doubles
	 */
	public RealValuedGenotype(ArrayList<Double> genes) {
		super(genes);
	}
	
	/**
	 * Constructs a RealValuedGenotype from an input double array
	 * 
	 * @param genes representative double array of genes
	 */
	public RealValuedGenotype(double[] genes) {
		super(ArrayUtils.toObject(genes));
	}
	
	/**
	 * Constructs an array of random values based on an input size
	 * 
	 * @param size desired size of array
	 */
	public RealValuedGenotype(int size) {
		this(RandomNumbers.randomArray(size));
	}

	/**
	 * Uses constructor to create an ArrayList<Double> of the 
	 * genes
	 */
	public Genotype<ArrayList<Double>> copy() {
		return new RealValuedGenotype(genes);
	}
	
	/**
	 * Creates new genotype of the same size as the original
	 * array of genes
	 */
	public Genotype<ArrayList<Double>> newInstance() {
		return new RealValuedGenotype(genes.size());
	}
	
	/**
	 * Mutates genotype through perturbation
	 */
	public void mutate() {
		new PerturbMutation(genes.size()).mutate(this);
	}
	
	// Stores parent IDs for tacking lineage. Not serialized.
	transient List<Long> parents = new LinkedList<Long>();
	
	/**
	 * Indicate one of the parents.
	 */
	@Override
	public void addParent(long id) {
		parents.add(id);
	}

	/**
	 * Get parent IDs
	 */
	@Override
	public List<Long> getParentIDs() {
		return parents;
	}

}

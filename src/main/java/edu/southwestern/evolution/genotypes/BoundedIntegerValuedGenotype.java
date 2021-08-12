package edu.southwestern.evolution.genotypes;

import edu.southwestern.evolution.mutation.integer.ReplaceMutation;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.util.random.RandomNumbers;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A basic genotype containing an array of integers.
 *
 * @author Jacob Schrum
 */
public class BoundedIntegerValuedGenotype extends NumericArrayGenotype<Integer> {

	/**
	 * Makes a new array with random integers bounded by discreteCeilings.
	 */
	public BoundedIntegerValuedGenotype() {
		super(RandomNumbers.randomIntArray(MMNEAT.discreteCeilings));
	}

	/**
	 * Creates evolvable genotype that is a list of Integers.
	 * @param genes ArrayList of Integers to initialize genotype.
	 */
	public BoundedIntegerValuedGenotype(ArrayList<Integer> genes) {
		super(genes);
	}

	/**
	 * Creates a copy of the whole genotype that
	 * contains a copy of the arrayList
	 * 
	 * @return returns the new genotype
	 */
	public Genotype<ArrayList<Integer>> copy() {
		return new BoundedIntegerValuedGenotype(genes);
	}

	/**
	 * Sets a value in the arrayList genes.
	 *  
	 * @param pos the position to set the new value at
	 * @param value the new value
	 */
	public void setValue(int pos, int value) {
		genes.set(pos, value);
	}

	/**
	 * Creates a new genotype instance.
	 * 
	 * @return returns the new genotype
	 */
	public Genotype<ArrayList<Integer>> newInstance() {
		return new BoundedIntegerValuedGenotype();
	}

	/**
	 * Mutates the arrayList genes.
	 */
	public void mutate() {
		new ReplaceMutation().mutate(this);
	}

	transient List<Long> parents = new LinkedList<Long>();
	
	/**
	 * Adds a parent.
	 * 
	 * @param id number of parent genotype
	 */
	@Override
	public void addParent(long id) {
		parents.add(id);
	}

	/**
	 * Gets parent IDs.
	 */
	@Override
	public List<Long> getParentIDs() {
		return parents;
	}
}

package edu.utexas.cs.nn.evolution.genotypes;

import edu.utexas.cs.nn.evolution.mutation.real.PerturbMutation;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jacob Schrum
 */
public class RealValuedGenotype extends NumericArrayGenotype<Double> {

	public RealValuedGenotype(ArrayList<Double> genes) {
		super(genes);
	}
	
	/**
	 * Constructs a RealValuedGenotype from an input double array
	 * 
	 * @param genes representative double array of genes
	 */
	public RealValuedGenotype(double[] genes) {
		super(ArrayUtil.primitiveDoubleArrayToDoubleArray(genes));
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
	 * Mutates genotype
	 */
	public void mutate() {
		new PerturbMutation(genes.size()).mutate(this);
	}
	
	transient List<Long> parents = new LinkedList<Long>();
	
	@Override
	public void addParent(long id) {
		parents.add(id);
	}

	@Override
	public List<Long> getParentIDs() {
		return parents;
	}

}

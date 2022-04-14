package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.mutation.real.PerturbMutation;
import edu.southwestern.evolution.mutation.real.SegmentCopyMutation;
import edu.southwestern.evolution.mutation.real.SegmentSwapMutation;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.BoundedTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Genotype for evolving real-valued vectors.
 *
 * @author Jacob Schrum
 */
public class RealValuedGenotype extends NumericArrayGenotype<Double> {

	public RealValuedGenotype() {
		// Not using bounds themselves, but using length of bounds array to know how many variables are in solution vector
		this(((BoundedTask) MMNEAT.task).getLowerBounds().length);
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
		if (RandomNumbers.randomGenerator.nextDouble() <= Parameters.parameters.doubleParameter("anyRealVectorModificationRate")) {
			new PerturbMutation(genes.size()).mutate(this);
		}
		genotypeMutations();
		
	}
	
	/**
	 * Executes mutations meant to be directly applied to the genotype
	 */
	protected void genotypeMutations() {
		// Should probably be logging the mutations above too, but will worry about that later
		StringBuilder sb = new StringBuilder();
		sb.append(this.getId()+" ");
		if(GANProcess.type != null) { // Only use these mutations if using a GAN
			new SegmentSwapMutation().go(this, sb);
			new SegmentCopyMutation().go(this, sb);
		}
		EvolutionaryHistory.logMutationData(sb.toString());
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

package edu.utexas.cs.nn.evolution.genotypes;

import edu.utexas.cs.nn.evolution.mutation.integer.ReplaceMutation;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class BoundedIntegerValuedGenotype extends NumericArrayGenotype<Integer> {

	public BoundedIntegerValuedGenotype(int size) {
		super(RandomNumbers.randomIntArray(size, MMNEAT.discreteCeilings));
	}

	public BoundedIntegerValuedGenotype(ArrayList<Integer> genes) {
		super(genes);
	}

	public Genotype<ArrayList<Integer>> copy() {
		return new BoundedIntegerValuedGenotype(genes);
	}

	public void setValue(int pos, int value) {
		genes.set(pos, value);
	}

	public Genotype<ArrayList<Integer>> newInstance() {
		return new BoundedIntegerValuedGenotype(genes.size());
	}

	public void mutate() {
		new ReplaceMutation().mutate(this);
	}
}

package edu.southwestern.evolution.genotypes;

import edu.southwestern.evolution.mutation.integer.ReplaceMutation;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.util.random.RandomNumbers;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jacob Schrum
 */
public class BoundedIntegerValuedGenotype extends NumericArrayGenotype<Integer> {

	public BoundedIntegerValuedGenotype() {
		super(RandomNumbers.randomIntArray(MMNEAT.discreteCeilings));
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
		return new BoundedIntegerValuedGenotype();
	}

	public void mutate() {
		new ReplaceMutation().mutate(this);
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

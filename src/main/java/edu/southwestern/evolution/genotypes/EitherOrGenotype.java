package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Can be one of two types of Genotype, though the population
 * must start as the first of the two types. There is an allowance
 * for Genotypes to transition to the second form, though the details
 * of that transition are not handled by this class.
 * 
 * This class tolerates some type warnings in order to be more flexible.
 * 
 * @author Jacob Schrum
 *
 * @param <X> First Genotype form
 * @param <Y> Second Genotype form
 */
@SuppressWarnings("rawtypes")
public class EitherOrGenotype<X,Y> implements Genotype {
	// Have to store a copy of the first genotype used for newInstance method to work
	private static Genotype original = null;

	private ArrayList<Long> parentIds;
	protected Genotype current; // Could be X or Y
	protected boolean firstForm;
	
	/**
	 * New genotype that has one of two types.
	 * 
	 * @param genotype The genotype for this specific instance. Should be of type X or Y
	 * @param firstForm Whether the type is X (true) or Y (false)
	 */
	public EitherOrGenotype(Genotype genotype, boolean firstForm) {
		current = genotype;
		this.firstForm = firstForm;
		if(original == null) {
			if(!firstForm) throw new IllegalArgumentException("First EitherOrGenotype made must have first form");
			original = current.copy();
		}
	}

	@Override
	public void addParent(long id) {
		parentIds.add(id);
	}

	@Override
	public List<Long> getParentIDs() {
		return parentIds;
	}

	@Override
	public Genotype copy() {
		return new EitherOrGenotype<X,Y>(current.copy(), this.firstForm);
	}

	@Override
	public void mutate() {
		current.mutate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Genotype crossover(Genotype g) {
		EitherOrGenotype<X,Y> other = (EitherOrGenotype<X,Y>) g;
		// If both genotypes are at the same stage/are of the same type
		if(firstForm == other.firstForm) {
			// Do crossover
			return current.crossover(other.current);
		} else {
			// Otherwise, just return other genotype without performing crossover
			return other;
		}
	}

	@Override
	public Object getPhenotype() {
		return current.getPhenotype();
	}

	@Override
	public Genotype newInstance() {
		return original.newInstance();
	}

	@Override
	public long getId() {
		return current.getId();
	}
	public void switchForms(Genotype g) {
	    current = g;
	    firstForm = false;
	}
}

package edu.southwestern.evolution.genotypes;

import java.util.LinkedList;
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
public abstract class EitherOrGenotype<X,Y> implements Genotype {
	protected LinkedList<Long> parentIds = new LinkedList<Long>();
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
	public void mutate() {
		current.mutate();
	}

	@Override
	public Object getPhenotype() {
		return current.getPhenotype();
	}

	@Override
	public long getId() {
		return current.getId();
	}

	/**
	 * Causes a transition to the second form
	 * @param g Genotype that should be of the second form
	 */
	public void switchForms(Genotype<Y> g) {
	    current = g;
	    firstForm = false;
	}
	
	public boolean getFirstForm() {
		return firstForm;
	}
	
	public Genotype getCurrentGenotype() { 
		return current;
	}
}

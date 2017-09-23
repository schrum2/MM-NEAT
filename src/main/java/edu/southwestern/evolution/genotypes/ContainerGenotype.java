package edu.southwestern.evolution.genotypes;

import java.util.List;

/**
 * Not really a genotype: Just a way of wrapping an object
 * to pass into a task that expects a genotype. Primarily intended
 * for reinforcement learning, not evolution.
 * @author Jacob Schrum
 */
public class ContainerGenotype<T> implements Genotype<T> {

	private T phenotype;

	public ContainerGenotype(T phenotype) {
		this.phenotype = phenotype;
	}
	
	@Override
	public void addParent(long id) {
		throw new UnsupportedOperationException("Should not have actual parents");
	}

	@Override
	public List<Long> getParentIDs() {
		throw new UnsupportedOperationException("Should not have actual parents");
	}

	@Override
	public Genotype<T> copy() {
		return new ContainerGenotype<>(phenotype);
	}

	@Override
	public void mutate() {
		throw new UnsupportedOperationException("Should not mutate such genotypes");
	}

	@Override
	public Genotype<T> crossover(Genotype<T> g) {
		throw new UnsupportedOperationException("Should not crossover such genotypes");
	}

	@Override
	public T getPhenotype() {
		return phenotype;
	}

	@Override
	public Genotype<T> newInstance() {
		return copy();
	}

	@Override
	public long getId() {
		return 0; // Should only be one
	}

}

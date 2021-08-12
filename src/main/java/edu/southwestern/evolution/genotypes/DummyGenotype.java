package edu.southwestern.evolution.genotypes;

import java.util.List;

/**
 * Contains no information, but can be used for methods that
 * accept a Genotype parameter, but don't really need the
 * information in it.
 * 
 * @author Jacob Schrum
 *
 * @param <T>
 */
public class DummyGenotype<T> implements Genotype<T> {

	@Override
	public void addParent(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Long> getParentIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Genotype<T> copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void mutate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Genotype<T> crossover(Genotype<T> g) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T getPhenotype() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Genotype<T> newInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

}

package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;

import edu.southwestern.networks.TWEANN;

/**
 * Allows to switch back and forth between a CPPN or Block Vector genotype.
 * 
 * @author Alejandro Medina
 *
 */
public class CPPNOrBlockVectorGenotype extends EitherOrGenotype<TWEANN,ArrayList<Double>>{

	
	/**
	 * default is TWEANN
	 */
	public CPPNOrBlockVectorGenotype() {
		this(new TWEANNGenotype(), true);
	}
	
	/**
	 * constructor that allows for changing from the default
	 * TWEANN
	 * @param genotype the genotype
	 * @param firstForm whether or not it is a TWEANN
	 */
	@SuppressWarnings("rawtypes")
	public CPPNOrBlockVectorGenotype(Genotype genotype, boolean firstForm) {
		super(genotype, firstForm);
	}
	
	/**
	 * Has a chance of mutating to change to CPPN
	 */
	//@SuppressWarnings("unchecked")
	@Override
	public void mutate() {
		// TODO: mutate method
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Genotype copy() {
		return new CPPNOrBlockVectorGenotype(current.copy(), this.firstForm);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Genotype crossover(Genotype g) {
		CPPNOrBlockVectorGenotype other = (CPPNOrBlockVectorGenotype) g;
		// If both genotypes are at the same stage/are of the same type
		if(firstForm == other.firstForm) {
			// Do crossover
			return new CPPNOrBlockVectorGenotype(current.crossover(other.current), firstForm);
		} else {
			// Otherwise, just return other genotype without performing crossover
			return other;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Genotype newInstance() {
		return new CPPNOrBlockVectorGenotype();
	}

}

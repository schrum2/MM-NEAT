package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;

import edu.southwestern.evolution.mutation.tweann.ConvertCPPN2GANtoDirect2GANMutation;
import edu.southwestern.networks.TWEANN;

/**
 * Allows to switch back and forth randomly between a CPPN to GAN 
 * or Direct to GAN genotype
 * 
 *
 */
public class CPPNOrDirectToGANGenotype extends EitherOrGenotype<TWEANN,ArrayList<Double>> {

	/**
	 * default is TWEANN
	 */
	public CPPNOrDirectToGANGenotype() {
		this(new TWEANNGenotype(), true);
	}	
	
	/**
	 * constructor that allows for changing from the default
	 * TWEANN
	 * @param genotype the genotype
	 * @param firstForm whether or not it is a TWEANN
	 */
	@SuppressWarnings("rawtypes")
	public CPPNOrDirectToGANGenotype(Genotype genotype, boolean firstForm) {
		super(genotype, firstForm);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	/**
	 * Has a chance of mutating to change to CPPN
	 */
	public void mutate() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getId());
		sb.append(" ");
		// Transition from CPPN to Direct, but keep identical expressed phenotype
		new ConvertCPPN2GANtoDirect2GANMutation().go(this, sb);
		// Now allow for slight changes
		super.mutate();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Genotype copy() {
		return new CPPNOrDirectToGANGenotype(current.copy(), this.firstForm);
	}


}

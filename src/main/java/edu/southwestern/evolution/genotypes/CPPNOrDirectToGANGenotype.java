package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;

import edu.southwestern.evolution.mutation.tweann.ConvertMarioCPPN2GANtoDirect2GANMutation;
import edu.southwestern.evolution.mutation.tweann.ConvertZeldaCPPN2GANtoDirect2GANMutation;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.gan.GANProcess.GAN_TYPE;

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
		if(GANProcess.type.equals(GAN_TYPE.ZELDA))
			new ConvertZeldaCPPN2GANtoDirect2GANMutation().go(this, sb);
		else if(GANProcess.type.equals(GAN_TYPE.MARIO))
			new ConvertMarioCPPN2GANtoDirect2GANMutation().go(this, sb);
		else //if (GANProcess.type.equals(GAN_TYPE.LODE_RUNNER))
			throw new UnsupportedOperationException(GANProcess.type.name() + " not supported yet");
		// Now allow for slight changes
		super.mutate();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Genotype copy() {
		return new CPPNOrDirectToGANGenotype(current.copy(), this.firstForm);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Genotype newInstance() {
		return new CPPNOrDirectToGANGenotype();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Genotype crossover(Genotype g) {
		CPPNOrDirectToGANGenotype other = (CPPNOrDirectToGANGenotype) g;
		// If both genotypes are at the same stage/are of the same type
		if(firstForm == other.firstForm) {
			// Do crossover
			return new CPPNOrDirectToGANGenotype(current.crossover(other.current), firstForm);
		} else {
			// Otherwise, just return other genotype without performing crossover
			return other;
		}
	}
}

package edu.southwestern.evolution.genotypes;

import edu.southwestern.networks.Network;

/**
 * A genotype that encodes some kind of neural network
 * 
 * @author Jacob Schrum
 * @param <T>
 *            The evolved phenotype, which must be a neural network
 */
public interface NetworkGenotype<T extends Network> extends Genotype<T> {
	/**
	 * Number of network modules
	 * 
	 * @return number of modules
	 */
	public int numModules();

	/**
	 * Assign the module usage of a network phenotype back to the genotype which
	 * spawned it for logging purposes.
	 * 
	 * @param usage
	 *            Array where each index corresponds to a module and contains
	 *            the number of times that module was used by the network
	 *            phenotype.
	 */
	public void setModuleUsage(int[] usage);

	/**
	 * Return module usage of the genotype
	 * 
	 * @return array of usage numbers
	 */
	public int[] getModuleUsage();
}

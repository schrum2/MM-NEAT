package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;

/**
 * an abstract class for mutating modules.
 * 
 * @author Jacob Schrum
 */
public abstract class ModuleMutation extends TWEANNMutation {

	/**
	 * Constructor for a moduleMutation
	 * 
	 * @param rate rate of mutation occurrences
	 */
	public ModuleMutation(String rate) {
		super(rate);
	}

	/**
	 * Mutates a TWEANN genotype
	 * 
	 * @param genotype the genotype to be mutated
	 */
	public void mutate(Genotype<TWEANN> genotype) {
		if (CommonConstants.weakenBeforeModeMutation) {// weakens modules if needed
			((TWEANNGenotype) genotype).weakenAllModules(Parameters.parameters.doubleParameter("weakenPortion"));
			if (infoTracking != null) {
				infoTracking.append("WEAKEN ");
			}
		}
		// Option to freeze existing network before adding new module
		if (CommonConstants.freezeBeforeModeMutation) {
			((TWEANNGenotype) genotype).freezeNetwork();
			if (infoTracking != null) {
				infoTracking.append("FREEZE ");
			}
		}
		// this is the method that changes between mutation methods
		addModule((TWEANNGenotype) genotype);
	}

	abstract public void addModule(TWEANNGenotype genotype);
}

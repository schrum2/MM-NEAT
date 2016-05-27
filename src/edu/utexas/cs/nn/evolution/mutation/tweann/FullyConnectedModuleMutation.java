package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.parameters.CommonConstants;

/**
 *
 * @author Jacob Schrum
 */
public class FullyConnectedModuleMutation extends ModuleMutation {

	public FullyConnectedModuleMutation() {
		super("fullMMRate");
	}

	@Override
	public boolean perform() {
		return !CommonConstants.fs && super.perform();
	}

	@Override
	public void addModule(TWEANNGenotype genotype) {
		int linksAdded = genotype.fullyConnectedModeMutation();
		int[] subs = new int[linksAdded];
		for (int i = 0; i < linksAdded; i++) {
			subs[i] = i + 1;
		}
		cullForBestWeight(genotype, subs);
	}
}

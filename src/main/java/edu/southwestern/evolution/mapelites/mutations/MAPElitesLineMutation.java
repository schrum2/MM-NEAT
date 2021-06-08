package edu.southwestern.evolution.mapelites.mutations;

import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.evolution.mutation.real.RealMutation;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;

public class MAPElitesLineMutation extends RealMutation {
	
	protected final double lineRate; 
	
	public MAPElitesLineMutation() {
		this.lineRate = Parameters.parameters.doubleParameter("meLineMutationRate");
	}
	
	@Override
	public void mutateIndex(RealValuedGenotype genotype, int i) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean perform() {
		return RandomNumbers.randomGenerator.nextDouble() <= lineRate;
	}
	
}

package edu.southwestern.evolution.mapelites.mutations;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
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
	public void mutate(Genotype<ArrayList<Double>> genotype) {
		// Randomly select y from archive, and save in global variable
		for (int i = 0; i < genotype.getPhenotype().size(); i++) {
			mutateIndex((RealValuedGenotype) genotype, i);
		}
	}
	
	@Override
	public void mutateIndex(RealValuedGenotype genotypeX, int index) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean perform() {
		return RandomNumbers.randomGenerator.nextDouble() <= lineRate;
	}
	
}

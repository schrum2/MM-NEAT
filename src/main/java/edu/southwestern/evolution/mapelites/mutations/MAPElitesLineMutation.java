package edu.southwestern.evolution.mapelites.mutations;

import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mutation.real.RealMutation;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;

/**
 * MAP-Elites-Line mutation described by:
 * https://arxiv.org/pdf/2003.04389.pdf
 * and originally implemented in:
 * https://arxiv.org/pdf/1804.03906v1.pdf
 * 
 * @author battertm
 *
 */
public class MAPElitesLineMutation extends RealMutation {
	
	protected final double lineRate;
	public static final double SIGMA1 = 0.01; // both sigmas from https://arxiv.org/pdf/1804.03906v1.pdf
	public static final double SIGMA2 = 0.2;
	public ArrayList<Double> genotypeY;
	public double normalDistribution2;
	
	// Constructor initializes mutation rate
	public MAPElitesLineMutation() {
		this.lineRate = Parameters.parameters.doubleParameter("meLineMutationRate");
	}
	
	// Mutation setup gets a random genotype to compare to, and gets a random number that remains the same the entire time
	@Override
	public void mutate(Genotype<ArrayList<Double>> genotype) {
		@SuppressWarnings("unchecked")
		Archive<ArrayList<Double>> archive = (Archive<ArrayList<Double>>) MMNEAT.getArchive();
		genotypeY = archive.getElite(archive.randomOccupiedBinIndex()).individual.getPhenotype();
		normalDistribution2 = MMNEAT.weightPerturber.randomOutput();
		
		for (int i = 0; i < genotype.getPhenotype().size(); i++) {
			mutateIndex((RealValuedGenotype) genotype, i);
		}
	}
	
	// Functionally performs vector multiplication and addition through the above for loop
	@Override
	public void mutateIndex(RealValuedGenotype genotypeX, int index) {
		genotypeX.getPhenotype().set(index,
				genotypeX.getPhenotype().get(index) +
				SIGMA1 * MMNEAT.weightPerturber.randomOutput() + 
				SIGMA2 * (genotypeX.getPhenotype().get(index)-genotypeY.get(index)) * normalDistribution2
		);
	}
	
	/*
	 * Determines if mutation should be performed, 
	 * otherwise uses normal mutation
	 */
	@Override
	public boolean perform() {
		return RandomNumbers.randomGenerator.nextDouble() <= lineRate;
	}
	
}

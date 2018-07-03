package edu.southwestern.evolution.genotypes;

import edu.southwestern.evolution.genotypes.TWEANNGenotype.FullNodeGene;

public class NormalizedMemoryNodeGene extends FullNodeGene{
	double gamma;
	double beta;

	protected NormalizedMemoryNodeGene(int ftype, int ntype, long innovation, boolean frozen, double bias, double gamma, double beta) {
		super(ftype, ntype, innovation, frozen, bias);
		this.gamma = gamma;
		this.beta = beta;
	}
	
}

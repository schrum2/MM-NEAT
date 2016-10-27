package edu.utexas.cs.nn.evolution.mutation.tweann;

import java.util.ArrayList;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.util.random.RandomNumbers;

public class WeightRandomReplacement extends TWEANNMutation {

	public WeightRandomReplacement() {
		super("WeightRandomReplacement");//for its use, will always happen, rate will be 1
		// TODO Auto-generated constructor stub
	}

	@Override
	public void mutate(Genotype<TWEANN> genotype) {
		TWEANNGenotype geno = (TWEANNGenotype) genotype;
		ArrayList<LinkGene> links = geno.links;
		for(int i = 0; i < links.size(); i++) {
			LinkGene link = links.get(i);
			link.weight = RandomNumbers.fullSmallRand();
		}
		
	}

}

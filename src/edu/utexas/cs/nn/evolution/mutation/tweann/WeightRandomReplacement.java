package edu.utexas.cs.nn.evolution.mutation.tweann;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.util.random.RandomNumbers;

public class WeightRandomReplacement extends TWEANNMutation {

	public WeightRandomReplacement() {
		super(1.0);//for its use, will always happen, rate will be 1
	}

	@Override
	public void mutate(Genotype<TWEANN> genotype) {//randomizes all links in genotype
		TWEANNGenotype geno = (TWEANNGenotype) genotype;
		for(LinkGene link: geno.links){
			link.weight = RandomNumbers.fullSmallRand();
		}
		
	}

}

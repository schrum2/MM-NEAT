package edu.southwestern.evolution.mutation.tweann;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.util.random.RandomNumbers;

public class WeightRandomReplacement extends TWEANNMutation {
	/**
	 * default constructor
	 */
	public WeightRandomReplacement() {
		super(1.0);//for its use, will always happen, rate will be 1
	}
	
	/**
	 * Loops through genotype and randomizes all of its links.
	 */
	@Override
	public void mutate(Genotype<TWEANN> genotype) {//randomizes all links in genotype
		TWEANNGenotype geno = (TWEANNGenotype) genotype;
		for(LinkGene link: geno.links){
			link.weight = RandomNumbers.fullSmallRand();
		}
		
	}

}

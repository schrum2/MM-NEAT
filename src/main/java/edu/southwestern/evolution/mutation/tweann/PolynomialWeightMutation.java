package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.evolution.mutation.real.PolynomialMutation;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.networks.TWEANN;

/**
 *
 * @author Jacob Schrum
 */
public class PolynomialWeightMutation extends TWEANNMutation {

	//used for default method  calls
	private final PolynomialMutation polynomial;
	private final double bound;

	/**
	 * Default constructor
	 */
	public PolynomialWeightMutation() {
		//command line parameter, "Mutation rate for network weight perturbation"
		super("netPerturbRate");
		//Done here because of loading of classes in MMNEAT
		this.polynomial = new PolynomialMutation();
		this.bound = Parameters.parameters.doubleParameter("weightBound");
	}

	/**
	 * Adds a new link with a polynomial-based weight mutation
	 * @param genotype TWEANNGenotype to be mutated
	 */
	public void mutate(Genotype<TWEANN> genotype) {
		TWEANNGenotype g = (TWEANNGenotype) genotype;
		LinkGene lg = g.randomAlterableLink();
		double weight = lg.weight;
		lg.weight = polynomial.newValue(weight, -bound, bound);
	}
}

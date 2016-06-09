package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.utexas.cs.nn.evolution.mutation.real.PolynomialMutation;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.networks.TWEANN;

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

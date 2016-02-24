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

    private final PolynomialMutation polynomial;
    private final double bound;

    public PolynomialWeightMutation() {
        super("netPerturbRate");
        this.polynomial = new PolynomialMutation();
        this.bound = Parameters.parameters.doubleParameter("weightBound");
    }

    public void mutate(Genotype<TWEANN> genotype) {
        TWEANNGenotype g = (TWEANNGenotype) genotype;
        LinkGene lg = g.randomAlterableLink();
        double weight = lg.weight;
        lg.weight = polynomial.newValue(weight, -bound, bound);
    }
}

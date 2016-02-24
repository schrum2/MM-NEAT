package edu.utexas.cs.nn.evolution.mutation.blueprint;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.NumericArrayGenotype;
import edu.utexas.cs.nn.evolution.mutation.Mutation;
import edu.utexas.cs.nn.experiment.MultiplePopulationGenerationalEAExperiment;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class BlueprintRandomMutation extends Mutation<ArrayList<Long>> {

    protected final double rate;

    public BlueprintRandomMutation() {
        this.rate = Parameters.parameters.doubleParameter("blueprintRandomRate");
    }

    /**
     * Always perform mutation, but have per-index-rate
     *
     * @return
     */
    @Override
    public boolean perform() {
        return true;
    }

    @Override
    public void mutate(Genotype<ArrayList<Long>> genotype) {
        NumericArrayGenotype<Long> ng = (NumericArrayGenotype<Long>) genotype;
        for (int i = 0; i < ng.getPhenotype().size(); i++) {
            if (RandomNumbers.randomGenerator.nextDouble() <= rate) {
                mutateIndex(ng, i);
            }
        }
    }

    /**
     * Switches a single genotype id in the blueprint to be the genotype id of a
     * random member of appropriate subpopulation. Only makes sense in the
     * context of cooperative coevolution, which is why MONE.experiment must be
     * an MultiplePopulationGenerationalEAExperiment.
     *
     * @param genotype blueprint genotype
     * @param i index in blueprint to modify
     */
    public void mutateIndex(NumericArrayGenotype<Long> genotype, int i) {
        assert (MMNEAT.experiment instanceof MultiplePopulationGenerationalEAExperiment);
        MultiplePopulationGenerationalEAExperiment exp = (MultiplePopulationGenerationalEAExperiment) MMNEAT.experiment;
        long newId = exp.randomIdFromSubpop(i);
        genotype.getPhenotype().set(i, newId);
    }
}

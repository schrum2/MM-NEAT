package edu.utexas.cs.nn.evolution.genotypes;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.crossover.ArrayCrossover;
import edu.utexas.cs.nn.evolution.mutation.blueprint.BlueprintChildrenMutation;
import edu.utexas.cs.nn.evolution.mutation.blueprint.BlueprintRandomMutation;
import edu.utexas.cs.nn.experiment.MultiplePopulationGenerationalEAExperiment;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class SimpleBlueprintGenotype extends NumericArrayGenotype<Long> {

    /**
     * Only used to create example genotype. The array will contain no useful
     * data, but the newInstance method will make useful instances for the
     * actual population.
     */
    public SimpleBlueprintGenotype(int size) {
        super(new Long[size]);
    }

    public SimpleBlueprintGenotype(ArrayList<Long> genes) {
        super(genes);
    }

    @Override
    public Genotype<ArrayList<Long>> crossover(Genotype<ArrayList<Long>> g) {
        return new ArrayCrossover<Long>().crossover(this, g);
    }

    @Override
    public void mutate() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getId());
        sb.append(" ");
        new BlueprintRandomMutation().go(this, sb);
        new BlueprintChildrenMutation().go(this, sb);
        EvolutionaryHistory.logMutationData(sb.toString());
    }

    public Genotype<ArrayList<Long>> copy() {
        return new SimpleBlueprintGenotype(genes);
    }

    public Genotype<ArrayList<Long>> newInstance() {
        return new SimpleBlueprintGenotype(((MultiplePopulationGenerationalEAExperiment) MMNEAT.experiment).randomBlueprint());
    }
}

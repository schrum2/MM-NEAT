package edu.utexas.cs.nn.evolution.nsga2.bd.characterizations;

import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.utexas.cs.nn.evolution.nsga2.bd.vectors.RealBehaviorVector;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
 *
 * @author Jacob Schrum
 * @param <TWEANN>
 */
public class ModuleUsageCharacterization<TWEANN> implements BehaviorCharacterization<TWEANN> {

    @Override
    public BehaviorVector getBehaviorVector(Score<TWEANN> score) {
        return new RealBehaviorVector(StatisticsUtilities.distribution(((TWEANNGenotype) score.individual).getModuleUsage()));
    }

    @Override
    public void prepare() {
    }
}

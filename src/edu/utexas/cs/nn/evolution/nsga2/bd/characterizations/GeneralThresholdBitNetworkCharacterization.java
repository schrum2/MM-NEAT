package edu.utexas.cs.nn.evolution.nsga2.bd.characterizations;

import edu.utexas.cs.nn.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.utexas.cs.nn.evolution.nsga2.bd.vectors.ThresholdBitBehaviorVector;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.scores.Score;

/**
 *
 * @author Jacob Schrum
 */
public class GeneralThresholdBitNetworkCharacterization<T extends Network> extends GeneralNetworkCharacterization<T> {

    @Override
    public BehaviorVector getBehaviorVector(Score<T> score) {
        return new ThresholdBitBehaviorVector(getBehaviorVector(score.individual.getPhenotype(), this.syllabus));
    }
}

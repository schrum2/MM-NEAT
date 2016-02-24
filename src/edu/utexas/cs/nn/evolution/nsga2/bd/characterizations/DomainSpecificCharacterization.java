package edu.utexas.cs.nn.evolution.nsga2.bd.characterizations;

import edu.utexas.cs.nn.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.utexas.cs.nn.evolution.nsga2.bd.vectors.RealBehaviorVector;
import edu.utexas.cs.nn.scores.Score;

/**
 *
 * @author Jacob Schrum
 */
public class DomainSpecificCharacterization<T> implements BehaviorCharacterization<T> {

    /**
     * The behavior vector comes directly from the domain. This approach simply
     * passes on information calculated elsewhere.
     *
     * @param score Score of the bot, which also stores a behavior vector
     * @return the stored behavior vector
     */
    public BehaviorVector getBehaviorVector(Score<T> score) {
        return new RealBehaviorVector(score.behaviorVector);
    }

    public void prepare() {
    }
}

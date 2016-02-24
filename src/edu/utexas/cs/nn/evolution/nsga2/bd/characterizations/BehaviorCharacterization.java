package edu.utexas.cs.nn.evolution.nsga2.bd.characterizations;

import edu.utexas.cs.nn.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.utexas.cs.nn.scores.Score;

/**
 *
 * @author Jacob Schrum
 */
public interface BehaviorCharacterization<T> {

    public void prepare();

    public BehaviorVector getBehaviorVector(Score<T> score);
}

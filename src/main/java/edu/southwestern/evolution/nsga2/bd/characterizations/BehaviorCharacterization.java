package edu.southwestern.evolution.nsga2.bd.characterizations;

import edu.southwestern.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.southwestern.scores.Score;

/**
 * Interface for behavior characterization specified by each domain or a
 * generalized form is used as default
 * 
 * @author Jacob Schrum
 * @commented Lauren Gillespie
 */
public interface BehaviorCharacterization<T> {

	/**
	 * Prepares data so behaviorVector can be created
	 */
	public void prepare();

	/**
	 * gets the behavior vector(score of diversity) from raw score
	 * 
	 * @param score
	 *            raw score
	 * @return behavior vector
	 */
	public BehaviorVector getBehaviorVector(Score<T> score);

}

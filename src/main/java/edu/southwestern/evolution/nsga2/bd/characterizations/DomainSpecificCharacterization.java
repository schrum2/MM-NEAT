package edu.southwestern.evolution.nsga2.bd.characterizations;

import edu.southwestern.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.southwestern.evolution.nsga2.bd.vectors.RealBehaviorVector;
import edu.southwestern.scores.Score;

/**
 * creates domain-specific characterization behavior vectors
 * 
 * @author Jacob Schrum
 * @commented Lauren Gillespie
 */
public class DomainSpecificCharacterization<T> implements BehaviorCharacterization<T> {

	/**
	 * The behavior vector comes directly from the domain. This approach simply
	 * passes on information calculated elsewhere.
	 *
	 * @param score
	 *            Score of the bot, which also stores a behavior vector
	 * @return the stored behavior vector
	 */
	public BehaviorVector getBehaviorVector(Score<T> score) {
		return new RealBehaviorVector(score.behaviorVector);
	}

	/**
	 * gets new random syllabus for testing
	 */
	public void prepare() {
	}
}

package edu.southwestern.scores;

/**
 * The ObjectiveBasedSelector class stores a specific index where scores of
 * individuals should be compared and compares scores between individuals.
 * 
 * @author Jacob Schrum
 */
public class ObjectiveBasedSelector<T> implements Better<Score<T>> {

	private final int index;// index of the score to be looked at

	/**
	 * Constructor for an ObjectiveBasedSelector
	 * 
	 * @param index:
	 *            the index for the selector
	 */
	public ObjectiveBasedSelector(int index) {
		this.index = index;
	}

	/**
	 * The better method determines which agent has the better score at the
	 * stored index.
	 * 
	 * @param e1:
	 *            the score of the first agent to be compared
	 * 
	 * @param e2:
	 *            the score of the second agent to be compared
	 * 
	 * @return: returns a boolean corresponding to whether or not e1's score is
	 *          better than e2's
	 */
	public Score<T> better(Score<T> e1, Score<T> e2) {
		return e1.scores[index] > e2.scores[index] ? e1 : e2;
	}
}

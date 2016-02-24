package edu.utexas.cs.nn.scores;

/**
 *
 * @author Jacob Schrum
 */
public class ObjectiveBasedSelector implements Better<Score> {

    private final int index;

    public ObjectiveBasedSelector(int index) {
        this.index = index;
    }

    public Score better(Score e1, Score e2) {
        return e1.scores[index] > e2.scores[index] ? e1 : e2;
    }
}

package edu.utexas.cs.nn.evolution.metaheuristics;

import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.scores.Score;

/**
 * Fitness that encourages having lots of modes. Only makes sense if some other
 * mechanism is limiting mode mutations.
 *
 * @author Jacob Schrum
 */
public class MaxModesFitness implements Metaheuristic {

    public void augmentScore(Score s) {
        s.extraScore(((TWEANNGenotype) s.individual).numModes);
    }

    public double minScore() {
        return 1;
    }

    public double startingTUGGoal() {
        return minScore();
    }
}

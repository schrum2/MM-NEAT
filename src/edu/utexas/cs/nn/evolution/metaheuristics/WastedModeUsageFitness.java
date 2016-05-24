package edu.utexas.cs.nn.evolution.metaheuristics;


import edu.utexas.cs.nn.scores.Score;

/**
 *
 * @author Jacob Schrum
 */
public class WastedModeUsageFitness implements Metaheuristic {

    public WastedModeUsageFitness() {
    }

    // On the verge of completely removing this fitness function as an option
    @SuppressWarnings("rawtypes")
	public void augmentScore(Score s) {
        throw new UnsupportedOperationException("Not supported: disabled");
//        s.extraScore(-((TWEANNGenotype) s.individual).wastedModeUsage(EvolutionaryHistory.maxModesOfAnyNetwork));
    }

    public double minScore() {
        return -1;
    }

    public double startingTUGGoal() {
        return minScore();
    }
}

package edu.utexas.cs.nn.evolution.metaheuristics;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;

/**
 *
 * @author Jacob Schrum
 */
public class LinkPenalty implements Metaheuristic {

    private final int populationIndex;
    private final boolean modeAvg;

    public LinkPenalty() {
        this(0);
    }

    public LinkPenalty(int populationIndex) {
        this.populationIndex = populationIndex;
        this.modeAvg = Parameters.parameters.booleanParameter("penalizeLinksPerMode");
    }

    public void augmentScore(Score s) {
        s.extraScore(getScore((TWEANNGenotype) s.individual));
    }

    public double minScore() {
        int nodes = EvolutionaryHistory.archetypeSize(populationIndex);
        return -(nodes * nodes); // Every node connected to every other
    }

    public double startingTUGGoal() {
        return minScore();
    }

    public double getScore(TWEANNGenotype g) {
        return -(g.links.size() / (1.0 * (modeAvg ? g.numModes : 1)));
    }
}

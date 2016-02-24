/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution.metaheuristics;

import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.scores.Score;

/**
 *
 * @author Jacob Schrum
 */
public class AntiMaxModeUsageFitness implements Metaheuristic {

    public AntiMaxModeUsageFitness() {
    }

    public void augmentScore(Score s) {
        s.extraScore(getScore((TWEANNGenotype) s.individual));
    }

    public double minScore() {
        return -1;
    }

    public double startingTUGGoal() {
        return minScore();
    }

    public double getScore(TWEANNGenotype g) {
        return -g.maxModeUsage();
    }
}

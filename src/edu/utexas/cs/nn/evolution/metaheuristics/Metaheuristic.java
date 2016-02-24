/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution.metaheuristics;

import edu.utexas.cs.nn.scores.Score;

/**
 *
 * @author He_Deceives
 */
public interface Metaheuristic {

    public void augmentScore(Score s);

    public double minScore();

    public double startingTUGGoal();
}

/*
 * Method for determining what other members of a population are in the same niche
 * for local competition
 */
package edu.utexas.cs.nn.evolution.nsga2.bd.localcompetition;

import edu.utexas.cs.nn.scores.Score;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public abstract class NicheDefinition<T> {

    ArrayList<Score<T>> originalPopulation;

    public void loadPopulation(ArrayList<Score<T>> originalPopulation) {
        this.originalPopulation = originalPopulation;
    }

    public abstract ArrayList<Score<T>> getNiche(Score<T> individual);
}

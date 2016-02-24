/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.scores.Score;
import java.util.ArrayList;

/**
 *
 * @author He_Deceives
 */
public interface SinglePopulationTask<T> extends Task {

    public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population);
}

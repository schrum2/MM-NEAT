/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import java.util.ArrayList;

/**
 * Annoying Programming Languages problem: Want to allow an arbitrary number of
 * subpopulations, and allow each one to hold a different type of genotype.
 * Cannot support both options. Either need to designate a specific number and
 * provide a type variable for each one, or use an arbitrary length structure
 * like ArrayList with the specific type of Genotype undefined, so that each one
 * can be different.
 *
 * @author He_Deceives
 */
public interface MultiplePopulationGenerationalEA extends GenerationalEA {

    public ArrayList<ArrayList<Genotype>> initialPopulations(ArrayList<Genotype> examples);

    public ArrayList<ArrayList<Genotype>> getNextGeneration(ArrayList<ArrayList<Genotype>> populations);

    public void close(ArrayList<ArrayList<Genotype>> populations);
}

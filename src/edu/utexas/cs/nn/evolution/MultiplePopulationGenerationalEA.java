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
 * @author Jacob Schrum
 */
public interface MultiplePopulationGenerationalEA extends GenerationalEA {

	@SuppressWarnings("rawtypes")
	public ArrayList<ArrayList<Genotype>> initialPopulations(ArrayList<Genotype> examples);

	@SuppressWarnings("rawtypes")
	public ArrayList<ArrayList<Genotype>> getNextGeneration(ArrayList<ArrayList<Genotype>> populations);

	@SuppressWarnings("rawtypes")
	public void close(ArrayList<ArrayList<Genotype>> populations);
}

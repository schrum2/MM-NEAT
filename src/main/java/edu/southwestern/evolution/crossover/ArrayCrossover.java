package edu.southwestern.evolution.crossover;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;
import java.util.ArrayList;

/**
 * This class accomplishes the task of crossing over two different
 * genotypes and a helper method that performs the actual swapping.
 * 
 * @author Jacob Schrum
 */
public class ArrayCrossover<T> extends Crossover<ArrayList<T>> {

	/**
	 * The method crossover is an inherited method from the abstract crossover
	 * class. It performs a standard single-point crossover by taking a random
	 * number, i, seeded with a number based on the phenotype of the toModify
	 * parameter. The values of the objects toModify and toReturn are swapped
	 * after the i value using a for loop and the helper method
	 * newIndexContents.
	 *
	 * @param toModify Reference to genotype that is modified by crossover.
	 * @param toReturn Reference to genotype that is returned unmodified.
	 * @return Returns the second genotype produced by the crossover.
	 */
	@Override
	public Genotype<ArrayList<T>> crossover(Genotype<ArrayList<T>> toModify, Genotype<ArrayList<T>> toReturn) {
		// the random seeded number that corresponds to the single-point at
		// which the crossover occurs
		int point = RandomNumbers.randomGenerator.nextInt(toModify.getPhenotype().size());
		// the for loop that swaps the following values.
		for (int i = point; i < toModify.getPhenotype().size(); i++) {
			Pair<T, T> p = newIndexContents(toReturn.getPhenotype().get(i), toModify.getPhenotype().get(i), i);
			toReturn.getPhenotype().set(i, p.t1);
			toModify.getPhenotype().set(i, p.t2);
		}
		return toReturn;
	}

	/**
	 * The method newIndexContents works as a helper function that uses the swap
	 * method from the abstract class Crossover.
	 *
	 * @param par1 the object to swap from array 1.
	 * @param par2 the object to swap from toModify.
	 * @param index the index of each array where the par objects are located,
	 *            	used to facilitate the swap.
	 * @return returns a pair containing both values swapped.
	 */
	public Pair<T, T> newIndexContents(T par1, T par2, int index) {
		return swap(par1, par2);
	}
}

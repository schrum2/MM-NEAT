package edu.southwestern.evolution.crossover;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Randomly selects genes from each parent regardless of position.
 * 
 * @author schrum2
 *
 * @param <T>
 */
public class MultipointCrossover<T> extends ArrayCrossover<T> {
	@Override
	public Genotype<ArrayList<T>> crossover(Genotype<ArrayList<T>> toModify, Genotype<ArrayList<T>> toReturn) {
		for (int i = 0; i < toModify.getPhenotype().size(); i++) {
			if(RandomNumbers.coinFlip()) {
				// This comes from ArrayCrossover, and is basically a swap operation
				Pair<T, T> p = newIndexContents(toReturn.getPhenotype().get(i), toModify.getPhenotype().get(i), i);
				toReturn.getPhenotype().set(i, p.t1);
				toModify.getPhenotype().set(i, p.t2);
			}
		}

		//System.out.println("AFTER :"+toModify.getPhenotype() + ":"+toReturn.getPhenotype());
		return toReturn;
	}
}

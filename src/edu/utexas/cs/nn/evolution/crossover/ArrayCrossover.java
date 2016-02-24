package edu.utexas.cs.nn.evolution.crossover;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class ArrayCrossover<T> extends Crossover<ArrayList<T>> {

    public Genotype<ArrayList<T>> crossover(Genotype<ArrayList<T>> toModify, Genotype<ArrayList<T>> toReturn) {
        int point = RandomNumbers.randomGenerator.nextInt(toModify.getPhenotype().size());
        for (int i = point; i < toModify.getPhenotype().size(); i++) {
            Pair<T, T> p = newIndexContents(toReturn.getPhenotype().get(i), toModify.getPhenotype().get(i), i);
            toReturn.getPhenotype().set(i, p.t1);
            toModify.getPhenotype().set(i, p.t2);
        }
        return toReturn;
    }

    public Pair<T, T> newIndexContents(T par1, T par2, int index) {
        return swap(par1, par2);
    }
}

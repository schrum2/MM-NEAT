package edu.utexas.cs.nn.evolution.genotypes;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.crossover.ArrayCrossover;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public abstract class NumericArrayGenotype<T> implements Genotype<ArrayList<T>> {

    ArrayList<T> genes;
    private long id = EvolutionaryHistory.nextGenotypeId();

    public NumericArrayGenotype(ArrayList<T> genes) {
        this.genes = (ArrayList<T>) genes.clone();
    }

    public NumericArrayGenotype(T[] genes) {
        this.genes = new ArrayList<T>(genes.length);
        for (int i = 0; i < genes.length; i++) {
            this.genes.add(genes[i]);
        }
    }

    public void setValue(int pos, T value) {
        genes.set(pos, value);
    }

    public Genotype<ArrayList<T>> crossover(Genotype<ArrayList<T>> g) {
        return new ArrayCrossover<T>().crossover(this, g);
    }

    public ArrayList<T> getPhenotype() {
        return genes;
    }

    @Override
    public String toString() {
        return getId() + ":" + genes.toString();
    }

    public long getId() {
        return id;
    }
}

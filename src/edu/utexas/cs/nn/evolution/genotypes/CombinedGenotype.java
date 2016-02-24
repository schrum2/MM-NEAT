package edu.utexas.cs.nn.evolution.genotypes;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 *
 * @author Jacob Schrum
 */
public class CombinedGenotype<X, Y> extends Pair<Genotype<X>, Genotype<Y>> implements Genotype<Pair<X, Y>> {

    private long id = EvolutionaryHistory.nextGenotypeId();

    public CombinedGenotype(Genotype<X> x, Genotype<Y> y) {
        super(x, y);
    }

    public void mutate() {
        t1.mutate();
        t2.mutate();
    }

    public Genotype<Pair<X, Y>> crossover(Genotype<Pair<X, Y>> g) {
        // Assume it will be a combined genotype
        CombinedGenotype<X, Y> other = (CombinedGenotype<X, Y>) g;
        Genotype<X> result1 = t1.crossover(other.t1);
        Genotype<Y> result2 = t2.crossover(other.t2);
        return new CombinedGenotype<X, Y>(result1, result2);
    }

    public Genotype<Pair<X, Y>> copy() {
        return new CombinedGenotype<X, Y>(t1.copy(), t2.copy());
    }

    public Pair<X, Y> getPhenotype() {
        return new Pair<X, Y>(t1.getPhenotype(), t2.getPhenotype());
    }

    public Genotype<Pair<X, Y>> newInstance() {
        return new CombinedGenotype<X, Y>(t1.newInstance(), t2.newInstance());
    }

    public long getId() {
        return id;
    }
}

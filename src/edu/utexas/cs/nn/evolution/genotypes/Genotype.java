package edu.utexas.cs.nn.evolution.genotypes;

/**
 *
 * @author Jacob Schrum
 */
public interface Genotype<T> {

    public Genotype<T> copy();

    public void mutate();

    public Genotype<T> crossover(Genotype<T> g);

    public T getPhenotype();

    public Genotype<T> newInstance();

    public long getId();
}

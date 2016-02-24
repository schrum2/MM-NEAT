package edu.utexas.cs.nn.evolution.genotypes;

import edu.utexas.cs.nn.evolution.genotypes.pool.GenotypePool;
import edu.utexas.cs.nn.networks.HierarchicalTWEANN;
import edu.utexas.cs.nn.networks.TWEANN;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class HierarchicalTWEANNGenotype extends TWEANNGenotype {

    public BoundedIntegerValuedGenotype subNetIds;

    public HierarchicalTWEANNGenotype() {
        super();
        this.subNetIds = new BoundedIntegerValuedGenotype(GenotypePool.numPools());
    }

    public HierarchicalTWEANNGenotype(TWEANNGenotype tg, BoundedIntegerValuedGenotype subNetIds) {
        super((TWEANNGenotype) tg);
        this.subNetIds = (BoundedIntegerValuedGenotype) subNetIds.copy();
    }

    public HierarchicalTWEANNGenotype(HierarchicalTWEANN ht) {
        super(ht);
        this.subNetIds = new BoundedIntegerValuedGenotype(ht.getSubNetIds());
    }

    @Override
    public Genotype<TWEANN> copy() {
        int[] temp = modeUsage;
        TWEANNGenotype tg = (TWEANNGenotype) super.copy();
        HierarchicalTWEANNGenotype result = new HierarchicalTWEANNGenotype(tg, this.subNetIds);
        result.modeUsage = new int[modeUsage.length];
        // Mode usage is erased by getPhenotype(), so it is restored here
        modeUsage = temp;
        System.arraycopy(this.modeUsage, 0, result.modeUsage, 0, modeUsage.length);
        return result;
    }

    @Override
    public TWEANN getPhenotype() {
        HierarchicalTWEANN result = new HierarchicalTWEANN(this);
        this.modeUsage = result.modeUsage;
        return result;
    }

    @Override
    public Genotype<TWEANN> crossover(Genotype<TWEANN> g) {
        HierarchicalTWEANNGenotype htgOther = (HierarchicalTWEANNGenotype) g;

        // tgOther now holds the modified TWEANN from g, and "this" has a modified TWEANN portion
        TWEANNGenotype tgOther = (TWEANNGenotype) super.crossover(htgOther);

        // modifies subNetIds of "this", and bigOther contains modified subnet ids of g
        BoundedIntegerValuedGenotype bigOther = (BoundedIntegerValuedGenotype) subNetIds.crossover(htgOther.subNetIds);

        // Combine the separate components
        HierarchicalTWEANNGenotype result = new HierarchicalTWEANNGenotype(tgOther, bigOther);
        result.calculateNumModes();

        return result;
    }

    @Override
    public Genotype<TWEANN> newInstance() {
        HierarchicalTWEANNGenotype result = new HierarchicalTWEANNGenotype((TWEANNGenotype) super.newInstance(), (BoundedIntegerValuedGenotype) subNetIds.newInstance());
        result.modeUsage = new int[result.numModes];
        return result;
    }

    @Override
    public void mutate() {
        super.mutate();
        this.subNetIds.mutate();
    }

    public Genotype<TWEANN> getSubNetGenotype(int pool) {
        return GenotypePool.getMember(pool, subNetIds.getPhenotype().get(pool));
    }

    public ArrayList<Integer> getSubNetIds() {
        return subNetIds.getPhenotype();
    }
}

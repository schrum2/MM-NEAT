package edu.utexas.cs.nn.networks;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.HierarchicalTWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.pool.GenotypePool;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class HierarchicalTWEANN extends TWEANN {

    ArrayList<Integer> subNetIds;

    public HierarchicalTWEANN(int numIn, int numOut, boolean featureSelective, int ftype, int numModes, ArrayList<Integer> subNets, int archetypeIndex) {
        super(numIn, numOut, featureSelective, ftype, numModes, archetypeIndex);
        this.subNetIds = subNets;
    }

    public HierarchicalTWEANN(HierarchicalTWEANNGenotype g) {
        super(g);
        subNetIds = g.getSubNetIds();
    }

    public Genotype<TWEANN> getSubNetGenotype(int pool) {
        return GenotypePool.getMember(pool, subNetIds.get(pool));
    }

    public ArrayList<Integer> getSubNetIds() {
        return subNetIds;
    }
}

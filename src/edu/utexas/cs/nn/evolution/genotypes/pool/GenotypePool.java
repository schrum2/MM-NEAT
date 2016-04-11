package edu.utexas.cs.nn.evolution.genotypes.pool;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.util.file.XMLFilter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * A pool of genotypes from a previously evolved population. Networks can be
 * selected from such a pool to fill in slots of hierarchical networks, or to
 * create ensembles.
 *
 * @author Jacob Schrum
 */
public class GenotypePool<T extends Network> {

	// This is a static collection of pools accessible to everyone. 
	// Type must be raw so that different pools can contain different types of genotypes.
    @SuppressWarnings("rawtypes")
	public static ArrayList<GenotypePool> pools = new ArrayList<GenotypePool>();

    @SuppressWarnings("rawtypes")
	public static void addPool(String directory) {
        pools.add(new GenotypePool(directory));
    }

    @SuppressWarnings("unchecked")
	public static <T> Genotype<T> getMember(int pool, int slot) {
        return pools.get(pool).getMember(slot);
    }

    /**
     * Number of genotype pools to draw from
     * @return number of pools
     */
    public static int numPools() {
        return pools.size();
    }

    /**
     * Number of genotypes in a particular pool
     * @param pool Index of pool
     * @return size of particular pool
     */
    public static int poolSize(int pool) {
        return pools.get(pool).size();
    }
    private String[] pool;

    /**
     * Treat all .xml files in a particular directory as genotypes,
     * and use them to create the genotype pool.
     * @param directory Directory containing .xml genotypes
     */
    public GenotypePool(String directory) {
        System.out.println("Loading file names from: " + directory);

        FilenameFilter filter = new XMLFilter();

        File dir = new File(directory);
        pool = dir.list(filter);
        if (!dir.exists() || pool == null) {
            System.err.println("Can't load population, folder '" + directory + "' does not exist");
            System.exit(1);
        } else {
            // Put paths in front of names
            for (int i = 0; i < pool.length; i++) {
                pool[i] = directory + "/" + pool[i];
            }
        }
    }

    /**
     * Retrieve actual genotype using filename stored in pool at particular index
     * @param x index in genotype pool of desired filename
     * @return genotype stored in designated file
     */
    public Genotype<T> getMember(int x) {
        return EvolutionaryHistory.getSubnetwork(pool[x]);
    }

    /**
     * Size of this genotype pool
     * @return size of pool
     */
    public int size() {
        return pool.length;
    }
}

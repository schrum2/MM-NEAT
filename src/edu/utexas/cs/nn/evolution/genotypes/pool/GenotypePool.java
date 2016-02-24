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

    public static ArrayList<GenotypePool> pools = new ArrayList<GenotypePool>();

    public static void addPool(String directory) {
        pools.add(new GenotypePool(directory));
    }

    public static <T> Genotype<T> getMember(int pool, int slot) {
        return pools.get(pool).getMember(slot);
    }

    public static int numPools() {
        return pools.size();
    }

    public static int poolSize(int pool) {
        return pools.get(pool).size();
    }
    private String[] pool;

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

    public Genotype<T> getMember(int x) {
        return EvolutionaryHistory.getSubnetwork(pool[x]);
    }

    public int size() {
        return pool.length;
    }
}

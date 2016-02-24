package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.pool.GenotypePool;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.utexas.cs.nn.util.ClassCreation;

/**
 *
 * @author Jacob Schrum
 */
public class PillEatingNetworkBlock<T extends Network> extends SubNetworkBlock<T> {

    public static final int PILL_POOL = 1;

    public PillEatingNetworkBlock() throws NoSuchMethodException {
        this(Parameters.parameters.stringParameter("pillEatingSubnetwork"), Parameters.parameters.booleanParameter("subsumptionIncludesInputs"));
    }

    /*
     * Gets subnetwork from saved xml file
     */
    public PillEatingNetworkBlock(String xml, boolean includeInputs) throws NoSuchMethodException {
        this(EvolutionaryHistory.getSubnetwork(xml), includeInputs);
    }

    public PillEatingNetworkBlock(Genotype<Network> g, boolean includeInputs) throws NoSuchMethodException {
        this(g == null ? (Network) GenotypePool.getMember(PILL_POOL, 0).getPhenotype() : g.getPhenotype(), includeInputs);
    }

    public PillEatingNetworkBlock(Network n, boolean includeInputs) throws NoSuchMethodException {
        super(n, (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacManMediatorClass2"), "Pill Eater", includeInputs);
    }
}

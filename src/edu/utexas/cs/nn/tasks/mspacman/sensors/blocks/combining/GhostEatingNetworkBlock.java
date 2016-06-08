package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.pool.GenotypePool;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.MsPacManTask;
import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.GhostTaskMediator;
import edu.utexas.cs.nn.util.ClassCreation;

/**
 *
 * @author Jacob Schrum
 */
public class GhostEatingNetworkBlock<T extends Network> extends SubNetworkBlock<T> {

	public static final int GHOST_POOL = 0;

	public GhostEatingNetworkBlock() throws NoSuchMethodException {
		this(Parameters.parameters.stringParameter("ghostEatingSubnetwork"),
				Parameters.parameters.booleanParameter("subsumptionIncludesInputs"));
	}

	/*
	 * Gets subnetwork from saved xml file
	 */
	public GhostEatingNetworkBlock(String xml, boolean includeInputs) throws NoSuchMethodException {
		this(EvolutionaryHistory.getSubnetwork(xml), includeInputs);
	}

	/**
	 * First network in appropriate pool can be temporarily allowed as a dummy
	 * value to be replaced later
	 *
	 * @param g
	 *            genotype with network
	 * @param includeInputs
	 *            whether or not the input block will include the inputs to the
	 *            subnet
	 */
	public GhostEatingNetworkBlock(Genotype<Network> g, boolean includeInputs) throws NoSuchMethodException {
		this(g == null ? (Network) GenotypePool.getMember(GHOST_POOL, 0).getPhenotype() : g.getPhenotype(),
				includeInputs);
	}

	public GhostEatingNetworkBlock(Network n, boolean includeInputs) throws NoSuchMethodException {
		super(n, (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacManMediatorClass1"),
				"Ghost Eater", includeInputs);
	}

	/**
	 * For unit testing
	 * 
	 * Though I'm having trouble remembering what this actually does.
	 *
	 * @param args
	 */
	public static void main(String[] args) throws NoSuchMethodException {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "showNetworks:true", "watch:true", "task:edu.utexas.cs.nn.tasks.mspacman.MsPacManTask" });
		MMNEAT.loadClasses();
		MsPacManTask task = new MsPacManTask(true);

		MsPacManControllerInputOutputMediator ghostMediator = new GhostTaskMediator();
		MMNEAT.pacmanInputOutputMediator = ghostMediator;
		TWEANN ghostNet = new TWEANN(ghostMediator.numIn(), ghostMediator.numOut(), false, 0, 1, 0);
		TWEANNGenotype ghostGenotype = new TWEANNGenotype(ghostNet);
		for (int i = 0; i < 25; i++) {
			ghostGenotype.mutate();
		}
		System.out.println("Ghost Only ---------------------------------------");
		task.evaluate(ghostGenotype);

		BlockLoadedInputOutputMediator subsumptionMediator = new BlockLoadedInputOutputMediator();
		subsumptionMediator.blocks.add(new GhostEatingNetworkBlock(ghostGenotype.getPhenotype(), false));
		MMNEAT.pacmanInputOutputMediator = subsumptionMediator;
		TWEANN subsumptionNet = new TWEANN(subsumptionMediator.numIn(), subsumptionMediator.numOut(), false, 0, 1, -1);
		TWEANNGenotype subsumptionGenotype = new TWEANNGenotype(subsumptionNet);

		System.out.println("Subsumption --------------------------------------");
		task.evaluate(subsumptionGenotype);
	}
}

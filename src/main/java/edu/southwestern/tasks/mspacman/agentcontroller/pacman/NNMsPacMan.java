package edu.southwestern.tasks.mspacman.agentcontroller.pacman;

import edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype;

import edu.southwestern.evolution.Organism;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.HierarchicalTWEANNGenotype;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.HierarchicalTWEANN;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.CooperativeCheckEachMultitaskSelectorMsPacManTask;
import edu.southwestern.tasks.mspacman.CooperativeSubtaskSelectorMsPacManTask;
import edu.southwestern.tasks.mspacman.sensors.ActionBlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.CombiningInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.combining.GhostEatingNetworkBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.combining.PillEatingNetworkBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.combining.SubNetworkBlock;

/**
 * Defines an evolved MsPacMan agent
 * 
 * @author Jacob Schrum
 * @param <T> phenotype which must be a network
 */
public class NNMsPacMan<T extends Network> extends Organism<T> {

	public NNPacManController controller;

	/**
	 * Messy trick to allow coevolution of multitask scheme groups
	 *
	 * @param controller
	 */
	public NNMsPacMan(NNPacManController controller) {
		super(null);
		this.controller = controller;
	}

	/**
	 * method which defines the controller based on parameters and classOptions
	 * and the mediators
	 * 
	 * @param genotype
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public NNMsPacMan(Genotype<T> genotype) {
		super(genotype);
		if(CommonConstants.hyperNEAT) {
			controller = new NNHyperNEATPacManController((HyperNEATCPPNGenotype) genotype);
		} else {
			Network net = (Network) this.getGenotype().getPhenotype();
			boolean evolveNetworkSelector = Parameters.parameters.booleanParameter("evolveNetworkSelector");
			try {
				if (MMNEAT.pacmanInputOutputMediator instanceof ActionBlockLoadedInputOutputMediator) {
					controller = new NNActionPacManController(net);
				} else if (MMNEAT.pacmanInputOutputMediator instanceof VariableDirectionBlockLoadedInputOutputMediator) {
					if (MMNEAT.sharedMultitaskNetwork != null) {
						// All individuals define preference usage for the same
						// shared Multitask policy network
						controller = new NNMultitaskSelectorCheckEachDirectionPacManController(
								MMNEAT.sharedMultitaskNetwork, genotype, MMNEAT.directionalSafetyFunction);
					} else if (MMNEAT.sharedPreferenceNetwork != null) {
						// All individuals use same preference net for different
						// evolved Multitask policy networks
						controller = new NNMultitaskSelectorCheckEachDirectionPacManController(genotype,
								MMNEAT.sharedPreferenceNetwork, MMNEAT.directionalSafetyFunction);
					} else if (CooperativeCheckEachMultitaskSelectorMsPacManTask.multitaskGenotype != null) {
						controller = new NNMultitaskSelectorCheckEachDirectionPacManController(
								CooperativeCheckEachMultitaskSelectorMsPacManTask.multitaskGenotype, genotype,
								MMNEAT.directionalSafetyFunction);
					} else {
						controller = new NNCheckEachDirectionPacManController(genotype, MMNEAT.directionalSafetyFunction);
					}
				} else if (genotype instanceof HierarchicalTWEANNGenotype) {
					HierarchicalTWEANN ht = (HierarchicalTWEANN) net;
					Genotype<TWEANN> ghostNet = ht.getSubNetGenotype(GhostEatingNetworkBlock.GHOST_POOL);
					Genotype<TWEANN> pillNet = ht.getSubNetGenotype(PillEatingNetworkBlock.PILL_POOL);
					if (MMNEAT.pacmanInputOutputMediator instanceof CombiningInputOutputMediator) {
						// Evolve combining net with population of possible subnets
						((SubNetworkBlock) ((CombiningInputOutputMediator) MMNEAT.pacmanInputOutputMediator).blocks
								.get(GhostEatingNetworkBlock.GHOST_POOL)).changeNetwork(ghostNet.getPhenotype());
						((SubNetworkBlock) ((CombiningInputOutputMediator) MMNEAT.pacmanInputOutputMediator).blocks
								.get(PillEatingNetworkBlock.PILL_POOL)).changeNetwork(pillNet.getPhenotype());
						controller = new ReactiveNNPacManController(net);
					} else if (evolveNetworkSelector) {
						// Evolving a selector with a population of possible subnets
						// Assumes network is a TWEANN
						Genotype[] genotypes = new Genotype[] { ghostNet, pillNet };
						controller = new MultinetworkSelectorNetworkMsPacManController(ht, genotypes);
					}
				} else if (evolveNetworkSelector) {
					// Assumes network is a TWEANN
					if (MMNEAT.genotypeExamples == null) {
						// Evolving a selector with set subnetworks
						controller = new MultinetworkSelectorNetworkMsPacManController((TWEANN) net);
					} else {
						// Subnets come from coevolution task
						controller = new MultinetworkSelectorNetworkMsPacManController((TWEANN) net,
								CooperativeSubtaskSelectorMsPacManTask.subNetworks);
					}
				} else if (Parameters.parameters.booleanParameter("afterStates")) {
					// This should be controlled by a commandline parameter
					// controller = new DecisionPointAfterStateNNPacManController(net);
					controller = new ImmediateAfterStateNNPacManController(net);
				} else {
					controller = new ReactiveNNPacManController(net);
				}
			} catch (NoSuchMethodException e) {
				System.out.println("Classes mediators not loading correctly");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	/**
	 * returns the controller for this evolved pacMan
	 * 
	 * @return controller
	 */
	public NNPacManController getController() {
		return controller;
	}
}

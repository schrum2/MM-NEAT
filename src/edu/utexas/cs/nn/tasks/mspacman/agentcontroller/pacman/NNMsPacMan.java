package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.HierarchicalTWEANNGenotype;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.HierarchicalTWEANN;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.CooperativeCheckEachMultitaskSelectorMsPacManTask;
import edu.utexas.cs.nn.tasks.mspacman.CooperativeSubtaskSelectorMsPacManTask;
import edu.utexas.cs.nn.tasks.mspacman.sensors.ActionBlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.CombiningInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining.GhostEatingNetworkBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining.PillEatingNetworkBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining.SubNetworkBlock;

/**
 * Defines an evolved MsPacMan agent
 * @author Jacob Schrum 
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
     * method which defines the controller based on parameters and classOptions and the mediators
     * @param genotype
     */
    public NNMsPacMan(Genotype<T> genotype) {
        super(genotype);
        Network net = (Network) this.getGenotype().getPhenotype();
        boolean evolveNetworkSelector = Parameters.parameters.booleanParameter("evolveNetworkSelector");
        try {
            if (MMNEAT.pacmanInputOutputMediator instanceof ActionBlockLoadedInputOutputMediator) {
                controller = new NNActionPacManController(net);
            } else if (MMNEAT.pacmanInputOutputMediator instanceof VariableDirectionBlockLoadedInputOutputMediator) {
                if (MMNEAT.sharedMultitaskNetwork != null) {
                    // All individuals define preference usage for the same shared Multitask policy network
                    controller = new NNMultitaskSelectorCheckEachDirectionPacManController(MMNEAT.sharedMultitaskNetwork, genotype, MMNEAT.directionalSafetyFunction);
                } else if(MMNEAT.sharedPreferenceNetwork != null) {
                    // All individuals use same preference net for different evolved Multitask policy networks
                    controller = new NNMultitaskSelectorCheckEachDirectionPacManController(genotype, MMNEAT.sharedPreferenceNetwork, MMNEAT.directionalSafetyFunction);
                } else if(CooperativeCheckEachMultitaskSelectorMsPacManTask.multitaskGenotype != null) {
                    controller = new NNMultitaskSelectorCheckEachDirectionPacManController(CooperativeCheckEachMultitaskSelectorMsPacManTask.multitaskGenotype, genotype, MMNEAT.directionalSafetyFunction);
                } else {
                    controller = new NNCheckEachDirectionPacManController(genotype, MMNEAT.directionalSafetyFunction);
                }
            } else if (genotype instanceof HierarchicalTWEANNGenotype) {
                HierarchicalTWEANN ht = (HierarchicalTWEANN) net;
                Genotype<TWEANN> ghostNet = ht.getSubNetGenotype(GhostEatingNetworkBlock.GHOST_POOL);
                //System.out.println("ghostNet = " + ghostNet.getId());
                Genotype<TWEANN> pillNet = ht.getSubNetGenotype(PillEatingNetworkBlock.PILL_POOL);
                //System.out.println("pillNet = " + pillNet.getId());
                if (MMNEAT.pacmanInputOutputMediator instanceof CombiningInputOutputMediator) {
                    // Evolve combining net with population of possible subnets
                    ((SubNetworkBlock) ((CombiningInputOutputMediator) MMNEAT.pacmanInputOutputMediator).blocks.get(GhostEatingNetworkBlock.GHOST_POOL)).changeNetwork(ghostNet.getPhenotype());
                    ((SubNetworkBlock) ((CombiningInputOutputMediator) MMNEAT.pacmanInputOutputMediator).blocks.get(PillEatingNetworkBlock.PILL_POOL)).changeNetwork(pillNet.getPhenotype());
                    controller = new ReactiveNNPacManController(net);
                } else if (evolveNetworkSelector) {
                    // Evolving a selector with a population of possible subnets
                    // Assumes network is a TWEANN
                    Genotype[] genotypes = new Genotype[]{ghostNet, pillNet};
                    controller = new MultinetworkSelectorNetworkMsPacManController(ht, genotypes);
                }
            } else if (evolveNetworkSelector) {
                // Assumes network is a TWEANN
                if (MMNEAT.genotypeExamples == null) {
                    // Evolving a selector with set subnetworks
                    controller = new MultinetworkSelectorNetworkMsPacManController((TWEANN) net);
                } else {
                    // Subnets come from coevolution task
                    controller = new MultinetworkSelectorNetworkMsPacManController((TWEANN) net, CooperativeSubtaskSelectorMsPacManTask.subNetworks);
                }
            } else if (Parameters.parameters.booleanParameter("afterStates")) {
                // This should be controlled by a commandline parameter
                //controller = new DecisionPointAfterStateNNPacManController(net);
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

    /**
     * returns the controller for this evolved pacMan
     * @return controller
     */
    public NNPacManController getController() {
        return controller;
    }
}

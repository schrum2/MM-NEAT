package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.multitask.NetworkModeSelector;
import edu.utexas.cs.nn.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.utexas.cs.nn.util.ClassCreation;

/**
 *
 * @author Jacob Schrum
 */
public class MultinetworkSelectorNetworkMsPacManController extends MultinetworkSelectorMsPacManController<TWEANN> {

    public MultinetworkSelectorNetworkMsPacManController(TWEANN nn) throws NoSuchMethodException {
        this(nn, new Genotype[]{
                    EvolutionaryHistory.getSubnetwork(Parameters.parameters.stringParameter("ghostEatingSubnetwork")),
                    EvolutionaryHistory.getSubnetwork(Parameters.parameters.stringParameter("pillEatingSubnetwork"))});
    }

    public MultinetworkSelectorNetworkMsPacManController(TWEANN nn, Genotype[] genotypes) throws NoSuchMethodException {
        super(genotypes,
                MMNEAT.coevolutionMediators != null ? // For backwards compatibility 
                MMNEAT.coevolutionMediators
                : new MsPacManControllerInputOutputMediator[]{(MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacManMediatorClass1"), (MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacManMediatorClass2")},
                new NetworkModeSelector<TWEANN>(nn));
        for (Network n : networks) {
            ((TWEANN) n).canDraw = false;
        }
    }

    @Override
    public int getAction(GameFacade game, long timeDue) {
        // Done to keep mediator updated
        MMNEAT.pacmanInputOutputMediator.mediatorStateUpdate(game);
        int action = super.getAction(game, timeDue);
        return action;
    }

    @Override
    public void reset() {
        super.reset();
        this.ms.reset();
    }
}

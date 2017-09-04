package edu.southwestern.tasks.mspacman.agentcontroller.pacman;

import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.multitask.NetworkModeSelector;
import edu.southwestern.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.southwestern.util.ClassCreation;

/**
 *
 * @author Jacob Schrum
 */
public class MultinetworkSelectorNetworkMsPacManController extends MultinetworkSelectorMsPacManController<TWEANN> {

	public MultinetworkSelectorNetworkMsPacManController(TWEANN nn) throws NoSuchMethodException {
		this(nn, new Genotype[] {
				EvolutionaryHistory.getSubnetwork(Parameters.parameters.stringParameter("ghostEatingSubnetwork")),
				EvolutionaryHistory.getSubnetwork(Parameters.parameters.stringParameter("pillEatingSubnetwork")) });
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MultinetworkSelectorNetworkMsPacManController(TWEANN nn, Genotype[] genotypes) throws NoSuchMethodException {
		super(genotypes,
				MMNEAT.coevolutionMediators != null ? // For backwards
														// compatibility
						MMNEAT.coevolutionMediators
						: new MsPacManControllerInputOutputMediator[] {
								(MsPacManControllerInputOutputMediator) ClassCreation
										.createObject("pacManMediatorClass1"),
								(MsPacManControllerInputOutputMediator) ClassCreation
										.createObject("pacManMediatorClass2") },
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

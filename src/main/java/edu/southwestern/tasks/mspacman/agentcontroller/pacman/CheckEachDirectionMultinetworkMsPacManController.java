package edu.southwestern.tasks.mspacman.agentcontroller.pacman;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.multitask.MsPacManModeSelector;
import edu.southwestern.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;

public class CheckEachDirectionMultinetworkMsPacManController<T extends Network> extends NNDirectionalPacManController {

	protected NNCheckEachDirectionPacManController[] agents;

	public CheckEachDirectionMultinetworkMsPacManController(Genotype<T>[] genotypes,
			MsPacManControllerInputOutputMediator[] inputMediators, MsPacManModeSelector modeSelector) {
		super(genotypes[0].getPhenotype());
		// The main mediator cannot be null, since this causes problems with
		// reset ... messy code
		this.inputMediator = inputMediators[0]; // Problematic?
		assert(genotypes.length == inputMediators.length) : "Genotypes length and mediators length are not the same";
		agents = new NNCheckEachDirectionPacManController[genotypes.length];
		for (int i = 0; i < genotypes.length; i++) {
			agents[i] = new NNCheckEachDirectionPacManController(genotypes[i], MMNEAT.directionalSafetyFunction);
			assert inputMediators[i] != null : "Mediator " + i + " is null!";
			agents[i].inputMediator = inputMediators[i];
		}
		// for (NNCheckEachDirectionPacManController c : agents) {
		// ((TWEANN) c.nn).canDraw = false;
		// }
		ms = modeSelector;
	}

	@Override
	public double[] getDirectionPreferences(GameFacade gf) {
		double[][] allActionPreferences = new double[agents.length][];
		for (int i = 0; i < agents.length; i++) {
			allActionPreferences[i] = agents[i].getDirectionPreferences(gf);
		}
		ms.giveGame(gf);
		int mode = ms.mode();
		return allActionPreferences[mode];
	}

	@Override
	public void reset() {
		super.reset();
		for (int i = 0; i < agents.length; i++) {
			agents[i].reset();
		}
	}
}

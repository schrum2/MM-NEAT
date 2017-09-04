package edu.southwestern.tasks.mspacman.agentcontroller.pacman;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.tasks.mspacman.ensemble.MsPacManEnsembleArbitrator;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.southwestern.util.CombinatoricUtilities;
import java.awt.Graphics2D;

@SuppressWarnings("rawtypes")
public class EnsembleMsPacManController<T extends Network> extends MultinetworkMsPacManController {

	private MsPacManEnsembleArbitrator arbitrator;

	@SuppressWarnings("unchecked")
	public EnsembleMsPacManController(Genotype<T>[] genotypes, MsPacManControllerInputOutputMediator[] inputMediators) {
		super(genotypes, inputMediators);
		this.arbitrator = MMNEAT.ensembleArbitrator;
	}

	protected void drawModeUsage(GameFacade game, Graphics2D g, int[] actions, int action, int currentScale, int mode) {
		for (int i = 0; i < actions.length; i++) {
			// Draw dot for each net winner
			if (actions[i] == action) {
				g.setColor(CombinatoricUtilities.colorFromInt(i));
				g.fillRect(currentScale, scaledMode(i), MODE_DOT_DIM, MODE_DOT_DIM);
			}
		}
	}

	protected int[][] getAllControllerActions(GameFacade game, long timeDue) {
		// Determine each net's preferences and action
		int[] actions = new int[inputMediators.length];
		double[][] preferences = new double[inputMediators.length][];
		for (int i = 0; i < inputMediators.length; i++) { // for each network
			nn = networks[i];
			inputMediator = inputMediators[i];
			preferences[i] = getDirectionPreferences(game); // get four
															// direction
															// preferences
			actions[i] = directionFromPreferences(preferences[i]);
		}

		// ensemble picks one action
		int action = arbitrator.choose(game, preferences);
		// winners agreement tracked
		for (int i = 0; i < actions.length; i++) {
			if (actions[i] == action) {
				usage[i]++;
			}
		}

		int[][] result = new int[3][];
		result[0] = new int[] { action };
		result[1] = new int[] { -1 };
		result[2] = actions;
		return result;
	}
}

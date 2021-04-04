package edu.southwestern.tasks.mspacman.agentcontroller.pacman;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.Organism;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.sensors.ActionBlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;

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
	public NNMsPacMan(Genotype<T> genotype) {
		super(genotype);
		if(CommonConstants.hyperNEAT) {
			controller = new NNHyperNEATPacManController((HyperNEATCPPNGenotype) genotype);
		} else {
			Network net = (Network) this.getGenotype().getPhenotype();
			if (MMNEAT.pacmanInputOutputMediator instanceof ActionBlockLoadedInputOutputMediator) {
				controller = new NNActionPacManController(net);
			} else if (MMNEAT.pacmanInputOutputMediator instanceof VariableDirectionBlockLoadedInputOutputMediator) {
				controller = new NNCheckEachDirectionPacManController(genotype, MMNEAT.directionalSafetyFunction);
			} else if (Parameters.parameters.booleanParameter("afterStates")) {
				controller = new ImmediateAfterStateNNPacManController(net);
			} else {
				controller = new ReactiveNNPacManController(net);
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

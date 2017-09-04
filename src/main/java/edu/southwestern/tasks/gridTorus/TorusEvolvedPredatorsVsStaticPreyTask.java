package edu.southwestern.tasks.gridTorus;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.gridTorus.controllers.TorusPredPreyController;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;

/**
 * The following class sets up tasks for
 * learning agents and NPCs. This class is for a task where the
 * predators are evolved while the prey are kept static
 * 
 * @author Alex Rollins, Jacob Schrum 
 * @param <T> evolved phenotype
 */
public class TorusEvolvedPredatorsVsStaticPreyTask<T extends Network> extends TorusPredPreyTask<T> {

	private TorusPredPreyController[] staticAgents = null;

	/**
	 * constructor for a task where the predators are evolved while the prey are
	 * kept static sends false to the parent constructor, indicating that the
	 * predator is the agent evolving Includes all of the fitness scores that
	 * the user wants from the command line parameters
	 */
	public TorusEvolvedPredatorsVsStaticPreyTask() {
		super(false);
		addAllObjectives();
	}

	/**
	 * A method that gives a list of controllers for the evolving agents
	 * (predators) The predators are all defined as a new agent of the given
	 * genotype with an evolved controller The user also indicates in a command
	 * line parameter how many predators there will be (default of 3)
	 * 
	 * @return evolvedAgents a list of controllers for the evolved agents for
	 *         this class, which is the predators
	 * @param individual
	 *            the genotype that will be given to all predator agents
	 *            (homogeneous team)
	 */
	@Override
	public TorusPredPreyController[] getPredAgents(Genotype<T> individual) {
		evolved = new TorusPredPreyController[Parameters.parameters.integerParameter("torusPredators")];
		getEvolvedControllers(evolved, individual, true);
		return evolved;
	}

	@Override
	/**
	 * A method that gives a list of controllers for the static agents (prey)
	 * The prey are all given a simple, non-evolving controller (specified by
	 * user) The user also indicates in a command line parameter how many prey
	 * there will be (default of 2)
	 * 
	 * @return staticAgents a list of controllers for the static agents for this
	 *         class, which is the prey (static meaning the agent type that is
	 *         chosen by the user to not evolve)
	 * @param individual
	 *            the genotype that will be given to all prey agents
	 *            (homogeneous team)
	 */
	public TorusPredPreyController[] getPreyAgents(Genotype<T> individual) {
		if (staticAgents == null)
			staticAgents = getStaticControllers(false,Parameters.parameters.integerParameter("torusPreys"));

		return staticAgents;
	}
}

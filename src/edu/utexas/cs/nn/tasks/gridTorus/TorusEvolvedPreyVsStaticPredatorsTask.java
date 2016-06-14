
package edu.utexas.cs.nn.tasks.gridTorus;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;

/**
 * The following class sets up tasks for
 * learning agents and NPCs. This class is for a task where the prey are
 * evolved while the predators are kept static
 * 
 * @author Alex Rollins, Jacob Schrum 
 * @param <T> evolved phenotype
 */
public class TorusEvolvedPreyVsStaticPredatorsTask<T extends Network> extends TorusPredPreyTask<T> {

	private TorusPredPreyController[] staticAgents = null;

	/**
	 * constructor for a task where the prey are evolved while the predators are
	 * kept static sends true to the parent constructor, indicating that the
	 * prey is the agent evolving Includes all of the fitness scores that the
	 * user wants from the command line parameters
	 */
	public TorusEvolvedPreyVsStaticPredatorsTask() {
		super(true);
		addAllObjectives();
	}

	@Override
	/**
	 * A method that gives a list of controllers for the static agents
	 * (predators) The predators are all given the simple, non-evolving
	 * controller (specified by user) The user also indicates in a command line
	 * parameter how many predators there will be (default of 4)
	 * 
	 * @return staticAgents a list of controllers for the static agents for this
	 *         class, which is the predators (static meaning the agent type that
	 *         is chosen by the user to not evolve)
	 * @param individual
	 *            the genotype that will be given to all predator agents
	 *            (homogeneous team)
	 */
	public TorusPredPreyController[] getPredAgents(Genotype<T> individual) {
		if (staticAgents == null)
			staticAgents = getStaticControllers(true,Parameters.parameters.integerParameter("torusPredators"));

		return staticAgents;
	}

	@Override
	/**
	 * A method that gives a list of controllers for the evolving agents (prey)
	 * The prey are all defined as a new agent of the given genotype with an
	 * evolved controller The user also indicates in a command line parameter
	 * how many prey there will be (default of 1)
	 * 
	 * @return evolvedAgents a list of controllers for the evolved agents for
	 *         this class, which is the prey
	 * @param individual
	 *            the genotype that will be given to all prey agents
	 *            (homogeneous team)
	 */
	public TorusPredPreyController[] getPreyAgents(Genotype<T> individual) {
		evolved = new TorusPredPreyController[Parameters.parameters.integerParameter("torusPreys")];
		getEvolvedControllers(evolved, individual, false);
		return evolved; 
	}
}

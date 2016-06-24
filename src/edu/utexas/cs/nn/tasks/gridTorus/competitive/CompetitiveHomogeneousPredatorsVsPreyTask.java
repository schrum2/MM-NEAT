package edu.utexas.cs.nn.tasks.gridTorus.competitive;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.TorusPredPreyTask;
import edu.utexas.cs.nn.tasks.gridTorus.GroupTorusPredPreyTask;

/**
 * The following class sets up tasks for
 * learning agents and NPCs. This class is for a task where the
 * predators are evolved while the prey are also evolved
 * (competitive coevolution)
 * 
 * There are a total of two populations: 1 prey pop, 1 pred pop
 * Homogeneous teams with copies of genotypes/networks to each team member
 * 
 * @author rollinsa
 */
public class CompetitiveHomogeneousPredatorsVsPreyTask<T extends Network> extends GroupTorusPredPreyTask<T> {

	/**
	 * constructor for a task where the predators are evolved while the prey are
	 * also evolved. Calls parent constructor with no parameters, indicating that the
	 * predator and the prey agents are both evolving. Includes all of the fitness scores that
	 * the user wants from the command line parameters
	 */
	public CompetitiveHomogeneousPredatorsVsPreyTask() {
		super();
		task.evolved = new TorusPredPreyController[Parameters.parameters.integerParameter("torusPredators") + Parameters.parameters.integerParameter("torusPreys")];
	}

	/**
	 * A method that gives a list of controllers for the evolving agents
	 * (predators) The predators are all defined as a new agent of the given
	 * genotype with an evolved controller The user also indicates in a command
	 * line parameter how many predators there will be (default of 3)
	 *  
	 * @return evolvedAgents a list of controllers for the evolved agents for
	 *         this class, which is both prey and predators
	 * @param team, 
	 *            the genotype that will be given to all predator agents
	 *            in index 0
	 *            (homogeneous team)
	 */
	@Override
	public TorusPredPreyController[] getPredAgents(Genotype<T>[] team) {
		TorusPredPreyTask.getEvolvedControllers(task.evolved, team[TorusPredPreyGame.AGENT_TYPE_PRED], true, 0, Parameters.parameters.integerParameter("torusPredators"));
		// Make smaller array to return just the preds
		TorusPredPreyController[] predOnly = new TorusPredPreyController[Parameters.parameters.integerParameter("torusPredators")];
		System.arraycopy(task.evolved, 0, predOnly, 0, Parameters.parameters.integerParameter("torusPredators"));
		return predOnly;
	}

	@Override
	/**
	 * A method that gives a list of controllers for the evolving agents (prey)
	 * The prey are all defined as a new agent of the given genotype with an
	 * evolved controller The user also indicates in a command line parameter
	 * how many prey there will be (default of 2)
	 * 
	 * @return evolvedAgents a list of controllers for the evolved agents for
	 *         this class, which is both prey and predators
	 * @param team, 
	 *            the genotype that will be given to all prey agents
	 *            in index 1
	 *            (homogeneous team)
	 */
	public TorusPredPreyController[] getPreyAgents(Genotype<T>[] team) {
		TorusPredPreyTask.getEvolvedControllers(task.evolved, team[TorusPredPreyGame.AGENT_TYPE_PREY], false, Parameters.parameters.integerParameter("torusPredators"), Parameters.parameters.integerParameter("torusPreys"));
		// Make smaller array to return just the preys
		TorusPredPreyController[] preyOnly = new TorusPredPreyController[Parameters.parameters.integerParameter("torusPreys")];
		System.arraycopy(task.evolved, Parameters.parameters.integerParameter("torusPredators"), preyOnly, 0, Parameters.parameters.integerParameter("torusPreys"));
		return preyOnly; 
	}

	@Override
	/**
	 * gets and returns the task instance for competitive coevolution
	 * @return task, torusPredPreyTask instance
	 */
	public TorusPredPreyTask<T> getLonerTaskInstance() {
		if(task == null) {
			// Anonymous class
			task = new TorusPredPreyTask<T>(){

				// Not used by coevolution
				@Override
				public TorusPredPreyController[] getPredAgents(Genotype<T> individual) {
					throw new UnsupportedOperationException("The CompetitiveHomogeneousPredatorsVsPreyTask should not need the getPredAgents method of its LonerTask instance");
				}

				// Not used by coevolution
				@Override
				public TorusPredPreyController[] getPreyAgents(Genotype<T> individual) {
					throw new UnsupportedOperationException("The CompetitiveHomogeneousPredatorsVsPreyTask should not need the getPreyAgents method of its LonerTask instance");
				}
				
			};
			task.addAllObjectives(TorusPredPreyGame.AGENT_TYPE_PRED, false);
			task.addAllObjectives(TorusPredPreyGame.AGENT_TYPE_PREY, true);
		}
		return task;
	}
}
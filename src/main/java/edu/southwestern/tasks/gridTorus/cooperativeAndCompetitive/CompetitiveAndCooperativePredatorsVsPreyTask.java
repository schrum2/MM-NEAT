package edu.southwestern.tasks.gridTorus.cooperativeAndCompetitive;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.gridTorus.controllers.TorusPredPreyController;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gridTorus.GroupTorusPredPreyTask;
import edu.southwestern.tasks.gridTorus.TorusPredPreyTask;

/**
 * The following class sets up tasks for
 * learning agents and NPCs. This class is for a task where the
 * predators are evolved while the prey are also evolved
 * (competitive coevolution)
 * 
 * Each agent in each team has its own population/genotype/network.
 * 
 * @author rollinsa
 */
public class CompetitiveAndCooperativePredatorsVsPreyTask<T extends Network> extends GroupTorusPredPreyTask<T> {

	/**
	 * constructor for a task where the predators are evolved while the prey are
	 * also evolved. Calls parent constructor with no parameters, indicating that the
	 * predator and the prey agents are both evolving. Includes all of the fitness scores that
	 * the user wants from the command line parameters
	 */
	public CompetitiveAndCooperativePredatorsVsPreyTask() {
		super();
		task.evolved = new TorusPredPreyController[Parameters.parameters.integerParameter("torusPredators") + Parameters.parameters.integerParameter("torusPreys")];
	}

	/**
	 * A method that gives a list of controllers for the evolving agents
	 * (predators) The predators are all defined as a new agent of the given
	 * genotype with an evolved controller. The user also indicates in a command
	 * line parameter how many predators there will be (default of 3)
	 *  
	 * @return evolvedAgents a list of controllers for the evolved agents for
	 *         this class, which is both prey and predators
	 * @param team, each agent has its own genotype
	 */
	@Override
	public TorusPredPreyController[] getPredAgents(Genotype<T>[] team) {
		int numPreds = Parameters.parameters.integerParameter("torusPredators");
		Genotype[] predTeam = new Genotype[numPreds];
		//NOTE: Assumes that predators were stored first in the "team" list
		System.arraycopy(team, 0, predTeam, 0, numPreds);
		TorusPredPreyTask.getEvolvedControllers(task.evolved, predTeam, true, 0);
		// Make smaller array to return just the preds
		TorusPredPreyController[] predOnly = new TorusPredPreyController[numPreds];
		System.arraycopy(task.evolved, 0, predOnly, 0, numPreds);
		return predOnly;
	}

	@Override
	/**
	 * A method that gives a list of controllers for the evolving agents (prey)
	 * The prey are all defined as a new agent of the given genotype with an
	 * evolved controller. The user also indicates in a command line parameter
	 * how many prey there will be (default of 2)
	 * 
	 * @return evolvedAgents a list of controllers for the evolved agents for
	 *         this class, which is both prey and predators
	 * @param team, each agent has its own genotype
	 */
	public TorusPredPreyController[] getPreyAgents(Genotype<T>[] team) {
		int numPreys = Parameters.parameters.integerParameter("torusPreys");
		int numPreds = Parameters.parameters.integerParameter("torusPredators");
		Genotype[] preyTeam = new Genotype[numPreys];
		//NOTE: Assumes that predators were stored first in the "team" list, then prey
		System.arraycopy(team, numPreds, preyTeam, 0, numPreys);
		TorusPredPreyTask.getEvolvedControllers(task.evolved, preyTeam, false, numPreds);
		// Make smaller array to return just the preys
		TorusPredPreyController[] preyOnly = new TorusPredPreyController[numPreys];
		System.arraycopy(task.evolved, numPreds, preyOnly, 0, numPreys);
		return preyOnly; 
	}

	@Override
	/**
	 * gets and returns the task instance for competitive and cooperative coevolution
	 * @return task, torusPredPreyTask instance
	 */
	public TorusPredPreyTask<T> getLonerTaskInstance() {
		if(task == null) {
			// Anonymous class
			task = new TorusPredPreyTask<T>(){

				// Not used by coevolution
				@Override
				public TorusPredPreyController[] getPredAgents(Genotype<T> individual) {
					throw new UnsupportedOperationException("The CompetitiveAndCooperativePredatorsVsPreyTask should not need the getPredAgents method of its LonerTask instance");
				}

				// Not used by coevolution
				@Override
				public TorusPredPreyController[] getPreyAgents(Genotype<T> individual) {
					throw new UnsupportedOperationException("The CompetitiveAndCooperativePredatorsVsPreyTask should not need the getPreyAgents method of its LonerTask instance");
				}

			};
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPredators"); i++){
				task.addAllObjectives(i, false);
			}
			for(int i = 0; i < Parameters.parameters.integerParameter("torusPreys"); i++){
				task.addAllObjectives(i+Parameters.parameters.integerParameter("torusPredators"), true);
			}
		}
		return task;
	}
}

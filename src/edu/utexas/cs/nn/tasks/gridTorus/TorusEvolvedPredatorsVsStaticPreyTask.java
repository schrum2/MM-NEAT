package edu.utexas.cs.nn.tasks.gridTorus;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.gridTorus.controllers.FleeAllPreyController;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;

/**
 *
 * @author Rollinsa
 * The following class sets up tasks for learning agents and NPCs.
 * This class is for a task where the predators are evolved while the prey are kept static
 */
public class TorusEvolvedPredatorsVsStaticPreyTask<T extends Network> extends TorusPredPreyTask<T> {

	/**
	 * constructor for a task where the predators are evolved while the prey are kept static
	 * sends false to the parent constructor, indicating that the predator is the agent evolving
	 */
	public TorusEvolvedPredatorsVsStaticPreyTask() {
		super(false); 

		MMNEAT.registerFitnessFunction("Time Alive"); 
	}

	@Override
	/**
	 * A method that gives a list of controllers for the evolving agents (predators)
	 * The predators are all defined as a new agent of the given genotype with an evolved controller 
	 * The user also indicates in a command line parameter how many predators there will be (default of 4)
	 * @return evolvedAgents a list of controllers for the evolved agents for this class, which is the predators 
	 * @param individual the genotype that will be given to all predator agents (homogeneous team)
	 */
	public TorusPredPreyController[] getPredAgents(Genotype<T> individual) {
		int numPredators = Parameters.parameters.integerParameter("torusPredators");
		TorusPredPreyController[] evolvedAgents = new TorusPredPreyController[numPredators];    	
		for(int i = 0; i < numPredators; i++){
			//true to indicate that this is a predator
			evolvedAgents[i] = new NNTorusPredPreyAgent<T>(individual, true).getController(); 
		} 
		return evolvedAgents;
	}

	@Override
	/**
	 * A method that gives a list of controllers for the static agents (prey)
	 * The prey are all given the simple, non-evolving "FearfulPreyController" 
	 * The user also indicates in a command line parameter how many prey there will be (default of 1)
	 * @return staticAgents a list of controllers for the static agents for this class,
	 * which is the prey (static meaning the agent type that is chosen by the user to not evolve)
	 * @param individual the genotype that will be given to all prey agents (homogeneous team)
	 */
	public TorusPredPreyController[] getPreyAgents(Genotype<T> individual) {
		int numPrey = Parameters.parameters.integerParameter("torusPreys"); 
		TorusPredPreyController[] staticAgents = new TorusPredPreyController[numPrey];
		for(int i = 0; i < numPrey; i++) {
			staticAgents[i] = new FleeAllPreyController(); 
		}
		return staticAgents;
	}
}


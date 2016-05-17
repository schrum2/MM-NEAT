
package edu.utexas.cs.nn.tasks.gridTorus;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.PreyMaximizeGameTime;
import edu.utexas.cs.nn.util.ClassCreation;

/**
 *
 * @author Alex Rollins, Jacob Schrum
 * The following class sets up tasks for learning agents and NPCs.
 * This class is for a task where the prey are evolved while the predators are kept static
 */
public class TorusEvolvedPreyVsStaticPredatorsTask<T extends Network> extends TorusPredPreyTask<T> {

	private TorusPredPreyController[] staticAgents = null;

	/**
	 * constructor for a task where the prey are evolved while the predators are kept static
	 * sends true to the parent constructor, indicating that the prey is the agent evolving
	 * Includes all of the fitness scores that the user wants from the command line parameters
	 */
	public TorusEvolvedPreyVsStaticPredatorsTask() {
		super(true); 
		if(Parameters.parameters.booleanParameter("PreyMaximizeTotalTime"))
			addObjective(new PreyMaximizeGameTime<T>(), objectives);
	}

	@Override
	/**
	 * A method that gives a list of controllers for the static agents (predators)
	 * The predators are all given the simple, non-evolving controller (specified by user)
	 * The user also indicates in a command line parameter how many predators there will be (default of 4)
	 * @return staticAgents a list of controllers for the static agents for this class,
	 * which is the predators (static meaning the agent type that is chosen by the user to not evolve)
	 * @param individual the genotype that will be given to all predator agents (homogeneous team)
	 */
	public TorusPredPreyController[] getPredAgents(Genotype<T> individual) {
		if(staticAgents == null){
			int numPredators = Parameters.parameters.integerParameter("torusPredators"); 
			staticAgents = new TorusPredPreyController[numPredators];
			for(int i = 0; i < numPredators; i++) {
				try {
					staticAgents[i] = (TorusPredPreyController) ClassCreation.createObject("staticPredatorController");
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
					System.out.println("Could not load static prey");
					System.exit(1);
				} 
			}
		}
		return staticAgents;
	}

	@Override
	/**
	 * A method that gives a list of controllers for the evolving agents (prey)
	 * The prey are all defined as a new agent of the given genotype with an evolved controller 
	 * The user also indicates in a command line parameter how many prey there will be (default of 1)
	 * @return evolvedAgents a list of controllers for the evolved agents for this class, which is the prey 
	 * @param individual the genotype that will be given to all prey agents (homogeneous team)
	 */
	public TorusPredPreyController[] getPreyAgents(Genotype<T> individual) {
		int numPrey = Parameters.parameters.integerParameter("torusPreys");
		TorusPredPreyController[] evolvedAgents = new TorusPredPreyController[numPrey];    	
		for(int i = 0; i < numPrey; i++){
			//false to indicate that this is not a predator, but a prey
			evolvedAgents[i] = new NNTorusPredPreyAgent<T>(individual, false).getController(); 
		}
		return evolvedAgents;
	}
}

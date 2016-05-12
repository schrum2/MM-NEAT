package edu.utexas.cs.nn.tasks.gridTorus;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.gridTorus.controllers.FearfulPreyController;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;

/**
 *
 * @author Rollinsa
 * The following class sets up tasks for learning agents and NPCs.
 */
public class TorusEvolvedPredatorsVsStaticPreyTask<T extends Network> extends TorusPredPreyTask<T> {

	public TorusEvolvedPredatorsVsStaticPreyTask() {
		super(false); 

		MMNEAT.registerFitnessFunction("Time Alive"); 
	}

	@Override
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
	public TorusPredPreyController[] getPreyAgents(Genotype<T> individual) {
		int numPrey = Parameters.parameters.integerParameter("torusPreys"); 
		TorusPredPreyController[] staticAgents = new TorusPredPreyController[numPrey];
		for(int i = 0; i < numPrey; i++) {
			staticAgents[i] = new FearfulPreyController(); 
		}
		return staticAgents;
	}
}


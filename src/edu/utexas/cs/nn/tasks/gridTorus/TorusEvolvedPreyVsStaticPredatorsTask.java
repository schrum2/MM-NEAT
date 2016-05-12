
package edu.utexas.cs.nn.tasks.gridTorus;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.gridTorus.controllers.AggressivePredatorController;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;

/**
 *
 * @author Rollinsa
 * The following class sets up tasks for learning agents and NPCs.
 */
public class TorusEvolvedPreyVsStaticPredatorsTask<T extends Network> extends TorusPredPreyTask<T> {

	public TorusEvolvedPreyVsStaticPredatorsTask() {
		super(true); 

		MMNEAT.registerFitnessFunction("Time Alive"); 
	}

	@Override
	public TorusPredPreyController[] getPredAgents(Genotype<T> individual) {
		int numPredators = Parameters.parameters.integerParameter("torusPredators"); 
		TorusPredPreyController[] staticAgents = new TorusPredPreyController[numPredators];
		for(int i = 0; i < numPredators; i++) {
			staticAgents[i] = new AggressivePredatorController(); 
		}
		return staticAgents;	
	}

	@Override
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

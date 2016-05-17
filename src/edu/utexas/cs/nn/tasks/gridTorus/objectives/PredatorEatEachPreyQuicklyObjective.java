package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;

public class PredatorEatEachPreyQuicklyObjective<T extends Network> extends GridTorusObjective<T> {

	@Override
	/**
	 * reward the predators for each prey that gets eaten
	 * heavily encourages that the prey are eaten as quickly as possible
	 */
	public double fitness(Organism<T> individual) {
		int numPrey = Parameters.parameters.integerParameter("torusPreys");
		double score = 0;
		//get the death time of each prey and subtract that from the score so that quicker death times are encouraged
		for(int i = 0; i < numPrey; i++){
			score -= game.getDeathTime(i);
		}
		return score;
	}
	
	@Override
	/**
	 * worst possible score for a predator is the full game time 
	 * multiplied by how many prey there are (no prey eaten at all)
	 */
	public double minScore(){
		return -Parameters.parameters.integerParameter("torusTimeLimit") * Parameters.parameters.integerParameter("torusPreys");
	}
}


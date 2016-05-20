package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.networks.Network;

/**
 * 
 * @author rollinsa
 * 	
 * Find the score of the predator based on how many prey died
 */
public class PredatorCatchObjective <T extends Network> extends GridTorusObjective<T>{

	public static final double NO_PREY_SCORE = 25;
	
	@Override
	/**
	 * Find the score of the predator based on how many prey died
	 */
	public double fitness(Organism<T> individual) {
		
		TorusAgent[] prey = game.getPrey();
		int numPrey = prey.length;
		double numCaught = 0;
		
		//get number of caught prey
		for(TorusAgent p : prey){
			if(p == null){
				numCaught++;
			}
		}
		
		//return a score based on percentage of caught prey
		return (((double)numCaught)/((double)numPrey)) * NO_PREY_SCORE;	
	}

}
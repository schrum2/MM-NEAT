package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;

/**
 * 
 * @author rollinsa
 * 
 * Encourages catching all prey with a very high score for doing so
 * if the prey aren't all caught, this fitness function will emphasize that as many prey are caught
 * as possible, and if none are caught then it minimizes distance to the prey
 */
public class PredatorCatchCloseObjective <T extends Network> extends GridTorusObjective<T>{

	public static final double NO_PREY_SCORE = 25;
	
	@Override
	/**
	 * Find the score of the predator based on if all prey died or not
	 */
	public double fitness(Organism<T> individual) {
		
		TorusAgent[] prey = game.getPrey();
		TorusAgent[] preds = game.getPredators();
		int numPrey = prey.length;
		int numPreds = preds.length;
		double numCaught = 0;
		
		for(TorusAgent p : prey){
			if(p == null){
				numCaught++;
			}
		}
		//when all prey have been caught. Best possible score
		if(numCaught == numPrey)
			return NO_PREY_SCORE;
		
		double sumOfDistances = PredatorMinimizeDistanceFromPreyObjective.sumOfPredToPreyDistances(preds, prey);
		int height = Parameters.parameters.integerParameter("torusYDimensions");
		int width = Parameters.parameters.integerParameter("torusXDimensions");
		//max possible distance is the sum of half the world height and width because the world wraps around
		double maxDistance = height/2 + width/2;
		//d = the normalized sum of distances from the predator to each prey at the end of the simulation
		double d = sumOfDistances / (numPrey * numPreds * maxDistance);

		assert 0 <= d : "normalized distance less than 0! " + d;
		assert 1 >= d : "normalized distance greater than 1! " + d;
		
		//make d essentially its inverse so that less distance is encouraged
		d = 1 - d;
		double normalizedCaught = (numCaught/numPrey);
		
		//divided by three because there are two factors in the equation (%caught and distance) and the %caught is multiplied
		//by two so that it is weighted twice as much as the distance to the prey and this whole score 
		//needs to be less than the maximum score, NO_PREY_SCORE, which is given when all prey are caught
		double WEIGHT = (NO_PREY_SCORE/3.0); 
		
		return d*WEIGHT + 2*normalizedCaught*WEIGHT;	
	}

}
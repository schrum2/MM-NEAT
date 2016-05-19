package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;

/**
 * 
 * @author rollinsa
 * The predator fitness function appearing in Constructing Competitive and Cooperative Agent Behavior Using Coevolution
 * By Aditya Rawal, Padmini Rajagopalan, and Risto Miikkulainen
 * http://nn.cs.utexas.edu/?rawal:cig10
 * 
 * Predator fitness score:
 * 25 ------ if both prey caught
 * (20c / n) + (2c) + (20d / n) ------ otherwise
 * c = number of prey caught
 * n = total number of prey
 * d = normalized sum of distances from the predator to each prey at the end of the simulation
 * 
 * Encourages catching all prey with an emphasis on being as close as possible in distance to catching those that weren't caught
 */
public class PredatorRawalRajagopalanMiikkulainenObjective <T extends Network> extends GridTorusObjective<T>{

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
		
		//because of the equation for the fitness function given by the RRM paper, there must be two prey
		if(numPrey != 2){
			throw new IllegalArgumentException("The number of prey must be two for this fitness function");
		}
		
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
		
		double WEIGHT = (NO_PREY_SCORE * (4/5));
		//now we have d = d, n = numPrey, c = numCaught
		//------ (20c / n) + (2c) + (20d / n) ------ (generalized for variable number of prey/preds)
		return ((WEIGHT * numCaught) / numPrey) + (2 * numCaught) + ((WEIGHT * d) / numPrey);
	}

}

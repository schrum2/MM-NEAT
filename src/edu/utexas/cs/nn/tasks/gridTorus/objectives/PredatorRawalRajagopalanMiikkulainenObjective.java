package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

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
public class PredatorRawalRajagopalanMiikkulainenObjective <T extends Network> extends GridTorusObjective<T> {


	public static final double NO_PREY_SCORE = 25;

	@Override
	/**
	 * Find the score of the predator based on if all prey died or not
	 */
	public double fitness(Organism<T> individual) {
		TorusAgent[] prey = game.getPrey();
		int numPrey = prey.length;
		double numCaught = 0;
		for(TorusAgent p : prey){
			if(p == null){
				numCaught++;
			}
		}
		//when all prey have been caught. Best possible score
		if(numCaught == numPrey)
			return NO_PREY_SCORE;

		TorusAgent[] preds = game.getPredators();
		//totalScore will hold the sum of the fitnesses of each individual predator
		double totalScore = 0;
		for(int i = 0; i < preds.length; i++){
			totalScore += getFitness(preds[i], numCaught, numPrey);
		}
		return totalScore;

	}

	/**
	 * utility function used to find the score of a single predator when not all prey have been caught
	 * @param pred predator currently being evaluated
	 * @param numCaught number of caught prey
	 * @param numPrey total number of prey
	 * @return the fitness of this predator
	 */
	public double getFitness(TorusAgent pred, double numCaught, int numPrey){
		//NO_PREY_SCORE*(numCaught / numPrey)
		TorusAgent[] prey = game.getPrey();

		//otherwise

		//find d, the normalized sum of distances from the predator to each prey at the end of the simulation
		//this array holds the distances to all prey from this predator
		double[] distances = new double[numPrey];
		distances = pred.distances(prey);
		//this is the sum of distances from the predator to each prey at the end of the simulation
		double sumOfDists = 0;
		for(int i = 0; i < numPrey; i++){
			if(prey[i] != null)
				sumOfDists += distances[i];
		}
		int height = Parameters.parameters.integerParameter("torusYDimensions");
		int width = Parameters.parameters.integerParameter("torusXDimensions");
		//max possible distance is the sum of half the world height and width because the world wraps around
		double maxDistance = height/2 + width/2;
		//d = the normalized sum of distances from the predator to each prey at the end of the simulation
		double d = ((sumOfDists / numPrey) / maxDistance);

		//now we have d = d, n = numPrey, c = numCaught
		//------ (20c / n) + (2c) + (20d / n) ------ (generalized for variable number of prey/preds)
		return ((((4/5) * NO_PREY_SCORE) * numCaught) / numPrey) + (2 * numCaught) + ((((4/5) * NO_PREY_SCORE) * d) / numPrey);
	}

	@Override
	/**
	 * worst possible score for the predator is when no prey have died and the prey are all max distance away 
	 * (20c / n) + (2c) + (20d / n) ------ becomes 0 + 0 + (20 / n)
	 * c = number of prey caught
	 * n = total number of prey
	 * d = normalized sum of distances from the predator to each prey at the end of the simulation
	 */
	public double minScore(){
		return ((4/5) * NO_PREY_SCORE) / Parameters.parameters.integerParameter("torusPreys");
	}


}

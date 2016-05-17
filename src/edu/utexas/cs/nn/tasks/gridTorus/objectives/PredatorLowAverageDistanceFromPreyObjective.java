package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.networks.Network;

public class PredatorLowAverageDistanceFromPreyObjective<T extends Network> extends GridTorusObjective<T> {

	@Override
	/**
	 * reward the predators for being as close to the prey as possible as often as possible
	 * by encouraging a low average distance from the prey
	 * if the prey are dead then distance from that prey is counted as zero (meaning that the
	 * predators are also inherently encouraged to eat the prey)
	 */
	public double fitness(Organism<T> individual) {
		//find avg distance from prey for each pred and add the sum to a fitness score
		return 0;
	}
	
	@Override
	/**
	 * worst possible score for a predator is the maximum possible distance from the prey
	 * This is simply half the world height and width because the world wraps around
	 */
	public double minScore(){
		return (game.getWorld().height()/2) + (game.getWorld().width()/2);
	}
}
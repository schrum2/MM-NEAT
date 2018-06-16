package edu.southwestern.tasks.ut2004.fitness;

import edu.southwestern.evolution.Organism;
import edu.southwestern.networks.Network;

/**
 * Measures how high the bot's team can score
 * @author Jacob Schrum
 */
public class TeamScoreFitness<T extends Network> extends UT2004FitnessFunction<T> {

	/**
	 * @return returns the bot's team's score for the game
	 */
	public double fitness(Organism<T> individual) {
		return game.getAgentStats().getTeamScore(); 
	}
}

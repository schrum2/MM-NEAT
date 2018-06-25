package edu.southwestern.tasks.ut2004;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.ut2004.controller.BotController;
import edu.southwestern.tasks.ut2004.fitness.*;
import edu.southwestern.util.ClassCreation;

/**
 * Evolves the bot using the shock rifle against the dummy bots
 * @author Jacob Schrum
 * @param <T> Evolved phenotype
 */
public class UT2004ShockRifleVsGBBotsDeathMatchTask<T extends Network> extends UT2004Task<T> {

	/**
	 * sets the parameters for the server and evaluation 
	 */
	public UT2004ShockRifleVsGBBotsDeathMatchTask() {
		this(Parameters.parameters.stringParameter("utMap"),
				getOpponents(Parameters.parameters.integerParameter("utNumOpponents")),
				Parameters.parameters.integerParameter("utEvalMinutes"),
				Parameters.parameters.integerParameter("utEvolvingBotSkill"));
	}

	/**
	 * creates an array of the bot's opponents
	 * @param num (the address in the array where the opponent will be asigned)
	 * @return returns the array of opponents
	 */
	public static BotController[] getOpponents(int num) {
		BotController[] result = new BotController[num];
		try {
			for (int i = 0; i < num; i++) {
				result[i] = (BotController) ClassCreation.createObject("utGameBotsOpponent");
			}
		} catch (NoSuchMethodException ex) {
			System.out.println("Could not load bot opponents");
			ex.printStackTrace();
			System.exit(1);
		}
		return result;
	}

	/**
	 * sets up the server and the match
	 * @param map (the map that will be used)
	 * @param opponents (the array of opponents)
	 * @param evalMinutes (how long the eval will last)
	 * @param desiredSkill (the skill level of the bot and opponent)
	 */
	public UT2004ShockRifleVsGBBotsDeathMatchTask(String map, BotController[] opponents, int evalMinutes,
			int desiredSkill) {
		super(map, evalMinutes, desiredSkill, opponents);
		// Fitness objectives
		addObjective(new DamageDealtFitness<T>(), fitness, true);
		addObjective(new ShockRifleAccuracyFitness<T>(), fitness, true);
		addObjective(new DamageReceivedFitness<T>(), fitness, true);
		// Other stats to track
		addObjective(new FragFitness<T>(), others, false);
		addObjective(new DeathsFitness<T>(), others, false);
		addObjective(new ScoreFitness<T>(), others, false);
		addObjective(new HighestEnemyScoreFitness<T>(), others, false);
		addObjective(new StreakFitness<T>(), others, false);

		System.out.println("Fitness:" + fitness);
		System.out.println("Other Scores:" + others);
	}
}

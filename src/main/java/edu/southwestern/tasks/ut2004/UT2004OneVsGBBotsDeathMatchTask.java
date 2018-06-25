package edu.southwestern.tasks.ut2004;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.ut2004.controller.BotController;
import edu.southwestern.tasks.ut2004.fitness.DamageDealtFitness;
import edu.southwestern.tasks.ut2004.fitness.DamageReceivedFitness;
import edu.southwestern.tasks.ut2004.fitness.DeathsFitness;
import edu.southwestern.tasks.ut2004.fitness.FragFitness;
import edu.southwestern.tasks.ut2004.fitness.HighestEnemyScoreFitness;
import edu.southwestern.tasks.ut2004.fitness.ScoreFitness;
import edu.southwestern.tasks.ut2004.fitness.StreakFitness;
import edu.southwestern.util.ClassCreation;

/**
 * Launches servers to evolve bots against the DummyBots (which spawn at a random location on the map and stand there)
 * @author Jacob Schrum
 */
public class UT2004OneVsGBBotsDeathMatchTask<T extends Network> extends UT2004Task<T> {

	/**
	 * sets the parameters for the server and evaluation
	 */
	public UT2004OneVsGBBotsDeathMatchTask() {
		this(Parameters.parameters.stringParameter("utMap"),
				getOpponents(Parameters.parameters.integerParameter("utNumOpponents")),
				Parameters.parameters.integerParameter("utEvalMinutes"),
				Parameters.parameters.integerParameter("utEvolvingBotSkill"));
	}

	/**
	 * gets the opponents of the bot
	 * @param num (the address of the array that the opponent will be assigned to)
	 * @return returns the opponent array
	 */
	public static BotController[] getOpponents(int num) {
		System.out.println("CREATE " + num + " ControllerBot opponents");
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
	public UT2004OneVsGBBotsDeathMatchTask(String map, BotController[] opponents, int evalMinutes, int desiredSkill) {
		super(map, evalMinutes, desiredSkill, opponents);
		// Fitness objectives
		addObjective(new DamageDealtFitness<T>(), fitness, true);
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

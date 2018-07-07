package edu.southwestern.tasks.ut2004;

import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.ut2004.controller.BotController;
import edu.southwestern.tasks.ut2004.fitness.*;

/**
 * launches a deathmatch server with the evolving bot and native bots
 * @author Jacob Schrum
 * @param <T> evolved phenotype
 */
public class UT2004OneVsNativeBotsDeathMatchTask<T extends Network> extends UT2004Task<T> {

	/**
	 * sets the parameters for the server and evaluation
	 */
	public UT2004OneVsNativeBotsDeathMatchTask() {
		this(Parameters.parameters.stringParameter("utMap"),
				Parameters.parameters.integerParameter("utEvalMinutes"),
				Parameters.parameters.integerParameter("utEvolvingBotSkill"));
	}

	/**
	 * sets up the server and the match
	 * @param map (map the match will be played on)
	 * @param nativeBotSkills (skill level of the native bots)
	 * @param evalMinutes (how long the eval will last)
	 * @param desiredSkill (skill level of the evolving bot)
	 */
	public UT2004OneVsNativeBotsDeathMatchTask(String map, int evalMinutes, int desiredSkill) {
		super(evalMinutes, desiredSkill, new BotController[0]);
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

	/**
	 * Testing
	 * @param args
	 */
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "utDrive:D", "trials:2", "io:false", "netio:false", 
				"task:edu.southwestern.tasks.ut2004.UT2004OneVsNativeBotsDeathMatchTask" });
		MMNEAT.loadClasses();
		UT2004Task utTask = (UT2004Task) MMNEAT.task;
		new UT2004OneVsNativeBotsDeathMatchTask<TWEANN>("DM-TrainingDay", 5, 1).evaluate(
				new TWEANNGenotype(utTask.sensorModel.numberOfSensors(), utTask.outputModel.numberOfOutputs(), 0));
	}
}

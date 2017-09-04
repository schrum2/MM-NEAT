package edu.utexas.cs.nn.tasks.ut2004;

import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.ut2004.controller.BotController;
import edu.utexas.cs.nn.tasks.ut2004.fitness.*;

/**
 *
 * @author Jacob Schrum
 * @param <T> evolved phenotype
 */
public class UT2004OneVsNativeBotsDeathMatchTask<T extends Network> extends UT2004Task<T> {

	public UT2004OneVsNativeBotsDeathMatchTask() {
		this(Parameters.parameters.stringParameter("utMap"),
				new int[] { Parameters.parameters.integerParameter("utNativeBotSkill") },
				Parameters.parameters.integerParameter("utEvalMinutes"),
				Parameters.parameters.integerParameter("utEvolvingBotSkill"));
	}

	public UT2004OneVsNativeBotsDeathMatchTask(String map, int[] nativeBotSkills, int evalMinutes, int desiredSkill) {
		super(map, nativeBotSkills, evalMinutes, desiredSkill, new BotController[0]);
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
	 *
	 * @param args
	 */
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "utDrive:D", "trials:2", "io:false", "netio:false", 
				"task:edu.utexas.cs.nn.tasks.ut2004.UT2004OneVsNativeBotsDeathMatchTask" });
		MMNEAT.loadClasses();
		UT2004Task utTask = (UT2004Task) MMNEAT.task;
		new UT2004OneVsNativeBotsDeathMatchTask<TWEANN>("DM-TrainingDay", new int[] { 3, 4, 5 }, 5, 1).evaluate(
				new TWEANNGenotype(utTask.sensorModel.numberOfSensors(), utTask.outputModel.numberOfOutputs(), 0));
	}
}

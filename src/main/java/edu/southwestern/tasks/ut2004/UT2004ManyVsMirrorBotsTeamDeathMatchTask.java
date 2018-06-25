package edu.southwestern.tasks.ut2004;

import edu.southwestern.evolution.genotypes.Genotype;
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
import edu.southwestern.tasks.ut2004.fitness.TeamScoreFitness;
import edu.southwestern.util.datastructures.Pair;

/**
 * launches a team deathmatch server with the evolving bot and native bots
 * @author Jacob Schrum
 * @param <T> evolved phenotype
 */
public class UT2004ManyVsMirrorBotsTeamDeathMatchTask<T extends Network> extends UT2004Task<T> {

	/**
	 * sets the parameters for the server and evaluation
	 */
	public UT2004ManyVsMirrorBotsTeamDeathMatchTask() {
		this(Parameters.parameters.stringParameter("utMap"),
				Parameters.parameters.integerParameter("utEvalMinutes"),
				Parameters.parameters.integerParameter("utEvolvingBotSkill"));
	}
	//	
	//	public static int[] getMirrorBotSkillArray() {
	//		//array of native bots with all Parameters.parameters.integerParameter("utNativeBotSkill")
	//		int[] mirrorBotArray = new int[Parameters.parameters.integerParameter("utNumMirrorBots")];
	//		for(int i = 0; i < mirrorBotArray.length; i++) {
	//			mirrorBotArray[i] = Parameters.parameters.integerParameter("utNativeBotSkill");
	//		}
	//		return mirrorBotArray;
	//	}

	/**
	 * sets up the server and the match
	 * @param map (map the match will be played on)
	 * @param desiredSkill (skill level of the native bots)
	 * @param evalMinutes (how long the eval will last)
	 * @param desiredSkill (skill level of the evolving bot)
	 */
	public UT2004ManyVsMirrorBotsTeamDeathMatchTask(String map, int evalMinutes, int desiredSkill) {
		super(map, evalMinutes, desiredSkill, new BotController[0]);
		// Fitness objectives
		//add one for team score
		addObjective(new TeamScoreFitness<T>(), fitness, true);
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


	@SuppressWarnings("unchecked")
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {

		@SuppressWarnings("rawtypes")
		Genotype[] teamArray = new Genotype[Parameters.parameters.integerParameter("utTeamSize")];
		for(int i = 0; i < teamArray.length; i++) {
			teamArray[i] = individual.copy();
		}
		//create an array of genotypes that is size of team
		//loop through and each one is copy of individual

		Pair<double[], double[]>[] result = evaluateMultipleGenotypes(teamArray, map, 0, // 0 native bots
				sensorModel, outputModel, weaponManager, opponents,
				evalMinutes, desiredSkill,
				fitness, others);

		//result[0] = ;
		//have array of results, make that a single result for whole team
		return result[0]; // TODO change
	}
}

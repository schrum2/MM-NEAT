package edu.southwestern.tasks.ut2004.testing;

import java.io.IOException;
import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.ut2004.UT2004Task;
import edu.southwestern.tasks.ut2004.controller.BehaviorListController;
import edu.southwestern.tasks.ut2004.controller.BotController;
import edu.southwestern.tasks.ut2004.controller.behaviors.BehaviorModule;
import edu.southwestern.tasks.ut2004.fitness.UT2004FitnessFunction;

public class AttemptAtTeammateServer {
	public static void main(String[] args) throws IOException {
		Parameters.initializeParameterCollections(new String[] {"runNumber:0", "io:false", "netio:false", "numUT2Bots:0", "numMirrorBots:0", "botprizeMod:true", "utEvalMinutes:15", "utNumOpponents:1", "utGameType:botTeamGame", "utMap:DM-Phobos2"});
		
		
		@SuppressWarnings("unchecked")
		Genotype<TWEANN>[] individuals = new Genotype[0];
		BotController[] controller = new BotController[1];

		BotController behaviorListController = new AttemptAtTeammateController();
		
		controller[0] = behaviorListController;
		
		int[] nativeBotSkills = new int[0];
		int evalMinutes = Parameters.parameters.integerParameter("utEvalMinutes");
		String map = Parameters.parameters.stringParameter("utMap");
		int desiredSkill = 0;
		ArrayList<UT2004FitnessFunction<TWEANN>> fitness = new ArrayList<>();
		ArrayList<UT2004FitnessFunction<TWEANN>> others = new ArrayList<>();
		//launches server
		UT2004Task.evaluateMultipleGenotypes(individuals, map, null, null, null, controller, evalMinutes, desiredSkill,	fitness, others);
	}
}

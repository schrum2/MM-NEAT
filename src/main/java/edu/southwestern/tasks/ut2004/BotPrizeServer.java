package edu.southwestern.tasks.ut2004;

import java.io.IOException;
import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.ut2004.controller.BotController;
import edu.southwestern.tasks.ut2004.fitness.UT2004FitnessFunction;

/**
 * Launches a server with the botprize modifier and botprize agents:
 * two UT^2 agents and two MirrorBot agents
 * @author Jacob Schrum
 */
public class BotPrizeServer {
	public static void main(String[] args) throws IOException {
		Parameters.initializeParameterCollections(new String[] {"runNumber:0", "io:false", "netio:false", "numUT2Bots:2", "numMirrorBots:2", "botprizeMod:true", "utEvalMinutes:10"});
		
		//copyBotPrizeVersionOfGameBots(); // Make sure right version of GameBots is being used
		UT2004Util.copyBotPrizeVersionOfGameBots();
		
		@SuppressWarnings("unchecked")
		Genotype<TWEANN>[] individuals = new Genotype[0];
		BotController[] controller = new BotController[0];
		int evalMinutes = Parameters.parameters.integerParameter("utEvalMinutes");
		int desiredSkill = 0;
		int numNativeBots = 0; // However, the BotPrize server may auto-add native bots
		ArrayList<UT2004FitnessFunction<TWEANN>> fitness = new ArrayList<>();
		ArrayList<UT2004FitnessFunction<TWEANN>> others = new ArrayList<>();
		
		String[] mapList = new String[] {"DM-GoatswoodPlay", "DM-Flux2", "DM-JunkYard", "DM-DE-OSIRIS2", "DM-Antalus", "DM-IceHenge"};
		
		//launches server
		UT2004Task.evaluateMultipleGenotypesAcrossMultipleMaps(individuals, mapList, numNativeBots, null, null, null, controller, evalMinutes, desiredSkill, fitness, others);
	}
}

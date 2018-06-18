package edu.southwestern.tasks.ut2004;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
		Parameters.initializeParameterCollections(new String[] {"runNumber:0", "io:false", "netio:false", "numUT2Bots:2", "numMirrorBots:2", "botprizeMod:true", "utEvalMinutes:15"});
		
		copyBotPrizeVersionOfGameBots(); // Make sure right version of GameBots is being used
		
		@SuppressWarnings("unchecked")
		Genotype<TWEANN>[] individuals = new Genotype[0];
		BotController[] controller = new BotController[0];
		int[] nativeBotSkills = new int[0];
		int evalMinutes = Parameters.parameters.integerParameter("utEvalMinutes");
		int desiredSkill = 0;
		ArrayList<UT2004FitnessFunction<TWEANN>> fitness = new ArrayList<>();
		ArrayList<UT2004FitnessFunction<TWEANN>> others = new ArrayList<>();
		//launches server
		UT2004Task.evaluateMultipleGenotypes(individuals, "DM-TrainingDay", null, null, null, controller,	nativeBotSkills, evalMinutes, desiredSkill,	fitness, others);
	}
	
	/**
	 * Copy the version of GameBots stored in MM-NEAT into the UT2004\System directory to make sure that
	 * the botprize mode is available.
	 * @throws IOException 
	 */
	public static void copyBotPrizeVersionOfGameBots() throws IOException {
		String[] filesToCopy = new String[] {
				"GameBots2004.u",	// 2012 version of BotPrize mod
				"GameBots2004.ucl", // 2012 version of BotPrize mod
				"GameBots2004.ini"	// Configuration that disables all GameBots visualization
		};
		String utSystemDir = Parameters.parameters.stringParameter("utDrive") + ":" + File.separator + Parameters.parameters.stringParameter("utPath") + File.separator + "System";
		for(String file :  filesToCopy) {
			Files.copy(Paths.get("data" + File.separatorChar + "unreal" + File.separatorChar + "BotPrizeGameBots"  + File.separatorChar + file), 
					   Paths.get(utSystemDir + File.separatorChar + file), 
					   REPLACE_EXISTING);
		}
	}
}

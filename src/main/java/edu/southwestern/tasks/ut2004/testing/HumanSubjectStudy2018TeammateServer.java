package edu.southwestern.tasks.ut2004.testing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.ut2004.UT2004Task;
import edu.southwestern.tasks.ut2004.actuators.OpponentAndTeammateRelativeMovementOutputModel;
import edu.southwestern.tasks.ut2004.actuators.UT2004OutputInterpretation;
import edu.southwestern.tasks.ut2004.controller.BotController;
import edu.southwestern.tasks.ut2004.fitness.UT2004FitnessFunction;
import edu.southwestern.tasks.ut2004.sensors.OpponentAndTeammateRelativeSensorModel;
import edu.southwestern.tasks.ut2004.sensors.UT2004SensorModel;
import edu.southwestern.tasks.ut2004.weapons.SimpleWeaponManager;
import wox.serial.Easy;

public class HumanSubjectStudy2018TeammateServer {
	enum BOT_TYPE {Ethan, Jude}; // Ethan = evolved, Jude = hard-coded
	
	/**
	 * Assumes parameters have been initialized elsewhere
	 * @param type Either the evolved bot or the hard-coded bot
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void runTrial(BOT_TYPE type) throws IOException {		
		//UT2004Util.copyDefaultVersionOfGameBots();
		
		Genotype<TWEANN>[] individuals;
		BotController[] controller;
		SimpleWeaponManager weaponManager = new SimpleWeaponManager();
		UT2004SensorModel sensorModel = null;
		UT2004OutputInterpretation outputModel = null;
		if(type.equals(BOT_TYPE.Ethan)) { // Ethan is the evolved bot
			Genotype<TWEANN> ethan = (Genotype<TWEANN>) Easy.load("data" + File.separator + "unreal" + File.separator + "Study2018" + File.separator + "Ethan.xml");
			individuals = new Genotype[] {ethan};
			controller = new BotController[0]; // No controller needed, since the genotype will handle it			
			sensorModel = new OpponentAndTeammateRelativeSensorModel();
			outputModel = new OpponentAndTeammateRelativeMovementOutputModel();
			
			// I'm afraid this won't work with a human teammate
			HashMap<String,Location> friendDistances = new HashMap<String,Location>();
			((OpponentAndTeammateRelativeSensorModel) sensorModel).giveTeamLocations(friendDistances);
			HashMap<String,Double> friendHealthLevels = new HashMap<String,Double>();
			((OpponentAndTeammateRelativeSensorModel) sensorModel).giveTeamHelathLevels(friendHealthLevels);;
			
		} else if(type.equals(BOT_TYPE.Jude)) { // Jude is the hard coded bot
			individuals = new Genotype[0]; 
			controller = new BotController[1]; // The one controller will be for Jude
			BotController behaviorListController = new HardCodedTeammateController(weaponManager);
			controller[0] = behaviorListController;
		} else {
			throw new IllegalArgumentException("Must be using either Ethan or Jude as the bot in this study");
		}
		
		int evalMinutes = Parameters.parameters.integerParameter("utEvalMinutes");
		String map = Parameters.parameters.stringParameter("utMap");
		int desiredSkill = Parameters.parameters.integerParameter("utEvolvingBotSkill"); 
		int nativeBotSkill = Parameters.parameters.integerParameter("utNativeBotSkill");
		int numNativeBotOpponents = Parameters.parameters.integerParameter("utNumNativeBots"); 
		ArrayList<UT2004FitnessFunction<TWEANN>> fitness = new ArrayList<>();
		ArrayList<UT2004FitnessFunction<TWEANN>> others = new ArrayList<>();
		//launches server
		UT2004Task.evaluateMultipleGenotypes(individuals, map, numNativeBotOpponents, sensorModel, outputModel, weaponManager, controller, evalMinutes, desiredSkill, nativeBotSkill,	fitness, others);
	}
	
	public static void main(String[] args) throws IOException {
		Parameters.initializeParameterCollections(new String[] {"runNumber:0", "io:false", "netio:false", "numUT2Bots:0", "numMirrorBots:0", "utNumNativeBots:2", "botprizeMod:false", "utEvalMinutes:10", "utNumOpponents:1", "utGameType:botTeamGame", "utMap:DM-Flux2","utBotLogOutput:true"});
		//runTrial(BOT_TYPE.Jude);
		runTrial(BOT_TYPE.Ethan);
	}
}

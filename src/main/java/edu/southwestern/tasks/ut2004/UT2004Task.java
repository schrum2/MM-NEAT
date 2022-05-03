package edu.southwestern.tasks.ut2004;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.AddBot;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.MyUCCWrapper;
import cz.cuni.amis.pogamut.ut2004.utils.MyUCCWrapperConf;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.Organism;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.ut2004.actuators.UT2004OutputInterpretation;
import edu.southwestern.tasks.ut2004.bots.ControllerBot;
import edu.southwestern.tasks.ut2004.bots.GameDataCollector;
import edu.southwestern.tasks.ut2004.controller.BehaviorListController;
import edu.southwestern.tasks.ut2004.controller.BotController;
import edu.southwestern.tasks.ut2004.controller.NetworkController;
import edu.southwestern.tasks.ut2004.controller.behaviors.BattleNetworkBehaviorModule;
import edu.southwestern.tasks.ut2004.controller.behaviors.BehaviorModule;
import edu.southwestern.tasks.ut2004.controller.behaviors.ItemExplorationBehaviorModule;
import edu.southwestern.tasks.ut2004.controller.behaviors.WeaponGrabBehaviorModule;
import edu.southwestern.tasks.ut2004.fitness.UT2004FitnessFunction;
import edu.southwestern.tasks.ut2004.maps.MapList;
import edu.southwestern.tasks.ut2004.sensors.UT2004SensorModel;
import edu.southwestern.tasks.ut2004.server.ServerUtil;
import edu.southwestern.tasks.ut2004.weapons.UT2004WeaponManager;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.stats.Statistic;

/**
 * launches UT2004
 * @author Jacob Schrum
 * @param <T> evolved phenotype
 */
public abstract class UT2004Task<T extends Network> extends NoisyLonerTask<T>implements NetworkTask {

	public UT2004SensorModel sensorModel;
	public UT2004OutputInterpretation outputModel;
	public UT2004WeaponManager weaponManager;
	protected String[] mapList;
	protected final int evalMinutes;
	protected final int desiredSkill;
	protected final BotController[] opponents;
	public ArrayList<UT2004FitnessFunction<T>> fitness = new ArrayList<UT2004FitnessFunction<T>>();
	public ArrayList<UT2004FitnessFunction<T>> others = new ArrayList<UT2004FitnessFunction<T>>();

	/**
	 * Sets out the parameters for the server to be launched
	 * @param map (map that the match will be played on)
	 * @param nativeBotSkills (the abilities of the bots provided by the game)
	 * @param evalMinutes (the total time for the evaluation)
	 * @param desiredSkill (the skill to be evaluated)
	 * @param opponents (the other players the bot is playing against)
	 */
	public UT2004Task(int evalMinutes, int desiredSkill, BotController[] opponents) {
		this.evalMinutes = evalMinutes;
		this.desiredSkill = desiredSkill;
		this.opponents = opponents;

		try {
			if(Parameters.parameters.classParameter("utMapList") != null) { // Use multiple maps, from class
				MapList maps = (MapList) ClassCreation.createObject("utMapList");
				this.mapList = maps.getMapList();
			} else { // Use only one map, defines at command line
				this.mapList = new String[] {Parameters.parameters.stringParameter("utMap")};
			}
			this.sensorModel = (UT2004SensorModel) ClassCreation.createObject("utSensorModel");
			this.outputModel = (UT2004OutputInterpretation) ClassCreation.createObject("utOutputModel");
			this.weaponManager = (UT2004WeaponManager) ClassCreation.createObject("utWeaponManager");
		} catch (NoSuchMethodException ex) {
			System.out.println("Could not load sensor/output model for UT2004");
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	//objectives are things to be logged, used by evolutionary alg, to determine fitness0

	@Override
	/**
	 * @return returns an array containing the labels for the sensor model
	 */
	public String[] sensorLabels() {
		return sensorModel.sensorLabels();
	}

	@Override
	/**
	 * @return returns an array containing the labels for the output model
	 */
	public String[] outputLabels() {
		return outputModel.outputLabels();
	}

	/**
	 * adds an objective to be tracked
	 * @param o (a fitness function)
	 * @param list (an accumulating list of objectives)
	 * @param affectsSelection (shows whether or not it changes the fitness function)
	 */
	public final void addObjective(UT2004FitnessFunction<T> o, ArrayList<UT2004FitnessFunction<T>> list,boolean affectsSelection) {
		addObjective(o, list, null, affectsSelection);
	}

	/**
	 * add an objective to be tracked that will affect the fitness function
	 * @param o (fitness function to be used)
	 * @param list (place to store fitness function results)
	 * @param override
	 * @param affectsSelection (shows whether or not it changes the fitness function)
	 */
	public final void addObjective(UT2004FitnessFunction<T> o, ArrayList<UT2004FitnessFunction<T>> list, Statistic override, boolean affectsSelection) {
		list.add(o);
		MMNEAT.registerFitnessFunction(o.getClass().getSimpleName(), override, affectsSelection);
	}

	/**
	 * Connects the server to the correct port, the bot to the correct server, and applies any mutators to the server
	 * @param individual (the genetic representation of the bot being evaluated)
	 * @param num (the number evaluation you're on)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		// The version for multiple maps is used, although the list may only have a single map in it
		return evaluateMultipleGenotypesAcrossMultipleMaps(new Genotype[] {individual}, mapList, Parameters.parameters.integerParameter("utNumNativeBots"), 
					sensorModel, outputModel, weaponManager, opponents,
					evalMinutes, desiredSkill,  Parameters.parameters.integerParameter("utNativeBotSkill"),
					null, null, // Teams and names do not matter during evolution
					fitness, others)[0]; // If there is only one individual then it will be the first in the result array		
	}
	
	/**
	 * Evaluate multiple genotypes and return evaluation information on each of them.
	 * 
	 * @param individuals (Genotypes that can fill NetworkControllers)
	 * @param num (Evaluation number)
	 * @param map (UT2004 map)
	 * @param sensorModel (Sensor model used by evolved bots)
	 * @param outputModel (Output model used by evolved bots
	 * @param weaponManager (How evolved bot selects its weapons)
	 * @param opponents (Hard-coded opponent bot controllers)
	 * @param evalMinutes (Number of minutes to evaluate for)
	 * @param desiredSkill (Skill setting for evolved bots)
	 * @param fitness (List of fitness scores used to evaluate agents)
	 * @param others (List of other scores tracked from evaluation)
	 * @return Array of paired fitness and other scores for the evolved agents
	 */
	public static <T extends Network> Pair<double[], double[]>[] evaluateMultipleGenotypes(Genotype<T>[] individuals, String map, int numNativeBots,
			UT2004SensorModel sensorModel, UT2004OutputInterpretation outputModel, UT2004WeaponManager weaponManager, BotController[] opponents,
			int evalMinutes, int desiredSkill, int nativeBotSkill,
			ArrayList<UT2004FitnessFunction<T>> fitness, ArrayList<UT2004FitnessFunction<T>> others) {    
		return evaluateMultipleGenotypes(individuals, map, numNativeBots,
				sensorModel, outputModel, weaponManager, opponents,
				evalMinutes, desiredSkill, nativeBotSkill, null, null,
				fitness, others);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Network> Pair<double[], double[]>[] evaluateMultipleGenotypes(Genotype<T>[] individuals, String map, int numNativeBots,
			UT2004SensorModel sensorModel, UT2004OutputInterpretation outputModel, UT2004WeaponManager weaponManager, BotController[] opponents,
			int evalMinutes, int desiredSkill, int nativeBotSkill, int[] nativeTeams, String[] nativeNames,
			ArrayList<UT2004FitnessFunction<T>> fitness, ArrayList<UT2004FitnessFunction<T>> others) {    
		// finds the port connection for the bot
		int botPort = ServerUtil.getAvailablePort();
		int controlPort = ServerUtil.getAvailablePort();
		int observePort = ServerUtil.getAvailablePort();
		int gamePort = ServerUtil.getAvailablePort();
		// sets the map, gametype, and Gamebots that will be used
		MyUCCWrapperConf config = getUCCWrapperConfig(botPort, controlPort, observePort, gamePort, map);

		//Launches the server, and ensures that it is empty at the outset
		MyUCCWrapper ucc = null;
		Pair<double[], double[]>[] result = new Pair[individuals.length + opponents.length];
		int attempts = 1; //tracks the number of attempts to launch the server
		while (ArrayUtil.anyNull(result) || // Loop as long as there are problems with the results
			   individuals.length == 0) { // Special case to run the server when no genotypes are being evolved
			if(individuals.length == 0) System.out.println("No evolving bots!");
			
			
			if(attempts > 3) {
				System.out.println("Evaluation Keeps Failing! Give up");
				System.exit(1);
			}
			
			System.out.println("Eval attempt " + (attempts++));
			try {
				ucc = new MyUCCWrapper(config);
				ucc.getLogger().setLevel(Level.OFF); // Stop logging (too much clutter)
				IUT2004Server server = ucc.getUTServer();
				System.out.println(botPort + ": Confirming empty server");
				//responsible for launching native bots into the server, resets if the port is not empty
				while (server.getAgents().size() > 0 
						|| server.getNativeAgents().size() > 0
						|| server.getPlayers().size() > 0) {
					System.out.println(botPort + ": NOT EMPTY! RESET!");
					ServerUtil.destroyServer(ucc, true);

					ucc = new MyUCCWrapper(config);
					server = ucc.getUTServer();
				}
				// Server was launched?
				int claimTicket = ServerUtil.addServer(ucc);
				System.out.println(botPort + ": Empty server gets ticekt: " + claimTicket);
				server.getLogger().setLevel(Level.OFF); // Turn off the server log info (too much clutter)
				try {
					// Make controller for each genotype
					NetworkController<T>[] organisms = new NetworkController[individuals.length];
					// Will contain evolved network controllers and hard coded opponents, if any
					BotController[] controllers = new BotController[individuals.length + opponents.length];
					for(int i = 0; i < organisms.length; i++) {		
						// Each controller gets its own copy of the sensor model, output model, and weapon manager
						assert individuals[i] != null;
						assert sensorModel != null;
						assert outputModel != null;
						assert weaponManager != null;
						organisms[i] = new NetworkController<T>(individuals[i], sensorModel.copy(), outputModel.copy(), weaponManager.copy());
						controllers[i] = wrapNetworkInBehaviorListController(organisms[i]);
					}
					// Copy the fixed opponent controllers into the controllers array after the evolved network controllers
					System.arraycopy(opponents, 0, controllers, individuals.length, opponents.length);
					// Evaluate network controllers and fixed controllers
					long evaluateStartTime = System.currentTimeMillis();
					GameDataCollector[] collectors = evaluateAgentsOnServer(server, controllers, botPort, gamePort, numNativeBots, evalMinutes, desiredSkill, nativeBotSkill, nativeTeams, nativeNames); 					
					System.out.println("evaluateAgentsOnServer finished: " + evaluateStartTime + " to " + System.currentTimeMillis());
					// Transfer stats data to result: only evolved organisms
					for(int j = 0; j < organisms.length; j++) {
						if (collectors[j].evalWasSuccessful()) {
							result[j] = relevantScores(collectors[j], organisms[j], fitness, others);
						}						
					}
					// If we want to run a server with only native bots
					long bufferTime = 30*1000; // 30 seconds
					long evalTimeMillis = evalMinutes*60*1000 + bufferTime; // Eval time in milliseconds
					while(collectors.length == 0 && // No gamebots agents are running 
						  System.currentTimeMillis() - evaluateStartTime < evalTimeMillis) { // time not up
						// Will hopefully only sleep once
						System.out.println("Sleep to delay at: " + System.currentTimeMillis());
						try {
							Thread.sleep(1000); // Sleep in small increments
						} catch (InterruptedException e) {
							e.printStackTrace();
							System.out.println("Interrupted while waiting for server");
						}
					}
				} finally {
					System.out.println(botPort + ": Past evaluate block: " + System.currentTimeMillis());
					ServerUtil.removeServer(claimTicket);
					server.stop(); // Will this cause a problematic exception?
				}
			} catch (ComponentCantStartException ccse) {//gets rid of the server if it can't be started
				System.out.println("EXCEPTION: Can't start the server. Failed eval. Destroy server");
				ServerUtil.destroyServer(ucc, true);
				// Reset all eval results
				result = new Pair[individuals.length + opponents.length];
			} finally {
				if (ArrayUtil.anyNull(result)) {//repeats the evaluation if it is unsuccessful the first time
					System.out.println("Evaluation failed: repeat: " + botPort);
				}
				// If bots are not evolving, then perform one eval before finishing
				if(individuals.length == 0) {
					return result; // Will be an empty result
				}
			}
		}
		return result;
	}

	/**
	 * Take a network controller for combat and make a complete agent that
	 * has a list of other (basic) behaviors to use outside of combat.
	 * @param organism Network controller derived from genotype
	 * @return Complete controller using a list of behaviors
	 */
	public static <T extends Network> BotController wrapNetworkInBehaviorListController(NetworkController<T> organism) {
		return wrapNetworkInBehaviorListController(organism, null, null); // Use default name and skin
	}
	
	/**
	 * Same as above, but specifies the name and skin of the bot
	 * @param organism Network controller for agent
	 * @param name String displayed for agent in game
	 * @param skin String that defines a skin class for the agent to use
	 * @return Controller with a list of behaviors, including the network for combat
	 */
	public static <T extends Network> BotController wrapNetworkInBehaviorListController(NetworkController<T> organism, String name, String skin) {
		// The evolved network controllers use the network for battle, and have a basic item exploration module
		ArrayList<BehaviorModule> behaviors = new ArrayList<BehaviorModule>(2);
		behaviors.add(new BattleNetworkBehaviorModule<T>(organism)); // Fighting with evolved network takes top priority
		behaviors.add(new WeaponGrabBehaviorModule());	// Getting a weapon is also important
		behaviors.add(new ItemExplorationBehaviorModule()); // Grab random items otherwise
		BotController controller = name == null || skin == null ? // If name or skin not overridden
				new BehaviorListController(behaviors) : // Use default
				new BehaviorListController(behaviors, name, skin); // Else use specified skin and name
		return controller;
	}
	
	/**
	 * Evaluate multiple genotypes across different maps and return evaluation information on each of them.
	 * 
	 * @param individuals (Genotypes that can fill NetworkControllers)
	 * @param num (Evaluation number)
	 * @param map (an array of maps that the bot will be evaluated on)
	 * @param sensorModel (Sensor model used by evolved bots)
	 * @param outputModel (Output model used by evolved bots
	 * @param weaponManager (How evolved bot selects its weapons)
	 * @param opponents (Hard-coded opponent bot controllers)
	 * @param evalMinutes (Number of minutes to evaluate for)
	 * @param desiredSkill (Skill setting for evolved bots)
	 * @param fitness (List of fitness scores used to evaluate agents)
	 * @param others (List of other scores tracked from evaluation)
	 * @return Array of paired fitness and other scores for the evolved agents
	 */
	public static <T extends Network> Pair<double[], double[]>[] evaluateMultipleGenotypesAcrossMultipleMaps(Genotype<T>[] individuals, String[] map, int numNativeBots, 
			UT2004SensorModel sensorModel, UT2004OutputInterpretation outputModel, UT2004WeaponManager weaponManager, BotController[] opponents,
			int evalMinutes, int desiredSkill, int nativeBotSkill, 
			ArrayList<UT2004FitnessFunction<T>> fitness, ArrayList<UT2004FitnessFunction<T>> others) {    
		return evaluateMultipleGenotypesAcrossMultipleMaps(individuals, map, numNativeBots, 
				sensorModel, outputModel, weaponManager, opponents,
				evalMinutes, desiredSkill, nativeBotSkill, 
				null, null, // Use default names and teams
				fitness, others);
	}
	
	/**
	 * Same as above, but adds team specification for each native bot, and name specification for each native bot.
	 * @param individuals
	 * @param map
	 * @param numNativeBots
	 * @param sensorModel
	 * @param outputModel
	 * @param weaponManager
	 * @param opponents
	 * @param evalMinutes
	 * @param desiredSkill
	 * @param nativeBotSkill
	 * @param nativeTeams
	 * @param nativeNames
	 * @param fitness
	 * @param others
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Network> Pair<double[], double[]>[] evaluateMultipleGenotypesAcrossMultipleMaps(Genotype<T>[] individuals, String[] map, int numNativeBots, 
			UT2004SensorModel sensorModel, UT2004OutputInterpretation outputModel, UT2004WeaponManager weaponManager, BotController[] opponents,
			int evalMinutes, int desiredSkill, int nativeBotSkill, int[] nativeTeams, String[] nativeNames,
			ArrayList<UT2004FitnessFunction<T>> fitness, ArrayList<UT2004FitnessFunction<T>> others) {    
		
		double[][][] fitnessArr = new double[individuals.length][map.length][];
		double[][][] otherScoresArr = new double[individuals.length][map.length][];
		for(int i = 0; i < map.length; i++) {
			Pair<double[], double[]>[] oneResult = evaluateMultipleGenotypes(individuals, map[i], numNativeBots, sensorModel, outputModel, weaponManager, opponents,
																			evalMinutes, desiredSkill, nativeBotSkill, nativeTeams, nativeNames, fitness, others);
			for(int j = 0; j < individuals.length; j++) {
				fitnessArr[j][i] = oneResult[j].t1;
				otherScoresArr[j][i] = oneResult[j].t2;
			}
		}
		Pair<double[], double[]>[] resultToReturn = new Pair[individuals.length]; 
		for(int i = 0; i <individuals.length; i++) {
			resultToReturn[i] = NoisyLonerTask.averageResults(fitnessArr[i], otherScoresArr[i]);
		}
		
		return resultToReturn;
	}
	
	/**
	 * Configure the server with several ports as well as command line parameter information
	 * 
	 * @param botPort
	 * @param controlPort
	 * @param observePort
	 * @param gamePort
	 * @param map UT2004 map name
	 * @return Configuration for server
	 */
	public static MyUCCWrapperConf getUCCWrapperConfig(int botPort, int controlPort, int observePort, int gamePort, String map) {
		MyUCCWrapperConf config = new MyUCCWrapperConf();
		config.setPlayerPort(gamePort);
		config.setStartOnUnusedPort(false);
		config.setMapName(map);
		config.setGameBotsPack("GameBots2004");
		config.setGameType(Parameters.parameters.stringParameter("utGameType"));

		//Creates an arraylist of mutators that will be applied to the server
		ArrayList<String> mutators = new ArrayList<>();
		if(Parameters.parameters.booleanParameter("botprizeMod")) {
			mutators.add("GameBots2004.BotprizeMutator");		
		}
		if(Parameters.parameters.booleanParameter("navCubes")) {
			mutators.add("GameBots2004.PathMarkerMutator");
		}
		if(Parameters.parameters.booleanParameter("GBHUDMutator")) {
			mutators.add("Gamebots2004.GBHUD");
		}

		//converts the arraylist into a string that will be given to the server as a command
		String mutatorString = mutators.isEmpty() ? "":"?mutator=" + String.join(",", mutators);

		// Setup the server configuration
		config.setOptions(mutatorString//sets the preferences for the game, and players
				+ "?fraglimit=0?GoalScore=0?TimeLimit=" + Parameters.parameters.integerParameter("utEvalMinutes")
				+ "?DoUplink=False?UplinkToGamespy=False?SendStats=False?bAllowPrivateChat=False?bAllowTaunts=False?bEnableVoiceChat=False?bAllowLocalBroadcast=False?BotServerPort="
				+ botPort + "?ControlServerPort=" + controlPort + "?ObservingServerPort=" + observePort);
		config.setUnrealHome(UT2004Util.getUnrealHomeDir());
		//System.out.println(config);
		return config;
	}

	/**
	 * Evaluate several BotControllers on a server. Each BotController is launched inside of a ControllerBot
	 * instance. Native bots can also be added, as well as other types of bots, though these are added in 
	 * the ControllerBot's launchBot method.
	 * 
	 * @param server Server bots will run on
	 * @param controllers Controllers for the ControllerBots
	 * @param botPort 
	 * @param gamePort 
	 * @param numNativeBots
	 * @param evalMinutes How many minutes the server evaluation lasts for
	 * @param desiredSkill skill level of the evolving bots 
	 * @return Collections of performance information about each ControllerBot
	 */
	public static GameDataCollector[] evaluateAgentsOnServer(IUT2004Server server, BotController[] controllers, int botPort, int gamePort,
			int numNativeBots, int evalMinutes, int desiredSkill, int nativeBotSkill, int[] teams, String[] nativeBotNames) {
		assert teams == null || teams.length == numNativeBots;
		assert nativeBotNames == null || nativeBotNames.length == numNativeBots;
		// Launch Native Bots
		for (int i = 0; i < numNativeBots; i++) {
			//server.connectNativeBot("Bot" + i, "Type" + i, 1); // TEAM 1: All on opposing team	
			// The connectNativeBot method above is insufficient because it does not allow the bot skill to be set.
			server.getAct().act(new AddBot(
					nativeBotNames == null ? ("Bot" + i) : nativeBotNames[i], // Default name if none are provided, or one from array 
					null, null, // Do not specify start location or rotation
					nativeBotSkill, teams == null ? 1 : teams[i], // Default to Team 1 (opposition) or allow team to be specified
					"Type" + i)); // What does the type do?
		}
		// Create names for all bot controllers
		String[] names = new String[controllers.length];
		for(int i = 0; i < names.length; i++) {
			String className = controllers[i].getClass().getName();
			// Just get the class name, not the package portion
			className = className.substring(className.lastIndexOf('.')+1)+i;
			names[i] = className;
		}
		
		// Launch bots on server and retrieve collected fitness info
		GameDataCollector[] collectors = ControllerBot.launchBot(
				server, names, controllers,
				evalMinutes * 60, desiredSkill, "localhost", botPort);
		return collectors; // Info about all ControllerBots
	}
	
	/**
	 * @return returns the time of the game
	 */
	@Override
	public double getTimeStamp() {
		return 0; // Not correct, but also not needed
	}

	/**
	 * returns how many objectives have been tracked that affect the fitness function
	 * @return returns the number of objectives that have been tracked
	 */
	@Override
	public int numObjectives() {
		return fitness.size();
	}

	@Override
	/**
	 * @return returns the number of objectives that have not affected the fitness function 
	 */
	public int numOtherScores() {
		return others.size();
	}

	/**
	 * 
	 * @param stats (the stats from the game)
	 * @param o (the organism to be monitored)
	 * @param fitness List of fitness scores used to evaluate agents
	 * @param others List of other scores tracked from evaluation
	 * @return returns the organism's scores in the fitness function
	 */
	public static <T extends Network> Pair<double[], double[]> relevantScores(GameDataCollector stats, Organism<T> o, 
			ArrayList<UT2004FitnessFunction<T>> fitness, ArrayList<UT2004FitnessFunction<T>> others) {
		double[] fitnessScores = new double[fitness.size()];
		for (int i = 0; i < fitnessScores.length; i++) {
			fitnessScores[i] = fitness.get(i).score(stats, o);
		}
		System.out.println("Fitness Scores:" + Arrays.toString(fitnessScores));

		double[] otherScores = new double[others.size()];
		for (int i = 0; i < otherScores.length; i++) {
			otherScores[i] = others.get(i).score(stats, o);
		}
		System.out.println("Other Scores:" + Arrays.toString(otherScores));

		return new Pair<double[], double[]>(fitnessScores, otherScores);
	}
	
	@Override
	public void postConstructionInitialization() {
		try {
			if(Parameters.parameters.booleanParameter("overwriteGameBots")) {
				if(Parameters.parameters.booleanParameter("botprizeMod")) {
					UT2004Util.copyBotPrizeVersionOfGameBots();
				} else {
					UT2004Util.copyDefaultVersionOfGameBots();
				}
			}
		} catch(IOException e) {
			System.out.println("Problem setting up GameBots");
			e.printStackTrace();
			System.exit(1);
		}
		MMNEAT.setNNInputParameters(sensorModel.numberOfSensors(), outputModel.numberOfOutputs());
	}
}

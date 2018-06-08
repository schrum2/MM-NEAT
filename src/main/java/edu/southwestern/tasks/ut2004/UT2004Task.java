package edu.southwestern.tasks.ut2004;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
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
import edu.southwestern.tasks.ut2004.fitness.UT2004FitnessFunction;
import edu.southwestern.tasks.ut2004.sensors.UT2004SensorModel;
import edu.southwestern.tasks.ut2004.server.ServerUtil;
import edu.southwestern.tasks.ut2004.weapons.UT2004WeaponManager;
import edu.southwestern.util.ClassCreation;
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
	private final String map;
	private final int[] nativeBotSkills;
	private final int evalMinutes;
	private final int desiredSkill;
	private final BotController[] opponents;
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
	public UT2004Task(String map, int[] nativeBotSkills, int evalMinutes, int desiredSkill, BotController[] opponents) {
		this.map = map;
		this.evalMinutes = evalMinutes;
		this.nativeBotSkills = nativeBotSkills;
		this.desiredSkill = desiredSkill;
		this.opponents = opponents;

		try {
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

	@Override
	/**
	 * Connects the server to the correct port, the bot to the correct server, and applies any mutators to the server
	 * @param individual (the genetic representation of the bot being evaluated)
	 * @param num (the number evaluation you're on)
	 */
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {            
		// finds the port connection for the bot
		int botPort = ServerUtil.getAvailablePort();
		int controlPort = ServerUtil.getAvailablePort();
		int observePort = ServerUtil.getAvailablePort();
		int gamePort = ServerUtil.getAvailablePort();
		// sets the map, gametype, and Gamebots that will be used
		MyUCCWrapperConf config = new MyUCCWrapperConf();
		config.setPlayerPort(gamePort);
		config.setStartOnUnusedPort(false);
		config.setMapName(map);
		config.setGameBotsPack("GameBots2004");
		config.setGameType("BotDeathMatch");


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

		//converts the srraylist into a string that will be given to the server as a command
		String mutatorString = mutators.isEmpty() ? "":"?mutator=" + String.join(",", mutators);

		// Setup the server configuration
		config.setOptions(mutatorString//sets the preferences for the game, and players
				+ "?fraglimit=0?GoalScore=0?DoUplink=False?UplinkToGamespy=False?SendStats=False?bAllowPrivateChat=False?bAllowTaunts=False?bEnableVoiceChat=False?bAllowLocalBroadcast=False?BotServerPort="
				+ botPort + "?ControlServerPort=" + controlPort + "?ObservingServerPort=" + observePort);
		config.setUnrealHome(Parameters.parameters.stringParameter("utDrive") + ":" + File.separator + Parameters.parameters.stringParameter("utPath"));
		//System.out.println(config);

		//Launches the server, and ensures that it is empty at the outset
		MyUCCWrapper ucc = null;
		Pair<double[], double[]> result = null;
		int attempts = 1; //tracks the number of sttempts to launch the server
		while (result == null) {
			System.out.println("Eval attempt " + (attempts++));
			try {
				ucc = new MyUCCWrapper(config);
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
				try {
					for (int i = 0; i < nativeBotSkills.length; i++) {
						server.connectNativeBot("Bot" + i, "Type" + i, nativeBotSkills[i]);
					}
					// Evaluate genotype
					NetworkController organism = new NetworkController<T>(individual, sensorModel.copy(), outputModel.copy(), weaponManager.copy());
					ArrayList<BehaviorModule> behaviors = new ArrayList<BehaviorModule>(2);
					behaviors.add(new BattleNetworkBehaviorModule<T>(organism));
					behaviors.add(new ItemExplorationBehaviorModule());
					BotController controller = new BehaviorListController(behaviors);
					// Store evolving bot and opponents that are ControllerBots
					BotController[] allBots = new BotController[this.opponents.length + 1];
					allBots[0] = controller;
					System.arraycopy(opponents, 0, allBots, 1, opponents.length);
					// Create names for all bots
					String[] names = new String[allBots.length];
					names[0] = "EvolvingBot" + gamePort;
					for(int i = 1; i < names.length; i++) {
						String className = allBots[i].getClass().getName();
						// Just get the class name, not the package portion
						className = className.substring(className.lastIndexOf('.')+1);
						names[i] = className;
					}
					// Launch bots on server and retrieve collected fitness info
					GameDataCollector[] collectors = ControllerBot.launchBot(
							server, names, allBots,
							evalMinutes * 60, desiredSkill, "localhost", botPort);
					// For now, assume we always want just the first collector
					GameDataCollector stats = collectors[0]; 

					// System.out.println("Eval over");
					// Transfer stats data to result
					if (stats.evalWasSuccessful()) {
						result = relevantScores(stats, organism);
					}
				} finally {
					System.out.println(botPort + ": Past evaluate block: " + System.currentTimeMillis());
					ServerUtil.removeServer(claimTicket);
				}
			} catch (ComponentCantStartException ccse) {//gets rid of the server if it can't be started
				System.out.println("EXCEPTION: Can't start the server. Failed eval. Destroy server");
				ServerUtil.destroyServer(ucc, true);
				result = null;
			} finally {
				if (result == null) {//repeats the evaluation if it is unsucessful the first time
					System.out.println("Evaluation failed: repeat: " + botPort);
				}
			}
		}
		return result;
	}

	@Override
	/**
	 * @return returns the time of the game
	 */
	public double getTimeStamp() {
		// Can the game time be retrieved?
		return 0; // Not correct
	}

	@Override
	/**
	 * returns how many objectives have been tracked that affect the fitness function
	 * @return returns the number of objectives that have been tracked
	 */
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
	 * @return returns the organism's scores in the fitness function
	 */
	public Pair<double[], double[]> relevantScores(GameDataCollector stats, Organism<T> o) {
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
}

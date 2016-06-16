package edu.utexas.cs.nn.tasks.ut2004;

import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.MyUCCWrapper;
import cz.cuni.amis.pogamut.ut2004.utils.MyUCCWrapperConf;
import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.tasks.ut2004.actuators.UT2004OutputInterpretation;
import edu.utexas.cs.nn.tasks.ut2004.bots.ControllerBot;
import edu.utexas.cs.nn.tasks.ut2004.bots.GameDataCollector;
import edu.utexas.cs.nn.tasks.ut2004.controller.BehaviorListController;
import edu.utexas.cs.nn.tasks.ut2004.controller.BotController;
import edu.utexas.cs.nn.tasks.ut2004.controller.NetworkController;
import edu.utexas.cs.nn.tasks.ut2004.controller.behaviors.BattleNetworkBehaviorModule;
import edu.utexas.cs.nn.tasks.ut2004.controller.behaviors.BehaviorModule;
import edu.utexas.cs.nn.tasks.ut2004.controller.behaviors.ItemExplorationBehaviorModule;
import edu.utexas.cs.nn.tasks.ut2004.fitness.UT2004FitnessFunction;
import edu.utexas.cs.nn.tasks.ut2004.sensors.UT2004SensorModel;
import edu.utexas.cs.nn.tasks.ut2004.server.ServerUtil;
import edu.utexas.cs.nn.tasks.ut2004.weapons.UT2004WeaponManager;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.stats.Statistic;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 * @param <T> evolved phenotype
 */
public abstract class UT2004Task<T extends Network> extends NoisyLonerTask<T>implements NetworkTask {

	public static final boolean BOTPRIZE = false; // Whether the botprize mod should be used
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

        @Override
	public String[] sensorLabels() {
		return sensorModel.sensorLabels();
	}

        @Override
	public String[] outputLabels() {
		return outputModel.outputLabels();
	}

	public final void addObjective(UT2004FitnessFunction<T> o, ArrayList<UT2004FitnessFunction<T>> list,boolean affectsSelection) {
		addObjective(o, list, null, affectsSelection);
	}

	public final void addObjective(UT2004FitnessFunction<T> o, ArrayList<UT2004FitnessFunction<T>> list, Statistic override, boolean affectsSelection) {
		list.add(o);
		MMNEAT.registerFitnessFunction(o.getClass().getSimpleName(), override, affectsSelection);
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {            
		int botPort = ServerUtil.getAvailablePort();
		int controlPort = ServerUtil.getAvailablePort();
		int observePort = ServerUtil.getAvailablePort();
		int gamePort = ServerUtil.getAvailablePort();

		MyUCCWrapperConf config = new MyUCCWrapperConf();
		config.setPlayerPort(gamePort);
		config.setStartOnUnusedPort(false);
		config.setMapName(map);
		config.setGameBotsPack("GameBots2004");
		config.setGameType("BotDeathMatch");
		String botprizeMod = BOTPRIZE ? "?mutator=GameBots2004.BotPrizeMutator" : "";
		// config.setOptions(botprizeMod + "?timelimit=" + evalMinutes +
		// "?fraglimit=0?GoalScore=0?DoUplink=False?UplinkToGamespy=False?SendStats=False?bAllowPrivateChat=False?bAllowTaunts=False?bEnableVoiceChat=False?bAllowLocalBroadcast=False?BotServerPort="
		// + botPort + "?ControlServerPort=" + controlPort +
		// "?ObservingServerPort=" + observePort);
		config.setOptions(botprizeMod
				+ "?fraglimit=0?GoalScore=0?DoUplink=False?UplinkToGamespy=False?SendStats=False?bAllowPrivateChat=False?bAllowTaunts=False?bEnableVoiceChat=False?bAllowLocalBroadcast=False?BotServerPort="
				+ botPort + "?ControlServerPort=" + controlPort + "?ObservingServerPort=" + observePort);
		config.setUnrealHome(Parameters.parameters.stringParameter("utDrive") + ":\\" + Parameters.parameters.stringParameter("utPath"));

		MyUCCWrapper ucc = null;
		Pair<double[], double[]> result = null;
		int attempts = 1;
		while (result == null) {
			System.out.println("Eval attempt " + (attempts++));
			try {
				ucc = new MyUCCWrapper(config);
				IUT2004Server server = ucc.getUTServer();
				System.out.println(botPort + ": Confirming empty server");
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
					// Store evolving bot and opponents
					BotController[] allBots = new BotController[this.opponents.length + 1];
					allBots[0] = controller;
					System.arraycopy(opponents, 0, allBots, 1, opponents.length);
					GameDataCollector[] collectors = ControllerBot.launchBot(
                                                        server, "EvolvingBot" + gamePort, allBots,
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
			} catch (ComponentCantStartException ccse) {
				System.out.println("EXCEPTION: Can't start the server. Failed eval. Destroy server");
				ServerUtil.destroyServer(ucc, true);
				result = null;
			} finally {
				if (result == null) {
					System.out.println("Evaluation failed: repeat: " + botPort);
				}
			}
		}

		return result;
	}

        @Override
	public double getTimeStamp() {
		// Can the game time be retrieved?
		return 0; // Not correct
	}

        @Override
	public int numObjectives() {
		return fitness.size();
	}

	@Override
	public int numOtherScores() {
		return others.size();
	}

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

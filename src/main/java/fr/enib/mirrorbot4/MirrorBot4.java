package fr.enib.mirrorbot4;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.params.IRemoteAgentParameters;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004AStarPathPlanner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004GetBackToNavGraph;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004RunStraight;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.ut2004.bots.MultiBotLauncher;
import edu.southwestern.tasks.ut2004.server.BotKiller;

/**
 * Runs the mirrorBot as a Java Application
 * @author Mihai Polceanu
 */
@SuppressWarnings("rawtypes")
public class MirrorBot4 extends UT2004BotModuleController{
	private MyNavigator myNav = null;
	private Brain brain = null;
	private RayData rayData = null;
	private double lastTime = 0.0;
	private boolean initTime = false;

	@Override
	/**
	 * sets up the bot's weapon preferences (true = primary fire, false = secondary fire)
	 * @param bot	
	 */
	public void prepareBot(UT2004Bot bot){
		// DEFINE WEAPON PREFERENCES
		weaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, false);
		weaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, true);
		//weaponPrefs.addGeneralPref(ItemType.LINK_GUN, false);
		weaponPrefs.addGeneralPref(UT2004ItemType.LIGHTNING_GUN, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.SHOCK_RIFLE, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.ROCKET_LAUNCHER, true);
		//weaponPrefs.addGeneralPref(ItemType.LINK_GUN, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.ASSAULT_RIFLE, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, false);
		weaponPrefs.addGeneralPref(UT2004ItemType.BIO_RIFLE, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.SHIELD_GUN, true);
	}

	/**
	 * loads the bot into the game with it's name and the skill level
	 * @return returns the initialization parameters
	 */
	@Override
	public Initialize getInitializeCommand(){
		Initialize init = null;

		try{
			init = new Initialize();
		}
		catch (Exception e) {}

		if (init != null){
			init.setName("MirrorBot");
			init.setAutoPickupOff(false);
			init.setDesiredSkill(2+(int)(Math.random()*5));
			init.setSkin("HumanFemaleA.EgyptFemaleA");
		}

		return init;
	}

	/**
	 * initializes the bot's navigation functions
	 * (the parameters are not used, because this method was inherited from the interface)
	 * @param gameInfo
	 * @param currentConfig
	 * @param init
	 */
	@Override
	public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init){
		rayData = new RayData(this);
		if (myNav != null){
			myNav.getRunner().setRayData(rayData);
		}
		brain = new Brain(this, rayData);

		getNavigation().getPathExecutor().getState().addStrongListener(new FlagListener<IPathExecutorState>(){
			@Override
			public void flagChanged(IPathExecutorState changedValue){
				brain.pathExecutorStateChange(changedValue.getState());
			}			
		});
	}

	@Override
	/**
	 * this method was inherited from the interface
	 * @param gameInfo
	 * @param config
	 * @param init
	 * @param self
	 */
	public void botFirstSpawn(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self){
	}

	@Override
	/**
	 * this method was inherited from the interface
	 */
	public void beforeFirstLogic(){
	}

	@Override
	/**
	 * 
	 */
	public void logic() throws PogamutException{
		//Added By Adina
		if (game.getTime() > getParams().getEvalSeconds()) {
			endEval();
		}
		try{
			if (!initTime){
				lastTime = System.currentTimeMillis();
				initTime = true;
			}
			double dt = (System.currentTimeMillis() - lastTime)/1000.0;
			lastTime = System.currentTimeMillis();

			if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
				System.out.println(info.getName() + ":");
			}
			brain.execute(dt);
		}
		catch (Exception e){
			System.out.println("BUG found");
			e.printStackTrace();
		}
	}
	
	//TODO//
	public void endEval() {
		BotKiller.killBot(bot);
	}

	public MirrorBotParameters getParams() {
		return (MirrorBotParameters) bot.getParams();
	}

	@Override
	/**
	 * detects bot death and cleans up the data for respawn
	 * @param event (the bot dies)
	 */
	public void botKilled(BotKilled event){
		if (brain != null){
			brain.deathClean();
		}
	}

	/**
	 * @return returns the bot's brain (central controller)
	 */
	public Brain getBrain(){
		return brain;
	}

	/**
	 * connects the bot to the server and sets up the host and port number
	 * @param args
	 * @throws PogamutException
	 */
	public static void main(String args[]) throws PogamutException{
		Class[] botClasses = new Class[] {MirrorBot4.class};

		IRemoteAgentParameters[] params = new IRemoteAgentParameters[] {new MirrorBotParameters()};
		MultiBotLauncher.launchMultipleBots(botClasses, params, "localhost", 3000);//launchMultipleBots(botClasses, params, "localhost", 3000);

		// This original main method did more complicated command line parsing
//		String host = "localhost";
//		int port = 3000;
//
//		if (args.length > 0){
//			String customHost = args[0];
//			host = customHost;
//			System.out.println("Using custom host: "+host);
//		}
//		else{
//			System.out.println("Custom host not specified. Resuming with default host: "+host);
//		}
//
//		if (args.length > 1){
//			String customPort = args[1];
//			try{
//				int custPort = Integer.parseInt(customPort);
//				port = custPort;
//				System.out.println("Using custom port: "+port);
//			}
//			catch (Exception e){
//				System.out.println("Invalid port. Expecting numeric. Resuming with default port: "+port);
//			}
//		}
//		else{
//			System.out.println("Custom port not specified. Resuming with default port: "+port);
//		}
//
//		while (true){
//			try{
//				UT2004BotRunner runner = new UT2004BotRunner(MirrorBot4.class, "MirrorBot", host, port);
//				runner.setMain(true);
//				runner.setLogLevel(Level.OFF);
//				runner.startAgent();
//				Thread.sleep(1234);
//			}
//			catch (ComponentCantStartException e){
//				Throwable cause = e.getCause();
//				if (cause instanceof ConnectionException){
//					System.out.println("Connection to server failed... retrying");
//					e.printStackTrace();
//				}
//				else if (cause instanceof BusStoppedInterruptedException){
//					e.printStackTrace();
//					System.out.println("Aborting...");
//					break;
//				}
//				else{
//					e.printStackTrace();
//					System.out.println("Some other cause for ComponentCantStartException... retrying");
//				}
//			}
//			catch (Exception e){
//				e.printStackTrace();
//				System.out.println("Some other exception... retrying");
//			}
//		}
	}

	/**
	 * initializes the bot's pathfinding modules
	 * @param bot (bot to be used)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void initializePathFinding(UT2004Bot bot){
		//System.out.println("SUCCESS HACK");
		Logger myLog = Logger.getAnonymousLogger();
		if (myLog == null){
			myLog = log;
		}
		else{
			myLog.setLevel(Level.OFF);
		}

		ut2004PathPlanner = new UT2004AStarPathPlanner(bot);
		fwMap        = new FloydWarshallMap(bot);
		myNav        = new MyNavigator(bot, myLog);
		// J. Schrum: 5/25/18: Added parameters for new constructor for Pogamut 3.7.0
		MyPathExecutor<ILocated> pathExecutor = new MyPathExecutor<ILocated>(bot, info, move, myNav, myLog);

		pathExecutor.addStuckDetector(new UT2004TimeStuckDetector(bot, 3000, 100000)); // if the bot does not move for 3 seconds, considered that it is stuck
		pathExecutor.addStuckDetector(new UT2004PositionStuckDetector(bot));           // watch over the position history of the bot, if the bot does not move sufficiently enough, consider that it is stuck
		pathExecutor.addStuckDetector(new UT2004DistanceStuckDetector(bot));           // watch over distances to target

		UT2004GetBackToNavGraph getBackToNavGraph = new UT2004GetBackToNavGraph(bot, info, move);
		UT2004RunStraight runStraight = new UT2004RunStraight(bot, info, move);
		navigation = new UT2004Navigation(bot, pathExecutor, fwMap, getBackToNavGraph, runStraight);    
	}
}

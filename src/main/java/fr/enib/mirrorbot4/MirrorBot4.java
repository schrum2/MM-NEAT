package fr.enib.mirrorbot4;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.communication.connection.exception.ConnectionException;
import cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch.BusStoppedInterruptedException;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
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
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;

/**
 * Runs the mirrorBot as a Java Application
 * @author Mihai Polceanu
 */
public class MirrorBot4 extends UT2004BotModuleController{
	private MyNavigator myNav = null;
    private Brain brain = null;
	private RayData rayData = null;
    private double lastTime = 0.0;
    private boolean initTime = false;

    @Override
    /**
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
     * 
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
		}
		
		return init;
    }

    /**
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
     * @param gameInfo
     * @param config
     * @param init
     * @param self
     */
    public void botFirstSpawn(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self){
    }

    @Override
    public void beforeFirstLogic(){
    }

    @Override
    public void logic() throws PogamutException{
		try{
			if (!initTime){
				lastTime = System.currentTimeMillis();
				initTime = true;
			}
			double dt = (System.currentTimeMillis() - lastTime)/1000.0;
			lastTime = System.currentTimeMillis();

			System.out.println(info.getName() + ":");
			brain.execute(dt);
		}
		catch (Exception e){
			System.out.println("BUG found");
			e.printStackTrace();
		}
    }

    @Override
    public void botKilled(BotKilled event){
		if (brain != null){
			brain.deathClean();
		}
    }
	
	public Brain getBrain(){
		return brain;
	}

    public static void main(String args[]) throws PogamutException{
		String host = "localhost";
		int port = 3000;
		
		if (args.length > 0){
			String customHost = args[0];
			host = customHost;
			System.out.println("Using custom host: "+host);
		}
		else{
			System.out.println("Custom host not specified. Resuming with default host: "+host);
		}
		
		if (args.length > 1){
			String customPort = args[1];
			try{
				int custPort = Integer.parseInt(customPort);
				port = custPort;
				System.out.println("Using custom port: "+port);
			}
			catch (Exception e){
				System.out.println("Invalid port. Expecting numeric. Resuming with default port: "+port);
			}
		}
		else{
			System.out.println("Custom port not specified. Resuming with default port: "+port);
		}
		
		while (true){
			try{
				UT2004BotRunner runner = new UT2004BotRunner(MirrorBot4.class, "MirrorBot", host, port);
				runner.setMain(true);
				runner.setLogLevel(Level.OFF);
				runner.startAgent();
				Thread.sleep(1234);
			}
			catch (ComponentCantStartException e){
				Throwable cause = e.getCause();
				if (cause instanceof ConnectionException){
					System.out.println("Connection to server failed... retrying");
					e.printStackTrace();
				}
				else if (cause instanceof BusStoppedInterruptedException){
					e.printStackTrace();
					System.out.println("Aborting...");
					break;
				}
				else{
					e.printStackTrace();
					System.out.println("Some other cause for ComponentCantStartException... retrying");
				}
			}
			catch (Exception e){
				e.printStackTrace();
				System.out.println("Some other exception... retrying");
			}
		}
    }

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

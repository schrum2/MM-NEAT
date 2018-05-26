package pogamut.navigationbot;

import java.util.logging.Level;

import cz.cuni.amis.introspection.java.JProp;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavPoints;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathExecutorStuckState;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.astar.UT2004AStar;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMeshModule;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;

/**
 * Example of Simple Pogamut bot, that randomly walks around the map. 
 * 
 * <p><p> 
 * Bot is able to handle movers as well as teleporters. 
 * 
 * <p><p> 
 * It also implements player-following, that is, if it sees a player, 
 * it will start to navigate to it.
 * 
 * <p><p>
 * We recommend you to try it on map DM-1on1-Albatross or CTF-LostFaith or DM-Flux2.
 * 
 * <p><p>
 * This bot also contains an example of {@link TabooSet} usage.
 * 
 * <p><p>
 * Bot also instantiates {@link UT2004PathAutoFixer} that automatically removes bad-edges
 * from navigation graph of UT2004. Note that Pogamut bot's cannot achieve 100% safe navigation
 * inside UT2004 maps mainly due to edges that does not contain enough information on how
 * to travel them, we're trying our best, but some edges inside navigation graph exported
 * from UT2004 cannot be traveled with our current implementation.
 * 
 * <p><p>
 * You may control the way the bot informs you about its decisions via {@link #shouldLog} and {@link #shouldSpeak} flags.
 * 
 * <p><p>
 * We advise to change chat settings within UT2004 via ESCAPE -> Settings -> HUD -> Max. Chat Count -> set to 8 (max).
 *
 * @author Rudolf Kadlec aka ik
 * @author Jakub Gemrot aka Jimmy
 */
@AgentScoped
public class NavigationBot extends UT2004BotModuleController {

    /**
     * Taboo set is working as "black-list", that is you might add some
     * NavPoints to it for a certain time, marking them as "unavailable".
     */
    protected TabooSet<NavPoint> tabooNavPoints;
    
    /**
     * Current navigation point we're navigating to.
     */
    protected NavPoint targetNavPoint;
    
    /**
     * Path auto fixer watches for navigation failures and if some navigation
     * link is found to be unwalkable, it removes it from underlying navigation
     * graph.
     *
     * Note that UT2004 navigation graphs are some times VERY stupid or contains
     * VERY HARD TO FOLLOW links...
     */
    protected UT2004PathAutoFixer autoFixer;
    
    /**
     * Standard {@link UT2004BotModuleController#getNavigation()} is using {@link FloydWarshallMap} to find the path.
     * <p><p>
     * This {@link UT2004Navigation} is initialized using {@link UT2004BotModuleController#getAStar()} and can be used to confirm, that
     * {@link UT2004AStar} is working in the map.
     */
    protected UT2004Navigation navigationAStar;
    
    /**
     * {@link NavigationBot#talking} state.
     */
    protected int talking;
    
    /**
     * Whether to use {@link #navigationAStar} and {@link UT2004AStar} (== true).
     * <p><p>
     * Can be configured from NetBeans plugin during runtime.
     */
    @JProp
    public boolean useAStar = false;
    
    /**
     * Whether to use {@link #nmNav} or standard {@link UT2004BotModuleController#getNavigation()}.
     * <p><p>
     * Can be configured from NetBeans plugin during runtime.
     * <p><p>
     * Note that you must have corresponding .navmesh file for a current played map within directory ./navmesh, more info available at {@link NavMeshModule}.
     * <p><p>
     * Note that navigation bot comes with only three navmeshes DM-TrainingDay, DM-1on1-Albatross and DM-Flux2 (see ./navmesh folder within the project folder).
     */
    @JProp
    public boolean useNavMesh = false;
    
    /**
     * Whether we should draw the navmesh before we start running using {@link #nmNav} or standard {@link UT2004BotModuleController#getNavigation()}.
     * <p><p>
     * Can be configured from NetBeans plugin during runtime.
     */
    @JProp
    public boolean drawNavMesh = true;

    /**
     * Whether we should speak using in game communication within {@link #say(String)}.
     */
    public boolean shouldSpeak = true;
    
    /**
     * Whether to LOG messages within {@link #say(String)}.
     */
    public boolean shouldLog = false;
    
    /**
     * What log level to use.  
     */
    public Level navigationLogLevel = Level.WARNING;
    
    /**
     * Here we will store either {@link UT2004BotModuleController#getNavigation()} or {@link #navigationAStar} according to {@link #useAStar}.
     */
    protected IUT2004Navigation navigationToUse;

	private boolean navMeshDrawn = false;

	private int waitForMesh;

	private double waitingForMesh;

	private boolean offMeshLinksDrawn = false;

	private int waitForOffMeshLinks;

	private double waitingForOffMeshLinks;

    /**
     * Here we can modify initializing command for our bot.
     *
     * @return
     */
    @Override
    public Initialize getInitializeCommand() {
        return new Initialize().setName("NavigationBot");
    }
    
    @Override
    public void mapInfoObtained() {
    	// YOU CAN USE navBuilder IN HERE
    	
    	// IN WHICH CASE YOU SHOULD UNCOMMENT FOLLOWING LINE AFTER EVERY CHANGE
    	navMeshModule.setReloadNavMesh(true); // tells NavMesh to reconstruct OffMeshPoints    	
    }

    /**
     * The bot is initialized in the environment - a physical representation of
     * the bot is present in the game.
     *
     * @param config information about configuration
     * @param init information about configuration
     */
    @SuppressWarnings("unchecked")
    @Override
    public void botInitialized(GameInfo gameInfo, ConfigChange config, InitedMessage init) {
        // initialize taboo set where we store temporarily unavailable navpoints
        tabooNavPoints = new TabooSet<NavPoint>(bot);

        // auto-removes wrong navigation links between navpoints
        autoFixer = new UT2004PathAutoFixer(bot, navigation.getPathExecutor(), fwMap, aStar, navBuilder);

        // IMPORTANT
        // adds a listener to the path executor for its state changes, it will allow you to 
        // react on stuff like "PATH TARGET REACHED" or "BOT STUCK"
        navigation.getPathExecutor().getState().addStrongListener(new FlagListener<IPathExecutorState>() {

            @Override
            public void flagChanged(IPathExecutorState changedValue) {
                pathExecutorStateChange(changedValue);
            }
        });
        
        nmNav.getPathExecutor().getState().addStrongListener(new FlagListener<IPathExecutorState>() {

            @Override
            public void flagChanged(IPathExecutorState changedValue) {
                pathExecutorStateChange(changedValue);
            }
        });
        
        navigationAStar = new UT2004Navigation(bot, navigation.getPathExecutor(), aStar, navigation.getBackToNavGraph(), navigation.getRunStraight());          
        navigationAStar.getLog().setLevel(navigationLogLevel);
        
        navigation.getLog().setLevel(navigationLogLevel);
        
        nmNav.setLogLevel(navigationLogLevel);
    }

    /**
     * The bot is initialized in the environment - a physical representation of
     * the bot is present in the game.
     *
     * @param config information about configuration
     * @param init information about configuration
     */
    @Override
    public void botFirstSpawn(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self) {
        // receive logs from the navigation so you can get a grasp on how it is working
        //navigation.getPathExecutor().getLog().setLevel(Level.ALL);
    	//nmNav.setLogLevel(Level.ALL);
    	//navigationAStar.getPathExecutor().getLog().setLevel(Level.ALL);
    }

    /**
     * This method is called only once right before actual logic() method is
     * called for the first time.
     */
    @Override
    public void beforeFirstLogic() {
    }

    /**
     * Main method that controls the bot - makes decisions what to do next. It
     * is called iteratively by Pogamut engine every time a synchronous batch
     * from the environment is received. This is usually 4 times per second - it
     * is affected by visionTime variable, that can be adjusted in GameBots ini
     * file in UT2004/System folder.
     */
    @Override
    public void logic() {
        // mark that another logic iteration has began
        say("--- Logic iteration ---");
        
        // decide which navigation to use
        chooseNavigationToUse();
        if (navigationToUse == nmNav && drawNavMesh) {
        	if (!drawNavMesh()) return;
        	if (!drawOffMeshLinks()) return;
        }

        if (players.canSeePlayers() || navigationToUse.getCurrentTargetPlayer() != null) {
            // we can see some player / is navigating to some point where we lost the player from sight
            // => navigate to player
            handlePlayerNavigation();
        } else {
            // no player can be seen
            // => navigate to navpoint
            handleNavPointNavigation();
        }
    }

    private void chooseNavigationToUse() {
    	 if (useAStar) {
         	if (navigationToUse != navigationAStar) {
         		say("Using UT2004AStar to find path.");
         		if (navigationToUse != null) navigationToUse.stopNavigation();
         		navigationToUse = navigationAStar;
         		info.getBotName().setInfo("UT2004-ASTAR");
         	}
         } else
    	 if (useNavMesh) {
    		 if (nmNav.isAvailable()) {
	    		 if (navigationToUse != nmNav) {
	    			 say("Using NavMesh for navigation.");
	    			 if (navigationToUse != null) navigationToUse.stopNavigation();
	    		 	 navigationToUse = nmNav;
	    		 	info.getBotName().setInfo("NAVMESH");
	    		 }
    		 } else {
    			 log.warning("NavMesh not available! See startup log for more details.");
    		 }
    	 }
    	 if (navigationToUse == null || (!useAStar && !useNavMesh)) {
        	if (navigationToUse != navigation) {
	         	say("Using FloydWarshallMap to find path.");
		        if (navigationToUse != null) navigationToUse.stopNavigation();
		        navigationToUse = navigation;
		        info.getBotName().setInfo("FW");
        	}
         }		
	}

	private void handlePlayerNavigation() {
        if (navigationToUse.isNavigating() && navigationToUse.getCurrentTargetPlayer() != null) {
            // WE'RE NAVIGATING TO SOME PLAYER
            logNavigation();
            return;
        }
        
        // NAVIGATION HAS STOPPED ... 
        // => we need to choose another player to navigate to

        Player player = players.getNearestVisiblePlayer();
        if (player == null) {
            // NO PLAYERS AT SIGHT
            // => navigate to random navpoint
            handleNavPointNavigation();
            return;
        }

        // CHECK DISTANCE TO THE PLAYER ...
        if (info.getLocation().getDistance(player.getLocation()) < UT2004Navigation.AT_PLAYER) {
            // PLAYER IS NEXT TO US... 
            // => talk to player			
            talkTo(player);
            return;
        }

        navigationToUse.navigate(player);
        logNavigation();
    }

    private void handleNavPointNavigation() {
        if (navigationToUse.isNavigatingToNavPoint()) {
            // IS TARGET CLOSE & NEXT TARGET NOT SPECIFIED?
            while (navigationToUse.getContinueTo() == null && navigationToUse.getRemainingDistance() < 400) {
                // YES, THERE IS NO "next-target" SET AND WE'RE ABOUT TO REACH OUR TARGET!
            	NavPoint nextNavPoint = getRandomNavPoint();
            	say("EXTENDING THE PATH: " + NavPoints.describe(nextNavPoint));
                navigationToUse.setContinueTo(nextNavPoint);
                // note that it is WHILE because navigation may immediately eat up "next target" and next target may be actually still too close!
            }

            // WE'RE NAVIGATING TO SOME NAVPOINT
            logNavigation();
            return;
        }
        
        // NAVIGATION HAS STOPPED ... 
        // => we need to choose another navpoint to navigate to
        // => possibly follow some players ...

        targetNavPoint = getRandomNavPoint();
        if (targetNavPoint == null) {
            log.severe("COULD NOT CHOOSE ANY NAVIGATION POINT TO RUN TO!!!");
            if (world.getAll(NavPoint.class).size() == 0) {
                log.severe("world.getAll(NavPoint.class).size() == 0, there are no navigation ponits to choose from! Is exporting of nav points enabled in GameBots2004.ini inside UT2004?");
            }
            config.setName("NavigationBot [CRASHED]");
            return;
        }

        talking = 0;

        say("CHOOSING FIRST NAVPOINT TO RUN TO: " + NavPoints.describe(targetNavPoint));
        navigationToUse.navigate(targetNavPoint);
        logNavigation();
    }

    private void logNavigation() {
        // log how many navpoints & items the bot knows about and which is visible    	
        if (navigationToUse.getCurrentTargetPlayer() != null) {
            say("-> " + NavPoints.describe(navigationToUse.getCurrentTargetPlayer()));
        } else {
            say("-> " + NavPoints.describe(navigationToUse.getCurrentTarget()));
        }
        int pathLeftSize = navigationToUse.getPathExecutor().getPath() == null ? 0 : navigationToUse.getPathExecutor().getPath().size() - navigationToUse.getPathExecutor().getPathElementIndex();
        say("Path points left:   " + pathLeftSize);
        say("Remaining distance: " + navigationToUse.getRemainingDistance());
        say("Visible navpoints:  " + world.getAllVisible(NavPoint.class).size() + " / " + world.getAll(NavPoint.class).size());
        say("Visible items:      " + items.getVisibleItems().values() + " / " + world.getAll(Item.class).size());
        say("Visible players:    " + players.getVisiblePlayers().size());       
    }

    private void talkTo(Player player) {
        // FACE THE PLAYER
        move.turnTo(player);

        // SEND MESSAGES
        switch (talking) {
            case 0:
                say("Hi!");
                break;
            case 4:
                say("Howdy!");
                break;
            case 10:
                say("I'm NavigationBot made to fool around and test Pogamut's navigation stuff!");
                break;
            case 18:
                say("My work is extremely important.");
                break;
            case 24:
                say("So do not interrupt me, ok?");
                break;
            case 26:
                if (random.nextDouble() > 0.5) {
                    move.jump();
                }
                break;
            case 40:
                say(getRandomLogoutMessage());
                break;
        }

        ++talking;
        if (talking > 40) {
            talking = 25;
        }
    }

    private String getRandomLogoutMessage() {
        switch (random.nextInt(8)) {
            case 0:
                return "I would appriciate if you log out or switch to SPECTATE mode.";
            case 1:
                return "Would you please log out or switch to SPECTATE mode.";
            case 2:
                return "Just log out, will ya?";
            case 3:
                return "As I've said, I'M SOMEONE, so log out, ok?";
            case 4:
                return "I can see you don't get it... LOGOUT! OK!";
            case 5:
                return "I hate when humans are so clueless... just press ESCAPE key and press Spectate button, that's all I want!";
            case 6:
                return "I guess you do not know how to switch to spectate mode, right? Just press ESCAPE key and press Spectate button";
            default:
                return "AHAHAHAYYYAAAA!";
        }

    }
    
    private boolean drawNavMesh() { 
		if (!navMeshDrawn) {
    		navMeshDrawn = true;
    		say("Drawing NavMesh...");
    		navMeshModule.getNavMeshDraw().clearAll();
    		navMeshModule.getNavMeshDraw().draw(true, false);
    		say("Okey, drawing commands issued, now we have to wait a bit till it gets drawn completely...");
    		
    		waitForMesh = navMeshModule.getNavMesh().getPolys().size() / 35;
    		waitingForMesh = -info.getTimeDelta();
    	}
		
		if (waitForMesh > 0) {
    		waitForMesh -= info.getTimeDelta();
    		waitingForMesh += info.getTimeDelta();
    		if (waitingForMesh > 2) {
    			waitingForMesh = 0;
    			say(((int)Math.round(waitForMesh)) + "s...");
    		}
    		if (waitForMesh > 0) {
    			return false;
    		}    		
    	}
		
		return true;
	}
	
	private boolean drawOffMeshLinks() { 		
		if (!offMeshLinksDrawn) {
			offMeshLinksDrawn = true;
			
			if (navMeshModule.getNavMesh().getOffMeshPoints().size() == 0) {
				say("Ha! There are no off-mesh points / links within this map!");
				return true;
			}
			
			say("Drawing OffMesh Links...");
    		navMeshModule.getNavMeshDraw().draw(false, true);
    		say("Okey, drawing commands issued, now we have to wait a bit till it gets drawn completely...");    		
    		waitForOffMeshLinks = navMeshModule.getNavMesh().getOffMeshPoints().size() / 10;
    		waitingForOffMeshLinks = -info.getTimeDelta();
    	}
		
		if (waitForOffMeshLinks > 0) {
			waitForOffMeshLinks -= info.getTimeDelta();
			waitingForOffMeshLinks += info.getTimeDelta();
    		if (waitingForOffMeshLinks > 2) {
    			waitingForOffMeshLinks = 0;
    			say(((int)Math.round(waitForOffMeshLinks)) + "s...");
    		}
    		if (waitForOffMeshLinks > 0) {
    			return false;
    		}    		
    	}
		
		return true;
	}

	private void say(String text) {
		if (shouldSpeak) { 
			body.getCommunication().sendGlobalTextMessage(text);
		}
		if (shouldLog) {
			say(text);
		}
	}

    /**
     * Called each time our bot die. Good for reseting all bot state dependent
     * variables.
     *
     * @param event
     */
    @Override
    public void botKilled(BotKilled event) {
        navigation.stopNavigation();
    }

    /**
     * Path executor has changed its state (note that {@link UT2004BotModuleController#getPathExecutor()}
     * is internally used by
     * {@link UT2004BotModuleController#getNavigation()} as well!).
     *
     * @param event
     */
    protected void pathExecutorStateChange(IPathExecutorState event) {
        switch (event.getState()) {
            case PATH_COMPUTATION_FAILED:
                // if path computation fails to whatever reason, just try another navpoint
                // taboo bad navpoint for 3 minutes
                tabooNavPoints.add(targetNavPoint, 180);
                break;

            case TARGET_REACHED:
                // taboo reached navpoint for 3 minutes
                tabooNavPoints.add(targetNavPoint, 180);
                break;

            case STUCK:
            	UT2004PathExecutorStuckState stuck = (UT2004PathExecutorStuckState)event;
            	if (stuck.isGlobalTimeout()) {
            		say("UT2004PathExecutor GLOBAL TIMEOUT!");
            	} else {
            		say(stuck.getStuckDetector() + " reported STUCK!");
            	}
            	if (stuck.getLink() == null) {
            		say("STUCK LINK is NOT AVAILABLE!");
            	} else {
            		say("Bot has stuck while running from " + stuck.getLink().getFromNavPoint().getId() + " -> " + stuck.getLink().getToNavPoint().getId());
            	}
            	
                // the bot has stuck! ... target nav point is unavailable currently
                tabooNavPoints.add(targetNavPoint, 60);
                break;

            case STOPPED:
                // path execution has stopped
                targetNavPoint = null;
                break;
        }
    }

    /**
     * Randomly picks some navigation point to head to.
     *
     * @return randomly choosed navpoint
     */
    protected NavPoint getRandomNavPoint() {
        say("Picking new target navpoint.");

        // choose one feasible navpoint (== not belonging to tabooNavPoints) randomly
        NavPoint chosen = MyCollections.getRandomFiltered(getWorldView().getAll(NavPoint.class).values(), tabooNavPoints);

        if (chosen != null) {
            return chosen;
        }

        log.warning("All navpoints are tabooized at this moment, choosing navpoint randomly!");

        // ok, all navpoints have been visited probably, try to pick one at random
        return MyCollections.getRandom(getWorldView().getAll(NavPoint.class).values());
    }

    public static void main(String args[]) throws PogamutException {
        // wrapped logic for bots executions, suitable to run single bot in single JVM

        // you can set the log level to FINER to see (almost) all logs 
        // that describes decision making behind movement of the bot as well as incoming environment events
		// however note that in NetBeans this will cause your bot to lag heavilly (in Eclipse it is ok)
        new UT2004BotRunner(NavigationBot.class, "NavigationBot").setMain(true).setLogLevel(Level.WARNING).startAgent();
    }
}

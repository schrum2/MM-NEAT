package pogamut.navigationbot;

import java.util.Collection;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;

/**
 * Example of Simple Pogamut bot, that randomly walks around the map.
 * <p>
 * <p>
 * Bot is able to handle movers as well as teleporters.
 * <p>
 * <p>
 * It also implements player-following, that is, if it sees a player, it will
 * start to navigate to it.
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
	 * {@link NavigationBot#talking} state.
	 */
	protected int talking;

	/**
	 * Here we can modify initializing command for our bot.
	 *
	 * @return
	 */
	@Override
	public Initialize getInitializeCommand() {
		return new Initialize().setName("NavigationBot");
	}

	/**
	 * The bot is initialized in the environment - a physical representation of
	 * the bot is present in the game.
	 *
	 * @param config
	 *            information about configuration
	 * @param init
	 *            information about configuration
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void botInitialized(GameInfo gameInfo, ConfigChange config, InitedMessage init) {
		// initialize taboo set where we store temporarily unavailable navpoints
		tabooNavPoints = new TabooSet<NavPoint>(bot);

		// auto-removes wrong navigation links between navpoints
		autoFixer = new UT2004PathAutoFixer(bot, pathExecutor, fwMap, navBuilder);

		// IMPORTANT
		// adds a listener to the path executor for its state changes, it will
		// allow you to
		// react on stuff like "PATH TARGET REACHED" or "BOT STUCK"
		pathExecutor.getState().addStrongListener(new FlagListener<IPathExecutorState>() {
			@Override
			public void flagChanged(IPathExecutorState changedValue) {
				pathExecutorStateChange(changedValue.getState());
			}
		});
	}

	/**
	 * The bot is initilized in the environment - a physical representation of
	 * the bot is present in the game.
	 *
	 * @param config
	 *            information about configuration
	 * @param init
	 *            information about configuration
	 */
	@Override
	public void botFirstSpawn(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self) {
		// receive logs from the navigation so you can get a grasp on how it is
		// working
		pathExecutor.getLog().setLevel(Level.ALL);
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
		log.info("--- Logic iteration ---");

		if (players.canSeePlayers() || navigation.getCurrentTargetPlayer() != null) {
			// we can see some player / is navigating to some point where we
			// lost the player from sight
			// => navigate to player
			handlePlayerNavigation();
		} else {
			// no player can be seen
			// => navigate to navpoint
			handleNavPointNavigation();
		}
	}

	private void handlePlayerNavigation() {
		if (navigation.isNavigating() && navigation.getCurrentTargetPlayer() != null) {
			// WE'RE NAVIGATING TO SOME PLAYER
			logNavigation();
			return;
		}

		config.setName("NavigationBot [PLAYER]");

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

		navigation.navigate(player);
		logNavigation();
	}

	private void handleNavPointNavigation() {
		if (navigation.isNavigating()) {
			// WE'RE NAVIGATING TO SOME NAVPOINT
			logNavigation();
			return;
		}

		config.setName("NavigationBot [NAVPOINT]");

		// NAVIGATION HAS STOPPED ...
		// => we need to choose another navpoint to navigate to
		// => possibly follow some players ...

		targetNavPoint = getRandomNavPoint();
		if (targetNavPoint == null) {
			log.severe("COULD NOT CHOOSE ANY NAVIGATION POINT TO RUN TO!!!");
			if (world.getAll(NavPoint.class).size() == 0) {
				log.severe(
						"world.getAll(NavPoint.class).size() == 0, there are no navigation ponits to choose from! Is exporting of nav points enabled in GameBots2004.ini inside UT2004?");
			}
			config.setName("NavigationBot [CRASHED]");
			return;
		}

		talking = 0;

		navigation.navigate(targetNavPoint);
		logNavigation();
	}

	private void logNavigation() {
		// log how many navpoints & items the bot knows about and which is
		// visible
		if (navigation.getCurrentTargetPlayer() != null) {
			log.info("Pursuing player:    " + navigation.getCurrentTargetPlayer());
		} else {
			log.info("Navigating to:      " + navigation.getCurrentTarget());
		}
		int pathLeftSize = pathExecutor.getPath() == null ? 0
				: pathExecutor.getPath().size() - pathExecutor.getPathElementIndex();
		log.info("Path points left:   " + pathLeftSize);
		log.info("Remaining distance: " + pathExecutor.getRemainingDistance());
		log.info("Visible navpoints:  " + world.getAllVisible(NavPoint.class).size() + " / "
				+ world.getAll(NavPoint.class).size());
		log.info("Visible items:      " + items.getVisibleItems().values() + " / " + world.getAll(Item.class).size());
		log.info("Visible players:    " + players.getVisiblePlayers().size());
	}

	private void talkTo(Player player) {
		// FACE THE PLAYER
		move.turnTo(player);

		// SEND MESSAGES
		switch (talking) {
		case 0:
			body.getCommunication().sendGlobalTextMessage("Hi!");
			break;
		case 4:
			body.getCommunication().sendGlobalTextMessage("Howdy!");
			break;
		case 10:
			body.getCommunication().sendGlobalTextMessage(
					"I'm NavigationBot made to fool around and test Pogamut's navigation stuff!");
			break;
		case 18:
			body.getCommunication().sendGlobalTextMessage("My work is extremely important.");
			break;
		case 24:
			body.getCommunication().sendGlobalTextMessage("So do not interrupt me, ok?");
			break;
		case 26:
			if (random.nextDouble() > 0.5) {
				move.jump();
			}
			break;
		case 40:
			body.getCommunication().sendGlobalTextMessage(getRandomLogoutMessage());
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
	 * Path executor has changed its state (note that
	 * {@link UT2004BotModuleController#getPathExecutor()} is internally used by
	 * {@link UT2004BotModuleController#getNavigation()} as well!).
	 *
	 * @param state
	 */
	protected void pathExecutorStateChange(PathExecutorState state) {
		switch (state) {
		case PATH_COMPUTATION_FAILED:
			// if path computation fails to whatever reason, just try another
			// navpoint
			// taboo bad navpoint for 3 minutes
			tabooNavPoints.add(targetNavPoint, 180);
			break;

		case TARGET_REACHED:
			// taboo reached navpoint for 3 minutes
			tabooNavPoints.add(targetNavPoint, 180);
			break;

		case STUCK:
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
		log.info("Picking new target navpoint.");

		// choose one feasible navpoint (== not belonging to tabooNavPoints)
		// randomly
		NavPoint chosen = MyCollections.getRandomFiltered(getWorldView().getAll(NavPoint.class).values(),
				tabooNavPoints);

		if (chosen != null) {
			return chosen;
		}

		log.warning("All navpoints are tabooized at this moment, choosing navpoint randomly!");

		// ok, all navpoints have been visited probably, try to pick one at
		// random
		return MyCollections.getRandom(getWorldView().getAll(NavPoint.class).values());
	}

	public static void main(String args[]) throws PogamutException {
		// wrapped logic for bots executions, suitable to run single bot in
		// single JVM

		// we're forcingly setting logging to aggressive level FINER so you can
		// see (almost) all logs
		// that describes decision making behind movement of the bot as well as
		// incoming environment events
		new UT2004BotRunner(NavigationBot.class, "NavigationBot").setMain(true).setLogLevel(Level.FINER).startAgent();
	}
}

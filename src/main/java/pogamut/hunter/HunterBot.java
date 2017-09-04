package pogamut.hunter;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.*;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Example of Simple Pogamut bot, that randomly walks around the map searching
 * for preys shooting at everything that is in its way.
 *
 * @author Rudolf Kadlec aka ik
 * @author Jimmy
 */
@SuppressWarnings("rawtypes")
@AgentScoped
public class HunterBot extends UT2004BotModuleController<UT2004Bot> {

	/**
	 * boolean switch to activate engage behavior
	 */
	public boolean shouldEngage = true;
	/**
	 * boolean switch to activate pursue behavior
	 */
	public boolean shouldPursue = true;
	/**
	 * boolean switch to activate rearm behavior
	 */
	public boolean shouldRearm = true;
	/**
	 * boolean switch to activate collect items behavior
	 */
	public boolean shouldCollectItems = true;
	/**
	 * boolean switch to activate collect health behavior
	 */
	public boolean shouldCollectHealth = true;
	/**
	 * how low the health level should be to start collecting health items
	 */
	public int healthLevel = 90;
	/**
	 * how many bot the hunter killed other bots (i.e., bot has fragged them /
	 * got point for killing somebody)
	 */
	public int frags = 0;
	/**
	 * how many times the hunter died
	 */
	public int deaths = 0;

	/**
	 * {@link PlayerKilled} listener that provides "frag" counting + is switches
	 * the state of the hunter.
	 *
	 * @param event
	 */
	@EventListener(eventClass = PlayerKilled.class)
	public void playerKilled(PlayerKilled event) {
		if (event.getKiller().equals(info.getId())) {
			++frags;
		}
		if (enemy == null) {
			return;
		}
		if (enemy.getId().equals(event.getId())) {
			previousState = State.OTHER;
			enemy = null;
		}
	}

	/**
	 * Used internally to maintain the information about the bot we're currently
	 * hunting, i.e., should be firing at.
	 */
	protected Player enemy = null;
	/**
	 * Taboo list of items that are forbidden for some time.
	 */
	protected TabooSet<Item> tabooItems = null;
	@SuppressWarnings("unused")
	private UT2004PathAutoFixer autoFixer;

	/**
	 * Bot's preparation - called before the bot is connected to GB2004 and
	 * launched into UT2004.
	 */
	@Override
	public void prepareBot(UT2004Bot bot) {
		tabooItems = new TabooSet<Item>(bot);

		// add stuck detector that watch over the path-following, if it
		// (heuristicly) finds out that the bot has stuck somewhere,
		// it reports an appropriate path event and the path executor will stop
		// following the path which in turn allows
		// us to issue another follow-path command in the right time
		// if the bot does not move for 3 seconds, considered that it is stuck

		pathExecutor.addStuckDetector(new UT2004TimeStuckDetector(bot, 3000, 10000)); 
		// watch over the position history of the bot, if the bot does not move sufficiently enough, consider that it is stuck
		pathExecutor.addStuckDetector(new UT2004PositionStuckDetector(bot)); 
		// watch over distances to target
		pathExecutor.addStuckDetector(new UT2004DistanceStuckDetector(bot)); 
		 // auto-removes wrong navigation links between navpoints
		autoFixer = new UT2004PathAutoFixer(bot, pathExecutor, fwMap, navBuilder);

		// listeners
		pathExecutor.getState().addListener(new FlagListener<IPathExecutorState>() {
			@SuppressWarnings("incomplete-switch")
			@Override
			public void flagChanged(IPathExecutorState changedValue) {
				switch (changedValue.getState()) {
				case PATH_COMPUTATION_FAILED:
				case STUCK:
					if (item != null) {
						tabooItems.add(item, 10);
					}
					reset();
					break;

				case TARGET_REACHED:
					reset();
					break;
				}
			}
		});

		// DEFINE WEAPON PREFERENCES
		weaponPrefs.addGeneralPref(ItemType.MINIGUN, false);
		weaponPrefs.addGeneralPref(ItemType.MINIGUN, true);
		weaponPrefs.addGeneralPref(ItemType.LINK_GUN, false);
		weaponPrefs.addGeneralPref(ItemType.LIGHTNING_GUN, true);
		weaponPrefs.addGeneralPref(ItemType.SHOCK_RIFLE, true);
		weaponPrefs.addGeneralPref(ItemType.ROCKET_LAUNCHER, true);
		weaponPrefs.addGeneralPref(ItemType.LINK_GUN, true);
		weaponPrefs.addGeneralPref(ItemType.ASSAULT_RIFLE, true);
		weaponPrefs.addGeneralPref(ItemType.FLAK_CANNON, false);
		weaponPrefs.addGeneralPref(ItemType.FLAK_CANNON, true);
		weaponPrefs.addGeneralPref(ItemType.BIO_RIFLE, true);
	}

	/**
	 * Here we can modify initializing command for our bot.
	 *
	 * @return
	 */
	@Override
	public Initialize getInitializeCommand() {
		// just set the name of the bot, nothing else
		return new Initialize().setName("Hunter").setDesiredSkill(5);
	}

	/**
	 * The hunter maintains the information of the state it was in the previous
	 * logic-cycle.
	 *
	 * @author Jimmy
	 */
	protected static enum State {

		OTHER, ENGAGE, PURSUE, MEDKIT, GRAB, ITEMS
	}

	/**
	 * Resets the state of the Hunter.
	 */
	protected void reset() {
		previousState = State.OTHER;
		notMoving = 0;
		enemy = null;
		navigation.stopNavigation();
		itemsToRunAround = null;
		item = null;
	}

	/**
	 * The previous state the hunter was inside during the previous logic
	 * iteration.
	 */
	protected State previousState = State.OTHER;
	/**
	 * Global anti-stuck mechanism. When this counter reaches a certain
	 * constant, the bot's mind gets a {@link HunterBot#reset()}.
	 */
	protected int notMoving = 0;

	/**
	 * Main method that controls the bot - makes decisions what to do next. It
	 * is called iteratively by Pogamut engine every time a synchronous batch
	 * from the environment is received. This is usually 4 times per second - it
	 * is affected by visionTime variable, that can be adjusted in GameBots ini
	 * file in UT2004/System folder.
	 *
	 * @throws cz.cuni.amis.pogamut.base.exceptions.PogamutException
	 */
	@Override
	public void logic() {
		// global anti-stuck?
		if (!info.isMoving()) {
			++notMoving;
			if (notMoving > 4) {
				// we're stuck - reset the bot's mind
				reset();
				return;
			}
		}

		// 1) do you see enemy? -> go to PURSUE (start shooting / hunt the
		// enemy)
		if (shouldEngage && players.canSeeEnemies() && weaponry.hasLoadedWeapon()) {
			stateEngage();
			return;
		}

		// 2) are you shooting? -> stop shooting, you've lost your target
		if (info.isShooting() || info.isSecondaryShooting()) {
			getAct().act(new StopShooting());
		}

		// 3) are you being shot? -> go to HIT (turn around - try to find your
		// enemy)
		if (senses.isBeingDamaged()) {
			this.stateHit();
			return;
		}

		// 4) have you got enemy to pursue? -> go to the last position of enemy
		if (enemy != null && shouldPursue && weaponry.hasLoadedWeapon()) { // !enemy.isVisible()
																			// because
																			// of
																			// 2)
			this.statePursue();
			return;
		}

		// 5) are you hurt? -> get yourself some medKit
		if (info.getHealth() < healthLevel && canRunAlongMedKit()) {
			this.stateMedKit();
			return;
		}

		// 6) do you see item? -> go to GRAB_ITEM (pick the most suitable item
		// and run for)
		if (shouldCollectItems && !items.getVisibleItems().isEmpty()) {
			stateSeeItem();
			previousState = State.GRAB;
			return;
		}

		// 7) if nothing ... run around items
		stateRunAroundItems();
	}

	//////////////////
	// STATE ENGAGE //
	//////////////////
	protected boolean runningToPlayer = false;

	/**
	 * Fired when bot see any enemy.
	 * <ol>
	 * <li>if enemy that was attacked last time is not visible than choose new
	 * enemy
	 * <li>if out of ammo - switch to another weapon
	 * <li>if enemy is reachable and the bot is far - run to him
	 * <li>if enemy is not reachable - stand still (kind a silly, right? :-)
	 * </ol>
	 */
	protected void stateEngage() {
		log.info("Decision is: ENGAGE");
		// config.setName("Hunter [ENGAGE]");

		boolean shooting = false;
		double distance = Double.MAX_VALUE;

		// 1) pick new enemy if the old one has been lost
		if (previousState != State.ENGAGE || enemy == null || !enemy.isVisible()) {
			// pick new enemy
			enemy = players.getNearestVisiblePlayer(players.getVisibleEnemies().values());
			if (enemy == null) {
				log.info("Can't see any enemies... ???");
				return;
			}
			if (info.isShooting()) {
				// stop shooting
				getAct().act(new StopShooting());
			}
			runningToPlayer = false;
		}

		if (enemy != null) {
			// 2) if not shooting at enemyID - start shooting
			distance = info.getLocation().getDistance(enemy.getLocation());

			// 3) should shoot?
			if (shoot.shoot(weaponPrefs, enemy) != null) {
				log.info("Shooting at enemy!!!");
				shooting = true;
			}
		}

		// 4) if enemy is far - run to him
		int decentDistance = Math.round(random.nextFloat() * 800) + 200;
		if (!enemy.isVisible() || !shooting || decentDistance < distance) {
			if (!runningToPlayer) {
				navigation.navigate(enemy);
				runningToPlayer = true;
			}
		} else {
			runningToPlayer = false;
			navigation.stopNavigation();
			getAct().act(new Stop());
		}

		previousState = State.ENGAGE;
	}

	///////////////
	// STATE HIT //
	///////////////
	protected void stateHit() {
		log.info("Decision is: HIT");
		getAct().act(new Rotate().setAmount(32000));
		previousState = State.OTHER;
	}

	//////////////////
	// STATE PURSUE //
	//////////////////
	/**
	 * State pursue is for pursuing enemy who was for example lost behind a
	 * corner. How it works?:
	 * <ol>
	 * <li>initialize properties
	 * <li>obtain path to the enemy
	 * <li>follow the path - if it reaches the end - set lastEnemy to null - bot
	 * would have seen him before or lost him once for all
	 * </ol>
	 */
	protected void statePursue() {
		log.info("Decision is: PURSUE");
		// config.setName("Hunter [PURSUE]");
		if (previousState != State.PURSUE) {
			pursueCount = 0;
			navigation.navigate(enemy);
		}
		++pursueCount;
		if (pursueCount > 30) {
			reset();
		} else {
			previousState = State.PURSUE;
		}
	}

	protected int pursueCount = 0;

	//////////////////
	// STATE MEDKIT //
	//////////////////
	@SuppressWarnings("unchecked")
	protected void stateMedKit() {
		log.info("Decision is: MEDKIT");
		// config.setName("Hunter [MEDKIT]");
		if (previousState != State.MEDKIT) {
			List<Item> healths = new LinkedList();
			healths.addAll(items.getSpawnedItems(ItemType.HEALTH_PACK).values());
			if (healths.size() == 0) {
				healths.addAll(items.getSpawnedItems(ItemType.MINI_HEALTH_PACK).values());
			}
			Set<Item> okHealths = tabooItems.filter(healths);
			if (okHealths.size() == 0) {
				log.log(Level.WARNING, "No suitable health to run for.");
				stateRunAroundItems();
				return;
			}
			item = fwMap.getNearestItem(okHealths, info.getNearestNavPoint());
			navigation.navigate(item);
		}
		previousState = State.MEDKIT;
	}

	////////////////////
	// STATE SEE ITEM //
	////////////////////
	protected Item item = null;

	protected void stateSeeItem() {
		log.info("Decision is: SEE ITEM");
		// config.setName("Hunter [SEE ITEM]");

		if (item != null && item.getLocation().getDistance(info.getLocation()) < 100) {
			reset();
		}

		if (previousState != State.GRAB) {
			item = DistanceUtils.getNearest(items.getVisibleItems().values(), info.getLocation());
			if (item.getLocation().getDistance(info.getLocation()) < 300) {
				getAct().act(new Move().setFirstLocation(item.getLocation()));
			} else {
				navigation.navigate(item);
			}
		}

	}

	protected boolean canRunAlongMedKit() {
		boolean result = !items.getSpawnedItems(ItemType.HEALTH_PACK).isEmpty()
				|| !items.getSpawnedItems(ItemType.MINI_HEALTH_PACK).isEmpty();
		return result;
	}

	////////////////////////////
	// STATE RUN AROUND ITEMS //
	////////////////////////////
	protected List<Item> itemsToRunAround = null;

	protected void stateRunAroundItems() {
		log.info("Decision is: ITEMS");
		// config.setName("Hunter [ITEMS]");
		if (previousState != State.ITEMS) {
			itemsToRunAround = new LinkedList<Item>(items.getSpawnedItems().values());
			Set<Item> items = tabooItems.filter(itemsToRunAround);
			if (items.size() == 0) {
				log.log(Level.WARNING, "No item to run for...");
				reset();
				return;
			}
			item = items.iterator().next();
			navigation.navigate(item);
		}
		previousState = State.ITEMS;
	}

	////////////////
	// BOT KILLED //
	////////////////
	@Override
	public void botKilled(BotKilled event) {
		itemsToRunAround = null;
		enemy = null;
	}

	///////////////////////////////////
	@SuppressWarnings("unchecked")
	public static void main(String args[]) throws PogamutException {
		// starts 4 Hunters at once
		// note that this is the most easy way to get a bunch of bots running at
		// the same time
		new UT2004BotRunner(HunterBot.class, "Hunter").setMain(true).setLogLevel(Level.INFO).startAgents(4);
	}
}

package pogamut.hunter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import cz.cuni.amis.introspection.java.JProp;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.NavigationState;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Rotate;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Stop;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.ut2004.bots.ControllerBotParameters;
import edu.southwestern.tasks.ut2004.server.BotKiller;
import pogamut.hunter.HunterBotParameters;

/**
 * Example of Simple Pogamut bot, that randomly walks around the map searching
 * for preys shooting at everything that is in its way.
 *
 * @author Rudolf Kadlec aka ik
 * @author Jimmy
 */
@AgentScoped
public class HunterBot extends UT2004BotModuleController<UT2004Bot> {

	/**
	 * boolean switch to activate engage behavior
	 */
	@JProp
	public boolean shouldEngage = true;
	/**
	 * boolean switch to activate pursue behavior
	 */
	@JProp
	public boolean shouldPursue = true;
	/**
	 * boolean switch to activate rearm behavior
	 */
	@JProp
	public boolean shouldRearm = true;
	/**
	 * boolean switch to activate collect health behavior
	 */
	@JProp
	public boolean shouldCollectHealth = true;
	/**
	 * how low the health level should be to start collecting health items
	 */
	@JProp
	public int healthLevel = 75;
	/**
	 * how many bot the hunter killed other bots (i.e., bot has fragged them /
	 * got point for killing somebody)
	 */
	@JProp
	public int frags = 0;
	/**
	 * how many times the hunter died
	 */
	@JProp
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
			enemy = null;
		}
	}
	/**
	 * Used internally to maintain the information about the bot we're currently
	 * hunting, i.e., should be firing at.
	 */
	protected Player enemy = null;
	/**
	 * Item we're running for. 
	 */
	protected Item item = null;
	/**
	 * Taboo list of items that are forbidden for some time.
	 */
	protected TabooSet<Item> tabooItems = null;

	private UT2004PathAutoFixer autoFixer;

	private static int instanceCount = 0;

	/**
	 * Bot's preparation - called before the bot is connected to GB2004 and
	 * launched into UT2004.
	 */
	@Override
	public void prepareBot(UT2004Bot bot) {
		tabooItems = new TabooSet<Item>(bot);

		autoFixer = new UT2004PathAutoFixer(bot, navigation.getPathExecutor(), fwMap, aStar, navBuilder); // auto-removes wrong navigation links between navpoints

		// listeners        
		navigation.getState().addListener(new FlagListener<NavigationState>() {

			@Override
			public void flagChanged(NavigationState changedValue) {
				switch (changedValue) {
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
		weaponPrefs.addGeneralPref(UT2004ItemType.LIGHTNING_GUN, true);                
		weaponPrefs.addGeneralPref(UT2004ItemType.SHOCK_RIFLE, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, false);
		weaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, true);        
		weaponPrefs.addGeneralPref(UT2004ItemType.ROCKET_LAUNCHER, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.LINK_GUN, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.ASSAULT_RIFLE, true);        
		weaponPrefs.addGeneralPref(UT2004ItemType.BIO_RIFLE, true);
	}

	/**
	 * Here we can modify initializing command for our bot.
	 *
	 * @return
	 */
	@Override
	public Initialize getInitializeCommand() {
		// just set the name of the bot and his skill level, 1 is the lowest, 7 is the highest
		// skill level affects how well will the bot aim
		return new Initialize().setName("Hunter-" + (++instanceCount)).setDesiredSkill(5);
	}

	/**
	 * Resets the state of the Hunter.
	 */
	protected void reset() {
		item = null;
		enemy = null;
		navigation.stopNavigation();
		itemsToRunAround = null;
	}

	@EventListener(eventClass=PlayerDamaged.class)
	public void playerDamaged(PlayerDamaged event) {
		if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
			log.info("I have just hurt other bot for: " + event.getDamageType() + "[" + event.getDamage() + "]");
		}
	}

	@EventListener(eventClass=BotDamaged.class)
	public void botDamaged(BotDamaged event) {
		if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
			log.info("I have just been hurt by other bot for: " + event.getDamageType() + "[" + event.getDamage() + "]");
		}
	}

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
		// Added by Adina
		// Ends the test if it has reached its time limit
		if (game.getTime() > getParams().getEvalSeconds()) {
			endEval();
		}


		// 1) do you see enemy? 	-> go to PURSUE (start shooting / hunt the enemy)
		if (shouldEngage && players.canSeeEnemies() && weaponry.hasLoadedWeapon()) {
			stateEngage();
			return;
		}

		// 2) are you shooting? 	-> stop shooting, you've lost your target
		if (info.isShooting() || info.isSecondaryShooting()) {
			getAct().act(new StopShooting());
		}

		// 3) are you being shot? 	-> go to HIT (turn around - try to find your enemy)
		if (senses.isBeingDamaged()) {
			this.stateHit();
			return;
		}

		// 4) have you got enemy to pursue? -> go to the last position of enemy
		if (enemy != null && shouldPursue && weaponry.hasLoadedWeapon()) {  // !enemy.isVisible() because of 2)
			this.statePursue();
			return;
		}

		// 5) are you hurt?			-> get yourself some medKit
		if (shouldCollectHealth && info.getHealth() < healthLevel) {
			this.stateMedKit();
			return;
		}

		// 6) if nothing ... run around items
		stateRunAroundItems();
	}

	/**
	 * Added by Adina to be able to force the bot to stop.
	 */
	public void endEval() {
		BotKiller.killBot(bot);
	}

	/**
	 * Added by Adina to track when the bot should stop running
	 * @return
	 */
	public HunterBotParameters getParams() {
		return (HunterBotParameters) bot.getParams();
	}
	
	//////////////////
	// STATE ENGAGE //
	//////////////////
	protected boolean runningToPlayer = false;

	/**
	 * Fired when bot see any enemy. <ol> <li> if enemy that was attacked last
	 * time is not visible than choose new enemy <li> if enemy is reachable and the bot is far - run to him
	 * <li> otherwise - stand still (kind a silly, right? :-)
	 * </ol>
	 */
	protected void stateEngage() {
		//log.info("Decision is: ENGAGE");
		//config.setName("Hunter [ENGAGE]");

		boolean shooting = false;
		double distance = Double.MAX_VALUE;
		pursueCount = 0;

		// 1) pick new enemy if the old one has been lost
		if (enemy == null || !enemy.isVisible()) {
			// pick new enemy
			enemy = players.getNearestVisiblePlayer(players.getVisibleEnemies().values());
			if (enemy == null) {
				if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
					log.info("Can't see any enemies... ???");
				}
				return;
			}
		}

		// 2) stop shooting if enemy is not visible
		if (!enemy.isVisible()) {
			if (info.isShooting() || info.isSecondaryShooting()) {
				// stop shooting
				getAct().act(new StopShooting());
			}
			runningToPlayer = false;
		} else {
			// 2) or shoot on enemy if it is visible
			distance = info.getLocation().getDistance(enemy.getLocation());
			if (shoot.shoot(weaponPrefs, enemy) != null) {
				if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
					log.info("Shooting at enemy!!!");
				}
				shooting = true;
			}
		}

		// 3) if enemy is far or not visible - run to him
		int decentDistance = Math.round(random.nextFloat() * 800) + 200;
		if (!enemy.isVisible() || !shooting || decentDistance < distance) {
			if (!runningToPlayer) {
				navigation.navigate(enemy);
				runningToPlayer = true;
			}
		} else {
			runningToPlayer = false;
			navigation.stopNavigation();
		}

		item = null;
	}

	///////////////
	// STATE HIT //
	///////////////
	protected void stateHit() {
		//log.info("Decision is: HIT");
		bot.getBotName().setInfo("HIT");
		if (navigation.isNavigating()) {
			navigation.stopNavigation();
			item = null;
		}
		getAct().act(new Rotate().setAmount(32000));
	}

	//////////////////
	// STATE PURSUE //
	//////////////////
	/**
	 * State pursue is for pursuing enemy who was for example lost behind a
	 * corner. How it works?: <ol> <li> initialize properties <li> obtain path
	 * to the enemy <li> follow the path - if it reaches the end - set lastEnemy
	 * to null - bot would have seen him before or lost him once for all </ol>
	 */
	protected void statePursue() {
		//log.info("Decision is: PURSUE");
		++pursueCount;
		if (pursueCount > 30) {
			reset();
		}
		if (enemy != null) {
			bot.getBotName().setInfo("PURSUE");
			navigation.navigate(enemy);
			item = null;
		} else {
			reset();
		}
	}
	protected int pursueCount = 0;

	//////////////////
	// STATE MEDKIT //
	//////////////////
	protected void stateMedKit() {
		//log.info("Decision is: MEDKIT");
		Item item = items.getPathNearestSpawnedItem(ItemType.Category.HEALTH);
		if (item == null) {
			if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
				log.warning("NO HEALTH ITEM TO RUN TO => ITEMS");
			}
			stateRunAroundItems();
		} else {
			bot.getBotName().setInfo("MEDKIT");
			navigation.navigate(item);
			this.item = item;
		}
	}

	////////////////////////////
	// STATE RUN AROUND ITEMS //
	////////////////////////////
	protected List<Item> itemsToRunAround = null;

	protected void stateRunAroundItems() {
		//log.info("Decision is: ITEMS");
		//config.setName("Hunter [ITEMS]");
		if (navigation.isNavigatingToItem()) return;

		List<Item> interesting = new ArrayList<Item>();

		// ADD WEAPONS
		for (ItemType itemType : ItemType.Category.WEAPON.getTypes()) {
			if (!weaponry.hasLoadedWeapon(itemType)) interesting.addAll(items.getSpawnedItems(itemType).values());
		}
		// ADD ARMORS
		for (ItemType itemType : ItemType.Category.ARMOR.getTypes()) {
			interesting.addAll(items.getSpawnedItems(itemType).values());
		}
		// ADD QUADS
		interesting.addAll(items.getSpawnedItems(UT2004ItemType.U_DAMAGE_PACK).values());
		// ADD HEALTHS
		if (info.getHealth() < 100) {
			interesting.addAll(items.getSpawnedItems(UT2004ItemType.HEALTH_PACK).values());
		}

		Item item = MyCollections.getRandom(tabooItems.filter(interesting));
		if (item == null) {
			if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
				log.warning("NO ITEM TO RUN FOR!");
			}
			if (navigation.isNavigating()) return;
			bot.getBotName().setInfo("RANDOM NAV");
			navigation.navigate(navPoints.getRandomNavPoint());
		} else {
			this.item = item;
			if(Parameters.parameters == null || Parameters.parameters.booleanParameter("utBotLogOutput")) {
				log.info("RUNNING FOR: " + item.getType().getName());
			}
			bot.getBotName().setInfo("ITEM: " + item.getType().getName() + "");
			navigation.navigate(item);        	
		}        
	}

	////////////////
	// BOT KILLED //
	////////////////
	@Override
	public void botKilled(BotKilled event) {
		reset();
	}

	///////////////////////////////////
	public static void main(String args[]) throws PogamutException {
		// starts 3 Hunters at once
		// note that this is the most easy way to get a bunch of (the same) bots running at the same time        
		new UT2004BotRunner(HunterBot.class, "Hunter").setMain(true).setLogLevel(Level.INFO).startAgents(2);
	}
}

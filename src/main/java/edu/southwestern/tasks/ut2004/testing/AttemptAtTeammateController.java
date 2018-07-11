package edu.southwestern.tasks.ut2004.testing;

import java.util.Map;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.utils.collections.MyCollections;
import edu.southwestern.tasks.ut2004.actions.BotAction;
import edu.southwestern.tasks.ut2004.actions.EmptyAction;
import edu.southwestern.tasks.ut2004.actions.FollowTeammateAction;
import edu.southwestern.tasks.ut2004.actions.NavigateToLocationAction;
import edu.southwestern.tasks.ut2004.actions.OldActionWrapper;
import edu.southwestern.tasks.ut2004.actions.PursueEnemyAction;
import edu.southwestern.tasks.ut2004.controller.BotController;
import edu.southwestern.tasks.ut2004.controller.RandomItemPathExplorer;
import edu.southwestern.tasks.ut2004.controller.SequentialPathExplorer;
import edu.southwestern.tasks.ut2004.controller.behaviors.AttackEnemyAloneModule;
import edu.southwestern.tasks.ut2004.controller.behaviors.ItemExplorationBehaviorModule;
import edu.southwestern.tasks.ut2004.weapons.UT2004WeaponManager;
import edu.utexas.cs.nn.Constants;
import edu.utexas.cs.nn.weapons.WeaponPreferenceTable;
import edu.utexas.cs.nn.weapons.WeaponPreferenceTable.WeaponTableEntry;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;
import mockcz.cuni.pogamut.MessageObjects.Triple;
import utopia.agentmodel.actions.ApproachEnemyAction;
import utopia.agentmodel.actions.DodgeShootAction;
import utopia.agentmodel.actions.GotoItemAction;
import utopia.agentmodel.actions.QuickTurnAction;
import utopia.controllers.scripted.ChasingController;

/**
 * Provides the control for a bot created to aid players
 * bot should follow it's teammate if they are in view and attack enemies, if it finds itself alone, it will run around collecting items until it encounters someone else
 * @author Adina Friedman
 */
public class AttemptAtTeammateController implements BotController {

	AttackEnemyAloneModule attackAlone = new AttackEnemyAloneModule();
	RandomItemPathExplorer runAroundItems = new RandomItemPathExplorer();
	//SequentialPathExplorer runAroundGeneral = new SequentialPathExplorer();
	AgentMemory memory;
	AgentBody body;
	public final UT2004WeaponManager weaponManager;
	public static WeaponPreferenceTable weaponPreferences;
	public static final int FULL_HEALTH = 100; //players spawn with 100 hp, and can overheal to a level of 199 hp
	public static final int THRESHOLD_HEALTH_LEVEL = 20;
	public static final int DNE_HEALTH_LEVEL = 30; //DNE = do not engage. if the bot is below this health it should avoid an enemy it sees
	public static final double MAX_DISTANCE_TO_ITEM = 300;
	public static final double COMBAT_TYPE_THRESHOLD_DISTANCE = 150; //distance for approach enemy vs dodge shoot

	public AttemptAtTeammateController(UT2004WeaponManager weaponManager) {
		this.weaponManager = weaponManager;
		if (weaponPreferences == null) {
			weaponPreferences = new WeaponPreferenceTable();
		}
	}

	/**
	 * contains the actual logic for the bot to move around see interior comments for more details
	 */
	public BotAction control(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {//loops through over and over again
		Player visibleFriend = bot.getPlayers().getNearestVisibleFriend();
		Player lastSeenFriend = bot.getPlayers().getNearestFriend(10); //friend who bot just saw but is now out of view
		Player visibleEnemy = bot.getPlayers().getNearestVisibleEnemy();
		Player lastSeenEnemy = bot.getPlayers().getNearestEnemy(10); //enemy who bot just saw but is now out of view

		equipBestWeapon(bot);

		/**if bot is being damaged but doesn't see an enemy, turn to see who it is*/
		if(bot.getSenses().isBeingDamaged() && visibleEnemy == null && lastSeenEnemy == null) {
			System.out.println("Turning around");
			return new OldActionWrapper(new QuickTurnAction(OldActionWrapper.getAgentMemory(bot)));
		} else {
			//			System.out.println("NOT TURNING!");
			//			System.out.println("bot.getSenses().isBeingDamaged() = " + bot.getSenses().isBeingDamaged());
			//			System.out.println("visibleEnemy == null = " + (visibleEnemy == null));
			//			System.out.println("lastSeenEnemy == null = " + (lastSeenEnemy == null));
		}

		/**bot will look for health pickups if it drops below 20hp*/
		if((bot.getBot().getSelf().getHealth()) < THRESHOLD_HEALTH_LEVEL) {
			//tell bot to abandon whatever it's doing and go find a SPAWNED health kit, standing at a spawn point waiting = certain death 
			Item nearestHealth = bot.getItems().getNearestSpawnedItem(ItemType.Category.HEALTH);
			Location healthLoc =  nearestHealth.getLocation();
			System.out.println("Going to health");
			return new NavigateToLocationAction(healthLoc);
		}

		/**if an enemy is visible attack?*/
		if(visibleEnemy != null){
			double enemyDistance = visibleEnemy.getLocation().getDistance(bot.getBot().getLocation());
			lastSeenEnemy = visibleEnemy;
			if(shouldEngage(bot)) { //fight if you have health and ammo
				System.out.println("Attacking enemy");
				return new OldActionWrapper(new ApproachEnemyAction(OldActionWrapper.getAgentMemory(bot), true, true, false, true));
			}else { //RUN BITCH! TODO: take this out before you get in trouble
				Item nearestHealth = bot.getItems().getNearestSpawnedItem(ItemType.Category.HEALTH);
				return new OldActionWrapper(new GotoItemAction(OldActionWrapper.getAgentMemory(bot), nearestHealth));
				//go get health because enemy might have seen bot, and bot's gonna need it
			}
		}

		/**should you go after the last enemy you saw?*/
		if(shouldEngage(bot) && visibleEnemy == null && lastSeenEnemy != null) {
			//HUNT THEM DOWN
			return new PursueEnemyAction(lastSeenEnemy, true);		
		}

		/**if bot sees friend when no enemies are nearby, it should follow teammate*/
		if(visibleFriend != null) { //follow visible teammate
			lastSeenFriend = visibleFriend;
			System.out.println("Following friend");
			return new FollowTeammateAction(visibleFriend);
		} //if it loses sight of friend, go to the last location it saw friend at - this carries the cchance that the bot will see friend again,
		if(visibleFriend == null && lastSeenFriend != null) {
			System.out.println("Following friend");
			return new NavigateToLocationAction(lastSeenFriend.getLocation());
		}

		//start randomly running around to get items
		/**if the bot does not see anyone nearby run around collecting items*/

		//make sure to pick up weapons
		if(bot.getItems().getNearestVisibleItem(ItemType.Category.WEAPON) != null){
			Item nearestWeapon = bot.getItems().getNearestVisibleItem(ItemType.Category.WEAPON);
			Location weaponLocation =  bot.getItems().getNearestVisibleItem(ItemType.Category.WEAPON).getLocation();
			double weaponDistance = weaponLocation.getDistance(bot.getBot().getLocation());
			if(weaponDistance < MAX_DISTANCE_TO_ITEM) {
				System.out.println("getting weapon");
				//return new NavigateToLocationAction(itemLocation);
				return new OldActionWrapper(new GotoItemAction(OldActionWrapper.getAgentMemory(bot), nearestWeapon));
			}
		}

		//make sure to pick up armour
		if(bot.getItems().getNearestVisibleItem(ItemType.Category.ARMOR) != null){
			Item nearestArmour = bot.getItems().getNearestVisibleItem(ItemType.Category.ARMOR);
			Location armourLocation =  bot.getItems().getNearestVisibleItem(ItemType.Category.ARMOR	).getLocation();
			double armourDistance = armourLocation.getDistance(bot.getBot().getLocation());
			if(armourDistance < MAX_DISTANCE_TO_ITEM) {
				System.out.println("getting weapon");
				//return new NavigateToLocationAction(itemLocation);
				return new OldActionWrapper(new GotoItemAction(OldActionWrapper.getAgentMemory(bot), nearestArmour));
			}
		}


		System.out.println("running like a headless chicken");
		return runAroundItems.control(bot);
		//		System.out.println("standing still");
		//		return new EmptyAction();
	}



	/**
	 * determines whether the bot should try to fight the enemy it's looking at.
	 * engage if hp > 30 and has ammo
	 * @return returns whether bot should engage
	 */
	public boolean shouldEngage(UT2004BotModuleController bot) {
		//check ammo
		boolean hasGun = bot.getWeaponry().hasLoadedWeapon();
		boolean hasHealth = (bot.getBot().getSelf().getHealth()) > DNE_HEALTH_LEVEL;
		if(hasGun && hasHealth) {
			return true;
		}
		return false;
	}

	//	public boolean shouldChase(UT2004BotModuleController bot, boolean shouldEngage) {
	//		if(shouldEngage = true) {
	//			Location attackLocation =  
	//		}
	//		return false;
	//	}

	/**
	 * Equips the best weapon for the current situation and returns true if the
	 * weapon is good enough that the bot should fight with it. Will not change
	 * the weapon in the middle of combat.
	 *
	 * @return true if the weapon is good for fighting.
	 */
	public boolean equipBestWeapon(UT2004BotModuleController bot) {
		return equipBestWeapon(bot, false);
	}

	/**
	 * COPIED FROM UT2 by Jacob Schrum
	 * Same as equipBestWeapon(), but the added parameter informs the method
	 * that it is being called after the bot picked up an item
	 *
	 * @param added if true, the bot can switch weapons even in the middle of
	 * combat
	 * @return whether the weapons it good to fight with
	 */
	public boolean equipBestWeapon(UT2004BotModuleController bot, boolean added) {
		Weaponry weaponry = bot.getWeaponry();//added by adina for compatibility
		boolean hasGoodWeapon = weaponPreferences.hasGoodWeapon(weaponry.getLoadedRangedWeapons(), bot.getPlayers(), OldActionWrapper.getAgentMemory(bot));
		Weapon recommendation = weaponPreferences.savedRec;
		Weapon current = weaponry.getCurrentWeapon();

		double distance = WeaponTableEntry.MAX_RANGED_RANGE - 1;
		if (bot.getPlayers().canSeeEnemies() && OldActionWrapper.getAgentMemory(bot).getCombatTarget() != null && OldActionWrapper.getAgentMemory(bot).getCombatTarget().getLocation() != null && bot.getInfo().getLocation() != null) {
			distance = Triple.distanceInSpace(OldActionWrapper.getAgentMemory(bot).getCombatTarget().getLocation(), bot.getInfo().getLocation());
		}

		// Don't switch weapons in the heat of battle, unless out of ammo, or just got new weapon, or using crap weapon
		if (!added
				&& current != null
				&& !current.getType().equals(UT2004ItemType.LINK_GUN)
				&& !current.getType().equals(UT2004ItemType.BIO_RIFLE)
				&& !current.getType().equals(UT2004ItemType.ASSAULT_RIFLE)
				&& !current.getType().equals(UT2004ItemType.LIGHTNING_GUN) // Sniping weapons are crap at close range
				&& !current.getType().equals(UT2004ItemType.SNIPER_RIFLE)
				&& !current.getType().equals(UT2004ItemType.SHIELD_GUN)
				&& !(OldActionWrapper.getAgentBody(bot).isSecondaryChargingWeapon(current) && bot.getInfo().isSecondaryShooting())
				&& (OldActionWrapper.getAgentMemory(bot)).isThreatened()
				|| OldActionWrapper.getAgentMemory(bot).isThreatening(OldActionWrapper.getAgentMemory(bot).getCombatTarget())
				|| bot.getInfo().isShooting()
				|| (distance < WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 2)
				&& weaponry.hasAmmoForWeapon(current.getType())) {
			return hasGoodWeapon;
		}

		if (recommendation != null && recommendation != null) {
			if (current == null || !current.getType().equals(recommendation.getType())) {
				weaponry.changeWeapon(recommendation);
				OldActionWrapper.getAgentMemory(bot).weaponSwitchTime = bot.getGame().getTime();//game.getTime();
			}
		}
		current = weaponry.getCurrentWeapon();
		if (current == null || weaponry.getCurrentAmmo() == 0) {
			Map<ItemType, Weapon> loadedWeapons = weaponry.getLoadedWeapons();
			for (Weapon x : loadedWeapons.values()) {
				ItemType t = x.getType();
				if (!t.equals(UT2004ItemType.LINK_GUN)
						&& !t.equals(UT2004ItemType.SHIELD_GUN)
						&& !t.equals(UT2004ItemType.ONS_GRENADE_LAUNCHER)
						&& weaponry.getAmmo(t) > 0) {

					current = weaponry.getCurrentWeapon();
					if (current == null || !current.getType().equals(t)) {
						weaponry.changeWeapon(t);
						// This check seems unreliable, so it is being done in two different ways
						if (weaponry.getCurrentAmmo() > 0 || weaponry.getAmmo(t) > 0) {
							return hasGoodWeapon;
						}
					}
				}
			}
		}
		// Final resort is shield gun
		current = weaponry.getCurrentWeapon();
		if (current != null && weaponry.getCurrentAmmo() == 0 && weaponry.getAmmo(current.getType()) == 0) {
			weaponry.changeWeapon(UT2004ItemType.SHIELD_GUN);
			return false;
		}

		return hasGoodWeapon;
	}


	/**
	 * initializes the controller
	 */
	public void initialize(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		memory = OldActionWrapper.getAgentMemory(bot);
		body = OldActionWrapper.getAgentBody(bot);		
		bot.getBot().getBotName().setNameBase("Jude");
		//bot.getInitializeCommand().setSkin("Aliens.Alien")
		//bot.getBot()
	}	

	/**
	 * resets the controller
	 */
	public void reset(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
	}

}

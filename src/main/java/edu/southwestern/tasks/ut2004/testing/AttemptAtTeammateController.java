package edu.southwestern.tasks.ut2004.testing;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.utils.collections.MyCollections;
import edu.southwestern.tasks.ut2004.actions.BotAction;
import edu.southwestern.tasks.ut2004.actions.EmptyAction;
import edu.southwestern.tasks.ut2004.actions.FollowTeammateAction;
import edu.southwestern.tasks.ut2004.actions.NavigateToLocationAction;
import edu.southwestern.tasks.ut2004.actions.OldActionWrapper;
import edu.southwestern.tasks.ut2004.controller.BotController;
import edu.southwestern.tasks.ut2004.controller.RandomItemPathExplorer;
import edu.southwestern.tasks.ut2004.controller.behaviors.AttackEnemyAloneModule;
import edu.utexas.cs.nn.Constants;
import edu.utexas.cs.nn.weapons.WeaponPreferenceTable;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;
import utopia.agentmodel.actions.ApproachEnemyAction;
import utopia.agentmodel.actions.QuickTurnAction;
import utopia.controllers.scripted.ChasingController;

public class AttemptAtTeammateController implements BotController {
	
	AttackEnemyAloneModule attackAlone = new AttackEnemyAloneModule();
	RandomItemPathExplorer runAround = new RandomItemPathExplorer();
	AgentMemory memory;
	AgentBody body;
	private ChasingController chaseController;
	
	public static final int FULL_HEALTH = 100;
	public static final int THRESHOLD_HEALTH_LEVEL = 20;
	//var timesteps teammate hasn't moved
	/**
	 * 
	 */
	public BotAction control(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {//loops thourhg over and over again
		Player visibleFriend = bot.getPlayers().getNearestVisibleFriend();
		Player lastSeenFriend = bot.getPlayers().getNearestFriend(10); //friend who bot just saw but if now out of view
		Player visibleEnemy = bot.getPlayers().getNearestVisibleEnemy();
		Player lastSeenEnemy = bot.getPlayers().getNearestEnemy(10); //enemy who bot just saw but if now out of view
		
		/**bot will look for health pickups if it drops below 20hp*/
		if((bot.getBot().getSelf().getHealth()) < THRESHOLD_HEALTH_LEVEL) {
			//tell bot to abandon whatever it's doing and go find a SPAWNED health kit, standing at a spawn point waiting = certain death 
			Item nearestHealth = bot.getItems().getNearestSpawnedItem(ItemType.Category.HEALTH);
			Location healthLoc =  nearestHealth.getLocation();
			return new NavigateToLocationAction(healthLoc);
		}
		
		/**if bot sees friend when no enemies are nearby, it should follow teammate*/
//		if(visibleFriend != null || lastSeenFriend != null) {
		if(visibleFriend != null) { //follow visible teammate
			lastSeenFriend = visibleFriend;
			return new FollowTeammateAction(visibleFriend);
		} //if it loses sight of friend, go tot he last location it saw friend at
		if(visibleFriend == null && lastSeenFriend != null) {
			return new NavigateToLocationAction(lastSeenFriend.getLocation());
		}
//			if(visibleFriend == null && lastSeenFriend != null) {//go to location of last visible friend
//				
//			}
//		}
		//follow teammate
		//still for too long, ditch them
		
		//start randomly running around to get items
//		if(nearestEnemy == null && nearestFriend == null) {
//			return runAround.control(bot);
//		}
//		
//		if(bot.getSenses().isBeingDamaged() && nearestEnemy == null) {
//			return new OldActionWrapper(new QuickTurnAction(OldActionWrapper.getAgentMemory(bot)));
//		}
//		
//		if(nearestEnemy !=  null && nearestFriend == null) {
////			if(shouldChase(nearestEnemy, bot)) {
//			return new OldActionWrapper(new ApproachEnemyAction(OldActionWrapper.getAgentMemory(bot), true, true, false, true));
////			}
//			//return attackAlone.control(bot);
//		}
		
//		if(nearestEnemy == null && lastSeenEnemy != null) {
//			
//		}
		
		
		// null check first
//		if(nearestFriend != null) {
//			return new FollowTeammateAction(nearestFriend);
//		}
////
//		if(attackAlone.trigger(bot)) {
//			return attackAlone.control(bot);
//		}
		return new EmptyAction();	
//		return runAround.control(bot);
	}
	
//	//copied and adjusted from UT2
//    public boolean shouldChase(Player currentTarget, UT2004BotModuleController bot) {
//        Player chasingEnemy = this.chaseController.getLastEnemy();
//        if (currentTarget != null && chasingEnemy != null
//                && currentTarget.getId().equals(chasingEnemy.getId())) {
////            // Fight what can be seen
//            return false;
//        }
//        Player nearest = OldActionWrapper.getAgentMemory(bot).getSeeEnemy();
//        if (OldActionWrapper.getAgentMemory(bot).numVisibleOpponents() > 2 && nearest != null && nearest.getLocation().getDistance(bot.getInfo().getLocation()) < WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE) {
//            return false;
//        }
//        // Only chase if can't see target
//        return OldActionWrapper.getAgentMemory(bot).canFocusOn(chasingEnemy);
//    //	return false;
//    }

	
	/**
	 * initializes the controller
	 */
	public void initialize(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		memory = OldActionWrapper.getAgentMemory(bot);
		body = OldActionWrapper.getAgentBody(bot);
		//getComponentId().getName().setFlag()
		bot.getBot().getComponentId().getName().setFlag("Jude");
		//this.chaseController = new ChasingController(bot.getBot(), memory);
	}

	
	
	/**
	 * resets the controller
	 */
	public void reset(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
	}

}

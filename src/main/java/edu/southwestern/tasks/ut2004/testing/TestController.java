package edu.southwestern.tasks.ut2004.testing;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import edu.southwestern.tasks.ut2004.actions.BotAction;
import edu.southwestern.tasks.ut2004.actions.EmptyAction;
import edu.southwestern.tasks.ut2004.actions.FollowEnemy;
import edu.southwestern.tasks.ut2004.actions.FollowTeammate;
import edu.southwestern.tasks.ut2004.controller.BotController;
import edu.southwestern.tasks.ut2004.controller.behaviors.AttackEnemyAloneModule;

public class TestController implements BotController {
	AttackEnemyAloneModule attack = new AttackEnemyAloneModule();
	
	/**
	 * bot will follow closest teammate, if one is not there, it will stand in place
	 */
	public BotAction control(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		Player nearestFriend = bot.getPlayers().getNearestVisibleFriend();
		Player nearestEnemy = bot.getPlayers().getNearestVisibleEnemy();
		
		// null check first
//		if(nearestFriend != null) {
//			return new FollowTeammate(nearestFriend);
//		}
//		
//		if(nearestEnemy != null) {
//			return new FollowEnemy(nearestEnemy);
//		}
//		return new BotAction() {
//
//			@Override
//			public String execute(UT2004BotModuleController bot) {
//				bot.getBody().getLocomotion().jump();
//				return "JUMP";
//			}
//			
//		};
		if(attack.trigger(bot)) {
			return attack.control(bot);
		}
		return new EmptyAction();
	}

	
	/**
	 * initializes the controller
	 */
	public void initialize(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
	}

	
	/**
	 * resets the controller
	 */
	public void reset(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
	}

}

package edu.southwestern.tasks.ut2004.controller.behaviors;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.actions.BotAction;
import edu.southwestern.tasks.ut2004.actions.OldActionWrapper;
import mockcz.cuni.pogamut.Client.AgentMemory;
import utopia.agentmodel.actions.ApproachEnemyAction;

public class AttackEnemyAloneModule implements BehaviorModule {

	@Override
	public BotAction control(UT2004BotModuleController bot) {

		AgentMemory memory = OldActionWrapper.getAgentMemory(bot); 
		boolean shoot = true; 
		boolean secondary = false; 
		boolean jump = false; 
		boolean forcePath = false; // What is this?
		
		return new OldActionWrapper(new ApproachEnemyAction( memory,  shoot,  secondary,  jump,  forcePath));
	}

	@Override
	public void initialize(UT2004BotModuleController bot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset(UT2004BotModuleController bot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean trigger(UT2004BotModuleController bot) {
		// TODO Auto-generated method stub
		if((bot.getPlayers().getNearestVisibleEnemy() != null) && (bot.getPlayers().getNearestVisibleFriend() == null)) {
			return true;
		}
		return false;
	}

}

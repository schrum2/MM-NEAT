package edu.southwestern.tasks.ut2004.controller.behaviors;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.actions.BotAction;
import edu.southwestern.tasks.ut2004.actions.OldActionWrapper;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;
import utopia.agentmodel.actions.ApproachEnemyAction;

public class AttackEnemyAloneModule implements BehaviorModule {

	AgentMemory memory;
	AgentBody body;
	
	@Override
	public BotAction control(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		boolean shoot = true; 
		boolean secondary = false; 
		boolean jump = false; 
		boolean forcePath = true;
		
		return new OldActionWrapper(new ApproachEnemyAction(memory, shoot, secondary, jump, forcePath), body);
	}

	@Override
	public void initialize(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		memory = OldActionWrapper.getAgentMemory(bot); 		
		body = OldActionWrapper.getAgentBody(bot); 		
	}

	@Override
	public void reset(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		memory = OldActionWrapper.getAgentMemory(bot); 		
		body = OldActionWrapper.getAgentBody(bot); 		
	}

	@Override
	public boolean trigger(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		if((bot.getPlayers().getNearestVisibleEnemy() != null) && (bot.getPlayers().getNearestVisibleFriend() == null)) {
			return true;
		}
		return false;
	}

}

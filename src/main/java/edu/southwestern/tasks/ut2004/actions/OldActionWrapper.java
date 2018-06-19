package edu.southwestern.tasks.ut2004.actions;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import mockcz.cuni.pogamut.Client.AgentBody;
import utopia.agentmodel.actions.Action;

/**
 * This class takes Actions used by UT^2 and makes them compatible
 * with our current framework for evolving bot controllers that use
 * actions. This class can be wrapped around any of the old Actions.
 * 
 * @author Jacob Schrum
 */
public class OldActionWrapper implements BotAction {

	// An old style action, as used by UT^2
	Action action;
	
	/**
	 * Take an action for UT^2 and save it
	 * @param oldAction Old style action
	 */
	public OldActionWrapper(Action oldAction) {
		action = oldAction;
	}
	
	@Override
	public String execute(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		// This is a mock AgentBody class that emulates the old deprecated AgentBody class used in Pogamut.
		// This mock class is used by the UT^2 actions, so it has to be constructed before being passed to the execute method.
		AgentBody body = new AgentBody(bot.getBody(), bot.getRaycasting(), bot.getAct(), bot.getInfo(), bot.getSenses(), bot.getGame(), bot.getWorldView(), bot.getItems(), bot.getWeaponry());
		action.execute(body);
		return action.getClass().getName();
	}

}

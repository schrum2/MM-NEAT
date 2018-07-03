package edu.southwestern.tasks.ut2004.actions;

import java.util.logging.Level;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.unreal.agent.navigation.IUnrealPathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathNavigator;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathRunner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.loquenavigator.KefikRunner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.loquenavigator.LoqueNavigator;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import mockcz.cuni.amis.pogamut.base.agent.navigation.PathPlanner;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;
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
		AgentBody body = getAgentBody(bot);
		action.execute(body);
		return action.getClass().getName();
	}

	public static AgentBody getAgentBody(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		return new AgentBody(bot.getBody(), bot.getRaycasting(), bot.getAct(), bot.getInfo(), bot.getSenses(), bot.getGame(), bot.getWorldView(), bot.getItems(), bot.getWeaponry());
	}
	
	public static AgentMemory getAgentMemory(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		        
		// Setting these up multiple times for each action is probably a  bad idea. Better to set them up once and save and resuse them somehow. Solve this issue later.

		bot.getLog().setLevel(Level.OFF);
		
        // Set up item path executor
        IUT2004PathRunner itemKefik = new KefikRunner(bot.getBot(), bot.getInfo(), bot.getMove(), bot.getLog());            
        IUT2004PathNavigator<ILocated> itemLoque = new LoqueNavigator<ILocated>(bot.getBot(), bot.getInfo(), bot.getMove(), itemKefik, bot.getLog());
        IUnrealPathExecutor<ILocated> itemPathExecutor = new UT2004PathExecutor<ILocated>(bot.getBot(), bot.getInfo(), bot.getMove(), itemLoque);
        
        // Set up player path executor
        IUT2004PathRunner playerKefik = new KefikRunner(bot.getBot(), bot.getInfo(), bot.getMove(), bot.getLog());
        IUT2004PathNavigator<ILocated> playerLoque = new LoqueNavigator<ILocated>(bot.getBot(), bot.getInfo(), bot.getMove(), playerKefik, bot.getLog());
        IUnrealPathExecutor<ILocated> playerPathExecutor = new UT2004PathExecutor<ILocated>(bot.getBot(), bot.getInfo(), bot.getMove(), playerLoque);

		
		return new AgentMemory(getAgentBody(bot), bot.getInfo(), bot.getSenses(), bot.getPlayers(), new PathPlanner(bot.getUT2004AStarPathPlanner(), bot.getFwMap(), getAgentBody(bot)), itemPathExecutor, playerPathExecutor, bot.getItems(), bot.getWeaponry(), bot.getWorldView(), bot.getGame());
	}
	
}

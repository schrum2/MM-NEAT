package pogamut;

import cz.cuni.amis.pogamut.base.agent.params.IRemoteAgentParameters;
import cz.cuni.amis.pogamut.ut2004.agent.utils.UT2004BotDescriptor;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.utils.MultipleUT2004BotRunner;
import pogamut.hunter.HunterBot;

/**
 * TODO: This can launch several HunterBots, but we need a way to launch several bots of different types.
 *       This can hopefully be done if all bots are made to extend a common super class that selects between them.
 * @author schrum2
 *
 */
public class DifferentBotLaunchTest {
	public static void main(String[] args) {
		IRemoteAgentParameters[] params = new IRemoteAgentParameters[3];
		for (int i = 0; i < params.length; i++) {
			params[i] = new UT2004BotParameters();
		}
		MultipleUT2004BotRunner multi = new MultipleUT2004BotRunner("MultipleBots").setHost("localhost").setPort(3000);
		UT2004BotDescriptor bots = new UT2004BotDescriptor().setController(HunterBot.class).setAgentParameters(params);
		multi.setMain(true).startAgents(bots);
	}
}

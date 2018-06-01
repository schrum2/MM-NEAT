package edu.southwestern.tasks.ut2004.bots;

import cz.cuni.amis.pogamut.base.agent.params.IRemoteAgentParameters;
import cz.cuni.amis.pogamut.ut2004.agent.utils.UT2004BotDescriptor;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.utils.MultipleUT2004BotRunner;
import pogamut.hunter.HunterBot;
import pogamut.navigationbot.NavigationBot;

public class MultiBotLauncher {
	public static void launchMultipleBots(Class[] botClasses, IRemoteAgentParameters[] params) {
		assert botClasses.length == params.length : "ERROR MESSAGE HERE";
		Thread[] threads = new Thread[botClasses.length];
		for(int i = 0; i < botClasses.length; i++) {
			threads[i] = new Thread() {
				public void run() {
					IRemoteAgentParameters[] params = new IRemoteAgentParameters[1];
					for (int i = 0; i < params.length; i++) {
						params[i] = new UT2004BotParameters();
					}
					MultipleUT2004BotRunner multi = new MultipleUT2004BotRunner("Hunt").setHost("localhost").setPort(3000);
					UT2004BotDescriptor bots = new UT2004BotDescriptor().setController(botClasses.class).setAgentParameters(params);
					multi.setMain(true).startAgents(bots);
				}
			};
		}
		
		for(int i = 0; i < botClasses.length; i++) {
			threads[i].start();
		}

		for(int i = 0; i < botClasses.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


	}
	
	public static void main(String[] args) {
		Class[] botClasses = new Class[] {HunterBot.class, NavigationBot.class};
		IRemoteAgentParameters[] params = new IRemoteAgentParameters[] {new UT2004BotParameters(), new UT2004BotParameters()};
		launchMultipleBots(botClasses, params);
	}
	
}

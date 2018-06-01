package edu.southwestern.tasks.ut2004.bots;

import cz.cuni.amis.pogamut.base.agent.params.IRemoteAgentParameters;
import cz.cuni.amis.pogamut.ut2004.agent.utils.UT2004BotDescriptor;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.utils.MultipleUT2004BotRunner;
import pogamut.hunter.HunterBot;
import pogamut.navigationbot.NavigationBot;

/**
 * launches different bots into the same soldier
 * @author Jacob Schrum
 */
public class MultiBotLauncher {
	
	/**
	 * creates threads for all the given bot classes in the server
	 * @param botClasses (the classes of bot to be spawned into the server)
	 * @param params (parameters for the bot)
	 * @param host (the host of the server)
	 * @param port (the port that the server will connect to)
	 */
	public static void launchMultipleBots(Class[] botClasses, IRemoteAgentParameters[] params,String host, int port) {
		assert botClasses.length == params.length : "List of bots and bot parameters must be same length";
		Thread[] threads = new Thread[botClasses.length];

		//creates threads for each class of bot
		for(int i = 0; i < botClasses.length; i++) {
			final int index = i;
			threads[i] = new Thread() {
				public void run() {
					MultipleUT2004BotRunner multi = new MultipleUT2004BotRunner(botClasses.getClass().getName()).setHost(host).setPort(port);
					UT2004BotDescriptor bots = new UT2004BotDescriptor().setController(botClasses[index]).setAgentParameters(new IRemoteAgentParameters[] {params[index]});
					multi.setMain(true).startAgents(bots);
				}
			};
		}
		
		//starts each thread
		for(int i = 0; i < botClasses.length; i++) {
			threads[i].start();
		}

		//joins each thread to the server
		for(int i = 0; i < botClasses.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


	}
	
	/**
	 * launches a test server with the HunterBot and NavigationBot
	 */
	public static void main(String[] args) {
		Class[] botClasses = new Class[] {HunterBot.class, NavigationBot.class};
		IRemoteAgentParameters[] params = new IRemoteAgentParameters[] {new UT2004BotParameters(), new UT2004BotParameters()};
		launchMultipleBots(botClasses, params, "localhost", 3000);
	}
	
}

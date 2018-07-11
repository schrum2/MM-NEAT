package edu.southwestern.tasks.ut2004.bots;

import java.util.Arrays;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.params.IRemoteAgentParameters;
import cz.cuni.amis.pogamut.ut2004.agent.utils.UT2004BotDescriptor;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.utils.MultipleUT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import edu.southwestern.tasks.ut2004.controller.DummyController;
import edu.utexas.cs.nn.bots.UT2;
import edu.utexas.cs.nn.bots.UT2.UT2Parameters;
import fr.enib.mirrorbot4.MirrorBot4;
import fr.enib.mirrorbot4.MirrorBotParameters;
import pogamut.hunter.HunterBot;
import pogamut.hunter.HunterBotParameters;
import pogamut.navigationbot.NavigationBot;

/**
 * launches different bots into the same server
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
	public static void launchMultipleBots(@SuppressWarnings("rawtypes") Class[] botClasses, IRemoteAgentParameters[] params, String host, int port) {
		assert botClasses.length == params.length : "List of bots and bot parameters must be same length";
		Thread[] threads = new Thread[botClasses.length];
		System.out.println("===== Launch bots: " + Arrays.toString(botClasses));
		//creates threads for each class of bot
		for(int i = 0; i < botClasses.length; i++) {
			final int index = i;
			threads[i] = new Thread() {
				@SuppressWarnings("unchecked")
				public void run() {
					try {
						String className = botClasses.getClass().getName();
						// Just get the class name, not the package portion
						className = className.substring(className.lastIndexOf('.')+1);
						@SuppressWarnings("rawtypes")
						MultipleUT2004BotRunner multi = new MultipleUT2004BotRunner(className).setHost(host).setPort(port);
						@SuppressWarnings("rawtypes")
						UT2004BotDescriptor bots = new UT2004BotDescriptor().setController(botClasses[index]).setAgentParameters(new IRemoteAgentParameters[] {params[index]});
						// I believe the setMain causes certain exceptions to be caught and suppressed
						multi.setMain(true).setLogLevel(Level.OFF).startAgents(bots);
					} catch (PogamutException e) {
						// For ControllerBots, we can check if the eval ended properly or badly
						if(params[index] instanceof ControllerBotParameters) {
							// The evaluation was not successful, then we need to know the reason
							if(!((ControllerBotParameters) params[index]).getStats().evalWasSuccessful()) {
								System.out.println("ControllerBot evaluation failed!");
								e.printStackTrace();
							}
							// Otherwise, the exception is suppressed, because it is the standard complaint about killing an active bot
						} else {
							System.out.println("NOT A ControllerBot. Uncertain if exception is problematic");
							e.printStackTrace();
						}
						// Obligatory exception that happens from stopping the bot. Just suppress.
					}
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

				System.out.println("Thread " + i + " interrupted");
				System.exit(1);
			}
		}


	}

	/**
	 * Launches a test server with the HunterBot, NavigationBot, UT^2, MirrorBot, and a ControllerBot with a DummyController.
	 * 
	 * I launched these bots on servers several times, and it definitely works. However, I did notice unusual behavior when
	 * launching it multiple times in a row. For some reason, launching this code twice in quick successful causes problems.
	 * I wonder if ports are not shutting down properly when this code is terminated, so some time is needed for the ports
	 * to be cleared up.
	 */
	public static void main(String[] args) {
		@SuppressWarnings("rawtypes")
		Class[] botClasses = new Class[] {HunterBot.class, NavigationBot.class, UT2.class, MirrorBot4.class, ControllerBot.class};

		UT2Parameters ut2params = new UT2Parameters();
		ControllerBotParameters dummyParameters = new ControllerBotParameters(null, new DummyController(), "Dummy", new GameDataCollector(), 300, 4, 3000);

		IRemoteAgentParameters[] params = new IRemoteAgentParameters[] {new HunterBotParameters(), new UT2004BotParameters(), ut2params, new MirrorBotParameters(), dummyParameters};
		launchMultipleBots(botClasses, params, "localhost", 3000);
	}

}

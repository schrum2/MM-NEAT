package edu.southwestern.tasks.ut2004.bots;

import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import edu.southwestern.tasks.ut2004.controller.BotController;

public class ControllerBotParameters extends UT2004BotParameters {

	private final String name;
	private final BotController controller;
	private final GameDataCollector stats;
	private final IUT2004Server server;
	private final int evalSeconds;
	private final int desiredSkill;
	private final int botPort;

	public ControllerBotParameters(IUT2004Server server, BotController controller, String name, GameDataCollector stats,
			int evalSeconds, int desiredSkill, int botPort) {
		this.controller = controller;
		this.name = name;
		this.stats = stats;
		this.server = server;
		this.evalSeconds = evalSeconds;
		this.desiredSkill = desiredSkill;
		this.botPort = botPort;
	}

	public int getBotPort() {
		return botPort;
	}

	public int getDesiredSkill() {
		return desiredSkill;
	}

	public int getEvalSeconds() {
		return evalSeconds;
	}

	public IUT2004Server getServer() {
		return server;
	}

	public GameDataCollector getStats() {
		return stats;
	}

	/**
	 * This returns the name of the bot to be used.
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get brain controlling bot
	 *
	 * @return
	 */
	public BotController getController() {
		return controller;
	}
}

package edu.southwestern.tasks.ut2004.bots;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentStats;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import edu.southwestern.tasks.ut2004.controller.BotController;

/**
 * Sets the parameters for the bot when it launches
 * @author Jacob Shcrum
 */
public class ControllerBotParameters extends UT2004BotParameters {

	private final String name;
	private final String skin;
	private final BotController controller;
	private final GameDataCollector stats;
	private final IUT2004Server server;
	private final int evalSeconds;
	private final int desiredSkill;
	private final int botPort;

	/**
	 * sets the parameters for the bot
	 * @param server (what server the bot will use)
	 * @param controller (what the control source for the bot will be)
	 * @param name (what name the bot will use)
	 * @param stats (the bot's stats at the beginning of the game)
	 * @param evalSeconds (how long the evaluation will last)
	 * @param desiredSkill (what skill level the bot will have)
	 * @param botPort (what port the bot will use)
	 */
	public ControllerBotParameters(IUT2004Server server, BotController controller, String name, GameDataCollector stats,
			int evalSeconds, int desiredSkill, int botPort, String skin) {
		this.controller = controller;
		this.name = name;
		this.stats = stats;
		this.server = server;
		this.evalSeconds = evalSeconds;
		this.desiredSkill = desiredSkill;
		this.botPort = botPort;
		this.skin = skin;
	}

	/**
	* @returns what paort the bot is using
	*/
	public int getBotPort() {
		return botPort;
	}

	/**
	 * @return returns the skill level of the bot
	 */
	public int getDesiredSkill() {
		return desiredSkill;
	}

	/**
	 * @return returns how long the evaluation has run
	 */
	public int getEvalSeconds() {
		return evalSeconds;
	}

	/**
	 * @return returns what server the bot is in
	 */
	public IUT2004Server getServer() {
		return server;
	}

	/**
	 * @return returns the bot's stats
	 */
	public GameDataCollector getStats() {
		return stats;
	}

	/**
	 * This returns the name of the bot to be used.
	 *
	 * @return returns the bot's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get brain controlling bot
	 *
	 * @return returns controller for bot
	 */
	public BotController getController() {
		return controller;
	}

	/**
	 * Give the AgentStats instance from the bot
	 * @param stats2
	 */
	public void giveStats(AgentStats stats) {
		this.stats.giveAgentStats(stats);
	}

	/**
	 * Specify skin of bot
	 * @return
	 */
	public String getSkin() {
		return skin;
	}
	
}

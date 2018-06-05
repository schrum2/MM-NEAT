package pogamut.hunter;

import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import edu.southwestern.tasks.ut2004.bots.GameDataCollector;
import edu.southwestern.tasks.ut2004.controller.BotController;


public class HunterBotParameters extends UT2004BotParameters {
	
	private final int evalSeconds;
	private final GameDataCollector stats;
	
	public HunterBotParameters(GameDataCollector stats, int evalSeconds) {
		this.evalSeconds = evalSeconds;
		this.stats = stats;
	}
	
	/**
	 * @return returns how long the evaluation has run
	 */
	public int getEvalSeconds() {
		return evalSeconds;
	}

	
	public GameDataCollector getStats() {
		return stats;
	}
	



}

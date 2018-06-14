package pogamut.hunter;

import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;

/**
 * Added to bot to force it to terminate at the end of evaluation
 * @author Adina Friedmann
 */
public class HunterBotParameters extends UT2004BotParameters {
	
	private final int evalSeconds;

	public HunterBotParameters() {
		this(Integer.MAX_VALUE);
	}
	
	public HunterBotParameters(int evalSeconds) {
		this.evalSeconds = evalSeconds;
	}
	
	/**
	 * @return returns how long the evaluation has run
	 */
	public int getEvalSeconds() {
		return evalSeconds;
	}
}

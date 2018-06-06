package fr.enib.mirrorbot4;

import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;

/**
 * Added to bot to force it to terminate at the end of evaluation
 * @author Adina Friedmann
 */
public class MirrorBotParameters extends UT2004BotParameters {
	
	private final int evalSeconds;
	
	public MirrorBotParameters(int evalSeconds) {
		this.evalSeconds = evalSeconds;
	}
	
	/**
	 * @return returns how long the evaluation has run
	 */
	public int getEvalSeconds() {
		return evalSeconds;
	}
}

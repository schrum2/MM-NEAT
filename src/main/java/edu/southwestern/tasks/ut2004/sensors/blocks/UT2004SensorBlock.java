package edu.southwestern.tasks.ut2004.sensors.blocks;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;

/**
 *
 * @author He_Deceives
 */
public interface UT2004SensorBlock {

	public void prepareBlock(UT2004BotModuleController bot);

	/**
	 * Pass in array of inputs so far and add to them
	 *
	 * @param bot
	 *            Pogamut bot
	 * @param in
	 *            starting index in inputs array
	 * @param inputs
	 *            partially filled array of inputs to add to
	 * @return number of inputs that are now filled in array
	 */
	public int incorporateSensors(UT2004BotModuleController bot, int in, double[] inputs);

	public int incorporateLabels(int in, String[] labels);

	public int numberOfSensors();
}

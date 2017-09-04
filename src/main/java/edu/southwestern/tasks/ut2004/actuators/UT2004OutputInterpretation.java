/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.ut2004.actuators;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.actions.BotAction;

/**
 *
 * @author Jacob Schrum
 */
public interface UT2004OutputInterpretation {

	public String[] outputLabels();

	public BotAction interpretOutputs(UT2004BotModuleController bot, double[] outputs);

	public int numberOfOutputs();

	public UT2004OutputInterpretation copy();
}

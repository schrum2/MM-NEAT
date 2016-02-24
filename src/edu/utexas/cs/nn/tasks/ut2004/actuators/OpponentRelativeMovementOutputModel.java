/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.ut2004.actuators;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import edu.utexas.cs.nn.tasks.ut2004.actions.BotAction;
import edu.utexas.cs.nn.tasks.ut2004.actions.OpponentRelativeMovementAction;

/**
 *
 * @author Jacob Schrum
 */
public class OpponentRelativeMovementOutputModel implements UT2004OutputInterpretation {

    public static int MEMORY_TIME = 30;

    public String[] outputLabels() {
        return new String[]{"Left/right impulse", "Towards/back impulse", "Shoot", "Jump"};
    }

    public BotAction interpretOutputs(UT2004BotModuleController bot, double[] outputs) {
        Player opponent = bot.getPlayers().getNearestEnemy(MEMORY_TIME);
        double towards = outputs[0];
        double side = outputs[1];
        boolean shoot = outputs[2] > 0;
        boolean jump = outputs[3] > 0;
        return new OpponentRelativeMovementAction(opponent, towards, side, shoot, jump);
    }

    /**
     * - Opponent relative left/right movement impulse - Opponent relative
     * forward/back movement impulse - True/false shoot - True/false jump
     *
     * @return
     */
    public int numberOfOutputs() {
        return 4;
    }

    public UT2004OutputInterpretation copy() {
        return new OpponentRelativeMovementOutputModel();
    }
}

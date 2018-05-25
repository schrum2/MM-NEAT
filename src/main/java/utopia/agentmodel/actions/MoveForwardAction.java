/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;

/**
 *
 * @author nvh
 */
public class MoveForwardAction extends Action {

    @Override
    public String toString() {
        return "MoveForward";
    }

    @Override
    public void execute(AgentBody body) {
        body.contMove();
    }
}

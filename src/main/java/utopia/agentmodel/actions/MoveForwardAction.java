package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;

/**
 * Tells the bot to move forward without any other actions
 * @author nvh
 */
public class MoveForwardAction extends Action {

    @Override
    /**
     * allows the bot to print out a description of its actions
     */
    public String toString() {
        return "MoveForward";
    }

    @Override
    /**
     * tells the bot to execute the action
     */
    public void execute(AgentBody body) {
        body.contMove();
    }
}

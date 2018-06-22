package utopia.agentmodel.actions;

import mockcz.cuni.pogamut.Client.AgentBody;

/**
 * Tells the bot to remain stationary
 * @author nvh
 */
public class EmptyAction extends Action {

    @Override
    public String toString(){
        return "Empty";
    }

    public EmptyAction() {
    }

    @Override
    public void execute(AgentBody body) {
    }
}

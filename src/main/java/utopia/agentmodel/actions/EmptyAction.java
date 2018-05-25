/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utopia.agentmodel.actions;

import mockcz.cuni.pogamut.Client.AgentBody;

/**
 *
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

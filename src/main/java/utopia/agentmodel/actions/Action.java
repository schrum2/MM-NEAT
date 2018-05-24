package utopia.agentmodel.actions;


import java.io.Serializable;
import mockcz.cuni.pogamut.Client.AgentBody;

/**
 * The Action class represents an action that can be applied to on AgentBody
 * @author Niels van Hoorn
 */
public abstract class Action  implements Serializable {

    /**
     * The Logger to use for debugging
     */
    //protected Logger log;

    /**
     * Sets the Logger
     * @param log the logger to use
     */
    /*public void setLog(Logger log) {
        this.log = log;
    }*/
    
    /**
     * The main method a subclass should implement.
     * Executes the action on an AgentBody
     * @param body the body to perform the Acion on
     */
    public abstract void execute(AgentBody body);
}

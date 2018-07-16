package utopia.agentmodel.actions;

import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;

/**
 * Tells the bot whether to turn left or right
 * @author Jacob Schrum
 */
public class TurnAction extends OpponentRelativeAction {

    private boolean turnRight;

    @Override
    /**
     * allows the bot to print out a description of its actions
     */
    public String toString() {
        return "Turn:" + (turnRight ? "Right" : "Left");
    }

    /**
     * Tells the bot whether to turn right or left
     * @param memory (agent memory to use)
     * @param right (true =  turn right, false = turn left)
     */
    public TurnAction(AgentMemory memory, boolean right) {
        super(memory, false, false, false);
        this.turnRight = right;
    }

    @Override
    /**
     * tells the bot to execute the action
     */
    public void execute(AgentBody body) {
        turn(body, turnRight);
    }
}

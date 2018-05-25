package utopia.agentmodel.actions;

import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;

public class TurnAction extends OpponentRelativeAction {

    private boolean turnRight;

    @Override
    public String toString() {
        return "Turn:" + (turnRight ? "Right" : "Left");
    }

    public TurnAction(AgentMemory memory, boolean right) {
        super(memory, false, false, false);
        this.turnRight = right;
    }

    @Override
    public void execute(AgentBody body) {
        turn(body, turnRight);
    }
}

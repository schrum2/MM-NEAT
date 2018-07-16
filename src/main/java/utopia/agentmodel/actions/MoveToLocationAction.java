package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;

/**
 * Tells the bot to go to a given location
 * @author nvh
 */
public class MoveToLocationAction extends Action {
    private final Location target;
    private final Player focusEnemy;
    private final AgentMemory memory;
    private final boolean jump;

    @Override
    /**
     * allows the bot to print out a description of its actions
     */
    public String toString(){
        return "MoveToLocation:" + (focusEnemy == null ? "No Enemy" : focusEnemy.getName()) + ":" + target.toString();
    }

    /**
     * initializes the action with the memory, target location, enemy, and whether the bot should jump
     * @param memory (agent memory to use)
     * @param target (location to go to)
     * @param enemy (enemy that the bot is targeting)
     * @param jump (should the bot be jumping)
     */
    public MoveToLocationAction(AgentMemory memory, Location target, Player enemy, boolean jump) {
        this.memory = memory;
        this.target = target;
        this.focusEnemy = enemy;
        this.jump = jump;
    }

    /**
	 * initializes the action with the memory, target location, enemy, bot assumes it should not jump
     * @param memory (agent memory to use)
     * @param target (location to go to)
     * @param enemy (enemy that the bot is targeting)
     */
    public MoveToLocationAction(AgentMemory memory, Location target, Player enemy) {
        this(memory, target, enemy, false);
    }

    /**
     * initializes the action with the memory, and target location, bot assumes there is no enemy
     * @param memory (agent memory to use)
     * @param target (location to go to)
     */
    public MoveToLocationAction(AgentMemory memory, Location target) {
        this(memory,target,null);
    }

    @Override
   /**
    * tells the bot to execute the action and travel to a location
    */
    public void execute(AgentBody body) {
        //body.body.getLocomotion().moveTo(target);
        Move move = new Move().setFirstLocation(target);
        if(focusEnemy != null){
            move.setFocusTarget(focusEnemy.getId());
            OpponentRelativeAction.shootDecision(memory, focusEnemy, true, false);
        }
        body.act.act(move);
        if(jump){
            body.jump();
        }
        //ConditionalJumper.conditionalJump(body, target.getLocation(), true);
    }
}

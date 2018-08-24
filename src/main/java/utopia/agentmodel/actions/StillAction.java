package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;

/**
 * Tells the bot to stand still possibly shooting at enemy
 * @author Jacob Schrum
 */
public class StillAction extends OpponentRelativeAction {

    @Override
    /**
     * allows the bot to print out a description of its action in the form of a string
     */
    public String toString() {
        return "Still" + (shoot ? ":Shoot:" + (secondary ? "Alt" : "Pri") : "") + (jump ? ":Jump" : "");
    }

    /**
     * Initializes the action with the agent memory, whether the bot should shoot, whether it should use secondary firing mode, and whether it should jump
     * @param memory (agent memory to use)
     * @param shoot (should the bot shoot)
     * @param secondary (should the bot use secondary firing mode)
     * @param jump (should the bot jump)
     */
    public StillAction(AgentMemory memory, boolean shoot, boolean secondary, boolean jump) {
        super(memory, shoot, secondary, jump);
    }

    /**
     * Initializes the action with the agent memory, whether the bot should shoot, and whether it should use secondary firing mode
     * @param memory (agent memory to use)
     * @param shoot (should the bot shoot)
     * @param secondary (should the bot use secondary firing mode)
     */
    public StillAction(AgentMemory memory, boolean shoot, boolean secondary) {
        this(memory, shoot, secondary, false);
    }

    /**
     * Initializes the action with the agent memory
     * @param memory (agent memory to use)
     */
    public StillAction(AgentMemory memory) {
        this(memory, true, true);
    }

    @Override
    /**
     * tells the bot to execute the command
     */
    public void execute(AgentBody body) {
        if(body.isMoving()) { //Bot stops moving
            //System.out.println("Stop moving");
            body.stop();
        }
        Player enemy = this.memory.getCombatTarget(); //sets the enemy
        if (enemy != null && enemy.getLocation() != null) {
            body.body.getLocomotion().strafeTo(body.info.getLocation(), enemy);
            super.shootDecision(enemy); //tells bot to shoot at enemy
        } else if (memory.onElevator()){
            Location bot = memory.info.getLocation();
            if(bot != null) {
                NavPoint second = memory.nearestLiftExit();
                if(second != null && second.getLocation() != null && second.getLocation().z > bot.z){
                    body.body.getLocomotion().strafeTo(body.info.getLocation(), second);
                }
            }
        }
    }
}

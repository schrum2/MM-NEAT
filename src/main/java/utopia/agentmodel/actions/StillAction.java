package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;

/**
 *
 * @author Jacob Schrum
 */
public class StillAction extends OpponentRelativeAction {

    @Override
    public String toString() {
        return "Still" + (shoot ? ":Shoot:" + (secondary ? "Alt" : "Pri") : "") + (jump ? ":Jump" : "");
    }

    public StillAction(AgentMemory memory, boolean shoot, boolean secondary, boolean jump) {
        super(memory, shoot, secondary, jump);
    }

    public StillAction(AgentMemory memory, boolean shoot, boolean secondary) {
        this(memory, shoot, secondary, false);
    }

    public StillAction(AgentMemory memory) {
        this(memory, true, true);
    }

    @Override
    public void execute(AgentBody body) {
        if(body.isMoving()) {
            //System.out.println("Stop moving");
            body.stop();
        }
        Player enemy = this.memory.getCombatTarget();
        if (enemy != null && enemy.getLocation() != null) {
            body.body.getLocomotion().strafeTo(body.info.getLocation(), enemy);
            super.shootDecision(enemy);
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

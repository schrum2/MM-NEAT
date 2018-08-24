package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.gbinfomessages.IPlayer;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;

/**
 * Quickly turn around
 * @author Jacob Schrum
 */
public class QuickTurnAction extends Action {

    private final AgentMemory memory;

    @Override
    /**
     * allows the bot to print out a description of its action in the form of a string
     */
    public String toString() {
        return "QuickTurn";
    }

    /**
     * Initializes the action witht he agent memory
     * @param mem (agent memory to use)
     */
    public QuickTurnAction(AgentMemory mem) {
        this.memory = mem;
    }

    @Override
    /**
     * tells the bot to execute the action
     */
    public void execute(AgentBody body) {
        IPlayer enemy = memory.getCombatTarget(); //sets the enemy for the bot to target
        if (enemy == null) {
            enemy = memory.lastCombatTarget; //if no enemies are seen, last seen one is  now enemy
        }
        Location loc = body.info.getLocation();
        if (loc != null) {
            if (enemy != null) {
                body.act.act(new Move().setFirstLocation(loc).setFocusLocation(enemy.getLocation()));
            } else {
                if (body.info.getRotation() != null) {
                    // Don't suddenly turn fire on unknown opponent
                    if(memory.info.isShooting()) body.stopShoot();
                    Location facing = body.info.getRotation().toLocation().getNormalized().scale(500);
                    body.act.act(new Move().setFirstLocation(loc.add(facing)).setFocusLocation(loc.sub(facing)));
                }
            }
        }
    }
}

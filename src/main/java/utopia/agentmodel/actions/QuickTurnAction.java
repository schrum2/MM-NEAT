/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
    public String toString() {
        return "QuickTurn";
    }

    public QuickTurnAction(AgentMemory mem) {
        this.memory = mem;
    }

    @Override
    public void execute(AgentBody body) {
        IPlayer enemy = memory.getCombatTarget();
        if (enemy == null) {
            enemy = memory.lastCombatTarget;
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

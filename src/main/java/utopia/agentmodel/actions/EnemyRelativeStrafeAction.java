package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import edu.utexas.cs.nn.weapons.WeaponPreferenceTable;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;
import mockcz.cuni.pogamut.MessageObjects.Triple;

public class EnemyRelativeStrafeAction extends OpponentRelativeAction {

    @Override
    public String toString() {
        return "Strafe:" + (left ? "Left" : "Right") + (shoot ? ":Shoot:" + (secondary ? "Alt" : "Pri") : "") + (jump ? ":Jump" : "");
    }
    private final boolean left;
    private final boolean allowDodge;

    public EnemyRelativeStrafeAction(AgentMemory memory, boolean shoot, boolean secondary, boolean left, boolean jump, boolean allowDodge) {
        super(memory, shoot, secondary, jump);
        this.left = left;
        this.allowDodge = allowDodge;
    }

    // Allow dodge by default
    public EnemyRelativeStrafeAction(AgentMemory memory, boolean shoot, boolean secondary, boolean left, boolean jump) {
        this(memory, shoot, secondary, left, jump, true);
    }

    public EnemyRelativeStrafeAction(AgentMemory memory, boolean shoot, boolean secondary, boolean left) {
        this(memory, shoot, secondary, left, false);
    }

    @Override
    public void execute(AgentBody body) {
        Player enemy = this.memory.getCombatTarget();
        Triple agentLocation = this.memory.getAgentLocation();
        Triple agentRotation = this.memory.getAgentRotation();
        if (enemy != null && enemy.getLocation() != null && agentLocation != null && agentRotation != null) {
            super.shootDecision(enemy);
            Triple lookAt = Triple.locationToTriple(enemy.getLocation());

            double range = 300.0;
            double rotation = Triple.utAngleToRad(agentRotation.y);
            Triple vectorToEnemy = Triple.subtract(lookAt, agentLocation);
            rotation = rotation - Triple.utAngleToRad(Triple.angle(vectorToEnemy, new Triple(1, 0, 0)));
            double y = (left ? -1 : 1) * range * Math.cos(rotation);
            double x = (left ? 1 : -1) * range * Math.sin(rotation);
            Triple target = Triple.add(agentLocation, new Triple(x, y, 0));
            target.z = lookAt.z;
            if (allowDodge && !memory.sideWallClose(left) && Math.random() < DODGE_CHANCE) {
                body.dodge(Triple.subtract(target, memory.getAgentLocation()));
            } else {
                body.strafeToLocation(target, lookAt, 100);
            }
            // Only consider jumping if not too close to enemy
            if (enemy.getLocation().getDistance(memory.info.getLocation()) > WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 2) {
                jumpDecision(body);
            }
        } else {
            if(left){
                body.body.getLocomotion().strafeLeft(100);
            } else {
                body.body.getLocomotion().strafeRight(100);
            }
        }
    }
}

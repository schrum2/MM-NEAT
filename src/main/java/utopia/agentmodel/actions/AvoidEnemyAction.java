package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;
import mockcz.cuni.pogamut.MessageObjects.Triple;

/**
 * Tells the bot to avoid enemies that it sees
 * @author nvh
 */
public class AvoidEnemyAction extends OpponentRelativeAction {

    @Override
   /**
    * allows the bot's actions to be printed out
    */
    public String toString() {
        return "Avoid" + (shoot ? ":Shoot:" + (secondary ? "Alt" : "Pri") : "") + (jump ? ":Jump" : "");
    }

    public AvoidEnemyAction(AgentMemory memory, boolean shoot, boolean secondary, boolean jump) {
        super(memory, shoot, secondary, jump);
    }

    /**
     * initializes the bot's internal settings
     * 
     * @param memory (bot memory)
     * @param shoot (whether or not the bot should shoot)
     * @param secondary (whether or not the bot will be using the secondary firing mode)
     */
    public AvoidEnemyAction(AgentMemory memory, boolean shoot, boolean secondary) {
        this(memory, shoot, secondary, false);
    }

    @Override
    /**
     * Tells bot how to execute the actions
     */
    public void execute(AgentBody body) {
        Player enemy = this.memory.getCombatTarget(); //ID's enemy
        Triple agentLocation = this.memory.getAgentLocation(); //gives enemy location
        if (enemy != null && enemy.getLocation() != null && agentLocation != null) {
            super.shootDecision(enemy);
            Triple lookAt = Triple.locationToTriple(enemy.getLocation());
            Triple target = Triple.subtract(agentLocation, Triple.subtract(lookAt, agentLocation));
            target.z = agentLocation.z; // lookAt.z;
            body.strafeToLocation(target, lookAt, 100);
        } else if (agentLocation != null && memory.info.getRotation() != null) {
            Triple lookAt = Triple.locationToTriple(agentLocation.add(memory.info.getRotation().toLocation()));
            Triple target = Triple.subtract(agentLocation, Triple.subtract(lookAt, agentLocation));
            body.strafeToLocation(target, lookAt, 100);
        }
        jumpDecision(body); //does the bot need to jump?
    }
}

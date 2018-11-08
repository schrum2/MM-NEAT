package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import edu.utexas.cs.nn.Constants;
import edu.utexas.cs.nn.weapons.WeaponPreferenceTable;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;
import mockcz.cuni.pogamut.MessageObjects.Triple;
import utopia.Utils;

/**
 * Decides how and if the bot should approach an enemy
 * @author Jacob Schrum
 */
@SuppressWarnings("serial")
public class ApproachEnemyAction extends OpponentRelativeAction {
    protected boolean forcePath; //if true: I don't care how far you are, just get there, using nav grid (don't walk off a cliff)

    @Override
    /**
     * allows the bot to print a description of its actions
     */
    public String toString() {
        return "Approach" + (shoot ? ":Shoot:" + (secondary ? "Alt" : "Pri") : "") + (jump ? ":Jump" : "") + (forcePath ? ":ForcePath" : "");
    }

    /**
     * initializes the action
     * @param memory (agent memory to use)
     * @param shoot (should the bot shoot)
     * @param secondary (should the bot use secondary firing mode)
     * @param jump (should the bot jump)
     * @param forcePath (should the bot find a way to a given enemy)
     */
    public ApproachEnemyAction(AgentMemory memory, boolean shoot, boolean secondary, boolean jump, boolean forcePath) {
        super(memory, shoot, secondary, jump);
        this.forcePath = forcePath; 
    }

    @Override
    /**
     * tells the bot to carry out the action
     */
    public void execute(AgentBody body) {
        Player enemy = this.memory.getCombatTarget();
        if (enemy != null && enemy.getLocation() != null) {
            super.shootDecision(enemy);
            runToOrPath(body, enemy);
            // Only consider jumping if not too close to enemy
            if (enemy.getLocation().getDistance(memory.info.getLocation()) > WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 3) {
                jumpDecision(body);
            }
        } else {
            enemy = memory.lastCombatTarget;
            if (enemy == null) {
                try {
                    enemy = memory.players.getNearestEnemy(Constants.MEMORY_TIME.getDouble());
                } catch (NullPointerException e) {
                    //System.out.println("Ignored NullPointerException from memory.players.getNearestEnemy in ApproachEnemyAction");
                }
            }
            if(enemy == null){
                enemy = memory.lastEnemySpotting;
                if(enemy != null) {
                    System.out.println("\tLast enemy seen of any kind: " + enemy.getName());
                }
            }
            if (enemy != null) {
                System.out.println("\tEnemy:" + enemy.getName() + ":loc:" + enemy.getLocation());
                runToOrPath(body, enemy);
            } else {
                // Don't do this if walls are in the way
                System.out.println("\tContinuous Move");
                body.contMove();
            }
        }
    }

   
    /**
     * Decides whether the bot should run directly at a given enemy, or use the navGrid to approach them
     * @param body (the agent itself)
     * @param enemy (enemmy bot is looking at)
     */
    private void runToOrPath(AgentBody body, Player enemy) {
        Location agent = this.memory.info.getLocation();
        double distance = (agent != null && enemy.getLocation() != null) ? enemy.getLocation().getDistance(agent) : Double.MAX_VALUE;
        if (!forcePath && distance < Constants.MAX_BATTLE_DISTANCE.getInt() && !memory.isAboveMe(enemy)) {//ignores nav grid and directly approaches enemy
            double half = Constants.MAX_BATTLE_DISTANCE.getInt() / 2;
            Triple agentLocation = this.memory.getAgentLocation();
            Triple agentRotation = this.memory.getAgentRotation();
            if (agentLocation != null && agentRotation != null && distance < half && (distance / half) < Math.random()) {
                boolean left = Utils.myRandom.nextBoolean();
                System.out.println(body.info.getName() + ":ZIGZAG:" + (left?"Left":"Right"));
                
                Triple lookAt = Triple.locationToTriple(enemy.getLocation());
                double range = 250.0;
                double rotation = Triple.utAngleToRad(agentRotation.y);
                Triple vectorToEnemy = Triple.subtract(lookAt, agentLocation);
                rotation = rotation - Triple.utAngleToRad(Triple.angle(vectorToEnemy, new Triple(1, 0, 0)));
                double y = (left ? -1 : 1) * range * Math.cos(rotation);
                double x = (left ? 1 : -1) * range * Math.sin(rotation);
                Triple target = Triple.add(Triple.locationToTriple(enemy.getLocation()), new Triple(x, y, 0));

                body.body.getLocomotion().strafeTo(target, enemy.getId());
            } else {
                body.runToTarget(enemy);
            }
        } else {
            memory.playerPathExecutor.followPath(memory.pathPlanner.computePath(enemy)); //uses nav grid to plot a course to enemy
        }
    }
}

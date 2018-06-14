package utopia.controllers.scripted;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import mockcz.cuni.pogamut.Client.AgentMemory;
import mockcz.cuni.pogamut.MessageObjects.Triple;
import edu.utexas.cs.nn.weapons.WeaponPreferenceTable;
import utopia.agentmodel.Controller;
import utopia.agentmodel.actions.Action;
import utopia.agentmodel.actions.ApproachEnemyAction;
import utopia.agentmodel.actions.AvoidEnemyAction;

/**
 *
 * @author He_Deceives
 * Special behavior for when using shield gun
 */
public class ShieldGunController extends Controller {

    Item target;
    private final PathController pathController;
    public static final double SHIELD_RUSH_RANGE = 500;

    public ShieldGunController(PathController pathController) {
        this.target = null;
        this.pathController = pathController;
    }

    @Override
    public void registerActions() {
        register("Shield Attack");
        register("Retreat");
        register("Fall Through");
        register("Item Path");
    }

    @Override
    public Action control(AgentMemory memory) {
        Player nearest = memory.getSeeEnemy();
        target = getTarget(memory);
        if (nearest != null && memory.info.getLocation() != null) {
            memory.lastPlayerDamaged = null;
            double enemyDistance = memory.info.getLocation().getDistance(nearest.getLocation());
            double itemDistance = Double.MAX_VALUE;
            if (target != null) {
                itemDistance = memory.info.getLocation().getDistance(target.getLocation());
            }
            if (itemDistance > enemyDistance) {
                if (enemyDistance < SHIELD_RUSH_RANGE) {
                    takeAction("Shield Attack");
                    return new ApproachEnemyAction(memory, true, false, false, false);
                } else if (nearest.getFiring() != 0 && enemyDistance < WeaponPreferenceTable.WeaponTableEntry.MAX_RANGED_RANGE && !memory.senses.isColliding()) {
                    takeAction("Retreat");
                    return new AvoidEnemyAction(memory, true, true, Math.random() < 0.6);
                }
            }
        }

        if (target == null) {
            takeAction("Fall Through");
            return null;
        }
        takeAction("Item Path");
        return this.pathController.control(memory);
    }

    public Item getTarget(AgentMemory memory) {
        Triple agentLoc = memory.getAgentLocation();
        if (agentLoc == null) {
            return null;
        }
        Player nearest = memory.getSeeEnemy();
        Triple toEnemy = new Triple(0, 0, 0);
        if (nearest != null) {
            // Try to find a way around enemies
            Triple enemyLoc = Triple.locationToTriple(nearest.getLocation());
            toEnemy = Triple.subtract(enemyLoc, agentLoc);
        }

        Item closestWeapon = memory.getNearestWeapon(toEnemy);
        Item closestAmmo = memory.getNearestUsableAmmo(toEnemy);
        Item winner = null;

        if (closestWeapon == null || memory.weaponry.hasWeapon(closestWeapon.getType())) {
            winner = closestAmmo;
        } else if (closestAmmo == null) {
            winner = closestWeapon;
        } else {
            Triple ammoLoc = Triple.locationToTriple(closestAmmo.getLocation());
            Triple weaponLoc = Triple.locationToTriple(closestWeapon.getLocation());

            if (nearest != null) {
                ammoLoc = Triple.add(ammoLoc, toEnemy);
                weaponLoc = Triple.add(weaponLoc, toEnemy);
            }
            double ammoDistance = Triple.distanceInSpace(ammoLoc, agentLoc);
            double weaponDistance = Triple.distanceInSpace(weaponLoc, agentLoc);

            winner = (ammoDistance < weaponDistance ? closestAmmo : closestWeapon);
        }
        return winner;
    }

    @Override
    public void reset() {
        target = null;
    }
}

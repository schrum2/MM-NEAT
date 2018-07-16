package utopia.controllers.scripted;

import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import mockcz.cuni.pogamut.Client.AgentMemory;
import utopia.agentmodel.actions.Action;
import utopia.agentmodel.Controller;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import edu.utexas.cs.nn.weapons.WeaponPreferenceTable;
import utopia.agentmodel.actions.ApproachEnemyAction;
import utopia.agentmodel.actions.AvoidEnemyAction;
import utopia.agentmodel.actions.OpponentRelativeAction;
import utopia.agentmodel.actions.StillAction;
import utopia.controllers.TWEANN.TWEANNController;

public class ObservingController extends Controller {

    public static final double ABLE_TO_OBSERVE_DISTANCE = 800;
    public static final double TOO_CLOSE_DISTANCE = 400;
    private final TWEANNController battleController;

    public ObservingController(TWEANNController battleController) {
        this.battleController = battleController;
    }

    @Override
    public void registerActions() {
        register("Approach From Far");
        register("Still On High Ground");
        register("Retreat Because Close");
        register("Battle Action");
    }

    /**
     * Some options for movement should probably be added
     * @param memory
     * @return
     */
    @Override
    public Action control(AgentMemory memory) {
        //memory.changeWeapon(UT2004ItemType.LINK_GUN);  <-- Now only done if far or on high ground
        Player enemy = memory.getCombatTarget();
        double distance = enemy == null ? Double.MAX_VALUE : memory.info.getLocation().getDistance(enemy.getLocation());
        Player nearest = memory.getSeeEnemy();
        double nearestDistance = nearest == null ? Double.MAX_VALUE : memory.info.getLocation().getDistance(nearest.getLocation());
        if (enemy != null
                && (nearestDistance > WeaponPreferenceTable.WeaponTableEntry.MAX_MELEE_RANGE * 3)
                && distance > ABLE_TO_OBSERVE_DISTANCE
                && !memory.botHasHighGround()) {
            takeAction("Approach From Far");
            memory.changeWeapon(UT2004ItemType.LINK_GUN);
            return new ApproachEnemyAction(memory, false, false, false, false);
        } else if (memory.botHasHighGround() && !memory.isThreatened()) {
            takeAction("Still On High Ground");
            //memory.changeWeapon(UT2004ItemType.LINK_GUN);
            return new StillAction(memory, false, false, false);
        }
        //return new StillAction(memory, false, false, false);
        Action action = battleController.control(memory);
        if (action instanceof OpponentRelativeAction) {
            OpponentRelativeAction result = (OpponentRelativeAction) action;
            result.observe();
            if (enemy != null && distance < TOO_CLOSE_DISTANCE && result instanceof ApproachEnemyAction) {
                takeAction("Retreat Because Close");
                return new AvoidEnemyAction(memory, false, false, false);
            }
        }
        takeAction("Battle Action");
        return action;
    }

    @Override
    public void reset() {
    }
}

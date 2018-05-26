package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.MessageObjects.Triple;
import java.util.ArrayList;
import utopia.Utils;

public abstract class ShootingAction extends Action {

    private static final double AIM_THRESHOLD = 0.1;
    protected boolean shoot = false;
    protected boolean secondaryFire = false;
    protected Triple agentRotation;
    protected Triple agentLocation;
    private ArrayList<Player> players;

    public ShootingAction(boolean shoot, boolean secondaryFire, Triple agentRotation, Triple agentLocation, ArrayList<Player> players) {
        this.shoot = shoot;
        this.secondaryFire = secondaryFire;
        this.agentRotation = agentRotation;
        this.agentLocation = agentLocation;
        this.players = players;
    }

    public ShootingAction(Triple agentRotation, Triple agentLocation) {
        this(false, false, agentRotation, agentLocation, null);
    }

    @Override
    public void execute(AgentBody body) {
        if (this.shoot) {
            Triple targetVector = Triple.rotationAsVectorUTUnits(agentRotation);
            double scale = 10000;

            Triple playerTarget = null;
            for (Player p : players) {
                Triple enemyLocation = Triple.locationToTriple(p.getLocation());
                if (enemyLocation == null) {
                    continue;
                }
                double angleToPlayer = Utils.relativeAngleToTarget(agentLocation, agentRotation, enemyLocation);
                if (angleToPlayer < AIM_THRESHOLD) {
                    playerTarget = new Triple(enemyLocation.x,enemyLocation.y,enemyLocation.z);
                    break;
                }
            }

            // Is ok to shoot directly at player if he is sort of in line of sight anyway
            Triple shootTarget = null;
            if (playerTarget == null) {
                targetVector = new Triple(targetVector.x * scale, targetVector.y * scale, agentLocation.z);
                shootTarget = Triple.add(agentLocation, targetVector);
            } else {
                shootTarget = playerTarget;
            }

            if (secondaryFire) {
                body.shootAlternate(shootTarget);
            } else {
                body.shoot(shootTarget);
            }
        } else {
            body.stopShoot();
        }
    }
}

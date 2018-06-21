package utopia.agentmodel.actions;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.MessageObjects.Triple;
import java.util.ArrayList;
import utopia.Utils;

/**
 * Tells the bot to dage around while shooting; minimizes chances of getting hit while attacking
 * @author Jacob Schrum
 */
public class DodgeShootAction extends ShootingAction {

    @Override
    /**
     * allows the bot to print a description of its actions
     */
    public String toString(){
        return "Dodge:" + (turn == null ? direction : (turn < -0.5 ? "Left" : (turn > 0.5 ? "Right" : "Forward"))) + (shoot ? ":Shoot:" + (secondaryFire ? "Alt" : "Pri") : "");
    }

    private Double turn = null;
    private Triple direction;

    public DodgeShootAction(Triple direction, boolean shoot, boolean secondaryFire, Triple agentRotation, Triple agentLocation, ArrayList<Player> players) {
        super(shoot,secondaryFire,agentRotation,agentLocation,players);
        this.direction = direction;
    }

    public DodgeShootAction(double turn, boolean shoot, boolean secondaryFire, Triple agentRotation, Triple agentLocation, ArrayList<Player> players) {
        this(directionFromTurn(turn), shoot, secondaryFire, agentRotation, agentLocation,  players);
        // Just for the sake of the toString() at this point
        this.turn = Utils.limitBetween(turn, Math.PI);
    }

    /**
     * 
     * @param turn
     * @return
     */
    private static Triple directionFromTurn(Double turn){
        if(turn == null) {
            return new Triple(Utils.randposneg() * Math.random(), Utils.randposneg() * Math.random(), Utils.randposneg() * Math.random());
        } else {
            double temp = Utils.limitBetween(turn, Math.PI);
            if (temp < -0.5) {
                return (new Triple(0, -1, 0));
            } else if (temp > 0.5) {
                return (new Triple(0, 1, 0));
            } else {
                return (new Triple(1, 0, 0));
            }
        }
    }

    /**
     * initializes the action with the agent's turn, rotation, and location 
     * @param turn
     * @param agentRotation
     * @param agentLocation
     */
    public DodgeShootAction(double turn, Triple agentRotation, Triple agentLocation) {
        this(turn, false, false, agentRotation, agentLocation, null);
    }

    /**
     * initializes the action with the agent's direction, rotation, and location 
     * @param direction (direction in which the agent is heading)
     * @param agentRotation (how the agent is rotated)
     * @param agentLocation (where
     */
    public DodgeShootAction(Triple direction, Triple agentRotation, Triple agentLocation) {
        this(direction, false, false, agentRotation, agentLocation, null);
    }

    @Override
    /**
     * tells the agent to execute the action
     */
    public void execute(AgentBody body) {
        //shooting
        super.execute(body);
        body.dodge(direction, true);
    }
}

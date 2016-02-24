package edu.utexas.cs.nn.tasks.ut2004.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import edu.utexas.cs.nn.tasks.ut2004.Util;
import javax.vecmath.Vector3d;

/**
 *
 * @author Jacob Schrum
 */
public class OpponentRelativeMovementAction implements BotAction {

    /*
     * Distance to each movement target
     */
    public static final double MOVEMENT_MAGNITUDE = 100;
    private final Player player;
    private final double towards;
    private final double side;
    private final boolean shoot;
    private final boolean jump;

    public OpponentRelativeMovementAction(Player player, double towards, double side, boolean shoot, boolean jump) {
        this.player = player;
        this.towards = towards;
        this.side = side;
        this.shoot = shoot;
        this.jump = jump;
    }

    public String execute(UT2004BotModuleController bot) {
        if (player != null) {
            Location opponentLocation = player.getLocation();
            UnrealId opponentId = player.getId();
            Location botLocation = bot.getInfo().getLocation();
            if (opponentId != null && opponentLocation != null && botLocation != null) {
                // vector-to-opponent
                Location botToOpponent = opponentLocation.sub(botLocation);
                botToOpponent = new Location(botToOpponent.x, botToOpponent.y, 0); // Ignore height differences
                // Counter-clockwise angle from vector-to-opponent to vector-to-destination
                double polarAngle = Math.atan2(side, towards);
                // vector-to-destination
                Vector3d botToDestination = Util.rotateVectorInPlane(botToOpponent.asVector3d(), polarAngle);
                // Destination
                Location destination = botLocation.add(new Location(botToDestination.x, botToDestination.y, botToDestination.z));

                // Now start issuing commands to bot
                bot.getMove().strafeTo(destination, opponentId);
                if (jump) {
                    bot.getMove().jump();
                }
                if (shoot) {
                    bot.getShoot().shoot(opponentId);
                } else {
                    bot.getShoot().stopShoot();
                }
                return "[Move " + destination + "]" + (jump ? "[Jump]" : "") + (shoot ? "[Shoot]" : "");
            }
        }
        bot.getMove().stopMovement();
        bot.getShoot().stopShooting();
        return "[No Action]";
    }
}

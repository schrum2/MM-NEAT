package edu.southwestern.tasks.ut2004.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import edu.southwestern.tasks.ut2004.Util;
import javax.vecmath.Vector3d;

/**
 *This class determines how the bot should move in relation to an identifies opponent
 *
 * @author Jacob Schrum
 */
public class OpponentRelativeMovementAction implements BotAction {

	/*
	 * Distance to each movement target
	 */
	public static final double MOVEMENT_MAGNITUDE = 100;
	private final Player opponent;
	private final double distanceFromOpponent;
	private final double strafeLeftRight;
	private final boolean shoot;
	private final boolean jump;

	/**
	 * gathers movement data about the opponent
	 * 
	 * @param opponent (player identified as opponent)
	 * @param distanceFromOpponent (distance between player and opponent)
	 * @param strafeLeftRight	(strafing around opponent, negative = left, positive = right, magnitude = speed)
	 * @param shoot (should the bot shoot)
	 * @param jump (should the bot jump)
	 */
	public OpponentRelativeMovementAction(Player opponent, double distanceFromOpponent, double strafeLeftRight, boolean shoot, boolean jump) {
		this.opponent = opponent;
		this.distanceFromOpponent = distanceFromOpponent;
		this.strafeLeftRight = strafeLeftRight;
		this.shoot = shoot;
		this.jump = jump;
	}
	/**
	 *instructs bot on how to respond to opponent data
	 *
	 * @param bot (identifies which bot should execute the command)
	 * @return (returns a list in the form of strings of what actions the bot executed)
	 */
	public String execute(UT2004BotModuleController bot) {
		if (opponent != null) {
			Location opponentLocation = opponent.getLocation();
			UnrealId opponentId = opponent.getId();
			Location botLocation = bot.getInfo().getLocation();
			if (opponentId != null && opponentLocation != null && botLocation != null) {
				// vector-to-opponent
				Location botToOpponent = opponentLocation.sub(botLocation);
				botToOpponent = new Location(botToOpponent.x, botToOpponent.y, 0); // Ignore
																					// height
																					// differences
				// Counter-clockwise angle from vector-to-opponent to
				// vector-to-destination
				double polarAngle = Math.atan2(strafeLeftRight, distanceFromOpponent);
				// vector-to-destination
				Vector3d botToDestination = Util.rotateVectorInPlane(botToOpponent.asVector3d(), polarAngle);
				// Destination
				Location destination = botLocation
						.add(new Location(botToDestination.x, botToDestination.y, botToDestination.z));

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

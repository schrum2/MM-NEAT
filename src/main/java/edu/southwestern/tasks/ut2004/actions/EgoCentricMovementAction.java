package edu.southwestern.tasks.ut2004.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.UT2004Util;
import javax.vecmath.Vector3d;

/**
 *Moves the bot in relation to itself, using turns instead of strafing
 *
 * @author Jacob Schrum
 */
public class EgoCentricMovementAction implements BotAction {

	/*
	 * Distance to each movement target
	 */
	private static final double TURN_ACTIVATION = 0.01;
	private static final double MOVE_ACTIVATION = 0.1;
	private static final double MOVEMENT_MAGNITUDE = 300;
	private final double move;
	private final double turn;
	private final boolean shoot;
	private final boolean jump;
	private final boolean moveBackwards;

	/**
	 * assigns variables based on the bot status
	 * 
	 * @param moveForwardBackward (negative = backward, positive = forward, magnitude = speed)
	 * @param turn (negative = left, positive = right, magnitude = speed)
	 * @param shoot (should the bot shoot)
	 * @param jump (should the bot jump_
	 */
	public EgoCentricMovementAction(double moveForwardBackward, double turn, boolean shoot, boolean jump) {
		this.moveBackwards = moveForwardBackward < 0;
		this.move = Math.abs(moveForwardBackward * 2);
		this.turn = turn * Math.PI;
		this.shoot = shoot;
		this.jump = jump;
	}

	/**
	 * Tells the bot how it should move without taking any other players into account
	 * 
	 * @param bot (the bot to execute the commands)
	 * @return returns a list of commands executed by the bot in the form of a string
	 */
	public String execute(UT2004BotModuleController bot) {
		Rotation botRotation = bot.getInfo().getRotation();
		Location botLocation = bot.getInfo().getLocation();
		if (botRotation != null && botLocation != null) {
			String action = "";
			if (this.shoot) {
				action += "[Shoot]";
				Vector3d targetVector = UT2004Util.rotationAsVectorUTUnits(botRotation);
				double scale = 10000;
				targetVector.normalize();
				targetVector.scale(scale);
				Location shootTarget = botLocation.add(new Location(targetVector.x, targetVector.y, 0));
				bot.getShoot().shoot(shootTarget);
				// bot.getShoot().shoot();
			} else {
				bot.getShoot().stopShooting();
			}
			boolean shouldTurn = !UT2004Util.isBetween(turn, TURN_ACTIVATION);
			if (move < MOVE_ACTIVATION && shouldTurn) {
				action += "[Turn]";
				bot.getMove().stopMovement();
				bot.getMove().turnHorizontal((int) ((turn / Math.PI) * 180));
			} else if (move >= MOVE_ACTIVATION) {
				if (moveBackwards || shouldTurn) {
					double rotation = UT2004Util.utAngleToRad(botRotation.getYaw());
					if (shouldTurn) {
						rotation = rotation + this.turn;
					}
					double x = MOVEMENT_MAGNITUDE * Math.cos(rotation);
					double y = MOVEMENT_MAGNITUDE * Math.sin(rotation);
					Location lookAt = botLocation.add(new Location(x, y, 0));
					Location target = moveBackwards ? new Location(-lookAt.x, -lookAt.y, lookAt.z) : lookAt;
					bot.getMove().strafeTo(target, lookAt);
					action += "[Move to " + target + "]";
					if (jump) {
						action += "[Jump]";
						bot.getMove().jump();
					}
				} else {
					bot.getMove().moveContinuos();
					action += "[Move forward]";
				}
			} else {
				action += "[Stop]";
				bot.getMove().stopMovement();
			}
			return action;
		}
		bot.getMove().stopMovement();
		bot.getShoot().stopShooting();
		return "[Nothing]";
	}
}

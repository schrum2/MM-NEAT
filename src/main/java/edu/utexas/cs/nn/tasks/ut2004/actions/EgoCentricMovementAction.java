package edu.utexas.cs.nn.tasks.ut2004.actions;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.utexas.cs.nn.tasks.ut2004.Util;
import javax.vecmath.Vector3d;

/**
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

	public EgoCentricMovementAction(double towards, double turn, boolean shoot, boolean jump) {
		this.moveBackwards = towards < 0;
		this.move = Math.abs(towards * 2);
		this.turn = turn * Math.PI;
		this.shoot = shoot;
		this.jump = jump;
	}

	public String execute(UT2004BotModuleController bot) {
		Rotation botRotation = bot.getInfo().getRotation();
		Location botLocation = bot.getInfo().getLocation();
		if (botRotation != null && botLocation != null) {
			String action = "";
			if (this.shoot) {
				action += "[Shoot]";
				Vector3d targetVector = Util.rotationAsVectorUTUnits(botRotation);
				double scale = 10000;
				targetVector.normalize();
				targetVector.scale(scale);
				Location shootTarget = botLocation.add(new Location(targetVector.x, targetVector.y, 0));
				bot.getShoot().shoot(shootTarget);
				// bot.getShoot().shoot();
			} else {
				bot.getShoot().stopShooting();
			}
			boolean shouldTurn = !Util.isBetween(turn, TURN_ACTIVATION);
			if (move < MOVE_ACTIVATION && shouldTurn) {
				action += "[Turn]";
				bot.getMove().stopMovement();
				bot.getMove().turnHorizontal((int) ((turn / Math.PI) * 180));
			} else if (move >= MOVE_ACTIVATION) {
				if (moveBackwards || shouldTurn) {
					double rotation = Util.utAngleToRad(botRotation.getYaw());
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

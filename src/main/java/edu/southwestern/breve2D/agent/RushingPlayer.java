package edu.southwestern.breve2D.agent;

import edu.southwestern.breve2D.Breve2DGame;
import edu.southwestern.util.CartesianGeometricUtilities;
import edu.southwestern.util.util2D.Tuple2D;
import java.util.ArrayList;

public class RushingPlayer implements AgentController {
	/**
	 * Default constructor for rushing player
	 * 
	 * Sets player's actions based on the position of the nearest monster
	 */
	public Breve2DAction getAction(Breve2DGame game) {
		Tuple2D player = game.getPlayerPosition(); //saves player position into Tuple2D
		ArrayList<Agent> monsters = game.monstersByDistanceFrom(player); //creates list organizing monsters by distance from player
		if (monsters.isEmpty()) {
			return new Breve2DAction(0, 1); //action if there are no monsters on screen
		}
		Agent target = monsters.get(0); 
		int index = 1;
		while (!game.getPlayer().isFacing(target.getPosition(), Math.PI / 2) && index < monsters.size()) {
			target = monsters.get(index++); //adjusts target until player is facing it
		}
		double angle = CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(player, target,
				game.getPlayerHeading());
		// return new Breve2DAction(angle/Math.PI, 1);
		return new Breve2DAction(Math.signum(angle), 1); //action adjusted to account for target angle
	}
	/**
	 * Resets players - is currently empty
	 */
	public void reset() {
	}
}

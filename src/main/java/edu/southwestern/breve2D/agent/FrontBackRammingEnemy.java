/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.breve2D.agent;

import edu.southwestern.breve2D.Breve2DGame;
import edu.southwestern.breve2D.dynamics.RammingDynamics;
import edu.southwestern.util.util2D.Tuple2D;

/**
 * Controls the enemy Agents based on the Player Agent's position;
 * The closest monster Agent will attempt to rush at the player
 * while the other monster Agents will attempt to escape.
 *
 * @author Jacob Schrum
 */
public class FrontBackRammingEnemy implements AgentController {

	private final EscapingPlayer escape;
	private final RushingPlayer rush;

	/**
	 * Constructor for the FrontBackRammingEnemy
	 */
	public FrontBackRammingEnemy() {
		this.escape = new EscapingPlayer();
		this.rush = new RushingPlayer();
	}

	/**
	 * Returns the actions each monster Agent should take based on the player's current position
	 * 
	 * @param game A specific instance of a Breve2DGame
	 * @return A Breve2DAction representing the actions that the monster Agent should take
	 */
	public Breve2DAction getAction(Breve2DGame game) {
		Agent player = game.getPlayer();
		Agent nearestMonter = game.nearestMonsterToPosition(player);
		Tuple2D ramOffset = ((RammingDynamics) game.dynamics).getRamOffset();
		Tuple2D ramPosition = nearestMonter.getPosition().add(ramOffset.rotate(nearestMonter.getHeading()));
		if (player.distance(ramPosition) < player.distance(nearestMonter)) {
			return this.escape.getAction(game);
		} else {
			return this.rush.getAction(game);
		}
	}

	/**
	 * Resets the Escaping and the Rushing Agents
	 */
	public void reset() {
		escape.reset();
		rush.reset();
	}
}

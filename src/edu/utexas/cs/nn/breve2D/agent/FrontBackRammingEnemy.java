/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.breve2D.agent;

import edu.utexas.cs.nn.breve2D.Breve2DGame;
import edu.utexas.cs.nn.breve2D.dynamics.RammingDynamics;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

/**
 * TODO!
 *
 * @author Jacob Schrum
 */
public class FrontBackRammingEnemy implements AgentController {

    private final EscapingPlayer escape;
    private final RushingPlayer rush;

    public FrontBackRammingEnemy() {
        this.escape = new EscapingPlayer();
        this.rush = new RushingPlayer();
    }

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

    public void reset() {
        escape.reset();
        rush.reset();
    }
}

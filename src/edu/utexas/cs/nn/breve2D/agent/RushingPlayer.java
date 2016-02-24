package edu.utexas.cs.nn.breve2D.agent;

import edu.utexas.cs.nn.breve2D.Breve2DGame;
import edu.utexas.cs.nn.util.CartesianGeometricUtilities;
import edu.utexas.cs.nn.util.util2D.Tuple2D;
import java.util.ArrayList;

public class RushingPlayer implements AgentController {

    public Breve2DAction getAction(Breve2DGame game) {
        Tuple2D player = game.getPlayerPosition();
        ArrayList<Agent> monsters = game.monstersByDistanceFrom(player);
        if (monsters.isEmpty()) {
            return new Breve2DAction(0, 1);
        }
        Agent target = monsters.get(0);
        int index = 1;
        while (!game.getPlayer().isFacing(target.getPosition(), Math.PI / 2) && index < monsters.size()) {
            target = monsters.get(index++);
        }
        double angle = CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(player, target, game.getPlayerHeading());
        //return new Breve2DAction(angle/Math.PI, 1);
        return new Breve2DAction(Math.signum(angle), 1);
    }

    public void reset() {
    }
}

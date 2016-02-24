package edu.utexas.cs.nn.breve2D.agent;

import edu.utexas.cs.nn.breve2D.Breve2DGame;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.util.CartesianGeometricUtilities;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class EscapingPlayer implements AgentController {

    private Tuple2D previousTarget = null;

    public Breve2DAction getAction(Breve2DGame game) {
        final Tuple2D player = game.getPlayerPosition();
        final double playerHeading = game.getPlayerHeading();
        Agent nearestMonster = game.nearestMonsterToPosition(player);

        ArrayList<Agent> monsters = game.getMonsters();
        Collections.sort(monsters, new Comparator<Agent>() {
            public int compare(Agent o1, Agent o2) {
                double a1 = CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(player, o1.getPosition(), playerHeading);
                double a2 = CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(player, o2.getPosition(), playerHeading);
                return (int) Math.signum(a1 - a2);
            }
        });

        //System.out.println(monsters);

        double turn, force;
        int left = 0, right = 0;
        double biggestDiff = 0;
        Tuple2D loc = game.getPlayerPosition();
        for (int i = 0; i < monsters.size(); i++) {
            if (!monsters.get(i).isDead()) {
                int i2 = ((i + 1) % monsters.size());
                while (monsters.get(i2).isDead()) {
                    i2 = ((i2 + 1) % monsters.size());
                }
                if (CommonConstants.watch) {
                    game.addLine(Color.BLACK, monsters.get(i), monsters.get(i2));
                }
                loc = (monsters.get(i).getPosition().midpoint(monsters.get(i2).getPosition()));
                if (!CartesianGeometricUtilities.sourceHeadingTowardsTarget(playerHeading, player, loc, 3 * Math.PI / 4)) {
                    double diff = monsters.get(i).distance(monsters.get(i2));
                    if (previousTarget != null && previousTarget.sub(loc).length() < Breve2DGame.AGENT_MAGNITUDE) {
                        diff += 1;
                    }

                    if (diff > biggestDiff) {
                        biggestDiff = diff;
                        left = i;
                        right = i2;
                    }
                }
            }
        }

        Color c = Color.RED;
        if (biggestDiff > 0) {
            loc = monsters.get(left).getPosition().midpoint(monsters.get(right).getPosition());

            double leftDistance = monsters.get(left).distance(player);
            double rightDistance = monsters.get(right).distance(player);
            Tuple2D closer = leftDistance < rightDistance ? monsters.get(left).getPosition() : monsters.get(right).getPosition();
            double borderAngle = loc.angleBetweenTargets(closer, player);
            boolean tooSharp = borderAngle < Math.PI / 4;

//            double anglePreviousAndMidpoint = previousTarget == null ? 0 : player.angleBetweenTargets(loc, previousTarget);
//            boolean pastThreshold = anglePreviousAndMidpoint < Math.PI / 2;
//            boolean pointsDiffer = previousTarget.sub(loc).length() > 20;
            if (CommonConstants.watch) {
                c = tooSharp ? Color.MAGENTA : Color.BLACK;
                game.addLine(c, loc, closer);
                game.addLine(c, loc, player);
            }
            if (tooSharp) {
                // Too close to border: look past it
                Tuple2D midToMonster = closer.sub(loc);
                Tuple2D swap1 = new Tuple2D(-midToMonster.y, midToMonster.x);
                Tuple2D swap2 = new Tuple2D(midToMonster.y, -midToMonster.x);
                double distance1 = loc.add(swap1).distance((ILocated2D) player);
                double distance2 = loc.add(swap2).distance((ILocated2D) player);
                Tuple2D farther = distance1 > distance2 ? swap1 : swap2;
                Tuple2D midpoint = loc;
                loc = loc.add(farther.normalize().mult(100));
                if (CommonConstants.watch) {
                    game.addLine(Color.GREEN, loc, midpoint);
                }
            }

            double angleTo = CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(player, loc, CartesianGeometricUtilities.restrictRadians(playerHeading + Math.PI));
            //turn = angleTo / Math.PI;
            turn = Math.signum(angleTo);
            previousTarget = loc;
            c = Color.BLUE;
        } else {
            turn = 0;
        }

        if (CommonConstants.watch) {
            game.addLine(c, player, loc);
        }

        if(nearestMonster == null) { // why does this happen?
            return new Breve2DAction(0, 0);
        }
        double angle = CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(player, nearestMonster.getPosition(), playerHeading);
        if (angle >= Math.PI / 2) {
            //turn = angle / Math.PI;
            turn = Math.signum(angle);
        }
        force = -1;
        return new Breve2DAction(turn, force);
    }

    public void reset() {
        previousTarget = null;
    }
}

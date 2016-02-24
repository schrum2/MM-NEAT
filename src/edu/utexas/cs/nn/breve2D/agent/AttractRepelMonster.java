package edu.utexas.cs.nn.breve2D.agent;

import edu.utexas.cs.nn.breve2D.Breve2DGame;
import edu.utexas.cs.nn.util.CartesianGeometricUtilities;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

public class AttractRepelMonster implements AgentController {

    private final int index;
    private final int sign;

    public AttractRepelMonster(int index, boolean forward) {
        this.index = index;
        this.sign = forward ? 1 : -1;
    }

    public Breve2DAction getAction(Breve2DGame game) {
        Tuple2D player = game.getPlayerPosition();
        Tuple2D monster = game.getMonsterPosition(index);
        double monsterRadians = game.getMonsterRadians(index);
        //if(player[1] != monster[1] || player[0] >= monster[0]) return new double[2];
        double angle = CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(monster, player, monsterRadians);
        //System.out.println("P:"+player[0]+","+player[1]+" M:"+monster[0]+","+monster[1]+" MR:"+monsterRadians+" Angle:"+angle+" Turn: " + (angle/Math.PI));
        return new Breve2DAction(angle / Math.PI, sign * 1);
    }

    public void reset() {
    }
}

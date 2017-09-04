package edu.southwestern.breve2D.agent;

import edu.southwestern.breve2D.Breve2DGame;
import edu.southwestern.util.CartesianGeometricUtilities;
import edu.southwestern.util.util2D.Tuple2D;

public class AttractRepelMonster implements AgentController {

	private final int index;
	private final int sign;

	/**
	 * Constructor for a new AttractRepelMonster
	 * 
	 * @param index Integer value setting the index of this specific AttractRepelMonster
	 * @param forward Boolean value for if the AttractRepelMonster should move forward; true = forward, false = backward
	 */
	public AttractRepelMonster(int index, boolean forward) {
		this.index = index;
		this.sign = forward ? 1 : -1;
	}

	/**
	 * Returns the actions each monster Agent should take based on the player's current position
	 * 
	 * @param game A specific instance of a Breve2DGame
	 * @return A Breve2DAction representing the actions that the monster Agent should take
	 */
	public Breve2DAction getAction(Breve2DGame game) {
		Tuple2D player = game.getPlayerPosition();
		Tuple2D monster = game.getMonsterPosition(index);
		double monsterRadians = game.getMonsterRadians(index);
		// if(player[1] != monster[1] || player[0] >= monster[0]) return new
		// double[2];
		double angle = CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(monster, player,
				monsterRadians);
		// System.out.println("P:"+player[0]+","+player[1]+"
		// M:"+monster[0]+","+monster[1]+" MR:"+monsterRadians+" Angle:"+angle+"
		// Turn: " + (angle/Math.PI));
		return new Breve2DAction(angle / Math.PI, sign * 1);
	}

	/**
	 * Would reset the actions for the AttractRepelMonster, but does not do anything as of now
	 */
	public void reset() {
	}
}

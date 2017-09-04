package edu.southwestern.breve2D.dynamics;

import edu.southwestern.breve2D.Breve2DGame;
import edu.southwestern.breve2D.agent.Agent;
import edu.southwestern.breve2D.agent.Breve2DAction;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.util.CartesianGeometricUtilities;

/**
 *
 * @author Jacob Schrum
 */
public final class PlayerPreyMonsterPredator extends Breve2DDynamics {

	protected double playerDamageReceived;

	public PlayerPreyMonsterPredator() {
		reset();
	}

	public void reset() {
		playerDamageReceived = 0;
	}

	@Override
	public boolean playerRespondsToMonster() {
		return true;
	}

	@Override
	public boolean sensePlayerResponseToMonster() {
		return true;
	}

	@Override
	public Breve2DAction playerInitialResponseToMonster(Agent player, Agent monster, int time) {
		double turn = CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(player.getPosition(),
				monster.getPosition(), player.getHeading());
		player.takeDamage(10);
		playerDamageReceived += 10;
		return new Breve2DAction(turn / Breve2DGame.TURN_MULTIPLIER, 0);
	}

	@Override
	public Breve2DAction playerContinuedResponseToMonster(Agent player, Agent monster, int time) {
		return new Breve2DAction(0, -2);
	}

	@Override
	public double[] fitnessScores() {
		return new double[] { playerDamageReceived };
	}

	@Override
	public void registerFitnessFunctions() {
		MMNEAT.registerFitnessFunction("Damage Dealt");
	}

	@Override
	public int numFitnessFunctions() {
		return 1;
	}

	@Override
	public int numInputSensors() {
		return 29; // 39;
	}

	@Override
	public double[] minScores() {
		return new double[] { 0 };
	}
}

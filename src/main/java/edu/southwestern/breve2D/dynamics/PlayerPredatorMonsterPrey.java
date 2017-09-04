package edu.utexas.cs.nn.breve2D.dynamics;

import edu.utexas.cs.nn.breve2D.Breve2DGame;
import edu.utexas.cs.nn.breve2D.agent.Agent;
import edu.utexas.cs.nn.breve2D.agent.Breve2DAction;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.CartesianGeometricUtilities;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class PlayerPredatorMonsterPrey extends Breve2DDynamics {

	private double[] monsterDamageReceived;
	private double[] monsterTimeAlive;
	private final int numMonsters;
	private final int timeLimit;

	public PlayerPredatorMonsterPrey() {
		this(Parameters.parameters.integerParameter("numBreve2DMonsters"),
				Parameters.parameters.integerParameter("breve2DTimeLimit"));
	}

	public PlayerPredatorMonsterPrey(int numMonsters, int timeLimit) {
		this.numMonsters = numMonsters;
		this.timeLimit = timeLimit;
		reset();
	}

	@Override
	public void reset() {
		monsterDamageReceived = new double[numMonsters];
		monsterTimeAlive = new double[numMonsters];
		Arrays.fill(monsterTimeAlive, timeLimit);
	}

	public int numInputSensors() {
		return 24; // 34;
	}

	@Override
	public boolean monsterRespondsToPlayer() {
		return true;
	}

	@Override
	public boolean senseMonsterResponseToPlayer() {
		return true;
	}

	@Override
	public Breve2DAction monsterInitialResponseToPlayer(Agent player, Agent monster, int time) {
		double turn = CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(monster.getPosition(),
				player.getPosition(), monster.getHeading());
		monster.takeDamage(10);
		monsterDamageReceived[monster.getIdentifier()] -= 10;
		if (monster.isDead()) {
			monsterTimeAlive[monster.getIdentifier()] = time;
		}
		return new Breve2DAction(turn / Breve2DGame.TURN_MULTIPLIER, 0);
	}

	@Override
	public Breve2DAction monsterContinuedResponseToPlayer(Agent player, Agent monster, int time) {
		return new Breve2DAction(0, -2);
	}

	@Override
	public double[] fitnessScores() {
		double sumDamage = 0;
		double sumTimeAlive = 0;
		for (int i = 0; i < monsterDamageReceived.length; i++) {
			sumDamage += monsterDamageReceived[i];
			sumTimeAlive += monsterTimeAlive[i];
		}
		return new double[] { sumDamage / monsterDamageReceived.length, sumTimeAlive / monsterDamageReceived.length };
	}

	@Override
	public int numFitnessFunctions() {
		return 2;
	}

	@Override
	public double[] minScores() {
		return new double[] { -Parameters.parameters.integerParameter("breve2DAgentHealth"), 0.0 };
	}

	@Override
	public void registerFitnessFunctions() {
		MMNEAT.registerFitnessFunction("Damage Received Penalty");
		MMNEAT.registerFitnessFunction("Time Alive");
	}
}

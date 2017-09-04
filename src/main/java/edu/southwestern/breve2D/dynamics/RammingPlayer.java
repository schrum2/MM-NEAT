/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.breve2D.dynamics;

import edu.southwestern.breve2D.Breve2DGame;
import edu.southwestern.breve2D.agent.Agent;
import edu.southwestern.breve2D.agent.Breve2DAction;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.CartesianGeometricUtilities;
import edu.southwestern.util.util2D.ILocated2D;
import edu.southwestern.util.util2D.Tuple2D;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public final class RammingPlayer extends Breve2DDynamics implements RammingDynamics {

	private double[] monsterDamageReceived;
	private double[] monsterTimeAlive;
	private double playerDamageReceived;
	private final int numMonsters;
	private final int timeLimit;
	private boolean breveDamageOnly;

	public RammingPlayer() {
		this(Parameters.parameters.integerParameter("numBreve2DMonsters"),
				Parameters.parameters.integerParameter("breve2DTimeLimit"));
	}

	public RammingPlayer(int numMonsters, int timeLimit) {
		this.numMonsters = numMonsters;
		this.timeLimit = timeLimit;
		this.breveDamageOnly = Parameters.parameters.booleanParameter("breveDamageOnly");
		reset();
	}

	@Override
	public void reset() {
		monsterDamageReceived = new double[numMonsters];
		monsterTimeAlive = new double[numMonsters];
		Arrays.fill(monsterTimeAlive, timeLimit);
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
	public int numInputSensors() {
		return 31;
	}

	@Override
	public double[] fitnessScores() {
		double sumDamage = 0;
		double sumTimeAlive = 0;
		for (int i = 0; i < monsterDamageReceived.length; i++) {
			sumDamage += monsterDamageReceived[i];
			sumTimeAlive += monsterTimeAlive[i];
		}
		if (breveDamageOnly) {
			return new double[] { playerDamageReceived };
		} else {
			return new double[] { playerDamageReceived, sumDamage / monsterDamageReceived.length,
					sumTimeAlive / monsterDamageReceived.length };
		}
	}

	@Override
	public int numFitnessFunctions() {
		return breveDamageOnly ? 1 : 3;
	}

	@Override
	public void registerFitnessFunctions() {
		MMNEAT.registerFitnessFunction("Damage Dealt");
		if (!breveDamageOnly) {
			MMNEAT.registerFitnessFunction("Damage Received Penalty");
			MMNEAT.registerFitnessFunction("Time Alive");
		}
	}

	@Override
	public double[] minScores() {
		if (breveDamageOnly) {
			return new double[] { 0.0 };
		} else {
			return new double[] { 0.0, -Parameters.parameters.integerParameter("breve2DAgentHealth"), 0.0 };
		}
	}

	public Tuple2D getRamOffset() {
		return new Tuple2D(Breve2DGame.AGENT_MAGNITUDE, 0);
	}

	public boolean monstersHaveRams() {
		return false;
	}

	public Breve2DAction playerInitialResponseToRam(Agent player, ILocated2D ram, int time) {
		return null;
	}

	public Breve2DAction playerContinuedResponseToRam(Agent player, ILocated2D ram, int time) {
		return null;
	}

	public boolean playerHasRam() {
		return true;
	}

	public Breve2DAction monsterInitialResponseToRam(Agent monster, ILocated2D ram, int time) {
		double turn = CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(monster.getPosition(),
				ram.getPosition(), monster.getHeading());
		monster.takeDamage(10);
		monsterDamageReceived[monster.getIdentifier()] -= 10;
		if (monster.isDead()) {
			monsterTimeAlive[monster.getIdentifier()] = time;
		}
		return new Breve2DAction(turn / Breve2DGame.TURN_MULTIPLIER, 0);
	}

	public Breve2DAction monsterContinuedResponseToRam(Agent monster, ILocated2D ram, int time) {
		return new Breve2DAction(0, -2);
	}

	public boolean senseMonstersHaveRams() {
		return false;
	}

	public boolean sensePlayerHasRam() {
		return true;
	}
}

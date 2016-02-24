package edu.utexas.cs.nn.breve2D.dynamics;

import edu.utexas.cs.nn.breve2D.Breve2DGame;
import edu.utexas.cs.nn.breve2D.agent.Agent;
import edu.utexas.cs.nn.breve2D.agent.Breve2DAction;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.util.CartesianGeometricUtilities;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class RammingMonsters extends Breve2DDynamics implements RammingDynamics {

    private double[] monsterDamageReceived;
    private double[] monsterTimeAlive;
    private double playerDamageReceived;
    private final int numMonsters;
    private final int timeLimit;
    private final boolean front;

    public RammingMonsters(boolean front) {
        this(front, Parameters.parameters.integerParameter("numBreve2DMonsters"), Parameters.parameters.integerParameter("breve2DTimeLimit"));
    }

    public RammingMonsters(boolean front, int numMonsters, int timeLimit) {
        this.front = front;
        this.numMonsters = numMonsters;
        this.timeLimit = timeLimit;
        reset();
    }

    @Override
    public void reset() {
        monsterDamageReceived = new double[numMonsters];
        monsterTimeAlive = new double[numMonsters];
        Arrays.fill(monsterTimeAlive, timeLimit);
        playerDamageReceived = 0;
    }

    public Tuple2D getRamOffset() {
        return new Tuple2D((front ? 1 : -1) * Breve2DGame.AGENT_MAGNITUDE, 0);
    }

    public int numInputSensors() {
        return 31;
    }

    public Breve2DAction playerInitialResponseToRam(Agent player, ILocated2D ram, int time) {
        double turn = CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(player.getPosition(), ram.getPosition(), player.getHeading());
        player.takeDamage(10);
        playerDamageReceived += 10;
        return new Breve2DAction(turn / Breve2DGame.TURN_MULTIPLIER, 0);
    }

    public Breve2DAction playerContinuedResponseToRam(Agent player, ILocated2D ram, int time) {
        return new Breve2DAction(0, -2);
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
        double turn = CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(monster.getPosition(), player.getPosition(), monster.getHeading());
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
        return new double[]{playerDamageReceived, sumDamage / monsterDamageReceived.length, sumTimeAlive / monsterDamageReceived.length};
    }

    @Override
    public int numFitnessFunctions() {
        return 3;
    }

    @Override
    public double[] minScores() {
        return new double[]{0.0, -Parameters.parameters.integerParameter("breve2DAgentHealth"), 0.0};
    }

    @Override
    public void registerFitnessFunctions() {
        MMNEAT.registerFitnessFunction("Ram Damage Dealt");
        MMNEAT.registerFitnessFunction("Damage Received Penalty");
        MMNEAT.registerFitnessFunction("Time Alive");
    }

    public boolean monstersHaveRams() {
        return true;
    }

    public boolean playerHasRam() {
        return false;
    }

    public Breve2DAction monsterInitialResponseToRam(Agent monster, ILocated2D ram, int time) {
        return null;
    }

    public Breve2DAction monsterContinuedResponseToRam(Agent monster, ILocated2D ram, int time) {
        return null;
    }

    public boolean senseMonstersHaveRams() {
        return true;
    }

    public boolean sensePlayerHasRam() {
        return false;
    }
}

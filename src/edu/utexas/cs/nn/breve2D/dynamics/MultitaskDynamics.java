package edu.utexas.cs.nn.breve2D.dynamics;

import edu.utexas.cs.nn.breve2D.agent.Agent;
import edu.utexas.cs.nn.breve2D.agent.Breve2DAction;

/**
 *
 * @author He_Deceives
 */
public abstract class MultitaskDynamics extends Breve2DDynamics {

    protected final Breve2DDynamics[] tasks;

    public MultitaskDynamics(Breve2DDynamics[] tasks) {
        this.tasks = tasks;
    }

    @Override
    public int numTasks() {
        return tasks.length;
    }

    @Override
    public void reset() {
        tasks[task].reset();
    }

    /*
     * Must account for all inputs across all tasks
     */
    public abstract int numInputSensors();

    /*
     * May change in multitask domains
     */
    @Override
    public boolean playerRespondsToMonster() {
        return tasks[task].playerRespondsToMonster();
    }

    /*
     * Should never change within a domain
     */
    @Override
    public boolean sensePlayerResponseToMonster() {
        boolean or = false;
        for (Breve2DDynamics dynamics : tasks) {
            or = or || dynamics.sensePlayerResponseToMonster();
        }
        return or;
    }

    @Override
    public Breve2DAction playerInitialResponseToMonster(Agent player, Agent monster, int time) {
        return tasks[task].playerInitialResponseToMonster(player, monster, time);
    }

    @Override
    public Breve2DAction playerContinuedResponseToMonster(Agent player, Agent monster, int time) {
        return tasks[task].playerContinuedResponseToMonster(player, monster, time);
    }

    /*
     * May change in multitask domains
     */
    @Override
    public boolean monsterRespondsToPlayer() {
        return tasks[task].monsterRespondsToPlayer();
    }

    /*
     * Should never change within a domain
     */
    @Override
    public boolean senseMonsterResponseToPlayer() {
        boolean or = false;
        for (Breve2DDynamics dynamics : tasks) {
            or = or || dynamics.senseMonsterResponseToPlayer();
        }
        return or;
    }

    @Override
    public Breve2DAction monsterInitialResponseToPlayer(Agent player, Agent monster, int time) {
        return tasks[task].monsterInitialResponseToPlayer(player, monster, time);
    }

    @Override
    public Breve2DAction monsterContinuedResponseToPlayer(Agent player, Agent monster, int time) {
        return tasks[task].monsterContinuedResponseToPlayer(player, monster, time);
    }

    public double[] fitnessScores() {
        return aggregateTaskArrays(false);
    }

    public void registerFitnessFunctions() {
        for (Breve2DDynamics dynamics : tasks) {
            dynamics.registerFitnessFunctions();
        }
    }

    public double[] minScores() {
        return aggregateTaskArrays(true);
    }

    public double[] aggregateTaskArrays(boolean mins) {
        double[] all = new double[numFitnessFunctions()];
        int index = 0;
        for (Breve2DDynamics dynamics : tasks) {
            double[] single = mins ? dynamics.minScores() : dynamics.fitnessScores();
            for (int i = 0; i < single.length; i++) {
                all[index++] = single[i];
            }
        }
        return all;
    }

    public int numFitnessFunctions() {
        int sum = 0;
        for (Breve2DDynamics dynamics : tasks) {
            sum += dynamics.numFitnessFunctions();
        }
        return sum;
    }
}

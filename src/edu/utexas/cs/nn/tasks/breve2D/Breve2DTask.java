package edu.utexas.cs.nn.tasks.breve2D;

import edu.utexas.cs.nn.breve2D.Breve2DExec;
import edu.utexas.cs.nn.breve2D.agent.AgentController;
import edu.utexas.cs.nn.breve2D.agent.MultitaskPlayer;
import edu.utexas.cs.nn.breve2D.dynamics.Breve2DDynamics;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.nsga2.tug.TUGTask;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 *
 * @author Jacob Schrum
 */
public class Breve2DTask<T extends Network> extends NoisyLonerTask<T> implements TUGTask, NetworkTask {

    private int numMonsters;
    private AgentController enemy;
    private Breve2DDynamics dynamics;
    private Breve2DExec exec;

    public Breve2DTask() {
        this(Parameters.parameters.booleanParameter("deterministic"));
    }

    public Breve2DTask(boolean det) {
        super();
        try {
            enemy = (AgentController) ClassCreation.createObject("breveEnemy");
            dynamics = (Breve2DDynamics) ClassCreation.createObject("breveDynamics");
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        this.numMonsters = Parameters.parameters.integerParameter("numBreve2DMonsters");
        this.dynamics.registerFitnessFunctions();
    }

    @Override
    public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
        AgentController[] monsters = new AgentController[numMonsters];
        for (int i = 0; i < monsters.length; i++) {
            monsters[i] = new NNBreve2DMonster<T>(i, individual);
        }
        exec = new Breve2DExec();
        for (int t = 0; t < dynamics.numIsolatedTasks(); t++) {
            dynamics.reset();
            for (int j = 0; j < monsters.length; j++) {
                ((NNBreve2DMonster<T>) monsters[j]).reset();
            }
            enemy.reset();
            if (CommonConstants.watch) {
                exec.runGameTimed(dynamics, enemy, monsters, true);
            } else {
                exec.runExperiment(dynamics, enemy, monsters);
            }
            // Collect score info
            dynamics.advanceTask();
            if (enemy instanceof MultitaskPlayer) {
                ((MultitaskPlayer) enemy).advanceTask();
            }
        }
        double[] oneTrialFitness = dynamics.fitnessScores();
        double[] otherStats = new double[0];
        return new Pair<double[], double[]>(oneTrialFitness, otherStats);
    }

    public int numObjectives() {
        return minScores().length;
    }

    /**
     * All zeroes, since objectives are positive
     *
     * @return
     */
    public double[] startingGoals() {
        return dynamics.minScores();
    }

    @Override
    public double[] minScores() {
        return dynamics.minScores();
    }

    public String[] sensorLabels() {
        return NNBreve2DMonster.sensorLabels(dynamics, numMonsters);
    }

    public String[] outputLabels() {
        return new String[]{"Turn", "Thrust"};
    }

    public double getTimeStamp() {
        if(exec == null || exec.game == null) {
            return 0;
        }
        return exec.game.getTime();
    }
}

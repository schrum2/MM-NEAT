package edu.utexas.cs.nn.tasks.gridTorus;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.nsga2.tug.TUGTask;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.gridTorus.TorusWorldExec;
import edu.utexas.cs.nn.gridTorus.controllers.AggressivePredatorController;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 *
 * @author Jacob Schrum
 */
public class TorusPredPreyTask<T extends Network> extends NoisyLonerTask<T> implements TUGTask, NetworkTask {

    private TorusPredPreyController brain;
    private TorusPredPreyController[] npcs;
    private TorusWorldExec exec;

    public TorusPredPreyTask() {
        super();
        int numPredators = Parameters.parameters.integerParameter("torusPredators");
        npcs = new TorusPredPreyController[numPredators];
        for(int i = 0; i < numPredators; i++) {
            npcs[i] = new AggressivePredatorController();
        }
        MMNEAT.registerFitnessFunction("Time Alive");
    }

    @Override
    public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
        NNTorusPredPreyAgent<T> agent = new NNTorusPredPreyAgent<T>(individual);
        brain = agent.getController();
        exec = new TorusWorldExec();
        TorusPredPreyGame game;
        if (CommonConstants.watch) {
            game = exec.runGameTimed(npcs, new TorusPredPreyController[]{brain}, true);
        } else {
            game = exec.runExperiment(npcs, new TorusPredPreyController[]{brain});
        }
        double[] oneTrialFitness = new double[]{game.getTime()};
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
        return minScores();
    }

    @Override
    public double[] minScores() {
        return new double[]{0};
    }

    public String[] sensorLabels() {
        return NNTorusPredPreyController.sensorLabels(npcs.length);
    }

    public String[] outputLabels() {
        return new String[]{"UP", "RIGHT", "DOWN", "LEFT"};
    }

    public double getTimeStamp() {
        return exec.game.getTime();
    }
}

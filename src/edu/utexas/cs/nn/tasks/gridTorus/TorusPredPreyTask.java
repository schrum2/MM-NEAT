package edu.utexas.cs.nn.tasks.gridTorus;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.nsga2.tug.TUGTask;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.gridTorus.TorusWorldExec;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 *
 * @author Rollinsa
 */
public abstract class TorusPredPreyTask<T extends Network> extends NoisyLonerTask<T> implements TUGTask, NetworkTask {

    public abstract TorusPredPreyController[] getPredAgents(Genotype<T> individual);

    public abstract TorusPredPreyController[] getPreyAgents(Genotype<T> individual);

    //boolean to indicate which agent is to be evolved
    private boolean preyEvolve;

    private TorusWorldExec exec;

    /**
     *
     * @param preyEvolve if true prey are being evolved; if false predators are
     * being evolved
     */
    public TorusPredPreyTask(boolean preyEvolve) {
        super();
        this.preyEvolve = preyEvolve;
    }

    @Override
    public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
        TorusPredPreyController[] predAgents = getPredAgents(individual);
        TorusPredPreyController[] preyAgents = getPreyAgents(individual);
        exec = new TorusWorldExec();
        TorusPredPreyGame game;
        if (CommonConstants.watch) {
            game = exec.runGameTimed(predAgents, preyAgents, true);
        } else {
            game = exec.runExperiment(predAgents, preyAgents);
        }
        double[] oneTrialFitness;
        //fitness for the prey
        if (preyEvolve) {
            oneTrialFitness = new double[]{game.getTime()};
        } //fitness for the predators
        else {
            oneTrialFitness = new double[]{-game.getTime()};
        }
        double[] otherStats = new double[0];
        return new Pair<double[], double[]>(oneTrialFitness, otherStats);
    }

    public int numObjectives() {
        return minScores().length;
    }

    public double[] startingGoals() {
        return minScores();
    }

    @Override
    public double[] minScores() {
        return new double[]{preyEvolve ? 0 : Parameters.parameters.integerParameter("torusTimeLimit")};
    }
    //for agent evolving

    public String[] sensorLabels() {
        //if it is the predator who will evolve, get number of prey agents
        if (!preyEvolve) {
            return NNTorusPredPreyController.sensorLabels(Parameters.parameters.integerParameter("torusPreys"), "Prey");
        } //it is the prey who is evolving, so get number of predator agents
        else {
            return NNTorusPredPreyController.sensorLabels(Parameters.parameters.integerParameter("torusPredators"), "Pred");
        }
    }
    //for evolving agent

    public String[] outputLabels() {
        //if it is the predator evolving
        if (!preyEvolve) {
            return Parameters.parameters.booleanParameter("allowDoNothingActionForPredators")
                    ? new String[]{"UP", "RIGHT", "DOWN", "LEFT", "NOTHING"}
                    : new String[]{"UP", "RIGHT", "DOWN", "LEFT"};
        }
        //the prey is evolving
        return Parameters.parameters.booleanParameter("allowDoNothingActionForPreys")
                ? new String[]{"UP", "RIGHT", "DOWN", "LEFT", "NOTHING"}
                : new String[]{"UP", "RIGHT", "DOWN", "LEFT"};
    }

    /**
     * Accesses the time stamps for the current game being executed, use for
     * evaluation purposes.
     */
    public double getTimeStamp() {
        return exec.game.getTime();
    }
}

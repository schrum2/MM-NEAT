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
        super(); //Super from TUGTask and Network task? -Gab
        int numPredators = Parameters.parameters.integerParameter("torusPredators"); //So the assigned number of predators is gathered here... -Gab
        npcs = new TorusPredPreyController[numPredators]; //...and then used here to created predators as "npcs" -Gab
        for(int i = 0; i < numPredators; i++) {
            npcs[i] = new AggressivePredatorController(); //They are all set to aggressive, for loop makes much more sense to me now -Gab
        }
        MMNEAT.registerFitnessFunction("Time Alive"); //What are other fitness functions that could be used here? -Gab
    }

    @Override
    public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
        NNTorusPredPreyAgent<T> agent = new NNTorusPredPreyAgent<T>(individual); //This is the prey agent then? -Gab
        brain = agent.getController(); //The prey utilizes a brain while the predators are npcs -Gab
        exec = new TorusWorldExec(); //Exec as in execution? -Gab
        TorusPredPreyGame game; //Just for initializing? -Gab
        if (CommonConstants.watch) { //What is CommonConstants watching for? Winning? Losing? -Gab
            game = exec.runGameTimed(npcs, new TorusPredPreyController[]{brain}, true); //What does adding true change? -Gab
        } else {
            game = exec.runExperiment(npcs, new TorusPredPreyController[]{brain});
        }
        double[] oneTrialFitness = new double[]{game.getTime()}; 
        double[] otherStats = new double[0];
        return new Pair<double[], double[]>(oneTrialFitness, otherStats); //The time taken and other stats will help to find out the actual fitness score? -Gab
    }

    public int numObjectives() { //Whose objectives? -Gab
        return minScores().length; 
    }

    /**
     * All zeroes, since objectives are positive
     *
     * @return
     */
    public double[] startingGoals() { //Again, whose goals? -Gab
        return minScores();
    }

    @Override
    public double[] minScores() { //Empty to start, who utilizes this? Both prey and predators? Are they specific to each side or each agent? -Gab
        return new double[]{0};
    }

    public String[] sensorLabels() { //What are the sensor labels used for in the experiment? -Gab
        return NNTorusPredPreyController.sensorLabels(npcs.length);
    }

    public String[] outputLabels() { //These are just for output or for the prey/predators to read too? -Gab
        return new String[]{"UP", "RIGHT", "DOWN", "LEFT"};
    }

    public double getTimeStamp() {
        return exec.game.getTime();
    }
}

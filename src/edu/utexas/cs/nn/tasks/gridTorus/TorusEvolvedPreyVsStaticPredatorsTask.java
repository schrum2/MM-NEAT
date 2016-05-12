package edu.utexas.cs.nn.tasks.gridTorus;

/**
 * Imports needed parts to initialize the task.
 */
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
 * @author Jacob Schrum, Gabby Gonzalez
 * The following class sets up tasks for learning agents and NPCs.
 */
public class TorusEvolvedPreyVsStaticPredatorsTask<T extends Network> extends NoisyLonerTask<T> implements TUGTask, NetworkTask {
	    /**
	     * Initializes the controllers and world to be used.
	     */
    private TorusPredPreyController brain;
    private TorusPredPreyController[] staticAgents;
    private TorusWorldExec exec;

    /**
     * Extends NoisyLonerTask to make a generalized task for evolving a brain with static enemies.
     * Enemies are created with the parameter being torusPredators and initially all aggressive.
     */
    public TorusEvolvedPreyVsStaticPredatorsTask() {
        super(); 
        int numPredators = Parameters.parameters.integerParameter("torusPredators"); 
        staticAgents = new TorusPredPreyController[numPredators];
        for(int i = 0; i < numPredators; i++) {
            staticAgents[i] = new AggressivePredatorController(); 
        }
        MMNEAT.registerFitnessFunction("Time Alive"); 
    }

    /**
     * Used to evaluated the fitness score of the brain agent genotype. Additionally, initializes the game to do this.
     * one prey is evolved while predators are static
     * @param genotype and num (evaluation being performed)
     * @return pair of fitness and states from evaluation
     */
    @Override
    public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
        NNTorusPredPreyAgent<T> agent = new NNTorusPredPreyAgent<T>(individual, false); 
        brain = agent.getController(); 
        exec = new TorusWorldExec(); 
        TorusPredPreyGame game; 
        if (CommonConstants.watch) { 
            game = exec.runGameTimed(staticAgents, new TorusPredPreyController[]{brain}, true); 
        } else {
            game = exec.runExperiment(staticAgents, new TorusPredPreyController[]{brain});
        }
        double[] oneTrialFitness = new double[]{game.getTime()}; 
        double[] otherStats = new double[0];
        return new Pair<double[], double[]>(oneTrialFitness, otherStats); 
    }

    /**
     * Returns the current number of objectives for the agent that initialized minScores, i.e. brain agent.
     */
    public int numObjectives() { 
        return minScores().length; 
    }

    /**
     * Goals set to zeroes, since objectives are positive
     */
    public double[] startingGoals() {
        return minScores();
    }

    /**
     * Initialize the array minScores for the brain agent.
     */
    @Override
    public double[] minScores() { 
        return new double[]{0};
    }

    /**
     * Accesses the labels for the sensors used in network visualization.
     */
    public String[] sensorLabels() { 
        return NNTorusPredPreyController.sensorLabels(staticAgents.length);
    }

    /**
     * Accesses the outputs that will be used by an agent, i.e. the movement directions.
     */
    public String[] outputLabels() { 
    	//this class only evolves the prey, so only the prey actions (whether do nothing action is enabled for prey or not)
    	//matter, and the predator do nothing action option does not
        //return new String[]{"UP", "RIGHT", "DOWN", "LEFT"};
    	return Parameters.parameters.booleanParameter("allowDoNothingActionForPreys") ?
    			new String[]{"UP", "RIGHT", "DOWN", "LEFT", "NOTHING"} :
    			new String[]{"UP", "RIGHT", "DOWN", "LEFT"};
    }

    /**
     * Accesses the time stamps for the current game being executed, use for evaluation purposes.
     */
    public double getTimeStamp() {
        return exec.game.getTime();
    }
}

package edu.utexas.cs.nn.tasks.gridTorus;

/**
 * Imports needed parts to initialize the task.
 */
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.nsga2.tug.TUGTask;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.gridTorus.TorusWorldExec;
import edu.utexas.cs.nn.gridTorus.controllers.FearfulPreyController;
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
 * @author Rollinsa
 * The following class sets up tasks for learning agents and NPCs.
 */
public class TorusEvolvedPredatorsVsStaticPreyTask<T extends Network> extends NoisyLonerTask<T> implements TUGTask, NetworkTask {
	/**
	 * Initializes the controllers and world to be used.
	 */
	private TorusPredPreyController[] staticAgents;
	private TorusWorldExec exec;

	/**
	 * Extends NoisyLonerTask to make a generalized task for evolving a brain with static enemies.
	 * Enemies are created with the parameter being torusPreys and initially all fearful.
	 */
	public TorusEvolvedPredatorsVsStaticPreyTask() {
		super(); 
		int numPrey = Parameters.parameters.integerParameter("torusPreys"); 
		staticAgents = new TorusPredPreyController[numPrey];
		for(int i = 0; i < numPrey; i++) {
			staticAgents[i] = new FearfulPreyController(); 
		}
		MMNEAT.registerFitnessFunction("Time Alive"); 
	}

	/**
	 * Used to evaluated the fitness score of the brain agent genotype. Additionally, initializes the game to do this.
	 * one or more predators are evolved while preys are static
	 * @param genotype and num (evaluation being performed)
	 * @return pair of fitness and states from evaluation
	 */
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		int numPredators = Parameters.parameters.integerParameter("torusPredators");
		TorusPredPreyController[] evolvedAgents = new TorusPredPreyController[numPredators];    	
		for(int i = 0; i < numPredators; i++){
			//true to indicate that this is a predator
			NNTorusPredPreyAgent<T> predatorAgent = new NNTorusPredPreyAgent<T>(individual, true);
			evolvedAgents[i] = predatorAgent.getController(); 
		}
		exec = new TorusWorldExec(); 
		TorusPredPreyGame game; 
		if (CommonConstants.watch) { 
			game = exec.runGameTimed(evolvedAgents, staticAgents, true); 
		} else {
			game = exec.runExperiment(evolvedAgents, staticAgents);
		}
		double[] oneTrialFitness = new double[]{-game.getTime()}; 
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
		//this class only evolves the predators, so only the predator actions (whether do nothing action is enabled for predator or not)
		//matter, and the prey do nothing action option does not
		//return new String[]{"UP", "RIGHT", "DOWN", "LEFT"};
		return Parameters.parameters.booleanParameter("allowDoNothingActionForPredators") ?
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


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
 * Defines the Breve 2D Task by creating the controllers and dynamics, evaluating the
 * genotype of the evolved agent, and defining other task specific details such as numObjectives
 * @author Jacob Schrum
 * @param <T> phenotype, which must be a network
 */
public class Breve2DTask<T extends Network> extends NoisyLonerTask<T>implements TUGTask, NetworkTask {

	private int numMonsters;
	private AgentController enemy;
	private Breve2DDynamics dynamics;
	private Breve2DExec exec;

	/**
	 * Constructs a Breve2DTask by sending the deterministic parameter to the other constructor
	 */
	public Breve2DTask() {
		this(Parameters.parameters.booleanParameter("deterministic"));
	}

	/**
	 * Constructs a Breve2DTask by calling the super class (NoisyLonerTask) constructor, 
	 * defining the enemy and the dynamics of the domain, setting the number of monsters, 
	 * and finding the fitness scores according to the dynamics given.
	 * @param det, deterministic command line parameter
	 */
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

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * 
	 * Provides an evaluation for a single genotype. 
	 * 
	 * @param individual, the genotype
	 * @param num, which evaluation is currently being performed
	 * @return a Pair of the fitness scores and the otherStats as arrays of doubles
	 */
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		AgentController[] monsters = new AgentController[numMonsters];
		for (int i = 0; i < monsters.length; i++) {
			monsters[i] = new NNBreve2DMonster<T>(i, individual);
		}
		exec = new Breve2DExec();
		for (int t = 0; t < dynamics.numIsolatedTasks(); t++) {
			dynamics.reset();
                        for (AgentController monster : monsters) {
                            ((NNBreve2DMonster<T>) monster).reset();
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

	/**
	 * gets the number of objectives
	 * @return the number of objectives as an int
	 */
        @Override
	public int numObjectives() {
		return minScores().length;
	}

	/**
	 * All zeroes, since objectives are positive
	 *
	 * @return the starting goals in an array of doubles
	 */
        @Override
	public double[] startingGoals() {
		return dynamics.minScores();
	}

	@Override
	/**
	 * gets the min scores
	 * @return the min scores as an array of doubles
	 */
	public double[] minScores() {
		return dynamics.minScores();
	}

	/**
	 * gets the sensor labels
	 * @return the sensor labels as an array of strings
	 */
        @Override
	public String[] sensorLabels() {
		return NNBreve2DMonster.sensorLabels(dynamics, numMonsters);
	}

	/**
	 * gets the output labels 
	 * @return the output labels as an array of strings
	 */
        @Override
	public String[] outputLabels() {
		return new String[] { "Turn", "Thrust" };
	}

	/**
	 * gets the current game time, returning zero if the game hasn't started yet
	 * @return the current game time (time stamp) as a double
	 */
        @Override
	public double getTimeStamp() {
		if (exec == null || exec.game == null) {
			return 0;
		}
		return exec.game.getTime();
	}
}

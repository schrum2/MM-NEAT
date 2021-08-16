package edu.southwestern.tasks.rlglue;

import java.util.ArrayList;

import org.rlcommunity.rlglue.codec.LocalGlue;
import org.rlcommunity.rlglue.codec.RLGlue;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.rlglue.featureextractors.FeatureExtractor;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.Pair;

/**
 * My task class for interfacing with RL Glue Environments. Note that all
 * RL-Glue environments need a bit of modification in order to be compatible
 * with this code.
 * 
 * @author Jacob Schrum
 *
 * @param <T>
 */
public class RLGlueTask<T extends Network> extends NoisyLonerTask<T> implements NetworkTask {

	public static RLGlueEnvironment environment;
	public static TaskSpec tso;
	public static FeatureExtractor rlGlueExtractor;

	@SuppressWarnings("rawtypes") // Needs static access, and type T isn't known yet
	public static RLGlueAgent agent;
	protected int[] rlNumSteps;
	protected double[] rlReturn;
	// cutoff
	protected int maxStepsPerEpisode;
	private ArrayList<Double> behaviorVector;

	/**
	 * Default constructor for the RLGlueTask, it calls the
	 * MMNEAT.rlGlueEnvironment that it needs as a parameter
	 */
	public RLGlueTask() {
		this(setupRLGlue());
	}

	/**
	 * Set up RL Glue task and associated agent and other fields.
	 *
	 * @param environment Generally from MMNEAT, set at commandline
	 */
	@SuppressWarnings("unchecked")
	public RLGlueTask(RLGlueEnvironment environment) {
		super();
		rlNumSteps = new int[CommonConstants.trials];
		rlReturn = new double[CommonConstants.trials];
		maxStepsPerEpisode = Parameters.parameters.integerParameter("steps");
		RLGlueTask.environment = environment;

		try {
			agent = (RLGlueAgent<T>) ClassCreation.createObject("rlGlueAgent");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("Could not launch RLGlue agent");
			System.exit(1);
		}
		// The local glue codec does not need any network connectivity
		RLGlue.setGlue(new LocalGlue(environment, agent));
	}

	public static RLGlueEnvironment setupRLGlue() {
		// RL-Glue environment, if RL-Glue is being used
		try {
			environment = (RLGlueEnvironment) ClassCreation.createObject("rlGlueEnvironment");
			if (environment != null) {
				System.out.println("Define RL-Glue Task Spec");
				tso = environment.makeTaskSpec();
				rlGlueExtractor = (FeatureExtractor) ClassCreation.createObject("rlGlueExtractor");
			}
			return environment;
		} catch (NoSuchMethodException e) {
			System.out.println("RL Glue environment or extractor not properly specified.");
			System.out.println("Environment: " + environment);
			System.out.println("Extractor: " + rlGlueExtractor);
			System.exit(1);
		}
		return null;
	}

	/**
	 * Starts the other initializing methods
	 */
	@Override
	public void prep() {
		behaviorVector = new ArrayList<Double>();
		RLGlue.RL_init();
	}

	/**
	 * Cleans the task, doesn't delete anything
	 */
	@Override
	public void cleanup() {
		RLGlue.RL_cleanup();
	}

	/**
	 * Getter for behavior vector (array list)
	 * 
	 * @return Behavior characterization
	 */
	@Override
	public ArrayList<Double> getBehaviorVector() {
		return behaviorVector;
	}

	/**
	 * Other scores: overridden by specific environments
	 * 
	 * @return default of 0
	 */
	@Override
	public int numOtherScores() {
		return 0;
	}

	/**
	 * Used for testing a genotype and is added to an agent and runs in order to
	 * test it.
	 *
	 * @return Pair of doubles arrays: fitness scores followed by "other" scores
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		agent.replaceGenotype(individual);
		System.out.print("Episode: " + num);
		RLGlue.RL_episode(maxStepsPerEpisode);
		System.out.println("\t steps: " + RLGlue.RL_num_steps());
		rlNumSteps[num] = RLGlue.RL_num_steps();
		rlReturn[num] = RLGlue.RL_return();
		behaviorVector.addAll(environment.getBehaviorVector());

		return episodeResult(num);
	}

	/**
	 * Return fitness results for single episode
	 * @param num episode/eval number
	 * @return fitness and other scores for episode
	 */
	public Pair<double[], double[]> episodeResult(int num){
		return new Pair<double[], double[]>(new double[] { rlReturn[num] }, new double[0]);
	}
	
	/**
	 * Returns the number of objectives
	 * 
	 * @return number of objectives
	 */
	@Override
	public int numObjectives(){
			return 1; // default: just the RL Return
	}

	/**
	 * Supposedly a getter for the time stamp, but returns number of steps Used
	 * by TWEANN.java
	 * 
	 * @return Supposed to be how much time has passed in episode
	 */
	@Override
	public double getTimeStamp() {
		// Need to fix this a bit
		return rlNumSteps[0];
	}

	/**
	 * Brings in the labels for features, which are the NN inputs.
	 * 
	 * @return array of sensor input labels
	 */
	@Override
	public String[] sensorLabels() {
		return rlGlueExtractor.featureLabels();
	}

	/**
	 * Creates string array of labels for network output neurons
	 * 
	 * @return String of labels
	 */
	@Override
	public String[] outputLabels() {
		int numDiscreteActions = MMNEAT.networkOutputs;
		String[] labels = new String[numDiscreteActions];
		for (int i = 0; i < numDiscreteActions; i++) {
			labels[i] = "Action " + i; // There is a distinct label for each action
		}
		return labels;
	}
}

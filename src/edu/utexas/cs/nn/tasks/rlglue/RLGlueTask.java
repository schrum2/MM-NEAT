package edu.utexas.cs.nn.tasks.rlglue;

/*
 Copyright 2007 Brian Tanner
 brian@tannerpages.com
 http://brian.tannerpages.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.tasks.rlglue.tetris.TetrisAfterStateAgent;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.rlcommunity.environments.puddleworld.PuddleWorld;
import org.rlcommunity.environments.puddleworld.PuddleWorldState;
import org.rlcommunity.environments.tetris.Tetris;
import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.NetGlue;
import org.rlcommunity.rlglue.codec.RLGlue;
import org.rlcommunity.rlglue.codec.util.AgentLoader;
import org.rlcommunity.rlglue.codec.util.EnvironmentLoader;

public class RLGlueTask<T extends Network> extends NoisyLonerTask<T>implements NetworkTask {

	/**
	 * Initialize the elements to be used here
	 */
	protected static Process rlglue = null;
	protected static AgentLoader agentLoader = null;
	protected static EnvironmentLoader environmentLoader = null;
	protected static RLGlueEnvironment environment;
	public static RLGlueAgent agent;
	protected int[] rlNumSteps;
	protected double[] rlReturn;
	// cutoff
	protected int maxStepsPerEpisode;
	private final boolean moPuddleWorld;
	// private final boolean moTetris;
	private final boolean tetrisTimeSteps;
	private final boolean tetrisBlocksOnScreen;
	private ArrayList<Double> behaviorVector;
	public static final int DEFAULT_PORT = 4096;
	public int rlGluePort;

	/**
	 * Initializer for the RLGlueTask, it called the
	 * MMNEAT.rlGlueEnvironmentthat it needs as a parameter
	 */
	public RLGlueTask() {
		this(MMNEAT.rlGlueEnvironment);
	}

	/**
	 * Initializes the RLGlueTask with MMNEAT.rlGlueEnvironment environment.
	 * Puddleworld and Tetris have special-case options for using multiple
	 * objectives that are set up here.
	 *
	 * @param environment
	 */
	public RLGlueTask(RLGlueEnvironment environment) {
		super();
		boolean puddleWorld = (MMNEAT.rlGlueEnvironment instanceof PuddleWorld);
		moPuddleWorld = puddleWorld && Parameters.parameters.booleanParameter("moPuddleWorld");
		boolean tetris = (MMNEAT.rlGlueEnvironment instanceof Tetris);
		tetrisTimeSteps = tetris && Parameters.parameters.booleanParameter("tetrisTimeSteps");
		tetrisBlocksOnScreen = tetris && Parameters.parameters.booleanParameter("tetrisBlocksOnScreen");
		rlGluePort = Parameters.parameters.integerParameter("rlGluePort");

		if (moPuddleWorld) {
			MMNEAT.registerFitnessFunction("Time Penalty");
			MMNEAT.registerFitnessFunction("Puddle Penalty");
		}
		if (tetrisTimeSteps) { // Staying alive is good
			MMNEAT.registerFitnessFunction("Time Steps");
		}
		if (tetrisBlocksOnScreen) { // On game over, more blocks left is better
			MMNEAT.registerFitnessFunction("Blocks on Screen"); 
		}
		MMNEAT.registerFitnessFunction("RL Return");
		
		// Now register the other scores for Tetris
		if(tetrisTimeSteps || tetrisBlocksOnScreen) {
			MMNEAT.registerFitnessFunction("Rows of 1", null, false);
			MMNEAT.registerFitnessFunction("Rows of 2", null, false);
			MMNEAT.registerFitnessFunction("Rows of 3", null, false);
			MMNEAT.registerFitnessFunction("Rows of 4", null, false);
		}

		rlNumSteps = new int[CommonConstants.trials];
		rlReturn = new double[CommonConstants.trials];
		maxStepsPerEpisode = Parameters.parameters.integerParameter("steps");
		RLGlueTask.environment = environment;

		/*
		 * Need to launch RL-Glue, the program that interfaces the separate
		 * components.
		 */
		if (environment != null && rlglue == null) {
			launchRLGlue();
			/*
			 * RL-Glue runs the Agent, Environment and Experiment separately so
			 * this class needs to launch the Agent and Environment as well
			 */
			try {
				agent = (RLGlueAgent) ClassCreation.createObject("rlGlueAgent");
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				System.out.println("Could not launch RLGlue agent");
				System.exit(1);
			}
			launchAgent(agent);
			launchEnvironment(environment);
			RLGlue.setGlue(new NetGlue("localhost", rlGluePort));
		}
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
		// rlglue.destroy(); // Not needed?
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
	 * Specific to PuddleWorld? Returns the number of other scores
	 * 
	 * @return Only Puddle World has an "other" score. Rest have none (0)
	 */
	@Override
	public int numOtherScores() {
		return moPuddleWorld ? 1 : (tetrisBlocksOnScreen || tetrisTimeSteps ? 4 : 0);
	}

	/**
	 * Used for testing a genotype and is added to an agent and runs in order to
	 * test it.
	 *
	 * @return Pair of doubles arrays: fitness scores followed by "other" scores
	 */
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		agent.replaceGenotype(individual);
		System.out.print("Episode: " + num);
		RLGlue.RL_episode(maxStepsPerEpisode);
		System.out.println("\t steps: " + RLGlue.RL_num_steps());
		rlNumSteps[num] = RLGlue.RL_num_steps();
		rlReturn[num] = RLGlue.RL_return();
		behaviorVector.addAll(environment.getBehaviorVector());

		// Special case for MO Puddle World
		if (moPuddleWorld) {
			Pair<double[], double[]> p = new Pair<double[], double[]>(
					new double[] { PuddleWorldState.finalStepScore, PuddleWorldState.finalPuddleScore },
					new double[] { rlReturn[num] });
			PuddleWorldState.finalStepScore = 0;
			PuddleWorldState.finalPuddleScore = 0;
			return p;
		}
		// Special case for MO Tetris
		if (tetrisBlocksOnScreen || tetrisTimeSteps) {
			Tetris game = (Tetris) environment; 
			double[] fitness = null;
			if (tetrisBlocksOnScreen) {
				TetrisAfterStateAgent<T> tasa = (TetrisAfterStateAgent<T>) agent;
				int numberOfBlocksInState;
				// Checks if the we have reached the last step allowed
				if (rlNumSteps[num] == maxStepsPerEpisode) { 
					// Sets to max to reward not losing for this long
					numberOfBlocksInState = TetrisState.worldHeight * TetrisState.worldWidth; 
					
				} else {
					numberOfBlocksInState = tasa.getNumberOfBlocksInLastState();
				}
				if (tetrisBlocksOnScreen && tetrisTimeSteps) {
					fitness = new double[] { rlNumSteps[num], numberOfBlocksInState, rlReturn[num] };
				} else if (tetrisBlocksOnScreen) {
					fitness = new double[] { rlNumSteps[num], numberOfBlocksInState, rlReturn[num] };
				}
			} else { // timeSteps only
				fitness = new double[] { rlNumSteps[num], rlReturn[num] };
			}
			Pair<double[], double[]> p = new Pair<double[], double[]>(fitness, game.getNumberOfRows());
			return p;
		}

		return new Pair<double[], double[]>(new double[] { rlReturn[num] }, new double[0]);
	}

	/**
	 * Actual launch of the RL glue program
	 */
	public void launchRLGlue() {
		System.out.println("Launch RL Glue");
		ProcessBuilder processBuilder = new ProcessBuilder("RL-Glue/rl_glue.exe"); 
		Map<String, String> env = processBuilder.environment();
		env.put("RLGLUE_HOST", "localhost");
		env.put("RLGLUE_PORT", "" + rlGluePort);
		// Runtime rt = Runtime.getRuntime();
		try {
			// Launch an executable program
			// rlglue = rt.exec("RL-Glue/rl_glue.exe"); 
			rlglue = processBuilder.start();
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("        Exception when launching rl_glue.exe!");
			System.exit(1);
		}
	}

	/**
	 * Launches the agent used in the scenario
	 *
	 * @param agent
	 *            An agent for RL Glue tasks
	 */
	public void launchAgent(final AgentInterface agent) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				agentLoader = new AgentLoader("localhost", "" + rlGluePort, agent);
				// agentLoader = new AgentLoader(agent);
				agentLoader.run();
				System.out.println("Agent done running");
			}
		}).start();
	}

	/**
	 * Launches the environment using threads
	 *
	 * @param environment
	 *            an RLGlueEnvironment environment
	 */
	public void launchEnvironment(final RLGlueEnvironment environment) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				environmentLoader = new EnvironmentLoader("localhost", "" + rlGluePort, environment);
				// environmentLoader = new EnvironmentLoader(environment);
				environmentLoader.run();
				System.out.println("Environment done running");
			}
		}).start();
	}

	/**
	 * Returns the number of objectives
	 * 
	 * @return number of objectives
	 */
	@Override
	public int numObjectives() {
		// There are special cases, but the default fitness is the total summed reward
		if (moPuddleWorld)
			return 2;
		if (tetrisTimeSteps && tetrisBlocksOnScreen)
			return 3; // Tetris: both fitnesses used
		if (tetrisTimeSteps || tetrisBlocksOnScreen)
			return 2; // Tetris: only one is used
		else
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
		return MMNEAT.rlGlueExtractor.featureLabels();
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

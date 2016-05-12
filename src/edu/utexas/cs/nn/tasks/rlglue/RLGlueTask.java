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
import edu.utexas.cs.nn.util.datastructures.Pair;
import java.io.IOException;
import java.util.ArrayList;
import org.rlcommunity.environments.puddleworld.PuddleWorld;
import org.rlcommunity.environments.puddleworld.PuddleWorldState;
import org.rlcommunity.environments.tetris.Tetris;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.RLGlue;
import org.rlcommunity.rlglue.codec.util.AgentLoader;
import org.rlcommunity.rlglue.codec.util.EnvironmentLoader;

public final class RLGlueTask<T extends Network> extends NoisyLonerTask<T> implements NetworkTask {

	/**
	 * Initialize the elements to be used here
	 */
    protected static Process rlglue = null;
    protected static AgentLoader agentLoader = null;
    protected static EnvironmentLoader environmentLoader = null;
    protected static RLGlueEnvironment environment;
    protected static RLGlueAgent agent;
    protected int[] rlNumSteps;
    protected double[] rlReturn;
    //cutoff
    protected int maxStepsPerEpisode;
    private final boolean moPuddleWorld;
    private final boolean moTetris;
    private ArrayList<Double> behaviorVector;

    /**
     * Initializer for the RLGlueTask, it called the MMNEAT.rlGlueEnvironmentthat it needs as a parameter
     */
    public RLGlueTask() {
        this(MMNEAT.rlGlueEnvironment);
    }

    /**
     * Initializes the RLGlueTask with MMNEAT.rlGlueEnvironment environment
     * Sets up both puddleworld and tetris(?)
     * Not sure what is being set up here specifically, but I'm assuming it's a way to launch the actual game
     * @param environment
     */
    public RLGlueTask(RLGlueEnvironment environment) {
        super();
        boolean puddleWorld = (MMNEAT.rlGlueEnvironment instanceof PuddleWorld);
        moPuddleWorld = puddleWorld && Parameters.parameters.booleanParameter("moPuddleWorld");
        boolean tetris = (MMNEAT.rlGlueEnvironment instanceof Tetris);
        moTetris = tetris && Parameters.parameters.booleanParameter("moTetris");

        if (moPuddleWorld) {
            MMNEAT.registerFitnessFunction("Time Penalty");
            MMNEAT.registerFitnessFunction("Puddle Penalty");
        }
        if (moTetris) {
            MMNEAT.registerFitnessFunction("Time Steps");
        }
        MMNEAT.registerFitnessFunction("RL Return");

        rlNumSteps = new int[CommonConstants.trials];
        rlReturn = new double[CommonConstants.trials];
        maxStepsPerEpisode = Parameters.parameters.integerParameter("steps");
        RLGlueTask.environment = environment;

        /*
         * Need to launch RL-Glue, the program that interfaces
         * the separate components.
         */
        if (environment != null && rlglue == null) {
            launchRLGlue();
            /*
             * RL-Glue runs the Agent, Environment and Experiment separately
             * so this class needs to launch the Agent and Environment as well
             */
            agent = new RLGlueAgent<T>(this.numObjectives());
            launchAgent(agent);
            launchEnvironment(environment);
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
        //rlglue.destroy();
    }
    
    /**
     * Getter for behavior vector (array list)
     */
    @Override
    public ArrayList<Double> getBehaviorVector() {
        return behaviorVector;
    }

    /**
     * Specific to PuddleWorld? Returns the number of other scores
     */
    @Override
    public int numOtherScores() {
        return moPuddleWorld ? 1 : 0;
    }

    /**
     * Used for testing a genotype and is added to an agent and runs in order to test it.
     * @Return Pair of doubles arrays
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

        //Special case for MO Puddle World
        if (moPuddleWorld) {
            Pair<double[], double[]> p = new Pair<double[], double[]>(new double[]{PuddleWorldState.finalStepScore, PuddleWorldState.finalPuddleScore}, new double[]{rlReturn[num]});
            PuddleWorldState.finalStepScore = 0;
            PuddleWorldState.finalPuddleScore = 0;
            return p;
        }
        // Special case for MO Tetris
        if (moTetris) {
            Pair<double[], double[]> p = new Pair<double[], double[]>(new double[]{rlNumSteps[num], rlReturn[num]}, new double[0]);
            return p;
        }

        return new Pair<double[], double[]>(new double[]{rlReturn[num]}, new double[0]);
    }

    /**
     * Actual launch of the RL glue program
     */
    public void launchRLGlue() {
        System.out.println("Launch RL Glue");
        Runtime rt = Runtime.getRuntime();
        try {
            rlglue = rt.exec("RL-Glue/rl_glue.exe");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("        Exception when launching rl_glue.exe!");
            System.exit(1);
        }
    }

    /**
     * Launches the agent used in the scenario
     * @param agent
     */
    public void launchAgent(final AgentInterface agent) {
        new Thread(
                new Runnable() {
                    public void run() {
                        agentLoader = new AgentLoader(agent);
                        agentLoader.run();
                        System.out.println("Agent done running");
                    }
                }).start();
    }

    /**
     * Launches the environment using threads
     * @param environment
     */
    public void launchEnvironment(final RLGlueEnvironment environment) {
        new Thread(
                new Runnable() {
                    public void run() {
                        environmentLoader = new EnvironmentLoader(environment);
                        environmentLoader.run();
                        System.out.println("Environment done running");
                    }
                }).start();
    }

    /**
     * Returns the number of objectives
     */
    public int numObjectives() {
        return (moPuddleWorld || moTetris) ? 2 : 1;
    }

    /**
     * Supposedly a getter for the time stamp, but returns number of steps
     * Used by TWEANN.java for gnuplot(??)
     */
    public double getTimeStamp() {
        // Need to fix this a bit
        return rlNumSteps[0];
    }

    /**
     * Brings in the labels for features (sensors?)
     */
    public String[] sensorLabels() {
        return MMNEAT.rlGlueExtractor.featureLabels();
    }

    /**
     * Creates string array of labels
     */
    public String[] outputLabels() {
    	// Schrum: Not clear why TSO was null
        int numDiscreteActions = MMNEAT.networkOutputs; //agent.TSO.getNumDiscreteActionDims();
        String[] labels = new String[numDiscreteActions];
        for (int i = 0; i < numDiscreteActions; i++) {
            labels[i] = "Action " + i;
        }
        return labels;
    }
}

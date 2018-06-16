package org.deeplearning4j.examples.rl4j;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.deeplearning4j.rl4j.learning.HistoryProcessor;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteConv;
import org.deeplearning4j.rl4j.mdp.vizdoom.DeadlyCorridor;
import org.deeplearning4j.rl4j.mdp.vizdoom.HealthGather;
import org.deeplearning4j.rl4j.mdp.vizdoom.VizDoom;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdConv;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.util.DataManager;

import vizdoom.SpecifyDLL;

/**
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 8/11/16.
 *
 * Main example for doom  DQN
 *
 * Note: those parameters do not converge to the gif
 * The gif was obtained with a higher learning rate (0.005)
 * no skip frame (skip frame = 1), on a deadly corridir configured
 * with less doomSkills (now 5, was default value before)
 * and a code that has been heavily modified.
 *
 * The example in the gif doesn't kill the ennemy, he just runs straight
 * which is "too easy"
 */
public class Doom {

	// Change into a Parameter after further development
	private static String modelSaveLocation = "doom-end.model";

    public static QLearning.QLConfiguration DOOM_QL =
            new QLearning.QLConfiguration(
                    123,      //Random seed
                    10000,    //Max step By epoch
                    8000000,  //Max step
                    1000000,  //Max size of experience replay
                    32,       //size of batches
                    10000,    //target update (hard)
                    50000,    //num step noop warmup
                    0.001,    //reward scaling
                    0.99,     //gamma
                    100.0,    //td-error clipping
                    0.1f,     //min epsilon
                    100000,   //num step for eps greedy anneal
                    true      //double-dqn
            );




    public static DQNFactoryStdConv.Configuration DOOM_NET =
            new DQNFactoryStdConv.Configuration(
                    0.00025, //learning rate
                    0.000,    //l2 regularization
                    null, null
            );

    public static HistoryProcessor.Configuration DOOM_HP =
            new HistoryProcessor.Configuration(
                    4,       //History length
                    84,      //resize width
                    84,      //resize height
                    84,      //crop width
                    84,      //crop height
                    0,       //cropping x offset
                    0,       //cropping y offset
                    4        //skip mod (one frame is picked every x
            );


    public static void main(String[] args) throws IOException {
		SpecifyDLL.specifyDLLPath(); // Added to be able to find the vizdoom.dll
        doomLearn();
        //loadDoom();
    }

    public static VizDoom getMDP(boolean render) {
    	VizDoom mdp = new DeadlyCorridor(render);
        //VizDoom mdp = new HealthGather(render);
    	return mdp;
    }
    
    public static void doomLearn() throws IOException {

        //record the training data in rl4j-data in a new folder
        DataManager manager = new DataManager(true);

        //setup the Doom environment through VizDoom
        VizDoom mdp = getMDP(false);

        //load the previous agent
        DQNPolicy<VizDoom.GameScreen> dqn = null;
        if(new File(modelSaveLocation).exists()) {
        	dqn = DQNPolicy.load(modelSaveLocation);
        }
        
        QLearningDiscreteConv<VizDoom.GameScreen> dql = dqn != null ? 
        		// Load previous DQN
        		new QLearningDiscreteConv<VizDoom.GameScreen>(mdp, dqn.getNeuralNet(), DOOM_HP, DOOM_QL, manager) :
        		// Create new DQN from configuration
        		new QLearningDiscreteConv<>(mdp, DOOM_NET, DOOM_HP, DOOM_QL, manager);
        
        //start the training
        dql.train();

        //save the model at the end
        dql.getPolicy().save(modelSaveLocation );

        //close the doom env
        mdp.close();
    }
    
    public static void loadDoom() throws IOException{
        //define the mdp from gym (name, render)
        VizDoom mdp = getMDP(true);
        //load the previous agent
        DQNPolicy<VizDoom.GameScreen> dqn = DQNPolicy.load(modelSaveLocation);
        //evaluate the agent
        mdp.reset();
        double reward = dqn.play(mdp, new HistoryProcessor(DOOM_HP));
        mdp.close();
        Logger.getAnonymousLogger().info("Reward: " + reward);
    }

}

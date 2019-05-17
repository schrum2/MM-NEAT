package org.deeplearning4j.examples.rl4j;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger; /* Logger is a class in the logging package, which 
* provides logging capabilities (that is, the ability to report and persist various
* types of info messages) to Java programs. */

/** 
* All of the following classes are imported as packages to help Doom.java with a
* multitude of tasks. For instance, VizDoom is imported along with its subclasses,
* DeadlyCorridor and HealthGather, because one of its subclasses calls for some of
* its variables to pass down to an MDP in the getMDP() method. Most of the other
* classes are necessary to initialize three particular static variables, which will
* be explained soon.
*/
import org.deeplearning4j.rl4j.learning.HistoryProcessor;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteConv;
import org.deeplearning4j.rl4j.mdp.vizdoom.DeadlyCorridor;
import org.deeplearning4j.rl4j.mdp.vizdoom.HealthGather;
import org.deeplearning4j.rl4j.mdp.vizdoom.VizDoom;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdConv;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.util.DataManager;

import vizdoom.SpecifyDLL; /* SpecifyDLL is imported so as to enable setting
* java.library.path to standard location of vizdoom.dll. */

/**
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 8/11/16.
 * Modified by nazaruka (nazaruka@southwestern.edu) on 5/16/19.
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

	/**
	 * This code is taken from Ruben Fiszel's article "Reinforcement Learning and 
	 * DQN, learning to play from pixels" as an example of running Doom with RL4J.
	 * As shown, three static variables are implemented, with the purpose of being
	 * accessed later in Doom.java. 
	 * 
	 * Fiszel makes use of a reinforcement learning algorithm known as Q-learning.
	 * We may think of DOOM_QL (the Q-learning variable) as representing how the
	 * network learns, DOOM_NET (the deep Q-network variable) as how the network
	 * is implemented, and DOOM_HP (the history processor variable) as specifics
	 * regarding how data is to be displayed.
	 */
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



	// Added comment regarding the data types of the nulled variables.
    public static DQNFactoryStdConv.Configuration DOOM_NET =
            new DQNFactoryStdConv.Configuration(
                    0.00025,   /* learning rate (note this one being less than 0.005,
					which was what Fiszel worked with to produce the GIF in his article) */
                    0.000,     //l2 regularization
                    null, null //IUpdater and TrailingListener[] variables are null
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


    /** 
	 * Pretty much all this main method does is in setting the Java path to vizdoom.dll
	 * and running the big doomLearn() helper method. Since loadDoom() is commented out,
	 * we know that Doom.java is currently set to train the network instead of running
	 * the network through the stage.
	 */ 
	public static void main(String[] args) throws IOException {
		SpecifyDLL.specifyDLLPath(); // Added to be able to find the vizdoom.dll
        doomLearn();
        //loadDoom();
    }
	
	/**
	 * An MDP, or Markov Decision Process, defines an environment and is comprised of
	 * several (usually five) characteristics like possible states, possible actions, and
	 * next states' conditional distribution P(s'|s,a).
	 *
	 * When initialized, getMDP(render) will call DeadlyCorridor(render), which will
	 * call its superclass, VizDoom. Then, render will remain the same (e.g. if
	 * getMDP(true) is called, mdp will have one boolean value of true), and actions
	 * will be an empty ArrayList.
	 */
    public static VizDoom getMDP(boolean render) {
    	VizDoom mdp = new DeadlyCorridor(render);
        //VizDoom mdp = new HealthGather(render);
    	return mdp;
    }
    
	// This method will teach the network to run through the stage.
    public static void doomLearn() throws IOException {

        //record the training data in rl4j-data in a new folder
        DataManager manager = new DataManager(true);

        //setup the Doom environment through VizDoom
        VizDoom mdp = getMDP(true); // Boolean determines visualization

        //load the previous agent
        DQNPolicy<VizDoom.GameScreen> dqn = null;
        if(new File(modelSaveLocation).exists()) {
        	dqn = DQNPolicy.load(modelSaveLocation);
        }
        
        QLearningDiscreteConv<VizDoom.GameScreen> dql = dqn != null ? // Loads only if DQN is not null.
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
    
	/** 
	 * This method will initialize the stage so as to enable the trained network to run 
	 * through it. The environment is initialized, and our most recent iteration of the
	 * network is loaded into the environment. The network plays through and receives a
	 * "reward" based on its performance in the stage.
	 */ 
    public static void loadDoom() throws IOException{
        //define the mdp from gym (name, render)
        VizDoom mdp = getMDP(true);
        //load the previous agent
        DQNPolicy<VizDoom.GameScreen> dqn = DQNPolicy.load(modelSaveLocation);
        //evaluate the agent
        mdp.reset(); // With enough available memory, game resets to start screen.
        double reward = dqn.play(mdp, new HistoryProcessor(DOOM_HP));
        mdp.close(); // Game closes.
        Logger.getAnonymousLogger().info("Reward: " + reward);
    }

}

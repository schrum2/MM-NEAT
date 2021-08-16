package edu.southwestern.experiment.rl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.nd4j.linalg.learning.config.Adam;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.rlglue.EncodableObservation;
import edu.southwestern.tasks.rlglue.RLGlueMDP;
import edu.southwestern.tasks.rlglue.RLGlueTask;

public class RL4JExperiment implements Experiment {

	// This should be a command line parameter
	private String modelSaveName = "rl-glue.model";
	
	int maxEpisodes;
	int currentEpisode;
	// Generalize the type parameters to this later
	MDP<EncodableObservation, Integer, DiscreteSpace> mdp;
	
	@Override
	public void init() {
		// Overriding the meaning of maxGens to treat it like maxIterations
		maxEpisodes = Parameters.parameters.integerParameter("maxGens");
		currentEpisode = 0;
	}

	// Should these all be command line parameters?
    @SuppressWarnings("deprecation")
	public static QLearning.QLConfiguration QL =
            new QLearning.QLConfiguration(
                    123,    //Random seed
                    200,    //Max step By epoch
                    600000, //300000, //150000, //Max step
                    600000, //300000, //150000, //Max size of experience replay
                    32,     //size of batches
                    500,    //target update (hard)
                    10,     //num step noop warmup
                    0.01,   //reward scaling
                    0.99,   //gamma
                    1.0,    //td-error clipping
                    0.1f,   //min epsilon
                    1000,   //num step for eps greedy anneal
                    true    //double DQN
            );

	// Should these all be command line parameters?
    @SuppressWarnings("deprecation")
	public static DQNFactoryStdDense.Configuration QNET =
        DQNFactoryStdDense.Configuration.builder()
            .l2(0.001)
            .updater(new Adam(0.0005))
            .numHiddenNodes(16)
            .numLayer(3)
            .build();
	
	@Override
	public void run() {
		try {
			mdp = new RLGlueMDP(RLGlueTask.environment);
			trainAndSave();
//			loadAndWatch();
		} catch (IOException e1) {
			System.out.println("Problem running MDP");
			e1.printStackTrace();
			System.exit(1);
		}				
	}
	
	/**
	 * Assumes global mdp has been initialized
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public void trainAndSave() throws IOException {
		DataManager manager = new DataManager(false); // false = do not save to rl4j-data
		//define the training
        QLearningDiscreteDense<EncodableObservation> dql; 
//        if(new File(modelSaveName).exists()) { // Load previously saved model
//            DQNPolicy<EncodableObservation> policy = DQNPolicy.load(modelSaveName);
        
        // HOW TO LOAD POLICY? getNeuralNet() has protected access
        
//        	dql = new QLearningDiscreteDense<>(mdp, policy.getNeuralNet(), QL, manager);
//        } else { // Start fresh
        	dql = new QLearningDiscreteDense<>(mdp, QNET, QL, manager);
//        }
        //train
        dql.train();
        
        //get the final policy
        DQNPolicy<EncodableObservation> pol = dql.getPolicy();
        //serialize and save (serialization showcase, but not required)
        pol.save(modelSaveName);	        
        //close the mdp 
        mdp.close();
	}
	
	/**
	 * Assumes global mdp has been initialized
	 * @throws IOException
	 */
	public void loadAndWatch() throws IOException {
        // Load
        DQNPolicy<EncodableObservation> policy = DQNPolicy.load(modelSaveName);
        //evaluate the agent
        double rewards = 0;
        for (int i = 0; i < 1000; i++) {
            mdp.reset();
            double reward = policy.play(mdp);
            rewards += reward;
            Logger.getAnonymousLogger().info("Reward: " + reward);
        }
        System.out.println("Final rewards: " + rewards);
        mdp.close();
	}

	@Override
	public boolean shouldStop() {
		return currentEpisode >= maxEpisodes;
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// Straight-forward RL-Glue domains
		MMNEAT.main(new String[] {"runNumber:0","io:false","netio:false","maxGens:10",
				"watch:true",
//				"watch:false",
				//"steps:100000000",
				// CartPole
				"task:edu.southwestern.tasks.rlglue.cartpole.CartPoleTask",
				"rlGlueEnvironment:org.rlcommunity.environments.cartpole.CartPole",
				// AcroBot
//				"task:edu.southwestern.tasks.rlglue.acrobot.AcrobotTask",
//				"rlGlueEnvironment:org.rlcommunity.environments.acrobot.Acrobot",
				// MountainCar
//				"task:edu.southwestern.tasks.rlglue.mountaincar.MountainCarTask",
//				"rlGlueEnvironment:org.rlcommunity.environments.mountaincar.MountainCar",
				"experiment:edu.southwestern.experiment.rl.RL4JExperiment"});
	}
}

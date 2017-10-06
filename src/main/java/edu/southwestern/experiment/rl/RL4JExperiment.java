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

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.networks.dl4j.DL4JNetworkWrapper;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.rlglue.EncodableObservation;
import edu.southwestern.tasks.rlglue.RLGlueMDP;

public class RL4JExperiment implements Experiment {

	int maxEpisodes;
	int currentEpisode;
	// Must be both a LonerTask and a HyperNEATTask (might lessen this restriction in the future)
	LonerTask<DL4JNetworkWrapper> task;
	//ContainerGenotype<DL4JNetworkWrapper> individual;

	// Generalize the type parameters to this later
	MDP<EncodableObservation, Integer, DiscreteSpace> mdp;
	
	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		// Overriding the meaning of maxGens to treat it like maxIterations
		maxEpisodes = Parameters.parameters.integerParameter("maxGens");
		currentEpisode = 0;
		task = (LonerTask<DL4JNetworkWrapper>) MMNEAT.task;
		// Create neural network
//		DL4JNetworkWrapper wrappedNetwork;
//		if(task instanceof HyperNEATTask) { // Assume this is always true for now
//			HyperNEATTask hnt = (HyperNEATTask) task;
//			TensorNetwork tensorNetwork = new TensorNetworkFromHyperNEATSpecification(hnt);
//			// Input/output shape also comes from HyperNEATTask
//			List<Substrate> substrates = hnt.getSubstrateInformation();
//			int[] inputShape = HyperNEATUtil.getInputShape(substrates);
//			int outputCount = HyperNEATUtil.getOutputCount(substrates);
//			// Wrap again: DL4JNetworkWrapper implements Network
//			wrappedNetwork = new DL4JNetworkWrapper(tensorNetwork, inputShape, outputCount);
//		} else {
//			// IS THIS NEEDED?
//		}
		// Put in a "genotype" so it can be accepted by tasks
		//individual = new ContainerGenotype<DL4JNetworkWrapper>(wrappedNetwork);
	}

	// Should these all be command line parameters?
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
    public static DQNFactoryStdDense.Configuration QNET =
        DQNFactoryStdDense.Configuration.builder()
            .l2(0.001)
            .learningRate(0.0005)
            .numHiddenNodes(16)
            .numLayer(3)
            .build();
	
	@Override
	public void run() {
//		while(!shouldStop()) {
//			task.evaluate(individual);
//			currentEpisode++;
//		}
		
		try {
			mdp = new RLGlueMDP(MMNEAT.rlGlueEnvironment);
//			trainAndSave();
			loadAndWatch();
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
	public void trainAndSave() throws IOException {
		DataManager manager = new DataManager(false); // false = do not save to rl4j-data
		//define the training
        QLearningDiscreteDense<EncodableObservation> dql = new QLearningDiscreteDense<>(mdp, QNET, QL, manager);
        //train
        dql.train();
        
        //get the final policy
        DQNPolicy<EncodableObservation> pol = dql.getPolicy();
        //serialize and save (serialization showcase, but not required)
        pol.save("rl-glue-cartpole");	        
        //close the mdp 
        mdp.close();
	}
	
	/**
	 * Assumes global mdp has been initialized
	 * @throws IOException
	 */
	public void loadAndWatch() throws IOException {
        // Load
        DQNPolicy<EncodableObservation> policy = DQNPolicy.load("rl-glue-cartpole");
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

		// Once Tetris works
		
//		MMNEAT.main(new String[] {"runNumber:0","io:false","netio:false","maxGens:10","watch:true",
//				"task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask",
//				"rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris",
//				"rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor",
//				"rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent",
//				"splitRawTetrisInputs:true",
//				"senseHolesDifferently:true",
//				"hyperNEAT:true", // Prevents extra bias input
//				"steps:500000",
//				"HNProcessDepth:4","HNProcessWidth:4","convolution:true",
//				"experiment:edu.southwestern.experiment.rl.RL4JExperiment"});
	}
}

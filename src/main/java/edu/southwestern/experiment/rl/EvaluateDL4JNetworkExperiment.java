package edu.southwestern.experiment.rl;

import java.io.FileNotFoundException;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.ContainerGenotype;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.dl4j.DL4JNetworkWrapper;
import edu.southwestern.networks.dl4j.TensorNetwork;
import edu.southwestern.networks.dl4j.TensorNetworkFromHyperNEATSpecification;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.LonerTask;

public class EvaluateDL4JNetworkExperiment implements Experiment {

	int maxEpisodes;
	int currentEpisode;
	// Must be both a LonerTask and a HyperNEATTask (might lessen this restriction in the future)
	LonerTask<DL4JNetworkWrapper> task;
	ContainerGenotype<DL4JNetworkWrapper> individual;
	
	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		// Overriding the meaning of maxGens to treat it like maxIterations
		maxEpisodes = Parameters.parameters.integerParameter("maxGens");
		currentEpisode = 0;
		task = (LonerTask<DL4JNetworkWrapper>) MMNEAT.task;
		// Create neural network
		DL4JNetworkWrapper wrappedNetwork;
		if(task instanceof HyperNEATTask) { // Assume this is always true for now
			HyperNEATTask hnt = (HyperNEATTask) task;
			TensorNetwork tensorNetwork = new TensorNetworkFromHyperNEATSpecification(hnt);
			// Input/output shape also comes from HyperNEATTask
			List<Substrate> substrates = hnt.getSubstrateInformation();
			int[] inputShape = HyperNEATUtil.getInputShape(substrates);
			int outputCount = HyperNEATUtil.getOutputCount(substrates);
			// Wrap again: DL4JNetworkWrapper implements Network
			wrappedNetwork = new DL4JNetworkWrapper(tensorNetwork, inputShape, outputCount);
		} else {
			// IS THIS NEEDED?
			wrappedNetwork = null; // Temporary
		}
		// Put in a "genotype" so it can be accepted by tasks
		individual = new ContainerGenotype<DL4JNetworkWrapper>(wrappedNetwork);
	}
	
	@Override
	public void run() {
		while(!shouldStop()) {
			task.evaluate(individual);
			currentEpisode++;
		}		
	}
	
	@Override
	public boolean shouldStop() {
		return currentEpisode >= maxEpisodes;
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {		
		MMNEAT.main(new String[] {"runNumber:0","randomSeed:0","io:false","netio:false","maxGens:10","watch:true",
				"task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask",
				"rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris",
				"rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor",
				"rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent",
				"splitRawTetrisInputs:true",
				"senseHolesDifferently:true",
				"hyperNEAT:true", // Prevents extra bias input
				"steps:500000",
				"trials:1000", // Lots of trials so same network keeps learning
				"linkExpressionThreshold:-0.1", // Express all links
				"heterogeneousSubstrateActivations:true", // Allow mix of activation functions
				"inputsUseID:true", // Inputs are Identity (mandatory in DL4J?)
				"rlGamma:0.99", "rlBackprop:true", "backpropLearningRate:0.01", "rlBatchSize:100", "rlEpsilonGreedy:true", "rlEpsilon:0.1",
				"ftype:"+ActivationFunctions.FTYPE_RE_LU, // Because Tetris scores are unbounded positive values
				"stride:1","receptiveFieldSize:3","zeroPadding:false","convolutionWeightSharing:true",
				"HNProcessDepth:4","HNProcessWidth:4","convolution:true",
				"experiment:edu.southwestern.experiment.rl.EvaluateDL4JNetworkExperiment"});
	}
}

package edu.southwestern.experiment.rl;

import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.ContainerGenotype;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.networks.dl4j.DL4JNetworkWrapper;
import edu.southwestern.networks.dl4j.TensorNetwork;
import edu.southwestern.networks.dl4j.TensorNetworkFromHyperNEATSpecification;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.LonerTask;

public class RL4JExperiment implements Experiment {

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
		//if(task instanceof HyperNEATTask) // Assume this is always true for now
		HyperNEATTask hnt = (HyperNEATTask) task;
		TensorNetwork tensorNetwork = new TensorNetworkFromHyperNEATSpecification(hnt);
		// Input/output shape also comes from HyperNEATTask
		List<Substrate> substrates = hnt.getSubstrateInformation();
		int [] inputShape = HyperNEATUtil.getInputShape(substrates);
		int [] outputShape = HyperNEATUtil.getOutputShape(substrates);
		// Wrap again: DL4JNetworkWrapper implements Network
		DL4JNetworkWrapper wrappedNetwork = new DL4JNetworkWrapper(tensorNetwork, inputShape, outputShape);
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

}

package edu.utexas.cs.nn.tasks.gridTorus;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
 * controller for pred/prey that uses hyperNEAT instead of regular NEAT
 * 
 * @author gillespl
 *
 */
public class HyperNEATNNTorusPredPreyController extends NNTorusPredPreyController {

	public HyperNEATNNTorusPredPreyController(Network nn, boolean isPredator) {
		super(nn, isPredator);
	}

	public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		double[] inputs = inputs();
		double[] outputs = nn.process(inputs);
		double[] modifiedOutputs = mapSubstrateOutputsToStandardOutputs(outputs);
		// Assume one output for each direction
		return isPredator ? predatorActions()[StatisticsUtilities.argmax(modifiedOutputs)] : preyActions()[StatisticsUtilities.argmax(modifiedOutputs)];
	}
	
	public double[] mapSubstrateOutputsToStandardOutputs(double[] outputs) {
		double[] modifiedOutputs;
		if(Parameters.parameters.booleanParameter("allowDoNothingActionForPredators") || Parameters.parameters.booleanParameter("allowDoNothingActionForPreys")) {
			modifiedOutputs = new double[5];
			modifiedOutputs[4] = outputs[4];
		} else modifiedOutputs =  new double[4];
		modifiedOutputs[0] = outputs[1];
		modifiedOutputs[1] = outputs[3];
		modifiedOutputs[2] = outputs[5];
		modifiedOutputs[3] = outputs[7];
		return modifiedOutputs;
	}

	public double[] inputs() {
		HyperNEATTask hnt = (HyperNEATTask) MMNEAT.task;
		double[] inputs = hnt.getSubstrateInputs(hnt.getSubstrateInformation());
		return inputs;
	}
	
	//TODO
	public static String[] sensorLabels(int numAgents, String type) {
		return null; // What to do?
	}
}

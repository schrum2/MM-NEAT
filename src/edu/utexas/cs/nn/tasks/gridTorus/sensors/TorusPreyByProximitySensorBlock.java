package edu.utexas.cs.nn.tasks.gridTorus.sensors;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.NNTorusPredPreyController;

/**
 * finds the sensor inputs for the prey by proximity. The inputs will be the X
 * and Y offsets to each prey. The inputs will be in order of closest prey to
 * most distance prey So, each input index can hold different agents at
 * different times depending upon relative distance, whereas with the index
 * sensors each index of the sensorValue array would have held the same agent
 * per each index constantly throughout the task
 * 
 * @author rollinsa
 *
 */
public class TorusPreyByProximitySensorBlock implements TorusPredPreySensorBlock {

	private int numPrey;

	public TorusPreyByProximitySensorBlock() {
		numPrey = Parameters.parameters.integerParameter("torusPreys");
	}

	@Override
	/**
	 * @return the sensor inputs for the prey by proximity. The inputs will be
	 *         the X and Y offsets to each prey from this agent. Index 0 will
	 *         hold the closest prey and the last index in the array will hold
	 *         the furthest prey (array in ascending order by distance) Distance
	 *         here is defined as the sum of the absolute value of the X and Y
	 *         offsets
	 */
	public double[] sensorValues(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		double[] preyOffsets = NNTorusPredPreyController.getAgentOffsets(me, world, prey);
		//Is the agent sensing himself? If so, take that into account
		boolean self = false;
		if(preyOffsets.length == numPrey * 2 - 2){
			self = true;
		}

		double[] proximityPrey = new double[self ? (2 * numPrey - 2) : (2 * numPrey)];
		
		// holds sum of absolute values of X and Y offsets and the first index
		// of the original offsets
		double[][] overallDists = new double[self ? (numPrey - 1) : numPrey][2];
		// finds the sum of each absolute value X and Y offset pair and stores
		// them
		for (int i = 0; i < (self ? (numPrey - 1) : numPrey); i++) {
			overallDists[i][0] = Math.abs(preyOffsets[2 * i]) + Math.abs(preyOffsets[2 * i + 1]);
			overallDists[i][1] = 2 * i;
		}
		// sort overallDists array so that the min index is the min distanced
		// agent (ascending order by distance)
		for (int i = 0; i < (self ? (numPrey - 1) : numPrey); i++) {
			int indexOfMin = 0;
			double minValue = Double.POSITIVE_INFINITY;
			// find the next lowest summation value in the double array
			for (int j = 0; j < (self ? (numPrey - 1) : numPrey); j++) {
				if (overallDists[j][0] < minValue) {
					indexOfMin = j;
					minValue = overallDists[j][0];
				}
			}
			proximityPrey[2 * i] = preyOffsets[(int) overallDists[indexOfMin][1]];
			proximityPrey[2 * i + 1] = preyOffsets[((int) overallDists[indexOfMin][1]) + 1];
			// this is done so that the agent just put into the array by
			// proximity will not be put in again
			overallDists[indexOfMin][0] = Double.POSITIVE_INFINITY;
		}
		
		//cut off array proximityPreds so that it only senses the closest specified number of agents
		if(me.getAgentType()==0 && !Parameters.parameters.booleanParameter("predsSenseAllPrey")){
			double[] adjustedOffsets = new double[Parameters.parameters.integerParameter("numberPreySensedByPreds") * 2];
			System.arraycopy(proximityPrey, 0, adjustedOffsets, 0, Parameters.parameters.integerParameter("numberPreySensedByPreds") * 2);
			return adjustedOffsets;
		}
		if(me.getAgentType()==1 && !Parameters.parameters.booleanParameter("preySenseAllPrey")){
			double[] adjustedOffsets = new double[Parameters.parameters.integerParameter("numberPreySensedByPrey") * 2];
			System.arraycopy(proximityPrey, 0, adjustedOffsets, 0, Parameters.parameters.integerParameter("numberPreySensedByPrey") * 2);
			return adjustedOffsets;
		}
		
		return proximityPrey;
	}

	@Override
	/**
	 * @return the total number of sensors for the prey (X and Y offsets to each
	 *         prey)
	 */
	public int numSensors(boolean isPredator) {
		return isPredator ? (numPrey * 2) : (numPrey * 2 - 2);
	}

	@Override
	/**
	 * Finds the sensor labels for this agent and returns them in an array of
	 * Strings The sensor labels for this class will be the list of prey being
	 * sensed by proximity
	 * 
	 * @return the sensorLabels for the prey by proximity
	 */
	public String[] sensorLabels(boolean isPredator) {
		return isPredator ? NNTorusPredPreyController.sensorLabels(numPrey, "Closest Prey") : 
			NNTorusPredPreyController.sensorLabels(numPrey-1, "Closest Prey");
	}

}

package edu.utexas.cs.nn.tasks.gridTorus.sensors;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.NNTorusPredPreyController;

public class TorusPreyByProximitySensorBlock implements TorusPredPreySensorBlock {

	private int numPrey;
	
	public TorusPreyByProximitySensorBlock() {
		numPrey = Parameters.parameters.integerParameter("torusPreys");
	}
	
	@Override
	public double[] sensorValues(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		double[] proximityPrey = new double[2*numPrey];
		double[] preyOffsets =  NNTorusPredPreyController.getPreyOffsets(me, world, prey);

		//holds sum of absolute values of X and Y offsets and the first index of the original offsets
		double[][] overallDists = new double[numPrey][2];
		//finds the sum of each absolute value X and Y offset pair and stores them
		for(int i = 0; i < numPrey; i++){
			overallDists[i][0] = Math.abs(preyOffsets[2*i]) + Math.abs(preyOffsets[2*i+1]);
			overallDists[i][1] = 2*i;
		}
		//sort overallDists array so that the min index is the min distanced agent (ascending order by distance)
		for(int i = 0; i < numPrey; i++){
			int indexOfMin = 0;
			double minValue = Double.POSITIVE_INFINITY;
			//find the next lowest summation value in the double array 
			for(int j = 0; j < numPrey; j++){
				if(overallDists[j][0] < minValue){
					indexOfMin = j;
					minValue = overallDists[j][0];
				}
			}
			proximityPrey[2*i] = preyOffsets[(int) overallDists[indexOfMin][1]];
			proximityPrey[2*i+1] = preyOffsets[((int) overallDists[indexOfMin][1]) + 1];
			//this is done so that the agent just put into the array by proximity will not be put in again
			overallDists[indexOfMin][0] = Double.POSITIVE_INFINITY;
		}
		return proximityPrey;
	}

	@Override
	public int numSensors() {
		return numPrey * 2;
	}

	@Override
	public String[] sensorLabels() {
		return NNTorusPredPreyController.sensorLabels(numPrey, "Closest Prey");
	}

}

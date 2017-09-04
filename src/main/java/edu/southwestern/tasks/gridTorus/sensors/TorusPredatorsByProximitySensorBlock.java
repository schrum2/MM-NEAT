package edu.southwestern.tasks.gridTorus.sensors;

import java.util.Arrays;
import java.util.Comparator;

import edu.southwestern.gridTorus.TorusAgent;
import edu.southwestern.gridTorus.TorusPredPreyGame;
import edu.southwestern.gridTorus.TorusWorld;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gridTorus.NNTorusPredPreyController;
import edu.southwestern.util.datastructures.Pair;

/**
 * finds the sensor inputs for the predators by proximity. The inputs will be
 * the X and Y offsets to each predator. The inputs will be in order of closest
 * predators to most distance predators So, each input index can hold different
 * agents at different times depending upon relative distance, whereas with the
 * index sensors each index of the sensorValue array would have held the same
 * agent per each index constantly throughout the task
 * 
 * @author rollinsa
 *
 */
public class TorusPredatorsByProximitySensorBlock implements TorusPredPreySensorBlock {

	private int numPredators;

	public TorusPredatorsByProximitySensorBlock() {
		numPredators = Parameters.parameters.integerParameter("torusPredators");
	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * @return the sensor inputs for the predators by proximity. The inputs will
	 *         be the X and Y offsets to each predator from this agent. Index 0
	 *         will hold the closest predator and the last index in the array
	 *         will hold the furthest predator (array in ascending order by
	 *         distance) Distance here is defined as the sum of the absolute
	 *         value of the X and Y offsets
	 */
	public double[] sensorValues(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		double[] predOffsets = NNTorusPredPreyController.getAgentOffsets(me, world, preds);
		//Is the agent sensing himself? If so, take that into account
		boolean self = false;
		if(predOffsets.length == numPredators * 2 - 2){
			self = true;
		}
		
		// Make individual x/y offsets into an array of Pairs of (x,y) offsets
		Pair<Double,Double>[] pairs = new Pair[predOffsets.length/2];
		for (int i = 0; i < pairs.length; i++) {
			pairs[i] = new Pair<Double,Double>(predOffsets[2*i], predOffsets[2*i + 1]);
		}
		// Sort offset pairs by Manhattan distance
		Arrays.sort(pairs, new Comparator<Pair<Double,Double>>(){
			public int compare(Pair<Double,Double> o1, Pair<Double,Double> o2){
				return (int) Math.signum((Math.abs(o1.t1) + Math.abs(o1.t2)) - (Math.abs(o2.t1) + Math.abs(o2.t2)));
			}
		});
		// Transfer sorted pair data back into array of separate x and y offsets
		double[] proximityPreds = new double[self ? (2 * numPredators - 2) : (2 * numPredators)];
		for (int i = 0; i < pairs.length; i++) {
			proximityPreds[2 * i] = pairs[i].t1;
			proximityPreds[2 * i + 1] = pairs[i].t2;
		}
		
		//cut off array proximityPreds so that it only senses the closest specified number of agents
		if(me.getAgentType()==TorusPredPreyGame.AGENT_TYPE_PRED && !Parameters.parameters.booleanParameter("predsSenseAllPreds")){
			double[] adjustedOffsets = new double[Parameters.parameters.integerParameter("numberPredsSensedByPreds") * 2];
			System.arraycopy(proximityPreds, 0, adjustedOffsets, 0, Parameters.parameters.integerParameter("numberPredsSensedByPreds") * 2);
			return adjustedOffsets;
		}
		if(me.getAgentType()==TorusPredPreyGame.AGENT_TYPE_PREY && !Parameters.parameters.booleanParameter("preySenseAllPreds")){
			double[] adjustedOffsets = new double[Parameters.parameters.integerParameter("numberPredsSensedByPrey") * 2];
			System.arraycopy(proximityPreds, 0, adjustedOffsets, 0, Parameters.parameters.integerParameter("numberPredsSensedByPrey") * 2);
			return adjustedOffsets;
		}
		
		return proximityPreds;
	}

	@Override
	/**
	 * @return the total number of sensors for the predators (X and Y offsets to
	 *         each pred)
	 */
	public int numSensors(boolean isPredator) {
		
		if(isPredator){
			if(Parameters.parameters.booleanParameter("predsSenseAllPrey")){
				return numPredators * 2 - 2;
			}else{
				return Parameters.parameters.integerParameter("numberPredsSensedByPreds") * 2;
			}
		}else{
			if(Parameters.parameters.booleanParameter("preySenseAllPrey")){
				return numPredators * 2;
			}else{
				return Parameters.parameters.integerParameter("numberPredsSensedByPrey") * 2;
			}
		}
	}

	@Override
	/**
	 * Finds the sensor labels for this agent and returns them in an array of
	 * Strings The sensor labels for this class will be the list of Predators
	 * being sensed by proximity
	 * 
	 * @return the sensorLabels for the predators by proximity
	 */
	public String[] sensorLabels(boolean isPredator) {	
		if(isPredator){
			if(Parameters.parameters.booleanParameter("predsSenseAllPrey")){
				return NNTorusPredPreyController.sensorLabels(numPredators-1, "Closest Pred");
			}else{
				return NNTorusPredPreyController.sensorLabels(Parameters.parameters.integerParameter("numberPredsSensedByPreds"), "Closest Pred");
			}
		}else{
			if(Parameters.parameters.booleanParameter("preySenseAllPrey")){
				return NNTorusPredPreyController.sensorLabels(numPredators, "Closest Pred");
			}else{
				return NNTorusPredPreyController.sensorLabels(Parameters.parameters.integerParameter("numberPredsSensedByPrey"), "Closest Pred");
			}
		}
	}

}

package edu.southwestern.tasks.rlglue;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.util.ArrayUtil;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * RL4J requires that its observations implement Encodable in order to
 * train networks, so this clas simply wraps the RL Glue Observation in
 * the Encodable interface by providing the toArray method.
 * @author Jacob Schrum
 */
public class EncodableObservation implements Encodable {

	private Observation o;

	public EncodableObservation(Observation o) {
		this.o = o;
	}
	
	@Override
	public double[] toArray() {
		double[] intValues = ArrayUtil.toDouble(o.intArray);
		List<double[]> arrays = new ArrayList<>(2);
		arrays.add(intValues);
		arrays.add(o.doubleArray);
		return ArrayUtil.combineDouble(arrays);
	}
	
}

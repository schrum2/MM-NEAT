package edu.southwestern.evolution.nsga2.bd.vectors;

import java.util.BitSet;

/**
 * Compare two binary behavior characterizations in terms of Hamming Distance
 * @author Jacob Schrum
 */
public class BitBehaviorVector implements BehaviorVector {

	private final BitSet bs;

	public BitBehaviorVector(BitSet bs) {
		this.bs = bs;
	}

	/**
	 * Compute Hamming distance by computing XOR between the vectors and counting the 1's in the result
	 */
	public double distance(BehaviorVector rhs) {
		// Clone so the xor operation doesn't change the original set
		BitSet other = (BitSet) ((BitBehaviorVector) rhs).bs.clone();
		other.xor(bs);
		return other.cardinality();
	}
}

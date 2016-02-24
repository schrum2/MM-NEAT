package edu.utexas.cs.nn.evolution.nsga2.bd.vectors;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.BitSet;

/**
 *
 * @author Jacob Schrum
 */
public class BitBehaviorVector implements BehaviorVector {

    private final BitSet bs;

    public BitBehaviorVector(BitSet bs) {
        this.bs = bs;
    }

    /*
     * Hamming distance
     */
    public double distance(BehaviorVector rhs) {
        // Clone so the xor operation doesn't change the original set
        BitSet other = (BitSet) ((BitBehaviorVector) rhs).bs.clone();
        other.xor(bs);
        return other.cardinality();
    }
}

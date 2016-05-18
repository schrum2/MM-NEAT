package edu.utexas.cs.nn.tasks.rlglue.tetris;

import java.util.List;

import org.rlcommunity.environments.tetris.TetrisState;

import edu.utexas.cs.nn.util.datastructures.Pair;

public class TetrisStateActionPair extends Pair<TetrisState, List<Integer>> {

	/**
	 * Initializes the StateAction pair
	 * @param pos1
	 * @param pos2
	 */
	public TetrisStateActionPair(TetrisState pos1, List<Integer> pos2) {
		super(pos1, pos2);
	}
	
	/**
	 * Override for the equals method, made in such a way to ignore the second half of each pair in comparisons
	 */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Pair p = (Pair) other;
            return t1.equals(p.t1);
        }
        return false;
    }
    
    /**
     * Auto-generated hash code for Pairs, just copied over to StateActionPairs
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.t1 != null ? this.t1.hashCode() : 0);
        hash = 41 * hash + (this.t2 != null ? this.t2.hashCode() : 0);
        return hash;
    }

}

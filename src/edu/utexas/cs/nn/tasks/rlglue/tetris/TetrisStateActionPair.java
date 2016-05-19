package edu.utexas.cs.nn.tasks.rlglue.tetris;

import java.util.List;

import org.rlcommunity.environments.tetris.TetrisState;

import edu.utexas.cs.nn.util.datastructures.Pair;

public class TetrisStateActionPair extends Pair<TetrisState, List<Integer>> {

    /**
     * Initializes the StateAction pair
     *
     * @param pos1 Tetris state that sequence of actions results in
     * @param pos2 List of actions that results in Tetris state
     */
    public TetrisStateActionPair(TetrisState pos1, List<Integer> pos2) {
        super(pos1, pos2);
    }

    /**
     * Override for the equals method, made in such a way to ignore the second
     * half of each pair in comparisons
     * @return Other Object that should be a TetrisStateActionPair
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof TetrisStateActionPair) {
            TetrisStateActionPair p = (TetrisStateActionPair) other;
            return t1.equals(p.t1); // Only care if Tetris states are the same
        }
        return false;
    }

    /**
     * We only care about the end state and not the action sequence
     * @return Hash code that only depends on Tetris state
     */
    @Override
    public int hashCode() {
        return t1.hashCode();
    }

}

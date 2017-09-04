package edu.southwestern.tasks.rlglue.tetris;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.rlcommunity.environments.tetris.TetrisState;

import edu.southwestern.util.datastructures.Pair;

public class TetrisStateActionPair extends Pair<TetrisState, List<Integer>> {

	/**
	 * Initializes the StateAction pair
	 *
	 * @param pos1 Tetris end state
	 * @param pos2 List of actions to get there
	 */
	public TetrisStateActionPair(TetrisState pos1, List<Integer> pos2) {
		super(pos1, pos2);
	}

	/**
	 * Creates a new StateAction pair given there's one to copy
	 * @param toCopy
	 */
	public TetrisStateActionPair(TetrisStateActionPair toCopy) {
		this(new TetrisState(toCopy.t1), new LinkedList<Integer>(toCopy.t2));
	}

	/**
	 * Creates a new StateAction pair given a TetrisState
	 * @param startState
	 */
	public TetrisStateActionPair(TetrisState startState) {
		this(startState, new LinkedList<Integer>());
	}

	/**
	 * Override for the equals method, made in such a way to ignore the second
	 * half of each pair in comparisons
	 *
	 * @param obj
	 *            Object that should be a TetrisStateActionPair
	 * @return whether obj and this object contain the same TetrisState
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TetrisState other = ((TetrisStateActionPair) obj).t1;
		return Arrays.equals(t1.worldState, other.worldState);
	}

	/**
	 * We only care about the end state and not the action sequence
	 *
	 * @return Hash code that only depends on Tetris state
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(t1.worldState);
		return result;
	}

}

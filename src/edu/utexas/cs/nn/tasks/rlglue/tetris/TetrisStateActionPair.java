package edu.utexas.cs.nn.tasks.rlglue.tetris;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.rlcommunity.environments.tetris.TetrisState;

import edu.utexas.cs.nn.util.MiscUtil;
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

    public TetrisStateActionPair(TetrisStateActionPair toCopy) {
    	this(new TetrisState(toCopy.t1), new LinkedList<Integer>(toCopy.t2));
    }
    
    public TetrisStateActionPair(TetrisState startState) {
    	this(startState,  new LinkedList<Integer>());
    }
    
    /**
     * Override for the equals method, made in such a way to ignore the second
     * half of each pair in comparisons
     * @return Other Object that should be a TetrisStateActionPair
     */
    @Override
	public boolean equals(Object obj) {
    	if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TetrisState other = ((TetrisStateActionPair) obj).t1;
//		for(int i = 0; i < t1.worldState.length; i++) {
//			if(t1.worldState[i] != other.worldState[i]) {
//				System.out.println("bad! " + i);
//				System.out.println(t1);
//				MiscUtil.waitForReadStringAndEnterKeyPress();
//				return false;
//			}
//		}
		//System.out.println(t1.worldState.length + ":" + other.worldState.length);
		if (!Arrays.equals(t1.worldState, other.worldState)) {
//			System.out.println("Not equal, right?");
			return false;
		}
		return true;
	}

    /**
     * We only care about the end state and not the action sequence
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

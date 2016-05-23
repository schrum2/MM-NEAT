/**
 * Class to help evaluate the after-states in the Tetris project.
 *
 * @author Jacob Schrum, Gabby Gonzalez
 */
package edu.utexas.cs.nn.tasks.rlglue.tetris;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.rlcommunity.environments.tetris.TetrisState;

public class TetrisAfterStateGenerator {

    /**
     * Takes in the current Tetris state and finds each of the possible after
     * states, as well as the actions to get to that state. The after states
     * will be states for each possible orientation of the falling piece as they
     * hit the bottom from each placement across the width.
     *
     * @param ts Start state
     * @return set of pairs (evaluated after-states and list of actions)
     */
    public static HashSet<TetrisStateActionPair> generateAfterStates(TetrisState ts) {
        int[] actOrder = {TetrisState.NONE, TetrisState.FALL, TetrisState.LEFT, TetrisState.RIGHT, TetrisState.CW, TetrisState.CCW}; // Replace with actual action constants (magic numbers!)
        HashSet<TetrisStateActionPair> evaluated = new HashSet<TetrisStateActionPair>(); //only ever as big as # of orientations times the width
        HashSet<TetrisState> seen = new HashSet<TetrisState>();
        Queue<TetrisStateActionPair> fringe = new LinkedList<TetrisStateActionPair>();
        fringe.add(new TetrisStateActionPair(ts));
        seen.add(new TetrisState(ts));

        while (!fringe.isEmpty()) {
            TetrisStateActionPair pair = fringe.poll();
            for (int i = 0; i < actOrder.length; i++) {
                int action = actOrder[i];
                TetrisStateActionPair copy = new TetrisStateActionPair(pair);
                if (copy.t1.take_action(action)) {
                    copy.t2.add(action); // add action to list
                    copy.t1.update(); // result of action in the given state
                    if (!copy.t1.blockMobile) {
                        evaluated.add(copy);
                    } else if (!seen.contains(copy.t1)) {
                        fringe.add(copy);
                        seen.add(copy.t1);
                    }
                }
            }
        }

        return evaluated;
    }
}

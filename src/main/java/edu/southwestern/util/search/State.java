package edu.southwestern.util.search;

import java.util.ArrayList;

import edu.southwestern.util.datastructures.Triple;

public abstract class State<T extends Action> {

	/**
	 * What State results from performing a particular Action within that state?
	 * Assumes deterministic outcomes
	 * @param a An Action
	 * @return Resulting State
	 */
	public abstract State<T> getSuccessor(T a);
	
	/**
	 * Return all Actions that can be performed in State s.
	 * @param s A State
	 * @return List of the Actions that can be performed
	 */
	public abstract ArrayList<T> getLegalActions(State<T> s);
	
	/**
	 * Return a list of all States that can be reached from this state, combined with the associated action
	 * and step cost to get to the State.
	 * @param s A State
	 * @return Neighboring states of State s (along with Action and cost)
	 */
	public ArrayList<Triple<State<T>,T,Double>> getSuccessors() {
		ArrayList<Triple<State<T>,T,Double>> successors = new ArrayList<>();
		for(T a : getLegalActions(this)) {
			State<T> successor = getSuccessor(a);
			successors.add(new Triple<State<T>,T,Double>(successor,a,stepCost(successor,a)));
		}
		return successors;
	}
	
	/**
	 * Test for determining if the current state is a goal state.
	 * @return True if this state is a goal state
	 */
	public abstract boolean isGoal();
	
	/**
	 * The cost of performing Action a in State s
	 * @param s State
	 * @param a Action to perform
	 * @return Step cost of that Action
 	 */
	public abstract double stepCost(State<T> s, T a);
}

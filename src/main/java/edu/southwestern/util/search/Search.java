package edu.southwestern.util.search;

import java.util.ArrayList;

public interface Search<A extends Action, S extends State<A>> { 
	/**
	 * Given a start State, return a list of actions that reaches a goal state
	 * @param start Start State
	 * @return List of Actions that an agent can perform to reach a goal from the start
	 */
	public ArrayList<A> search(S start);
}

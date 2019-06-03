package edu.southwestern.util.search;

public interface Heuristic<A extends Action, S extends State<A>> {
	/**
	 * Returns the heuristic value of State s
	 * @param s A State
	 * @return The heuristic estimate of the cost to reach the goal from s
	 */
	public double h(S s);
}

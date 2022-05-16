package edu.southwestern.util.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Triple;

public abstract class GraphSearch<A extends Action, S extends State<A>> implements Search<A,S> {

	private HashSet<S> visited;

	@Override
	public ArrayList<A> search(S start) {
		return search(start, true, Integer.MAX_VALUE); // Reset search by default
	}
	
	/**
	 * Searches through the level to find the solution. Whether or not it stops at the solution 
	 * can be toggled as a command line parameter
	 * All graph search algorithms have the same general structure. They only difference in
	 * the data structure used to maintain the fringe. This comes from getQueueStrategy().
	 * @param start Start point
	 * @param reset Boolean on whether or not to reset
	 * @param budget The number of iterations allowed before an exception is thrown
	 * @return null if no solution is found, or the ArrayList<A> for the solution if found
	 */
	public ArrayList<A> search(S start, boolean reset, int budget) {
		Queue<Triple<S, ArrayList<A>, Double>> pq = getQueueStrategy();
		// No actions or cost to reach starting point
		pq.add(new Triple<S,ArrayList<A>,Double>(start, new ArrayList<A>(), new Double(0)));
		if(reset) visited = new HashSet<>();
		int count = 0;
		boolean found = false;
		ArrayList<A> solution=null;
		//While loop runs until there are no more reachable spaces, or, if specified, until the orb is found.
		//If searchContinuesAfterSuccess is true, then it will find all reachable places
		while(!pq.isEmpty() && (!found || Parameters.parameters.booleanParameter("searchContinuesAfterSuccess"))) {
			// Each state includes the path that led to it, and cost of that path
			Triple<S,ArrayList<A>,Double> current = pq.poll();
			S s = current.t1;
			ArrayList<A> actions = current.t2;
			double cost = current.t3;
			
			//If the orb is found, sets found to true to ensure only the first path is returned
			if(s.isGoal() && !found) {
				solution = actions;
				found=true;
			} else if(!visited.contains(s)) {
				// Ensures that search doesn't take too long
				count++;
				if(count > budget) {
					System.out.println("A* taking too long.");
					throw new IllegalStateException("A* exceeded computation budget");
				}
			    visited.add(s);
			    ArrayList<Triple<State<A>, A, Double>> successors = s.getSuccessors();
			    for(Triple<State<A>,A,Double> triple : successors) {
			    	@SuppressWarnings("unchecked")
					S nextState = (S) triple.t1;
			    	assert nextState != null : "State is null! Parent was " + s + ", and action was " + triple.t2;
			    	ArrayList<A> actionsSoFar = new ArrayList<A>();
			    	actionsSoFar.addAll(actions); // Previous actions
			    	actionsSoFar.add(triple.t2); // Next step to new action
			    	double costSoFar = cost + triple.t3;
			    	pq.add(new Triple<S,ArrayList<A>,Double>(nextState, actionsSoFar, costSoFar));
			    }
			}
		}
		// TODO: Even when we fail, we might want to return some indication of the amount of
		//       work done. This could be relevant for a fitness function, since we might want
		//       to know how close we were to succeeding, or how much we explored. Fix later.
		return solution;
	}

	/**
	 * Return an empty data structure that manages the fringe of the graph during
	 * the search process.
	 * @return
	 */
	public abstract Queue<Triple<S, ArrayList<A>, Double>> getQueueStrategy();
	
	public HashSet<S> getVisited(){
		return this.visited;
	}

	
	
}

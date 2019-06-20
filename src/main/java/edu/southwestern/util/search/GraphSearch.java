package edu.southwestern.util.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;

import edu.southwestern.util.datastructures.Triple;

public abstract class GraphSearch<A extends Action, S extends State<A>> implements Search<A,S> {

	private HashSet<S> visited;

	@Override
	public ArrayList<A> search(S start) {
		return search(start, true); // Reset search by default
	}
	
	/**
	 * All graph search algorithms have the same general structure. They only differ in
	 * the data structure used to maintain the fringe. This comes from getQueueStrategy().
	 */
	public ArrayList<A> search(S start, boolean reset) {
		Queue<Triple<S, ArrayList<A>, Double>> pq = getQueueStrategy();
		// No actions or cost to reach starting point
		pq.add(new Triple<S,ArrayList<A>,Double>(start, new ArrayList<A>(), new Double(0)));
		if(reset) visited = new HashSet<>();
		while(!pq.isEmpty()) {
			// Each state includes the path that led to it, and cost of that path
			Triple<S,ArrayList<A>,Double> current = pq.poll();
			S s = current.t1;
			ArrayList<A> actions = current.t2;
			double cost = current.t3;
			if(s.isGoal()) {
				return actions; // SUCCESS!
			} else if(!visited.contains(s)) {
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
		// Failure!
		// TODO: Even when we fail, we might want to return some indication of the amount of
		//       work done. This could be relevant for a fitness function, since we might want
		//       to know how close we were to succeeding, or how much we explored. Fix later.
		return null;
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

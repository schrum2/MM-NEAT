package edu.southwestern.util.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

import edu.southwestern.util.datastructures.Triple;

public class AStarSearch<A extends Action, S extends State<A>> implements Search<A,S> {

	private Heuristic<A,S> h;

	public AStarSearch(Heuristic<A,S> h) {
		this.h = h;
	}
	
	@Override
	public ArrayList<A> search(S start) {
		PriorityQueue<Triple<S,ArrayList<A>,Double>> pq = new PriorityQueue<>(50, new Comparator<Triple<S,ArrayList<A>,Double>>() {
			/**
			 * A* compares states based on a combination of cost and heuristic estimate.
			 */
			@Override
			public int compare(Triple<S, ArrayList<A>, Double> o1,
							   Triple<S, ArrayList<A>, Double> o2) {
				// g(n) + h(n)
				double value1 = o1.t3 + h.h(o1.t1);
				double value2 = o2.t3 + h.h(o2.t1);
				return (int) Math.signum(value1 - value2);
			}
			
		});
		// No actions or cost to reach starting point
		pq.add(new Triple<S,ArrayList<A>,Double>(start, new ArrayList<A>(), new Double(0)));
		HashSet<S> visited = new HashSet<>();
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

	
}

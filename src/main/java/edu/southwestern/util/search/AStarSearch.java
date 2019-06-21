package edu.southwestern.util.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import edu.southwestern.util.datastructures.Triple;

public class AStarSearch<A extends Action, S extends State<A>> extends GraphSearch<A,S> {

	private Heuristic<A,S> h;

	/**
	 * Initialize A* search by providing a heuristic function.
	 * @param h Class containing the heuristic function
	 */
	public AStarSearch(Heuristic<A,S> h) {
		this.h = h;
	}

	/**
	 * Return an empty data structure that will manage the fringe of the graph search. In the case
	 * of A* search that data structure is a priority queue where the priority is defined to be
	 * the sum of the actual cost so far and the heuristic value from the given state.
	 * @return
	 */
	public Queue<Triple<S, ArrayList<A>, Double>> getQueueStrategy() {
		Queue<Triple<S,ArrayList<A>,Double>> pq = new PriorityQueue<>(50, new Comparator<Triple<S,ArrayList<A>,Double>>() {
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
		return pq;
	}

	
}

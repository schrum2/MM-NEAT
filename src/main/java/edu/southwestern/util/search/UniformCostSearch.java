package edu.southwestern.util.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import edu.southwestern.util.datastructures.Triple;

public class UniformCostSearch<A extends Action, S extends State<A>> extends GraphSearch<A,S> {

	/**
	 * Initialize uniform cost search
	 */
	public UniformCostSearch() {
	}

	/**
	 * Return an empty data structure that will manage the fringe of the graph search. In the case
	 * of UCS that data structure is a priority queue where the priority is defined to be
	 * the actual cost so far.
	 * @return
	 */
	public Queue<Triple<S, ArrayList<A>, Double>> getQueueStrategy() {
		Queue<Triple<S,ArrayList<A>,Double>> pq = new PriorityQueue<>(50, new Comparator<Triple<S,ArrayList<A>,Double>>() {
			/**
			 * A* compares states based on cost.
			 */
			@Override
			public int compare(Triple<S, ArrayList<A>, Double> o1,
							   Triple<S, ArrayList<A>, Double> o2) {
				return (int) Math.signum(o1.t3 - o2.t3);
			}
			
		});
		return pq;
	}

	
}

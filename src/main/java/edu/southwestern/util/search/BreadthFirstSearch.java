package edu.southwestern.util.search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import edu.southwestern.util.datastructures.Triple;

public class BreadthFirstSearch<A extends Action, S extends State<A>> extends GraphSearch<A,S> {

	/**
	 * Initialize breadth first search
	 */
	public BreadthFirstSearch() {
	}

	/**
	 * Return an empty data structure that will manage the fringe of the graph search. In the case
	 * of BFS that data structure is a simple FIFO queue.
	 * @return
	 */
	public Queue<Triple<S, ArrayList<A>, Double>> getQueueStrategy() {
		// Java's standard LinkedList behaves like a Queue with respect to the add and poll commands
		Queue<Triple<S,ArrayList<A>,Double>> pq = new LinkedList<>();
		return pq;
	}

	
}

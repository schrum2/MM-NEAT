package pacman.game.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.PriorityQueue;

import pacman.game.Game;
import pacman.game.Constants.MOVE;

/*
 * This class is used to compute the shortest path for the ghosts: as these may not reverse, one cannot use 
 * a simple look-up table. Instead, we use the pre-computed shortest path distances as an admissable
 * heuristic. Although AStar needs to be run every time a path is to be found, it is very quick and does
 * not expand too many nodes beyond those on the optimal path.
 */
public class AStar {

    private N[] graph;

    public void createGraph(Node[] nodes) {
        graph = new N[nodes.length];

        //create graph
        for (int i = 0; i < nodes.length; i++) {
            graph[i] = new N(nodes[i].nodeIndex);
        }

        //add neighbours
        for (int i = 0; i < nodes.length; i++) {
            EnumMap<MOVE, Integer> neighbours = nodes[i].neighbourhood;
            MOVE[] moves = MOVE.values();

            for (int j = 0; j < moves.length; j++) {
                if (neighbours.containsKey(moves[j])) {
                    graph[i].adj.add(new E(graph[neighbours.get(moves[j])], moves[j], 1));
                }
            }
        }
    }

    public synchronized int[] computePathsAStar(int s, int t, MOVE lastMoveMade, Game game) {
        N start = graph[s];
        N target = graph[t];

        PriorityQueue<N> open = new PriorityQueue<N>();
        ArrayList<N> closed = new ArrayList<N>();

        start.g = 0;
        start.h = game.getShortestPathDistance(start.index, target.index);

        start.reached = lastMoveMade;

        open.add(start);

        while (!open.isEmpty()) {
            N currentNode = open.poll();
            closed.add(currentNode);

            if (currentNode.isEqual(target)) {
                break;
            }

            for (E next : currentNode.adj) {
                if (next.move != currentNode.reached.opposite()) {
                    double currentDistance = next.cost;

                    if (!open.contains(next.node) && !closed.contains(next.node)) {
                        next.node.g = currentDistance + currentNode.g;
                        next.node.h = game.getShortestPathDistance(next.node.index, target.index);
                        next.node.parent = currentNode;

                        next.node.reached = next.move;

                        open.add(next.node);
                    } else if (currentDistance + currentNode.g < next.node.g) {
                        next.node.g = currentDistance + currentNode.g;
                        next.node.parent = currentNode;

                        next.node.reached = next.move;

                        if (open.contains(next.node)) {
                            open.remove(next.node);
                        }

                        if (closed.contains(next.node)) {
                            closed.remove(next.node);
                        }

                        open.add(next.node);
                    }
                }
            }
        }

        return extractPath(target);
    }

    public synchronized int[] computePathsAStar(int s, int t, Game game) {
        return computePathsAStar(s, t, MOVE.NEUTRAL, game);
    }

    private synchronized int[] extractPath(N target) {
        ArrayList<Integer> route = new ArrayList<Integer>();
        N current = target;
        route.add(current.index);

        while (current.parent != null) {
            route.add(current.parent.index);
            current = current.parent;
        }

        Collections.reverse(route);

        int[] routeArray = new int[route.size()];

        for (int i = 0; i < routeArray.length; i++) {
            routeArray[i] = route.get(i);
        }

        return routeArray;
    }

    public void resetGraph() {
        for (N node : graph) {
            node.g = 0;
            node.h = 0;
            node.parent = null;
            node.reached = null;
        }
    }
}

class N implements Comparable<N> {

    public N parent;
    public double g, h;
    public boolean visited = false;
    public ArrayList<E> adj;
    public int index;
    public MOVE reached = null;

    public N(int index) {
        adj = new ArrayList<E>();
        this.index = index;
    }

    public N(double g, double h) {
        this.g = g;
        this.h = h;
    }

    public boolean isEqual(N another) {
        return index == another.index;
    }

    public String toString() {
        return "" + index;
    }

    public int compareTo(N another) {
        if ((g + h) < (another.g + another.h)) {
            return -1;
        } else if ((g + h) > (another.g + another.h)) {
            return 1;
        }

        return 0;
    }
}

class E {

    public N node;
    public MOVE move;
    public double cost;

    public E(N node, MOVE move, double cost) {
        this.node = node;
        this.move = move;
        this.cost = cost;
    }
}
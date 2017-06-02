package pacman.entries.pacman.eiisolver.graph;

import java.util.*;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * Graph containing only junctions (>2 edges). There may be multiple edges
 * between 2 nodes of this graph. There may be edges from a node to itself.
 *
 * @author louis
 *
 */
public class JunctionGraph {

	/**
	 * underlying maze
	 */
	public Game game;
	/**
	 * Dense array containing all junction nodes
	 */
	public Node[] junctionNodes;
	/**
	 * Contains all nodes in the current maze
	 */
	public Node[] nodes;
	public List<BigEdge> edges = new ArrayList<BigEdge>();
	private static int[] skipDistances = new int[] { 7// 1, 7, 18, 32, 48, 70
	};
	private static int[] currPath = new int[50];
	private static int currPathLen;
	/**
	 * Contains paths. The elements are junction indices, or -1 to denote the
	 * end of a path. See ghostPath.
	 */
	public static byte[] paths = new byte[2000000];
	private static int pathIndex;
	/**
	 * Contains the ghost distance between any two junction nodes.
	 * ghostDist[move1][index1][move2][index2] gives the distance between
	 * junction nodes index1 and index 2 given that move1 was the move leading
	 * to index1, move2 will be the move made from index2
	 */
	public int[][][][] ghostDist;
	/**
	 * Contains the ghost path between any two junction nodes.
	 * ghostPath[move1][index1][move2][index2] contains the index in paths that
	 * contain the junction indices of the junctions when walking from index1
	 * (junction index) to index2 (junction index). The path ends with -1. The
	 * final node is included in the path, but not the first node.
	 */
	public int[][][][] ghostPath;
	/**
	 * Contains the ghost distance from any node to a junction node.
	 * ghostDist[move1][index1][move2][index2] gives the distance between nodes
	 * index1 and junction node with junction index 2 given that move1 was the
	 * move leading to index1, move2 will be the move made from index2
	 */
	public int[][][][] ghostDistToJunc;

	/**
	 * Returns the ghost distance from junction index1 to junction index2
	 *
	 * @param index1
	 *            index of source node(normal index, not junction index) of from
	 *            node
	 * @param lastMoveMade
	 *            last move made that led to index1
	 * @param index2
	 *            index of destination
	 * @param moveFromIndex2
	 *            move to be made from index2
	 * @return
	 */
	public int getGhostDistBetweenJunctions(int index1, MOVE lastMoveMade, int index2, MOVE moveFromIndex2) {
		int j1 = nodes[index1].junctionIndex;
		int j2 = nodes[index2].junctionIndex;
		return ghostDist[lastMoveMade.ordinal()][j1][moveFromIndex2.ordinal()][j2];
	}

	public int getGhostPathToJunction(int junctionIndex1, MOVE lastMoveMade, int junctionIndex2, MOVE moveFromIndex2) {
		return ghostPath[lastMoveMade.ordinal()][junctionIndex1][moveFromIndex2.ordinal()][junctionIndex2];
	}

	/**
	 * Returns the ghost distance from a node (junction or not) to a junction
	 *
	 * @param index1
	 *            index of source node(normal index, not junction index) of from
	 *            node
	 * @param lastMoveMade
	 *            last move made that led to index1
	 * @param index2
	 *            index of destination (normal index, not junction index)
	 * @param moveFromIndex2
	 *            move to be made from index2
	 * @return
	 */
	public int getGhostDistToJunction(int index1, MOVE lastMoveMade, int index2, MOVE moveFromIndex2) {
		return ghostDistToJunc[lastMoveMade.ordinal()][index1][moveFromIndex2.ordinal()][nodes[index2].junctionIndex];
	}

	private int getGhostDistToJunctionInternal(int srcIndex, MOVE lastMoveMade, int destIndex, MOVE moveFromDest) {
		int junc1;
		MOVE lastMove;
		int distToJunc1;
		Node n1 = nodes[srcIndex];
		if (n1.isJunction()) {
			junc1 = srcIndex;
			lastMove = lastMoveMade;
			distToJunc1 = 0;
		} else if (lastMoveMade == MOVE.NEUTRAL) {
			int bestDist = 10000;
			for (int i = 0; i < n1.nrNeighbours; ++i) {
				int dist = getGhostDistToJunctionInternal(srcIndex, n1.neighbourMoves[i], destIndex, moveFromDest);
				if (dist < bestDist) {
					bestDist = dist;
				}
			}
			return bestDist;
		} else {
			junc1 = n1.getNextJunction(lastMoveMade);
			lastMove = n1.getLastMoveToNextJunction(lastMoveMade);
			distToJunc1 = n1.getDistToNextJunction(lastMoveMade);
		}
		return distToJunc1 + getGhostDistBetweenJunctions(junc1, lastMove, destIndex, moveFromDest);
	}

	public Node find(int x, int y) {
		for (Node n : nodes) {
			if (n.x == x && n.y == y) {
				return n;
			}
		}
		return null;
	}

	public BigEdge findEdge(Node junction1, Node junction2) {
		for (BigEdge edge : edges) {
			if ((edge.endpoints[0] == junction1 && edge.endpoints[1] == junction2)
					|| (edge.endpoints[0] == junction2 && edge.endpoints[1] == junction1)) {
				return edge;
			}
		}
		return null;
	}

	/**
	 * Constructs the junction graph from the given maze.
	 *
	 * @param maze
	 */
	public void createFromMaze(Game game) {
		long start = System.currentTimeMillis();
		this.game = game;
		// create internal nodes
		nodes = new Node[game.getNumberOfNodes()];
		for (int i = 0; i < nodes.length; ++i) {
			Node n = new Node();
			n.index = i;
			nodes[i] = n;
			n.neighbourMoves = game.getPossibleMoves(i);
			n.neighbours = new int[n.neighbourMoves.length];
			n.nrNeighbours = n.neighbours.length;
			n.x = game.getNodeXCoord(i);
			n.y = game.getNodeYCoord(i);
			for (int m = 0; m < n.neighbourMoves.length; ++m) {
				n.neighbours[m] = game.getNeighbour(i, n.neighbourMoves[m]);
			}
		}
		// create junction nodes
		junctionNodes = new Node[game.getJunctionIndices().length];
		for (int i = 0; i < junctionNodes.length; ++i) {
			junctionNodes[i] = nodes[game.getJunctionIndices()[i]];
			junctionNodes[i].junctionIndex = i;
		}
		// create edges
		for (int i = 0; i < junctionNodes.length; ++i) {
			Node n = junctionNodes[i];
			for (MOVE move : game.getPossibleMoves(n.index)) {
				addEdge(n, move);
			}
		}
		// set onlyGhostMove
		for (int i = 0; i < nodes.length; ++i) {
			nodes[i].calcOnlyMove();
		}
		calcGhostDist();
		long now = System.currentTimeMillis();
		System.out.println("Junction graph: #junctions = " + junctionNodes.length + ", #edges = " + edges.size());
		System.out.println("nr millis: " + (now - start));
	}

	private void addEdge(Node n, MOVE move) {
		MOVE lastMoveMade = move;
		int lastIndex = n.index;
		int nextIndex = game.getNeighbour(lastIndex, lastMoveMade);
		if (nodes[nextIndex].edge != null) {
			// edge has already been added
			return;
		}
		if (game.isJunction(nextIndex) && nextIndex < lastIndex) {
			// edge without internal nodes, but it has already been added
			return;
		}
		// this is a new edge
		BigEdge edge = new BigEdge();
		edge.id = edges.size();
		edges.add(edge);
		List<Node> nodeList = new ArrayList<Node>();
		edge.firstMoveToOtherEnd[0] = lastMoveMade;
		while (!game.isJunction(nextIndex)) {
			nodes[nextIndex].edge = edge;
			nodeList.add(nodes[nextIndex]);
			nodes[nextIndex].lastMoveIfForward = lastMoveMade;
			nodes[nextIndex].moveToPrevNode = lastMoveMade.opposite();
			MOVE[] possibleMoves = game.getPossibleMoves(nextIndex, lastMoveMade);
			lastIndex = nextIndex;
			lastMoveMade = possibleMoves[0];
			nodes[nextIndex].moveToNextNode = lastMoveMade;
			nextIndex = game.getNeighbour(nextIndex, lastMoveMade);
		}
		edge.firstMoveToOtherEnd[1] = lastMoveMade.opposite();
		Node n2 = nodes[nextIndex];
		edge.endpoints[0] = n;
		edge.endpoints[1] = n2;
		edge.internalNodes = nodeList.toArray(new Node[0]);
		edge.length = edge.internalNodes.length + 1;
		n.edges[n.nrEdges] = edge;
		++n.nrEdges;
		n2.edges[n2.nrEdges] = edge;
		++n2.nrEdges;
		for (int i = 0; i < edge.internalNodes.length; ++i) {
			Node node = edge.internalNodes[i];
			node.distToJunction = new int[2];
			node.distToJunction[0] = i + 1;
			node.distToJunction[1] = edge.internalNodes.length - i;
			node.distToClosestJunction = Math.min(node.distToJunction[0], node.distToJunction[1]);
			node.skipOpposite = true;
			if (node.distToClosestJunction == edge.length / 2 || node.distToClosestJunction == 1) {
				node.skipOpposite = false;
			} else {
				for (int s = 0; s < skipDistances.length; ++s) {
					if (skipDistances[s] == node.distToClosestJunction && skipDistances[s] < edge.length / 2 - 4) {
						node.skipOpposite = false;
					}
				}
			}
			node.edgeIndex = i;
		}
	}

	private void calcGhostDist() {
		int N = junctionNodes.length;
		ghostDist = new int[5][N][5][N];
		ghostPath = new int[5][N][5][N];
		pathIndex = 0;
		// initialize all distances with large value
		for (int m = 0; m < 5; ++m) {
			for (int i = 0; i < N; ++i) {
				for (int m2 = 0; m2 < 5; ++m2) {
					Arrays.fill(ghostDist[m][i][m2], 100000);
				}
			}
		}
		check("init");
		// set 0-distance between junction and itself
		for (int i = 0; i < N; ++i) {
			for (MOVE m1 : MOVE.values()) {
				for (MOVE m2 : MOVE.values()) {
					if (m1.opposite() != m2) {
						ghostDist[m1.ordinal()][i][m2.ordinal()][i] = 0;
					}
				}
			}
		}
		check("after 0-dist");
		// set all distances where ghost shortest path == real shortest path
		/*
		 * for (int i = 0; i < N; ++i) { Node n1 = junctionNodes[i]; for (int j
		 * = i; j < N; ++j) { Node n2 = junctionNodes[j]; int shortestDist =
		 * game.getShortestPathDistance(n1.index, n2.index); for (int e1 = 0; e1
		 * < n1.nrNeighbours; ++e1) { for (int e2 = 0; e2 < n2.nrNeighbours;
		 * ++e2) { int neighbourDist =
		 * game.getShortestPathDistance(n1.neighbours[e1], n2.neighbours[e2]);
		 * if (neighbourDist == shortestDist - 2) { updateDist(n1,
		 * n1.neighbourMoves[e1], n2, n2.neighbourMoves[e2].opposite(),
		 * shortestDist); } } } } }
		 */
		check("after shortest path");
		// calculate ghost distances that are other than shortest dist
		boolean[] visited = new boolean[junctionNodes.length];
		long startTime = System.currentTimeMillis();
		for (int maxDepth = 1; maxDepth < 20 && System.currentTimeMillis() - startTime < 100; ++maxDepth) {
			System.out.println("walk, maxDepth = " + maxDepth);
			nrVisit = 0;
			for (int i = 0; i < junctionNodes.length; ++i) {
				Arrays.fill(visited, false);
				visited[i] = true;
				Node start = junctionNodes[i];
				for (int e = 0; e < start.nrEdges; ++e) {
					BigEdge edge = start.edges[e];
					MOVE startMove = edge.getFirstMove(start);
					Node nextNode = edge.getOtherJunction(start);
					MOVE nextLastMove = edge.getFirstMove(nextNode).opposite();
					currPath[0] = nextNode.junctionIndex;
					currPathLen = 1;
					walk(visited, start, startMove, nextNode, nextLastMove, edge.length, maxDepth, 180);
				}
			}
			System.out.println("visits = " + nrVisit + ", time: " + (System.currentTimeMillis() - startTime));
		}
		check("after walk");
		for (BigEdge edge : edges) {

			Log.println(edge.endpoints[0] + " " + edge.firstMoveToOtherEnd[0].opposite() + " - " + edge.endpoints[1]
					+ " " + edge.firstMoveToOtherEnd[1] + ": "
					+ ghostDist[edge.firstMoveToOtherEnd[0].opposite()
							.ordinal()][edge.endpoints[0].junctionIndex][edge.firstMoveToOtherEnd[1]
									.ordinal()][edge.endpoints[1].junctionIndex]);
			Log.println(edge.endpoints[1] + " - " + edge.endpoints[0] + ": "
					+ ghostDist[edge.firstMoveToOtherEnd[1].opposite()
							.ordinal()][edge.endpoints[1].junctionIndex][edge.firstMoveToOtherEnd[0]
									.ordinal()][edge.endpoints[0].junctionIndex]);

		}
		// initialize neutral move distances
		for (int m = 0; m < 5; ++m) {
			if (m != MOVE.NEUTRAL.ordinal()) {
				for (int i = 0; i < N; ++i) {
					for (int j = 0; j < N; ++j) {
						int minDist = 10000;
						for (int m2 = 0; m2 < 5; ++m2) {
							int dist = ghostDist[m][i][m2][j];
							if (dist < minDist) {
								minDist = dist;
							}
						}
						ghostDist[m][i][MOVE.NEUTRAL.ordinal()][j] = minDist;
					}
				}
			}
		}
		setGhostDistToJunction();
	}

	/**
	 * Calculates ghostDistToJunc
	 */
	private void setGhostDistToJunction() {
		ghostDistToJunc = new int[5][nodes.length][5][junctionNodes.length];
		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < nodes.length; ++j) {
				for (int k = 0; k < 5; ++k) {
					Arrays.fill(ghostDistToJunc[i][j][k], 10000);
				}
			}
		}
		for (int i = 0; i < nodes.length; ++i) {
			Node n = nodes[i];
			for (int e = 0; e < n.nrNeighbours; ++e) {
				MOVE lastMoveMade = n.neighbourMoves[e].opposite();
				for (int j = 0; j < junctionNodes.length; ++j) {
					for (int e2 = 0; e2 < junctionNodes[j].nrNeighbours; ++e2) {
						MOVE moveFromIndex2 = junctionNodes[j].neighbourMoves[e2];
						int dist = getGhostDistToJunctionInternal(i, lastMoveMade, junctionNodes[j].index,
								moveFromIndex2);
						ghostDistToJunc[lastMoveMade.ordinal()][i][moveFromIndex2.ordinal()][j] = dist;
						if (dist == 0 && i != junctionNodes[j].index) {
							throw new RuntimeException("dist = 0");
						}

						ghostDistToJunc[MOVE.NEUTRAL.ordinal()][i][moveFromIndex2
								.ordinal()][j] = getGhostDistToJunctionInternal(i, MOVE.NEUTRAL, junctionNodes[j].index,
										moveFromIndex2);
						ghostDistToJunc[lastMoveMade.ordinal()][i][MOVE.NEUTRAL
								.ordinal()][j] = getGhostDistToJunctionInternal(i, lastMoveMade, junctionNodes[j].index,
										MOVE.NEUTRAL);
					}
				}
			}
		}
	}

	static long nrVisit = 0;

	private void walk(boolean[] visited, Node start, MOVE startMove, Node end, MOVE lastMove, int currDist, int depth,
			int maxDist) {
		++nrVisit;
		// update distances
		@SuppressWarnings("unused")
		boolean improvedDistance = updateDist(start, startMove, end, lastMove, currDist);
		// if (visited[end.junctionIndex] && !improvedDistance) {
		if (maxDist <= 0 /* || !improvedDistance */) {
			return;
		}
		visited[end.junctionIndex] = true;
		if (depth > 0) {
			for (int i = 0; i < end.nrEdges; ++i) {
				BigEdge edge = end.edges[i];
				MOVE firstMove = edge.getFirstMove(end);
				if (firstMove != lastMove.opposite()) {
					Node nextNode = edge.getOtherJunction(end);
					MOVE nextLastMove = edge.getFirstMove(nextNode).opposite();
					currPath[currPathLen] = nextNode.junctionIndex;
					++currPathLen;
					walk(visited, start, startMove, nextNode, nextLastMove, currDist + edge.length, depth - 1,
							maxDist - edge.length);
					--currPathLen;
				}
			}
		}
	}

	/**
	 * Adds distance info to ghostDist
	 *
	 * @return true if ghostDist was really updated
	 */
	private boolean updateDist(Node start, MOVE startMove, Node end, MOVE lastMove, int dist) {
		boolean improvedDistance = false;
		for (int i = 0; i < start.nrNeighbours; ++i) {
			if (start.neighbourMoves[i] != startMove) {
				MOVE moveToStart = start.neighbourMoves[i].opposite();
				int m1 = moveToStart.ordinal();
				for (int j = 0; j < end.nrNeighbours; ++j) {
					MOVE firstMove = end.neighbourMoves[j];
					if (firstMove != lastMove.opposite()) {
						int m2 = firstMove.ordinal();
						int[] arr = ghostDist[m1][start.junctionIndex][m2];
						if (dist <= arr[end.junctionIndex]) {
							improvedDistance = true;
						}
						if (dist < arr[end.junctionIndex]) {
							improvedDistance = true;
							int path1Index = pathIndex;
							for (int p = 0; p < currPathLen; ++p) {
								paths[pathIndex] = (byte) currPath[p];
								++pathIndex;
							}
							paths[pathIndex] = -1;
							++pathIndex;
							int returnPathIndex = pathIndex;
							for (int p = 0; p < currPathLen - 1; ++p) {
								paths[pathIndex] = (byte) currPath[currPathLen - p - 2];
								++pathIndex;
							}
							paths[pathIndex] = (byte) start.junctionIndex;
							++pathIndex;
							paths[pathIndex] = -1;
							++pathIndex;
							setDist(start, moveToStart, end, firstMove, dist, path1Index);
							setDist(end, firstMove.opposite(), start, moveToStart.opposite(), dist, returnPathIndex);
							if (dist < ghostDist[MOVE.NEUTRAL.ordinal()][start.junctionIndex][m2][end.junctionIndex]) {
								setDist(start, MOVE.NEUTRAL, end, firstMove, dist, path1Index);
								setDist(end, firstMove.opposite(), start, MOVE.NEUTRAL, dist, returnPathIndex);
							}
						}
					}
				}
			}
		}
		return improvedDistance;
	}

	private static final boolean track = false;
	private static final int trackY1 = 92, trackX1 = 36, trackY2 = 92, trackX2 = 12;
	private static final MOVE trackLastMove = MOVE.RIGHT, trackFirstMove = MOVE.UP;

	private void setDist(Node n1, MOVE lastMove, Node n2, MOVE firstMove, int dist, int indexInPaths) {
		// Log.println("setDist " + n1 + " " + lastMove + "-" + n2 + " " +
		// firstMove + " = " + dist);
		ghostDist[lastMove.ordinal()][n1.junctionIndex][firstMove.ordinal()][n2.junctionIndex] = dist;
		ghostPath[lastMove.ordinal()][n1.junctionIndex][firstMove.ordinal()][n2.junctionIndex] = indexInPaths;
		if (track) {
			if (n1.x == trackX1 && n1.y == trackY1 && lastMove == trackLastMove && n2.x == trackX2 && n2.y == trackY2
					&& firstMove == trackFirstMove) {
				System.out.println("dist = " + dist);
			}
		}
	}

	private void check(String msg) {
		if (track) {
			Node n1 = find(trackX1, trackY1);
			Node n2 = find(trackX2, trackY2);
			if (n1 != null && n2 != null) {
				System.out
						.println(msg + ", dist = " + ghostDist[trackLastMove.ordinal()][n1.junctionIndex][trackFirstMove
								.ordinal()][n2.junctionIndex]);
			}
		}
	}

	public void print(Game game, Board b) {
		String[][] repr = new String[200][200];
		int maxX = 0;
		int maxY = 0;
		for (int i = 0; i < game.getNumberOfNodes(); ++i) {
			int x = game.getNodeXCoord(i);
			if (x > maxX) {
				maxX = x;
			}
			int y = game.getNodeYCoord(i);
			if (y > maxY) {
				maxY = y;
			}
			Node n = nodes[i];
			String s = null;
			for (int g = 0; g < b.ghosts.length; ++g) {
				if (b.ghosts[g].currentNodeIndex == n.index) {
					s = "" + b.ghosts[g].lastMoveMade.toString().charAt(0);
					if (b.ghosts[g].edibleTime > 0) {
						s = s.toLowerCase();
					}
					break;
				}
			}
			if (s == null) {
				s = ".";
				if (n.index == b.pacmanLocation) {
					s = "P";
				} else if (b.containsPowerPill[i]) {
					s = "X";
				} else if (b.containsPill[i]) {
					s = "o";
				}
			}
			repr[x][y] = s;
			/*
			 * if (n.isJunction()) { repr[x][y] = "J" + n.nrEdges; } else {
			 * repr[x][y] = ""+n.edgeIndex; }
			 */
		}
		for (int j = 0; j <= maxY; ++j) {
			String row = String.format("%3d ", j);
			Log.print(row);
			for (int i = 0; i <= maxX; ++i) {
				String s = repr[i][j];
				if (s == null) {
					s = " ";
				}
				Log.print(s);
			}
			Log.println();
		}

	}
}

package pacman.entries.pacman.eiisolver;

import java.util.ArrayList;
import java.util.List;
import pacman.entries.pacman.eiisolver.graph.*;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostTrap {

    private static List<PathToPill> traps;
    private static PathToPill lastPath = null;

    public static void updateLevel(Game game, Board b) {
        traps = new ArrayList<PathToPill>();
        int level = game.getCurrentLevel() % 4;
        if (level == 0) {
            constructLevel0(game, b);
        } else if (level == 1) {
            constructLevel1(game, b);
        } else if (level == 2) {
            constructLevel2(game, b);
        } else if (level == 3) {
            constructLevel3(game, b);
        }
        lastPath = null;
    }

    public static MOVE rigTrap(Game game, Board b) {
        int ghostDist = Search.distToFarthestGhostInTrain();
        if (ghostDist >= 0 && ghostDist < 40) {
            System.out.println("In ghost train!");
            Log.println("In ghost train!");
            if (lastPath != null) {
                //return lastPath.getMove(game, b);
            }
            // walk to closest trap
            PathToPill bestPath = null;
            int shortestDist = 1000;
            int distToClosestGhost = 0;
            for (PathToPill path : traps) {
                if (b.containsPowerPill[path.powerPill.index]) {
                    int dist = game.getShortestPathDistance(b.pacmanLocation, path.powerPill.index);
                    int gDist = ghostDist(game, b, path.powerPill.index);
                    if (dist + 2 < gDist) {
                        System.out.println("To " + path.path[0] + ": pacmanDist " + dist + ", gDist: " + gDist);
                        if (dist < shortestDist) {
                            System.out.println("Better dist");
                            shortestDist = dist;
                            bestPath = path;
                            distToClosestGhost = gDist - dist;
                        }
                    }
                }
            }
            lastPath = bestPath;
            if (lastPath != null) {
                System.out.println("Set up trap towards " + lastPath.powerPill);
                Log.println("Set up trap towards " + lastPath.powerPill);
                lastPath = bestPath;
                shortestDist = 1000;
                Node pacmanNode = b.graph.nodes[b.pacmanLocation];
                MOVE bestMove = MOVE.NEUTRAL; // wait until nearest ghost gets very close
                if (distToClosestGhost <= 4) {
                    for (int j = 0; j < pacmanNode.nrNeighbours; ++j) {
                        int newDist = game.getShortestPathDistance(pacmanNode.neighbours[j], bestPath.powerPill.index);
                        if (newDist < shortestDist) {
                            bestMove = pacmanNode.neighbourMoves[j];
                            shortestDist = newDist;
                            System.out.println("Best move: " + bestMove + ", dist: " + shortestDist);
                        }
                    }
                }
                return bestMove;
            }
        }
        lastPath = null;
        return null;
    }
    /*public static MOVE rigTrapOld(Game game, Board b) {
     int ghostDist = Search.distToFarthestGhostInTrain();
     if (ghostDist >= 0 && ghostDist < 40) {
     System.out.println("In ghost train!");
     Log.println("In ghost train!");
     if (lastPath != null) {
     //return lastPath.getMove(game, b);
     }
     // walk to closest trap
     PathToPill bestPath = null;
     int shortestDist = 1000;
     int distToClosestGhost = 0;
     for (PathToPill path : traps) {
     if (b.containsPowerPill[path.powerPill.index]) {
     int dist = game.getShortestPathDistance(b.pacmanLocation, path.path[0].index);
     int gDist = ghostDist(game, b, path.path[0].index);
     if (dist < gDist + 2) {
     System.out.println("To " + path.path[0] + ": pacmanDist " + dist + ", gDist: " + gDist);
     if (path.isOnPath(b.graph.nodes[b.pacmanLocation])) {
     bestPath = path;
     shortestDist = 0;
     } else {
     if (dist < shortestDist) {
     System.out.println("Better dist");
     shortestDist = dist;
     bestPath = path;
     distToClosestGhost = gDist - dist;
     }
     }
     }
     }
     }
     lastPath = bestPath;
     if (lastPath != null) {
     System.out.println("Set up trap towards " + lastPath.powerPill);
     Log.println("Set up trap towards " + lastPath.powerPill);
     lastPath = bestPath;
     MOVE bestMove = MOVE.NEUTRAL;
     if (distToClosestGhost <= 3) {
     bestMove = lastPath.getMove(game, b);
     }
     return bestMove;
     }
     }
     lastPath = null;
     return null;
     }*/

    private static void constructLevel0(Game game, Board b) {
        PathToPill p1 = constructPath(game, b, 8, 4, new int[]{16, 12}, 16, 11, MOVE.LEFT);
        PathToPill p2 = constructPath(game, b, 8, 104, new int[]{16, 96}, 16, 97, MOVE.RIGHT);
        PathToPill p3 = constructPath(game, b, 108, 4, new int[]{92, 12}, 92, 11, MOVE.LEFT);
        PathToPill p4 = constructPath(game, b, 108, 104, new int[]{92, 96}, 92, 97, MOVE.RIGHT);
        traps.add(p1);
        traps.add(p2);
        traps.add(p3);
        traps.add(p4);
    }

    private static void constructLevel1(Game game, Board b) {
        PathToPill p1 = constructPath(game, b, 16, 4, new int[]{40, 16}, 40, 15, MOVE.LEFT);
        PathToPill p2 = constructPath(game, b, 16, 104, new int[]{40, 92}, 40, 93, MOVE.RIGHT);
        PathToPill p3 = constructPath(game, b, 104, 4, new int[]{116, 48, 116, 24}, 116, 47, MOVE.LEFT);
        PathToPill p4 = constructPath(game, b, 104, 104, new int[]{116, 60, 116, 84}, 40, 93, MOVE.RIGHT);
        traps.add(p1);
        traps.add(p2);
        traps.add(p3);
        traps.add(p4);
    }

    private static void constructLevel2(Game game, Board b) {
        PathToPill p1 = constructPath(game, b, 12, 4, new int[]{16, 36}, 15, 36, MOVE.UP);
        PathToPill p2 = constructPath(game, b, 12, 104, new int[]{16, 72}, 15, 72, MOVE.UP);
        PathToPill p3 = constructPath(game, b, 92, 4, new int[]{92, 36, 92, 24, 104, 24, 104, 4}, 103, 4, MOVE.LEFT);
        PathToPill p4 = constructPath(game, b, 92, 104, new int[]{92, 72, 92, 84, 104, 84, 104, 104}, 103, 104, MOVE.RIGHT);
        traps.add(p1);
        traps.add(p2);
        traps.add(p3);
        traps.add(p4);
    }

    private static void constructLevel3(Game game, Board b) {
        PathToPill p1 = constructPath(game, b, 12, 4, new int[]{4, 16}, 4, 15, MOVE.LEFT);
        PathToPill p2 = constructPath(game, b, 12, 104, new int[]{4, 92}, 4, 93, MOVE.RIGHT);
        PathToPill p3 = constructPath(game, b, 108, 4, new int[]{116, 16}, 116, 15, MOVE.LEFT);
        PathToPill p4 = constructPath(game, b, 108, 104, new int[]{116, 92}, 116, 93, MOVE.RIGHT);
        traps.add(p1);
        traps.add(p2);
        traps.add(p3);
        traps.add(p4);
    }

    private static int ghostDist(Game game, Board b, int nodeIndex) {
        int dist = 1000;
        for (MyGhost ghost : b.ghosts) {
            if (ghost.canKill()) {
                int d = game.getShortestPathDistance(ghost.currentNodeIndex, nodeIndex);
                if (d < dist) {
                    dist = d;
                }
            }
        }
        return dist;
    }

    private static PathToPill constructPath(Game game, Board b, int pillY, int pillX, int[] edgeCoords, int lastY, int lastX, MOVE firstMoveIntoPath) {
        PathToPill path = new PathToPill();
        path.powerPill = b.graph.find(pillX, pillY);
        path.firstMoveIntoPath = firstMoveIntoPath;
        List<Node> nodeList = new ArrayList<Node>();
        Node n = b.graph.find(edgeCoords[1], edgeCoords[0]);
        nodeList.add(n);
        for (int i = 2; i < edgeCoords.length; i += 2) {
            Node n1 = b.graph.find(edgeCoords[i - 1], edgeCoords[i - 2]);
            Node n2 = b.graph.find(edgeCoords[i + 1], edgeCoords[i]);
            BigEdge edge = b.graph.findEdge(n1, n2);
            if (edge.endpoints[0] == n1) {
                for (int j = 0; j < edge.internalNodes.length; ++j) {
                    nodeList.add(edge.internalNodes[j]);
                }
            } else {
                for (int j = edge.internalNodes.length - 1; j >= 0; --j) {
                    nodeList.add(edge.internalNodes[j]);
                }
            }
            nodeList.add(n2);
        }
        Node lastNode = b.graph.find(lastX, lastY);
        nodeList.add(lastNode);
        path.path = nodeList.toArray(new Node[0]);
        return path;
    }

    private static class PathToPill {

        Node powerPill;
        Node[] path;
        MOVE firstMoveIntoPath;

        public boolean isOnPath(Node n) {
            for (int i = 0; i < path.length; ++i) {
                if (path[i] == n) {
                    return true;
                }
            }
            return false;
        }

        public MOVE getMove(Game game, Board b) {
            Node pacmanNode = b.graph.nodes[b.pacmanLocation];
            for (int i = 0; i < path.length - 1; ++i) {
                if (path[i] == pacmanNode) {
                    // pacman is already on the trapping path; let her go to the next step
                    for (int j = 0; j < path[i].nrNeighbours; ++j) {
                        if (path[i].neighbours[j] == path[i + 1].index) {
                            return path[i].neighbourMoves[j];
                        }
                    }
                }
            }
            int shortestDist = 1000;
            MOVE bestMove = null;
            for (int j = 0; j < pacmanNode.nrNeighbours; ++j) {
                int newDist = game.getShortestPathDistance(pacmanNode.neighbours[j], powerPill.index);
                if (newDist < shortestDist) {
                    bestMove = pacmanNode.neighbourMoves[j];
                    shortestDist = newDist;
                }
            }
            return bestMove;
        }
    }
}

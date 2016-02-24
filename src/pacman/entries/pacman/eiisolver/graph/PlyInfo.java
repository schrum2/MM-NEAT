package pacman.entries.pacman.eiisolver.graph;

import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/**
 * Contains information specific at a certain ply during searching.
 */
public class PlyInfo {
    // ############################################################################
    // IN-PARAMETERS TO THE SEARCH
    // ############################################################################

    public int alpha;
    public int beta;
    /**
     * value when this ply was entered
     */
    public int currValue;
    /**
     * "budget"; 1 move costs normally 10
     */
    public int budget;
    /**
     * score (the points of the pacman)
     */
    public int score;
    // ############################################################################
    // SAVED PARAMETERS
    // ############################################################################
    /**
     * Board as it was at the beginning of the ply
     */
    public Board savedBoard;
    /**
     * Number of possible moves
     */
    int nrPossibleMoves;
    /**
     * Current move nr; ranges from 0 (first move searched) to nrPossibleMoves
     */
    int currMoveNr;
    int pacmanMoveIndex;
    /**
     * contains the moves that pacman can make at this ply; indices into
     * pacmanLocation.neighbours
     */
    int[] pacmanMoves = new int[4];
    int[] ghostMoveIndex;
    /**
     * nrGhostMoves[ghostIndex] contains the number of moves that each ghost can
     * make
     */
    int[] nrGhostMoves = new int[4];
    /**
     * ghostMoves[ghostIndex] contains the moves that a ghost can make at this
     * play; indices into ghost[].neighbours
     */
    int[][] ghostMoves = new int[4][4];
    /**
     * Points scored at this move
     */
    int moveScore;
    /**
     * if a pill was eaten at this move
     */
    boolean pillValue;
    /**
     * if a power pill was eaten at this move
     */
    boolean powerPillValue;
    /**
     * if a ghost was killed at this move
     */
    boolean ghostKilled;
    /**
     * if move towards ghost was skipped at this move
     */
    boolean moveTowardsGhostSkipped;
    /**
     * hash value of the board
     */
    long hash;
    /**
     * Transpos entry for this position (null if not found)
     */
    TransposTable.TransposInfo transpos;
    /**
     * Points to higher ply
     */
    PlyInfo prev;
    /**
     * Targets that were backed up in this ply
     */
    Target[] backedUpTargets = new Target[20];
    int nrBackedUpTargets;
    // ############################################################################
    // OUTPUT PARAMETERS
    // ############################################################################
    /**
     * The value of the best move
     */
    public int bestValue;
    public int[] bestGhostMove;
    public int bestPacmanMove;
    /**
     * number of moves that do not lead to death
     */
    public int nrSurvivingMoves;

    public PlyInfo() {
        savedBoard = new Board();
        ghostMoveIndex = new int[GHOST.values().length];
        bestGhostMove = new int[GHOST.values().length];
    }

    /**
     * Initializes move generation. Updates nrPossibleMoves
     *
     * @param movePacman
     */
    public void initMove(boolean movePacman, boolean skipOpposite) {
        currMoveNr = 0;
        if (movePacman) {
            Node n = Search.nodes[Search.b.pacmanLocation];
            nrPossibleMoves = 0;
            moveTowardsGhostSkipped = false;
            boolean checkSkipNeeded = true;
            if (prev != null) {
                PlyInfo prev2 = prev.prev;
                if (prev2 != null) {
                    if (Search.nodes[prev2.savedBoard.pacmanLocation].edge == Search.nodes[Search.b.pacmanLocation].edge
                            && Search.nodes[Search.b.pacmanLocation].edge != null) {
                        // pacman on same edge as last move
                        if (prev2.moveTowardsGhostSkipped) {
                            // last move we skipped towards ghost, so situation has not changed. 
                            // Just skip a move opposite to last move made
                            moveTowardsGhostSkipped = true;
                            checkSkipNeeded = false;
                            skipOpposite = true;
                        } else if (prev.nrPossibleMoves == 1) {
                            // last move we did not skip a move towards ghost, and the ghosts
                            // could only make 1 move. 
                            checkSkipNeeded = false;
                        }
                    }
                }
            }
            int[] killerMoves = Search.pacmanKillerMoves[Search.b.pacmanLocation];
            for (int e = 0; e < n.nrNeighbours; ++e) {
                int index = killerMoves[e];
                if (!checkSkipNeeded || !Search.skipMoveTowardsGhost(n.neighbours[index])) {
                    pacmanMoves[nrPossibleMoves] = index;
                    ++nrPossibleMoves;
                } else if (Search.log) {
                    Search.log("Skip move towards ghost: " + n.neighbourMoves[index]);
                }
            }
            if (nrPossibleMoves == 0) {
                // all moves are towards a ghost; just pick a random one
                pacmanMoves[0] = 0;
                nrPossibleMoves = 1;
            } else if (nrPossibleMoves == n.nrNeighbours && skipOpposite) {
                nrPossibleMoves = 0;
                // no moves were skipped due to moving to ghost; do move generation once more; skip opposite move
                for (int e = 0; e < n.nrNeighbours; ++e) {
                    int index = killerMoves[e];
                    if (n.neighbourMoves[index] != Search.b.pacmanLastMove.opposite()) {
                        pacmanMoves[nrPossibleMoves] = index;
                        ++nrPossibleMoves;
                    }
                }
            }
            pacmanMoveIndex = -1;
        } else {
            nrPossibleMoves = 1;
            for (int i = 0; i < ghostMoveIndex.length; ++i) {
                MyGhost ghost = Search.b.ghosts[i];
                if (ghost.lairTime > 0 || (ghost.edibleTime > 0 && (ghost.edibleTime & 1) == 0)) {
                    ghostMoveIndex[i] = -2;
                    nrGhostMoves[i] = 0;
                } else {
                    Node n = Search.nodes[ghost.currentNodeIndex];
                    int move = n.onlyGhostMove[ghost.lastMoveMade.ordinal()];
                    if (move >= 0) {
                        ghostMoveIndex[i] = 0;
                        ghostMoves[i][0] = move;
                        nrGhostMoves[i] = 1;
                    } else {
                        int[] killerMoves = Search.ghostKillerMoves[ghost.currentNodeIndex][ghost.lastMoveMade.ordinal()];
                        int moveIndex = 0;
                        for (int e = 0; e < n.nrNeighbours; ++e) {
                            int index = killerMoves[e];
                            if (n.neighbourMoves[index] != ghost.lastMoveMade.opposite()) {
                                ghostMoves[i][moveIndex] = index;
                                ++moveIndex;
                            }
                        }
                        ghostMoveIndex[i] = moveIndex - 1;//i == 0 ? -1 : 0;
                        nrGhostMoves[i] = moveIndex;
                        nrPossibleMoves *= moveIndex;
                    }
                }
            }
        }
    }

    /**
     * If we found the current position in the transposition table, we want to
     * try the best move from that table first.
     *
     * @param movePacman
     */
    public void setTransposMoveFirst(boolean movePacman) {
        // if we found a transposition table entry, put its move (which has been put in bestMove) first.
        if (movePacman) {
            if (Search.log) {
                Search.log("Try transpos move first: " + Search.nodes[Search.b.pacmanLocation].neighbourMoves[bestPacmanMove]);
            }
            int j;
            for (j = 0; j < nrPossibleMoves && pacmanMoves[j] != bestPacmanMove; ++j) {
            }
            if (pacmanMoves[j] == bestPacmanMove) {
                for (; j > 0; --j) {
                    pacmanMoves[j] = pacmanMoves[j - 1];
                }
                pacmanMoves[0] = bestPacmanMove;
            }
        } else {
            for (int i = 0; i < ghostMoveIndex.length; ++i) {
                if (nrGhostMoves[i] > 1) {
                    int j;
                    int[] moves = ghostMoves[i];
                    int bestMove = bestGhostMove[i];
                    if (Search.log) {
                        Node n = Search.nodes[Search.b.ghosts[i].currentNodeIndex];
                        Search.log("Try transpos move first: " + n + ", " + n.neighbourMoves[bestMove]);
                    }
                    for (j = 0; j < nrGhostMoves[i] && moves[j] != bestMove; ++j) {
                    }
                    if (moves[j] == bestMove) {
                        for (; j > 0; --j) {
                            moves[j] = moves[j - 1];
                        }
                        moves[0] = bestMove;
                    }
                }
            }
        }
    }

    /**
     * Used during extended search; filter away moves that do not lead to
     * targets or to pacman.
     */
    public void filterTargetMoves() {
        nrBackedUpTargets = 0;
        for (int i = 0; i < ghostMoveIndex.length; ++i) {
            if (nrGhostMoves[i] > 1) {
                MyGhost ghost = Search.b.ghosts[i];
                // update target information
                boolean anyReached = false; // will be true if any of the targets has already been reached
                Target reachedNowTarget = null; // not null if we just got to a new target
                for (int t = 0; t < Search.nrGhostTargets[i]; ++t) {
                    Target target = Search.ghostTargets[i][t];
                    anyReached |= target.reached;
                    if (!target.reached && !target.abandoned) {
                        target.backup();
                        backedUpTargets[nrBackedUpTargets] = target;
                        ++nrBackedUpTargets;
                        int currDist = Search.graph.getGhostDistToJunction(ghost.currentNodeIndex, ghost.lastMoveMade,
                                target.ghostJunction.index, target.firstMoveFromGhost);
                        if (currDist == 0) {
                            // target reached!
                            target.reached = true;
                            reachedNowTarget = target;
                        } else if (currDist > target.currDist) {
                            target.abandoned = true;
                        }
                        target.currDist = currDist;
                    }
                }
                // go through all moves, filter away those that do not go to target or to pacman
                int[] moves = ghostMoves[i];
                for (int m = 0; m < nrGhostMoves[i]; ++m) {
                    int newIndex = Search.nodes[ghost.currentNodeIndex].neighbours[moves[m]];
                    MOVE newMove = Search.nodes[ghost.currentNodeIndex].neighbourMoves[moves[m]];
                    boolean keepMove = false;
                    int nrOpen = 0;
                    // while there are still open targets left, ghost should go to one of these
                    // targets
                    for (int t = 0; t < Search.nrGhostTargets[i] && !keepMove; ++t) {
                        Target target = Search.ghostTargets[i][t];
                        if (!target.reached && !target.abandoned) {
                            ++nrOpen;
                            int newDist = Search.graph.getGhostDistToJunction(newIndex, newMove,
                                    target.ghostJunction.index, target.firstMoveFromGhost);
                            if (newDist < target.currDist) {
                                keepMove = true;
                            }
                        } else if (reachedNowTarget == target) {
                            // we just reached the target; now continue into the edge belonging to the target
                            keepMove = newMove == target.firstMoveFromGhost;
                        }
                    }
                    if (nrOpen == 0 || anyReached) {
                        // no targets left, or we have already reached a target
                        int currDist = Search.game.getShortestPathDistance(ghost.currentNodeIndex, Search.b.pacmanLocation);
                        int newDist = Search.game.getShortestPathDistance(newIndex, Search.b.pacmanLocation);
                        if (newDist < currDist) {
                            keepMove = true;
                        }
                    }
                    if (!keepMove) {
                        if (Search.log) {
                            Search.log("skipped move " + Search.nodes[ghost.currentNodeIndex]
                                    + " " + Search.nodes[ghost.currentNodeIndex].neighbourMoves[moves[m]]);
                        }
                        for (int m2 = m + 1; m2 < nrGhostMoves[i]; ++m2) {
                            moves[m2 - 1] = moves[m2];
                        }
                        if (nrGhostMoves[i] > 1) { // keep at least 1 move
                            --nrGhostMoves[i];
                            --m;
                        }
                    }
                }
            }
        }
        nrPossibleMoves = 1;
        for (int i = 0; i < 4; ++i) {
            if (nrGhostMoves[i] > 0) {
                nrPossibleMoves *= nrGhostMoves[i];
            }
        }
    }

    /**
     * Used during extended search: filters out pacman moves into dead ends
     * (border edges that will certainly be covered by ghosts)
     */
    public void filterDeadEnds() {
        if (Search.nrDeadEdges == 0) {
            return;
        }
        Node n = Search.nodes[Search.b.pacmanLocation];
        if (n.isJunction()) {
            for (int m = 0; m < nrPossibleMoves; ++m) {
                int index = pacmanMoves[m];
                Node nextNode = Search.nodes[n.neighbours[index]];
                BigEdge edge = nextNode.edge;
                boolean keepMove = true;
                for (int i = 0; i < Search.nrDeadEdges; ++i) {
                    if (Search.deadEdges[i] == edge) {
                        keepMove = false;
                        break;
                    }
                }
                if (!keepMove) {
                    if (Search.log) {
                        Search.log("skipped pacman move " + n + " " + n.neighbourMoves[index]);
                    }
                    for (int m2 = m + 1; m2 < nrPossibleMoves; ++m2) {
                        pacmanMoves[m2 - 1] = pacmanMoves[m2];
                    }
                    if (nrPossibleMoves > 1) {
                        --nrPossibleMoves;
                        --m;
                    }
                }
            }
        }
    }

    public void restoreTargets() {
        for (int t = 0; t < nrBackedUpTargets; ++t) {
            backedUpTargets[t].restore();
        }
        nrBackedUpTargets = 0;
    }

    /**
     * Sets next move
     *
     * @param movePacman
     * @return false if there were no more moves
     */
    public boolean nextMove(boolean movePacman) {
        if (movePacman) {
            ++pacmanMoveIndex;
        } else {
            nextGhostMove(0);
        }
        ++currMoveNr;
        return currMoveNr <= nrPossibleMoves;
    }

    public void saveBestMove(boolean movePacman) {
        if (movePacman) {
            bestPacmanMove = pacmanMoves[pacmanMoveIndex];
        } else {
            for (int i = 0; i < bestGhostMove.length; ++i) {
                if (ghostMoveIndex[i] >= 0) {
                    bestGhostMove[i] = ghostMoves[i][ghostMoveIndex[i]];
                } else {
                    bestGhostMove[i] = -1;
                }
            }
        }
    }

    private void nextGhostMove(int index) {
        if (index >= ghostMoveIndex.length) {
            return;
        }
        if (ghostMoveIndex[index] == -2) {
            nextGhostMove(index + 1);
            return;
        }
        ++ghostMoveIndex[index];
        if (ghostMoveIndex[index] >= nrGhostMoves[index]) {
            ghostMoveIndex[index] = 0;
            nextGhostMove(index + 1);
        }
    }

    public void move(boolean movePacman) {
        if (movePacman) {
            movePacman(Search.b);
        } else {
            moveGhosts(Search.b);
        }
    }

    public void unmove(boolean movePacman) {
        if (movePacman) {
            unmovePacman(Search.b);
        } else {
            unmoveGhosts(Search.b);
        }
    }

    public String moveToString(boolean movePacman) {
        if (movePacman) {
            if (pacmanMoveIndex >= 0) {
                Node n = Search.graph.nodes[Search.b.pacmanLocation];
                return "(" + n.y + "," + n.x + ","
                        + Search.b.graph.nodes[Search.b.pacmanLocation].neighbourMoves[pacmanMoves[pacmanMoveIndex]].toString()
                        + ")";
            } else {
                return "NEUTRAL";
            }
        } else {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < ghostMoveIndex.length; ++i) {
                int index = Search.b.ghosts[i].currentNodeIndex;
                if (index >= 0) {
                    Node n = Search.b.graph.nodes[index];
                    buf.append("(" + n.y + "," + n.x + ",");
                    int moveIndex = ghostMoveIndex[i];
                    if (moveIndex >= 0) {
                        buf.append(n.neighbourMoves[ghostMoves[i][moveIndex]].toString());
                    } else {
                        buf.append("NEUTRAL");
                    }
                    buf.append(") ");
                }
            }
            return buf.toString();
        }
    }

    public void movePacman(Board b) {
        moveScore = 0;
        savedBoard.copyFrom(b);
        if (pacmanMoveIndex >= 0) {
            moveScore -= Search.heuristics.getNodeScore(b.pacmanLocation);
            Node n = b.graph.nodes[b.pacmanLocation];
            int index = pacmanMoves[pacmanMoveIndex];
            b.pacmanLocation = n.neighbours[index];
            b.pacmanLastMove = n.neighbourMoves[index];
            moveScore += Search.heuristics.getNodeScore(b.pacmanLocation);
            pillValue = b.containsPill[b.pacmanLocation];
            if (pillValue) {
                --b.nrPillsOnBoard;
                moveScore += Search.heuristics.getPillScore(b.pacmanLocation);
                b.currentPillHash ^= Board.pillHash[b.pacmanLocation];
                b.containsPill[b.pacmanLocation] = false;
            }
            powerPillValue = b.containsPowerPill[b.pacmanLocation];
            if (powerPillValue) {
                --b.nrPowerPillsOnBoard;
                moveScore += Search.heuristics.getPowerPillScore();
                for (int i = 0; i < ghostMoveIndex.length; ++i) {
                    MyGhost ghost = b.ghosts[i];
                    ghost.edibleTime = Search.b.currentEdibleTime;
                    ghost.lastMoveMade = ghost.lastMoveMade.opposite();
                }
                b.currentPillHash ^= Board.pillHash[b.pacmanLocation];
                b.containsPowerPill[b.pacmanLocation] = false;
            }
        }
    }

    public void unmovePacman(Board b) {
        if (pillValue) {
            ++b.nrPillsOnBoard;
        }
        b.containsPill[b.pacmanLocation] = pillValue;
        if (powerPillValue) {
            ++b.nrPowerPillsOnBoard;
        }
        b.containsPowerPill[b.pacmanLocation] = powerPillValue;
        if (pacmanMoveIndex >= 0) {
            b.copyFrom(savedBoard);
        }
    }

    public void moveGhosts(Board b) {
        savedBoard.copyFrom(b);
        for (int i = 0; i < ghostMoveIndex.length; ++i) {
            MyGhost ghost = b.ghosts[i];
            int moveIndex = ghostMoveIndex[i];
            if (moveIndex >= 0) {
                Node n = b.graph.nodes[b.ghosts[i].currentNodeIndex];
                int index = ghostMoves[i][moveIndex];
                ghost.currentNodeIndex = n.neighbours[index];
                ghost.lastMoveMade = n.neighbourMoves[index];
            }
            if (ghost.edibleTime > 0) {
                --ghost.edibleTime;
            }
            if (ghost.lairTime > 0) {
                --ghost.lairTime;
                if (ghost.lairTime == 0) {
                    ghost.currentNodeIndex = Search.game.getGhostInitialNodeIndex();
                    ghost.edibleTime = 0;
                    ghost.lastMoveMade = MOVE.NEUTRAL;
                }
            }
        }
    }

    public void unmoveGhosts(Board b) {
        b.copyFrom(savedBoard);
    }

    /**
     * Copies the results of a search (best move, best value) from the given
     * source.
     *
     * @param src
     */
    public void copySearchResult(PlyInfo src) {
        budget = src.budget;
        bestValue = src.bestValue;
        for (int i = 0; i < bestGhostMove.length; ++i) {
            bestGhostMove[i] = src.bestGhostMove[i];
        }
        bestPacmanMove = src.bestPacmanMove;
    }
}

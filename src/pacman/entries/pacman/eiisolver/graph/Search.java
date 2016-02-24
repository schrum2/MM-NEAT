package pacman.entries.pacman.eiisolver.graph;

import java.util.*;

import static pacman.game.Constants.EAT_DISTANCE;
import static pacman.game.Constants.GHOST_EAT_SCORE;

import java.util.Random;

import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class Search {

    public static final boolean log = false;
    public static final boolean stopSearchWhenLogging = false;
    /**
     * If true, we use pacman evaluation function.
     */
    public static boolean pacmanEvaluation = true;
    public static EvaluationExtra evaluationExtra;
    /**
     * Absolute maximum value
     */
    public static final int MAX_VALUE = 100000;
    /**
     * score (for ghosts) when we know for sure pacman will die
     */
    public static final int PACMAN_DIES_VALUE = MAX_VALUE / 2;
    public static final int PACMAN_WILL_DIE = MAX_VALUE / 4;
    public static final int MAX_PLY = 1000;
    public static PlyInfo[] plyInfo = new PlyInfo[MAX_PLY];
    /**
     * True if pacman moves at even plies (and ghosts at odd plies)
     */
    public static boolean pacmanMovesFirst = false;
    /**
     * current ply that is being searched
     */
    private static int currDepth;
    public static Board b;
    public static JunctionGraph graph;
    public static Game game;
    public static Heuristics heuristics = new Heuristics();
    /**
     * shortcut for graph.nodes
     */
    public static Node[] nodes;
    /**
     * total nr of nodes searched
     */
    public static int nodesSearched = 0;
    public static Random rand = new Random();
    /**
     * If we reach this time, we must stop searching immediately
     */
    private static long emergencyStopTime;
    /**
     * true if the search stopped in the middle due to emergencyStopTime being
     * reached
     */
    private static boolean emergencyStopped;
    private static StaticEvaluator staticEvaluator = new StaticEvaluator();
    // helper variables used when calculating shortest path to all edible ghosts
    private static int[] edibleGhosts = new int[4];
    private static boolean[] edibleVisited = new boolean[4];
    /**
     * Helper variable, contains for every edible ghost the distance to the
     * closest non-edible ghost
     */
    private static int[] ghostToNearestGhostDist = new int[4];
    private static int[] pathLengths = new int[4];
    private static int nrEdibleGhosts = 0;
    /**
     * pacmanKillerMoves[node] contains indices into nodes[node].neighbours.
     */
    public static int[][] pacmanKillerMoves;
    /**
     * ghostKillerMoves[node][move] contains indices into
     * nodes[node].neighbours.
     */
    public static int[][][] ghostKillerMoves;
    /**
     * Used in debugging: see where pacman moved during a search
     */
    public static boolean[] pacmanVisited;
    /**
     * helper variable
     */
    private static boolean pacmanCanGetToPowerPill;
    /**
     * Called after every search iteration
     */
    public static Runnable searchIterationFinished;
    private static PlyInfo backup = new PlyInfo();
    /**
     * Targets assigned to ghosts during extended search
     */
    public static Target[][] ghostTargets = new Target[4][7];
    /**
     * Contains for every ghost the number of assigned targets during extended
     * search (0 means: follow pacman)
     */
    public static int[] nrGhostTargets = new int[4];
    /**
     * Contains during extended search edges that are closed; pacman does not
     * need to walk into these edges
     */
    public static BigEdge[] deadEdges = new BigEdge[10];
    public static int nrDeadEdges;
    public static int extendedSearchDepth = -1;
    private static BorderEdge[] path1 = new BorderEdge[10];
    private static int path1Length = 0;
    private static BorderEdge[] path2 = new BorderEdge[10];
    private static int path2Length = 0;
    public static long nrRetrievedStatic = 0;

    static {
        init();
    }

    private static void init() {
        for (int i = 0; i < plyInfo.length; ++i) {
            plyInfo[i] = new PlyInfo();
            if (i > 0) {
                plyInfo[i].prev = plyInfo[i - 1];
            }
        }
        for (int i = 0; i < ghostTargets.length; ++i) {
            for (int j = 0; j < ghostTargets[i].length; ++j) {
                ghostTargets[i][j] = new Target();
                ghostTargets[i][j].init();
            }
        }
    }

    public static void update(Board board, JunctionGraph newGraph, Game newGame) {
        b = board;
        graph = newGraph;
        nodes = graph.nodes;
        game = newGame;
        pacmanKillerMoves = new int[nodes.length][4];
        for (int n = 0; n < nodes.length; ++n) {
            for (int i = 0; i < nodes[n].nrNeighbours; ++i) {
                pacmanKillerMoves[n][i] = i;
            }
        }
        ghostKillerMoves = new int[nodes.length][MOVE.values().length][4];
        for (int n = 0; n < nodes.length; ++n) {
            for (MOVE m : MOVE.values()) {
                for (int i = 0; i < nodes[n].nrNeighbours; ++i) {
                    ghostKillerMoves[n][m.ordinal()][i] = i;
                }
            }
        }
        pacmanVisited = new boolean[nodes.length];
        staticEval2.update();
        TransposTable.clear();
    }

    public static void searchMove(Game newGame, long timeDue) {
        game = newGame;
        long startTime = System.currentTimeMillis();
        if (timeDue < 0) {
            timeDue = startTime + 40;
        }
        // just for performance measurement purposes
        for (int i = 0; i < plyInfo.length; ++i) {
            plyInfo[i].alpha = plyInfo[i].beta = 0;
        }
        Arrays.fill(pacmanVisited, false);
        Search.currDepth = 0;
        Search.nodesSearched = 0;
        nrRetrievedStatic = 0;
        PlyInfo p = plyInfo[0];
        p.alpha = -MAX_VALUE;
        p.beta = MAX_VALUE;
        p.score = 0;
        long normalStopTime = (startTime + timeDue) / 2;
        emergencyStopTime = startTime + 100000; // we want to search at least 1 ply without emergency stops
        emergencyStopped = false;
        boolean stop = false;
        p.budget = 20;
        heuristics.updateForNewMove(game, b);
        TransposTable.toggleMoveMask();
        boolean haveBackup = false;
        while (!stop) {
            p.budget += 10;
            if (log) {
                log("searchMove, budget = " + p.budget);
            }
            search();
            if (!emergencyStopped) {
                if (p.bestValue > -PACMAN_WILL_DIE) {
                    // save the search results
                    backup.copySearchResult(p);
                    haveBackup = true;
                }
                searchIterationFinished.run();
            }
            emergencyStopTime = timeDue - 7; // now set the real emergency stop time, with a little slack.
            long timeSpent = System.currentTimeMillis() - startTime;
            stop = Math.abs(p.bestValue) >= PACMAN_WILL_DIE
                    || startTime + timeSpent >= normalStopTime
                    || (log && stopSearchWhenLogging);
            // reduce max time if everything looks ok
            if (!stop && pacmanEvaluation && game.getCurrentLevel() > 1) {
                if (game.getPacmanNumberOfLivesRemaining() > 2) {
                    stop = timeSpent > 10 || p.nrSurvivingMoves <= 1;
                } else if (pacmanEvaluation && game.getPacmanNumberOfLivesRemaining() == 2) {
                    stop = p.nrSurvivingMoves <= 1;
                }
            }
        }
        if (emergencyStopped) {
            System.out.println("Search was emergency stopped");
            // search was stopped in the middle of a ply, cannot use search result
            // that currently is present in p.
            p.copySearchResult(backup);
            if (System.currentTimeMillis() >= timeDue) {
                System.err.println("TIME EXCEEDED");
            }
        } else if (p.bestValue <= -PACMAN_WILL_DIE && haveBackup) {
            // in our backup we did not loose, but in the last search we discovered that we
            // will loose anyway. We try to make it as hard as possible for the opponent,
            // so we choose the best move from the backup
            p.copySearchResult(backup);
            System.out.println("I will loose, select best move from backup");
        }
    }

    /**
     * Performs an alpha-beta search. Search depth is variable, continues until
     * p.budget < 0.
     */
    public static void search() {
        if (emergencyStopped) {
            if (log) {
                log("Emergency stop");
            }
            return;
        }
        if ((nodesSearched & 0x7f) == 0) {
            emergencyStopped = System.currentTimeMillis() >= emergencyStopTime;
        }
        ++nodesSearched;
        PlyInfo p = plyInfo[currDepth];
        if (log) {
            log("search " + currDepth + ", nodes = " + nodesSearched + " [" + p.alpha + ", " + p.beta + "], budget = " + p.budget);
        }
        p.bestValue = -MAX_VALUE;
        boolean evenPly = (currDepth & 1) == 0;
        boolean movePacman = evenPly == pacmanMovesFirst;
        p.moveScore = 0;
        p.hash = 0;
        // on even plies, check pacman/ghost alive status
        if (evenPly) {
            // check for dead pacman or ghosts
            int value = _feast(movePacman);
            if (Math.abs(value) >= PACMAN_DIES_VALUE) {
                p.bestValue = value;
                return;
            }
            p.score += value;
            staticEval2.clear();
            if (extendedSearchDepth > 0) {
                if (p.budget <= -60 || currDepth >= MAX_PLY - 4) {
                    // end of extended search reached; static check if pacman is in danger
                    value = checkPacmanHealth(p, movePacman);
                    TransposTable.storeStaticEval(p);
                    if (value >= PACMAN_WILL_DIE) {
                        p.bestValue = movePacman ? -value : value;
                        return;
                    }
                    evaluate(p, movePacman, value == 1);
                    return;
                }
            } else {
                boolean dropOutEarly = false;
                if (currDepth < 2 || plyInfo[currDepth - 2].nrPossibleMoves > 1 || plyInfo[currDepth - 1].nrPossibleMoves > 1) {
                    // static check if pacman is in danger
                    value = checkPacmanHealth(p, movePacman);
                    TransposTable.storeStaticEval(p);
                    if (value >= PACMAN_WILL_DIE && currDepth > 0) {
                        p.bestValue = movePacman ? -value : value;
                        return;
                    }
                    // If pacman is totally safe a few moves before the end we return before reaching budget 0.
                    if (currDepth > 20 && p.budget <= 30) {
                        dropOutEarly = staticEval2.nrBorders >= 8 || (staticEval2.nrBorders > 6 && staticEval2.hasCircles);
                    }
                    if (log && dropOutEarly) {
                        log("drop out early");
                    }
                }
                // if no budget left we do a static analysis and return.
                // But only on even plies.
                if (p.budget <= 0 || dropOutEarly) {
                    boolean extendSearch = evaluate(p, movePacman, value == 1);
                    if (extendSearch && !dropOutEarly) {
                        extendedSearchDepth = currDepth;
                        // reset p.bestValue
                        p.bestValue = -MAX_VALUE;
                        if (log) {
                            log("extend search");
                        }
                        setExtendedSearchTargets();
                    } else {
                        return;
                    }
                }
            }
        }
        boolean cutoff = false;
        boolean skipOpposite = false;
        if (movePacman) {
            Node pacmanNode = nodes[b.pacmanLocation];
            skipOpposite = currDepth >= 2 && plyInfo[currDepth - 1].nrPossibleMoves == 1
                    /*&& !plyInfo[currDepth-2].pillValue*/ && !plyInfo[currDepth - 2].powerPillValue
                    && !plyInfo[currDepth & ~1].ghostKilled && !pacmanNode.isJunction();
            if (skipOpposite) {
                if (pacmanNode.distToClosestJunction == 1) {
                    skipOpposite = !nodes[plyInfo[currDepth - 2].savedBoard.pacmanLocation].isJunction();
                } else {
                    skipOpposite = pacmanNode.skipOpposite;
                }
            }
            if (log && skipOpposite) {
                log("Skip opposite");
            }
        }
        p.initMove(movePacman, skipOpposite);
        // if we are lucky we can skip searching.
        if (p.nrPossibleMoves > 1) {
            if (TransposTable.retrieve(b, p, movePacman)) {
                if (log) {
                    if (movePacman) {
                        log("From transpos: pacman move, " + nodes[b.pacmanLocation].neighbourMoves[p.bestPacmanMove] + ", value: " + p.bestValue);
                    } else {
                        log("From transpos: ghost move , value: " + p.bestValue);
                    }
                }
                if (extendedSearchDepth == currDepth) {
                    extendedSearchDepth = -1;
                    if (ghostTargets[0][0].nrBackups > 0 || p.budget > 0) {
                        throw new RuntimeException("target.backups = " + ghostTargets[0][0].nrBackups);
                    }
                }
                return;
            }
            if (p.transpos != null) {
                p.setTransposMoveFirst(movePacman);
            }
        }
        if (extendedSearchDepth > 0) {
            if (movePacman) {
                p.filterDeadEnds();
            } else {
                // during extended search we only consider very few ghost moves
                p.filterTargetMoves();
            }
        }
        PlyInfo nextP = plyInfo[currDepth + 1];
        p.nrSurvivingMoves = 0;
        // loop through all moves
        while (!cutoff && p.nextMove(movePacman)) {
            if (log) {
                String onlyMove = p.nrPossibleMoves == 1 ? "; only move" : "";
                if (movePacman) {
                    log("pacman move, " + p.moveToString(movePacman) + ", p.score: " + p.score + onlyMove);
                } else {
                    log("ghost move " + p.moveToString(movePacman) + onlyMove);
                }
            }
            int value = 0;
            p.move(movePacman);
            pacmanVisited[b.pacmanLocation] = true;
            if (log && movePacman && p.moveScore != 0) {
                log("moveScore: " + p.moveScore);
            }
            nextP.alpha = -p.beta;
            nextP.beta = -((p.alpha) > (p.bestValue) ? (p.alpha)
                    : (p.bestValue));
            // determine cost for this ply
            int cost = 10;
            if (p.nrPossibleMoves == 1) {
                cost = 0;
            }
            nextP.budget = p.budget - cost;
            nextP.score = p.score + p.moveScore;
            ++currDepth;
            search();
            --currDepth;
            value = -nextP.bestValue;
            p.unmove(movePacman);
            if (value > p.bestValue) {
                if (log) {
                    log("New best value: " + value);
                }
                p.bestValue = value;
                p.saveBestMove(movePacman);
                if (value >= p.beta) {
                    cutoff = true;
                    if (log) {
                        log("cutoff!");
                    }
                }
            } else if (log) {
                if (log) {
                    log("search returned " + value + ", best value = " + p.bestValue);
                }
            }
            if (value > -PACMAN_WILL_DIE) {
                ++p.nrSurvivingMoves;
            }
        }
        if (!emergencyStopped) {
            if (p.nrPossibleMoves > 1) {
                updateKillerMoves(movePacman);
                TransposTable.store(b, p, movePacman);
            }
        }
        if (!movePacman && extendedSearchDepth > 0) {
            p.restoreTargets();
        }
        if (extendedSearchDepth == currDepth) {
            extendedSearchDepth = -1;
            if (ghostTargets[0][0].nrBackups > 0 || p.budget > 0) {
                if (log) {
                    log("ERROR: target.backups = " + ghostTargets[0][0].nrBackups + ", budget=" + p.budget);
                }
                System.err.println("ERROR: target.backups = " + ghostTargets[0][0].nrBackups + ", budget=" + p.budget);
                //throw new RuntimeException("target.backups = " + ghostTargets[0][0].nrBackups);
            }
        }
    }

    /**
     * (only for performance measurement)
     *
     * @return
     */
    public static int deepestSearchedPly() {
        for (int i = 0; i < plyInfo.length; ++i) {
            if (plyInfo[i].alpha == 0 && plyInfo[i].beta == 0) {
                return i - 1;
            }
        }
        return -1;
    }

    /**
     * Returns the time it will take the given ghost to travel dist. Edible
     * ghosts take longer time, and if they are still edible after having
     * travelled dist, a long time will be returned.
     *
     * @param ghost
     * @param dist
     * @return
     */
    private static int ghostDist(MyGhost ghost, int dist) {
        if (ghost.edibleTime == 0) {
            return dist;
        }
        if ((pacmanEvaluation && heuristics.isWeakOpponent()) || ghost.edibleTime > dist + dist) {
            return 1000;
        }
        return dist + ghost.edibleTime / 2;
    }

    /**
     * Performs a static evaluation, and sets p.bestValue
     *
     * @param p
     * @param movePacman
     * @return true if extended searching is recommended (because the situation
     * looks dangerous for pacman)
     */
    private static boolean evaluate(PlyInfo p, boolean movePacman, boolean mustTakePowerPillToSurvive) {
        boolean extendSearch = false;
        int graphBonus = 400;
        if (!pacmanCanGetToPowerPill || !pacmanEvaluation || heuristics.getPowerPillScore() < 0) {
            calcBorderEdges(p, movePacman);
            if (staticEval2.nrBorders < 8 && staticEval2.nrPacmanNodes < 8) {
                boolean wouldDieWithoutPowerPill = staticEval2.wouldDieWithoutPowerPill();
                if (wouldDieWithoutPowerPill && pacmanCanGetToPowerPill) {
                    if (log) {
                        log("mustTakePowerPillToSurvive");
                    }
                    mustTakePowerPillToSurvive = true;
                }
                /*if (!staticEval2.matchCalled && staticEval2.nrBorders <= 6) {
                 staticEval2.match();
                 if (staticEval2.match() && pacmanCanGetToPowerPill) {
                 if(log)log("mustTakePowerPillToSurvive");
                 mustTakePowerPillToSurvive = true;
                 }
                 }*/
                int nrInvolved = staticEval2.getNrInvolvedGhosts();
                graphBonus = 20 * staticEval2.nrBorders + 20 * staticEval2.nrPacmanNodes - 30 * nrInvolved;
                if (staticEval2.hasCircles) {
                    graphBonus += 80;
                    extendSearch = !wouldDieWithoutPowerPill && nrInvolved >= staticEval2.nrBorders - 1;
                } else if (staticEval2.nrBorders - nrInvolved <= 2) {
                    graphBonus -= 100;
                    extendSearch = !wouldDieWithoutPowerPill;
                }
                /*if (staticEval2.match()) {
                 if (staticEval2.canReachPowerPill) {
                 graphBonus += heuristics.getPowerPillScore()/4;
                 } else {
                 graphBonus -= 1000;
                 }
                 }*/
            } else {
                if (!pacmanEvaluation) {
                    graphBonus = 300 + 20 * calcNrJunctionsClosestToPacman();
                } else {
                    graphBonus = 500;
                }
            }
        }
        // calculate distance of ghost that is nearest to pacman
        // + try to maximize distance to pacman for edible ghosts
        int closestDist = 400;
        int farAwayBonus = 0;
        nrEdibleGhosts = 0;
        int nrInLair = 0;
        int maxEdibleTime = 0;
        for (MyGhost ghost : b.ghosts) {
            if (ghost.lairTime == 0) {
                //int dist = graph.getGhostDistToJunction(ghost.currentNodeIndex, ghost.lastMoveMade, nextPacmanJunction.index, nextPacmanJunction.neighbourMoves[0]);
                int dist = game.getShortestPathDistance(b.pacmanLocation, ghost.currentNodeIndex);
                if (ghost.edibleTime > 0) {
                    edibleGhosts[nrEdibleGhosts] = ghost.currentNodeIndex;
                    edibleVisited[nrEdibleGhosts] = false;
                    ++nrEdibleGhosts;
                    if (ghost.edibleTime > maxEdibleTime) {
                        maxEdibleTime = ghost.edibleTime;
                    }
                } else if (ghost.canKill()) {
                    if (dist < closestDist) {
                        closestDist = dist;
                    }
                    if (dist > 40) {
                        farAwayBonus += 5 * (dist - 40);
                    }
                }
            } else {
                ++nrInLair;
            }
        }
        // calculate shortest path to eat all edible ghosts, assuming pacman is greedy;
        // first moves to closest ghost, then to next, etc.
        int edibleBonus = 0;
        int longestDist = 0;
        if (nrEdibleGhosts > 0) {
            if (log) {
                log("nrEdible = " + nrEdibleGhosts);
            }
            /*if (pacmanEvaluation) {
             // pacman will move to closest edible ghost
             int shortestDist = 500;
             int nearestGhost = -1;
             for (int g = 0; g < nrEdibleGhosts; ++g) {
             int dist = game.getShortestPathDistance(b.pacmanLocation, edibleGhosts[g]);
             dist += dist/4; // assume ghost moves away
             if (dist < shortestDist) {
             boolean killingGhostIsCloser = false;
             for (MyGhost ghost : b.ghosts) {
             if (ghost.canKill()) {
             if (game.getShortestPathDistance(ghost.currentNodeIndex, edibleGhosts[g]) < shortestDist) {
             if(log)log("killing ghost is closer ");
             killingGhostIsCloser = true;
             break;
             }
             }
             }
             if (killingGhostIsCloser) {
             shortestDist += 30;
             }
             if (dist < shortestDist) {
             shortestDist = dist;
             nearestGhost = g;
             }
             }
             if (dist > longestDist) {
             longestDist = dist;
             }
             }
             if(log)log("nearest ghost: " + shortestDist + ", maxEdible = " + maxEdibleTime);
             if (shortestDist < maxEdibleTime) {
             edibleBonus = 10*GHOST_EAT_SCORE-25*shortestDist;
             if(log)log("edible bonus: " + edibleBonus);
             }
				
             } else {*/
            // calculate distance from edible ghosts to non-edible ghosts
            for (int g = 0; g < nrEdibleGhosts; ++g) {
                ghostToNearestGhostDist[g] = 1000;
                for (MyGhost ghost : b.ghosts) {
                    if (ghost.edibleTime == 0) {
                        int dist;
                        if (ghost.lairTime == 0) {
                            dist = game.getShortestPathDistance(ghost.currentNodeIndex, edibleGhosts[g]);
                        } else {
                            dist = ghost.lairTime + game.getShortestPathDistance(game.getGhostInitialNodeIndex(), edibleGhosts[g]);
                        }
                        if (dist < ghostToNearestGhostDist[g]) {
                            ghostToNearestGhostDist[g] = dist;
                        }
                    }
                }
            }
            // ghosts will try to spread out
            int pathLength = 0; // will contain the length of the path that eats all ghosts
            int distToGhost1 = 1000;
            int currNode = b.pacmanLocation;
            int nrGhostsInRange;
            for (nrGhostsInRange = 0; nrGhostsInRange < nrEdibleGhosts; ++nrGhostsInRange) {
                int shortestDist = 500;
                int nearestGhost = -1;
                for (int g = 0; g < nrEdibleGhosts; ++g) {
                    if (!edibleVisited[g]) {
                        int dist = game.getShortestPathDistance(currNode, edibleGhosts[g]);
                        if (dist < shortestDist) {
                            if (ghostToNearestGhostDist[g] + 2 <= dist) {
                                dist += 30; // can this be improved?
                            }
                            if (dist < shortestDist) {
                                shortestDist = dist;
                                nearestGhost = g;
                            }
                        }
                    }
                }
                pathLength += shortestDist;
                pathLengths[nrGhostsInRange] = pathLength;
                if (currNode == b.pacmanLocation) {
                    distToGhost1 = shortestDist;
                }
                currNode = edibleGhosts[nearestGhost];
                edibleVisited[nearestGhost] = true;
                if (pathLength > maxEdibleTime) {
                    break;
                }
            }
            if (nrGhostsInRange == 0) {
                edibleBonus = 0;
            } else {
                if (true || pacmanEvaluation) {
                    int maxScore = 8 * GHOST_EAT_SCORE;
                    int threshold = maxEdibleTime / 2;
                    if (maxEdibleTime - threshold < 2) {
                        threshold = maxEdibleTime;
                    }
                    for (int i = 0; i < nrGhostsInRange; ++i) {
                        int bonus;
                        if (pathLengths[i] <= threshold) {
                            bonus = maxScore - 2 * pathLengths[i];
                        } else {
                            bonus = GHOST_EAT_SCORE + ((maxScore - GHOST_EAT_SCORE) * (pathLengths[i] - threshold)) / (maxEdibleTime - threshold) - 2 * pathLengths[i];
                        }
                        if (bonus >= 0) {
                            edibleBonus += bonus;
                        }
                        if (log) {
                            log("g: " + i + ", bonus: " + bonus + ", pathLen: " + pathLengths[i] + ", total: " + edibleBonus);
                        }
                    }
                    //edibleBonus = 6*GHOST_EAT_SCORE*nrGhostsInRange - 10*distToGhost1 -5*pathLength ;
                } else {
                    edibleBonus = 1000 - 5 * distToGhost1 - 5 * pathLength + 300 * nrGhostsInRange;
                }
            }
            if (log) {
                log("pathLenght: " + pathLength + ", nrGhostsInRange: " + nrGhostsInRange
                        + ",edibleBonus: " + edibleBonus + ", distToGhost1: " + distToGhost1 + ", pathLen: " + pathLength + ", edibleTime: " + maxEdibleTime);
            }
            //}
        }
        // being on a long big edge is potentially dangerous
        int edgeLength;
        Node pacmanNode = graph.nodes[b.pacmanLocation];
        if (pacmanNode.isJunction()) {
            // junction: edge length is minimum length of connected edges
            edgeLength = pacmanNode.edges[0].length;
            for (int i = 1; i < pacmanNode.nrEdges; ++i) {
                if (pacmanNode.edges[i].length < edgeLength) {
                    edgeLength = pacmanNode.edges[i].length;
                }
            }
        } else {
            edgeLength = pacmanNode.edge.length;
        }
        // value is relative to pacman (positive value is good for pacman)
        int value = p.score + edibleBonus;
        if (pacmanEvaluation) {
            if (heuristics.hasManyLivesLeft()) {
                // many lives left, we can take a risk and try to optimize score
                if (graphBonus < 0) {
                    value += 4 * graphBonus;
                }
                if (heuristics.isWeakOpponent()) {
                    // we want to be close to our opponent
                    if (closestDist < 300) {
                        value += 250 - 3 * closestDist;
                    }
                    if (edibleBonus == 0) {
                        value -= farAwayBonus; // we want pacman to be close to all ghosts if they are weak
                    }
                }
            } else {
                value += 4 * graphBonus;
                if (mustTakePowerPillToSurvive && heuristics.getPowerPillScore() < 0) {
                    value += heuristics.getPowerPillScore() / 2 - 300 + 2 * (currDepth + distToClosestPowerPill());
                }
                if (pacmanCanGetToPowerPill) {
                    value += 300;
                }
            }
            /*if (nrInLair >= 3 && nrEdibleGhosts == 0 && game.getCurrentLevel() == 0) {
             System.out.println("nrlair");
             value = 80*(60-game.getShortestPathDistance(b.pacmanLocation, game.getGhostInitialNodeIndex()));
             }*/
            value += rand.nextInt(4);
        } else {
            value = graphBonus + 5 * closestDist + farAwayBonus + rand.nextInt(5)
                    + (p.score + edibleBonus) / 8;
            if (heuristics.isWeakOpponent()) {
                if (nrEdibleGhosts == 0) {
                    if (pacmanCanGetToPowerPill) {
                        value += killingGhostEvaluation();
                        if (log) {
                            log("killingGhostEval: " + value);
                        }
                    } else {
                        //value = graphBonus;
                        if (log) {
                            log("weak pacman, not to powerpill: value = " + value);
                        }
                    }
                }
                // keep some ghosts far away
                //value -= 100*farAwayBonus;
            }
        }
        p.bestValue = value;
        // hook to add some ugly extra evaluation stuff
        if (evaluationExtra != null && Math.abs(value) < PACMAN_WILL_DIE / 2) {
            evaluationExtra.evaluateExtra(p);
        }
        if (!movePacman) {
            p.bestValue = -value;
        }
        if (log) {
            log("eval: value = " + p.bestValue + ", graphBonus: " + graphBonus
                    + ", far: " + farAwayBonus + ", close: " + closestDist
                    + (edibleBonus != 0 ? "" : ", edible: " + edibleBonus)
                    + ", score: " + p.score);
        }
        return extendSearch;
    }

    /**
     * Evaluation of ghosts to be used when there are no edible ghosts, and
     * pacman is closer to a power pill than any ghost
     *
     * @return positive value is good for pacman
     */
    private static int killingGhostEvaluation() {
        // calculate which pill is closest to pacman, and how far it is away
        int closestPillDist = 10000;
        int closestPillLocation = -1;
        for (int i = 0; i < b.nrPowerPills; ++i) {
            int powerPill = b.powerPillLocation[i];
            if (b.containsPowerPill[powerPill]) {
                int pacmanDist = game.getShortestPathDistance(b.pacmanLocation, powerPill);
                if (pacmanDist < closestPillDist) {
                    closestPillDist = pacmanDist;
                    closestPillLocation = powerPill;
                }
            }
        }
        if (closestPillLocation < 0) {
            return 0;
        }
        // the closer pacman is to the pill, the further away we want the ghosts to be.
        // Preferably the ghosts are out of edible range. But 1 ghost needs to be close
        // so when eaten, it gets out of the lair as quickly as possible
        int closestDist = 1000;
        int safeDist = 150;//Math.min(160, b.currentEdibleTime * 7)/8;
        int farAwayFee = 0;
        boolean isGhostInLair = false;
        int biggestDist = 0; // distance of the farthest ghost
        for (MyGhost ghost : b.ghosts) {
            int dist;
            if (ghost.lairTime == 0) {
                dist = ghostDist(ghost, game.getShortestPathDistance(ghost.currentNodeIndex, closestPillLocation));
                if (dist + closestPillDist > biggestDist) {
                    biggestDist = dist + closestPillDist;
                }
                int margin = Math.max(0, safeDist - (dist + closestPillDist));
                if (dist < closestDist) {
                    if (closestDist < 1000) {
                        farAwayFee += Math.max(0, safeDist - (closestDist/* + closestPillDist*/));
                    }
                    closestDist = dist;
                } else {
                    farAwayFee += margin;
                }
            } else {
                isGhostInLair = true;
            }
        }
        int value = 0;
        if (closestDist == 1000 || isGhostInLair || biggestDist >= safeDist) {
            value = 0;// all ghosts in lair
        } else {
            value = 40 * (safeDist - biggestDist);
        }
        if (pacmanCanGetToPowerPill) {
            value += 100;
        }
        return value;
        //if(log)log("killingGhosts: farAwayFee = " + farAwayFee + ", closestDist = " + closestDist);
        //return -800 +5*farAwayFee + 10*closestDist;
    }

    private static int calcNrJunctionsClosestToPacman() {
        // calculate how many junctions are closer to pacman than to any ghost
        int nrJunctionsClosestToPacman = 0;
        for (Node n : graph.junctionNodes) {
            int pacmanDist = game.getShortestPathDistance(b.pacmanLocation, n.index);
            boolean pacmanIsClosest = true;
            for (MyGhost ghost : b.ghosts) {
                if (ghost.lairTime == 0) {
                    int ghostDist = ghostDist(ghost, game.getShortestPathDistance(ghost.currentNodeIndex, n.index));
                    if (ghostDist + EAT_DISTANCE < pacmanDist) {
                        pacmanIsClosest = false;
                        break;
                    }
                }
            }
            if (pacmanIsClosest) {
                ++nrJunctionsClosestToPacman;
            }
        }
        return nrJunctionsClosestToPacman;

    }

    /**
     * Returns distance to the ghost that is farthest away, or -1 if there is a
     * ghost in the lair or an edible ghost.
     *
     * @return
     */
    public static int distToFarthestGhostInTrain() {
        int dist = 0;
        for (MyGhost ghost : b.ghosts) {
            if (ghost.canKill()) {
                int ghostDist = game.getShortestPathDistance(ghost.currentNodeIndex, b.pacmanLocation);
                if (ghostDist > dist) {
                    dist = ghostDist;
                }
            } else {
                return -1;
            }
        }
        return dist;
    }

    /**
     * (for test purposes)
     *
     * @return
     */
    public static int checkPacmanHealth() {
        PlyInfo p = new PlyInfo();
        p.hash = 0;//b.getHash(true);
        return checkPacmanHealth(p, true);
    }

    /**
     * Checks if pacman will survive
     *
     * @param p
     * @param movePacman
     * @return 0 if pacman survives, 1 if pacman survives but must take a power
     * pill, or a high value if pacman will die (the higher value, the less time
     * it will take for the ghosts to capture pacman)
     */
    public static int checkPacmanHealth(PlyInfo p, boolean movePacman) {
        pacmanCanGetToPowerPill = false;
        calcBorderEdges(p, movePacman);
        if (staticEval2.resultFromCache) {
            pacmanCanGetToPowerPill = staticEval2.canReachPowerPill;
            return staticEval2.pacmanHealth;
        }
        //if (staticEval2.nrBorders <= 6 
        //		&& staticEval2.nrPacmanNodes <= 4 && staticEval2.match()) {
        if (staticEval2.wouldDieWithoutPowerPill()) {
            pacmanCanGetToPowerPill = staticEval2.canReachPowerPill;
            if (pacmanCanGetToPowerPill) {
                // pacman would normally die, but fortunately it can reach a power pill
                if (log) {
                    log("pacman dies but can take power pill");
                }
                staticEval2.pacmanHealth = 1;
                return 1;
            }
            // pacman will die!
            // try to make it as difficult for the ghosts as possible to find the correct path.
            int longestDist = 0;
            int longestPacmanDist = 0;
            for (int i = 0; i < staticEval2.nrBorders; ++i) {
                BorderEdge borderEdge = staticEval2.borders[i];
                int closestGhostDist = 10000;
                for (int g = 0; g < borderEdge.ghostDist.length; ++g) {
                    if (borderEdge.ghostDist[g] < closestGhostDist) {
                        closestGhostDist = borderEdge.ghostDist[g];
                    }
                }
                if (closestGhostDist > longestDist) {
                    longestDist = closestGhostDist;
                }
                if (borderEdge.pacmanDist > longestPacmanDist) {
                    longestPacmanDist = borderEdge.pacmanDist;
                }
            }
            int difficulty = 10 * staticEval2.nrBorders + 8 * staticEval2.nrPacmanNodes + longestDist + longestPacmanDist;
            staticEval2.pacmanHealth = PACMAN_WILL_DIE + 5000 - difficulty - currDepth - game.getCurrentLevelTime();
            return staticEval2.pacmanHealth;
        }
        staticEval2.pacmanHealth = 0;
        return staticEval2.pacmanHealth;

        /*boolean pacmanDies = false;
         int difficulty = 0;
         if (pacmanNode.isJunction()) {
         difficulty = 10;
         staticEvaluator.nrJunctions = pacmanNode.nrNeighbours;
         for (int i = 0; i < pacmanNode.nrNeighbours; ++i) {
         BigEdge edge = nodes[pacmanNode.neighbours[i]].edge;
         Node otherJunction = edge.getOtherJunction(pacmanNode);
         staticEvaluator.edges[i] = edge;
         staticEvaluator.junctions[i] = otherJunction.index;
         staticEvaluator.viaJunctions[i] = otherJunction.index;
         }
         pacmanDies = staticEvaluator.checkPacmanHealth("pacman on junction");
         } else {
         // check if there are two other ghosts on the same edge as pacman, attacking from 
         // both sides
         difficulty = 0;
         BigEdge pacmanEdge = pacmanNode.edge;
         pacmanDies = staticEvaluator.checkPacmanHealth(pacmanEdge.endpoints[0].index, pacmanEdge, 
         pacmanEdge.endpoints[1].index, pacmanEdge, "direct pacman edge");
         if (!pacmanDies) {
         difficulty = 40;
         pacmanDies = checkPacmanEdgeJunction(pacmanEdge.endpoints[0].index);
         }
         if (!pacmanDies) {
         pacmanDies = checkPacmanEdgeJunction(pacmanEdge.endpoints[1].index);
         }
         if (!pacmanDies && pacmanEdge.endpoints[0].nrNeighbours + pacmanEdge.endpoints[1].nrNeighbours <= 6) {
         // pacman will die if ghosts are closer to the 4 junctions that are 2 junctions away from pacman
         difficulty = 60;
         int nrJunctions = 0;
         for (int e = 0; e < 2; ++e) {
         Node junction = pacmanEdge.endpoints[e];
         for (int i = 0; i < junction.nrNeighbours; ++i) {
         BigEdge edge = junction.edges[i];
         if (edge != pacmanEdge) {
         // otherJunction lies 2 junctions away
         Node otherJunction = edge.getOtherJunction(pacmanEdge.endpoints[e]);
         staticEvaluator.edges[nrJunctions] = edge;
         staticEvaluator.junctions[nrJunctions] = otherJunction.index;
         staticEvaluator.viaJunctions[nrJunctions] = junction.index;
         ++nrJunctions;
         }
         }
         }
         staticEvaluator.nrJunctions = nrJunctions;
         pacmanDies = staticEvaluator.checkPacmanHealth("pacman 2 junctions away");
         }
         }
         return pacmanDies ? PACMAN_WILL_DIE + 2000 - difficulty - currDepth : 0;*/
    }

    private static boolean checkPacmanEdgeJunction(int junction) {
        Node pacmanNode = graph.nodes[b.pacmanLocation];
        BigEdge pacmanEdge = pacmanNode.edge;
        Node junction2 = pacmanEdge.getOtherJunction(nodes[junction]);
        staticEvaluator.edges[0] = pacmanEdge;
        staticEvaluator.junctions[0] = junction;
        staticEvaluator.viaJunctions[0] = junction;
        int nrJunctions = 1;
        for (int i = 0; i < junction2.nrNeighbours; ++i) {
            BigEdge edge = nodes[junction2.neighbours[i]].edge;
            if (edge != pacmanEdge) {
                Node otherJunction = edge.getOtherJunction(junction2);
                staticEvaluator.edges[nrJunctions] = edge;
                staticEvaluator.junctions[nrJunctions] = otherJunction.index;
                staticEvaluator.viaJunctions[nrJunctions] = junction2.index;
                staticEvaluator.firstMoveFromJunctions[nrJunctions] = edge.getFirstMove(otherJunction);
                ++nrJunctions;
            }
        }
        staticEvaluator.nrJunctions = nrJunctions;
        boolean pacmanDies = staticEvaluator.checkPacmanHealth("pacman edge/junctions");
        return pacmanDies;
    }

    /**
     * Checks for dead pacman or dead ghosts. Returns score points for killed
     * ghosts. (stolen from Game._feast)
     *
     * @param movePacman
     * @return
     */
    private static int _feast(boolean movePacman) {
        int score = 0;
        plyInfo[currDepth].ghostKilled = false;
        for (MyGhost ghost : b.ghosts) {
            if (ghost.lairTime == 0) {
                int ghostEatMultiplier = 1;
                int distance = game.getShortestPathDistance(b.pacmanLocation,
                        ghost.currentNodeIndex);

                if (distance <= EAT_DISTANCE && distance != -1) {
                    if (ghost.edibleTime > 0) {
                        // pac-man eats ghost
                        if (log) {
                            log("ghost dies, within eat distance");
                        }
                        score -= 10 * GHOST_EAT_SCORE * ghostEatMultiplier;
                        ghostEatMultiplier *= 2;
                        ghost.edibleTime = 0;
                        ghost.lairTime = b.currentEdibleTime;
                        ghost.currentNodeIndex = -1;// currentMaze.lairNodeIndex;
                        ghost.lastMoveMade = MOVE.NEUTRAL;
                        plyInfo[currDepth].ghostKilled = true;
                    } else {
                        // ghost eats pac-man
                        score = PACMAN_DIES_VALUE + 2500 - currDepth;
                        if (log) {
                            log("pacman dies, within eat distance");
                        }
                    }
                }
            }
        }
        if (score != PACMAN_DIES_VALUE && b.nrPillsOnBoard == 0 && b.nrPowerPillsOnBoard == 0) {
            // pacman has eaten all pills, to next level
            score -= PACMAN_DIES_VALUE;
        }
        if (movePacman) {
            score = -score;
        }
        return score;
    }

    /**
     * Returns true if we can skip the pacman move to destLocation because it
     * would be towards a ghost on the same edge.
     *
     * @param destLocation
     * @return
     */
    public static boolean skipMoveTowardsGhost(int destLocation) {
        if (currDepth == 0) {
            return false;
        }
        PlyInfo p = plyInfo[currDepth];
        Node destNode = nodes[destLocation];
        boolean skip = false;
        if (!destNode.isJunction()) {
            BigEdge edge = destNode.edge;
            for (int g = 0; g < b.ghosts.length; ++g) {
                MyGhost ghost = b.ghosts[g];
                if (ghost.canKill()) {
                    Node ghostNode = nodes[ghost.currentNodeIndex];
                    if (ghostNode.edge == edge && ghostNode.isOnPath(destNode, ghost.lastMoveMade)) {
                        int pacmanDist = game.getShortestPathDistance(b.pacmanLocation, ghost.currentNodeIndex);
                        int newDist = Math.abs(destNode.edgeIndex - ghostNode.edgeIndex);
                        if (newDist < pacmanDist) {
                            // the move is towards the ghost; check if there is something interesting on the
                            // path halfway to the ghost
                            int middle = (destNode.edgeIndex + ghostNode.edgeIndex) / 2;
                            int step = middle >= destNode.edgeIndex ? 1 : -1;
                            for (int i = destNode.edgeIndex; i != middle; i += step) {
                                Node n = edge.internalNodes[i];
                                if (b.containsPill[n.index] || b.containsPowerPill[n.index]) {
                                    return false;
                                }
                            }
                            skip = true; // no, nothing interesting
                        }
                    }
                } else if (ghost.lairTime == 0 && nodes[ghost.currentNodeIndex].edge == edge) {
                    // edible ghost is on the path; don't skip under any condition
                    p.moveTowardsGhostSkipped = false;
                    return false;
                }
            }
        }
        p.moveTowardsGhostSkipped = skip;
        return skip;
    }

    private static void updateKillerMoves(boolean movePacman) {
        PlyInfo p = plyInfo[currDepth];
        if (p.nrPossibleMoves <= 1) {
            return;
        }
        if (movePacman) {
            int[] moveIndices = pacmanKillerMoves[b.pacmanLocation];
            int j;
            for (j = 0; moveIndices[j] != p.bestPacmanMove; ++j) {
            }
            for (; j > 0; --j) {
                moveIndices[j] = moveIndices[j - 1];
            }
            moveIndices[0] = p.bestPacmanMove;
        } else {
            for (int g = 0; g < b.ghosts.length; ++g) {
                MyGhost ghost = b.ghosts[g];
                if (p.bestGhostMove[g] >= 0) {
                    int[] moveIndices = ghostKillerMoves[ghost.currentNodeIndex][ghost.lastMoveMade.ordinal()];
                    int j;
                    for (j = 0; moveIndices[j] != p.bestGhostMove[g]; ++j) {
                    }
                    for (; j > 0; --j) {
                        moveIndices[j] = moveIndices[j - 1];
                    }
                    moveIndices[0] = p.bestGhostMove[g];
                }
            }
        }
    }

    /**
     * Checks if pacman is closer to a power pill than any ghost, just by
     * looking at shortestDist. If true is returned, it is 100% certain that
     * pacman can reach a powerpill, but if false is returned the result may be
     * inaccurate, it may in fact be that the closer ghost(s) cannot stop pacman
     * from taking the pill.
     *
     * @return
     */
    private static boolean canGetToPowerPillQuickAndDirty() {
        // check if pacman can get safely to a power pill
        boolean canReachPowerPill = false;
        for (int i = 0; i < b.nrPowerPills; ++i) {
            int powerPill = b.powerPillLocation[i];
            if (b.containsPowerPill[powerPill]) {
                int pacmanDist = game.getShortestPathDistance(b.pacmanLocation, powerPill);
                int ghostDist = 100000;
                for (MyGhost ghost : b.ghosts) {
                    int dist;
                    if (ghost.lairTime == 0) {
                        dist = ghostDist(ghost, game.getShortestPathDistance(ghost.currentNodeIndex, powerPill));
                    } else {
                        dist = ghost.lairTime + game.getShortestPathDistance(game.getGhostInitialNodeIndex(), powerPill);
                    }
                    if (dist < ghostDist) {
                        ghostDist = dist;
                    }
                }
                if (pacmanDist + EAT_DISTANCE < ghostDist) {
                    // we have found a power pill that is closer to pacman than to any ghost.
                    // pacman is safe.
                    canReachPowerPill = true;
                }
            }
        }
        return canReachPowerPill;
    }

    /**
     * Returns pacman's distance to the closest power pill
     */
    private static int distToClosestPowerPill() {
        int dist = 1000;
        for (int i = 0; i < b.nrPowerPills; ++i) {
            int powerPill = b.powerPillLocation[i];
            if (b.containsPowerPill[powerPill]) {
                int pacmanDist = game.getShortestPathDistance(b.pacmanLocation, powerPill);
                if (pacmanDist < dist) {
                    dist = pacmanDist;
                }
            }
        }
        return dist;
    }

    public static void log(String msg) {
        if (!log) {
            System.err.println("Should not be called: log " + msg);
        }
        for (int i = 0; i < currDepth; ++i) {
            Log.print("   ");
        }
        Log.print(currDepth + " ");
        Log.println(msg);
    }

    // ############################################################################
    // StaticEvaluator
    // ############################################################################
    /**
     * Helper class used to check statically if pacman can escape via a set of
     * provided junctions.
     *
     * @author louis
     *
     */
    private static class StaticEvaluator {

        /**
         * junctions of interest
         */
        public int[] junctions = new int[4];
        /**
         * via which junction
         */
        public int[] viaJunctions = new int[4];
        /**
         * edges of interest
         */
        public BigEdge[] edges = new BigEdge[4];
        public int nrJunctions;
        /**
         * The first move from the junction to go towards pacman
         */
        private MOVE[] firstMoveFromJunctions = new MOVE[4];
        //boolean[] includedGhosts = new boolean[GHOST.values().length];
        private int[] pacmanDist = new int[4];
        /**
         * ghostIsCloser[ghost] contains bitmap, if bit set is true -> ghost is
         * closer to that junction than pacman
         */
        private int[] ghostIsCloser;

        public boolean checkPacmanHealth(int junction1, BigEdge edge1, int junction2, BigEdge edge2, String logMsg) {
            nrJunctions = 2;
            junctions[0] = junction1;
            viaJunctions[0] = junction1;
            junctions[1] = junction2;
            viaJunctions[1] = junction2;
            edges[0] = edge1;
            edges[1] = edge2;
            return checkPacmanHealth(logMsg);
        }

        /**
         * Returns true if pacman will die.
         *
         * @param logMsg
         * @return
         */
        public boolean checkPacmanHealth(String logMsg) {
            Node pacmanNode = nodes[b.pacmanLocation];
            if (log) {
                log("checkPacmanHealth " + logMsg + ", pacman at " + pacmanNode);
            }
            for (int i = 0; i < nrJunctions; ++i) {
                if (pacmanNode.isJunction()) {
                    pacmanDist[i] = game.getShortestPathDistance(b.pacmanLocation, junctions[i]) + EAT_DISTANCE;
                } else {
                    pacmanDist[i] = pacmanNode.edge.getDistanceToJunction(pacmanNode, nodes[viaJunctions[i]]) + EAT_DISTANCE;
                    if (viaJunctions[i] != junctions[i]) {
                        pacmanDist[i] += game.getShortestPathDistance(viaJunctions[i], junctions[i]);
                    }
                }
                firstMoveFromJunctions[i] = edges[i].getFirstMove(nodes[junctions[i]]);
                if (log) {
                    log(i + ": " + nodes[junctions[i]] + ", pacman dist " + pacmanDist[i]);
                }
            }
            ghostIsCloser = new int[GHOST.values().length];
            for (int j = 0; j < nrJunctions; ++j) {
                boolean someGhostIsCloser = false;
                for (int g = 0; g < b.ghosts.length; ++g) {
                    MyGhost ghost = b.ghosts[g];
                    if (ghost.lairTime == 0) {
                        Node ghostNode = nodes[ghost.currentNodeIndex];
                        if (ghostNode.edge == edges[j] && ghostNode.getNextJunction(ghost.lastMoveMade) != junctions[j]) {
                            // ghost is already on the edge, on the move to pacman. Pacman cannot escape via this junction
                            // unless ghost is on same edge as pacman and already past pacman.
                            if (pacmanNode.edge != ghostNode.edge || ghostNode.isOnPath(pacmanNode, ghost.lastMoveMade)) {
                                ghostIsCloser[g] |= 1 << j;
                                someGhostIsCloser = true;
                                if (log) {
                                    log(j + ": ghost on same edge, " + ghostNode);
                                }
                            }
                        } else {
                            int dist1 = ghostDist(ghost, game.getShortestPathDistance(ghost.currentNodeIndex, junctions[j]));
                            if (dist1 <= pacmanDist[j]) {
                                // ghost is closer, but can it also move to the junction
                                int dist = ghostDist(ghost, graph.getGhostDistToJunction(ghost.currentNodeIndex, ghost.lastMoveMade,
                                        junctions[j], firstMoveFromJunctions[j]));
                                if (dist <= pacmanDist[j]) {
                                    ghostIsCloser[g] |= 1 << j;
                                    someGhostIsCloser = true;
                                    if (log) {
                                        log(j + ": closer ghost: " + ghostNode + ", dist: " + dist);
                                    }
                                } else {
                                    if (log) {
                                        log(j + ": ghost " + ghostNode + " shortest dist = " + dist1 + ", real dist: " + dist);
                                    }
                                }
                            }
                        }
                    }
                }
                if (!someGhostIsCloser) {
                    return false; // pacman can escape safely to junction j
                }
            }
            if (log) {
                log("ghosts are closer; check for match");
            }
            // we have some ghost closer to every junction. But it must be distinct ghosts! (one ghost can only cover 1 junction)
            if (match(ghostIsCloser, nrJunctions)) {
                if (log) {
                    log("Pacman will die: " + logMsg);
                }
                return true;
            }
            if (log) {
                log("no match, pacman survives");
            }
            return false;
        }
    }
    public static StaticEvaluator2 staticEval2 = new StaticEvaluator2();

    public static void calcBorderEdges(PlyInfo p, boolean movePacman) {
        if (staticEval2.expandCalled) {
            return;
        }
        if (!TransposTable.retrieveStaticEval(p, movePacman)) {
            staticEval2.expand();
        } else {
            nrRetrievedStatic++;
        }
    }

    private static void setExtendedSearchTargets() {
        int assignedMask = 0; // which borders are assigned
        for (int i = 0; i < ghostTargets.length; ++i) {
            nrGhostTargets[i] = 0;
            int assignedBorder = staticEval2.ghostAssignment.bestAssignedBorders[i];
            if (assignedBorder >= 0) {
                assignedMask |= 1 << assignedBorder;
                BorderEdge edge = staticEval2.borders[assignedBorder];
                MyGhost ghost = b.ghosts[i];
                //if (ghost.lairTime == 0) {
                // ghost is not yet on the assigned border edge, so create a target
                Target target = ghostTargets[i][0];
                target.set(edge, i);
                // if already on edge, target is already reached. By adding this target anyway,
                // pacman gets the choice of following pacman or pursuing some other target.
                target.reached = ghost.lairTime == 0 && nodes[ghost.currentNodeIndex].edge == edge.edge;
                ++nrGhostTargets[i];
                if (log) {
                    log("Target ghost " + i + " <- " + target.ghostJunction);
                }
                //}
            }
        }
        // check unassigned borders, create targets for them
        for (int e = 0; e < staticEval2.nrBorders; ++e) {
            if (((1 << e) & assignedMask) == 0) {
                BorderEdge edge = staticEval2.borders[e];
                for (int i = 0; i < ghostTargets.length; ++i) {
                    if ((edge.closerGhosts & (1 << i)) != 0) {
                        MyGhost ghost = b.ghosts[i];
                        if (ghost.lairTime > 0 || nodes[ghost.currentNodeIndex].edge != edge.edge) {
                            // ghost is not yet on the assigned border edge, so create a target
                            Target target = ghostTargets[i][nrGhostTargets[i]];
                            target.set(edge, i);
                            ++nrGhostTargets[i];
                            if (log) {
                                log("Target ghost " + i + ": " + target.ghostJunction);
                            }
                        }
                    }
                }
            }
        }
        // determine dead ends (ghosts that have only 1 target: don't walk into those) */
        nrDeadEdges = 0;
        for (int i = 0; i < ghostTargets.length; ++i) {
            if (nrGhostTargets[i] == 1) {
                Target target = ghostTargets[i][0];
                if (!target.edge.containsPowerPill) {
                    if (log) {
                        log("dead end: " + target.edge);
                    }
                    deadEdges[nrDeadEdges] = target.edge;
                    ++nrDeadEdges;
                    // also remove this target from other ghosts
                    // (we know that ghost i will for sure move to this target, so other ghosts do
                    // not need to follow this target as well)
                    for (int j = 0; j < ghostTargets.length; ++j) {
                        if (j != i && nrGhostTargets[j] > 1) {
                            for (int k = 1; k < nrGhostTargets[j]; ++k) {
                                if (ghostTargets[j][k] == target) {
                                    if (log) {
                                        log("Removed target " + target.ghostJunction + " from ghost " + j + " (covered by " + i + ")");
                                    }
                                    --nrGhostTargets[j];
                                    if (nrGhostTargets[j] > 1) {
                                        ghostTargets[j][k] = ghostTargets[j][nrGhostTargets[j]];
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if pacman can reach a power pill. Must be called after
     * checkPacmanHealth().
     *
     * @return
     */
    public static boolean canReachPowerPill() {
        return pacmanCanGetToPowerPill;
    }

    public static void clearStaticEval() {
        staticEval2.clear();
    }

    public static class StaticEvaluator2 {

        private static final boolean statLog = log && false;
        BorderEdge[] borders = new BorderEdge[50];
        int nrBorders;
        Node[] pacmanNodes = new Node[100];
        int nrPacmanNodes;
        int[] pacmanDistances;
        BorderEdge[] borderEdgeCache;
        BorderEdge[] added = new BorderEdge[50];
        int nrAdded;
        BorderEdge[] newAdded = new BorderEdge[50];
        int nrNewAdded;
        public boolean hasCircles;
        /**
         * true if expand has been called
         */
        boolean expandCalled;
        /**
         * true if match has been called
         */
        boolean matchCalled;
        /**
         * cached result of the call to match
         */
        boolean matchResult;
        /**
         * if ghosts have been assigned to border edges
         */
        boolean ghostAssignCalled;
        public GhostAssignment ghostAssignment = new GhostAssignment();
        public boolean canReachPowerPill;
        int nrInvolvedGhosts;
        /**
         * True if the result was a cached result
         */
        boolean resultFromCache;
        int pacmanHealth;

        public void update() {
            pacmanDistances = new int[graph.junctionNodes.length];
            // initialize border edge cache
            borderEdgeCache = new BorderEdge[2 * graph.edges.size()];
            for (int i = 0; i < graph.edges.size(); ++i) {
                BigEdge edge = graph.edges.get(i);
                for (int endpoint = 0; endpoint < 2; ++endpoint) {
                    BorderEdge border = new BorderEdge();
                    border.edge = edge;
                    border.pacmanJunction = edge.endpoints[endpoint];
                    border.ghostJunction = edge.endpoints[1 - endpoint];
                    MOVE firstMoveFromOther = edge.getFirstMove(border.ghostJunction);
                    border.firstMoveFromGhost = firstMoveFromOther;
                    borderEdgeCache[2 * i + endpoint] = border;
                }
            }
        }

        /**
         * Clears any cached results
         */
        public void clear() {
            expandCalled = false;
            matchCalled = false;
            ghostAssignCalled = false;
            resultFromCache = false;
            nrInvolvedGhosts = -1;
        }

        public void expand() {
            if (expandCalled) {
                return;
            }
            if (log) {
                log("calcBorderEdges");
            }
            Arrays.fill(pacmanDistances, 10000);
            nrBorders = 0;
            nrPacmanNodes = 0;
            nrAdded = 0;
            hasCircles = false;
            canReachPowerPill = false;
            matchCalled = false;
            matchResult = false;
            Node n = nodes[b.pacmanLocation];
            if (n.isJunction()) {
                addPacman(n, 0);
                for (int i = 0; i < n.nrNeighbours; ++i) {
                    BorderEdge borderEdge = createEdge(n, n.edges[i], n.edges[i].length);
                    if (borderEdge != null) {
                        borderEdge.parent = null;
                        added[nrAdded] = borderEdge;
                        ++nrAdded;
                    }
                }
            } else {
                for (int j = 0; j < 2; ++j) {
                    BorderEdge borderEdge = createEdge(n.edge.endpoints[1 - j], n.edge, n.edge.getDistanceToJunction(n, n.edge.endpoints[j]));
                    if (borderEdge != null) {
                        borderEdge.parent = null;
                        added[nrAdded] = borderEdge;
                        ++nrAdded;
                    }
                }
            }
            while (nrAdded > 0 && nrPacmanNodes < 10 && nrBorders < 9) {
                expandOneMore(true);
            }
            if (log) {
                logState();
            }
            expandCalled = true;
        }

        private void expandOneMore(boolean goDeeper) {
            nrNewAdded = 0;
            for (int b = 0; b < nrAdded; ++b) {
                BorderEdge borderEdge = added[b];
                Node n = borderEdge.ghostJunction;
                if (borderEdge.closerGhosts == 0) {
                    // this is not border edge;both junctions belong to pacman
                    // check if the other junction was already a pacman node
                    Node existingNode = findPacmanNode(borderEdge.ghostJunction);
                    boolean expansionNeeded = false;
                    if (existingNode == null) {
                        // new node
                        addPacman(n, borderEdge.pacmanDist);
                        if (borderEdge.edge.containsPowerPill) {
                            canReachPowerPill = true; // pacman can reach a power pill
                        }
                        expansionNeeded = true;
                    } else {
                        hasCircles = true;
                        if (pacmanDistances[existingNode.junctionIndex] > borderEdge.pacmanDist) {
                            // node was already present, but now we found a shorter way to it
                            pacmanDistances[existingNode.junctionIndex] = borderEdge.pacmanDist;
                            expansionNeeded = true; // need to recalculate distances
                        }
                    }
                    if (goDeeper && expansionNeeded) {
                        for (int i = 0; i < n.nrNeighbours; ++i) {
                            BigEdge edge = n.edges[i];
                            if (edge != borderEdge.edge) {
                                int distToJunction = borderEdge.pacmanDist + n.edges[i].length;
                                int existingEdgeIndex = find(edge);
                                if (existingEdgeIndex < 0) {
                                    BorderEdge newEdge = createEdge(n, n.edges[i], distToJunction);
                                    if (newEdge != null) {
                                        newEdge.parent = borderEdge;
                                        newAdded[nrNewAdded] = newEdge;
                                        ++nrNewAdded;
                                    }
                                } else {
                                    BorderEdge existingEdge = borders[existingEdgeIndex];
                                    if (distToJunction < existingEdge.pacmanDist) {
                                        existingEdge.pacmanDist = distToJunction;
                                        // recalculate which ghosts are closer
                                        existingEdge.closerGhosts = 0;
                                        for (int j = 0; j < existingEdge.ghostDist.length; ++j) {
                                            if (existingEdge.ghostDist[j] < distToJunction) {
                                                existingEdge.closerGhosts |= 1 << j;
                                            }
                                        }
                                        // if new result is that no ghosts are closer, then this is not a border edge anymore
                                        if (existingEdge.closerGhosts == 0) {
                                            borders[existingEdgeIndex] = borders[nrBorders - 1];
                                            --nrBorders;
                                        }
                                        // recalculate sub-graph 
                                        newAdded[nrNewAdded] = existingEdge;
                                        ++nrNewAdded;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    borders[nrBorders] = borderEdge;
                    ++nrBorders;
                    if (statLog) {
                        log("add border: " + borderEdge);
                    }
                }
            }
            // swap newAdded/added
            BorderEdge[] help = added;
            added = newAdded;
            newAdded = help;
            int h = nrAdded;
            nrAdded = nrNewAdded;
            nrNewAdded = h;
        }

        public int getNrInvolvedGhosts() {
            if (nrInvolvedGhosts >= 0) {
                return nrInvolvedGhosts;
            }
            if (ghostAssignCalled) {
                nrInvolvedGhosts = ghostAssignment.bestNrAssignedGhosts;
                return nrInvolvedGhosts;
            }
            int ghosts = 0;
            for (int b = 0; b < nrBorders; ++b) {
                ghosts |= borders[b].closerGhosts;
            }
            int nrGhosts = 0;
            for (int i = 0; i < 4; ++i) {
                if ((ghosts & (1 << i)) != 0) {
                    ++nrGhosts;
                }
            }
            nrInvolvedGhosts = nrGhosts;
            return nrGhosts;
        }

        private BorderEdge createEdge(Node pacmanJunction, BigEdge edge, int dist) {
            if (statLog) {
                log("createEdge, pacmanJunc " + pacmanJunction + ", edge " + edge + ", dist" + dist);
            }
            int endpoint = edge.endpoints[0] == pacmanJunction ? 0 : 1;
            BorderEdge borderEdge = borderEdgeCache[2 * edge.id + endpoint];
            Node otherJunction = borderEdge.ghostJunction;
            int pacmanDist = dist;
            if (pacmanDistances[otherJunction.junctionIndex] <= pacmanDist) {
                // already been here with same distance or less
                return null;
            }
            borderEdge.pacmanDist = pacmanDist;
            borderEdge.closerGhosts = 0;
            if (statLog) {
                log(otherJunction + ", pacmanDist: " + pacmanDist);
            }
            for (int g = 0; g < b.ghosts.length; ++g) {
                MyGhost ghost = b.ghosts[g];
                if (ghost.lairTime == 0) {
                    Node ghostNode = nodes[ghost.currentNodeIndex];
                    if (ghostNode.edge == edge && ghostNode.getNextJunction(ghost.lastMoveMade) != otherJunction.index) {
                        // ghost is already on the edge, on the move to pacman. Pacman cannot escape via this junction
                        // unless ghost is on same edge as pacman and already past pacman.
                        Node pacmanNode = nodes[b.pacmanLocation];
                        if (pacmanNode.edge != ghostNode.edge || ghostNode.isOnPath(pacmanNode, ghost.lastMoveMade)) {
                            boolean closer = ghost.edibleTime == 0;
                            if (!closer) {
                                // an edible ghost close enough to pacman cannot stop pacman
                                int ghostToPacmanDist;
                                if (pacmanNode.edge == ghostNode.edge) {
                                    // pacman and ghost on same edge
                                    ghostToPacmanDist = Math.abs(pacmanNode.edgeIndex - ghostNode.edgeIndex);
                                } else {
                                    ghostToPacmanDist = pacmanDist + ghostNode.getDistToNextJunction(ghost.lastMoveMade);
                                }
                                closer = 3 * ghost.edibleTime + EAT_DISTANCE < 2 * ghostToPacmanDist;
                            }
                            if (closer) {
                                borderEdge.closerGhosts |= 1 << g;
                                borderEdge.ghostDist[g] = 0;
                                if (statLog) {
                                    log(otherJunction + ": ghost on same edge, " + ghostNode);
                                }
                            }
                        }
                    } else {
                        int d = graph.getGhostDistToJunction(ghost.currentNodeIndex, ghost.lastMoveMade,
                                otherJunction.index, borderEdge.firstMoveFromGhost);
                        if (log) {
                            int shortestDist = game.getShortestPathDistance(ghost.currentNodeIndex, otherJunction.index);
                            if (d < shortestDist) {
                                throw new RuntimeException("Internal error in ghostDist, ghostDist = " + d + ", shortest dist = " + shortestDist);
                            }
                        }
                        int ghostDist = ghostDist(ghost, d);
                        borderEdge.ghostDist[g] = ghostDist;
                        if (ghostDist - EAT_DISTANCE <= pacmanDist) {
                            borderEdge.closerGhosts |= 1 << g;
                            if (statLog) {
                                log(otherJunction + ": closer ghost: " + ghostNode + ", dist: " + ghostDist);
                            }
                        } else {
                            if (statLog) {
                                log(otherJunction + ": farther away: ghost " + ghostNode + ", dist: " + ghostDist);
                            }
                        }
                    }
                } else {
                    int ghostDist = ghost.lairTime + graph.getGhostDistToJunction(Search.game.getGhostInitialNodeIndex(), MOVE.NEUTRAL,
                            otherJunction.index, borderEdge.firstMoveFromGhost);
                    borderEdge.ghostDist[g] = ghostDist;
                    if (ghostDist - EAT_DISTANCE <= pacmanDist) {
                        borderEdge.closerGhosts |= 1 << g;
                        if (statLog) {
                            log(otherJunction + ": closer ghost: lair ghost " + g + ", dist: " + ghostDist);
                        }
                    } else {
                        if (statLog) {
                            log(otherJunction + ": longer away: ghost lair ghost " + g + ", dist: " + ghostDist);
                        }
                    }
                }
            }
            if (statLog) {
                log("created edge " + borderEdge);
            }
            return borderEdge;
        }

        private void addPacman(Node node, int dist) {
            if (statLog) {
                log("addPacmanNode " + node + ", dist " + dist);
            }
            pacmanNodes[nrPacmanNodes] = node;
            ++nrPacmanNodes;
            pacmanDistances[node.junctionIndex] = dist;
        }

        private Node findPacmanNode(Node node) {
            for (int i = 0; i < nrPacmanNodes; ++i) {
                if (pacmanNodes[i] == node) {
                    return pacmanNodes[i];
                }
            }
            return null;
        }

        private int find(BigEdge edge) {
            for (int b = 0; b < nrBorders; ++b) {
                if (borders[b].edge == edge) {
                    return b;
                }
            }
            return -1;
        }

        public boolean wouldDieWithoutPowerPill() {
            boolean result = false;
            if (nrBorders <= 6 && staticEval2.nrPacmanNodes <= 5) {
                result = match();
                if (result) {
                    // ghosts can invade pacmans territory via all edges
                    if (staticEval2.hasCircles) {
                        // pacman has circles, perhaps he can survive
                        if (staticEval2.nrBorders >= 3 || staticEval2.ghostAssignment.bestNrAssignedGhosts < 3) {
                            // ok, let's hope for the best...
                            result = false;
                        }
                    }
                }
            }
            return result;
        }

        /**
         * Checks if the ghosts can capture pacman. expand must have been called
         * before.
         *
         * @return true if the ghosts can capture pacman
         */
        public boolean match() {
            if (matchCalled) {
                return matchResult;
            }
            matchCalled = true;
            if (nrBorders > 6) {
                matchResult = false;
            } else {
                ghostAssignment.calcAssignment(borders, nrBorders);
                ghostAssignCalled = true;
                boolean result = ghostAssignment.bestNrAssignedGhosts >= nrBorders;
                if (log) {
                    log("graph: match: " + result);
                }
                if (!result && !hasCircles) {
                    // do a tree analysis
                    result = treeMatch();
                }
                // special case, use old static evaluator for edge/junction evaluation, because
                // the new static evaluator cannot handle it correctly if there is for example
                // 1 ghost covering the only 2 border edges, but there are some more ghosts close.
                if (!result && nrBorders <= 3 && ghostAssignment.bestNrAssignedGhosts >= nrBorders - 1) {
                    Node pacmanNode = nodes[b.pacmanLocation];
                    if (!pacmanNode.isJunction()) {
                        BigEdge pacmanEdge = pacmanNode.edge;
                        boolean pacmanDies = checkPacmanEdgeJunction(pacmanEdge.endpoints[0].index);
                        if (!pacmanDies) {
                            pacmanDies = checkPacmanEdgeJunction(pacmanEdge.endpoints[1].index);
                        }
                        result = pacmanDies;
                    }
                }
                if (result && !canReachPowerPill) {
                    canReachPowerPill = checkPowerPillsOnBorderEdges();
                    if (!canReachPowerPill) {
                        // result may be inaccurate, take a second opinion.
                        canReachPowerPill = canGetToPowerPillQuickAndDirty();
                    }
                    if (log && canReachPowerPill) {
                        log("pacman can reach powerpill");
                    }
                    //result = !canReachPowerPill;
                }
                matchResult = result;
            }
            return matchResult;
        }

        /**
         * Makes a check if pacman survives if the border graph is a tree (no
         * circles). match must have been called before calling this method.
         * Involves checking non-assigned border edges; 1 ghost can cover more
         * than 1 border edge if it can choose later than pacman to which edge
         * it should go.
         *
         * @return true if pacman can be caught by the ghosts.
         */
        private boolean treeMatch() {
            if (log) {
                log("treeMatch, " + ghostAssignment);
            }
            int assignedMask = 0; // which borders are assigned
            for (int i = 0; i < ghostTargets.length; ++i) {
                int assignedBorder = ghostAssignment.bestAssignedBorders[i];
                if (assignedBorder >= 0) {
                    assignedMask |= 1 << assignedBorder;
                }
            }
            // check unassigned borders, create targets for them
            boolean pacmanCanEscape = false;
            for (int e = 0; !pacmanCanEscape && e < nrBorders; ++e) {
                if (((1 << e) & assignedMask) == 0) {
                    BorderEdge unassignedEdge = borders[e];
                    path1Length = 0;
                    for (BorderEdge parent = unassignedEdge.parent; parent != null; parent = parent.parent) {
                        if (statLog) {
                            log("path1: parent " + parent.edge + ", pacmandist: " + parent.pacmanDist);
                        }
                        path1[path1Length] = parent;
                        ++path1Length;
                    }
                    boolean catchesPacman = false;
                    for (int i = 0; !catchesPacman && i < ghostTargets.length; ++i) {
                        if ((unassignedEdge.closerGhosts & (1 << i)) != 0) {
                            MyGhost ghost = b.ghosts[i];
                            int border = ghostAssignment.bestAssignedBorders[i];
                            if (border >= 0) {
                                BorderEdge assignedEdge = borders[border];
                                if (statLog) {
                                    log("unassignedEdge: " + unassignedEdge.edge + ", assignedEdge: " + assignedEdge.edge + ", ghost: " + i);
                                }
                                path2Length = 0;
                                for (BorderEdge parent = assignedEdge.parent; parent != null; parent = parent.parent) {
                                    if (statLog) {
                                        log("path2: parent " + parent.edge + ", pacmandist: " + parent.pacmanDist);
                                    }
                                    path2[path2Length] = parent;
                                    ++path2Length;
                                }
                                int nrEqual = 0;
                                while (nrEqual < path1Length && nrEqual < path2Length && path1[path1Length - nrEqual - 1] == path2[path2Length - nrEqual - 1]) {
                                    ++nrEqual;
                                }
                                if (statLog) {
                                    log("path1Length: " + path1Length + ", path2Length: " + path2Length + ", nr equal: " + nrEqual);
                                }
                                int distToFirst = 0;
                                int worstSlack = 1000;
                                if (nrEqual > 0) {
                                    BorderEdge firstOnPath = path1[path1Length - nrEqual];
                                    distToFirst = firstOnPath.pacmanDist;
                                    Node firstNode = firstOnPath.ghostJunction;
                                    worstSlack = firstOnPath.getPacmanSlack();
                                    if (statLog) {
                                        log("firstOnPath: " + firstNode + ", slack: " + worstSlack);
                                    }
                                    for (int g = 0; g < ghostTargets.length; ++g) {
                                        MyGhost g2 = b.ghosts[g];
                                        if (g2.canKill()) {
                                            int ghostDist = b.graph.getGhostDistToJunction(g2.currentNodeIndex, g2.lastMoveMade, firstNode.index, MOVE.NEUTRAL);
                                            int slack = ghostDist - 3 - distToFirst;
                                            if (statLog) {
                                                log("ghostDist " + g + " to first: " + ghostDist + ", pacmanDist: " + distToFirst
                                                        + ", worstSlack: " + worstSlack + ", slack: " + slack);
                                            }
                                            if (slack < worstSlack && slack >= 0) {
                                                worstSlack = slack;
                                                if (statLog) {
                                                    log("new slack: " + worstSlack);
                                                }
                                            }
                                        }
                                    }
                                }
                                for (int k = 0; k < path1Length - nrEqual; ++k) {
                                    int slack = path1[k].getPacmanSlack();
                                    if (slack < worstSlack) {
                                        worstSlack = slack;
                                    }
                                }
                                for (int k = 0; k < path2Length - nrEqual; ++k) {
                                    int slack = path2[k].getPacmanSlack();
                                    if (slack < worstSlack) {
                                        worstSlack = slack;
                                    }
                                }
                                // unfortunately pacman cannot wait at a junction: must make 1 move and then back.
                                // this can influence the slack negatively with 1 (round slack down to even number)
                                worstSlack = worstSlack & 0xfffffe;
                                int timeUntilPacmanChoice = distToFirst + worstSlack;
                                int timeUntilGhostChoice = calcTimeUntilGhostChoice(ghost, assignedEdge, unassignedEdge);
                                /*if (timeUntilGhostChoice == 0) {
                                 Node ghostNode = nodes[ghost.currentNodeIndex];
                                 if (ghostNode.edge != null) {
                                 timeUntilGhostChoice = ghostNode.getDistToNextJunction(ghost.lastMoveMade);
                                 }
                                 }*/
                                if (statLog) {
                                    log("ghost choice: " + timeUntilGhostChoice + ", pacman: " + timeUntilPacmanChoice + ",distToFirst: " + distToFirst + ", slack: " + worstSlack);
                                }
                                catchesPacman = timeUntilGhostChoice >= timeUntilPacmanChoice;
                            }
                        }
                    }
                    pacmanCanEscape = !catchesPacman;
                }
            }
            if (log) {
                log("treeMatch: pacman can escape: " + pacmanCanEscape);
            }
            return !pacmanCanEscape;
        }

        private int calcTimeUntilGhostChoice(MyGhost ghost, BorderEdge edge1, BorderEdge edge2) {
            if (statLog) {
                log("calcTimeUntilGhostChoice " + edge1.ghostJunction + "/" + edge2.ghostJunction);
            }
            int timeUntilGhostChoice = ghost.lairTime;
            if (timeUntilGhostChoice == 0) {
                Node firstNode = nodes[ghost.currentNodeIndex];
                MOVE lastMoveMade = ghost.lastMoveMade;
                if (firstNode.edge != null) {
                    timeUntilGhostChoice = firstNode.getDistToNextJunction(ghost.lastMoveMade);
                    lastMoveMade = firstNode.getLastMoveToNextJunction(lastMoveMade);
                    firstNode = nodes[firstNode.getNextJunction(ghost.lastMoveMade)];
                }
                int path1 = graph.getGhostPathToJunction(firstNode.junctionIndex, lastMoveMade,
                        edge1.ghostJunction.junctionIndex, edge1.firstMoveFromGhost);
                int path2 = graph.getGhostPathToJunction(firstNode.junctionIndex, lastMoveMade,
                        edge2.ghostJunction.junctionIndex, edge2.firstMoveFromGhost);
                Node currNode = firstNode;
                if (statLog) {
                    log("path1 to " + edge1.ghostJunction);
                    for (int i = 0; JunctionGraph.paths[path1 + i] >= 0; ++i) {
                        log(i + ": " + graph.junctionNodes[JunctionGraph.paths[path1 + i]]);
                    }
                    log("path2 to " + edge2.ghostJunction);
                    for (int i = 0; JunctionGraph.paths[path2 + i] >= 0; ++i) {
                        log(i + ": " + graph.junctionNodes[JunctionGraph.paths[path2 + i]]);
                    }
                }
                for (int i = 0; JunctionGraph.paths[path1 + i] >= 0 && JunctionGraph.paths[path2 + i] == JunctionGraph.paths[path1 + i]; ++i) {
                    Node newNode = graph.junctionNodes[JunctionGraph.paths[path1 + i]];
                    timeUntilGhostChoice += game.getShortestPathDistance(currNode.index, newNode.index);
                    if (log) {
                        log("On common path: " + newNode + ", dist: " + timeUntilGhostChoice);
                    }
                    currNode = newNode;
                }
            }
            if (statLog) {
                log("calcTimeUntilGhostChoice returns " + timeUntilGhostChoice);
            }
            return timeUntilGhostChoice;
        }

        /**
         * Checks if there is a power pill on a border edge closer to pacman
         * than to ghosts.
         */
        public boolean checkPowerPillsOnBorderEdges() {
            if (statLog) {
                log("checkPowerPillsOnBorderEdges");
            }
            for (int i = 0; i < nrBorders; ++i) {
                BorderEdge border = borders[i];
                if (border.edge.containsPowerPill) {
                    if (statLog) {
                        log("check edge " + border.edge);
                    }
                    int nodeIndex = b.findPowerPill(border.edge);
                    if (nodeIndex >= 0 && b.containsPowerPill[nodeIndex]) {
                        Node node = nodes[nodeIndex]; // power pill node
                        Node pacmanNode = nodes[b.pacmanLocation];
                        if (pacmanNode.edge == node.edge) {
                            if (pacmanNode == node) {
                                return true;
                            }
                            int pacmanDistToGhostJunction = node.edge.getDistanceToJunction(pacmanNode, border.ghostJunction);
                            int pillDistToGhostJunction = node.edge.getDistanceToJunction(node, border.ghostJunction);
                            if (pacmanDistToGhostJunction > pillDistToGhostJunction) {
                                int pacmanDistToPowerPill = pacmanDistToGhostJunction - pillDistToGhostJunction;
                                boolean inReach = true;
                                for (int g = 0; g < border.ghostDist.length; ++g) {
                                    if ((border.closerGhosts & (1 << g)) != 0) {
                                        int ghostDistToPowerPill = border.ghostDist[g] + pillDistToGhostJunction;
                                        if (border.ghostDist[g] == 0) {
                                            ghostDistToPowerPill = game.getShortestPathDistance(b.ghosts[g].currentNodeIndex, node.index);
                                        }
                                        if (statLog) {
                                            log("ghost " + g + ", ghostDist " + ghostDistToPowerPill + ", pacdist: " + pacmanDistToPowerPill);
                                        }
                                        if (ghostDistToPowerPill - EAT_DISTANCE <= pacmanDistToPowerPill) {
                                            if (statLog) {
                                                log("closer ghost: " + g);
                                            }
                                            inReach = false;
                                            break;
                                        }
                                    }
                                }
                                if (inReach) {
                                    return inReach;
                                }
                            }
                        } else {
                            int distToGhostJunction = borders[i].edge.getDistanceToJunction(node, border.ghostJunction);
                            int pacmanDistToPowerPill = border.pacmanDist - distToGhostJunction;//game.getShortestPathDistance(b.pacmanLocation, nodeIndex);
                            if (statLog) {
                                log("checkPowerPillsOnBorderEdges, pill " + node + ", pacmanDist: " + pacmanDistToPowerPill);
                            }
                            boolean inReach = true;
                            for (int g = 0; g < border.ghostDist.length; ++g) {
                                if ((border.closerGhosts & (1 << g)) != 0 && border.ghostDist[g] + distToGhostJunction - EAT_DISTANCE <= pacmanDistToPowerPill) {
                                    if (statLog) {
                                        log("closer ghost: " + g);
                                    }
                                    inReach = false;
                                    break;
                                }
                            }
                            if (inReach) {
                                return inReach;
                            }
                        }
                    }
                }
            }
            return false;
        }

        private void logState() {
            log("internal graph:");
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < nrPacmanNodes; ++i) {
                buf.append(pacmanNodes[i] + ", dist: " + pacmanDistances[pacmanNodes[i].junctionIndex] + " ");
            }
            log("pacmanNodes, size = " + nrPacmanNodes + ", nodes: " + buf);
            log("borders, size = " + nrBorders);
            for (int b = 0; b < nrBorders; ++b) {
                log("" + borders[b]);
            }
        }
    }

    /**
     * check that every bit 0..n-1 is covered at least once by different ghosts
     *
     * @param arr every element contains bit mask for a ghost, bit set means
     * ghost is closer to junction than pacman
     * @param n
     * @return
     */
    private static boolean match(int[] arr, int n) {
        if (n == 0) {
            return true;
        }
        for (int i = 0; i < arr.length; ++i) {
            for (int j = 0; j < n; ++j) {
                if ((arr[i] & (1 << j)) != 0) {
                    int mask = (1 << j) - 1;
                    int[] arr2 = new int[arr.length];
                    for (int k = 0; k < arr.length; ++k) {
                        if (k == i) {
                            arr2[k] = 0;
                        } else {
                            arr2[k] = (arr[k] & mask)
                                    | ((arr[k] >> 1) & ~mask);
                        }
                    }
                    if (match(arr2, n - 1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

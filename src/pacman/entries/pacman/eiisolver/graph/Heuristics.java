package pacman.entries.pacman.eiisolver.graph;

import pacman.game.*;

public class Heuristics {

    public static final int POINT_FACTOR = 10;
    private Game game;
    private Board b;
    private JunctionGraph graph;
    /**
     * Power pill score
     */
    private int powerPillScore;
    private int[] nodeScore;
    private boolean weakOpponent;

    /**
     * Update heuristic parameters at beginning of a new move.
     */
    public void updateForNewMove(Game game, Board board) {
        this.game = game;
        this.b = board;
        graph = b.graph;
        nodeScore = new int[board.graph.nodes.length];
        determineWeakOpponent();
        setPowerPillScore();
        setNodeScore();
    }

    public int getPillScore(int location) {
        return (Constants.PILL * POINT_FACTOR) / 6;
    }

    public int getPowerPillScore() {
        return powerPillScore;
    }

    public int getNodeScore(int location) {
        return nodeScore[location];
    }

    public boolean hasManyLivesLeft() {
        return game.getPacmanNumberOfLivesRemaining() > 2;
    }

    /**
     * Returns true if we think the opponent is weak, and allows for more
     * aggressive play.
     *
     * @return
     */
    public boolean isWeakOpponent() {
        return weakOpponent;
    }

    private void determineWeakOpponent() {
        if (!Search.pacmanEvaluation) {
            weakOpponent = game.getCurrentLevel() == 0 /*&& b.nrPowerPillsOnBoard >= 1 
                     && (2300 - 400*game.getPacmanNumberOfLivesRemaining()) > game.getTotalTime()*/;
        } else {
            weakOpponent = game.getCurrentLevel() > 0 && hasManyLivesLeft();
        }
    }

    private void setNodeScore() {
        if (powerPillScore < 0) {
            // give negative node scores for nodes on
            // same edge as a power pill
            for (int i = 0; i < b.nrPowerPills; ++i) {
                int p = b.powerPillLocation[i];
                if (b.containsPowerPill[p] && !graph.nodes[p].isJunction()) {
                    for (Node n : graph.nodes[p].edge.internalNodes) {
                        nodeScore[n.index] = -3 * Constants.PILL * POINT_FACTOR / 2;
                    }
                }
            }
        }
        /*if (!Search.pacmanEvaluation && weakOpponent) {
         for (int i = 0; i < graph.nodes.length; ++i) {
         Node n = graph.nodes[i];
         if (n.y <= 54) {
         int pacmanX = graph.nodes[Search.b.pacmanLocation].x;
         if ((pacmanX <= 54) == (n.x <= 30)) {
         nodeScore[i] = 15000;
         }
         }
         }
         }*/
    }

    private void setPowerPillScore() {
        if (isWeakOpponent()) {
            if (!Search.pacmanEvaluation) {
                powerPillScore = Constants.GHOST_EAT_SCORE * POINT_FACTOR;
                return;
            }
        }
        boolean existNonKilling = existNonKillingGhosts();
        int weakScore = 0;
        if (isWeakOpponent()) {
            if (game.getCurrentLevel() < 8 || game.getCurrentLevelTime() < 1000) {
                weakScore = -3 * Constants.GHOST_EAT_SCORE * POINT_FACTOR;
            } else {
                weakScore = -((19 - game.getCurrentLevel()) * Constants.GHOST_EAT_SCORE * POINT_FACTOR) / 4;
            }
        }
        int factor = isWeakOpponent() ? 300 : 200;
        int leftNearEnd = game.getCurrentLevelTime() - 2800 + factor * b.nrPowerPillsOnBoard;
        if (existNonKilling) {
            // discourage eating power pills if there are still edible ghosts
            powerPillScore = -15000;//-8*Constants.GHOST_EAT_SCORE*POINT_FACTOR;
        } else if (game.getCurrentLevelTime() < 2600 - factor * b.nrPowerPillsOnBoard) {
            // discourage eating power pills in the beginning
            if (Search.pacmanEvaluation) {
                if (isWeakOpponent()) {
                    powerPillScore = weakScore;
                } else {
                    powerPillScore = -12000; //-10*Constants.GHOST_EAT_SCORE*POINT_FACTOR;
                }
            } else {
                powerPillScore = -2 * Constants.GHOST_EAT_SCORE * POINT_FACTOR;
            }
        } else if (leftNearEnd > 0) {
            powerPillScore = Constants.GHOST_EAT_SCORE + leftNearEnd;
        } else {
            powerPillScore = isWeakOpponent() ? weakScore : -500;
        }
        System.out.println("power pill score: " + powerPillScore
                + ", exist: " + existNonKilling + ", time: " + game.getCurrentLevelTime() + ", powerpillsOnBoard: " + b.nrPowerPillsOnBoard);
    }

    private boolean existNonKillingGhosts() {
        for (MyGhost ghost : b.ghosts) {
            if (!ghost.canKill()) {
                return true;
            }
        }
        return false;
    }
}

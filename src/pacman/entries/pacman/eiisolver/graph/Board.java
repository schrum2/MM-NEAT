package pacman.entries.pacman.eiisolver.graph;

import static pacman.game.Constants.COMMON_LAIR_TIME;
import static pacman.game.Constants.EDIBLE_TIME;
import static pacman.game.Constants.EDIBLE_TIME_REDUCTION;
import static pacman.game.Constants.LAIR_REDUCTION;

import java.util.Random;

import pacman.game.Game;
import pacman.game.Constants.*;

public class Board {

    private static long[] pacmanHash = new long[4000];
    public static long[] pillHash = new long[pacmanHash.length];
    public JunctionGraph graph;
    public MyGhost[] ghosts;
    public int pacmanLocation;
    public MOVE pacmanLastMove;
    public boolean[] containsPill;
    public boolean[] containsPowerPill;
    /**
     * Locations of the power pills
     */
    public int[] powerPillLocation = new int[20];
    /**
     * total nr of power pills at start of this level
     */
    public int nrPowerPills;
    /**
     * total nr of pills at start of this level
     */
    public int nrPills;
    /**
     * number of pills left
     */
    public int nrPowerPillsOnBoard;
    public int nrPillsOnBoard;
    public int currentEdibleTime = 200;
    public int currentLairTime = 200;
    public long currentPillHash;

    static {
        Random rnd = Search.rand;
        for (int i = 0; i < pacmanHash.length; ++i) {
            pacmanHash[i] = rnd.nextLong();
            pillHash[i] = rnd.nextLong();
        }
    }

    public Board() {
        ghosts = new MyGhost[GHOST.values().length];
        for (int i = 0; i < ghosts.length; ++i) {
            ghosts[i] = new MyGhost();
            ghosts[i].ghost = GHOST.values()[i];
        }
    }

    public void initHash() {
        for (MyGhost ghost : ghosts) {
            ghost.initHash(Search.rand);
        }
    }

    public void update(Game game) {
        nrPills = game.getPillIndices().length;
        nrPillsOnBoard = 0;
        containsPill = new boolean[graph.nodes.length];
        for (int index = 0; index < containsPill.length; ++index) {
            int pillIndex = game.getPillIndex(index);
            containsPill[index] = pillIndex >= 0 && game.isPillStillAvailable(pillIndex);
            if (containsPill[index]) {
                ++nrPillsOnBoard;
            }
        }
        for (BigEdge edge : graph.edges) {
            edge.containsPowerPill = false;
        }
        containsPowerPill = new boolean[graph.nodes.length];
        nrPowerPills = 0;
        nrPowerPillsOnBoard = 0;
        for (int index = 0; index < containsPowerPill.length; ++index) {
            int powerPillIndex = game.getPowerPillIndex(index);
            containsPowerPill[index] = powerPillIndex >= 0 && game.isPowerPillStillAvailable(powerPillIndex);
            if (containsPowerPill[index] && !graph.nodes[index].isJunction()) {
                graph.nodes[index].edge.containsPowerPill = true;
                ++nrPowerPillsOnBoard;
            }
            if (powerPillIndex >= 0) {
                powerPillLocation[nrPowerPills] = index;
                ++nrPowerPills;
            }
        }
        // calculate pill hash
        currentPillHash = 0;
        for (int i = 0; i < graph.nodes.length; ++i) {
            if (containsPowerPill[i] || containsPill[i]) {
                currentPillHash ^= pillHash[i];
            }
        }
        //game.getPillIndices().length;
        pacmanLocation = game.getPacmanCurrentNodeIndex();
        pacmanLastMove = game.getPacmanLastMoveMade();
        for (int i = 0; i < ghosts.length; ++i) {
            ghosts[i].currentNodeIndex = game.getGhostCurrentNodeIndex(ghosts[i].ghost);
            ghosts[i].lastMoveMade = game.getGhostLastMoveMade(ghosts[i].ghost);
            ghosts[i].edibleTime = game.getGhostEdibleTime(ghosts[i].ghost);
            ghosts[i].lairTime = game.getGhostLairTime(ghosts[i].ghost);
        }
        currentEdibleTime = (int) (EDIBLE_TIME * (Math.pow(EDIBLE_TIME_REDUCTION, game.getCurrentLevel())));
        currentLairTime = (int) (COMMON_LAIR_TIME * (Math.pow(LAIR_REDUCTION, game.getCurrentLevel())));
    }

    public void addPowerPill(int index) {
        containsPowerPill[index] = true;
        if (containsPowerPill[index] && !graph.nodes[index].isJunction()) {
            graph.nodes[index].edge.containsPowerPill = true;
            ++nrPowerPillsOnBoard;
        }
        powerPillLocation[nrPowerPills] = index;
        ++nrPowerPills;
    }

    public int findPowerPill(BigEdge edge) {
        for (int i = 0; i < nrPowerPills; ++i) {
            if (graph.nodes[powerPillLocation[i]].edge == edge) {
                return powerPillLocation[i];
            }
        }
        return -1;
    }

    public void copyFrom(Board src) {
        pacmanLocation = src.pacmanLocation;
        pacmanLastMove = src.pacmanLastMove;
        for (int i = 0; i < ghosts.length; ++i) {
            ghosts[i].copyFrom(src.ghosts[i]);
        }
        currentPillHash = src.currentPillHash;
    }

    public void logBoard(Game game) {
        graph.print(game, this);
    }

    /**
     * Calculates a hash of the current game situation.
     *
     * @return
     */
    public long getHash(boolean movePacman) {
        long hash = currentPillHash;
        hash ^= pacmanHash[pacmanLocation];
        for (int g = 0; g < ghosts.length; ++g) {
            hash ^= ghosts[g].getHash();
        }
        if (movePacman) {
            hash ^= 0xaaaaaaa;
        }
        return hash;
    }
}

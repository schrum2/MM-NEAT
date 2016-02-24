package pacman.entries.pacman.eiisolver.graph;

/**
 * Transposition table, contains search results of many positions.
 *
 * @author louis
 *
 */
public class TransposTable {

    /**
     * value in transpos entry is a real value
     */
    public static final short REAL_VALUE = 0;
    /**
     * value in transpos entry is an upper bound
     */
    public static final short UPPER_BOUND = 1;
    /**
     * value in transpos entry is an lower bound
     */
    public static final short LOWER_BOUND = 2;
    private static final int BITS = 17;
    private static final int NR_ENTRIES = (1 << BITS);
    private static final int MOVE_PACMAN = 4;
    /**
     * The transposition table.
     */
    private static final TransposInfo[] table = new TransposInfo[NR_ENTRIES];
    /**
     * Cached static evaluation results
     */
    private static final CachedStaticInfo[] cachedStaticInfo = new CachedStaticInfo[NR_ENTRIES];
    /**
     * Holds a mask that is changed every move to avoid retaining old hash
     * entries
     */
    private static int currentToggleMask = 0;
    /**
     * Every hash entry contains a version number that must match
     * currentVersion, otherwise the entry is invalid.
     */
    private static short currentVersion = 0;

    static {
        for (int i = 0; i < table.length; ++i) {
            table[i] = new TransposInfo();
        }
        for (int i = 0; i < cachedStaticInfo.length; ++i) {
            cachedStaticInfo[i] = new CachedStaticInfo();
        }
    }

    public static void clear() {
        // invalidate all hash entries by increasing the version number
        ++currentVersion;
        /*for (int i = 0; i < table.length; ++i) {
         table[i].hash = 0;
         }
         for (int i = 0; i < cachedStaticInfo.length; ++i) {
         cachedStaticInfo[i].hash = 0;
         }*/
    }

    public static void toggleMoveMask() {
        ++currentToggleMask;
        if (currentToggleMask >= 32) {
            currentToggleMask = 0;
        }
    }

    /**
     * Stores (maybe, if priority high enough) the current position in the hash
     * table
     */
    public static void store(Board b, PlyInfo p, boolean movePacman) {
        long hash = p.hash;
        int index = (int) (hash & (NR_ENTRIES - 1));
        TransposInfo t = table[index];
        if (t.hash == hash) {
            // current position was already present: do not update if p.budget
            // lower
            if (t.budget >= p.budget && t.version == currentVersion) {
                return;
            }
        } else {
            int existingToggle = t.flags >> 4;
            if (currentToggleMask == existingToggle && t.version == currentVersion) {
                // existing entry is of same ply; replace if budget is higher
                if (t.budget >= p.budget) {
                    return;
                }
            }
        }
        t.hash = hash;
        t.value = p.bestValue;
        t.budget = (short) p.budget;
        if (movePacman) {
            t.bestMove = (short) p.bestPacmanMove;
        } else {
            t.bestMove = 0;
            for (int i = 0; i < p.bestGhostMove.length; ++i) {
                int move = p.bestGhostMove[i];
                if (move < 0) {
                    move = 7;
                }
                t.bestMove += move << (3 * i);
            }
        }
        short scoreKind;
        if (p.bestValue > p.alpha) {
            if (p.bestValue < p.beta) {
                // real value
                scoreKind = REAL_VALUE;
            } else {
                // else: lower bound (no bits are set)
                scoreKind = LOWER_BOUND;
            }
        } else {
            // upper bound
            scoreKind = UPPER_BOUND;
        }
        t.flags = scoreKind;
        if (movePacman) {
            t.flags |= MOVE_PACMAN;
        }
        t.flags += currentToggleMask << 4;
        t.pacmanLocation = (short) b.pacmanLocation;
        t.version = currentVersion;
        if (Search.log) {
            Search.log("store trans " + t.hash + ", value " + t.value);
        }
    }

    /**
     * Looks up the current position in transpos table. Will influence move
     * iterator/best move/value if possible.
     *
     * @return true if no further search is necessary
     */
    public static boolean retrieve(Board b, PlyInfo p, boolean movePacman) {
        long hash = p.hash;
        if (hash == 0) {
            hash = b.getHash(movePacman);
            p.hash = hash;
        }
        p.transpos = null;
        int index = (int) (hash & (NR_ENTRIES - 1));
        TransposInfo t = table[index];
        if (t.hash == hash && t.version == currentVersion) {
            // hash matches, but double-check the result
            if (t.pacmanLocation != b.pacmanLocation
                    || movePacman != ((t.flags & MOVE_PACMAN) != 0)) {
                return false;
            }
            // copy the move to bestPacman/GhostMove
            p.transpos = t;
            if (movePacman) {
                p.bestPacmanMove = t.bestMove;
            } else {
                for (int g = 0; g < b.ghosts.length; ++g) {
                    p.bestGhostMove[g] = (t.bestMove >> (3 * g)) & 7;
                    if (p.bestGhostMove[g] == 7) {
                        p.bestGhostMove[g] = -1;
                    }
                }
            }
            int kind = t.flags & 3;
            boolean willDie = Math.abs(t.value) >= 20000;
            if (willDie && p.budget <= 0) {
                // death evaluations made with budget < 0 are not 100% reliable and will
                // propagate upwards; we trust them only a limited time.
                willDie = t.budget >= p.budget - 20;
            }
            if (t.budget >= p.budget || willDie) {
                if (kind == REAL_VALUE
                        || (kind == UPPER_BOUND && t.value <= p.alpha)
                        || (kind == LOWER_BOUND && t.value > p.beta)
                        || willDie) {
                    // transpos with useful value; no search needed
                    p.bestValue = t.value;
                    if (Search.log) {
                        Search.log("retrieve trans " + t.hash + ", value " + t.value);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static void storeStaticEval(PlyInfo p) {
        Search.StaticEvaluator2 eval = Search.staticEval2;
        if (eval.resultFromCache || !eval.expandCalled) {
            return;
        }
        long hash = p.hash;
        int index = (int) (hash & (NR_ENTRIES - 1));
        CachedStaticInfo cached = cachedStaticInfo[index];
        cached.hash = hash;
        cached.nrBorders = eval.nrBorders;
        cached.nrPacmanNodes = eval.nrPacmanNodes;
        cached.hasCircles = eval.hasCircles;
        cached.canReachPowerPill = eval.canReachPowerPill;
        cached.matchCalled = eval.matchCalled;
        cached.matchResult = eval.matchResult;
        cached.bestNrAssignedGhosts = eval.ghostAssignment.bestNrAssignedGhosts;
        cached.nrInvolvedGhosts = eval.nrInvolvedGhosts;
        cached.pacmanHealth = eval.pacmanHealth;
        cached.pacmanLocation = Search.b.pacmanLocation;
        cached.version = currentVersion;
        if (Search.log) {
            Search.log("storeStatic " + cached);
        }
    }

    public static boolean retrieveStaticEval(PlyInfo p, boolean movePacman) {
        long hash = p.hash;
        if (hash == 0) {
            hash = Search.b.getHash(movePacman);
            p.hash = hash;
            int index = (int) (hash & (NR_ENTRIES - 1));
            CachedStaticInfo cached = cachedStaticInfo[index];
            if (cached.hash == hash && cached.pacmanLocation == Search.b.pacmanLocation && cached.version == currentVersion) {
                if (Search.log) {
                    Search.log("Retrieved static, " + cached);
                }
                Search.StaticEvaluator2 eval = Search.staticEval2;
                eval.nrBorders = cached.nrBorders;
                eval.nrPacmanNodes = cached.nrPacmanNodes;
                eval.hasCircles = cached.hasCircles;
                eval.canReachPowerPill = cached.canReachPowerPill;
                eval.matchCalled = cached.matchCalled;
                eval.matchResult = cached.matchResult;
                eval.ghostAssignment.bestNrAssignedGhosts = cached.bestNrAssignedGhosts;
                eval.nrInvolvedGhosts = cached.nrInvolvedGhosts;
                eval.pacmanHealth = cached.pacmanHealth;
                eval.expandCalled = true;
                eval.resultFromCache = true;
                return true;
            }
        }
        return false;
    }

    /**
     * Contains saved information about a position.
     *
     * @author louis
     *
     */
    static class TransposInfo {

        long hash;
        int value;
        short flags;
        short bestMove;
        short budget;
        /**
         * pacman location (only used to verify consistency)
         */
        short pacmanLocation;
        short version;
    }

    static class CachedStaticInfo {

        long hash;
        int pacmanLocation;
        int nrBorders;
        int nrPacmanNodes;
        boolean hasCircles;
        boolean matchCalled;
        boolean matchResult;
        boolean canReachPowerPill;
        int bestNrAssignedGhosts;
        boolean ghostAssignCalled;
        int nrInvolvedGhosts;
        int pacmanHealth;
        short version;

        @Override
        public String toString() {
            return "CachedStaticInfo [hash=" + hash + ", nrBorders="
                    + nrBorders + ", nrPacmanNodes=" + nrPacmanNodes
                    + ", hasCircles=" + hasCircles + ", matchCalled="
                    + matchCalled + ", matchResult=" + matchResult
                    + ", canReachPowerPill=" + canReachPowerPill
                    + ", bestNrAssignedGhosts=" + bestNrAssignedGhosts
                    + ", ghostAssignCalled=" + ghostAssignCalled
                    + ", nrInvolvedGhosts=" + nrInvolvedGhosts
                    + ", pacmanHealth=" + pacmanHealth + "]";
        }
    }
}

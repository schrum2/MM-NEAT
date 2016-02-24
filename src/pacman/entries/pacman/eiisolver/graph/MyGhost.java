package pacman.entries.pacman.eiisolver.graph;

import java.util.Random;

import pacman.game.Constants;
import pacman.game.Constants.*;

/**
 * Copy of the Ghost class, but now made visible.
 *
 * @author louis
 *
 */
public class MyGhost {

    public int currentNodeIndex, edibleTime, lairTime;
    public MOVE lastMoveMade;
    public GHOST ghost;
    private long[] lairHash;
    private long[] indexHash;
    private long[] moveHash;
    private long[] edibleHash;

    public MyGhost() {
    }

    public void initHash(Random rnd) {
        lairHash = new long[1000];
        indexHash = new long[4000];
        moveHash = new long[MOVE.values().length];
        edibleHash = new long[2 * Constants.EDIBLE_TIME];
        for (int i = 0; i < lairHash.length; ++i) {
            lairHash[i] = rnd.nextLong();
        }
        for (int i = 0; i < indexHash.length; ++i) {
            indexHash[i] = rnd.nextLong();
        }
        for (int i = 0; i < moveHash.length; ++i) {
            moveHash[i] = rnd.nextLong();
        }
        for (int i = 0; i < edibleHash.length; ++i) {
            edibleHash[i] = rnd.nextLong();
        }
    }

    public void copyFrom(MyGhost src) {
        currentNodeIndex = src.currentNodeIndex;
        edibleTime = src.edibleTime;
        lairTime = src.lairTime;
        lastMoveMade = src.lastMoveMade;
    }

    public boolean canKill() {
        return lairTime == 0 && edibleTime == 0;
    }

    public long getHash() {
        if (lairTime == 0) {
            return indexHash[currentNodeIndex] ^ moveHash[lastMoveMade.ordinal()] ^ edibleHash[edibleTime];
        } else {
            return lairHash[lairTime];
        }
    }
}

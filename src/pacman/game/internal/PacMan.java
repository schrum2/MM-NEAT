package pacman.game.internal;

import pacman.game.Constants.MOVE;

/*
 * Data structure to hold all information pertaining to Ms Pac-Man.
 */
public final class PacMan {

    public int currentNodeIndex, numberOfLivesRemaining;
    public MOVE lastMoveMade;
    public boolean hasReceivedExtraLife;

    public PacMan(int currentNodeIndex, MOVE lastMoveMade, int numberOfLivesRemaining, boolean hasReceivedExtraLife) {
        this.currentNodeIndex = currentNodeIndex;
        this.lastMoveMade = lastMoveMade;
        this.numberOfLivesRemaining = numberOfLivesRemaining;
        this.hasReceivedExtraLife = hasReceivedExtraLife;
    }

    public PacMan copy() {
        return new PacMan(currentNodeIndex, lastMoveMade, numberOfLivesRemaining, hasReceivedExtraLife);
    }
}
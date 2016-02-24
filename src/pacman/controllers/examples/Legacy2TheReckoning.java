package pacman.controllers.examples;

import java.util.EnumMap;
import pacman.controllers.NewGhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * The Class Legacy2TheReckoning.
 */
public class Legacy2TheReckoning extends NewGhostController {

    public static final int CROWDED_DISTANCE = 30;
    public static final int PACMAN_DISTANCE = 10;
    public static final int PILL_PROXIMITY = 15;
    private final EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
    private final EnumMap<GHOST, Integer> cornerAllocation = new EnumMap<GHOST, Integer>(GHOST.class);

    /**
     * Instantiates a new legacy2 the reckoning.
     */
    public Legacy2TheReckoning() {
        cornerAllocation.put(GHOST.BLINKY, 0);
        cornerAllocation.put(GHOST.INKY, 1);
        cornerAllocation.put(GHOST.PINKY, 2);
        cornerAllocation.put(GHOST.SUE, 3);
    }

    /* (non-Javadoc)
     * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
     */
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        int pacmanIndex = game.getPacmanCurrentNodeIndex();

        for (GHOST ghost : GHOST.values()) {
            if (game.doesGhostRequireAction(ghost)) {
                int currentIndex = game.getGhostCurrentNodeIndex(ghost);

                //if ghosts are all in close proximity and not near Ms Pac-Man, disperse
                if (isCrowded(game) && !closeToMsPacMan(game, currentIndex)) {
                    myMoves.put(ghost, getRetreatActions(game, ghost));                          				//go towards the power pill locations
                } //if edible or Ms Pac-Man is close to power pill, move away from Ms Pac-Man
                else if (game.getGhostEdibleTime(ghost) > 0 || closeToPower(game)) {
                    myMoves.put(ghost, game.getApproximateNextMoveAwayFromTarget(currentIndex, pacmanIndex, game.getGhostLastMoveMade(ghost), DM.PATH));      			//move away from ms pacman
                } //else go towards Ms Pac-Man
                else {
                    myMoves.put(ghost, game.getApproximateNextMoveTowardsTarget(currentIndex, pacmanIndex, game.getGhostLastMoveMade(ghost), DM.PATH));       			//go towards ms pacman
                }
            }
        }

        return myMoves;
    }

    /**
     * Close to power.
     *
     * @param game the game
     * @return true, if successful
     */
    private boolean closeToPower(Game game) {
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
        int[] powerPillIndices = game.getActivePowerPillsIndices();

        for (int i = 0; i < powerPillIndices.length; i++) {
            if (game.getShortestPathDistance(powerPillIndices[i], pacmanIndex) < PILL_PROXIMITY) {
                return true;
            }
        }

        return false;
    }

    /**
     * Close to ms pac man.
     *
     * @param game the game
     * @param location the location
     * @return true, if successful
     */
    private boolean closeToMsPacMan(Game game, int location) {
        if (game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), location) < PACMAN_DISTANCE) {
            return true;
        }

        return false;
    }

    /**
     * Checks if is crowded.
     *
     * @param game the game
     * @return true, if is crowded
     */
    private boolean isCrowded(Game game) {
        GHOST[] ghosts = GHOST.values();
        float distance = 0;

        for (int i = 0; i < ghosts.length - 1; i++) {
            for (int j = i + 1; j < ghosts.length; j++) {
                distance += game.getShortestPathDistance(game.getGhostCurrentNodeIndex(ghosts[i]), game.getGhostCurrentNodeIndex(ghosts[j]));
            }
        }

        return (distance / 6) < CROWDED_DISTANCE ? true : false;
    }

    /**
     * Gets the retreat actions.
     *
     * @param game the game
     * @param ghost the ghost
     * @return the retreat actions
     */
    private MOVE getRetreatActions(Game game, GHOST ghost) {
        int currentIndex = game.getGhostCurrentNodeIndex(ghost);
        int pacManIndex = game.getPacmanCurrentNodeIndex();

        if (game.getGhostEdibleTime(ghost) == 0 && game.getShortestPathDistance(currentIndex, pacManIndex) < PACMAN_DISTANCE) {
            return game.getApproximateNextMoveTowardsTarget(currentIndex, pacManIndex, game.getGhostLastMoveMade(ghost), DM.PATH);
        } else {
            return game.getApproximateNextMoveTowardsTarget(currentIndex, game.getPowerPillIndices()[cornerAllocation.get(ghost)], game.getGhostLastMoveMade(ghost), DM.PATH);
        }
    }
}
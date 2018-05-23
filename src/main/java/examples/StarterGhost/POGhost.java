package examples.StarterGhost;

import pacman.controllers.IndividualGhostController;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.Random;

/**
 * Created by piers on 16/02/17.
 */
public class POGhost extends IndividualGhostController {
    private final static float CONSISTENCY = 0.9f;    //attack Ms Pac-Man with this probability
    private final static int PILL_PROXIMITY = 15;        //if Ms Pac-Man is this close to a power pill, back away
    Random rnd = new Random();

    public POGhost(Constants.GHOST ghost) {
        super(ghost);
    }

    public Constants.MOVE getMove(Game game, long timeDue) {
        if (game.doesGhostRequireAction(ghost))        //if ghost requires an action
        {
            if (game.getGhostEdibleTime(ghost) > 0 || closeToPower(game))    //retreat from Ms Pac-Man if edible or if Ms Pac-Man is close to power pill
            {
                return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
                        game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), Constants.DM.PATH);
            } else {
                // If can observe pacman and random says ok
                //                if(rnd.nextFloat() < CONSISTENCY) {

                if (game.getPacmanCurrentNodeIndex() != -1 && rnd.nextFloat() < CONSISTENCY) {            //attack Ms Pac-Man otherwise (with certain probability)
                    Constants.MOVE move = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
                            game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), Constants.DM.PATH);
                    //                    if(game.getPacmanCurrentNodeIndex() == -1) System.out.println("Was -1: " + move);
                    return move;
                } else                                    //else take a random legal action (to be less predictable)
                {
                    Constants.MOVE[] possibleMoves = game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost), game.getGhostLastMoveMade(ghost));
                    return possibleMoves[rnd.nextInt(possibleMoves.length)];
                }
            }
        }

        return null;
    }

    //This helper function checks if Ms Pac-Man is close to an available power pill
    private boolean closeToPower(Game game) {
        int[] powerPills = game.getPowerPillIndices();

        for (int i = 0; i < powerPills.length; i++) {
            Boolean powerPillStillAvailable = game.isPowerPillStillAvailable(i);
            int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();

            if (powerPillStillAvailable == null || pacmanNodeIndex == -1) {
                return false;
            }
            if (powerPillStillAvailable && game.getShortestPathDistance(powerPills[i], pacmanNodeIndex) < PILL_PROXIMITY) {
                return true;
            }
        }

        return false;
    }
}
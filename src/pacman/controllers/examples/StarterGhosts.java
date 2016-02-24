package pacman.controllers.examples;

import java.util.EnumMap;
import pacman.controllers.NewGhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * Ghost team controller as part of the starter package - simply upload this file as a zip called
 * MyGhosts.zip and you will be entered into the rankings - as simple as that! Feel free to modify 
 * it or to start from scratch, using the classes supplied with the original software. Best of luck!
 * 
 * This ghost controller does the following:
 * 1. If edible or Ms Pac-Man is close to power pill, run away from Ms Pac-Man
 * 2. If non-edible, attack Ms Pac-Man with certain probability, else choose random direction
 */
public final class StarterGhosts extends NewGhostController {

    private final static float CONSISTENCY = 0.9f;	//attack Ms Pac-Man with this probability
    private final static int PILL_PROXIMITY = 15;		//if Ms Pac-Man is this close to a power pill, back away
    EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);

    @Override
    public void reset() {
        myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
    }

    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        for (GHOST ghost : GHOST.values()) //for each ghost
        {
            if (game.doesGhostRequireAction(ghost)) //if ghost requires an action
            {
                if (game.getGhostEdibleTime(ghost) > 0 || closeToPower(game)) //retreat from Ms Pac-Man if edible or if Ms Pac-Man is close to power pill
                {
                    myMoves.put(ghost, game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
                            game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH));
                } else {
                    if (game.rnd.nextFloat() < CONSISTENCY) //attack Ms Pac-Man otherwise (with certain probability)
                    {
                        myMoves.put(ghost, game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
                                game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH));
                    } else //else take a random legal action (to be less predictable)
                    {
                        MOVE[] possibleMoves = game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost), game.getGhostLastMoveMade(ghost));
                        myMoves.put(ghost, possibleMoves[game.rnd.nextInt(possibleMoves.length)]);
                    }
                }
            }
        }

        return myMoves;
    }

    //This helper function checks if Ms Pac-Man is close to an available power pill
    private boolean closeToPower(Game game) {
        int[] powerPills = game.getPowerPillIndices();

        for (int i = 0; i < powerPills.length; i++) {
            if (game.isPowerPillStillAvailable(i) && game.getShortestPathDistance(powerPills[i], game.getPacmanCurrentNodeIndex()) < PILL_PROXIMITY) {
                return true;
            }
        }

        return false;
    }
}
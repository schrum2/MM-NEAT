package pacman.controllers.examples;

import java.util.ArrayList;
import pacman.controllers.NewPacManController;
import pacman.game.Game;

import static pacman.game.Constants.*;

/*
 * Pac-Man controller as part of the starter package - simply upload this file as a zip called
 * MyPacMan.zip and you will be entered into the rankings - as simple as that! Feel free to modify 
 * it or to start from scratch, using the classes supplied with the original software. Best of luck!
 * 
 * This controller utilises 3 tactics, in order of importance:
 * 1. Get away from any non-edible ghost that is in close proximity
 * 2. Go after the nearest edible ghost
 * 3. Go to the nearest pill/power pill
 */
public class StarterPacMan extends NewPacManController {

    private static final int MIN_DISTANCE = 20;	//if a ghost is this close, run away

    @Override
    public MOVE getMove(Game game, long timeDue) {
        int current = game.getPacmanCurrentNodeIndex();

        //Strategy 1: if any non-edible ghost is too close (less than MIN_DISTANCE), run away
        for (GHOST ghost : GHOST.values()) {
            if (game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost) == 0) {
                if (game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost)) < MIN_DISTANCE) {
                    return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost), DM.PATH);
                }
            }
        }

        //Strategy 2: find the nearest edible ghost and go after them 
        int minDistance = Integer.MAX_VALUE;
        GHOST minGhost = null;

        for (GHOST ghost : GHOST.values()) {
            if (game.getGhostEdibleTime(ghost) > 0) {
                int distance = game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost));

                if (distance < minDistance) {
                    minDistance = distance;
                    minGhost = ghost;
                }
            }
        }

        if (minGhost != null) //we found an edible ghost
        {
            return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(minGhost), DM.PATH);
        }

        //Strategy 3: go after the pills and power pills
        int[] pills = game.getPillIndices();
        int[] powerPills = game.getPowerPillIndices();

        ArrayList<Integer> targets = new ArrayList<Integer>();

        for (int i = 0; i < pills.length; i++) //check which pills are available			
        {
            if (game.isPillStillAvailable(i)) {
                targets.add(pills[i]);
            }
        }

        for (int i = 0; i < powerPills.length; i++) //check with power pills are available
        {
            if (game.isPowerPillStillAvailable(i)) {
                targets.add(powerPills[i]);
            }
        }

        int[] targetsArray = new int[targets.size()];		//convert from ArrayList to array

        for (int i = 0; i < targetsArray.length; i++) {
            targetsArray[i] = targets.get(i);
        }

        //return the next direction once the closest target has been identified
        return game.getNextMoveTowardsTarget(current, game.getClosestNodeIndexFromNodeIndex(current, targetsArray, DM.PATH), DM.PATH);
    }

    @Override
    public int getAction(Game game, long timeDue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void logEvaluationDetails() {
        // No details: simple static controller
    }
}

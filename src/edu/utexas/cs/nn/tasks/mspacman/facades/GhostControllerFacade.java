package edu.utexas.cs.nn.tasks.mspacman.facades;

import edu.utexas.cs.nn.parameters.CommonConstants;
import java.util.EnumMap;
import java.util.Map.Entry;
import pacman.controllers.NewGhostController;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/**
 *
 * @author Jacob Schrum
 */
public class GhostControllerFacade {

    NewGhostController newG = null;

    public GhostControllerFacade(NewGhostController g) {
        newG = g;
    }

    public int[] getActions(GameFacade game, long timeDue) {
        return moveEnumToArray(newG.getMove(game.newG, timeDue));
    }

    private int[] moveEnumToArray(EnumMap<GHOST, MOVE> moves) {
        int[] result = new int[CommonConstants.numActiveGhosts];
        for (Entry<GHOST, MOVE> e : moves.entrySet()) {
            result[GameFacade.ghostToIndex(e.getKey())] = GameFacade.moveToIndex(e.getValue());
        }
        //System.out.println(moves + "->" + Arrays.toString(result));
        return result;
    }

    /**
     * This is terrible coding that needs to be fixed
     *
     * @throws NoSuchMethodException
     */
    public void reset() {
        newG.reset();
    }
}

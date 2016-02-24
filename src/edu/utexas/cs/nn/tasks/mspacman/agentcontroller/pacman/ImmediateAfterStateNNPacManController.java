/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 *
 * @author Jacob Schrum
 */
public class ImmediateAfterStateNNPacManController extends AfterStateNNPacManController {

    public ImmediateAfterStateNNPacManController(Network n) {
        super(n);
    }

    @Override
    public Pair<int[], GameFacade[]> getAfterStates(GameFacade startState) {
        int[] moves = new int[GameFacade.NUM_DIRS];
        GameFacade[] states = new GameFacade[GameFacade.NUM_DIRS];
        int[] ghostMoves = new int[CommonConstants.numActiveGhosts];
        int current = startState.getPacmanCurrentNodeIndex();
        for (int i = 0; i < ghostMoves.length; i++) {
            if(startState.doesGhostRequireAction(i)) {
                ghostMoves[i] = startState.getNextGhostDirTowards(i, current);
            }
        }
        for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
            moves[i] = i;
            states[i] = startState.copy();
            states[i].advanceGame(i, ghostMoves);
        }
        return new Pair<int[], GameFacade[]>(moves, states);
    }
}

package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.datastructures.Pair;
import java.awt.Color;

/**
 *
 * @author Jacob Schrum
 */
public abstract class AfterStateNNPacManController extends NNDirectionalPacManController {

    public AfterStateNNPacManController(Network n) {
        super(n);
    }

    @Override
    public double[] getDirectionPreferences(GameFacade gf) {
        // Each index is for a possible direction
        Pair<int[], GameFacade[]> afterPair = getAfterStates(gf.copy());
        int[] movesIntoAfterStates = afterPair.t1;
        GameFacade[] afterstates = afterPair.t2;
        double[] directionPreferences = new double[GameFacade.NUM_DIRS];
        for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
            if (CommonConstants.watch) {
                afterstates[i].addLines(Color.green, gf.getPacmanCurrentNodeIndex(), afterstates[i].getPacmanCurrentNodeIndex());
            }
            if (afterstates[i].getPacmanNumberOfLivesRemaining() < this.lives) {
                directionPreferences[i] = -1;
            } else {
                int lastDir = movesIntoAfterStates[i];
                if (nn.isMultitask()) {
                    ms.giveGame(afterstates[i]);
                    nn.chooseMode(ms.mode());
                }
                inputMediator.mediatorStateUpdate(afterstates[i]);
                double[] inputs = inputMediator.getInputs(afterstates[i], lastDir);
                nn.flush();
                // Should just be one output for preference
                double[] outputs = nn.process(inputs);
                directionPreferences[i] = outputs[0];
            }
        }

        return directionPreferences;
    }

    public abstract Pair<int[], GameFacade[]> getAfterStates(GameFacade startState);
}

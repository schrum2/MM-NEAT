package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman;

import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.actions.MsPacManAction;
import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.actions.ToFarthestSafeLocationAction;
import edu.utexas.cs.nn.tasks.mspacman.data.JunctionNodes;
import edu.utexas.cs.nn.tasks.mspacman.data.NodeCollection;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.facades.GhostControllerFacade;
import pacman.controllers.NewPacManController;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.game.Game;

/**
 *
 * @author Jacob Schrum
 */
public class TestActionPacManController extends NewPacManController {

    private final GhostControllerFacade ghostModel;
    private final NodeCollection escapeNodes;
    private final MsPacManAction action;

    public TestActionPacManController(int depth) {
        this.ghostModel = new GhostControllerFacade(new AggressiveGhosts());
        this.escapeNodes = new JunctionNodes();
        this.action = new ToFarthestSafeLocationAction(depth, escapeNodes, ghostModel);
    }

    public int getAction(final Game gs, long timeDue) {
        return getAction(new GameFacade(gs), timeDue);
    }

    public int getAction(GameFacade gs, long timeDue) {
        escapeNodes.updateNodes(gs, gs.getPacmanCurrentNodeIndex(), false);
        int move = action.getMoveAction(gs);
        return move;
    }

    @Override
    public void reset() {
        super.reset();
        escapeNodes.reset();
    }

    @Override
    public void logEvaluationDetails() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.data.ScentPath;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.multitask.MsPacManModeSelector;
import edu.utexas.cs.nn.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import java.util.Arrays;
import pacman.controllers.NewPacManController;
import pacman.game.Game;

/**
 *
 * @author Jacob Schrum
 */
public abstract class NNPacManController extends NewPacManController {

    protected Network nn;
    public int lives = -1;
    public MsPacManControllerInputOutputMediator inputMediator;
    private int maxLevel;
    protected MsPacManModeSelector ms = null;
    // Accessed and reset by Performance log
    public static int timesAllLevelsBeaten = 0;
    public static int timesTimeLimitReached = 0;
    public static int timesDied = 0;

    // Called once a generation by Performance log
    public static void resetTimes() {
        timesAllLevelsBeaten = 0;
        timesTimeLimitReached = 0;
        timesDied = 0;
    }

    public NNPacManController(Network n) {
        nn = n;
        maxLevel = Parameters.parameters.integerParameter("pacmanMaxLevel");

        inputMediator = MMNEAT.pacmanInputOutputMediator;
        if (inputMediator != null) {
            inputMediator.reset();
        }

        if (nn != null && nn.isMultitask()) {
            ms = MMNEAT.pacmanMultitaskScheme;
        }
    }

    public int getAction(final Game gs, long timeDue) {
        return getAction(new GameFacade(gs), timeDue);
    }

    public int getAction(GameFacade gs, long timeDue) {
        ScentPath.scents.visit(gs, gs.getPacmanCurrentNodeIndex());
        int curLevel = gs.getCurrentLevel();
        if (curLevel >= maxLevel) {
            //System.out.println("Beat all levels");
            int ghostsEaten = gs.getNumEatenGhosts();
            if (ghostsEaten >= CommonConstants.ghostsForBonus * curLevel) {
                System.out.println("Extra eval for eating " + ghostsEaten + " ghosts");
                maxLevel++;
            } else {
                if (MMNEAT.evalReport != null) {
                    MMNEAT.evalReport.log("Reached MAX Level");
                    MMNEAT.evalReport.log("");
                }
                if (CommonConstants.watch) {
                    System.out.println("Reached MAX Level");
                }
                return END_GAME_CODE;
            }
        }
        int checkLives = gs.getPacmanNumberOfLivesRemaining();
        if (gs.levelJustChanged() || checkLives < lives) {
            reset();
        }
        lives = checkLives; // always do this in case lives increases
        inputMediator.mediatorStateUpdate(gs);
        int levelTime = gs.getCurrentLevelTime();
        if (CommonConstants.pacmanFatalTimeLimit && levelTime >= CommonConstants.pacManLevelTimeLimit) {
            timesTimeLimitReached++;
            if (MMNEAT.evalReport != null) {
                MMNEAT.evalReport.log("Level Time Limit Reached");
                MMNEAT.evalReport.log("");
            }
            if (CommonConstants.watch) {
                System.out.println("Level Time Limit Reached");
            }
            return END_GAME_CODE;
        }

        if (gs.getPacmanCurrentNodeIndex() == -1) {
            System.out.println("Ms. Pac-Man has no location");
            return -1; // A neutral action
        }
        return getDirection(gs);
    }

    @Override
    public void reset() {
        super.reset();
        nn.flush();
        inputMediator.reset();
        ScentPath.scents.reset();
        if(ScentPath.modeScents != null) {
            for (int i = 0; i < ScentPath.modeScents.length; i++) {
                ScentPath.modeScents[i].reset();
            }
        }
    }

    public abstract int getDirection(GameFacade gs);

    @Override
    public void logEvaluationDetails() {
        MMNEAT.evalReport.log("Network Details:");
        MMNEAT.evalReport.log("\tNum Nodes: " + ((TWEANN) nn).nodes.size());
        MMNEAT.evalReport.log("\tNum Modes: " + ((TWEANN) nn).numModules());
        MMNEAT.evalReport.log("\tNum Outputs: " + ((TWEANN) nn).numOutputs());
        MMNEAT.evalReport.log("\tNeurons Per Mode: " + ((TWEANN) nn).neuronsPerMode());
        MMNEAT.evalReport.log("\tMode Usage: " + Arrays.toString(((TWEANN) nn).moduleUsage));
        MMNEAT.evalReport.log("");
    }
}

package edu.utexas.cs.nn.gridTorus;

import edu.utexas.cs.nn.gridTorus.controllers.AggressivePredatorController;
import edu.utexas.cs.nn.gridTorus.controllers.FearfulPreyController;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.MiscUtil;

public class TorusWorldExec {
    //Several options are listed - simply remove comments to use the option you want

    public static void main(String[] args) {
        Parameters.initializeParameterCollections(new String[]{"watch:true"});
        TorusWorldExec exec = new TorusWorldExec();

        TorusPredPreyController[] preds = new TorusPredPreyController[4];
        for (int i = 0; i < preds.length; i++) {
            //preds[i] = new RandomController();
            preds[i] = new AggressivePredatorController();
        }
        
        TorusPredPreyController[] prey = new TorusPredPreyController[1];
        for (int i = 0; i < prey.length; i++) {
            //prey[i] = new RandomController();
            prey[i] = new FearfulPreyController();
        }
        
        exec.runGameTimed(preds, prey, true);
    }

    private static class ActionStorage {

        public int[] action = new int[]{0,0};
    }
    public TorusPredPreyGame game;

    /*
     * For running multiple games without visuals. This is useful to get a good
     * idea of how well a controller plays against a chosen opponent: the random
     * nature of the game means that performance can vary from game to game.
     * Running many games and looking at the average score (and standard
     * deviation/error) helps to get a better idea of how well the controller is
     * likely to do in the competition.
     */
    public TorusPredPreyGame runExperiment(TorusPredPreyController[] predControllers, TorusPredPreyController[] preyControllers) {
        game = new TorusPredPreyGame(100,100,predControllers.length, preyControllers.length);

        while (!game.gameOver()) {
            int[][] predActions = new int[predControllers.length][2];
            for (int i = 0; i < predControllers.length; i++) {
                predActions[i] = predControllers[i].getAction(game.getPredators()[i], game);
            }
            int[][] preyActions = new int[preyControllers.length][2];
            for (int i = 0; i < preyControllers.length; i++) {
                preyActions[i] = preyControllers[i].getAction(game.getPrey()[i], game);
            }
            game.advance(predActions, preyActions);
        }

        return game;
    }

    boolean hold = false;
    /*
     * Run game with time limit. This is how it will be done in the competition.
     * Can be played with and without visual display of game states.
     */
    public TorusPredPreyGame runGameTimed(TorusPredPreyController[] predControllers, TorusPredPreyController[] preyControllers, boolean visual) {
        game = new TorusPredPreyGame(100,100,predControllers.length, preyControllers.length);

        ActionStorage[] preyActions = new ActionStorage[preyControllers.length];
        Agent[] preyAgents = new Agent[preyControllers.length];
        for (int i = 0; i < preyControllers.length; i++) {
            preyActions[i] = new ActionStorage();
            preyAgents[i] = new Agent(game.getPrey()[i], preyControllers[i], preyActions[i]);
        }
        ActionStorage[] predActions = new ActionStorage[predControllers.length];
        Agent[] predAgents = new Agent[predControllers.length];
        for (int i = 0; i < predControllers.length; i++) {
            predActions[i] = new ActionStorage();
            predAgents[i] = new Agent(game.getPredators()[i], predControllers[i], predActions[i]);
        }

        TorusWorldView gv = null;

        if (visual) {
            gv = new TorusWorldView(game).showGame();
        }

        while (!game.gameOver()) {
            for (int i = 0; i < predAgents.length; i++) {
                predAgents[i].alert();
            }
            for (int i = 0; i < preyAgents.length; i++) {
                preyAgents[i].alert();
            }

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int[][] predGameActions = new int[predControllers.length][2];
            for (int i = 0; i < predControllers.length; i++) {
                predGameActions[i] = predControllers[i].getAction(game.getPredators()[i], game);
            }
            int[][] preyGameActions = new int[preyControllers.length][2];
            for (int i = 0; i < preyControllers.length; i++) {
                preyGameActions[i] = preyControllers[i].getAction(game.getPrey()[i], game);
            }
            game.advance(predGameActions, preyGameActions);

            if (visual) {
                gv.repaint();
            }
            
            if(hold) {
                MiscUtil.waitForReadStringAndEnterKeyPress();
            }
        }

        for (int i = 0; i < predAgents.length; i++) {
            predAgents[i].kill();
        }
        for (int i = 0; i < preyAgents.length; i++) {
            preyAgents[i].kill();
        }

        if (visual) {
            gv.getFrame().dispose();
        }

        return game;
    }
    /*
     * Wraps the controller in a thread for the timed execution. This class then
     * updates the directions for Exec to parse to the game.
     */

    private final class Agent extends Thread {

        private TorusAgent agent;
        private TorusPredPreyController controller;
        private final ActionStorage store;
        private boolean alive;
        
        public Agent(TorusAgent agent, TorusPredPreyController controller, ActionStorage store) {
            this.agent = agent;
            this.controller = controller;
            this.store = store;
            alive = true;
            start();
        }

        public synchronized void kill() {
            alive = false;
            notify();
        }

        public synchronized void alert() {
            notify();
        }

        @Override
        public synchronized void run() {
            while (alive) {
                try {
                    synchronized (this) {
                        wait();
                    }

                    if (!game.gameOver()) {
                        store.action = controller.getAction(agent, game);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

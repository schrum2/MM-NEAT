package edu.utexas.cs.nn.gridTorus;

import edu.utexas.cs.nn.gridTorus.controllers.AggressivePredatorController;
import edu.utexas.cs.nn.gridTorus.controllers.PreyFleeAllPredatorsController;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.MiscUtil;
/**
 * used to run evaluations in this domain
 */
public class TorusWorldExec {
	//Several options are listed - simply remove comments to use the option you want
	/**
	 * runs a single evaluation in the PredPrey domain with visuals
	 */
	public static void main(String[] args) {
		//activates the visual
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
			prey[i] = new PreyFleeAllPredatorsController();
		}
		//run one timed visual evaluation (visual set to true)
		exec.runGameTimed(preds, prey, true);
	}


	//create an instance of the game
	public TorusPredPreyGame game;

	/**
	 * For running multiple games without visuals. This is useful to get a good
	 * idea of how well a controller plays against a chosen opponent: the random
	 * nature of the game means that performance can vary from game to game.
	 * Running many games and looking at the average score (and standard
	 * deviation/error) helps to get a better idea of how well the controller is
	 * likely to do in the competition.
	 * @param predControllers
	 * @param preyControllers
	 * @return the TorusPredPreyGame object that is an instance of the PredPrey game
	 */
	public TorusPredPreyGame runExperiment(TorusPredPreyController[] predControllers, TorusPredPreyController[] preyControllers) {
		game = new TorusPredPreyGame(Parameters.parameters.integerParameter("torusXDimensions"),Parameters.parameters.integerParameter("torusYDimensions")
				,predControllers.length, preyControllers.length);

		while (!game.gameOver()) {
			int[][] predActions = new int[predControllers.length][2];
			for (int i = 0; i < predControllers.length; i++) {
				predActions[i] = predControllers[i].getAction(game.getPredators()[i], game);
			}
			int[][] preyActions = new int[preyControllers.length][2];
			for (int i = 0; i < preyControllers.length; i++) {
				//if this prey is null (because it was eaten), don't call getAction for it
				if(game.getPrey()[i] != null)
					preyActions[i] = preyControllers[i].getAction(game.getPrey()[i], game);
			}
			game.advance(predActions, preyActions);
		}

		return game;
	}

	/**
	 * a class which simply holds an array of the actions taken
	 * This is commented out because it is also unnecessary to include 
	 * (as well as the threads of Agents) 
	 */
	//        private static class ActionStorage {
	//            public int[] action = new int[]{0,0};
	//        }

	boolean hold = false;
	/**
	 * run one real time evaluation of the game (with the possibility of visuals)
	 * @param predControllers
	 * @param preyControllers
	 * @param visual specification of whether or not the evaluation will be visual
	 * @return the TorusPredPreyGame object that is an instance of the PredPrey game
	 */
	public TorusPredPreyGame runGameTimed(TorusPredPreyController[] predControllers, TorusPredPreyController[] preyControllers, boolean visual) {
		game = new TorusPredPreyGame(Parameters.parameters.integerParameter("torusXDimensions"),Parameters.parameters.integerParameter("torusYDimensions")
				,predControllers.length, preyControllers.length);


		//the threads in this code are overly complicated and unnecessary in this case

		//        ActionStorage[] preyActions = new ActionStorage[preyControllers.length];
		//        Agent[] preyAgents = new Agent[preyControllers.length];
		//        for (int i = 0; i < preyControllers.length; i++) {
		//            preyActions[i] = new ActionStorage();
		//            preyAgents[i] = new Agent(game.getPrey()[i], preyControllers[i], preyActions[i]);
		//        }
		//        ActionStorage[] predActions = new ActionStorage[predControllers.length];
		//        Agent[] predAgents = new Agent[predControllers.length];
		//        for (int i = 0; i < predControllers.length; i++) {
		//            predActions[i] = new ActionStorage();
		//            predAgents[i] = new Agent(game.getPredators()[i], predControllers[i], predActions[i]);
		//        }

		TorusWorldView gv = null;

		//if visual is requested then the worldView class is activated, providing a visual window of the evaluation
		if (visual) {
			gv = new TorusWorldView(game).showGame();
		}

		//a loop that runs for the duration of the game, constantly (every 40 milliseconds) updating
		//the predator and prey actions and applying visual mechanics if specified by user
		while (!game.gameOver()) {
			//unnecessary agent threads commented out
			//            for (int i = 0; i < predAgents.length; i++) {
			//                predAgents[i].alert();
			//            }
			//            for (int i = 0; i < preyAgents.length; i++) {
			//                preyAgents[i].alert();
			//            }

			try {
				//have the main thread pause in small increments so it is visually perceptible 
				Thread.sleep(40);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//specifies the predators actions
			int[][] predGameActions = new int[predControllers.length][2];
			for (int i = 0; i < predControllers.length; i++) {
				predGameActions[i] = predControllers[i].getAction(game.getPredators()[i], game);
			}
			//specifies the preys actions
			int[][] preyGameActions = new int[preyControllers.length][2];
			for (int i = 0; i < preyControllers.length; i++) {
				//if this prey is null (because it was eaten), don't call getAction for it
				if(game.getPrey()[i] != null)
					preyGameActions[i] = preyControllers[i].getAction(game.getPrey()[i], game);
			}
			//update the game according to the actions of the predators and the preys
			game.advance(predGameActions, preyGameActions);

			if (visual) {
				gv.repaint();
			}

			if(hold) {
				MiscUtil.waitForReadStringAndEnterKeyPress();
			}
		}

		//unnecessary agent threads commented out
		//        for (int i = 0; i < predAgents.length; i++) {
		//            predAgents[i].kill();
		//        }
		//        for (int i = 0; i < preyAgents.length; i++) {
		//            preyAgents[i].kill();
		//        }

		if (visual) {
			gv.getFrame().dispose();
		}

		return game;
	}

	//unnecessary agent threads commented out
	/*
	 * Wraps the controller in a thread for the timed execution. This class then
	 * updates the directions for Exec to parse to the game.
	 */
	//    private final class Agent extends Thread {
	//
	//        private TorusAgent agent;
	//        private TorusPredPreyController controller;
	//        private final ActionStorage store;
	//        private boolean alive;
	//        
	//        public Agent(TorusAgent agent, TorusPredPreyController controller, ActionStorage store) {
	//            this.agent = agent;
	//            this.controller = controller;
	//            this.store = store;
	//            alive = true;
	//            start();
	//        }
	//
	//        public synchronized void kill() {
	//            alive = false;
	//            notify();
	//        }
	//
	//        public synchronized void alert() {
	//            notify();
	//        }
	//
	//        @Override
	//        public synchronized void run() {
	//            while (alive) {
	//                try {
	//                    synchronized (this) {
	//                        wait();
	//                    }
	//
	//                    if (!game.gameOver()) {
	//                        store.action = controller.getAction(agent, game);
	//                    }
	//                } catch (InterruptedException e) {
	//                    e.printStackTrace();
	//                }
	//            }
	//        }
	//    }
}

package edu.utexas.cs.nn.breve2D;

import edu.utexas.cs.nn.breve2D.agent.*;
import edu.utexas.cs.nn.breve2D.dynamics.Breve2DDynamics;
import edu.utexas.cs.nn.breve2D.dynamics.InterleavedFightOrFlight;
import edu.utexas.cs.nn.parameters.Parameters;

public class Breve2DExec {
	// Several options are listed - simply remove comments to use the option you
	// want

	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "watch:true", "breve2DTimeLimit:200" });
		Breve2DExec exec = new Breve2DExec();

		// exec.runGameTimed(new HumanPlayer(), new AgentController[]{new
		// RushingMonster(0), new RushingMonster(1), new RushingMonster(2), new
		// RushingMonster(3), new RushingMonster(4), new RushingMonster(5), new
		// RushingMonster(6), new RushingMonster(7)}, true);
		// exec.runGameTimed(new HumanPlayer(), new AgentController[]{new
		// RushingMonster(0), new RushingMonster(1), new RushingMonster(2), new
		// RushingMonster(3)}, true);
		AgentController[] monsters = new AgentController[4];
		for (int i = 0; i < monsters.length; i++) {
			// monsters[i] = new RushingMonster(i);
			// monsters[i] = new AttractRepelMonster(i, true);
			monsters[i] = new EscapingMonster(i);
			// monsters[i] = new StationaryAgent();
		}

		// Breve2DDynamics dyn = new FrontBackRamming();
		Breve2DDynamics dyn = new InterleavedFightOrFlight();
		// AgentController agent = new FrontBackRammingEnemy();
		AgentController agent = new PredatorPreyEnemy();

		// exec.runGameTimed(new PlayerPreyMonsterPredator(), new HumanPlayer(),
		// monsters, true);
		// exec.runGameTimed(new PlayerPredatorMonsterPrey(), new
		// RushingPlayer(), monsters, true);
		// exec.runGameTimed(new PlayerPredatorMonsterPrey(monsters.length,
		// 600), new HumanPlayer(), monsters, true);
		// exec.runGameTimed(new RammingPlayer(), new RushingPlayer(), monsters,
		// true);
		// exec.runGameTimed(new PlayerPredatorMonsterPrey(monsters.length,
		// 600), new RushingPlayer(), monsters, true);
		// exec.runGameTimed(new PredatorPrey(), new HumanPlayer(), monsters,
		// true);
		// exec.runGameTimed(new PlayerPredatorMonsterPrey(monsters.length,
		// 600), new RushingPlayer(), monsters, true);
		// exec.runGameTimed(new PlayerPreyMonsterPredator(), new
		// EscapingPlayer(), monsters, true);
		exec.runGameTimed(dyn, agent, monsters, true);
		// dyn.advanceTask();
		// exec.runGameTimed(dyn, agent, monsters, true);

		// this allows you to record a game and replay it later. This could be
		// very useful when
		// running many games in non-visual mode - one can then pick out those
		// that appear irregular
		// and replay them in visual mode to see what is happening.
		// exec.runGameTimedAndRecorded(new Human(),new
		// AttractRepelGhosts(false),true,"human-v-Legacy2.txt");
		// exec.replayGame("human-v-Legacy2.txt");
	}

	private static class ActionStorage {

		public Breve2DAction action = new Breve2DAction(0, 0);
	}

	public Breve2DGame game;

	/*
	 * For running multiple games without visuals. This is useful to get a good
	 * idea of how well a controller plays against a chosen opponent: the random
	 * nature of the game means that performance can vary from game to game.
	 * Running many games and looking at the average score (and standard
	 * deviation/error) helps to get a better idea of how well the controller is
	 * likely to do.
	 */
	public Breve2DGame runExperiment(Breve2DDynamics dynamics, AgentController playerController,
			AgentController[] monsterControllers) {

		game = new Breve2DGame(monsterControllers.length, dynamics);
		game.init();

		while (!game.gameOver()) {
			// For interleaved task games
			if (dynamics.midGameTaskSwitch(game.getTime())) {
				dynamics.advanceTask();
				if (playerController instanceof MultitaskPlayer) {
					((MultitaskPlayer) playerController).advanceTask();
				}

			}

			if (game.resetAll) {
				for (int i = 0; i < monsterControllers.length; i++) {
					monsterControllers[i].reset();
				}
				playerController.reset();
				game.resetAll = false;
			}

			Breve2DAction[] monsterActionArray = new Breve2DAction[monsterControllers.length];
			for (int i = 0; i < monsterControllers.length; i++) {
				if (!game.monsterDead(i)) {
					monsterActionArray[i] = monsterControllers[i].getAction(game);
				} else {
					monsterActionArray[i] = new Breve2DAction(0, 0);
				}
			}
			game.advanceGame(playerController.getAction(game), monsterActionArray);
		}

		return game;
	}

	/*
	 * Run game with time limit. Can be played with and without visual display
	 * of game states.
	 */
	public Breve2DGame runGameTimed(Breve2DDynamics dynamics, AgentController playerController,
			AgentController[] monsterControllers, boolean visual) {
		game = new Breve2DGame(monsterControllers.length, dynamics);
		game.init();

		ActionStorage playerAction = new ActionStorage();
		Agent player = new Agent(playerController, playerAction);
		ActionStorage[] monsterActions = new ActionStorage[monsterControllers.length];
		Agent[] monsters = new Agent[monsterControllers.length];
		for (int i = 0; i < monsterControllers.length; i++) {
			monsterActions[i] = new ActionStorage();
			monsters[i] = new Agent(monsterControllers[i], monsterActions[i]);
		}

		Breve2DGameView gv = null;

		if (visual) {
			gv = new Breve2DGameView(game).showGame();

			if (playerController instanceof HumanPlayer) {
				gv.getFrame().addKeyListener((HumanPlayer) playerController);
			}
		}

		while (!game.gameOver()) {
			// For interleaved task games
			if (dynamics.midGameTaskSwitch(game.getTime())) {
				dynamics.advanceTask();
				if (playerController instanceof MultitaskPlayer) {
					((MultitaskPlayer) playerController).advanceTask();
				}
			}

			if (game.resetAll) {
				for (int i = 0; i < monsterControllers.length; i++) {
					monsterControllers[i].reset();
				}
				playerController.reset();
				game.resetAll = false;
			}

			player.alert();
			for (int i = 0; i < monsterControllers.length; i++) {
				monsters[i].alert();
			}

			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Breve2DAction[] monsterActionArray = new Breve2DAction[monsterControllers.length];
			for (int i = 0; i < monsterControllers.length; i++) {
				monsterActionArray[i] = monsterActions[i].action;
			}

			game.advanceGame(playerAction.action, monsterActionArray);

			if (visual) {
				gv.repaint();
			}
		}

		player.kill();
		for (int i = 0; i < monsterControllers.length; i++) {
			monsters[i].kill();
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

		private AgentController agent;
		private boolean alive;
		private final ActionStorage store;

		public Agent(AgentController agent, ActionStorage store) {
			this.agent = agent;
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
						store.action = agent.getAction(game);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

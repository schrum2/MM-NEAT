package pacman;

import edu.utexas.cs.nn.log.DeathLocationsLog;
import edu.utexas.cs.nn.log.MMNEATLog;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.StaticPacManController;
import edu.utexas.cs.nn.tasks.mspacman.data.ScentPath;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.CombinatoricUtilities;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;
import java.util.Scanner;
import pacman.controllers.Controller;
import pacman.controllers.HumanController;
import pacman.controllers.examples.Legacy;
import static pacman.game.Constants.DELAY;
import pacman.game.Constants.GHOST;
import static pacman.game.Constants.INTERVAL_WAIT;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

/**
 * This class may be used to execute the game in timed or un-timed modes, with
 * or without visuals. Competitors should implement their controllers in
 * game.entries.ghosts and game.entries.pacman respectively. The skeleton
 * classes are already provided. The package structure should not be changed
 * (although you may create sub-packages in these packages).
 */
@SuppressWarnings("unused")
public class Executor {

	public static boolean logOutput;
	public static MMNEATLog watch;
	public static MMNEATLog noWatch;
	public static DeathLocationsLog deaths = null;

	public Executor() {
		hold = Parameters.parameters.booleanParameter("stepByStepPacMan");
		logOutput = Parameters.parameters.booleanParameter("logPacManEvals");
		String saveTo = Parameters.parameters.stringParameter("saveTo");
		if (!saveTo.isEmpty() && Parameters.parameters.booleanParameter("logDeathLocations")) {
			deaths = new DeathLocationsLog();
		}
		if (!saveTo.isEmpty() && logOutput && watch == null && noWatch == null) {
			watch = new MMNEATLog("EvalPacMan-WatchScores");
			noWatch = new MMNEATLog("EvalPacMan-NoWatchScores");
		}
	}

	public void log(String name) {
		if (logOutput) {
			watch.log(name);
			noWatch.log(name);
		}
	}

	public static void close() {
		if (logOutput) {
			watch.close();
			noWatch.close();
		}
		if (deaths != null) {
			deaths.close();
		}
	}

	/**
	 * The main method. Several options are listed - simply remove comments to
	 * use the option you want.
	 *
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "watch:true", "escapeToPowerPills:true" });
		// DirectionalPathCache.init();
		Executor exec = new Executor();

		/*
		 * //run multiple games in batch mode - good for testing. int
		 * numTrials=10; exec.runExperiment(new RandomPacMan(),new
		 * RandomGhosts(),numTrials);
		 */

		/*
		 * //run a game in synchronous mode: game waits until controllers
		 * respond. int delay=5; boolean visual=true; exec.runGame(new
		 * RandomPacMan(),new RandomGhosts(),visual,delay);
		 */
		/// *
		// run the game in asynchronous mode.
		boolean visual = true;
		//// exec.runGameTimed(new NearestPillPacMan(),new
		//// AggressiveGhosts(),visual);
		//// exec.runGameTimed(new EIIPacman(), new Legacy(), visual);
		// Constants.MAX_TIME = 2000;
		// Constants.NUM_LIVES = 1;
		// Game g = new Game(0);
		// for (int i = 0; ; i++) {
		// exec.runExperiment(new TestActionPacManController(2), new Legacy(),
		//// g);
		// //exec.runGameTimed(new TestActionPacManController(2), new Legacy(),
		//// visual);
		// System.out.println("Game " + i + " done");
		// }
		// exec.runGameTimed(new HumanController(new KeyBoardInput()), new
		//// StarterGhosts(), visual);
		exec.runGameTimed(new StaticPacManController(2), new Legacy(), visual);
		// */

		/*
		 * //run the game in asynchronous mode but advance as soon as both
		 * controllers are ready - this is the mode of the competition. //time
		 * limit of DELAY ms still applies. boolean visual=true; boolean
		 * fixedTime=false; exec.runGameTimedSpeedOptimised(new
		 * RandomPacMan(),new RandomGhosts(),fixedTime,visual);
		 */

		/*
		 * //run game in asynchronous mode and record it to file for replay at a
		 * later stage. boolean visual=true; String fileName="replay.txt";
		 * exec.runGameTimedRecorded(new HumanController(new
		 * KeyBoardInput()),new RandomGhosts(),visual,fileName);
		 * //exec.replayGame(fileName,visual);
		 */
	}

	/**
	 * For running multiple games without visuals. This is useful to get a good
	 * idea of how well a controller plays against a chosen opponent: the random
	 * nature of the game means that performance can vary from game to game.
	 * Running many games and looking at the average score (and standard
	 * deviation/error) helps to get a better idea of how well the controller is
	 * likely to do in the competition.
	 *
	 * @param pacManController
	 *            The Pac-Man controller
	 * @param ghostController
	 *            The Ghosts controller
	 * @param trials
	 *            The number of trials to be executed
	 */
	public void runExperiment(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController,
			int trials) {
		double avgScore = 0;

		Random rnd = new Random(0);
		Game game;

		for (int i = 0; i < trials; i++) {
			game = new Game(rnd.nextLong());

			while (!game.gameOver()) {
				game.advanceGame(pacManController.getMove(game.copy(), System.currentTimeMillis() + DELAY),
						ghostController.getMove(game.copy(), System.currentTimeMillis() + DELAY));
			}

			avgScore += game.getScore();
			System.out.println(i + "\t" + game.getScore());
		}

		System.out.println(avgScore / trials);
	}

	// public static ArrayList<Double> actionTimes = new ArrayList<Double>();
	public void runExperiment(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController,
			Game game) {
		// game._init();
		while (!game.gameOver()) {
			// long start = System.currentTimeMillis();
			long due = System.currentTimeMillis() + DELAY;
			game.advanceGame(pacManController.getMove(game.copy(), due), ghostController.getMove(game.copy(), due));
			// long end = System.currentTimeMillis();
			// actionTimes.add(new Double(end - start));
		}

		// System.out.println("Average Action Time: " +
		// StatisticsUtilities.average(ArrayUtil.doubleArrayFromArrayList(actionTimes)));
	}

	/**
	 * Run a game in asynchronous mode: the game waits until a move is returned.
	 * In order to slow thing down in case the controllers return very quickly,
	 * a time limit can be used. If fasted gameplay is required, this delay
	 * should be put as 0.
	 *
	 * @param pacManController
	 *            The Pac-Man controller
	 * @param ghostController
	 *            The Ghosts controller
	 * @param visual
	 *            Indicates whether or not to use visuals
	 * @param delay
	 *            The delay between time-steps
	 */
	public void runGame(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController,
			boolean visual, int delay) {
		Game game = new Game(0);

		GameView gv = null;

		if (visual) {
			gv = new GameView(game).showGame();
		}

		while (!game.gameOver()) {
			game.advanceGame(pacManController.getMove(game.copy(), -1), ghostController.getMove(game.copy(), -1));

			try {
				Thread.sleep(delay);
			} catch (Exception e) {
			}

			if (visual && !game.gameOver()) {
				gv.repaint();
			}
		}
	}

	/**
	 * Run the game with time limit (asynchronous mode). This is how it will be
	 * done in the competition. Can be played with and without visual display of
	 * game states.
	 *
	 * @param pacManController
	 *            The Pac-Man controller
	 * @param ghostController
	 *            The Ghosts controller
	 * @param visual
	 *            Indicates whether or not to use visuals
	 */
	public Game runGameTimed(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController,
			boolean visual) {
		return runGameTimed(pacManController, ghostController, visual, new Game(0));
	}

	public static boolean hold = false;

	public Game runGameTimed(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController,
			boolean visual, Game game) {
		GameView gv = null;
		// game._init();

		if (visual) {
			gv = new GameView(game).showGame();
		}

		if (pacManController instanceof HumanController) {
			gv.getFrame().addKeyListener(((HumanController) pacManController).getKeyboardInput());
		}

		new Thread(pacManController).start();
		new Thread(ghostController).start();

		MOVE forcedMove = null;
		while (!game.gameOver()) {
			// Troubleshooting removal of pills near power pills
			// GameView.addPoints(game, Color.yellow,
			// ArrayUtil.intArrayFromArrayList(Game.color));
			// Executor.hold = true;

			pacManController.update(game.copy(), System.currentTimeMillis() + DELAY);
			ghostController.update(game.copy(), System.currentTimeMillis() + DELAY);

			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			game.advanceGame(forcedMove != null ? forcedMove : pacManController.getMove(), ghostController.getMove());
			forcedMove = null;

			if (deaths != null) {
				deaths.heatMap(new GameFacade(game));
			}
			if (hold) {
				String result = MiscUtil.waitForReadStringAndEnterKeyPress();
				if (result != null) {
					if (result.equals("n") || result.equals("next")) {
						break; // skip the trial
					} else if (result.equals("u") || result.equals("up")) {
						forcedMove = MOVE.UP; // force move up
					} else if (result.equals("d") || result.equals("down")) {
						forcedMove = MOVE.DOWN; // force move down
					} else if (result.equals("l") || result.equals("left")) {
						forcedMove = MOVE.LEFT; // force move left
					} else if (result.equals("r") || result.equals("right")) {
						forcedMove = MOVE.RIGHT; // force move right
					} else if (result.equals("h") || result.equals("hold")) {
						hold = false; // stop holding
					}
					// else if (result.equals("b")) { // beat the level
					// game._newLevelReset(); // have to make this method public
					// for the call to be legal
					// }
				}
			}

			if (visual && !game.gameOver()) {
				gv.repaint();
			}
		}

		pacManController.terminate();
		ghostController.terminate();

		gv.getFrame().dispose();
		return game;
	}

	/**
	 * Run the game in asynchronous mode but proceed as soon as both controllers
	 * replied. The time limit still applies so so the game will proceed after
	 * 40ms regardless of whether the controllers managed to calculate a turn.
	 *
	 * @param pacManController
	 *            The Pac-Man controller
	 * @param ghostController
	 *            The Ghosts controller
	 * @param fixedTime
	 *            Whether or not to wait until 40ms are up even if both
	 *            controllers already responded
	 * @param visual
	 *            Indicates whether or not to use visuals
	 * @param game
	 *            instance of pacman game to use
	 */
	public void runGameTimedSpeedOptimised(Controller<MOVE> pacManController,
			Controller<EnumMap<GHOST, MOVE>> ghostController, boolean fixedTime, boolean visual, Game game) {
		GameView gv = null;

		if (visual) {
			gv = new GameView(game).showGame();
		}

		if (pacManController instanceof HumanController) {
			gv.getFrame().addKeyListener(((HumanController) pacManController).getKeyboardInput());
		}

		new Thread(pacManController).start();
		new Thread(ghostController).start();

		while (!game.gameOver()) {
			pacManController.update(game.copy(), System.currentTimeMillis() + DELAY);
			ghostController.update(game.copy(), System.currentTimeMillis() + DELAY);

			try {
				int waited = DELAY / INTERVAL_WAIT;

				for (int j = 0; j < DELAY / INTERVAL_WAIT; j++) {
					Thread.sleep(INTERVAL_WAIT);

					if (pacManController.hasComputed() && ghostController.hasComputed()) {
						waited = j;
						break;
					}
				}

				if (fixedTime) {
					Thread.sleep(((DELAY / INTERVAL_WAIT) - waited) * INTERVAL_WAIT);
				}

				game.advanceGame(pacManController.getMove(), ghostController.getMove());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (visual && !game.gameOver()) {
				gv.repaint();
			}
		}

		pacManController.terminate();
		ghostController.terminate();
	}

	/**
	 * Run a game in asynchronous mode and recorded.
	 *
	 * @param pacManController
	 *            The Pac-Man controller
	 * @param ghostController
	 *            The Ghosts controller
	 * @param visual
	 *            Whether to run the game with visuals
	 * @param fileName
	 *            The file name of the file that saves the replay
	 */
	public void runGameTimedRecorded(Game game, Controller<MOVE> pacManController,
			Controller<EnumMap<GHOST, MOVE>> ghostController, boolean visual, String fileName) {
		StringBuilder replay = new StringBuilder();

		// Game game = new Game(0);
		GameView gv = null;

		if (visual) {
			gv = new GameView(game).showGame();

			if (pacManController instanceof HumanController) {
				gv.getFrame().addKeyListener(((HumanController) pacManController).getKeyboardInput());
			}
		}

		new Thread(pacManController).start();
		new Thread(ghostController).start();

		while (!game.gameOver()) {
			pacManController.update(game.copy(), System.currentTimeMillis() + DELAY);
			ghostController.update(game.copy(), System.currentTimeMillis() + DELAY);

			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			game.advanceGame(pacManController.getMove(), ghostController.getMove());

			if (visual && !game.gameOver()) {
				gv.repaint();
			}

			replay.append(game.getGameState() + "\n");
		}

		pacManController.terminate();
		ghostController.terminate();

		saveToFile(replay.toString(), fileName, false);
	}

	/**
	 * Replay a previously saved game.
	 *
	 * @param fileName
	 *            The file name of the game to be played
	 * @param visual
	 *            Indicates whether or not to use visuals
	 */
	public void replayGame(String fileName, boolean visual, int delay) {
		Scanner modes = null;
		boolean modePheremone = Parameters.parameters.booleanParameter("modePheremone");
		int scentMode = -1;
		if (modePheremone) {
			try {
				modes = new Scanner(new File(Parameters.parameters.stringParameter("pacmanSaveFile") + ".modes"));
				int numModes = 6; // Should set this according to actual num
									// modes
				ScentPath.modeScents = new ScentPath[numModes];
				for (int i = 0; i < ScentPath.modeScents.length; i++) {
					ScentPath.modeScents[i] = new ScentPath(0.99, true, CombinatoricUtilities.mapTuple(i + 1));
				}
				scentMode = Parameters.parameters.integerParameter("scentMode");
			} catch (FileNotFoundException ex) {
				System.out.println("Cannot replay mode scent paths");
			}
		}

		ArrayList<String> timeSteps = loadReplay(fileName);

		Game game = new Game(0);

		GameView gv = null;

		if (visual) {
			gv = new GameView(game).showGame();
		}

		int level = -1;
		int lives = 10;
		for (int j = 0; j < timeSteps.size(); j++) {
			game.setGameState(timeSteps.get(j));
			if (game.getCurrentLevel() != level || game.getPacmanNumberOfLivesRemaining() < lives) {
				level = game.getCurrentLevel();
				lives = game.getPacmanNumberOfLivesRemaining();
				for (int i = 0; i < ScentPath.modeScents.length; i++) {
					ScentPath.modeScents[i].reset();
				}
				if (level >= Parameters.parameters.integerParameter("pacmanMaxLevel")) {
					return;
				}
			}

			if (modePheremone) {
				if (modes != null && modes.hasNext()) {
					int mode = modes.nextInt();
					modes.nextLine(); // Get carriage return
					GameFacade gf = new GameFacade(game);
					if (scentMode == -1) {
						// for (int i = 0; i < ScentPath.modeScents.length; i++)
						// {
						// ScentPath.modeScents[i].visit(gf,
						// game.getPacmanCurrentNodeIndex(), i == mode ? 1.0 :
						// 0.0);
						// }
						ScentPath.modeScents[mode].visit(gf, game.getPacmanCurrentNodeIndex(), 1);
					} else {
						ScentPath.modeScents[scentMode].visit(gf, game.getPacmanCurrentNodeIndex(),
								scentMode == mode ? 1.0 : 0.0);
					}
				}
			}

			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (hold) {
				String result = MiscUtil.waitForReadStringAndEnterKeyPress();
			}

			if (visual) {
				gv.repaint();
			}
		}
	}

	// save file for replays
	public static void saveToFile(String data, String name, boolean append) {
		try {
			FileOutputStream outS = new FileOutputStream(name, append);
			PrintWriter pw = new PrintWriter(outS);

			pw.println(data);
			pw.flush();
			outS.close();

		} catch (IOException e) {
			System.out.println("Could not save data!");
		}
	}

	// load a replay
	private static ArrayList<String> loadReplay(String fileName) {
		ArrayList<String> replay = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			String input = br.readLine();

			while (input != null) {
				if (!input.equals("")) {
					replay.add(input);
				}

				input = br.readLine();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return replay;
	}
}

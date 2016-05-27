package pacman.game;

/**
 * This class contains the enumerations for the moves and the ghosts as well as
 * as the constants of the game. If you should change the constants, bear in
 * mind that this might significantly affect the game and hence the performance
 * of your controller. The set of constants used in the competition may be found
 * on the competition website.
 */
public final class Constants {

	/**
	 * Enumeration for the moves that are possible in the game. At each time
	 * step, a controller is required to supply one of the 5 actions available.
	 * If the controller replies NEUTRAL or does not reply in time, the previous
	 * action is repeated. If the previous action is not a legal move, a legal
	 * move is chosen uniformly at random.
	 */
	public enum MOVE {

		UP {
			public MOVE opposite() {
				return MOVE.DOWN;
			};
		},
		RIGHT {
			public MOVE opposite() {
				return MOVE.LEFT;
			};
		},
		DOWN {
			public MOVE opposite() {
				return MOVE.UP;
			};
		},
		LEFT {
			public MOVE opposite() {
				return MOVE.RIGHT;
			};
		},
		NEUTRAL {
			public MOVE opposite() {
				return MOVE.NEUTRAL;
			};

		};

		public abstract MOVE opposite();
	};

	/**
	 * Enumeration for the ghosts. The integer arguments are the initial lair
	 * times.
	 */
	public enum GHOST {

		BLINKY(40), PINKY(60), INKY(80), SUE(100);
		public final int initialLairTime;

		GHOST(int lairTime) {
			this.initialLairTime = lairTime;
		}
	};

	/**
	 * DM stands for Distance Metric, a simple enumeration for use with methods
	 * that require a distance metric. The metric available are as follows:
	 * PATH: the actual path distance (i.e., number of step required to reach
	 * target) EUCLID: Euclidean distance using the nodes' x and y coordinates
	 * MANHATTAN: Manhattan distance (absolute distance between x and y
	 * coordinates)
	 */
	public enum DM {

		PATH, EUCLID, MANHATTAN
	};

	public static final int PILL = 10; // points for a normal pill
	public static final int POWER_PILL = 50; // points for a power pill
	public static final int GHOST_EAT_SCORE = 200; // score for the first ghost
													// eaten (doubles every time
													// for the duration of a
													// single power pill)
	public static int EDIBLE_TIME = 200; // initial time a ghost is edible for
											// (decreases as level number
											// increases)
	public static final float EDIBLE_TIME_REDUCTION = 0.9f; // reduction factor
															// by which edible
															// time decreases as
															// level number
															// increases
	public static final float LAIR_REDUCTION = 0.9f; // reduction factor by
														// which lair times
														// decrease as level
														// number increases
	public static final int LEVEL_RESET_REDUCTION = 6;
	public static int COMMON_LAIR_TIME = 40; // time spend in lair after being
												// eaten
	// public static final int LEVEL_LIMIT = 4000; //time limit for a level
	public static final float GHOST_REVERSAL = 0.0015f; // probability of a
														// global ghost reversal
														// event
	public static int MAX_TIME = 24000; // maximum time a game can be played for
	public static final int AWARD_LIFE_LEFT = 800; // points awarded for every
													// life left at the end of
													// the game (when time runs
													// out)
	public static final int EXTRA_LIFE_SCORE = 10000; // extra life is awarded
														// when this many points
														// have been collected
	public static final int EAT_DISTANCE = 2; // distance in the connected graph
												// considered close enough for
												// an eating event to take place
	public static final int NUM_GHOSTS = 4; // number of ghosts in the game
	public static final int NUM_MAZES = 4; // number of different mazes in the
											// game
	public static final int DELAY = 40; // delay (in milliseconds) between game
										// advancements
	public static int NUM_LIVES = 3; // total number of lives Ms Pac-Man has
										// (current + NUM_LIVES-1 spares)
	public static final int GHOST_SPEED_REDUCTION = 2; // difference in speed
														// when ghosts are
														// edible (every
														// GHOST_SPEED_REDUCTION,
														// a ghost remains
														// stationary)
	public static final int EDIBLE_ALERT = 30; // for display only (ghosts
												// turning blue)
	public static final int INTERVAL_WAIT = 1; // for quicker execution: check
												// every INTERVAL_WAIT ms to see
												// if controllers have returned
	// for Competition
	public static final int WAIT_LIMIT = 5000; // time limit in milliseconds for
												// the controller to initialise;
	public static final int MEMORY_LIMIT = 512; // memory limit in MB for
												// controllers (including the
												// game)
	public static final int IO_LIMIT = 10; // limit in MB on the files written
											// by controllers
	// for Maze
	public static final String pathMazes = "data/pacman/mazes";
	public static final String pathDistances = "data/pacman/distances";
	public static final String[] nodeNames = { "a", "b", "c", "d" };
	public static final String[] distNames = { "da", "db", "dc", "dd" };
	// for GameView
	public static final int MAG = 2;
	public static final int GV_WIDTH = 114;
	public static final int GV_HEIGHT = 130;
	public static String pathImages = "data/pacman/images";
	public static String[] mazeNames = { "maze-a.png", "maze-b.png", "maze-c.png", "maze-d.png" };

	private Constants() {
	}
}
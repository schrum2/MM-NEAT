package edu.southwestern.tasks.gvgai;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.stats.StatisticsUtilities;
import gvgai.core.competition.CompetitionParameters;
import gvgai.core.game.BasicGame;
import gvgai.core.game.Game;
import gvgai.core.player.AbstractPlayer;
import gvgai.core.player.Player;
import gvgai.core.vgdl.VGDLFactory;
import gvgai.core.vgdl.VGDLParser;
import gvgai.core.vgdl.VGDLRegistry;
import gvgai.core.vgdl.VGDLViewer;
import gvgai.tools.Vector2d;
import gvgai.tracks.ArcadeMachine;
import gvgai.tracks.singlePlayer.tools.human.Agent;

/**
 * 
 * Utility class for GVG-AI games. Methods shared by all.
 * 
 * @author Jacob Schrum
 *
 */
public class GVGAIUtil {

	public static final double PRESENCE_THRESHOLD = 0.0;
	public static final int PRESENCE_INDEX = 0;
	public static final int FIRST_FIXED_INDEX = 1;
	public static final double RANDOM_ITEM_THRESHOLD = 0.3;

	/**
	 * Contains all of the data/classes needed to launch a game
	 * @author schrum2
	 */
	public static class GameBundle {
		public Game game;
		public String[] level; 
		public AbstractPlayer agent; 
		public int randomSeed; 
		public int playerID;
		
		public GameBundle(Game game, String[] level, AbstractPlayer agent, int randomSeed, int playerID) {
			this.game = game;
			this.level = level;
			this.agent = agent;
			this.randomSeed = randomSeed;
			this.playerID = playerID;
		}
	}
	
	/**
	 * Run one game with a single class as input. The GameBundle class is
	 * a collection of information needed to play the game.
	 * 
	 * @param bundle Contains the Game, level as a String array, agent, random seed, and player id
	 * @param visuals Whether to watch the game
	 * @return scores
	 */
	public static double[] runOneGame(GameBundle bundle, boolean visuals) {
		return runOneGame(bundle.game, bundle.level, visuals, bundle.agent, bundle.randomSeed, bundle.playerID);
	}
	
	/**
	 * Based on a more complicated method in Arcade Machine with the same name.
	 * The problem with that method is that is instantiates all information from
	 * file paths and class names. This method assumes those components have already
	 * been constructed, and uses the instantiated classes to play one game.
	 * 
	 * Note: Limited to playing single player games
	 * 
	 * @param toPlay Game instance that already has game rules loaded
	 * @param level String array of line by line contents of a level file
	 * @param visuals Whether to watch the game
	 * @param agent Agent that has already been initialized
	 * @param randomSeed Used in level construction, for example for enemy placement
	 * @param playerID Used when watching the game played
	 * @return Scores from evaluation: {victory, score, timestep} for every player
	 */
	public static double[] runOneGame(Game toPlay, String[] level, boolean visuals, AbstractPlayer agent, int randomSeed, int playerID) {
		toPlay.buildStringLevel(level, randomSeed); // TODO: Is path finding still required?
		return runOneGame(toPlay, visuals, agent, randomSeed, playerID);
	}
	
	public static double[] runDungeon(GameBundle bundle, boolean visuals, Dungeon dungeon) {
		// TODO Auto-generated method stub
		return runMultiLevelGame(bundle.game, dungeon, visuals, bundle.agent, bundle.randomSeed, bundle.playerID);
	}
	
	
	public static double[] runMultiLevelGame(Game toPlay, Dungeon dungeon, boolean visuals, AbstractPlayer agent, int randomSeed, int playerID) {
		
		toPlay.buildStringLevel(dungeon.getCurrentlevel().level.getStringLevel(new Point(8, 5)), randomSeed); // TODO: Is path finding still required?
		runOneGame(toPlay, visuals, agent, randomSeed, playerID);
		Vector2d exitVec = toPlay.getAvatar().getLastPosition();
		String exitPoint;
		if(exitVec == null) exitPoint = "null";
		else exitPoint = exitVec.toString();
		
		System.out.println("ExitPoint : " + exitPoint.toString());
		
		while(!exitPoint.equals("null")) {
			Point start = dungeon.getNextNode(exitPoint);
			Node node = dungeon.getCurrentlevel();
			if (start == null) {
				System.out.println("No start, exiting");
				break;
			}
			toPlay.reset();
			toPlay.buildStringLevel(node.level.getStringLevel(start), randomSeed); // TODO: Is path finding still required?
			System.out.println("Running Game ---------------------------------");
			runOneGame(toPlay, visuals, agent, randomSeed, playerID); // TODO: Is path finding still required?
			System.out.println("Ending Game ---------------------------------");
			exitVec = toPlay.getAvatar().getLastPosition();

			if(exitVec == null) exitPoint = "null";
			else exitPoint = exitVec.toString();
			
			System.out.println("ExitPoint : " + exitPoint.toString());
		}
		return toPlay.getFullResult();
	}		

	/**
	 * Like the method above, but the level has already been loaded into the game instance.
	 * 
	 * @param toPlay Game instance with rules and level already loaded
	 * @param visuals Whether to watch the game
	 * @param agent Agent that has already been initialized
	 * @param randomSeed Used in level construction, for example for enemy placement
	 * @param playerID Used when watching the game played
	 * @return Scores from evaluation: {victory, score, timestep} for every player
	 */
	public static double[] runOneGame(Game toPlay, boolean visuals, AbstractPlayer agent, int randomSeed, int playerID) {		
		// Warm the game up.
		ArcadeMachine.warmUp(toPlay, CompetitionParameters.WARMUP_TIME);

		// single player game
		Player[] players = new AbstractPlayer[] {agent};

		// Then, play the game.
		if (visuals)
			// Agent is the generically named class for a human controlled agent
			toPlay.playGame(players, randomSeed, agent instanceof Agent, playerID);
		else
			toPlay.runGame(players, randomSeed);

		// This, the last thing to do in this method, always:
		toPlay.handleResult();
		toPlay.printResult();
		
		System.out.println("Last position : " + toPlay.getAvatar().getLastPosition());
		System.out.println("Dimensions : " + toPlay.getScreenSize());

		return toPlay.getFullResult();
	}	
	
	
	
	/**
	 * Get a still preview image of what the game level looks like
	 * 
	 * @param game Game with level already loaded
	 * @param humanPlayer Agent representing human player (why is this needed?)
	 * @param width Width of image in pixels
	 * @param height Height of image in pixels
	 * @return buffered image of level
	 */
	public static BufferedImage getLevelImage(BasicGame game, String[] level, Agent humanPlayer, int width, int height, int seed) {
		int square_size = game.square_size;
		game.square_size = Math.min(height / level.length, width / level[0].length());
		game.buildStringLevel(level, seed); // Must change square size before building level to affect size
		VGDLViewer view = new VGDLViewer(game, humanPlayer);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics(); // Changed to graphics will affect the image
		view.paint(game.spriteGroups);
		view.paintComponent(g); // Put the sprites into the graphics object
		game.square_size = square_size; // restore before returning
		return image;
	}
	
	/**
	 * Take a CPPN and some sprite information to create a level
	 * for a grid-based game.
	 * 
	 * @param n The Neural network CPPN
	 * @param levelWidth Width in grid units
	 * @param levelHeight Height in grid units
	 * @param defaultBackground char that corresponds to nothing/floor/background
	 * @param border char that surrounds level as a well (TODO: not all games have this)
	 * @param fixed array of chars for fixed items, like walls, etc.
	 * @param unique array of chars for items/sprites of which there must be exactly one
	 * @param random array of chars for items/sprites of which there can be a variable/random number
	 * @param randomItems Number of items from the random array to place
	 * @return String array with level layout
	 */
	public static String[] generateLevelFromCPPN(Network n, double[] inputMultiples, int levelWidth, int levelHeight, 
			char defaultBackground, char border, char[] fixed, char[] unique, char[] random, int randomItems, char[] bottomItems) {
		// Start with 2D char array to fill out level: The +2 is for the border wall.
		char[][] level = new char[levelHeight+2][levelWidth+2];
		// Background
		for(int i = 0; i < level.length; i++) {
			Arrays.fill(level[i], defaultBackground);
		}
		// Border wall: TODO: Does not apply to all games ... remove?
		for(int y = 0; y < levelHeight+2; y++) { // Vertical walls
			level[y][0] = border;
			level[y][levelWidth+1] = border;
		}		
		for(int x = 1; x < levelWidth+1; x++) { // Horizontal walls
			level[0][x] = border;
			level[levelHeight+1][x] = border;
		}
		// Query CPPN
		double[] uniqueScores = new double[unique.length];
		// Location with highest score will have the unique item
		Arrays.fill(uniqueScores, Double.NEGATIVE_INFINITY);
		int[][] uniqueLocations = new int[unique.length][2];
		// Query spots within the border
		for(int y = 1; y < levelHeight+1; y++) {
			for(int x = 1; x < levelWidth+1; x++) {
				// Able to use a method from GraphicsUtil here. The -1 is time, which is ignored.
				double[] inputs = GraphicsUtil.get2DObjectCPPNInputs(x, y, levelWidth, levelHeight, -1);
				// Multiplies the inputs by the inputMultiples; used to turn on or off the effects in each input
				for(int i = 0; i < inputMultiples.length; i++) {
					inputs[i] = inputs[i] * inputMultiples[i];
				}
				double[] outputs = n.process(inputs);
				// Check if a fixed item is present
				if(outputs[PRESENCE_INDEX] > PRESENCE_THRESHOLD) {
					// Figure out which one it is
					double[] fixedActivations = new double[fixed.length];
					System.arraycopy(outputs, FIRST_FIXED_INDEX, fixedActivations, 0, fixed.length);
					int whichFixed = StatisticsUtilities.argmax(fixedActivations);
					level[y][x] = fixed[whichFixed]; // Place item in level
				}
				// Only place unique items on empty spaces
				if(level[y][x] == defaultBackground) {
					// Find maximal output for each unique item
					for(int i = 0; i < unique.length; i++) {
						// Store maximal location queried for each unique item
						if(outputs[i+fixed.length] > uniqueScores[i] && unclaimed(x,y,uniqueLocations)) {
							uniqueScores[i] = outputs[i+fixed.length];
							uniqueLocations[i] = new int[]{x,y};
						}
					}		
					// Now place random items: unique items have priority, random items are limited
					if(randomItems > 0) {
						//System.out.println("random check " + outputs[fixed.length+unique.length]);
						// Last CPPN output
						if(outputs[fixed.length+unique.length] > RANDOM_ITEM_THRESHOLD) {
							// Select one of the random item options
							level[y][x] = random[RandomNumbers.randomGenerator.nextInt(random.length)];
							randomItems--; // allow one fewer random item
						}
					}
				}
			}
		}
		// Place the unique items
		for(int i = 0; i < unique.length; i++) {
			level[uniqueLocations[i][1]][uniqueLocations[i][0]] = unique[i];
		}		
		
		// TODO: Add game-specific hacks here
		// TODO: For example, for aliens, put the ship near the bottom
		
		
		// Convert to String array
		String[] stringLevel = new String[levelHeight+2];
		for(int i = 0; i < level.length; i++) {
			stringLevel[i] = new String(level[i]);
		}
		return stringLevel;
	}
	
	/**
	 * Make sure that no unique item is currently claiming the spot (x,y)
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param uniqueLocations Current claimed unique item locations
	 * @return Whether the location is free
	 */
	private static boolean unclaimed(int x, int y, int[][] uniqueLocations) {
		for(int i = 0; i < uniqueLocations.length; i++) {
			if(uniqueLocations[i][0] == x && uniqueLocations[i][1] == y) {
				return false;
			}
		}
		return true;
	}

	/**
	 * For testing and troubleshooting
	 * @param args
	 */
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] {});
		//MMNEAT.loadClasses();
		
		VGDLFactory.GetInstance().init();
		VGDLRegistry.GetInstance().init();

		String game = "blacksmoke"; // "zelda";
		String gamesPath = "data/gvgai/examples/gridphysics/";
		String game_file = gamesPath + game + ".txt";
		int playerID = 0;
		int seed = 0;	
		
		////////////////////////////////////////////////////////
		// Allows for playing a bait level defined by a random CPPN
		TWEANNGenotype cppn = new TWEANNGenotype(4, 9, 0);
		TWEANN net = cppn.getPhenotype();
		String[] level = generateLevelFromCPPN(net, new double[] {1,1,1,1}, 20, 20, '.', 'w', 
				new char[]{'w','b','c'}, new char[]{'l','k','e','A'}, new char[]{'d'}, 15, new char[0]);

		Agent agent = new Agent();
		agent.setup(null, seed, true); // null = no log, true = human 

		// Image preview
		Game toPlay = new VGDLParser().parseGame(game_file); // Initialize the game
		BufferedImage levelImage = getLevelImage(((BasicGame) toPlay), level, agent, 200, 200, seed);
		DrawingPanel panel = GraphicsUtil.drawImage(levelImage, "Level Preview", 200, 200); 
		
		// Reinitialize to clean up mess from image preview
		toPlay = new VGDLParser().parseGame(game_file);
		GameBundle bundle = new GameBundle(toPlay, level, agent, seed, playerID);
		runOneGame(bundle, true);
		//////////////////////////////////////////////////////	
		
		panel.dispose();
		
		//////////////////////////////////////////////////////

		//Recreated the LevelBreederTask.Java in its simplest form to narrow down the errors that we receive
		
		//////////////////////////////////////////////////////
		
		VGDLFactory.GetInstance().init();
		VGDLRegistry.GetInstance().init();

		game = "zelda";
		gamesPath = "data/gvgai/examples/gridphysics/";
		game_file = gamesPath + game + ".txt";
		
		cppn = new TWEANNGenotype(4, 8 , 0);
		net = cppn.getPhenotype();
		level = generateLevelFromCPPN(net, new double[] {1,1,1,1}, 20, 20, '.', 'w', 
				new char[]{'w'}, new char[]{'g','+','A'}, new char[]{'1','2','3'}, 15, new char[0]);

		agent = new Agent();
		agent.setup(null, seed, true); // null = no log, true = human 

		// Image preview
		toPlay = new VGDLParser().parseGame(game_file); // Initialize the game
		levelImage = getLevelImage(((BasicGame) toPlay), level, agent, 200, 200, seed);
		panel = GraphicsUtil.drawImage(levelImage, "Level Preview", 200, 200); 
		
		// Reinitialize to clean up mess from image preview
		toPlay = new VGDLParser().parseGame(game_file);
		bundle = new GameBundle(toPlay, level, agent, seed, playerID);
		runOneGame(bundle, true);
		
		//////////////////////////////////////////////////////
		
		panel.dispose();
	}
	
	public static void troubleshooting1() {
		Parameters.initializeParameterCollections(new String[] {});
		//MMNEAT.loadClasses();
		
		VGDLFactory.GetInstance().init();
		VGDLRegistry.GetInstance().init();

		String game = "blacksmoke"; // "zelda";
		String gamesPath = "data/gvgai/examples/gridphysics/";
		String game_file = gamesPath + game + ".txt";
		int playerID = 0;
		int seed = 0;
	
		////////////////////////////////////////////////////////
		// Allows for playing of any of the existing Zelda levels
//		int levelNum = 2;
//		String level_file = gamesPath + game + "_lvl" + levelNum + ".txt";
//		
//		Game toPlay = new VGDLParser().parseGame(game_file); // Initialize the game
//		String[] level = new IO().readFile(level_file);
//
//		Agent agent = new Agent();
//		agent.setup(null, seed, true); // null = no log, true = human 
//
//		runOneGame(toPlay, level, true, agent, seed, playerID);
		//////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////
		// Allows for playing a Zelda level defined as a String array
//		Game toPlay = new VGDLParser().parseGame(game_file); // Initialize the game
//		String[] level = new String[] {
//			"wwwwwwwwwwwwwwwwwwwww", 
//			"w..3.ww............Aw", 
//			"w....w......wwwwwwwww", 
//			"w.w.....wwwwwwwwwwwww", 
//			"w.w........1.......1w", 
//			"w.wwwwwwwwwwwwwwwww.w", 
//			"w.......w...wwwwwww.w", 
//			"w...2w....wgww+....3w", 
//			"wwwwwwwwwwwwwwwwwwwww"	
//		};
		
//		String[] level = new String[] {
//				"wwwwwwwwwwwwwwwwwwwwww", 
//				"w...............3wwwww", 
//				"wA.......g......3wwwww", 
//				"w................1wwww", 
//				"w................12www", 
//				"w................33www", 
//				"w................212ww", 
//				"w...................ww", 
//				"w...................ww", 
//				"w...........+.......ww", 
//				"w...................ww", 
//				"w..................www", 
//				"w..................www", 
//				"w.................wwww", 
//				"w................wwwww", 
//				"w...............wwwwww", 
//				"w..............wwwwwww", 
//				"ww...........wwwwwwwww", 
//				"wwww.......wwwwwwwwwww", 
//				"wwwwwwwwwwwwwwwwwwwwww", 
//				"wwwwwwwwwwwwwwwwwwwwww", 
//				"wwwwwwwwwwwwwwwwwwwwww"	
//			};
//
//		Agent agent = new Agent();
//		agent.setup(null, seed, true); // null = no log, true = human 
//
//		runOneGame(toPlay, level, true, agent, seed, playerID);
		//////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////
		// Allows for playing a Zelda level defined by a random CPPN
//		Game toPlay = new VGDLParser().parseGame(game_file); // Initialize the game
//		TWEANNGenotype cppn = new TWEANNGenotype(4, 5, 0);
//		TWEANN net = cppn.getPhenotype();
//		String[] level = generateLevelFromCPPN(net, 20, 20, '.', 'w', 
//				new char[]{'w'}, new char[]{'g','+','A'}, new char[]{'1','2','3'}, 4);
//
//		Agent agent = new Agent();
//		agent.setup(null, seed, true); // null = no log, true = human 
//
//		runOneGame(toPlay, level, true, agent, seed, playerID);
		//////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////
		// Allows for playing a bait level defined by a random CPPN
//		Game toPlay = new VGDLParser().parseGame(game_file); // Initialize the game
//		TWEANNGenotype cppn = new TWEANNGenotype(4, 5, 0);
//		TWEANN net = cppn.getPhenotype();
//		String[] level = generateLevelFromCPPN(net, 10, 10, '.', 'w', 
//				new char[]{'w'}, new char[]{'k','g','A'}, new char[]{'0','1'}, 15);
//
//		Agent agent = new Agent();
//		agent.setup(null, seed, true); // null = no log, true = human 
//
//		runOneGame(toPlay, level, true, agent, seed, playerID);
		//////////////////////////////////////////////////////		
		
		////////////////////////////////////////////////////////
		// Allows for playing a bait level defined by a random CPPN
		TWEANNGenotype cppn = new TWEANNGenotype(4, 9, 0);
		TWEANN net = cppn.getPhenotype();
		String[] level = generateLevelFromCPPN(net, new double[] {1,1,1,1}, 20, 20, '.', 'w', 
				new char[]{'w','b','c'}, new char[]{'l','k','e','A'}, new char[]{'d'}, 15, new char[0]);

		Agent agent = new Agent();
		agent.setup(null, seed, true); // null = no log, true = human 

		// Image preview
		Game toPlay = new VGDLParser().parseGame(game_file); // Initialize the game
		BufferedImage levelImage = getLevelImage(((BasicGame) toPlay), level, agent, 200, 200, seed);
		DrawingPanel panel = GraphicsUtil.drawImage(levelImage, "Level Preview", 200, 200); 
		
		// Reinitialize to clean up mess from image preview
		toPlay = new VGDLParser().parseGame(game_file);
		GameBundle bundle = new GameBundle(toPlay, level, agent, seed, playerID);
		runOneGame(bundle, true);
		//////////////////////////////////////////////////////	
		
		panel.dispose();
	}


}

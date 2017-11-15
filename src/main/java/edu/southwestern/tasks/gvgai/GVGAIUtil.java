package edu.southwestern.tasks.gvgai;

import java.util.Arrays;

import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.graphics.GraphicsUtil;
import gvgai.core.competition.CompetitionParameters;
import gvgai.core.game.Game;
import gvgai.core.player.AbstractPlayer;
import gvgai.core.player.Player;
import gvgai.core.vgdl.VGDLFactory;
import gvgai.core.vgdl.VGDLParser;
import gvgai.core.vgdl.VGDLRegistry;
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

	public static final double FIXED_ITEM_THRESHOLD = 0.5;

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

		return toPlay.getFullResult();
	}	
	
	public static String[] generateLevelFromCPPN(Network n, int levelWidth, int levelHeight, 
			char defaultBackground, char border, char[] fixed, char[] unique, int maxScale) {
		// Start with 2D char array to fill out level: The +2 is for the border wall.
		char[][] level = new char[levelHeight+2][levelWidth+2];
		// Background
		for(int i = 0; i < level.length; i++) {
			Arrays.fill(level[i], defaultBackground);
		}
		// Border wall
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
				double[] outputs = n.process(inputs);
				// Check for presence of each fixed item
				for(int i = 0; i < fixed.length; i++) {
					if(outputs[i] > FIXED_ITEM_THRESHOLD) {
						level[y][x] = fixed[i]; // Place item in level
					}
				}
				// Only place unique and scaled items on empty spaces
				if(level[y][x] == defaultBackground) {
					
					
					// Find maximal output for each unique item
					for(int i = 0; i < unique.length; i++) {
						// Store maximal location queried for each unique item
						if(outputs[i+fixed.length] > uniqueScores[i] && unclaimed(x,y,uniqueLocations)) {
							uniqueScores[i] = outputs[i+fixed.length];
							uniqueLocations[i] = new int[]{x,y};
						}
					}				
				}
			}
		}
		// Place the unique items
		for(int i = 0; i < unique.length; i++) {
			level[uniqueLocations[i][1]][uniqueLocations[i][0]] = unique[i];
		}		
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

		String game = "zelda";
		String gamesPath = "data/gvgai/examples/gridphysics/";
		String game_file = gamesPath + game + ".txt";
		int playerID = 0;
		int seed = 0;
	
		////////////////////////////////////////////////////////
		// Allows for playing of any of the existing Zelda levels
//		int levelNum = 4;
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
//
//		Agent agent = new Agent();
//		agent.setup(null, seed, true); // null = no log, true = human 
//
//		runOneGame(toPlay, level, true, agent, seed, playerID);
		//////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////
		// Allows for playing a Zelda level defined as a String array
		Game toPlay = new VGDLParser().parseGame(game_file); // Initialize the game
		TWEANNGenotype cppn = new TWEANNGenotype(4, 4, 0);
		TWEANN net = cppn.getPhenotype();
		String[] level = generateLevelFromCPPN(net, 20, 20, '.', 'w', new char[]{'w'}, new char[]{'g','+','A'}, 3);

		Agent agent = new Agent();
		agent.setup(null, seed, true); // null = no log, true = human 

		runOneGame(toPlay, level, true, agent, seed, playerID);
		//////////////////////////////////////////////////////
		
		
	}
}

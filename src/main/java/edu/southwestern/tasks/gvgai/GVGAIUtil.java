package edu.southwestern.tasks.gvgai;

import gvgai.core.competition.CompetitionParameters;
import gvgai.core.game.Game;
import gvgai.core.player.AbstractPlayer;
import gvgai.core.player.Player;
import gvgai.core.vgdl.VGDLFactory;
import gvgai.core.vgdl.VGDLParser;
import gvgai.core.vgdl.VGDLRegistry;
import gvgai.tools.IO;
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
	
	/**
	 * For testing and troubleshooting
	 * @param args
	 */
	public static void main(String[] args) {
		VGDLFactory.GetInstance().init();
		VGDLRegistry.GetInstance().init();

		// Allows for playing of any of the existing Zelda levels
		String game = "zelda";
		int levelNum = 4;
		String gamesPath = "data/gvgai/examples/gridphysics/";
		String game_file = gamesPath + game + ".txt";
		String level_file = gamesPath + game + "_lvl" + levelNum + ".txt";
		
		Game toPlay = new VGDLParser().parseGame(game_file); // Initialize the game
		String[] level = new IO().readFile(level_file);

		int playerID = 0;
		int seed = 0;
		Agent agent = new Agent();
		agent.setup(null, seed, true); // null = no log, true = human 

		runOneGame(toPlay, level, true, agent, seed, playerID);
		//////////////////////////////////////////////////////
		
		// Allows for playing a Zelda level defined as a String array
		
		// TODO:
		
	}
}

package edu.southwestern.tasks.gvgai;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.gvgai.player.GVGAINNPlayer;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;
import edu.southwestern.util.random.RandomNumbers;
import gvgai.core.competition.CompetitionParameters;
import gvgai.core.game.Game;
import gvgai.core.player.AbstractPlayer;
import gvgai.core.player.Player;
import gvgai.core.vgdl.VGDLFactory;
import gvgai.core.vgdl.VGDLParser;
import gvgai.core.vgdl.VGDLRegistry;
import gvgai.tools.IO;
import gvgai.tracks.ArcadeMachine;

public class GVGAISinglePlayerTask<T extends Network> extends NoisyLonerTask<T> implements NetworkTask, HyperNEATTask{

	static String gamesPath = "data/gvgai/examples/gridphysics/"; // Comes from gvgai.tracks.singlePlayer.Test
	String game;
	int level;
	private Game toPlay;
	private GVGAINNPlayer<T> agent;

	@SuppressWarnings("unchecked")
	public GVGAISinglePlayerTask(){
		VGDLFactory.GetInstance().init();
		VGDLRegistry.GetInstance().init();

		game = Parameters.parameters.stringParameter("gvgaiGame");
		System.out.println("GVG-AI game is " + game);
		level = Parameters.parameters.integerParameter("gvgaiLevel");
		System.out.println("GVG-AI level is " + level);

		String game_file = gamesPath + game + ".txt";
		toPlay = new VGDLParser().parseGame(game_file); // Initialize the game

		try {
			agent = (GVGAINNPlayer<T>) ClassCreation.createObject("gvgaiPlayer");
			// null = do not save file of actions
			// random generator produces random seed : TODO: If deterministic, then always use same seed
			// false = not human
			agent.setup(null, RandomNumbers.randomGenerator.nextInt(), false); 
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("Could not create GVG AI player: " + Parameters.parameters.classParameter("gvgaiPlayer"));
			System.exit(1);
		}


		// Registers the three possible scores;
		// Each Score can be individually selected as a Selection Function or not
		// Defaults to only the Victory score being used for selection

		MMNEAT.registerFitnessFunction("Victory", Parameters.parameters.booleanParameter("gvgaiVictory"));
		MMNEAT.registerFitnessFunction("Score", Parameters.parameters.booleanParameter("gvgaiScore"));
		MMNEAT.registerFitnessFunction("Timestep", Parameters.parameters.booleanParameter("gvgaiTimestep"));
	}

	@Override
	public int numObjectives() {
		int numObjectives = 0;

		if(Parameters.parameters.booleanParameter("gvgaiVictory")) numObjectives++;
		if(Parameters.parameters.booleanParameter("gvgaiScore")) numObjectives++;
		if(Parameters.parameters.booleanParameter("gvgaiTimestep")) numObjectives++;

		return numObjectives;
	}

	@Override
	public int numOtherScores() {
		int numObjectives = 0;

		if(!Parameters.parameters.booleanParameter("gvgaiVictory")) numObjectives++;
		if(!Parameters.parameters.booleanParameter("gvgaiScore")) numObjectives++;
		if(!Parameters.parameters.booleanParameter("gvgaiTimestep")) numObjectives++;

		return numObjectives;
	}

	@Override
	public double getTimeStamp() {
		// Most Tasks don't use the Time Stamp
		return 0;
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {

		agent.assignNetwork(individual.getPhenotype());
		
		boolean visuals = CommonConstants.watch;
		int playerID = 0;

		String level_file = gamesPath + game + "_lvl" + level + ".txt";
		String[] level = new IO().readFile(level_file);
		int randomSeed = RandomNumbers.randomGenerator.nextInt(); // TODO: Allow to be deterministic

		// Will have 3 Indexes: {victory, score, timestep}; Stores these for every Player, in triplets: [w0,s0,t0,w1,s1,t1,...]
		double[] gvgaiScores = runOneGame(toPlay, level, visuals, agent, randomSeed, playerID);
		toPlay.reset();
		
		// Process the scores
		double[] fitness = new double[numObjectives()];
		double[] otherScores = new double[numOtherScores()];
		int fitIndex = 0;
		int otherIndex = 0;


		if(Parameters.parameters.booleanParameter("gvgaiVictory")){
			fitness[fitIndex++] = gvgaiScores[0]; // Index of the Victory score in gvgaiScores
		}else{
			otherScores[otherIndex++] = gvgaiScores[0];
		}


		if(Parameters.parameters.booleanParameter("gvgaiScore")){
			fitness[fitIndex++] = gvgaiScores[1]; // Index of the Game Score in gvgaiScores
		}else{
			otherScores[otherIndex++] = gvgaiScores[1];
		}


		if(Parameters.parameters.booleanParameter("gvgaiTimestep")){
			fitness[fitIndex++] = gvgaiScores[2]; // Index of the Timestep score in gvgaiScores
		}else{
			otherScores[otherIndex++] = gvgaiScores[2];
		}

		return new Pair<double[], double[]>(fitness, otherScores);
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
		// First, we create the game to be played..
		// TODO: Move this up so the level isn't recreated each time? But what if multiple levels are to be played?
		toPlay.buildStringLevel(level, randomSeed); // TODO: Is path finding still required?

		// Warm the game up.
		ArcadeMachine.warmUp(toPlay, CompetitionParameters.WARMUP_TIME);

		// single player game
		Player[] players = new AbstractPlayer[] {agent};

		// Then, play the game.
		double[] score; // TODO: Why is this here? Remove?
		if (visuals)
			// false = no humans
			score = toPlay.playGame(players, randomSeed, false, playerID);
		else
			score = toPlay.runGame(players, randomSeed);

		// This, the last thing to do in this method, always:
		toPlay.handleResult();
		toPlay.printResult();

		return toPlay.getFullResult();
	}

	@Override
	public int numCPPNInputs() {
		return HyperNEATTask.DEFAULT_NUM_CPPN_INPUTS;
	}

	@Override
	public double[] filterCPPNInputs(double[] fullInputs) {
		// Default behavior
		return fullInputs;
	}

	@Override
	public List<Substrate> getSubstrateInformation() {
		// TODO: Fix the Height and Width of the input substrates; find a way to get the height and width from the game
		int height = 1; // Currently only uses the raw scores; doesn't use the whole board yet
		int width = 4; // Currently only uses four inputs: gameScore, gameHealth, gameSpeed, and gameTick
		List<Triple<String, Integer, Integer>> outputInfo = new LinkedList<Triple<String, Integer, Integer>>();
		outputInfo.add(new Triple<String, Integer, Integer>("Utility Output", 1, 1));
		// Otherwise, no substrates will be defined, and the code will crash from the null result

		return HyperNEATUtil.getSubstrateInformation(width, height, 1, outputInfo); // Only has 1 Input Substrate with the Height and Width of the Board Game
	}

	@Override
	public List<Triple<String, String, Boolean>> getSubstrateConnectivity() {
		List<String> outputNames = new LinkedList<String>();
		outputNames.add("Utility Output");	

		return HyperNEATUtil.getSubstrateConnectivity(1, outputNames); // Only has 1 Input Substrate
	}

	@Override
	public String[] sensorLabels() {
		return new String[]{"Game Score", "Game Health", "Game Speed", "Game Tick", "BIAS"};
	}

	@Override
	public String[] outputLabels() {
		return new String[]{"Utility"};
	}

	// For testing and troubleshooting
	public static void main(String[] ignore) throws FileNotFoundException, NoSuchMethodException {
		String args = "runNumber:0 randomSeed:0 trials:2 maxGens:500 mu:100 io:false netio:false mating:true task:edu.southwestern.tasks.gvgai.GVGAISinglePlayerTask cleanOldNetworks:true fs:false log:Zelda-ZeldaReactive saveTo:ZeldaReactive gvgaiGame:zelda gvgaiLevel:0 gvgaiPlayer:edu.southwestern.tasks.gvgai.GVGAIReactiveNNPlayer watch:true";
		MMNEAT.main(args.split(" "));
	}
}
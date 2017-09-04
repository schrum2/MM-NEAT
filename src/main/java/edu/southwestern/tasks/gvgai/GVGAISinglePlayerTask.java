package edu.southwestern.tasks.gvgai;

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
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;
import gvgai.tracks.ArcadeMachine;

public class GVGAISinglePlayerTask<T extends Network> extends NoisyLonerTask<T> implements NetworkTask, HyperNEATTask{
	
	static String gamesPath = "data/gvgai/examples/gridphysics/"; // Comes from gvgai.tracks.singlePlayer.Test
	String game;
	int level;
	
	public GVGAISinglePlayerTask(){
		game = Parameters.parameters.stringParameter("gvgaiGame");
		level = Parameters.parameters.integerParameter("gvgaiLevel");
		
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
		// TODO: Find a better way to set the Network for all possible Players?
		GVGAIOneStepNNPlayer.network = individual.getPhenotype(); // Cannot construct a Player because GVGAI constructs players with Strings
		GVGAITreeSearchNNPlayer.network = individual.getPhenotype(); // Cannot construct a Player because GVGAI constructs players with Strings
		GVGAIReactiveNNPlayer.network = individual.getPhenotype();  // Cannot construct a Player because GVGAI constructs players with Strings
		
		String agentNames = Parameters.parameters.stringParameter("gvgaiPlayer");
		
		boolean visuals = CommonConstants.watch;
		int randomSeed = 0;
		int playerID = 0;
		
		String game_file = gamesPath + game + ".txt";
		String level_file = gamesPath + game + "_lvl" + level + ".txt";
		String actionFile = null;
		
		if(Parameters.parameters.booleanParameter("gvgaiSave")){
			actionFile = "actions_" + game + "_lvl" + level + "_" + randomSeed + ".txt";
		}
		
		// Will have 3 Indexes: {victory, score, timestep}; Stores these for every Player, in triplets: [w0,s0,t0,w1,s1,t1,...]
		double[] gvgaiScores = ArcadeMachine.runOneGame(game_file, level_file, visuals, agentNames, actionFile, randomSeed, playerID);
		GVGAIOneStepNNPlayer.network = null;
		
		
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



}
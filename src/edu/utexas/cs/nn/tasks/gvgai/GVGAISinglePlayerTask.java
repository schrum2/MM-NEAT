package edu.utexas.cs.nn.tasks.gvgai;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.datastructures.Pair;
import gvgai.tracks.ArcadeMachine;

public class GVGAISinglePlayerTask<T extends Network> extends NoisyLonerTask<T> {
	
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
	public double getTimeStamp() {
		// Most Tasks don't use the Time Stamp
		return 0;
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		String agentNames = ""; // TODO: Find a way to replace with the NN Player
		
		boolean visuals = CommonConstants.watch;
		int randomSeed = 0; // TODO: Change later?
		int playerID = (int) individual.getId();
		
		String game_file = gamesPath + game + ".txt";
		String level_file = gamesPath + game + "_lvl" + level + ".txt";
		String actionFile = null;
		
		if(Parameters.parameters.booleanParameter("gvgaiSave")){
			actionFile = "actions_" + game + "_lvl" + level + "_" + randomSeed + ".txt";
		}
		
		// Will have 3 Indexes: {victory, score, timestep}; Stores these for every Player, in triplets: [w0,s0,t0,w1,s1,t1,...]
		double[] gvgaiScores = ArcadeMachine.runOneGame(game_file, level_file, visuals, agentNames, actionFile, randomSeed, playerID);
		
		return new Pair<double[], double[]>(gvgaiScores, new double[]{});
	}

}

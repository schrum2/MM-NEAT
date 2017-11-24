package edu.southwestern.tasks.mario.level;

import java.util.ArrayList;

import ch.idsia.mario.engine.level.Level;
import edu.southwestern.parameters.Parameters;

public class MarioLevelUtil {
	
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] {});
		//MMNEAT.loadClasses();
				
		////////////////////////////////////////////////////////
		String[] stringBlock = new String[] {
				"--------------------------------------------------------", 
				"--------------------------------------------------------", 
				"--------------------------------------------------------", 
				"--------------------------------------------------------", 
				"--------------------------------------------------------", 
				"---------Q----------------------------------------------", 
				"--------------------------------------------------------", 
				"--------------------------------------------------------", 
				"----?---SSS---------------------------------------------", 
				"------------------X-------------------------------------", 
				"-----------------XX---------------------E-----<>--------", 
				"---SSSS---------XXX---------------------X-----[]--------", 
				"--------------XXXXX-------------------XXXXX---[]--------", 
				"------------XXXXXXX----------EE-----XXXXXXXXXX[]--------", 
				"XXXXXXXXXXXXXXXXXXX-----XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
			};
		
		ArrayList<String> lines = new ArrayList<String>();
		for(int i = 0; i < stringBlock.length; i++) {
			lines.add(stringBlock[i]);
		}

		LevelParser parse = new LevelParser();
		Level level = parse.createLevelASCII(lines);
		
		
		
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
		
		
	}
}

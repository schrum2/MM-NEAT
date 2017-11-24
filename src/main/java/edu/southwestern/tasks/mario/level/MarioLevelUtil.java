package edu.southwestern.tasks.mario.level;

import java.util.ArrayList;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import competition.cig.sergeykarakovskiy.SergeyKarakovskiy_JumpingAgent;
import edu.southwestern.parameters.Parameters;

public class MarioLevelUtil {
	
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] {});
		//MMNEAT.loadClasses();
				
		////////////////////////////////////////////////////////
		String[] stringBlock = new String[] {
				"---------Q----------------------------------------------", 
				"--------------------------------------------------------", 
				"--------------------------------------------------------", 
				"----?---SSS---------------------------------------------", 
				"------------------X-------------------------------------", 
				"-----------------XX---------------------E-----<>--------", 
				"---SSSS--<>-----XXX---------------------X-----[]--------", 
				"---------[]---XXXXX-------------------XXXXX---[]--------", 
				"---------[]-XXXXXXX----------EE-----XXXXXXXXXX[]--------", 
				"XXXXXXXXXXXXXXXXXXX-----XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
				"XXXXXXXXXXXXXXXXXXX-----XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
				"XXXXXXXXXXXXXXXXXXX-----XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
				"XXXXXXXXXXXXXXXXXXX-----XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
			};
		
		ArrayList<String> lines = new ArrayList<String>();
		for(int i = 0; i < stringBlock.length; i++) {
			lines.add(stringBlock[i]);
		}

		LevelParser parse = new LevelParser();
		Level level = parse.createLevelASCII(lines);
		
		Agent controller = new HumanKeyboardAgent(); //new SergeyKarakovskiy_JumpingAgent();
		EvaluationOptions options = new CmdLineOptions(new String[]{});
		options.setAgent(controller);
		ProgressTask task = new ProgressTask(options);

		// Added to change level
        options.setLevel(level);

		task.setOptions(options);

		System.out.println ("Score: " + task.evaluate (options.getAgent())[0]);
		
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

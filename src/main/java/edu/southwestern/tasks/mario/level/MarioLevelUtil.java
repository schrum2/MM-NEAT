package edu.southwestern.tasks.mario.level;

import java.awt.GraphicsConfiguration;
import java.util.ArrayList;
import java.util.Arrays;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.mario.engine.LevelRenderer;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.mutation.tweann.ActivationFunctionRandomReplacement;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.stats.StatisticsUtilities;

public class MarioLevelUtil {
	
	public static final int BLOCK_SIZE = 16;
	public static final int LEVEL_HEIGHT = 12;
	public static final double MAX_HEIGHT_INDEX = LEVEL_HEIGHT - 1;

	public static final int PRESENT_INDEX = 0;
	public static final double PRESENT_THRESHOLD = 0.0;

	public static final char SOLID_CHAR = 'X';
	public static final int SOLID_INDEX = 1;
	public static final double SOLID_THRESHOLD = 0.5;
	
	public static final int BLOCK_OR_QUESTION_INDEX = 2;
	public static final char QUESTION_CHAR = '?';
	public static final double QUESTION_THRESHOLD = 0.8;
	public static final char BLOCK_CHAR = 'S';
	public static final double BLOCK_THRESHOLD = 0.5;

	public static final char COIN_CHAR = 'o';
	public static final int COIN_INDEX = 3;
	public static final double COIN_THRESHOLD = 0.8;

	public static final char ENEMY_CHAR = 'E';
	public static final int ENEMY_INDEX = 4;
	public static final double ENEMY_THRESHOLD = 0.8;	
	
	public static final char EMPTY_CHAR = '-';
	
	/**
	 * Generate Mario level layout in form of String array using CPPN.
	 * @param net CPPN
	 * @param width Width in Mario blocks
	 * @return String array where each String is a row of the level
	 */
	public static String[] generateLevelLayoutFromCPPN(TWEANN net, int width) {
		String[] level = new String[LEVEL_HEIGHT];
		double halfWidth = width/2.0;
		// Top row has problems if it contains objects
		char[] top = new char[width];
		Arrays.fill(top, EMPTY_CHAR);
		level[0] = new String(top);
		for(int i = LEVEL_HEIGHT - 1; i > 0; i--) { // From bottom up: for enemy ground check
			level[i] = "";
			for(int j = 0; j < width; j++) {
				double x = (j - halfWidth) / halfWidth; // Horizontal symmetry
				double y = (MAX_HEIGHT_INDEX - i) / MAX_HEIGHT_INDEX; // Increasing from ground up
				
				double[] inputs = new double[] {x,y,1.0};
				double[] outputs = net.process(inputs);

				//System.out.println("["+i+"]["+j+"]"+Arrays.toString(inputs)+Arrays.toString(outputs));
				
				if(outputs[PRESENT_INDEX] > PRESENT_THRESHOLD) {
					outputs[PRESENT_INDEX] = Double.NEGATIVE_INFINITY; // Assure this index is not the biggest
					int highest = StatisticsUtilities.argmax(outputs);
					if(highest == SOLID_INDEX) {
						level[i] += SOLID_CHAR;
					} else if(highest == BLOCK_OR_QUESTION_INDEX) {
						level[i] += BLOCK_CHAR;
						// TODO: Question blocks
					} else if(highest == COIN_INDEX) {
						level[i] += COIN_CHAR;
					} else if(highest == ENEMY_INDEX // Must be true
							&& i+1 < level.length) { // Not the bottom row
						level[i] += ENEMY_CHAR;
					} else { // In case enemy condition checks failed
						level[i] += EMPTY_CHAR;
					}					
				} else {
					level[i] += EMPTY_CHAR;
				}
			}
		}
		
		return level;
	}

	/**
	 * Take a cppn and a width and completely generate the level
	 * @param net CPPN
	 * @param width In Mario blocks
	 * @return Level instance
	 */
	public static Level generateLevelFromCPPN(TWEANN net, int width) {
		String[] stringBlock = generateLevelLayoutFromCPPN(net, width);
						
		ArrayList<String> lines = new ArrayList<String>();
		for(int i = 0; i < stringBlock.length; i++) {
			//System.out.println(stringBlock[i]);
			lines.add(stringBlock[i]);
		}

		LevelParser parse = new LevelParser();
		Level level = parse.createLevelASCII(lines);
		return level;
	}
	
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] 
				{"runNumber:0","randomSeed:"+((int)(Math.random()*100)),"trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3","includeFullSigmoidFunction:true","includeFullGaussFunction:true","includeCosineFunction:true","includeGaussFunction:false","includeIdFunction:true","includeTriangleWaveFunction:true","includeSquareWaveFunction:true","includeFullSawtoothFunction:true","includeSigmoidFunction:false","includeAbsValFunction:true","includeSawtoothFunction:true"});
		MMNEAT.loadClasses();
				
		////////////////////////////////////////////////////////
//		String[] stringBlock = new String[] {
//				"--------------------------------------------------------", 
//				"--------------------------------------------------------", 
//				"--------------------------------------------------------", 
//				"---------ooooo------------------------------------------", 
//				"--------------------------------------------------------", 
//				"----?---S?S---------------------------------------------", 
//				"------------------X-------------------------------------", 
//				"-----------------XX---------------------E-----<>--------", 
//				"---SSSS--<>-----XXX---------------------X-----[]--------", 
//				"---------[]---XXXXX-------------------XXXXX---[]--------", 
//				"---------[]-XXXXXXX----------EE-----XXXXXXXXXX[]--------", 
//				"XXXXXXXXXXXXXXXXXXX-----XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
//			};
				
		// Instead of specifying the level, create it with a TWEANN	
		TWEANNGenotype cppn = new TWEANNGenotype(
				3, // Inputs: x, y, bias 
				5, // Outputs: Present?, solid, breakable, coin, enemy (TODO: pipes)
				0); // Archetype
		// Randomize activation functions
		new ActivationFunctionRandomReplacement().mutate(cppn);
		
		// Random mutations
//		for(int i = 0; i < 50; i++) {
//			cppn.mutate();
//		}
		
		TWEANN net = cppn.getPhenotype();
		DrawingPanel panel = new DrawingPanel(200,200, "Network");
		net.draw(panel, true, false);

		Level level = generateLevelFromCPPN(net, 60);
		
		Agent controller = new HumanKeyboardAgent(); //new SergeyKarakovskiy_JumpingAgent();
		EvaluationOptions options = new CmdLineOptions(new String[]{});
		options.setAgent(controller);
		ProgressTask task = new ProgressTask(options);

		// Added to change level
        options.setLevel(level);

		task.setOptions(options);

		int relevantWidth = (level.width - (2*LevelParser.BUFFER_WIDTH)) * BLOCK_SIZE;
		System.out.println("level.width:"+level.width);
		System.out.println("relevantWidth:"+relevantWidth);
		DrawingPanel levelPanel = new DrawingPanel(relevantWidth,LEVEL_HEIGHT*BLOCK_SIZE, "Level");
		LevelRenderer.renderArea(levelPanel.getGraphics(), level, 0, 0, LevelParser.BUFFER_WIDTH*BLOCK_SIZE, 0, relevantWidth, LEVEL_HEIGHT*BLOCK_SIZE);
		
		System.out.println ("Score: " + task.evaluate(options.getAgent())[0]);
		
				
	}
}

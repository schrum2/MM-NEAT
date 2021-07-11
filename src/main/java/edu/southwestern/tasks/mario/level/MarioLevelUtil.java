package edu.southwestern.tasks.mario.level;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.mario.engine.LevelRenderer;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.Evaluator;
import ch.idsia.tools.ToolsConfigurator;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.stats.StatisticsUtilities;

public class MarioLevelUtil {
	
	public static final int BLOCK_SIZE = 16;
	public static final int LEVEL_HEIGHT = 12;
	public static final double MAX_HEIGHT_INDEX = LEVEL_HEIGHT - 1;

	public static final int PRESENT_INDEX = 0;
	public static final double PRESENT_THRESHOLD = 0.0;

	public static final int SOLID_INDEX = 1;
	public static final char SOLID_CHAR = 'X';
	public static final int BLOCK_INDEX = 2;
	public static final char BLOCK_CHAR = 'S';
	public static final int QUESTION_INDEX = 3;
	public static final char QUESTION_CHAR = '?';
	public static final int COIN_INDEX = 4;
	public static final char COIN_CHAR = 'o';
	public static final int PIPE_INDEX = 5;
	public static final int CANNON_INDEX = 6;

	public static final int GOOMBA_INDEX = 7;
	public static final char GOOMBA_CHAR = 'E';
	public static final char WINGED_GOOMBA_CHAR = 'W';
	public static final int GREEN_KOOPA_INDEX = 8;
	public static final char GREEN_KOOPA_CHAR = 'g';
	public static final char WINGED_GREEN_KOOPA_CHAR = 'G';
	public static final int RED_KOOPA_INDEX = 9;
	public static final char RED_KOOPA_CHAR = 'r';
	public static final char WINGED_RED_KOOPA_CHAR = 'R';
	public static final int SPIKY_INDEX = 10;
	public static final char SPIKY_CHAR = '^';
	public static final char WINGED_SPIKY_CHAR = '&';

	public static final int WINGED_INDEX = 11; // If enemy, is it winged?
	public static final double WINGED_THRESHOLD = 0.75;
	
	public static final char EMPTY_CHAR = '-';
	
	/**
	 * Whether the character is any of the pipe characters
	 * @param p A character
	 * @return Whether it represents part of a pipe
	 */
	public static boolean isPipe(char p) {
		return p == '<' || p == '>' || p == '[' || p == ']';
	}

	/**
	 * Whether the character is part of a Bullet Bill cannon
	 * @param c
	 * @return
	 */
	public static boolean isCannon(char c) {
		return c == 'B' || c == 'b';
	}
	
	/**
	 * Whether the char is any of the koopas. Significant, since they take up two block cells
	 * in height.
	 * @param c
	 * @return
	 */
	public static boolean isKoopa(char c) {
		return c == GREEN_KOOPA_CHAR || c == RED_KOOPA_CHAR || c == WINGED_GREEN_KOOPA_CHAR || c == WINGED_RED_KOOPA_CHAR;
	}
	
	/**
	 * Generate Mario level layout in form of String array using CPPN.
	 * @param net CPPN
	 * @param width Width in Mario blocks
	 * @return String array where each String is a row of the level
	 */
	public static String[] generateLevelLayoutFromCPPN(Network net, double[] inputMultiples, int width) {
		String[] level = new String[LEVEL_HEIGHT];
		
		// Initially there are no enemies. Only allow one per column
		boolean[] enemyInColumn = new boolean[width];
		
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
				// Turn certain inputs off
				for(int k = 0; k < inputMultiples.length; k++) {
					inputs[k] = inputs[k] * inputMultiples[k];
				}
				double[] outputs = net.process(inputs);

//				for(int k = 0; k < level.length; k++) {
//					System.out.println(level[k]);
//				}
				//System.out.println("["+i+"]["+j+"]"+Arrays.toString(inputs)+Arrays.toString(outputs));
				
				if(outputs[PRESENT_INDEX] > PRESENT_THRESHOLD) {
					outputs[PRESENT_INDEX] = Double.NEGATIVE_INFINITY; // Assure this index is not the biggest
					double wingedValue = outputs[WINGED_INDEX]; // Save winged value before making it negative infinity
					outputs[WINGED_INDEX] = Double.NEGATIVE_INFINITY; // Assure this index is not the biggest
					int highest = StatisticsUtilities.argmax(outputs);
					if(highest == SOLID_INDEX) {
						level[i] += i < LEVEL_HEIGHT - 1 && isKoopa(level[i+1].charAt(j)) ? EMPTY_CHAR : SOLID_CHAR;
					} else if(highest == BLOCK_INDEX) {
						level[i] += i < LEVEL_HEIGHT - 1 && isKoopa(level[i+1].charAt(j)) ? EMPTY_CHAR : BLOCK_CHAR;
					} else if(highest == QUESTION_INDEX) {
						level[i] += i < LEVEL_HEIGHT - 1 && isKoopa(level[i+1].charAt(j)) ? EMPTY_CHAR : QUESTION_CHAR;
					} else if(highest == COIN_INDEX) {
						level[i] += i < LEVEL_HEIGHT - 1 && isKoopa(level[i+1].charAt(j)) ? EMPTY_CHAR : COIN_CHAR;
					} else if (highest == PIPE_INDEX) {
						int leftEdge = level[i].length() - 1;
						if(level[i].length() % 2 == 1 && // Only every other spot can have pipes
						   !isPipe(level[i].charAt(leftEdge))) { 							
							// Have to construct the pipe all the way down
							level[i] = level[i].substring(0, leftEdge) + "<>"; // Top
							int current = i+1;
							//System.out.println("before " + current + " leftEdge " + leftEdge);
							// Replace empty spaces with pipes
							while(current < LEVEL_HEIGHT &&
								  (isPipe(level[current].charAt(leftEdge)) ||
								   isPipe(level[current].charAt(leftEdge+1)) ||
								   isCannon(level[current].charAt(leftEdge)) ||
								   isCannon(level[current].charAt(leftEdge+1)) ||
								   level[current].charAt(leftEdge) == EMPTY_CHAR ||
								   level[current].charAt(leftEdge+1) == EMPTY_CHAR ||
								   level[current].charAt(leftEdge) == COIN_CHAR ||
								   level[current].charAt(leftEdge+1) == COIN_CHAR ||
								   OldLevelParser.isEnemy(level[current].charAt(leftEdge)) ||
								   OldLevelParser.isEnemy(level[current].charAt(leftEdge+1)))) {
								level[current] = level[current].substring(0, leftEdge) + "[]" + level[current].substring(leftEdge+2); // body
								//System.out.println(level[current]);
								current++;
								//System.out.println("loop " + current + " leftEdge " + leftEdge);
							}
						} else { // No room for pipe
							level[i] += EMPTY_CHAR;
						}
					} else if (highest == CANNON_INDEX) {
						int edge = level[i].length();
						// Have to construct the cannon all the way down
						level[i] += "B"; // Top
						int current = i+1;
						// Replace empty spaces with cannon support
						while(current < LEVEL_HEIGHT &&
								(isCannon(level[current].charAt(edge)) ||
										level[current].charAt(edge) == EMPTY_CHAR ||
										level[current].charAt(edge) == COIN_CHAR ||
										OldLevelParser.isEnemy(level[current].charAt(edge)))) {
							level[current] = level[current].substring(0, edge) + "b" + level[current].substring(edge+1); // support
							current++;
						}						
					} else { // Must be an enemy
						if(enemyInColumn[j]) {
							// Only allow one enemy per column: Too restrictive?
							level[i] += EMPTY_CHAR;
						} else {
							if(highest == GOOMBA_INDEX) {
								level[i] += wingedValue > WINGED_THRESHOLD ? WINGED_GOOMBA_CHAR : GOOMBA_CHAR;
							} else if(highest == GREEN_KOOPA_INDEX) {
								level[i] += wingedValue > WINGED_THRESHOLD ? WINGED_GREEN_KOOPA_CHAR : GREEN_KOOPA_CHAR;
							} else if(highest == RED_KOOPA_INDEX) {
								level[i] += wingedValue > WINGED_THRESHOLD ? WINGED_RED_KOOPA_CHAR : RED_KOOPA_CHAR;
							} else {
								assert highest == SPIKY_INDEX : "Only option left is spiky: " + highest;
								level[i] += wingedValue > WINGED_THRESHOLD ? WINGED_SPIKY_CHAR : SPIKY_CHAR;
							}
							// Indicate that there is now an enemy in the column
							enemyInColumn[j] = true;
						}
					}
				} else {
					level[i] += EMPTY_CHAR;
				}
			}
		}
		
		return level;
	}

	/**
	 * Generates a level assuming all CPPN inputs are turned on.
	 * Default behavior.
	 * @param net CPPN that generates level
	 * @param width Width of level in tiles
	 * @return A Mario level
	 */
	public static Level generateLevelFromCPPN(Network net, int width) {
		return generateLevelFromCPPN(net, ArrayUtil.doubleOnes(net.numInputs()), width);
	}
	
	/**
	 * Take a cppn and a width and completely generate the level
	 * @param net CPPN
	 * @param width In Mario blocks
	 * @return Level instance
	 */
	public static Level generateLevelFromCPPN(Network net, double[] inputMultiples, int width) {
		String[] stringBlock = generateLevelLayoutFromCPPN(net, inputMultiples, width);
						
		ArrayList<String> lines = new ArrayList<String>();
		for(int i = 0; i < stringBlock.length; i++) {
			//System.out.println(stringBlock[i]);
			lines.add(stringBlock[i]);
		}

		OldLevelParser parse = new OldLevelParser();
		Level level = parse.createLevelASCII(lines);
		return level;
	}
	
	/**
	 * Convert a VGLC Mario level to list of lists of numbers representing the GAN encoding.
	 * @param filename Mario level file
	 * @return List of lists
	 */
	public static ArrayList<List<Integer>> listLevelFromVGLCFile(String filename) {
		try {
			Scanner file = new Scanner(new File(filename));
			ArrayList<String> lines = new ArrayList<>();
			// Read all lines from file
			while(file.hasNextLine()) {
				lines.add(file.nextLine());
			}
			file.close();
			// Convert to array
			String[] lineArray = lines.toArray(new String[lines.size()]);
			// Convert to list
			ArrayList<List<Integer>> list = listLevelFromStringLevel(lineArray);
			return list;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		throw new IllegalStateException("Problem reading Mario level file: "+filename);
	}
	/**
	 * takes in a level and separates it into its smaller subsections allowing 
	 * for comparison of chunks
	 * @param oneLevel the level
	 * @param segmentWidth the width of the segments/chunk
	 * @return levelWithParsedSegments the level containing every segment
	 */
	public static List<List<List<Integer>>> getSegmentsFromLevel(List<List<Integer>> oneLevel, int segmentWidth){
		if (oneLevel.get(0).size()%segmentWidth!=0){
            System.out.println("getLevelStats: Level not multiple of segment width");
            return null;
        }      
		int height = oneLevel.size();
        List<List<List<Integer>>> levelWithParsedSegments = new ArrayList<List<List<Integer>>>();
        // Loop through each segment
        int numSegments = oneLevel.get(0).size()/segmentWidth;
        for(int l=0; l<numSegments; l++){
            ArrayList<List<Integer>> compareSegments = new ArrayList<List<Integer>>();

        	
            for(int i=0; i<height-1;i++){ // Loop from top to bottom
    			ArrayList<Integer> a = new ArrayList<Integer>();

            	// Loop from left to right through the tiles in this particular segment
                for(int j=l*segmentWidth;j<(l+1)*segmentWidth;j++){
                	Integer tile = oneLevel.get(i).get(j);
    				if(tile == MarioLevelUtil.SPIKY_INDEX||
    						tile == MarioLevelUtil.GOOMBA_INDEX||
    						tile==MarioLevelUtil.GREEN_KOOPA_INDEX||
    						tile==MarioLevelUtil.RED_KOOPA_INDEX||
    						tile==MarioLevelUtil.WINGED_INDEX) {
    					a.add(MarioLevelUtil.PRESENT_INDEX);
    				}else {
    					a.add(tile);
    				}
                }
    			compareSegments.add(a);

            }
            levelWithParsedSegments.add(compareSegments);
        }
		return levelWithParsedSegments;
	}
	/**
	 * Convert from String representation to list of lists
	 * @param stringLevel
	 * @return
	 */
	public static ArrayList<List<Integer>> listLevelFromStringLevel(String[] stringLevel) {
		ArrayList<List<Integer>> result = new ArrayList<List<Integer>>();
		for(String row : stringLevel) {
			List<Integer> listRow = new ArrayList<Integer>(row.length());
			for(int i = 0; i < row.length(); i++) {
				//System.out.println(i + ":" + row.charAt(i));
				Integer tile = Parameters.parameters.booleanParameter("marioGANUsesOriginalEncoding") ?
					OldLevelParser.indexOfBlock(row.charAt(i)) :
					LevelParser.tiles.get(row.charAt(i));
				listRow.add(tile);
			}
			result.add(listRow);
		}
		return result;
	}
	
	/**
	 * Return an image of the level, excluding the buffer zones at the
	 * beginning and end of every CPPN generated level. Also excludes
	 * the background, Mario, and enemy sprites.
	 * @param level A Mario Level
	 * @return Image of Mario level
	 */
	public static BufferedImage getLevelImage(Level level) {
		EvaluationOptions options = new CmdLineOptions(new String[0]);
		ProgressTask task = new ProgressTask(options);
		// Added to change level
        options.setLevel(level);
		task.setOptions(options);

		int relevantWidth = (level.width - (2*OldLevelParser.BUFFER_WIDTH)) * MarioLevelUtil.BLOCK_SIZE;
		BufferedImage image = new BufferedImage(relevantWidth, (1+level.height)*MarioLevelUtil.BLOCK_SIZE, BufferedImage.TYPE_INT_ARGB);
		// Skips buffer zones at start and end of level
		LevelRenderer.renderArea((Graphics2D) image.getGraphics(), level, 0, 0, OldLevelParser.BUFFER_WIDTH*BLOCK_SIZE, 0, relevantWidth, (1+level.height)*BLOCK_SIZE);
		return image;
	}
	
	/**
	 * Specified agent plays the specified level with visual display
	 * @param level
	 * @param agent
	 * @return
	 */
	public static List<EvaluationInfo> agentPlaysLevel(Level level, Agent agent) {
		EvaluationOptions options = new CmdLineOptions(new String[]{});
		return agentPlaysLevel(level, agent, options);
	}

	/**
	 * Same as above, but allows custom eval options that change many settings.
	 * @param level Level to evaluate in
	 * @param agent Agent to evaluate
	 * @param options Mario configuration options (but not the level or agent)
	 * @return list of information about the evaluations
	 */
	public static List<EvaluationInfo> agentPlaysLevel(Level level, Agent agent, EvaluationOptions options) {
		options.setAgent(agent);
        options.setLevel(level);
        return agentPlaysLevel(options);
	}
        
	/**
	 * Now, the evaluation options must also specify the level and the agent.
	 * @param options Mario options, including level and agent
	 * @return evaluation results
	 */
   	public static List<EvaluationInfo> agentPlaysLevel(EvaluationOptions options) {
        Evaluator evaluator = new Evaluator(options);
		List<EvaluationInfo> results = evaluator.evaluate();
		ToolsConfigurator.DestroyMarioComponentFrame();
		return results;
	}
	
//	/**
//	 * For testing and debugging
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		Parameters.initializeParameterCollections(new String[] 
//				{"runNumber:0","randomSeed:"+((int)(Math.random()*100)),"trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3","includeFullSigmoidFunction:true","includeFullGaussFunction:true","includeCosineFunction:true","includeGaussFunction:false","includeIdFunction:true","includeTriangleWaveFunction:true","includeSquareWaveFunction:true","includeFullSawtoothFunction:true","includeSigmoidFunction:false","includeAbsValFunction:true","includeSawtoothFunction:true"});
//		MMNEAT.loadClasses();
//				
//		////////////////////////////////////////////////////////
////		String[] stringBlock = new String[] {
////				"--------------------------------------------------------", 
////				"--------------------------------------------------------", 
////				"--------------------------------------------------------", 
////				"---------ooooo------------------------------------------", 
////				"--------------------------------------------------------", 
////				"----?---S?S---------------------------------------------", 
////				"------------------X-------------------------------------", 
////				"-----------------XX---------------------E-----<>--------", 
////				"---SSSS--<>-----XXX---------------------X-----[]--------", 
////				"---------[]---XXXXX-------------------XXXXX---[]--------", 
////				"---------[]-XXXXXXX----------EE-----XXXXXXXXXX[]--------", 
////				"XXXXXXXXXXXXXXXXXXX-----XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
////			};
//				
//		// Instead of specifying the level, create it with a TWEANN	
//		TWEANNGenotype cppn = new TWEANNGenotype(3, 12, 0); // Archetype
//		// Randomize activation functions
//		new ActivationFunctionRandomReplacement().mutate(cppn);
//		
//		// Random mutations
////		for(int i = 0; i < 50; i++) {
////			cppn.mutate();
////		}
//		
//		TWEANN net = cppn.getPhenotype();
//		DrawingPanel panel = new DrawingPanel(200,200, "Network");
//		net.draw(panel, true, false);
//
//		Level level = generateLevelFromCPPN(net, new double[] {1,1,1}, 60);
//		
//		Agent controller = new HumanKeyboardAgent(); //new SergeyKarakovskiy_JumpingAgent();
//		EvaluationOptions options = new CmdLineOptions(new String[]{});
//		options.setAgent(controller);
//		ProgressTask task = new ProgressTask(options);
//
//		// Added to change level
//        options.setLevel(level);
//
//		task.setOptions(options);
//
//		int relevantWidth = (level.width - (2*OldLevelParser.BUFFER_WIDTH)) * BLOCK_SIZE;
//		//System.out.println("level.width:"+level.width);
//		//System.out.println("relevantWidth:"+relevantWidth);
//		DrawingPanel levelPanel = new DrawingPanel(relevantWidth,LEVEL_HEIGHT*BLOCK_SIZE, "Level");
//		LevelRenderer.renderArea(levelPanel.getGraphics(), level, 0, 0, OldLevelParser.BUFFER_WIDTH*BLOCK_SIZE, 0, relevantWidth, LEVEL_HEIGHT*BLOCK_SIZE);
//		
//		System.out.println ("Score: " + task.evaluate(options.getAgent())[0]);
//		
//				
//	}
   	
   	
   	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
   		String inputFile1 = "src/main/python/GAN/Mario-all.json";
   		String inputFile2 = "src/main/python/GAN/Mario-all.json";
   		FileReader file1 = new FileReader(inputFile1);
   		FileReader file2 = new FileReader(inputFile2);
   		ArrayList<ArrayList<ArrayList<Integer>>> parsedFile1 = parseLevelJson(file1);
   		ArrayList<ArrayList<ArrayList<Integer>>> parsedFile2 = parseLevelJson(file2);
   		
   		for (int i = 0; i < parsedFile1.size(); i++) {
   			printLevelsSideBySide(parsedFile1.get(i), parsedFile2.get(i));
   			Thread.sleep(50);
   			System.out.println("\n\n");
   		}
   		
//   		System.out.println("Overall size:"+parsedFile1.size()+" | "+parsedFile2.size()+"\n");
//   		for (int i = 0; i < parsedFile1.size(); i++) {
//   			System.out.println(parsedFile1.get(i).size()+" | "+parsedFile2.get(i).size()+"\n");
//   		}
//   		System.out.println(parsedFile1.equals(parsedFile2));
   		
   	}
   	
   	public static ArrayList<ArrayList<ArrayList<Integer>>> parseLevelJson(FileReader file) {
   		ArrayList<ArrayList<ArrayList<Integer>>> parsedFile = new ArrayList<ArrayList<ArrayList<Integer>>>();
   		JsonStreamParser jsonParser = new JsonStreamParser(file);
   		JsonArray parsed = (JsonArray) jsonParser.next();
   		for (JsonElement element : parsed) {
   			ArrayList<ArrayList<Integer>> inner1 = new ArrayList<ArrayList<Integer>>();
   			for (JsonElement element2 : (JsonArray) element) {
   				ArrayList<Integer> inner2 = new ArrayList<Integer>();
   				for (JsonElement element3 : (JsonArray) element2) {
   					inner2.add(element3.getAsBigInteger().intValue());
   	   	   		}
   				inner1.add(inner2);
   	   		}
   			parsedFile.add(inner1);
   		}
   		return parsedFile;
   	}
   	
   	public static void printLevelsSideBySide(ArrayList<ArrayList<Integer>> level, ArrayList<ArrayList<Integer>> level2) {
   		String visualLine;
   		Set<Entry<Character, Integer>> tileset = LevelParser.tiles.entrySet();
   		for (int i = 0; i < level.size(); i++) {
   			visualLine = "";
   			for (Integer integ : level.get(i)) {
   				for (Entry<Character, Integer> e : tileset) {
   					if (e.getValue() == integ) {
   						visualLine += e.getKey();
   					}
   				}
   				
   			}
   			visualLine += " | ";
   			for (Integer integ : level2.get(i)) {
   				for (Entry<Character, Integer> e : tileset) {
   					if (e.getValue() == integ) {
   						visualLine += e.getKey();
   					}
   				}
   				
   			}
   			
   			System.out.println(visualLine);
   		}
   	}
}

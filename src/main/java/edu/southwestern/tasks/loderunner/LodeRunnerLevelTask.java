package edu.southwestern.tasks.loderunner;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.generalmappings.KLDivergenceBinLabels;
import edu.southwestern.evolution.mapelites.generalmappings.LatentVariablePartitionSumBinLabels;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.export.JsonLevelGenerationTask;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.graphics.GraphicsUtil;
import icecreamyou.LodeRunner.LodeRunner;

/**
 * 
 * Evolve Lode Runner levels
 * 
 * @author kdste
 *
 * @param <T>
 */
public abstract class LodeRunnerLevelTask<T> extends NoisyLonerTask<T> implements JsonLevelGenerationTask<T> {

	private int numFitnessFunctions = 0; 
	protected static final int NUM_OTHER_SCORES = 9;
	// Is actually logged to in the MAPElites class
	public MMNEATLog beatable;

	private int[][][] klDivLevels; // Should this be here?
	private double fitnessSaveThreshold = Parameters.parameters.doubleParameter("fitnessSaveThreshold");
	
	private boolean initialized = false; // become true on first evaluation

	/**
	 * Registers all fitness functions that are used, and other scores 
	 */
	public LodeRunnerLevelTask() {
		this(true); // registers the fitness functions and other scores
	}
	
	/**
	 * Only registers fitness functions and other scores if boolean value is true.
	 * If not, then fitness/scores will need to be registered somewhere else. The
	 * purpose of this constructor is to be called by child classes, like the
	 * LodeRunnerLevelSequenceTask
	 * 
	 * @param register whether to register fitness functions and other scores
	 */
	protected LodeRunnerLevelTask(boolean register) {
		if(register) {
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsSimpleAStarPath")) {
				MMNEAT.registerFitnessFunction("simpleAStarDistance");
				numFitnessFunctions++;
			}
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsConnectivity")) {
				MMNEAT.registerFitnessFunction("numOfPositionsVisited"); //connectivity
				numFitnessFunctions++;
			}
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsTSPSolutionPath")) {
				MMNEAT.registerFitnessFunction("TSPSolutionPathLength");
				numFitnessFunctions++;
			}
			if(Parameters.parameters.booleanParameter("lodeRunnerMaximizeEnemies")) {
				MMNEAT.registerFitnessFunction("NumEnemies");
				numFitnessFunctions++;
			}
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsAStarConnectivityCombo")) {
				MMNEAT.registerFitnessFunction("ConnectivityOrAStar");
				numFitnessFunctions++;
			}

			//registers the other things to be tracked that are not fitness functions, to be put in the otherScores array 
			MMNEAT.registerFitnessFunction("simpleAStarDistance",false);
			MMNEAT.registerFitnessFunction("numOfPositionsVisited",false); //connectivity
			MMNEAT.registerFitnessFunction("percentLadders", false);
			MMNEAT.registerFitnessFunction("percentGround", false);
			MMNEAT.registerFitnessFunction("percentRope", false);
			MMNEAT.registerFitnessFunction("percentConnected", false);
			MMNEAT.registerFitnessFunction("numTreasures", false);
			MMNEAT.registerFitnessFunction("numEnemies", false);
			MMNEAT.registerFitnessFunction("TSPDistance",false); // if available ... -1 otherwise
			
			// THIS ONLY WORKS WITH MAP Elites!
			if(Parameters.parameters.booleanParameter("io") && !(MMNEAT.task instanceof LodeRunnerLevelSequenceTask)) {
				// Is actually logged to in the MAPElites class
				beatable = new MMNEATLog("Beatable", false, false, false, true);
				
				// Create gnuplot file for percent beatable levels
				String experimentPrefix = Parameters.parameters.stringParameter("log")
						+ Parameters.parameters.integerParameter("runNumber");
				String beatablePrefix = experimentPrefix + "_" + "Beatable";
				String directory = FileUtilities.getSaveDirectory();// retrieves file directory
				directory += (directory.equals("") ? "" : "/");
				String fullFillName = directory + beatablePrefix + "_log.plt";
				File beatablePlot = new File(fullFillName);
				// Write to file
				try {
					// Archive plot
					int individualsPerGeneration = Parameters.parameters.integerParameter("steadyStateIndividualsPerGeneration");

					// Fill percentage plot
					PrintStream ps = new PrintStream(beatablePlot);
					ps.println("set term pdf enhanced");
					ps.println("set key bottom right");
					// Here, maxGens is actually the number of iterations, but dividing by individualsPerGeneration scales it to represent "generations"
					ps.println("set xrange [0:"+ (Parameters.parameters.integerParameter("maxGens")/individualsPerGeneration) +"]");
					ps.println("set title \"" + experimentPrefix + " Beatable Levels\"");
					ps.println("set output \"" + fullFillName.substring(fullFillName.lastIndexOf('/')+1, fullFillName.lastIndexOf('.')) + ".pdf\"");
					String name = fullFillName.substring(fullFillName.lastIndexOf('/')+1, fullFillName.lastIndexOf('.'));
					ps.println("plot \"" + name + ".txt\" u 1:2 w linespoints t \"Total\"");
					
					ps.println("set yrange [0:1]");
					ps.println("set title \"" + experimentPrefix + " Beatable Percentage\"");
					ps.println("set output \"" + fullFillName.substring(fullFillName.lastIndexOf('/')+1, fullFillName.lastIndexOf('.')) + "_Percent.pdf\"");
					ps.println("plot \"" + name + ".txt\" u 1:3 w linespoints t \"Percent\"");
					ps.close();
					
				} catch (FileNotFoundException e) {
					System.out.println("Could not create plot file: " + beatablePlot.getName());
					e.printStackTrace();
					System.exit(1);
				}
				
			}
		}
	}

	private void setupKLDivLevelsForComparison() {
		if (MMNEAT.usingDiversityBinningScheme && MMNEAT.getArchiveBinLabelsClass() instanceof KLDivergenceBinLabels) { 
			System.out.println("Instance of MAP Elites using KL Divergence Bin Labels");
			String level1FileName = Parameters.parameters.stringParameter("mapElitesKLDivLevel1"); 
			String level2FileName = Parameters.parameters.stringParameter("mapElitesKLDivLevel2"); 
			ArrayList<List<Integer>> level1List = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevel(level1FileName);
			ArrayList<List<Integer>> level2List = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevel(level2FileName);
			int[][] level1Array = ArrayUtil.int2DArrayFromListOfLists(level1List);
			int[][] level2Array = ArrayUtil.int2DArrayFromListOfLists(level2List);
			klDivLevels = new int[][][] {level1Array, level2Array};
		}
	}
	
	/**
	 * @return The number of fitness functions 
	 */
	@Override
	public int numObjectives() {
		return numFitnessFunctions; 
	}

	/**
	 * @return The number of other scores 
	 */
	@Override
	public int numOtherScores() {
		return NUM_OTHER_SCORES;
	}

	/**
	 * Different level generators use the genotype to generate a level in different ways
	 * @param individual Genotype 
	 * @return List of lists of integers corresponding to tile types
	 */
	public abstract List<List<Integer>> getLodeRunnerLevelListRepresentationFromGenotype(Genotype<T> individual);

	@Override
	public double getTimeStamp() {
		return 0; //not used 
	}

	/**
	 * Does one evaluation with the A* algorithm to see if the level is beatable 
	 * @param Genotype<T> 
	 * @param Integer 
	 * @return
	 * @throws IOException 
	 */
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num, HashMap<String,Object> behaviorCharacteristics) {
		if(!initialized) {
			setupKLDivLevelsForComparison();
			initialized = true;
		}
		List<List<Integer>> level = getLodeRunnerLevelListRepresentationFromGenotype(individual); //gets a level 
		double psuedoRandomSeed = getRandomSeedForSpawnPoint(individual); //creates the seed to be passed into the Random instance 
		return evaluateOneLevel(level, psuedoRandomSeed, individual, behaviorCharacteristics);
	}

	/**
	 * Calculates fitness scores and other scores for a single level
	 * using methods from the LodeRunnerLevelAnalysisUtil class
	 * 
	 * @param level 
	 * @param psuedoRandomSeed
	 * @param genotypeId
	 * @return Pair holding the scores 
	 */
	public Pair<double[], double[]> evaluateOneLevel(List<List<Integer>> level, double psuedoRandomSeed, Genotype<T> individual, HashMap<String,Object> behaviorMap) {
		long genotypeId = individual.getId();
		ArrayList<Double> fitnesses = new ArrayList<>(numFitnessFunctions); //initializes the fitness function array  
		Triple<HashSet<LodeRunnerState>, ArrayList<LodeRunnerAction>, LodeRunnerState> aStarInfo = LodeRunnerLevelAnalysisUtil.performAStarSearch(level, psuedoRandomSeed);
		HashSet<LodeRunnerState> mostRecentVisited = aStarInfo.t1;
		ArrayList<LodeRunnerAction> actionSequence = aStarInfo.t2;
		LodeRunnerState start = aStarInfo.t3;
		//calculates aStarPath length
		double simpleAStarDistance = LodeRunnerLevelAnalysisUtil.calculateSimpleAStarLength(actionSequence);
		//calculates the amount of the level that was covered in the search, connectivity.
		double connectivityOfLevel = LodeRunnerLevelAnalysisUtil.caluclateConnectivity(mostRecentVisited);
		//adds the fitness functions being used to the fitness array list
		if(Parameters.parameters.booleanParameter("lodeRunnerAllowsSimpleAStarPath")) {
			fitnesses.add(simpleAStarDistance);
		}
		if(Parameters.parameters.booleanParameter("lodeRunnerAllowsConnectivity")) {
			fitnesses.add(connectivityOfLevel);
		}
		
		//Calculate length of tsp path whether used for fitness or not
		double tspSolutionLength = -1;
		Pair<ArrayList<LodeRunnerAction>, HashSet<LodeRunnerState>> tspInfo = LodeRunnerTSPUtil.getFullActionSequenceAndVisitedStatesTSPGreedySolution(level);
		// Unsolvable levels received TSP fitness score of -1
		if(tspInfo.t1 != null) tspSolutionLength = tspInfo.t1.size();
		
		if(Parameters.parameters.booleanParameter("lodeRunnerAllowsTSPSolutionPath") || Parameters.parameters.booleanParameter("lodeRunnerAllowsLinearIncreasingTSPLength")) {
			fitnesses.add(tspSolutionLength);
		}

		//calculates other scores that are not fitness functions 
		double percentConnected = connectivityOfLevel/LodeRunnerLevelAnalysisUtil.TOTAL_TILES;		//calculates the percentage of the level that is connected
		double percentLadders = LodeRunnerLevelAnalysisUtil.calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_LADDER}, level);
		double percentGround = LodeRunnerLevelAnalysisUtil.calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_DIGGABLE, LodeRunnerState.LODE_RUNNER_TILE_GROUND}, level);
		double percentRopes = LodeRunnerLevelAnalysisUtil.calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_ROPE}, level);
		int numTreasure = 0; 
		int numEnemies = 0;
		for(int i = 0; i < level.size();i++) {
			for(int j = 0; j < level.get(i).size(); j++) {
				//calculates number of treasures
				if(level.get(i).get(j) == LodeRunnerState.LODE_RUNNER_TILE_GOLD) {
					numTreasure++;
				}
				//calculates the number of enemies
				if(level.get(i).get(j) == LodeRunnerState.LODE_RUNNER_TILE_ENEMY) {
					numEnemies++;
				}
			}
		}
				
		if(Parameters.parameters.booleanParameter("lodeRunnerMaximizeEnemies")) {
			fitnesses.add(1.0*numEnemies);
		}
		
		double comboFitness = Math.max(percentConnected, simpleAStarDistance);
		if(Parameters.parameters.booleanParameter("lodeRunnerAllowsAStarConnectivityCombo")) {
			fitnesses.add(comboFitness);
		}
		
		double[] otherScores = new double[] {simpleAStarDistance, connectivityOfLevel, percentLadders, percentGround, percentRopes, percentConnected, numTreasure, numEnemies, tspSolutionLength};

		if(CommonConstants.watch) {
			//prints values that are calculated above for debugging 
			System.out.println("Simple A* Distance " + simpleAStarDistance);
			System.out.println("TSP solution length "+ tspSolutionLength);
			System.out.println("Number of Positions Visited " + connectivityOfLevel);
			System.out.println("Percent of Ladders " + percentLadders);
			System.out.println("Percent of Ground " + percentGround);
			System.out.println("Percent of Ropes " + percentRopes);
			//System.out.println("Percent of Connectivity in Level " + percentConnected);
			System.out.println("Number of Treasures " + numTreasure);
			System.out.println("Number of Enemies " + numEnemies);

			// Prefer TSP solutions over A* if available
			if(tspInfo != null) {
				actionSequence = tspInfo.t1;
			}
			
			try {
				//displays the rendered solution path in a window 
				BufferedImage visualPath = LodeRunnerState.vizualizePath(level,mostRecentVisited,actionSequence,start);
				JFrame frame = new JFrame();
				JPanel panel = new JPanel();
				JLabel label = new JLabel(new ImageIcon(visualPath.getScaledInstance(LodeRunnerRenderUtil.LODE_RUNNER_COLUMNS*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X, 
						LodeRunnerRenderUtil.LODE_RUNNER_ROWS*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y, Image.SCALE_FAST)));
				panel.add(label);
				frame.add(panel);
				frame.pack();
				frame.setVisible(true);
			} catch (IOException e) {
				System.out.println("Could not display image");
				//e.printStackTrace();
			}
			//Gives you the option to play the level by pressing p, or skipping by pressing enter, after the visualization is displayed 
			System.out.println("Enter 'P' to play, or just press Enter to continue");
			String input = MiscUtil.waitForReadStringAndEnterKeyPress();
			System.out.println("Entered \""+input+"\"");
			//if the user entered P or p, then run
			if(input.toLowerCase().equals("p")) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new LodeRunner(level);
					}
				});
				System.out.println("Press enter");
				MiscUtil.waitForReadStringAndEnterKeyPress();
			}
		}

		// LodeRunnerLevelSequenceTask has it's own MAP Elites binning rules defined in LodeRunnerLevelSequenceTask
		if(MMNEAT.usingDiversityBinningScheme && !(MMNEAT.task instanceof LodeRunnerLevelSequenceTask)) {
			
			// Quality measure (designated in a manner distinct from the fitness score)
			double binScore = simpleAStarDistance;
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsAStarConnectivityCombo")) {
				// Combo of connectivity and A* overwhelms regular A*
				binScore = comboFitness;
			}
			behaviorMap.put("binScore", binScore);

			// All possible behavior characterization information
			behaviorMap.put("Connected Percent",percentConnected);
			behaviorMap.put("Ground Percent", percentGround);
			behaviorMap.put("Ladders Percent", percentLadders);
			behaviorMap.put("Treasures", numTreasure+0.0);
			behaviorMap.put("Enemies", numEnemies+0.0);
			// These characteristics are costly to compute, so only compute if specific labels are being used
			if (MMNEAT.getArchiveBinLabelsClass() instanceof KLDivergenceBinLabels) { 
				int[][] oneLevelAs2DArray = ArrayUtil.int2DArrayFromListOfLists(level);
				behaviorMap.put("2D Level", oneLevelAs2DArray);
				behaviorMap.put("Comparison Levels", klDivLevels);
			} else if (MMNEAT.getArchiveBinLabelsClass() instanceof LatentVariablePartitionSumBinLabels) {
				@SuppressWarnings("unchecked")
				ArrayList<Double> rawVector = (ArrayList<Double>) individual.getPhenotype();
				double[] latentVector = ArrayUtil.doubleArrayFromList(rawVector);
				behaviorMap.put("Solution Vector", latentVector);
			}
			
			int dim1D = MMNEAT.getArchiveBinLabelsClass().oneDimensionalIndex(behaviorMap);
			behaviorMap.put("dim1D", dim1D); // Save so it does not need to be computed again

			BufferedImage levelSolution = null;
			BufferedImage levelImage = null;
			try {
				//gets images of the level, both a standard render and the solution path 
				levelSolution = LodeRunnerState.vizualizePath(level,mostRecentVisited,actionSequence,start);
				levelImage = LodeRunnerRenderUtil.createBufferedImage(level, LodeRunnerRenderUtil.RENDERED_IMAGE_WIDTH, LodeRunnerRenderUtil.RENDERED_IMAGE_HEIGHT);
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("Could not get image");
			} 
			//this method saves the level images in the archive directory 
			setBinsAndSaveLevelImages(genotypeId, levelImage, levelSolution, dim1D, binScore);
		}


		return new Pair<double[],double[]>(ArrayUtil.doubleArrayFromList(fitnesses), otherScores);
	}

	/**
	 * This method  saves the images of the level, both a standard render and a render with the solution path
	 * @param genotypeId Genotype ID
	 * @param levelImage Standard render level
	 * @param levelSolution Solution path of rendered level
	 * @param dim1D 1D index of new solution within archive
	 * @param binScore AStarPath length 
	 */
	private void setBinsAndSaveLevelImages(long genotypeId, BufferedImage levelImage, BufferedImage levelSolution, int dim1D, double binScore) {		
		//saving images in bins 
		if(CommonConstants.netio) {
			System.out.println("Saving rendered level and solution path for level");
			@SuppressWarnings("unchecked")
			Archive<T> archive = MMNEAT.getArchive();
			List<String> binLabels = archive.getBinMapping().binLabels();
			// Index in flattened bin array
			Score<T> elite = archive.getElite(dim1D);
			//if that index is empty or the binScores is greater than what was there before
			if(elite==null || binScore > elite.behaviorIndexScore()) {
				if(binScore > fitnessSaveThreshold) {
					//formats to be 7 digits before the decimal, and 5 digits after, %7.5f
					//only doing direct right now, but will need to add CPPN label in addition, like in MarioLevelTask, if we start to use a CPPN
					String fileNameImage =  "_Direct-"+String.format("%7.5f", binScore) +"_"+ genotypeId + "-LevelRender" +".png";
					String binPath = archive.getArchiveDirectory() + File.separator + binLabels.get(dim1D);
					String fullNameImage = binPath + "_" + fileNameImage;
					System.out.println(fullNameImage);
					GraphicsUtil.saveImage(levelImage, fullNameImage);// saves the rendered level without the solution path
					String fileNameSolution = "_Direct-"+String.format("%7.5f", binScore) +"_"+ genotypeId + "-SolutionRender" +".png";
					String fullNameSolution = binPath + "_" +fileNameSolution;
					System.out.println(fullNameSolution);
					GraphicsUtil.saveImage(levelSolution, fullNameSolution);// saves the rendered level with the solution path
				}
			}
		}
	}

	/**
	 * Based on genotype, get a random seed that can be used to choose the level start point
	 * @param individual Level genotype
	 * @return Random seed
	 */
	public abstract double getRandomSeedForSpawnPoint(Genotype<T> individual);

}

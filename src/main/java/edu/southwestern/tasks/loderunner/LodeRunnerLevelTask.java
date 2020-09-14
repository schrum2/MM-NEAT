package edu.southwestern.tasks.loderunner;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
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
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.tasks.loderunner.mapelites.LodeRunnerMAPElitesPercentConnectedGroundAndLaddersBinLabels;
import edu.southwestern.tasks.loderunner.mapelites.LodeRunnerMAPElitesPercentConnectedNumGoldAndEnemiesBinLabels;
import edu.southwestern.tasks.loderunner.mapelites.LodeRunnerMAPElitesPercentGroundNumGoldAndEnemiesBinLabels;
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
public abstract class LodeRunnerLevelTask<T> extends NoisyLonerTask<T> {

	private int numFitnessFunctions = 0; 
	protected static final int NUM_OTHER_SCORES = 9;
	// Is actually logged to in the MAPElites class
	public MMNEATLog beatable;

	// Calculated in oneEval, so it can be passed on the getBehaviorVector
	private ArrayList<Double> behaviorVector;
	private Pair<int[],Double> oneMAPEliteBinIndexScorePair;

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
				String fullFillName = directory + beatablePrefix + "_log.plot";
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

	// This might break the Level Sequence Task, but that isn't really used, so will fix later if necessary.
	// In a pinch, this whole method can be commented out to make the level sequence work.
	@Override
	public Score<T> evaluate(Genotype<T> individual) {
		Score<T> result = super.evaluate(individual);
		if(MMNEAT.ea instanceof MAPElites)
			result.assignMAPElitesBinAndScore(oneMAPEliteBinIndexScorePair.t1, oneMAPEliteBinIndexScorePair.t2);
		return result;
	}
	
	/**
	 * Does one evaluation with the A* algorithm to see if the level is beatable 
	 * @param Genotype<T> 
	 * @param Integer 
	 * @return
	 * @throws IOException 
	 */
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num){
		List<List<Integer>> level = getLodeRunnerLevelListRepresentationFromGenotype(individual); //gets a level 
		double psuedoRandomSeed = getRandomSeedForSpawnPoint(individual); //creates the seed to be passed into the Random instance 
		long genotypeId = individual.getId();
		return evaluateOneLevel(level, psuedoRandomSeed, genotypeId);
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
	@SuppressWarnings("unchecked")
	protected Pair<double[], double[]> evaluateOneLevel(List<List<Integer>> level, double psuedoRandomSeed, long genotypeId) {
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
		if(MMNEAT.ea instanceof MAPElites && !(MMNEAT.task instanceof LodeRunnerLevelSequenceTask)) {
			// Assign to the behavior vector before using MAP-Elites
			//double[] archiveArray = null;
			final int BINS_PER_DIMENSION = LodeRunnerMAPElitesPercentConnectedGroundAndLaddersBinLabels.BINS_PER_DIMENSION;
			int dim1, dim2, dim3; //declares bin dimensions 
			double SCALE_GROUND_LADDERS = BINS_PER_DIMENSION/4.0; //scales by 1/4 of the dimension to go in steps of 4
			//gets correct indices for all dimensions based on percent and multiplied by 10 to be a non decimal 
			int connectedIndex = Math.min((int)(percentConnected*BINS_PER_DIMENSION), BINS_PER_DIMENSION-1); 
			
			// ground scaling is frustrating. percentGroundseems to land between 0.1 and 0.43. So, subtract 0.1 to get to
			// 0.0 to 0.33, then multiply by 3 to get 0.0 to 0.99
			int groundIndex = Math.max(0, Math.min((int)((percentGround-0.1)*3*BINS_PER_DIMENSION), BINS_PER_DIMENSION-1));
			int laddersIndex = Math.min((int)(percentLadders*SCALE_GROUND_LADDERS*BINS_PER_DIMENSION), BINS_PER_DIMENSION-1);
			double binScore = simpleAStarDistance;
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsAStarConnectivityCombo")) {
				// Combo of connectivity and A* overwhelms regular A*
				binScore = comboFitness;
			}

			if(((MAPElites<T>) MMNEAT.ea).getBinLabelsClass() instanceof LodeRunnerMAPElitesPercentConnectedGroundAndLaddersBinLabels) {
				//Initializes bin dimensions 
				dim1 = connectedIndex; //connectivity
				dim2 = groundIndex; //percent ground scaled
				dim3 = laddersIndex; //percent ladders scaled
				//becomes the behavior vector 
				//archiveArray = new double[BINS_PER_DIMENSION*BINS_PER_DIMENSION*BINS_PER_DIMENSION];
			} else if(((MAPElites<T>) MMNEAT.ea).getBinLabelsClass() instanceof LodeRunnerMAPElitesPercentConnectedNumGoldAndEnemiesBinLabels) {
				double treasureScale = 5.0; //scales bins to be in groups of 5, [0-5][5-10]...
				double enemyScale = 2.0; //scales bins to be in groups of 2, [0-2][2-4]...
				//gets correct indices for treasure and enemies
				int treasureIndex = (int) Math.min(numTreasure/treasureScale, BINS_PER_DIMENSION-1);
				int enemyIndex = (int) Math.min(numEnemies/enemyScale, BINS_PER_DIMENSION-1);
				dim1 = connectedIndex; //connectivity
				dim2 = treasureIndex;//number of treasures scaled 
				dim3 = enemyIndex; //number of enemies scaled
				//becomes the behavior vector 
				//archiveArray = new double[BINS_PER_DIMENSION*BINS_PER_DIMENSION*BINS_PER_DIMENSION];
			} else if(((MAPElites<T>) MMNEAT.ea).getBinLabelsClass() instanceof LodeRunnerMAPElitesPercentGroundNumGoldAndEnemiesBinLabels) {
				double treasureScale = 5.0; //scales bins to be in groups of 5, [0-5][5-10]...
				double enemyScale = 2.0; //scales bins to be in groups of 2, [0-2][2-4]...
				//gets correct indices for treasure and enemies
				int treasureIndex = (int) Math.min(numTreasure/treasureScale, BINS_PER_DIMENSION-1);
				int enemyIndex = (int) Math.min(numEnemies/enemyScale, BINS_PER_DIMENSION-1);
				dim1 = groundIndex; //ground percentage
				dim2 = treasureIndex;//number of treasures scaled 
				dim3 = enemyIndex; //number of enemies scaled
				//becomes the behavior vector 
				//archiveArray = new double[BINS_PER_DIMENSION*BINS_PER_DIMENSION*BINS_PER_DIMENSION];
			} else {
				throw new RuntimeException("A Valid Binning Scheme For Lode Runner Was Not Specified");
			}
			BufferedImage levelSolution = null;
			BufferedImage levelImage = null;
			try {
				//gets images of the level, both a standard render and the solution path 
				levelSolution = LodeRunnerState.vizualizePath(level,mostRecentVisited,actionSequence,start);
				levelImage = LodeRunnerRenderUtil.createBufferedImage(level, LodeRunnerRenderUtil.RENDERED_IMAGE_WIDTH, LodeRunnerRenderUtil.RENDERED_IMAGE_HEIGHT);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("Could not get image");
			} 
			//this method makes the bins and saves the level images in the archive directory 
			setBinsAndSaveLevelImages(genotypeId, levelImage, levelSolution, dim1, dim2, dim3, binScore);
		}


		return new Pair<double[],double[]>(ArrayUtil.doubleArrayFromList(fitnesses), otherScores);
	}

	/**
	 * This method makes the bins for levels to be placed in and then saves 
	 * the images of the level, both a standard render and a render with the solution path
	 * @param genotypeId Genotype ID
	 * @param levelImage Standard render level
	 * @param levelSolution Solution path of rendered level
	 * @param dim1 Dimension of connectivity
	 * @param dim2 Dimension of ground 
	 * @param dim3 Dimension of ladders 
	 * @param binScore AStarPath length 
	 */
	@SuppressWarnings("unchecked")
	private void setBinsAndSaveLevelImages(long genotypeId, BufferedImage levelImage,
			BufferedImage levelSolution, int dim1, int dim2, int dim3,
			double binScore) {
		
		oneMAPEliteBinIndexScorePair = new Pair<int[], Double>(new int[] {dim1, dim2, dim3}, binScore);

		//gets the index in the one dimensional array 
//		int binIndex = (dimConnected*BINS_PER_DIMENSION + dimGround)*BINS_PER_DIMENSION + dimLadders;
//		Arrays.fill(archiveArray, Double.NEGATIVE_INFINITY); // Worst score in all dimensions
//		archiveArray[binIndex] = binScore; //adds binScore at binIndex 
		
		System.out.println("["+dim1+"]["+dim2+"]["+dim3+"] = "+binScore);
		
//		behaviorVector = ArrayUtil.doubleVectorFromArray(archiveArray);
		//saving images in bins 
		if(CommonConstants.netio) {
			System.out.println("Saving rendered level and solution path for level");
			Archive<T> archive = ((MAPElites<T>) MMNEAT.ea).getArchive();
			List<String> binLabels = archive.getBinMapping().binLabels();
			// Index in flattened bin array
			Score<T> elite = archive.getElite(oneMAPEliteBinIndexScorePair.t1);
			//if that index is empty or the binScores is greater than what was there before
			if(elite==null || binScore > elite.behaviorIndexScore()) {
				//formats to be 7 digits before the decimal, and 5 digits after, %7.5f
				//only doing direct right now, but will need to add CPPN label in addition, like in MarioLevelTask, if we start to use a CPPN
				String fileNameImage =  "_Direct-"+String.format("%7.5f", binScore) +"_"+ genotypeId + "-LevelRender" +".png";
				String binPath = archive.getArchiveDirectory() + File.separator + binLabels.get(archive.getBinMapping().oneDimensionalIndex(oneMAPEliteBinIndexScorePair.t1));
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

	/**
	 * Data calculated in oneEval and returned here
	 * Meant to be used with MAPElites. It is an array of bins. Every level gets placed into a single bin 
	 * @return behaviorVector
	 */
	public ArrayList<Double> getBehaviorVector() {
		return behaviorVector;
	}

	/**
	 * Based on genotype, get a random seed that can be used to choose the level start point
	 * @param individual Level genotype
	 * @return Random seed
	 */
	public abstract double getRandomSeedForSpawnPoint(Genotype<T> individual);

}

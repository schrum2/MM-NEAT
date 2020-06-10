package edu.southwestern.tasks.loderunner;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.tasks.mario.MarioMAPElitesDecorNSAndLeniencyBinLabels;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;
import icecreamyou.LodeRunner.LodeRunner;

/**
 * 
 * @author kdste
 *
 * @param <T>
 */
public abstract class LodeRunnerLevelTask<T> extends NoisyLonerTask<T> {

	private static int numFitnessFunctions = 0; 
	public static final int TOTAL_TILES = 704; //for percentages, 22x32 levels 
	private static final int numOtherScores = 8;

	// Calculated in oneEval, so it can be passed on the getBehaviorVector
	private ArrayList<Double> behaviorVector;

	/**
	 * Registers all fitness functions that are used, rn only one is used for lode runner 
	 */
	public LodeRunnerLevelTask() {
		if(Parameters.parameters.booleanParameter("lodeRunnerAllowsSimpleAStarPath")) {
			MMNEAT.registerFitnessFunction("simpleAStarDistance");
			numFitnessFunctions++;
		}
		if(Parameters.parameters.booleanParameter("lodeRunnerAllowsConnectivity")) {
			MMNEAT.registerFitnessFunction("numOfPositionsVisited"); //connectivity
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
		return numOtherScores;
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
	@SuppressWarnings("unchecked")
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num){
		double psuedoRandomSeed = getRandomSeedForSpawnPoint(individual); //creates the seed to be passed into the Random instance 

		ArrayList<Double> fitnesses = new ArrayList<>(numFitnessFunctions); //initializes the fitness function array 
		List<List<Integer>> level = getLodeRunnerLevelListRepresentationFromGenotype(individual); //gets a level 
		List<Point> emptySpaces = LodeRunnerGANUtil.fillEmptyList(level); //fills a set with empty points fro the level to select a spawn point from 
		Random rand = new Random(Double.doubleToLongBits(psuedoRandomSeed));
		LodeRunnerGANUtil.setSpawn(level, emptySpaces, rand); //sets a random spawn point 
		List<List<Integer>> levelCopy = ListUtil.deepCopyListOfLists(level); //copy level so it is not effected by the search 
		LodeRunnerState start = new LodeRunnerState(levelCopy); //gets start state for search 
		Search<LodeRunnerAction,LodeRunnerState> search = new AStarSearch<>(LodeRunnerState.manhattanToFarthestGold); //initializes a search based on the heuristic 
		HashSet<LodeRunnerState> mostRecentVisited = null;
		ArrayList<LodeRunnerAction> actionSequence = null;
		double simpleAStarDistance = -1; //intialized to hold distance of solution path, or -1 if search fails
		//calculates the Distance to the farthest gold as a fitness fucntion 
		try { 
			actionSequence = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).search(start, true, Parameters.parameters.integerParameter( "aStarSearchBudget"));
			if(actionSequence == null) {
				simpleAStarDistance = -1.0;
			} else {
				simpleAStarDistance = 1.0*actionSequence.size();

			}
		} catch(IllegalStateException e) {
			simpleAStarDistance = -1.0;
			System.out.println("failed search");
			//e.printStackTrace();
		}
		if(Parameters.parameters.booleanParameter("lodeRunnerAllowsSimpleAStarPath")) {
			fitnesses.add(simpleAStarDistance);
		}
		mostRecentVisited = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).getVisited();


		//calculates the amount of the level that was covered in the search, connectivity.
		HashSet<Point> visitedPoints = new HashSet<>();
		double connectivityOfLevel = -1;
		for(LodeRunnerState s : mostRecentVisited) {
			visitedPoints.add(new Point(s.currentX,s.currentY));
		}
		connectivityOfLevel = 1.0*visitedPoints.size();
		if(Parameters.parameters.booleanParameter("lodeRunnerAllowsConnectivity")) {
			fitnesses.add(connectivityOfLevel);
		}


		//calculates other scores that are not fitness functions 
		double percentLadders = 0;
		double percentGround = 0;
		double percentRopes = 0;
		double numTreasure = 0; 
		double numEnemies = 0;
		for(int i = 0; i < level.size();i++) {
			for(int j = 0; j < level.get(i).size(); j++) {
				//counts ladders in level  
				if(level.get(i).get(j) == LodeRunnerState.LODE_RUNNER_TILE_LADDER) {
					percentLadders++;
				}
				//counts ground in level 
				if(level.get(i).get(j) == LodeRunnerState.LODE_RUNNER_TILE_GROUND || 
						level.get(i).get(j) == LodeRunnerState.LODE_RUNNER_TILE_DIGGABLE) {
					percentGround++;
				}
				//counts ropes in level 
				if(level.get(i).get(j) == LodeRunnerState.LODE_RUNNER_TILE_ROPE) {
					percentRopes++;
				}
				//calculates number of treasures
				if(level.get(i).get(j) == LodeRunnerState.LODE_RUNNER_TILE_GOLD) {
					numTreasure++;
				}
				//calcualtes the number of enemies
				if(level.get(i).get(j) == LodeRunnerState.LODE_RUNNER_TILE_ENEMY) {
					numEnemies++;
				}
			}
		}
		percentLadders = percentLadders/TOTAL_TILES;//calculates the percentage of ladders 
		percentGround = percentGround/TOTAL_TILES;//calculates the percentage of ground 
		percentRopes = percentRopes/TOTAL_TILES;//calculates the percentage of ropes
		//calculates the percentage of the level that is connected
		double percentConnected = connectivityOfLevel/TOTAL_TILES;
		double[] otherScores = new double[] {simpleAStarDistance, connectivityOfLevel, percentLadders, percentGround, percentRopes, percentConnected, numTreasure, numEnemies};

		if(CommonConstants.watch) {
			System.out.println("Simple A* Distance to Farthest Gold " + simpleAStarDistance);
			System.out.println("Number of Positions Visited " + connectivityOfLevel);
			System.out.println("Percent of Ladders " + percentLadders);
			System.out.println("Percent of Ground " + percentGround);
			System.out.println("Percent of Ropes " + percentRopes);
			System.out.println("Percent of Connectivity in Level " + percentConnected);
			System.out.println("Number of Treasures " + numTreasure);
			System.out.println("Number of Enemies " + numEnemies);

			try {
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

		if(MMNEAT.ea instanceof MAPElites) {
			// Assign to the behavior vector before using MAP-Elites
			double[] archiveArray = null;
			final int BINS_PER_DIMENSION = LodeRunnerMAPElitesPercentConnectedGroundAndLaddersBinLabels.BINS_PER_DIMENSION;
			int dimConnected, dimGround, dimLadders;
			int connectedIndex = Math.min((int)percentConnected*BINS_PER_DIMENSION, BINS_PER_DIMENSION-1);
			int groundIndex = Math.min((int)percentGround*BINS_PER_DIMENSION, BINS_PER_DIMENSION-1);
			int laddersIndex = Math.min((int)percentLadders*BINS_PER_DIMENSION, BINS_PER_DIMENSION-1);
			double binScore = simpleAStarDistance;
			if(((MAPElites<T>) MMNEAT.ea).getBinLabelsClass() instanceof LodeRunnerMAPElitesPercentConnectedGroundAndLaddersBinLabels) {
				dimConnected = connectedIndex;
				dimGround = groundIndex;
				dimLadders = laddersIndex;
				archiveArray = new double[BINS_PER_DIMENSION*BINS_PER_DIMENSION*BINS_PER_DIMENSION];
			}
			else {
				throw new RuntimeException("A Valid Binning Scheme For Lode Runner Was Not Specified");
			}
			BufferedImage levelSolution = null;
			BufferedImage levelImage = null;
			try {
				levelSolution = LodeRunnerState.vizualizePath(level,mostRecentVisited,actionSequence,start);
				levelImage = LodeRunnerRenderUtil.getBufferedImage(level);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			setBinsAndSaveLevelImages(individual, levelImage, levelSolution, archiveArray, dimConnected, dimConnected, dimLadders, BINS_PER_DIMENSION, binScore);
		}


		return new Pair<double[],double[]>(ArrayUtil.doubleArrayFromList(fitnesses), otherScores);
	}

	@SuppressWarnings("unchecked")
	private void setBinsAndSaveLevelImages(Genotype<T> individual, BufferedImage levelImage,
			BufferedImage levelSolution, double[] archiveArray, int dimConnected, int dimGround, int dimLadders,
			final int BINS_PER_DIMENSION, double binScore) {
		int binIndex = (dimConnected*BINS_PER_DIMENSION + dimConnected)*BINS_PER_DIMENSION + dimLadders;
		Arrays.fill(archiveArray, Double.NEGATIVE_INFINITY); // Worst score in all dimensions
		archiveArray[binIndex] = binScore; 
		behaviorVector = ArrayUtil.doubleVectorFromArray(archiveArray);
		//saving images in bins 
		if(CommonConstants.netio) {
			System.out.println("Saving rendered level and solution path for level");
			Archive<T> archive = ((MAPElites<T>) MMNEAT.ea).getArchive();
			List<String> binLabels = archive.getBinMapping().binLabels();
			// Index in flattened bin array
			Score<T> elite = archive.getElite(binIndex);
			//if that index is empty or the binScores is greater than what was there before
			if(elite==null || binScore > elite.behaviorVector.get(binIndex)) {
				//formats to be 7 digits before the decimal, and 5 digits after, %7.5f
				//only doing direct right now, but will need to add CPPN label in addtion, like in MarioLevelTask, if we start to use a CPPN
				String fileNameImage = "Direct-LevelRender-" + String.format("%7.5f", binScore) + "_" + individual.getId() + ".png";
				String binPath = archive.getArchiveDirectory() + File.separator + binLabels.get(binIndex);
				String fullNameImage = binPath + "_" + fileNameImage;
				System.out.println(fullNameImage);
				GraphicsUtil.saveImage(levelImage, fullNameImage);// saves the rendered level without the solution path
				String fileNameSolution = "Direct-SolutionRender-" + String.format("%7.5f", binScore) + "_" + individual.getId() + ".png";
				String fullNameSolution = binPath + "_" +fileNameSolution;
				System.out.println(fullNameSolution);
				GraphicsUtil.saveImage(levelImage, fullNameSolution);// saves the rendered level without the solution path
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

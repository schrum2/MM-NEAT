package edu.southwestern.tasks.loderunner;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
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
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.datastructures.Pair;
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

	/**
	 * Registers all fitness functions that are used, rn only one is used for lode runner 
	 */
	public LodeRunnerLevelTask() {
		if(Parameters.parameters.booleanParameter("lodeRunnerAllowsSimpleAStarPath")) {
			MMNEAT.registerFitnessFunction("simpleAStarDistanceToFarthestGold");
			numFitnessFunctions++;
		}
		if(Parameters.parameters.booleanParameter("lodeRunnerAllowsConnectivity")) {
			MMNEAT.registerFitnessFunction("connectivityOfLevel"); //connectivity
			numFitnessFunctions++;
		}
		
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
		return numFitnessFunctions; //only one fitness function right now 
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
	 */
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		double psuedoRandomSeed = getRandomSeedForSpawnPoint(individual);

		ArrayList<Double> fitnesses = new ArrayList<>(numFitnessFunctions);
		List<List<Integer>> level = getLodeRunnerLevelListRepresentationFromGenotype(individual);
		List<Point> emptySpaces = LodeRunnerGANUtil.fillEmptyList(level);
		Random rand = new Random(Double.doubleToLongBits(psuedoRandomSeed));
		LodeRunnerGANUtil.setSpawn(level, emptySpaces, rand);
		List<List<Integer>> levelCopy = ListUtil.deepCopyListOfLists(level); //copy level 
		LodeRunnerState start = new LodeRunnerState(levelCopy);
		Search<LodeRunnerAction,LodeRunnerState> search = new AStarSearch<>(LodeRunnerState.manhattanToFarthestGold);
		HashSet<LodeRunnerState> mostRecentVisited = null;
		ArrayList<LodeRunnerAction> actionSequence = null;
		double simpleAStarDistance = -1;
		//calculates the Distance to the farthest gold as a fitness fucntion 
		try { 
			actionSequence = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).search(start, true, Parameters.parameters.integerParameter( "aStarSearchBudget"));
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsSimpleAStarPath")) {
				if(actionSequence == null) {
					fitnesses.add(-1.0);
				} else {
					simpleAStarDistance = 1.0*actionSequence.size();
					fitnesses.add(simpleAStarDistance);
				}
			}
		} catch(IllegalStateException e) {
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsSimpleAStarPath")) 
				fitnesses.add(-1.0);
			System.out.println("failed search");
			//e.printStackTrace();
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
		
		
		//ccalculates other scores that are not fitness functions 
		double percentLadders = 0;
		double percentGround = 0;
		double percentRopes = 0;
		double numTreasure = 0; 
		double numEnemies = 0;
		for(int i = 0; i < level.size();i++) {
			for(int j = 0; j < level.get(i).size(); j++) {
				//calculates the percentage of ladders 
				if(level.get(j).get(i) == LodeRunnerState.LODE_RUNNER_TILE_LADDER) {
					percentLadders++;
				}
				//calculates the percentage of ground 
				if(level.get(j).get(i) == LodeRunnerState.LODE_RUNNER_TILE_GROUND || 
						level.get(j).get(i) == LodeRunnerState.LODE_RUNNER_TILE_DIGGABLE) {
					percentGround++;
				}
				//calculates the percentage of ropes
				if(level.get(j).get(i) == LodeRunnerState.LODE_RUNNER_TILE_ROPE) {
					percentRopes++;
				}
				//calculates number of treasures
				if(level.get(j).get(i) == LodeRunnerState.LODE_RUNNER_TILE_GOLD) {
					numTreasure++;
				}
				//calcualtes the number of enemies
				if(level.get(j).get(i) == LodeRunnerState.LODE_RUNNER_TILE_ENEMY) {
					numEnemies++;
				}
			}
		}
		percentLadders = percentLadders/TOTAL_TILES;
		percentGround = percentGround/TOTAL_TILES;
		percentRopes = percentRopes/TOTAL_TILES;
		//calculates the percentage of the level that is connected
		double percentConnected = connectivityOfLevel/TOTAL_TILES;
				

		double[] otherScores = new double[] {simpleAStarDistance, connectivityOfLevel, percentLadders, percentGround, percentRopes, percentConnected, numTreasure, numEnemies};
		if(CommonConstants.watch) {
			System.out.println("Simple A* Distance to Farthest Gold " + simpleAStarDistance);
			System.out.println("Connectivity of Level " + connectivityOfLevel);
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

		return new Pair<double[],double[]>(ArrayUtil.doubleArrayFromList(fitnesses), otherScores);
	}

	/**
	 * Based on genotype, get a random seed that can be used to choose the level start point
	 * @param individual Level genotype
	 * @return Random seed
	 */
	public abstract double getRandomSeedForSpawnPoint(Genotype<T> individual);

}

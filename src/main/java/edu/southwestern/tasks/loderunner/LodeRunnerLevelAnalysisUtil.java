package edu.southwestern.tasks.loderunner;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;


import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;

public class LodeRunnerLevelAnalysisUtil {

	public static void main(String[] args) {
		Parameters.initializeParameterCollections(args);
		List<List<Integer>> level = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH + "Level 1.txt");
		double[] doubleArray = RandomNumbers.randomArray(1);
		double simpleAStarDistance = calculateAStarDistanceAndConnectivity(level, doubleArray[0]).t1;
		double connectivity = calculateAStarDistanceAndConnectivity(level, doubleArray[0]).t2;

		System.out.println(level);
		System.out.println("simpleAStarDistance = " + simpleAStarDistance);
		System.out.println("connectivity = " + connectivity);

		double percentEmpty = calculatePercentage(new double[] {LodeRunnerState.LODE_RUNNER_TILE_EMPTY}, level);
		double percentLadders = calculatePercentage(new double[] {LodeRunnerState.LODE_RUNNER_TILE_LADDER}, level);
		double percentGround = calculatePercentage(new double[] {LodeRunnerState.LODE_RUNNER_TILE_DIGGABLE, LodeRunnerState.LODE_RUNNER_TILE_GROUND}, level);
		double percentSolid = calculatePercentage(new double[] {LodeRunnerState.LODE_RUNNER_TILE_GROUND}, level);
		double percentDiggable = calculatePercentage(new double[] {LodeRunnerState.LODE_RUNNER_TILE_DIGGABLE}, level);

		System.out.println("percentEmpty = " + percentEmpty);
		System.out.println("percentLadders = " + percentLadders);
		System.out.println("percentGround = " + percentGround);
		System.out.println("percentSolid = " + percentSolid);
		System.out.println("percentDiggable = " + percentDiggable);



	}

	public static double calculatePercentage(double[] tiles , List<List<Integer>> level) {
		double percent = 0;
		for(int i = 0; i < level.size();i++) {
			for(int j = 0; j < level.get(i).size(); j++) {
				for(int k = 0; k < tiles.length; k++) {
					if(level.get(i).get(j) == tiles[k]) {
						percent++;
					}
				}
			}
		}
		return percent/LodeRunnerLevelTask.TOTAL_TILES;

	}


	public static Pair<Double, Double> calculateAStarDistanceAndConnectivity(List<List<Integer>> level, double psuedoRandomSeed) {
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
		mostRecentVisited = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).getVisited();
		//calculates the amount of the level that was covered in the search, connectivity.
		HashSet<Point> visitedPoints = new HashSet<>();
		double connectivityOfLevel = -1;
		for(LodeRunnerState s : mostRecentVisited) {
			visitedPoints.add(new Point(s.currentX,s.currentY));
		}
		connectivityOfLevel = 1.0*visitedPoints.size();
		return new Pair<Double, Double>(simpleAStarDistance, connectivityOfLevel);

	}


}

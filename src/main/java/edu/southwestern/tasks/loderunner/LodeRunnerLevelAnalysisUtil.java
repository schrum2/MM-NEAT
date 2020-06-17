package edu.southwestern.tasks.loderunner;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;


import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Quad;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;

/**
 * This class is to help analyze the original levels of Lode Runner to try to get better training sets
 * many of the methods are used in the LodeRunnerLevelTask to calculate fitness functions and other scores
 * @author kdste
 *
 */
public class LodeRunnerLevelAnalysisUtil {
	
	public static final int TOTAL_TILES = 704; //for percentages, 22x32 levels 

	public static void main(String[] args) {
		Parameters.initializeParameterCollections(args);
		List<List<Integer>> level = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH + "Level 1.txt");
		double[] doubleArray = RandomNumbers.randomArray(1);

		HashSet<LodeRunnerState> mostRecentVisited = performAStarSearchAndCalculateAStarDistance(level,doubleArray[0]).t1;
		ArrayList<LodeRunnerAction> actionSequence = performAStarSearchAndCalculateAStarDistance(level,doubleArray[0]).t2;
		LodeRunnerState start = performAStarSearchAndCalculateAStarDistance(level,doubleArray[0]).t3;
		
//		System.out.println("actionSequence length = " + actionSequence.size());
		
		double simpleAStarDistance = performAStarSearchAndCalculateAStarDistance(level, doubleArray[0]).t4;
		double connectivity = caluclateConnectivity(mostRecentVisited);
	
//		System.out.println(level);
		System.out.println("simpleAStarDistance = " + simpleAStarDistance);
		System.out.println("connectivity = " + connectivity);

		double percentBackTrack = calculatePercentAStarBacktracking(actionSequence, start);
		double percentEmpty = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_EMPTY}, level);
		double percentLadders = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_LADDER}, level);
		double percentGround = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_DIGGABLE, LodeRunnerState.LODE_RUNNER_TILE_GROUND}, level);
		double percentSolid = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_GROUND}, level);
		double percentDiggable = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_DIGGABLE}, level);

		System.out.println("percentBackTrack = " + percentBackTrack);
		System.out.println("percentEmpty = " + percentEmpty);
		System.out.println("percentLadders = " + percentLadders);
		System.out.println("percentGround = " + percentGround);
		System.out.println("percentSolid = " + percentSolid);
		System.out.println("percentDiggable = " + percentDiggable);



	}

	/**
	 * Calculates the percentage of the tiles specified in the array for the level
	 * @param tiles An array of the tiles to look for 
	 * @param level One level
	 * @return The percentage of the level that is that tile
	 */
	public static double calculatePercentageTile(double[] tiles , List<List<Integer>> level) {
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
		return percent/TOTAL_TILES;

	}

	/**
	 * Performs the AStar search and calculates the simpleAStarDistance, used in LodeRunnerLevelTask
	 * @param level A single level
	 * @param psuedoRandomSeed Random seed
	 * @return Relevant information from the search in a Quad; mostRecentVistied, actionSeqeunce, starting state, simpleAStarDistance
	 */
	public static Quad<HashSet<LodeRunnerState>, ArrayList<LodeRunnerAction>, LodeRunnerState, Double> 
				performAStarSearchAndCalculateAStarDistance(List<List<Integer>> level, double psuedoRandomSeed) {
		List<Point> emptySpaces = LodeRunnerGANUtil.fillEmptyList(level); //fills a set with empty points from the level to select a spawn point from 
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
		return new Quad<HashSet<LodeRunnerState>, ArrayList<LodeRunnerAction>, LodeRunnerState, Double>(mostRecentVisited,actionSequence,start, simpleAStarDistance);
	}

	/**
	 * Calculates the connectivity of the level, can find percentage by dividing by total tiles 
	 * @param mostRecentVisited The vistied states in the A* search
	 * @return Connectivity of level
	 */
	public static double caluclateConnectivity(HashSet<LodeRunnerState> mostRecentVisited) {
		//calculates the amount of the level that was covered in the search, connectivity.
		HashSet<Point> visitedPoints = new HashSet<>();
		double connectivityOfLevel = -1;
		for(LodeRunnerState s : mostRecentVisited) {
			visitedPoints.add(new Point(s.currentX,s.currentY));
		}
		connectivityOfLevel = 1.0*visitedPoints.size();
		return connectivityOfLevel;
	}
	
	/**
	 * Calculates the percent of backtracking in for the A* search
	 * @param actionSequence A* path
	 * @param start 
	 * @return Percent backtracking
	 */
	public static double calculatePercentAStarBacktracking(ArrayList<LodeRunnerAction> actionSequence, LodeRunnerState start) {
		double percentBacktrack = 0; 
		HashSet<Pair<Integer,Integer>> visited = new HashSet<>();
		LodeRunnerState currentState = start;
		Pair<Integer, Integer> current = null;
		for(LodeRunnerAction a: actionSequence) {
			currentState = (LodeRunnerState) currentState.getSuccessor(a);	
			Pair<Integer,Integer> next = new Pair<>(currentState.currentX, currentState.currentY);
			if(current!=null && !current.equals(next)) {
				visited.add(current);
				if(visited.contains(next))
					percentBacktrack++;
			}
			current = next;
		}
		percentBacktrack = percentBacktrack/(double)actionSequence.size();
		return percentBacktrack;
	}


}

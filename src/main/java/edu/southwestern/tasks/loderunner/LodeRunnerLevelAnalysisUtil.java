package edu.southwestern.tasks.loderunner;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;
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

	/**
	 * This main method creates a CSV file in the Lode Runner directory of the VGLC data, 
	 * it has the scores that we will use for analysis of the levels to try to create better training sets 
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Parameters.initializeParameterCollections(args);
		PrintStream ps = new PrintStream(new File("data/VGLC/Lode Runner/LevelAnalysis.csv"));
		ps.println("Level, A* Length, Connectivity, TSP Solution Length, TSP Connectivity, Percent Dug, percentBackTrack, percentEmpty, percentLadders, percentGround, percentSolid, percentDiggable");
		for(int i = 1; i <= 150; i++) {
			String line = processOneLevel(i);
			System.out.println(line);
			ps.println(line);
		}
		ps.close();

		//displays a visualization of the A* path, helpful for debugging
		//		try {
		//			//visualizes the points visited with red and whit x's
		//			BufferedImage visualPath = LodeRunnerState.vizualizePath(level,mostRecentVisited,actionSequence,start);
		//			try { //displays window with the rendered level and the solution path/visited states
		//				JFrame frame = new JFrame();
		//				JPanel panel = new JPanel();
		//				JLabel label = new JLabel(new ImageIcon(visualPath.getScaledInstance(LodeRunnerRenderUtil.LODE_RUNNER_COLUMNS*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X, 
		//						LodeRunnerRenderUtil.LODE_RUNNER_ROWS*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y, Image.SCALE_FAST)));
		//				panel.add(label);
		//				frame.add(panel);
		//				frame.pack();
		//				frame.setVisible(true);
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}
		//
	}

	/**
	 * Processes one level and gets the values for analysis 
	 * @param num The level from VGLC
	 * @return A string that holds a line of the CSV file
	 */
	public static String processOneLevel(int num) {
		List<List<Integer>> level = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH + "Level "+num+".txt");
		double simpleAStarDistance;
		double connectivity;
		double tspSolutionPathLength;
		double tspConnectivity;
		double dugPercent;
		double percentBackTrack;
		if(num!=7 && num!=64 && num!=71 && num!=75 && num!=81 && num!=86 && num!=98 && num!=136 && num!=137 && num!=148) { //excludes the levels that we know that our tsp solver cannot solve
			Triple<HashSet<LodeRunnerState>, ArrayList<LodeRunnerAction>, LodeRunnerState> aStarInfo = performAStarSearch(level,Double.NaN);
			HashSet<LodeRunnerState> mostRecentVisited = aStarInfo.t1;
			ArrayList<LodeRunnerAction> actionSequence = aStarInfo.t2;
			LodeRunnerState start = aStarInfo.t3;
			simpleAStarDistance = calculateSimpleAStarLength(actionSequence);
			connectivity = caluclateConnectivity(mostRecentVisited);
			Pair<ArrayList<LodeRunnerAction>, HashSet<LodeRunnerState>> tspInfo = getTSPResults(level);
			tspSolutionPathLength= calculateTSPSolutionPathLength(tspInfo.t1);
			tspConnectivity = calculateTSPConnectivity(tspInfo.t2);
			dugPercent = getPercentOfDugSteps(actionSequence, tspInfo.t1, start, level);
			percentBackTrack = calculatePercentAStarBacktracking(actionSequence, start);
		}
		else {
			simpleAStarDistance = -1.0;
			connectivity = -1.0;
			tspSolutionPathLength= -1.0;
			tspConnectivity= -1.0;
			dugPercent = -1.0;
			percentBackTrack = -1.0;
		}
		double percentEmpty = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_EMPTY}, level);
		double percentLadders = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_LADDER}, level);
		double percentGround = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_DIGGABLE, LodeRunnerState.LODE_RUNNER_TILE_GROUND}, level);
		double percentSolid = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_GROUND}, level);
		double percentDiggable = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_DIGGABLE}, level);
		String line = "Level"+num+","+simpleAStarDistance+","+connectivity+","+tspSolutionPathLength+","+tspConnectivity+","+dugPercent+","+percentBackTrack+","+percentEmpty+","+percentLadders+","+percentGround+","+percentSolid+","+percentDiggable;
		return line;
	}
	
	public static double getPercentOfDugSteps(ArrayList<LodeRunnerAction> solutionAStar, ArrayList<LodeRunnerAction> solutionTSP, LodeRunnerState start, List<List<Integer>> level) {
		ArrayList<LodeRunnerAction> solution = findMinLengthSolutionPath(solutionAStar, solutionTSP);
		if(solution == null)
			return -1.0;
		double percentDug = 0;
		LodeRunnerState currentState = start;
		Pair<Integer, Integer> current = null;
		for(LodeRunnerAction a: solution) {
			currentState = (LodeRunnerState) currentState.getSuccessor(a);	
			current = new Pair<Integer, Integer>(currentState.currentX, currentState.currentY);
			if(tileAtPosition(current.t1,current.t2, level) == LodeRunnerState.LODE_RUNNER_TILE_DIGGABLE)
				percentDug++;
		}
		return percentDug/solution.size();
	}

	private static ArrayList<LodeRunnerAction> findMinLengthSolutionPath(ArrayList<LodeRunnerAction> solutionAStar,
			ArrayList<LodeRunnerAction> solutionTSP) {
		if(solutionAStar==null && solutionTSP==null)
			return null;
		if(solutionAStar!=null && solutionAStar.size() < solutionTSP.size())
			return solutionAStar;
		else
			return solutionTSP;
	}

	private static int tileAtPosition(int x, int y, List<List<Integer>> level) {
		return level.get(y).get(x);
	}

	/**
	 * Calculates the number of visited states from a tsp search
	 * @param mostRecentVisitedTSP Visited States from tsp search
	 * @return Length of the set holding the visited states for the TSP search
	 */
	private static double calculateTSPConnectivity(HashSet<LodeRunnerState> mostRecentVisitedTSP) {
		//calculates the amount of the level that was covered in the search, connectivity.
		HashSet<Point> visitedPoints = new HashSet<>();
		double connectivity = -1;
		for(LodeRunnerState s : mostRecentVisitedTSP) {
			visitedPoints.add(new Point(s.currentX,s.currentY));
		}
		connectivity = 1.0*visitedPoints.size();
		return connectivity;
	}

	/**
	 * Calculates the Solution path length for a tsp search
	 * @param tspSolution The solution path 
	 * @return The legnth of the solution path
	 */
	public static double calculateTSPSolutionPathLength(ArrayList<LodeRunnerAction> tspSolution) {
		double tspSolutionPathLength;
		if(tspSolution != null) {
			tspSolutionPathLength = 1.0*tspSolution.size();
		}
		else {
			tspSolutionPathLength = -1.0;
		}
		return tspSolutionPathLength;
	}

	/**
	 * Runs a TSP search 
	 * @param level
	 * @return
	 */
	public static Pair<ArrayList<LodeRunnerAction>, HashSet<LodeRunnerState>> getTSPResults(List<List<Integer>> level){
		return LodeRunnerTSPUtil.getFullActionSequenceAndVisitedStatesTSPGreedySolution(level);
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
	public static Triple<HashSet<LodeRunnerState>, ArrayList<LodeRunnerAction>, LodeRunnerState> performAStarSearch(List<List<Integer>> level, double psuedoRandomSeed) {
		//declares variable to be initizalized in the if statements below 
		LodeRunnerState start;
		List<List<Integer>> levelCopy;
		//if a random seed is not given then get the spawn point from the VGLC
		if(Double.isNaN(psuedoRandomSeed)) { 
			levelCopy = ListUtil.deepCopyListOfLists(level);
			start = new LodeRunnerState(levelCopy);
			//System.out.println(start);
		} 
		else{//other wise assign a random spawn point bsaed on of the random seed 
			List<Point> emptySpaces = LodeRunnerGANUtil.fillEmptyList(level); //fills a set with empty points from the level to select a spawn point from 
			Random rand = new Random(Double.doubleToLongBits(psuedoRandomSeed));
			LodeRunnerGANUtil.setSpawn(level, emptySpaces, rand); //sets a random spawn point 
			levelCopy = ListUtil.deepCopyListOfLists(level); //copy level so it is not effected by the search 
			start = new LodeRunnerState(levelCopy); //gets start state for search 
			//System.out.println(start);
		}
		Search<LodeRunnerAction,LodeRunnerState> search = new AStarSearch<>(LodeRunnerState.manhattanToFarthestGold); //initializes a search based on the heuristic 
		HashSet<LodeRunnerState> mostRecentVisited = null;
		ArrayList<LodeRunnerAction> actionSequence = null;
		//calculates the Distance to the farthest gold as a fitness function 
		try { 
			actionSequence = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).search(start, true, 150000);
		} catch(IllegalStateException e) {
			System.out.println("failed search");
		}
		mostRecentVisited = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).getVisited();
		return new Triple<HashSet<LodeRunnerState>, ArrayList<LodeRunnerAction>, LodeRunnerState>(mostRecentVisited,actionSequence,start);
	}

	/**
	 * Calculates the length of the A* path
	 * @param actionSequence Solution path
	 * @return Length of solution Path
	 */
	public static double calculateSimpleAStarLength(ArrayList<LodeRunnerAction> actionSequence) {
		double simpleAStarDistance;
		if(actionSequence == null) {
			simpleAStarDistance = -1.0;
		} else {
			simpleAStarDistance = 1.0*actionSequence.size();

		}
		return simpleAStarDistance;
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
		if(actionSequence == null) {
			return -1.0;
		}
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

package edu.southwestern.tasks.loderunner;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Quad;
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
	 * 
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Parameters.initializeParameterCollections(args);
		PrintStream ps = new PrintStream(new File("data/VGLC/Lode Runner/LevelAnalysis.csv"));
		ps.println("Level, A* Length, Connectivity, percentBackTrack, percentEmpty, percentLadders, percentGround, percentSolid, percentDiggable");
		for(int i = 1; i <= 150; i++) {
			String line = processOneLevel(i);
			System.out.println(line);
			ps.println(line);
		}
		ps.close();
		
//		List<List<Integer>> level = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH + "Level 1.txt");

//		HashSet<LodeRunnerState> mostRecentVisited = performAStarSearchAndCalculateAStarDistance(level,Double.NaN).t1;
//		ArrayList<LodeRunnerAction> actionSequence = performAStarSearchAndCalculateAStarDistance(level,Double.NaN).t2;
//		LodeRunnerState start = performAStarSearchAndCalculateAStarDistance(level,Double.NaN).t3;
//
//		System.out.println("actionSequence length = " + actionSequence.size());
//
//		double simpleAStarDistance = performAStarSearchAndCalculateAStarDistance(level, Double.NaN).t4;
//		double connectivity = caluclateConnectivity(mostRecentVisited);
//
//		System.out.println(level);
//		System.out.println("simpleAStarDistance = " + simpleAStarDistance);
//		System.out.println("connectivity = " + connectivity);
//
//		double percentBackTrack = calculatePercentAStarBacktracking(actionSequence, start);
//		double percentEmpty = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_EMPTY}, level);
//		double percentLadders = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_LADDER}, level);
//		double percentGround = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_DIGGABLE, LodeRunnerState.LODE_RUNNER_TILE_GROUND}, level);
//		double percentSolid = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_GROUND}, level);
//		double percentDiggable = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_DIGGABLE}, level);
		
		

//		System.out.println("percentBackTrack = " + percentBackTrack);
//		System.out.println("percentEmpty = " + percentEmpty);
//		System.out.println("percentLadders = " + percentLadders);
//		System.out.println("percentGround = " + percentGround);
//		System.out.println("percentSolid = " + percentSolid);
//		System.out.println("percentDiggable = " + percentDiggable);
//		System.out.println(start);
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
	
	
	public static String processOneLevel(int num) {
		List<List<Integer>> level = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH + "Level "+num+".txt");
		HashSet<LodeRunnerState> mostRecentVisited = performAStarSearchAndCalculateAStarDistance(level,Double.NaN).t1;
		ArrayList<LodeRunnerAction> actionSequence = performAStarSearchAndCalculateAStarDistance(level,Double.NaN).t2;
		LodeRunnerState start = performAStarSearchAndCalculateAStarDistance(level,Double.NaN).t3;
		double simpleAStarDistance = performAStarSearchAndCalculateAStarDistance(level, Double.NaN).t4;
		double connectivity = caluclateConnectivity(mostRecentVisited);
		double percentBackTrack = calculatePercentAStarBacktracking(actionSequence, start);
		double percentEmpty = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_EMPTY}, level);
		double percentLadders = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_LADDER}, level);
		double percentGround = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_DIGGABLE, LodeRunnerState.LODE_RUNNER_TILE_GROUND}, level);
		double percentSolid = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_GROUND}, level);
		double percentDiggable = calculatePercentageTile(new double[] {LodeRunnerState.LODE_RUNNER_TILE_DIGGABLE}, level);
		String line = "Level"+num+","+simpleAStarDistance+","+connectivity+","+percentBackTrack+","+percentEmpty+","+percentLadders+","+percentGround+","+percentSolid+","+percentDiggable;
		return line;
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
			System.out.println(start);
		}
		Search<LodeRunnerAction,LodeRunnerState> search = new AStarSearch<>(LodeRunnerState.manhattanToFarthestGold); //initializes a search based on the heuristic 
		HashSet<LodeRunnerState> mostRecentVisited = null;
		ArrayList<LodeRunnerAction> actionSequence = null;
		double simpleAStarDistance = -1; //intialized to hold distance of solution path, or -1 if search fails
		//calculates the Distance to the farthest gold as a fitness fucntion 
		try { 
			actionSequence = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).search(start, true, Parameters.parameters.integerParameter("aStarSearchBudget"));
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
		return new Quad<HashSet<LodeRunnerState>, ArrayList<LodeRunnerAction>, LodeRunnerState, Double>(mostRecentVisited,actionSequence,start,simpleAStarDistance);
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

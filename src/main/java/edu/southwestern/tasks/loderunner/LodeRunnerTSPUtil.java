package edu.southwestern.tasks.loderunner;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.util.datastructures.Graph;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

public class LodeRunnerTSPUtil {

	public static void main(String[] args) {
		Parameters.initializeParameterCollections(args);
		//int visitedSize = 0;
		List<List<Integer>> level = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH + "Level 4.txt");
		ArrayList<LodeRunnerAction> fullActionSequence = getFullActionSequenceFromTSPGreedySolution(level);
		
		System.out.println(fullActionSequence);
		//		//calculates number of states visited from the spawn to every other gold
		//		for(Point p : gold) {
		//			List<List<Integer>> levelCopy = ListUtil.deepCopyListOfLists(level);
		//			levelCopy.get(spawn.y).set(spawn.x, LodeRunnerState.LODE_RUNNER_TILE_SPAWN);
		//			levelCopy.get(p.y).set(p.x, LodeRunnerState.LODE_RUNNER_TILE_GOLD);
		//			Triple<HashSet<LodeRunnerState>, ArrayList<LodeRunnerAction>, LodeRunnerState> aStarInfo =LodeRunnerLevelAnalysisUtil.performAStarSearch(levelCopy, Double.NaN);
		//			visitedSize+=aStarInfo.t1.size();//keeps track of how many visited states for every run of A*
		//		}
		//		System.out.println(tsp.root());
		//		System.out.println(tsp.root().adjacencies());
		//		System.out.println(tsp.getNodes());
		//		System.out.println("Number of states visited: "+visitedSize);
	}

	public static ArrayList<LodeRunnerAction> getFullActionSequenceFromTSPGreedySolution(List<List<Integer>> level) {
		Pair<Graph<Point>, HashMap<Pair<Point, Point>, ArrayList<LodeRunnerAction>>> tspInfo = getTSPGraph(level);
		Graph<Point> tsp = tspInfo.t1;
		HashMap<Pair<Point, Point>, ArrayList<LodeRunnerAction>> tspActions = tspInfo.t2;
		List<Pair<Graph<Point>.Node, Double>> solutionPath = getTSPGreedySolution(tsp);
//		System.out.println(solutionPath);
//		System.out.println(tspActions);
		return getFullTSPActionSequence(tspActions, solutionPath);
	}

	/**
	 * Concatenates the A* paths between each gold visited by the solved TSP problem 
	 * @param tspActions HashMap; key is a pair of points, value is the action sequence between those points
	 * @param solutionPath Solution to the TSP problem 
	 * @return The full solution path from the TSP problem 
	 */
	public static ArrayList<LodeRunnerAction> getFullTSPActionSequence(
			HashMap<Pair<Point, Point>, ArrayList<LodeRunnerAction>> tspActions,
			List<Pair<Graph<Point>.Node, Double>> solutionPath) {
		ArrayList<LodeRunnerAction> fullActionSequence = new ArrayList<>();
		for(int i = 0; i < solutionPath.size()-1; i++) {
			Pair<Point, Point> key = new Pair<Point, Point>(solutionPath.get(i).t1.getData(), solutionPath.get(i+1).t1.getData());
			fullActionSequence.addAll(tspActions.get(key));
		}
		return fullActionSequence;
	}

	/**
	 * Creates a sequence to collect the gold for in the level 
	 * by moving to the node that has the least weight on its edge and removing 
	 * the current node
	 * It will always give a solution, but not an optimal one
	 * @param tsp A digraph holding points of gold and weights
	 * @return The order to collect gold
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Pair<Graph<Point>.Node, Double>> getTSPGreedySolution(Graph<Point> tsp) {
		//solving the TSP problem from the graph 
		List<Pair<Graph<Point>.Node, Double>> solutionPath = new ArrayList<>(); //set of points
		solutionPath.add(new Pair<Graph<Point>.Node, Double>(tsp.root(), 0.0)); //adds the spawn as the first point 
		//loops through all adjacent nodes, adds the node with the lowest weight 
		while(!tsp.root().adjacencies().isEmpty()) {
			Iterator itr = tsp.root().adjacencies().iterator();
			Pair<Graph<Point>.Node, Double> min = new Pair<Graph<Point>.Node, Double>(tsp.root(), Double.MAX_VALUE);
			while(itr.hasNext()) {
				Pair<Graph<Point>.Node, Double> node = (Pair<Graph<Point>.Node, Double>) itr.next();
				if(node.t2 < min.t2) {
					min = node;
				}
			}
			solutionPath.add(min);
			//System.out.println(solutionPath);
			tsp.root().adjacencies().remove(min);
		}
		return solutionPath;
	}

	/**
	 * Creates a graph for the TSP problem from the points where the gold are located
	 * @param level A level
	 * @return A graph with gold as nodes and the spawn point as the root
	 */
	public static Pair<Graph<Point>,  HashMap<Pair<Point, Point>, ArrayList<LodeRunnerAction>>> 
	getTSPGraph(List<List<Integer>> level) {
		//clears level of gold and spawn but maintains a reference in this set 
		HashSet<Point> gold = LodeRunnerState.fillGold(level);
		Point spawn = findSpawnAndRemove(level);
		HashMap<Pair<Point, Point>, ArrayList<LodeRunnerAction>> actionSequences = new HashMap<>();
		Graph<Point> tsp = new Graph<Point>();
		tsp.addNode(spawn);
		for(Point p : gold) {
			tsp.addNode(p);
		}
		for(Graph<Point>.Node p : tsp.getNodes()) {
			for(Graph<Point>.Node i : tsp.getNodes()) {
				List<List<Integer>> levelCopy = ListUtil.deepCopyListOfLists(level);
				//if the nodes aren't equal and makes the spawn point not be a destination, only a source
				if(!p.equals(i) && !i.getData().equals(spawn)) {
					levelCopy.get(p.getData().y).set(p.getData().x, LodeRunnerState.LODE_RUNNER_TILE_SPAWN);//sets spawn as one of the gold to get distance between the gold
					levelCopy.get(i.getData().y).set(i.getData().x, LodeRunnerState.LODE_RUNNER_TILE_GOLD); //destination gold 
					Triple<HashSet<LodeRunnerState>, ArrayList<LodeRunnerAction>, LodeRunnerState> aStarInfo = LodeRunnerLevelAnalysisUtil.performAStarSearch(levelCopy, Double.NaN);
					//visitedSize+=aStarInfo.t1.size(); //keeps track of how many visited states for every run of A*
					double simpleAStarDistance = LodeRunnerLevelAnalysisUtil.calculateSimpleAStarLength(aStarInfo.t2);
					tsp.addDirectedEdge(p, i, simpleAStarDistance); //adds the directed edge to the graph
					Pair<Point, Point> key = new Pair<Point, Point>(p.getData(), i.getData());
					actionSequences.put(key, aStarInfo.t2);
				}
			}
		}
		return new Pair<Graph<Point>,  HashMap<Pair<Point, Point>, ArrayList<LodeRunnerAction>>>(tsp,actionSequences);
	}

	/**
	 * This method finds the spawn point in the level, marks it, and removes it for artificial placement
	 * @param level A level
	 * @return Location of the spawn point
	 */
	public static Point findSpawnAndRemove(List<List<Integer>> level) {
		Point spawn = null;
		for(int i = 0; i < level.size(); i++) {
			for(int j = 0; j < level.get(i).size(); j++){
				if(level.get(i).get(j) == LodeRunnerState.LODE_RUNNER_TILE_SPAWN) {
					spawn = new Point(j, i);
					level.get(i).set(j, LodeRunnerState.LODE_RUNNER_TILE_EMPTY);
					break;
				}
			}
		}
		return spawn;
	}





}

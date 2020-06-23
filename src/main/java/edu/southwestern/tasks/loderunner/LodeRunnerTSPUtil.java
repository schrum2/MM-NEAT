package edu.southwestern.tasks.loderunner;

import java.awt.Point;
import java.util.ArrayList;
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
		List<List<Integer>> level = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH + "Level 150.txt");
		Graph<Point> tsp = getTSPGraph(level);
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

		List<Pair<Graph<Point>.Node, Double>> solutionPath = getTSPGreedyPath(tsp);
		System.out.println(solutionPath);
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
	private static List<Pair<Graph<Point>.Node, Double>> getTSPGreedyPath(Graph<Point> tsp) {
		//solving the TSP problem from the graph 
		List<Pair<Graph<Point>.Node, Double>> solutionPath = new ArrayList<>(); //set of points
		HashSet<Pair<Graph<Point>.Node, Double>> nodes = (HashSet<Pair<Graph<Point>.Node, Double>>) tsp.root().adjacencies();
		//Pair<Graph<Point>.Node, Double> min = new Pair<Graph<Point>.Node, Double>(tsp.root(), Double.MAX_VALUE);
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

	private static Graph<Point> getTSPGraph(List<List<Integer>> level) {
		//clears level of gold and spawn but maintains a reference in this set 
		HashSet<Point> gold = LodeRunnerState.fillGold(level);
		Point spawn = findSpawnAndRemove(level);
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
					levelCopy.get(p.getData().y).set(p.getData().x, LodeRunnerState.LODE_RUNNER_TILE_SPAWN);
					levelCopy.get(i.getData().y).set(i.getData().x, LodeRunnerState.LODE_RUNNER_TILE_GOLD);
					Triple<HashSet<LodeRunnerState>, ArrayList<LodeRunnerAction>, LodeRunnerState> aStarInfo =LodeRunnerLevelAnalysisUtil.performAStarSearch(levelCopy, Double.NaN);
					//visitedSize+=aStarInfo.t1.size(); //keeps track of how many visited states for every run of A*
					double simpleAStarDistance = LodeRunnerLevelAnalysisUtil.calculateSimpleAStarLength(aStarInfo.t2);
					tsp.addDirectedEdge(p, i, simpleAStarDistance);
				}
			}
		}
		return tsp;
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

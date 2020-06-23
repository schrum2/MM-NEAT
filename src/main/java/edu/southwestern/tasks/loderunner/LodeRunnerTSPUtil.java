package edu.southwestern.tasks.loderunner;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.util.datastructures.Graph;
import edu.southwestern.util.datastructures.Graph.Node;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

public class LodeRunnerTSPUtil {

	//we need to accumulate the size of the visited states 

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(args);
		//int visitedSize = 0;
		List<List<Integer>> level = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH + "Level 4.txt");
		//clears level of gold and spawn but maintains a reference in this set 
		HashSet<Point> gold = LodeRunnerState.fillGold(level);
		//System.out.println(gold.toString());
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
		
		//solving the TSP problem from the graph 
		System.out.println(tsp.root().adjacencies());
		List<Pair<Graph<Point>.Node, Double>> solutionPath = new ArrayList<>(); //set of points
		//loops through all adjacent nodes, adds the node with the lowest weight 
		for(Pair<Graph<Point>.Node, Double> n: tsp.root().adjacencies()) {
			for(Pair<Graph<Point>.Node, Double> p: tsp.root().adjacencies()) {
				
			}
		}
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

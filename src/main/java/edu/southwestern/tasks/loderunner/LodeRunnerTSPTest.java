package edu.southwestern.tasks.loderunner;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.util.datastructures.Graph;
import edu.southwestern.util.datastructures.ListUtil;
import edu.southwestern.util.datastructures.Triple;

public class LodeRunnerTSPTest {

	//we need to accumulate the size of the visited states 

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(args);
		int visitedSize = 0;
		List<List<Integer>> level = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH + "Level 4.txt");
		//clears level of gold and spawn but maintains a reference in this set 
		HashSet<Point> gold = LodeRunnerState.fillGold(level);
		//System.out.println(gold.toString());
		Point spawn = findSpawnAndRemove(level);
//		List<Point> graphList = new ArrayList<Point>();
//		graphList.add(spawn);
		Graph<Point> tsp = new Graph<Point>();
		tsp.addNode(spawn);
		for(Point p : gold) {
			tsp.addNode(p);
			//graphList.add(p);
			for(Point i : gold) {
				List<List<Integer>> levelCopy = ListUtil.deepCopyListOfLists(level);
				if(!p.equals(i)) {
					//tsp.addNode(p);
					//graphList.add(p);
					levelCopy.get(p.y).set(p.x, LodeRunnerState.LODE_RUNNER_TILE_SPAWN);
					levelCopy.get(i.y).set(i.x, LodeRunnerState.LODE_RUNNER_TILE_GOLD);
					Triple<HashSet<LodeRunnerState>, ArrayList<LodeRunnerAction>, LodeRunnerState> aStarInfo =LodeRunnerLevelAnalysisUtil.performAStarSearch(levelCopy, Double.NaN);
					visitedSize+=aStarInfo.t1.size(); //keeps track of how many visited states for every run of A*
					double simpleAStarDistance = LodeRunnerLevelAnalysisUtil.calculateSimpleAStarLength(aStarInfo.t2);
					//tsp.addDirectedEdge(p,i, simpleAStarDistance);
				}
			}
		}
		//calculates number of states visited from the spawn to every other gold
		for(Point p : gold) {
			List<List<Integer>> levelCopy = ListUtil.deepCopyListOfLists(level);
			levelCopy.get(spawn.y).set(spawn.x, LodeRunnerState.LODE_RUNNER_TILE_SPAWN);
			levelCopy.get(p.y).set(p.x, LodeRunnerState.LODE_RUNNER_TILE_GOLD);
			Triple<HashSet<LodeRunnerState>, ArrayList<LodeRunnerAction>, LodeRunnerState> aStarInfo =LodeRunnerLevelAnalysisUtil.performAStarSearch(levelCopy, Double.NaN);
			visitedSize+=aStarInfo.t1.size();//keeps track of how many visited states for every run of A*
		}
		//Graph<Point> tsp = new Graph(graphList);
		System.out.println(tsp.root());
		//System.out.println(tsp.breadthFirstTraversal());
		System.out.println("Number of states visited: "+visitedSize);
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
